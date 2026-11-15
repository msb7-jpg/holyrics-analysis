package com.limagiran.holyrics.hitem.item;

import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.MusicMain;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;

public class HItemAnnouncement implements IHItem, HItemUtils, Favoritable {
   private static final long serialVersionUID = 1L;
   public final long id;

   public HItemAnnouncement(long id) {
      this.id = id;
   }

   public HItemAnnouncement getInstance() {
      return this;
   }

   @Override
   public String getActionName() {
      return I18N.WORD_SHOW.val();
   }

   public Announcement getAnnouncement() {
      return AnnouncementController.find(this.id);
   }

   @Override
   public void favoriteAction(boolean immediate) {
      Announcement an = this.getAnnouncement();
      if (an == null) {
         Utils.error(I18N.MSG_ERROR_ANNOUNCEMENT_NOT_FOUND.val());
      } else {
         if (MusicMain.INSTANCE != null) {
            List<Presentation> list = new ArrayList<>(Arrays.asList(an.createPresentation()));
            GeneratePresentation.showPresentation(MusicMain.INSTANCE, Theme.BLACK_SCREEN, list, 0, ScreenDeviceSettings.EnumDeviceType.ANNOUNCEMENT);
         }
      }
   }

   @Override
   public BufferedImage getFavoriteThumb(Dimension size) {
      return this.getThumb(size);
   }

   @Override
   public BufferedImage getThumb(Dimension size) {
      Announcement an = this.getAnnouncement();
      return an != null
         ? PresentationSlideUtils.createImage(null, an.getText(), an.getTheme(), size, true)
         : PresentationSlideUtils.createThumb(Theme.BLACK_SCREEN, size);
   }

   @Override
   public ImageIcon loadThumbAsIcon(Dimension size) {
      return new ImageIcon(this.getThumb(size));
   }

   @Override
   public File getAsFile() {
      return null;
   }

   @Override
   public String getId() {
      return String.valueOf(this.id);
   }

   @Override
   public String getName() {
      return Optional.ofNullable(this.getAnnouncement()).map(Announcement::getName).orElse(I18N.WORD_ERROR.val() + " - " + I18N.MSG_NOT_FOUND.val());
   }

   @Override
   public List<File> listPathIfDirectory() {
      return Collections.EMPTY_LIST;
   }
}
