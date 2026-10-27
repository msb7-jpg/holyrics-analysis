package com.limagiran.holyrics.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Security {
   private static final String IV = "AAAAAAAAAAAAAAAA";

   public static byte[] encrypt(String plainText, String encKey) throws Exception {
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(getPass16(encKey).getBytes("UTF-8"), "AES");
      cip.init(1, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return cip.doFinal(plainText.getBytes("UTF-8"));
   }

   public static String decrypt(byte[] cipherText, String encKey) throws Exception {
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(getPass16(encKey).getBytes("UTF-8"), "AES");
      cip.init(2, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return new String(cip.doFinal(cipherText), "UTF-8");
   }

   public static byte[] encryptCompatApp(String message, String key) throws Exception {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(1, new SecretKeySpec(getPass16(key).getBytes(), "AES"));
      return cipher.doFinal(message.getBytes("UTF-8"));
   }

   public static String decryptCompatApp(byte[] cipherText, String key) throws Exception {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(2, new SecretKeySpec(getPass16(key).getBytes(), "AES"));
      return new String(cipher.doFinal(cipherText), "UTF-8");
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

   public static byte[] encryptBytes(byte[] bytes, String encKey) throws Exception {
      encKey = getPass16(encKey);
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
      cip.init(1, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return cip.doFinal(bytes);
   }

   public static byte[] decryptBytes(byte[] cipherText, String encKey) throws Exception {
      encKey = getPass16(encKey);
      Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
      SecretKeySpec key = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
      cip.init(2, key, new IvParameterSpec("AAAAAAAAAAAAAAAA".getBytes("UTF-8")));
      return cip.doFinal(cipherText);
   }

   public static String getMD5(String value) {
      String var3;
      try {
         MessageDigest mdEnc = MessageDigest.getInstance("MD5");
         mdEnc.update(value.getBytes(), 0, value.length());
         return new BigInteger(1, mdEnc.digest()).toString(16);
      } catch (Exception var7) {
         var3 = "";
      } finally {
         MessageDigest mdEnc = null;
      }

      return var3;
   }

   public static String getMD5(String value, int factor) {
      String i;
      try {
         value = value + value;
         byte[] bytes = value.getBytes();

         for (int ix = 0; ix < bytes.length; ix++) {
            if (ix % 3 == 0) {
               bytes[ix] = (byte)(bytes[ix] ^ factor);
            }
         }

         MessageDigest mdEnc = MessageDigest.getInstance("MD5");
         mdEnc.update(bytes, 0, value.length());
         return new BigInteger(1, mdEnc.digest()).toString(16);
      } catch (Exception var8) {
         i = "";
      } finally {
         MessageDigest mdEnc = null;
      }

      return i;
   }
}
