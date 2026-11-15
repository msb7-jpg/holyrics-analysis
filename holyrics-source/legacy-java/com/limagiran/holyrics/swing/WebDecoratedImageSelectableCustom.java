package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class WebDecoratedImageSelectableCustom extends JComponent {
   private static final BufferedImage BORDER_RED_LINE_TOP = Images.load("redLineBorder.png");
   private static final BufferedImage BORDER_RED_LINE_LEFT = Img.turn(BORDER_RED_LINE_TOP, 90.0, true);
   private static final BufferedImage BORDER_RED_LINE_BOTTOM = Img.turn(BORDER_RED_LINE_TOP, 180.0, true);
   private static final BufferedImage BORDER_RED_LINE_RIGHT = Img.turn(BORDER_RED_LINE_TOP, 270.0, true);
   private static final BufferedImage BORDER_RED_CORNER_TR = Images.load("redCornerBorder.png");
   private static final BufferedImage BORDER_RED_CORNER_RB = Img.turn(BORDER_RED_CORNER_TR, 90.0, true);
   private static final BufferedImage BORDER_RED_CORNER_BL = Img.turn(BORDER_RED_CORNER_TR, 180.0, true);
   private static final BufferedImage BORDER_RED_CORNER_LT = Img.turn(BORDER_RED_CORNER_TR, 270.0, true);
   private static final BufferedImage BORDER_GRAY_LINE_TOP = Images.load("grayLineBorder.png");
   private static final BufferedImage BORDER_GRAY_LINE_LEFT = Img.turn(BORDER_GRAY_LINE_TOP, 90.0, true);
   private static final BufferedImage BORDER_GRAY_LINE_BOTTOM = Img.turn(BORDER_GRAY_LINE_TOP, 180.0, true);
   private static final BufferedImage BORDER_GRAY_LINE_RIGHT = Img.turn(BORDER_GRAY_LINE_TOP, 270.0, true);
   private static final BufferedImage BORDER_GRAY_CORNER_TR = Images.load("grayCornerBorder.png");
   private static final BufferedImage BORDER_GRAY_CORNER_RB = Img.turn(BORDER_GRAY_CORNER_TR, 90.0, true);
   private static final BufferedImage BORDER_GRAY_CORNER_BL = Img.turn(BORDER_GRAY_CORNER_TR, 180.0, true);
   private static final BufferedImage BORDER_GRAY_CORNER_LT = Img.turn(BORDER_GRAY_CORNER_TR, 270.0, true);
   private static final Color GRAY = new Color(90, 90, 90);
   protected BufferedImage icon;
   private boolean selected;

   public void setImage(BufferedImage image) {
      this.icon = image;
   }

   public void setIcon(BufferedImage icon) {
      this.icon = icon;
   }

   public BufferedImage getIcon() {
      return this.icon;
   }

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (this.icon != null) {
         int w12 = this.icon.getWidth() + 12;
         int h12 = this.icon.getHeight() + 12;
         if (this.selected) {
            g.setColor(Color.RED);
            g.fillRect(12, 12, w12 - 12, h12 - 12);
            g.drawImage(BORDER_RED_LINE_TOP, 12, 0, w12 - 12, 12, null);
            g.drawImage(BORDER_RED_LINE_LEFT, w12, 12, 12, h12 - 12, null);
            g.drawImage(BORDER_RED_LINE_BOTTOM, 12, h12, w12 - 12, 12, null);
            g.drawImage(BORDER_RED_LINE_RIGHT, 0, 12, 12, h12 - 12, null);
            g.drawImage(BORDER_RED_CORNER_TR, 0, 0, null);
            g.drawImage(BORDER_RED_CORNER_RB, w12, 0, null);
            g.drawImage(BORDER_RED_CORNER_BL, w12, h12, null);
            g.drawImage(BORDER_RED_CORNER_LT, 0, h12, null);
         } else {
            g.setColor(GRAY);
            g.fillRect(12, 12, w12 - 12, h12 - 12);
            g.drawImage(BORDER_GRAY_LINE_TOP, 12, 0, w12 - 12, 12, null);
            g.drawImage(BORDER_GRAY_LINE_LEFT, w12, 12, 12, h12 - 12, null);
            g.drawImage(BORDER_GRAY_LINE_BOTTOM, 12, h12, w12 - 12, 12, null);
            g.drawImage(BORDER_GRAY_LINE_RIGHT, 0, 12, 12, h12 - 12, null);
            g.drawImage(BORDER_GRAY_CORNER_TR, 0, 0, null);
            g.drawImage(BORDER_GRAY_CORNER_RB, w12, 0, null);
            g.drawImage(BORDER_GRAY_CORNER_BL, w12, h12, null);
            g.drawImage(BORDER_GRAY_CORNER_LT, 0, h12, null);
         }

         g.drawImage(this.icon, 12, 12, null);
      }
   }

   @Override
   public Dimension getPreferredSize() {
      return this.icon != null ? new Dimension(this.icon.getWidth() + 24, this.icon.getHeight() + 24) : new Dimension(0, 0);
   }

   public boolean isSelected() {
      return this.selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
      this.repaint();
   }
}
