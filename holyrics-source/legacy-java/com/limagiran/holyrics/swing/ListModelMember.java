package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.MemberController;
import com.limagiran.holyrics.model.Member;
import java.util.Calendar;
import javax.swing.DefaultListModel;

public class ListModelMember extends DefaultListModel<Member> {
   public ListModelMember(Calendar date) {
      this.update();
   }

   public final void update() {
      this.removeAllElements();
      MemberController.getAll(false).stream().forEach(this::addElement);
   }
}
