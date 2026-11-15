package com.limagiran.holyrics.swing;

import com.alee.laf.list.WebList;
import javax.swing.DropMode;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

public class ListDragAndDropString extends WebList {
   public ListDragAndDropString(ListModel<String> listModel, ListCellRenderer lcr) {
      super(listModel);
      this.init(lcr);
   }

   public final void init(ListCellRenderer<String> lcr) {
      this.setTransferHandler(new ListItemTransferHandlerString());
      this.setDropMode(DropMode.INSERT);
      this.setDragEnabled(true);
      this.setCellRenderer(lcr);
   }
}
