package com.limagiran.holyrics.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RWObj {
   private static final String IV = "AAAAAAAAAAAAAAAA";

   public static byte[] getBytes(Object o) {
      try (
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
      ) {
         oos.writeObject(o);
         return baos.toByteArray();
      } catch (Exception var34) {
         return new byte[0];
      }
   }

   public static boolean write(Object object, File file) {
      return write(object, file, null);
   }

   public static boolean write(Object object, File file, String key) {
      File temp = new File(Utils.generateMillisecondId() + ".rwo");

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

   public static <T> T read(File file, Class<T> clazz) {
      return read(file, clazz, null);
   }

   public static <T> T read(File file, Class<T> clazz, String key) {
      try (
         FileInputStream fis = new FileInputStream(file);
         ObjectInputStream ois = new ObjectInputStream((InputStream)(key == null ? fis : decrypt(fis, key)));
      ) {
         Object _return = ois.readObject();
         return (T)(clazz.isInstance(_return) ? _return : null);
      } catch (Exception var37) {
         return null;
      }
   }

   public static <T> T read(byte[] bytes, Class<T> clazz, String key) {
      try (
         ByteArrayInputStream fis = new ByteArrayInputStream(bytes);
         ObjectInputStream ois = new ObjectInputStream((InputStream)(key == null ? fis : decrypt(fis, key)));
      ) {
         Object _return = ois.readObject();
         return (T)(clazz.isInstance(_return) ? _return : null);
      } catch (Exception var37) {
         return null;
      }
   }

   public static <T> T read(byte[] bytes, Class<T> clazz) {
      return read(bytes, clazz, null);
   }

   public static <T> T readJar(String file, Class<T> clazz) {
      return readJar(file, clazz, null);
   }

   public static <T> T readJar(String file, Class<T> clazz, String key) {
      try (
         InputStream is = RWObj.class.getResourceAsStream(file);
         ObjectInputStream ois = new ObjectInputStream((InputStream)(key == null ? is : decrypt(is, key)));
      ) {
         Object _return = ois.readObject();
         return (T)(clazz.isInstance(_return) ? _return : null);
      } catch (Exception var37) {
         return null;
      }
   }

   public static <T> T clone(Object object, Class<T> clazz) {
      try (
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
      ) {
         oos.writeObject(object);

         Object var11;
         try (
            ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(is);
         ) {
            Object retorno = ois.readObject();
            var11 = clazz.isInstance(retorno) ? retorno : null;
         }

         return (T)var11;
      } catch (Exception var95) {
         return null;
      }
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
}
