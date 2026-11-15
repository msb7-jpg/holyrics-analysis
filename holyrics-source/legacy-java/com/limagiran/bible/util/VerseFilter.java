package com.limagiran.bible.util;

import com.limagiran.bible.Verse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class VerseFilter {
   private static final Integer[] MAX_RESULT_LIST = new Integer[]{10, 25, 50, 100};
   public static final Integer MAX_RESULT_LIST_DEFAULT_VALUE = 50;
   private String searchStartsWith;
   private String searchEndsWith;
   private String searchContains;
   private boolean anyWord;
   private boolean wholeWord;
   private boolean exactPhrase;
   private VerseFilter.EnumParams en;
   private final Map<String, Boolean> mapVersion = new HashMap<>();
   private int maxResults = MAX_RESULT_LIST_DEFAULT_VALUE;

   public VerseFilter() {
      this.setSearch("");
      this.anyWord = false;
      this.wholeWord = false;
      this.exactPhrase = false;
      this.en = VerseFilter.EnumParams.ALL_BIBLE;
   }

   public VerseFilter setSearch(String search) {
      this.searchStartsWith = search != null && !search.replace(" ", "").isEmpty() ? Utils.removeAccent(search.toLowerCase()) : "";
      this.searchContains = " " + this.searchStartsWith;
      this.searchEndsWith = this.searchContains;
      if (this.searchEndsWith.endsWith(" ")) {
         this.searchEndsWith = this.searchEndsWith.substring(0, this.searchEndsWith.length() - 1);
      }

      return this;
   }

   public String getSearch() {
      return this.searchStartsWith;
   }

   public String getSearchStartsWith(boolean checkWholeWord) {
      return this.searchStartsWith + (this.wholeWord && checkWholeWord ? " " : "");
   }

   public String getSearchEndsWith() {
      return this.searchEndsWith;
   }

   public String getSearchContains(boolean checkWholeWord) {
      return this.searchContains + (this.wholeWord && checkWholeWord ? " " : "");
   }

   public boolean isAnyWord() {
      return this.anyWord;
   }

   public VerseFilter setAnyWord(boolean anyWord) {
      this.anyWord = anyWord;
      return this;
   }

   public boolean isWholeWord() {
      return this.wholeWord;
   }

   public VerseFilter setWholeWord(boolean wholeWord) {
      this.wholeWord = wholeWord;
      return this;
   }

   public boolean isExactPhrase() {
      return this.exactPhrase;
   }

   public VerseFilter setExactPhrase(boolean exactPhrase) {
      this.exactPhrase = exactPhrase;
      return this;
   }

   public VerseFilter.EnumParams getEnum() {
      return this.en;
   }

   public VerseFilter setEnum(VerseFilter.EnumParams en) {
      this.en = en;
      return this;
   }

   public VerseFilter setVersion(String version, boolean value) {
      this.mapVersion.put(version, value);
      return this;
   }

   public boolean getVersion(String version) {
      return this.mapVersion.getOrDefault(version, true);
   }

   public int getMaxResults() {
      return this.maxResults;
   }

   public VerseFilter setMaxResults(int maxResults) {
      this.maxResults = maxResults;
      return this;
   }

   public List<Verse> getVersesToShow(List<Verse> list) {
      return list.parallelStream()
         .filter(v -> this.mapVersion.getOrDefault(v.getVersionAbbrev(), true))
         .filter(this.en.getFilter())
         .collect(Collectors.toList());
   }

   public static Integer[] getMaxResultsList() {
      return MAX_RESULT_LIST;
   }

   public static enum EnumParams {
      ALL_BIBLE,
      OLD_TESTAMENT,
      NEW_TESTAMENT,
      BOOK;

      private String id;

      public VerseFilter.EnumParams setValue(String id) {
         this.id = id;
         return this;
      }

      public Predicate<Verse> getFilter() {
         switch (this) {
            case OLD_TESTAMENT:
               return Verse::isOldTestament;
            case NEW_TESTAMENT:
               return Verse::isNewTestament;
            case BOOK:
               return v -> v.getBook().id.equals(this.id);
            default:
               return v -> true;
         }
      }
   }
}
