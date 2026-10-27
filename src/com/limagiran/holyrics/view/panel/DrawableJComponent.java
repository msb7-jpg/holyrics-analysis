package com.limagiran.holyrics.view.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;
import javax.swing.JComponent;

public class DrawableJComponent extends JComponent {
   private final transient Rectangle rect = new Rectangle();
   protected final Supplier<BufferedImage> image;
   protected final boolean border;

   public DrawableJComponent(Supplier<BufferedImage> image, boolean border) {
      this.image = image;
      this.border = border;
   }

   public DrawableJComponent(Supplier<BufferedImage> image) {
      this(image, false);
   }

   @Override
   protected void paintComponent(Graphics g) {
      BufferedImage bi = this.image.get();
      if (bi != null) {
         this.rect.setLocation(0, 0);
         this.rect.setSize(this.getSize());
         if (this.border) {
            this.rect.x = 2;
            this.rect.y = 2;
            this.rect.width -= 4;
            this.rect.height -= 4;
         }

         Graphics2D g2 = (Graphics2D)g;
         this.rect.x = this.rect.x + (this.rect.width - bi.getWidth()) / 2;
         this.rect.y = this.rect.y + (this.rect.height - bi.getHeight()) / 2;
         g2.drawImage(bi, this.rect.x, this.rect.y, null);
         if (this.border) {
            g2.setColor(Color.GRAY);
            g2.drawRect(this.rect.x - 2, this.rect.y - 2, bi.getWidth() + 3, bi.getHeight() + 3);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(this.rect.x - 1, this.rect.y - 1, bi.getWidth() + 1, bi.getHeight() + 1);
         }
      }
   }

   public Dimension getDrawableAreaSize() {
      if (this.border) {
         Dimension size = this.getSize();
         size.width -= 4;
         size.height -= 4;
         return size;
      } else {
         return super.getSize();
      }
   }
}
