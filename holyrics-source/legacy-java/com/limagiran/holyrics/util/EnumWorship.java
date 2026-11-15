package com.limagiran.holyrics.util;

import com.limagiran.holyrics.i18n.I18N;
import java.time.DayOfWeek;

public class EnumWorship {
   public static enum EnumWorshipDays {
      SUN(I18N.WORD_SUNDAY.val(), DayOfWeek.SUNDAY),
      MON(I18N.WORD_MONDAY.val(), DayOfWeek.MONDAY),
      TUE(I18N.WORD_TUESDAY.val(), DayOfWeek.TUESDAY),
      WED(I18N.WORD_WEDNESDAY.val(), DayOfWeek.WEDNESDAY),
      THU(I18N.WORD_THURSDAY.val(), DayOfWeek.THURSDAY),
      FRI(I18N.WORD_FRIDAY.val(), DayOfWeek.FRIDAY),
      SAT(I18N.WORD_SATURDAY.val(), DayOfWeek.SATURDAY);

      public final String name;
      public final DayOfWeek day;

      private EnumWorshipDays(String name, DayOfWeek day) {
         this.name = name;
         this.day = day;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   public static enum EnumWorshipWeeks {
      ALL(I18N.WORD_ALL.val()),
      FIRST(I18N.WORD_FIRST.val()),
      SECOND(I18N.WORD_SECOND.val()),
      THIRD(I18N.WORD_THIRD.val()),
      FOURTH(I18N.WORD_FOURTH.val()),
      LAST(I18N.WORD_LAST.val());

      public final String name;

      private EnumWorshipWeeks(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
