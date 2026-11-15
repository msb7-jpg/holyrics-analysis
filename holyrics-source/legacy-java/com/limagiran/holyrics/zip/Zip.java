package com.limagiran.holyrics.zip;

import com.limagiran.holyrics.util.Utils;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Zip {
   private static final String ERR_NOT_EXISTS;
   private static final String ERR_MKDIRS;

   public static void zipText(String text, File out) throws IOException {
      try (
         FileOutputStream fos = new FileOutputStream(out);
         ZipOutputStream zos = new ZipOutputStream(fos);
      ) {
         zos.putNextEntry(new ZipEntry("text.txt"));
         zos.write(text.getBytes());
         zos.closeEntry();
      } catch (IOException var34) {
         throw var34;
      }
   }

   public static void zip(File in, File out, Zip.ZipProgress zipProgress) throws Exception {
      try {
         zipProgress = Zip.ZipProgress.nullSafe(zipProgress);
         mkdirs(out);
         if (in.isDirectory()) {
            zipFolder(in.getAbsolutePath(), out, zipProgress);
         } else {
            zipFile(in, out);
         }
      } finally {
         zipProgress.finish = true;
      }
   }

   private static void zipFile(File in, File out) throws IOException {
      try (
         FileInputStream fis = new FileInputStream(in);
         FileOutputStream fos = new FileOutputStream(out);
         ZipOutputStream zipOut = new ZipOutputStream(fos);
      ) {
         zipOut.setLevel(0);
         zipOut.putNextEntry(new ZipEntry(in.getName()));
         byte[] buf = new byte[16384];

         int content;
         while ((content = fis.read(buf)) >= 0) {
            zipOut.write(buf, 0, content);
         }

         zipOut.closeEntry();
      } catch (IOException var61) {
         throw var61;
      }
   }

   public static void zipBytes(List<Zip.Entry> entries, File out) throws Exception {
      mkdirs(out);

      try (
         FileOutputStream fos = new FileOutputStream(out);
         ZipOutputStream zipOut = new ZipOutputStream(fos, Charset.forName("IBM437"));
      ) {
         for (Zip.Entry in : entries) {
            zipOut.putNextEntry(new ZipEntry(in.name));
            zipOut.write(in.bytes);
         }

         zipOut.closeEntry();
      } catch (Exception var35) {
         throw var35;
      }
   }

   private static void zipFolder(String origin, File destination, Zip.ZipProgress zipProgress) throws Exception {
      zipProgress.total = count(new File(origin));

      try (
         FileOutputStream fos = new FileOutputStream(destination);
         ZipOutputStream zip = new ZipOutputStream(fos);
      ) {
         zip.setLevel(0);
         addFolderToZip("", origin, zip, zipProgress);
         zip.flush();
      }
   }

   private static void addFileToZip(String path, String srcFile, ZipOutputStream zip, Zip.ZipProgress zipProgress) throws Exception {
      File folder = new File(srcFile);
      if (folder.isDirectory()) {
         addFolderToZip(path, srcFile, zip, zipProgress);
      } else {
         byte[] buf = new byte[16384];

         try (FileInputStream fis = new FileInputStream(srcFile)) {
            zip.putNextEntry(new ZipEntry(path + File.separator + folder.getName()));

            int len;
            while ((len = fis.read(buf)) >= 0) {
               zip.write(buf, 0, len);
            }
         } finally {
            zipProgress.length++;
            zipProgress.file = srcFile;
         }
      }
   }

   private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip, Zip.ZipProgress zipProgress) throws Exception {
      File folder = new File(srcFolder);

      for (String fileName : folder.list()) {
         if (path.equals("")) {
            addFileToZip(folder.getName(), srcFolder + File.separator + fileName, zip, zipProgress);
         } else {
            addFileToZip(path + File.separator + folder.getName(), srcFolder + File.separator + fileName, zip, zipProgress);
         }
      }
   }

   public static void unzip(File dest, File zipFile) throws Exception {
      if (!zipFile.exists()) {
         throw new IOException(MessageFormat.format(ERR_NOT_EXISTS, zipFile.getAbsolutePath()));
      } else if (!buildDirectory(dest)) {
         throw new IOException(MessageFormat.format(ERR_MKDIRS, dest));
      } else {
         try (ZipFile zips = new ZipFile(zipFile)) {
            Enumeration entries = zips.entries();

            while (entries.hasMoreElements()) {
               ZipEntry entry = (ZipEntry)entries.nextElement();
               File file = new File(dest, File.separator + entry.getName());
               if (!buildDirectory(file.getParentFile())) {
                  throw new IOException(MessageFormat.format(ERR_NOT_EXISTS, file.getParentFile()));
               }

               if (!entry.isDirectory()) {
                  copyStream(zips.getInputStream(entry), file);
               } else if (!buildDirectory(file)) {
                  throw new IOException(MessageFormat.format(ERR_NOT_EXISTS, file));
               }
            }
         }
      }
   }

   public static void unzip(File zipFile) throws Exception {
      unzip(zipFile.getParentFile(), zipFile);
   }

   private static boolean buildDirectory(File file) {
      return file.exists() || file.mkdirs();
   }

   private static void copyStream(InputStream in, File file) throws Exception {
      try (
         FileOutputStream fos = new FileOutputStream(file);
         BufferedOutputStream bos = new BufferedOutputStream(fos);
      ) {
         byte[] buffer = new byte[16384];

         int len;
         while ((len = in.read(buffer)) >= 0) {
            bos.write(buffer, 0, len);
         }

         in.close();
      }
   }

   private static void mkdirs(File file) {
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

   public static byte[] zipBytes(byte[] in) throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try (OutputStream out = new DeflaterOutputStream(baos)) {
         out.write(in);
      }

      return baos.toByteArray();
   }

   public static byte[] unzipBytes(byte[] in) throws Exception {
      InputStream is = new InflaterInputStream(new ByteArrayInputStream(in));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];

      int len;
      while ((len = is.read(buffer)) > 0) {
         baos.write(buffer, 0, len);
      }

      return baos.toByteArray();
   }

   private static int count(File file) {
      File[] files = file.listFiles();
      int count = 0;

      for (File f : files) {
         count += f.isDirectory() ? count(f) : 1;
      }

      return count;
   }

   static {
      if (Locale.getDefault().toString().equalsIgnoreCase("pt_BR")) {
         ERR_NOT_EXISTS = "Erro - {0} não existe.";
         ERR_MKDIRS = "Não foi possível criar o diretório: {0}";
      } else {
         ERR_NOT_EXISTS = "Error - {0} not exists.";
         ERR_MKDIRS = "Could not create the directory: {0}";
      }
   }

   public static class Entry {
      public final String name;
      public final byte[] bytes;

      public Entry(String name, byte[] bytes) {
         this.name = name;
         this.bytes = bytes;
      }
   }

   public abstract static class ZipProgress {
      private int length = 0;
      private int total = 1;
      private String file = "";
      private boolean finish;

      public ZipProgress() {
         Utils.startThreadDaemon(() -> {
            while (!this.finish) {
               Utils.sleep(1000L);
               this.update(this.length, this.total, this.file);
            }
         });
      }

      public abstract void update(int var1, int var2, String var3);

      private static Zip.ZipProgress nullSafe(Zip.ZipProgress zipProgress) {
         return zipProgress == null ? new Zip.ZipProgress() {
            @Override
            public void update(int length, int total, String file) {
            }
         } : zipProgress;
      }
   }
}
