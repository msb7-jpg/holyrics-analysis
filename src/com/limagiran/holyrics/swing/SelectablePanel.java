package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.StyleUI;
import javax.swing.JComponent;

public interface SelectablePanel {
   JComponent getPanel();

   String getId();

   default boolean isSelected() {
      JComponent c = this.getPanel();
      return c instanceof WebDecoratedImageSelectableCustom
         ? ((WebDecoratedImageSelectableCustom)c).isSelected()
         : c.getBorder().equals(StyleUI.BORDER_SELECTED);
   }

   default void setSelected(boolean flag) {
      JComponent c = this.getPanel();
      if (c instanceof WebDecoratedImageSelectableCustom) {
         ((WebDecoratedImageSelectableCustom)c).setSelected(flag);
      } else {
         c.setBorder(flag ? StyleUI.BORDER_SELECTED : StyleUI.BORDER_UNSELECTED);
      }
   }

   default void clear() {
   }
}
