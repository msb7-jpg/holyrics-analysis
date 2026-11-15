package com.limagiran.holyrics.control;

import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Values;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ControllerFactory<T> {
   String PASSWORD = "&p4ssS3sp";

   static File getFile(String name) {
      File file = new File(Values.PATH_FILES + name).getAbsoluteFile();
      if (file.getParentFile() != null) {
         file.getParentFile().mkdirs();
      }

      return file;
   }

   static <T> List<T> load(String fileName, Class<T> clazz, Runnable preLoad) {
      try {
         Optional.ofNullable(preLoad).ifPresent(r -> r.run());
         List _return = RWObj.read(getFile(fileName), List.class, "&p4ssS3sp");
         if (_return != null && _return.parallelStream().anyMatch(clazz::isInstance)) {
            return _return;
         }
      } catch (Exception var4) {
      }

      return new ArrayList<>();
   }

   static <T> List<T> load(String fileName, Class<T> clazz) {
      return load(fileName, clazz, null);
   }

   static boolean save(List list, String fileName) {
      return RWObj.write(list, getFile(fileName), "&p4ssS3sp");
   }

   static long fileSize(String fileName) {
      return getFile(fileName).length();
   }
}
