package com.limagiran.bible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Bible extends BibleFind {
   private transient Bible bibleFind;
   private transient Map<Integer, Map<Integer, Integer>> map;
   private transient List<String> mapAsList;
   public final String version;
   public final String abbrev;
   public final long update;
   public final String language;
   public final List<Book> books;
   public final List<Verse> verses;

   protected Bible(String version, String abbrev, long update, String language, List<Book> books, List<Verse> verses) {
      this.version = version;
      this.abbrev = abbrev;
      this.update = update;
      this.language = language;
      this.books = books;
      this.verses = verses;
   }

   protected void setBibleFind(Bible bibleFind) {
      this.bibleFind = bibleFind;
   }

   @Override
   protected Bible getBible() {
      return this;
   }

   @Override
   protected Bible getBibleFind() {
      return this.bibleFind;
   }

   public Map<Integer, Map<Integer, Integer>> getMap() {
      return new HashMap<>(this.map == null ? (this.map = loadMap(this)) : this.map);
   }

   public List<String> getMapAsList() {
      return new ArrayList<>(this.mapAsList == null ? (this.mapAsList = loadMapAsList(this.getMap())) : this.mapAsList);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof Bible ? ((Bible)obj).version.equals(this.version) : super.equals(obj);
   }

   @Override
   public int hashCode() {
      return 217 + Objects.hashCode(this.version);
   }
}
