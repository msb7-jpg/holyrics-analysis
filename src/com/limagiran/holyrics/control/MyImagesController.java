package com.limagiran.holyrics.control;

import com.limagiran.holyrics.model.ImageHL;
import java.util.ArrayList;
import java.util.List;

public class MyImagesController {
   private static final String FILE_NAME = "myImages.hly";
   private static final List<ImageHL> LIST = ControllerFactory.load("myImages.hly", ImageHL.class);
   private static final transient Object LIST_LOCK = new Object();

   public static void add(ImageHL i) {
      int lastSize = LIST.size();
      synchronized (LIST_LOCK) {
         if (!LIST.contains(i)) {
            LIST.add(i);
         }
      }

      if (LIST.size() != lastSize) {
         save();
      }
   }

   public static void remove(ImageHL image) {
      remove(image.id);
   }

   public static void remove(int id) {
      int lastSize = LIST.size();
      synchronized (LIST_LOCK) {
         for (int i = 0; i < LIST.size(); i++) {
            if (LIST.get(i).id == id) {
               LIST.remove(i--);
            }
         }
      }

      if (LIST.size() != lastSize) {
         save();
      }
   }

   public static List<ImageHL> get() {
      synchronized (LIST_LOCK) {
         return new ArrayList<>(LIST);
      }
   }

   public static ImageHL get(int id) {
      synchronized (LIST_LOCK) {
         for (ImageHL i : LIST) {
            if (i.id == id) {
               return i;
            }
         }

         return null;
      }
   }

   public static synchronized void save() {
      ControllerFactory.save(LIST, "myImages.hly");
   }

   public static long fileSize() {
      return ControllerFactory.fileSize("myImages.hly");
   }
}
