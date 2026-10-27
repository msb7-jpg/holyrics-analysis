package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.History;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Date;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererPlayList extends AbstractWebListCellRendererWithSeparatorDoubleLine implements ListCellRenderer<Music> {
   private static final BufferedImage ICON = Img.opacity(Images.load("check36.png"), 0.45F);
   private Date date = null;

   public ListCellRendererPlayList() {
      super(new AbstractWebListCellRendererWithSeparatorDoubleLine.Builder().setVertical(true).setRatioSecondLine(0.75));
      this.init();
   }

   private void init() {
      Arrays.asList(this.top, this.bottom).forEach(c -> {
         c.setHorizontalAlignment(0);
         c.setMargin(-2);
      });
      this.content.setLayout(new GridLayout(2, 1, 0, -8));
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public Component getListCellRendererComponent(JList list, Music m, int index, boolean isSelected, boolean cellHasFocus) {
      this.setIcon(History.isPlayed(m, this.date) ? ICON : null);
      return super.getListCellRendererComponent(list, m.getTitle(), m.getArtist(), index, isSelected, cellHasFocus);
   }
}
