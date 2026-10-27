package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.BooleanOptional;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public abstract class AbstractWebListCellRendererIconLabel extends JPanel {
   protected final JLabel icon = new JLabel();
   protected final JLabel desc = new JLabel();
   protected final JPanel content = new JPanel();
   private final AbstractWebListCellRendererIconLabel.Builder builder;

   public AbstractWebListCellRendererIconLabel(AbstractWebListCellRendererIconLabel.Builder builder) {
      this.builder = builder;
      this.init();
   }

   private void init() {
      this.setLayout(new BorderLayout());
      this.setOpaque(false);
      this.setBorder(new EmptyBorder(4, 4, 2, 4));
      this.icon.setHorizontalAlignment(0);
      this.desc.setHorizontalAlignment(this.builder.descBottom ? 0 : 2);
      this.content.setLayout(new BorderLayout(6, 6));
      this.content.setOpaque(false);
      this.add(this.content, "Center");
      BooleanOptional.runIfTrue(this.builder.top, () -> this.add(new JSeparator(0), "First"));
      BooleanOptional.runIfTrue(this.builder.bottom, () -> this.add(new JSeparator(0), "Last"));
      BooleanOptional.runIfTrue(this.builder.left, () -> this.add(new JSeparator(1), "West"));
      BooleanOptional.runIfTrue(this.builder.right, () -> this.add(new JSeparator(1), "East"));
   }

   public Component getListCellRendererComponent(JList list, ImageIcon ic, String s, int index, boolean isSelected, boolean cellHasFocus) {
      this.content.removeAll();
      this.icon.setIcon(ic);
      this.desc.setText(s);
      this.content.add(this.icon, this.builder.descBottom ? "First" : "West");
      this.content.add(this.desc, "Center");
      return this;
   }

   public static class Builder {
      private boolean top;
      private boolean bottom;
      private boolean left;
      private boolean right;
      private boolean descBottom = true;

      public AbstractWebListCellRendererIconLabel.Builder setTop(boolean top) {
         this.top = top;
         return this;
      }

      public AbstractWebListCellRendererIconLabel.Builder setBottom(boolean bottom) {
         this.bottom = bottom;
         return this;
      }

      public AbstractWebListCellRendererIconLabel.Builder setLeft(boolean left) {
         this.left = left;
         return this;
      }

      public AbstractWebListCellRendererIconLabel.Builder setRight(boolean right) {
         this.right = right;
         return this;
      }

      public AbstractWebListCellRendererIconLabel.Builder setHorizontal(boolean flag) {
         return this.setLeft(flag).setRight(flag);
      }

      public AbstractWebListCellRendererIconLabel.Builder setVertical(boolean flag) {
         return this.setTop(flag).setBottom(flag);
      }

      public AbstractWebListCellRendererIconLabel.Builder setAll(boolean flag) {
         return this.setVertical(flag).setHorizontal(flag);
      }

      public AbstractWebListCellRendererIconLabel.Builder setDescBottom(boolean flag) {
         this.descBottom = flag;
         return this;
      }
   }
}
