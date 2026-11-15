package com.limagiran.holyrics.hitem.item;

import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.EnumTabType;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Values;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class HItemVideo implements IHItem, HItemUtils, Favoritable, Values {
   private static final long serialVersionUID = 1L;
   public static final BufferedImage DEFAULT_THUMB_LIST = Images.load("videoLibrary48.png");
   private final String name;
   private transient File file;

   public HItemVideo(String name) {
      this.name = name;
   }

   public HItemVideo getInstance() {
      return this;
   }

   @Override
   public synchronized File getAsFile() {
      return this.file == null ? (this.file = new File(this.getId()).getAbsoluteFile()) : this.file;
   }

   @Override
   public String getActionName() {
      return EnumTabType.VIDEO.actionName;
   }

   @Override
   public void favoriteAction(boolean immediate) {
      HItemUtils.run(immediate, this.getAsFile());
   }

   @Override
   public String getId() {
      return PATH_MEDIA_VIDEO + this.name;
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
