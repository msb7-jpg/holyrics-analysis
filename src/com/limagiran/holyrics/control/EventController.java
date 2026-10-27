package com.limagiran.holyrics.control;

import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.util.DateLG;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class EventController {
   private static final String FILE_NAME = "events.hly";
   private static final List<Event> EVENTS = ControllerFactory.load("events.hly", Event.class);

   public static void add(Event e) {
      if (!EVENTS.contains(e)) {
         EVENTS.add(e);
         save();
      }
   }

   public static void remove(Event e) {
      EVENTS.remove(e);
      ScheduleController.remove(ScheduleController.get(e));
      save();
   }

   public static List<Event> getAll() {
      return new ArrayList<>(EVENTS);
   }

   public static List<Event> search(int month, int year) {
      return EVENTS.stream().filter(e -> e.getDate().get(2) == month && e.getDate().get(1) == year).collect(Collectors.toList());
   }

   public static List<Event> search3Months(int month, int year) {
      Calendar start = Calendar.getInstance();
      Calendar end = Calendar.getInstance();
      start.set(year, month, 1);
      end.set(year, month, 1);
      start.add(2, -1);
      end.add(2, 1);
      return searchPeriod(start, end);
   }

   public static List<Event> searchPeriod(Calendar start, Calendar end) {
      return EVENTS.stream().filter(e -> DateLG.isBetween(e.getDate(), start, end)).collect(Collectors.toList());
   }

   public static List<Event> searchByDay(List<Event> events, Calendar date) {
      return events.stream()
         .filter(e -> e.getDate().get(5) == date.get(5) && e.getDate().get(2) == date.get(2) && e.getDate().get(1) == date.get(1))
         .collect(Collectors.toList());
   }

   public static void save() {
      ControllerFactory.save(EVENTS, "events.hly");
   }

   public static void save(List<Event> list) {
      EVENTS.clear();
      EVENTS.addAll(list.stream().distinct().collect(Collectors.toList()));
      save();
   }
}
