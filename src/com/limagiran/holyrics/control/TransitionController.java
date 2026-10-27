package com.limagiran.holyrics.control;

import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.transition.TransitionSettings;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Values;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TransitionController implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final Map<String, TransitionSettings> TRANSITIONS = TransitionController.TransitionControllerFactory.getInstance();

   public static void set(TransitionSettings set, ScreenDeviceSettings.EnumDeviceType deviceType) {
      if (deviceType.equals(ScreenDeviceSettings.EnumDeviceType.TEXT)) {
         deviceType = ScreenDeviceSettings.EnumDeviceType.MUSIC;
      }

      TRANSITIONS.put(deviceType.name(), set);
      save();
   }

   public static TransitionSettings get(ScreenDeviceSettings.EnumDeviceType deviceType) {
      if (deviceType.equals(ScreenDeviceSettings.EnumDeviceType.TEXT)) {
         deviceType = ScreenDeviceSettings.EnumDeviceType.MUSIC;
      }

      TransitionSettings _return = TRANSITIONS.get(deviceType.name());
      if (_return == null) {
         _return = new TransitionSettings();
         TRANSITIONS.put(deviceType.name(), _return);
      }

      return _return;
   }

   public static void save() {
      TransitionController.TransitionControllerFactory.save(TRANSITIONS);
   }

   private static class TransitionControllerFactory {
      private static final File FILE = new File(Values.PATH_FILES + "transitionSet.hly");
      private static final String PASSWORD = "&p4ssS3sp";

      public static Map<String, TransitionSettings> getInstance() {
         try {
            Map<String, TransitionSettings> _return = RWObj.read(FILE, Map.class, "&p4ssS3sp");
            if (_return != null
               && _return.keySet().stream().anyMatch(String.class::isInstance)
               && _return.values().stream().anyMatch(TransitionSettings.class::isInstance)) {
               return _return;
            }
         } catch (Exception var1) {
         }

         return new HashMap<>();
      }

      public static void save(Map<String, TransitionSettings> map) {
         RWObj.write(map, FILE, "&p4ssS3sp");
      }

      static {
         if (FILE.getParentFile() != null) {
            FILE.getParentFile().mkdirs();
         }
      }
   }
}
