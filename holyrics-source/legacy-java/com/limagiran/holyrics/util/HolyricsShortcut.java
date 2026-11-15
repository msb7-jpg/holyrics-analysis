package com.limagiran.holyrics.util;

import java.awt.Desktop;
import java.io.File;

public class HolyricsShortcut {
   private static final String ICON = "/com/limagiran/holyrics/img/holyrics.ico";

   private HolyricsShortcut() {
   }

   public static boolean createDesktop() {
      return create("Holyrics", Dir.getJarFile(), "/com/limagiran/holyrics/img/holyrics.ico", "WshShell.SpecialFolders(\"Desktop\")");
   }

   public static boolean createStartMenu() {
      return create("Holyrics", Dir.getJarFile(), "/com/limagiran/holyrics/img/holyrics.ico", "WshShell.SpecialFolders(\"StartMenu\")");
   }

   private static boolean create(String name, File path, String icon, String locale) {
      try {
         File temp = File.createTempFile("makeIcon", ".vbs");
         File tempIco = File.createTempFile("icon", ".ico");
         Dir.exportFromJar(icon, tempIco);
         temp.deleteOnExit();
         tempIco.deleteOnExit();
         StringBuilder sb = new StringBuilder();
         sb.append("set WshShell = WScript.CreateObject(\"WScript.Shell\")").append("\n");
         sb.append(String.format("set oShellLink = WshShell.CreateShortcut(%s & \"\\%s.lnk\")", locale, name)).append("\n");
         sb.append(String.format("oShellLink.TargetPath = \"%s\"", path.getAbsolutePath())).append("\n");
         sb.append("oShellLink.WindowStyle = 1").append("\n");
         sb.append(String.format("oShellLink.Description = \"%s\"", name)).append("\n");
         sb.append(String.format("oShellLink.WorkingDirectory = \"%s\"", path.getAbsolutePath())).append("\n");
         if (icon != null) {
            sb.append(String.format("oShellLink.IconLocation = \"%s\"", tempIco.getAbsolutePath())).append("\n");
         }

         sb.append("oShellLink.Save");
         RW.write(temp, sb.toString(), "Windows-1252");
         if (!Desktop.isDesktopSupported()) {
            return false;
         } else {
            Desktop.getDesktop().open(temp);
            return true;
         }
      } catch (Exception var7) {
         return false;
      }
   }
}
