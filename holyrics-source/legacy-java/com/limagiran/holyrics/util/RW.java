package com.limagiran.holyrics.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class RW {
   public static String read(File file, String charset) throws FileNotFoundException, IOException, Exception {
      StringBuilder sb = new StringBuilder();

      try (
         FileInputStream fis = new FileInputStream(file);
         InputStreamReader isr = new InputStreamReader(fis, charset);
         BufferedReader br = new BufferedReader(isr);
      ) {
         String firstLine = br.readLine();
         sb.append(firstLine == null ? "" : firstLine);

         while (br.ready()) {
            sb.append("\n");
            sb.append(br.readLine());
         }
      }

      return sb.toString().replace("\ufeff", "");
   }

   public static String read(File file) throws FileNotFoundException, IOException, Exception {
      return read(file, "UTF-8");
   }

   public static void write(File file, String value, String charset) throws IOException, Exception {
      try (
         FileOutputStream fos = new FileOutputStream(file);
         OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
         BufferedWriter bw = new BufferedWriter(osw);
      ) {
         String[] buff = value.split("\n");
         bw.write(buff[0]);

         for (int indice = 1; indice < buff.length; indice++) {
            bw.newLine();
            bw.write(buff[indice]);
         }

         bw.close();
      }
   }

   public static void write(File file, String value) throws IOException, Exception {
      write(file, value, "UTF-8");
   }

   public static byte[] readJar(String url) throws Exception {
      byte[] var7;
      try (
         InputStream is = RW.class.getResourceAsStream(url);
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
}
