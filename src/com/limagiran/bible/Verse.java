package com.limagiran.bible;

public class Verse implements Comparable<Verse> {
   private transient String id;
   private transient Bible bible;
   public final int book;
   public final int chapter;
   public final int verse;
   public final String content;
   private static final String FORMAT_DESC = "%s %s:%s";

   protected Verse(int book, int chapter, int verse, String content) {
      this.chapter = chapter;
      this.verse = verse;
      this.content = content;
      this.book = book;
   }

   @Override
   public String toString() {
      return this.getSmallDesc() + " - " + this.content;
   }

   public String getId() {
      return this.id != null ? this.id : String.format("%02d%03d%03d", this.book, this.chapter, this.verse);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Verse)) {
         return super.equals(obj);
      } else {
         Verse v = (Verse)obj;
         return v.book == this.book && v.chapter == this.chapter && v.verse == this.verse;
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 67 * hash + this.book;
      hash = 67 * hash + this.chapter;
      return 67 * hash + this.verse;
   }

   protected void setBible(Bible bible) {
      this.bible = bible;
   }

   public String getSmallDesc() {
      return String.format("%s %s:%s", this.bible.books.get(this.book - 1).abbrev, this.chapter, this.verse);
   }

   public String getFullDesc() {
      return String.format("%s %s:%s", this.bible.books.get(this.book - 1).name, this.chapter, this.verse);
   }

   public String getVersion() {
      return this.bible.version;
   }

   public String getVersionAbbrev() {
      return this.bible.abbrev;
   }

   public Book getBook() {
      return this.bible.books.get(this.book - 1);
   }

   public boolean isOldTestament() {
      return this.book < 40;
   }

   public boolean isNewTestament() {
      return this.book >= 40;
   }

   public int compareTo(Verse v) {
      return this.book != v.book
         ? this.book - v.book
         : (this.chapter != v.chapter ? this.chapter - v.chapter : (this.verse != v.verse ? this.verse - v.verse : this.getVersion().compareTo(v.getVersion())));
   }
}
