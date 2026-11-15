package com.limagiran.holyrics.vlc;

import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VLC {
   public static final boolean IS_32BITS = Utils.is32Bits();
   public static final boolean CHECK_PATH = VLCUtils.checkPath();
   public static final VLCPlayerMini PLAYER_CONTROL;
   public static final boolean ENABLED;
   private static EmbeddedMediaPlayerComponent vlcView;
   private static EmbeddedMediaPlayerComponent backgroundPresentationView;
   private static final String[] MP_FACTORY_ARGS = new String[]{
      "--ignore-config",
      "--video-title=holyrics",
      "--no-plugins-cache",
      "--no-video-title-show",
      "--no-snapshot-preview",
      "--quiet",
      "--quiet-synchro",
      "--sub-filter=logo:marq",
      "--intf=dummy",
      "--no-xlib"
   };

   public static EmbeddedMediaPlayerComponent getBackgroundPresentationView() {
      if (ENABLED && IS_32BITS && CHECK_PATH) {
         try {
            if (backgroundPresentationView == null) {
               backgroundPresentationView = new EmbeddedMediaPlayerComponent() {
                  protected String[] onGetMediaPlayerFactoryArgs() {
                     return VLC.MP_FACTORY_ARGS;
                  }
               };
               EmbeddedMediaPlayer mp = backgroundPresentationView.getMediaPlayer();
               mp.setPlaySubItems(true);
               mp.mute(true);
               mp.setRepeat(true);
            }

            return backgroundPresentationView;
         } catch (Exception var1) {
         }
      }

      return null;
   }

   static {
      VLCPlayerMini pc = null;
      if (IS_32BITS && CHECK_PATH) {
         try {
            System.setProperty("VLC_PLUGIN_PATH", VLCUtils.VLC_PATH + "/plugins");
            NativeLibrary.addSearchPath("libvlc", VLCUtils.VLC_PATH);
            vlcView = new EmbeddedMediaPlayerComponent() {
               protected String[] onGetMediaPlayerFactoryArgs() {
                  return VLC.MP_FACTORY_ARGS;
               }
            };
            vlcView.getMediaPlayer().setPlaySubItems(true);
            VLCView v = new VLCView(vlcView);
            pc = new VLCPlayerMini(vlcView.getMediaPlayer(), Settings.SET.getVLCSettings(), v::setVisible);
         } catch (Exception var2) {
         }
      } else if (!IS_32BITS) {
         WebLafUtils.notificationErrorDelay(
            "<html><div style='width:300px'>  Não foi possível inicializar o VLC Media Player<br>  Uma versão 32bits do Java não foi encontrada, verifique os arquivos de instalação do programa.",
            0L
         );
      } else {
         WebLafUtils.notificationErrorDelay(
            "<html><div style='width:300px'>  Não foi possível inicializar o VLC Media Player<br>  Os arquivos do VLC Media Player não foram encontrados, verifique os arquivos de instalação do programa.",
            0L
         );
      }

      ENABLED = pc != null;
      PLAYER_CONTROL = pc;
   }
}
