package com.limagiran.holyrics.event;

import com.limagiran.holyrics.model.Music;
import java.awt.event.MouseEvent;
import javax.swing.JList;

public interface MusicSearchDBOffLinePanelListener {
   void musicChanged(Music var1);

   void doubleClick(Music var1);

   void rightButtonClicked(MouseEvent var1, JList<Music> var2);
}
