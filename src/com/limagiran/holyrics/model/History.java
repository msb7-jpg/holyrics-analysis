package com.limagiran.holyrics.model;

import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Values;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History implements Serializable {
   private static final long serialVersionUID = 12491L;
   private static final Map<Long, List<Date>> HISTORIES = History.HistoryFactory.getInstance();
   private long version = 1L;

   public static synchronized Map<Long, List<Date>> getHistories() {
      return new HashMap<>(HISTORIES);
   }

   public static List<Date> getHistory(Music m) {
      if (HISTORIES.containsKey(m.getId())) {
         return HISTORIES.get(m.getId());
      } else {
         List<Date> list = new ArrayList<>();
         HISTORIES.put(m.getId(), list);
         return list;
      }
   }

   public static void addHistory(Music m, Date d) {
      List<Date> list = getHistory(m);
      list.add(d);
      save(m, list);
   }

   public static long minutesBetweenLastPlayed(Music m, Date d) {
      Date date = lastPlayed(m);
      return date == null ? Long.MAX_VALUE : (d.getTime() - date.getTime()) / 60000L;
   }

   public static Date lastPlayed(Music m) {
      List<Date> l = getHistory(m);
      return l.isEmpty() ? null : l.get(l.size() - 1);
   }

   public static boolean isPlayed(Music m, Date d) {
      List<Date> list = getHistory(m);
      return d != null
         ? list.stream().map(i -> (d.getTime() - i.getTime()) / 60000L).anyMatch(i -> i <= 30L && i >= -180L)
         : list.stream().anyMatch(DateLG::isToday);
   }

   public static int count(Music m) {
      if (m == null) {
         return 0;
      } else {
         List<Date> list = HISTORIES.get(m.getId());
         return list != null ? list.size() : 0;
      }
   }

   public static void save() {
      History.HistoryFactory.save(HISTORIES);
   }

   public static void save(Music music, List<Date> list) {
      list.sort(Comparator.naturalOrder());
      HISTORIES.put(music.getId(), list);
      History.HistoryFactory.save(HISTORIES);
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }

   private static class HistoryFactory {
      private static final File FILE = new File(Values.PATH_FILES + "history.hly");
      private static final String PASSWORD = "&p4ssS3sp";

      public static Map<Long, List<Date>> getInstance() {
         try {
            Map<Long, List<Date>> _return = RWObj.read(FILE, Map.class, "&p4ssS3sp");
            if (_return != null && _return.keySet().stream().anyMatch(Long.class::isInstance) && _return.values().stream().anyMatch(List.class::isInstance)) {
               return _return;
            }
         } catch (Exception var1) {
         }

         return new HashMap<>();
      }

      public static void save(Map<Long, List<Date>> map) {
         RWObj.write(map, FILE, "&p4ssS3sp");
      }

      static {
         if (FILE.getParentFile() != null) {
            FILE.getParentFile().mkdirs();
         }
      }
   }
}
