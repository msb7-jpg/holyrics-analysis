package com.limagiran.holyrics.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateLG {
   public static final DateFormat SDF_DT_FS = SimpleDateFormat.getDateTimeInstance(0, 3, Locale.getDefault());
   public static final DateFormat SDF_T_S = SimpleDateFormat.getTimeInstance(3, Locale.getDefault());
   public static final DateFormat SDF_D_F = SimpleDateFormat.getDateInstance(0, Locale.getDefault());
   public static final DateFormat SDF_D_S = SimpleDateFormat.getDateInstance(3, Locale.getDefault());
   public static final DateFormat SDF_HH_MM = new SimpleDateFormat("HH:mm");

   public static LocalDateTime toLocalDateTime(Date date) {
      return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
   }

   public static LocalDateTime toLocalDateTime(Calendar date) {
      return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
   }

   public static LocalDateTime toLocalDateTime(Instant instant) {
      return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
   }

   public static LocalDate toLocalDate(Date date) {
      return toLocalDateTime(date).toLocalDate();
   }

   public static LocalDate toLocalDate(Calendar date) {
      return toLocalDateTime(date).toLocalDate();
   }

   public static LocalDate toLocalDate(Instant instant) {
      return toLocalDateTime(instant).toLocalDate();
   }

   public static Date toDate(LocalDate localDate) {
      return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
   }

   public static Date toDate(LocalDateTime localDateTime) {
      return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
   }

   public static boolean equalDays(Calendar d1, Calendar d2) {
      return d1.get(1) == d2.get(1) && d1.get(2) == d2.get(2) && d1.get(5) == d2.get(5);
   }

   public static boolean isToday(Calendar date) {
      return equalDays(date, Calendar.getInstance());
   }

   public static boolean isToday(Date date) {
      Calendar c = Calendar.getInstance();
      c.setTime(date);
      return equalDays(c, Calendar.getInstance());
   }

   public static boolean isWeekend(Calendar date) {
      return date.get(7) == 1 || date.get(7) == 7;
   }

   public static boolean isBetween(Calendar date, Calendar start, Calendar end) {
      return start.compareTo(end) <= 0 && !date.before(start) && !date.after(end);
   }
}
