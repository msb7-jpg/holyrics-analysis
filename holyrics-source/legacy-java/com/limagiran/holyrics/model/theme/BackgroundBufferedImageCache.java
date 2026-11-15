package com.limagiran.holyrics.model.theme;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class BackgroundBufferedImageCache {
   private static final LinkedHashMap<String, BufferedImage> MAP = new LinkedHashMap<>();

   public static synchronized BufferedImage getImageCache(Background bg, Supplier<BufferedImage> create) {
      String id = bg.getId();
      BufferedImage _return = MAP.get(id);
      if (_return == null) {
         while (!MAP.isEmpty() && size(MAP) > 17000000L) {
            BufferedImage remove = MAP.remove(MAP.keySet().iterator().next());
            if (remove != null) {
               remove = null;
            }
         }

         _return = create.get();
      }

      MAP.remove(id);
      MAP.put(id, _return);
      return _return;
   }

   private static long size(LinkedHashMap<String, BufferedImage> map) {
      return map.values().stream().map(i -> (long)(i.getWidth() * i.getHeight()) * 4L).reduce(0L, Long::sum);
   }
}
