package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class ListCellRendererPresentationText extends AbstractWebListCellRendererWithSeparator implements ListCellRenderer<Presentation> {
   public ListCellRendererPresentationText() {
      super(true);
      this.init();
   }

   private void init() {
      this.view.setHorizontalAlignment(0);
      this.content.setBorder(new EmptyBorder(4, 4, 4, 4));
      this.view.setMaximumHeight(200);
      this.view.setMaximumWidth(400);
      this.view.setMinimumHeight(50);
      this.view.setMinimumWidth(75);
      this.view.setBackground(StyleUI.CELL_RENDERER_TEXT_MODE);
   }

   public Component getListCellRendererComponent(JList list, Presentation p, int index, boolean isSelected, boolean cellHasFocus) {
      String formattingLabel = p.text.getFormattingLabel();
      if (!formattingLabel.isEmpty()) {
         String color = Utils.getHexColor(WebDecoratedImageWithLabelSelectableCustom.getLabelColor(formattingLabel));
         formattingLabel = "<div style='padding-bottom:4px'><span style='font-size:14px;font-famyli:Trebuchet MS;color:#"
            + color
            + "'><b>"
            + formattingLabel
            + "</b></span></div>";
      }

      String value = String.format("<html><style> div {text-align:center;}</style>%s<div>%s</div>", formattingLabel, p.text.getText().replace("\n", "<br>"));
      this.view.setOpaque(isSelected);
      return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
   }
}
