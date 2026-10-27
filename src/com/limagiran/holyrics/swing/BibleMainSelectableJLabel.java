package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.G2Utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

public class BibleMainSelectableJLabel extends JLabel {
   private static final Color FOREGROUND = new Color(250, 250, 250, 230);
   private boolean paintedFooter;
   private boolean selected;
   private Color background;
   private Font footerFont = new Font(this.getFont().getName(), 0, 10);
   public final String footer;
   private final int borderOut = 0;
   private final int borderIn = 1;
   private final int fill = 2;
   public final AbsoluteConstraints ac = new AbsoluteConstraints(0, 0, -1, -1);

   private BibleMainSelectableJLabel(String text) {
      this(text, "");
   }

   private BibleMainSelectableJLabel(String text, String footer) {
      super(text, 0);
      this.footer = footer;
   }

   public static BibleMainSelectableJLabel createDefault(String text, String text2) {
      BibleMainSelectableJLabel sl = new BibleMainSelectableJLabel(text, text2);
      sl.setFont(new Font("Trebuchet MS", 1, 30));
      sl.setForeground(new Color(250, 250, 250, 230));
      sl.setOpaque(true);
      return sl;
   }

   public static BibleMainSelectableJLabel createDefault(String text) {
      return createDefault(text, "");
   }

   public void setSelected(boolean selected) {
      if (this.selected != selected) {
         super.setBackground(selected ? this.background.darker().darker().darker() : this.background);
      }

      this.selected = selected;
   }

   public void setFontSize(int size) {
      this.setFont(new Font(this.getFont().getName(), this.getFont().getStyle(), size));
   }

   public void setFooterFontSize(int size) {
      this.footerFont = new Font(this.footerFont.getFontName(), this.footerFont.getStyle(), size);
   }

   public Font getFooterFont() {
      return this.footerFont;
   }

   @Override
   public void setBackground(Color c) {
      this.background = c;
      super.setBackground(c);
   }

   public Color getOriginalBackground() {
      return this.background;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public boolean isPaintedFooter() {
      return this.paintedFooter;
   }

   @Override
   public int getWidth() {
      return this.ac.width != -1 ? this.ac.width : super.getWidth();
   }

   @Override
   public int getHeight() {
      return this.ac.height != -1 ? this.ac.height : super.getHeight();
   }

   public static String getSelectedText(List<BibleMainSelectableJLabel> list, String _default) {
      return list.stream().filter(s -> s.isSelected()).map(s -> s.getText()).findFirst().orElse(_default);
   }

   @Override
   protected void paintComponent(Graphics g) {
      if (this.selected) {
         this.paintSelected((Graphics2D)g);
      } else {
         this.paintUnselected((Graphics2D)g);
      }
   }

   private void paintUnselected(Graphics2D g) {
      G2Utils.setupGraphics(g);
      g.clearRect(0, 0, this.getWidth(), this.getHeight());
      this.paintBorders(g, this.background.brighter(), this.background.darker());
      g.setPaint(new GradientPaint((float)(this.getWidth() / 2), 0.0F, this.background, (float)(this.getWidth() / 2), (float)this.getHeight(), this.background));
      g.fillRect(2, 2, this.getWidth() - 4, this.getHeight() - 4);
      g.setColor(FOREGROUND);
      this.drawText(g);
      g.dispose();
   }

   private void paintSelected(Graphics2D g) {
      G2Utils.setupGraphics(g);
      g.clearRect(0, 0, this.getWidth(), this.getHeight());
      this.paintBorders(g, this.background.darker(), this.background.darker().darker());
      g.setPaint(
         new GradientPaint(
            (float)(this.getWidth() / 2),
            0.0F,
            this.background.darker().darker().darker().darker(),
            (float)(this.getWidth() / 2),
            (float)this.getHeight(),
            this.background.darker().darker()
         )
      );
      g.fillRect(2, 2, this.getWidth() - 4, this.getHeight() - 4);
      g.setColor(FOREGROUND);
      this.drawText(g);
      g.dispose();
   }

   private void drawText(Graphics2D g) {
      Font f = g.getFont();
      this.paintedFooter = false;
      if (!this.footer.isEmpty()) {
         g.setFont(this.footerFont);
         int w = g.getFontMetrics().stringWidth(this.footer);
         this.paintedFooter = w <= this.getWidth() - 6;
         BooleanOptional.runIfTrue(this.paintedFooter, () -> g.drawString(this.footer, (this.getWidth() - w) / 2, this.getHeight() - 6));
      }

      g.setFont(f);
      if (this.paintedFooter) {
         Dimension d = this.getSize();
         d.height -= 8;
         G2Utils.drawString(d, g, 0.0, G2Utils.VerticalAlign.MIDDLE, this.getText());
      } else {
         G2Utils.drawString(this.getSize(), g, 0.0, G2Utils.VerticalAlign.MIDDLE, this.getText());
      }
   }

   private void paintBorders(Graphics2D g, Color c1, Color c2) {
      g.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 150));
      g.fillRect(0, 0, this.getWidth() - 0, this.getHeight() - 0);
      g.setColor(c2);
      g.fillRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
   }

   public static void adjustFontOfAllJLabelFooter(JPanel panel) {
      List<BibleMainSelectableJLabel> labels = Arrays.asList(panel.getComponents())
         .stream()
         .map(o -> o instanceof BibleMainSelectableJLabel ? (BibleMainSelectableJLabel)o : null)
         .filter(Objects::nonNull)
         .collect(Collectors.toList());
      BibleMainSelectableJLabel l = labels.stream()
         .sorted((l1, l2) -> l2.getFontMetrics(l2.footerFont).stringWidth(l2.footer) - l1.getFontMetrics(l1.footerFont).stringWidth(l1.footer))
         .findFirst()
         .orElse(null);

      try {
         if (l != null) {
            int size = 14;
            Font f = new Font(l.footerFont.getName(), l.footerFont.getStyle(), size);

            while (l.getFontMetrics(f).stringWidth(l.footer) > l.getWidth() - (2 * 2 + 2) && size >= 10) {
               f = new Font(f.getName(), f.getStyle(), size--);
            }

            int fSize = size;
            labels.stream().forEach(lbl -> lbl.setFooterFontSize(fSize));
         }
      } finally {
         panel.revalidate();
         panel.repaint();
      }
   }
}
