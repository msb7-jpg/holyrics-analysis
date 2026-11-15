package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.model.Music;
import javax.swing.DefaultListModel;

public class ListModelOrder extends DefaultListModel<MusicController.Paragraph> {
   private Music music;

   public ListModelOrder(Music music) {
      this.setMusic(music);
   }

   public final void setMusic(Music music) {
      this.removeAllElements();
      this.music = music;
      this.music.getParagraphsToPresentation().forEach(this::addElement);
   }

   public Music getMusic() {
      return this.music;
   }
}
