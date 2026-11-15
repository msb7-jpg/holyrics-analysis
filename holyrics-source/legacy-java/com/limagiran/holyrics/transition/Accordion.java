package com.limagiran.holyrics.transition;

import com.alee.laf.panel.WebPanel;
import java.awt.Graphics2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;

public class Accordion extends RendererEffect {
   private Direction direction;
   private static final Direction[] DIRECTIONS = new Direction[]{Direction.RANDOM, Direction.HORIZONTAL, Direction.VERTICAL};

   @Override
   public void prepare2(MyImageTransition imageTransition, CustomTransitionEffect owner) {
      this.direction = Direction.getOrDefault(owner.transitionSettings, Direction.HORIZONTAL).getSpecific();
   }

   @Override
   public void paint(Graphics2D g, BufferedImage current, BufferedImage other, CustomTransitionEffect owner) {
      int width = other.getWidth();
      int height = other.getHeight();
      this.direction = this.direction != null ? this.direction : Direction.random();
      if (this.direction.isHorizontal()) {
         if (owner.isFirstHalf()) {
            int w = (int)((float)width * (1.0F - owner.getProgress() * 2.0F));
            g.drawImage(current, (width - w) / 2, 0, w, height, null);
         } else {
            int w = (int)((float)width * (owner.getProgress() * 2.0F % 1.0F));
            g.drawImage(other, (width - w) / 2, 0, w, height, null);
         }
      } else if (owner.isFirstHalf()) {
         int h = (int)((float)height * (1.0F - owner.getProgress() * 2.0F));
         g.drawImage(current, 0, (height - h) / 2, width, h, null);
      } else {
         int h = (int)((float)height * (owner.getProgress() * 2.0F % 1.0F));
         g.drawImage(other, 0, (height - h) / 2, width, h, null);
      }
   }

   public static WebPanel getPanelSet(TransitionSettings set) {
      DefaultPanelSet p = new DefaultPanelSet(set);
      Direction.setupDefaultComboBox(p, set, DIRECTIONS, Direction.HORIZONTAL);
      return p;
   }

   public static Double circleCoordinate(double radius1, double radius2, double progress) {
      double angle = Math.PI * progress + Math.PI;
      return new Double(radius1 * Math.cos(angle), radius2 * Math.sin(angle));
   }
}
