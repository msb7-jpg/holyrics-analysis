package com.limagiran.holyrics.hitem.item;

import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.EnumTabType;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.vlc.VLC;
import com.limagiran.holyrics.vlc.VLCUtils;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;

public class HItemURL implements IHItem, HItemUtils, Favoritable, Values {
   private static final BufferedImage DEFAULT_THUMB = Images.load("web32.png");
   private static final long serialVersionUID = 1L;
   public final String name;

   public HItemURL(String name) {
      this.name = name;
   }

   public HItemURL getInstance() {
      return this;
   }

   @Override
   public BufferedImage getThumb(Dimension size) {
      ThumbMediaController.loadYouTube(this.name);
      return Img.resize(VLCUtils.isYoutube(this.name) ? ThumbMediaController.getOrDefault(this.name, Images.ICON_YOUTUBE_32) : DEFAULT_THUMB, size);
   }

   @Override
   public String getActionName() {
      return EnumTabType.VIDEO.actionName;
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
   public void favoriteAction(boolean immediate) {
      if (MusicMain.INSTANCE != null) {
         if (VLC.ENABLED) {
            VLC.PLAYER_CONTROL.setPlaylist(this.name, VLC.PLAYER_CONTROL::playPause);
            MusicMain.staticExpandPlayerPanel();
         }
      }
   }

   @Override
   public synchronized File getAsFile() {
      return null;
   }

   @Override
   public String getId() {
      return this.name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public List<File> listPathIfDirectory() {
      return Collections.EMPTY_LIST;
   }
}
