package com.limagiran.holyrics.swing;

import com.alee.laf.table.renderers.WebTableCellRenderer;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;

public class TableCellRendererReportStringTrueFalse extends WebTableCellRenderer {
   private final Color foreground = this.getForeground();

   public TableCellRendererReportStringTrueFalse() {
      this.setHorizontalAlignment(0);
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      try {
         this.setForeground(value.toString().charAt(0) == 't' ? this.foreground : StyleUI.FOREGROUND_DISABLED_OBJECT.darker());
         value = value.toString().substring(1);
      } catch (Exception var8) {
         this.setForeground(this.foreground);
      }

      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (!isSelected && !hasFocus) {
         c.setBackground(UIManager.getLookAndFeelDefaults().getColor(row % 2 == 1 ? "Table.alternateRowColor" : "Table.background"));
      }

      return c;
   }
}
