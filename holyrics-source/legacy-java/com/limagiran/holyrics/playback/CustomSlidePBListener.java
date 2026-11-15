package com.limagiran.holyrics.playback;

import com.limagiran.holyrics.swing.JLabelToolTip;

interface CustomSlidePBListener {
   void durationChanged(CustomSlidePB var1);

   void play(CustomSlidePB var1, boolean var2);

   JLabelToolTip getToolTip();

   void changePosition(long var1);

   void changeStartAt(CustomSlidePB var1, int var2);
}
