package com.limagiran.holyrics.util;

import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;

public interface ISwingCRUD<T> {
   JButton getJButtonRemove();

   void save(T var1);

   default void add(T t) {
      if (t != null) {
         this.fillFields(t);
         this.save(t);
         this.changeEditable(t);
      }
   }

   void setCurrentEditable(T var1);

   T getCurrentEditable();

   void fillFields(T var1);

   void clearFields();

   List<JComponent> getAllFieldsEdit();

   default void changeEditable(T t) {
      List<JComponent> fields = this.getAllFieldsEdit();
      if (this.getCurrentEditable() != null) {
         this.save(this.getCurrentEditable());
      }

      this.setCurrentEditable(null);
      this.getJButtonRemove().setEnabled(t != null);
      if (t != null) {
         this.fillFields(t);
      } else {
         this.clearFields();
      }

      fields.forEach(c -> c.setEnabled(false));
      fields.forEach(c -> c.setEnabled(t != null));
      this.setCurrentEditable(t);
      fields.stream().findFirst().ifPresent(c -> c.requestFocus());
   }
}
