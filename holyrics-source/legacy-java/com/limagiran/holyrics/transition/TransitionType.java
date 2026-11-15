package com.limagiran.holyrics.transition;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.slider.WebSlider;
import com.alee.utils.ColorUtils;
import com.alee.utils.LafUtils;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.G2Utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum TransitionType {
   RANDOM(I18N.WORD_RANDOM.val(), 1000, Random::random, Random::getPanelSet),
   FADE("Fade", 1000, Fade::new, Fade::getPanelSet),
   SLIDE("Slide", 800, Slide::new, Slide::getPanelSet),
   ACCORDION("Accordion", 1000, Accordion::new, Accordion::getPanelSet),
   LINEAR_FADE("Fade Linear", 1600, LinearFade::new, LinearFade::getPanelSet),
   ZOOM("Zoom", 1000, Zoom::new, Zoom::getPanelSet),
   CURTAIN(I18N.WORD_CURTAIN.val(), 1800, Curtain::new, Curtain::getPanelSet);

   public final String name;
   public final int durationDefault;
   public final Supplier<RendererEffect> rendererFactory;
   public final Function<TransitionSettings, WebPanel> panelSetFactory;

   private TransitionType(String name, int durationDefault, Supplier<RendererEffect> rendererFactory, Function<TransitionSettings, WebPanel> panelSetFactory) {
      this.name = name;
      this.durationDefault = durationDefault;
      this.rendererFactory = rendererFactory;
      this.panelSetFactory = panelSetFactory;
   }

   @Override
   public String toString() {
      return this.name;
   }

   public static TransitionType getOrDefault(String name) {
      try {
         return valueOf(name);
      } catch (Exception var2) {
         return FADE;
      }
   }

   public static boolean exists(String name) {
      try {
         return valueOf(name) != null;
      } catch (Exception var2) {
         return false;
      }
   }

   public boolean isRandom() {
      return this == RANDOM;
   }

   public static void forEachExceptRandom(Consumer<TransitionType> consumer) {
      for (TransitionType tt : values()) {
         if (!tt.isRandom()) {
            consumer.accept(tt);
         }
      }
   }

   public static WebSlider setupDefaultSlider(
      DefaultPanelSet p, TransitionSettings set, String name, int value, int min, int max, String labelTitle, final Function<Integer, String> renderer
   ) {
      set.setValue(name, String.valueOf(value));
      WebSlider slider = new WebSlider(min, max, value) {
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension d = this.getSize();
            if (d.width > 0 && d.height > 0) {
               Graphics2D g2 = (Graphics2D)g;
               String s = renderer == null ? String.format("%02d%%", this.getValue()) : renderer.apply(this.getValue());
               Point p = G2Utils.centralize(new Rectangle(d), g2, s);
               g2.translate(p.x, p.y);
               LafUtils.paintTextEffect(g2, s, ColorUtils.removeAlpha(Color.BLACK), 3, -3.0, -3.0, false);
               g2.translate(-p.x, -p.y);
            }
         }
      };
      slider.setFontStyle(1);
      slider.setForeground(new Color(250, 250, 250));
      slider.addChangeListener(e -> set.setValue(name, String.valueOf(slider.getValue())));
      slider.setMaximumWidth(150);
      p.put(labelTitle, slider);
      return slider;
   }

   public static WebSlider setupDefaultSlider(DefaultPanelSet p, TransitionSettings set, String name, int value, int min, int max, String labelTitle) {
      return setupDefaultSlider(p, set, name, value, min, max, labelTitle, null);
   }

   public static WebCheckBox setupDefaultCheckBox(DefaultPanelSet p, TransitionSettings set, String name, boolean value, String labelTitle) {
      set.setValue(name, String.valueOf(value));
      WebCheckBox chkbx = new WebCheckBox(value);
      chkbx.setFocusable(false);
      chkbx.addChangeListener(e -> set.setValue(name, String.valueOf(chkbx.isSelected())));
      chkbx.setMaximumWidth(150);
      p.put(labelTitle, chkbx);
      return chkbx;
   }
}
