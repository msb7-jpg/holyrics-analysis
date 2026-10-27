package com.limagiran.vagalumeapi;

public class Music {
   public final String id;
   public final String name;
   public final int lang;
   public final String url;
   public final String text;

   public Music(String id, String name, int lang, String url, String text) {
      this.id = id;
      this.name = name;
      this.lang = lang;
      this.url = url;
      this.text = text;
   }
}
