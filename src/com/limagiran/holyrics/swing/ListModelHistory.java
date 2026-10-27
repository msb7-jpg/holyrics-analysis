package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.History;
import com.limagiran.holyrics.model.Music;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ListModelHistory extends DefaultListModel<Date> {
   private Music music;
   private boolean locked = false;

   public ListModelHistory(Music music) {
      this.addListDataListener(new ListDataListener() {
         @Override
         public void intervalAdded(ListDataEvent e) {
            ListModelHistory.this.save();
         }

         @Override
         public void intervalRemoved(ListDataEvent e) {
            ListModelHistory.this.save();
         }

         @Override
         public void contentsChanged(ListDataEvent e) {
            ListModelHistory.this.save();
         }
      });
      this.setMusic(music);
   }

   private void save() {
      if (!this.locked) {
         List<Date> list = new ArrayList<>();

         for (int i = 0; i < this.getSize(); i++) {
            list.add(this.getElementAt(i));
         }

         History.save(this.music, list);
         this.fire();
      }
   }

   public final void setMusic(Music music) {
      this.music = music;
      this.fire();
   }

   public Music getMusic() {
      return this.music;
   }

   public void fire() {
      this.locked = true;
      this.removeAllElements();
      History.getHistory(this.music).stream().sorted(Comparator.reverseOrder()).forEach(this::addElement);
      this.locked = false;
   }
}
