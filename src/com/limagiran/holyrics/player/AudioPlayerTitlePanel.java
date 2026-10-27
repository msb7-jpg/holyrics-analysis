package com.limagiran.holyrics.player;

import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebButton;
import com.alee.laf.text.WebTextFieldStyle;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyData;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.MusicMain;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class AudioPlayerTitlePanel extends JPanel {
   private static final ImageIcon ICON_PAUSE = new ImageIcon(Images.load("pause16.png"));
   private static final ImageIcon ICON_PLAY = new ImageIcon(Images.load("play16.png"));
   private transient boolean blockSlider;
   public final transient List<File> playeds = new ArrayList<>();
   private MediaPlayer player = null;
   private JButton jButtonNext;
   private JButton jButtonPlayPause;
   private JButton jButtonPrevious;
   private JButton jButtonStop;
   private JLabel jLabelCurrentTime;
   private JLabel jLabelTotalTime;
   private JPanel jPanelBottom;
   private JPanel jPanelTop;
   private JSlider jSliderTime;
   private JTextField jTextFieldMusic;

   abstract void previous(boolean var1);

   abstract void next(boolean var1);

   abstract void shuffle(boolean var1);

   abstract AudioPlayerSettings getSet();

   public AudioPlayerTitlePanel() {
      this.initComponents();
      this.init();
   }

   private void init() {
      Arrays.asList(this.jButtonPlayPause, this.jButtonStop, this.jButtonPrevious, this.jButtonNext).forEach(b -> ((WebButton)b).setUndecorated(true));
      WebButtonGroup g = new WebButtonGroup(false, new JComponent[]{this.jButtonPlayPause, this.jButtonStop, this.jButtonPrevious});
      g.setButtonsDrawFocus(false);
      this.jPanelTop.add(g, "West");
      this.jButtonPlayPause
         .addActionListener(
            e -> this.action(this.jButtonPlayPause.getIcon().equals(ICON_PLAY) ? AudioPlayerTitlePanel.EnumAction.PLAY : AudioPlayerTitlePanel.EnumAction.PAUSE)
         );
      this.jButtonStop.addActionListener(e -> this.action(AudioPlayerTitlePanel.EnumAction.STOP));
      this.jButtonPrevious.addActionListener(e -> this.action(AudioPlayerTitlePanel.EnumAction.PREVIOUS));
      this.jButtonNext.addActionListener(e -> this.action(AudioPlayerTitlePanel.EnumAction.NEXT));
      Utils.startThreadDaemon(() -> {
         while (true) {
            Utils.sleep(1000L);
            if (this.isPlaying()) {
               SwingUtilities.invokeLater(() -> {
                  if (!this.jSliderTime.getValueIsAdjusting()) {
                     try {
                        int sec = (int)Math.min(this.player.getMedia().getDuration().toSeconds(), (double)((int)this.player.getCurrentTime().toSeconds()));
                        this.blockSlider = true;
                        this.jSliderTime.setValue(sec);
                        this.blockSlider = false;
                     } catch (Exception var2x) {
                        this.jLabelCurrentTime.setText(this.formatTime(0));
                     }
                  }
               });
            }
         }
      });

      try {
         Map<AbstractButton, HotkeyData> map = new HashMap<>();
         map.put(this.jButtonPlayPause, Hotkey.F3);
         map.put(this.jButtonStop, Hotkey.F4);
         map.forEach((button, key) -> {
            Optional.ofNullable(key).ifPresent(k -> HotkeyManager.registerHotkey(MusicMain.INSTANCE, button, k, TooltipWay.up));
            TooltipManager.setTooltip(button, button.getToolTipText(), TooltipWay.up, 0);
            button.setToolTipText(null);
         });
      } catch (Exception var3) {
      }
   }

   private void initComponents() {
      this.jButtonPlayPause = new WebButton();
      this.jButtonStop = new WebButton();
      this.jButtonPrevious = new WebButton();
      this.jPanelTop = new JPanel();
      this.jTextFieldMusic = new JTextField();
      this.jButtonNext = new WebButton();
      this.jPanelBottom = new JPanel();
      this.jLabelCurrentTime = new JLabel();
      this.jSliderTime = new JSlider();
      this.jLabelTotalTime = new JLabel();
      this.jButtonPlayPause.setIcon(ICON_PLAY);
      this.jButtonPlayPause.setToolTipText("play/pause");
      this.jButtonPlayPause.setMinimumSize(new Dimension(0, 0));
      this.jButtonPlayPause.setName("PLAY");
      this.jButtonStop.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/stop16.png")));
      this.jButtonStop.setToolTipText("stop");
      this.jButtonStop.setMinimumSize(new Dimension(0, 0));
      this.jButtonStop.setName("STOP");
      this.jButtonPrevious.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/skipPrevious16.png")));
      this.jButtonPrevious.setMinimumSize(new Dimension(0, 0));
      this.jButtonPrevious.setName("PREVIOUS");
      this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.setFocusable(false);
      this.setOpaque(false);
      this.setLayout(new BorderLayout());
      this.jPanelTop.setOpaque(false);
      this.jPanelTop.setLayout(new BorderLayout());
      this.jTextFieldMusic.setEditable(false);
      this.jTextFieldMusic.setHorizontalAlignment(0);
      this.jTextFieldMusic.setFocusable(false);
      this.jTextFieldMusic.setMinimumSize(new Dimension(0, 0));
      this.jPanelTop.add(this.jTextFieldMusic, "Center");
      this.jButtonNext.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/skipNext16.png")));
      this.jButtonNext.setMinimumSize(new Dimension(0, 0));
      this.jButtonNext.setName("NEXT");
      this.jPanelTop.add(this.jButtonNext, "East");
      this.add(this.jPanelTop, "First");
      this.jPanelBottom.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
      this.jPanelBottom.setOpaque(false);
      this.jPanelBottom.setLayout(new BorderLayout(4, 0));
      this.jLabelCurrentTime.setFont(new Font("Courier New", 0, 11));
      this.jPanelBottom.add(this.jLabelCurrentTime, "West");
      this.jSliderTime.setFont(new Font("Courier New", 0, 11));
      this.jSliderTime.setMaximum(0);
      this.jSliderTime.setFocusable(false);
      this.jSliderTime.setMinimumSize(new Dimension(0, 0));
      this.jSliderTime.setPreferredSize(new Dimension(50, 23));
      this.jSliderTime.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            AudioPlayerTitlePanel.this.jSliderTimeStateChanged(evt);
         }
      });
      this.jPanelBottom.add(this.jSliderTime, "Center");
      this.jLabelTotalTime.setFont(new Font("Courier New", 0, 11));
      this.jPanelBottom.add(this.jLabelTotalTime, "East");
      this.add(this.jPanelBottom, "Last");
   }

   private void jSliderTimeStateChanged(ChangeEvent evt) {
      SwingUtils.runOnUIThread(() -> this.jLabelCurrentTime.setText(this.formatTime(this.jSliderTime.getValue())));
      if (!this.blockSlider && this.player != null && !this.jSliderTime.getValueIsAdjusting()) {
         this.player.seek(Duration.seconds((double)this.jSliderTime.getValue()));
      }
   }

   public void action(AudioPlayerTitlePanel.EnumAction action) {
      try {
         switch (action) {
            case PLAY:
               this.player.setStartTime(Duration.seconds((double)this.jSliderTime.getValue()));
               this.player.play();
               SwingUtils.runOnUIThread(() -> {
                  this.jButtonPlayPause.setIcon(ICON_PAUSE);
                  this.jTextFieldMusic.setBackground(new Color(128, 255, 128));
               });
               break;
            case PAUSE:
               SwingUtils.runOnUIThread(() -> {
                  this.jButtonPlayPause.setIcon(ICON_PLAY);
                  this.jTextFieldMusic.setBackground(WebTextFieldStyle.backgroundColor);
               });
               this.player.pause();
               break;
            case STOP:
               SwingUtils.runOnUIThread(() -> {
                  this.jSliderTime.setValue(0);
                  this.jButtonPlayPause.setIcon(ICON_PLAY);
                  this.jTextFieldMusic.setBackground(WebTextFieldStyle.backgroundColor);
               });
               this.player.stop();
               this.player.stop();
               break;
            case PREVIOUS:
               this.previous(this.isPlaying());
               break;
            case NEXT:
               this.next(this.isPlaying());
         }
      } catch (Exception var3) {
      }
   }

   public void buttonAction(AudioPlayerTitlePanel.EnumButtonAction action) {
      switch (action) {
         case PLAY_PAUSE:
            this.jButtonPlayPause.doClick();
            break;
         case STOP:
            this.jButtonStop.doClick();
            break;
         case PREVIOUS:
            this.jButtonPrevious.doClick();
            break;
         case NEXT:
            this.jButtonNext.doClick();
      }
   }

   public void mediaChanged(boolean play, MediaPlayer mp) {
      SwingUtils.runOnUIThread(() -> {
         File f = this.getSet().getMedia();
         this.jSliderTime.setValue(0);
         this.jSliderTime.setMaximum(0);
         this.action(AudioPlayerTitlePanel.EnumAction.STOP);
         this.jTextFieldMusic.setBackground(mp == null && f != null ? new Color(255, 128, 128) : WebTextFieldStyle.backgroundColor);
         if (mp == null) {
            this.player = null;
            this.jTextFieldMusic.setText(f == null ? "" : f.getName());
            this.jLabelTotalTime.setText(this.formatTime(0));
         } else {
            try {
               this.jTextFieldMusic.setText(this.getSet().getMedia().getName());
               this.player = mp;
               this.player.getAudioEqualizer().setEnabled(this.getSet().isEqualization());
               this.player.getAudioEqualizer().getBands().forEach(b -> b.setGain((double)this.getSet().getEqualizer((int)b.getCenterFrequency())));
               this.player.setVolume(this.getSet().getVolume());
               this.player.setMute(this.getSet().isMute());
               MediaPlayer p = this.player;
               Utils.startThreadDaemon(() -> {
                  while (p == this.player) {
                     double sec = p.getMedia().getDuration().toMillis();
                     if (sec > 0.0) {
                        SwingUtils.runOnUIThread(() -> {
                           this.jSliderTime.setMaximum((int)(sec / 1000.0));
                           this.jLabelTotalTime.setText(this.formatTime((int)(sec / 1000.0)));
                        });
                        break;
                     }

                     Utils.sleep(250L);
                  }
               });
               this.player.setOnEndOfMedia(() -> {
                  SwingUtils.runOnUIThread(() -> {
                     this.jSliderTime.setValue(0);
                     this.jButtonPlayPause.setIcon(ICON_PLAY);
                     this.jTextFieldMusic.setBackground(WebTextFieldStyle.backgroundColor);
                  });
                  p.stop();
                  p.stop();
                  this.playeds.add(f);
                  if (this.getSet().isPlayOneFile() && this.getSet().isRepeat()) {
                     Utils.startThreadDaemon(() -> {
                        Utils.sleep(500L);
                        SwingUtils.runOnUIThread(() -> {
                           this.jSliderTime.setValue(0);
                           this.jButtonPlayPause.setIcon(ICON_PAUSE);
                           this.jTextFieldMusic.setBackground(new Color(128, 255, 128));
                        });
                        Platform.runLater(() -> {
                           p.seek(Duration.ZERO);
                           p.setStartTime(Duration.ZERO);
                           p.play();
                        });
                     });
                  } else if (!this.getSet().isPlayOneFile()) {
                     this.next(true);
                  }
               });
               BooleanOptional.runIfTrue(play, () -> this.action(AudioPlayerTitlePanel.EnumAction.PLAY));
            } catch (Exception var5) {
            }
         }
      });
   }

   public boolean isPlaying() {
      return this.jButtonPlayPause.getIcon().equals(ICON_PAUSE);
   }

   private String formatTime(int s) {
      return String.format("%s:%02d", s / 60, s % 60);
   }

   public void showNotificationError(File media) {
      AudioPlayerSetUtils.showNotificationError(this.jTextFieldMusic, media);
   }

   public void volumeChanged() {
      Optional.ofNullable(this.player).ifPresent(p -> {
         if (this.getSet().isMute()) {
            p.setMute(true);
         } else {
            p.setMute(false);
            p.setVolume(this.getSet().getVolume());
         }
      });
   }

   public void showEqualizer(Component c) {
      Equalizer.showEqualizer(this.player, this.getSet(), c);
   }

   public static enum EnumAction {
      PLAY,
      PAUSE,
      STOP,
      PREVIOUS,
      NEXT;
   }

   public static enum EnumButtonAction {
      PLAY_PAUSE,
      STOP,
      PREVIOUS,
      NEXT;
   }
}
