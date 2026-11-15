package com.limagiran.holyrics.util;

import java.awt.Dimension;
import java.awt.Rectangle;

public class ScreenUtils {
   public static Dimension adjust(Dimension size, Dimension ratio) {
      if (size.getWidth() / size.getHeight() < ratio.getWidth() / ratio.getHeight()) {
         size.height = (int)(size.getWidth() * ratio.getHeight() / ratio.getWidth());
      } else {
         size.width = (int)(size.getHeight() * ratio.getWidth() / ratio.getHeight());
      }

      return size;
   }

   public static Rectangle adjustRect(Dimension size, Dimension ratio) {
      Rectangle area = new Rectangle(size);
      if (size.getWidth() / size.getHeight() < ratio.getWidth() / ratio.getHeight()) {
         area.height = (int)(size.getWidth() * ratio.getHeight() / ratio.getWidth());
         area.y = (size.height - area.height) / 2;
      } else {
         area.width = (int)(size.getHeight() * ratio.getWidth() / ratio.getHeight());
         area.x = (size.width - area.width) / 2;
      }

      return area;
   }

   public static Rectangle getNineDirectionsLocation(int w, int h, Dimension d, int position) {
      int p = Utils.range(position, 0, 8);
      int[] x = new int[]{0, (d.width - w) / 2, d.width - w};
      int[] y = new int[]{0, (d.height - h) / 2, d.height - h};
      Rectangle r = new Rectangle(x[p % 3], y[p / 3], w, h);
      r.x = Math.max(r.x, 0);
      r.y = Math.max(r.y, 0);
      if (r.getMaxX() > (double)d.width) {
         r.x = (int)((double)r.x - (r.getMaxX() - (double)d.width));
      }

      if (r.getMaxY() > (double)d.height) {
         r.y = (int)((double)r.y - (r.getMaxY() - (double)d.height));
      }

      return r;
   }
}
