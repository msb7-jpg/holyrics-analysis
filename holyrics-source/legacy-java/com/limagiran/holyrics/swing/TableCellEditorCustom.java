package com.limagiran.holyrics.swing;

import com.alee.laf.combobox.WebComboBoxCellRenderer;
import com.limagiran.holyrics.control.MemberController;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class TableCellEditorCustom extends AbstractCellEditor implements TableCellEditor {
   private final DefaultCellEditor cmbbx;
   private final DefaultCellEditor chkbx;
   private TableCellEditor editor;

   public TableCellEditorCustom() {
      List list = MemberController.getAll(false);
      list.add(0, null);
      JComboBox<Member> cmb = new JComboBox<>(list.toArray());
      cmb.setRenderer(new WebComboBoxCellRenderer() {
         private final Color foreground = this.getBoxRenderer().getForeground();

         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Color color = value instanceof Member && !((Member)value).isActive() ? StyleUI.FOREGROUND_DISABLED_OBJECT : this.foreground;
            list.setForeground(color);
            list.setSelectionForeground(color);
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         }
      });
      cmb.setFocusable(false);
      this.cmbbx = new DefaultCellEditor(cmb);
      cmb.addItemListener(e -> this.stopCellEditing());
      JCheckBox chk = new JCheckBox();
      chk.setFocusable(false);
      chk.addItemListener(e -> this.stopCellEditing());
      chk.setHorizontalAlignment(0);
      this.chkbx = new DefaultCellEditor(chk);
   }

   @Override
   public Object getCellEditorValue() {
      return this.editor != null ? this.editor.getCellEditorValue() : null;
   }

   @Override
   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      this.editor = row == 0 ? this.cmbbx : this.chkbx;
      if (!isSelected) {
         Component c = this.editor.getTableCellEditorComponent(table, value, isSelected, row, column);
         c.setBackground(row % 2 == 1 ? StyleUI.COLOR_ALTERNATE_ROW : Color.WHITE);
         return c;
      } else {
         return this.editor.getTableCellEditorComponent(table, value, isSelected, row, column);
      }
   }
}
