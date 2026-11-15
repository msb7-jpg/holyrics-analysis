package com.limagiran.holyrics.control;

import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Schedule;
import com.limagiran.holyrics.model.Worship;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleController {
   private static boolean save = false;
   private static final String FILE_NAME = "schedule.hly";
   private static final List<Schedule> SCHEDULES = ControllerFactory.load("schedule.hly", Schedule.class);
   private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

   public static void reload() {
      List<Schedule> list = ControllerFactory.load("schedule.hly", Schedule.class);
      save = false;
      SCHEDULES.clear();
      SCHEDULES.addAll(list);
      save = true;
   }

   public static void checkSave() {
      if (save) {
         save = false;
         save();
      }
   }

   public static String dateToID(Calendar c) {
      return SDF.format(c.getTime());
   }

   public static Date idToDate(String id) {
      try {
         return SDF.parse(id);
      } catch (Exception var2) {
         return new Date();
      }
   }

   public static void add(Schedule s) {
      if (!SCHEDULES.contains(s)) {
         SCHEDULES.add(s);
      }

      save();
   }

   public static void remove(Schedule s) {
      SCHEDULES.remove(s);
      save();
   }

   public static void remove(Worship w) {
      List<Schedule> remove = SCHEDULES.stream().filter(s -> s.id.startsWith(String.valueOf(w.id))).collect(Collectors.toList());
      SCHEDULES.removeAll(remove);
      save();
   }

   public static Schedule get(Event e) {
      Schedule _return = SCHEDULES.stream().filter(s -> s.id.equalsIgnoreCase(e.id)).findFirst().orElse(null);
      if (_return == null) {
         _return = new Schedule(e.id, e.getDate(), "", null, new ArrayList<>(), new ArrayList<>());
         SCHEDULES.add(_return);
         save = true;
      }

      return _return;
   }

   public static List<Calendar> getNextSchedules(Music m) {
      if (m == null) {
         return new ArrayList<>();
      } else {
         Calendar now = Calendar.getInstance();
         return SCHEDULES.stream().filter(s -> s.getDate().after(now) && s.contains(m)).map(Schedule::getDate).sorted().collect(Collectors.toList());
      }
   }

   public static Calendar getNextSchedule(Music m) {
      if (m == null) {
         return null;
      } else {
         Calendar now = Calendar.getInstance();
         return SCHEDULES.stream().filter(s -> s.getDate().after(now) && s.contains(m)).map(Schedule::getDate).sorted().findFirst().orElse(null);
      }
   }

   public static List<Schedule> getAll() {
      return new ArrayList<>(SCHEDULES);
   }

   public static void change(Event e, Calendar newDate) {
      get(e).setDate(newDate);
      save();
   }

   public static synchronized void save() {
      sort();
      ControllerFactory.save(SCHEDULES, "schedule.hly");
   }

   public static void sort() {
      SCHEDULES.sort(Schedule.sort());
   }

   public static void save(List<Schedule> list) {
      SCHEDULES.clear();
      SCHEDULES.addAll(list.stream().distinct().collect(Collectors.toList()));
      save();
   }
}
