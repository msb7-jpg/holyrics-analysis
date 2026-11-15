package com.limagiran.holyrics.hitem.item;

import com.limagiran.bible.Verse;
import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.view.BibleMain;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;

public class HItemVerse implements IHItem, HItemUtils, Favoritable {
   private static final long serialVersionUID = 1L;
   public static final BufferedImage DEFAULT_THUMB = Images.load("bible26.png");
   public final String id;

   public HItemVerse(String id) {
      this.id = id;
   }

   public HItemVerse(Verse verse) {
      this(verse.getId());
   }

   public Verse getVerse() {
      return BibleMain.getInstance().getBible().getVerse(this.id, true);
   }

   @Override
   public File getAsFile() {
      return null;
   }

   @Override
   public String getName() {
      return this.getVerse().getFullDesc();
   }

   @Override
   public void favoriteAction(boolean immediate) {
      Verse verse = this.getVerse();
      BibleMain.getInstance().showVerse(verse);
      BibleMain.getInstance().goTo(verse.book, verse.chapter, verse.verse);
   }

   @Override
   public String getActionName() {
      return I18N.WORD_SHOW.val();
   }

   @Override
   public BufferedImage getThumb(Dimension size) {
      return Img.resize(DEFAULT_THUMB, size);
   }

   @Override
   public BufferedImage getFavoriteThumb(Dimension size) {
      return this.getThumb(size);
   }

   @Override
   public ImageIcon loadThumbAsIcon(Dimension size) {
      return new ImageIcon(this.getThumb(size));
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public List<File> listPathIfDirectory() {
      return Collections.EMPTY_LIST;
   }

   @Override
   public IHItem getInstance() {
      return this;
   }
}
