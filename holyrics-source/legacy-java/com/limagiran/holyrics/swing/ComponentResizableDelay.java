package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.Utils;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ComponentResizableDelay extends ComponentAdapter {
   private final Runnable runnable;
   private long timeRun = -1L;

   public ComponentResizableDelay(long timeSleep, ComponentResizableDelay.ComponentResizableDelayListener listener) {
      this.runnable = () -> {
         if (this.timeRun < 0L) {
            for (this.timeRun = timeSleep; this.timeRun > 0L; this.timeRun -= 100L) {
               Utils.sleep(100L);
            }

            this.timeRun = -1L;
            listener.trigger();
         } else {
            this.timeRun = timeSleep;
         }
      };
   }

   @Override
   public void componentResized(ComponentEvent e) {
      Utils.startThreadDaemon(this.runnable);
   }

   public interface ComponentResizableDelayListener {
      void trigger();
   }
}
