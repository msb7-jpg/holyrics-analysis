package com.limagiran.holyrics.control;

import com.alee.laf.tabbedpane.WebTabbedPane;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.model.announcement.AnnouncementTitlePanel;
import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.model.theme.BackgroundAnnouncement;
import com.limagiran.holyrics.util.ReflectionUtils;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementController {
   private static final String FILE_NAME = "announcement.hly";
   private static final List<Announcement> ANNOUNCEMENTS = ControllerFactory.load("announcement.hly", Announcement.class);

   public static void add(Announcement a) {
      if (!ANNOUNCEMENTS.contains(a)) {
         ANNOUNCEMENTS.add(a);
         save();
      }
   }

   public static void remove(Announcement a) {
      ANNOUNCEMENTS.remove(a);
      Background bg = a.getTheme().getBackground();
      if (bg instanceof BackgroundAnnouncement) {
         ReflectionUtils.setField(((BackgroundAnnouncement)bg).getImageHL(), "bytes", new byte[0]);
         ReflectionUtils.setField(((BackgroundAnnouncement)bg).getImageHL(), "bytesThumbs", new byte[0]);
      }

      save();
   }

   public static List<Announcement> getAll() {
      return new ArrayList<>(ANNOUNCEMENTS);
   }

   public static Announcement find(long id) {
      for (Announcement an : getAll()) {
         if (an.id == id) {
            return an;
         }
      }

      return null;
   }

   public static synchronized void save() {
      ControllerFactory.save(ANNOUNCEMENTS, "announcement.hly");
   }

   public static void set(WebTabbedPane tabs) {
      try {
         List<Announcement> list = new ArrayList<>();

         for (int i = 0; i < tabs.getTabCount(); i++) {
            Component tab = tabs.getTabComponentAt(i);
            if (tab instanceof AnnouncementTitlePanel) {
               list.add(((AnnouncementTitlePanel)tab).getAnnouncement());
            }
         }

         ANNOUNCEMENTS.clear();
         ANNOUNCEMENTS.addAll(list);
         save();
      } catch (Exception var4) {
      }
   }
}
