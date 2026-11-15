package com.limagiran.holyrics.util;

import java.awt.GraphicsDevice;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class Freeze {
   private static volatile boolean FREEZE;
   private static final List<JFrame> SCREENS = new ArrayList<>();
   private static final List<Runnable> LISTENERS = new ArrayList<>();
   private static final Robot ROBOT = createRobot();

   private static Robot createRobot() {
      try {
         return new Robot();
      } catch (Exception var1) {
         return null;
      }
   }

   public static boolean isAvailable() {
      return ROBOT != null;
   }

   public static void freeze() {
      setFreeze(!FREEZE);
   }

   public static void setFreeze(boolean freeze) {
      if (isAvailable()) {
         SwingUtilities.invokeLater(() -> {
            while (!SCREENS.isEmpty()) {
               JFrame f = SCREENS.remove(0);
               f.removeAll();
               f.dispose();
               f.setVisible(false);
            }

            FREEZE = freeze;
            Utils.startThreadDaemon(() -> {
               synchronized (Freeze.class) {
                  LISTENERS.forEach(Runnable::run);
               }
            });
            if (FREEZE) {
               List<GraphicsDevice> list = VideoUtils.list();
               list.remove(0);
               list.forEach(device -> {
                  JFrame frame = VideoUtils.createFullscreenJFrame(device);
                  SCREENS.add(frame);
                  JLabel label = new JLabel(new ImageIcon(ROBOT.createScreenCapture(frame.getBounds())));
                  frame.getContentPane().add(label, "Center");
               });
               SCREENS.forEach(f -> f.setVisible(true));
            }
         });
      }
   }

   public static boolean isFreeze() {
      return FREEZE;
   }

   public static void addListener(Runnable listener) {
      synchronized (Freeze.class) {
         LISTENERS.add(listener);
      }
   }

   public static void removeListener(Runnable listener) {
      synchronized (Freeze.class) {
         LISTENERS.remove(listener);
      }
   }

   public static void setup(JCheckBoxMenuItem jCheckBoxMenuItem) {
      SwingUtilities.invokeLater(() -> {
         jCheckBoxMenuItem.setEnabled(isAvailable());
         jCheckBoxMenuItem.setVisible(isAvailable());
      });
      addListener(() -> SwingUtilities.invokeLater(() -> jCheckBoxMenuItem.setSelected(isFreeze())));
   }
}
