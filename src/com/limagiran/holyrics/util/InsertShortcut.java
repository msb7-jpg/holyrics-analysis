package com.limagiran.holyrics.util;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;

public class InsertShortcut {
   public static void add(RootPaneContainer p, List<InsertShortcut.Shortcut> list) {
      InputMap inputMap = p.getRootPane().getInputMap(2);
      p.getRootPane().setInputMap(2, inputMap);
      list.stream().forEach(s -> {
         inputMap.put(KeyStroke.getKeyStroke(s.key, s.modifiers), s.name);
         p.getRootPane().getActionMap().put(s.name, s);
      });
   }

   public static void add(JComponent c, List<InsertShortcut.Shortcut> list) {
      InputMap inputMap = c.getInputMap(2);
      c.setInputMap(2, inputMap);
      list.stream().forEach(s -> {
         inputMap.put(KeyStroke.getKeyStroke(s.key, s.modifiers), s.name);
         c.getActionMap().put(s.name, s);
      });
   }

   public static void add(JComponent c, InsertShortcut.Shortcut... list) {
      add(c, Arrays.asList(list));
   }

   public static void add(RootPaneContainer p, InsertShortcut.Shortcut... list) {
      add(p, Arrays.asList(list));
   }

   public static void addEscToDispose(JFrame f) {
      add(f, InsertShortcut.Shortcut.create(27, f::dispose));
   }

   public static void addEscToDispose(JDialog d) {
      add(d, InsertShortcut.Shortcut.create(27, d::dispose));
   }

   public static class Shortcut extends AbstractAction {
      private static final long serialVersionUID = 1L;
      public final InsertShortcut.Shortcut.RunSerial run;
      public final String name;
      public final int key;
      public final int modifiers;

      private Shortcut(String name, int key, int modifiers, InsertShortcut.Shortcut.RunSerial run) {
         this.name = name;
         this.run = run;
         this.key = key;
         this.modifiers = modifiers;
      }

      public static InsertShortcut.Shortcut create(String name, int key, int modifiers, InsertShortcut.Shortcut.RunSerial run) {
         return new InsertShortcut.Shortcut(name, key, modifiers, run);
      }

      public static InsertShortcut.Shortcut create(String name, int key, InsertShortcut.Shortcut.RunSerial run) {
         return create(name, key, 0, run);
      }

      public static InsertShortcut.Shortcut create(int key, int modifiers, InsertShortcut.Shortcut.RunSerial run) {
         return create(String.valueOf(Utils.generateMillisecondId()), key, modifiers, run);
      }

      public static InsertShortcut.Shortcut create(int key, InsertShortcut.Shortcut.RunSerial run) {
         return create(String.valueOf(Utils.generateMillisecondId()), key, 0, run);
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         this.run.run();
      }

      public interface RunSerial extends Runnable, Serializable {
         long serialVersionUID = 135790L;
      }
   }
}
