package com.limagiran.holyrics.model;

import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.EnumWorship;
import com.limagiran.holyrics.util.Utils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;

public class Worship implements Serializable {
   private static final long serialVersionUID = 12491L;
   public final long id;
   public final EnumWorship.EnumWorshipDays day;
   public final EnumWorship.EnumWorshipWeeks week;
   public final int hour;
   public final int minute;
   public final String desc;
   private boolean active;
   private long version = 1L;

   public Worship(EnumWorship.EnumWorshipDays day, EnumWorship.EnumWorshipWeeks week, int hour, int minute, String desc) {
      this.id = Utils.generateMillisecondId();
      this.day = day;
      this.week = week;
      this.hour = hour;
      this.minute = minute;
      this.desc = desc == null ? "" : desc;
      this.active = true;
   }

   public String getTime() {
      Calendar c = Calendar.getInstance();
      c.set(2000, 0, 1, this.hour, this.minute, 0);
      return DateLG.SDF_T_S.format(c.getTime());
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   @Override
   public String toString() {
      String arg1 = !this.week.equals(EnumWorship.EnumWorshipWeeks.ALL) ? this.week.toString() + " " : "";
      String arg2 = this.day.toString();
      String arg3 = this.getTime();
      String arg4 = this.desc.isEmpty() ? "" : " (" + this.desc + ")";
      return String.format("%s%s - %s%s", arg1, arg2, arg3, arg4);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Worship)) {
         return super.equals(obj);
      } else {
         Worship w = (Worship)obj;
         return w.id == this.id || w.day.equals(this.day) && w.week.equals(this.week) && w.hour == this.hour && w.minute == this.minute;
      }
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 59 * hash + (int)(this.id ^ this.id >>> 32);
      hash = 59 * hash + Objects.hashCode(this.day);
      hash = 59 * hash + Objects.hashCode(this.week);
      hash = 59 * hash + this.hour;
      hash = 59 * hash + this.minute;
      return 59 * hash + Objects.hashCode(this.desc);
   }

   public static Comparator<Worship> sort() {
      return (o1, o2) -> {
         if (o1 == null) {
            return o2 == null ? 0 : -1;
         } else if (o2 == null) {
            return 1;
         } else if (!o1.week.equals(o2.week)) {
            return o1.week.ordinal() - o2.week.ordinal();
         } else if (!o1.day.equals(o2.day)) {
            return o1.day.ordinal() - o2.day.ordinal();
         } else {
            return o1.hour != o2.hour ? o1.hour - o2.hour : o1.minute - o2.minute;
         }
      };
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
