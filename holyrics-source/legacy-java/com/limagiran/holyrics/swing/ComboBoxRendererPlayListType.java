package com.limagiran.holyrics.swing;

import com.alee.laf.combobox.WebComboBoxCellRenderer;
import com.alee.laf.combobox.WebComboBoxElement;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;

public class ComboBoxRendererPlayListType extends WebComboBoxCellRenderer {
   public Component getListCellRendererComponent(JList list, Object obj, int index, boolean isSelected, boolean cellHasFocus) {
      WebComboBoxElement c = (WebComboBoxElement)super.getListCellRendererComponent(list, obj, index, isSelected, cellHasFocus);
      c.setHorizontalAlignment(0);
      c.setBorder(new EmptyBorder(2, 0, 2, 0));
      return c;
   }
}
