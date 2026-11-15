package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.ProfileController;
import com.limagiran.holyrics.model.Profile;
import com.limagiran.holyrics.util.Utils;

public class ListModelProfile extends ListModelAddRemove<Profile> implements ListModelAddRemove.ListModelAddRemoveListener {
   public ListModelProfile() {
      super(ProfileController.getProfiles());
      super.setListModelAddRemoveListener(this);
      this.sort();
   }

   @Override
   public void changed() {
      ProfileController.save(this.list);
      this.sort();
   }

   public final void sort() {
      super.sort((o1, o2) -> {
         if (o1 == null) {
            return o2 == null ? 0 : -1;
         } else {
            return o2 == null ? 1 : Utils.removeAccent(o1.getName()).compareToIgnoreCase(Utils.removeAccent(o2.getName()));
         }
      });
   }
}
