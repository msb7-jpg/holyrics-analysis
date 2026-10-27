package com.limagiran.holyrics.transition;

import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Fade extends RendererEffect {
   private static final String WEIGHT_NAME = "weight";
   private static final int WEIGHT_DEFAULT = 30;
   private static final int WEIGHT_MIN = 10;
   private static final int WEIGHT_MAX = 100;
   private float weight;
   private long currentPeriod;
   private long nextPeriod;

   @Override
   public void prepare2(MyImageTransition imageTransition, CustomTransitionEffect owner) {
      this.weight = (float)getWeight(owner.transitionSettings) / 100.0F;
      this.currentPeriod = (long)((float)owner.transitionSettings.getDuration() * this.weight);
      this.nextPeriod = (long)((float)owner.transitionSettings.getDuration() * (1.0F - this.weight));
   }

   @Override
   public void paint(Graphics2D g, BufferedImage current, BufferedImage other, CustomTransitionEffect owner) {
      float f = (float)owner.getMilli() / (float)this.currentPeriod;
      float f2 = (float)(owner.getMilli() - this.currentPeriod) / (float)this.nextPeriod;
      float alpha = owner.getMilli() <= this.currentPeriod ? f : f2;
      if (owner.musicTextOrBible) {
         if (owner.getMilli() <= this.currentPeriod) {
            G2Utils.setupAlphaComposite(g, Math.abs(alpha - 1.0F));
            g.drawImage(current, 0, 0, null);
         } else {
            G2Utils.setupAlphaComposite(g, alpha);
            g.drawImage(other, 0, 0, null);
         }
      } else {
         g.drawImage(other, 0, 0, null);
         if (owner.getMilli() <= this.currentPeriod) {
            G2Utils.setupAlphaComposite(g, Math.abs(alpha - 1.0F));
            g.drawImage(current, 0, 0, null);
         }
      }
   }

   public static int getWeight(TransitionSettings set) {
      return set.isAllRandom() ? 30 : Utils.range(set.getInt("weight", 30), 10, 100);
   }

   public static WebPanel getPanelSet(TransitionSettings set) {
      DefaultPanelSet p = new DefaultPanelSet(set);
      TransitionType.setupDefaultSlider(p, set, "weight", getWeight(set), 10, 100, I18N.MSG_DIVIDER_POINT.val());
      return p;
   }
}
