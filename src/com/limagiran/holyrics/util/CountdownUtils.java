package com.limagiran.holyrics.util;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.model.CountdownSettings;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.view.PresentationSlideView;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public interface CountdownUtils {
   CountdownSettings getInstance();

   static Presentation getPresentation(String textBefore, long seconds, String textAfter, Theme theme, int size, boolean zerofill) {
      MusicController.Paragraph p = new MusicController.Paragraph(getTextToPresentation(textBefore, seconds, textAfter, zerofill))
         .setSlideType(PresentationSlideView.EnumSlideType.COUNTDOWN)
         .setInt(MusicController.Paragraph.EnumParagraphIntValues.COUNTDOWN_FONT_TIME_SIZE, size);
      return Presentation.create(p, theme);
   }

   default Presentation getPresentation() {
      CountdownSettings cs = this.getInstance();
      return getPresentation(cs.getTextBefore(true), this.getSeconds(), cs.getTextAfter(true), cs.getThemeObject(), cs.getSize(), cs.isZeroFill());
   }

   static String getTextToPresentation(String textBefore, long seconds, String textAfter, boolean zerofill) {
      return textBefore + "\n" + formatTime(seconds, zerofill) + "\n" + (textAfter.isEmpty() ? "#null#" : textAfter);
   }

   default String getTextToPresentation() {
      CountdownSettings cs = this.getInstance();
      return cs.getTextBefore(true) + "\n" + formatTime(this.getSeconds(), cs.isZeroFill()) + "\n" + cs.getTextAfter(true);
   }

   default long getSeconds() {
      CountdownSettings cs = this.getInstance();
      return cs.isDetermine() ? (long)(cs.getMinuteDetermine() * 60 + cs.getSecondDetermine()) : getSecondsExact(cs.getHourExact(), cs.getMinuteExact());
   }

   static String formatTime(long seconds, boolean zerofill, boolean forceFull, boolean negative) {
      long h = TimeUnit.SECONDS.toHours(seconds);
      long m = TimeUnit.SECONDS.toMinutes(seconds);
      long s = TimeUnit.SECONDS.toSeconds(seconds);
      s -= TimeUnit.MINUTES.toSeconds(m);
      m -= TimeUnit.HOURS.toMinutes(h);
      return (negative ? "-" : "")
         + (h <= 0L && !forceFull ? String.format((zerofill ? "%02d" : "%d") + ":%02d", m, s) : String.format("%02d:%02d:%02d", h, m, s));
   }

   static String formatTime(long seconds, boolean zerofill) {
      return formatTime(seconds, zerofill, false, false);
   }

   static long getSecondsExact(int h, int m) {
      long to = TimeUnit.HOURS.toSeconds((long)h) + TimeUnit.MINUTES.toSeconds((long)m);
      Calendar c = Calendar.getInstance();
      long from = TimeUnit.HOURS.toSeconds((long)c.get(11)) + TimeUnit.MINUTES.toSeconds((long)c.get(12)) + (long)c.get(13);
      return to < from ? to + 86400L - from : to - from;
   }

   static long toSeconds(Calendar c) {
      return TimeUnit.HOURS.toSeconds((long)c.get(11)) + TimeUnit.MINUTES.toSeconds((long)c.get(12)) + (long)c.get(13);
   }

   static long toSecondsNow() {
      return toSeconds(Calendar.getInstance());
   }
}
