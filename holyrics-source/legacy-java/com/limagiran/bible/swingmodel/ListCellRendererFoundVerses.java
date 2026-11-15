package com.limagiran.bible.swingmodel;

import com.limagiran.bible.Verse;
import com.limagiran.bible.util.SwingUtils;
import com.limagiran.bible.util.VerseFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.Box.Filler;
import javax.swing.border.EmptyBorder;

public class ListCellRendererFoundVerses extends JPanel implements ListCellRenderer<Verse> {
   private static final Color SELECTED_COLOR = new Color(184, 207, 229);
   private static final String HTML = "<html><body style='text-align:justify;color:#505050;font-family:%s, serif;font-size:%s;text-justify: inter-word;width: %s;'><span style=\"font-size:87%%\"><b>(%s) %s</b></span> - <span style=\"color:black\">%s</span>";
   private static String fontName = "Georgia";
   private static int fontSize = 14;
   private final JLabel label = new JLabel();
   private final Filler filler = new Filler(new Dimension(0, 1), new Dimension(0, 1), new Dimension(32767, 1));
   private final VerseFilter vf;

   public ListCellRendererFoundVerses(VerseFilter vf) {
      super(new BorderLayout(0, 2));
      this.vf = vf;
      this.init();
   }

   private void init() {
      this.label.setOpaque(true);
      this.setOpaque(false);
      this.setBorder(new EmptyBorder(2, 2, 0, 2));
      this.label.setBorder(new EmptyBorder(5, 4, 5, 2));
      this.filler.setOpaque(true);
      this.filler.setBackground(new Color(48, 48, 48));
      this.add(this.filler, "Last");
      this.add(this.label, "Center");
   }

   public Component getListCellRendererComponent(JList list, Verse verse, int index, boolean isSelected, boolean hasFocus) {
      String content = SwingUtils.setupHighlight(verse.content, this.vf);
      String ab = verse.getVersionAbbrev();
      ab = ab.contains("_") ? ab.substring(ab.indexOf("_") + 1) : ab;
      this.label
         .setText(
            String.format(
               "<html><body style='text-align:justify;color:#505050;font-family:%s, serif;font-size:%s;text-justify: inter-word;width: %s;'><span style=\"font-size:87%%\"><b>(%s) %s</b></span> - <span style=\"color:black\">%s</span>",
               fontName,
               fontSize,
               list.getFixedCellWidth() - 8,
               ab.toUpperCase(),
               verse.getFullDesc(),
               content
            )
         );
      this.label.setBackground(isSelected ? SELECTED_COLOR : new Color(250, 250, 250, 200));
      this.filler.setVisible(index != list.getModel().getSize() - 1);
      return this;
   }

   public static void setFontName(String fontName) {
      ListCellRendererFoundVerses.fontName = fontName;
   }

   public static void setFontSize(int fontSize) {
      ListCellRendererFoundVerses.fontSize = fontSize;
   }
}
