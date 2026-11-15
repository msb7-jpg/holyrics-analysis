package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.view.PresentationSlideView;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;

public class ListCellRendererTheme extends PresentationSlideView implements ListCellRenderer<Theme> {
   public ListCellRendererTheme() {
      super(
         Theme.BLACK_SCREEN,
         new MusicController.Paragraph(Settings.EnumSettings.THEME_SAMPLE_TEXT._default.toString()),
         PresentationSlideView.EnumScreenType.WIDESCREEN,
         false
      );
   }

   public Component getListCellRendererComponent(JList list, Theme t, int index, boolean isSelected, boolean cellHasFocus) {
      this.setTheme(t);
      this.setBorder(isSelected ? new LineBorder(Color.RED, 2) : new LineBorder(Color.WHITE, 1));
      this.setEnabled(list.isEnabled());
      this.setOpaque(true);
      return this;
   }
}
