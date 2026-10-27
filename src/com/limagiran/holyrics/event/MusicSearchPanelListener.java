package com.limagiran.holyrics.event;

import com.limagiran.holyrics.model.Music;
import java.awt.Window;
import java.awt.event.MouseEvent;
import javax.swing.JList;

public interface MusicSearchPanelListener {
   void musicChanged(Music var1);

   void doubleClick(Music var1);

   void rightButtonClicked(MouseEvent var1, JList<Music> var2);

   void newMusic();

   boolean keyLeftEvent();

   boolean keyRightEvent();

   Window getOwner();

   void openEditProfile();
}
