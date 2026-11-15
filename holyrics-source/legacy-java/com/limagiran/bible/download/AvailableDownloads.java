package com.limagiran.bible.download;

import com.limagiran.bible.util.Values;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AvailableDownloads {
   private static final String URL = "http://www.holyrics.com/download/bible/version/?.bib";
   private final List<AvailableDownloads.AvailableDownloadsItem> list;

   public AvailableDownloads(List<AvailableDownloads.AvailableDownloadsItem> list) {
      this.list = list;
   }

   public List<AvailableDownloads.AvailableDownloadsItem> getList() {
      return new ArrayList<>(this.list);
   }

   public static class AvailableDownloadsItem {
      public final String name;
      public final String abbrev;
      public final String language;
      public final String hash;

      protected AvailableDownloadsItem(String name, String abbrev, String language, String hash) {
         this.name = name;
         this.abbrev = abbrev;
         this.language = language;
         this.hash = hash;
      }

      public String getUrlDownload() {
         return "http://www.holyrics.com/download/bible/version/?.bib".replace("?", this.abbrev);
      }

      public File getPath() {
         return new File(Values.PATH_DATABASE + this.abbrev + ".bib");
      }
   }
}
