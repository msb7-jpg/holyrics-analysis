package com.limagiran.bible.util;

import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;

public class Values {
   private static final Map MAP = loadMap();
   private static final File FILE_MAP = new File(Values.PATH_DATABASE + "bible.dat");
   private static final String PASS_MAP = "abcDEFghiJKL$#@7";
   public static final String APP_NAME = "Bible LG";
   public static final String PATH_DATABASE = "Holyrics" + File.separator + "files" + File.separator + "Bible LG" + File.separator;
   public static final List<Image> ICONS_SEARCH = loadIcons("search", 16, 20, 24, 32);
   public static final List<Image> ICONS_REPORT_ERROR = loadIcons("edit", 16, 20, 24, 32);

   private static List<Image> loadIcons(String name, int... sizes) {
      String format = "/com/limagiran/bible/img/%s%s.png";
      return Arrays.stream(sizes)
         .mapToObj(size -> new ImageIcon(Values.class.getResource(String.format("/com/limagiran/bible/img/%s%s.png", name, size))).getImage())
         .collect(Collectors.toList());
   }

   private static Map loadMap() {
      try {
         Map _return = RWObj.read(FILE_MAP, Map.class, "abcDEFghiJKL$#@7");
         return (Map)(_return != null ? _return : new HashMap());
      } catch (Exception var1) {
         return new HashMap();
      }
   }

   public static Object put(Object key, Object value) {
      MAP.put(key, value);
      RWObj.write(MAP, FILE_MAP, "abcDEFghiJKL$#@7");
      return value;
   }

   public static String getOrDefault(Object key, Object _default) {
      return String.valueOf(MAP.getOrDefault(key, _default));
   }

   static {
      Dir.mkdirs(new File(PATH_DATABASE));
   }
}
