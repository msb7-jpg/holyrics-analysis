package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.model.ImageHL;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Utils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.media.Media;
import javax.imageio.ImageIO;

public class BackgroundAnnouncement extends Background implements Serializable {
   private static final long serialVersionUID = 12491L;
   public final int id;
   private final ImageHL image;
   private long version = 1L;

   public BackgroundAnnouncement(int id, ImageHL image) {
      this.id = id;
      this.image = image;
   }

   public BackgroundAnnouncement(ImageHL image) {
      this(Long.hashCode(Utils.generateMillisecondId()), image);
   }

   public ImageHL getImageHL() {
      return this.image;
   }

   @Override
   public final BufferedImage getImage() {
      BufferedImage var3;
      try {
         ByteArrayInputStream bais = new ByteArrayInputStream(this.image.bytes);
         return ImageIO.read(bais);
      } catch (Exception var7) {
         var3 = Images.imageNotFound();
      } finally {
         ByteArrayInputStream bais = null;
      }

      return var3;
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
      return "Announcement" + this.id;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
