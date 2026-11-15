package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import javafx.scene.media.Media;

class IThemeUtils {
   protected static Media getVideoDefault() {
      try {
         return new Media(Values.class.getResource("/com/limagiran/holyrics/model/theme/video/VIDEO23.mp4").toURI().toString());
      } catch (Exception var1) {
         Utils.error(I18N.MSG_ERROR_CORRUPTED_FILES.val());
         System.exit(0);
         return null;
      }
   }

   protected static void paintBrightness(Graphics2D g, Shape s, Color c, double d, int h) {
      g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 75));
      double ini = (double)h * d;
      double fim = ini / 5.0;

      for (double x = ini; x > 0.0; x -= fim) {
         g.setStroke(new BasicStroke((float)x, 1, 1));
         g.draw(s);
      }
   }

   protected static void paintShadow(Graphics2D g, Shape s, Color c, double x, double y, int h) {
      g.translate((double)h * x, (double)h * y);
      g.setColor(c);
      g.fill(s);
      g.translate(-((double)h * x), -((double)h * y));
   }

   protected static void paintOutline(Graphics2D g, Shape s, Color c, double d, int h) {
      g.setColor(c);
      g.setStroke(new BasicStroke((float)((double)h * d), 1, 1));
      g.draw(s);
   }
}
