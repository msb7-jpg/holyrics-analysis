package com.limagiran.holyrics.swing;

import com.alee.laf.table.renderers.WebTableCellRenderer;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.UIManager;

public class TableCellRendererReportMember extends WebTableCellRenderer {
   private final Color foreground;

   public TableCellRendererReportMember() {
      this.setIconTextGap(6);
      this.foreground = this.getForeground();
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      try {
         boolean active = value.toString().charAt(1) == 't';
         BufferedImage ic = Member.EnumGroups.values()[Integer.parseInt(value.toString().substring(0, 1))].image;
         this.setIcon(new ImageIcon(active ? ic : Img.opacity(ic, 0.5F)));
         this.setForeground(active ? this.foreground : StyleUI.FOREGROUND_DISABLED_OBJECT.darker());
         value = value.toString().substring(2);
      } catch (Exception var9) {
         this.setForeground(this.foreground);
         this.setIcon(null);
      }

      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (!isSelected && !hasFocus) {
         c.setBackground(UIManager.getLookAndFeelDefaults().getColor(row % 2 == 1 ? "Table.alternateRowColor" : "Table.background"));
      }

      return c;
   }
}
