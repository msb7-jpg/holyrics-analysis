package com.limagiran.holyrics.swing;

import com.limagiran.bible.Verse;

public class ListModelVerse extends ListModelAddRemove<Verse> {
   public final void refresh() {
      this.fireContentsChanged(this, 0, this.getSize());
   }
}
