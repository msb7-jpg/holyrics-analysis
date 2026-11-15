package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.hitem.EnumTabType;
import com.limagiran.holyrics.hitem.ListModelHItem;
import com.limagiran.holyrics.hitem.item.HItemAudio;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.StringUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

public class ListCellRendererHItem extends AbstractWebListCellRendererIconLabel implements ListCellRenderer<String> {
   private static final ImageIcon ICON_AUDIO_LIST = new ImageIcon(Img.resize(HItemAudio.DEFAULT_THUMB_LIST, new Dimension(36, 18)));
   private final EnumTabType tabType;
   public final ListModelHItem model;

   public ListCellRendererHItem(EnumTabType tabType, ListModelHItem model) {
      super(new AbstractWebListCellRendererIconLabel.Builder().setDescBottom(false));
      this.tabType = tabType;
      this.model = model;
      this.init();
   }

   private void init() {
      this.desc.setFont(new Font("Arial", 0, 12));
      this.setBorder(new EmptyBorder(6, 8, 6, 8));
      this.content.setLayout(new BorderLayout(10, 6));
   }

   public Component getListCellRendererComponent(JList list, String string, int index, boolean isSelected, boolean cellHasFocus) {
      ImageIcon ic = null;
      File f = new File(this.tabType.path, string);
      if (f.exists()) {
         this.icon.setVisible((ic = this.getIcon(string, f)) != null);
      }

      return super.getListCellRendererComponent(list, ic, this.formatText(f.getName(), list, ic), index, isSelected, cellHasFocus);
   }

   private ImageIcon getIcon(String string, File file) {
      try {
         if (this.tabType.equals(EnumTabType.AUDIO)) {
            return file.isDirectory() ? ICON_AUDIO_LIST : null;
         } else {
            return new ImageIcon(EnumTabType.getThumb(this.tabType.path + string, file, ThumbMediaController.getThumbSize()));
         }
      } catch (Exception var4) {
         return null;
      }
   }

   private String formatText(String text, JList list, ImageIcon ic) {
      try {
         int width = Math.max(list.getFixedCellWidth() - 30 - (ic != null ? ic.getIconWidth() : 0), 140);
         return "<html><div style='width:" + width + "'>" + StringUtils.setupHighlight(text, this.model.getFilter()) + "</div>";
      } catch (Exception var5) {
         return text;
      }
   }
}
