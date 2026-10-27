package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.EventController;
import com.limagiran.holyrics.control.WorshipController;
import com.limagiran.holyrics.model.Event;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.DefaultComboBoxModel;

public class ComboBoxModelEvent extends DefaultComboBoxModel<Event> {
   private final Calendar c = Calendar.getInstance();

   public ComboBoxModelEvent() {
      this.c.set(this.c.get(1), this.c.get(2), 1, 0, 0, 0);
      this.update();
   }

   public final void update() {
      this.removeAllElements();
      List<Event> list = EventController.search(this.c.get(2), this.c.get(1));
      list.addAll(WorshipController.searchAsEvent(this.c.get(2), this.c.get(1), true));
      list.sort(Event.sort());
      list.forEach(this::addElement);
   }

   public void set(int month, int year) {
      this.c.set(2, month);
      this.c.set(1, year);
      this.update();
   }

   public void setMonth(int month) {
      this.set(month, this.c.get(1));
   }

   public void setYear(int year) {
      this.set(this.c.get(2), year);
   }

   public List<Event> getAllElements() {
      return IntStream.range(0, this.getSize()).mapToObj(this::getElementAt).collect(Collectors.toList());
   }
}
