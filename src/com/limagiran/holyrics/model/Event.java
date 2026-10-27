package com.limagiran.holyrics.model;

import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.Utils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;

public class Event implements Serializable {
   private static final long serialVersionUID = 12491L;
   public final String id;
   private String name;
   private long datetime;
   private String desc;
   private transient Calendar date;
   private long version = 1L;

   public Event(String id, String name, Calendar date, String desc) {
      this.id = id;
      this.name = name;
      date.set(13, 0);
      date.set(14, 0);
      this.datetime = date.getTimeInMillis();
      this.desc = desc;
   }

   public Event(Calendar date) {
      this(Long.toString(Utils.generateMillisecondId()), "", date, "");
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Calendar getDate() {
      if (this.date == null) {
         this.date = Calendar.getInstance();
         this.date.setTimeInMillis(this.datetime);
      }

      return this.date;
   }

   public final void setDate(Calendar date) {
      date.set(13, 0);
      date.set(14, 0);
      ScheduleController.change(this, date);
      this.date = date;
      this.datetime = this.date.getTimeInMillis();
   }

   public String getDesc() {
      return this.desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }

   @Override
   public String toString() {
      return String.format("%s - %s", this.name, DateLG.SDF_DT_FS.format(this.getDate().getTime()));
   }

   public static Comparator<? super Event> sort() {
      return (o1, o2) -> o1.getDate().compareTo(o2.getDate());
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof Event ? ((Event)o).id.equalsIgnoreCase(this.id) : super.equals(o);
   }

   @Override
   public int hashCode() {
      return 57 + Objects.hashCode(this.id);
   }

   public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
   }
}
