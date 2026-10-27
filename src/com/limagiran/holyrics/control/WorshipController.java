package com.limagiran.holyrics.control;

import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.model.Worship;
import com.limagiran.holyrics.util.ReflectionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public final class WorshipController {
   private static final String FILE_NAME = "worship.hly";
   private static final List<Worship> WORSHIP = ControllerFactory.load("worship.hly", Worship.class);

   public static boolean add(Worship w) {
      if (WORSHIP.contains(w)) {
         return false;
      } else {
         WORSHIP.add(w);
         save();
         return true;
      }
   }

   public static void remove(Worship w) {
      WORSHIP.remove(w);
      ScheduleController.remove(w);
      save();
   }

   public static List<Worship> getWorships(boolean onlyActive) {
      return new ArrayList<>(WORSHIP.stream().filter(w -> !onlyActive || w.isActive()).collect(Collectors.toList()));
   }

   public static List<Event> searchAsEvent(int month, int year, boolean onlyEnabled) {
      List<Event> _return = new ArrayList<>();
      WORSHIP.stream().filter(w -> !onlyEnabled || w.isActive()).forEach(w -> daysOfMonthAsEvent(w, month, year).forEach(_return::add));
      return _return;
   }

   public static List<Event> searchAsEventMonthAndPreviousAndNext(int month, int year, boolean onlyEnabled) {
      List<Worship> list = WORSHIP.stream().filter(w -> !onlyEnabled || w.isActive()).collect(Collectors.toList());
      Calendar c = Calendar.getInstance();
      c.set(year, month, 1);
      List<Event> _return = new ArrayList<>();
      c.add(2, -1);
      list.forEach(w -> daysOfMonthAsEvent(w, c.get(2), c.get(1)).forEach(_return::add));
      c.add(2, 1);
      list.forEach(w -> daysOfMonthAsEvent(w, c.get(2), c.get(1)).forEach(_return::add));
      c.add(2, 1);
      list.forEach(w -> daysOfMonthAsEvent(w, c.get(2), c.get(1)).forEach(_return::add));
      return _return;
   }

   public static void save() {
      sort();
      ControllerFactory.save(WORSHIP, "worship.hly");
   }

   public static void sort() {
      WORSHIP.sort(Worship.sort());
   }

   public static void save(List<Worship> list) {
      WORSHIP.clear();
      WORSHIP.addAll(list.stream().distinct().collect(Collectors.toList()));
      save();
   }

   public static List<Calendar> daysOfMonth(Worship w, int month, int year) {
      Calendar c = Calendar.getInstance();
      c.set(year, month, 1, w.hour, w.minute, 0);
      List<Calendar> days = getDaysOfMonth(w, c);
      switch (w.week) {
         case ALL:
            return days;
         case LAST:
            return Arrays.asList(days.get(days.size() - 1));
         default:
            return Arrays.asList(days.get(w.week.ordinal() - 1));
      }
   }

   private static List<Calendar> getDaysOfMonth(Worship w, Calendar c) {
      List<Calendar> _return = new ArrayList<>();

      for (int month = c.get(2); c.get(2) == month; c.add(5, 1)) {
         if (c.get(7) == w.day.ordinal() + 1) {
            Calendar add = Calendar.getInstance();
            add.setTime(c.getTime());
            _return.add(add);
         }
      }

      return _return;
   }

   public static List<Event> daysOfMonthAsEvent(Worship w, int month, int year) {
      return daysOfMonth(w, month, year).stream().map(c -> new Event(createId(w, c), w.desc, c, "")).collect(Collectors.toList());
   }

   public static String createId(Worship w, Calendar c) {
      return w.id + ScheduleController.dateToID(c);
   }

   public static void setTimeAndDesc(Worship w, Worship _newValues) {
      ReflectionUtils.setField(w, "hour", _newValues.hour);
      ReflectionUtils.setField(w, "minute", _newValues.minute);
      ReflectionUtils.setField(w, "desc", _newValues.desc);
      ScheduleController.getAll().stream().filter(s -> s.id.startsWith(String.valueOf(w.id))).forEach(s -> {
         Calendar c = s.getDate();
         c.set(11, w.hour);
         c.set(12, w.minute);
         s.setDate(c);
         ReflectionUtils.setField(s, "id", createId(w, c));
      });
      ScheduleController.save();
   }
}
