package com.limagiran.holyrics.event;

public interface ThemeSettingsPanelListener {
   void themeChanged();

   void backgroundChanged();

   void saveButtonClicked();

   void repaint();

   void setPaintMargin(boolean var1);

   public static enum EnumChange {
      BACKGROUND_IMAGE,
      VELOCITY,
      OPACITY,
      BACKGROUND_POSITION,
      FONT_NAME,
      TRANSPARENCY_COLOR,
      FONT_COLOR,
      FONT_STYLE,
      FONT_SIZE,
      FONT_LINE_SPACING,
      TEXT_ALIGN,
      TEXT_ALIGNV,
      MARGIN,
      OUTLINE_COLOR,
      OUTLINE_SIZE,
      BRIGHTNESS_COLOR,
      BRIGHTNESS_SIZE,
      SHADOW_COLOR,
      SHADOW_X,
      SHADOW_Y;
   }
}
