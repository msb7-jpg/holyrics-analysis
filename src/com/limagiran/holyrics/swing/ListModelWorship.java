package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.WorshipController;
import com.limagiran.holyrics.model.Worship;
import javax.swing.DefaultListModel;

public class ListModelWorship extends DefaultListModel<Worship> {
   public ListModelWorship() {
      WorshipController.getWorships(false).forEach(this::addElement);
   }

   public final void update() {
      this.removeAllElements();
      WorshipController.getWorships(false).forEach(this::addElement);
   }
}
