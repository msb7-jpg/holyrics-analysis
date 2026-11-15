package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.control.MyImagesController;
import com.limagiran.holyrics.util.Images;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.media.Media;
import javax.imageio.ImageIO;

public class BackgroundMyImage extends Background implements Serializable {
   private static final long serialVersionUID = 12491L;
   public final int id;
   private long version = 1L;

   public BackgroundMyImage(int id) {
      this.id = id;
      this.getImage();
   }

   @Override
   public final BufferedImage getImage() {
      return BackgroundBufferedImageCache.getImageCache(this, () -> {
         BufferedImage var3;
         try {
            ByteArrayInputStream bais = new ByteArrayInputStream(MyImagesController.get(this.id).bytes);
            return ImageIO.read(bais);
         } catch (Exception var7) {
            var3 = Images.imageNotFound();
         } finally {
            ByteArrayInputStream bais = null;
         }

         return var3;
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
      return "MyImage" + this.id;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
