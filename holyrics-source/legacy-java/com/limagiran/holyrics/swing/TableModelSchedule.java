package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.model.Schedule;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class TableModelSchedule extends AbstractTableModel {
   private static final String FORMAT_ACTIVE_TRUE = "&nbsp;%s (%s)&nbsp;<br>%s";
   private static final String FORMAT_ACTIVE_FALSE = "<html><span style=\"color:#"
      + Utils.getHexColor(StyleUI.FOREGROUND_DISABLED_OBJECT)
      + "\">&nbsp;%s (%s)&nbsp;<br>%s</span>";
   private final List<Member> rows;
   private final List<Schedule> cols;
   private final String[] colsName;

   public TableModelSchedule(List<Member> rows, List<Schedule> cols) {
      this.rows = rows;
      this.colsName = new String[cols.size()];
      this.cols = cols;
      this.init();
   }

   private void init() {
      for (int i = 0; i < this.colsName.length; i++) {
         Calendar c = this.cols.get(i).getDate();
         String name = String.format(
            this.cols.get(i).isActive() ? "&nbsp;%s (%s)&nbsp;<br>%s" : FORMAT_ACTIVE_FALSE,
            c.get(5),
            c.getDisplayName(7, 1, Locale.getDefault()),
            DateLG.SDF_T_S.format(c.getTime())
         );
         this.colsName[i] = name;
      }
   }

   public Schedule getSchedule(int col) {
      return col >= 0 && col < this.cols.size() ? this.cols.get(col) : null;
   }

   public Member getMember(int row) {
      return row > 0 && row <= this.rows.size() ? this.rows.get(row - 1) : null;
   }

   @Override
   public int getColumnCount() {
      return this.colsName.length;
   }

   @Override
   public int getRowCount() {
      return this.rows.size() + 1;
   }

   @Override
   public String getColumnName(int columnIndex) {
      return SwingUtils.getHTMLCenterJLabel(this.colsName[columnIndex]);
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return Object.class;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      try {
         return rowIndex == 0 ? this.getSchedule(columnIndex).getResponsibleAsMember() : this.getSchedule(columnIndex).isScheduled(this.getMember(rowIndex));
      } catch (Exception var4) {
         return false;
      }
   }

   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      Optional.ofNullable(this.getSchedule(columnIndex))
         .ifPresent(
            s -> {
               try {
                  if (rowIndex == 0) {
                     if (aValue != null) {
                        s.setResponsible(((Member)aValue).id);
                        s.setScheduled((Member)aValue, true);
                     } else {
                        s.setResponsible(null);
                     }
                  } else {
                     Optional.ofNullable(this.getMember(rowIndex))
                        .ifPresent(m -> BooleanOptional.runIfTrue(!s.isResponsible(m), () -> s.setScheduled(m, Boolean.parseBoolean(String.valueOf(aValue)))));
                  }

                  ScheduleController.save();
               } catch (Exception var5) {
               }
            }
         );
      this.fireTableDataChanged();
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return true;
   }

   public void refresh() {
      this.fireTableDataChanged();
   }

   public static void configTable(JTable table) {
      JTableHeader header = table.getTableHeader();
      Font f = table.getFont();
      header.setFont(new Font(f.getName(), 1, f.getSize() + 2));
      SwingUtils.autoResizeByHeader(table, 15);
      table.setDefaultRenderer(Object.class, new TableCellRendererScheduleObject());
      table.setDefaultRenderer(String.class, new TableCellRendererScheduleAlternateRowColor());
      TableColumnModel tcm = table.getColumnModel();
      header.setPreferredSize(new Dimension(10000, header.getFontMetrics(header.getFont()).getHeight() * 2));

      for (int i = 0; i < tcm.getColumnCount(); i++) {
         tcm.getColumn(i).setCellEditor(new TableCellEditorCustom());
      }
   }
}
