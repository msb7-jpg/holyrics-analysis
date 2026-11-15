package com.limagiran.holyrics.util;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.History;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Profile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

public class MusicUtils {
   public static Music getDefault() {
      Music m = MusicDao.get(1L);
      return m != null ? m : Music.create();
   }

   public static boolean isCCorHC(Music m) {
      return m.getLanguage().equals("Cantor Cristão") || m.getLanguage().equals("Harpa Cristã");
   }

   public static synchronized List<Music> filter(List<Music> list, MusicUtils.MusicFilter f, boolean containsAccent) {
      List<Music> search = new ArrayList<>();
      if (containsAccent) {
         List<Long> ids = list.stream().map(Music::getId).collect(Collectors.toList());
         search.addAll(MusicDao.getLazyToSearch(ids));
      } else {
         search.addAll(list);
      }

      List<Music> founds = new ArrayList<>();
      search.forEach(
         m -> {
            if (f.profile == null || f.profile.containsMusic(m)) {
               if (!f.startWith.isEmpty()) {
                  if (f.isStartWithTitle() && !m.getTitle().startsWith(f.startWith)) {
                     return;
                  }

                  if (f.isStartWithArtist() && !m.getArtist().startsWith(f.startWith)) {
                     return;
                  }
               }

               if (f.searchStartsWith.isEmpty()
                  || f.artist && stringSearch(m.getArtist(), f)
                  || f.title && stringSearch(m.getTitle(), f)
                  || f.lyrics && stringSearch(m.getLyrics(), f)
                  || f.note && stringSearch(m.getNote(), f)) {
                  founds.add(m);
               }
            }
         }
      );
      List<Long> ids = founds.stream().map(Music::getId).collect(Collectors.toList());
      return MusicDao.getLazy(ids);
   }

   private static boolean stringSearch(String val, MusicUtils.MusicFilter f) {
      return val.contains(f.searchContains) || val.startsWith(f.searchStartsWith) || val.endsWith(f.searchEndsWith);
   }

   public static String getResume(String text, int size) {
      String p = text.replace("\n", " -- ");
      return size >= 0 && size < p.length() ? p.substring(0, size) + "..." : p;
   }

   public static String setupHighlight(String value, MusicUtils.MusicFilter mf) {
      if (!value.isEmpty() && mf != null && !mf.getSearch().isEmpty()) {
         String highlight = "";
         StringBuilder sb = new StringBuilder(value);
         if (stringToSearch(value).indexOf(mf.getSearchStartsWith()) == 0) {
            String var8 = sb.substring(0, mf.getSearchStartsWith().length());
            highlight = applyHighlight(var8);
            sb.replace(0, mf.getSearchStartsWith().length(), highlight);
         }

         int index = stringToSearch(sb.toString()).indexOf(mf.getSearchContains(), highlight.length());

         while (index >= 0) {
            int start = index + 1;
            int end = index + mf.getSearchContains().length();
            int size = sb.length();
            highlight = sb.substring(start, end);
            sb.replace(start, end, applyHighlight(highlight));
            index = Utils.removeAccent(sb.toString()).toLowerCase().indexOf(mf.getSearchContains(), end + (sb.length() - size));
         }

         if (stringToSearch(sb.toString()).endsWith(mf.getSearchEndsWith())) {
            highlight = sb.substring(sb.length() - mf.getSearchEndsWith().length(), sb.length());
            sb.replace(sb.length() - mf.getSearchEndsWith().length() + 1, sb.length(), applyHighlight(highlight));
         }

         return sb.toString();
      } else {
         return value;
      }
   }

   private static String stringToSearch(String value) {
      return Utils.removeAccent(value.replace("\n", " ")).toLowerCase();
   }

   public static void setupHighlight(JTextComponent text, MusicUtils.MusicFilter mf, Color c) {
      try {
         text.getHighlighter().removeAllHighlights();
         String textToSearch = stringToSearch(text.getDocument().getText(0, text.getDocument().getLength()));
         if (textToSearch.replace(" ", "").isEmpty() || mf == null || mf.getSearch().replace(" ", "").isEmpty()) {
            return;
         }

         DefaultHighlightPainter color = new DefaultHighlightPainter(c);
         if (textToSearch.indexOf(mf.getSearchStartsWith()) == 0) {
            text.getHighlighter().addHighlight(0, mf.getSearchStartsWith().length(), color);
         }

         for (int index = textToSearch.indexOf(mf.getSearchContains());
            index >= 0;
            index = textToSearch.indexOf(mf.getSearchContains(), index + mf.getSearchContains().length())
         ) {
            text.getHighlighter().addHighlight(index + 1, index + mf.getSearchContains().length(), color);
         }

         if (textToSearch.endsWith(mf.getSearchEndsWith())) {
            text.getHighlighter().addHighlight(textToSearch.length() - mf.getSearchEndsWith().length() + 1, textToSearch.length(), color);
         }
      } catch (Exception var6) {
      }
   }

   public static void setupHighlight(JTextComponent text, MusicUtils.MusicFilter mf) {
      setupHighlight(text, mf, StyleUI.COLOR_HIGHLIGHT_TEXT);
   }

   public static List<int[]> searchAndSetupHighlight(JTextComponent text, MusicUtils.MusicFilter mf, Color c) {
      List<int[]> _return = new ArrayList<>();

      try {
         text.getHighlighter().removeAllHighlights();
         String textToSearch = stringToSearch(text.getDocument().getText(0, text.getDocument().getLength()));
         if (textToSearch.replace(" ", "").isEmpty() || mf == null || mf.getSearch().replace(" ", "").isEmpty()) {
            return _return;
         }

         DefaultHighlightPainter color = new DefaultHighlightPainter(c);
         if (textToSearch.indexOf(mf.getSearchStartsWith()) == 0) {
            int[] add = new int[]{0, mf.getSearchStartsWith().length()};
            _return.add(add);
            text.getHighlighter().addHighlight(add[0], add[1], color);
         }

         for (int index = textToSearch.indexOf(mf.getSearchContains());
            index >= 0;
            index = textToSearch.indexOf(mf.getSearchContains(), index + mf.getSearchContains().length())
         ) {
            int[] add = new int[]{index + 1, index + mf.getSearchContains().length()};
            _return.add(add);
            text.getHighlighter().addHighlight(add[0], add[1], color);
         }

         if (textToSearch.endsWith(mf.getSearchEndsWith())) {
            int[] add = new int[]{textToSearch.length() - mf.getSearchEndsWith().length() + 1, textToSearch.length()};
            _return.add(add);
            text.getHighlighter().addHighlight(add[0], add[1], color);
         }
      } catch (Exception var8) {
      }

      return _return;
   }

   public static List<int[]> searchAndSetupHighlight(JTextComponent text, MusicUtils.MusicFilter mf) {
      return searchAndSetupHighlight(text, mf, StyleUI.COLOR_HIGHLIGHT_TEXT);
   }

   private static String applyHighlight(String value) {
      return "<span style=\"background-color:" + StyleUI.COLOR_HEX_HIGHLIGHT_TEXT + "\">" + value + "</span>";
   }

   public static MusicController.Paragraph createParagraph(String text) {
      MusicController.Paragraph p = new MusicController.Paragraph(text, "");

      try {
         if (text.startsWith("##")) {
            if (!text.contains("\n")) {
               text = text + "\n ";
            }

            p.setText(text.substring(text.indexOf("\n") + 1));
            p.setFormatting(text.substring(0, text.indexOf("\n")));
         }
      } catch (Exception var3) {
      }

      return p;
   }

   public static void setParagraph(Music music, int index, String value) {
      try {
         if (music == null) {
            return;
         }

         value = StringUtils.removeExcessLineBreak(value);
         if (value.isEmpty()) {
            value = " ";
         }

         List<Integer> order = music.getOrder();
         int realIndex = index;
         if (!order.isEmpty()) {
            realIndex = order.get(index);
         }

         StringBuilder lyrics = new StringBuilder(music.getLyrics());
         if (realIndex == 0) {
            int indexOf = lyrics.indexOf("\n\n");
            lyrics.replace(0, indexOf, value);
         } else {
            int indexOf = 0;

            for (int i = 0; i < realIndex; i++) {
               indexOf = lyrics.indexOf("\n\n", indexOf + 2);
               if (indexOf < 0) {
                  return;
               }
            }

            int indexOfEnd = lyrics.indexOf("\n\n", indexOf + 2);
            indexOfEnd = indexOfEnd >= 0 ? indexOfEnd : lyrics.length();
            lyrics.replace(indexOf + 2, indexOfEnd, value);
         }

         music.setLyrics(StringUtils.maxLength(lyrics.toString(), 12000, false));
      } catch (Exception var8) {
      }
   }

   public static enum EnumOrderBy {
      TITLE(I18N.WORD_TITLE.val(), MusicUtils.EnumOrderBy::sortTitle),
      ARTIST(I18N.WORD_ARTIST.val(), MusicUtils.EnumOrderBy::sortArtist),
      MOST_PLAYED(I18N.MSG_MOST_PLAYED.val(), MusicUtils.EnumOrderBy::sortMostPlayed),
      LATEST_PRESENTATIONS(I18N.MSG_LATEST_PRESENTATIONS.val(), MusicUtils.EnumOrderBy::sortLatestPresentations),
      CREATION_DATE(I18N.MSG_CREATION_DATE.val(), MusicUtils.EnumOrderBy::sortCreationDate);

      public final String string;
      public final Function<Boolean, Comparator<Music>> sort;

      private EnumOrderBy(String string, Function<Boolean, Comparator<Music>> sort) {
         this.string = string;
         this.sort = sort;
      }

      @Override
      public String toString() {
         return this.string;
      }

      public static int titleCompare(Music m1, Music m2, boolean inverse) {
         int _return = Utils.removeAccent(m1.getTitle()).compareToIgnoreCase(Utils.removeAccent(m2.getTitle()));
         return inverse ? -_return : _return;
      }

      public static int artistCompare(Music m1, Music m2, boolean inverse) {
         int _return = Utils.removeAccent(m1.getArtist()).compareToIgnoreCase(Utils.removeAccent(m2.getArtist()));
         return inverse ? -_return : _return;
      }

      public static int defaultSort(Music m1, Music m2, boolean inverse, Supplier<Integer> sort) {
         if (m1 == null) {
            return m2 == null ? 0 : -1;
         } else {
            int _return = sort.get();
            return m2 == null ? 1 : (inverse ? -_return : _return);
         }
      }

      public static Comparator<Music> sortTitle(boolean inverse) {
         return (m1, m2) -> defaultSort(m1, m2, inverse, () -> {
               int value = titleCompare(m1, m2, false);
               return value != 0 ? value : artistCompare(m1, m2, inverse);
            });
      }

      public static Comparator<Music> sortTitle() {
         return sortTitle(false);
      }

      public static Comparator<Music> sortArtist(boolean inverse) {
         return (m1, m2) -> defaultSort(m1, m2, inverse, () -> {
               int value = artistCompare(m1, m2, false);
               return value != 0 ? value : titleCompare(m1, m2, inverse);
            });
      }

      public static Comparator<Music> sortArtist() {
         return sortArtist(false);
      }

      public static Comparator<Music> sortMostPlayed(boolean inverse) {
         return (m1, m2) -> defaultSort(m1, m2, inverse, () -> {
               int m1Size = History.count(m1);
               int m2Size = History.count(m2);
               return m1Size != m2Size ? Integer.compare(m2Size, m1Size) : titleCompare(m1, m2, inverse);
            });
      }

      public static Comparator<Music> sortLatestPresentations(boolean inverse) {
         return (m1, m2) -> defaultSort(m1, m2, inverse, () -> {
               Date m1Date = History.lastPlayed(m1);
               Date m2Date = History.lastPlayed(m2);
               if (m2Date == null) {
                  return m1Date == null ? 0 : -1;
               } else if (m1Date == null) {
                  return 1;
               } else {
                  int value = m2Date.compareTo(m1Date);
                  return value != 0 ? value : titleCompare(m1, m2, inverse);
               }
            });
      }

      public static Comparator<Music> sortCreationDate(boolean inverse) {
         return (m1, m2) -> defaultSort(m1, m2, inverse, () -> Long.compare(m2.getId(), m1.getId()));
      }
   }

   public static class MusicFilter {
      public static final String REGEX_ACCENT = "(,|\\.|\\?|!)";
      private String searchStartsWith;
      private String searchEndsWith;
      private String searchContains;
      private boolean title;
      private boolean artist;
      private boolean lyrics;
      private boolean note;
      private Profile profile;
      private String startWith;
      private boolean startWithTitle;

      public MusicFilter() {
         this.clear();
      }

      public final void clear() {
         this.setSearch("");
         this.title = true;
         this.artist = true;
         this.lyrics = true;
         this.note = true;
         this.profile = null;
         this.startWith = "";
         this.startWithTitle = true;
      }

      public MusicUtils.MusicFilter setSearch(String search) {
         this.searchStartsWith = search != null && !search.replace(" ", "").isEmpty()
            ? Utils.removeAccent(search.toLowerCase().replaceAll("(,|\\.|\\?|!)", ""))
            : "";
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

      public String getSearchStartsWith() {
         return this.searchStartsWith;
      }

      public String getSearchEndsWith() {
         return this.searchEndsWith;
      }

      public String getSearchContains() {
         return this.searchContains;
      }

      public boolean isTitle() {
         return this.title;
      }

      public MusicUtils.MusicFilter setTitle(boolean title) {
         this.title = title;
         return this;
      }

      public boolean isArtist() {
         return this.artist;
      }

      public MusicUtils.MusicFilter setArtist(boolean artist) {
         this.artist = artist;
         return this;
      }

      public boolean isLyrics() {
         return this.lyrics;
      }

      public MusicUtils.MusicFilter setLyrics(boolean lyrics) {
         this.lyrics = lyrics;
         return this;
      }

      public boolean isNote() {
         return this.note;
      }

      public MusicUtils.MusicFilter setNote(boolean note) {
         this.note = note;
         return this;
      }

      public Profile getProfile() {
         return this.profile;
      }

      public MusicUtils.MusicFilter setProfile(Profile profile) {
         this.profile = profile;
         return this;
      }

      public MusicUtils.MusicFilter setStartWith(String startWith) {
         this.startWith = Optional.ofNullable(startWith).orElse("");
         return this;
      }

      public MusicUtils.MusicFilter toStartWithTitle() {
         this.startWithTitle = true;
         return this;
      }

      public MusicUtils.MusicFilter toStartWithArtist() {
         this.startWithTitle = false;
         return this;
      }

      public boolean isStartWithTitle() {
         return this.startWithTitle;
      }

      public boolean isStartWithArtist() {
         return !this.startWithTitle;
      }
   }
}
