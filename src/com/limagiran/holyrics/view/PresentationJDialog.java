package com.limagiran.holyrics.view;

import com.limagiran.bible.Verse;
import com.limagiran.holyrics.control.TransitionController;
import com.limagiran.holyrics.model.AlertSettings;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.AlertPanelAnimatedDefault;
import com.limagiran.holyrics.transition.CustomTransitionEffect;
import com.limagiran.holyrics.transition.MyImageTransition;
import com.limagiran.holyrics.util.BibleSlideUtils;
import com.limagiran.holyrics.util.Freeze;
import com.limagiran.holyrics.util.MediaPlayerUtils;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.StageViewUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.vlc.VLCVideoBackgroundBase;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
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
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class PresentationJDialog extends JDialog {
   private static final Object LOCK_PROCESS = new Object();
   public static MediaPlayer MEDIA_PLAYER;
   private static MediaView MEDIA_VIEW;
   public static volatile boolean FORCE_FX_THEME_VIDEO = MyProperties.EnumProperties.FORCE_FX_VIDEO_THEME.is();
   private static long currentInitFXId = 0L;
   private final Container content;
   private final Component focus;
   private Object renderer = "";
   private Theme theme;
   private Theme lastTheme;
   private final RendererValues rv = RendererValues.create(null, null, false);
   public BufferedImage currentBackground;
   public final JLabel background = new JLabel();
   private final CustomTransitionEffect effect;
   public final MyImageTransition imageTransition;
   private BufferedImage imgLyrics;
   private BufferedImage imgBackground;
   private BufferedImage currentForeground;
   public final ScreenDeviceSettings screenDeviceSettings;
   public final ScreenDeviceSettings.StageViewSettings stageViewSettings;
   public final ScreenDeviceSettings.GraphicsDeviceCustom device;
   private final JFXPanel fxPanel = new JFXPanel();
   private final AbsoluteConstraints slideBounds = new AbsoluteConstraints(0, 0, 0, 0);
   private final AbsoluteConstraints alertFooterBounds = new AbsoluteConstraints(0, 0, 0, 0);
   private final AbsoluteConstraints lastSlideBounds = new AbsoluteConstraints(0, 0, 0, 0);
   private boolean revalidate;
   private boolean fadein;
   private boolean fadeinBreak;
   private final AlertPanelAnimatedDefault alertFooterPaint = new AlertPanelAnimatedDefault() {
      @Override
      public void fireChange() {
         PresentationJDialog.this.revalidate = true;
         PresentationJDialog.this.rendererNow = true;
      }

      @Override
      public boolean isShow() {
         return Settings.SET.getAlertSettings().isShow() || CommunicationPanel.isDisplayAlert() && this.isStageView();
      }

      @Override
      public boolean stopAnimation() {
         return PresentationJDialog.this.closed;
      }

      @Override
      public int skipFrames() {
         if (PresentationJDialog.this.screenDeviceSettings == null) {
            return 64;
         } else {
            return PresentationJDialog.this.screenDeviceSettings.isPrimary()
               ? Settings.SET.getAlertSettings().getFrameSkip()
               : Math.max(Settings.SET.getAlertSettings().getFrameSkip(), 64);
         }
      }

      @Override
      public boolean isStageView() {
         return PresentationJDialog.this.screenDeviceSettings != null && PresentationJDialog.this.stageViewSettings != null
            ? !PresentationJDialog.this.screenDeviceSettings.isPrimary() && PresentationJDialog.this.stageViewSettings.isEnabled()
            : false;
      }
   };
   private final VLCVideoBackgroundBase vlcView;
   private volatile boolean allowFadeIn;
   private volatile boolean closed;
   private volatile boolean transition;
   private volatile boolean rendererNow = true;

   public PresentationJDialog(Component focus, Theme theme, ScreenDeviceSettings.GraphicsDeviceCustom device) {
      if (device.getScreenDeviceSettings().isPrimary() && !FORCE_FX_THEME_VIDEO) {
         this.vlcView = VLCVideoBackgroundBase.main(this);
      } else {
         this.vlcView = null;
      }

      this.content = this.getContentPane();
      this.focus = focus;
      this.device = device;
      this.device.setView(this);
      this.stageViewSettings = device.getScreenDeviceSettings().getStageViewSettings();
      this.screenDeviceSettings = Settings.SET.getScreenDeviceSettings(this.device.index);
      this.theme = StageViewUtils.getTheme(theme, device.deviceType, this.stageViewSettings);
      this.lastTheme = this.theme;
      this.effect = !this.screenDeviceSettings.isPrimary()
         ? null
         : new CustomTransitionEffect(device.deviceType, this.screenDeviceSettings, TransitionController.get(device.deviceType), this.theme);
      this.imageTransition = new MyImageTransition();
      this.init();
   }

   private void init() {
      this.setDefaultCloseOperation(0);
      this.setUndecorated(true);
      this.setBackground(this.device.deviceType.getBackgroundColor());
      this.content.setBackground(this.device.deviceType.getBackgroundColor());
      this.setAlwaysOnTop(true);
      this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
      if (this.focus != null) {
         Arrays.asList(this, this.fxPanel, this.background, this.imageTransition).forEach(c -> c.addFocusListener(this.redirectFocus(this.focus)));
      }

      Rectangle size = this.device.getBounds();
      this.setBounds(size);
      this.runVLCNotNull(vlc -> vlc.setBounds(size));
      this.setLayout(new AbsoluteLayout());
      this.content.add(this.alertFooterPaint, this.alertFooterBounds);
      this.content.add(this.imageTransition, this.slideBounds);
      this.content.add(this.background, this.slideBounds);
      this.imageTransition.setTransitionEffect(this.effect);
      Platform.setImplicitExit(false);
      Theme _theme = this.getThemeStageView();
      this.updateVideo(_theme);
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            Utils.startThreadDaemon(() -> {
               for (long count = -1L; PresentationJDialog.this.isVisible() && !PresentationJDialog.this.closed; Utils.sleep(125L)) {
                  if (++count == 16L || PresentationJDialog.this.rendererNow) {
                     PresentationJDialog.this.rendererNow = false;
                     count = 0L;
                     boolean transition = PresentationJDialog.this.process();
                     PresentationJDialog.this.renderer(transition);
                  }
               }
            });
            Utils.startThreadDaemon(() -> {
               while (!PresentationJDialog.this.allowFadeIn && !PresentationJDialog.this.closed) {
                  Utils.sleep(25L);
               }

               if (!PresentationJDialog.this.closed) {
                  PresentationJDialog.this.fade(true, () -> Freeze.setFreeze(false));
               }
            });
         }
      });
   }

   private boolean isPrimary() {
      return this.screenDeviceSettings != null ? this.screenDeviceSettings.isPrimary() : false;
   }

   private boolean isVideo(Theme t) {
      return this.isPrimary() && t.getBackground().isVideo();
   }

   private boolean isVideoAndIsEnabled(Theme t) {
      return this.isVideo(t) && (this.vlcView != null && !this.vlcView.isStopped() || MEDIA_PLAYER != null && MEDIA_PLAYER.getStatus() != Status.DISPOSED);
   }

   private void runVLCNotNull(Consumer<VLCVideoBackgroundBase> vlc) {
      Utils.safeException(() -> {
         VLCVideoBackgroundBase _vlcView = this.vlcView;
         if (_vlcView != null) {
            vlc.accept(_vlcView);
         }
      });
   }

   private boolean process() {
      synchronized (LOCK_PROCESS) {
         try {
            AlertSettings as = Settings.SET.getAlertSettings();
            this.slideBounds.x = this.slideBounds.y = 0;
            this.slideBounds.width = this.content.getWidth();
            this.slideBounds.height = this.content.getHeight();
            if (this.alertFooterPaint.isShow()) {
               this.alertFooterBounds.width = this.content.getWidth();
               this.alertFooterBounds.height = as.getAlertHeight(this.content.getHeight());
               this.alertFooterBounds.y = as.isTop() ? 0 : this.content.getHeight() - this.alertFooterBounds.height;
               if (!as.isOverlap()) {
                  this.slideBounds.height = this.slideBounds.height - this.alertFooterBounds.height;
                  this.slideBounds.y = as.isBottom() ? 0 : this.alertFooterBounds.height;
               }
            } else {
               this.alertFooterBounds.x = this.alertFooterBounds.y = this.alertFooterBounds.width = this.alertFooterBounds.height = 0;
            }

            if (!SwingUtils.equals(this.slideBounds, this.lastSlideBounds)) {
               this.revalidate = true;
            }

            this.lastSlideBounds.x = this.slideBounds.x;
            this.lastSlideBounds.y = this.slideBounds.y;
            this.lastSlideBounds.width = this.slideBounds.width;
            this.lastSlideBounds.height = this.slideBounds.height;
            Dimension slideSize = new Dimension(this.slideBounds.width, this.slideBounds.height);
            this.setupRendererValues();
            BufferedImage _imgLyrics;
            BufferedImage _imgBackground;
            if (!this.isPrimary()
               && this.stageViewSettings.isEnabled()
               && CommunicationPanel.isDisplayPresentation()
               && CommunicationPanel.isPresentationDisplayAhead()) {
               _imgLyrics = CommunicationPanel.getPresentationImage(slideSize);
               _imgBackground = null;
            } else {
               switch (this.device.deviceType) {
                  case BIBLE:
                     List<Verse> verses = getAsList(this.rv.renderer, Verse.class);
                     ScreenDeviceSettings.StageViewSettings svs = this.stageViewSettings;
                     boolean upperCase = this.isPrimary() && Settings.EnumSettings.BIBLE_UPPERCASE.is()
                        || svs.isEnabled() && svs.isUpperCase() && svs.isBibleEnabled();
                     _imgLyrics = BibleSlideUtils.createImage(verses, this.rv.theme, slideSize, false, false, upperCase, false);
                     _imgBackground = BibleSlideUtils.createImage(
                        verses, this.rv.theme, slideSize, !this.isVideoAndIsEnabled(this.rv.theme), true, false, true, true
                     );
                     if (this.isPrimary()) {
                        LiveView.setImage(slideSize, size -> BibleSlideUtils.createImage(verses, this.rv.theme, size, true, true, upperCase, true));
                     }
                     break;
                  case MUSIC:
                     List<Presentation> presentations = getAsList(this.rv.renderer, Presentation.class);
                     if (presentations.isEmpty()) {
                        if (this.isPrimary()) {
                           LiveView.clear();
                        }

                        return false;
                     }

                     Presentation p = StageViewUtils.apply(presentations.get(0), presentations.subList(1, presentations.size()), this);
                     p = Presentation.create(p.text, this.rv.theme);
                     p.text.setSkipPaintAddInfo(true);
                     _imgLyrics = PresentationSlideUtils.createImage(this.screenDeviceSettings, p, slideSize, false);
                     Presentation pBG = p.blank();
                     pBG.text.setTitleAndArtist(p.text.getTitleAndArtist()).setPageCount(p.text.getPageCount());
                     _imgBackground = PresentationSlideUtils.createImage(this.screenDeviceSettings, pBG, slideSize, !this.isVideoAndIsEnabled(this.rv.theme));
                     if (this.isPrimary()) {
                        LiveView.setImage(slideSize, size -> PresentationSlideUtils.createImage(this.screenDeviceSettings, p, size, true));
                     }
                     break;
                  case TEXT:
                     if (this.rv.renderer instanceof Presentation) {
                        Presentation p = (Presentation)this.rv.renderer;
                        p = Presentation.create(p.text, this.rv.theme, p.separatorOrEnd);
                        _imgLyrics = PresentationSlideUtils.createImage(this.screenDeviceSettings, p, slideSize, false);
                        _imgBackground = null;
                        if (!this.isVideoAndIsEnabled(this.rv.theme)) {
                           _imgBackground = PresentationSlideUtils.createImage(this.screenDeviceSettings, p.blank(), slideSize, true);
                        }

                        if (this.isPrimary()) {
                           LiveView.setImage(slideSize, size -> PresentationSlideUtils.createImage(this.screenDeviceSettings, p, size, true));
                        }
                     } else {
                        List<Presentation> presentationsx = getAsList(this.rv.renderer, Presentation.class);
                        if (presentationsx.isEmpty()) {
                           if (this.isPrimary()) {
                              LiveView.clear();
                           }

                           return false;
                        }

                        Presentation px = StageViewUtils.apply(presentationsx.get(0), presentationsx.subList(1, presentationsx.size()), this);
                        px = Presentation.create(px.text, this.rv.theme, px.separatorOrEnd);
                        _imgLyrics = PresentationSlideUtils.createImage(this.screenDeviceSettings, px, slideSize, false);
                        _imgBackground = null;
                        if (!this.isVideoAndIsEnabled(this.rv.theme)) {
                           _imgBackground = PresentationSlideUtils.createImage(this.screenDeviceSettings, px.blank(), slideSize, true);
                        }

                        if (this.isPrimary()) {
                           LiveView.setImage(slideSize, size -> PresentationSlideUtils.createImage(this.screenDeviceSettings, p, size, true));
                        }
                     }
                     break;
                  case IMAGE:
                     Presentation pxx;
                     if (this.rv.renderer instanceof Presentation) {
                        pxx = (Presentation)this.rv.renderer;
                     } else {
                        List<Presentation> presentationsxx = getAsList(this.rv.renderer, Presentation.class);
                        pxx = !presentationsxx.isEmpty() ? presentationsxx.get(0) : null;
                     }

                     if (pxx == null) {
                        if (this.isPrimary()) {
                           LiveView.clear();
                        }

                        return false;
                     }

                     _imgLyrics = PresentationSlideUtils.createImage(this.screenDeviceSettings, "", this.rv.theme, slideSize, true);
                     _imgBackground = null;
                     if (this.isPrimary()) {
                        LiveView.setImage(slideSize, size -> PresentationSlideUtils.createImage(this.screenDeviceSettings, "", this.rv.theme, size, true));
                     }
                     break;
                  default:
                     Presentation pxxx;
                     if (this.rv.renderer instanceof Presentation) {
                        pxxx = (Presentation)this.rv.renderer;
                     } else {
                        List<Presentation> presentationsxx = getAsList(this.rv.renderer, Presentation.class);
                        pxxx = !presentationsxx.isEmpty() ? presentationsxx.get(0) : null;
                     }

                     if (pxxx == null) {
                        if (this.isPrimary()) {
                           LiveView.clear();
                        }

                        return false;
                     }

                     pxxx = Presentation.create(pxxx.text, this.rv.theme, pxxx.separatorOrEnd);
                     _imgLyrics = PresentationSlideUtils.createImage(this.screenDeviceSettings, pxxx, slideSize, !this.isVideoAndIsEnabled(pxxx.theme));
                     _imgBackground = null;
                     if (this.isPrimary()) {
                        LiveView.setImage(slideSize, size -> PresentationSlideUtils.createImage(this.screenDeviceSettings, p, size, true));
                     }
               }
            }

            if (_imgLyrics != this.getImgLyrics()) {
               this.setImgLyrics(_imgLyrics);
            }

            if (_imgBackground != this.getImgBackground()) {
               this.setImgBackground(_imgBackground);
            }

            return this.rv.transition;
         } catch (Exception var11) {
            return this.rv.transition;
         }
      }
   }

   private void renderer(boolean transition) {
      boolean isBlackScreen = this.lastTheme.id == Theme.BLACK_SCREEN.id;
      if (this.lastTheme.id != this.getThemeStageView().id) {
         this.updateTheme();
      }

      isBlackScreen = isBlackScreen || this.lastTheme.id == Theme.BLACK_SCREEN.id;
      if (this.revalidate) {
         this.revalidate = false;
         Component[] cs = new Component[]{this.content, this.background, this.fxPanel, this.alertFooterPaint};

         for (Component c : cs) {
            c.revalidate();
         }
      }

      BufferedImage bi = this.getImgBackground();
      if (bi != this.currentBackground) {
         ImageIcon ic = bi == null ? null : new ImageIcon(bi);
         SwingUtilities.invokeLater(() -> this.background.setIcon(ic));
         this.currentBackground = bi;
         if (!this.allowFadeIn && bi != null) {
            this.allowFadeIn = true;
         }
      }

      bi = this.getImgLyrics();
      if (bi != this.currentForeground) {
         if (!this.allowFadeIn && bi != null) {
            this.allowFadeIn = true;
         }

         boolean flag = true;
         if (!transition
            || this.currentForeground == null
            || bi == null
            || this.currentForeground.getWidth() != bi.getWidth()
            || this.currentForeground.getHeight() != bi.getHeight()
            || isBlackScreen) {
            flag = false;
         }

         this.setForeground(bi, flag);
      }
   }

   public synchronized BufferedImage getImgLyrics() {
      return this.imgLyrics;
   }

   public synchronized void setImgLyrics(BufferedImage imgLyrics) {
      this.imgLyrics = imgLyrics;
   }

   public synchronized BufferedImage getImgBackground() {
      return this.imgBackground;
   }

   public synchronized void setImgBackground(BufferedImage imgBackground) {
      this.imgBackground = imgBackground;
   }

   private static void initFX(long id, JFXPanel fxPanel, MediaView mv, MediaPlayer mp, Theme t, PresentationJDialog f) {
      if (id >= currentInitFXId) {
         currentInitFXId = id;
         DoubleProperty w = mv.fitWidthProperty();
         DoubleProperty h = mv.fitHeightProperty();
         w.bind(Bindings.selectDouble(mv.sceneProperty(), new String[]{"width"}));
         h.bind(Bindings.selectDouble(mv.sceneProperty(), new String[]{"height"}));
         StackPane root = new StackPane();
         Color c = t.getTransparencyColor();
         root.setOpacity(0.0);
         Scene scene = new Scene(root);
         scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
         mp.setOnPlaying(() -> {
            root.setOpacity(1.0);
            root.setStyle("-fx-background-color: #" + Utils.getHexColor(c) + ";");
            scene.setFill(javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue()));
            Utils.startThreadDaemon(() -> {
               Utils.sleep(100L);
               f.allowFadeIn = true;
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
      if (!this.closed) {
         if (flag) {
            LiveView.fireStartPresentation();
            this.setOpacity(0.0F);
            super.setVisible(flag);
            this.runVLCNotNull(vlc -> vlc.toFront());
            this.toFront();
            if (VideoUtils.isSingleMonitor()) {
               this.requestFocus();
            }
         } else {
            this.exit();
         }
      }
   }

   @Override
   public void dispose() {
      this.exit();
   }

   public void exit() {
      if (!this.closed) {
         LiveView.clear();
         LiveView.fireFinishPresentation();
         boolean _closed = this.closed;
         this.closed = true;
         if (this.fadein) {
            this.fadeinBreak = true;
         }

         this.fade(false, () -> {
            if (!_closed) {
               MusicMain.staticRemoveFooter();
            }

            this.runVLCNotNull(vlc -> vlc.stop());
            Utils.safeException(() -> MediaPlayerUtils.stop(MEDIA_PLAYER));
            Utils.safeException(() -> super.setVisible(false));
            Utils.safeException(() -> super.dispose());
            this.runVLCNotNull(vlc -> vlc.setVisible(false));
            this.runVLCNotNull(vlc -> vlc.dispose());
            if (!VideoUtils.staticIsAnyVisible()) {
               WallpaperView.update();
            }

            VideoUtils.requestFocusInMainIfSingleMonitor();
            this.currentBackground = null;
            this.currentForeground = null;
            this.imgBackground = null;
            this.imgLyrics = null;
            if (this.background != null) {
               this.background.setIcon(null);
            }

            if (this.imageTransition != null) {
               this.imageTransition.clear();
            }
         });
      }
   }

   public void fade(boolean in, Runnable callback) {
      if (Settings.EnumSettings2.FADE_IN_OUT.is() && this.isPrimary()) {
         long fps = 50L;
         long duration = (long)Settings.EnumSettings2.FADE_IN_OUT_DURATION.getInt();
         float[] array = new float[Math.max((int)(duration / 50L), 2)];
         array[0] = 1.0F;
         int lastIndex = array.length - 1;
         array[array.length - 1] = 0.0F;
         float ratio = 1.0F / (float)lastIndex;

         for (int i = 1; i < lastIndex; i++) {
            array[i] = Math.max(array[i - 1] - ratio, 0.0F);
         }

         if (!in) {
            Utils.startThreadDaemon(() -> {
               for (float f : array) {
                  if (!this.fadeinBreak || !(f > this.getOpacity())) {
                     SwingUtilities.invokeLater(() -> this.setOpacity(f));
                     Utils.sleep(f > 0.5F ? 60L : 41L);
                  }
               }

               SwingUtils.runOnUIThread(() -> Optional.ofNullable(callback).ifPresent(Runnable::run));
            });
         } else {
            Utils.startThreadDaemon(() -> {
               this.fadein = true;

               try {
                  Arrays.sort(array);

                  for (float f : array) {
                     if (this.fadeinBreak) {
                        break;
                     }

                     SwingUtilities.invokeLater(() -> this.setOpacity(f));
                     Utils.sleep(50L);
                  }

                  SwingUtils.runOnUIThread(() -> Optional.ofNullable(callback).ifPresent(Runnable::run));
               } finally {
                  this.fadein = false;
               }
            });
         }
      } else {
         SwingUtils.runOnUIThread(() -> {
            this.setOpacity(in ? 1.0F : 0.0F);
            Optional.ofNullable(callback).ifPresent(Runnable::run);
         });
      }
   }

   @Override
   public void setOpacity(float opacity) {
      Utils.safeException(() -> super.setOpacity(opacity));
      this.runVLCNotNull(vlc -> vlc.setOpacity(opacity));
   }

   private void updateTheme() {
      this.lastTheme = this.getThemeStageView();
      if (this.effect != null) {
         this.effect.setTheme(this.lastTheme);
      }

      if (this.screenDeviceSettings.isPrimary()) {
         this.runVLCNotNull(vlc -> SwingUtilities.invokeLater(() -> {
               vlc.setShow(false);
               vlc.stop();
               this.revalidate = true;
            }));
         MediaPlayerUtils.stop(MEDIA_PLAYER);
         this.fxPanel.setScene(null);
         this.content.remove(this.fxPanel);
      }

      this.revalidate = true;
      this.updateVideo(this.lastTheme);
   }

   public void rendererNow() {
      this.rendererNow = true;
   }

   public synchronized void prepareRenderer(Object renderer, Theme theme, boolean transition) {
      this.transition = transition;
      this.renderer = renderer;
      this.theme = theme;
      this.rendererNow = true;
   }

   private synchronized void setupRendererValues() {
      this.rv.set(this.renderer, this.getThemeStageView(), this.transition);
      this.transition = false;
   }

   public synchronized void prepareRenderer(Theme theme) {
      this.transition = false;
      this.theme = theme;
      this.rendererNow = true;
   }

   private Theme getThemeStageView() {
      return StageViewUtils.getTheme(this.theme, this.device.deviceType, this.stageViewSettings);
   }

   private static <T> List<T> getAsList(Object obj, Class<T> clazz) {
      List<T> list = new ArrayList<>();
      if (obj instanceof List) {
         ((List)obj).forEach(o -> {
            if (clazz.isInstance(o)) {
               list.add((T)o);
            }
         });
      }

      return list;
   }

   private void updateVideo(Theme theme) {
      if (this.isVideo(theme)) {
         if (this.vlcView != null) {
            SwingUtilities.invokeLater(() -> {
               try {
                  this.vlcView.play(this.lastTheme);
                  this.vlcView.setShow(true);
               } catch (Exception var5) {
               } finally {
                  this.revalidate = true;
               }
            });
         } else {
            long id = System.currentTimeMillis();
            MediaPlayerUtils.stop(MEDIA_PLAYER);
            MEDIA_PLAYER = MediaPlayerUtils.setupMediaPlayer(theme, this, false);
            if (MEDIA_PLAYER != null) {
               MEDIA_VIEW = new MediaView(MEDIA_PLAYER);
               MEDIA_VIEW.setOpacity(this.lastTheme.getBackground().getOpacityPercent());
               MEDIA_PLAYER.setRate(Utils.range((double)theme.getVideoRate(), 0.25, 4.0));
               SwingUtilities.invokeLater(() -> {
                  this.content.add(this.fxPanel, this.slideBounds);
                  this.revalidate = true;
               });
               Platform.runLater(() -> initFX(id, this.fxPanel, MEDIA_VIEW, MEDIA_PLAYER, this.lastTheme, this));
            }
         }
      }
   }

   private void setForeground(BufferedImage newImg, boolean transition) {
      if (this.imageTransition != null && newImg != null) {
         this.currentForeground = newImg;
         if (this.effect == null) {
            this.imageTransition.putImage(this.currentForeground, false);
         } else {
            this.imageTransition.putImage(this.currentForeground, transition && this.effect.getTransitionSettings().isEnabled());
         }
      }
   }

   public boolean isClosed() {
      return this.closed;
   }
}
