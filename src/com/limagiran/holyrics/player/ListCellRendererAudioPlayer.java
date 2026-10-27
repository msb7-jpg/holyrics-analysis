package com.limagiran.holyrics.player;

import com.alee.laf.list.WebListCellRenderer;
import com.alee.laf.list.WebListStyle;
import com.limagiran.holyrics.util.Dir;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.JList;

public class ListCellRendererAudioPlayer extends WebListCellRenderer {
   private final AudioPlayerSettings set;

   public ListCellRendererAudioPlayer(AudioPlayerSettings set) {
      this.set = set;
   }

   public Component getListCellRendererComponent(JList list, Object o, int index, boolean isSelected, boolean cellHasFocus) {
      boolean flag = this.set.isSelected((File)o);
      String val = (flag ? "► " : "") + Dir.getFileName((File)o);
      Component c = super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
      c.setForeground(flag ? Color.BLUE.darker() : WebListStyle.foreground);
      c.setFont(flag ? c.getFont().deriveFont(1) : c.getFont().deriveFont(0));
      return c;
   }
}
