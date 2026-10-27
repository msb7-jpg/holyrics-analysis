package com.limagiran.holyrics.event;

import com.limagiran.holyrics.model.Music;
import java.util.List;

public interface SchedulePanelGridListener {
   void show(List<Music> var1);

   void musicChanged(Music var1);

   boolean playedMusic(Music var1);

   void playlistChanged();

   void playlistHItemChanged();
}
