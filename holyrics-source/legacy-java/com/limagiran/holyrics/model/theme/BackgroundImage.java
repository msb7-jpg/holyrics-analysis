package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.util.Images;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.media.Media;
import javax.imageio.ImageIO;

public class BackgroundImage extends Background implements Serializable {
   private static final long serialVersionUID = 12491L;
   public final String url;
   private long version = 1L;

   public BackgroundImage(String url) {
      this.url = url;
      this.getImage();
   }

   @Override
   public final BufferedImage getImage() {
      return BackgroundBufferedImageCache.getImageCache(this, () -> {
         try {
            return ImageIO.read(BackgroundMyImage.class.getResource(this.url));
         } catch (Exception var2) {
            return Images.imageNotFound();
         }
      });
   }

   @Override
   public Media getVideo() throws Exception {
      return null;
   }

   @Override
   public boolean isVideo() {
      return false;
   }

   @Override
   public Background.EnumBackgroundType[] getTypes() {
      return Background.EnumBackgroundType.values();
   }

   @Override
   public Background.EnumBackgroundType getDefaultType() {
      return Background.EnumBackgroundType.EXTEND;
   }

   @Override
   public String getId() {
      return "Image" + this.url;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
