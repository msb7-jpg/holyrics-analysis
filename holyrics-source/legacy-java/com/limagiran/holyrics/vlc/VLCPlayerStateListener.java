package com.limagiran.holyrics.vlc;

public interface VLCPlayerStateListener {
   void stateChanged(VLCPlayerStateListener.EnumPlayerState var1);

   public static enum EnumPlayerState {
      PLAY,
      PAUSE;

      public boolean isPlay() {
         return this.equals(PLAY);
      }

      public boolean isPause() {
         return this.equals(PAUSE);
      }
   }
}
