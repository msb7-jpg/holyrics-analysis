package com.limagiran.holyrics.playback;

import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.transition.CustomTransitionEffect;
import com.limagiran.holyrics.transition.MyImageTransition;
import com.limagiran.holyrics.transition.TransitionSettings;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TransitionEffectRenderer extends CustomTransitionEffect {
   public TransitionEffectRenderer(
      ScreenDeviceSettings.EnumDeviceType deviceType, ScreenDeviceSettings screenDeviceSettings, TransitionSettings transitionSettings, Theme theme
   ) {
      super(deviceType, screenDeviceSettings, transitionSettings, theme, true);
   }

   @Override
   public void prepareAnimation(MyImageTransition imageTransition) {
      this.progress = 0.0F;
      this.renderer = getRenderer(this.transitionSettings);
      if (this.renderer != null) {
         this.renderer.prepare(imageTransition, this);
      }
   }

   @Override
   public boolean performAnimation(MyImageTransition imageTransition) {
      return false;
   }

   public boolean isContinue(long milli) {
      return milli < (long)this.transitionSettings.getDuration();
   }

   public void paint(Graphics2D g2d, MyImageTransitionRenderer transition, long milli) {
      this.milli = milli;
      this.progress = Utils.range((float)this.milli / (float)this.transitionSettings.getDuration(), 0.0F, 0.999F);
      if (this.renderer != null) {
         this.renderer.paint(g2d, transition, this);
      }
   }

   public BufferedImage createImage(MyImageTransitionRenderer transition, long milli) {
      Dimension size = transition.getPreferredSize();
      BufferedImage image = new BufferedImage(size.width, size.height, 2);
      Graphics2D g = image.createGraphics();
      this.paint(g, transition, milli);
      g.dispose();
      return image;
   }

   public void paintWithBackground(Graphics2D g2d, MyImageTransitionRenderer transition, Presentation presentation, long milli) {
      Presentation pBlank = presentation.blank();
      pBlank.text.setTitleAndArtist(presentation.text.getTitleAndArtist()).setPageCount(presentation.text.getPageCount());
      BufferedImage background = PresentationSlideUtils.createImage(
         this.screenDeviceSettings, pBlank, transition.getPreferredSize(), !presentation.theme.getBackground().isVideo()
      );
      g2d.drawImage(background, 0, 0, null);
      this.paint(g2d, transition, milli);
   }

   public BufferedImage createImageWithBackground(MyImageTransitionRenderer transition, Presentation presentation, long milli) {
      Dimension size = transition.getPreferredSize();
      BufferedImage image = new BufferedImage(size.width, size.height, 2);
      Graphics2D g = image.createGraphics();
      this.paintWithBackground(g, transition, presentation, milli);
      g.dispose();
      return image;
   }

   @Override
   public void paint(Graphics2D g2d, MyImageTransition transition) {
   }

   @Override
   public long getStart() {
      return System.currentTimeMillis() - this.milli;
   }
}
