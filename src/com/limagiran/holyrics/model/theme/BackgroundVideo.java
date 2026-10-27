package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.vlc.VLCUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.media.Media;

public class BackgroundVideo extends Background implements Serializable, VLCUtils.MRLtoVLC {
   private static final long serialVersionUID = 12491L;
   public final String fileName;
   public final String hash;
   public final BackgroundImage image;
   private long version = 1L;

   public BackgroundVideo(String fileName, String hash, String image) {
      this.fileName = fileName;
      this.hash = hash;
      this.image = new BackgroundImage(image);
   }

   public File getPath() {
      return new File(Values.PATH_BACKGROUND_VIDEO + this.fileName);
   }

   @Override
   public Background.EnumBackgroundType getType() {
      return Background.EnumBackgroundType.EXTEND;
   }

   @Override
   public BufferedImage getImage() {
      return BackgroundBufferedImageCache.getImageCache(this, () -> this.image.getImage());
   }

   @Override
   public Media getVideo() throws Exception {
      File path = this.getPath();
      if (checkLongVideo(path, this.fileName)) {
         return new Media(createLongPathVideo(path).toURI().toString());
      } else if (!path.exists()) {
         throw new Exception(I18N.MSG_EXCEPTION_VIDEO_NOT_FOUND.val());
      } else {
         String dirHash = "";

         try {
            dirHash = Dir.getHash(path);
         } catch (Exception var4) {
            throw new Exception(I18N.MSG_ERROR_TRY_AGAIN.val());
         }

         if (!this.hash.equals(dirHash)) {
            throw new Exception(I18N.MSG_EXCEPTION_INVALID_VIDEO.val());
         } else {
            return new Media(path.toURI().toString());
         }
      }
   }

   @Override
   public String getMRLtoVLC() {
      File path = this.getPath();
      if (checkLongVideo(path, this.fileName)) {
         return createLongPathVideo(path).getAbsolutePath();
      } else if (!path.exists()) {
         WebLafUtils.notificationErrorDelay(I18N.MSG_EXCEPTION_VIDEO_NOT_FOUND.val(), 5L);
         return null;
      } else {
         String dirHash;
         try {
            dirHash = Dir.getHash(path);
         } catch (Exception var4) {
            WebLafUtils.notificationErrorDelay("Erro ao carregar vídeo", 5L);
            return null;
         }

         if (!this.hash.equals(dirHash)) {
            WebLafUtils.notificationErrorDelay(I18N.MSG_EXCEPTION_INVALID_VIDEO.val(), 5L);
            return null;
         } else {
            return path.getAbsolutePath();
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
      return "Video" + Values.PATH_BACKGROUND_VIDEO + this.fileName;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
