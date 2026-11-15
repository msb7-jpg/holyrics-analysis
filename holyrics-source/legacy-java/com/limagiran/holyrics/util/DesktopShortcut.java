package com.limagiran.holyrics.util;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DesktopShortcut {
   private String name = "";
   private File path = new File("");
   private File workingDirectory = new File("");
   private File icon;

   public static DesktopShortcut create(String name, File path) {
      return new DesktopShortcut().name(name).path(path);
   }

   public DesktopShortcut name(String name) {
      this.name = name;
      return this;
   }

   public DesktopShortcut path(File path) {
      this.path = path;
      return this;
   }

   public DesktopShortcut workingDirectory(File workingDirectory) {
      this.workingDirectory = workingDirectory;
      return this;
   }

   public DesktopShortcut icon(File icon) {
      this.icon = icon;
      return this;
   }

   public boolean createDesktop() {
      return create(this.name, this.path, this.workingDirectory, this.icon, "WshShell.SpecialFolders(\"Desktop\")");
   }

   public boolean createStartMenu() {
      return create(this.name, this.path, this.workingDirectory, this.icon, "WshShell.SpecialFolders(\"StartMenu\")");
   }

   private static boolean create(String name, File path, File workingDirectory, File icon, String locale) {
      try {
         File temp = File.createTempFile("makeIcon", ".vbs");
         temp.deleteOnExit();
         StringBuilder sb = new StringBuilder();
         sb.append("set WshShell = WScript.CreateObject(\"WScript.Shell\")").append("\n");
         sb.append(String.format("set oShellLink = WshShell.CreateShortcut(%s & \"\\%s.lnk\")", locale, name)).append("\n");
         sb.append(String.format("oShellLink.TargetPath = \"%s\"", path.getAbsolutePath())).append("\n");
         sb.append("oShellLink.WindowStyle = 1").append("\n");
         sb.append(String.format("oShellLink.Description = \"%s\"", name)).append("\n");
         sb.append(String.format("oShellLink.WorkingDirectory = \"%s\"", workingDirectory.getAbsolutePath())).append("\n");
         if (icon != null) {
            sb.append(String.format("oShellLink.IconLocation = \"%s\"", icon)).append("\n");
         }

         sb.append("oShellLink.Save");
         write(temp, sb.toString(), "Windows-1252");
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

   private static void write(File file, String value, String charset) throws IOException, Exception {
      try (
         FileOutputStream fos = new FileOutputStream(file);
         OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
         BufferedWriter bw = new BufferedWriter(osw);
      ) {
         String[] buff = value.split("\n");
         bw.write(buff[0]);

         for (int index = 1; index < buff.length; index++) {
            bw.newLine();
            bw.write(buff[index]);
         }

         bw.close();
      }
   }
}
