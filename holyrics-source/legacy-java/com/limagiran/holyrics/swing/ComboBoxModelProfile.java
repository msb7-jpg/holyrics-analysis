package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.ProfileController;
import com.limagiran.holyrics.model.Profile;
import javax.swing.DefaultComboBoxModel;

public class ComboBoxModelProfile extends DefaultComboBoxModel<Profile> {
   public ComboBoxModelProfile() {
      super(ProfileController.getProfilesInOrderOfAmount(true).toArray(new Profile[0]));
   }

   public void update() {
      Profile p = (Profile)this.getSelectedItem();
      this.removeAllElements();
      ProfileController.getProfilesInOrderOfAmount(true).stream().forEach(this::addElement);

      for (int i = 0; i < this.getSize(); i++) {
         if (this.getElementAt(i) == p) {
            this.setSelectedItem(p);
            break;
         }
      }
   }
}
