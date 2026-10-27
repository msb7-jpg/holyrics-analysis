package com.limagiran.holyrics.util;

import com.limagiran.holyrics.model.ClockSettings;
import com.limagiran.holyrics.view.WallpaperView;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface ClockSettingsUtils {
   SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");

   ClockSettings getInstance();

   default void update() {
      WallpaperView.update();
   }

   default int getLineHeight(int h) {
      try {
         return (int)((double)(h * this.getInstance().getLineHeight()) / 100.0);
      } catch (Exception var3) {
         return (int)((double)h * 0.08);
      }
   }

   default String getKey(String s, boolean show) {
      ClockSettings c = this.getInstance();
      return !show
         ? "no_show"
         : String.format("%s=%s:%s:%s:%s:%s:%s", c.isShow(), s, c.getForeground(), c.getBackground(), c.isBackground(), c.getLineHeight(), c.getPosition());
   }

   default void paint(Graphics2D g, Dimension d, int h, int translateY) {
      G2Utils.setupGraphics(g);
      setupFont(g, h);
      FontMetrics fm = g.getFontMetrics();
      g.setColor(this.getInstance().getBackgroundToPaint());
      int textHeight = getTextHeight(fm);
      Rectangle r = this.getLocation(fm.stringWidth("12345"), textHeight, d, 4 + (int)((double)textHeight * 0.16));
      r.y += translateY;
      g.fillRect(r.x, r.y, r.width, r.height);
      g.setColor(this.getInstance().getForeground());
      int x = r.x + (int)((double)(r.width - fm.stringWidth(getTime())) / 2.0);
      int y = r.y + (r.height - textHeight) / 2 + textHeight;
      g.drawString(getTime(), x, y);
   }

   static Graphics2D setupFont(Graphics2D g, int h) {
      Font font = ClockSettings.getFont((float)h);
      g.setFont(font);

      while (getTextHeight(g.getFontMetrics()) > h) {
         font = font.deriveFont((float)(font.getSize() - 1));
         g.setFont(font);
         if (font.getSize() <= 1) {
            break;
         }
      }

      return g;
   }

   default boolean isTop() {
      return this.getInstance().getPosition() >= 0 && this.getInstance().getPosition() <= 2;
   }

   default boolean isMiddle() {
      return this.getInstance().getPosition() >= 3 && this.getInstance().getPosition() <= 5;
   }

   default boolean isBottom() {
      return this.getInstance().getPosition() >= 6 && this.getInstance().getPosition() <= 8;
   }

   default boolean isLeft() {
      int p = this.getInstance().getPosition();
      return p == 0 || p == 3 || p == 6;
   }

   default boolean isCenter() {
      int p = this.getInstance().getPosition();
      return p == 1 || p == 4 || p == 7;
   }

   default boolean isRight() {
      int p = this.getInstance().getPosition();
      return p == 2 || p == 5 || p == 8;
   }

   default FontMetrics getFontMetrics(int h) {
      return setupFont(Values.GRAPHICS, h).getFontMetrics();
   }

   default Rectangle getLocation(int w, int h, Dimension d, int margin) {
      int p = Utils.range(this.getInstance().getPosition(), 0, 8);
      int[] x = new int[]{0, (d.width - w) / 2, d.width - w};
      int[] y = new int[]{0, (d.height - h) / 2, d.height - h};
      Rectangle r = new Rectangle(x[p % 3], y[p / 3], w, h);
      r.x = Math.max(r.x - margin, 0);
      r.y = Math.max(r.y - margin, 0);
      r.width += margin * 2;
      r.height += margin * 2;
      if (r.getMaxX() > (double)d.width) {
         r.x = (int)((double)r.x - (r.getMaxX() - (double)d.width));
      }

      if (r.getMaxY() > (double)d.height) {
         r.y = (int)((double)r.y - (r.getMaxY() - (double)d.height));
      }

      return r;
   }

   static String getTime() {
      return SDF.format(new Date());
   }

   static int getTextHeight(FontMetrics fm) {
      return fm.getFont().getSize() - fm.getAscent() / 2;
   }
}
