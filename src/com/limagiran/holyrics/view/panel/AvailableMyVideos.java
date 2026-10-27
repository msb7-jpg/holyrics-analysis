package com.limagiran.holyrics.view.panel;

import com.limagiran.holyrics.model.VideoHL;
import com.limagiran.holyrics.swing.SelectablePanel;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public abstract class AvailableMyVideos extends JPanel implements SelectablePanel {
   public static final Dimension SIZE = new Dimension(170, 100);
   private final JLabel jLabelGif = new JLabel();
   private JButton jButtonRemove = new JButton();
   public final int videoByteId;
   private byte[] videoByteGif;

   public AvailableMyVideos(VideoHL videoByte) {
      this.videoByteId = videoByte.id;
      this.videoByteGif = videoByte.gif;
      this.init();
   }

   public abstract void remove();

   private void init() {
      this.setBackground(new Color(222, 222, 222));
      this.setBorder(StyleUI.BORDER_UNSELECTED);
      this.setMaximumSize(SIZE);
      this.setMinimumSize(SIZE);
      this.setPreferredSize(SIZE);
      this.setLayout(new AbsoluteLayout());
      this.jLabelGif.setIcon(new ImageIcon(this.videoByteGif));
      this.jLabelGif.setHorizontalAlignment(0);
      this.add(this.jLabelGif, new AbsoluteConstraints(5, 5, 160, 90));
      this.jButtonRemove = this.createDefaultButtonRemove();
      this.jButtonRemove.setBorder(null);
      this.jButtonRemove.setBorderPainted(false);
      this.jButtonRemove.setContentAreaFilled(false);
      this.jButtonRemove.setCursor(new Cursor(12));
      this.jButtonRemove.setFocusPainted(false);
      this.jButtonRemove.setFocusable(false);
      this.jButtonRemove.setMargin(new Insets(0, 0, 0, 0));
      this.jButtonRemove.addActionListener(e -> this.remove());
      this.add(this.jButtonRemove, new AbsoluteConstraints(130, 0, 36, 36), 0);
   }

   private JButton createDefaultButtonRemove() {
      return new JButton() {
         @Override
         protected void paintComponent(Graphics g) {
            BufferedImage bi = this.getModel().isRollover() ? Images.ICON_REMOVE_36 : Images.ICON_REMOVE_24;
            Graphics2D g2 = (Graphics2D)g;
            G2Utils.setupGraphics(g2);
            g2.setColor(new Color(200, 200, 200, 150));
            Point p = new Point((this.getWidth() - bi.getWidth()) / 2, (this.getHeight() - bi.getHeight()) / 2);
            g2.fillOval(p.x, p.y, bi.getWidth() + 4, bi.getHeight() + 4);
            g2.drawImage(bi, p.x + 2, p.y + 2, null);
            g2.dispose();
         }
      };
   }

   @Override
   public JComponent getPanel() {
      return this;
   }

   @Override
   public void clear() {
      this.jLabelGif.setIcon(null);
      this.videoByteGif = null;
   }
}
