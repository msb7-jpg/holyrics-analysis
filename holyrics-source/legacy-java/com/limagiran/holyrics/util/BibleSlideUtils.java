package com.limagiran.holyrics.util;

import com.limagiran.bible.Verse;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BibleSlideUtils {
   public static String getKey(List<Verse> verses, Theme t, Dimension s, boolean pb, boolean pdh, boolean uc, boolean ph, boolean skipVerses) {
      try {
         boolean onlyReference = Settings.EnumSettings2.BIBLE_SHOW_ONLY_REFERENCE.is();
         EnumVerseDescription en = onlyReference
            ? EnumVerseDescription.BEGIN
            : EnumVerseDescription.valueOfOrDefault(Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION.getString());
         String header = !verses.isEmpty() && !onlyReference ? en.getHeader(verses, Settings.EnumSettings.BIBLE_SHOW_VERSION.is()) : "";
         String content = skipVerses
            ? ""
            : verses.stream()
               .map(en.getContentFunction(uc, Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt() > 1))
               .collect(Collectors.joining(Settings.EnumSettings.BIBLE_VERSE_EMPTY_LINE_BETWEEN.is() ? " \n \n" : " \n"));
         if (!skipVerses && onlyReference) {
            content = EnumVerseDescription.BELOW.getDesc(verses, true);
            content = uc ? content.toUpperCase() : content;
         }

         String cacheKey = new StringBuilder(128)
            .append("bible:")
            .append(content)
            .append(":")
            .append(header)
            .append(":")
            .append(en)
            .append(":")
            .append(pdh)
            .append(":")
            .append(ph)
            .append(":")
            .append(skipVerses)
            .toString();
         return PresentationCache.generateKey(cacheKey, t, s, pb);
      } catch (Exception var13) {
         return String.valueOf(System.nanoTime());
      }
   }

   public static BufferedImage createImage(List<Verse> verses, Theme t, Dimension _s, boolean pb, boolean pdh, boolean uc, boolean ph, boolean skipVerses) {
      try {
         Dimension s = (Dimension)_s.clone();
         Dimension d = (Dimension)s.clone();
         boolean onlyReference = Settings.EnumSettings2.BIBLE_SHOW_ONLY_REFERENCE.is();
         EnumVerseDescription en = onlyReference
            ? EnumVerseDescription.BEGIN
            : EnumVerseDescription.valueOfOrDefault(Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION.getString());
         String header = !verses.isEmpty() && !onlyReference ? en.getHeader(verses, Settings.EnumSettings.BIBLE_SHOW_VERSION.is()) : "";
         String content = skipVerses
            ? ""
            : verses.stream()
               .map(en.getContentFunction(uc, Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt() > 1))
               .collect(Collectors.joining(Settings.EnumSettings.BIBLE_VERSE_EMPTY_LINE_BETWEEN.is() ? " \n \n" : " \n"));
         if (!skipVerses && onlyReference) {
            content = EnumVerseDescription.BELOW.getDesc(verses, true);
            content = uc ? content.toUpperCase() : content;
         }

         String cacheKey = new StringBuilder(128)
            .append("bible:")
            .append(content)
            .append(":")
            .append(header)
            .append(":")
            .append(en)
            .append(":")
            .append(pdh)
            .append(":")
            .append(ph)
            .append(":")
            .append(skipVerses)
            .toString();
         String cacheKeyFinal = PresentationCache.generateKey(cacheKey, t, s, pb);
         BufferedImage bi = PresentationCache.get(cacheKeyFinal, s);
         if (bi != null) {
            return bi;
         } else {
            bi = new BufferedImage(d.width, d.height, 2);
            Graphics2D g = bi.createGraphics();
            G2Utils.setupGraphics(g);
            BooleanOptional.runIfTrue(pb, () -> t.getBackground().paintBackground(g, d, t.getTransparencyColor()));
            int headerHeight = !header.isEmpty() ? (int)((double)d.height / 14.0) : 0;
            d.height -= headerHeight;
            g.setFont(t.getFont(d.height, g));
            Rectangle rect = t.getRectWithMargin(d);
            rect.y += headerHeight;
            BooleanOptional.runIfTrue(pb, () -> t.paintSmoke(g, rect));
            rect.y -= headerHeight;
            BibleSlideUtils.CustomString[] lines = getLines(content, g, t.getLineSpacing(), rect.getSize());
            Point[] ps = G2Utils.adjustString(rect, g, t.getLineSpacing(), G2Utils.VerticalAlign.getVerticalAlign(t), lines);
            FontMetrics fm = g.getFontMetrics();
            int h = fm.getHeight();

            for (int i = 0; i < lines.length; i++) {
               boolean notJustify = i == lines.length - 1 || lines[i + 1].start;
               String line = lines[i].string.toString();
               ps[i].y += headerHeight;
               t.adjustAlign(ps[i], d, fm.stringWidth(line));
               t.paintLine(line, ps[i], g, h, _s.width, !notJustify);
            }

            BooleanOptional.runIfTrue(!header.isEmpty() && ph, () -> paintDesc(header, t, g, d, en, pdh));
            g.dispose();
            return PresentationCache.put(cacheKeyFinal, s, bi);
         }
      } catch (Exception var27) {
         return Img.createBlank(Color.WHITE, 50, 50);
      }
   }

   public static BufferedImage createImage(List<Verse> verses, Theme t, Dimension _s, boolean pb, boolean pdh, boolean uc, boolean ph) {
      return createImage(verses, t, _s, pb, pdh, uc, ph, false);
   }

   public static BufferedImage createDarkenedHeader(Dimension _s, Theme t) {
      try {
         Dimension s = (Dimension)_s.clone();
         Dimension d = (Dimension)s.clone();
         EnumVerseDescription en = EnumVerseDescription.valueOfOrDefault(Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION.getString());
         String cacheKey = PresentationCache.generateKey("darkenedHeader:" + _s, t, s, true);
         BufferedImage bi = PresentationCache.get(cacheKey, s);
         if (bi != null) {
            return bi;
         } else {
            bi = new BufferedImage(d.width, d.height, 2);
            Graphics2D g = bi.createGraphics();
            G2Utils.setupGraphics(g);
            int headerHeight = en.isTop() ? (int)((double)d.height / 14.0) : 0;
            d.height -= headerHeight;
            g.setFont(t.getFont(d.height, g));
            paintDesc(" ", t, g, d, en, true);
            g.dispose();
            return PresentationCache.put(cacheKey, s, bi);
         }
      } catch (Exception var9) {
         return Img.createBlank(Color.WHITE, 50, 50);
      }
   }

   private static BibleSlideUtils.CustomString[] getLines(String content, Graphics2D g, double lineSpacing, Dimension d) {
      List<BibleSlideUtils.CustomString> list = new ArrayList<>();
      Font f = g.getFont();
      int size = g.getFont().getSize();

      FontMetrics fm;
      do {
         g.setFont(f.deriveFont((float)size));
         fm = g.getFontMetrics();
         list.clear();
         String[] words = content.split(" ");
         int wordsIndex = 0;
         BibleSlideUtils.CustomString append = new BibleSlideUtils.CustomString(words[wordsIndex++], true);
         list.add(append);

         while (wordsIndex < words.length) {
            boolean start = words[wordsIndex].startsWith("\n");
            if (!start && fm.stringWidth(append + words[wordsIndex]) < d.width) {
               append.string.append(" ").append(words[wordsIndex++]);
            } else {
               append = new BibleSlideUtils.CustomString(words[wordsIndex++], start);
               list.add(append);
            }
         }
      } while (--size > 0 && (double)fm.getHeight() * (lineSpacing + 1.0) * (double)list.size() > (double)d.height);

      return list.toArray(new BibleSlideUtils.CustomString[list.size()]);
   }

   private static void paintDesc(String hd, Theme t, Graphics2D g, Dimension d, EnumVerseDescription en, boolean pdh) {
      int h = (int)((double)d.height / 14.0);
      Font font = G2Utils.getBestFont((int)((double)d.width * 0.95), h - 2, hd, t.getFont(), g);
      g.setFont(font);
      FontMetrics fm = g.getFontMetrics();
      if (en.equals(EnumVerseDescription.TOP_SLIDE_DARKENED)) {
         if (pdh) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, d.width, fm.getHeight());
         }

         g.setColor(new Color(255, 255, 255, 200));
         G2Utils.drawString(new Rectangle(d.width, fm.getHeight()), g, 0.0, G2Utils.VerticalAlign.MIDDLE, hd);
      } else {
         g.setColor(t.getFontColor());
         Point p = G2Utils.adjustString(new Rectangle(d.width, h), g, 0.0, G2Utils.VerticalAlign.MIDDLE, hd)[0];
         t.adjustAlign(p, d, g.getFontMetrics().stringWidth(hd));
         t.paintLine(hd, p, g, h, d.width, false);
      }
   }

   public static List<Verse> getVerses(Verse v, List list, int size) {
      int i = list.indexOf(v);
      List<Verse> _return = new ArrayList<>();
      _return.add(v);

      for (int x = 1; x < size; x++) {
         i++;
         if (i < list.size() && list.get(i) instanceof Verse && !_return.contains((Verse)list.get(i))) {
            _return.add((Verse)list.get(i));
         }
      }

      return _return;
   }

   public static class CustomString {
      public final StringBuilder string = new StringBuilder();
      public final boolean start;

      public CustomString(String string, boolean start) {
         this.string.append(string);
         this.start = start;
      }

      @Override
      public String toString() {
         return this.string.toString();
      }
   }
}
