package com.limagiran.holyrics.util;

import com.limagiran.holyrics.model.AlertSettings;

public interface AlertSettingsUtils {
   AlertSettings getInstance();

   default int getAlertHeight(int h) {
      try {
         return (int)((double)(h * this.getInstance().getLineHeight()) / 100.0);
      } catch (Exception var3) {
         return (int)((double)h * 0.08);
      }
   }
}
