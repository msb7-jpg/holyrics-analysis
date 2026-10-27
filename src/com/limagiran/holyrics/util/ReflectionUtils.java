package com.limagiran.holyrics.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
   public static boolean setField(Object obj, String name, Object newValue) {
      try {
         Field f = obj.getClass().getDeclaredField(name);
         f.setAccessible(true);
         f.set(obj, newValue);
         return true;
      } catch (Exception var4) {
         return false;
      }
   }
}
