package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ListCellRendererMusic extends AbstractWebListCellRendererWithSeparator implements ListCellRenderer<Music> {
   private static final String FORMAT_TITLE_AND_ARTIST = "<html><head><style>span{white-space:nowrap}</style></head><span>%s <span style=\"font-size:80%%;color: "
      + Utils.getHexColor(StyleUI.FOREGROUND_LIGHT)
      + "\"> (%s)</span></span>";
   private static final String FORMAT_TITLE = "<html><head><style>span{white-space:nowrap}</style></head><span>%s</span>";
   private final MusicUtils.MusicFilter musicFilter;

   public ListCellRendererMusic(MusicUtils.MusicFilter musicFilter) {
      super(false, true, false, false);
      this.musicFilter = musicFilter;
   }

   public Component getListCellRendererComponent(JList l, Music m, int i, boolean selected, boolean hasFocus) {
      String html = this.format(m.getTitle(), m.getArtist());
      if (this.musicFilter != null && !this.musicFilter.getSearch().isEmpty()) {
         String title = this.musicFilter.isTitle() ? MusicUtils.setupHighlight(m.getTitle(), this.musicFilter) : m.getTitle();
         String artist = this.musicFilter.isArtist() ? MusicUtils.setupHighlight(m.getArtist(), this.musicFilter) : m.getArtist();
         return super.getListCellRendererComponent(l, this.format(title, artist), i, selected, hasFocus);
      } else {
         return super.getListCellRendererComponent(l, html, i, selected, hasFocus);
      }
   }

   private String format(String title, String artist) {
      return artist.isEmpty()
         ? String.format("<html><head><style>span{white-space:nowrap}</style></head><span>%s</span>", title)
         : String.format(FORMAT_TITLE_AND_ARTIST, title, artist);
   }
}
