package com.limagiran.holyrics.util;

import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.vlc.VLC;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

public class KeyboardUtils {
   public static void startCapture() {
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ke -> {
         if (ke.toString().contains("RELEASED") && MusicMain.INSTANCE != null) {
            try {
               if (VLC.ENABLED) {
                  if (!ke.isAltDown() && !ke.isControlDown() && !ke.isShiftDown()) {
                     switch (ke.getKeyCode()) {
                        case 114:
                           VLC.PLAYER_CONTROL.playPause();
                           break;
                        case 115:
                           VLC.PLAYER_CONTROL.stop();
                           break;
                        case 122:
                           VLC.PLAYER_CONTROL.fullscreen();
                     }
                  }

                  if (!ke.isAltDown() && ke.isControlDown() && !ke.isShiftDown()) {
                     switch (ke.getKeyCode()) {
                        case 77:
                           VLC.PLAYER_CONTROL.mute();
                     }
                  }
               }

               Field f = KeyEvent.class.getDeclaredField("rawCode");
               f.setAccessible(true);
               int cd = (int)((Long)f.get(ke)).longValue();
               switch (cd) {
                  case 173:
                  case 174:
                  case 175:
                  case 176:
                  case 177:
                  default:
                     break;
                  case 178:
                     BooleanOptional.runIfTrue(VLC.ENABLED, () -> VLC.PLAYER_CONTROL.stop());
                     break;
                  case 179:
                     BooleanOptional.runIfTrue(VLC.ENABLED, () -> VLC.PLAYER_CONTROL.playPause());
               }
            } catch (Exception var3) {
            }

            return false;
         } else {
            return false;
         }
      });
   }
}
