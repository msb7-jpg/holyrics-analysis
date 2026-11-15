package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.BackgroundImageFile;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererPresentationImage extends WebDecoratedImageWithLabelSelectableCustom implements ListCellRenderer<Presentation> {
   private final Dimension screenSize;

   public ListCellRendererPresentationImage(Dimension screenSize) {
      this.screenSize = screenSize;
      this.init();
   }

   private void init() {
      this.setOpaque(true);
   }

   public Component getListCellRendererComponent(JList list, Presentation p, int index, boolean isSelected, boolean cellHasFocus) {
      Dimension d = new Dimension(Integer.MAX_VALUE, list.getFixedCellHeight());
      d.width = (int)(d.getHeight() * this.screenSize.getWidth() / this.screenSize.getHeight());
      Utils.maximumSetup(d, new Dimension(d.width - 24, d.height - (p.theme.getBackground() instanceof BackgroundImageFile ? 24 : 48)));
      this.setSelected(isSelected);
      this.setImage(this.createImage(p, d));
      this.setLabel(p.text.getFormattingLabel());
      return this;
   }

   private BufferedImage createImage(Presentation p, Dimension d) {
      Theme t = p.theme;
      if (t.getBackground() instanceof BackgroundImageFile) {
         this.setPaintLabel(false);
         BackgroundImageFile bg = (BackgroundImageFile)t.getBackground();
         return bg.getThumbs(d);
      } else {
         this.setPaintLabel(true);
         return PresentationSlideUtils.createImage(null, p, d, true);
      }
   }
}
