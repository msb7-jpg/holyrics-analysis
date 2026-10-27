package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.DateLG;
import java.awt.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class ListCellRendererNextSchedules extends AbstractWebListCellRendererWithSeparatorTripleLine implements ListCellRenderer<Calendar> {
   public ListCellRendererNextSchedules() {
      super(new AbstractWebListCellRendererWithSeparatorTripleLine.Builder().setRatioBottomLine(0.8));
      Arrays.asList(this.top, this.middle, this.bottom).stream().forEach(c -> {
         c.setHorizontalAlignment(0);
         c.setHorizontalTextPosition(0);
      });
      this.content.setBorder(new EmptyBorder(0, 0, 2, 0));
   }

   public Component getListCellRendererComponent(JList list, Calendar c, int index, boolean isSelected, boolean cellHasFocus) {
      String arg1 = DateLG.SDF_D_S.format(c.getTime());
      String arg2 = String.format("(%s) %s", c.getDisplayName(7, 1, Locale.getDefault()), DateLG.SDF_T_S.format(c.getTime()));
      return super.getListCellRendererComponent(list, arg1, arg2, this.getDesc(c), index, isSelected, cellHasFocus);
   }

   private String getDesc(Calendar date) {
      Calendar now = Calendar.getInstance();
      if (now.after(date)) {
         return "";
      } else if (DateLG.isToday(date)) {
         return I18N.WORD_TODAY.val();
      } else {
         LocalDate start = DateLG.toLocalDate(now);
         LocalDate end = DateLG.toLocalDate(date);
         long days = ChronoUnit.DAYS.between(start, end);
         return days <= 60L
            ? (days == 1L ? I18N.MSG_IN_ONE_DAY.val() : I18N.MSG_IN_X_DAYS.format(days))
            : I18N.MSG_IN_X_MONTHS.format(ChronoUnit.MONTHS.between(start, end));
      }
   }
}
