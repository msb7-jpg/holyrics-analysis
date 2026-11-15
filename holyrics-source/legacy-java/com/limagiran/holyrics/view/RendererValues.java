package com.limagiran.holyrics.view;

import com.limagiran.holyrics.model.theme.Theme;

class RendererValues {
   protected Object renderer;
   protected Theme theme;
   protected boolean transition;

   public RendererValues(Object renderer, Theme theme, boolean transition) {
      this.renderer = renderer;
      this.theme = theme;
      this.transition = transition;
   }

   public static RendererValues create(Object renderer, Theme theme, boolean transition) {
      return new RendererValues(renderer, theme, transition);
   }

   public void set(Object renderer, Theme theme, boolean transition) {
      this.renderer = renderer;
      this.theme = theme;
      this.transition = transition;
   }
}
