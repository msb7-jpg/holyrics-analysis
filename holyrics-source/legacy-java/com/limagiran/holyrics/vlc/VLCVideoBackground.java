package com.limagiran.holyrics.vlc;

import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.MediaPlayerUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.PresentationJDialog;
import com.limagiran.holyrics.view.ThemeView;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VLCVideoBackground extends JDialog implements ComponentListener {
   public static volatile boolean playing;
   private final VLCVideoBackgroundBase base;
   private final EmbeddedMediaPlayerComponent mediaView;
   protected final EmbeddedMediaPlayer mediaPlayer;
   protected double maxOpacity = 1.0;

   public VLCVideoBackground(VLCVideoBackgroundBase base, EmbeddedMediaPlayerComponent mediaView) {
      super((Dialog)base);
      this.base = base;
      this.mediaView = mediaView;
      this.mediaPlayer = mediaView.getMediaPlayer();
      this.initComponents();
      this.init();
   }

   private void init() {
      access$001(this, 0.0F);
      this.setFocusable(false);
      this.setFocusableWindowState(false);
      this.setAlwaysOnTop(true);
      this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
      this.getContentPane().setBackground(Color.BLACK);
      this.add(this.mediaView, "Center");
      MouseAdapter mouseAdapter = new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            VLCVideoBackground.this.base.runIfDialogIsNotNull(d -> {
               d.toFront();
               d.requestFocus();
            });
         }
      };
      this.mediaView.addMouseListener(mouseAdapter);
      this.addMouseListener(mouseAdapter);
   }

   private void initComponents() {
      this.setDefaultCloseOperation(0);
      this.setBackground(new Color(0, 0, 0));
      this.setUndecorated(true);
      this.setOpacity(0.0F);
      this.setResizable(false);
      this.pack();
      this.setLocationRelativeTo(null);
   }

   protected void setup(PresentationJDialog dialog) {
      dialog.removeComponentListener(this);
      dialog.addComponentListener(this);
   }

   public void play(Theme theme) {
      Utils.safeException(() -> {
         if (this.isDisplayable()) {
            try {
               this.maxOpacity = theme.getBackground().getOpacityPercent();
               float rate = Utils.range(theme.getVideoRate(), 0.25F, 4.0F);
               Background bg = theme.getBackground();
               if (!(bg instanceof VLCUtils.MRLtoVLC)) {
                  return;
               }

               String mrl = ((VLCUtils.MRLtoVLC)bg).getMRLtoVLC();
               MediaPlayerUtils.stop(ThemeView.MEDIA_PLAYER);
               MediaPlayerUtils.stop(PresentationJDialog.MEDIA_PLAYER);
               playing = true;
               VLCUtils.runVLCThread(() -> {
                  if (this.mediaPlayer.isPlaying()) {
                     this.mediaPlayer.stop();
                  }

                  while (this.mediaPlayer.isPlaying()) {
                     Utils.sleep(25L);
                  }

                  Utils.sleep(25L);
                  this.mediaPlayer.setAspectRatio(this.getWidth() + ":" + this.getHeight());
                  this.mediaPlayer.setRate(rate);
                  playing = true;
                  this.mediaPlayer.playMedia(mrl, new String[0]);
               });
            } catch (Exception var5) {
            }
         }
      });
   }

   public void stop() {
      playing = false;
      VLCUtils.runVLCThread(this.mediaPlayer::stop);
   }

   @Override
   public void setOpacity(float opacity) {
      Utils.safeException(() -> super.setOpacity(Utils.range((float)((double)opacity * this.maxOpacity), 0.0F, 1.0F)));
   }

   @Override
   public void componentResized(ComponentEvent e) {
      this.base.runIfDialogIsNotNull(d -> this.setBounds(d.getBounds()));
   }

   @Override
   public void componentMoved(ComponentEvent e) {
      this.base.runIfDialogIsNotNull(d -> this.setBounds(d.getBounds()));
   }

   @Override
   public void componentShown(ComponentEvent e) {
   }

   @Override
   public void componentHidden(ComponentEvent e) {
   }
}
