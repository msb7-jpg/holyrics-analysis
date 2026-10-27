package com.limagiran.holyrics.transition;

import com.alee.global.StyleConstants;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class CustomTransitionEffect implements Values {
   public static final BufferedImage EMPTY = new BufferedImage(1, 1, 10);
   public static final BufferedImage BI1X1 = new BufferedImage(1, 1, 10);
   protected final ScreenDeviceSettings.EnumDeviceType deviceType;
   protected final ScreenDeviceSettings screenDeviceSettings;
   protected final TransitionSettings transitionSettings;
   protected RendererEffect renderer;
   protected float progress;
   protected long start;
   protected long milli;
   private Theme theme;
   private final boolean alwaysEnable;
   public final boolean musicTextOrBible;
   protected boolean animating = false;
   private static final Map<String, RendererEffect> MAP_RENDERER = new HashMap<>();

   public CustomTransitionEffect(
      ScreenDeviceSettings.EnumDeviceType deviceType,
      ScreenDeviceSettings screenDeviceSettings,
      TransitionSettings transitionSettings,
      Theme theme,
      boolean alwaysEnable
   ) {
      this.deviceType = deviceType;
      this.musicTextOrBible = this.deviceType.isMusicTextOrBible();
      this.screenDeviceSettings = screenDeviceSettings;
      this.transitionSettings = transitionSettings;
      this.theme = theme;
      this.alwaysEnable = alwaysEnable;
   }

   public CustomTransitionEffect(
      ScreenDeviceSettings.EnumDeviceType deviceType, ScreenDeviceSettings screenDeviceSettings, TransitionSettings transitionSettings, Theme theme
   ) {
      this(deviceType, screenDeviceSettings, transitionSettings, theme, false);
   }

   public boolean performAnimationTick(MyImageTransition transition) {
      if (!this.animating) {
         this.animating = true;
         this.prepareAnimation(transition);
         return false;
      } else {
         boolean finished = this.performAnimation(transition);
         if (finished) {
            this.animating = false;
         }

         return finished;
      }
   }

   public long getAnimationDelay() {
      return StyleConstants.animationDelay;
   }

   public boolean isAnimating() {
      return this.animating;
   }

   public void prepareAnimation(MyImageTransition imageTransition) {
      this.progress = 0.0F;
      this.start = System.currentTimeMillis();
      this.milli = 0L;
      this.renderer = getRenderer(this.transitionSettings);
      if (this.renderer != null) {
         this.renderer.prepare(imageTransition, this);
      }

      if (this.transitionSettings.isEnabled() || this.alwaysEnable) {
         imageTransition.repaint();
      }
   }

   public boolean performAnimation(MyImageTransition imageTransition) {
      if (!this.transitionSettings.isEnabled() && !this.alwaysEnable) {
         return true;
      } else {
         this.milli = System.currentTimeMillis() - this.start;
         if (this.milli >= (long)this.transitionSettings.getDuration()) {
            return true;
         } else {
            this.progress = Utils.range((float)this.milli / (float)this.transitionSettings.getDuration(), 0.0F, 1.0F);
            imageTransition.repaint();
            return false;
         }
      }
   }

   public void paint(Graphics2D g2d, MyImageTransition transition) {
      if (this.renderer != null) {
         this.renderer.paint(g2d, transition, this);
      }
   }

   public float getProgress() {
      return this.progress;
   }

   public boolean isFirstHalf() {
      return this.progress <= 0.5F;
   }

   public long getStart() {
      return this.start;
   }

   public long getMilli() {
      return this.milli;
   }

   public TransitionSettings getTransitionSettings() {
      return this.transitionSettings;
   }

   public Theme getTheme() {
      return this.theme;
   }

   public void setTheme(Theme theme) {
      this.theme = theme;
   }

   public static RendererEffect getRenderer(TransitionSettings ts) {
      TransitionType type = ts.getType();
      if (type.isRandom()) {
         return Random.random(ts);
      } else {
         RendererEffect _return = MAP_RENDERER.get(ts.getType().name());
         if (_return != null) {
            return _return;
         } else {
            _return = type.rendererFactory.get();
            MAP_RENDERER.put(type.name(), _return);
            return _return;
         }
      }
   }
}
