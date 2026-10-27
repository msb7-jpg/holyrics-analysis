package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.StringUtils;
import java.awt.Component;
import java.util.List;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererCaptureDialog extends AbstractWebListCellRendererWithSeparator implements ListCellRenderer<int[]> {
   private final List<String> paragraphs;

   public ListCellRendererCaptureDialog(Music music) {
      super(false, true, false, false);
      this.paragraphs = music.getParagraphs();
      this.view.setMargin(2, 0, 2, 0);
   }

   public Component getListCellRendererComponent(JList l, int[] value, int i, boolean selected, boolean hasFocus) {
      return super.getListCellRendererComponent(l, this.format(value), i, selected, hasFocus);
   }

   private String format(int[] value) {
      try {
         String duration = "&nbsp;";
         String text = "&nbsp;";
         if (value[0] >= 0) {
            duration = "[" + StringUtils.formatTimeMm_Ss_MSms((long)value[1]) + "]";
            text = MusicUtils.getResume(this.paragraphs.get(value[0]), 256);
         }

         return this.format(duration, text);
      } catch (Exception var4) {
         return this.format("&nbsp;", "&nbsp;");
      }
   }

   private String format(String duration, String text) {
      return "<html><span style='white-space:nowrap'><span style='font-family:Courier New'><b>" + duration + "</b></span> " + text + "</span>";
   }
}
