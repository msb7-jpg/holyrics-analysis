package com.limagiran.holyrics.model.theme;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.media.Media;

public class BackgroundColor extends Background implements Serializable {
   private static final long serialVersionUID = 12491L;
   private static final transient BufferedImage EMPTY = new BufferedImage(8, 8, 2);
   public final Color color;
   private long version = 1L;

   public BackgroundColor(Color color) {
      this.color = color;
      this.init();
   }

   private void init() {
      if (this.isTransparent()) {
         this.setOpacity(70);
      }
   }

   public boolean isTransparent() {
      return this.color.equals(new Color(0, 0, 0, 0));
   }

   @Override
   public final BufferedImage getImage() {
      return EMPTY;
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
      return "Color" + this.color.getRGB();
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
