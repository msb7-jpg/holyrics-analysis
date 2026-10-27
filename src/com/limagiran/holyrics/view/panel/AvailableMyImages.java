package com.limagiran.holyrics.view.panel;

import com.limagiran.holyrics.model.ImageHL;
import com.limagiran.holyrics.swing.SelectablePanel;
import com.limagiran.holyrics.swing.WebDecoratedImageSelectableCustom;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import javax.swing.JButton;

public abstract class AvailableMyImages extends WebDecoratedImageSelectableCustom implements SelectablePanel {
   public static final Dimension SIZE = new Dimension(330, 190);
   public int imageByteId;
   private JButton jButtonRemove;

   public AvailableMyImages(ImageHL imageByte) {
      this.imageByteId = imageByte.id;

      BufferedImage bi;
      try {
         bi = ImageIO.read(new ByteArrayInputStream(imageByte.bytesThumb));
      } catch (Exception var4) {
         bi = Images.imageNotFound();
      }

      this.init(bi);
   }

   public abstract void remove();

   private void init(BufferedImage image) {
      this.setImage(Img.resize(image, SIZE));
      this.jButtonRemove = this.createDefaultButtonRemove();
      this.jButtonRemove.setBorder(null);
      this.jButtonRemove.setBorderPainted(false);
      this.jButtonRemove.setContentAreaFilled(false);
      this.jButtonRemove.setCursor(new Cursor(12));
      this.jButtonRemove.setFocusPainted(false);
      this.jButtonRemove.setFocusable(false);
      this.jButtonRemove.setMargin(new Insets(0, 0, 0, 0));
      this.jButtonRemove.addActionListener(e -> this.remove());
      this.jButtonRemove.setBounds(this.getIcon().getWidth() - 14, 10, 36, 36);
      this.add(this.jButtonRemove);
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

   public WebDecoratedImageSelectableCustom getPanel() {
      return this;
   }

   @Override
   public void clear() {
      this.setImage(Images.NULL1X1);
   }
}
