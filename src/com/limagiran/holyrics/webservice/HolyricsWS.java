package com.limagiran.holyrics.webservice;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotification;
import com.google.gson.Gson;
import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.control.AppMessageController;
import com.limagiran.holyrics.control.FavoriteController;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.favorite.FavoriteItem;
import com.limagiran.holyrics.favorite.FavoriteUtils;
import com.limagiran.holyrics.hitem.EnumTabType;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.hitem.ListModelHItem;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.model.CountdownSettings;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.BackgroundImageFile;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.share.ReceiveUpdateFromMobileView;
import com.limagiran.holyrics.share.ShareFilesApp;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Security;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebServiceUtils;
import com.limagiran.holyrics.view.CommunicationPanel;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.view.PresentationViewRemoteControl;
import com.limagiran.holyrics.view.panel.AlertPanel;
import com.limagiran.holyrics.vlc.VLC;
import com.limagiran.holyrics.vlc.VLCUtils;
import com.limagiran.holyrics.worker.UpdateFromJsonWorker;
import com.limagiran.lgsaveopen.SerialMap;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.jws.WebService;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.ws.Endpoint;

@WebService(
   endpointInterface = "com.limagiran.holyrics.webservice.IHolyricsWS"
)
public class HolyricsWS implements IHolyricsWS {
   private static final transient Dimension SIZE_IMG_PREVIEW = new Dimension(320, 180);
   private static Endpoint ENDPOINT;
   private static String currentIP = "";
   public static final Gson GSON = new Gson();

   public static void open() {
      if (Settings.EnumSettings2.WEB_SERVICE_ENABLED.is()) {
         Utils.startThreadDaemon(
            () -> {
               try {
                  close();
                  Optional.ofNullable(WebServiceUtils.getIP())
                     .ifPresent(ip -> ENDPOINT = Endpoint.publish("http://" + (currentIP = ip.ip) + ":888/holyricsWS", new HolyricsWS()));
               } catch (Exception var1) {
               }
            }
         );
      }
   }

   public static boolean isOpen() {
      WebServiceUtils.IP ip = WebServiceUtils.getIP();
      return ENDPOINT != null && ENDPOINT.isPublished() && ip != null && currentIP.equals(ip.ip);
   }

   public static boolean isOpenFast() {
      return ENDPOINT != null && ENDPOINT.isPublished();
   }

   public static void close() {
      Optional.ofNullable(ENDPOINT).ifPresent(e -> e.stop());
   }

   @Override
   public String getSharedFiles(String pack) {
      try {
         Pack p = Pack.create(pack);
         if (!Settings.EnumSettings.PASSWORD_REMOTE_UPDATE.getString().isEmpty()
            && !p.getString("password", "").equals(Settings.EnumSettings.PASSWORD_REMOTE_UPDATE.getString())) {
            return Pack.create(false, "invalid_password").toJSon();
         } else {
            String json = ShareFilesApp.create().toJSon();
            return Pack.create(true).put("json", json).toJSon();
         }
      } catch (Exception var4) {
         return Pack.create(false, "exception").toJSon();
      }
   }

   @Override
   public String sendSharedFiles(String value) {
      if (!ReceiveUpdateFromMobileView.staticIsVisible()) {
         return I18N.MSG_ERROR_UP_FROM_MOB_NOT_ENABLED.val();
      } else if (!ReceiveUpdateFromMobileView.isAvailable()) {
         return I18N.MSG_BUSY_PROGRAM.val();
      } else {
         ReceiveUpdateFromMobileView.setAvailable(false);
         ReceiveUpdateFromMobileView.setStatus(I18N.WORD_UPDATING___.val());
         Utils.startThreadDaemon(() -> PopUpWorker.exe(new UpdateFromJsonWorker(value)));
         return I18N.MSG_UPDATE_SENT.val();
      }
   }

   @Override
   public String initRemoteControl(String pack) {
      try {
         Pack p = Pack.create(pack);
         if (!Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_HOLYRICS.getString().isEmpty()
            && !p.getString("password", "").equals(Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_HOLYRICS.getString())) {
            return Pack.create(false, "invalid_password").toJSon();
         } else {
            Music m = (Music)GSON.fromJson(p.getString("music", ""), Music.class);
            if (m == null) {
               return Pack.create(false, "invalid_music").toJSon();
            } else {
               Theme t = m.getDefaultThemeOr(Settings.SET.getTheme());
               String theme = p.getString("theme", "");
               if (!theme.isEmpty() && !theme.equalsIgnoreCase("null")) {
                  try {
                     SerialMap sm = RWObj.read(Utils.base64ToBytes(theme), SerialMap.class);
                     t = Theme.create().createFromSerialMap(sm);
                  } catch (Exception var10) {
                  }
               }

               long id = Utils.generateMillisecondId();
               if (m.getOrder().isEmpty()) {
                  Music _m = MusicDao.getLazy(m.getId());
                  if (_m != null && _m.getLyrics().equalsIgnoreCase(m.getLyrics())) {
                     m.getOrder().addAll(_m.getOrder());
                  }
               }

               List<String> list = GeneratePresentation.showRemoteControl(m, t, id);
               PresentationViewRemoteControl.staticSleep(id, 3000L);
               return Pack.create(true).put("id", id).put("list", list).toJSon();
            }
         }
      } catch (Exception var11) {
         return Pack.create(false, "exception").toJSon();
      }
   }

   @Override
   public String changeRemoteControlIndex(String pack) {
      try {
         Pack _return = Pack.create(true);
         if (!PresentationViewRemoteControl.staticIsVisible()) {
            return Pack.create(false, "closed").toJSon();
         } else {
            Pack p = Pack.create(pack);
            if (p.getLong("id", -8L) != PresentationViewRemoteControl.staticId()) {
               return Pack.create(false, "closed").toJSon();
            } else {
               if (!p.getString("blank", "").isEmpty()) {
                  SwingUtils.runOnUIThread(() -> PresentationViewRemoteControl.staticBlank(p.getBoolean("blank")));
               } else if (!p.getString("black", "").isEmpty()) {
                  SwingUtils.runOnUIThread(() -> PresentationViewRemoteControl.staticBlack(p.getBoolean("black")));
               } else if (p.getBoolean("exit")) {
                  Music presentation = PresentationViewRemoteControl.staticGetMusic();
                  SwingUtils.runOnUIThread(PresentationViewRemoteControl::staticExit);
                  if (p.getBoolean("setToMusicPlayed")) {
                     Optional.ofNullable(MusicDao.getLazy(presentation.getId()))
                        .ifPresent(
                           m -> {
                              if (m.getId() == presentation.getId()
                                 && m.getTitle().equalsIgnoreCase(presentation.getTitle())
                                 && m.getArtist().equalsIgnoreCase(presentation.getArtist())) {
                                 SwingUtilities.invokeLater(() -> MusicMain.INSTANCE.playedMusic(m));
                              }
                           }
                        );
                  }
               } else if (p.getInt("index", -1) != -1) {
                  SwingUtils.runOnUIThread(() -> PresentationViewRemoteControl.staticSetIndex(p.getInt("index", 0)));
               }

               if (!p.getString("theme", "").isEmpty()) {
                  if (p.getString("theme", "").equalsIgnoreCase("null")) {
                     _return.put("theme_id", "null");
                     Music music = PresentationViewRemoteControl.staticGetMusic();
                     Theme theme = music.getDefaultThemeOr(Settings.SET.getTheme());
                     SwingUtilities.invokeLater(() -> PresentationViewRemoteControl.staticSetTheme(theme));
                  } else {
                     try {
                        SerialMap sm = RWObj.read(Utils.base64ToBytes(p.getString("theme", "")), SerialMap.class);
                        Theme t = Theme.create().createFromSerialMap(sm);
                        Optional.ofNullable(t).ifPresent(i -> SwingUtilities.invokeLater(() -> PresentationViewRemoteControl.staticSetTheme(i)));
                        _return.put("theme_id", p.getString("theme_id", "null"));
                     } catch (Exception var6) {
                     }
                  }
               }

               return _return.toJSon();
            }
         }
      } catch (Exception var7) {
         return Pack.create(false, "exception").toJSon();
      }
   }

   @Override
   public String getStreaming(String pack) {
      try {
         Pack p = Pack.create(pack);
         if (!Settings.EnumSettings.STREAMING_ON.is()) {
            return Pack.create(true).put("image", "disabled").put("update", HolyricsStreaming.getUpdate() + "_").toJSon();
         } else {
            boolean widescreen = p.getBoolean("widescreen");
            HolyricsStreaming.request(widescreen);
            return !p.getString("update", HolyricsStreaming.getUpdate()).equals(HolyricsStreaming.getUpdate())
               ? HolyricsStreaming.getStreamingPack(widescreen).toJSon()
               : Pack.create(true).put("image", "same").toJSon();
         }
      } catch (Exception var4) {
         return Pack.create(true).put("image", "same").toJSon();
      }
   }

   @Override
   public String getPlaylist(String pack) {
      Pack p = Pack.create(pack);
      if (!Settings.EnumSettings.PASSWORD_REMOTE_UPDATE.getString().isEmpty()
         && !p.getString("password", "").equals(Settings.EnumSettings.PASSWORD_REMOTE_UPDATE.getString())) {
         return Pack.create(false, "invalid_password").toJSon();
      } else {
         ShareFilesApp.MusicApp app = new ShareFilesApp.MusicApp(new ArrayList<>());
         BooleanOptional.runIfTrue(MusicMain.INSTANCE != null, () -> MusicMain.staticGetPlayList().forEach(app::put));
         return Pack.create(true).put("playlist", app.getJson()).toJSon();
      }
   }

   @Override
   public String webService(String pack) {
      return this.webService2(pack).put("wb", true).toJSon();
   }

   private Pack webService2(String pack) {
      Pack p = Pack.create(pack);
      String var3 = p.getString("request", "");
      switch (var3) {
         case "initQuickPresentation":
            return this.initQuickPresentation(p);
         case "changeQuickPresentation":
            return this.changeQuickPresentation(p);
         case "alert":
            return this.alert(p);
         case "getAlert":
            return this.getAlert(p);
         case "announcementList":
            return this.announcementList(p);
         case "announcement":
            return this.announcement(p);
         case "mediaList":
            return this.mediaList(p, EnumTabType.values());
         case "audioList":
            return this.mediaList(p, EnumTabType.AUDIO);
         case "videoList":
            return this.mediaList(p, EnumTabType.VIDEO);
         case "imageList":
            return this.mediaList(p, EnumTabType.IMAGE);
         case "showImage":
            return this.showImage(p);
         case "playlistMedia":
            return this.playlistMedia(p);
         case "favoritesList":
            return this.favoritesList(p);
         case "vlcStatus":
            return this.vlcStatus(p);
         case "vlcAction":
            return this.vlcAction(p);
         case "sendBytes":
            return this.sendBytes(p);
         case "sendPlaylistMusic":
            return this.sendPlaylistMusic(p);
         case "getCountdownSettings":
            return this.getCountdownSettings(p);
         case "showCountdown":
            return this.showCountdown(p);
         case "closeCountdown":
            return this.closeCountdown(p);
         case "getCommunicationPanel":
            return this.getCommunicationPanel(p);
         case "communicationPanelAction":
            return this.communicationPanelAction(p);
         case "getObject":
            return this.getObject(p);
         case "appMessage":
            return this.appMessage(p);
         default:
            return Pack.create(false, "not_found");
      }
   }

   private Pack defaultPasswordRemoteControlFilter(Pack p, Supplier<Pack> _return) {
      try {
         return !Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_HOLYRICS.getString().isEmpty()
               && !p.getString("password", "").equals(Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_HOLYRICS.getString())
            ? Pack.create(false, "invalid_password")
            : _return.get();
      } catch (Exception var4) {
         return Pack.create(false, "exception");
      }
   }

   private Pack defaultPasswordRemoteControlMediaFilter(Pack p, Function<Pack, Pack> _return) {
      try {
         String pass = Settings.EnumSettings2.PASSWORD_REMOTE_CONTROL_MEDIA_HOLYRICS.getString();
         p = Pack.createDecrypt(p, Security.getMD5(pass, 7));
         String clientPass = p.getString("password", "");
         return !pass.isEmpty() && !clientPass.equals(Security.getMD5(pass, 7))
            ? Pack.create(false, "invalid_password")
            : _return.apply(p).encrypt(Security.getMD5(pass, 7));
      } catch (Exception var5) {
         return Pack.create(false, "exception");
      }
   }

   private Pack initQuickPresentation(Pack p) {
      return this.defaultPasswordRemoteControlFilter(p, () -> {
         Music m = (Music)GSON.fromJson(p.getString("music", ""), Music.class);
         if (m == null) {
            return Pack.create(false, "invalid_music");
         } else {
            Theme t = Settings.SET.getQuickPresentationTheme();
            if (!p.getString("theme", "").isEmpty() && !p.getString("theme", "").equalsIgnoreCase("null")) {
               try {
                  SerialMap sm = RWObj.read(Utils.base64ToBytes(p.getString("theme", "")), SerialMap.class);
                  t = Theme.create().createFromSerialMap(sm);
               } catch (Exception var7) {
               }
            }

            long id = Utils.generateMillisecondId();
            List<String> list = GeneratePresentation.showRemoteQuickPresentation(m, t, id);
            PresentationViewRemoteControl.staticSleep(id, 3000L);
            return Pack.create(true).put("id", id).put("list", list);
         }
      });
   }

   private Pack changeQuickPresentation(Pack p) {
      try {
         Pack _return = Pack.create(true);
         if (!PresentationViewRemoteControl.staticIsVisible()) {
            return Pack.create(false, "closed");
         } else if (p.getLong("id", -8L) != PresentationViewRemoteControl.staticId()) {
            return Pack.create(false, "closed");
         } else {
            if (!p.getString("blank", "").isEmpty()) {
               SwingUtils.runOnUIThread(() -> PresentationViewRemoteControl.staticBlank(p.getBoolean("blank")));
            } else if (!p.getString("black", "").isEmpty()) {
               SwingUtils.runOnUIThread(() -> PresentationViewRemoteControl.staticBlack(p.getBoolean("black")));
            } else if (p.getBoolean("exit")) {
               SwingUtils.runOnUIThread(PresentationViewRemoteControl::staticExit);
            } else if (p.getInt("index", -1) != -1) {
               SwingUtils.runOnUIThread(() -> PresentationViewRemoteControl.staticSetIndex(p.getInt("index", 0)));
            }

            if (!p.getString("theme", "").isEmpty()) {
               if (p.getString("theme", "").equalsIgnoreCase("null")) {
                  _return.put("theme_id", "null");
                  SwingUtilities.invokeLater(() -> PresentationViewRemoteControl.staticSetTheme(Settings.SET.getQuickPresentationTheme()));
               } else {
                  try {
                     SerialMap sm = RWObj.read(Utils.base64ToBytes(p.getString("theme", "")), SerialMap.class);
                     Theme t = Theme.create().createFromSerialMap(sm);
                     Optional.ofNullable(t).ifPresent(i -> SwingUtilities.invokeLater(() -> PresentationViewRemoteControl.staticSetTheme(i)));
                     _return.put("theme_id", p.getString("theme_id", "null"));
                  } catch (Exception var5) {
                  }
               }
            }

            return _return;
         }
      } catch (Exception var6) {
         return Pack.create(false, "exception");
      }
   }

   private Pack alert(Pack p) {
      return this.defaultPasswordRemoteControlFilter(p, () -> {
         SwingUtilities.invokeLater(() -> {
            AlertPanel.INSTANCE.setText(p.getString("text", "").replace("\n", " "));
            AlertPanel.INSTANCE.setShow(p.getBoolean("state"));
         });
         return Pack.create(true);
      });
   }

   private Pack getAlert(Pack p) {
      return this.defaultPasswordRemoteControlFilter(
         p, () -> Pack.create(true).put("text", AlertPanel.INSTANCE.getText()).put("state", AlertPanel.INSTANCE.isShow())
      );
   }

   private Pack announcementList(Pack p) {
      return this.defaultPasswordRemoteControlFilter(
         p,
         () -> {
            List<Announcement> list = AnnouncementController.getAll();
            List<String> ids = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            List<String> images = new ArrayList<>();
            Function<Announcement, BufferedImage> anToImg = an -> PresentationSlideUtils.createImage(
                  null, an.getText(), an.getTheme(), new Dimension(320, 180), true
               );
            list.forEach(an -> {
               try {
                  images.add(Utils.bytesToBase64(Img.getBytesJPG(anToImg.apply(an), 0.7F)));
                  ids.add(Long.toString(an.id));
                  titles.add(an.getName());
               } catch (Exception var6) {
               }
            });
            Function<List<String>, String> joiningList = l -> l.stream().collect(Collectors.joining("\n"));
            return Pack.create(true).put("ids", joiningList.apply(ids)).put("titles", joiningList.apply(titles)).put("images", joiningList.apply(images));
         }
      );
   }

   private Pack announcement(Pack p) {
      return this.defaultPasswordRemoteControlFilter(
         p,
         () -> {
            if (p.getBoolean("close")) {
               SwingUtils.runOnUIThread(PresentationViewRemoteControl::staticExit);
               return Pack.create(true);
            } else {
               Announcement a = AnnouncementController.getAll().stream().filter(an -> an.id == p.getLong("id", -1L)).findFirst().orElse(null);
               if (a == null) {
                  return Pack.create(false, "id_not_found");
               } else {
                  Presentation presentation = a.createPresentation();
                  if (!PresentationViewRemoteControl.staticSetSinglePresentation(presentation, ScreenDeviceSettings.EnumDeviceType.ANNOUNCEMENT, true)) {
                     long id = Utils.generateMillisecondId();
                     PresentationViewRemoteControl.show(
                        id, new ArrayList<>(Arrays.asList(presentation)), Music.create(), a.getTheme(), ScreenDeviceSettings.EnumDeviceType.ANNOUNCEMENT
                     );
                     PresentationViewRemoteControl.staticSleep(id, 3000L);
                  }

                  return Pack.create(true);
               }
            }
         }
      );
   }

   private Pack mediaList(Pack pack, EnumTabType... types) {
      return this.defaultPasswordRemoteControlMediaFilter(
         pack,
         p -> {
            Collector<CharSequence, ?, String> join = Collectors.joining("\n");
            Pack pReturn = Pack.create(true);

            for (EnumTabType tabType : types) {
               List<ListModelHItem.MyFile> list = MusicMain.staticGetMediaList(tabType);
               pReturn.put(tabType.name() + "-names", list.stream().map(i -> i.name).collect(join))
                  .put(tabType.name() + "-names_normalized", list.stream().map(i -> i.nameNormalized).collect(join))
                  .put(tabType.name() + "-paths", list.stream().map(i -> i.file.getAbsolutePath()).collect(join))
                  .put(tabType.name() + "-isdir", list.stream().map(i -> String.valueOf(i.dir)).collect(join))
                  .put(tabType.name() + "-images", list.stream().map(i -> i.getThumb64(tabType)).collect(join));
            }

            return pReturn;
         }
      );
   }

   private Pack showImage(Pack pack) {
      return this.defaultPasswordRemoteControlMediaFilter(
         pack,
         p -> {
            if (p.getBoolean("close")) {
               SwingUtils.runOnUIThread(PresentationViewRemoteControl::staticExit);
               return Pack.create(true);
            } else {
               String image = p.getString("image", "*:");
               File file;
               if (image.startsWith("bytes64;")) {
                  try {
                     String image64 = image.substring(8);
                     if (image64.trim().isEmpty()) {
                        file = new File("*image-not-found*");
                     } else {
                        file = File.createTempFile("showRemoteBase64", ".png");
                        file.deleteOnExit();
                        byte[] bytes = Utils.base64ToBytes(image64);
                        Dir.saveBytes(bytes, file);
                     }
                  } catch (Exception var6) {
                     file = new File("*image-not-found*");
                  }
               } else {
                  file = new File(image);
                  if (!file.getAbsolutePath().startsWith(Values.PATH_MEDIA_FULL) || !file.exists() || !HItemUtils.isImage(file)) {
                     file = new File("*image-not-found*");
                  }
               }

               Presentation presentation = (new BackgroundImageFile(file)).presentation;
               if (!PresentationViewRemoteControl.staticSetSinglePresentation(presentation, ScreenDeviceSettings.EnumDeviceType.IMAGE, true)) {
                  long id = Utils.generateMillisecondId();
                  PresentationViewRemoteControl.show(
                     id, new ArrayList<>(Arrays.asList(presentation)), Music.create(), presentation.theme, ScreenDeviceSettings.EnumDeviceType.IMAGE
                  );
                  PresentationViewRemoteControl.staticSleep(id, 3000L);
               }

               return Pack.create(true);
            }
         }
      );
   }

   private Pack playlistMedia(Pack p) {
      return this.defaultPasswordRemoteControlFilter(
         p,
         () -> {
            Collector<CharSequence, ?, String> join = Collectors.joining("\n");
            Pack pReturn = Pack.create(true);
            List<IHItem> list = MusicMain.staticGetPlayListMedia();
            pReturn.put(
                  "names",
                  list.stream()
                     .map(fav -> FavoriteUtils.getWSType(fav).equals("fav-url") ? VLCUtils.getYouTubeTitleOffline(fav.getName()) : fav.getName())
                     .collect(join)
               )
               .put("action_names", list.stream().map(Favoritable::getActionName).collect(join))
               .put("ids", list.stream().map(Favoritable::getId).collect(join))
               .put("files", list.stream().map(hitem -> String.valueOf(hitem.getAsFile())).collect(join))
               .put("types", list.stream().map(FavoriteUtils::getWSType).collect(join))
               .put("images", list.stream().map(FavoriteUtils::getWSThumb64).collect(join));
            return pReturn;
         }
      );
   }

   private Pack favoritesList(Pack pack) {
      return this.defaultPasswordRemoteControlMediaFilter(
         pack,
         p -> {
            Collector<CharSequence, ?, String> join = Collectors.joining("\n");
            Pack pReturn = Pack.create(true);
            List<FavoriteItem> list = FavoriteController.getAll();
            list.sort((o1, o2) -> Integer.compare(o1.getPosition(), o2.getPosition()));
            pReturn.put("names", list.stream().map(fi -> fi.getName()).collect(join))
               .put("action_names", list.stream().map(fi -> fi.getFavorite().getActionName()).collect(join))
               .put("ids", list.stream().map(fi -> fi.getFavorite().getId()).collect(join))
               .put("files", list.stream().map(fi -> String.valueOf(fi.getFavorite().getAsFile())).collect(join))
               .put("types", list.stream().map(fi -> FavoriteUtils.getWSType(fi.getFavorite())).collect(join))
               .put("images", list.stream().map(fi -> FavoriteUtils.getWSThumb64(fi.getFavorite())).collect(join));
            return pReturn;
         }
      );
   }

   private Pack defaultVLCFilter(Pack pack, Function<Pack, Pack> _return) {
      return this.defaultPasswordRemoteControlMediaFilter(pack, p -> {
         try {
            return !VLC.ENABLED ? Pack.create(false, "O programa Holyrics não está com o módulo VLC Player ativado.") : _return.apply(p);
         } catch (Exception var3) {
            return Pack.create(false, "exception");
         }
      });
   }

   private Pack vlcStatus(Pack pack) {
      return this.defaultVLCFilter(pack, p -> VLC.PLAYER_CONTROL.fillWSStatus(Pack.create(true)));
   }

   private Pack vlcAction(Pack pack) {
      return this.defaultVLCFilter(
         pack,
         p -> {
            Pack pReturn = Pack.create(true);
            String var2 = p.getString("action", "null");
            switch (var2) {
               case "media":
                  String[] array = p.getString("media-list", "null").split("\n");
                  List<String> medias = new ArrayList<>();
                  if (array.length == 1 && new File(array[0]).isDirectory()) {
                     Stream.of(new File(array[0]).listFiles()).filter(HItemUtils::isMedia).map(File::getAbsolutePath).forEach(medias::add);
                  } else {
                     Stream.of(array)
                        .filter(path -> HItemUtils.check(path, VLCUtils.EnumFileType.MEDIA))
                        .filter(media -> media.startsWith(Values.PATH_MEDIA_FULL))
                        .forEach(medias::add);
                  }

                  VLC.PLAYER_CONTROL.setPlaylist(medias.toArray(new String[medias.size()]), () -> {
                     if (p.getBoolean("play") && !medias.isEmpty()) {
                        VLC.PLAYER_CONTROL.playPause();
                     }
                  });
                  break;
               case "play/pause":
                  VLC.PLAYER_CONTROL.playPause();
                  break;
               case "stop":
                  VLC.PLAYER_CONTROL.stop();
                  break;
               case "next":
                  VLC.PLAYER_CONTROL.skipNext();
                  break;
               case "previous":
                  VLC.PLAYER_CONTROL.skipPrevious();
                  break;
               case "mute":
                  VLC.PLAYER_CONTROL.mute();
                  break;
               case "volume":
                  VLC.PLAYER_CONTROL.volume(p.getInt("volume-value", VLC.PLAYER_CONTROL.getVolume()));
                  break;
               case "position":
                  VLC.PLAYER_CONTROL.position(p.getInt("position-value", VLC.PLAYER_CONTROL.getPosition()));
                  break;
               case "fullscreen":
                  VLC.PLAYER_CONTROL.fullscreen();
                  break;
               case "repeat":
                  VLC.PLAYER_CONTROL.repeat();
                  break;
               default:
                  p.put("error", "action_not_found");
            }

            Utils.sleep(250L);
            return VLC.PLAYER_CONTROL.fillWSStatus(pReturn);
         }
      );
   }

   private Pack sendBytes(Pack pack) {
      return this.defaultVLCFilter(pack, p -> {
         Pack pReturn = Pack.create(true);

         try {
            String name = p.getString("name", ":*:null:*:");
            if (name.equals(":*:null:*:")) {
               return Pack.create(false, I18N.MSG_ERROR_TRY_AGAIN.val());
            } else {
               String extension = p.getString("extension", "");
               File file = new File(Values.PATH_MEDIA_APP + name + "." + extension);
               pReturn.put("fullname", file.getAbsolutePath());
               Dir.mkdirs(file);
               if (!p.getBoolean("finish")) {
                  int count = 0;

                  while (p.getBoolean("starting") && file.exists()) {
                     file = new File(String.format("%s%s (%s).%s", Values.PATH_MEDIA_APP, name, ++count, extension));
                     pReturn.put("fullname", file.getAbsolutePath());
                  }

                  try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                     raf.setLength(p.getLong("length", 1024L));
                     raf.seek(p.getLong("index", 0L));
                     raf.write(Utils.base64ToBytes(p.getString("bytes", "")));
                  }
               }

               if (p.getBoolean("notification")) {
                  File fullname = new File(p.getString("fullname", file.getAbsolutePath()));
                  SwingUtilities.invokeLater(() -> {
                     WebNotification wn = new WebNotification();
                     wn.setIcon(new ImageIcon(Images.load("notifications48.png")));
                     WebButton openFolder = new WebButton(I18N.MSG_OPEN_THE_FOLDER.val());
                     WebButton close = new WebButton(I18N.WORD_CLOSE.val());
                     Arrays.asList(openFolder, close).forEach(b -> {
                        b.setShadeColor(Values.COLOR_TRANSPARENT);
                        b.setFont(b.getFont().deriveFont(1));
                        b.addActionListener(ex -> wn.hidePopup());
                     });
                     openFolder.addActionListener(ex -> Utils.openLocation(fullname));
                     JPanel buttonsPanel = new JPanel(new FlowLayout(2, 8, 0));
                     buttonsPanel.add(openFolder);
                     buttonsPanel.add(close);
                     buttonsPanel.setOpaque(false);
                     JPanel content = new JPanel(new BorderLayout(0, 8));
                     content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                     content.setOpaque(false);
                     content.add(new WebLabel(I18N.MSG_FILE_RECEIVED_APP.val()), "Center");
                     content.add(buttonsPanel, "Last");
                     wn.setContent(content);
                     NotificationManager.showNotification(wn);
                  });
               }

               return pReturn;
            }
         } catch (Exception var19) {
            return Pack.create(false, I18N.MSG_ERROR_TRY_AGAIN.val());
         }
      });
   }

   private Pack sendPlaylistMusic(Pack p) {
      return this.defaultPasswordRemoteControlFilter(p, () -> {
         String[] musics = p.getString("musics", "").split("\n");
         List<Music> list = new ArrayList<>();

         for (String music : musics) {
            Music m = (Music)GSON.fromJson(music, Music.class);
            if (m != null) {
               list.add(m);
            }
         }

         list.forEach(mx -> {
            if (MusicDao.getLazy(mx.getId()) == null) {
               MusicDao.addKeepId(mx);
            }
         });
         SwingUtilities.invokeLater(() -> MusicMain.staticAddPlayList(list, true));
         return Pack.create(true);
      });
   }

   private Pack getCountdownSettings(Pack pack) {
      return this.defaultPasswordRemoteControlFilter(
         pack,
         () -> {
            CountdownSettings set = Settings.SET.getCountdownSettings();
            return Pack.create(true)
               .put("determine", set.isDetermine())
               .put("minuteDetermine", set.getMinuteDetermine())
               .put("secondDetermine", set.getSecondDetermine())
               .put("hourExact", set.getHourExact())
               .put("minuteExact", set.getMinuteExact())
               .put("size", set.getSize())
               .put("textBefore", set.getTextBefore(false))
               .put("textAfter", set.getTextAfter(false));
         }
      );
   }

   private Pack showCountdown(Pack pack) {
      return this.defaultPasswordRemoteControlFilter(pack, () -> {
         CountdownSettings set = Settings.SET.getCountdownSettings();
         CountdownSettings setToShow = new CountdownSettings();
         setToShow.setDetermine(pack.getBoolean("determine"));
         setToShow.setMinuteDetermine(pack.getInt("minuteDetermine", set.getMinuteDetermine()));
         setToShow.setSecondDetermine(pack.getInt("secondDetermine", set.getSecondDetermine()));
         setToShow.setHourExact(pack.getInt("hourExact", set.getHourExact()));
         setToShow.setMinuteExact(pack.getInt("minuteExact", set.getMinuteExact()));
         setToShow.setSize(pack.getInt("size", set.getSize()));
         setToShow.setTextBefore(pack.getString("textBefore", set.getTextBefore(false)));
         setToShow.setTextAfter(pack.getString("textAfter", set.getTextAfter(false)));
         GeneratePresentation.showCountdown(setToShow);
         return Pack.create(true);
      });
   }

   private Pack closeCountdown(Pack pack) {
      return this.defaultPasswordRemoteControlFilter(pack, () -> {
         PresentationViewRemoteControl.staticExitCountdown();
         return Pack.create(true);
      });
   }

   private Pack getCommunicationPanel(Pack p) {
      return this.defaultPasswordRemoteControlFilter(
         p,
         () -> Pack.create(true)
               .put("presentation-text", CommunicationPanel.getPresentationText())
               .put("presentation-show", CommunicationPanel.isPresentationShow())
               .put("presentation-displayAhead", CommunicationPanel.isPresentationDisplayAhead())
               .put("alert-text", CommunicationPanel.getAlertText())
               .put("alert-show", CommunicationPanel.isAlertShow())
               .put("display-clock-bg", CommunicationPanel.isDisplayClockAsBackground())
               .put("display-clock-alert", CommunicationPanel.isDisplayClockOnAlert())
      );
   }

   private Pack communicationPanelAction(Pack p) {
      return this.defaultPasswordRemoteControlFilter(p, () -> {
         String var1x = p.getString("action", "null");
         switch (var1x) {
            case "presentation":
               boolean show = p.getBoolean("show");
               if (show) {
                  CommunicationPanel.setPresentationText(p.getString("text", ""));
               }

               CommunicationPanel.setPresentationDisplayAhead(p.getBoolean("displayAhead"));
               CommunicationPanel.setPresentationShow(show);
               break;
            case "alert":
               boolean show = p.getBoolean("show");
               if (show) {
                  CommunicationPanel.setAlertText(p.getString("text", "").replace("\n", " "));
               }

               CommunicationPanel.setAlertShow(show);
               break;
            case "displayClockAsBackground":
               CommunicationPanel.setDisplayClockBackground(p.getBoolean("value"));
               break;
            case "displayClockOnAlert":
               CommunicationPanel.setDisplayClockAlert(p.getBoolean("value"));
         }

         return Pack.create(true);
      });
   }

   private Pack getObject(Pack pack) {
      return this.defaultPasswordRemoteControlMediaFilter(pack, p -> {
         try {
            String e = p.getString("get", "null");
            switch (e) {
               case "music":
                  Music m = MusicDao.getLazy(p.getLong("id", -1L));
                  ShareFilesApp.MusicApp.Music musicJson = new ShareFilesApp.MusicApp.Music(m);
                  return Pack.create(true).put("music_json", GSON.toJson(musicJson));
               case "announcement":
                  String idx = p.getString("id", "");

                  for (Announcement an : AnnouncementController.getAll()) {
                     if (String.valueOf(an.id).equals(idx)) {
                        BufferedImage anPreview = PresentationSlideUtils.createImage(null, an.getText(), an.getTheme(), SIZE_IMG_PREVIEW, true);
                        byte[] imgBytes = Img.getBytesJPG(anPreview, 0.9F);
                        String img64 = Utils.bytesToBase64(imgBytes);
                        return Pack.create(true).put("id", an.id).put("title", an.getName()).put("image", img64);
                     }
                  }
               case "image_file":
                  Function<File, String> parseToImg64 = filex -> {
                     try {
                        BufferedImage bi;
                        try {
                           bi = ImageIO.read(filex);
                        } catch (Exception var3x) {
                           bi = ThumbMediaController.IMAGE_NOT_FOUND;
                        }

                        bi = Img.resize(bi, SIZE_IMG_PREVIEW);
                        byte[] imgBytes = Img.getBytes(bi);
                        return Utils.bytesToBase64(imgBytes);
                     } catch (Exception var4x) {
                        return null;
                     }
                  };
                  List<String> images = new ArrayList<>();
                  List<String> files = new ArrayList<>();
                  String id = p.getString("id", "");
                  File file;
                  if (id.startsWith(Values.PATH_MEDIA_FULL) && (file = new File(id)).exists()) {
                     if (file.isFile()) {
                        images.add(parseToImg64.apply(file));
                        files.add(file.getAbsolutePath());
                     } else {
                        for (File f : file.listFiles(EnumTabType.IMAGE.fileFilter::test)) {
                           images.add(parseToImg64.apply(f));
                           files.add(f.getAbsolutePath());
                        }
                     }

                     return Pack.create(true).put("files", String.join("\n", files)).put("images", String.join("\n", images));
                  }
                  break;
               case "file_hash_app":
                  String name = p.getString("name", ":*:null:*:");
                  if (name.equals(":*:null:*:")) {
                     return Pack.create(false, "Requisição inválida");
                  }

                  String extension = p.getString("extension", "");
                  File filex = new File(Values.PATH_MEDIA_APP + name + "." + extension);

                  try {
                     return Pack.create(true).put("hash", Dir.getHash(filex)).put("fullname", filex.getAbsolutePath());
                  } catch (Exception var12) {
                     return Pack.create(true).put("hash", "not_found");
                  }
            }

            return Pack.create(false, "Requisição não encontrada");
         } catch (Exception var13) {
            return Pack.create(false, "exception");
         }
      });
   }

   private Pack appMessage(Pack pack) {
      String var2 = pack.getString("type", "");
      switch (var2) {
         case "send":
            try {
               String message = pack.getString("message", "").trim();
               message = StringUtils.maxLength(message, 1024);
               if (!message.isEmpty()) {
                  String name = pack.getString("name", "").trim();
                  name = StringUtils.maxLength(name, 24);
                  String userId = pack.getString("id", "").trim();
                  userId = StringUtils.maxLength(userId, 24);
                  AppMessageController.newMessage(userId, name, message);
               }
            } catch (Exception var7) {
            }

            return Pack.create(true).put("json", AppMessageController.getAsJson(pack));
         case "receive":
            return Pack.create(true).put("json", AppMessageController.getAsJson(pack));
         default:
            return Pack.create(true);
      }
   }
}
