package com.limagiran.holyrics.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public final class PopUp extends JLabel {
   private Color foreground = Color.BLACK;
   private Color shadow = new Color(180, 180, 180);
   private final Font font;
   private ImageIcon imageIcon;
   public final String text;
   public final long time;

   public static void show(String text, long time) {
      show(text, new Font("Tahoma", 1, 40), time);
   }

   public static void show(String text, Font font, long time) {
      JFrame f = new JFrame();
      f.setUndecorated(true);
      PopUp popUp = new PopUp(text, font, time);
      f.add(popUp);
      f.setBackground(Values.COLOR_TRANSPARENT);
      f.setSize(popUp.getPreferredSize());
      f.setLocationRelativeTo(null);
      popUp.fadeOut(f);
      SwingUtilities.invokeLater(() -> f.setVisible(true));
   }

   private PopUp(String text, Font font, long time) {
      this.text = text;
      this.font = font;
      this.time = time;

      try {
         this.init();
      } catch (Exception var6) {
      }
   }

   private void init() throws Exception {
      BufferedImage image = new BufferedImage(this.getFontMetrics(this.font).stringWidth(this.text) + 10, this.font.getSize() + 5, 2);
      Graphics2D g = image.createGraphics();
      G2Utils.setupGraphics(g);
      TextLayout tl = new TextLayout(this.text, this.font, g.getFontRenderContext());
      g.setPaint(this.shadow);
      tl.draw(g, 3.0F, (float)(this.font.getSize() - 2));
      g.dispose();
      float n = 0.11111111F;
      float[] kernel = new float[]{n, n, n, n, n, n, n, n, n};
      ConvolveOp op = new ConvolveOp(new Kernel(3, 3, kernel), 1, null);
      BufferedImage image2 = op.filter(image, null);
      Graphics2D g2 = image2.createGraphics();
      G2Utils.setupGraphics(g2);
      g2.setPaint(this.foreground);
      tl.draw(g2, 0.0F, (float)this.font.getSize());
      g2.dispose();
      this.imageIcon = new ImageIcon(image2);
      this.setIcon(this.imageIcon);
      this.setSize(this.getFontMetrics(this.font).stringWidth(this.text) + 10, this.font.getSize() + 5);
      this.setPreferredSize(this.getSize());
   }

   private void fadeOut(JFrame f) {
      Utils.startThreadDaemon(() -> {
         try {
            Utils.sleep(this.time);

            for (float j = 19.0F; j >= 0.0F; j--) {
               float jFinal = j;
               SwingUtilities.invokeLater(() -> this.setIcon(transparencia(this.imageIcon, jFinal / 20.0F)));
               Utils.sleep(50L);
            }

            SwingUtilities.invokeLater(f::dispose);
         } catch (Exception var4) {
         }
      });
   }

   private static ImageIcon transparencia(ImageIcon img, float t) {
      BufferedImage _new = new BufferedImage(img.getIconWidth(), img.getIconHeight(), 2);
      Graphics2D g = _new.createGraphics();
      g.setComposite(AlphaComposite.getInstance(3, t));
      g.drawImage(img.getImage(), 0, 0, null);
      g.dispose();
      return new ImageIcon(_new);
   }
}
