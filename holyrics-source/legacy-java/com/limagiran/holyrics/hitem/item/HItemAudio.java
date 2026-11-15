package com.limagiran.holyrics.hitem.item;

import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.EnumTabType;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.Values;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class HItemAudio implements IHItem, HItemUtils, Favoritable, Values {
   public static final BufferedImage FAVORITE_THUMB = Images.load("music32.png");
   public static final BufferedImage DEFAULT_THUMB = Images.NULL1X1;
   public static final BufferedImage DEFAULT_THUMB_LIST = Images.loadWithPadding("musicLibrary40-edited.png", 4, 0);
   private static final long serialVersionUID = 1L;
   public final String name;
   private transient File file;

   public HItemAudio(String name) {
      this.name = name;
   }

   public HItemAudio getInstance() {
      return this;
   }

   @Override
   public String getActionName() {
      return EnumTabType.AUDIO.actionName;
   }

   @Override
   public void favoriteAction(boolean immediate) {
      HItemUtils.run(immediate, this.getAsFile());
   }

   @Override
   public BufferedImage getFavoriteThumb(Dimension size) {
      return Img.resize(this.getAsFile().isDirectory() ? DEFAULT_THUMB_LIST : FAVORITE_THUMB, size);
   }

   @Override
   public synchronized File getAsFile() {
      return this.file == null ? (this.file = new File(this.getId()).getAbsoluteFile()) : this.file;
   }

   @Override
   public String getId() {
      return PATH_MEDIA_AUDIO + this.name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public List<File> listPathIfDirectory() {
      return HItemUtils.getListIfDirectory(this.getAsFile(), HItemUtils::isMedia);
   }
}
