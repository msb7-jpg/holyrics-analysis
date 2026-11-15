package com.limagiran.lgsaveopen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerialMap implements Serializable {
   private static final long serialVersionUID = 12491L;
   public static final String MAIN = "main";
   private final Map<String, byte[]> map = new HashMap<>();
   public final long hashCode;

   public SerialMap(LGSaveOpen hashCode) {
      this.hashCode = hashCode.getHashCode();
   }

   public <T> T get(String key, Class<T> classe) {
      try (
         ByteArrayInputStream is = new ByteArrayInputStream(this.map.get(key));
         ObjectInputStream ois = new ObjectInputStream(is);
      ) {
         Object retorno = ois.readObject();
         return (T)(classe.isInstance(retorno) ? retorno : null);
      } catch (Exception var37) {
         return null;
      }
   }

   public boolean put(String key, Object object) {
      try (
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
      ) {
         oos.writeObject(object);
         this.map.put(key, baos.toByteArray());
         return true;
      } catch (Exception var36) {
         return false;
      }
   }
}
