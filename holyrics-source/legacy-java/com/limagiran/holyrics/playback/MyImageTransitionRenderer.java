package com.limagiran.holyrics.playback;

import com.limagiran.holyrics.transition.MyImageTransition;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class MyImageTransitionRenderer extends MyImageTransition {
   public MyImageTransitionRenderer() {
   }

   public MyImageTransitionRenderer(BufferedImage otherImage) {
      super(otherImage);
   }

   public MyImageTransitionRenderer(BufferedImage currentImage, BufferedImage otherImage) {
      super(currentImage, otherImage);
   }

   public synchronized void setCurrentImage(BufferedImage currentImage) {
      this.currentImage = currentImage;
   }

   public synchronized void setOtherImage(BufferedImage otherImage) {
      this.otherImage = otherImage;
   }

   @Override
   protected void paintComponent(Graphics g) {
   }
}
