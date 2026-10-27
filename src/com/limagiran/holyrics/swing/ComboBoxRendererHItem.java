package com.limagiran.holyrics.swing;

import com.alee.laf.combobox.WebComboBoxCellRenderer;
import com.alee.laf.combobox.WebComboBoxElement;
import com.limagiran.holyrics.favorite.FavoriteItem;
import com.limagiran.holyrics.hitem.item.HItemMusic;
import com.limagiran.holyrics.hitem.item.HItemURL;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.vlc.VLCUtils;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

public class ComboBoxRendererHItem extends WebComboBoxCellRenderer {
   private static final Dimension ICON_SIZE = new Dimension(36, 18);

   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      WebComboBoxElement renderer = (WebComboBoxElement)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      Icon icon = null;
      String text = " ";
      if (value instanceof FavoriteItem) {
         text = ((FavoriteItem)value).getName();
         icon = new ImageIcon(((FavoriteItem)value).getFavorite().getFavoriteThumb(ICON_SIZE));
      } else if (value instanceof IHItem) {
         text = ((IHItem)value).getName();
         if (value instanceof HItemURL) {
            text = VLCUtils.getYouTubeTitleOffline(text);
         }

         icon = new ImageIcon(((IHItem)value).getFavoriteThumb(ICON_SIZE));
      } else if (value instanceof Music) {
         text = ((Music)value).toString();
         icon = new ImageIcon(Img.resize(HItemMusic.DEFAULT_THUMB, ICON_SIZE));
      }

      if (!renderer.getFontName().equalsIgnoreCase("arial")) {
         renderer.setFontName("Arial");
         renderer.setFontSize(14);
      }

      renderer.setIcon(icon);
      renderer.setText(text);
      return renderer;
   }
}
