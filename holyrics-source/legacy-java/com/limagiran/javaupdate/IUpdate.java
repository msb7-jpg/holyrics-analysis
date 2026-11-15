package com.limagiran.javaupdate;

import com.limagiran.javaupdate.util.Utils;
import java.io.File;

public interface IUpdate {
   String getAppName();

   String getAppDesc();

   String getVersion();

   String getUrlVersion();

   String getUrlLinkApp();

   String getUrlHash();

   default File getAbsoluteFileApp(String version) {
      return new File(Utils.getJarFile().getParent(), String.format("%s %s.exe", this.getAppName(), version));
   }

   void runBeforeRestart();

   void disableCheckUpdate();
}
