package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.Profile;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererProfile extends AbstractWebListCellRendererWithSeparator implements ListCellRenderer<Profile> {
   public ListCellRendererProfile() {
      super(true, false);
      this.view.setHorizontalAlignment(0);
   }

   public Component getListCellRendererComponent(JList<? extends Profile> list, Profile p, int index, boolean isSelected, boolean cellHasFocus) {
      return this.getListCellRendererComponent(list, p.getName(), index, isSelected, cellHasFocus);
   }
}
