package com.limagiran.lgsaveopen;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class Utils {
   private static final String WORD_SAVE;
   private static final String WORD_DIRECTORY;
   private static final String MSG_OVERRIDE;
   private static final String MSG_SAVE_FILE;
   private static final String MSG_ERROR_INVALID_NAME;
   private static final String IV = "AAAAAAAAAAAAAAAA";

   public static File windowsSave(String title, String suggestedName, final String typeFile, String... extensions) {
      final List<String> exList = Arrays.asList(extensions);
      JFileChooser open = new JFileChooser();
      open.setFileSelectionMode(0);
      open.setDialogType(1);
      open.setAcceptAllFileFilterUsed(false);
      open.setDialogTitle(title);
      open.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return exList.stream().anyMatch(ex -> f.getName().toLowerCase().endsWith(ex) || f.isDirectory());
         }

         @Override
         public String getDescription() {
            return typeFile;
         }
      });
      if (!suggestedName.isEmpty()) {
         open.setSelectedFile(new File(open.getCurrentDirectory().getAbsoluteFile() + File.separator + suggestedName));
      }

      while (true) {
         int option = open.showSaveDialog(null);
         if (option == 0) {
            File file = open.getSelectedFile();
            if (exList.stream().noneMatch(ex -> file.getName().endsWith(ex))) {
               file = new File(file + exList.stream().findFirst().orElse(""));
            }

            List<String> list = Arrays.asList("\\", "/", ":", "*", "?", "\"", "<", ">", "|");
            String name = file.getName();
            if (list.stream().anyMatch(c -> name.contains(c))) {
               JOptionPane.showMessageDialog(null, MessageFormat.format(MSG_ERROR_INVALID_NAME, list));
            } else {
               if (!file.exists()) {
                  return file.getAbsoluteFile();
               }

               if (file.exists() && 0 == JOptionPane.showConfirmDialog(null, MSG_OVERRIDE, MSG_SAVE_FILE, 0)) {
                  return file.getAbsoluteFile();
               }
            }
         } else if (option == 1) {
            return null;
         }
      }
   }

   public static File windowsFile(String title, final String typeFile, String... extensions) {
      final List<String> exList = new ArrayList<>(Arrays.asList(extensions));
      JFileChooser open = new JFileChooser();
      open.setFileSelectionMode(0);
      open.setDialogType(0);
      open.setAcceptAllFileFilterUsed(false);
      open.setDialogTitle(title);
      open.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return exList.stream().anyMatch(ex -> f.getName().toLowerCase().endsWith(ex) || f.isDirectory());
         }

         @Override
         public String getDescription() {
            return typeFile;
         }
      });

      while (open.showOpenDialog(null) == 0) {
         if (open.getSelectedFile().isFile()) {
            open.setVisible(false);
            return open.getSelectedFile().getAbsoluteFile();
         }

         open.setCurrentDirectory(open.getSelectedFile());
      }

      return null;
   }

   public static boolean write(Object object, File file) {
      return write(object, file, null);
   }

   public static synchronized boolean write(Object object, File file, String key) {
      File temp = new File("temp.rwo");

      boolean var5;
      try (
         FileOutputStream fos = new FileOutputStream(temp);
         ObjectOutputStream oos = new ObjectOutputStream((OutputStream)(key == null ? fos : encrypt(fos, key)));
      ) {
         oos.writeObject(object);
         return true;
      } catch (Exception var49) {
         var5 = false;
      } finally {
         file.delete();
         temp.renameTo(file);
         temp.delete();
      }

      return var5;
   }

   public static <T> T read(File file, Class<T> classe) {
      return read(file, classe, null);
   }

   public static <T> T read(File file, Class<T> classe, String key) {
      try (
         FileInputStream fis = new FileInputStream(file);
         ObjectInputStream ois = new ObjectInputStream((InputStream)(key == null ? fis : decrypt(fis, key)));
      ) {
         Object _return = ois.readObject();
         return (T)(classe.isInstance(_return) ? _return : null);
      } catch (Exception var37) {
         return null;
      }
   }

   private static CipherOutputStream encrypt(OutputStream os, String encKey) throws Exception {
      encKey = getPass16(encKey);
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
      cip.init(1, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return new CipherOutputStream(os, cip);
   }

   private static CipherInputStream decrypt(InputStream is, String encKey) throws Exception {
      encKey = getPass16(encKey);
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
      cip.init(2, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return new CipherInputStream(is, cip);
   }

   public static boolean confirm(Component c, String text, String title) {
      return JOptionPane.showConfirmDialog(c, text, title, 0) == 0;
   }

   public static boolean confirm(String text, String title) {
      return JOptionPane.showConfirmDialog(null, text, title, 0) == 0;
   }

   public static boolean confirm(String text) {
      return JOptionPane.showConfirmDialog(null, text, "", 0) == 0;
   }

   public static void error(Component c, String text, String title) {
      JOptionPane.showMessageDialog(c, text, title, 0);
   }

   public static void error(String text, String title) {
      error(null, text, title);
   }

   public static void error(Component c, String text) {
      error(c, text, "");
   }

   public static void error(String text) {
      error(null, text, "");
   }

   public static byte[] encrypt(String plainText, String encKey) throws Exception {
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
      cip.init(1, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return cip.doFinal(plainText.getBytes("UTF-8"));
   }

   public static String decrypt(byte[] cipherText, String encKey) throws Exception {
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
      cip.init(2, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return new String(cip.doFinal(cipherText), "UTF-8");
   }

   public static String getSHA256(String value) throws Exception {
      MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
      byte[] messageDigest = algorithm.digest(value.getBytes("UTF-8"));
      StringBuilder hexString = new StringBuilder();

      for (byte b : messageDigest) {
         hexString.append(String.format("%02X", 255 & b));
      }

      return hexString.toString();
   }

   public static String getPass16(String key) {
      key = key != null && !key.isEmpty() ? key : "A";

      try {
         return getSHA256(key).substring(0, 16);
      } catch (Exception var2) {
         while (key.length() < 16) {
            key = key + key;
         }

         return key.substring(0, 16);
      }
   }

   static {
      if (Locale.getDefault().toString().equalsIgnoreCase("pt_BR")) {
         WORD_SAVE = "Salvar";
         WORD_DIRECTORY = "Diretório";
         MSG_OVERRIDE = "Arquivo já existe.\nDeseja substituí-lo?";
         MSG_SAVE_FILE = "Salvar Arquivo";
         MSG_ERROR_INVALID_NAME = "O nome do arquivo não pode conter nenhum dos seguintes caracteres:\n {0}";
      } else {
         WORD_SAVE = "Save";
         WORD_DIRECTORY = "Directory";
         MSG_OVERRIDE = "File already exists.\nDo you want to replace it?";
         MSG_SAVE_FILE = "Save File";
         MSG_ERROR_INVALID_NAME = "The file name can not contain any of the following characters:\n {0}";
      }
   }
}
