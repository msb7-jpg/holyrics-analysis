package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.playback.ListModelPlayBack;
import com.limagiran.holyrics.util.StringUtils;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererPlayBack extends AbstractWebListCellRendererWithSeparator implements ListCellRenderer<String> {
   public final ListModelPlayBack model;

   public ListCellRendererPlayBack(ListModelPlayBack model) {
      super(false, true, false, false);
      this.model = model;
      this.init();
   }

   private void init() {
      this.view.setFont(new Font("Arial", 0, 14));
      this.content.setMargin(2, 4, 2, 4);
   }

   @Override
   public Component getListCellRendererComponent(JList list, String string, int index, boolean isSelected, boolean cellHasFocus) {
      return super.getListCellRendererComponent(list, this.formatText(string), index, isSelected, cellHasFocus);
   }

   private String formatText(String text) {
      try {
         text = text.contains(".") ? text.substring(0, text.lastIndexOf(".")) : text;
         return "<html>" + StringUtils.setupHighlight(text, this.model.getFilter());
      } catch (Exception var3) {
         return text;
      }
   }
}
