package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.model.theme.Theme;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class ComboBoxModelTheme extends AbstractListModel<Theme> implements ComboBoxModel<Theme> {
   private Object selected;

   @Override
   public int getSize() {
      return ThemeController.getThemes().size();
   }

   public Theme getElementAt(int index) {
      return ThemeController.getThemes().get(index);
   }

   @Override
   public void setSelectedItem(Object anItem) {
      this.selected = anItem;
   }

   @Override
   public Object getSelectedItem() {
      return this.selected;
   }

   public void update() {
      super.fireContentsChanged(this, 0, this.getSize());
   }
}
