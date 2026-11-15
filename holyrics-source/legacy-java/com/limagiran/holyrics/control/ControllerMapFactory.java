package com.limagiran.holyrics.control;

import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Values;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public interface ControllerMapFactory {
   String PASSWORD = "&p4ssS3sp";

   static File getFile(String name) {
      File file = new File(Values.PATH_FILES + name).getAbsoluteFile();
      if (file.getParentFile() != null) {
         file.getParentFile().mkdirs();
      }

      return file;
   }

   static <T, U> Map<T, U> load(String fileName, Class<T> classKey, Class<U> classValue) {
      try {
         Map<T, U> _return = RWObj.read(getFile(fileName), Map.class, "&p4ssS3sp");
         if (_return != null && _return.keySet().stream().anyMatch(classKey::isInstance) && _return.values().stream().anyMatch(classValue::isInstance)) {
            return _return;
         }
      } catch (Exception var4) {
      }

      return new HashMap<>();
   }

   static boolean save(Map map, String fileName) {
      return RWObj.write(map, getFile(fileName), "&p4ssS3sp");
   }

   static long size(LinkedHashMap<String, BufferedImage> map) {
      return map.values().stream().map(i -> {
         try {
            return (long)(i.getWidth() * i.getHeight()) * 4L;
         } catch (Exception var2) {
            return 1024L;
         }
      }).reduce(0L, Long::sum);
   }

   static long sizeFromThumb(LinkedHashMap<String, ThumbMediaController.Thumb> map) {
      return map.values().stream().filter(Objects::nonNull).map(i -> i.thumb != null ? (long)i.thumb.length : 8L).reduce(0L, Long::sum);
   }
}
