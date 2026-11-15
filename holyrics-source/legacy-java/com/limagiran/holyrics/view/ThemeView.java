package com.limagiran.holyrics.view;

import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.event.ThemeSettingsPanelListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MediaPlayerUtils;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.ScreenUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.view.panel.ThemeSettingsPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public final class ThemeView extends JDialog implements ThemeSettingsPanelListener {
   private static final int BORDER = 2;
   public static MediaPlayer MEDIA_PLAYER;
   private static MediaView MEDIA_VIEW;
   private final ExecutorService UPDATE_EXECUTOR = Executors.newSingleThreadExecutor();
   private final ThemeSettingsPanel themeSettingsPanel;
   private final Theme theme;
   private final JLabel borderLabel = new JLabel();
   private final JLabel slidePreview = new JLabel();
   private final JFXPanel fxPanel = new JFXPanel();
   private final AbsoluteConstraints borderBounds = new AbsoluteConstraints(0, 0);
   private final AbsoluteConstraints slidePreviewBounds = new AbsoluteConstraints(0, 0);
   private final Runnable callback;
   private boolean newTheme;
   private boolean closed;
   private boolean paintMargin;
   private JPanel jPanelSettings;
   private JPanel jPanelSlidePreview;
   private JPanel jPanelTheme;

   private ThemeView(Theme theme, boolean newTheme, Runnable callback) {
      this.theme = theme.cloneTheme();
      this.callback = callback;
      this.themeSettingsPanel = new ThemeSettingsPanel(this.theme, newTheme, this, ThemeSettingsPanel.EnumModality.DEFAULT);
      this.newTheme = newTheme;
      this.initComponents();
      this.init();
      this.backgroundChanged();
   }

   private void init() {
      Platform.setImplicitExit(false);
      this.jPanelSettings.add(this.themeSettingsPanel, "First");
      SwingUtils.maximize(this, 50);
      this.borderLabel.setBorder(new LineBorder(new Color(30, 30, 30), 2));
      this.jPanelSlidePreview.add(this.borderLabel, this.borderBounds);
      this.jPanelSlidePreview.add(this.slidePreview, this.slidePreviewBounds);
      this.fxPanel.setOpaque(true);
      this.fxPanel.setBackground(Color.BLACK);
      this.jPanelSlidePreview.add(this.fxPanel, this.slidePreviewBounds);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::close));
      this.setupWebLaf();
   }

   private void setupWebLaf() {
      WebLafUtils.setupWebLafBorderLayout(this.jPanelSettings);
      SwingUtils.setJPanelToOpaqueFalse(this);
   }

   private static MediaView initFX(JFXPanel fxPanel, MediaPlayer mp) {
      if (mp == null) {
         fxPanel.setScene(null);
         return null;
      } else {
         MediaView mv = new MediaView(mp);
         DoubleProperty w = mv.fitWidthProperty();
         DoubleProperty h = mv.fitHeightProperty();
         w.bind(Bindings.selectDouble(mv.sceneProperty(), new String[]{"width"}));
         h.bind(Bindings.selectDouble(mv.sceneProperty(), new String[]{"height"}));
         StackPane root = new StackPane();
         Scene scene = new Scene(root);
         root.getChildren().add(mv);
         mv.setPreserveRatio(false);
         mv.setCacheHint(CacheHint.SPEED);
         mv.getMediaPlayer().setCycleCount(-1);
         mv.getMediaPlayer().setMute(true);
         mv.getMediaPlayer().setAutoPlay(true);
         fxPanel.setScene(scene);
         return mv;
      }
   }

   private void initComponents() {
      this.jPanelTheme = new JPanel();
      this.jPanelSettings = new WebPanel(true);
      this.jPanelSlidePreview = new JPanel();
      this.setDefaultCloseOperation(0);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.theme")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(1050, 500));
      this.setModal(true);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            ThemeView.this.formWindowClosing(evt);
         }
      });
      this.jPanelTheme.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelTheme.setLayout(new BorderLayout());
      this.jPanelSettings.setBorder(BorderFactory.createEtchedBorder());
      this.jPanelSettings.setLayout(new BorderLayout());
      this.jPanelTheme.add(this.jPanelSettings, "First");
      this.jPanelSlidePreview.setMinimumSize(new Dimension(0, 0));
      this.jPanelSlidePreview.setLayout(new AbsoluteLayout());
      this.jPanelTheme.add(this.jPanelSlidePreview, "Center");
      this.getContentPane().add(this.jPanelTheme, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.close();
   }

   public static void main(Theme theme, boolean newTheme, Runnable callback) {
      SwingUtilities.invokeLater(() -> new ThemeView(theme, newTheme, callback).setVisible(true));
   }

   private void close() {
      if (this.themeSettingsPanel.buttonSaveEnabled()) {
         switch (JOptionPane.showConfirmDialog(this, I18N.MSG_CONFIRM_SAVE_CHANGES.val(), I18N.WORD_CONFIRM.val(), 1)) {
            case 0:
               this.saveAndExit();
               this.safeExit();
               break;
            case 1:
               this.safeExit();
         }
      } else {
         this.safeExit();
      }
   }

   private void safeExit() {
      MediaPlayerUtils.stop(MEDIA_PLAYER);

      try {
         this.setVisible(false);
         super.dispose();
      } catch (Exception var2) {
      }
   }

   private void saveAndExit() {
      this.themeSettingsPanel.setButtonSaveEnabled(false);
      if (this.newTheme) {
         this.newTheme = !this.newTheme;
         ThemeController.add(this.theme);
      } else {
         ThemeController.set(this.theme);
      }

      Optional.ofNullable(this.callback).ifPresent(r -> r.run());
      this.safeExit();
   }

   @Override
   public void backgroundChanged() {
      MediaPlayerUtils.stop(MEDIA_PLAYER);
      if (this.theme.getBackground().isVideo()) {
         try {
            MEDIA_PLAYER = MediaPlayerUtils.setupMediaPlayer(this.theme, this, true);
            if (MEDIA_PLAYER == null) {
               return;
            }

            Platform.runLater(() -> MEDIA_VIEW = initFX(this.fxPanel, MEDIA_PLAYER));
         } catch (Exception var2) {
            Utils.error(this, var2.getMessage());
         }
      }

      this.themeChanged();
   }

   @Override
   public void themeChanged() {
      Rectangle a = ScreenUtils.adjustRect(this.jPanelSlidePreview.getSize(), this.themeSettingsPanel.getEnumScreenType().getSize());
      this.UPDATE_EXECUTOR
         .submit(
            () -> {
               Dimension slideSize = new Dimension(a.width - 4, a.height - 4);
               Presentation paragraph = Presentation.create(
                  MusicController.Paragraph.create(Settings.EnumSettings.THEME_SAMPLE_TEXT.getString()).setPaintMargin(this.paintMargin), this.theme
               );
               ImageIcon icon = new ImageIcon(PresentationSlideUtils.createImage(null, paragraph, slideSize, !this.isVideoAndIsEnabled()));
               SwingUtilities.invokeLater(() -> {
                  this.slidePreview.setIcon(icon);
                  SwingUtils.cloneBounds(this.borderBounds, a);
                  this.cloneBounds(this.slidePreviewBounds, this.borderBounds, 2);
                  if (this.isVideoAndIsEnabled()) {
                     this.fxPanel.setVisible(true);
                     Utils.safeException(() -> {
                        MediaView mv = MEDIA_VIEW;
                        if (mv != null) {
                           double opacity = this.theme.getBackground().getOpacityPercent();
                           if (mv.getOpacity() != opacity) {
                              mv.opacityProperty().setValue(opacity);
                           }

                           double rate = Utils.range((double)this.theme.getVideoRate(), 0.25, 4.0);
                           if (mv.getMediaPlayer().getRate() != rate) {
                              mv.getMediaPlayer().setRate(rate);
                           }
                        }
                     });
                     Color c = this.theme.getTransparencyColor();
                     Optional.ofNullable(this.fxPanel.getScene()).ifPresent(s -> {
                        Optional.ofNullable(s.getRoot()).ifPresent(r -> r.setStyle("-fx-background-color: #" + Utils.getHexColor(c) + ";"));
                        s.setFill(javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue()));
                     });
                  } else {
                     this.fxPanel.setVisible(false);
                  }

                  this.jPanelSlidePreview.revalidate();
               });
               Utils.sleep(100L);
            }
         );
   }

   private boolean isVideoAndIsEnabled() {
      return this.theme.getBackground().isVideo() && MEDIA_PLAYER != null && MEDIA_PLAYER.getStatus() != Status.DISPOSED;
   }

   @Override
   public void setVisible(boolean flag) {
      this.closed = !flag;
      if (!this.closed) {
         this.refreshBackgroundLoop();
      }

      super.setVisible(flag);
   }

   @Override
   public void dispose() {
      super.dispose();
      this.closed = true;
   }

   private void refreshBackgroundLoop() {
      Utils.startThreadDaemon(() -> {
         while (!this.closed) {
            this.themeChanged();
            Utils.sleep(1000L);
         }
      });
   }

   @Override
   public void saveButtonClicked() {
      this.saveAndExit();
   }

   @Override
   public void setPaintMargin(boolean paint) {
      this.paintMargin = paint;
      this.themeChanged();
   }

   private void cloneBounds(AbsoluteConstraints ac1, AbsoluteConstraints ac2, int border) {
      ac1.x = Math.max(ac2.x + border, 0);
      ac1.y = Math.max(ac2.y + border, 0);
      ac1.width = Math.max(ac2.width - border * 2, 0);
      ac1.height = Math.max(ac2.height - border * 2, 0);
   }
}
