package com.limagiran.vagalumeapi;

import com.google.gson.Gson;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class VagalumeAPI {
   public static final String URL = "http://www.vagalume.com.br";

   private VagalumeAPI() {
   }

   public static Result resultByID(String id, String key) {
      try {
         String url = "https://api.vagalume.com.br/search.php";
         Map<String, Object> map = new HashMap<>();
         map.put("musid", URLEncoder.encode(id, "UTF-8"));
         map.put("apikey", URLEncoder.encode(key, "UTF-8"));
         String json = sendRequest(url, map);
         Result result = (Result)new Gson().fromJson(json, Result.class);

         while (result.captcha) {
            String udig = result.getCaptcha();
            if (udig == null) {
               return null;
            }

            map.put("udig", udig);
            map.put("serial", result.serial);
            json = sendRequest(url, map);
            result = (Result)new Gson().fromJson(json, Result.class);
         }

         return result;
      } catch (Exception var7) {
         return null;
      }
   }

   public static Search searchByMusic(String value) {
      try {
         String url = "https://api.vagalume.com.br/search.excerpt";
         Map<String, Object> map = new HashMap<>();
         map.put("q", URLEncoder.encode(value, "UTF-8"));
         map.put("limit", 10);
         String json = sendRequest(url, map);
         return (Search)new Gson().fromJson(json, Search.class);
      } catch (Exception var4) {
         return null;
      }
   }

   private static String sendRequest(String url, Map<String, Object> map) {
      try {
         StringBuilder params = new StringBuilder();
         map.forEach((s, o) -> params.append(s).append("=").append(o).append("&"));
         url = url + "?" + params.toString().substring(0, params.toString().length() - 1);
         Download d = Download.download(url);
         return d.getText();
      } catch (Exception var4) {
         return null;
      }
   }
}
