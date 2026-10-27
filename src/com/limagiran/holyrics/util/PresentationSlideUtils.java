package com.limagiran.holyrics.util;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import java.awt.BasicStroke;
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

public class PresentationSlideUtils {
   private static final transient ScreenDeviceSettings SDS_PRIMARY = Settings.SET.getScreenDeviceSettings(1);

   public static String getKey(ScreenDeviceSettings sds, MusicController.Paragraph p, Theme th, Dimension s, boolean pb) {
      boolean paintAddInfo = !p.isSkipPaintAddInfo();
      ScreenDeviceSettings sdsFinal = sds != null ? sds : SDS_PRIMARY;
      boolean pageCount = sdsFinal.isShowPageCount() && p.isPageCount();
      boolean titleAndArtist = sdsFinal.isShowTitleAndArtistAllSlides() && !p.getTitleAndArtist().isEmpty();
      String breaklineKey = Settings.EnumSettings2.BREAK_LINE.is() + "" + p.isHackBreakLine();
      String cacheKey = "presentation:"
         + p.getSlideType().toString()
         + Settings.EnumSettings2.ARTIST_UNDERLINE.is()
         + ":"
         + p.hash()
         + breaklineKey
         + paintAddInfo
         + ":"
         + sdsFinal.hash()
         + pageCount
         + titleAndArtist;
      return PresentationCache.generateKey(cacheKey, th, s, pb);
   }

   public static BufferedImage createImage(ScreenDeviceSettings sds, MusicController.Paragraph p, Theme th, Dimension _s, boolean pb) {
      boolean paintAddInfo = !p.isSkipPaintAddInfoAndToFalse();
      ScreenDeviceSettings sdsFinal = sds != null ? sds : SDS_PRIMARY;
      boolean pageCount = sdsFinal.isShowPageCount() && p.isPageCount();
      boolean titleAndArtist = sdsFinal.isShowTitleAndArtistAllSlides() && !p.getTitleAndArtist().isEmpty();
      String breaklineKey = Settings.EnumSettings2.BREAK_LINE.is() + "" + p.isHackBreakLine();
      Dimension s = (Dimension)_s.clone();
      String cacheKey = "presentation:"
         + p.getSlideType().toString()
         + Settings.EnumSettings2.ARTIST_UNDERLINE.is()
         + ":"
         + p.hash()
         + breaklineKey
         + paintAddInfo
         + ":"
         + sdsFinal.hash()
         + pageCount
         + titleAndArtist;
      String cacheKeyFinal = PresentationCache.generateKey(cacheKey, th, s, pb);
      Dimension d = (Dimension)s.clone();
      double lineSpacing = th.getLineSpacing() + 1.0;

      try {
         BufferedImage bi = PresentationCache.get(cacheKeyFinal, s);
         if (bi != null) {
            return bi;
         } else {
            bi = new BufferedImage(d.width, d.height, 2);
            Graphics2D g = bi.createGraphics();
            G2Utils.setupGraphics(g);
            BooleanOptional.runIfTrue(pb, () -> th.getBackground().paintBackground(g, d, th.getTransparencyColor()));
            String[] array = p.getLines();
            Rectangle rect = th.getRectWithMargin(d);
            if (p.isPaintMargin()) {
               g.setColor(Color.WHITE);
               G2Utils.drawRect(rect, g);
               g.setStroke(new BasicStroke(1.0F, 1, 1, 5.0F, new float[]{16.0F, 16.0F}, 0.0F));
               g.setColor(Color.BLACK);
               G2Utils.drawRect(rect, g);
            }

            G2Utils.VerticalAlign valign = G2Utils.VerticalAlign.getVerticalAlign(th);
            BooleanOptional.runIfTrue(pb, () -> th.paintSmoke(g, rect));
            if (p.getSlideType().isTitle() && array.length == 2) {
               if (pageCount || titleAndArtist) {
                  int heightAddInfo = sdsFinal.getAdditionalInfoLineHeightTo(d.height);
                  if (pageCount && titleAndArtist && sdsFinal.isPageCountPositionTop() == sdsFinal.isTitleAndArtistPositionTop()) {
                     sdsFinal.translatePageCount(rect, heightAddInfo);
                  } else {
                     BooleanOptional.runIfTrue(pageCount, () -> sdsFinal.translatePageCount(rect, heightAddInfo));
                     BooleanOptional.runIfTrue(titleAndArtist, () -> sdsFinal.translateTitleAndArtist(rect, heightAddInfo));
                  }
               }

               Font fontTitle = th.getFont((int)((double)d.height * 1.3), g);
               Font fontArtist = th.getFont((int)((double)d.height * 1.1), g);
               double rate = 0.95;
               FontMetrics fmTitle = g.getFontMetrics(fontTitle);

               for (FontMetrics fmArtist = g.getFontMetrics(fontArtist);
                  fmTitle.stringWidth(array[0]) > rect.width
                     || fmArtist.stringWidth(array[1]) > rect.width
                     || (double)fmTitle.getHeight() * lineSpacing + (double)fmArtist.getHeight() * lineSpacing > (double)rect.height;
                  fmArtist = g.getFontMetrics(fontArtist)
               ) {
                  fontTitle = fontTitle.deriveFont((float)((int)((double)fontTitle.getSize() * rate)));
                  fontArtist = fontArtist.deriveFont((float)((int)((double)fontArtist.getSize() * rate)));
                  if ((rate -= 0.05) <= 0.0) {
                     break;
                  }

                  fmTitle = g.getFontMetrics(fontTitle);
               }

               g.setFont(fontTitle);
               Point pTitle = G2Utils.adjustString(rect, g, th.getLineSpacing(), valign, array[0])[0];
               int hTitle = (int)((double)g.getFontMetrics().getHeight() * lineSpacing);
               g.setFont(fontArtist);
               Point pArtist = G2Utils.adjustString(rect, g, th.getLineSpacing(), valign, array[1])[0];
               int hArtist = (int)((double)g.getFontMetrics().getHeight() * lineSpacing);
               switch (th.getTextAlignV()) {
                  case -1:
                     pArtist.y += hTitle;
                     break;
                  case 1:
                     pTitle.y -= hArtist;
                     break;
                  default:
                     pTitle.y -= hArtist / 2;
                     pArtist.y += hTitle / 2;
               }

               g.setFont(fontTitle);
               FontMetrics fm = g.getFontMetrics();
               th.adjustAlign(pTitle, d, fm.stringWidth(array[0]));
               th.paintLine(array[0], Settings.EnumSettings2.ARTIST_UNDERLINE.is(), pTitle, g, fm.getHeight(), _s.width, false);
               g.setFont(fontArtist);
               fm = g.getFontMetrics();
               th.adjustAlign(pArtist, d, fm.stringWidth(array[1]));
               th.paintLine(array[1], pArtist, g, fm.getHeight(), _s.width, false);
            } else if (p.isCountdown() && array.length == 3) {
               Font fontTime = th.getFont(
                  (int)((double)d.height * ((double)p.getInt(MusicController.Paragraph.EnumParagraphIntValues.COUNTDOWN_FONT_TIME_SIZE) / 100.0)), g
               );
               Font fontText = th.getFont(d.height, g);
               boolean redCount = array[1].startsWith("[red]");
               if (redCount) {
                  array[1] = array[1].substring(5);
               }

               double rate = 0.95;
               String[] arrayBefore = array[0].split("<brtk=t>");
               String[] arrayAfter = array[2].split("<brtk=t>");
               if (arrayBefore.length <= 0) {
                  arrayBefore = new String[]{""};
               }

               if (arrayAfter.length <= 0) {
                  arrayAfter = new String[]{""};
               }

               String largeLineBefore = getLargeLine(g, arrayBefore);
               String largeLineAfter = getLargeLine(g, arrayAfter);

               while (
                  g.getFontMetrics(fontTime).stringWidth(array[1]) > rect.width
                     || g.getFontMetrics(fontText).stringWidth(largeLineBefore) > rect.width
                     || g.getFontMetrics(fontText).stringWidth(largeLineAfter) > rect.width
               ) {
                  fontTime = fontTime.deriveFont((float)((int)((double)fontTime.getSize() * rate)));
                  fontText = fontText.deriveFont((float)((int)((double)fontText.getSize() * rate)));
                  if ((rate -= 0.05) <= 0.0) {
                     break;
                  }
               }

               g.setFont(fontTime);
               Point pTime = G2Utils.adjustString(rect, g, th.getLineSpacing(), valign, array[1])[0];
               int hTime = (int)((double)g.getFontMetrics().getHeight() * lineSpacing);
               g.setFont(fontText);
               int hText1 = 0;
               boolean paintText1 = !array[0].isEmpty();
               boolean paintText2 = !array[2].isEmpty() && !array[2].equals("#null#");
               Point[] pText1 = new Point[]{new Point()};
               if (paintText1) {
                  pText1 = G2Utils.adjustString(rect, g, th.getLineSpacing(), valign, arrayBefore);
                  pText1[0].y = G2Utils.adjustString(rect, g, th.getLineSpacing(), valign, largeLineBefore)[0].y;
                  hText1 = (int)((double)g.getFontMetrics().getHeight() * lineSpacing);
               }

               Point[] pText2 = new Point[]{new Point()};
               int hText2 = 0;
               if (paintText2) {
                  pText2 = G2Utils.adjustString(rect, g, th.getLineSpacing(), valign, arrayAfter);
                  pText2[0].y = G2Utils.adjustString(rect, g, th.getLineSpacing(), valign, largeLineBefore)[0].y;
                  hText2 = (int)((double)g.getFontMetrics().getHeight() * lineSpacing);
               }

               switch (th.getTextAlignV()) {
                  case -1:
                     pTime.y += hText1 * arrayBefore.length;
                     pText2[0].y += hText1 * arrayBefore.length + hTime;
                     break;
                  case 1:
                     pTime.y -= hText2 * arrayAfter.length;
                     pText1[0].y -= hText2 * arrayAfter.length + hTime + (arrayBefore.length - 1) * hText1;
                     pText2[0].y -= hText2 * (arrayAfter.length - 1);
                     break;
                  default:
                     if (hText1 > 0 || hText2 > 0) {
                        if (hText1 == 0) {
                           pTime.y -= hText2 / 2;
                           pText2[0].y += hTime / 2;
                        } else if (hText2 == 0) {
                           pTime.y += hText1 / 2 + hText1 * (arrayBefore.length - 1);
                           pText1[0].y -= hTime / 2;
                        } else {
                           int hTime2 = hTime / 2;
                           pText1[0].y -= hTime2 + hText1 / 2 + hText1 * (arrayBefore.length - 1);
                           pText2[0].y += hTime2 + hText2 / 2;
                        }
                     }
               }

               g.setFont(fontTime);
               FontMetrics fm = g.getFontMetrics();
               th.adjustAlign(pTime, d, fm.stringWidth(array[1]));
               Color backupFontColor = th.getFontColor();
               if (redCount) {
                  th.setFontColor(Color.RED);
               }

               th.paintLine(array[1], pTime, g, fm.getHeight(), _s.width, false);
               th.setFontColor(backupFontColor);
               g.setFont(fontText);
               fm = g.getFontMetrics();
               if (paintText1) {
                  int y = pText1[0].y;

                  for (int i = 0; i < arrayBefore.length; i++) {
                     pText1[i].y = y;
                     th.adjustAlign(pText1[i], d, fm.stringWidth(arrayBefore[i]));
                     th.paintLine(arrayBefore[i], pText1[i], g, fm.getHeight(), _s.width, false);
                     y += hText1;
                  }
               }

               if (paintText2) {
                  int y = pText2[0].y;

                  for (int i = 0; i < arrayAfter.length; i++) {
                     pText2[i].y = y;
                     th.adjustAlign(pText2[i], d, fm.stringWidth(arrayAfter[i]));
                     th.paintLine(arrayAfter[i], pText2[i], g, fm.getHeight(), _s.width, false);
                     y += hText2;
                  }
               }
            } else {
               if (p.isUppercase()) {
                  for (int i = 0; i < array.length; i++) {
                     array[i] = array[i].toUpperCase();
                  }
               }

               if (pageCount || titleAndArtist) {
                  paintAdditionalInfo(sdsFinal, p, th, g, d, pageCount, titleAndArtist, rect, paintAddInfo);
               }

               Font font = th.getFont(d.height, g);
               g.setFont(formatFont(font, p));
               if (breaklineKey.contains("true")) {
                  array = breakLine(array, g, lineSpacing, rect.getSize());
               } else {
                  adjustText(array, g, lineSpacing, rect.getSize());
               }

               Point[] ps = G2Utils.adjustString(rect, g, th.getLineSpacing(), valign, array);
               FontMetrics fmx = g.getFontMetrics();
               int h = fmx.getHeight();

               for (int i = 0; i < array.length; i++) {
                  th.adjustAlign(ps[i], d, fmx.stringWidth(array[i]));
                  th.paintLine(array[i], ps[i], g, h, _s.width, true);
               }
            }

            g.dispose();
            return PresentationCache.put(cacheKeyFinal, s, bi);
         }
      } catch (Exception var42) {
         return Img.createBlank(Color.WHITE, 50, 50);
      }
   }

   public static BufferedImage createImage(ScreenDeviceSettings sds, String s, Theme th, Dimension _s, boolean pb) {
      return createImage(sds, new MusicController.Paragraph(s), th, _s, pb);
   }

   public static BufferedImage createThumb(String t, Theme th, Dimension d) {
      return createImage(null, new MusicController.Paragraph(t), th, d, true);
   }

   public static BufferedImage createThumb(Theme th, Dimension d) {
      return createImage(null, new MusicController.Paragraph(Settings.EnumSettings.THEME_SAMPLE_TEXT._default.toString()), th, d, true);
   }

   public static BufferedImage createImage(ScreenDeviceSettings sds, Presentation p, Dimension d, boolean pb) {
      return createImage(sds, p.text, p.theme, d, pb);
   }

   private static String getLargeLine(Graphics2D g, String[] array) {
      FontMetrics fm = g.getFontMetrics();
      String _return = "";
      int maxWidth = Integer.MIN_VALUE;

      for (String line : array) {
         int currentWidth = fm.stringWidth(line);
         if (currentWidth > maxWidth) {
            _return = line;
            maxWidth = currentWidth;
         }
      }

      return _return;
   }

   private static String[] breakLine(String[] lines, Graphics2D g, double lineSpacing, Dimension d) {
      try {
         List<String> list = new ArrayList<>();
         Font f = g.getFont();
         int size = g.getFont().getSize();

         FontMetrics fm;
         do {
            g.setFont(f.deriveFont((float)size));
            fm = g.getFontMetrics();
            list.clear();

            for (String line : lines) {
               String[] words = line.split(" ");
               if (words.length == 0) {
                  list.add(line);
               } else {
                  int wordsIndex = 0;
                  List<StringBuilder> appendList = new ArrayList<>(lines.length);
                  StringBuilder append = new StringBuilder(words[wordsIndex++]);
                  appendList.add(append);

                  while (wordsIndex < words.length) {
                     if (fm.stringWidth(append + words[wordsIndex]) < d.width) {
                        append.append(" ").append(words[wordsIndex++]);
                     } else {
                        append = new StringBuilder(words[wordsIndex++]);
                        appendList.add(append);
                     }
                  }

                  appendList.forEach(sb -> list.add(sb.toString()));
               }
            }
         } while (--size > 0 && (double)fm.getHeight() * lineSpacing * (double)list.size() > (double)d.height);

         return list.toArray(new String[list.size()]);
      } catch (Exception var17) {
         return lines;
      }
   }

   private static void adjustText(String[] lines, Graphics2D g, double lineSpacing, Dimension d) {
      Font f = g.getFont();
      String largeLine = getLargeLine(g, lines);
      FontMetrics fm = g.getFontMetrics();

      while (fm.stringWidth(largeLine) > d.width || (double)fm.getHeight() * lineSpacing * (double)lines.length > (double)d.height) {
         f = f.deriveFont((float)(f.getSize() - 1));
         g.setFont(f);
         fm = g.getFontMetrics();
         if (f.getSize() <= 1) {
            break;
         }
      }
   }

   private static Font formatFont(Font font, MusicController.Paragraph p) {
      try {
         return !p.isFormat() ? font : SwingUtils.deriveFont(font, p.getScale(font), font.isBold() || p.isBold(), font.isItalic() || p.isItalic());
      } catch (Exception var3) {
         return font;
      }
   }

   private static void setupPositionSix(Rectangle rect, Dimension size, int position) {
      switch (position) {
         case 1:
         case 4:
            rect.x = rect.x + (size.width - rect.width) / 2;
            break;
         case 2:
         case 5:
            rect.x = rect.x + (size.width - rect.width);
         case 3:
      }

      switch (position) {
         case 3:
         case 4:
         case 5:
            rect.y = rect.y + (size.height - rect.height);
      }
   }

   private static void paintAdditionalInfo(
      ScreenDeviceSettings sds,
      MusicController.Paragraph p,
      Theme th,
      Graphics2D g,
      Dimension d,
      boolean pageCount,
      boolean titleAndArtist,
      Rectangle screen,
      boolean paintAddInfo
   ) {
      int height = sds.getAdditionalInfoLineHeightTo(d.height);
      int pcPos = sds.getShowPageCountPosition();
      int taPos = sds.getShowTitleAndArtistAllSlidesPosition();
      boolean paintEffects = sds.isAdditionalInfoPaintEffects();
      g.setFont(sds.getFont(th.getFont()));
      g.setColor(sds.getColor(th.getFontColor()));
      String[] additionalInfo = new String[]{null, null, null};
      String taaFull = p.getTitleAndArtist();
      int idx = taaFull.indexOf("\n");
      String[] taa;
      if (idx < 0) {
         taa = new String[]{taaFull};
      } else {
         String title = taaFull.substring(0, idx);
         String artist = taaFull.substring(idx + 1, taaFull.length());
         if (sds.isAdditionalInfoTwoLines()) {
            taa = new String[]{title, artist};
         } else {
            taa = new String[]{title + " (" + artist + ")"};
         }
      }

      if (pageCount && titleAndArtist && sds.isPageCountPositionTop() == sds.isTitleAndArtistPositionTop()) {
         if (pcPos == taPos) {
            additionalInfo[0] = taa[0];
            additionalInfo[1] = taa.length > 1 ? taa[1] : null;
            additionalInfo[2] = p.getPageCountFormated();
            boolean reverse = !ScreenDeviceSettings.isRight(pcPos);
            paintAdditionalInfo(paintAddInfo, height, pcPos, additionalInfo, th, g, d, reverse, paintEffects);
         } else if (ScreenDeviceSettings.isCenter(pcPos)) {
            additionalInfo[0] = taa[0];
            additionalInfo[1] = taa.length > 1 ? taa[1] : null;
            additionalInfo[2] = p.getPageCountFormated();
            boolean reverse = !ScreenDeviceSettings.isLeft(taPos);
            paintAdditionalInfo(paintAddInfo, height, taPos, additionalInfo, th, g, d, reverse, paintEffects);
         } else {
            additionalInfo[0] = "";
            additionalInfo[1] = null;
            additionalInfo[2] = p.getPageCountFormated();
            int width = (int)((double)paintAdditionalInfo(paintAddInfo, height, pcPos, additionalInfo, th, g, d, false, paintEffects) * 1.25);
            int[] dif;
            if (ScreenDeviceSettings.isLeft(taPos)) {
               dif = new int[]{0, width};
            } else if (taPos != 1 && taPos != 4) {
               dif = new int[]{width};
            } else {
               dif = new int[]{width, width};
            }

            additionalInfo[0] = taa[0];
            additionalInfo[1] = taa.length > 1 ? taa[1] : null;
            additionalInfo[2] = "";
            paintAdditionalInfo(paintAddInfo, height, taPos, additionalInfo, th, g, d, false, paintEffects, dif);
         }

         sds.translatePageCount(screen, taa.length > 1 ? height * 2 : height);
      } else {
         if (pageCount) {
            additionalInfo[0] = "";
            additionalInfo[1] = null;
            additionalInfo[2] = p.getPageCountFormated();
            paintAdditionalInfo(paintAddInfo, height, pcPos, additionalInfo, th, g, d, false, paintEffects);
            sds.translatePageCount(screen, height);
         }

         if (titleAndArtist) {
            additionalInfo[0] = taa[0];
            additionalInfo[1] = taa.length > 1 ? taa[1] : null;
            additionalInfo[2] = "";
            paintAdditionalInfo(paintAddInfo, height, taPos, additionalInfo, th, g, d, false, paintEffects);
            sds.translateTitleAndArtist(screen, taa.length > 1 ? height * 2 : height);
         }
      }
   }

   private static int paintAdditionalInfo(
      boolean paint, int height, int position, String[] array, Theme t, Graphics2D g, Dimension d, boolean reverse, boolean paintEffects, int... margin
   ) {
      Rectangle screen = t.getRectWithMargin(d);
      if (margin.length >= 1) {
         screen.x = screen.x + margin[0];
         screen.width = screen.width - margin[0];
      }

      if (margin.length >= 2) {
         screen.width = screen.width - margin[1];
      }

      boolean isTop = ScreenDeviceSettings.isTop(position);
      boolean twoLines = array[1] != null;
      String separator = !array[twoLines && !isTop ? 1 : 0].isEmpty() && !array[2].isEmpty() ? " - " : "";
      String lineTop = array[reverse ? 2 : 0] + separator + array[reverse ? 0 : 2];
      String lineBottom = twoLines ? array[reverse ? 2 : 1] + separator + array[reverse ? 1 : 2] : "";
      String largeText = twoLines ? getLargeLine(g, new String[]{lineTop, lineBottom}) : lineTop;
      String defaultText = twoLines && !isTop ? lineBottom : lineTop;
      Font font = G2Utils.getBestFont(screen.width, height, largeText, g.getFont(), g);
      g.setFont(font);
      FontMetrics fm = g.getFontMetrics();
      if (paint && !largeText.replace(" - ", "").trim().isEmpty()) {
         Rectangle rect = new Rectangle(screen.x, screen.y, fm.stringWidth(defaultText), fm.getHeight());
         setupPositionSix(rect, screen.getSize(), position);
         Point point = rect.getLocation();
         point.y = point.y + (rect.height - g.getFontMetrics().getDescent());
         int dif = (height - rect.height) / 2;
         point.y += isTop ? dif : -dif;
         t.paintLineWithoutFormatting(twoLines && !isTop ? lineBottom : lineTop, point, g, rect.height, g.getColor(), paintEffects);
         int _return = rect.width;
         if (twoLines) {
            rect.setBounds(screen.x, screen.y, fm.stringWidth(array[isTop ? 1 : 0]), fm.getHeight());
            setupPositionSix(rect, screen.getSize(), position);
            point.x = rect.x;
            point.y += isTop ? height : -height;
            if (ScreenDeviceSettings.isLeft(position) && reverse) {
               point.x = point.x + fm.stringWidth(array[2] + separator);
            } else if (ScreenDeviceSettings.isRight(position) && !reverse) {
               point.x = point.x - fm.stringWidth(array[2] + separator);
            } else if (ScreenDeviceSettings.isCenter(position)) {
            }

            t.paintLineWithoutFormatting(array[isTop ? 1 : 0], point, g, rect.height, g.getColor(), paintEffects);
         }

         return _return;
      } else {
         return fm.stringWidth(largeText);
      }
   }
}
