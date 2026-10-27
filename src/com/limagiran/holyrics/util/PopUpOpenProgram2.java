package com.limagiran.holyrics.util;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window.Type;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class PopUpOpenProgram2 extends JFrame {
   private static final Dimension SIZE = new Dimension(660, 320);
   private static final BasicStroke STROKE = new BasicStroke(10.0F, 1, 1);
   private static final Color COLOR = new Color(255, 255, 255, 8);
   private static final Font FONT = new Font("Palatino Linotype", 0, 80);
   private boolean closed;

   public PopUpOpenProgram2() {
      this.init();
   }

   private void init() {
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            super.componentShown(e);
            Utils.startThreadDaemon(() -> {
               while (PopUpOpenProgram2.this.isVisible()) {
                  SwingUtilities.invokeLater(PopUpOpenProgram2.this::repaint);
                  Utils.sleep(20L);
               }
            });
         }

         @Override
         public void componentHidden(ComponentEvent e) {
            super.componentHidden(e);
         }
      });
      this.setLayout(new BorderLayout());
      this.setDefaultCloseOperation(0);
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setResizable(false);
      this.setType(Type.POPUP);
      this.setUndecorated(true);
      this.setSize(SIZE);
      this.setPreferredSize(SIZE);
      this.add(this.createLabel(), "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   @Override
   public void setVisible(boolean flag) {
      if (!flag) {
         this.dispose();
      }

      if (!this.closed) {
         super.setVisible(true);
      }
   }

   @Override
   public void dispose() {
      this.closed = true;
      super.dispose();
   }

   private JLabel createLabel() {
      return new JLabel() {
         @Override
         public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            G2Utils.setupGraphics(g2);
            g2.setColor(new Color(0, 162, 232));
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.setFont(PopUpOpenProgram2.FONT);
            g2.setColor(Color.WHITE);
            G2Utils.drawString(PopUpOpenProgram2.SIZE, g2, 0.0, G2Utils.VerticalAlign.MIDDLE, "Holyrics");
            BufferedImage l = PopUpOpenProgram2.createLoad((int)(System.currentTimeMillis() % 3600L / 10L));
            g2.drawImage(l, PopUpOpenProgram2.SIZE.width / 2 - 50, PopUpOpenProgram2.SIZE.height - 110, null);
            g2.dispose();
         }
      };
   }

   private static BufferedImage createLoad(int angle) {
      BufferedImage bi = new BufferedImage(100, 100, 2);
      Graphics2D g = bi.createGraphics();
      G2Utils.setupGraphics(g);
      g.setStroke(STROKE);
      int initAng = (90 - angle) % 360 + 90;
      g.setColor(COLOR);
      IntStream.rangeClosed(1, 255).forEach(op -> g.drawArc(10, 10, 80, 80, initAng, (int)Math.pow(1.022055, 256.0 - (double)op)));
      g.dispose();
      return bi;
   }
}
