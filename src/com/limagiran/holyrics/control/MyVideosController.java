package com.limagiran.holyrics.control;

import com.limagiran.holyrics.model.VideoHL;
import java.util.ArrayList;
import java.util.List;

public class MyVideosController {
   private static final String FILE_NAME = "myVideos.hly";
   private static final transient List<VideoHL> LIST = ControllerFactory.load("myVideos.hly", VideoHL.class);
   private static final transient Object LIST_LOCK = new Object();

   public static void add(VideoHL i) {
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

   public static void remove(VideoHL image) {
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

   public static List<VideoHL> get() {
      synchronized (LIST_LOCK) {
         return new ArrayList<>(LIST);
      }
   }

   public static VideoHL get(int id) {
      synchronized (LIST_LOCK) {
         for (VideoHL i : LIST) {
            if (i.id == id) {
               return i;
            }
         }

         return null;
      }
   }

   public static synchronized void save() {
      ControllerFactory.save(LIST, "myVideos.hly");
   }

   public static long fileSize() {
      return ControllerFactory.fileSize("myVideos.hly");
   }
}
