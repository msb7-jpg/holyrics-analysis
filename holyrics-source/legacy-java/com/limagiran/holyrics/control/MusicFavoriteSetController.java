package com.limagiran.holyrics.control;

import com.limagiran.holyrics.model.theme.Theme;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;

public class MusicFavoriteSetController {
   private static final String FILE_NAME = "musicFavoriteSettings.hly";
   private static final Map<Long, MusicFavoriteSetController.Set> MAP = ControllerMapFactory.load(
      "musicFavoriteSettings.hly", Long.class, MusicFavoriteSetController.Set.class
   );
   private static final Object MAP_LOCK = new Object();
   private static boolean save;

   public static void setDefaultTheme(long music, long theme) {
      MusicFavoriteSetController.Set set;
      synchronized (MAP_LOCK) {
         set = MAP.get(music);
         if (set == null) {
            set = new MusicFavoriteSetController.Set();
            MAP.put(music, set);
         }
      }

      set.setTheme(theme);
      save = true;
   }

   public static void removeDefaultTheme(long music) {
      setDefaultTheme(music, -1L);
   }

   public static Theme getDefaultTheme(long music) {
      MusicFavoriteSetController.Set set;
      synchronized (MAP_LOCK) {
         set = MAP.get(music);
      }

      if (set != null && set.theme > 0L) {
         Theme theme = ThemeController.getOrDefault(set.theme, null);
         if (theme == null) {
            synchronized (MAP_LOCK) {
               MAP.remove(music);
               save = true;
            }
         }

         return theme;
      } else {
         return null;
      }
   }

   public static void checkSave() {
      if (save) {
         save = false;
         save();
      }
   }

   private static void save() {
      synchronized (MAP_LOCK) {
         ControllerMapFactory.save(MAP, "musicFavoriteSettings.hly");
      }
   }

   public static class Set implements Serializable {
      private static final long serialVersionUID = 1L;
      private long theme;
      private long version = 1L;

      public Set(long theme) {
         this.theme = theme;
      }

      public Set() {
         this(-1L);
      }

      public long getTheme() {
         return this.theme;
      }

      public void setTheme(long theme) {
         this.theme = theme;
      }

      private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
         ois.defaultReadObject();
      }
   }
}
