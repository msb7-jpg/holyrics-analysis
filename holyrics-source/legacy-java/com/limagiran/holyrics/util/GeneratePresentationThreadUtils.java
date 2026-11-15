package com.limagiran.holyrics.util;

import com.limagiran.holyrics.settings.Settings;
import javax.swing.SwingUtilities;

public class GeneratePresentationThreadUtils {
   private static Runnable TO_EXEC;
   private static Runnable CACHE;
   private static long lastPut = 0L;

   public static void start() {
      Utils.startThreadDaemon(() -> {
         while (true) {
            try {
               Runnable exec = next();
               if (exec != null) {
                  while (VideoUtils.staticIsAnyVisible()) {
                     SwingUtilities.invokeLater(() -> VideoUtils.staticExitAllExcept(String.class));
                     Utils.sleep(50L);
                  }

                  SwingUtils.runOnUIThread(exec);
                  Utils.sleep((long)Settings.EnumSettings2.FADE_IN_OUT_DURATION.getInt());
               }
            } catch (Exception var1) {
            }

            Utils.sleep(100L);
         }
      });
   }

   public static synchronized void put(Runnable run) {
      if (System.currentTimeMillis() - lastPut >= 500L) {
         lastPut = System.currentTimeMillis();
         if (TO_EXEC == null) {
            TO_EXEC = run;
         } else {
            CACHE = run;
         }
      }
   }

   private static synchronized Runnable next() {
      if (TO_EXEC != null) {
         Runnable _return = TO_EXEC;
         TO_EXEC = CACHE;
         CACHE = null;
         return _return;
      } else {
         TO_EXEC = CACHE;
         CACHE = null;
         return TO_EXEC;
      }
   }
}
