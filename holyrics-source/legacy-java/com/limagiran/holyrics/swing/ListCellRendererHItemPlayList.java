package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.hitem.item.HItemURL;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.vlc.VLCUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class ListCellRendererHItemPlayList extends AbstractWebListCellRendererIconLabel implements ListCellRenderer<IHItem> {
   private static final Dimension ICON_SIZE = new Dimension(36, 18);

   public ListCellRendererHItemPlayList() {
      super(new AbstractWebListCellRendererIconLabel.Builder().setDescBottom(false));
      this.init();
   }

   private void init() {
      this.desc.setFont(new Font("Arial", 0, 12));
      this.setBorder(new EmptyBorder(6, 2, 6, 2));
      this.content.setLayout(new BorderLayout(4, 6));
   }

   public Component getListCellRendererComponent(JList list, IHItem hItem, int index, boolean isSelected, boolean cellHasFocus) {
      String name = hItem.getName();
      if (hItem instanceof HItemURL) {
         name = VLCUtils.getYouTubeTitleOffline(name);
      }

      ImageIcon ic = new ImageIcon(hItem.getFavoriteThumb(ICON_SIZE));
      return super.getListCellRendererComponent(list, ic, name, index, isSelected, cellHasFocus);
   }
}
