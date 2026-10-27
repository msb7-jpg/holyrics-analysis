package com.limagiran.holyrics.playback;

import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.panel.WebPanelStyle;
import com.alee.laf.separator.WebSeparator;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.vlc.VLCSettings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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

public class PlayBackPlayerMiniShow extends WebPanel {
   private static PlayBackPlayerMiniShow INSTANCE;
   private static final ImageIcon ICON_PLAY = new ImageIcon(Images.load("play24.png"));
   private static final ImageIcon ICON_PAUSE = new ImageIcon(Images.load("pause24.png"));
   private MediaPlayer mediaPlayer;
   private File media;
   private int duration;
   private JLabel timeLabel;
   private JSlider positionSlider;
   private JSlider volumeSlider;
   private WebPanel panelPlayPause;
   private WebButton playPauseButton;
   private WebToggleButton muteToggleButton;
   private boolean mousePressedPlaying = false;
   private WebSeparator separator;
   private int timeMillis;

   public static synchronized PlayBackPlayerMiniShow getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new PlayBackPlayerMiniShow();
      }

      return INSTANCE;
   }

   private PlayBackPlayerMiniShow() {
      this.init();
      this.setMedia(null, false);
   }

   private void init() {
      this.createComponents();
      this.addComponents();
      this.registerListeners();
   }

   private void createComponents() {
      this.timeLabel = new JLabel("00:00:00");
      this.positionSlider = new JSlider();
      this.positionSlider.setMinimum(0);
      this.positionSlider.setMaximum(100);
      this.positionSlider.setValue(0);
      this.positionSlider.setFocusable(false);
      this.positionSlider.setSize(240, 36);
      this.positionSlider.setPreferredSize(this.positionSlider.getSize());
      this.volumeSlider = new JSlider();
      this.volumeSlider.setMinimum(0);
      this.volumeSlider.setMaximum(100);
      VLCSettings set = Settings.SET.getVLCSettings();
      double volume = (double)(set.getVolume() - 0);
      double range = 120.0;
      this.volumeSlider.setValue(Utils.range((int)(volume / 120.0 * 100.0), 0, 100));
      this.volumeSlider.setFocusable(false);
      this.volumeSlider.setSize(240, 36);
      this.volumeSlider.setPreferredSize(this.volumeSlider.getSize());
      this.playPauseButton = this.createDefaultButton(ICON_PLAY);
      this.playPauseButton.setUndecorated(false);
      int l = (int)((double)ICON_PLAY.getIconWidth() * 1.5);
      this.playPauseButton.setSize(l, l);
      this.playPauseButton.setPreferredSize(l, l);
      this.playPauseButton.setRound(ICON_PLAY.getIconWidth());
      this.playPauseButton.setToolTipText("Play/Pause");
      this.muteToggleButton = this.createDefaultToggleButton("volumeUp16.png", "volumeOff16.png", set.isMute());
      this.muteToggleButton.setSelectedIcon(new ImageIcon(Images.load("volumeOff16.png", Color.RED)));
      this.muteToggleButton.setToolTipText("Mute");
      this.separator = WebSeparator.createVertical();
      this.panelPlayPause = new WebPanel(new GridBagLayout());
   }

   private void addComponents() {
      this.setLayout(new GridBagLayout());
      this.panelPlayPause.add(this.playPauseButton, new GridBagConstraints());
      WebPanel panelPosition = new WebPanel(new BorderLayout(5, 0));
      panelPosition.add(this.timeLabel, "West");
      panelPosition.add(this.positionSlider, "Center");
      WebPanel panelVolume = new WebPanel(new BorderLayout(5, 0));
      panelVolume.add(this.muteToggleButton, "West");
      panelVolume.add(this.volumeSlider, "Center");
      WebPanel panelSliders = new WebPanel(new GridLayout(2, 1, 0, 4), new Component[]{panelPosition, panelVolume});
      WebPanel panel = new WebPanel(new BorderLayout(5, 5));
      panel.setMargin(4);
      panel.add(this.panelPlayPause, "West");
      panel.add(panelSliders, "Center");
      this.add(panel, new GridBagConstraints());
      Arrays.asList(this, this.panelPlayPause, panelVolume, panelVolume, panelSliders).forEach(c -> c.setOpaque(false));
   }

   private void runIfMPNonNull(Consumer<MediaPlayer> consumer) {
      MediaPlayer mp = this.mediaPlayer;
      BooleanOptional.runIfTrue(mp != null, () -> Platform.runLater(() -> consumer.accept(mp)));
   }

   private void registerListeners() {
      MouseAdapter mouseAdapter = new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            PlayBackPlayerMiniShow.this.runIfMPNonNull(mp -> {
               if (PlayBackPlayerMiniShow.isPlaying()) {
                  PlayBackPlayerMiniShow.this.mousePressedPlaying = true;
                  mp.pause();
               } else {
                  PlayBackPlayerMiniShow.this.mousePressedPlaying = false;
               }
            });
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            PlayBackPlayerMiniShow.this.runIfMPNonNull(mp -> {
               mp.setStartTime(Duration.seconds((double)PlayBackPlayerMiniShow.this.positionSlider.getValue()));
               mp.setStopTime(Duration.hours(24.0));
               BooleanOptional.runIfTrue(PlayBackPlayerMiniShow.this.mousePressedPlaying, mp::play);
            });
         }
      };
      this.positionSlider.addMouseListener(mouseAdapter);
      this.positionSlider.addChangeListener(e -> this.updateTime((long)(this.positionSlider.getValue() * 1000)));
      this.muteToggleButton.addActionListener(e -> this.runIfMPNonNull(mp -> mp.setMute(this.muteToggleButton.isSelected())));
      this.volumeSlider.addChangeListener(e -> {
         this.runIfMPNonNull(mp -> {
            mp.setVolume(Utils.range((double)this.volumeSlider.getValue() / 100.0, 0.0, 1.0));
            BooleanOptional.runIfTrue(mp.isMute(), () -> mp.setMute(false));
         });
         SwingUtilities.invokeLater(() -> this.muteToggleButton.setSelected(false));
      });
      this.playPauseButton.addActionListener(e -> this.playPause());
   }

   public void playPause() {
      if (this.mediaPlayer == null) {
         Utils.error(I18N.MSG_ERROR_INVALID_FILE.format("") + "\n" + I18N.MSG_SELECT_A_MP3_AUDIO.val());
      } else if (isPlaying()) {
         this.runIfMPNonNull(MediaPlayer::pause);
      } else {
         this.runIfMPNonNull(MediaPlayer::play);
      }
   }

   private void updatePlayingUI() {
      SwingUtilities.invokeLater(() -> {
         boolean playing = isPlaying();
         this.playPauseButton.setIcon(playing ? ICON_PAUSE : ICON_PLAY);
         this.separator.setSeparatorColor(playing ? StyleUI.COLOR_VLC_PLAYER_PLAYING.darker() : WebPanelStyle.borderColor);
         this.panelPlayPause.setWebColoredBackground(!playing);
         this.panelPlayPause.setBackground(playing ? StyleUI.COLOR_VLC_PLAYER_PLAYING : WebPanelStyle.backgroundColor);
         this.panelPlayPause.setBorderColor(playing ? StyleUI.COLOR_VLC_PLAYER_PLAYING.darker() : WebPanelStyle.borderColor);
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

   private WebToggleButton createDefaultToggleButton(String iconName, String selectedIcon, boolean selected) {
      WebToggleButton wtb = new WebToggleButton(new ImageIcon(Images.load(iconName)), selected);
      wtb.setSelectedIcon(new ImageIcon(Images.load(selectedIcon)));
      wtb.setFocusable(false);
      wtb.setShadeColor(new Color(0, 0, 0, 0));
      wtb.setUndecorated(true);
      return wtb;
   }

   private void updateTime(long millis) {
      String text = StringUtils.formatTimeHMS(millis);
      SwingUtilities.invokeLater(() -> this.timeLabel.setText(text));
   }

   public void position(long value) {
      this.runIfMPNonNull(mp -> {
         boolean flag = isPlaying();
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
         this.setupMediaPlayer();
      } catch (Exception var4) {
         this.mediaPlayer = null;
         this.duration = 0;
      }
   }

   public int getDuration() {
      return this.duration;
   }

   public static boolean isPlaying() {
      if (INSTANCE == null) {
         return false;
      } else {
         MediaPlayer mp = INSTANCE.mediaPlayer;
         return mp != null && Status.PLAYING.equals(mp.getStatus());
      }
   }

   public boolean isStopped() {
      MediaPlayer mp = this.mediaPlayer;
      if (mp == null) {
         return true;
      } else {
         switch (mp.getStatus()) {
            case READY:
            case PLAYING:
            case PAUSED:
               return false;
            default:
               return true;
         }
      }
   }

   public boolean isCurrentMedia(File media) {
      File f = this.media;
      return f != null && media != null && f.getAbsolutePath().equalsIgnoreCase(media.getAbsolutePath());
   }

   public String getTime() {
      return this.timeLabel.getText();
   }

   public long getTimeMillis() {
      return (long)this.timeMillis;
   }

   private void setupMediaPlayer() {
      this.runIfMPNonNull(mp -> {
         mp.setMute(this.muteToggleButton.isSelected());
         mp.setVolume(Utils.range((double)this.volumeSlider.getValue() / 100.0, 0.0, 1.0));
         Runnable fireStopOrEnd = () -> {
            try {
               if (!Status.DISPOSED.equals(mp.getStatus())) {
                  mp.setStartTime(Duration.ZERO);
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
