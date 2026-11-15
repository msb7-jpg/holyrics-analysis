package com.limagiran.holyrics.playback;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.panel.WebPanelStyle;
import com.alee.laf.separator.WebSeparator;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

public class PlayBackPlayerMini extends WebPanel {
   private static final ImageIcon ICON_PLAY = new ImageIcon(Images.load("play24.png"));
   private static final ImageIcon ICON_PAUSE = new ImageIcon(Images.load("pause24.png"));
   private static final ImageIcon ICON_STOP = new ImageIcon(Images.load("stop24.png"));
   private MediaPlayer mediaPlayer;
   private File media;
   private int duration;
   private JLabel timeLabel;
   private JSlider positionSlider;
   private WebButton playPauseButton;
   private WebButton stopButton;
   private boolean mousePressedPlaying = false;
   private WebPanel panelPlayPauseStop;
   private WebSeparator separator;
   private int timeMillis;

   public PlayBackPlayerMini(File media) {
      this.createUI();
      this.setMedia(media, true);
   }

   private void createUI() {
      this.createControls();
      this.addComponents();
      this.registerListeners();
   }

   private void createControls() {
      this.timeLabel = new JLabel("00:00:00");
      this.positionSlider = new JSlider();
      this.positionSlider.setMinimum(0);
      this.positionSlider.setMaximum(100);
      this.positionSlider.setValue(0);
      this.positionSlider.setFocusable(false);
      this.positionSlider.setSize(240, 36);
      this.positionSlider.setPreferredSize(this.positionSlider.getSize());
      this.stopButton = this.createDefaultButton(ICON_STOP);
      this.stopButton.setToolTipText("Stop");
      this.playPauseButton = this.createDefaultButton(ICON_PLAY);
      this.playPauseButton.setToolTipText("Play/Pause");
      this.panelPlayPauseStop = new WebPanel(true, new GridBagLayout());
      this.separator = WebSeparator.createVertical();
   }

   private void addComponents() {
      this.setLayout(new BorderLayout(5, 0));
      this.panelPlayPauseStop.setMargin(0, 4, 0, 4);
      this.panelPlayPauseStop.add(this.playPauseButton, new GridBagConstraints());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets.left = gbc.insets.right = 6;
      gbc.fill = 3;
      this.separator.setDrawSideLines(false);
      this.panelPlayPauseStop.add(this.separator, gbc);
      this.panelPlayPauseStop.add(this.stopButton, new GridBagConstraints());
      this.add(this.panelPlayPauseStop, "West");
      this.add(this.timeLabel, "Center");
      this.add(this.positionSlider, "East");
      Arrays.asList(this, this.panelPlayPauseStop).forEach(c -> c.setOpaque(false));
   }

   private void runIfMPNonNull(Consumer<MediaPlayer> consumer) {
      MediaPlayer mp = this.mediaPlayer;
      BooleanOptional.runIfTrue(mp != null, () -> Platform.runLater(() -> consumer.accept(mp)));
   }

   private void registerListeners() {
      MouseAdapter mouseAdapter = new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            PlayBackPlayerMini.this.runIfMPNonNull(mp -> {
               if (PlayBackPlayerMini.this.isPlaying()) {
                  PlayBackPlayerMini.this.mousePressedPlaying = true;
                  mp.pause();
               } else {
                  PlayBackPlayerMini.this.mousePressedPlaying = false;
               }
            });
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            PlayBackPlayerMini.this.runIfMPNonNull(mp -> {
               mp.setStartTime(Duration.seconds((double)PlayBackPlayerMini.this.positionSlider.getValue()));
               mp.setStopTime(Duration.hours(24.0));
               BooleanOptional.runIfTrue(PlayBackPlayerMini.this.mousePressedPlaying, mp::play);
            });
         }
      };
      this.positionSlider.addMouseListener(mouseAdapter);
      this.positionSlider.addChangeListener(e -> this.updateTime((long)(this.positionSlider.getValue() * 1000)));
      this.stopButton.addActionListener(e -> this.runIfMPNonNull(MediaPlayer::stop));
      this.playPauseButton.addActionListener(e -> this.playPause());
   }

   private void playPause() {
      if (this.mediaPlayer == null) {
         Utils.error(I18N.MSG_ERROR_INVALID_FILE.format("") + "\n" + I18N.MSG_SELECT_A_MP3_AUDIO.val());
      } else if (this.isPlaying()) {
         this.runIfMPNonNull(MediaPlayer::pause);
      } else {
         this.runIfMPNonNull(MediaPlayer::play);
      }
   }

   public void play(int start, int stop) {
      if (this.mediaPlayer == null) {
         Utils.error(I18N.MSG_ERROR_INVALID_FILE.format("") + "\n" + I18N.MSG_SELECT_A_MP3_AUDIO.val());
      } else {
         this.runIfMPNonNull(mp -> {
            mp.stop();
            Utils.startThreadDaemon(() -> {
               for (int delay = 0; mp.getStatus() != Status.STOPPED && delay < 1000; delay += 50) {
                  Utils.sleep(50L);
               }

               switch (mp.getStatus()) {
                  case STOPPED:
                  case READY:
                     mp.setStartTime(Duration.millis((double)start));
                     mp.setStopTime(Duration.millis((double)stop));
                     mp.play();
               }
            });
         });
      }
   }

   private void updatePlayingUI() {
      SwingUtilities.invokeLater(() -> {
         boolean playing = this.isPlaying();
         this.playPauseButton.setIcon(playing ? ICON_PAUSE : ICON_PLAY);
         this.separator.setSeparatorColor(playing ? StyleUI.COLOR_VLC_PLAYER_PLAYING.darker() : WebPanelStyle.borderColor);
         this.panelPlayPauseStop.setWebColoredBackground(!playing);
         this.panelPlayPauseStop.setBackground(playing ? StyleUI.COLOR_VLC_PLAYER_PLAYING : WebPanelStyle.backgroundColor);
         this.panelPlayPauseStop.setBorderColor(playing ? StyleUI.COLOR_VLC_PLAYER_PLAYING.darker() : WebPanelStyle.borderColor);
      });
   }

   private WebButton createDefaultButton(ImageIcon ic) {
      WebButton wb = new WebButton(ic);
      wb.setFocusable(false);
      wb.setShadeColor(new Color(0, 0, 0, 0));
      wb.setShadeWidth(0);
      wb.setMargin(0, 8, 0, 8);
      wb.setUndecorated(true);
      return wb;
   }

   public void stop() {
      this.stopButton.doClick(0);
   }

   private void updateTime(long millis) {
      String text = StringUtils.formatTimeHMS(millis);
      SwingUtilities.invokeLater(() -> this.timeLabel.setText(text));
   }

   public void position(long value) {
      this.runIfMPNonNull(mp -> {
         boolean flag = this.isPlaying();
         mp.stop();
         mp.setStartTime(Duration.millis((double)value));
         this.timeMillis = (int)value;
         Utils.startThreadDaemon(() -> {
            Utils.sleep(250L);
            this.updatePosition((int)(value / 1000L));
            this.updateTime((long)((int)(value / 1000L)));
         });
         BooleanOptional.runIfTrue(flag, mp::play);
      });
   }

   private void updatePosition(int value) {
      SwingUtilities.invokeLater(() -> this.positionSlider.setValue(value));
   }

   public final synchronized void setMedia(File media, boolean deleteFileMedia) {
      this.runIfMPNonNull(mp -> {
         try {
            mp.stop();
            mp.dispose();
         } catch (Exception var4x) {
         }

         if (deleteFileMedia) {
            Optional.ofNullable(this.media).ifPresent(File::delete);
         }
      });
      this.media = media;

      try {
         this.updatePosition(0);
         this.updateTime(0L);
         this.mediaPlayer = null;
         Media _media = new Media(this.media.toURI().toString());
         this.mediaPlayer = new MediaPlayer(_media);
         this.setupMediaPlayerListeners();
      } catch (Exception var4) {
         this.mediaPlayer = null;
         this.duration = 0;
      }
   }

   public int getDuration() {
      return this.duration;
   }

   public boolean isPlaying() {
      MediaPlayer mp = this.mediaPlayer;
      return mp != null && Status.PLAYING.equals(mp.getStatus());
   }

   public String getTime() {
      return this.timeLabel.getText();
   }

   public long getTimeMillis() {
      return (long)this.timeMillis;
   }

   private void setupMediaPlayerListeners() {
      this.runIfMPNonNull(mp -> {
         Runnable fireStopOrEnd = () -> {
            try {
               if (!Status.DISPOSED.equals(mp.getStatus())) {
                  mp.setStartTime(Duration.ZERO);
                  mp.setStopTime(Duration.seconds(2.147483647E9));
               }
            } catch (Exception var3) {
            }

            SwingUtilities.invokeLater(() -> {
               this.updatePlayingUI();
               this.positionSlider.setValue(0);
            });
         };
         mp.setOnReady(() -> {
            Media _media = mp.getMedia();
            if (_media != null) {
               this.duration = (int)_media.getDuration().toSeconds();
               SwingUtils.runOnUIThread(() -> this.positionSlider.setMaximum(this.duration));
               _media.getMetadata().entrySet().forEach(entry -> {
               });
            }
         });
         mp.setOnPaused(this::updatePlayingUI);
         mp.setOnStopped(fireStopOrEnd);
         mp.setOnEndOfMedia(mp::stop);
         mp.setOnPlaying(this::updatePlayingUI);
         mp.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
            this.timeMillis = (int)newDuration.toMillis();
            this.updatePosition((int)newDuration.toSeconds());
            this.updateTime((long)this.timeMillis);
         });
      });
   }
}
