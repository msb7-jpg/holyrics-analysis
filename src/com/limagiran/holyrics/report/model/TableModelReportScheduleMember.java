package com.limagiran.holyrics.report.model;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.swing.TableCellRendererReportMember;
import com.limagiran.holyrics.swing.TableCellRendererReportStringTrueFalse;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

public class TableModelReportScheduleMember extends AbstractTableModel {
   private final List<ReportScheduleMember> rows;
   private final String[] colsName = new String[]{I18N.WORD_MEMBER.val(), I18N.WORD_SCHEDULED.val(), I18N.WORD_RESPONSIBLE.val()};

   public TableModelReportScheduleMember(List<ReportScheduleMember> rows) {
      this.rows = rows;
   }

   public Member getMember(int row) {
      try {
         return this.rows.get(row).member;
      } catch (Exception var3) {
         return null;
      }
   }

   public ReportScheduleMember getRow(int row) {
      try {
         return this.rows.get(row);
      } catch (Exception var3) {
         return null;
      }
   }

   @Override
   public int getColumnCount() {
      return this.colsName.length;
   }

   @Override
   public int getRowCount() {
      return this.rows.size();
   }

   @Override
   public String getColumnName(int columnIndex) {
      return this.colsName[columnIndex];
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return columnIndex == 0 ? Member.class : String.class;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      try {
         Member m = this.rows.get(rowIndex).member;
         String active = m.isActive() ? "t" : "f";
         switch (columnIndex) {
            case 0:
               Member.EnumGroups en = m.getGroups().isEmpty() ? Member.EnumGroups.OTHERS : m.getGroups().first();
               return en.ordinal() + active + m.toString();
            case 1:
               return active + Long.toString(this.rows.get(rowIndex).getScheduledCount());
            case 2:
               return active + Long.toString(this.rows.get(rowIndex).getResponsibleCount());
         }
      } catch (Exception var6) {
      }

      return "";
   }

   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      this.fireTableDataChanged();
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
   }

   public void refresh() {
      this.fireTableDataChanged();
   }

   public static void configTable(JTable table) {
      JTableHeader header = table.getTableHeader();
      header.setFont(new Font("Georgia", 1, 15));
      table.setDefaultRenderer(String.class, new TableCellRendererReportStringTrueFalse());
      table.setDefaultRenderer(Member.class, new TableCellRendererReportMember());
      SwingUtils.autoResizeByHeader(table, 15);
      table.setAutoResizeMode(2);
      header.setPreferredSize(new Dimension(10000, header.getFontMetrics(header.getFont()).getHeight() * 2));
   }
}
