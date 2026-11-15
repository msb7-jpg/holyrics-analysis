package com.limagiran.holyrics.hitem.item;

import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.playback.PlayBackUtils;
import com.limagiran.holyrics.util.Values;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HItemPlayBack implements IHItem, HItemUtils, Favoritable, Values {
   private static final long serialVersionUID = 1L;
   public final String name;
   private transient File file;
   private static final List<File> EMPTY_LIST = new ArrayList<>();

   public HItemPlayBack(String name) {
      this.name = name;
   }

   public HItemPlayBack getInstance() {
      return this;
   }

   @Override
   public String getActionName() {
      return I18N.WORD_SHOW.val();
   }

   @Override
   public void favoriteAction(boolean immediate) {
      PlayBackUtils.run(this.getAsFile());
   }

   @Override
   public BufferedImage getThumb(Dimension size) {
      return PlayBackUtils.getThumb(size);
   }

   @Override
   public BufferedImage getFavoriteThumb(Dimension size) {
      return PlayBackUtils.getThumb(size);
   }

   @Override
   public synchronized File getAsFile() {
      return this.file == null ? (this.file = new File(this.getId()).getAbsoluteFile()) : this.file;
   }

   @Override
   public String getId() {
      return PATH_MEDIA_AUTOMATIC_PRESENTATIONS + this.name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public String getNameWithoutExtension() {
      return this.name.endsWith(".ap") ? this.name.substring(0, this.name.length() - 3) : this.name;
   }

   @Override
   public List<File> listPathIfDirectory() {
      return EMPTY_LIST;
   }
}
