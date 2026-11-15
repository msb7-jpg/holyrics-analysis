package com.limagiran.holyrics.report.model;

import com.limagiran.holyrics.util.SwingUtils;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class TableModelPlayedMusic extends AbstractTableModel {
   private final List<PlayedMusics> cols;
   private final String[] colsName;

   public TableModelPlayedMusic(List<PlayedMusics> cols) {
      this.cols = cols;
      this.colsName = cols.stream().map(p -> p.name).toArray(String[]::new);
      this.init();
   }

   private void init() {
   }

   @Override
   public int getColumnCount() {
      return this.colsName.length;
   }

   @Override
   public int getRowCount() {
      return this.cols.get(0).list.size();
   }

   @Override
   public String getColumnName(int columnIndex) {
      return this.colsName[columnIndex];
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return String.class;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      try {
         return this.cols.get(columnIndex).list.get(rowIndex);
      } catch (Exception var4) {
         return "";
      }
   }

   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      this.fireTableDataChanged();
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
   }

   public String getPlayedMusic(int col, int row) {
      try {
         return this.cols.get(col).list.get(row).getToolTipText();
      } catch (Exception var4) {
         return null;
      }
   }

   public void refresh() {
      this.fireTableDataChanged();
   }

   public static void configTable(JTable table) {
      JTableHeader header = table.getTableHeader();
      Font f = table.getFont();
      header.setFont(new Font(f.getName(), 1, f.getSize() + 2));
      SwingUtils.autoResize(table, SwingUtils.autoResizeByHeader(table, 15), 600);
      table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
         @Override
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
               c.setBackground(UIManager.getLookAndFeelDefaults().getColor(row % 2 == 1 ? "Table.alternateRowColor" : "Table.background"));
            }

            return c;
         }
      });
   }
}
