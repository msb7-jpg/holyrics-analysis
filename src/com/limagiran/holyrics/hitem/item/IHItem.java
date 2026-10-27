package com.limagiran.holyrics.hitem.item;

import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.hitem.EnumTabType;
import com.limagiran.holyrics.util.IScheduleTask;
import com.limagiran.holyrics.util.Images;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public interface IHItem extends Serializable, Favoritable, IScheduleTask {
   @Override
   File getAsFile();

   String getName();

   default BufferedImage getThumb(Dimension size) {
      return this.getAsFile() == null ? Images.NULL1X1 : EnumTabType.getThumb(this.getId(), this.getAsFile(), size);
   }

   default ImageIcon loadThumbAsIcon(Dimension size) {
      return new ImageIcon(this.getThumb(size));
   }

   default ImageIcon loadDefaultThumbAsIcon() {
      return this.loadThumbAsIcon(ThumbMediaController.getThumbSize());
   }

   @Override
   default BufferedImage getFavoriteThumb(Dimension size) {
      return this.getThumb(size);
   }

   @Override
   default void run() {
      SwingUtilities.invokeLater(() -> this.favoriteAction(true));
   }

   @Override
   default String getTitle() {
      return this.getName();
   }

   @Override
   default ImageIcon getIcon() {
      return new ImageIcon(this.getFavoriteThumb(new Dimension(64, 36)));
   }
}
