package com.limagiran.holyrics;

import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.RW;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Dev {
   public static final boolean ENABLE = false;
   public static final String PATH_APP = "";
   public static final Dimension SIZE = new Dimension(480, 270);
   public static final boolean CHANGE_SCREEN_SIZE = false;
   public static final boolean SINGLE_MONITOR_AS_TWO = false;
   public static final boolean SIMULATE_STAGE_VIEW = false;
   public static final boolean DISABLE_COUNT_AND_UPDATE = false;
   public static final boolean ENABLE_TRANSITION_DEV = false;
   public static final boolean SHOW_MEMORY_BAR = false;
   public static final boolean ENABLE_GRID_LINE_RENDERER = false;
   private static int countProp = 0;

   public static void showInJFrame(Component c, boolean exitOnClose, int... margin) {
      SwingUtilities.invokeLater(() -> {
         JFrame f = new JFrame();
         if (exitOnClose) {
            f.setDefaultCloseOperation(3);
         }

         f.setLayout(new BorderLayout());
         JPanel p = new JPanel(new BorderLayout());
         p.setOpaque(false);
         p.add(c);
         switch (margin.length) {
            case 1:
               p.setBorder(new EmptyBorder(margin[0], margin[0], margin[0], margin[0]));
               break;
            case 2:
               p.setBorder(new EmptyBorder(margin[0], margin[1], margin[0], margin[1]));
            case 3:
            default:
               break;
            case 4:
               p.setBorder(new EmptyBorder(margin[0], margin[1], margin[2], margin[3]));
         }

         f.add(p);
         f.pack();
         f.setLocationRelativeTo(null);
         InsertShortcut.add(f, InsertShortcut.Shortcut.create(27, () -> System.exit(0)));
         f.setVisible(true);
      });
   }

   public static synchronized void showPropInc() {
      if (++countProp == 10) {
         Utils.startThreadDaemon(() -> {
            StringBuilder sb = new StringBuilder();
            System.getProperties().forEach((k, v) -> sb.append(String.format("%s: %s%n", k, v)));

            try {
               RW.write(new File("dev-info_" + System.currentTimeMillis() + ".txt"), sb.toString());
            } catch (Exception var2) {
            }
         });
      }
   }

   public static void main(String[] args) throws Exception {
   }
}
