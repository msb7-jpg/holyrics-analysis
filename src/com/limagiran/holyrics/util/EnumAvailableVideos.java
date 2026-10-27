package com.limagiran.holyrics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.ImageIcon;

public enum EnumAvailableVideos {
   VIDEO01("Sword In The Field", "3,1 mb", "896372d0e04b4634a5c0670d8ceb08addd677e36"),
   VIDEO02("Majesty", "3,9 mb", "42eda6319d88d21f029519639315ce78c930c9e3"),
   VIDEO03("Nature", "2,5 mb", "5cfe31da0db3595b08756649f92c114a6c219198"),
   VIDEO04("Grey Flourish", "5,4 mb", "e66b2c84d4b2e19b6d5227775af807c948fef35e"),
   VIDEO05("Fire Rain", "1,2 mb", "2ba074a06d53cf26b1c4503a63bae39821782397"),
   VIDEO06("Rock Concert Crowd", "1,0 mb", "78a80a63fa1ac95a073ba5c6203954a210ce5e13"),
   VIDEO07("Autum", "3,1 mb", "25a236d15f09d3bf74316241458133c4bfd262fb"),
   VIDEO08("Warm Sun", "4,9 mb", "ebe3edcde6db4619a298223ec2fef6ac761c56f2"),
   VIDEO09("Street", "10,0 mb", "37917c5bb39508fcbb1feb385ecd7f5f4a4dbc16"),
   VIDEO10("Snow 4", "11,6 mb", "1b49e4727762c57609c5ecabb37810e1e4d656f"),
   VIDEO11("Hexagons", "5,7 mb", "d3815407ed98925f16ac3bf34dff2f6f96ad6ca3"),
   VIDEO12("Blocks", "2,0 mb", "a1b4ec23f1bf094663784481b5bc6c200cb771a6"),
   VIDEO13("Ocean 2", "30,6 mb", "72347ea6a4ac2f2a84d01761b911ecd361054196"),
   VIDEO14("Bokeh Blue", "4,2 mb", "d74a3a8aae976a207d3d71b1c540fb0a4c77d05b"),
   VIDEO15("Bokeh Lens Focus For Green", "1,7 mb", "5539c101293977fc7335435d6ef6aee3d3699911"),
   VIDEO16("Bright Circles", "1,8 mb", "cab87226eb1efa032682447bdefa2a635a5d695f"),
   VIDEO17("Brightness", "2,5 mb", "4af94eb6bfa3a78e86551a5508eaa58e91156c82"),
   VIDEO18("City Temple Ruins", "7,6 mb", "dac40ae5cdbcc2d58119a0bb01a0d83bfee030e1"),
   VIDEO19("Cross 1", "5,6 mb", "2f6176dde89a058f3c92de33a9212ffbfd45a36e"),
   VIDEO20("Cross 2", "3,6 mb", "a548c32bbb387d6335cd33ac91bf5bc8e234252"),
   VIDEO21("Drop", "3,7 mb", "a9a6446b04abb005fabd46e6c6a957c13c5bf448"),
   VIDEO22("Free Urban", "8,0 mb", "602127a07d72280941d6e2482069410b48a0c53b"),
   VIDEO23("Jonah", "0,4 mb", "56773134308d3fcc19f62d6e033b2cea42798cb6"),
   VIDEO24("lasers", "6,6 mb", "fa9866bc007f76226349d8da7e9d2d574461ca68"),
   VIDEO25("Light", "5,5 mb", "ad62d431dad580e09d6610e258fa99817ca67e41"),
   VIDEO26("Night", "3,2 mb", "6e00381b5a2793a9032198168979d9b8a0d7c4fd"),
   VIDEO27("Ocean 1", "3,5 mb", "fb07a2bdfaecab82273f63a76639daeb5bbc8239"),
   VIDEO28("Particles 1", "2,3 mb", "3a90f3cece3e27618044469a500d78d5bbc3a26a"),
   VIDEO29("Particles 2", "2,5 mb", "4888ceef5ac9565bb5a36fd99bf8f82d06742506"),
   VIDEO30("Particles 3", "2,3 mb", "215cfbf610760af4edf7d4c47cb2b3a8de131202"),
   VIDEO31("Particles 4", "9,7 mb", "2e81d25901593b78f18c0c881015cd3214cbbb82"),
   VIDEO32("Rain On The Glass", "2,1 mb", "d2670527b29bde43919cc6d7280afe82425293b"),
   VIDEO33("Rain", "10,6 mb", "83f47f06c88196a2f8785a85e8e1f3559447c17c"),
   VIDEO34("Shine", "9,1 mb", "6d18b66c256b5ed32cd32bf707d506f38066025"),
   VIDEO35("Snow 1", "8,3 mb", "6c7929f4f9e03dc14d8233ad28d2a5d9c6b9c79d"),
   VIDEO36("Snow 2", "4,8 mb", "23c97db580258b299f91800917a59fee30a0845a"),
   VIDEO37("Snow 3", "5,9 mb", "59e15493ed121ee5597601c84a9f81e9f6e9f9cb"),
   VIDEO38("Submerged", "4,0 mb", "cb3eda282cc26b0a5bb5b3f5ca5cef2c0d5741f");

   private static final String RESOURCE_VIDEO = "/com/limagiran/holyrics/model/theme/video/%s.mp4";
   private static final String RESOURCE_GIF = "/com/limagiran/holyrics/model/theme/img/%s.gif";
   private static final String RESOURCE_JPG = "/com/limagiran/holyrics/model/theme/img/%s.jpg";
   private static final String URL = "http://www.holyrics.com/download/video/%s.mp4";
   public final String fileName;
   public final ImageIcon gif = new ImageIcon(this.getClass().getResource(String.format("/com/limagiran/holyrics/model/theme/img/%s.gif", this.toString())));
   public final String imageUrl = String.format("/com/limagiran/holyrics/model/theme/img/%s.jpg", this.toString());
   public final String urlDownload = String.format("http://www.holyrics.com/download/video/%s.mp4", this.toString());
   public final String name;
   public final String size;
   public final String hash;

   public static void load() {
   }

   private EnumAvailableVideos(String name, String size, String hash) {
      this.fileName = this.toString() + ".mp4";
      this.name = name;
      this.size = size;
      this.hash = hash;
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

   public static boolean exportFromJar(String resource, File out) {
      if (out.exists()) {
         out.delete();
      }

      try {
         File file = out.getAbsoluteFile();
         if (file.getName().contains(".")) {
            file.getParentFile().mkdirs();
         } else {
            file.mkdirs();
         }
      } catch (Exception var33) {
      }

      try (
         InputStream is = EnumAvailableVideos.class.getResourceAsStream(resource);
         FileOutputStream fos = new FileOutputStream(out);
      ) {
         byte[] buf = new byte[4096];

         int len;
         while ((len = is.read(buf)) >= 0) {
            fos.write(buf, 0, len);
         }

         return true;
      } catch (Exception var38) {
         out.delete();
         return false;
      }
   }

   static {
      try {
         File f = new File(Values.PATH_BACKGROUND_VIDEO + VIDEO23.toString() + ".mp4");
         if (!f.exists() || !getHash(f).equals(VIDEO23.hash)) {
            exportFromJar(String.format("/com/limagiran/holyrics/model/theme/video/%s.mp4", VIDEO23), f);
         }
      } catch (Exception var1) {
      }
   }
}
