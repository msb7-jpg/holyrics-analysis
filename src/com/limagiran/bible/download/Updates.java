package com.limagiran.bible.download;

import com.google.gson.Gson;
import com.limagiran.bible.AvailableVersions;
import com.limagiran.bible.Bible;
import com.limagiran.bible.FactoryBible;
import com.limagiran.bible.util.Download;
import com.limagiran.bible.util.Log;
import com.limagiran.bible.util.Utils;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Updates {
   private static final String URL_CHECK_UPDATES = "http://www.holyrics.com/download/bible/updates.json";
   public final List<Updates.UpdateItem> list;

   public Updates(List<Updates.UpdateItem> list) {
      this.list = list;
   }

   public static void checkUpdates() {
      new Thread(() -> {
         Download d = Download.download("http://www.holyrics.com/download/bible/updates.json");
         byte[] bytes = d.getBytes();
         if (bytes != null && d.isOk()) {
            try {
               String json = Utils.decompress(bytes);
               Updates updates = (Updates)new Gson().fromJson(json, Updates.class);
               updateAll(updates.list);
            } catch (Exception var4) {
               Log.send(var4);
            }
         }
      }).start();
   }

   private static void updateAll(List<Updates.UpdateItem> updates) {
      AvailableVersions.getVersions()
         .forEach(
            version -> {
               try {
                  Bible bible = version.loadBible();
                  List<Updates.UpdateItem> list = updates.stream()
                     .filter(update -> update.abbrev.equals(bible.abbrev))
                     .filter(update -> update.update > bible.update)
                     .collect(Collectors.toList());
                  if (!list.isEmpty() && list.stream().allMatch(update -> updateVerse(update, bible))) {
                     long maxUpdate = list.stream().map(i -> i.update).max(Comparator.naturalOrder()).orElse(0L);
                     setField(bible, "update", maxUpdate);
                     FactoryBible.save(bible, version.pathBible);
                  }
               } catch (Exception var6) {
               }
            }
         );
   }

   private static boolean updateVerse(Updates.UpdateItem verse, Bible bible) {
      return setField(bible.getVerse(verse.id, false), "content", verse.content);
   }

   private static boolean setField(Object obj, String fieldName, Object newValue) {
      try {
         Field f = obj.getClass().getField(fieldName);
         f.setAccessible(true);
         f.set(obj, newValue);
         return true;
      } catch (Exception var4) {
         return false;
      }
   }

   public static class UpdateItem {
      public final long update;
      public final String abbrev;
      public final String id;
      public final String content;

      public UpdateItem(long update, String abbrev, String id, String content) {
         this.update = update;
         this.abbrev = abbrev;
         this.id = id;
         this.content = content;
      }
   }
}
