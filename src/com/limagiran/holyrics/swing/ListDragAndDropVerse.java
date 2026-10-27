package com.limagiran.holyrics.swing;

import com.limagiran.bible.Verse;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

public class ListDragAndDropVerse extends JList<Verse> {
   public ListDragAndDropVerse(ListModel listModel, ListCellRenderer lcr) {
      super(listModel);
      this.init(lcr);
   }

   public final void init(ListCellRenderer lcr) {
      this.setTransferHandler(new ListItemTransferHandlerVerse());
      this.setDropMode(DropMode.INSERT);
      this.setDragEnabled(true);
      this.setCellRenderer(lcr);
   }
}
