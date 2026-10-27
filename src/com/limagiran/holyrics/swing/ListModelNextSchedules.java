package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.model.Music;
import java.awt.Component;
import java.util.Calendar;
import java.util.Optional;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;

public class ListModelNextSchedules extends DefaultListModel<Calendar> {
   private JScrollPane panel;
   private Music music;

   public ListModelNextSchedules(Music music) {
      this.setMusic(music);
   }

   public final void setMusic(Music music) {
      this.music = music;
      this.fire();
   }

   public final void setPanel(JScrollPane panel) {
      this.panel = panel;
      this.panel.setVisible(!this.isEmpty());
   }

   public Music getMusic() {
      return this.music;
   }

   public void fire() {
      this.removeAllElements();
      Optional.ofNullable(ScheduleController.getNextSchedule(this.music)).ifPresent(this::addElement);
      Optional.ofNullable(this.panel).ifPresent(p -> {
         p.setVisible(!this.isEmpty());
         Optional.ofNullable(p.getParent()).ifPresent(Component::revalidate);
      });
      this.fireContentsChanged(this, 0, this.getSize());
   }
}
