package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.Utils;
import java.util.Optional;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ChangeEventDelay implements ChangeListener {
   private final Runnable runnable;
   private long timeRun = -1L;

   public ChangeEventDelay(long timeSleep, ChangeEventDelay.ChangeEventDelayListener listener) {
      this.runnable = () -> {
         if (this.timeRun < 0L) {
            for (this.timeRun = timeSleep; this.timeRun > 0L; this.timeRun -= 50L) {
               Utils.sleep(50L);
            }

            this.timeRun = -1L;
            Optional.ofNullable(listener).ifPresent(l -> l.changeEventDelayTrigger());
         } else {
            this.timeRun = timeSleep;
         }
      };
   }

   @Override
   public void stateChanged(ChangeEvent e) {
      Utils.startThreadDaemon(this.runnable);
   }

   public interface ChangeEventDelayListener {
      void changeEventDelayTrigger();
   }
}
