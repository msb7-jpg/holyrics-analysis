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
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public abstract class AbstractWebListCellRendererWithSeparatorDoubleLine extends JPanel {
   protected final WebListCellRenderer top = new WebListCellRenderer();
   protected final WebListCellRenderer bottom = new WebListCellRenderer();
   protected final double ratioSecondLine;
   private BufferedImage icon;
   protected final JPanel content = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         if (AbstractWebListCellRendererWithSeparatorDoubleLine.this.icon != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            G2Utils.setupGraphics(g2);
            g2.drawImage(
               AbstractWebListCellRendererWithSeparatorDoubleLine.this.icon,
               (this.getWidth() - AbstractWebListCellRendererWithSeparatorDoubleLine.this.icon.getWidth()) / 2,
               (this.getHeight() - AbstractWebListCellRendererWithSeparatorDoubleLine.this.icon.getHeight()) / 2,
               null
            );
            g2.dispose();
         }
      }
   };

   public AbstractWebListCellRendererWithSeparatorDoubleLine(AbstractWebListCellRendererWithSeparatorDoubleLine.Builder builder) {
      this.ratioSecondLine = builder.ratioSecondLine;
      this.init(builder);
   }

   private void init(AbstractWebListCellRendererWithSeparatorDoubleLine.Builder builder) {
      this.setLayout(new BorderLayout());
      this.setOpaque(false);
      this.content.setLayout(new GridLayout(2, 1));
      this.content.setOpaque(false);
      this.add(this.content, "Center");
      BooleanOptional.runIfTrue(builder.top, () -> this.add(new JSeparator(0), "First"));
      BooleanOptional.runIfTrue(builder.bottom, () -> this.add(new JSeparator(0), "Last"));
      BooleanOptional.runIfTrue(builder.left, () -> this.add(new JSeparator(1), "West"));
      BooleanOptional.runIfTrue(builder.right, () -> this.add(new JSeparator(1), "East"));
   }

   public Component getListCellRendererComponent(JList list, String valueTop, String valueBottom, int index, boolean isSelected, boolean cellHasFocus) {
      this.content.removeAll();
      Component c = this.top.getListCellRendererComponent(list, valueTop, index, isSelected, cellHasFocus);
      this.content.add(c);
      Component c2 = this.bottom.getListCellRendererComponent(list, valueBottom, index, isSelected, cellHasFocus);
      Font f = c2.getFont();
      c2.setFont(new Font(f.getName(), f.getStyle(), (int)Math.ceil((double)f.getSize() * this.ratioSecondLine)));
      this.content.add(c2);
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
      private double ratioSecondLine = 1.0;

      public AbstractWebListCellRendererWithSeparatorDoubleLine.Builder setTop(boolean top) {
         this.top = top;
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorDoubleLine.Builder setBottom(boolean bottom) {
         this.bottom = bottom;
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorDoubleLine.Builder setLeft(boolean left) {
         this.left = left;
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorDoubleLine.Builder setRight(boolean right) {
         this.right = right;
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorDoubleLine.Builder setRatioSecondLine(double ratioSecondLine) {
         this.ratioSecondLine = Utils.range(ratioSecondLine, 0.1, 2.0);
         return this;
      }

      public AbstractWebListCellRendererWithSeparatorDoubleLine.Builder setHorizontal(boolean flag) {
         return this.setLeft(flag).setRight(flag);
      }

      public AbstractWebListCellRendererWithSeparatorDoubleLine.Builder setVertical(boolean flag) {
         return this.setTop(flag).setBottom(flag);
      }

      public AbstractWebListCellRendererWithSeparatorDoubleLine.Builder setAll(boolean flag) {
         return this.setVertical(flag).setHorizontal(flag);
      }
   }
}
