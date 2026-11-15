package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.util.StyleUI;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class JLabelToolTip extends JLabel {
   public final AbsoluteConstraints cons = new AbsoluteConstraints(0, 0);
   private boolean alignRight = true;
   private boolean alignBottom = true;
   public int distance;

   public JLabelToolTip() {
      this.init();
   }

   private void init() {
      this.setBackground(StyleUI.BACKGROUND);
      this.setForeground(StyleUI.FOREGROUND);
      this.setBorder(new CompoundBorder(LineBorder.createGrayLineBorder(), new EmptyBorder(3, 3, 3, 3)));
      this.setOpaque(true);
      this.setHorizontalAlignment(2);
      this.setVerticalAlignment(3);
   }

   public void atRight() {
      this.alignRight = true;
   }

   public void atLeft() {
      this.alignRight = false;
   }

   public void atBottom() {
      this.alignBottom = true;
   }

   public void atTop() {
      this.alignBottom = false;
   }

   private void refresh(JPanel glass, boolean visible) {
      int var10002 = this.alignRight ? 0 : this.getSize().width;
      this.cons.x = MouseInfo.getPointerInfo().getLocation().x - var10002;
      var10002 = this.alignBottom ? 0 : this.getSize().height;
      this.cons.y = MouseInfo.getPointerInfo().getLocation().y - var10002;
      int d = Math.max(this.distance, 0);
      if (d > 0) {
         this.cons.x = this.cons.x + (this.alignRight ? this.distance : -this.distance);
         this.cons.y = this.cons.y + (this.alignBottom ? this.distance : -this.distance);
      }

      try {
         glass.setVisible(true);
         this.cons.x = this.cons.x - glass.getLocationOnScreen().x;
         this.cons.y = this.cons.y - glass.getLocationOnScreen().y;
         this.cons.x = Math.max(0, this.cons.x);
         this.cons.y = Math.max(0, this.cons.y);
         if (Arrays.asList(glass.getComponents()).stream().noneMatch(this::equals)) {
            glass.setLayout(new AbsoluteLayout());
            glass.add(this, this.cons);
         }
      } catch (Exception var6) {
      }

      this.revalidate();
      this.repaint();
      this.setVisible(visible);

      try {
         this.getRootPane().repaint();
      } catch (Exception var5) {
      }
   }

   public MouseListener getMouseExitedListener() {
      return new MouseAdapter() {
         @Override
         public void mouseExited(MouseEvent e) {
            JLabelToolTip.this.setVisible(false);
         }
      };
   }

   public void refresh(JDialog glass, boolean visible) {
      this.refresh((JPanel)glass.getGlassPane(), visible);
   }

   public void refresh(JFrame glass, boolean visible) {
      this.refresh((JPanel)glass.getGlassPane(), visible);
   }

   public void refresh(JDialog glass) {
      this.refresh((JPanel)glass.getGlassPane(), true);
   }

   public void refresh(JFrame glass) {
      this.refresh((JPanel)glass.getGlassPane(), true);
   }
}
