package com.limagiran.holyrics.view;

import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;

class JLabelTheme extends JLabel {
   static final Dimension SIZE = new Dimension(160, 90);
   public final Theme theme;

   public JLabelTheme(Theme theme) {
      this.theme = theme;
      super.setPreferredSize(SIZE);
      super.setSize(SIZE);
   }

   @Override
   protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      g2.drawImage(PresentationSlideUtils.createThumb(this.theme, SIZE), 0, 0, null);
   }
}
