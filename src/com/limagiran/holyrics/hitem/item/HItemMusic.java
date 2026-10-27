package com.limagiran.holyrics.hitem.item;

import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.MusicMain;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;

public class HItemMusic implements IHItem, HItemUtils, Favoritable {
   private static final long serialVersionUID = 1L;
   public static final BufferedImage DEFAULT_THUMB = Images.load("holyricsIcon48.png");
   public final long id;

   public HItemMusic(long id) {
      this.id = id;
   }

   public HItemMusic getInstance() {
      return this;
   }

   @Override
   public String getActionName() {
      return I18N.WORD_SHOW.val();
   }

   public Music getMusic() {
      return MusicDao.getLazy(this.id);
   }

   @Override
   public void favoriteAction(boolean immediate) {
      Music music = this.getMusic();
      if (music == null) {
         Utils.error(I18N.MSG_NOT_FOUND.val());
      } else {
         if (MusicMain.INSTANCE != null) {
            GeneratePresentation.show(MusicMain.INSTANCE, Settings.SET.getTheme(), Arrays.asList(music), 0);
         }
      }
   }

   @Override
   public BufferedImage getFavoriteThumb(Dimension size) {
      return this.getThumb(size);
   }

   @Override
   public BufferedImage getThumb(Dimension size) {
      return Img.resize(DEFAULT_THUMB, size);
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
      return Optional.ofNullable(this.getMusic()).map(Music::toString).orElse(I18N.WORD_ERROR.val() + " - " + I18N.MSG_NOT_FOUND.val());
   }

   @Override
   public List<File> listPathIfDirectory() {
      return Collections.EMPTY_LIST;
   }
}
