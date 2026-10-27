package com.limagiran.holyrics.control;

import com.alee.utils.FileUtils;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.FFMPEG;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Supplier;
import javax.imageio.ImageIO;

public class ThumbCacheController implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final Dimension THUMB_SIZE = new Dimension(112, 63);
   private static final transient Object THUMBS_LOCK = new Object();
   private static final LinkedHashMap<String, ThumbMediaController.Thumb> THUMBS = ThumbCacheController.ThumbCacheFactory.getInstance();
   private static boolean save;
   private static final FFMPEG FFMPEG_OBJ = new FFMPEG();

   public static Dimension getThumbSize() {
      return new Dimension(THUMB_SIZE);
   }

   public static BufferedImage getAsBufferedImage(String id, Dimension size) {
      BufferedImage var6;
      try {
         byte[] bytes = getOrDefault(id, (byte[])null);
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         BufferedImage img = ImageIO.read(bais);
         if (img.getWidth() != size.width || img.getHeight() != size.height) {
            img = Img.resize(img, size);
         }

         return img;
      } catch (Exception var10) {
         BufferedImage img = ThumbMediaController.IMAGE_NOT_FOUND;
         if (img.getWidth() != size.width || img.getHeight() != size.height) {
            img = Img.resize(ThumbMediaController.IMAGE_NOT_FOUND, size);
         }

         var6 = img;
      } finally {
         ByteArrayInputStream bais = null;
         BufferedImage img = null;
         byte[] bytes = null;
      }

      return var6;
   }

   public static BufferedImage getAsBufferedImage(String id) {
      return getAsBufferedImage(id, THUMB_SIZE);
   }

   public static boolean contains(String id) {
      synchronized (THUMBS_LOCK) {
         return THUMBS.get(id) != null;
      }
   }

   public static ThumbMediaController.Thumb getOrCreate(String id, long lastModified, Supplier<BufferedImage> generate) {
      if (checkError(id)) {
         return null;
      } else {
         ThumbMediaController.Thumb thumb;
         synchronized (THUMBS_LOCK) {
            thumb = THUMBS.get(id);
         }

         if (thumb == null || thumb.lastModified < lastModified || thumb.thumb == null) {
            BufferedImage bi = null;

            byte[] bytes;
            try {
               bi = generate.get();
               bi = Img.resize(bi, THUMB_SIZE);
               bytes = Img.getBytesJPG(bi, 0.9F);
            } catch (Exception var14) {
               bytes = null;
            } finally {
               if (bi != null) {
                  bi.flush();
                  BufferedImage var19 = null;
               }
            }

            thumb = new ThumbMediaController.Thumb(bytes, lastModified);
            if (thumb.thumb == null) {
               addError(id);
               return null;
            }

            synchronized (THUMBS_LOCK) {
               while (ControllerMapFactory.sizeFromThumb(THUMBS) >= 3145728L) {
                  THUMBS.remove(THUMBS.keySet().iterator().next());
               }

               THUMBS.put(id, thumb);
            }

            save = true;
         }

         return thumb;
      }
   }

   public static void addError(String id) {
      String key = "thumb2Error-" + id;
      String val = Settings.SET.get(key, "f");
      if (val.equals("f")) {
         Settings.SET.put(key, "t");
         MyProperties.setFlag(key, "t");
      } else if (val.equals("t") && !MyProperties.getFlag(key, "f").equals("t")) {
         Settings.SET.put(key, "tt");
      }
   }

   public static boolean checkError(String id) {
      return Settings.SET.get("thumbError-" + id, "f").equals("tt");
   }

   public static byte[] getOrDefault(String id, byte[] _default) {
      synchronized (THUMBS_LOCK) {
         return Optional.ofNullable(THUMBS.get(id)).map(t -> t.thumb).orElse(_default);
      }
   }

   public static BufferedImage getOrDefault(String id, BufferedImage _default) {
      byte[] bytes = getOrDefault(id, (byte[])null);
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

   private static void save() {
      synchronized (THUMBS_LOCK) {
         ThumbCacheController.ThumbCacheFactory.save(THUMBS);
      }
   }

   public static void checkSave() {
      if (save) {
         save = false;
         save();
      }
   }

   public static BufferedImage generateVideoThumbnail(File file) {
      BufferedImage[] hack = new BufferedImage[]{null};
      boolean[] forceStopHack = new boolean[]{false};
      Utils.startThreadDaemon(() -> {
         int duration = 3;

         try {
            final File output = File.createTempFile("thumb", ".jpg");
            output.deleteOnExit();
            FFMPEG_OBJ.getImage(file, output, THUMB_SIZE, duration, new FFMPEG.FFMPEGAdapter() {
               @Override
               public void success() {
                  try {
                     hack[0] = ImageIO.read(output);
                  } catch (Exception var2) {
                     hack[0] = null;
                  }
               }

               @Override
               public void error(String msg) {
                  forceStopHack[0] = true;
               }
            });
         } catch (Exception var5) {
         }
      });
      long start = System.currentTimeMillis();

      while (!forceStopHack[0] && hack[0] == null && System.currentTimeMillis() - start < 2500L) {
         Utils.sleep(50L);
      }

      return hack[0] != null ? hack[0] : Img.toBufferedImage2(FileUtils.getStandartFileIcon(file, true, true).getImage());
   }

   private static class ThumbCacheFactory {
      private static final File FILE = new File(Values.PATH_FILES + "thumbCache2.hly");
      private static final String PASSWORD = "&p4ssS3sp";

      public static LinkedHashMap<String, ThumbMediaController.Thumb> getInstance() {
         try {
            LinkedHashMap<String, ThumbMediaController.Thumb> _return = RWObj.read(FILE, LinkedHashMap.class, "&p4ssS3sp");
            if (_return != null
               && _return.keySet().stream().anyMatch(String.class::isInstance)
               && _return.values().stream().anyMatch(ThumbMediaController.Thumb.class::isInstance)) {
               return _return;
            }
         } catch (Exception var1) {
         }

         return new LinkedHashMap<>();
      }

      public static void save(LinkedHashMap<String, ThumbMediaController.Thumb> map) {
         RWObj.write(map, FILE, "&p4ssS3sp");
      }

      static {
         if (FILE.getParentFile() != null) {
            FILE.getParentFile().mkdirs();
         }
      }
   }
}
