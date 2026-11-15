package com.limagiran.holyrics.playback;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.text.WebTextField;
import com.limagiran.holyrics.control.MusicFavoriteSetController;
import com.limagiran.holyrics.control.TransitionController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.transition.TransitionSettings;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.FFMPEG;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.PresentationCache;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.vlc.VLCUtils;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.NumberEditor;

public class PlayBackUtils {
   private static PlayBackTab TAB_INSTANCE;

   public static BufferedImage getThumb(Dimension size) {
      BufferedImage bi = PresentationCache.get("playback-fav-thumb", size);
      return bi != null ? bi : Img.resize(Images.ICON_MUSIC_VIDEO_32, size);
   }

   public static void run(File file) {
      try {
         if (file == null) {
            return;
         }

         PlayBack playBack = PlayBack.load(file);
         Theme theme = MusicFavoriteSetController.getDefaultTheme(playBack.getMusic().getId());
         if (theme == null) {
            theme = Settings.SET.getTheme();
         }

         PopupUtils.themeChooser(theme, t -> GeneratePresentation.showAutomaticPresentation(playBack, t));
      } catch (Exception var3) {
         Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
      }
   }

   public static String getId(String string) {
      return Values.PATH_MEDIA_AUTOMATIC_PRESENTATIONS + string;
   }

   public static File getFile(String string) {
      return new File(getId(string));
   }

   public static void mkdirs() {
      new File(Values.PATH_MEDIA_AUTOMATIC_PRESENTATIONS).mkdirs();
   }

   public static void openDir() {
      try {
         mkdirs();
         Desktop.getDesktop().open(new File(Values.PATH_MEDIA_AUTOMATIC_PRESENTATIONS));
      } catch (Exception var1) {
         Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
      }
   }

   private static PlayBackTab getTabInstance(PlayBackTab.PlayBackTabListener listener) {
      if (TAB_INSTANCE == null) {
         TAB_INSTANCE = new PlayBackTab(listener);
      }

      return TAB_INSTANCE;
   }

   public static void openTab(PlayBackTab.PlayBackTabListener listener) {
      SwingUtilities.invokeLater(() -> {
         JDialog dialog = new JDialog((Frame)null, true);
         dialog.setDefaultCloseOperation(2);
         dialog.setTitle(I18N.MSG_AUTOMATIC_SHOW.val());
         dialog.setIconImages(Images.HOLYRICS_ICON);
         dialog.setLayout(new BorderLayout());
         WebPanel panel = new WebPanel(new BorderLayout());
         panel.setMargin(16);
         panel.setOpaque(false);
         PlayBackTab tab = getTabInstance(listener);
         tab.owner = dialog;
         panel.add(tab);
         dialog.add(panel);
         dialog.setSize(450, (int)((double)VideoUtils.getSizeDisplay().height * 0.7));
         dialog.setPreferredSize(dialog.getSize());
         dialog.pack();
         dialog.setLocationRelativeTo(null);
         InsertShortcut.addEscToDispose(dialog);
         dialog.setVisible(true);
      });
   }

   public static void create(Consumer<PlayBack> callback) {
      PopupUtils.musicChooser(music -> {
         WebTextField textField = new WebTextField(Dir.removeInvalidChar(music.toString()), 32);
         textField.setFontSize(14);
         SwingUtils.requestFocusOnVisible(textField);
         int op = JOptionPane.showConfirmDialog(null, textField, I18N.WORD_NAME.val(), 2, -1);
         if (op == 0) {
            selectMp3Audio(file -> create(textField.getText(), music, file, callback));
         }
      });
   }

   public static File selectMp3Audio() {
      Settings.EnumSettings2 en = Settings.EnumSettings2.PLAYBACK_SELECT_AUDIO_FILE;
      File file = Dir.windowsFile(I18N.MSG_SELECT_A_MP3_AUDIO.val(), VLCUtils.getInitDir(en.getString()), "Audio File (*.mp3)", ".mp3");
      if (file != null) {
         en.set(file.getParent());
      }

      return file;
   }

   public static void selectMp3Audio(Consumer<File> callback) {
      Optional.ofNullable(selectMp3Audio()).ifPresent(callback);
   }

   public static void changeMp3Audio(PlayBack playback, Runnable callback) {
      Optional.ofNullable(selectMp3Audio()).ifPresent(file -> {
         try {
            playback.setMedia(file);
         } catch (Exception var4) {
            Utils.error(I18N.MSG_ERROR_INVALID_FILE.format(String.valueOf(file)));
            return;
         }

         Optional.ofNullable(callback).ifPresent(Runnable::run);
      });
   }

   private static void create(String name, Music music, File media, Consumer<PlayBack> callback) {
      try {
         name = Dir.removeInvalidChar(name);
         File file = getFile(name + ".ap");
         int i = 1;

         while (file.exists()) {
            file = getFile(name + " (" + i++ + ").ap");
         }

         PlayBack pb = PlayBack.create(file, music);
         pb.setMedia(media);
         pb.save();
         EditPlayBackDialog.main(pb, () -> callback.accept(pb));
      } catch (Exception var7) {
         Utils.error(I18N.MSG_ERROR_INVALID_FILE.format(String.valueOf(media)));
      }
   }

   protected static void setStartAt(int current, Consumer<Integer> callback) {
      int m = (int)TimeUnit.MILLISECONDS.toMinutes((long)current);
      int s = (int)TimeUnit.MILLISECONDS.toSeconds((long)current);
      SpinnerNumberModel mModel = new SpinnerNumberModel(Utils.range(m, 0, 99).intValue(), 0, 99, 1);
      SpinnerNumberModel sModel = new SpinnerNumberModel(Utils.range(s % 60, 0, 59).intValue(), 0, 59, 1);
      SpinnerNumberModel msModel = new SpinnerNumberModel(Utils.range(current % 1000, 0, 999).intValue(), 0, 999, 1);
      WebSpinner mSpinner = new WebSpinner(mModel);
      WebSpinner sSpinner = new WebSpinner(sModel);
      WebSpinner msSpinner = new WebSpinner(msModel);
      sSpinner.setEditor(new NumberEditor(sSpinner, "00"));
      msSpinner.setEditor(new NumberEditor(msSpinner, "000"));
      WebLabel mLabel = new WebLabel("m");
      WebLabel sLabel = new WebLabel("s");
      WebLabel msLabel = new WebLabel("ms");
      Arrays.asList(mLabel, sLabel, msLabel).forEach(lbl -> lbl.setMargin(0, 3, 0, 8));
      List<JComponent> components = Arrays.asList(mSpinner, mLabel, sSpinner, sLabel, msSpinner, msLabel);
      components.forEach(c -> c.setFont(new Font("Courier New", 0, 16)));
      WebPanel panel = new WebPanel();
      panel.setLayout(new BoxLayout(panel, 2));
      components.forEach(panel::add);
      int op = JOptionPane.showConfirmDialog(null, panel, I18N.WORD_START.val(), 2, -1);
      if (op == 0) {
         Arrays.asList(mSpinner, sSpinner, msSpinner).forEach(SwingUtils::commitEdit);
         int value = msModel.getNumber().intValue();
         value = (int)((long)value + TimeUnit.SECONDS.toMillis((long)sModel.getNumber().intValue()));
         value = (int)((long)value + TimeUnit.MINUTES.toMillis((long)mModel.getNumber().intValue()));
         callback.accept(value);
      }
   }

   public static void exportToVideo(PlayBack playback, Theme theme, File out, Dimension size) {
      boolean paintTransition = true;
      boolean paintAdditionalInfo = true;
      ExportMediaWorker.main(
         playback,
         file -> PopUpWorker.exe(
               new MySwingWorker<Void, String>(null) {
                  protected Void doInBackground() throws Exception {
                     PlayBackPlayerMiniShow mp = PlayBackPlayerMiniShow.getInstance();
                     mp.setMedia(file, true);

                     for (long delay = 0L; mp.getDuration() <= 0 && delay < 2000L; delay += 100L) {
                        Utils.sleep(100L);
                     }

                     if (mp.getDuration() <= 0) {
                        Utils.error(I18N.MSG_ERROR_INVALID_FILE.format(String.valueOf(file)));
                        return null;
                     } else {
                        final File path = Files.createTempDirectory("makeVideoHolyrics").toFile();
                        int index = 0;
                        int time = 0;
                        int sleep = 40;
                        int duration = mp.getDuration() * 1000;
                        Function generateFile = i -> new File(path, BigInteger.valueOf((long)i.intValue()).toString(36) + ".png");
                        this.process(Arrays.asList("Criando imagens"));
                        size.width = size.width - size.width % 2;
                        size.height = size.height - size.height % 2;
                        List list = new ArrayList();
                        ScreenDeviceSettings.EnumDeviceType deviceType = ScreenDeviceSettings.EnumDeviceType.MUSIC;
                        TransitionSettings ts = TransitionController.get(deviceType);
                        ScreenDeviceSettings sds = Settings.SET.getScreenDeviceSettings(1);
                        TransitionEffectRenderer effect = new TransitionEffectRenderer(deviceType, sds, ts, theme);
                        MyImageTransitionRenderer images = new MyImageTransitionRenderer();
                        images.setTransitionEffect(effect);
                        effect.prepareAnimation(images);
                        BufferedImage lastBi = null;
                        boolean effectPrepare = false;

                        while (time < duration) {
                           try {
                              int idx = playback.getIndexAt((long)time);
                              BufferedImage bi = PresentationSlideUtils.createImage(
                                 sds, playback.getPresentationAtFromIndex(idx, theme), size, !theme.getBackground().isVideo()
                              );
                              if (ts.isEnabled()) {
                                 int previousIdx = playback.getIndexAt((long)(time - ts.getDuration() + 1));
                                 if (previousIdx != idx) {
                                    if (effectPrepare) {
                                       effectPrepare = false;
                                       effect.prepareAnimation(images);
                                    }

                                    int transitionProgress = time - playback.getStartAt(idx);
                                    Presentation p1 = playback.getPresentationAtFromIndex(previousIdx, theme);
                                    Presentation p2 = playback.getPresentationAtFromIndex(idx, theme);
                                    p1.text.setSkipPaintAddInfo(true);
                                    images.setCurrentImage(PresentationSlideUtils.createImage(sds, p1, size, false));
                                    p2.text.setSkipPaintAddInfo(true);
                                    images.setOtherImage(PresentationSlideUtils.createImage(sds, p2, size, false));
                                    bi = effect.createImageWithBackground(images, p2, (long)transitionProgress);
                                 } else {
                                    effectPrepare = true;
                                 }
                              }

                              if (bi != lastBi) {
                                 lastBi = bi;
                                 File file = (File)generateFile.apply(index++);
                                 ImageIO.write(bi, "png", file);
                                 list.add(new Object[]{file, 40});
                              } else {
                                 Object[] map = (Object[])list.get(list.size() - 1);
                                 map[1] = Integer.parseInt(map[1].toString()) + 40;
                              }

                              time += 40;
                              this.setProgress(Utils.range(Utils.getPercentageInt((double)time, (double)duration), 0, 100));
                           } catch (Exception var24) {
                           }
                        }

                        Object[][] map = new Object[list.size()][2];

                        for (int i = 0; i < list.size(); i++) {
                           map[i] = (Object[])list.get(i);
                           map[i][1] = (double)Utils.intOrElse(map[i][1], 1) / 1000.0;
                        }

                        final FFMPEG ffmpeg = new FFMPEG();
                        FFMPEG.FFMPEGListener listener = new FFMPEG.FFMPEGListener() {
                           @Override
                           public void progress(double value) {
                              if (!isCancelled()) {
                                 setProgress(Utils.range((int)value, 0, 100));
                              }
                           }

                           @Override
                           public void progress(String msg) {
                              if (!isCancelled()) {
                                 process(Arrays.asList(msg));
                              }
                           }

                           @Override
                           public void error(String msg) {
                              if (!isCancelled()) {
                                 Utils.sleep(1000L);
                                 SwingUtilities.invokeLater(() -> Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val()));
                              }

                              ffmpeg.kill();
                              Dir.deleteDir(path);
                           }

                           @Override
                           public void success() {
                              if (!isCancelled()) {
                                 Utils.sleep(1000L);
                                 SwingUtilities.invokeLater(() -> {
                                    if (Utils.confirm(I18N.MSG_SAVED_SUCCESSFULLY.val() + "\n" + I18N.MSG_OPEN_FILE_LOCATION.question())) {
                                       Utils.openLocation(out);
                                    }
                                 });
                              }

                              Dir.deleteDir(path);
                           }
                        };
                        if (!theme.getBackground().isVideo()) {
                           this.process(Arrays.asList("Criando vídeo..."));
                           ffmpeg.makeVideo(map, file, size, 30, out, listener);
                        } else {
                           if (!(theme.getBackground() instanceof VLCUtils.MRLtoVLC)) {
                              WebLafUtils.notificationErrorDelay(I18N.MSG_EXCEPTION_VIDEO_NOT_FOUND.val(), 5L);
                              return null;
                           }

                           String mrl = ((VLCUtils.MRLtoVLC)theme.getBackground()).getMRLtoVLC();
                           if (mrl == null) {
                              return null;
                           }

                           File videoFile = new File(mrl);
                           if (!videoFile.exists()) {
                              WebLafUtils.notificationErrorDelay(I18N.MSG_EXCEPTION_VIDEO_NOT_FOUND.val(), 5L);
                           }

                           this.process(Arrays.asList("Preparando plano de fundo..."));
                           File tmp = File.createTempFile("playback-enlarge", ".mp4");
                           if (!ffmpeg.enlarge(videoFile, tmp, duration / 1000 + 1, 45)) {
                              Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
                              return null;
                           }

                           this.process(Arrays.asList("Criando vídeo..."));
                           ffmpeg.addImagesAndAudioInVideo(tmp, map, file, size, duration / 1000 + 1, out, listener);
                        }

                        return null;
                     }
                  }
               },
               true
            )
      );
   }
}
