package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.FavoriteController;
import com.limagiran.holyrics.view.MusicMain;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class ComboBoxModelHItemNextPresentation extends AbstractListModel<Object> implements ComboBoxModel<Object> {
   private final List list = new ArrayList();
   private Object selected;

   public ComboBoxModelHItemNextPresentation() {
      this.fillList();
   }

   private void fillList() {
      synchronized (this) {
         this.list.clear();
         MusicMain.staticGetPlayList().forEach(this.list::add);
         MusicMain.staticGetPlayListMedia().forEach(this.list::add);
         FavoriteController.getAll().stream().forEach(this.list::add);
      }
   }

   @Override
   public int getSize() {
      return this.list.size() + 1;
   }

   @Override
   public Object getElementAt(int index) {
      try {
         return index > 0 ? this.list.get(index - 1) : null;
      } catch (Exception var3) {
         return null;
      }
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
      this.fillList();
      super.fireContentsChanged(this, 0, this.getSize());
   }
}
