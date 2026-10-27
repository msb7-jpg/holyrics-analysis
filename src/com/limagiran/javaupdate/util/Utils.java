package com.limagiran.javaupdate.util;

import com.limagiran.javaupdate.Update;
import java.awt.Desktop;
import java.awt.Window;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import javax.swing.JOptionPane;

public class Utils {
   public static void sleep(long time) {
      try {
         Thread.sleep(time);
      } catch (Exception var3) {
      }
   }

   public static boolean openFile(File file) {
      try {
         if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
            sleep(2000L);
            return true;
         } else {
            File temp = File.createTempFile("delete", ".bat");
            temp.deleteOnExit();
            write(temp, "\"" + file.getAbsolutePath() + "\"", "cp850");
            Runtime.getRuntime().exec("\"" + temp.getAbsolutePath() + "\"");
            sleep(2000L);
            return true;
         }
      } catch (Exception var2) {
         return false;
      }
   }

   public static void write(File file, String valor, String charset) throws IOException, Exception {
      try (
         FileOutputStream fos = new FileOutputStream(file);
         OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
         BufferedWriter bw = new BufferedWriter(osw);
      ) {
         String[] buff = valor.split("\n");
         bw.write(buff[0]);

         for (int indice = 1; indice < buff.length; indice++) {
            bw.newLine();
            bw.write(buff[indice]);
         }

         bw.close();
      }
   }

   public static File getJarFile() {
      try {
         return new File(URLDecoder.decode(Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
      } catch (Exception var1) {
         return new File(Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " "));
      }
   }

   public static boolean copy(File in, File out) {
      if (out.exists()) {
         out.delete();
      }

      mkdirs(out);

      try (
         FileInputStream fis = new FileInputStream(in);
         FileChannel inCh = fis.getChannel();
         FileOutputStream fos = new FileOutputStream(out);
         FileChannel outCh = fos.getChannel();
      ) {
         inCh.transferTo(0L, inCh.size(), outCh);
         return true;
      } catch (Exception var93) {
         out.delete();
         return false;
      }
   }

   public static void mkdirs(File file) {
      try {
         file = file.getAbsoluteFile();
         if (file.getName().contains(".")) {
            file.getParentFile().mkdirs();
         } else {
            file.mkdirs();
         }
      } catch (Exception var2) {
      }
   }

   public static boolean hideFile(File file) {
      try {
         String command = "ATTRIB.EXE +H \"" + file.getAbsolutePath() + "\"";
         Runtime.getRuntime().exec(command);
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isWindows() {
      return System.getProperty("os.name") != null && System.getProperty("os.name").contains("indows");
   }

   public static boolean confirm(Window owner, String msg) {
      return JOptionPane.showConfirmDialog(owner, msg, Update.WORD_CONFIRM, 0) == 0;
   }
}
