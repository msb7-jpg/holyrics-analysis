package com.limagiran.holyrics.hitem;

import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyData;
import com.limagiran.bible.Bible;
import com.limagiran.bible.Verse;
import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.control.FavoriteController;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.event.BibleFastSearchListener;
import com.limagiran.holyrics.favorite.FavoriteUtils;
import com.limagiran.holyrics.hitem.item.HItemAnnouncement;
import com.limagiran.holyrics.hitem.item.HItemPlayBack;
import com.limagiran.holyrics.hitem.item.HItemURL;
import com.limagiran.holyrics.hitem.item.HItemVerse;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.FFMPEG;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.BibleMain;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.vlc.VLC;
import com.limagiran.holyrics.vlc.VLCUtils;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public interface HItemUtils {
   FFMPEG FFMPEG_OBJ = new FFMPEG();
   ImageIcon IC_OPEN_LOCATION = new ImageIcon(Images.load("open16.png"));
   ImageIcon IC_PLAY = new ImageIcon(Images.load("play16.png"));
   ImageIcon IC_PLAY_SELECT_FILES = new ImageIcon(Images.load("viewModule16.png"));
   ImageIcon IC_PLAY_SELECT_ONE = new ImageIcon(Images.load("file16.png"));
   ImageIcon IC_ADD_PLAY_LIST_MEDIA = new ImageIcon(Images.load("videoLibrary16.png"));
   ImageIcon IC_ADD_FAVORITE = new ImageIcon(Images.load("favorite16.png"));

   IHItem getInstance();

   static boolean check(String path, VLCUtils.EnumFileType fileType) {
      String tmp = path.toLowerCase();
      return Stream.of(fileType.filter).anyMatch(tmp::endsWith);
   }

   static boolean check(File f, VLCUtils.EnumFileType fileType) {
      return check(f.getAbsolutePath(), fileType);
   }

   static boolean isAudio(File f) {
      return check(f, VLCUtils.EnumFileType.AUDIO);
   }

   static boolean isVideo(File f) {
      return check(f, VLCUtils.EnumFileType.VIDEO);
   }

   static boolean isMedia(File f) {
      return check(f, VLCUtils.EnumFileType.MEDIA);
   }

   static boolean isImage(File f) {
      return check(f, VLCUtils.EnumFileType.IMAGE);
   }

   static void run(boolean immediate, File... files) {
      try {
         if (MusicMain.INSTANCE == null || files.length <= 0) {
            return;
         }

         if (files.length == 1 && files[0].isDirectory()) {
            files = files[0].listFiles();
            Predicate<File> predicate = isMedia(files[0]) ? HItemUtils::isMedia : HItemUtils::isImage;
            files = Stream.of(files).filter(predicate).toArray(File[]::new);
            if (files.length <= 0) {
               Utils.error(I18N.MSG_ERROR_EMPTY_LIST.val());
               return;
            }

            if (!immediate) {
               ShowGridDialog.main(files);
               return;
            }
         }

         if (isImage(files[0])) {
            GeneratePresentation.showImages(null, false, false, 7, files);
            return;
         }

         if (VLC.ENABLED && isMedia(files[0])) {
            String[] medias = Arrays.stream(files).map(File::getAbsolutePath).toArray(String[]::new);
            VLC.PLAYER_CONTROL.setPlaylist(medias, VLC.PLAYER_CONTROL::playPause);
            MusicMain.staticExpandPlayerPanel();
         }
      } catch (Exception var3) {
      }
   }

   static WebMenuItem getOpenLocationMenuItem(Component parent, File file) {
      WebMenuItem _return = new WebMenuItem(I18N.MSG_OPEN_FILE_LOCATION.val(), IC_OPEN_LOCATION);
      _return.addActionListener(evt -> openLocation(parent, file));
      return _return;
   }

   static void openLocation(Component parent, File file) {
      if (!file.exists()) {
         Utils.error(parent, I18N.MSG_ERROR_FILE_NOT_FOUND_X.format(file.getAbsolutePath()));
      } else {
         try {
            if (file.isDirectory()) {
               Desktop.getDesktop().open(file);
            } else {
               Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
            }
         } catch (Exception var3) {
            Utils.error(parent, I18N.MSG_ERROR_TRY_AGAIN.val());
         }
      }
   }

   static void setupPopupMenuAction(WebPopupMenu popupMenu, String actionName, Consumer<Boolean> itemAction, List<File> listIfDirectory, boolean setAccelerator) {
      WebMenuItem action = new WebMenuItem(actionName, IC_PLAY);
      action.addActionListener(ae -> itemAction.accept(true));
      BooleanOptional.runIfTrue(setAccelerator, () -> action.setAccelerator(Hotkey.F6));
      popupMenu.add(action);
      if (!listIfDirectory.isEmpty()) {
         WebMenuItem actionSelectFiles = new WebMenuItem(I18N.MSG_SELECT_FILES.val(), IC_PLAY_SELECT_FILES);
         actionSelectFiles.addActionListener(evt -> itemAction.accept(false));
         BooleanOptional.runIfTrue(setAccelerator, () -> actionSelectFiles.setAccelerator(new HotkeyData(false, false, true, 117)));
         popupMenu.add(actionSelectFiles);
         WebMenu actionList = new WebMenu(actionName + " ", IC_PLAY_SELECT_ONE);
         SwingUtils.addHtmlTooltip(actionList, "(" + I18N.WORD_FILE.val() + ")");
         listIfDirectory.forEach(f -> {
            WebMenuItem actionItem = new WebMenuItem(f.getName());
            actionItem.addActionListener(evt -> run(true, f));
            actionList.add(actionItem);
         });
         popupMenu.add(actionList);
      }
   }

   static void setupPopupMenuAddPlaylistPlayer(WebPopupMenu popup, File... files) {
      if (VLC.ENABLED && files != null) {
         WebMenuItem addPlaylist = new WebMenuItem(I18N.MSG_ADD_PLAY_LIST.val(), new ImageIcon(Images.ICON_PLAY_LIST_PLAYER_16));
         SwingUtils.addHtmlTooltip(addPlaylist, " (Player)");
         List<String> medias = new ArrayList<>();

         for (File file : files) {
            try {
               if (file.isDirectory()) {
                  Stream.of(file.listFiles()).map(File::getAbsolutePath).forEach(medias::add);
               } else {
                  medias.add(file.getAbsolutePath());
               }
            } catch (Exception var9) {
            }
         }

         addPlaylist.addActionListener(evt -> VLC.PLAYER_CONTROL.addPlaylist(medias.toArray(new String[medias.size()])));
         popup.add(addPlaylist);
      }
   }

   static void setupPopupMenuAddPlaylistEvent(WebPopupMenu popup, ActionListener action) {
      WebMenuItem addPlaylist = new WebMenuItem(I18N.MSG_ADD_PLAY_LIST.val(), IC_ADD_PLAY_LIST_MEDIA);
      SwingUtils.addHtmlTooltip(addPlaylist, " (" + I18N.WORD_WORSHIP_AND_EVENT.val() + ")");
      addPlaylist.addActionListener(action);
      popup.add(addPlaylist);
   }

   static void setupPopupMenuAddFavorite(JPopupMenu popup, IHItem hitem) {
      WebMenuItem addFavorite = new WebMenuItem(I18N.MSG_ADD_TO_FAVORITE.val(), IC_ADD_FAVORITE);
      addFavorite.addActionListener(evt -> {
         ImageIcon icon = new ImageIcon(hitem.getThumb(ThumbMediaController.getThumbSize()));
         String name = hitem.getName();
         if (hitem instanceof HItemPlayBack) {
            name = ((HItemPlayBack)hitem).getNameWithoutExtension();
         }

         FavoriteUtils.getNameAndCreate(null, hitem, icon, name);
      });
      addFavorite.setEnabled(!FavoriteController.contains(hitem));
      popup.add(addFavorite);
   }

   static void setupPopupMenuAddHItem(
      WebPopupMenu popup, Component parent, Predicate<? super Announcement> announcementPredicateRemoveIf, BiConsumer<String, IHItem> consumerNameAndHItem
   ) {
      List<Announcement> anList = AnnouncementController.getAll();
      anList.removeIf(announcementPredicateRemoveIf);
      if (!anList.isEmpty()) {
         WebMenu addAnnountement = new WebMenu(I18N.MSG_FAVORITE_ADD_ANNOUNCEMENT.val(), new ImageIcon(Images.ICON_IMAGE_16));
         anList.forEach(an -> {
            WebMenuItem anMenuItem = new WebMenuItem(StringUtils.maxLength(an.getName(), 16, true));
            anMenuItem.addActionListener(e -> consumerNameAndHItem.accept(anMenuItem.getText(), new HItemAnnouncement(an.id)));
            addAnnountement.add(anMenuItem);
         });
         popup.add(addAnnountement);
      }

      if (VLC.ENABLED) {
         WebMenuItem addURL = new WebMenuItem(I18N.MSG_FAVORITE_ADD_URL.val(), new ImageIcon(Images.ICON_WEB_16));
         addURL.addActionListener(e -> VLCUtils.openInputLink(parent, url -> {
               String title = VLCUtils.getYouTubeTitle(url);
               if (title.trim().isEmpty()) {
                  title = "URL Link";
               }

               consumerNameAndHItem.accept(title, new HItemURL(url));
            }));
         popup.add(addURL);
      }

      WebMenuItem addVerse = new WebMenuItem(I18N.MSG_FAVORITE_ADD_VERSE.val(), new ImageIcon(Images.ICON_BIBLE_16));
      addVerse.addActionListener(e -> BibleMain.getInstance().fastSearchVerse(new BibleFastSearchListener() {
            @Override
            public Bible getBible() {
               return BibleMain.getInstance().getBible();
            }

            @Override
            public void fastSearchVerseFound(int book, int chapter, int verse) {
               Utils.startThreadDaemon(() -> SwingUtilities.invokeLater(() -> {
                     Verse _verse = BibleMain.getInstance().getBible().getVerse(book, chapter, verse, true);
                     consumerNameAndHItem.accept(_verse.getFullDesc(), new HItemVerse(_verse));
                  }));
            }
         }));
      popup.add(addVerse);
   }

   static List<File> getListIfDirectory(File asFile, Predicate<File> fileFilter) {
      if (asFile != null && asFile.isDirectory()) {
         File[] files = asFile.listFiles();
         return files == null ? Collections.EMPTY_LIST : Stream.of(files).filter(fileFilter).collect(Collectors.toList());
      } else {
         return Collections.EMPTY_LIST;
      }
   }
}
