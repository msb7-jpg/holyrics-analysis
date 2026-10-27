package com.limagiran.holyrics.hitem;

import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.item.HItemAudio;
import com.limagiran.holyrics.hitem.item.HItemImage;
import com.limagiran.holyrics.hitem.item.HItemVideo;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.FFMPEG;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.PresentationCache;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

public enum EnumTabType {
   AUDIO(I18N.WORD_AUDIO.val(), I18N.LIBRARY_ITEM_MEDIA_ACTION_NAME.val(), Values.PATH_MEDIA_AUDIO, f -> HItemUtils.isAudio(f) || f.isDirectory()),
   VIDEO(I18N.WORD_VIDEO.val(), I18N.LIBRARY_ITEM_MEDIA_ACTION_NAME.val(), Values.PATH_MEDIA_VIDEO, f -> HItemUtils.isVideo(f) || f.isDirectory()),
   IMAGE(I18N.WORD_IMAGE.val(), I18N.WORD_SHOW.val(), Values.PATH_MEDIA_IMAGE, f -> HItemUtils.isImage(f) || f.isDirectory());

   public final String title;
   public final String actionName;
   public final String path;
   public final File pathAsFile;
   public final Predicate<File> fileFilter;
   private static final BufferedImage THUMB_DEFAULT_IMAGE_LIST_LIBRARY = Images.loadWithPadding("photoLibrary48.png", 18, 0);

   private EnumTabType(String title, String actionName, String path, Predicate<File> fileFilter) {
      this.title = title;
      this.actionName = actionName;
      this.path = path;
      this.pathAsFile = new File(path).getAbsoluteFile();
      this.fileFilter = fileFilter;
   }

   public Favoritable getFavoritable(String name) {
      return this.getHItem(name);
   }

   public IHItem getHItem(String name) {
      switch (this) {
         case AUDIO:
            return new HItemAudio(name);
         case VIDEO:
            return new HItemVideo(name);
         case IMAGE:
            return new HItemImage(name);
         default:
            return null;
      }
   }

   public String getID(String name) {
      return this.path + name;
   }

   public File[] listFiles() {
      if (!this.pathAsFile.exists()) {
         this.pathAsFile.mkdirs();
      }

      File[] files = this.pathAsFile.listFiles(this.fileFilter::test);
      return files != null ? files : new File[0];
   }

   public Predicate<File> getFileFilterDirectory() {
      switch (this) {
         case AUDIO:
         case VIDEO:
            return HItemUtils::isMedia;
         case IMAGE:
            return HItemUtils::isImage;
         default:
            return File::isFile;
      }
   }

   public static BufferedImage getThumb(String id, File file, Dimension d) {
      try {
         if (HItemUtils.isAudio(file) && !file.isDirectory()) {
            return Images.NULL1X1;
         } else if (file.isDirectory() && !id.startsWith(Values.PATH_MEDIA_IMAGE)) {
            return createThumb(id, file, d);
         } else {
            BufferedImage img = ThumbMediaController.getAsBufferedImage(id, d);
            return id.startsWith(Values.PATH_MEDIA_IMAGE) && file.isDirectory() ? setupImageList(img) : img;
         }
      } catch (Exception var4) {
         return Img.resize(ThumbMediaController.IMAGE_NOT_FOUND, d);
      }
   }

   private static BufferedImage createThumb(String id, File file, Dimension size) {
      BufferedImage bi;
      try {
         if (ThumbMediaController.checkError(id)) {
            return null;
         }

         if (!file.isDirectory()) {
            if (HItemUtils.isImage(file)) {
               BufferedImage img = ImageIO.read(file);
               return Img.resize(img, size);
            }

            if (!HItemUtils.isVideo(file)) {
               return Images.NULL1X1;
            }

            if (!file.exists()) {
               return null;
            }

            BufferedImage[] hack = new BufferedImage[]{null};
            Utils.startThreadDaemon(() -> {
               int duration = 2;

               try {
                  int _duration = HItemUtils.FFMPEG_OBJ.duration(file);
                  if (_duration > 6) {
                     duration = Math.min((int)((double)_duration * 0.333), 7);
                  }
               } catch (Exception var6) {
               }

               try {
                  final File output = File.createTempFile("thumb", ".jpg");
                  output.deleteOnExit();
                  HItemUtils.FFMPEG_OBJ.getImage(file, output, size, duration, new FFMPEG.FFMPEGAdapter() {
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
                        hack[0] = Images.NULL1X1;
                     }
                  });
               } catch (Exception var5) {
               }
            });
            long start = System.currentTimeMillis();

            while (!EventQueue.isDispatchThread() && hack[0] == null && System.currentTimeMillis() - start < 10000L) {
               Utils.sleep(200L);
            }

            HItemUtils.FFMPEG_OBJ.kill();
            if (hack[0] == Images.NULL1X1 || hack[0] == null) {
               ThumbMediaController.addError(id);
            }

            return hack[0] == Images.NULL1X1 ? null : hack[0];
         }

         File[] files = file.listFiles();
         if (!id.startsWith(Values.PATH_MEDIA_IMAGE)) {
            if (id.startsWith(Values.PATH_MEDIA_AUDIO)) {
               bi = PresentationCache.get("EnumTabType-audio-dir", size);
               return bi != null ? bi : Img.resize(HItemAudio.DEFAULT_THUMB_LIST, size);
            }

            if (id.startsWith(Values.PATH_MEDIA_VIDEO)) {
               bi = PresentationCache.get("EnumTabType-video-dir", size);
               return bi != null ? bi : Img.resize(HItemVideo.DEFAULT_THUMB_LIST, size);
            }

            return Images.NULL1X1;
         }

         file = Stream.of(files).filter(HItemUtils::isImage).findFirst().orElse(null);
         if (file != null) {
            bi = PresentationCache.get("EnumTabType-" + file.getAbsolutePath() + file.lastModified(), size);
            return bi != null ? bi : Img.resize(ImageIO.read(file), size);
         }

         bi = null;
      } catch (Exception var12) {
         ThumbMediaController.addError(id);
         return null;
      } finally {
         BufferedImage img = null;
      }

      return bi;
   }

   public static byte[] createThumbBytes(String id, File file, Dimension size) {
      try {
         Object e;
         try {
            BufferedImage img = createThumb(id, file, size);
            if (img != null) {
               img = Img.resize(img, size);
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               return ImageIO.write(img, "png", baos) ? baos.toByteArray() : null;
            }

            e = null;
         } catch (Exception var11) {
            return null;
         } finally {
            ByteArrayOutputStream baos = null;
            BufferedImage img = null;
         }

         return (byte[])e;
      } catch (Exception var13) {
         return null;
      }
   }

   private static BufferedImage setupImageList(BufferedImage img) {
      BufferedImage g;
      try {
         int border = img.getHeight() < 27 ? 4 : 6;
         BufferedImage bi = new BufferedImage(img.getWidth() + border, img.getHeight() + border, 2);
         Graphics2D gx = bi.createGraphics();
         G2Utils.setupGraphics(gx);
         Rectangle r = new Rectangle(0, 0, bi.getWidth() - (border + 1), bi.getHeight() - (border + 1));

         for (int i = 0; i < border; i += 2) {
            r.x = border - i;
            r.y = i;
            gx.setColor(Color.LIGHT_GRAY);
            gx.fillRect(r.x, r.y, r.width, r.height);
            gx.setColor(Color.DARK_GRAY);
            gx.drawRect(r.x, r.y, r.width, r.height);
         }

         r.x = 0;
         r.y = border;
         gx.drawImage(img, r.x, r.y, r.width, r.height, null);
         gx.setColor(Color.DARK_GRAY);
         gx.drawRect(r.x, r.y, r.width, r.height);
         gx.dispose();
         return bi;
      } catch (Exception var9) {
         g = Img.resize(THUMB_DEFAULT_IMAGE_LIST_LIBRARY, new Dimension(img.getWidth(), img.getHeight()));
      } finally {
         BufferedImage var11 = null;
         BufferedImage bi = null;
      }

      return g;
   }

   public boolean isAudio() {
      return this == AUDIO;
   }

   public boolean isVideo() {
      return this == VIDEO;
   }

   public boolean isImage() {
      return this == IMAGE;
   }
}
