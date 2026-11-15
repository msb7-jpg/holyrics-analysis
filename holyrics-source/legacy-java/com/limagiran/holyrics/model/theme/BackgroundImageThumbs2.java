package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.util.Img;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import javafx.scene.media.Media;
import javax.imageio.ImageIO;

public class BackgroundImageThumbs2 extends Background {
   private final BufferedImage bi;
   private final String id;

   public BackgroundImageThumbs2(byte[] bytes, Dimension d) {
      this.bi = this.load(bytes, d);
      this.id = Integer.toString(Arrays.hashCode(bytes));
   }

   private BufferedImage load(byte[] bytes, Dimension d) {
      BufferedImage var5;
      try {
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         return ImageIO.read(bais);
      } catch (Exception var9) {
         var5 = Img.createBlank(Color.BLACK, d);
      } finally {
         ByteArrayInputStream bais = null;
      }

      return var5;
   }

   @Override
   public final BufferedImage getImage() {
      return this.bi;
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
      return "ImageThumbs" + this.id;
   }
}
