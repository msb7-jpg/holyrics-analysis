package com.limagiran.holyrics.swing;

import com.alee.laf.list.WebListCellRenderer;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.util.BooleanOptional;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public abstract class AbstractWebListCellRendererWithSeparator extends JPanel {
   protected final WebPanel content = new WebPanel();
   protected final WebListCellRenderer view = new WebListCellRenderer();

   public AbstractWebListCellRendererWithSeparator(boolean top, boolean bottom, boolean left, boolean right) {
      this.init(top, bottom, left, right);
   }

   public AbstractWebListCellRendererWithSeparator(boolean all) {
      this(all, all, all, all);
   }

   public AbstractWebListCellRendererWithSeparator(boolean vertical, boolean horizontal) {
      this(vertical, vertical, horizontal, horizontal);
   }

   private void init(boolean top, boolean bottom, boolean left, boolean right) {
      this.setLayout(new BorderLayout());
      BooleanOptional.runIfTrue(top, () -> this.add(new JSeparator(0), "First"));
      BooleanOptional.runIfTrue(bottom, () -> this.add(new JSeparator(0), "Last"));
      BooleanOptional.runIfTrue(left, () -> this.add(new JSeparator(1), "West"));
      BooleanOptional.runIfTrue(right, () -> this.add(new JSeparator(1), "East"));
      this.content.setLayout(new BorderLayout());
      this.content.setOpaque(false);
      this.setOpaque(false);
      this.add(this.content, "Center");
   }

   public Component getListCellRendererComponent(JList list, String value, int index, boolean isSelected, boolean cellHasFocus) {
      this.content.removeAll();
      Component c = this.view.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      this.content.add(c, "Center");
      return this;
   }
}
