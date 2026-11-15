package com.limagiran.bible.swingmodel;

import com.limagiran.bible.Verse;
import javax.swing.DefaultListModel;

public class ListModelVerse extends DefaultListModel<Verse> {
   public void repaint() {
      super.fireContentsChanged(this, 0, this.getSize());
   }
}
