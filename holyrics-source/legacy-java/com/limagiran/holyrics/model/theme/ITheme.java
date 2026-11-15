package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Utils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D.Float;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public interface ITheme {
   Media VIDEO_DEFAULT = IThemeUtils.getVideoDefault();

   Theme getInstance();

   default MediaPlayer getMediaPlayer(Component parent) {
      if (!this.getInstance().getBackground().isVideo()) {
         return null;
      } else {
         try {
            return new MediaPlayer(this.getInstance().getBackground().getVideo());
         } catch (Exception var3) {
            Utils.error(parent, var3.getMessage());
            return new MediaPlayer(VIDEO_DEFAULT);
         }
      }
   }

   static void save(Theme theme, Window owner) {
      try {
         if (theme.getBackground() instanceof BackgroundMyVideo) {
            BackgroundMyVideo bg = (BackgroundMyVideo)theme.getBackground();
            if (new File(bg.getMRLtoVLC()).length() > 96468992L) {
               Utils.error(owner, I18N.MSG_ERROR_BIG_VIDEO_SAVE_THEME.val());
               return;
            }
         }

         theme.save(owner);
      } catch (Exception var3) {
      }
   }

   default void paintLine(String s, boolean u, Point p, Graphics2D g, int h, int width, boolean j) {
      s = this.justify(s, p, g.getFontMetrics(), width, j);
      Stroke stroke = g.getStroke();
      GlyphVector v = g.getFont().createGlyphVector(g.getFontMetrics().getFontRenderContext(), s);
      Shape sh = v.getOutline((float)p.x, (float)p.y);
      int w = g.getFontMetrics().stringWidth(s);
      Shape sh2 = new Float((float)p.x, (float)p.y + (float)h * 0.08F, (float)w, (float)h * 0.08F);
      Theme t = this.getInstance();
      if (t.getBrightnessThickness() > 0.0) {
         IThemeUtils.paintBrightness(g, sh, t.getBrightnessColor(), t.getBrightnessThickness(), h);
         if (u) {
            IThemeUtils.paintBrightness(g, sh2, t.getBrightnessColor(), t.getBrightnessThickness(), h);
         }
      }

      if (t.getShadowX() != 0.0 || t.getShadowY() != 0.0) {
         IThemeUtils.paintShadow(g, sh, t.getShadowColor(), t.getShadowX(), t.getShadowY(), h);
         if (u) {
            IThemeUtils.paintShadow(g, sh2, t.getShadowColor(), t.getShadowX(), t.getShadowY(), h);
         }
      }

      if (u && t.getOutlineThickness() > 0.0) {
         IThemeUtils.paintOutline(g, sh2, t.getOutlineColor(), t.getOutlineThickness(), h);
      }

      g.setColor(t.getFontColor());
      g.setStroke(new BasicStroke((float)h * 0.05F, 1, 1));
      if (u) {
         g.fill(sh2);
      }

      if (t.getOutlineThickness() > 0.0) {
         IThemeUtils.paintOutline(g, sh, t.getOutlineColor(), t.getOutlineThickness(), h);
      }

      g.setColor(t.getFontColor());
      g.setStroke(stroke);
      g.fill(sh);
   }

   default void paintLineWithoutFormatting(String s, Point p, Graphics2D g, int h, Color fc, boolean pe) {
      Stroke stroke = g.getStroke();
      GlyphVector v = g.getFont().createGlyphVector(g.getFontMetrics().getFontRenderContext(), s);
      Shape sh = v.getOutline((float)p.x, (float)p.y);
      Theme t = this.getInstance();
      if (pe && t.getBrightnessThickness() > 0.0) {
         IThemeUtils.paintBrightness(g, sh, t.getBrightnessColor(), t.getBrightnessThickness(), h);
      }

      if (pe && (t.getShadowX() != 0.0 || t.getShadowY() != 0.0)) {
         IThemeUtils.paintShadow(g, sh, t.getShadowColor(), t.getShadowX(), t.getShadowY(), h);
      }

      g.setStroke(new BasicStroke((float)h * 0.05F, 1, 1));
      if (pe && t.getOutlineThickness() > 0.0) {
         IThemeUtils.paintOutline(g, sh, t.getOutlineColor(), t.getOutlineThickness(), h);
      }

      g.setColor(fc);
      g.setStroke(stroke);
      g.fill(sh);
   }

   default void paintLineWithoutFormatting(String s, Point p, Graphics2D g, int h, boolean pe) {
      this.paintLineWithoutFormatting(s, p, g, h, this.getInstance().getFontColor(), pe);
   }

   default void paintLine(String s, Point p, Graphics2D g, int h, int w, boolean j) {
      this.paintLine(s, false, p, g, h, w, j);
   }

   default void paintSmoke(Graphics2D g, Rectangle r) {
      Theme t = this.getInstance();
      if (t.isTransparent() && t.getBackground().getOpacity() > 0) {
         Color currentColor = g.getColor();
         Color c = t.getTransparencyColorReal();
         int alpha = (int)(255.0F * Utils.range((float)t.getBackground().getOpacityPercent(), 0.0F, 1.0F));
         g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Utils.range(alpha, 0, 255)));
         int hmargin = (int)((double)r.width * 0.025);
         int vmargin = (int)((double)r.height * 0.025);
         g.fillRect(r.x - hmargin, r.y - vmargin, r.width + hmargin * 2, r.height + vmargin * 4);
         g.setColor(currentColor);
      }
   }

   default void adjustAlign(Point p, Dimension d, int w) {
      switch (this.getInstance().getTextAlign()) {
         case -1:
            p.x = this.getMarginLeftTo(d.width);
            break;
         case 1:
            p.x = d.width - w - this.getMarginRightTo(d.width);
      }
   }

   default String justify(String s, Point p, FontMetrics fm, int w, boolean j) {
      if (this.getInstance().getTextAlign() != 2) {
         return s;
      } else if (!j) {
         p.x = (int)((double)w * this.getMarginLeft());
         return s;
      } else {
         try {
            String[] words = s.split(" ");
            String buff = String.join(" ", words);
            int width = fm.stringWidth(buff);
            int index = 0;
            int maxW = w - this.getMarginHTo(w);

            while (width < maxW) {
               buff = String.join(" ", words);
               StringBuilder var10000 = new StringBuilder();
               int var10002 = index++;
               words[var10002 % (words.length - 1)] = var10000.append(words[var10002 % (words.length - 1)]).append(" ").toString();
               int width2 = fm.stringWidth(String.join(" ", words));
               if (width2 == width) {
                  break;
               }

               width = width2;
            }

            p.x = this.getMarginLeftTo(w);
            return buff;
         } catch (Exception var12) {
            return s;
         }
      }
   }

   default double getMarginTop() {
      return this.getInstance().getMargins()[0];
   }

   default double getMarginRight() {
      return Math.min(0.99 - this.getMarginLeft(), this.getInstance().getMargins()[1]);
   }

   default double getMarginBottom() {
      return Math.min(0.99 - this.getMarginTop(), this.getInstance().getMargins()[2]);
   }

   default double getMarginLeft() {
      return this.getInstance().getMargins()[3];
   }

   default int getMarginTopTo(int h) {
      return (int)((double)h * this.getMarginTop());
   }

   default int getMarginRightTo(int w) {
      return (int)((double)w * this.getMarginRight());
   }

   default int getMarginBottomTo(int h) {
      return (int)((double)h * this.getMarginBottom());
   }

   default int getMarginLeftTo(int w) {
      return (int)((double)w * this.getMarginLeft());
   }

   default int getMarginHTo(int w) {
      return (int)((double)w * (this.getMarginRight() + this.getMarginLeft()));
   }

   default int getMarginVTo(int h) {
      return (int)((double)h * (this.getMarginTop() + this.getMarginBottom()));
   }

   default Rectangle getRectWithMargin(Dimension d) {
      return new Rectangle(
         this.getMarginLeftTo(d.width), this.getMarginTopTo(d.height), d.width - this.getMarginHTo(d.width), d.height - this.getMarginVTo(d.height)
      );
   }
}
