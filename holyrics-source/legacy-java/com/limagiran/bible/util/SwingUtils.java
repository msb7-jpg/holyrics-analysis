package com.limagiran.bible.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JDialog;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class SwingUtils {
   public static final String COLOR_HEX_HIGHLIGHT_TEXT = "96E8FF";

   public static JTextField createJTextFieldPlaceHolder(final String ph, final Color phColor) {
      return new JTextField() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.getText().replace(" ", "").isEmpty()) {
               Graphics2D g2 = (Graphics2D)g.create();
               g2.setColor(phColor);
               g2.setFont(this.getFont());
               SwingUtils.drawStringCenter(new Rectangle(this.getSize()), g2, ph);
               g2.dispose();
            }
         }
      };
   }

   public static JTextField createJTextFieldPlaceHolder(String ph) {
      return createJTextFieldPlaceHolder(ph, Color.LIGHT_GRAY);
   }

   public static void drawStringCenter(Rectangle r, Graphics2D g, String... t) {
      Point[] p = centralizeText(r, g, t);

      for (int i = 0; i < t.length; i++) {
         g.drawString(t[i], p[i].x, p[i].y);
      }
   }

   public static Point[] centralizeText(Rectangle r, Graphics2D g, String... text) {
      Point[] _return = new Point[text.length];
      int h = g.getFontMetrics(g.getFont()).getHeight();
      int marginTop = (r.height - text.length * h) / 2 + g.getFontMetrics(g.getFont()).getAscent();

      for (int i = 0; i < text.length; i++) {
         int x = r.x + (r.width - g.getFontMetrics(g.getFont()).stringWidth(text[i])) / 2;
         int y = marginTop + h * i + r.y;
         _return[i] = new Point(x, y);
      }

      return _return;
   }

   public static void runOnUIThread(Runnable runnable) {
      try {
         if (EventQueue.isDispatchThread()) {
            runnable.run();
         } else {
            SwingUtilities.invokeAndWait(runnable);
         }
      } catch (Exception var2) {
         Log.send(var2);
      }
   }

   public static void maximize(JDialog dialog, int margin) {
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      d.width -= margin;
      d.height -= margin;
      dialog.setSize(d);
      dialog.setPreferredSize(d);
      dialog.setLocationRelativeTo(null);
   }

   public static String setupHighlight(String text, VerseFilter vf) {
      if (!text.replace(" ", "").isEmpty() && !vf.getSearch().replace(" ", "").isEmpty()) {
         StringBuilder sb = new StringBuilder(text);

         try {
            if (vf.isExactPhrase()) {
               try {
                  int index = stringToSearch(sb.toString()).indexOf(vf.getSearch());

                  while (index >= 0) {
                     int end = index + vf.getSearch().length();
                     int size = sb.length();
                     String highlight = sb.substring(index, end);
                     sb.replace(index, end, applyHighlight(highlight));
                     index = stringToSearch(sb.toString()).indexOf(vf.getSearch(), end + (sb.length() - size));
                  }
               } catch (Exception var10) {
               }
            } else {
               List<VerseFilter> filters = Arrays.asList(Utils.removeExcessSpace(vf.getSearch()).split(" "))
                  .stream()
                  .map(value -> new VerseFilter().setSearch(value).setWholeWord(vf.isWholeWord()))
                  .sorted((o1, o2) -> o1.getSearch().length() - o2.getSearch().length())
                  .collect(Collectors.toList());
               String highlight = "";
               VerseFilter match = match(filters, stringToSearch(sb.toString()), -1);
               if (match != null && stringToSearch(sb.toString()).indexOf(match.getSearchStartsWith(true)) == 0) {
                  String var14 = sb.substring(0, match.getSearchStartsWith(false).length());
                  highlight = applyHighlight(var14);
                  sb.replace(0, match.getSearchStartsWith(false).length(), highlight);
               }

               match = match(filters, stringToSearch(sb.toString()), highlight.length());
               if (match != null) {
                  int index = stringToSearch(sb.toString()).indexOf(match.getSearchContains(true), highlight.length());

                  while (index >= 0) {
                     int start = index + 1;
                     int end = index + match.getSearchContains(false).length();
                     int size = sb.length();
                     highlight = sb.substring(start, end);
                     sb.replace(start, end, applyHighlight(highlight));
                     match = match(filters, stringToSearch(sb.toString()), end + (sb.length() - size));
                     if (match == null) {
                        break;
                     }

                     index = stringToSearch(sb.toString()).indexOf(match.getSearchContains(false), end + (sb.length() - size));
                  }
               }

               for (VerseFilter f : filters) {
                  if (stringToSearch(sb.toString()).endsWith(f.getSearchEndsWith())) {
                     highlight = sb.substring(sb.length() - f.getSearchEndsWith().length(), sb.length());
                     sb.replace(sb.length() - f.getSearchEndsWith().length() + 1, sb.length(), applyHighlight(highlight));
                     break;
                  }
               }
            }

            return sb.toString();
         } catch (Exception var11) {
            return text;
         }
      } else {
         return text;
      }
   }

   private static VerseFilter match(List<VerseFilter> list, String value, int start) {
      VerseFilter vf = null;
      int i = -1;

      for (VerseFilter f : list) {
         String val = start == -1 ? f.getSearchStartsWith(true) : f.getSearchContains(true);
         int indexOf = value.indexOf(val, start);
         if (indexOf >= 0 && (i == -1 || indexOf <= i) && (vf == null || vf.getSearch().length() <= f.getSearch().length())) {
            i = indexOf;
            vf = f;
         }
      }

      return vf;
   }

   private static String stringToSearch(String value) {
      return Utils.removeAccent(value.replace("\n", " ")).toLowerCase();
   }

   private static String applyHighlight(String value) {
      return "<span style=\"background-color:96E8FF\">" + value + "</span>";
   }

   public static Rectangle getThumbSize(JScrollBar sb) {
      try {
         Method m = BasicScrollBarUI.class.getDeclaredMethod("getThumbBounds");
         m.setAccessible(true);
         return (Rectangle)m.invoke((BasicScrollBarUI)sb.getUI());
      } catch (Exception var2) {
         return new Rectangle();
      }
   }
}
