package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.Utils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class WebDecoratedImageWithLabelSelectableCustom extends WebDecoratedImageSelectableCustom {
   private static final Font FONT = new Font("Trebuchet MS", 1, 19);
   private static final Map<String, Color> MAP_LABEL_COLOR = loadMapLabelColor();
   private String label = "";
   private volatile boolean paintLabel = true;
   private final Rectangle labelRect;

   public WebDecoratedImageWithLabelSelectableCustom() {
      this.setFont(FONT);
      this.labelRect = new Rectangle(12, 4, 1, 22);
   }

   public void setLabel(String label) {
      this.label = label != null ? label.trim() : "";
   }

   public void setPaintLabel(boolean paintLabel) {
      this.paintLabel = paintLabel;
   }

   public String getLabel() {
      return this.label;
   }

   @Override
   protected void paintComponent(Graphics g) {
      boolean _paintLabel = this.paintLabel;
      String _label = this.getLabel();
      if (_paintLabel) {
         g.translate(0, _label.isEmpty() ? 11 : 22);
      }

      super.paintComponent(g);
      if (_paintLabel) {
         g.translate(0, _label.isEmpty() ? -11 : -22);
         if (!_label.isEmpty()) {
            Graphics2D g2 = (Graphics2D)g;
            G2Utils.setupGraphics(g2);
            g2.setColor(new Color(0, 0, 0, 192));
            this.labelRect.width = this.icon.getWidth();
            g2.setColor(getLabelColor(_label));
            G2Utils.drawString(this.labelRect, g2, 0.0, G2Utils.VerticalAlign.MIDDLE, _label);
         }
      }
   }

   private static Map<String, Color> loadMapLabelColor() {
      Map<String, Color> map = new HashMap<>();
      String prop = MyProperties.EnumProperties.MAP_SLIDE_LABEL_TITLE_COLOR.getString();
      if (prop.isEmpty()) {
         return map;
      } else {
         for (String value : prop.split(";")) {
            String[] pair = value.split(":");
            if (pair.length == 2) {
               Utils.decodeColor(pair[1], c -> {
                  Color var10000 = map.put(pair[0].trim().toLowerCase(), c);
               });
            }
         }

         return map;
      }
   }

   public static Color getLabelColor(String _label) {
      Color color = MAP_LABEL_COLOR.get(String.valueOf(_label).toLowerCase());
      if (color == null) {
         color = MAP_LABEL_COLOR.get("_default");
      }

      return color != null ? color : Color.BLACK;
   }
}
