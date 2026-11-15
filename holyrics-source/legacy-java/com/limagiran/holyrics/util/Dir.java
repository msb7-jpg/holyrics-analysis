package com.limagiran.holyrics.util;

import com.alee.laf.filechooser.WebFileChooser;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class Dir {
   private static WebFileChooser FILE_CHOOSER;
   public static final String WORD_DIRECTORY;
   public static final String MSG_OVERRIDE;
   public static final String MSG_SAVE_FILE;
   public static final String MSG_ERROR_INVALID_NAME;
   public static final String MSG_FILE_NOT_FOUND_IN_DIRECTORY;
   public static final String MSG_FILE_NOT_FOUND;
   public static final String MSG_REMOTE_NAME;
   private static final String IV = "AAAAAAAAAAAAAAAA";

   public static void loadFileChooser() {
      SwingUtilities.invokeLater(() -> {
         File file = new File(Settings.EnumSettings2.LAST_DIR.getString()).getAbsoluteFile();
         FILE_CHOOSER = new WebFileChooser() {
            public void setCurrentDirectory(File dir) {
               if (dir == null) {
                  dir = new File(Settings.EnumSettings2.LAST_DIR.getString());
               }

               if (dir.getAbsoluteFile().exists()) {
                  super.setCurrentDirectory(dir.isDirectory() ? dir.getAbsoluteFile() : dir.getParentFile());
               } else {
                  super.setCurrentDirectory(new File("").getAbsoluteFile());
               }

               File file = super.getCurrentDirectory();
               if (file != null) {
                  Settings.EnumSettings2.LAST_DIR.set(file.getAbsolutePath());
               }
            }

            public void addActionListener(ActionListener l) {
               for (ActionListener listener : super.getActionListeners()) {
                  super.removeActionListener(listener);
               }

               super.addActionListener(l);
            }
         };
         BooleanOptional.runIfTrue(file.exists(), () -> FILE_CHOOSER.setCurrentDirectory(file));
         FILE_CHOOSER.setPreferredSize(new Dimension(800, 500));
         FILE_CHOOSER.getFileChooserPanel().getFileList().setThumbnailProvider(WebLafUtils.createFileThumbnailProvider());
      });
   }

   public static void setScrollPane(Component[] cs) {
      if (cs != null) {
         for (Component c : cs) {
            if (c instanceof JScrollPane) {
               ((JScrollPane)c).getVerticalScrollBar().setUnitIncrement(25);
               ((JScrollPane)c).getHorizontalScrollBar().setUnitIncrement(25);
            } else if (c instanceof Container) {
               setScrollPane(((Container)c).getComponents());
            }
         }
      }
   }

   public static boolean deleteDir(File dir) {
      if (dir.isDirectory()) {
         String[] files = dir.list();

         for (String file : files) {
            boolean success = deleteDir(new File(dir, file));
            if (!success) {
               return false;
            }
         }
      }

      return dir.delete();
   }

   public static File canDelete(File file) {
      if (file.isDirectory()) {
         String[] files = file.list();

         for (String fileName : files) {
            File result = canDelete(new File(file, fileName));
            if (result != null) {
               return result;
            }
         }
      }

      return !file.renameTo(file) ? file : null;
   }

   public static File windowsSave(String title, String suggestedName, File initDir, final String typeFile, String... extensions) {
      final List<String> exList = Arrays.asList(extensions);
      FILE_CHOOSER.setFileSelectionMode(0);
      FILE_CHOOSER.setDialogType(1);
      FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
      FILE_CHOOSER.setDialogTitle(title);
      FILE_CHOOSER.setCurrentDirectory(initDir);
      FILE_CHOOSER.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return f.isDirectory() || exList.isEmpty() || exList.stream().anyMatch(ex -> f.getName().toLowerCase().endsWith(ex));
         }

         @Override
         public String getDescription() {
            return typeFile;
         }
      });
      if (!suggestedName.isEmpty()) {
         FILE_CHOOSER.setSelectedFile(new File(FILE_CHOOSER.getCurrentDirectory().getAbsoluteFile() + File.separator + suggestedName));
      }

      while (true) {
         int option = FILE_CHOOSER.showSaveDialog((Component)null);
         if (option == 0) {
            File file = FILE_CHOOSER.getSelectedFile();
            if (exList.stream().noneMatch(ex -> file.getName().endsWith(ex))) {
               file = new File(file + exList.stream().findFirst().orElse(""));
            }

            List<String> list = Arrays.asList("\\", "/", ":", "*", "?", "\"", "<", ">", "|");
            String name = file.getName();
            if (list.stream().anyMatch(c -> name.contains(c))) {
               Utils.error(MessageFormat.format(MSG_ERROR_INVALID_NAME, list));
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

   public static File windowsSave(String title, String suggestedName, String typeFile, String... extensions) {
      return windowsSave(title, suggestedName, null, typeFile, extensions);
   }

   public static File windowsSave(String title, File initDir, String typeFile, String... extensions) {
      return windowsSave(title, "", initDir, typeFile, extensions);
   }

   public static File windowsSave(String title, String typeFile, String extensions) {
      return windowsSave(title, "", typeFile, extensions);
   }

   public static File windowsFile(String title, File initDir, final String typeFile, String... extensions) {
      final List<String> exList = Arrays.asList(extensions);
      FILE_CHOOSER.setFileSelectionMode(0);
      FILE_CHOOSER.setDialogType(0);
      FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
      FILE_CHOOSER.setDialogTitle(title);
      FILE_CHOOSER.setCurrentDirectory(initDir);
      FILE_CHOOSER.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return f.isDirectory() || exList.isEmpty() || exList.stream().anyMatch(ex -> f.getName().toLowerCase().endsWith(ex));
         }

         @Override
         public String getDescription() {
            return typeFile;
         }
      });

      while (FILE_CHOOSER.showOpenDialog((Component)null) == 0) {
         if (FILE_CHOOSER.getSelectedFile().isFile()) {
            return FILE_CHOOSER.getSelectedFile().getAbsoluteFile();
         }

         FILE_CHOOSER.setCurrentDirectory(FILE_CHOOSER.getSelectedFile());
      }

      return null;
   }

   public static File windowsFile(String title, String typeFile, String... extensions) {
      return windowsFile(title, null, typeFile, extensions);
   }

   public static File windowsImage(String title, File initDir) {
      final List<String> exList = Arrays.asList(".jpg", ".png", ".gif", ".bmp");
      FILE_CHOOSER.setFileSelectionMode(0);
      FILE_CHOOSER.setDialogType(0);
      FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
      FILE_CHOOSER.setDialogTitle(title);
      FILE_CHOOSER.setCurrentDirectory(initDir);
      FILE_CHOOSER.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return f.isDirectory() || exList.isEmpty() || exList.stream().anyMatch(ex -> f.getName().toLowerCase().endsWith(ex));
         }

         @Override
         public String getDescription() {
            return "Image File (*.jpg, *.png, *.gif, *.bmp)";
         }
      });

      while (FILE_CHOOSER.showOpenDialog((Component)null) == 0) {
         if (FILE_CHOOSER.getSelectedFile().isFile()) {
            return FILE_CHOOSER.getSelectedFile().getAbsoluteFile();
         }

         FILE_CHOOSER.setCurrentDirectory(FILE_CHOOSER.getSelectedFile());
      }

      return null;
   }

   public static File windowsImage(String title) {
      return windowsImage(title, null);
   }

   public static List<File> windowsFileMultiple(String title, File initDir, final String typeFile, String... extensions) {
      final List<String> exList = Arrays.asList(extensions);
      FILE_CHOOSER.setFileSelectionMode(0);
      FILE_CHOOSER.setDialogType(0);
      FILE_CHOOSER.setMultiSelectionEnabled(true);
      FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
      FILE_CHOOSER.setDialogTitle(title);
      FILE_CHOOSER.setCurrentDirectory(initDir);
      FILE_CHOOSER.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return f.isDirectory() || exList.isEmpty() || exList.stream().anyMatch(ex -> f.getName().toLowerCase().endsWith(ex));
         }

         @Override
         public String getDescription() {
            return typeFile;
         }
      });
      return (List<File>)(FILE_CHOOSER.showOpenDialog((Component)null) == 0
         ? Arrays.asList(FILE_CHOOSER.getSelectedFiles()).stream().filter(File::isFile).collect(Collectors.toList())
         : new ArrayList<>());
   }

   public static List<File> windowsFileMultiple(String title, String typeFile, String... extensions) {
      return windowsFileMultiple(title, null, typeFile, extensions);
   }

   public static File windowsDir(String title, File initDir) {
      FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
      FILE_CHOOSER.setFileSelectionMode(1);
      FILE_CHOOSER.setDialogType(0);
      FILE_CHOOSER.setDialogTitle(title);
      FILE_CHOOSER.setFileHidingEnabled(false);
      FILE_CHOOSER.setCurrentDirectory(initDir);
      FILE_CHOOSER.setApproveButtonText(I18N.WORD_SAVE.val());
      FILE_CHOOSER.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return f.isDirectory();
         }

         @Override
         public String getDescription() {
            return Dir.WORD_DIRECTORY;
         }
      });
      return FILE_CHOOSER.showOpenDialog((Component)null) == 0 ? FILE_CHOOSER.getSelectedFile() : null;
   }

   public static File windowsUniqueFile(String title, File initDir, final String acceptFile) {
      FILE_CHOOSER.setAcceptAllFileFilterUsed(false);
      FILE_CHOOSER.setFileSelectionMode(2);
      FILE_CHOOSER.setDialogType(0);
      FILE_CHOOSER.setDialogTitle(title);
      FILE_CHOOSER.setFileHidingEnabled(false);
      FILE_CHOOSER.setCurrentDirectory(initDir);
      FILE_CHOOSER.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            return f.getName().toLowerCase().equals(acceptFile) || f.isDirectory();
         }

         @Override
         public String getDescription() {
            return acceptFile;
         }
      });

      while (true) {
         int op = FILE_CHOOSER.showOpenDialog((Component)null);
         if (op != 0) {
            return null;
         }

         File file = FILE_CHOOSER.getSelectedFile();
         if (!file.isDirectory()) {
            return file;
         }

         if ((file = containsFile(file, acceptFile)) != null) {
            return file;
         }

         Utils.error(MSG_FILE_NOT_FOUND_IN_DIRECTORY);
      }
   }

   private static File containsFile(File file, String name) {
      return Arrays.asList(file.listFiles()).stream().filter(f -> f.getName().compareToIgnoreCase(name) == 0).findFirst().orElse(null);
   }

   public static boolean copy(File in, File out) {
      if (out.exists()) {
         out.delete();
      }

      mkdirs(out);

      try (
         FileInputStream fis = new FileInputStream(in);
         FileChannel sourceChannel = fis.getChannel();
         FileOutputStream fos = new FileOutputStream(out);
         FileChannel destinationChannel = fos.getChannel();
      ) {
         sourceChannel.transferTo(0L, sourceChannel.size(), destinationChannel);
         return true;
      } catch (Exception var93) {
         out.delete();
         return false;
      }
   }

   public static boolean exportFromJar(String resource, File out) {
      if (out.exists()) {
         out.delete();
      }

      mkdirs(out);

      try (
         InputStream is = Dir.class.getResourceAsStream(resource);
         FileOutputStream fos = new FileOutputStream(out);
      ) {
         byte[] buf = new byte[4096];

         int len;
         while ((len = is.read(buf)) >= 0) {
            fos.write(buf, 0, len);
         }

         return true;
      } catch (Exception var35) {
         out.delete();
         return false;
      }
   }

   public static File getAbsoluteDir() {
      return getAbsoluteDir(new File(""));
   }

   public static File getAbsoluteDir(File file) {
      String dir = file.getAbsolutePath();
      if (!dir.contains(":")) {
         return file;
      } else {
         try {
            Runtime cmd = Runtime.getRuntime();
            Process p = cmd.exec("NET USE " + dir.charAt(0) + ":");

            File var9;
            try (
               InputStreamReader isr = new InputStreamReader(p.getInputStream(), "cp850");
               BufferedReader in = new BufferedReader(isr);
            ) {
               p.waitFor();

               String buff;
               do {
                  if (!in.ready()) {
                     return file;
                  }

                  buff = in.readLine();
               } while (!buff.contains(MSG_REMOTE_NAME));

               var9 = new File(dir.replace(dir.substring(0, dir.indexOf(":") + 1), buff.substring(buff.indexOf("\\\\"))));
            }

            return var9;
         } catch (Exception var41) {
            return file;
         }
      }
   }

   public static boolean hideFile(File file) {
      try {
         String command = "ATTRIB.EXE +H \"" + file.getAbsolutePath() + "\"";
         Runtime.getRuntime().exec(command);
         return true;
      } catch (IOException var2) {
         return false;
      }
   }

   public static long getCreationTime(File file) throws IOException {
      if (!file.exists()) {
         throw new FileNotFoundException(MSG_FILE_NOT_FOUND);
      } else {
         Path path = file.toPath();
         BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
         return attr.creationTime().toMillis();
      }
   }

   public static long getLastModifiedTime(File file) throws IOException {
      if (!file.exists()) {
         throw new FileNotFoundException(MSG_FILE_NOT_FOUND);
      } else {
         Path path = file.toPath();
         BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
         return attr.lastModifiedTime().toMillis();
      }
   }

   public static boolean setLastModifiedTime(File file, long time) {
      try {
         Path path = file.toPath();
         FileTime fileTime = FileTime.fromMillis(time);
         Files.setLastModifiedTime(path, fileTime);
         return true;
      } catch (IOException var5) {
         return false;
      }
   }

   public static File getJarFile() {
      try {
         return new File(URLDecoder.decode(Dir.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
      } catch (Exception var1) {
         return new File(Dir.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " "));
      }
   }

   public static String[] listFilesJar(String path) throws URISyntaxException, IOException {
      Class classe = Dir.class;
      URL dirURL = classe.getClassLoader().getResource(path);
      if (dirURL != null && dirURL.getProtocol().equals("file")) {
         return new File(dirURL.toURI()).list();
      } else {
         if (dirURL == null) {
            String me = classe.getName().replace(".", "/") + ".class";
            dirURL = classe.getClassLoader().getResource(me);
         }

         if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries();
            Set<String> result = new HashSet<>();

            while (entries.hasMoreElements()) {
               String name = entries.nextElement().getName();
               if (name.startsWith(path)) {
                  String entry = name.substring(path.length());
                  int checkSubdir = entry.indexOf("/");
                  if (checkSubdir >= 0) {
                     entry = entry.substring(0, checkSubdir);
                  }

                  result.add(entry);
               }
            }

            return result.toArray(new String[result.size()]);
         } else {
            throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
         }
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

   public static boolean encrypt(File file, String key) {
      try {
         byte[] bytes = getBytes(file);
         file.delete();

         try (
            FileOutputStream fos = new FileOutputStream(file);
            CipherOutputStream cos = encrypt(fos, Security.getPass16(key));
         ) {
            cos.write(bytes);
         }

         return true;
      } catch (Exception var35) {
         return false;
      }
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

   public static byte[] getBytesNoException(File file) {
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
         return null;
      }
   }

   public static byte[] getBytesJar(String url) throws Exception {
      byte[] var7;
      try (
         InputStream is = Dir.class.getResourceAsStream(url);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ) {
         byte[] buf = new byte[2048];

         int i;
         while ((i = is.read(buf)) >= 0) {
            baos.write(buf, 0, i);
         }

         var7 = baos.toByteArray();
      }

      return var7;
   }

   public static void saveBytes(byte[] bytes, File file) throws Exception {
      try (FileOutputStream fos = new FileOutputStream(file)) {
         fos.write(bytes);
      }
   }

   public static boolean decrypt(File file, String key) {
      try {
         byte[] bytes = getBytes(file, Security.getPass16(key));
         file.delete();

         try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
         }

         return true;
      } catch (Exception var16) {
         return false;
      }
   }

   private static byte[] getBytes(File file, String key) throws Exception {
      byte[] var10;
      try (
         FileInputStream fis = new FileInputStream(file);
         CipherInputStream cis = decrypt(fis, key);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ) {
         byte[] buf = new byte[2048];

         int i;
         while ((i = cis.read(buf)) >= 0) {
            baos.write(buf, 0, i);
         }

         var10 = baos.toByteArray();
      }

      return var10;
   }

   private static CipherOutputStream encrypt(OutputStream os, String encKey) throws Exception {
      encKey = Security.getPass16(encKey);
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
      cip.init(1, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return new CipherOutputStream(os, cip);
   }

   private static CipherInputStream decrypt(InputStream is, String encKey) throws Exception {
      encKey = Security.getPass16(encKey);
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
      cip.init(2, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return new CipherInputStream(is, cip);
   }

   public static synchronized String getHash(File file) throws IOException, NoSuchAlgorithmException {
      String var8;
      try (FileInputStream fis = new FileInputStream(file)) {
         MessageDigest digest = MessageDigest.getInstance("SHA-1");
         byte[] buffer = new byte[65536];

         int bytesRead;
         while ((bytesRead = fis.read(buffer, 0, buffer.length)) > 0) {
            digest.update(buffer, 0, bytesRead);
         }

         byte[] md5sum = digest.digest();
         BigInteger bigInt = new BigInteger(1, md5sum);
         var8 = bigInt.toString(16);
      }

      return var8;
   }

   public static String removeInvalidChar(String name) {
      return Arrays.asList("\\", "/", ":", "*", "?", "\"", "<", ">", "|").stream().reduce(name, (c, d) -> c.replace(d, ""));
   }

   public static String getFileName(File f) {
      return f.getName().contains(".") ? f.getName().substring(0, f.getName().lastIndexOf(".")) : f.getName();
   }

   public static boolean isFile(File f) {
      try {
         f.getCanonicalPath();
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   static {
      if (Locale.getDefault().toString().equalsIgnoreCase("pt_BR")) {
         WORD_DIRECTORY = "Diretório";
         MSG_OVERRIDE = "Arquivo já existe.\nDeseja substituí-lo?";
         MSG_SAVE_FILE = "Salvar Arquivo";
         MSG_ERROR_INVALID_NAME = "O nome do arquivo não pode conter nenhum dos seguintes caracteres:\n {0}";
         MSG_FILE_NOT_FOUND_IN_DIRECTORY = "Arquivo não encontrado na pasta";
         MSG_FILE_NOT_FOUND = "Arquivo não encontrado";
         MSG_REMOTE_NAME = "Nome remoto";
      } else {
         WORD_DIRECTORY = "Directory";
         MSG_OVERRIDE = "File already exists.\nDo you want to replace it?";
         MSG_SAVE_FILE = "Save File";
         MSG_ERROR_INVALID_NAME = "The file name can not contain any of the following characters:\n {0}";
         MSG_FILE_NOT_FOUND_IN_DIRECTORY = "File not found in directory";
         MSG_FILE_NOT_FOUND = "File not found";
         MSG_REMOTE_NAME = "Remote name";
      }
   }
}
