package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.MemberController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Member;
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

public class TableModelScheduleOnlyMember extends AbstractTableModel {
   private boolean onlyActive;

   public TableModelScheduleOnlyMember(boolean onlyActive) {
      this.onlyActive = onlyActive;
   }

   public Member getMember(int row) {
      List<Member> list = MemberController.getAll(this.onlyActive);
      if (row == 0) {
         return null;
      } else {
         return row > 0 && row <= list.size() ? list.get(row - 1) : null;
      }
   }

   public String getMemberName(int row) {
      try {
         return this.getMember(row).getName();
      } catch (Exception var3) {
         return "";
      }
   }

   @Override
   public int getColumnCount() {
      return 1;
   }

   @Override
   public int getRowCount() {
      return MemberController.getAll(this.onlyActive).size() + 1;
   }

   @Override
   public String getColumnName(int columnIndex) {
      return I18N.WORD_NAME.val();
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return String.class;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      try {
         Member m;
         if ((m = this.getMember(rowIndex)) != null) {
            Member.EnumGroups en = m.getGroups().isEmpty() ? Member.EnumGroups.OTHERS : m.getGroups().first();
            return en.ordinal() + (m.isActive() ? "t" : "f") + m.toString();
         } else {
            return I18N.WORD_RESPONSIBLE;
         }
      } catch (Exception var5) {
         return "";
      }
   }

   @Override
   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
   }

   public void refresh() {
      this.fireTableDataChanged();
   }

   public void setOnlyActive(boolean onlyActive) {
      this.onlyActive = onlyActive;
      this.refresh();
   }

   public static void configTable(JTable table) {
      JTableHeader header = table.getTableHeader();
      Font f = table.getFont();
      header.setFont(new Font(f.getName(), 1, f.getSize() + 2));
      SwingUtils.autoResizeByHeader(table, 15);
      TableCellRendererScheduleAlternateRowColor renderer = new TableCellRendererScheduleAlternateRowColor();
      renderer.setHorizontalAlignment(0);
      table.setDefaultRenderer(Object.class, renderer);
      table.setDefaultRenderer(String.class, new TableCellRendererScheduleMember());
      TableColumnModel tcm = table.getColumnModel();
      if (tcm.getColumnCount() > 0) {
         int width = 15;

         for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer render = table.getCellRenderer(row, 0);
            Component comp = table.prepareRenderer(render, row, 0);
            width = Math.max(comp.getPreferredSize().width + 5, width);
         }

         tcm.getColumn(0).setPreferredWidth(width);
      }

      header.setPreferredSize(new Dimension(10000, header.getFontMetrics(header.getFont()).getHeight() * 2));
   }
}
