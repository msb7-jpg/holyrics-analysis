package com.limagiran.holyrics.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class DragComponentMouseAdapterWithRolloverAction extends DragComponentMouseAdapter {
   private final DragComponentMouseAdapterWithRolloverAction.DragComponentListenerWithRolloverAction listener2;
   private Component lastOpaque;
   private final Point p = new Point();
   private int moveScroll;

   public DragComponentMouseAdapterWithRolloverAction(DragComponentMouseAdapterWithRolloverAction.DragComponentListenerWithRolloverAction listener) {
      super(listener);
      this.listener2 = listener;
   }

   @Override
   public void mouseExited(MouseEvent e) {
      super.mouseExited(e);
      if (this.lastOpaque != null) {
         this.listener2.rollover(this.lastOpaque, false);
      }

      this.moveScroll = 0;
   }

   @Override
   public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      this.p.setLocation(e.getPoint());
   }

   @Override
   public void mouseReleased(MouseEvent e) {
      super.mouseReleased(e);

      try {
         JComponent parent = (JComponent)e.getComponent();
         Component c = parent.getComponentAt(e.getPoint());
         if (!this.p.equals(e.getPoint())) {
            return;
         }

         this.listener2.rollover(c, true);
         if (SwingUtilities.isLeftMouseButton(e)) {
            this.listener2.click(e, c, true);
            return;
         }

         if (SwingUtilities.isRightMouseButton(e)) {
            this.listener2.click(e, c, false);
         }
      } catch (Exception var4) {
      }
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      super.mouseMoved(e);

      try {
         JComponent parent = (JComponent)e.getComponent();
         this.moveScroll = e.getX() - parent.getVisibleRect().x <= 16 ? -16 : 0;
      } catch (Exception var4) {
      }

      try {
         JComponent parent = (JComponent)e.getComponent();
         Component c = parent.getComponentAt(e.getPoint());
         if (this.lastOpaque != null && (c == null || c != this.lastOpaque)) {
            this.listener2.rollover(this.lastOpaque, false);
            this.lastOpaque = null;
         }

         this.lastOpaque = c;
         this.listener2.rollover(c, true);
      } catch (Exception var5) {
      }
   }

   public int getMoveScroll() {
      return this.moveScroll;
   }

   public interface DragComponentListenerWithRolloverAction extends DragComponentMouseAdapter.DragComponentListener {
      void rollover(Component var1, boolean var2);

      void click(MouseEvent var1, Component var2, boolean var3);
   }
}
