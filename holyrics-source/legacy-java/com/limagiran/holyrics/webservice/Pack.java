package com.limagiran.holyrics.webservice;

import com.limagiran.holyrics.util.Security;
import com.limagiran.holyrics.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pack {
   public static final String KEY_ERROR = "key_error";
   public static final String KEY_OK = "key_ok";
   public final Map<String, Object> map = new HashMap<>();

   public static Pack create(String json) {
      try {
         return (Pack)HolyricsWS.GSON.fromJson(json, Pack.class);
      } catch (Exception var2) {
         return create(false, "exception");
      }
   }

   public static Pack createDecrypt(Pack p, String key) {
      try {
         String toJson64 = p.getString("pack64", "");
         byte[] bytesJson = Utils.base64ToBytes(toJson64);
         return create(Security.decryptCompatApp(bytesJson, key));
      } catch (Exception var4) {
         return create(false, "exception");
      }
   }

   public String toJSon() {
      return HolyricsWS.GSON.toJson(this);
   }

   public Pack encrypt(String key) {
      try {
         byte[] bytesJsonEnc = Security.encryptCompatApp(this.toJSon(), key);
         String packEnc64 = Utils.bytesToBase64(bytesJsonEnc);
         return create(true).put("request", this.getString("request", "")).put("pack64", packEnc64);
      } catch (Exception var4) {
         return create(false, "exception");
      }
   }

   public static Pack create(boolean ok, String error) {
      return new Pack().put("key_ok", String.valueOf(ok)).put("key_error", error);
   }

   public static Pack create(boolean ok) {
      return create(ok, "");
   }

   public Pack put(String key, Object value) {
      if (!value.getClass().isPrimitive() && !Long.class.isInstance(value) && !Integer.class.isInstance(value)) {
         this.map.put(key, value);
      } else {
         this.map.put(key, value.toString());
      }

      return this;
   }

   public boolean isOk() {
      return Boolean.parseBoolean(this.map.getOrDefault("key_ok", "false").toString());
   }

   public String error() {
      return this.map.getOrDefault("key_error", "").toString();
   }

   public String getString(String key, String _default) {
      return this.map.getOrDefault(key, _default).toString();
   }

   public List<String> getList(String key) {
      try {
         return (List<String>)this.map.get(key);
      } catch (Exception var3) {
         return new ArrayList<>();
      }
   }

   public boolean getBoolean(String key) {
      return Boolean.parseBoolean(this.map.getOrDefault(key, "false").toString());
   }

   public int getInt(String key, int _default) {
      try {
         return Integer.parseInt(this.map.get(key).toString());
      } catch (Exception var4) {
         return _default;
      }
   }

   public long getLong(String key, long _default) {
      try {
         return Long.parseLong(this.map.get(key).toString());
      } catch (Exception var5) {
         return _default;
      }
   }
}
