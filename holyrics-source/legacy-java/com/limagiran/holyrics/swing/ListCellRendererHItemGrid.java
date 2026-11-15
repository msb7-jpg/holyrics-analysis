package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.hitem.item.HItemAudio;
import com.limagiran.holyrics.util.Img;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class ListCellRendererHItemGrid extends AbstractWebListCellRendererIconLabel implements ListCellRenderer<File> {
   private static final ImageIcon ICON_AUDIO_LIST = new ImageIcon(Img.resize(HItemAudio.FAVORITE_THUMB, ThumbMediaController.getThumbSize()));

   public ListCellRendererHItemGrid() {
      super(new AbstractWebListCellRendererIconLabel.Builder());
      this.init();
   }

   private void init() {
      this.desc.setFont(new Font("Arial", 0, 11));
      this.setBorder(new EmptyBorder(6, 8, 6, 8));
      this.content.setLayout(new BorderLayout(8, 6));
   }

   public Component getListCellRendererComponent(JList list, File file, int index, boolean isSelected, boolean cellHasFocus) {
      ImageIcon ic;
      if (HItemUtils.isAudio(file)) {
         ic = ICON_AUDIO_LIST;
      } else {
         ic = new ImageIcon(ThumbMediaController.getAsBufferedImage(file));
      }

      return super.getListCellRendererComponent(list, ic, this.getHTML(list, file), index, isSelected, cellHasFocus);
   }

   private String getHTML(JList list, File file) {
      try {
         return "<html><div style='text-align:center;width:" + (list.getFixedCellWidth() - 20) + "'>" + file.getName() + "</div>";
      } catch (Exception var4) {
         return "<html>" + file.getName();
      }
   }
}
