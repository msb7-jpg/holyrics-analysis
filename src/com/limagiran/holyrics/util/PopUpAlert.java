package com.limagiran.holyrics.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public final class PopUpAlert extends JLabel {
   private static final int BORDER_THICKNESS = 4;
   private Color foreground = Color.BLACK;
   private Color shadow = new Color(180, 180, 180);
   private final Font font;
   private ImageIcon imageIcon;
   public final String text;
   public final long time;

   public static void show(String text, long time, Rectangle rect) {
      show(text, new Font("Tahoma", 1, 40), time, rect);
   }

   public static void show(String text, long time, Component c) {
      show(text, new Font("Tahoma", 1, 40), time, c);
   }

   public static void show(String text, Font font, long time, Component c) {
      try {
         Rectangle rect = new Rectangle(c.getLocationOnScreen(), c.getSize());
         rect.x -= 4;
         rect.y -= 4;
         rect.width += 8;
         rect.height += 8;
         show(text, font, time, rect);
      } catch (Exception var6) {
      }
   }

   public static void show(String s, Font f, long t, Rectangle r) {
      final JDialog dialog = new JDialog();
      dialog.addFocusListener(new FocusAdapter() {
         @Override
         public void focusLost(FocusEvent e) {
            dialog.dispose();
         }
      });
      dialog.setLayout(new AbsoluteLayout());
      dialog.setUndecorated(true);
      PopUpAlert popUp = new PopUpAlert(s, f, t);
      dialog.add(popUp, new AbsoluteConstraints(new Point(), popUp.getPreferredSize()));
      JPanel panel = new JPanel();
      panel.setOpaque(false);
      panel.setBorder(new LineBorder(new Color(64, 64, 64), 4));
      dialog.add(panel, new AbsoluteConstraints(0, popUp.getPreferredSize().height, r.width, r.height));
      dialog.setBackground(Values.COLOR_TRANSPARENT);
      dialog.pack();
      dialog.setLocation(r.x, r.y - popUp.getPreferredSize().height);
      popUp.fadeOut(dialog, panel);
      SwingUtilities.invokeLater(() -> dialog.setVisible(true));
   }

   private PopUpAlert(String text, Font font, long time) {
      this.text = text;
      this.font = font;
      this.time = time;

      try {
         this.init();
      } catch (Exception var6) {
      }
   }

   private void init() throws Exception {
      FontMetrics fm = this.getFontMetrics(this.font);
      BufferedImage bi = new BufferedImage(fm.stringWidth(this.text) + 10, fm.getHeight() + 5, 2);
      Graphics2D g = bi.createGraphics();
      G2Utils.setupGraphics(g);
      TextLayout tl = new TextLayout(this.text, this.font, g.getFontRenderContext());
      g.setPaint(this.shadow);
      tl.draw(g, 3.0F, (float)(this.font.getSize() - 2));
      g.dispose();
      float n = 0.11111111F;
      float[] kernel = new float[]{n, n, n, n, n, n, n, n, n};
      ConvolveOp op = new ConvolveOp(new Kernel(3, 3, kernel), 1, null);
      bi = op.filter(bi, null);
      Graphics2D g2 = bi.createGraphics();
      G2Utils.setupGraphics(g2);
      g2.setPaint(this.foreground);
      tl.draw(g2, 0.0F, (float)this.font.getSize());
      g2.dispose();
      this.imageIcon = new ImageIcon(bi);
      this.setIcon(this.imageIcon);
      this.setSize(this.getFontMetrics(this.font).stringWidth(this.text) + 10, this.font.getSize() + 5);
      this.setPreferredSize(this.getSize());
   }

   private void fadeOut(JDialog f, JPanel panel) {
      Utils.startThreadDaemon(() -> {
         Utils.sleep(this.time);

         for (float j = 19.0F; j >= 0.0F; j--) {
            float jFinal = j;
            SwingUtilities.invokeLater(() -> {
               float t = jFinal / 20.0F;
               this.setIcon(transparencia(this.imageIcon, t));
               panel.setBorder(new LineBorder(new Color(64, 64, 64, (int)(255.0F * t)), 4));
               panel.repaint();
            });
            Utils.sleep(50L);
         }

         SwingUtilities.invokeLater(f::dispose);
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
