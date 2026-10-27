package com.limagiran.holyrics.control;

import com.limagiran.holyrics.hitem.EnumTabType;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Download;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.PopUpOpenProgram;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.vlc.VLCUtils;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.imageio.ImageIO;

public class ThumbMediaController implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final Dimension THUMB_SIZE = new Dimension(64, 36);
   private static final transient Object THUMBS_LOCK = new Object();
   private static final Map<String, ThumbMediaController.Thumb> THUMBS = ThumbMediaController.ThumbControllerFactory.getInstance();
   public static final BufferedImage IMAGE_NOT_FOUND = Img.resize(Images.load("imageNotFound48x48.png"), THUMB_SIZE);
   private static final String PATH_PREFIX = new File("").getAbsolutePath() + File.separator;

   public static void load(PopUpOpenProgram popup) {
      Map<String, File> mapIdFile = new HashMap<>();

      for (EnumTabType en : new EnumTabType[]{EnumTabType.VIDEO, EnumTabType.IMAGE}) {
         for (File f : en.listFiles()) {
            String id = en.path + f.getName();
            if (!contains(id)) {
               mapIdFile.put(id, f);
            }
         }
      }

      List<Entry<String, File>> list = new ArrayList<>(mapIdFile.entrySet());

      for (int i = 0; i < list.size(); i++) {
         Entry<String, File> entry = list.get(i);
         if (!contains(entry.getKey())) {
            getOrCreate(entry.getKey(), entry.getValue().lastModified(), size -> EnumTabType.createThumbBytes(entry.getKey(), entry.getValue(), size), false);
            popup.setProgress(String.format("%s (%s)", I18N.WORD_LIBRARY.val(), Utils.getPercentage((double)(i + 1), (double)mapIdFile.size())));
         }
      }

      BooleanOptional.runIfTrue(!list.isEmpty(), ThumbMediaController::save);
      mapIdFile.clear();
      if (!Settings.EnumSettings2.THUMB_UPDATE_2_1_0.is()) {
         Settings.EnumSettings2.THUMB_UPDATE_2_1_0.set(true);
         String format = I18N.WORD_UPDATE.val() + " 2.1.0 (%02d%%)";
         synchronized (THUMBS_LOCK) {
            List<String> keys = new ArrayList<>(THUMBS.keySet());

            for (int ix = 0; ix < keys.size(); ix++) {
               String key = keys.get(ix);
               ThumbMediaController.Thumb thumb = THUMBS.get(key);
               if (thumb != null && thumb.thumb != null) {
                  byte[] bytes = thumb.thumb;
                  ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                  ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length / 2);

                  try {
                     BufferedImage bi = ImageIO.read(bais);
                     ImageIO.write(bi, "png", baos);
                     ThumbMediaController.Thumb newThumb = new ThumbMediaController.Thumb(baos.toByteArray(), thumb.lastModified);
                     THUMBS.put(key, newThumb);
                     int progress = Utils.getPercentageInt((double)(ix + 1), (double)keys.size());
                     popup.setProgress(String.format(format, progress));
                  } catch (Exception var20) {
                  } finally {
                     bais = null;
                     ByteArrayOutputStream var35 = null;
                  }
               }
            }
         }
      }
   }

   public static Dimension getThumbSize() {
      return new Dimension(THUMB_SIZE);
   }

   public static BufferedImage getAsBufferedImage(String path, Dimension size) {
      BufferedImage var6;
      try {
         byte[] bytes = getOrDefault(path, (byte[])null);
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         BufferedImage img = ImageIO.read(bais);
         if (img.getWidth() != size.width || img.getHeight() != size.height) {
            img = Img.resize(img, size);
         }

         return img;
      } catch (Exception var10) {
         BufferedImage img = IMAGE_NOT_FOUND;
         if (img.getWidth() != size.width || img.getHeight() != size.height) {
            img = Img.resize(IMAGE_NOT_FOUND, size);
         }

         var6 = img;
      } finally {
         ByteArrayInputStream bais = null;
         BufferedImage img = null;
         byte[] bytes = null;
      }

      return var6;
   }

   public static BufferedImage getAsBufferedImage(String path) {
      return getAsBufferedImage(path, THUMB_SIZE);
   }

   public static BufferedImage getAsBufferedImage(File absolutePath, Dimension size) {
      return getAsBufferedImage(absolutePath.getAbsolutePath().replace(PATH_PREFIX, ""), size);
   }

   public static BufferedImage getAsBufferedImage(File absolutePath) {
      return getAsBufferedImage(absolutePath, THUMB_SIZE);
   }

   public static boolean contains(String contains) {
      synchronized (THUMBS_LOCK) {
         return THUMBS.get(contains) != null;
      }
   }

   public static ThumbMediaController.Thumb getOrCreate(String path, long lastModified, Function<Dimension, byte[]> generate, boolean save) {
      ThumbMediaController.Thumb thumb;
      synchronized (THUMBS_LOCK) {
         thumb = THUMBS.get(path);
      }

      if (thumb == null || thumb.lastModified < lastModified || thumb.thumb == null) {
         thumb = new ThumbMediaController.Thumb(generate.apply(THUMB_SIZE), lastModified);
         if (thumb.thumb == null) {
            return thumb;
         }

         synchronized (THUMBS_LOCK) {
            THUMBS.put(path, thumb);
         }

         if (save) {
            save();
         }
      }

      return thumb;
   }

   public static void addError(String id) {
      String key = "thumbError-" + id;
      String val = Settings.SET.get(key, "f");
      if (val.equals("f")) {
         Settings.SET.put(key, "t");
         MyProperties.setFlag(key, "t");
      } else if (val.equals("t") && !MyProperties.getFlag(key, "f").equals("t")) {
         Settings.SET.put(key, "tt");
      }
   }

   public static boolean checkError(String id) {
      return Settings.SET.get("thumbError-" + id, "f").equals("tt") || MyProperties.getFlag("thumbError-" + id, "f").equals("t");
   }

   public static void loadYouTube(String youtubeUrl) {
      if (!checkError(youtubeUrl)) {
         Function<Dimension, byte[]> generate = size -> {
            Download d = Download.download("http://i1.ytimg.com/vi/" + VLCUtils.getYouTubeID(youtubeUrl) + "/default.jpg");
            long count = 0L;

            while (!d.isFinish() && count < 500L) {
               count += 50L;
               Utils.sleep(50L);
            }

            if (!d.isFinish()) {
               d.cancel();
               addError(youtubeUrl);
               return null;
            } else {
               try (ByteArrayInputStream bais = new ByteArrayInputStream(d.getBytes())) {
                  BufferedImage ytimg = ImageIO.read(bais);
                  ytimg = Img.resize(ytimg, size);
                  byte[] bytes = Img.getBytes(ytimg);
                  ytimg.flush();
                  return bytes;
               } catch (Exception var21) {
                  return null;
               }
            }
         };
         getOrCreate(youtubeUrl, 0L, generate, true);
      }
   }

   public static BufferedImage getYouTubeBI(String youtubeUrl) {
      return getOrDefault(youtubeUrl, Images.ICON_YOUTUBE_32);
   }

   public static byte[] getOrDefault(String path, byte[] _default) {
      synchronized (THUMBS_LOCK) {
         return Optional.ofNullable(THUMBS.get(path)).map(t -> t.thumb).orElse(_default);
      }
   }

   public static BufferedImage getOrDefault(String path, BufferedImage _default) {
      byte[] bytes = getOrDefault(path, (byte[])null);
      BufferedImage bi = null;

      BufferedImage var5;
      try {
         bi = Img.load(bytes);
         return bi != null ? bi : _default;
      } catch (Exception var9) {
         var5 = _default;
      } finally {
         bytes = null;
         bi = null;
      }

      return var5;
   }

   public static void remove(String... path) {
      synchronized (THUMBS_LOCK) {
         for (String hc : path) {
            THUMBS.remove(hc);
         }
      }

      if (path.length > 0) {
         save();
      }
   }

   public static ThumbMediaController.Thumb getOrCreate(String path, long lastModified, Function<Dimension, byte[]> generate) {
      return getOrCreate(path, lastModified, generate, true);
   }

   public static void createIfNotExists(String id, File file) {
      if (!contains(id)) {
         getOrCreate(id, file.lastModified(), size -> EnumTabType.createThumbBytes(id, file, size), false);
      }
   }

   public static void save() {
      synchronized (THUMBS_LOCK) {
         ThumbMediaController.ThumbControllerFactory.save(THUMBS);
      }
   }

   public static BufferedImage getOrDefaultFullPath(String currentMedia, BufferedImage _default) {
      return getOrDefault(currentMedia.replace(PATH_PREFIX, ""), _default);
   }

   static {
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         try {
            List<String> remove = new ArrayList<>();
            THUMBS.forEach((path, u) -> {
               if (!new File(path).getAbsoluteFile().exists()) {
                  remove.add(path);
               }
            });
            remove.forEach(THUMBS::remove);
            save();
         } catch (Exception var1) {
         }
      }));
   }

   public static class Thumb implements Serializable {
      private static final long serialVersionUID = 1L;
      public final byte[] thumb;
      public final long lastModified;

      protected Thumb(byte[] thumb, long lastModified) {
         this.thumb = thumb;
         this.lastModified = lastModified;
      }
   }

   private static class ThumbControllerFactory {
      private static final File FILE = new File(Values.PATH_FILES + "thumbCache.hly");
      private static final String PASSWORD = "&p4ssS3sp";

      public static Map<String, ThumbMediaController.Thumb> getInstance() {
         try {
            Map<String, ThumbMediaController.Thumb> _return = RWObj.read(FILE, Map.class, "&p4ssS3sp");
            if (_return != null
               && _return.keySet().stream().anyMatch(String.class::isInstance)
               && _return.values().stream().anyMatch(ThumbMediaController.Thumb.class::isInstance)) {
               return _return;
            }
         } catch (Exception var1) {
         }

         return new HashMap<>();
      }

      public static void save(Map<String, ThumbMediaController.Thumb> map) {
         RWObj.write(map, FILE, "&p4ssS3sp");
      }

      static {
         if (FILE.getParentFile() != null) {
            FILE.getParentFile().mkdirs();
         }
      }
   }
}
