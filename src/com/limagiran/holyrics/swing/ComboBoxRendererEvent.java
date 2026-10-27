package com.limagiran.holyrics.swing;

import com.alee.laf.combobox.WebComboBoxCellRenderer;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
import java.awt.Component;
import java.util.Locale;
import javax.swing.JList;

public class ComboBoxRendererEvent extends WebComboBoxCellRenderer {
   private static final String FORMAT_FULL = "<html><head><style>span{white-space:nowrap}</style></head><span>&nbsp;%s, %s - %s <span style=\"font-size:85%%;color: "
      + Utils.getHexColor(StyleUI.FOREGROUND_LIGHT)
      + "\">(%s)</span></span>";
   private static final String FORMAT_WITHOUT_NAME = "<html><head><style>span{white-space:nowrap}</style></head><span>&nbsp;%s, %s - %s</span>";

   public Component getListCellRendererComponent(JList list, Object o, int index, boolean isSelected, boolean cellHasFocus) {
      return super.getListCellRendererComponent(list, format(o), index, isSelected, cellHasFocus);
   }

   private static String format(Object o) {
      if (!Event.class.isInstance(o)) {
         return "";
      } else {
         Event e = (Event)o;
         String arg0 = e.getDate().getDisplayName(7, 1, Locale.getDefault()).toUpperCase();
         String arg1 = String.format("%02d", e.getDate().get(5));
         String arg2 = DateLG.SDF_T_S.format(e.getDate().getTime());
         return e.getName().replace(" ", "").isEmpty()
            ? String.format("<html><head><style>span{white-space:nowrap}</style></head><span>&nbsp;%s, %s - %s</span>", arg0, arg1, arg2)
            : String.format(FORMAT_FULL, arg0, arg1, arg2, e.getName());
      }
   }
}
