package com.limagiran.bible.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Log {
   private static final String URL = "http://www.holyrics.com/bible/log.php";

   public static void send(Exception e) {
      new Thread(() -> {
         try {
            StringBuilder txt = new StringBuilder();
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + " - " + e;
            txt.append("\n").append(date).append("\n\n");

            for (int i = e.getStackTrace().length - 1; i >= 0; i--) {
               txt.append(e.getStackTrace()[i]).append("\n");
            }

            Map<String, Object> map = new HashMap<>();
            map.put("log", txt.toString());
            Download.sendPost("http://www.holyrics.com/bible/log.php", map);
         } catch (Exception var4) {
         }
      }).start();
   }
}
