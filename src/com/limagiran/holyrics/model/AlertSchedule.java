package com.limagiran.holyrics.model;

import com.limagiran.holyrics.util.Utils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AlertSchedule implements Serializable, Comparable<AlertSchedule> {
   private static final long serialVersionUID = 1L;
   public final long id = Utils.generateMillisecondId();
   private boolean enabled;
   private int hour;
   private int minute;
   private final Map<Integer, Boolean> days = new HashMap<>();
   private long version = 1L;

   public AlertSchedule(boolean enabled, int hour, int minute) {
      this.enabled = enabled;
      this.setHour(hour);
      this.setMinute(minute);
   }

   public void setDay(int dayOfWeek, boolean flag) {
      if (dayOfWeek >= 1 && dayOfWeek <= 7) {
         this.days.put(dayOfWeek, flag);
      }
   }

   public boolean isDay(int dayOfWeek) {
      return this.days.getOrDefault(dayOfWeek, false);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public int getHour() {
      return this.hour;
   }

   public final void setHour(int hour) {
      this.hour = Utils.range(hour, 0, 23);
   }

   public int getMinute() {
      return this.minute;
   }

   public final void setMinute(int minute) {
      this.minute = Utils.range(minute, 0, 59);
   }

   public int getTotalMinute() {
      return this.hour * 60 + this.minute;
   }

   public boolean isEnabledAndToday() {
      return this.enabled && this.isDay(Calendar.getInstance().get(7));
   }

   public String getFormatedTime() {
      return String.format("%02d:%02d", this.hour, this.minute);
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof AlertSchedule ? ((AlertSchedule)obj).id == this.id : super.equals(obj);
   }

   @Override
   public int hashCode() {
      return 427 + (int)(this.id ^ this.id >>> 32);
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }

   public int compareTo(AlertSchedule o) {
      return Integer.compare(this.getTotalMinute(), o.getTotalMinute());
   }
}
