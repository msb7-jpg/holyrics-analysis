package com.limagiran.bible.util;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Utils {
   public static final String WORD_ERROR;
   public static final String WORD_CONFIRM;
   public static final String WORD_MESSAGE;
   private static long x = -1L;
   private static long y = -1L;
   private static long z = 0L;

   public static boolean isDoubleClick(MouseEvent e) {
      if (e.getButton() == 1) {
         long _x = x;
         long _y = y;
         long _z = z;
         return (x = (long)e.getXOnScreen()) == _x & (y = (long)e.getYOnScreen()) == _y & (z = System.currentTimeMillis()) - _z <= 500L;
      } else {
         x = -1L;
         return false;
      }
   }

   public static void sleep(long time) {
      try {
         Thread.sleep(time);
      } catch (Exception var3) {
      }
   }

   public static byte[] compress(String text) throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try (OutputStream out = new DeflaterOutputStream(baos)) {
         out.write(text.getBytes("UTF-8"));
      }

      return baos.toByteArray();
   }

   public static String decompress(byte[] bytes) throws Exception {
      InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];

      int len;
      while ((len = in.read(buffer)) > 0) {
         baos.write(buffer, 0, len);
      }

      return new String(baos.toByteArray(), "UTF-8");
   }

   public static byte[] getBytes(File file) throws Exception {
      try (
         FileInputStream fis = new FileInputStream(file);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ) {
         byte[] buf = new byte[2048];

         int i;
         while ((i = fis.read(buf)) >= 0) {
            baos.write(buf, 0, i);
         }

         return baos.toByteArray();
      } catch (Exception var36) {
         throw var36;
      }
   }

   public static byte[] getBytesJar(String url) throws Exception {
      try (
         InputStream fis = Utils.class.getResourceAsStream(url);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ) {
         byte[] buf = new byte[2048];

         int i;
         while ((i = fis.read(buf)) >= 0) {
            baos.write(buf, 0, i);
         }

         return baos.toByteArray();
      } catch (Exception var36) {
         throw var36;
      }
   }

   public static String removeAccent(String string) {
      return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
   }

   public static String removeExcessSpace(String value) {
      return value == null ? null : value.trim().replaceAll("[ ]{2,}", " ");
   }

   public static boolean confirm(Component c, String text, String title) {
      return JOptionPane.showConfirmDialog(c, text, title, 0) == 0;
   }

   public static boolean confirm(Component c, String text) {
      return confirm(c, text, WORD_CONFIRM);
   }

   public static boolean confirm(String text, String title) {
      return JOptionPane.showConfirmDialog(null, text, title, 0) == 0;
   }

   public static boolean confirm(String text) {
      return JOptionPane.showConfirmDialog(null, text, WORD_CONFIRM, 0) == 0;
   }

   public static void error(Component c, String text, String title) {
      SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(c, text, title, 0));
   }

   public static void error(String text, String title) {
      error(null, text, title);
   }

   public static void error(Component c, String text) {
      error(c, text, WORD_ERROR);
   }

   public static void error(String text) {
      error(null, text, WORD_ERROR);
   }

   public static <T extends Comparable> T range(T value, T min, T max) {
      return (double)value.compareTo(min) < 0.0 ? min : ((double)value.compareTo(max) > 0.0 ? max : value);
   }

   public static void write(File file, byte[] bytes) throws Exception {
      try (FileOutputStream fos = new FileOutputStream(file)) {
         fos.write(bytes);
      }
   }

   static {
      if (Locale.getDefault().toString().equalsIgnoreCase("pt_BR")) {
         WORD_ERROR = "Erro";
         WORD_CONFIRM = "Confirmar";
         WORD_MESSAGE = "Mensagem";
      } else {
         WORD_ERROR = "Error";
         WORD_CONFIRM = "Confirm";
         WORD_MESSAGE = "Message";
      }
   }
}
