package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.util.Images;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.media.Media;
import javax.imageio.ImageIO;

public class BackgroundPattern extends Background implements Serializable {
   private static final long serialVersionUID = 12491L;
   public final String url;
   private transient BufferedImage bi;
   private long version = 1L;

   public BackgroundPattern(String url) {
      this.url = url;
      this.getImage();
   }

   @Override
   public final BufferedImage getImage() {
      if (this.bi == null) {
         try {
            this.bi = ImageIO.read(BackgroundPattern.class.getResource(this.url));
            int size = 256;
            if (this.bi.getWidth() < size) {
               int w = this.bi.getWidth() * (size / this.bi.getWidth());
               int h = this.bi.getHeight() * (size / this.bi.getHeight());
               BufferedImage bi2 = new BufferedImage(w, h, 2);
               Graphics2D g2 = bi2.createGraphics();
               g2.setPaint(new TexturePaint(this.bi, new Rectangle(0, 0, this.bi.getWidth(), this.bi.getHeight())));
               g2.fill(new Rectangle(0, 0, w, h));
               g2.dispose();
               this.bi = bi2;
            }
         } catch (Exception var6) {
            this.bi = Images.imageNotFound();
         }
      }

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
      return new Background.EnumBackgroundType[]{Background.EnumBackgroundType.SIDE_BY_SIDE};
   }

   @Override
   public Background.EnumBackgroundType getDefaultType() {
      return Background.EnumBackgroundType.SIDE_BY_SIDE;
   }

   @Override
   public String getId() {
      return "Pattern" + this.url;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
