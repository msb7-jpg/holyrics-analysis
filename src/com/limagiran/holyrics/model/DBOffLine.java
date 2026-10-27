package com.limagiran.holyrics.model;

import com.google.gson.Gson;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.Security;
import com.limagiran.holyrics.util.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DBOffLine {
   public static final List<Music> LIST = new ArrayList<>(read("db-offline", false));
   public static final List<Music> LIST_TO_SEARCH = new ArrayList<>(read("db-offline", true));
   public static final List<Music> LIST_MOST_PLAYED = new ArrayList<>(read("db-offline-best", false));
   private final List<DBOffLine.LyricsJson> list;

   private static List<Music> read(String fileName, boolean toLowerCaseAndRemoveAccent) {
      List i;
      try {
         byte[] jsonZipSec = Dir.getBytesJar(String.format("/com/limagiran/holyrics/file/%s.hly", fileName));
         byte[] jsonZip = Security.decryptBytes(jsonZipSec, "#hly$k3y#");
         String json = Utils.decompress(jsonZip);
         if (toLowerCaseAndRemoveAccent) {
            json = normalize(Utils.removeAccent(json.toLowerCase()).replace("\"youtubeid\":\"", "\"youtubeId\":\"").replace("\\n", " "));
         }

         DBOffLine db = (DBOffLine)new Gson().fromJson(json, DBOffLine.class);

         for (int ix = 0; ix < db.list.size(); ix++) {
            if (db.list.get(ix).lyrics.length() <= 30) {
               db.list.remove(ix--);
            }
         }

         return Arrays.asList(
            db.list.stream().map(l -> Music.create().setTitle(l.title).setArtist(l.artist).setLanguage(l.language).setLyrics(l.lyrics)).toArray(Music[]::new)
         );
      } catch (Exception var10) {
         i = Arrays.asList();
      } finally {
         byte[] jsonZipSec = null;
         byte[] jsonZip = null;
         String json = null;
      }

      return i;
   }

   private static List<Music> readHCAndCC(String type, boolean toLowerCaseAndRemoveAccent) {
      List var6;
      try {
         byte[] jsonZipSec = Dir.getBytesJar(String.format("/com/limagiran/holyrics/file/%s.hly", type));
         byte[] jsonZip = Security.decryptBytes(jsonZipSec, "#hly$k3y#");
         String json = Utils.decompress(jsonZip);
         if (toLowerCaseAndRemoveAccent) {
            json = Utils.removeAccent(json.toLowerCase().replace("  ", " "));
            json = normalize(json.replace("\"youtubeid\":\"", "\"youtubeId\":\""));
         }

         DBOffLine db = (DBOffLine)new Gson().fromJson(json, DBOffLine.class);
         return Arrays.asList(
            db.list
               .stream()
               .map(
                  l -> Music.create()
                        .setTitle(toLowerCaseAndRemoveAccent ? l.title : Utils.firstLetterToUpperCase(l.title))
                        .setArtist(l.artist)
                        .setLanguage(l.language)
                        .setLyrics(l.lyrics)
                        .setNote(l.youtubeId)
               )
               .toArray(Music[]::new)
         );
      } catch (Exception var10) {
         var6 = Arrays.asList();
      } finally {
         byte[] jsonZipSec = null;
         byte[] jsonZip = null;
         String json = null;
      }

      return var6;
   }

   public static synchronized List<Music> search(MusicUtils.MusicFilter f) {
      List<Music> founds = new ArrayList<>();
      LIST_TO_SEARCH.forEach(
         m -> {
            if (f.getSearch().isEmpty()
               || f.isArtist() && stringSearch(m.getArtist(), f)
               || f.isTitle() && stringSearch(m.getTitle(), f)
               || f.isLyrics() && stringSearch(m.getLyrics(), f)
               || stringSearch(m.getNote(), f)) {
               founds.add(m);
            }
         }
      );
      List<Long> ids = founds.stream().map(Music::getId).collect(Collectors.toList());
      return LIST.parallelStream().filter(m -> ids.contains(m.getId())).collect(Collectors.toList());
   }

   private static boolean stringSearch(String val, MusicUtils.MusicFilter f) {
      return val.contains(f.getSearchContains()) || val.startsWith(f.getSearchStartsWith()) || val.endsWith(f.getSearchEndsWith());
   }

   private static String normalize(String value) {
      return value.replace("\",", "╚").replace("},", "╔").replaceAll("(,|\\.|\\?|!)", " ").replace("╚", "\",").replace("╔", "},").replace("  ", " ");
   }

   private DBOffLine(List<DBOffLine.LyricsJson> list) {
      this.list = list;
   }

   static {
      LIST.addAll(readHCAndCC("hc", false));
      LIST.addAll(readHCAndCC("cc", false));
      LIST_MOST_PLAYED.addAll(readHCAndCC("hc", false));
      LIST_MOST_PLAYED.addAll(readHCAndCC("cc", false));
      LIST_TO_SEARCH.addAll(readHCAndCC("hc", true));
      LIST_TO_SEARCH.addAll(readHCAndCC("cc", true));
      Arrays.asList(LIST, LIST_TO_SEARCH, LIST_MOST_PLAYED).forEach(l -> {
         for (int i = 0; i < l.size(); i++) {
            ((Music)l.get(i)).setId((long)(-i));
         }
      });
   }

   public static class LyricsJson {
      public final String title;
      public final String artist;
      public final String lyrics;
      public final String youtubeId;
      public final String language;

      public LyricsJson(String title, String artist, String lyrics, String youtubeId, String language) {
         this.title = title;
         this.artist = artist;
         this.lyrics = lyrics;
         this.youtubeId = youtubeId;
         this.language = language;
      }
   }
}
