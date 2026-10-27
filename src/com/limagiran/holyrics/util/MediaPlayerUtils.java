package com.limagiran.holyrics.util;

import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.playback.PlayBackPlayerMiniShow;
import com.limagiran.holyrics.vlc.VLC;
import com.limagiran.holyrics.vlc.VLCVideoBackground;
import java.awt.Component;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

public class MediaPlayerUtils {
   public static MediaPlayer MEDIA_PLAYER;

   public static synchronized MediaPlayer setupMediaPlayer(Theme theme, Component parent, boolean safe) {
      if (vlcIsPlayingVideo() || PlayBackPlayerMiniShow.isPlaying()) {
         return null;
      } else if (safe && mediaPlayerIsPlaying()) {
         return null;
      } else {
         stop(MEDIA_PLAYER);
         System.gc();
         if (safe) {
            try {
               MEDIA_PLAYER = new MediaPlayer(theme.getBackground().getVideo());
            } catch (Exception var4) {
               Utils.error(parent, var4.getMessage());
               MEDIA_PLAYER = null;
               return null;
            }
         } else {
            MEDIA_PLAYER = theme.getMediaPlayer(parent);
         }

         return MEDIA_PLAYER;
      }
   }

   private static boolean mediaPlayerIsPlaying() {
      return MEDIA_PLAYER != null && MEDIA_PLAYER.getStatus() != Status.DISPOSED;
   }

   private static boolean vlcIsPlayingVideo() {
      return VLC.ENABLED && (VLC.PLAYER_CONTROL.isPlayingAndIsVideoOrStream() || VLCVideoBackground.playing);
   }

   public static synchronized void stop(MediaPlayer mp) {
      Utils.safeException(() -> {
         if (mp != null) {
            mp.pause();
            mp.stop();
            mp.dispose();
            Utils.sleep(50L);
         }
      });
   }

   public static void stop() {
      stop(MEDIA_PLAYER);
   }
}
