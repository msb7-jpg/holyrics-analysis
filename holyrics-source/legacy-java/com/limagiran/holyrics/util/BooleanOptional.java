package com.limagiran.holyrics.util;

public class BooleanOptional {
   public static void runIfTrue(boolean value, BooleanOptional.BooleanConsumer consumer) {
      if (value) {
         consumer.run();
      }
   }

   @FunctionalInterface
   public interface BooleanConsumer {
      void run();
   }
}
