package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class TableCellRendererScheduleMember extends DefaultTableCellRenderer {
   private final Color foreground;

   public TableCellRendererScheduleMember() {
      this.setIconTextGap(6);
      this.foreground = this.getForeground();
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (!value.toString().equals(I18N.WORD_RESPONSIBLE.val())) {
         try {
            boolean active = value.toString().charAt(1) == 't';
            BufferedImage ic = Member.EnumGroups.values()[Integer.parseInt(value.toString().substring(0, 1))].image;
            this.setIcon(new ImageIcon(active ? ic : Img.opacity(ic, 0.5F)));
            this.setForeground(active ? this.foreground : StyleUI.FOREGROUND_DISABLED_OBJECT.darker());
            value = value.toString().substring(2);
         } catch (Exception var9) {
            this.setIcon(null);
         }
      } else {
         this.setForeground(this.foreground);
         this.setIcon(null);
      }

      this.setBackground(UIManager.getLookAndFeelDefaults().getColor(row % 2 == 1 ? "Table.alternateRowColor" : "Table.background"));
      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
   }
}
