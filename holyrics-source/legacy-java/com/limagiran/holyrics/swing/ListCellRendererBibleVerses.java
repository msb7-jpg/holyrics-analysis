package com.limagiran.holyrics.swing;

import com.limagiran.bible.Verse;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.view.BiblePresentationView;
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

public class ListCellRendererBibleVerses extends JPanel implements ListCellRenderer<Verse> {
   private static final Color SELECTED_COLOR = new Color(184, 207, 229);
   private static final Color DEFAULT_BACKGROUND = new Color(250, 250, 250, 200);
   private static final String FORMAT = "<html><body style='text-align:justify;font-family:%s;font-size:%s;text-justify: inter-word;width: %s;'><b>%s</b> - %s";
   private final JLabel label = new JLabel();
   private final Filler filler = new Filler(new Dimension(0, 1), new Dimension(0, 1), new Dimension(32767, 1));

   public ListCellRendererBibleVerses() {
      super(new BorderLayout(0, 2));
      this.init();
   }

   private void init() {
      this.label.setOpaque(true);
      this.setOpaque(false);
      this.setBorder(new EmptyBorder(2, 2, 0, 2));
      this.label.setBorder(new EmptyBorder(5, 4, 5, 2));
      this.filler.setOpaque(true);
      this.filler.setBackground(StyleUI.FOREGROUND_LIGHT);
      this.add(this.filler, "Last");
      this.add(this.label, "Center");
   }

   public Component getListCellRendererComponent(JList list, Verse verse, int index, boolean isSelected, boolean hasFocus) {
      try {
         this.label
            .setText(
               String.format(
                  "<html><body style='text-align:justify;font-family:%s;font-size:%s;text-justify: inter-word;width: %s;'><b>%s</b> - %s",
                  Settings.EnumSettings.BIBLE_FONT_NAME.getString(),
                  Settings.EnumSettings.BIBLE_FONT_SIZE.getInt(),
                  list.getParent().getWidth() - 10,
                  verse.verse,
                  verse.content
               )
            );
      } catch (Exception var7) {
      }

      this.label.setBackground(this.getBackground(verse, list, index, isSelected));
      this.filler.setVisible(index != list.getModel().getSize() - 1);
      return this;
   }

   private Color getBackground(Verse verse, JList list, int index, boolean isSelected) {
      try {
         if (BiblePresentationView.staticOnDisplay(verse)) {
            return StyleUI.CELL_RENDERER_TEXT_MODE;
         }

         if (isSelected || this.isSelected(list, index)) {
            return SELECTED_COLOR;
         }
      } catch (Exception var6) {
      }

      return DEFAULT_BACKGROUND;
   }

   private boolean isSelected(JList list, int index) {
      try {
         for (int i = 1; i < Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt(); i++) {
            if (list.getSelectedIndex() + i == index) {
               return true;
            }
         }
      } catch (Exception var4) {
      }

      return false;
   }
}
