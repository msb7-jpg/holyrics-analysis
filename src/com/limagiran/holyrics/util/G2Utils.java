package com.limagiran.holyrics.util;

import com.limagiran.holyrics.model.theme.Theme;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public class G2Utils {
   public static void drawRect(Rectangle r, Graphics2D g) {
      g.drawRect(r.x, r.y, r.width, r.height);
   }

   public static void drawRoundRect(Rectangle r, Graphics2D g, float t) {
      Stroke s = g.getStroke();
      g.setStroke(new BasicStroke(t, 1, 1));
      g.drawRoundRect(r.x, r.y, r.width, r.height, 15, 15);
      g.setStroke(s);
   }

   public static void fillRect(Rectangle r, Graphics2D g) {
      g.fillRect(r.x, r.y, r.width, r.height);
   }

   public static Point centralize(Rectangle r, Graphics2D g, String s) {
      FontMetrics fm = g.getFontMetrics();
      int y = fm.getAscent() + fm.getLeading() + r.y + (r.height - fm.getHeight()) / 2;
      int x = r.x + (int)((double)(r.width - fm.stringWidth(s)) / 2.0);
      return new Point(x, y);
   }

   public static Point[] adjustString(Rectangle r, Graphics2D g, double ls, G2Utils.VerticalAlign va, String... t) {
      return adjustString(r, g, ls, null, va, t);
   }

   public static Point[] adjustString(Rectangle r, Graphics2D g, double ls, FontMetrics fm, G2Utils.VerticalAlign va, String... t) {
      Point[] _return = new Point[t.length];
      fm = fm == null ? g.getFontMetrics() : fm;
      FontMetrics fmX = g.getFontMetrics();
      int h = fm.getHeight();
      double lineSpacing = (double)h * ls;
      h = (int)((double)h + lineSpacing);
      int margin = fm.getAscent() + fm.getLeading() + r.y + (int)(lineSpacing / 2.0);
      switch (va) {
         case MIDDLE:
            margin += (r.height - t.length * h) / 2;
            break;
         case BOTTOM:
            margin += r.height - t.length * h;
      }

      for (int i = 0; i < t.length; i++) {
         int x = r.x + (int)((double)(r.width - fmX.stringWidth(t[i])) / 2.0);
         int y = margin + h * i;
         _return[i] = new Point(x, y);
      }

      return _return;
   }

   public static Point[] adjustString(Rectangle r, Graphics2D g, double ls, G2Utils.VerticalAlign va, BibleSlideUtils.CustomString... t) {
      return adjustString(r, g, ls, null, va, t);
   }

   public static Point[] adjustString(Rectangle r, Graphics2D g, double ls, FontMetrics fm, G2Utils.VerticalAlign va, BibleSlideUtils.CustomString... t) {
      Point[] _return = new Point[t.length];
      fm = fm == null ? g.getFontMetrics() : fm;
      int h = fm.getHeight();
      double lineSpacing = (double)h * ls;
      h = (int)((double)h + lineSpacing);
      int margin = fm.getAscent() + fm.getLeading() + r.y + (int)(lineSpacing / 2.0);
      switch (va) {
         case MIDDLE:
            margin += (r.height - t.length * h) / 2;
            break;
         case BOTTOM:
            margin += r.height - t.length * h;
      }

      for (int i = 0; i < t.length; i++) {
         int x = r.x + (int)((double)(r.width - fm.stringWidth(t[i].string.toString())) / 2.0);
         int y = margin + h * i;
         _return[i] = new Point(x, y);
      }

      return _return;
   }

   public static void setupGraphics(Graphics2D g) {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
   }

   public static void drawString(Rectangle r, Graphics2D g, double ls, FontMetrics fm, G2Utils.VerticalAlign va, String... t) {
      Point[] p = adjustString(r, g, ls, fm, va, t);

      for (int i = 0; i < t.length; i++) {
         g.drawString(t[i], p[i].x, p[i].y);
      }
   }

   public static void drawString(Rectangle r, Graphics2D g, double ls, G2Utils.VerticalAlign va, String... t) {
      drawString(r, g, ls, null, va, t);
   }

   public static void drawString(Dimension d, Graphics2D g, double ls, FontMetrics fm, G2Utils.VerticalAlign va, String... t) {
      drawString(new Rectangle(d), g, ls, fm, va, t);
   }

   public static void drawString(Dimension d, Graphics2D g, double ls, G2Utils.VerticalAlign va, String... t) {
      drawString(new Rectangle(d), g, ls, va, t);
   }

   public static Composite setupAlphaComposite(Graphics2D g, float alpha) {
      Composite old = g.getComposite();
      g.setComposite(AlphaComposite.getInstance(3, Utils.range(alpha, 0.0F, 1.0F)));
      return old;
   }

   public static Font getBestFont(int maxHeight, Font font, Graphics2D g) {
      int range = 1000;
      int middle = 500;
      int left = middle - 1;
      int right = middle;

      while (true) {
         int index = middle - 1;
         int candidate = g.getFontMetrics(font.deriveFont((float)index)).getHeight();
         if (candidate == maxHeight) {
            return font.deriveFont((float)index);
         }

         if (candidate > maxHeight) {
            if (left == 0) {
               for (int i = index; i > 0; i--) {
                  if (g.getFontMetrics(font.deriveFont((float)i)).getHeight() < maxHeight) {
                     return font.deriveFont((float)i);
                  }
               }

               return font.deriveFont(1.0F);
            }

            right = left / 2;
            left = left % 2 == 0 ? right - 1 : right;
            middle -= right + 1;
         } else {
            if (right == 0) {
               for (int ix = index; ix <= 1000; ix++) {
                  if (g.getFontMetrics(font.deriveFont((float)ix)).getHeight() > maxHeight) {
                     return font.deriveFont((float)ix - 1.0F);
                  }
               }

               return font.deriveFont(1000.0F);
            }

            if (right % 2 == 0) {
               right /= 2;
               left = right - 1;
            } else {
               right /= 2;
               left = right;
            }

            middle += left + 1;
         }
      }
   }

   public static Font getBestFont(int maxWidth, int maxHeight, String text, Font font, Graphics2D g) {
      int range = 1000;
      int middle = 500;
      int left = middle - 1;
      int right = middle;

      while (true) {
         int index = middle - 1;
         FontMetrics fm = g.getFontMetrics(font.deriveFont((float)index));
         int w = fm.stringWidth(text);
         int h = fm.getHeight();
         if (maxWidth == w && maxHeight == h) {
            return font.deriveFont((float)index);
         }

         if (w <= maxWidth && h <= maxHeight) {
            if (right == 0) {
               for (int i = index; i <= 1000; i++) {
                  fm = g.getFontMetrics(font.deriveFont((float)i));
                  h = fm.getHeight();
                  w = fm.stringWidth(text);
                  if (w > maxWidth || h > maxHeight) {
                     return font.deriveFont((float)i - 1.0F);
                  }
               }

               return font.deriveFont(1000.0F);
            }

            if (right % 2 == 0) {
               right /= 2;
               left = right - 1;
            } else {
               right /= 2;
               left = right;
            }

            middle += left + 1;
         } else {
            if (left == 0) {
               for (int ix = index; ix > 0; ix--) {
                  fm = g.getFontMetrics(font.deriveFont((float)ix));
                  h = fm.getHeight();
                  w = fm.stringWidth(text);
                  if (w <= maxWidth && h <= maxHeight) {
                     return font.deriveFont((float)ix);
                  }
               }

               return font.deriveFont(1.0F);
            }

            right = left / 2;
            left = left % 2 == 0 ? right - 1 : right;
            middle -= right + 1;
         }
      }
   }

   public static void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2, int size) {
      Graphics2D g = (Graphics2D)g2.create();
      double dx = (double)(x2 - x1);
      double dy = (double)(y2 - y1);
      double angle = Math.atan2(dy, dx);
      int len = (int)Math.sqrt(dx * dx + dy * dy);
      AffineTransform at = AffineTransform.getTranslateInstance((double)x1, (double)y1);
      at.concatenate(AffineTransform.getRotateInstance(angle));
      g.transform(at);
      Stroke oldStroke = g.getStroke();
      g.setStroke(new BasicStroke((float)size / 4.0F, 1, 1));
      g.drawLine(0, 0, len - size / 2, 0);
      g.setStroke(oldStroke);
      g.fillPolygon(new int[]{len, len - size, len - size, len}, new int[]{0, -size, size, 0}, 4);
   }

   public static enum VerticalAlign {
      TOP,
      MIDDLE,
      BOTTOM;

      public static G2Utils.VerticalAlign getVerticalAlign(int va) {
         return va == -1 ? TOP : (va == 1 ? BOTTOM : MIDDLE);
      }

      public static G2Utils.VerticalAlign getVerticalAlign(Theme t) {
         return getVerticalAlign(t.getTextAlignV());
      }
   }
}
