package com.limagiran.holyrics.swing;

import com.alee.laf.list.WebListCellRenderer;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class AbstractWebListCellRendererWithSeparatorTripleLine extends JPanel {
   protected final GridBagConstraints gbc = new GridBagConstraints();
   protected final WebListCellRenderer top;
   protected final WebListCellRenderer middle;
   protected final WebListCellRenderer bottom;
   protected final double ratioMiddleLine;
   protected final double ratioBottomLine;
   private BufferedImage icon;
   protected final JPanel content;

   public AbstractWebListCellRendererWithSeparatorTripleLine(AbstractWebListCellRendererWithSeparatorTripleLine.Builder builder) {
      this.gbc.weightx = 1.0;
      this.gbc.gridy = 0;
      this.gbc.fill = 2;
      this.gbc.insets.set(-4, 0, -4, 0);
      this.top = new WebListCellRenderer();
      this.middle = new WebListCellRenderer();
      this.bottom = new WebListCellRenderer();
      this.content = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (AbstractWebListCellRendererWithSeparatorTripleLine.this.icon != null) {
               Graphics2D g2 = (Graphics2D)g.create();
               G2Utils.setupGraphics(g2);
               g2.drawImage(
                  AbstractWebListCellRendererWithSeparatorTripleLine.this.icon,
                  (this.getWidth() - AbstractWebListCellRendererWithSeparatorTripleLine.this.icon.getWidth()) / 2,
                  (this.getHeight() - AbstractWebListCellRendererWithSeparatorTripleLine.this.icon.getHeight()) / 2,
                  null
               );
               g2.dispose();
            }
         }
      };
      this.ratioMiddleLine = builder.ratioMiddleLine;
      this.ratioBottomLine = builder.ratioBottomLine;
      this.init(builder);
   }

   private void init(AbstractWebListCellRendererWithSeparatorTripleLine.Builder builder) {
      this.setLayout(new BorderLayout());
      this.setOpaque(false);
      this.content.setLayout(new GridBagLayout());
      this.content.setOpaque(false);
      this.add(this.content, "Center");
      BooleanOptional.runIfTrue(builder.top, () -> this.add(new JSeparator(0), "First"));
      BooleanOptional.runIfTrue(builder.bottom, () -> this.add(new JSeparator(0), "Last"));
      BooleanOptional.runIfTrue(builder.left, () -> this.add(new JSeparator(1), "West"));
      BooleanOptional.runIfTrue(builder.right, () -> this.add(new JSeparator(1), "East"));
   }

   public Component getListCellRendererComponent(
      JList list, String valueTop, String valueMiddle, String valueBottom, int index, boolean isSelected, boolean cellHasFocus
   ) {
      this.content.removeAll();
      Component c = this.top.getListCellRendererComponent(list, valueTop, index, isSelected, cellHasFocus);
      this.content.add(c, this.gbc);
      Component c2 = this.middle.getListCellRendererComponent(list, valueMiddle, index, isSelected, cellHasFocus);
      Font f = c2.getFont();
      c2.setFont(new Font(f.getName(), f.getStyle(), (int)Math.ceil((double)f.getSize() * this.ratioMiddleLine)));
      GridBagConstraints gbcClone = (GridBagConstraints)this.gbc.clone();
      gbcClone.gridy = 1;
      this.content.add(c2, gbcClone);
      Component c3 = this.bottom.getListCellRendererComponent(list, valueBottom, index, isSelected, cellHasFocus);
      f = c3.getFont();
      c3.setFont(new Font(f.getName(), f.getStyle(), (int)Math.ceil((double)f.getSize() * this.ratioBottomLine)));
      gbcClone = (GridBagConstraints)this.gbc.clone();
      gbcClone.gridy = 2;
      this.content.add(c3, gbcClone);
      return this;
   }

   protected void setIcon(BufferedImage icon) {
      this.icon = icon;
   }

   public static class Builder {
      private boolean top;
      private boolean bottom;
      private boolean left;
      private boolean right;
      private double ratioMiddleLine = 1.0;
      private double ratioBottomLine = 1.0;

      public AbstractWebListCellRendererWithSeparatorTripleLine.Builder setTop(boolean top) {
         this.top = top;
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorTripleLine.Builder setBottom(boolean bottom) {
         this.bottom = bottom;
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorTripleLine.Builder setLeft(boolean left) {
         this.left = left;
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorTripleLine.Builder setRight(boolean right) {
         this.right = right;
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorTripleLine.Builder setRatioMiddleLine(double ratioMiddleLine) {
         this.ratioMiddleLine = Utils.range(ratioMiddleLine, 0.1, 2.0);
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorTripleLine.Builder setRatioBottomLine(double ratioBottomLine) {
         this.ratioBottomLine = Utils.range(ratioBottomLine, 0.1, 2.0);
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorTripleLine.Builder setHorizontal(boolean flag) {
         return this.setLeft(flag).setRight(flag);
      }

      public AbstractWebListCellRendererWithSeparatorTripleLine.Builder setVertical(boolean flag) {
         return this.setTop(flag).setBottom(flag);
      }

      public AbstractWebListCellRendererWithSeparatorTripleLine.Builder setAll(boolean flag) {
         return this.setVertical(flag).setHorizontal(flag);
      }
   }
}
