package com.limagiran.holyrics.hitem.item;

import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.EnumTabType;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.util.Values;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class HItemImage implements HItemUtils, IHItem, Favoritable, Values {
   private static final long serialVersionUID = 1L;
   public final String name;
   private transient File file;

   public HItemImage(String name) {
      this.name = name;
   }

   public HItemImage getInstance() {
      return this;
   }

   @Override
   public synchronized File getAsFile() {
      return this.file == null ? (this.file = new File(this.getId()).getAbsoluteFile()) : this.file;
   }

   @Override
   public String getActionName() {
      return EnumTabType.IMAGE.actionName;
   }

   @Override
   public void favoriteAction(boolean immediate) {
      HItemUtils.run(immediate, this.getAsFile());
   }

   @Override
   public BufferedImage getFavoriteThumb(Dimension size) {
      return this.getAsFile().isDirectory() ? EnumTabType.getThumb(this.getId(), this.getAsFile(), size) : IHItem.super.getFavoriteThumb(size);
   }

   @Override
   public String getId() {
      return PATH_MEDIA_IMAGE + this.name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public List<File> listPathIfDirectory() {
      return HItemUtils.getListIfDirectory(this.getAsFile(), HItemUtils::isImage);
   }
}
