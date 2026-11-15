package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.MusicController;
import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

public class ListDragAndDropOrder extends JList<MusicController.Paragraph> {
   public ListDragAndDropOrder(ListModel listModel, ListCellRenderer lcr) {
      super(listModel);
      this.init(lcr);
   }

   public final void init(ListCellRenderer lcr) {
      this.setTransferHandler(new ListItemTransferHandlerOrder());
      this.setDropMode(DropMode.INSERT);
      this.setDragEnabled(true);
      this.setCellRenderer(lcr);
   }
}
