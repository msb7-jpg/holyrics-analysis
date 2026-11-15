package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.control.MyVideosController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.VideoHL;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.vlc.VLCUtils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.media.Media;
import javax.imageio.ImageIO;

public class BackgroundMyVideo extends Background implements Serializable, VLCUtils.MRLtoVLC {
   private static final long serialVersionUID = 12491L;
   public final int id;
   private long version = 1L;

   public BackgroundMyVideo(int id) {
      this.id = id;
   }

   @Override
   public Background.EnumBackgroundType getType() {
      return Background.EnumBackgroundType.EXTEND;
   }

   @Override
   public final BufferedImage getImage() {
      return BackgroundBufferedImageCache.getImageCache(this, () -> {
         byte[] bytes = MyVideosController.get(this.id).image;

         try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            return ImageIO.read(bais);
         } catch (Exception var16) {
            return Images.imageNotFound();
         }
      });
   }

   @Override
   public Media getVideo() throws Exception {
      VideoHL video = MyVideosController.get(this.id);
      if (video == null) {
         throw new Exception(I18N.MSG_EXCEPTION_VIDEO_NOT_FOUND.val());
      } else {
         File path = new File(Values.PATH_BACKGROUND_VIDEO + video.fileName);
         if (checkLongVideo(path, video.fileName)) {
            return new Media(createLongPathVideo(path).toURI().toString());
         } else if (!path.exists()) {
            throw new Exception(I18N.MSG_EXCEPTION_VIDEO_NOT_FOUND.val());
         } else {
            String dirHash = "";

            try {
               dirHash = Dir.getHash(path);
            } catch (Exception var5) {
               throw new Exception(I18N.MSG_ERROR_TRY_AGAIN.val());
            }

            if (!video.hash.equals(dirHash)) {
               throw new Exception(I18N.MSG_EXCEPTION_INVALID_VIDEO.val());
            } else {
               return new Media(path.toURI().toString());
            }
         }
      }
   }

   @Override
   public String getMRLtoVLC() {
      VideoHL video = MyVideosController.get(this.id);
      if (video == null) {
         WebLafUtils.notificationErrorDelay(I18N.MSG_EXCEPTION_VIDEO_NOT_FOUND.val(), 5L);
         return null;
      } else {
         File path = new File(Values.PATH_BACKGROUND_VIDEO + video.fileName);
         if (checkLongVideo(path, video.fileName)) {
            return createLongPathVideo(path).getAbsolutePath();
         } else {
            if (!path.exists()) {
               WebLafUtils.notificationErrorDelay(I18N.MSG_EXCEPTION_VIDEO_NOT_FOUND.val(), 5L);
            }

            String dirHash;
            try {
               dirHash = Dir.getHash(path);
            } catch (Exception var5) {
               WebLafUtils.notificationErrorDelay(I18N.MSG_ERROR_TRY_AGAIN.val(), 5L);
               return null;
            }

            if (!video.hash.equals(dirHash)) {
               WebLafUtils.notificationErrorDelay(I18N.MSG_EXCEPTION_INVALID_VIDEO.val(), 5L);
               return null;
            } else {
               return path.getAbsolutePath();
            }
         }
      }
   }

   @Override
   public boolean isVideo() {
      return true;
   }

   @Override
   public Background.EnumBackgroundType[] getTypes() {
      return new Background.EnumBackgroundType[]{Background.EnumBackgroundType.EXTEND};
   }

   @Override
   public Background.EnumBackgroundType getDefaultType() {
      return Background.EnumBackgroundType.EXTEND;
   }

   @Override
   public String getId() {
      return "MyVideo" + this.id;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
