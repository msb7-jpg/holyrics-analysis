package com.limagiran.bible.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Dir {
   public static boolean exportFromJar(String resource, File out) {
      if (out.exists()) {
         out.delete();
      }

      mkdirs(out);

      try (
         InputStream is = Dir.class.getResourceAsStream(resource);
         FileOutputStream fos = new FileOutputStream(out);
      ) {
         byte[] buf = new byte[4096];

         int len;
         while ((len = is.read(buf)) >= 0) {
            fos.write(buf, 0, len);
         }

         return true;
      } catch (Exception var35) {
         Log.send(var35);
         out.delete();
         return false;
      }
   }

   public static void mkdirs(File file) {
      try {
         file = file.getAbsoluteFile();
         if (file.getName().contains(".")) {
            file.getParentFile().mkdirs();
         } else {
            file.mkdirs();
         }
      } catch (Exception var2) {
         Log.send(var2);
      }
   }
}
