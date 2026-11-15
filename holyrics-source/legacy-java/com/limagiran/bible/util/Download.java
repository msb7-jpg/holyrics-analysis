package com.limagiran.bible.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;

public class Download implements Runnable {
   private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
   public static final int STATUS_OK = 0;
   public static final int STATUS_ERROR = 1;
   public static final int STATUS_DOWNLOADING = 2;
   private static final String MSG_INVALID_HASH;
   private final String url;
   private final String hash;
   private String velocity = "";
   private File file;
   private byte[] bytes;
   private int status;
   private int totalBytes = 0;
   private int downloadedBytes = 0;
   private int bytesTaxa = 0;
   private boolean cancelled = false;
   private boolean finish = false;
   private Exception error;
   private final CountDownLatch latch = new CountDownLatch(1);

   private Download(String url, File file, String hash) {
      this.url = url;
      this.file = file != null ? file.getAbsoluteFile() : file;
      this.hash = hash;
   }

   @Override
   public void run() {
      this.status = 2;

      try {
         this.downloadBytes();
         this.checkHash();
         this.save();
         this.status = 0;
      } catch (Exception var5) {
         this.status = 1;
         this.error = var5;
      } finally {
         this.finish = true;
      }

      this.latch.countDown();
   }

   public void waitFinish() {
      try {
         this.latch.await();
      } catch (Exception var2) {
      }
   }

   public static Download download(String url, File file, String hash) {
      Download download = new Download(url, file, hash);
      new Thread(download).start();
      return download;
   }

   public static Download download(String url, File file) {
      return download(url, file, null);
   }

   public static Download download(String url) {
      return download(url, null, null);
   }

   public static Download download(String url, String hash) {
      return download(url, null, hash);
   }

   private void downloadBytes() throws Exception {
      byte[] buf = new byte[2048];

      try {
         this.rateDownload();
         URLConnection conn = new URL(this.url).openConnection();
         conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");

         try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = conn.getInputStream();
         ) {
            this.totalBytes = conn.getContentLength();
            this.fileName(conn);
            this.downloadedBytes = 0;
            this.bytesTaxa = 0;

            int len;
            while ((len = is.read(buf)) >= 0) {
               baos.write(buf, 0, len);
               this.downloadedBytes += len;
               this.bytesTaxa += len;
               this.downloadedBytes = this.downloadedBytes > this.totalBytes ? this.totalBytes : this.downloadedBytes;
               if (this.cancelled) {
                  throw new Exception("canceled");
               }
            }

            this.bytes = baos.toByteArray();
         }
      } catch (Exception var36) {
         throw var36;
      }
   }

   public String getText() {
      return this.getText(Charset.defaultCharset().toString());
   }

   public String getText(String charset) {
      this.waitFinish();

      try {
         if (this.status == 0 && this.isCompressed()) {
            String var9;
            try (
               ByteArrayInputStream bais = new ByteArrayInputStream(this.bytes);
               GZIPInputStream gis = new GZIPInputStream(bais);
            ) {
               StringBuilder _return = new StringBuilder();
               byte[] buf = new byte[524288];

               int len;
               while ((len = gis.read(buf)) >= 0) {
                  _return.append(new String(buf, 0, len, charset));
               }

               var9 = _return.toString();
            }

            return var9;
         } else {
            return new String(this.bytes, charset);
         }
      } catch (Exception var38) {
         return "";
      }
   }

   public byte[] getBytes() {
      this.waitFinish();

      try {
         if (this.status == 0 && this.isCompressed()) {
            byte[] var8;
            try (
               ByteArrayInputStream bais = new ByteArrayInputStream(this.bytes);
               GZIPInputStream gis = new GZIPInputStream(bais);
            ) {
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               byte[] buf = new byte[524288];

               int len;
               while ((len = gis.read(buf)) >= 0) {
                  baos.write(buf, 0, len);
               }

               var8 = baos.toByteArray();
            }

            return var8;
         } else {
            return Arrays.copyOf(this.bytes, this.bytes.length);
         }
      } catch (Exception var37) {
         return null;
      }
   }

   public boolean isCompressed() {
      return this.bytes != null && this.bytes.length >= 2 ? this.bytes[0] == 31 && this.bytes[1] == -117 : false;
   }

   public String getPercentageString() {
      return new DecimalFormat("##0.0").format(this.getPercentage()) + "%";
   }

   public double getPercentage() {
      return this.totalBytes > 0 ? (double)this.downloadedBytes / (double)this.totalBytes * 100.0 : 0.0;
   }

   public String getVelocity() {
      return this.velocity;
   }

   private void rateDownload() {
      new Thread(() -> {
         while (!this.finish) {
            this.velocity = "Velocidade: " + (int)((double)this.bytesTaxa / 1000.0) + "KB";
            this.bytesTaxa = 0;
            sleep(1000L);
         }
      }).start();
   }

   private static void sleep(long time) {
      try {
         Thread.sleep(time);
      } catch (Exception var3) {
      }
   }

   public int getStatus() {
      return this.status;
   }

   public boolean isError() {
      this.waitFinish();
      return this.status == 1;
   }

   public boolean isOk() {
      this.waitFinish();
      return this.status == 0;
   }

   public boolean isFinish() {
      return this.finish;
   }

   public void cancel() {
      this.cancelled = true;
   }

   private void save() throws IOException {
      if (this.file != null) {
         this.mkdirs();

         try (FileOutputStream fos = new FileOutputStream(new File(this.file.getAbsoluteFile() + (this.isCompressed() ? ".gz" : "")))) {
            fos.write(this.bytes);
         }
      }
   }

   private void fileName(URLConnection conn) {
      if (this.file != null && this.file.isDirectory()) {
         String fileName;
         if (this.url.contains("/") && !this.url.endsWith("/")) {
            fileName = this.url.split("/")[this.url.split("/").length - 1];
         } else {
            String type = conn.getContentType();
            type = type != null ? "." + type.split("/")[1] : "";
            fileName = "Download " + Long.toString(System.currentTimeMillis()).substring(7) + type;
         }

         this.file = new File(this.file.getAbsolutePath() + File.separator + fileName);
      }
   }

   public Exception getError() {
      return this.error;
   }

   private void checkHash() throws Exception {
      if (this.hash != null && !getHash(Arrays.copyOf(this.bytes, this.bytes.length)).equals(this.hash)) {
         throw new Exception(MSG_INVALID_HASH);
      }
   }

   public static String getHash(byte[] in) throws IOException, NoSuchAlgorithmException {
      byte[] out = MessageDigest.getInstance("SHA-1").digest(in);
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < out.length; i++) {
         sb.append(Integer.toString((out[i] & 255) + 256, 16).substring(1));
      }

      return sb.toString();
   }

   private void mkdirs() {
      try {
         if (this.file.getName().contains(".")) {
            this.file.getParentFile().mkdirs();
         } else {
            this.file.mkdirs();
         }
      } catch (Exception var2) {
      }
   }

   public static byte[] sendPost(String url, Map<String, Object> map) {
      try {
         StringBuilder params = new StringBuilder();
         map.forEach((s, o) -> params.append(s).append("=").append(normalizeParam(o.toString())).append("&"));
         byte[] postData = params.toString().getBytes(Charset.forName("UTF-8"));
         int postDataLength = postData.length;
         URL _url = new URL(url);
         HttpURLConnection conn = (HttpURLConnection)_url.openConnection();
         conn.setConnectTimeout(4000);
         conn.setDoOutput(true);
         conn.setInstanceFollowRedirects(false);
         conn.setRequestMethod("POST");
         conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setRequestProperty("charset", "utf-8");
         conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
         conn.setUseCaches(false);

         try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
         }

         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         try (InputStream is = conn.getInputStream()) {
            byte[] buf = new byte[524288];

            int len;
            while ((len = is.read(buf)) >= 0) {
               baos.write(buf, 0, len);
            }
         }

         return baos.toByteArray();
      } catch (Exception var37) {
         return new byte[0];
      }
   }

   private static String normalizeParam(String s) {
      return s.replace("+", "%2B").replace("=", "%3D").replace(" ", "%20").replace("&", "%26");
   }

   static {
      if (Locale.getDefault().toString().equalsIgnoreCase("pt_BR")) {
         MSG_INVALID_HASH = "Hash Inválido";
      } else {
         MSG_INVALID_HASH = "Invalid Hash";
      }
   }
}
