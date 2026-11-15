package com.limagiran.holyrics.transition;

import com.alee.utils.swing.WebTimer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class MyImageTransition extends JComponent implements ActionListener {
   protected BufferedImage currentImage = null;
   protected BufferedImage otherImage = null;
   protected CustomTransitionEffect transitionEffect;
   protected WebTimer animator = null;
   protected boolean animating = false;

   public MyImageTransition() {
      this(null, null);
   }

   public MyImageTransition(BufferedImage otherImage) {
      this(null, otherImage);
   }

   public MyImageTransition(BufferedImage currentImage, BufferedImage otherImage) {
      this.currentImage = currentImage;
      this.otherImage = otherImage;
   }

   public boolean isAnimating() {
      return this.animating || this.animator != null && this.animator.isRunning();
   }

   public BufferedImage getCurrentImage() {
      return this.currentImage;
   }

   public BufferedImage getOtherImage() {
      return this.otherImage;
   }

   public void putImage(BufferedImage otherImage, boolean animate) {
      synchronized (this) {
         this.currentImage = this.otherImage;
         this.otherImage = otherImage;
      }

      if (animate && !this.isAnimating() && this.currentImage != null) {
         this.animating = true;
         this.performTransition();
      } else {
         this.repaint();
      }
   }

   public CustomTransitionEffect getTransitionEffect() {
      return this.transitionEffect;
   }

   public void setTransitionEffect(CustomTransitionEffect transitionEffect) {
      this.transitionEffect = transitionEffect;
   }

   private void performTransition() {
      long animationDelay = this.transitionEffect != null ? this.transitionEffect.getAnimationDelay() : 0L;
      this.animator = new WebTimer("ImageTransition.animator", animationDelay, this);
      this.animator.setUseEventDispatchThread(false);
      this.animator.setNonBlockingStop(true);
      this.animator.start();
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      if (this.transitionEffect == null || this.transitionEffect.performAnimationTick(this)) {
         this.animator.stop();
         this.finishTransition();
      }
   }

   public void cancelTransition() {
      if (this.animator != null) {
         this.animator.stop();
      }
   }

   protected void finishTransition() {
      this.animating = false;
      this.repaint();
   }

   @Override
   protected void paintComponent(Graphics g) {
      BufferedImage _currentImage = this.currentImage;
      BufferedImage _otherImage = this.otherImage;
      Graphics2D g2d = (Graphics2D)g;
      if (this.transitionEffect != null && this.transitionEffect.isAnimating() && _currentImage != null && _otherImage != null) {
         this.transitionEffect.paint(g2d, this);
      } else if (this.animating && _currentImage != null) {
         g2d.drawImage(_currentImage, 0, 0, null);
      } else {
         if (!this.animating && _otherImage != null) {
            g2d.drawImage(_otherImage, 0, 0, null);
         }
      }
   }

   @Override
   public Dimension getPreferredSize() {
      BufferedImage _currentImage = this.currentImage;
      BufferedImage _otherImage = this.otherImage;
      if (_otherImage != null) {
         return new Dimension(_otherImage.getWidth(), _otherImage.getHeight());
      } else {
         return _otherImage != null ? new Dimension(_currentImage.getWidth(), _currentImage.getHeight()) : new Dimension(0, 0);
      }
   }

   public synchronized void clear() {
      this.currentImage = null;
      this.otherImage = null;
   }
}
