package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.Music;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

public class ListDragAndDropMusic extends JList<Music> {
   public ListDragAndDropMusic(ListModel listModel, ListCellRenderer lcr) {
      super(listModel);
      this.init(lcr);
   }

   public final void init(ListCellRenderer lcr) {
      this.setTransferHandler(new ListItemTransferHandlerMusic());
      this.setDropMode(DropMode.INSERT);
      this.setDragEnabled(true);
      this.setCellRenderer(lcr);
   }
}
