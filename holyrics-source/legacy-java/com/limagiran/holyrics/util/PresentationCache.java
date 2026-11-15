package com.limagiran.holyrics.util;

import com.limagiran.holyrics.model.theme.Theme;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;

public class PresentationCache implements Values {
   private static final LinkedHashMap<String, BufferedImage> MAP_SMALL = new LinkedHashMap<>();
   private static final LinkedHashMap<String, BufferedImage> MAP_BIG = new LinkedHashMap<>();

   private static synchronized BufferedImage get(String key, LinkedHashMap<String, BufferedImage> map) {
      BufferedImage _return = map.get(key);
      if (_return != null) {
         map.remove(key);
         map.put(key, _return);
      }

      return _return;
   }

   private static synchronized BufferedImage put(String key, BufferedImage bi, LinkedHashMap<String, BufferedImage> map) {
      while (!map.isEmpty() && size(map) > (map == MAP_BIG ? 26000000L : 12000000L)) {
         BufferedImage _bi = map.remove(map.keySet().iterator().next());
         if (bi != null) {
            _bi.flush();
            Object var4 = null;
         }
      }

      map.put(key, bi);
      return bi;
   }

   private static synchronized long size(LinkedHashMap<String, BufferedImage> map) {
      return map.values().stream().map(i -> (long)(i.getWidth() * i.getHeight()) * 4L).reduce(0L, Long::sum);
   }

   public static BufferedImage get(String key, Dimension size) {
      return get(key, (long)(size.width * size.height * 4) > 230400L ? MAP_BIG : MAP_SMALL);
   }

   public static BufferedImage put(String key, Dimension size, BufferedImage bi) {
      return put(key, bi, (long)(size.width * size.height * 4) > 230400L ? MAP_BIG : MAP_SMALL);
   }

   public static String generateKey(String val, Theme theme, Dimension size, boolean paintBackground) {
      return new StringBuilder(128)
         .append(val)
         .append(";")
         .append(theme.id)
         .append(";")
         .append(theme.hash())
         .append(":")
         .append(size)
         .append(";")
         .append(paintBackground)
         .toString();
   }
}
