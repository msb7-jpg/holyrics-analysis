package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class TableCellRendererScheduleObject extends DefaultTableCellRenderer {
   private final Color foreground = this.getForeground();

   public TableCellRendererScheduleObject() {
      this.setHorizontalAlignment(0);
      this.setIconTextGap(6);
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      this.setBackground(UIManager.getLookAndFeelDefaults().getColor(row % 2 == 1 ? "Table.alternateRowColor" : "Table.background"));
      this.setForeground(value instanceof Member && !((Member)value).isActive() ? StyleUI.FOREGROUND_DISABLED_OBJECT : this.foreground);
      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
   }
}
