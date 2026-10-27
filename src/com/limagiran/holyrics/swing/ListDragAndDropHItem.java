package com.limagiran.holyrics.swing;

import com.alee.laf.list.WebList;
import javax.swing.DropMode;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

public class ListDragAndDropHItem extends WebList {
   public ListDragAndDropHItem(ListModel listModel, ListCellRenderer lcr) {
      super(listModel);
      this.init(lcr);
   }

   public final void init(ListCellRenderer lcr) {
      this.setTransferHandler(new ListItemTransferHandlerHItem());
      this.setDropMode(DropMode.INSERT);
      this.setDragEnabled(true);
      this.setCellRenderer(lcr);
   }
}
