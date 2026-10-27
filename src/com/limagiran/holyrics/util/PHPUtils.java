package com.limagiran.holyrics.util;

import com.limagiran.holyrics.Update;
import com.limagiran.holyrics.settings.Settings;
import java.util.HashMap;
import java.util.Map;

public class PHPUtils {
   private static long startTime;

   public static void open() {
      startTime = System.currentTimeMillis();
      Map<String, String> map = new HashMap<>();
      map.put("count", "ok");
      map.put("agent", Utils.getMacAddress() + "-" + Update.INSTANCE.app.getVersion() + (Settings.EnumSettings.FIRST_OPEN.is() ? "-firstOpen" : ""));
      new Thread(() -> Download.sendPost("http://www.holyrics.com.br/count.php", map)).start();
   }

   public static void close(long sleep) {
      Map<String, String> map = new HashMap<>();
      map.put("count", "ok");
      map.put("time", String.valueOf((System.currentTimeMillis() - startTime) / 60000L));
      map.put("agent", Utils.getMacAddress() + "-" + Update.INSTANCE.app.getVersion());
      new Thread(() -> Download.sendPost("http://www.holyrics.com.br/countExit.php", map)).start();
      Utils.sleep(sleep);
   }
}
