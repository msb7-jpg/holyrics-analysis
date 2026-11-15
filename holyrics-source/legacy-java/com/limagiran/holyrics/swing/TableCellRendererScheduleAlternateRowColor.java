package com.limagiran.holyrics.swing;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class TableCellRendererScheduleAlternateRowColor extends DefaultTableCellRenderer {
   public TableCellRendererScheduleAlternateRowColor() {
      this.setIconTextGap(6);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      this.setBackground(UIManager.getLookAndFeelDefaults().getColor(row % 2 == 1 ? "Table.alternateRowColor" : "Table.background"));
      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
   }
}
