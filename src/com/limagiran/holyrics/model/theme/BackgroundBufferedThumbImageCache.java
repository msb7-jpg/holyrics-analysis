package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.util.Utils;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class BackgroundBufferedThumbImageCache {
   private static final LinkedHashMap<String, BufferedImage> MAP = new LinkedHashMap<>();

   public static synchronized BufferedImage getImageCache(Background bg, Dimension d, Supplier<BufferedImage> create) {
      String id = bg.getId() + d.toString();
      BufferedImage _return = MAP.get(id);
      if (_return == null) {
         while (!MAP.isEmpty() && size(MAP) > 8000000L) {
            BufferedImage remove = MAP.remove(MAP.keySet().iterator().next());
            if (remove != null) {
               Utils.startThreadDaemon(remove::flush);
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
