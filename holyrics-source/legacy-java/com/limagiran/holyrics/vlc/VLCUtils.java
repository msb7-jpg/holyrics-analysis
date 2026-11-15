package com.limagiran.holyrics.vlc;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Download;
import com.limagiran.holyrics.util.PopUpOpenProgram;
import com.limagiran.holyrics.util.RW;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.zip.Zip;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;
import sevenzip.LzmaAlone;
import uk.co.caprica.vlcj.filter.AudioFileFilter;
import uk.co.caprica.vlcj.filter.VideoFileFilter;

public class VLCUtils implements Values {
   private static final ExecutorService VLC_EXECUTOR = Executors.newSingleThreadExecutor();
   public static final String VLC_PATH = new File("Holyrics/VLC").getAbsoluteFile().getAbsolutePath();

   public static void load(PopUpOpenProgram popup) {
      if (!checkPath()) {
         popup.setProgress("VLC Media Player");
         File tempLZMA = null;
         File tempZIP = null;

         try {
            tempLZMA = File.createTempFile("vlc-export-lzma", ".zip");
            tempZIP = File.createTempFile("vlc-export-zip", ".zip");
            tempLZMA.deleteOnExit();
            tempZIP.deleteOnExit();
            if (!exportFromJar("/com/limagiran/holyrics/file/VLC-lzma.zip", tempLZMA.getAbsoluteFile())) {
               throw new Exception();
            }

            LzmaAlone.main(new String[]{"d", tempLZMA.getAbsolutePath(), tempZIP.getAbsolutePath()});
            Utils.sleep(1000L);
            Zip.unzip(new File("Holyrics\\").getAbsoluteFile(), tempZIP);
            Utils.sleep(1000L);
         } catch (Exception var7) {
            popup.setProgress("<html><span style='color:red'>[ERRO] Não foi possível instalar o VLC");
            Utils.sleep(2000L);
         } finally {
            Optional.ofNullable(tempLZMA).ifPresent(File::delete);
            Optional.ofNullable(tempZIP).ifPresent(File::delete);
         }
      }
   }

   public static boolean checkPath() {
      return Stream.of("vlc.exe", "libvlc.dll", "libvlccore.dll")
         .map(path -> new File(VLC_PATH + File.separator + path).getAbsoluteFile())
         .allMatch(File::exists);
   }

   private static void defaultConsumer(List<File> list, Consumer<List<File>> callback) {
      BooleanOptional.runIfTrue(!list.isEmpty(), () -> {
         callback.accept(list);
         Settings.EnumSettings2.VLC_UTILS_LAST_DIR_MEDIA.set(list.get(0).getParent());
      });
   }

   private static void defaultConsumer(File file, Consumer<File> callback) {
      Optional.ofNullable(file).ifPresent(f -> {
         callback.accept(f);
         Settings.EnumSettings2.VLC_UTILS_LAST_DIR_MEDIA.set(f.getParent());
      });
   }

   public static void openSelect(VLCUtils.EnumFileType en, Consumer<File> callback) {
      defaultConsumer(Dir.windowsFile(I18N.WORD_OPEN.val(), getInitDirMedia(), en.getDesc(), en.getFilter()), callback);
   }

   public static void openSelectMultiple(VLCUtils.EnumFileType en, Consumer<List<File>> callback) {
      defaultConsumer(Dir.windowsFileMultiple(I18N.WORD_OPEN.val(), getInitDirMedia(), en.getDesc(), en.getFilter()), callback);
   }

   private static void openSelectPlaylist(Consumer<List<String>> callback) {
      Optional.ofNullable(Dir.windowsFile(I18N.WORD_OPEN.val(), getInitDirPlaylist(), "Holyrics Playlist File", ".hpl")).ifPresent(file -> {
         try {
            Settings.EnumSettings2.VLC_UTILS_LAST_DIR_PLAYLIST.set(file.getParent());
            String playlist = RW.read(file);
            callback.accept(Arrays.asList(playlist.split("\n")));
         } catch (Exception var3) {
            Utils.error(I18N.MSG_ERROR_INVALID_FILE.format(file.getName()));
         }
      });
   }

   private static void openSavePlaylist(List<String> playlist) {
      Optional.ofNullable(Dir.windowsSave(I18N.WORD_SAVE.val(), getInitDirPlaylist(), "Holyrics Playlist File (*.hpl)", ".hpl")).ifPresent(file -> {
         try {
            Settings.EnumSettings2.VLC_UTILS_LAST_DIR_PLAYLIST.set(file.getParent());
            RW.write(file, playlist.stream().collect(Collectors.joining("\n")));
            Utils.msg(I18N.MSG_SAVED_SUCCESSFULLY.val());
         } catch (Exception var3) {
            Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
         }
      });
   }

   public static void openInputLink(Component parent, Consumer<String> consumer) {
      String clipboard = Utils.getClipboard();
      String init = "";
      if (isYoutube(clipboard) || isLink(clipboard)) {
         init = clipboard;
      }

      JTextField input = new JTextField(init, 48);
      SwingUtils.toEndPosition(input);
      SwingUtils.setupCCPUAndRedo(input);
      WebPanel panel = new WebPanel(new BorderLayout(0, 8));
      String alertYoutube = "<br><br><span style='font-size:95%;color:770000'>Alguns links do YouTube podem não funcionar por estarem protegidos por direitos autorais. <br>(clipes de músicas geralmente não funcionam)</span>";
      panel.add(
         new WebLabel(
            "<html>"
               + I18N.MSG_VLC_ENTER_URL.val().replace("\n", "<br>")
               + "<br><br><span style='font-size:95%;color:770000'>Alguns links do YouTube podem não funcionar por estarem protegidos por direitos autorais. <br>(clipes de músicas geralmente não funcionam)</span>"
         ),
         "First"
      );
      panel.add(input, "Center");
      SwingUtils.requestFocusOnVisible(input);
      int op = JOptionPane.showConfirmDialog(parent, panel, "URL", 2, -1);
      if (op == 0) {
         String link = input.getText();
         if (link != null && !link.trim().isEmpty()) {
            consumer.accept(link);
         }
      }
   }

   public static File getInitDir(String dir) {
      File file = new File(dir);
      return !dir.trim().isEmpty() && file.exists() ? file : FileSystemView.getFileSystemView().getDefaultDirectory();
   }

   private static File getInitDirMedia() {
      return getInitDir(Settings.EnumSettings2.VLC_UTILS_LAST_DIR_MEDIA.getString());
   }

   private static File getInitDirPlaylist() {
      return getInitDir(Settings.EnumSettings2.VLC_UTILS_LAST_DIR_PLAYLIST.getString());
   }

   public static boolean isLink(String url) {
      return url.toLowerCase().matches("^(https?|rtp|rtsp|mms):\\/\\/.*");
   }

   public static boolean isYoutube(String url) {
      url = url.toLowerCase();
      return (url.contains("http://") || url.contains("https://")) && (url.contains("youtube") || url.contains("youtu.be"));
   }

   public static String getYouTubeID(String youtubeUrl) {
      String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu\\.be\\/)[^#\\&\\?]*";
      Pattern compiledPattern = Pattern.compile(pattern);
      Matcher matcher = compiledPattern.matcher(youtubeUrl);
      return matcher.find() ? matcher.group() : null;
   }

   public static String getYouTubeResource(String youtubeUrl, String resource, long delay) {
      String id = getYouTubeID(youtubeUrl);
      if (id == null) {
         return "";
      } else {
         Download d = Download.download(resource + id);
         int count = 0;

         while (!d.isFinish() && (long)count < delay) {
            count += 50;
            Utils.sleep(50L);
         }

         if (!d.isFinish()) {
            d.cancel();
            return "";
         } else {
            return d.getText();
         }
      }
   }

   public static String getYouTubeStream(String youtubeUrl) {
      return getYouTubeResource(youtubeUrl, Values.URL_YOUTUBE_ID_TO_STREAM, 2000L);
   }

   public static String getYouTubeTitle(String youtubeUrl) {
      String title = getYouTubeResource(youtubeUrl, Values.URL_YOUTUBE_ID_TO_TITLE, 1000L);
      if (!title.trim().isEmpty()) {
         Settings.SET.put("yt-tt-" + youtubeUrl, title);
      }

      return !title.isEmpty() ? title : youtubeUrl;
   }

   public static String getYouTubeTitleOffline(String youtubeUrl) {
      return Settings.SET.get("yt-tt-" + youtubeUrl, youtubeUrl);
   }

   public static String getYouTubeStreamFirstUrl(String youtubeUrl) {
      try {
         String[] _return = getYouTubeStream(youtubeUrl).split("<br>");
         Settings.SET.put("yt-tt-" + youtubeUrl, _return[0]);
         return _return[1];
      } catch (Exception var2) {
         return null;
      }
   }

   public static boolean exportFromJar(String resource, File out) {
      if (out.exists()) {
         out.delete();
      }

      try {
         File file = out.getAbsoluteFile();
         if (file.getName().contains(".")) {
            file.getParentFile().mkdirs();
         } else {
            file.mkdirs();
         }
      } catch (Exception var33) {
      }

      try (
         InputStream is = VLCUtils.class.getResourceAsStream(resource);
         FileOutputStream fos = new FileOutputStream(out);
      ) {
         byte[] buf = new byte[4096];

         int len;
         while ((len = is.read(buf)) >= 0) {
            fos.write(buf, 0, len);
         }

         return true;
      } catch (Exception var38) {
         out.delete();
         return false;
      }
   }

   public static void runVLCThread(Runnable run) {
      VLC_EXECUTOR.submit(() -> {
         try {
            run.run();
         } catch (Exception var2) {
         }
      });
   }

   public static void runVLCThreadAndWait(Runnable run) {
      try {
         VLC_EXECUTOR.submit(run).get();
      } catch (Exception var2) {
      }
   }

   public static enum EnumFileType {
      AUDIO(AudioFileFilter.INSTANCE.getExtensions(), "Audio File"),
      VIDEO(VideoFileFilter.INSTANCE.getExtensions(), "Video File"),
      MEDIA(createMediaFilter(), "Media File"),
      IMAGE(new String[]{"jpg", "jpeg", "png", "bmp", "gif"}, "Image File");

      public final String[] filter;
      public final String name;

      private EnumFileType(String[] filter, String name) {
         this.filter = filter;
         this.name = name;
      }

      public String[] getFilter() {
         return this.filter;
      }

      public String getDesc() {
         return this.name;
      }

      private static String[] createMediaFilter() {
         return Stream.concat(
               Arrays.asList(AudioFileFilter.INSTANCE.getExtensions()).stream(), Arrays.asList(VideoFileFilter.INSTANCE.getExtensions()).stream()
            )
            .toArray(String[]::new);
      }
   }

   public interface MRLtoVLC {
      String getMRLtoVLC();
   }
}
