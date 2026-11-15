package com.limagiran.holyrics.model;

import com.limagiran.holyrics.control.AlertController;
import com.limagiran.holyrics.util.Utils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Alert implements Serializable {
   private static final long serialVersionUID = 1L;
   public final long id = Utils.generateMillisecondId();
   private String text;
   private final List<AlertSchedule> schedules = new ArrayList<>();
   private long version = 1L;

   public Alert(String text) {
      this.setText(text);
   }

   public String getText() {
      return this.text;
   }

   public final void setText(String text) {
      this.text = Optional.ofNullable(text).orElse("");
   }

   public void addSchedule(AlertSchedule alertSchedule) {
      this.schedules.add(alertSchedule);
      this.schedules.sort(Comparator.naturalOrder());
   }

   public void removeSchedule(AlertSchedule alertSchedule) {
      this.schedules.remove(alertSchedule);
   }

   public List<AlertSchedule> getSchedules() {
      return new ArrayList<>(this.schedules);
   }

   public List<AlertSchedule> getSchedulesFiltered() {
      return new ArrayList<>(this.schedules).stream().filter(AlertSchedule::isEnabledAndToday).collect(Collectors.toList());
   }

   public void save() {
      AlertController.save();
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof Alert ? ((Alert)obj).id == this.id : super.equals(obj);
   }

   @Override
   public int hashCode() {
      return 413 + (int)(this.id ^ this.id >>> 32);
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
