package com.limagiran.bible.util;

import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

public abstract class MySwingWorker<T, V> extends SwingWorker<T, V> {
   private PopUpWorker popup;
   public final Window owner;
   public final String msg;

   public MySwingWorker(Window owner, String msg) {
      this.owner = owner;
      this.msg = msg;
   }

   @Override
   protected void process(List<V> chunks) {
      super.process(chunks);
      if (!chunks.isEmpty()) {
         Optional.ofNullable(this.popup).ifPresent(p -> p.setMessage(chunks.get(0).toString()));
      }
   }

   public void setupPopup(PopUpWorker popup) {
      this.popup = popup;
      this.addPropertyChangeListener(event -> {
         if ("state".equals(event.getPropertyName()) && StateValue.DONE == event.getNewValue()) {
            this.popup.setVisible(false);
            this.popup.dispose();
         }
      });
      this.popup.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            super.componentShown(e);
            MySwingWorker.this.execute();
         }
      });
   }

   protected void showResult() {
      try {
         String s = this.get().toString();
         switch (s.charAt(0)) {
            case 'f':
               JOptionPane.showMessageDialog(this.owner, s.substring(1), Utils.WORD_ERROR, 0);
               break;
            case 't':
               JOptionPane.showMessageDialog(this.owner, s.substring(1), Utils.WORD_MESSAGE, -1);
         }
      } catch (Exception var2) {
      }
   }

   protected String getResult() {
      try {
         return this.get().toString();
      } catch (Exception var2) {
         return "";
      }
   }

   protected boolean isOk() {
      try {
         return this.get().toString().charAt(0) == 't';
      } catch (Exception var2) {
         return false;
      }
   }
}
