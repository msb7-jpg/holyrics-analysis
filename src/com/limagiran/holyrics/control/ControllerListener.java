package com.limagiran.holyrics.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerListener {
   private static final Map<Class, List<ControllerListener.ControllerChangeListener>> LISTENERS = new HashMap<>();

   public static void addChangeListener(Class clazz, ControllerListener.ControllerChangeListener listener) {
      List<ControllerListener.ControllerChangeListener> list = LISTENERS.getOrDefault(clazz, new ArrayList<>());
      list.add(listener);
      LISTENERS.put(clazz, list);
   }

   public static void removeChangeListener(Class clazz, ControllerListener.ControllerChangeListener listener) {
      List<ControllerListener.ControllerChangeListener> list = LISTENERS.getOrDefault(clazz, new ArrayList<>());
      list.remove(listener);
      LISTENERS.put(clazz, list);
   }

   public static void fireChangeListeners(Class clazz) {
      LISTENERS.getOrDefault(clazz, new ArrayList<>()).forEach(c -> c.controllerListenerTrigger(clazz));
   }

   public interface ControllerChangeListener {
      void controllerListenerTrigger(Class var1);
   }
}
