package com.limagiran.holyrics.view.panel;

import com.alee.laf.label.WebLabel;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.util.EnumSlideSizePreview;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class SimpleSlidePreview extends WebLabel {
   private static final Color COLOR_BACKGROUND = new Color(252, 252, 252);
   private static final Border BORDER = new LineBorder(Color.BLACK, 2);
   public final MusicController.Paragraph paragraph;
   private boolean selected;
   protected ImageIcon icSelected;
   protected ImageIcon icUnselected;
   private final Dimension size;

   public SimpleSlidePreview(MusicController.Paragraph paragraph, Dimension size) {
      this.paragraph = paragraph;
      this.size = size;
      this.init();
   }

   private void init() {
      this.updateSize();
      this.setOpaque(false);
      this.setFont(new Font("Arial", 0, 12));
      this.setIcon(this.icUnselected);
      this.setBackground(COLOR_BACKGROUND);
      this.setForeground(Color.BLACK);
      this.setOpaque(true);
      this.setBorder(BORDER);
   }

   public void updateSize() {
      this.icSelected = new ImageIcon(PresentationSlideUtils.createThumb(this.paragraph.getText(), EnumSlideSizePreview.THEME_SELECTED, this.size));
      this.icUnselected = new ImageIcon(PresentationSlideUtils.createThumb(this.paragraph.getText(), EnumSlideSizePreview.THEME_UNSELECTED, this.size));
      this.setSelected(this.selected);
   }

   public boolean isSelected() {
      return this.selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
      SwingUtilities.invokeLater(() -> this.setIcon(this.selected ? this.icSelected : this.icUnselected));
      this.repaint();
   }

   public void unselect() {
      this.setSelected(false);
   }

   public void select() {
      this.setSelected(true);
   }
}
