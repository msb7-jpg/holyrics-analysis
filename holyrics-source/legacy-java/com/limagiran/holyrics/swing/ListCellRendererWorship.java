package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Worship;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererWorship extends AbstractWebListCellRendererWithSeparator implements ListCellRenderer<Worship> {
   private final Color foreground = this.view.getForeground();

   public ListCellRendererWorship() {
      super(true, false);
      this.view.setHorizontalAlignment(0);
   }

   public Component getListCellRendererComponent(JList list, Worship w, int index, boolean isSelected, boolean cellHasFocus) {
      list.setSelectionForeground(w.isActive() ? this.foreground : StyleUI.FOREGROUND_DISABLED_OBJECT);
      list.setForeground(w.isActive() ? this.foreground : StyleUI.FOREGROUND_DISABLED_OBJECT);
      String value = (!w.isActive() ? "[" + I18N.WORD_DISABLED.val() + "] " : "") + w.toString();
      return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
   }
}
