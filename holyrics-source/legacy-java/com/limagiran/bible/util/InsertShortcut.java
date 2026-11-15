package com.limagiran.bible.util;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
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

   public static void add(RootPaneContainer p, InsertShortcut.Shortcut... list) {
      add(p, Arrays.asList(list));
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
         return create(String.valueOf(System.nanoTime()), key, modifiers, run);
      }

      public static InsertShortcut.Shortcut create(int key, InsertShortcut.Shortcut.RunSerial run) {
         return create(String.valueOf(System.nanoTime()), key, 0, run);
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
