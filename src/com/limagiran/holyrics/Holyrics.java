package com.limagiran.holyrics;

import com.alee.global.StyleConstants;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.filechooser.WebFileChooserStyle;
import com.alee.laf.list.WebListStyle;
import com.alee.laf.tooltip.WebTooltipStyle;
import com.alee.managers.language.LanguageManager;
import com.alee.managers.tooltip.WebCustomTooltipStyle;
import com.limagiran.holyrics.control.AlertController;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.EnumAvailableVideos;
import com.limagiran.holyrics.util.GeneratePresentationThreadUtils;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.KeyboardUtils;
import com.limagiran.holyrics.util.Log;
import com.limagiran.holyrics.util.MediaPlayerUtils;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.PHPUtils;
import com.limagiran.holyrics.util.PopUpOpenProgram;
import com.limagiran.holyrics.util.PresentationRendererUtils;
import com.limagiran.holyrics.util.RW;
import com.limagiran.holyrics.util.SQLite;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.UDPServer;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.view.BibleMain;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.view.PresentationJDialog;
import com.limagiran.holyrics.view.ScheduleView;
import com.limagiran.holyrics.view.ThemeView;
import com.limagiran.holyrics.view.WallpaperView;
import com.limagiran.holyrics.vlc.VLCUtils;
import com.limagiran.holyrics.webservice.HolyricsWS;
import com.limagiran.holyrics.webservice.RemoteControlBibleWS;
import java.awt.Color;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Stream;
import javafx.embed.swing.JFXPanel;
import javax.swing.JComponent;

public class Holyrics {
   private static final JFXPanel FX = new JFXPanel();

   public static void main(String[] args) {
      try {
         System.setProperty("jna.encoding", "UTF8");
         MyProperties.init();
         locale();
         sqliteFix();
         tempFileDelete();
         applyLAF();
         if (SQLite.open()) {
            setup();
            PopUpOpenProgram popup = PopUpOpenProgram.main();
            loadFilesAndUI(popup);
            popup.setProgress("Holyrics");
            openServices();
            startThreadWhile();
            System.out.println("Dev: false");
            MusicMain.main(true, popup);
         } else {
            Utils.error(I18N.WORD_ERROR.val());
            System.exit(0);
         }
      } catch (Exception var2) {
         Log.send(var2);
         Utils.error(I18N.WORD_ERROR.val());
         System.exit(0);
      }
   }

   public static void applyLAF() {
      if (!WebLookAndFeel.isInstalled()) {
         DateLG.SDF_DT_FS.toString();
         SwingUtils.setupUIDefaults();
         SwingUtils.runOnUIThread(WebLookAndFeel::install);
         StyleConstants.topBgColor = new Color(250, 250, 250);
         StyleConstants.bottomBgColor = new Color(236, 236, 236);
         StyleConstants.rolloverDarkBorderOnly = true;
         WebListStyle.highlightRolloverCell = true;
         WebListStyle.selectionShadeWidth = 2;
         WebFileChooserStyle.dividerLocation = 220;
         WebTooltipStyle.trasparency = 0.95F;
         WebCustomTooltipStyle.trasparency = WebTooltipStyle.trasparency;
         WebTooltipStyle.textColor = new Color(248, 248, 248);
         WebCustomTooltipStyle.textColor = WebTooltipStyle.textColor;
         if (Locale.getDefault().toString().equalsIgnoreCase("pt_pt")) {
            Locale.setDefault(new Locale("pt", "BR"));
         }

         SwingUtils.hackHTMLBase64();
      }
   }

   public static void loadFilesAndUI(PopUpOpenProgram popup) {
      popup.setProgress(I18N.WORD_SETTINGS.val());
      Settings.SET.hashCode();
      Dir.loadFileChooser();
      Utils.sleep(500L);
      VLCUtils.load(popup);
      popup.setProgress(I18N.WORD_MUSICS.val());
      MusicDao.getAll();
      popup.setProgress(I18N.WORD_BIBLE.val());
      BibleMain.load();
      popup.setProgress(I18N.WORD_VIDEO.val());
      EnumAvailableVideos.load();
      popup.setProgress(I18N.WORD_SCHEDULE.val());
      ScheduleView.load();
      popup.setProgress(I18N.WORD_WALLPAPER.val());
      WallpaperView.start();
      popup.setProgress(I18N.WORD_ANNOUNCEMENT.val());
      Announcement.load();
      PresentationRendererUtils.start();
      GeneratePresentationThreadUtils.start();
      popup.setProgress(I18N.WORD_UPDATE.val());
      Update.apply(popup);
      popup.setProgress(I18N.WORD_LIBRARY.val());
      ThumbMediaController.load(popup);
      popup.setProgress(I18N.WORD_THEMES.val());
      ThemeController.load(popup);
      PHPUtils.open();
      KeyboardUtils.startCapture();
      HolyricsStreaming.clear();
   }

   private static void openServices() {
      UDPServer.open();
      RemoteControlBibleWS.open();
      HolyricsWS.open();
   }

   private static void startThreadWhile() {
      Utils.startThreadDaemon(() -> {
         int count = 0;
         int lastMinute = 0;

         while (true) {
            Utils.sleep(2000L);
            Calendar c = Calendar.getInstance();
            int _lastMinute = c.get(11) * 60 + c.get(12);
            if (_lastMinute > lastMinute) {
               lastMinute = _lastMinute;
               AlertController.fireMinuteEvent(_lastMinute);
               Settings.SET.getClockSettings().update();
            }

            if (count % 5 == 0) {
               BooleanOptional.runIfTrue(!RemoteControlBibleWS.isOpen(), RemoteControlBibleWS::open);
               BooleanOptional.runIfTrue(!HolyricsWS.isOpen(), HolyricsWS::open);
            }

            BooleanOptional.runIfTrue(count % 12 == 0 && !VideoUtils.staticIsAnyVisible(), System::gc);
            BooleanOptional.runIfTrue(count % 60 == 0, System::gc);
            BooleanOptional.runIfTrue(count++ % 15 == 0, () -> {
               LanguageManager.getLanguageListeners().forEach(LanguageManager::removeLanguageListener);
               LanguageManager.getComponentLanguageListeners().forEach((component, listener) -> {
                  if (component instanceof JComponent) {
                     LanguageManager.removeLanguageListener((JComponent)component);
                  }
               });
            });
         }
      });
   }

   private static void sqliteFix() {
      try {
         File temp = File.createTempFile("prefix", ".hly");
         Stream.of(temp.getParentFile().listFiles())
            .filter(f -> f.getName().startsWith("sqlite-3.8.11.2"))
            .filter(f -> f.getName().endsWith(".dll"))
            .forEach(File::delete);
         temp.delete();
      } catch (Exception var1) {
      }
   }

   private static void tempFileDelete() {
      try {
         Stream.of(new File("").getAbsoluteFile().listFiles())
            .filter(f -> f.getName().startsWith("holyrics-thumbnail-tmp"))
            .filter(f -> f.getName().endsWith(".jpg"))
            .forEach(File::delete);
         File temp = File.createTempFile("prefix", ".hly");
         temp.deleteOnExit();
         Stream.of(temp.getParentFile().listFiles()).filter(f -> {
            String n = f.getName();
            return n.startsWith("holyrics-apmedia") && n.endsWith(".mp3") || n.startsWith("playback-enlarge") && n.endsWith(".mp4");
         }).forEach(File::delete);
         temp.delete();
         temp = Files.createTempDirectory("makeVideoHolyrics2").toFile();
         temp.deleteOnExit();
         Stream.of(temp.getParentFile().listFiles()).filter(f -> f.getName().startsWith("makeVideoHolyrics") && f.isDirectory()).forEach(Dir::deleteDir);
         temp.delete();
      } catch (Exception var1) {
      }
   }

   private static void locale() {
      String prop = MyProperties.EnumProperties.LOCALE.getString();
      if (!prop.equals(".default") && !prop.trim().isEmpty()) {
         Locale.setDefault(Settings.EnumLanguage.getOrDefault(prop));
         LanguageManager.setLanguage(Locale.getDefault().getLanguage());
      }
   }

   private static void setup() {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> PHPUtils.close(2000L)));
      Utils.startThreadDaemon(
         () -> {
            PrintStream ps = new PrintStream(System.err) {
               private long lastTimeError;
               private long countError;

               @Override
               public void print(String x) {
                  super.print(x);
                  if (String.valueOf(x).contains("java.lang.OutOfMemoryError")) {
                     MediaPlayerUtils.stop(ThemeView.MEDIA_PLAYER);
                     MediaPlayerUtils.stop(PresentationJDialog.MEDIA_PLAYER);
                     MediaPlayerUtils.stop();
                     System.gc();
                     String msg = "Oh, desculpe! Ocorreu um erro interno e o vídeo precisou ser pausado.";
                     WebLafUtils.notificationErrorDelay(
                        "<html><div style='width:300px; text-align:justify;font-size:16px'>Oh, desculpe! Ocorreu um erro interno e o vídeo precisou ser pausado.",
                        10L
                     );
                  }
               }
            };
            System.setErr(ps);
         }
      );
   }

   public static void openPluginNewThread() {
      Utils.startThreadDaemon(Holyrics::openPlugin);
   }

   public static void openPlugin() {
      File pluginFile = new File("Holyrics-plugin.exe");
      if (!pluginFile.exists()) {
         exportPlugin(pluginFile);
      }

      Utils.openFile(pluginFile);
   }

   private static void exportPlugin(File file) {
      try {
         byte[] bytes = RW.readJar("/com/limagiran/holyrics/file/holyrics-plugin");
         Dir.saveBytes(bytes, file);
      } catch (Exception var2) {
      }
   }
}
