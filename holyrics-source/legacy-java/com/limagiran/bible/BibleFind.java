package com.limagiran.bible;

import com.limagiran.bible.util.Utils;
import com.limagiran.bible.util.VerseFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BibleFind {
   protected abstract Bible getBible();

   protected abstract Bible getBibleFind();

   public Book getBook(int book, boolean safe) {
      try {
         if (safe) {
            book = Utils.range(book, 1, 66);
         }

         return this.getBible().books.get(book - 1);
      } catch (Exception var4) {
         return null;
      }
   }

   public Verse getVerse(String id, boolean safe) {
      Verse _return = this.getBible().verses.parallelStream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
      if (_return == null && safe) {
         _return = this.getBible().verses.get(0);

         for (int i = this.getBible().verses.size() - 1; i >= 0; i--) {
            if (id.compareTo(this.getBible().verses.get(i).getId()) > 0) {
               _return = this.getBible().verses.get(i);
               break;
            }
         }
      }

      return _return;
   }

   public Verse nextVerse(Verse v) {
      int index = this.getBible().verses.indexOf(v);
      if (index != -1) {
         if (++index < this.getBible().verses.size()) {
            return this.getBible().verses.get(index);
         }
      }

      return v;
   }

   public Verse previousVerse(Verse v) {
      int index = this.getBible().verses.indexOf(v);
      if (index != -1) {
         if (--index >= 0) {
            return this.getBible().verses.get(index);
         }
      }

      return v;
   }

   public Verse getVerse(int book, int chapter, int verse, boolean safe) {
      try {
         if (safe) {
            book = Utils.range(book, 1, 66);
         }

         return this.getBook(book, safe).getVerse(chapter, verse, safe);
      } catch (Exception var6) {
         return null;
      }
   }

   public List<Verse> getVerses(int book, int chapter) {
      try {
         return this.getBook(book, false).getVerses(chapter);
      } catch (Exception var4) {
         return new ArrayList<>();
      }
   }

   public Book findBookByAbbrev(String abbrev) {
      return this.getBible().books.stream().filter(b -> b.abbrev.equalsIgnoreCase(abbrev)).findFirst().orElse(null);
   }

   public Book findBookByName(String name) {
      String nameF = Utils.removeAccent(name).replace(" ", "");
      Book book = this.getBibleFind().books.stream().filter(b -> b.name.replace(" ", "").equalsIgnoreCase(nameF)).findFirst().orElse(null);
      return book != null ? this.getBible().books.get(book.getIdInt() - 1) : null;
   }

   public List<Verse> find(VerseFilter filter) {
      if (filter.isExactPhrase()) {
         String valueFinal = Utils.removeExcessSpace(filter.getSearch());
         List<Verse> map = this.getBibleFind().verses.parallelStream().filter(v -> v.content.contains(valueFinal)).collect(Collectors.toList());
         return this.getBible().verses.parallelStream().filter(map::contains).collect(Collectors.toList());
      } else {
         List<VerseFilter> values = Arrays.asList(Utils.removeExcessSpace(filter.getSearch()).split(" "))
            .stream()
            .map(value -> new VerseFilter().setSearch(value))
            .collect(Collectors.toList());
         List<Verse> map = this.getBibleFind()
            .verses
            .parallelStream()
            .filter(this.filterAllOrAnyWords(values, filter.isAnyWord(), filter.isWholeWord()))
            .collect(Collectors.toList());
         return this.getBible().verses.parallelStream().filter(map::contains).collect(Collectors.toList());
      }
   }

   private Predicate<Verse> filterAllOrAnyWords(List<VerseFilter> values, boolean anyWord, boolean wholeWord) {
      return anyWord
         ? v -> values.stream().anyMatch(this.filterExactOrPartial(v, wholeWord))
         : v -> values.stream().allMatch(this.filterExactOrPartial(v, wholeWord));
   }

   private Predicate<VerseFilter> filterExactOrPartial(Verse verse, boolean wholeWord) {
      return !wholeWord
         ? f -> stringSearch(verse.content, f)
         : s -> Arrays.asList(verse.content.split(" "))
               .stream()
               .map(ss -> ss.replaceAll("[^\\w\\d-']", ""))
               .anyMatch(ss -> ss.matches("^[^\\w\\d]*\\Q" + s.getSearch() + "\\E[^\\w\\d]*$"));
   }

   private static boolean stringSearch(String val, VerseFilter f) {
      return val.contains(f.getSearchContains(false)) || val.startsWith(f.getSearchStartsWith(false)) || val.endsWith(f.getSearchEndsWith());
   }

   protected static Map<Integer, Map<Integer, Integer>> loadMap(Bible bible) {
      Map<Integer, Map<Integer, Integer>> _return = new HashMap<>();
      bible.verses.stream().forEach(v -> {
         Map<Integer, Integer> map = _return.getOrDefault(v.book, new HashMap<>());
         _return.put(v.book, map);
         map.put(v.chapter, map.getOrDefault(v.chapter, 0) + 1);
      });
      return _return;
   }

   protected static List<String> loadMapAsList(Map<Integer, Map<Integer, Integer>> map) {
      List<String> _return = new ArrayList<>();

      for (int i = 1; i <= 66; i++) {
         Map<Integer, Integer> _map = map.get(i);
         int maxChapter = _map.entrySet().stream().map(Entry::getKey).sorted(Comparator.reverseOrder()).findFirst().orElse(1);
         List<String> verses = new ArrayList<>();

         for (int chapter = 1; chapter <= maxChapter; chapter++) {
            verses.add(String.valueOf(_map.getOrDefault(chapter, 1)));
         }

         _return.add(String.join(";", verses));
      }

      return _return;
   }
}
