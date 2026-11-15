package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.model.theme.Theme;
import javax.swing.DefaultListModel;

public class ListModelTheme extends DefaultListModel<Theme> {
   public ListModelTheme() {
      ThemeController.getThemes().forEach(this::addElement);
   }

   public final void update() {
      this.removeAllElements();
      ThemeController.getThemes().forEach(this::addElement);
   }
}
