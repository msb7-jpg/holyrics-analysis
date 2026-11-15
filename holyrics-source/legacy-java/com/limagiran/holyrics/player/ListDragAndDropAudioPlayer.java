package com.limagiran.holyrics.player;

import com.alee.laf.list.WebList;
import javax.swing.DropMode;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

public class ListDragAndDropAudioPlayer extends WebList {
   public ListDragAndDropAudioPlayer(ListModel listModel, ListCellRenderer lcr) {
      super(listModel);
      this.init(lcr);
   }

   public final void init(ListCellRenderer lcr) {
      this.setTransferHandler(new ListItemTransferHandlerAudioPlayer());
      this.setDropMode(DropMode.INSERT);
      this.setDragEnabled(true);
      this.setCellRenderer(lcr);
   }
}
