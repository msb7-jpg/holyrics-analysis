package com.limagiran.holyrics.swing;

import com.alee.laf.table.renderers.WebTableCellRenderer;
import com.limagiran.holyrics.util.BooleanOptional;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;

public class TableCellRendererReportString extends WebTableCellRenderer {
   public TableCellRendererReportString(boolean alignCenter) {
      BooleanOptional.runIfTrue(alignCenter, () -> this.setHorizontalAlignment(0));
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (!isSelected && !hasFocus) {
         c.setBackground(UIManager.getLookAndFeelDefaults().getColor(row % 2 == 1 ? "Table.alternateRowColor" : "Table.background"));
      }

      return c;
   }
}
