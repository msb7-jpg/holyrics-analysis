package com.limagiran.holyrics.util;

import com.limagiran.bible.Verse;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum EnumVerseDescription {
   TOP_SLIDE_DARKENED(I18N.ENUM_VERSE_DESC_TOP_BLACK.val()),
   TOP_SLIDE_ORIGINAL(I18N.ENUM_VERSE_DESC_TOP.val()),
   BEGIN(I18N.ENUM_VERSE_DESC_BEGIN.val()),
   ABOVE(I18N.ENUM_VERSE_DESC_ABOVE.val()),
   END(I18N.ENUM_VERSE_DESC_END.val()),
   BELOW(I18N.ENUM_VERSE_DESC_BELOW.val());

   public final String toString;

   private EnumVerseDescription(String toString) {
      this.toString = toString;
   }

   @Override
   public String toString() {
      return this.toString;
   }

   public static EnumVerseDescription valueOfOrDefault(String name) {
      try {
         return valueOf(name);
      } catch (Exception var2) {
         return TOP_SLIDE_DARKENED;
      }
   }

   public static String getDesc(Verse v, boolean full) {
      return full ? v.getFullDesc() : v.getSmallDesc();
   }

   public static String getDesc(Verse v) {
      return getDesc(v, Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION_FULL.is());
   }

   public String getDesc(List<Verse> l, boolean full) {
      String separator = "    ";
      if (l.isEmpty()) {
         return "";
      } else if (l.size() == 1) {
         return getDesc(l.get(0), full);
      } else {
         Function<Verse, String> book = v -> full ? v.getBook().name : v.getBook().abbrev;
         BiFunction<Verse, Verse, String> reduce = (v1, v2) -> String.format("%s %s:%s-%s", book.apply(v1), v1.chapter, v1.verse, v2.verse);
         if (l.stream().map(v -> v.book).distinct().count() == 1L && l.stream().map(v -> v.chapter).distinct().count() == 1L) {
            return reduce.apply(l.get(0), l.get(l.size() - 1));
         } else {
            BiFunction<Verse, Verse, Boolean> equals = (v1, v2) -> v1.book == v2.book && v1.chapter == v2.chapter;
            if (equals.apply(l.get(0), l.get(1))) {
               return reduce.apply(l.get(0), l.get(1)) + (l.size() > 2 ? "    " + getDesc(l.get(2), full) : "");
            } else {
               return l.size() > 2 && equals.apply(l.get(1), l.get(2))
                  ? getDesc(l.get(0), full) + "    " + reduce.apply(l.get(1), l.get(2))
                  : l.stream().map(v -> getDesc(v, full)).collect(Collectors.joining("    "));
            }
         }
      }
   }

   public String getDesc(List<Verse> l) {
      return this.getDesc(l, Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION_FULL.is());
   }

   public Function<Verse, String> getContentFunction(boolean upperCase, boolean manyVerses) {
      return v -> {
         String content = upperCase ? v.content.toUpperCase() : v.content;
         switch (this) {
            case BEGIN:
               return getDesc(v) + " - " + content;
            case ABOVE:
               return getDesc(v) + " \n" + content;
            case END:
               return content + "   " + getDesc(v).replace(" ", " ");
            case BELOW:
               return content + " \n" + getDesc(v);
            default:
               return content;
         }
      };
   }

   public String getHeader(List<Verse> vs, boolean showVersion) {
      String version = showVersion ? vs.get(0).getVersion() : "";
      switch (this) {
         case TOP_SLIDE_DARKENED:
         case TOP_SLIDE_ORIGINAL:
            return this.getDesc(vs) + (version.isEmpty() ? "" : "  -  ") + version;
         default:
            return version;
      }
   }

   public boolean isDarkened() {
      return this == TOP_SLIDE_DARKENED;
   }

   public boolean isTop() {
      switch (this) {
         case TOP_SLIDE_DARKENED:
         case TOP_SLIDE_ORIGINAL:
            return true;
         default:
            return false;
      }
   }
}
