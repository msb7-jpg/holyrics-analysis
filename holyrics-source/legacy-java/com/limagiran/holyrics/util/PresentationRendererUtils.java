package com.limagiran.holyrics.util;

public class PresentationRendererUtils {
   private static Runnable TO_RENDERER;
   private static Runnable CACHE;

   public static void start() {
      Utils.startThreadDaemon(() -> {
         while (true) {
            try {
               Runnable exec = getRenderer();
               if (exec != null) {
                  Thread t = new Thread(exec);
                  t.start();
                  t.join();
               }
            } catch (Exception var2) {
            }

            Utils.sleep(100L);
         }
      });
   }

   public static synchronized Runnable put(Runnable run) {
      return TO_RENDERER == null ? (TO_RENDERER = run) : (CACHE = run);
   }

   private static synchronized Runnable getRenderer() {
      if (TO_RENDERER != null) {
         Runnable _return = TO_RENDERER;
         TO_RENDERER = CACHE;
         CACHE = null;
         return _return;
      } else {
         TO_RENDERER = CACHE;
         CACHE = null;
         return TO_RENDERER;
      }
   }

   public static synchronized boolean isEmpty() {
      return TO_RENDERER == null && CACHE == null;
   }
}
