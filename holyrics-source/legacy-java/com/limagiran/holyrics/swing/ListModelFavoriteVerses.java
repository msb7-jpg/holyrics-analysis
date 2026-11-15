package com.limagiran.holyrics.swing;

import com.limagiran.bible.Bible;
import com.limagiran.bible.Verse;
import com.limagiran.holyrics.settings.Settings;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ListModelFavoriteVerses extends DefaultListModel<Verse> {
   public ListModelFavoriteVerses(Bible bible) {
      this.init(bible);
   }

   private void init(Bible bible) {
      String playlist = Settings.EnumSettings2.BIBLE_PLAY_LIST.getString();
      if (!playlist.isEmpty()) {
         Arrays.stream(playlist.split(";")).map(id -> bible.getVerse(id, true)).distinct().forEach(this::addElement);
      }

      this.addListDataListener(
         new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
               this.save();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
               this.save();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
               this.save();
            }

            private void save() {
               String string = Arrays.stream(ListModelFavoriteVerses.this.toArray())
                  .filter(Verse.class::isInstance)
                  .map(Verse.class::cast)
                  .map(Verse::getId)
                  .collect(Collectors.joining(";"));
               Settings.EnumSettings2.BIBLE_PLAY_LIST.set(string);
            }
         }
      );
   }

   public void fireUpdate() {
      this.fireContentsChanged(this, 0, this.getSize());
   }
}
