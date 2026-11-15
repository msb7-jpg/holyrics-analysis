package com.limagiran.holyrics.swing;

import com.limagiran.bible.Verse;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererFavoriteVerse extends AbstractWebListCellRendererWithSeparatorDoubleLine implements ListCellRenderer<Verse> {
   private static final String FORMAT_TOP = "<html><span style=\"font-size=85%%\">%s</span>";
   private static final String FORMAT_BOTTOM = "%s:%s";

   public ListCellRendererFavoriteVerse() {
      super(new AbstractWebListCellRendererWithSeparatorDoubleLine.Builder().setHorizontal(true));
      this.init();
   }

   private void init() {
      Arrays.asList(this.top, this.bottom).forEach(l -> l.setHorizontalAlignment(0));
      this.content.setLayout(new GridLayout(2, 1, 0, -5));
   }

   public Component getListCellRendererComponent(JList list, Verse v, int index, boolean isSelected, boolean cellHasFocus) {
      String t = String.format("<html><span style=\"font-size=85%%\">%s</span>", v.getBook().name);
      String b = String.format("%s:%s", v.chapter, v.verse);
      return super.getListCellRendererComponent(list, t, b, index, isSelected, cellHasFocus);
   }
}
