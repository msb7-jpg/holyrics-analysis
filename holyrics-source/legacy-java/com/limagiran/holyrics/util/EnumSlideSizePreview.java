package com.limagiran.holyrics.util;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.BackgroundColor;
import com.limagiran.holyrics.model.theme.Theme;
import java.awt.Color;
import java.awt.Dimension;

public enum EnumSlideSizePreview {
   SMALL(new Dimension(192, 108), I18N.WORD_SMALL_M.val()),
   MEDIUM(new Dimension(240, 135), I18N.WORD_MEDIUM_M.val()),
   BIG(new Dimension(320, 180), I18N.WORD_BIG.val());

   public static final Theme THEME_UNSELECTED = Theme.create().setSize(0.15).setMargin(new double[]{0.01, 0.01, 0.01, 0.01});
   public static final Theme THEME_SELECTED = THEME_UNSELECTED.cloneTheme().setBackground(new BackgroundColor(Color.GRAY));
   public final Dimension size;
   public final String name;

   private EnumSlideSizePreview(Dimension size, String name) {
      this.size = size;
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }
}
