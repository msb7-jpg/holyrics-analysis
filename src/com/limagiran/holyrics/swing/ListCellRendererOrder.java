package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.util.MusicUtils;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class ListCellRendererOrder extends AbstractWebListCellRendererWithSeparator implements ListCellRenderer<MusicController.Paragraph> {
   public ListCellRendererOrder() {
      super(false, true, false, false);
      this.init();
   }

   private void init() {
      this.view.setHorizontalAlignment(0);
      this.content.setBorder(new EmptyBorder(0, 0, 0, 5));
   }

   public Component getListCellRendererComponent(JList list, MusicController.Paragraph p, int index, boolean isSelected, boolean cellHasFocus) {
      String text = MusicUtils.getResume(String.valueOf(p), -1);
      return super.getListCellRendererComponent(list, text.isEmpty() ? " " : text, index, isSelected, cellHasFocus);
   }
}
