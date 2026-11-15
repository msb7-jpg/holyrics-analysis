package com.limagiran.holyrics.transition;

import com.alee.laf.panel.WebPanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Expande extends RendererEffect {
   @Override
   public void prepare2(MyImageTransition imageTransition, CustomTransitionEffect owner) {
   }

   @Override
   public void paint(Graphics2D g, BufferedImage current, BufferedImage other, CustomTransitionEffect owner) {
      int width = other.getWidth();
      int height = other.getHeight();
      int h = (int)((float)height * (owner.getProgress() * 2.0F % 1.0F));
      g.drawImage(other, (height - h) / 2, 0, width, h, null);
   }

   public static WebPanel getPanelSet(TransitionSettings set) {
      DefaultPanelSet p = new DefaultPanelSet(set);
      set.setValue("...", "...");
      return p;
   }
}
