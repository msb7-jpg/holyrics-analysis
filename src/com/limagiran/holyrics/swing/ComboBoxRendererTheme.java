package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComboBoxRendererTheme implements ListCellRenderer<Theme> {
   private final Dimension d;

   public ComboBoxRendererTheme(int width, int height) {
      this.d = new Dimension(width, ++height);
   }

   public Component getListCellRendererComponent(JList list, final Theme theme, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel l = new JLabel() {
         @Override
         protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.drawImage(PresentationSlideUtils.createThumb(theme, ComboBoxRendererTheme.this.d), 0, 0, null);
            g2.setColor(Color.WHITE);
            g2.drawLine(0, ComboBoxRendererTheme.this.d.height - 1, ComboBoxRendererTheme.this.d.width, ComboBoxRendererTheme.this.d.height - 1);
            g2.dispose();
         }
      };
      l.setSize(this.d);
      l.setPreferredSize(this.d);
      return l;
   }
}
