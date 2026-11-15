package com.limagiran.holyrics.control;

import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.favorite.FavoriteItem;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FavoriteController {
   private static final String FILE_NAME = "favorites.hly";
   private static final List<FavoriteItem> FAVORITES = ControllerFactory.load("favorites.hly", FavoriteItem.class);

   public static synchronized void add(FavoriteItem f, boolean start) {
      if (!FAVORITES.contains(f)) {
         IntStream map = FAVORITES.stream().mapToInt(FavoriteItem::getPosition);
         f.setPosition(start ? map.min().orElse(0) - 1 : map.max().orElse(0) + 1);
         FAVORITES.add(f);
         save();
      }
   }

   public static synchronized void remove(FavoriteItem f) {
      FAVORITES.remove(f);
      save();
   }

   public static synchronized void clear() {
      FAVORITES.clear();
      save();
   }

   public static synchronized List<FavoriteItem> getAll() {
      return new ArrayList<>(FAVORITES);
   }

   public static void save() {
      ControllerFactory.save(FAVORITES, "favorites.hly");
      ControllerListener.fireChangeListeners(FavoriteController.class);
   }

   public static synchronized FavoriteItem get(String id) {
      for (int i = 0; i < FAVORITES.size(); i++) {
         if (FAVORITES.get(i).getFavorite().getId().equals(id)) {
            return FAVORITES.get(i);
         }
      }

      return null;
   }

   public static synchronized boolean contains(String id) {
      for (int i = 0; i < FAVORITES.size(); i++) {
         if (FAVORITES.get(i).getFavorite().getId().equals(id)) {
            return true;
         }
      }

      return false;
   }

   public static synchronized boolean contains(Favoritable item) {
      return contains(item.getId());
   }
}
