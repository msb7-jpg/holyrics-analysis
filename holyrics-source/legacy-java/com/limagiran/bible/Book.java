package com.limagiran.bible;

import java.util.List;
import java.util.stream.Collectors;

public class Book {
   private transient Bible bible;
   private transient int idInt = 0;
   public final String id;
   public final String name;
   public final String abbrev;
   public final String testament;
   public final String testamentAbbrev;

   protected Book(String id, String name, String abbrev, String testament, String testamentAbbrev) {
      this.id = id;
      this.name = name;
      this.abbrev = abbrev;
      this.testament = testament;
      this.testamentAbbrev = testamentAbbrev;
   }

   @Override
   public String toString() {
      return this.name;
   }

   public int getIdInt() {
      return this.idInt > 0 ? this.idInt : (this.idInt = Integer.parseInt(this.id));
   }

   protected void setBible(Bible bible) {
      this.bible = bible;
   }

   public int getAmountChapter() {
      return (int)this.getAllVerses().parallelStream().map(v -> v.chapter).distinct().count();
   }

   public List<Verse> getAllVerses() {
      return this.bible.verses.parallelStream().filter(v -> v.book == this.getIdInt()).collect(Collectors.toList());
   }

   public Verse getVerse(int chapter, int verse, boolean safe) {
      Verse _return = this.bible
         .verses
         .parallelStream()
         .filter(v -> v.book == this.getIdInt() && v.chapter == chapter && v.verse == verse)
         .findFirst()
         .orElse(null);
      return _return == null && safe ? this.bible.getVerse(String.format("%02d%03d%03d", this.getIdInt(), chapter, verse), safe) : _return;
   }

   public List<Verse> getVerses(int chapter) {
      return this.bible.verses.parallelStream().filter(v -> v.book == this.getIdInt() && v.chapter == chapter).collect(Collectors.toList());
   }
}
