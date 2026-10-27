package com.limagiran.holyrics.view;

import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.VideoUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class PresentationJDialogBackup extends JDialog {
   private final Component focus;
   private Theme theme;
   public final JLabel slide = new JLabel() {
      private boolean flag;

      @Override
      public void setIcon(Icon icon) {
         super.setIcon(icon);
         if (this.isVisible() && icon != null && !this.flag) {
            this.flag = true;
            PresentationJDialogBackup.this.fade(true);
         }
      }

      @Override
      public void setVisible(boolean visible) {
         super.setVisible(visible);
         if (visible && this.getIcon() != null && !this.flag) {
            this.flag = true;
            PresentationJDialogBackup.this.fade(true);
         }
      }
   };
   private final GraphicsDevice device;
   private MediaPlayer mediaPlayer;
   private MediaView mediaView;
   private final JFXPanel fxPanel = new JFXPanel();
   private int width;
   private int height;

   public PresentationJDialogBackup(Component focus, Theme theme, GraphicsDevice device) {
      this.focus = focus;
      this.theme = theme;
      this.device = device;
      this.init();
   }

   private void init() {
      this.setDefaultCloseOperation(0);
      this.setUndecorated(true);
      this.setAlwaysOnTop(true);
      this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
      this.setBackground(Values.COLOR_TRANSPARENT);
      this.getContentPane().setBackground(Values.COLOR_TRANSPARENT);
      this.mediaPlayer = this.theme.getMediaPlayer(this);
      if (this.focus != null) {
         BooleanOptional.runIfTrue(this.theme.getBackground().isVideo(), () -> this.fxPanel.addFocusListener(this.redirectFocus(this.focus)));
         this.addFocusListener(this.redirectFocus(this.focus));
         this.slide.addFocusListener(this.redirectFocus(this.focus));
      }

      this.setBounds(this.device.getDefaultConfiguration().getBounds());
      Rectangle r = this.device.getDefaultConfiguration().getBounds();
      this.setBounds(r);
      this.width = this.getWidth();
      this.height = this.getHeight();
      this.setLayout(new AbsoluteLayout());
      this.add(this.slide, new AbsoluteConstraints(0, 0, this.width, this.height));
      if (this.theme.getBackground().isVideo()) {
         Platform.setImplicitExit(false);
         this.mediaView = new MediaView(this.mediaPlayer);
         this.mediaView.setOpacity(this.theme.getBackground().getOpacityPercent());
         Platform.runLater(() -> initFX(this.fxPanel, this.mediaView, this.mediaPlayer, this.theme, this.slide));
         this.add(this.fxPanel, new AbsoluteConstraints(0, 0, this.width, this.height));
         Thread.currentThread().setPriority(10);
      }
   }

   private static void initFX(JFXPanel fxPanel, MediaView mv, MediaPlayer mp, Theme t, JLabel slide) {
      DoubleProperty w = mv.fitWidthProperty();
      DoubleProperty h = mv.fitHeightProperty();
      w.bind(Bindings.selectDouble(mv.sceneProperty(), new String[]{"width"}));
      h.bind(Bindings.selectDouble(mv.sceneProperty(), new String[]{"height"}));
      StackPane root = new StackPane();
      Color c = t.getTransparencyColor();
      root.setOpacity(0.0);
      Scene scene = new Scene(root);
      scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
      SwingUtils.runOnUIThread(() -> slide.setVisible(false));
      mp.setOnPlaying(() -> {
         root.setOpacity(1.0);
         root.setStyle("-fx-background-color: #" + Utils.getHexColor(c) + ";");
         scene.setFill(javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue()));
         Utils.startThreadDaemon(() -> {
            Utils.sleep(100L);
            SwingUtilities.invokeLater(() -> slide.setVisible(true));
         });
      });
      root.getChildren().add(mv);
      mv.setPreserveRatio(false);
      mv.setCacheHint(CacheHint.SPEED);
      mp.setCycleCount(-1);
      mp.setMute(true);
      mp.setAutoPlay(true);
      fxPanel.setScene(scene);
   }

   private FocusListener redirectFocus(final Component focus) {
      return new FocusAdapter() {
         @Override
         public void focusGained(FocusEvent e) {
            focus.requestFocus();
         }
      };
   }

   @Override
   public void setVisible(boolean flag) {
      if (flag) {
         this.setOpacity(0.0F);
         super.setVisible(flag);
         this.toFront();
      } else {
         this.exit();
      }
   }

   @Override
   public void dispose() {
      this.exit();
   }

   public void exit() {
      this.fade(false);
      this.stop();

      try {
         super.setVisible(false);
         super.dispose();
      } catch (Exception var2) {
      }

      MusicMain.staticRemoveFooter();
      if (!VideoUtils.staticIsAnyVisible()) {
         WallpaperView.update();
      }
   }

   private void fade(boolean in) {
      if (Settings.EnumSettings2.FADE_IN_OUT.is()) {
         float[] array = new float[]{1.0F, 0.9F, 0.7F, 0.5F, 0.3F, 0.1F};
         BooleanOptional.runIfTrue(in, () -> Arrays.sort(array));
         SwingUtils.runOnUIThread(() -> {
            for (float f : array) {
               this.setOpacity(f);
               Utils.sleep(16L);
            }
         });
      } else {
         SwingUtils.runOnUIThread(() -> this.setOpacity(in ? 1.0F : 0.0F));
      }
   }

   @Override
   public void setOpacity(float opacity) {
      try {
         super.setOpacity(opacity);
      } catch (Exception var3) {
      }
   }

   protected void setThemeAndRefresh(Theme theme) {
      this.theme = theme;
      this.stop();
      this.remove(this.fxPanel);
      if (this.theme.getBackground().isVideo()) {
         this.mediaPlayer = this.theme.getMediaPlayer(this);
         Platform.setImplicitExit(false);
         this.mediaView = new MediaView(this.mediaPlayer);
         this.mediaView.setOpacity(this.theme.getBackground().getOpacityPercent());
         Platform.runLater(() -> initFX(this.fxPanel, this.mediaView, this.mediaPlayer, this.theme, this.slide));
         this.add(this.fxPanel, new AbsoluteConstraints(0, 0, this.width, this.height));
      } else {
         this.slide.setVisible(true);
      }
   }

   private void stop() {
      try {
         if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.dispose();
         }
      } catch (Exception var2) {
      }
   }
}
