package com.limagiran.holyrics.report.model;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.swing.TableCellRendererReportString;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class TableModelReportScheduleMusic extends AbstractTableModel {
   private final List<ReportScheduleMusic> rows;
   private final String[] colsName = new String[]{I18N.WORD_MUSIC.val(), I18N.WORD_TIMES.val()};

   public TableModelReportScheduleMusic(List<ReportScheduleMusic> rows) {
      this.rows = rows;
   }

   public ReportScheduleMusic getRow(int row) {
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
      return columnIndex == 0 ? String.class : Long.class;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      try {
         switch (columnIndex) {
            case 0:
               return this.rows.get(rowIndex).music;
            case 1:
               return this.rows.get(rowIndex).size();
         }
      } catch (Exception var4) {
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
      SwingUtils.autoResizeByHeader(table, 15);
      table.setAutoResizeMode(2);
      TableColumnModel tcm = table.getColumnModel();
      if (tcm.getColumnCount() == 2) {
         int width = 15;

         for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, 0);
            Component comp = table.prepareRenderer(renderer, row, 0);
            width = Math.max(comp.getPreferredSize().width + 5, width);
         }

         tcm.getColumn(0).setPreferredWidth(width);
         tcm.getColumn(1).setMinWidth(100);
      }

      header.setPreferredSize(new Dimension(10000, header.getFontMetrics(header.getFont()).getHeight() * 2));
      table.setDefaultRenderer(Long.class, new TableCellRendererReportString(true));
      table.setDefaultRenderer(String.class, new TableCellRendererReportString(false));
   }
}
