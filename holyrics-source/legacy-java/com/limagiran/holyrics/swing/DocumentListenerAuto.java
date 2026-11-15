package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.Utils;
import java.util.Optional;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class DocumentListenerAuto implements DocumentListener {
   private final Runnable runnable;
   private long timeRun = -1L;

   public DocumentListenerAuto(long timeSleep, DocumentListenerAuto.DocumentListenerAutoListener listener) {
      this.runnable = () -> {
         if (this.timeRun < 0L) {
            for (this.timeRun = timeSleep; this.timeRun > 0L; this.timeRun -= 100L) {
               Utils.sleep(100L);
            }

            this.timeRun = -1L;
            Optional.ofNullable(listener).ifPresent(l -> l.trigger());
         } else {
            this.timeRun = timeSleep;
         }
      };
   }

   @Override
   public void insertUpdate(DocumentEvent e) {
      Utils.startThreadDaemon(this.runnable);
   }

   @Override
   public void removeUpdate(DocumentEvent e) {
      Utils.startThreadDaemon(this.runnable);
   }

   @Override
   public void changedUpdate(DocumentEvent e) {
      Utils.startThreadDaemon(this.runnable);
   }

   public interface DocumentListenerAutoListener {
      void trigger();
   }
}
