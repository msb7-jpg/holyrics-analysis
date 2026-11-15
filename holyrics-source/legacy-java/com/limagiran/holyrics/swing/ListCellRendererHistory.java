package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.DateLG;
import java.awt.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class ListCellRendererHistory extends AbstractWebListCellRendererWithSeparatorTripleLine implements ListCellRenderer<Date> {
   public ListCellRendererHistory() {
      super(new AbstractWebListCellRendererWithSeparatorTripleLine.Builder().setHorizontal(true).setRatioBottomLine(0.8));
      Arrays.asList(this.top, this.middle, this.bottom).stream().forEach(c -> {
         c.setHorizontalAlignment(0);
         c.setHorizontalTextPosition(0);
      });
      this.content.setBorder(new EmptyBorder(0, 0, 2, 0));
   }

   public Component getListCellRendererComponent(JList list, Date d, int index, boolean isSelected, boolean cellHasFocus) {
      Calendar c = Calendar.getInstance();
      c.setTime(d);
      String arg1 = DateLG.SDF_D_S.format(d);
      String arg2 = String.format("(%s) %s", c.getDisplayName(7, 1, Locale.getDefault()), DateLG.SDF_T_S.format(d));
      return super.getListCellRendererComponent(list, arg1, arg2, this.getDesc(c), index, isSelected, cellHasFocus);
   }

   private String getDesc(Calendar date) {
      if (Calendar.getInstance().before(date)) {
         return "";
      } else if (DateLG.isToday(date)) {
         return I18N.WORD_TODAY.val();
      } else {
         LocalDate start = DateLG.toLocalDate(date);
         LocalDate end = DateLG.toLocalDate(Calendar.getInstance());
         long days = ChronoUnit.DAYS.between(start, end);
         return days <= 60L
            ? (days == 1L ? I18N.MSG_ONE_DAY_AGO.val() : I18N.MSG_X_DAYS_AGO.format(days))
            : I18N.MSG_X_MONTHS_AGO.format(ChronoUnit.MONTHS.between(start, end));
      }
   }
}
