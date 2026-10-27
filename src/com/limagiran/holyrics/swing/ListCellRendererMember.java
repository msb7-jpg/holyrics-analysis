package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererMember extends AbstractWebListCellRendererWithSeparator implements ListCellRenderer<Member> {
   private final Color foreground = this.view.getForeground();

   public ListCellRendererMember() {
      super(true);
      this.view.setHorizontalAlignment(0);
   }

   public Component getListCellRendererComponent(JList<? extends Member> list, Member m, int index, boolean isSelected, boolean cellHasFocus) {
      list.setSelectionForeground(m.isActive() ? this.foreground : StyleUI.FOREGROUND_DISABLED_OBJECT);
      list.setForeground(m.isActive() ? this.foreground : StyleUI.FOREGROUND_DISABLED_OBJECT);
      String value = (!m.isActive() ? "[" + I18N.WORD_DISABLED.val() + "] " : "") + m.toString();
      return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
   }
}
