package com.limagiran.holyrics.model;

import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.model.theme.BackgroundAnnouncement;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Utils;
import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Optional;

public class Announcement implements Serializable {
   private static final long serialVersionUID = 12491L;
   private static final BackgroundAnnouncement DEFAULT_BACKGROUND = createDefaultBackground();
   public final long id;
   private String name;
   private String text;
   private Theme theme;
   private long version = 1L;

   public static void load() {
      AnnouncementController.getAll();
   }

   public Announcement() {
      this.id = Utils.generateMillisecondId();
      this.name = "";
      this.text = "";
      this.theme = Theme.create().setBackground(getDefaultBG()).setFontColor(new Color(30, 30, 30));
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public Theme getTheme() {
      return this.theme;
   }

   public void setTheme(Theme theme) {
      this.theme = theme;
   }

   public Presentation createPresentation() {
      return Presentation.create(MusicController.Paragraph.create(this, this.getText(), ""), this.getTheme());
   }

   @Override
   public String toString() {
      return String.format("%-8s", this.name);
   }

   private static BackgroundAnnouncement createDefaultBackground() {
      try {
         return RWObj.readJar("/com/limagiran/holyrics/file/defaultBGAnnouncement.hly", BackgroundAnnouncement.class, "#hly$k3y#");
      } catch (Exception var1) {
         Utils.error(I18N.MSG_ERROR_CORRUPTED_FILES.val());
         System.exit(0);
         return null;
      }
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof Announcement ? ((Announcement)obj).id == this.id : super.equals(obj);
   }

   @Override
   public int hashCode() {
      return 291 + (int)(this.id ^ this.id >>> 32);
   }

   private static Background getDefaultBG() {
      return Optional.ofNullable(RWObj.clone(DEFAULT_BACKGROUND, BackgroundAnnouncement.class)).orElse(DEFAULT_BACKGROUND);
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
