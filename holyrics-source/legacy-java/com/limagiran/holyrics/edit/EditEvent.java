package com.limagiran.holyrics.edit;

import com.limagiran.holyrics.control.EventController;
import com.limagiran.holyrics.edit.event.EventTable;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Images;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class EditEvent extends EventTable implements IEditView {
   @Override
   public String getTitle() {
      return I18N.WORD_EVENTS.val();
   }

   @Override
   public JPanel getPanel() {
      return this;
   }

   @Override
   public boolean exit() {
      EventController.save();
      return true;
   }

   @Override
   public ImageIcon getIcon() {
      return new ImageIcon(Images.load("event16.png"));
   }
}
