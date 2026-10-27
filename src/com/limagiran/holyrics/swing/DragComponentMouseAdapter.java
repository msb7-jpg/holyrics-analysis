package com.limagiran.holyrics.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class DragComponentMouseAdapter extends MouseAdapter {
   private static final int xoffset = 16;
   private static final Rectangle R1 = new Rectangle();
   private static final Rectangle R2 = new Rectangle();
   private static Rectangle prevRect;
   private final JWindow window = new JWindow();
   private Component draggingComponent;
   private int index = -1;
   private Component gap;
   private Point startPt;
   private Point dragOffset;
   private final int gestureMotionThreshold = DragSource.getDragThreshold();
   private final DragComponentMouseAdapter.DragComponentListener listener;

   public DragComponentMouseAdapter(DragComponentMouseAdapter.DragComponentListener listener) {
      this.listener = listener;
      this.window.setBackground(new Color(0, true));
      this.window.setAlwaysOnTop(true);
   }

   @Override
   public void mousePressed(MouseEvent e) {
      JComponent parent = (JComponent)e.getComponent();
      if (parent.getComponentCount() <= 1) {
         this.startPt = null;
      } else {
         this.startPt = e.getPoint();
      }
   }

   private void startDragging(JComponent parent, Point pt) {
      Component c = parent.getComponentAt(pt);
      this.index = parent.getComponentZOrder(c);
      if (!Objects.equals(c, parent) && this.index >= 0) {
         this.draggingComponent = c;
         Dimension d = this.draggingComponent.getSize();
         Point dp = this.draggingComponent.getLocation();
         this.dragOffset = new Point(pt.x - dp.x, pt.y - dp.y);
         this.gap = Box.createRigidArea(d);
         swapComponentLocation(parent, c, this.gap, this.index);
         this.window.add(this.draggingComponent);
         this.window.pack();
         this.updateWindowLocation(pt, parent);
         this.window.setVisible(true);
      }
   }

   private void updateWindowLocation(Point pt, JComponent parent) {
      Point p = new Point(pt.x - this.dragOffset.x, pt.y - this.dragOffset.y);
      SwingUtilities.convertPointToScreen(p, parent);
      this.window.setLocation(p);
   }

   private static int getTargetIndex(Rectangle r, Point pt, int i) {
      int ht2 = (int)(0.5 + (double)r.height * 0.5);
      R1.setBounds(r.x, r.y, r.width, ht2);
      R2.setBounds(r.x, r.y + ht2, r.width, ht2);
      if (R1.contains(pt)) {
         prevRect = R1;
         return i - 1 > 0 ? i : 0;
      } else if (R2.contains(pt)) {
         prevRect = R2;
         return i;
      } else {
         return -1;
      }
   }

   private static void swapComponentLocation(Container parent, Component remove, Component add, int idx) {
      parent.remove(remove);
      parent.add(add, idx);
      parent.revalidate();
      parent.repaint();
   }

   @Override
   public void mouseDragged(MouseEvent e) {
      if (this.startPt != null) {
         Point pt = e.getPoint();
         JComponent parent = (JComponent)e.getComponent();
         double a = Math.pow((double)(pt.x - this.startPt.x), 2.0);
         double b = Math.pow((double)(pt.y - this.startPt.y), 2.0);
         if (this.draggingComponent == null && Math.sqrt(a + b) > (double)this.gestureMotionThreshold) {
            this.startDragging(parent, pt);
         } else if (this.window.isVisible() && this.draggingComponent != null) {
            this.updateWindowLocation(pt, parent);
            if (prevRect == null || !prevRect.contains(pt)) {
               for (int i = 0; i < parent.getComponentCount(); i++) {
                  Component c = parent.getComponent(i);
                  Rectangle r = c.getBounds();
                  if (Objects.equals(c, this.gap) && r.contains(pt)) {
                     return;
                  }

                  int tgt = getTargetIndex(r, pt, i);
                  if (tgt >= 0) {
                     swapComponentLocation(parent, this.gap, this.gap, tgt);
                     return;
                  }
               }

               parent.remove(this.gap);
               parent.revalidate();
            }
         }
      }
   }

   @Override
   public void mouseReleased(MouseEvent e) {
      this.startPt = null;
      if (this.window.isVisible() && this.draggingComponent != null) {
         try {
            Point pt = e.getPoint();
            JComponent parent = (JComponent)e.getComponent();
            Component cmp = this.draggingComponent;
            this.draggingComponent = null;
            prevRect = null;
            this.startPt = null;
            this.dragOffset = null;
            this.window.setVisible(false);

            for (int i = 0; i < parent.getComponentCount(); i++) {
               Component c = parent.getComponent(i);
               if (Objects.equals(c, this.gap)) {
                  swapComponentLocation(parent, this.gap, cmp, i);
                  return;
               }

               int tgt = getTargetIndex(c.getBounds(), pt, i);
               if (tgt >= 0) {
                  swapComponentLocation(parent, this.gap, cmp, tgt);
                  return;
               }
            }

            if (parent.getParent().getBounds().contains(pt)) {
               swapComponentLocation(parent, this.gap, cmp, parent.getComponentCount());
            } else {
               swapComponentLocation(parent, this.gap, cmp, this.index);
            }
         } catch (Exception var11) {
         } finally {
            this.listener.positionChanged();
         }
      }
   }

   public interface DragComponentListener {
      void positionChanged();
   }
}
