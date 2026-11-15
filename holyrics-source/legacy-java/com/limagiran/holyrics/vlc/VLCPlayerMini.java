package com.limagiran.holyrics.vlc;

import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.list.WebList;
import com.alee.laf.list.WebListCellRenderer;
import com.alee.laf.list.WebListModel;
import com.alee.laf.list.WebListStyle;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.panel.WebPanelStyle;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.slider.WebSlider;
import com.alee.managers.hotkey.Hotkey;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ListDragAndDropString;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.FFMPEG;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MediaPlayerUtils;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.view.PresentationJDialog;
import com.limagiran.holyrics.view.ThemeView;
import com.limagiran.holyrics.webservice.Pack;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class VLCPlayerMini extends WebPanel implements VLCPlayerStateListener {
   private static final FFMPEG FFMPEG_OBJ = new FFMPEG();
   private static final ImageIcon ICON_TITLE_DEFAULT = new ImageIcon(Images.load("music32.png"));
   private static final ImageIcon ICON_PLAY = new ImageIcon(Images.load("play16.png"));
   private static final ImageIcon ICON_PAUSE = new ImageIcon(Images.load("pause16.png"));
   private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
   private final EmbeddedMediaPlayer mediaPlayer;
   private JLabel titleLabel;
   private WebButton playlistButton;
   private JLabel timeLabel;
   private JSlider positionSlider;
   private WebToggleButton repeatToggleButton;
   private WebToggleButton fullscreenToggleButton;
   private WebButton playPauseButton;
   private volatile boolean playing;
   private volatile boolean stopped = true;
   private WebButton stopButton;
   private WebButton skipNextButton;
   private WebButton skipPreviousButton;
   private WebToggleButton muteToggleButton;
   private WebSlider volumeSlider;
   private WebButton connectButton;
   private WebButton fileButton;
   private boolean mousePressedPlaying = false;
   private final VLCSettings set;
   private final VLCPlayerMini.VLCPlayerMiniListener listener;
   private VLCPlayerStateListener playerStateListener;
   private final WebPanel toolBarControl = new WebPanel(true, new GridBagLayout());
   private final Component toolBarControlPadding = Box.createHorizontalStrut(12);
   private final WebSeparator toolBarControlSeparator = WebSeparator.createVertical();
   private WebButton playPauseToolBarButton;
   private WebButton stopToolBarButton;
   private boolean firstPlay;
   private WebDialog wpoPlaylist;
   private final WebListModel<String> modelPlaylist = new WebListModel();
   private WebList listViewPlayList;
   private String currentMedia = "";
   private volatile long currentTimeMillis;
   private long lastChange = 0L;

   protected VLCPlayerMini(EmbeddedMediaPlayer mediaPlayer, VLCSettings set, VLCPlayerMini.VLCPlayerMiniListener listener) {
      this.mediaPlayer = mediaPlayer;
      this.set = set;
      this.listener = listener;
      this.createUI();
      this.executorService.scheduleAtFixedRate(new VLCPlayerMini.UpdateRunnable(mediaPlayer), 0L, 1L, TimeUnit.SECONDS);
   }

   public Dimension getSize() {
      return new Dimension(310, super.getSize().height);
   }

   public Dimension getPreferredSize() {
      return new Dimension(310, super.getPreferredSize().height);
   }

   private void createUI() {
      this.createControls();
      this.addComponents();
      this.registerListeners();
      this.init();
   }

   private void createControls() {
      this.playPauseToolBarButton = this.createDefaultButton("play16.png");
      this.playPauseToolBarButton.setUndecorated(true);
      this.playPauseToolBarButton.setToolTipText("Play/Pause (F3)");
      this.stopToolBarButton = this.createDefaultButton("stop16.png");
      this.stopToolBarButton.setToolTipText("Stop (F4)");
      this.stopToolBarButton.setUndecorated(true);
      this.titleLabel = new JLabel("Player", 0);
      this.titleLabel.setFont(new Font("Trebuchet MS", 1, 14));
      this.titleLabel.setIcon(ICON_TITLE_DEFAULT);
      this.titleLabel.setMinimumSize(new Dimension());
      this.playlistButton = this.createDefaultButton("list16.png");
      this.playlistButton.setUndecorated(true);
      this.timeLabel = new JLabel("00:00:00");
      this.positionSlider = new JSlider();
      this.positionSlider.setMinimum(0);
      this.positionSlider.setMaximum(1000);
      this.positionSlider.setValue(0);
      this.positionSlider.setFocusable(false);
      this.repeatToggleButton = this.createDefaultToggleButton("repeat16.png", "repeat16.png", !this.set.isNoRepeat());
      this.repeatToggleButton.setToolTipText(I18N.WORD_REPEAT.val());
      this.repeatToggleButton.setText(this.set.getRepeatChar());
      this.repeatToggleButton.setUndecorated(false);
      this.repeatToggleButton.setRound(4);
      this.repeatToggleButton.setFont(new Font("Courier New", 0, 12));
      VLCUtils.runVLCThread(() -> this.mediaPlayer.setRepeat(this.set.isRepeatOne()));
      this.stopButton = this.createDefaultButton("stop16.png");
      this.stopButton.setToolTipText("Stop (F4)");
      this.stopButton.setMargin(0);
      this.skipPreviousButton = this.createDefaultButton("skipPrevious16.png");
      this.skipPreviousButton.setMargin(0);
      this.skipPreviousButton.setUndecorated(true);
      this.skipNextButton = this.createDefaultButton("skipNext16.png");
      this.skipNextButton.setMargin(0);
      this.skipNextButton.setUndecorated(true);
      this.fullscreenToggleButton = this.createDefaultToggleButton("fullScreen16.png", "fullScreen16.png", false);
      this.fullscreenToggleButton.setToolTipText(I18N.WORD_FULLSCREEN.val() + " (F11)");
      this.fullscreenToggleButton.setUndecorated(false);
      this.fullscreenToggleButton.setMargin(0);
      this.fullscreenToggleButton.setSelectedForeground(Color.RED);
      this.playPauseButton = this.createDefaultButton("play16.png");
      this.playPauseButton.setToolTipText("Play/Pause (F3)");
      this.playPauseButton.setMargin(4);
      this.playPauseButton.setRound(16);
      this.playPauseButton.addPropertyChangeListener("icon", e -> this.playPauseToolBarButton.setIcon(this.playPauseButton.getIcon()));
      this.muteToggleButton = this.createDefaultToggleButton("volumeUp16.png", "volumeOff16.png", this.set.isMute());
      this.muteToggleButton.setSelectedIcon(new ImageIcon(Images.load("volumeOff16.png", Color.RED)));
      this.muteToggleButton.setToolTipText("Mute");
      VLCUtils.runVLCThread(() -> this.mediaPlayer.mute(this.muteToggleButton.isSelected()));
      this.volumeSlider = new WebSlider();
      this.volumeSlider.setPreferredWidth(75);
      this.volumeSlider.setOrientation(0);
      this.volumeSlider.setMinimum(0);
      this.volumeSlider.setMaximum(120);
      this.volumeSlider.setFocusable(false);
      this.volumeSlider.setValue(this.set.getVolume());
      VLCUtils.runVLCThread(() -> this.mediaPlayer.setVolume(this.volumeSlider.getValue()));
      this.connectButton = this.createDefaultButton("web16.png");
      this.connectButton.setUndecorated(true);
      this.fileButton = this.createDefaultButton("open16.png");
      this.fileButton.setUndecorated(true);
      this.playPauseToolBarButton.addActionListener(e -> this.playPauseButton.doClick(0));
      this.stopToolBarButton.addActionListener(e -> this.stopButton.doClick(0));
      this.toolBarControl.setMargin(0, 8, 0, 8);
      this.toolBarControl.add(this.playPauseToolBarButton, new GridBagConstraints());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets.left = gbc.insets.right = 6;
      gbc.fill = 3;
      this.toolBarControlSeparator.setDrawSideLines(false);
      this.toolBarControl.add(this.toolBarControlSeparator, gbc);
      this.toolBarControl.add(this.stopToolBarButton, new GridBagConstraints());
   }

   private void addComponents() {
      this.setLayout(new BorderLayout());
      JPanel positionPanel = new JPanel(new GridLayout(1, 1));
      positionPanel.add(this.positionSlider);
      JPanel topPanel = new JPanel(new BorderLayout(8, 0));
      topPanel.add(this.titleLabel, "Center");
      topPanel.add(this.playlistButton, "East");
      JPanel middlePanel = new JPanel(new GridBagLayout());
      middlePanel.add(this.timeLabel, new GridBagConstraints());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.weightx = 1.0;
      gbc.fill = 2;
      gbc.insets.left = gbc.insets.right = 8;
      middlePanel.add(positionPanel, gbc);
      middlePanel.add(this.repeatToggleButton, new GridBagConstraints());
      this.add(middlePanel, "North");
      JPanel bottomPanel = new JPanel(new GridBagLayout());
      bottomPanel.add(this.fullscreenToggleButton, new GridBagConstraints());
      gbc = new GridBagConstraints();
      gbc.insets.left = 12;
      bottomPanel.add(this.skipPreviousButton, gbc);
      gbc = new GridBagConstraints();
      gbc.insets.left = gbc.insets.right = 2;
      bottomPanel.add(this.playPauseButton, gbc);
      bottomPanel.add(this.skipNextButton, new GridBagConstraints());
      gbc = new GridBagConstraints();
      gbc.insets.left = 4;
      gbc.insets.right = 8;
      bottomPanel.add(this.stopButton, gbc);
      bottomPanel.add(this.muteToggleButton, new GridBagConstraints());
      bottomPanel.add(this.volumeSlider, new GridBagConstraints());
      gbc = new GridBagConstraints();
      gbc.insets.left = 8;
      gbc.insets.right = -2;
      bottomPanel.add(this.fileButton, gbc);
      bottomPanel.add(this.connectButton, gbc);
      this.add(topPanel, "First");
      this.add(middlePanel, "Center");
      this.add(bottomPanel, "Last");
      Arrays.asList(topPanel, middlePanel, bottomPanel, positionPanel).forEach(c -> c.setOpaque(false));
   }

   private void init() {
      this.setOpaque(false);
      if (!Settings.EnumSettings2.VLC_PLAYLIST.getString().trim().isEmpty()) {
         this.setPlaylist(Settings.EnumSettings2.VLC_PLAYLIST.getString().split("\n"), null);
      }

      this.modelPlaylist.addListDataListener(new ListDataListener() {
         @Override
         public void intervalAdded(ListDataEvent e) {
            this.fire();
         }

         @Override
         public void intervalRemoved(ListDataEvent e) {
            this.fire();
         }

         @Override
         public void contentsChanged(ListDataEvent e) {
            this.fire();
         }

         private void fire() {
            if (VLCPlayerMini.this.wpoPlaylist != null) {
               VLCPlayerMini.this.wpoPlaylist.pack();
            }

            VLCPlayerMini.this.savePlaylist();
         }
      });
   }

   private void updateUIState() {
      VLCUtils.runVLCThread(() -> {
         if (!this.mediaPlayer.isPlaying() && !this.mousePressedPlaying) {
            int oldVolume = this.mediaPlayer.getVolume();
            this.mediaPlayer.setVolume(0);
            this.mediaPlayer.play();
            Utils.sleep(500L);
            this.mediaPlayer.pause();
            this.mediaPlayer.setVolume(oldVolume);
         }

         if (this.mediaPlayer.isSeekable()) {
            float positionValue = (float)this.positionSlider.getValue() / 1000.0F;
            if (positionValue > 0.98F) {
               positionValue = 0.98F;
            }

            this.mediaPlayer.setPosition(positionValue);
            Utils.sleep(100L);
         }

         if (this.mousePressedPlaying) {
            this.mediaPlayer.play();
         }

         if (this.mediaPlayer.isSeekable()) {
            long time = this.mediaPlayer.getTime();
            int position = (int)(this.mediaPlayer.getPosition() * 1000.0F);
            this.updateTime(time);
            this.updatePosition(position);
         }
      });
   }

   private void registerListeners() {
      VLCUtils.runVLCThread(() -> this.mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            private static final String PREFIX = "[ERROR] -> ";

            public void paused(MediaPlayer mediaPlayer) {
               super.paused(mediaPlayer);
               VLCPlayerMini.this.playing = false;
               VLCPlayerMini.this.updatePlayingUI();
            }

            public void stopped(MediaPlayer mediaPlayer) {
               super.stopped(mediaPlayer);
               this.fireStop();
            }

            public void finished(MediaPlayer mediaPlayer) {
               super.finished(mediaPlayer);
               if (!VLCPlayerMini.this.set.isRepeatOne()) {
                  VLCUtils.runVLCThread(VLCPlayerMini.this.mediaPlayer::stop);
               }

               if (VLCPlayerMini.this.set.isRepeatAll() && !VLCPlayerMini.this.modelPlaylist.isEmpty()) {
                  VLCPlayerMini.this.skip(1, true);
               }
            }

            private void fireStop() {
               VLCPlayerMini.this.firstPlay = true;
               VLCPlayerMini.this.playing = false;
               VLCPlayerMini.this.stopped = true;
               SwingUtilities.invokeLater(() -> {
                  VLCPlayerMini.this.updatePlayingUI();
                  VLCPlayerMini.this.positionSlider.setValue(0);
                  if (!VLCPlayerMini.this.titleLabel.getText().startsWith("[ERROR] -> ") && VLCPlayerMini.this.set.isNoRepeat()) {
                     VLCPlayerMini.this.fullscreenToggleButton.setSelected(false);
                  }
               });
            }

            public void playing(MediaPlayer mediaPlayer) {
               super.playing(mediaPlayer);
               VLCPlayerMini.this.playing = true;
               VLCPlayerMini.this.stopped = false;
               VLCPlayerMini.this.updatePlayingUI();
               if (VLCPlayerMini.this.titleLabel.getText().startsWith("[ERROR] -> ")) {
                  VLCPlayerMini.this.updateTitle(VLCPlayerMini.this.getMetaName(), StyleUI.FOREGROUND_DARK);
               }
            }

            public void error(MediaPlayer mediaPlayer) {
               super.error(mediaPlayer);
               if (VLCUtils.isYoutube(VLCPlayerMini.this.currentMedia)) {
                  String ytstream = VLCUtils.getYouTubeStreamFirstUrl(VLCPlayerMini.this.currentMedia);
                  if (ytstream != null) {
                     VLCUtils.runVLCThread(() -> VLCPlayerMini.this.mediaPlayer.playMedia(ytstream, new String[0]));
                     return;
                  }
               }

               VLCPlayerMini.this.playing = false;
               VLCPlayerMini.this.updatePlayingUI();
               if (!VLCPlayerMini.this.titleLabel.getText().startsWith("[ERROR] -> ")) {
                  VLCPlayerMini.this.updateTitle("[ERROR] -> " + VLCPlayerMini.this.getMetaName(), StyleUI.COLOR_ERROR);
                  File f = new File(VLCPlayerMini.this.currentMedia);
                  if (Dir.isFile(f) && !f.exists()) {
                     Utils.error(I18N.MSG_ERROR_FILE_NOT_FOUND_X.format(VLCPlayerMini.this.currentMedia));
                  }
               }
            }

            public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
               super.mediaChanged(mediaPlayer, media, mrl);
               VLCPlayerMini.this.playing = false;
               SwingUtilities.invokeLater(() -> {
                  VLCPlayerMini.this.updatePlayingUI();
                  VLCPlayerMini.this.positionSlider.setValue(0);
               });
            }
         }));
      this.positionSlider.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            VLCUtils.runVLCThread(() -> {
               if (VLCPlayerMini.this.mediaPlayer.isPlaying()) {
                  VLCPlayerMini.this.mousePressedPlaying = true;
                  VLCPlayerMini.this.mediaPlayer.pause();
               } else {
                  VLCPlayerMini.this.mousePressedPlaying = false;
               }
            });
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            VLCPlayerMini.this.updateUIState();
         }
      });
      this.stopButton.addActionListener(e -> {
         this.setFullscreen(false);
         VLCUtils.runVLCThread(this.mediaPlayer::stop);
      });
      this.playPauseButton.addActionListener(e -> {
         if (!this.currentMedia.trim().isEmpty()) {
            if (this.playing) {
               VLCUtils.runVLCThread(this.mediaPlayer::pause);
            } else {
               if (this.firstPlay && (VLCUtils.isYoutube(this.currentMedia) || this.isVideo() || this.isStream())) {
                  this.firstPlay = false;
                  this.setFullscreen(true);
               }

               if (this.isVideo()) {
                  MediaPlayerUtils.stop(ThemeView.MEDIA_PLAYER);
                  MediaPlayerUtils.stop(PresentationJDialog.MEDIA_PLAYER);
               }

               VLCUtils.runVLCThread(this.mediaPlayer::play);
            }
         }
      });
      this.fullscreenToggleButton.addItemListener(e -> this.listener.fullscreen(e.getStateChange() == 1));
      this.repeatToggleButton.addActionListener(e -> {
         this.set.nextRepeatOption();
         VLCUtils.runVLCThread(() -> this.mediaPlayer.setRepeat(this.set.isRepeatOne()));
         SwingUtilities.invokeLater(() -> {
            this.repeatToggleButton.setText(this.set.getRepeatChar());
            this.repeatToggleButton.setSelected(!this.set.isNoRepeat());
         });
      });
      this.muteToggleButton.addActionListener(e -> VLCUtils.runVLCThread(() -> this.mediaPlayer.mute(this.set.setMute(this.muteToggleButton.isSelected()))));
      this.volumeSlider.addChangeListener(e -> {
         JSlider source = (JSlider)e.getSource();
         this.set.setVolume(source.getValue());
         VLCUtils.runVLCThread(() -> {
            this.mediaPlayer.setVolume(this.set.getVolume());
            if (this.mediaPlayer.isMute()) {
               this.mediaPlayer.mute(false);
            }

            SwingUtilities.invokeLater(() -> this.muteToggleButton.setSelected(false));
         });
      });
      this.connectButton.addActionListener(e -> VLCUtils.openInputLink(this, link -> this.setPlaylist(link, null)));
      this.fileButton
         .addActionListener(
            e -> VLCUtils.openSelectMultiple(
                  VLCUtils.EnumFileType.MEDIA, f -> this.setPlaylist(f.stream().map(File::getAbsolutePath).toArray(String[]::new), null)
               )
         );
      this.playlistButton.addActionListener(e -> this.showPlaylist());
      this.skipPreviousButton.addActionListener(e -> this.skip(-1, this.playing));
      this.skipNextButton.addActionListener(e -> this.skip(1, this.playing));
   }

   private void updatePlayingUI() {
      SwingUtilities.invokeLater(() -> {
         this.playPauseButton.setIcon(this.playing ? ICON_PAUSE : ICON_PLAY);
         this.toolBarControlSeparator.setSeparatorColor(this.playing ? StyleUI.COLOR_VLC_PLAYER_PLAYING.darker() : WebPanelStyle.borderColor);
         this.toolBarControl.setWebColoredBackground(!this.playing);
         this.toolBarControl.setBackground(this.playing ? StyleUI.COLOR_VLC_PLAYER_PLAYING : WebPanelStyle.backgroundColor);
         this.toolBarControl.setBorderColor(this.playing ? StyleUI.COLOR_VLC_PLAYER_PLAYING.darker() : WebPanelStyle.borderColor);
         this.stateChanged(this.playing ? VLCPlayerStateListener.EnumPlayerState.PLAY : VLCPlayerStateListener.EnumPlayerState.PAUSE);
      });
   }

   private WebButton createDefaultButton(String iconName) {
      WebButton wb = new WebButton(new ImageIcon(Images.load(iconName)));
      wb.setFocusable(false);
      wb.setShadeColor(new Color(0, 0, 0, 0));
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

   public void stop() {
      this.stopButton.doClick(0);
   }

   public void playPause() {
      this.playPauseButton.doClick(0);
   }

   public void fullscreen() {
      this.fullscreenToggleButton.doClick(0);
   }

   public void mute() {
      this.muteToggleButton.doClick(0);
   }

   public void volume(int volume) {
      try {
         this.volumeSlider.setValue(Utils.range(volume, 0, 120));
      } catch (Exception var3) {
      }
   }

   public void position(int position) {
      VLCUtils.runVLCThread(() -> {
         if (this.mediaPlayer.isSeekable()) {
            float positionValue = (float)Utils.range(position, 0, 1000).intValue() / 1000.0F;
            if (positionValue > 0.99F) {
               positionValue = 0.99F;
            }

            this.mediaPlayer.setPosition(positionValue);
         }
      });
   }

   public void skipNext() {
      this.skipNextButton.doClick(0);
   }

   public void skipPrevious() {
      this.skipPreviousButton.doClick(0);
   }

   private void updateThumbnail() {
      final long time = System.currentTimeMillis();
      if (VLCUtils.isYoutube(this.currentMedia)) {
         Utils.startThreadDaemon(() -> {
            ThumbMediaController.loadYouTube(this.currentMedia);
            SwingUtilities.invokeLater(() -> this.setThumbnail(new ImageIcon(ThumbMediaController.getYouTubeBI(this.currentMedia)), time));
         });
      } else {
         SwingUtilities.invokeLater(() -> this.setThumbnail(ICON_TITLE_DEFAULT, time));
         BufferedImage bi = ThumbMediaController.getOrDefaultFullPath(this.currentMedia, null);
         if (bi != null) {
            SwingUtilities.invokeLater(() -> this.setThumbnail(new ImageIcon(Img.resize(bi, new Dimension(56, 32))), time));
         } else {
            final File output = new File("holyrics-thumbnail-tmp" + System.nanoTime() + ".jpg").getAbsoluteFile();
            FFMPEG_OBJ.getImage(new File(this.currentMedia), output, new Dimension(0, 32), 2, new FFMPEG.FFMPEGAdapter() {
               @Override
               public void success() {
                  try {
                     BufferedImage bi = ImageIO.read(output);
                     SwingUtilities.invokeLater(() -> VLCPlayerMini.this.setThumbnail(new ImageIcon(bi), time));
                  } catch (Exception var5) {
                  } finally {
                     output.delete();
                  }
               }
            });
         }
      }
   }

   private void setThumbnail(ImageIcon ic, long time) {
      if (time >= this.lastChange) {
         this.lastChange = time;
         this.titleLabel.setIcon(ic);
      }
   }

   private boolean isStream() {
      String url = this.currentMedia.toLowerCase();
      return (url.contains("http://") || url.contains("https://")) && url.contains("/video");
   }

   private boolean isVideo() {
      return HItemUtils.check(this.currentMedia, VLCUtils.EnumFileType.VIDEO);
   }

   private void skip(int i, boolean play) {
      Utils.safeException(
         () -> this.setMedia(
               (String)this.modelPlaylist.get((this.modelPlaylist.size() + this.modelPlaylist.indexOf(this.currentMedia) + i) % this.modelPlaylist.size()),
               () -> BooleanOptional.runIfTrue(play, this::playPause)
            )
      );
   }

   private void showPlaylist() {
      if (this.wpoPlaylist == null) {
         this.wpoPlaylist = new WebDialog(MusicMain.INSTANCE);
         this.wpoPlaylist.setDefaultCloseOperation(2);
         InsertShortcut.addEscToDispose(this.wpoPlaylist);
         this.wpoPlaylist.setCloseOnFocusLoss(false);
         this.wpoPlaylist.setLayout(new BorderLayout());
         this.listViewPlayList = new ListDragAndDropString(this.modelPlaylist, new WebListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
               String string = String.valueOf(value);
               File f = new File(string);
               if (Dir.isFile(f)) {
                  string = f.getName();
               } else if (VLCUtils.isYoutube(string)) {
                  string = VLCUtils.getYouTubeTitleOffline(string);
               }

               Component c = super.getListCellRendererComponent(list, string, index, isSelected, cellHasFocus);
               if (String.valueOf(value).equalsIgnoreCase(VLCPlayerMini.this.currentMedia)) {
                  c.setForeground(Color.BLUE.darker());
                  c.setFont(c.getFont().deriveFont(1));
               } else {
                  c.setForeground(WebListStyle.foreground);
                  c.setFont(c.getFont().deriveFont(0));
               }

               return c;
            }
         });
         Runnable removeConsumer = () -> {
            int selected = this.listViewPlayList.getSelectedIndex();
            this.removePlayList(this.listViewPlayList.getSelectedValuesList());
            this.listViewPlayList.setSelectedIndex(Math.min(selected, this.modelPlaylist.getSize() - 1));
            this.listViewPlayList.repaint();
         };
         this.listViewPlayList.addListSelectionListener(e -> {
            if (this.listViewPlayList.getSelectedIndex() == -1) {
               this.listViewPlayList.setUnselectable(false);
            }
         });
         this.listViewPlayList.setSelectedValue(this.currentMedia);
         this.listViewPlayList.onMenuTrigger(e1 -> SwingUtils.popupMenuTrigger(e1, this.listViewPlayList, item -> {
               WebPopupMenu popup = new WebPopupMenu();
               if (!VLCUtils.isYoutube(item.toString()) && !VLCUtils.isLink(item.toString())) {
                  WebMenuItem openFileLocation = new WebMenuItem(I18N.MSG_OPEN_FILE_LOCATION.val());
                  openFileLocation.addActionListener(evt -> Utils.openLocation(new File(item.toString())));
                  popup.add(openFileLocation);
               }

               WebMenuItem removex = new WebMenuItem(I18N.WORD_REMOVE.val());
               removex.addActionListener(evt -> removeConsumer.run());
               popup.add(removex);
               popup.show(this.listViewPlayList, e1.getX() - 20, e1.getY());
            }));
         this.listViewPlayList.setMultiplySelectionAllowed(false);
         this.listViewPlayList.setUnselectable(false);
         this.listViewPlayList.onDoubleClick(e1 -> {
            if (SwingUtilities.isLeftMouseButton(e1)) {
               this.stop();
               this.setMedia(String.valueOf(this.listViewPlayList.getSelectedValue()), this::playPause);
               this.listViewPlayList.repaint();
            }
         });
         this.listViewPlayList.onKeyRelease(Hotkey.DELETE, e1 -> removeConsumer.run());
         this.listViewPlayList.setMaximumWidth(650);
         this.listViewPlayList.setMinimumWidth(200);
         this.listViewPlayList.setVisibleRowCount(12);
         JLabel title = new JLabel(I18N.WORD_PLAY_LIST.val(), 0);
         title.setFont(title.getFont().deriveFont(14.0F));
         WebButton addFile = new WebButton(new ImageIcon(Images.load("open16.png")), evt -> VLCUtils.openSelectMultiple(VLCUtils.EnumFileType.MEDIA, list -> {
               this.addPlaylist(list);
               this.listViewPlayList.setSelectedIndex(this.modelPlaylist.getSize() - 1);
            }));
         WebButton addLink = new WebButton(new ImageIcon(Images.load("web16.png")), evt -> VLCUtils.openInputLink(this, url -> {
               this.addPlaylist(url);
               this.listViewPlayList.setSelectedIndex(this.modelPlaylist.getSize() - 1);
            }));
         WebButton remove = new WebButton(new ImageIcon(Images.load("delete16.png")), evt -> removeConsumer.run());
         WebButton clear = new WebButton(I18N.WORD_CLEAN.val(), evt -> this.clear());
         Arrays.asList(addFile, addLink, remove).forEach(wb -> wb.setUndecorated(true).setFocusable(false));
         clear.setFocusable(false);
         WebPanel panelButtons = new WebPanel(
            new FlowLayout(1, 6, 2), new Component[]{addFile, addLink, remove, Box.createHorizontalStrut(31), Box.createHorizontalStrut(8), clear}
         );
         JPanel panelHeader = new JPanel(new BorderLayout(6, 0));
         panelHeader.add(title, "First");
         panelHeader.add(panelButtons, "Center");
         Arrays.asList(panelButtons, panelHeader).forEach(c -> c.setOpaque(false));
         WebPanel panel = new WebPanel(new BorderLayout(0, 2));
         panel.add(panelHeader, "First");
         panel.add(new WebScrollPane(this.listViewPlayList, false), "Center");
         this.wpoPlaylist.add(panel, "Center");
         this.wpoPlaylist.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
               VLCPlayerMini.this.listViewPlayList.ensureIndexIsVisible(VLCPlayerMini.this.listViewPlayList.getSelectedIndex());
            }
         });
      }

      this.listViewPlayList.setSelectedValue(this.currentMedia);
      this.wpoPlaylist.pack();
      Point p = MouseInfo.getPointerInfo().getLocation();
      p.translate(-100, -15);
      this.wpoPlaylist.setLocation(p);
      this.wpoPlaylist.setVisible(true);
   }

   private void removePlayList(List list) {
      list.forEach(l -> {
         String var10000 = (String)this.modelPlaylist.remove(String.valueOf(l));
      });
      if (!this.modelPlaylist.contains(this.currentMedia)) {
         this.currentMedia = this.modelPlaylist.isEmpty() ? "" : (String)this.modelPlaylist.get(0);
         this.stop();
         this.setMedia(this.currentMedia, null);
      }

      this.savePlaylist();
   }

   private void clear() {
      this.modelPlaylist.clear();
      this.setMedia(null, null);
   }

   private void savePlaylist() {
      Settings.EnumSettings2.VLC_PLAYLIST.set(this.modelPlaylist.getElements().stream().collect(Collectors.joining("\n")));
   }

   private void updateTime(long millis) {
      this.currentTimeMillis = millis;
      String text = StringUtils.formatTimeHMS(this.currentTimeMillis);
      SwingUtilities.invokeLater(() -> this.timeLabel.setText(text));
   }

   private void updatePosition(int value) {
      SwingUtilities.invokeLater(() -> this.positionSlider.setValue(value));
   }

   private String getMetaName() {
      if (VLCUtils.isYoutube(this.currentMedia)) {
         return VLCUtils.getYouTubeTitleOffline(this.currentMedia);
      } else if ((this.currentMedia.contains("\\holyrics-apmedia") || this.currentMedia.contains("//holyrics-apmedia")) && this.currentMedia.endsWith(".mp3")) {
         return I18N.MSG_AUTOMATIC_SHOW.val();
      } else {
         String[] hack = new String[]{"Player"};
         VLCUtils.runVLCThreadAndWait(() -> {
            try {
               hack[0] = this.mediaPlayer.getMediaMeta().getTitle();
            } catch (Exception var3) {
            }
         });
         return hack[0];
      }
   }

   public final synchronized void setPlaylist(String item, Runnable callback) {
      this.setPlaylist(new String[]{item}, callback);
   }

   public final synchronized void setPlaylist(String[] playlist, Runnable callback) {
      if (playlist != null && playlist.length != 0) {
         if (this.isPlaying()) {
            this.stop();
         }

         this.modelPlaylist.clear();
         this.addPlaylist(playlist);
         this.setMedia((String)this.modelPlaylist.get(0), callback);
      }
   }

   public synchronized void addPlaylist(String... playlist) {
      Arrays.asList(playlist).stream().distinct().forEach(media -> {
         if (!this.modelPlaylist.contains(media)) {
            this.modelPlaylist.add(media);
         }
      });
      this.modelPlaylist.remove("");
      if (this.modelPlaylist.isEmpty()) {
         this.modelPlaylist.add("");
      }

      this.skipPreviousButton.setEnabled(this.modelPlaylist.size() > 1);
      this.skipNextButton.setEnabled(this.modelPlaylist.size() > 1);
      if (String.valueOf(this.currentMedia).trim().isEmpty() && !this.modelPlaylist.isEmpty()) {
         this.setMedia((String)this.modelPlaylist.get(0), null);
      }

      this.savePlaylist();
   }

   public synchronized boolean contains(String media) {
      return this.modelPlaylist.contains(media);
   }

   public void addPlaylist(List<File> files) {
      this.addPlaylist(files.stream().map(File::getAbsolutePath).toArray(String[]::new));
   }

   public void setMedia(String media, Runnable callback) {
      Utils.startThreadDaemon(() -> {
         synchronized (this) {
            try {
               String mediaFinal = Optional.ofNullable(media).orElse("");
               if (mediaFinal != null) {
                  this.firstPlay = true;
                  this.currentMedia = mediaFinal;
                  if (!this.modelPlaylist.contains(this.currentMedia)) {
                     this.modelPlaylist.add(this.currentMedia);
                  }

                  String prepareMedia = this.currentMedia;
                  if (VLCUtils.isYoutube(this.currentMedia)) {
                     String ytstream = VLCUtils.getYouTubeStreamFirstUrl(this.currentMedia);
                     if (ytstream != null) {
                        prepareMedia = ytstream;
                     }
                  }

                  String prepareMediaFinal = prepareMedia;
                  VLCUtils.runVLCThread(() -> {
                     Utils.sleep(100L);
                     this.mediaPlayer.prepareMedia(prepareMediaFinal, new String[0]);
                     Utils.sleep(100L);
                  });
                  Utils.startThreadDaemon(this::updateThumbnail);
                  this.updatePosition(0);
                  this.updateTime(0L);
                  this.updateTitle(!mediaFinal.trim().isEmpty() ? this.getMetaName() : "Player", StyleUI.FOREGROUND_DARK);
               }
            } finally {
               Utils.sleep(100L);
               Optional.ofNullable(callback).ifPresent(Runnable::run);
            }
         }
      });
   }

   public boolean isCurrentMedia(String media) {
      return Objects.equals(this.currentMedia, media);
   }

   private void updateTitle(String text, Color color) {
      SwingUtilities.invokeLater(() -> {
         try {
            this.titleLabel.setForeground(color);
            this.titleLabel.setText(text);
            this.titleLabel.setToolTipText(text);
         } catch (Exception var4) {
         }
      });
   }

   public void setFullscreen(boolean fullscreen) {
      if (this.fullscreenToggleButton.isSelected() != fullscreen) {
         this.fullscreenToggleButton.setSelected(fullscreen);
      }
   }

   public boolean allContainsInPlayList(List<String> medias) {
      return medias.stream().allMatch(this.modelPlaylist::contains);
   }

   public void setupToolbarButton(JFrame frame) {
      JMenuBar menuBar = frame.getJMenuBar();
      if (menuBar != null) {
         menuBar.add(this.toolBarControlPadding);
         menuBar.add(this.toolBarControl);
         menuBar.revalidate();
      }
   }

   public void repeat() {
      this.repeatToggleButton.doClick(0);
   }

   public String getMedia() {
      return this.currentMedia;
   }

   public String getMediaName() {
      return this.titleLabel.getText();
   }

   public String getMediaList() {
      return Arrays.stream(this.modelPlaylist.toArray()).map(String::valueOf).collect(Collectors.joining("\n"));
   }

   public boolean isPlaying() {
      return this.playing;
   }

   public boolean isStopped() {
      return this.stopped;
   }

   public boolean isPlayingAndIsVideoOrStream() {
      return this.isPlaying() && (this.isVideo() || this.isStream());
   }

   public int getPosition() {
      int[] hack = new int[]{0};
      VLCUtils.runVLCThreadAndWait(() -> {
         try {
            hack[0] = (int)(this.mediaPlayer.getPosition() * 1000.0F);
         } catch (Exception var3) {
            hack[0] = this.positionSlider.getValue();
         }
      });
      return hack[0];
   }

   public long getTimeMillis() {
      long[] hack = new long[]{0L};
      VLCUtils.runVLCThreadAndWait(() -> {
         try {
            hack[0] = this.mediaPlayer.getTime();
         } catch (Exception var3) {
         }
      });
      return hack[0];
   }

   public String getTime() {
      return this.timeLabel.getText();
   }

   public int getVolume() {
      return this.volumeSlider.getValue();
   }

   public String getVolumeAndMinAndMax() {
      return this.volumeSlider.getValue() + ";" + this.volumeSlider.getMinimum() + ";" + this.volumeSlider.getMaximum();
   }

   public boolean isMute() {
      return this.muteToggleButton.isSelected();
   }

   public boolean isFullscreen() {
      return this.fullscreenToggleButton.isSelected();
   }

   public String getRepeat() {
      return this.set.getRepeatChar();
   }

   public Pack fillWSStatus(Pack p) {
      return p.put("media", this.getMediaName())
         .put("playlist", this.getMediaList())
         .put("playing", this.isPlaying())
         .put("position", this.getPosition())
         .put("time", this.getTime())
         .put("volumeAndMinAndMax", this.getVolumeAndMinAndMax())
         .put("mute", this.isMute())
         .put("repeat", this.getRepeat())
         .put("fullscreen", this.isFullscreen());
   }

   public void setPlayerStateListener(VLCPlayerStateListener playerStateListener) {
      this.playerStateListener = playerStateListener;
   }

   @Override
   public void stateChanged(VLCPlayerStateListener.EnumPlayerState newState) {
      VLCPlayerStateListener _listener = this.playerStateListener;
      if (_listener != null) {
         _listener.stateChanged(newState);
      }
   }

   private final class UpdateRunnable implements Runnable {
      private final MediaPlayer mediaPlayer;

      private UpdateRunnable(MediaPlayer mediaPlayer) {
         this.mediaPlayer = mediaPlayer;
      }

      @Override
      public void run() {
         long time = this.mediaPlayer.getTime();
         int position = this.mediaPlayer.isSeekable() ? (int)(this.mediaPlayer.getPosition() * 1000.0F) : VLCPlayerMini.this.positionSlider.getValue();
         if (this.mediaPlayer.isPlaying()) {
            VLCPlayerMini.this.updateTime(time);
            VLCPlayerMini.this.updatePosition(position);
         }
      }
   }

   public interface VLCPlayerMiniListener {
      void fullscreen(boolean var1);
   }
}
