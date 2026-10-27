package com.limagiran.holyrics.share;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ReceiveUpdateFromMobileView extends JDialog {
   private static final Dimension SIZE = new Dimension(700, 450);
   private static final BasicStroke STROKE = new BasicStroke(10.0F, 1, 1);
   private static final Color COLOR = new Color(255, 255, 255, 8);
   private static final Font FONT = new Font("Palatino Linotype", 0, 40);
   private static final ReceiveUpdateFromMobileView INSTANCE = new ReceiveUpdateFromMobileView();
   private boolean available;
   private String status = "";

   private ReceiveUpdateFromMobileView() {
      this.init();
   }

   private void init() {
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            super.componentShown(e);
            Utils.startThreadDaemon(() -> {
               while (ReceiveUpdateFromMobileView.this.isVisible()) {
                  SwingUtilities.invokeLater(ReceiveUpdateFromMobileView.this::repaint);
                  Utils.sleep(20L);
               }
            });
         }

         @Override
         public void componentHidden(ComponentEvent e) {
            super.componentHidden(e);
            ReceiveUpdateFromMobileView.this.available = false;
         }
      });
      this.setTitle("Holyrics");
      this.setLayout(new BorderLayout());
      this.setDefaultCloseOperation(2);
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setResizable(false);
      this.setType(Type.POPUP);
      this.setModal(true);
      this.setSize(SIZE);
      this.setPreferredSize(SIZE);
      this.add(this.createLabel(), "Center");
      this.pack();
      this.setLocationRelativeTo(null);
      InsertShortcut.addEscToDispose(this);
   }

   @Override
   public void dispose() {
      this.available = false;
      super.dispose();
   }

   private JLabel createLabel() {
      return new JLabel() {
         @Override
         public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            G2Utils.setupGraphics(g2);
            g2.setColor(StyleUI.COLOR_BLUE);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.setFont(ReceiveUpdateFromMobileView.FONT);
            g2.setColor(Color.WHITE);
            G2Utils.drawString(
               new Dimension(ReceiveUpdateFromMobileView.SIZE.width, ReceiveUpdateFromMobileView.SIZE.height - 120),
               g2,
               0.0,
               G2Utils.VerticalAlign.MIDDLE,
               ReceiveUpdateFromMobileView.this.status
            );
            BufferedImage l = ReceiveUpdateFromMobileView.createLoad((int)(System.currentTimeMillis() % 3600L / 10L));
            g2.drawImage(l, ReceiveUpdateFromMobileView.SIZE.width / 2 - 50, ReceiveUpdateFromMobileView.SIZE.height - 200, null);
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

   public static boolean staticIsVisible() {
      return INSTANCE.isVisible();
   }

   public static void start() {
      if (!INSTANCE.isVisible()) {
         INSTANCE.available = true;
         INSTANCE.status = I18N.MSG_UPDATE_MOBILE_TEXT.val();
         INSTANCE.setLocationRelativeTo(null);
         INSTANCE.setVisible(true);
      }
   }

   public static void setStatus(String value) {
      INSTANCE.status = value;
   }

   public static boolean isAvailable() {
      return INSTANCE.available;
   }

   public static void setAvailable(boolean available) {
      INSTANCE.available = available;
   }
}
