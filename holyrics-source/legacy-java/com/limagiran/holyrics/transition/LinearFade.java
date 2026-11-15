package com.limagiran.holyrics.transition;

import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.Utils;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class LinearFade extends RendererEffect {
   private static final String DISTANCE_NAME = "lf-distance";
   private static final int DISTANCE_DEFAULT = 30;
   private static final int DISTANCE_MIN = 5;
   private static final int DISTANCE_MAX = 90;
   private static final String FADE_NAME = "lf-fade";
   private static final int FADE_DEFAULT = 60;
   private static final int FADE_MIN = 2;
   private static final int FADE_MAX = 90;
   private float lightWidth;
   private float fade;
   private Direction direction;

   @Override
   public void prepare2(MyImageTransition imageTransition, CustomTransitionEffect owner) {
      this.lightWidth = (float)getDistance(owner.transitionSettings) / 100.0F;
      this.fade = (float)(90 - (getFade(owner.transitionSettings) - 2)) / 100.0F;
      this.direction = Direction.getOrDefault(owner.transitionSettings, Direction.HORIZONTAL).getSpecific();
   }

   @Override
   public void paint(Graphics2D g, BufferedImage current, BufferedImage other, CustomTransitionEffect owner) {
      boolean horizontal = this.direction.isHorizontal();
      boolean inverse = this.direction.isLeft() || this.direction.isUp();
      if (!owner.deviceType.isMusicTextOrBible()) {
         g.drawImage(current, 0, 0, null);
      }

      Img.applyLinearLight(g, current, other, owner.progress, this.lightWidth, this.fade, !horizontal, inverse);
   }

   public static int getDistance(TransitionSettings set) {
      return set.isAllRandom() ? 30 : Utils.range(set.getInt("lf-distance", 30), 5, 90);
   }

   public static int getFade(TransitionSettings set) {
      return set.isAllRandom() ? 60 : Utils.range(set.getInt("lf-fade", 60), 2, 90);
   }

   public static WebPanel getPanelSet(TransitionSettings set) {
      DefaultPanelSet p = new DefaultPanelSet(set);
      Direction.setupDefaultComboBox(p, set, Direction.values(), Direction.LEFT);
      TransitionType.setupDefaultSlider(p, set, "lf-distance", getDistance(set), 5, 90, I18N.WORD_DISTANCE.val());
      TransitionType.setupDefaultSlider(p, set, "lf-fade", getFade(set), 2, 90, "Fade");
      return p;
   }
}
