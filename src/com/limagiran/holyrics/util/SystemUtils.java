package com.limagiran.holyrics.util;

import com.limagiran.lgsaveopen.I18N;
import com.sun.management.OperatingSystemMXBean;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.SystemColor;
import java.awt.Window;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Locale;

public class SystemUtils {
   private static final String WORD_RESTARTING;

   public static void restartApp(int mb) throws Exception {
      String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
      File currentJar = Dir.getJarFile();
      if (currentJar.getName().endsWith(".jar")) {
         ArrayList<String> cmd = new ArrayList<>();
         cmd.add(javaBin);
         cmd.add("-jar");
         if (mb > 128) {
            cmd.add(String.format("-Xmx%sM", mb));
            cmd.add(String.format("-Xms%sM", mb));
         }

         cmd.add(currentJar.getPath());
         ProcessBuilder builder = new ProcessBuilder(cmd);
         builder.directory(currentJar.getAbsoluteFile().getParentFile());
         builder.start();
         System.exit(3000);
      }
   }

   public static boolean openFile(File file) {
      try {
         if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
            Utils.sleep(3000L);
            return true;
         } else {
            File temp = File.createTempFile("delete", ".bat");
            temp.deleteOnExit();
            RW.write(temp, "\"" + file.getAbsolutePath() + "\"", "cp850");
            Runtime.getRuntime().exec("\"" + temp.getAbsolutePath() + "\"");
            Utils.sleep(3000L);
            return true;
         }
      } catch (Exception var2) {
         return false;
      }
   }

   public static void validateMemory1GB() {
      long memorySize = ((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
      if (memorySize < 800000000L) {
         Utils.error("É necessário 1GB de memória RAM para executar este programa.");
         System.exit(0);
      }

      if (Runtime.getRuntime().maxMemory() < 800000000L) {
         try {
            restartApp(900);
         } catch (Exception var3) {
            Log.send(var3);
            Utils.error("ERRO!\n" + var3.getMessage());
         }

         System.exit(0);
      }
   }

   public static void createDesktopIcon(Window owner) {
      PopUpWorker.exe(new MySwingWorker(owner) {
         @Override
         protected Object doInBackground() throws Exception {
            HolyricsShortcut.createDesktop();
            return null;
         }

         @Override
         protected void done() {
            super.done();
            Utils.msg(this.owner, I18N.WORD_DONE);
         }
      });
   }

   public static Color getWallpaperColor() {
      try {
         return new Color(SystemColor.desktop.getRGB());
      } catch (Exception var1) {
         return Color.BLACK;
      }
   }

   static {
      if (Locale.getDefault().toString().equalsIgnoreCase("pt_BR")) {
         WORD_RESTARTING = "Reiniciando...";
      } else {
         WORD_RESTARTING = "Restarting...";
      }
   }
}
