package com.limagiran.holyrics.swing;

import java.io.File;
import javax.swing.DropMode;
import javax.swing.JList;

public class ListDragAndDropFile {
   public static void setup(JList<File> jlist) {
      jlist.setTransferHandler(new ListItemTransferHandlerFile());
      jlist.setDropMode(DropMode.INSERT);
      jlist.setDragEnabled(true);
   }
}
