package com.limagiran.holyrics.settings;

import com.limagiran.holyrics.control.ControllerFactory;
import com.limagiran.holyrics.util.RWObj;

public class SettingsFactory {
   private static final String FILE_NAME = "set.hly";

   public static Settings getInstance() {
      Settings _return = RWObj.read(ControllerFactory.getFile("set.hly"), Settings.class, "&p4ssS3sp");
      return _return != null ? _return : new Settings();
   }

   public static void save(Settings s) {
      RWObj.write(s, ControllerFactory.getFile("set.hly"), "&p4ssS3sp");
   }
}
