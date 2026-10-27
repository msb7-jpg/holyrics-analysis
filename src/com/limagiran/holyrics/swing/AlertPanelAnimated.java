package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public abstract class AlertPanelAnimated extends JComponent {
   public static final float VALUE = 32.0F;
   public static final int MIN_SKIP_NON_PRIMARY = 64;
   private BufferedImage image;
   private double count = 0.0;
   private boolean show;
   private boolean overlap;

   public abstract void fireChange();

   public abstract boolean paint();

   public abstract boolean isShow();

   public abstract boolean isStageView();

   public abstract boolean isOverlap();

   public abstract String getContent();

   public abstract Color getBackgroundColor();

   public abstract Color getForegroundColor();

   public abstract Font getTextFont();

   public abstract double getVelocity();

   public abstract boolean stopAnimation();

   public abstract int skipFrames();

   public void updated() {
   }

   public AlertPanelAnimated() {
      this.init();
   }

   private void init() {
      this.setDoubleBuffered(true);
      Utils.startThreadDaemon(() -> {
         Utils.sleep(1000L);
         this.show = this.isShow();
         this.overlap = this.isOverlap();
         long lastTime = System.currentTimeMillis();

         for (long skip = -1L; !this.stopAnimation(); Utils.sleep(32L)) {
            long currentTime = System.currentTimeMillis();
            this.count = this.count - this.getVelocity() / 2.0 * (double)((float)(currentTime - lastTime) / 32.0F);
            lastTime = currentTime;
            int skipFrames = this.skipFrames();
            if (this.paint() && (skipFrames <= 0 || ++skip % (long)skipFrames == 0L)) {
               this.prepare();
               this.updated();
               this.repaint();
            }

            boolean fire = false;
            if (this.show != this.isShow()) {
               this.show = this.isShow();
               fire = true;
            }

            if (this.overlap != this.isOverlap()) {
               this.overlap = this.isOverlap();
               fire = true;
            }

            if (fire) {
               this.fireChange();
            }
         }
      });
   }

   @Override
   protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      BufferedImage bi = this.getImage();
      if (bi != null) {
         g2.drawImage(bi, 0, 0, null);
      }
   }

   public void prepare() {
      String text = this.getContent();
      Dimension d = this.getSize();
      if (d.width > 0 && d.height > 0 && this.paint()) {
         BufferedImage bi = new BufferedImage(d.width, d.height, 2);
         Graphics2D g = bi.createGraphics();
         G2Utils.setupGraphics(g);
         g.setFont(G2Utils.getBestFont(Math.max(d.height - 8, 1), this.getTextFont(), g));
         g.setColor(this.getBackgroundColor());
         g.fillRect(0, 0, d.width, d.height);
         g.setColor(this.getForegroundColor());
         FontMetrics fm = g.getFontMetrics();
         int y = fm.getAscent() + fm.getLeading() + (d.height - fm.getHeight()) / 2;
         int width = fm.stringWidth(text);
         double countAbs = Math.abs(this.count);
         if (countAbs > (double)width) {
            this.count = (double)(d.width / 2);
         }

         if ((double)width < (double)d.width * 0.98) {
            g.drawString(text, (d.width - width) / 2, y);
         } else {
            g.drawString(text, (float)this.count, (float)y);
            float endPoint = (float)((double)width - countAbs);
            if (endPoint > 0.0F && endPoint < (float)(d.width / 2)) {
               g.drawString(text, endPoint + (float)(d.width / 2), (float)y);
            }
         }

         g.dispose();
         this.setImage(bi);
      } else {
         this.setImage(null);
      }
   }

   private synchronized void setImage(BufferedImage image) {
      if (this.image != null) {
         this.image.flush();
         this.image = null;
      }

      this.image = image;
   }

   public synchronized BufferedImage getImage() {
      return this.image;
   }
}
