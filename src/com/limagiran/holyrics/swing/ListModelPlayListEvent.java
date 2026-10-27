package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Schedule;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public abstract class ListModelPlayListEvent extends DefaultListModel<Music> {
   private Schedule schedule;
   private boolean locked = false;

   public abstract void playlistChanged();

   private void changed() {
      this.save();
      this.playlistChanged();
   }

   public ListModelPlayListEvent(Event event) {
      this.addListDataListener(new ListDataListener() {
         @Override
         public void intervalAdded(ListDataEvent e) {
            ListModelPlayListEvent.this.changed();
         }

         @Override
         public void intervalRemoved(ListDataEvent e) {
            ListModelPlayListEvent.this.changed();
         }

         @Override
         public void contentsChanged(ListDataEvent e) {
            ListModelPlayListEvent.this.changed();
         }
      });
      this.schedule = event != null ? ScheduleController.get(event) : null;
      this.setSchedule(this.schedule);
   }

   public ListModelPlayListEvent() {
      this.addListDataListener(new ListDataListener() {
         @Override
         public void intervalAdded(ListDataEvent e) {
            ListModelPlayListEvent.this.changed();
         }

         @Override
         public void intervalRemoved(ListDataEvent e) {
            ListModelPlayListEvent.this.changed();
         }

         @Override
         public void contentsChanged(ListDataEvent e) {
            ListModelPlayListEvent.this.changed();
         }
      });
   }

   private void save() {
      if (!this.locked && this.schedule != null) {
         List<Music> list = new ArrayList<>();

         for (int i = 0; i < this.getSize(); i++) {
            list.add(this.getElementAt(i));
         }

         this.schedule.setMusics(list);
         ScheduleController.save();
      }
   }

   private void setSchedule(Schedule schedule) {
      this.schedule = schedule;
      this.locked = true;
      this.removeAllElements();
      if (this.schedule != null) {
         this.schedule.getMusics().stream().forEach(this::addElement);
      }

      this.locked = false;
   }

   public final void setEvent(Event event) {
      this.setSchedule(event != null ? ScheduleController.get(event) : null);
   }

   public Schedule getSchedule() {
      return this.schedule;
   }
}
