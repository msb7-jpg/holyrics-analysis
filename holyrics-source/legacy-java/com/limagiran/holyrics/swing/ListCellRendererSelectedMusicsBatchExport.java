package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.Music;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererSelectedMusicsBatchExport extends AbstractWebListCellRendererWithSeparatorDoubleLine implements ListCellRenderer<Music> {
   public ListCellRendererSelectedMusicsBatchExport() {
      super(new AbstractWebListCellRendererWithSeparatorDoubleLine.Builder().setAll(true).setRatioSecondLine(0.75));
      this.init();
   }

   private void init() {
      Arrays.asList(this.top, this.bottom).forEach(c -> {
         c.setHorizontalAlignment(0);
         c.setMargin(-2);
      });
      this.content.setLayout(new GridLayout(2, 1, 0, -8));
   }

   public Component getListCellRendererComponent(JList list, Music m, int index, boolean isSelected, boolean cellHasFocus) {
      return super.getListCellRendererComponent(list, m.getTitle(), m.getArtist(), index, isSelected, cellHasFocus);
   }
}
