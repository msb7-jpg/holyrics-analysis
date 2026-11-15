package com.limagiran.lgsaveopen;

import java.awt.Window;
import java.io.File;
import javax.swing.JOptionPane;

public interface LGSaveOpen<T> {
   String KEY = "$%#@keyLGSO*&¨12";

   long getHashCode();

   T getInstance();

   String getExtension();

   String getFileDesc();

   default SerialMap toSerialMap(T t) throws Exception {
      SerialMap map = new SerialMap(this);
      map.put("main", t);
      return map;
   }

   T createFromSerialMap(SerialMap var1) throws Exception;

   default void save(Window owner) {
      try {
         SerialMap map = this.toSerialMap(this.getInstance());
         File f = Utils.windowsSave(I18N.WORD_SAVE, "", this.getFileDesc(), this.getExtension());
         if (f != null) {
            Utils.write(map, f, "$%#@keyLGSO*&¨12");
            JOptionPane.showMessageDialog(owner, I18N.WORD_DONE);
         }
      } catch (Exception var4) {
         Utils.error(owner, I18N.MSG_ERROR_TRY_AGAIN);
      }
   }

   default void save(Window owner, File file) {
      try {
         SerialMap map = this.toSerialMap(this.getInstance());
         Utils.write(map, file, "$%#@keyLGSO*&¨12");
      } catch (Exception var4) {
         Utils.error(owner, I18N.MSG_ERROR_TRY_AGAIN);
      }
   }

   default boolean save(File file) {
      try {
         SerialMap map = this.toSerialMap(this.getInstance());
         Utils.write(map, file, "$%#@keyLGSO*&¨12");
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   static <T> T open(Window owner, Class<T> clazz, LGSaveOpen lgso) {
      try {
         File file = Utils.windowsFile(I18N.WORD_OPEN, lgso.getFileDesc(), lgso.getExtension());
         if (file != null) {
            SerialMap map = Utils.read(file, SerialMap.class, "$%#@keyLGSO*&¨12");
            if (map != null && map.hashCode == lgso.getHashCode()) {
               Object retorno = lgso.createFromSerialMap(map);
               return (T)(clazz.isInstance(retorno) ? retorno : null);
            }

            Utils.error(owner, I18N.MSG_ERROR_INVALID_FILE);
            return null;
         }
      } catch (Exception var6) {
         Utils.error(owner, I18N.MSG_ERROR_TRY_AGAIN);
      }

      return null;
   }

   static <T> T open(Window owner, Class<T> clazz, LGSaveOpen lgso, File file) {
      try {
         SerialMap map = Utils.read(file, SerialMap.class, "$%#@keyLGSO*&¨12");
         if (map != null && map.hashCode == lgso.getHashCode()) {
            Object _return = lgso.createFromSerialMap(map);
            return (T)(clazz.isInstance(_return) ? _return : null);
         } else {
            Utils.error(owner, I18N.MSG_ERROR_INVALID_FILE);
            return null;
         }
      } catch (Exception var6) {
         Utils.error(owner, I18N.MSG_ERROR_TRY_AGAIN);
         return null;
      }
   }

   static <T> T open(Class<T> clazz, LGSaveOpen lgso, File file) {
      try {
         SerialMap map = Utils.read(file, SerialMap.class, "$%#@keyLGSO*&¨12");
         if (map != null && map.hashCode == lgso.getHashCode()) {
            Object _return = lgso.createFromSerialMap(map);
            return (T)(clazz.isInstance(_return) ? _return : null);
         } else {
            return null;
         }
      } catch (Exception var5) {
         return null;
      }
   }
}
