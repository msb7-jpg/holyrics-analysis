package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.Utils;
import java.util.Optional;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public final class CaretListenerAuto implements CaretListener {
   private final Runnable runnable;
   private long timeRun = -1L;

   public CaretListenerAuto(long timeSleep, CaretListenerAuto.CaretListenerAutoListener listener) {
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
   public void caretUpdate(CaretEvent e) {
      Utils.startThreadDaemon(this.runnable);
   }

   public interface CaretListenerAutoListener {
      void trigger();
   }
}
