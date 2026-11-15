package com.limagiran.holyrics.dao;

import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.util.Log;
import com.limagiran.holyrics.util.SQLite;
import com.limagiran.holyrics.util.Utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MusicDao {
   private static final List<Music> ALL = new ArrayList<>();
   private static final List<Music> ALL_TO_SEARCH = new ArrayList<>();

   public static boolean add(Music m) {
      if (m.getId() > 0L && get(m.getId()) != null) {
         return true;
      } else {
         m.setId(Utils.generateMillisecondId());
         String sql = "INSERT INTO music VALUES (?,?)";

         try (
            Connection conn = SQLite.getConnection();
            PreparedStatement stm = conn.prepareStatement("INSERT INTO music VALUES (?,?)");
         ) {
            stm.setLong(1, m.getId());
            stm.setString(2, m.getSerial());
            stm.execute();
            if (ALL.contains(m)) {
               ALL.set(ALL.indexOf(m), m);
            } else {
               ALL.add(m);
            }

            if (ALL_TO_SEARCH.contains(m)) {
               ALL_TO_SEARCH.set(ALL_TO_SEARCH.indexOf(m), m.getInstanceToSearch());
            } else {
               ALL_TO_SEARCH.add(m.getInstanceToSearch());
            }

            return true;
         } catch (Exception var35) {
            Log.send(var35);
            return false;
         }
      }
   }

   public static boolean addKeepId(Music m) {
      if (m.getId() > 0L && get(m.getId()) == null) {
         String sql = "INSERT INTO music VALUES (?,?)";

         try (
            Connection conn = SQLite.getConnection();
            PreparedStatement stm = conn.prepareStatement("INSERT INTO music VALUES (?,?)");
         ) {
            stm.setLong(1, m.getId());
            stm.setString(2, m.getSerial());
            stm.execute();
            if (ALL.contains(m)) {
               ALL.set(ALL.indexOf(m), m);
            } else {
               ALL.add(m);
            }

            if (ALL_TO_SEARCH.contains(m)) {
               ALL_TO_SEARCH.set(ALL_TO_SEARCH.indexOf(m), m.getInstanceToSearch());
            } else {
               ALL_TO_SEARCH.add(m.getInstanceToSearch());
            }

            return true;
         } catch (Exception var35) {
            Log.send(var35);
            return false;
         }
      } else {
         return true;
      }
   }

   public static void add(List<Music> list) {
      List<Music> ms = new ArrayList<>(list);

      for (int i = 0; i < ms.size(); i++) {
         ms.get(i).setId((long)(-i));
      }

      int limit = 200;

      while (!ms.isEmpty()) {
         List<Music> batch = ms.subList(0, Math.min(ms.size(), limit));
         batchAdd(batch);
         ms.removeAll(batch);
      }

      updateAll();
   }

   private static void batchAdd(List<Music> list) {
      if (!list.isEmpty() && list.size() <= 200) {
         StringBuilder sb = new StringBuilder("INSERT INTO music VALUES (?, ?)");
         IntStream.range(1, list.size()).forEach(ix -> sb.append(", (?, ?)"));

         try (
            Connection conn = SQLite.getConnection();
            PreparedStatement stm = conn.prepareStatement(sb.toString());
         ) {
            for (int i = 0; i < list.size(); i++) {
               Music m = list.get(i).setId(Utils.generateMillisecondId());
               stm.setLong(i * 2 + 1, m.getId());
               stm.setString(i * 2 + 2, m.getSerial());
            }

            stm.execute();
         } catch (Exception var35) {
            Log.send(var35);
         }
      }
   }

   public static Music get(long id) {
      String sql = "SELECT * FROM music WHERE _id = ?";

      try (
         Connection conn = SQLite.getConnection();
         PreparedStatement stm = conn.prepareStatement("SELECT * FROM music WHERE _id = ?");
      ) {
         stm.setLong(1, id);

         Music var9;
         try (ResultSet rs = stm.executeQuery()) {
            var9 = rs.next() ? Music.create(rs.getString("music")) : null;
         }

         return var9;
      } catch (Exception var62) {
         Log.send(var62);
         return null;
      }
   }

   public static boolean set(Music m) {
      String sql = "UPDATE music SET music = ? WHERE _id = ?";

      try (
         Connection conn = SQLite.getConnection();
         PreparedStatement stm = conn.prepareStatement("UPDATE music SET music = ? WHERE _id = ?");
      ) {
         stm.setString(1, m.getSerial());
         stm.setLong(2, m.getId());
         stm.execute();
         if (ALL.contains(m)) {
            ALL.set(ALL.indexOf(m), m);
         }

         if (ALL_TO_SEARCH.contains(m)) {
            ALL_TO_SEARCH.set(ALL_TO_SEARCH.indexOf(m), m.getInstanceToSearch());
         }

         return true;
      } catch (Exception var35) {
         Log.send(var35);
         return false;
      }
   }

   public static boolean remove(Music m) {
      try (Connection conn = SQLite.getConnection()) {
         SQLite.setPragma(conn, true);
         String sql = "DELETE FROM music WHERE _id = ?";

         boolean var6;
         try (PreparedStatement stm = conn.prepareStatement("DELETE FROM music WHERE _id = ?")) {
            stm.setLong(1, m.getId());
            stm.execute();
            ALL.remove(m);
            ALL_TO_SEARCH.remove(m);
            var6 = true;
         }

         return var6;
      } catch (Exception var35) {
         Log.send(var35);
         return false;
      }
   }

   public static void remove(List<Music> list) {
      List<Music> ms = list.parallelStream().filter(m -> m.getId() > 0L).collect(Collectors.toList());
      int limit = 100;

      while (!ms.isEmpty()) {
         List<Music> batch = ms.subList(0, Math.min(ms.size(), limit));
         removeAll(batch);
         ms.removeAll(batch);
      }

      updateAll();
   }

   private static void removeAll(List<Music> list) {
      try (Connection conn = SQLite.getConnection()) {
         String array = list.stream().filter(Objects::nonNull).map(Music::getId).map(String::valueOf).collect(Collectors.joining(","));
         String sql = "DELETE FROM music WHERE _id IN (" + array + ")";
         SQLite.setPragma(conn, true);

         try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.execute();
            ALL.removeAll(list);
            ALL_TO_SEARCH.removeAll(list);
         }
      } catch (Exception var35) {
      }
   }

   public static List<Music> getAll() {
      return new ArrayList<>(ALL);
   }

   public static List<Music> getAllToSearch() {
      return new ArrayList<>(ALL_TO_SEARCH);
   }

   public static int getSize() {
      return ALL_TO_SEARCH.size();
   }

   public static synchronized Music getLazy(long id) {
      return ALL.parallelStream().filter(m -> m.getId() == id).findFirst().orElse(null);
   }

   public static synchronized List<Music> getLazy(List<Long> ids) {
      return ALL.parallelStream().filter(m -> ids.contains(m.getId())).collect(Collectors.toList());
   }

   public static synchronized Music getLazyToSearch(long id) {
      return ALL_TO_SEARCH.parallelStream().filter(m -> m.getId() == id).findFirst().orElse(null);
   }

   public static synchronized List<Music> getLazyToSearch(List<Long> ids) {
      return ALL_TO_SEARCH.parallelStream().filter(m -> ids.contains(m.getId())).collect(Collectors.toList());
   }

   public static void updateDatabase(List<Music> list) {
      List<Music> ms = new ArrayList<>(list.parallelStream().filter(m -> m.getId() > 0L).collect(Collectors.toList()));
      int limit = 200;

      while (!ms.isEmpty()) {
         List<Music> batch = ms.subList(0, Math.min(ms.size(), limit));
         batchUpdate(batch);
         ms.removeAll(batch);
      }

      updateAll();
   }

   private static void updateAll() {
      List<Music> result = new ArrayList<>();
      String sql = "SELECT * FROM music";

      try (
         Connection conn = SQLite.getConnection();
         PreparedStatement stm = conn.prepareStatement("SELECT * FROM music");
         ResultSet rs = stm.executeQuery();
      ) {
         while (rs.next()) {
            result.add(Music.create(rs.getString("music")));
         }
      } catch (Exception var60) {
         Log.send(var60);
         return;
      }

      ALL.clear();
      ALL.addAll(result);
      ALL_TO_SEARCH.clear();
      ALL_TO_SEARCH.addAll(ALL.parallelStream().map(Music::getInstanceToSearch).collect(Collectors.toList()));
   }

   private static void batchUpdate(List<Music> list) {
      if (!list.isEmpty() && list.size() <= 200) {
         StringBuilder sb = new StringBuilder("INSERT INTO music VALUES (?, ?)");
         IntStream.range(1, list.size()).forEach(ix -> sb.append(", (?, ?)"));

         try (
            Connection conn = SQLite.getConnection();
            PreparedStatement stm = conn.prepareStatement(sb.toString());
         ) {
            for (int i = 0; i < list.size(); i++) {
               stm.setLong(i * 2 + 1, list.get(i).getId());
               stm.setString(i * 2 + 2, list.get(i).getSerial());
            }

            stm.execute();
         } catch (Exception var34) {
            Log.send(var34);
         }
      }
   }

   public static synchronized boolean contains(String title, String artist) {
      String hash = Utils.removeAccent(title + ";" + artist).toLowerCase();
      return ALL_TO_SEARCH.parallelStream().map(m -> m.getTitle() + ";" + m.getArtist()).anyMatch(s -> s.equals(hash));
   }

   public static synchronized boolean contains(Music music) {
      Music ms = music.getInstanceToSearch();
      return contains(ms.getTitle(), ms.getArtist());
   }

   static {
      updateAll();
   }
}
