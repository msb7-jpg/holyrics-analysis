package com.limagiran.holyrics.view;

import com.limagiran.holyrics.model.AlertSettings;
import com.limagiran.holyrics.model.ClockSettings;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.AlertPanelAnimatedDefault;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.SystemUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.view.panel.DrawableJComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.media.Media;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class WallpaperView extends JDialog {
   private static final List<WallpaperView> INSTANCES = Collections.synchronizedList(new ArrayList<>());
   private static final ExecutorService WALLPAPER_UPDATE_EXECUTOR = Executors.newSingleThreadExecutor();
   public static final Theme WALLPAPER_THEME = Theme.create()
      .setTransparencyColor(SystemUtils.getWallpaperColor())
      .setBackground(createWallpaperThemeBackground());
   private final AbsoluteConstraints wallpaperBounds = new AbsoluteConstraints(0, 0, 0, 0);
   private final AbsoluteConstraints alertFooterBounds = new AbsoluteConstraints(0, 0, 0, 0);
   private BufferedImage biWallpaper;
   private BufferedImage biClock;
   private BufferedImage biCommunicationPanel;
   private final JComponent wallpaper = new DrawableJComponent(() -> this.biWallpaper);
   private final JComponent clock = new DrawableJComponent(() -> this.biClock);
   private final JComponent communicationPanel = new DrawableJComponent(() -> this.biCommunicationPanel);
   private final GraphicsDevice device;
   private boolean exit;
   private final int index;
   private final AlertPanelAnimatedDefault alertPanelAnimated = new AlertPanelAnimatedDefault() {
      @Override
      public boolean stopAnimation() {
         return WallpaperView.this.exit;
      }

      @Override
      public void fireChange() {
         WallpaperView.update();
      }

      @Override
      public boolean isShow() {
         return Settings.SET.getAlertSettings().isShow() || CommunicationPanel.isDisplayAlert() && this.isStageView();
      }

      @Override
      public boolean paint() {
         return super.paint() && !VideoUtils.staticIsAnyVisible();
      }

      @Override
      public int skipFrames() {
         return WallpaperView.this.isPrimary() ? Settings.SET.getAlertSettings().getFrameSkip() : Math.max(Settings.SET.getAlertSettings().getFrameSkip(), 64);
      }

      @Override
      public boolean isStageView() {
         return WallpaperView.this.isStageView();
      }
   };
   private transient volatile ScreenDeviceSettings sds;

   public static void start() {
      Utils.startThreadDaemon(
         () -> {
            int secondMonitorIndex = Settings.EnumSettings2.SECOND_MONITOR_INDEX.getInt();

            while (true) {
               if (!VideoUtils.staticIsAnyVisible()
                  && (getDevicesToShow().size() != INSTANCES.size() || secondMonitorIndex != Settings.EnumSettings2.SECOND_MONITOR_INDEX.getInt())) {
                  secondMonitorIndex = Settings.EnumSettings2.SECOND_MONITOR_INDEX.getInt();
                  restart();
               }

               Utils.sleep(5000L);
            }
         }
      );
   }

   private WallpaperView(GraphicsDevice device, int index) {
      this.device = device;
      this.index = index;
      this.init();
   }

   private void init() {
      this.setLayout(new AbsoluteLayout());
      this.setUndecorated(true);
      this.setBackground(Values.COLOR_TRANSPARENT);
      this.setDefaultCloseOperation(0);
      Rectangle size = ScreenDeviceSettings.GraphicsDeviceCustom.getBounds(this.device, this.getScreenDeviceSettings());
      this.add(this.alertPanelAnimated, this.alertFooterBounds);
      this.add(this.communicationPanel, this.wallpaperBounds);
      this.add(this.clock, this.wallpaperBounds);
      this.add(this.wallpaper, this.wallpaperBounds);
      this.setFocusable(false);
      this.setFocusableWindowState(false);
      this.wallpaper.setFocusable(false);
      this.communicationPanel.setFocusable(false);
      this.clock.setFocusable(false);
      this.wallpaper.setBackground(Color.BLACK);
      this.communicationPanel.setBackground(Color.BLACK);
      this.setBounds(size);
      this.setResizable(false);
      this.toBack();
      this.updateBounds();
   }

   public boolean isPrimary() {
      return this.index == 1;
   }

   public boolean isStageView() {
      return !this.isPrimary() && this.getScreenDeviceSettings().getStageViewSettings().isEnabled();
   }

   public ScreenDeviceSettings getScreenDeviceSettings() {
      return this.sds != null ? this.sds : (this.sds = Settings.SET.getScreenDeviceSettings(this.index));
   }

   public static void update() {
      try {
         WALLPAPER_UPDATE_EXECUTOR.submit(() -> INSTANCES.forEach(i -> {
               i.updateBounds();
               if (i.wallpaperBounds.width > 0 && i.wallpaperBounds.height > 0) {
                  Dimension size = new Dimension(i.wallpaperBounds.width, i.wallpaperBounds.height);
                  i.updateClock(size);
                  i.updateWallpaper(size);
                  i.updateCommunicationPanel(size);
                  i.repaintAll();
               }
            }));
      } catch (Exception var1) {
      }
   }

   private void updateClock(Dimension wpSize) {
      ClockSettings cs = Settings.SET.getClockSettings();
      BufferedImage _biClock = this.biClock;
      if (_biClock != null) {
         _biClock.flush();
      }

      if (cs.isShow()) {
         int translateY = 0;
         if (this.alertFooterBounds.height > 0 && this.alertPanelAnimated.isOverlap()) {
            if (cs.isBottom() && this.alertFooterBounds.y > 0) {
               translateY -= this.alertFooterBounds.height;
            } else if (cs.isTop() && this.alertFooterBounds.y == 0) {
               translateY += this.alertFooterBounds.height;
            }
         }

         BufferedImage bi = Img.createBlank(Values.COLOR_TRANSPARENT, wpSize);
         Graphics2D g = bi.createGraphics();
         cs.paint(g, wpSize, cs.getLineHeight(wpSize.height), translateY);
         g.dispose();
         this.biClock = bi;
      } else {
         this.biClock = null;
      }
   }

   private void updateWallpaper(Dimension wpSize) {
      if (!Settings.EnumSettings.DISPLAY_WALLPAPER.is()) {
         this.biWallpaper = null;
      } else {
         String img64 = Settings.EnumSettings.WALLPAPER_IMAGE64.getString();
         BufferedImage original = Images.loadFrom64(img64);
         this.biWallpaper = Img.resize(original, wpSize);
         original.flush();
      }
   }

   private void updateCommunicationPanel(Dimension wpSize) {
      this.biCommunicationPanel = CommunicationPanel.isDisplayPresentation() && this.isStageView() ? CommunicationPanel.getPresentationImage(wpSize) : null;
   }

   private void repaintAll() {
      Component[] array = new Component[]{this.getContentPane(), this.wallpaper, this.clock, this.communicationPanel, this.alertPanelAnimated};

      for (Component c : array) {
         BooleanOptional.runIfTrue(c != null, () -> c.revalidate());
      }

      for (Component c : array) {
         BooleanOptional.runIfTrue(c != null, () -> c.repaint());
      }
   }

   public static void restart() {
      SwingUtilities.invokeLater(() -> {
         while (!INSTANCES.isEmpty()) {
            INSTANCES.remove(0).dispose();
         }

         List<GraphicsDevice> devices = getDevicesToShow();

         for (int i = 0; i < devices.size(); i++) {
            WallpaperView wallpaper = new WallpaperView(devices.get(i), i + 1);
            INSTANCES.add(wallpaper);
         }

         INSTANCES.forEach(ix -> ix.setVisible(true));
         update();
      });
   }

   private static List<GraphicsDevice> getDevicesToShow() {
      List<GraphicsDevice> devices = VideoUtils.list();
      if (devices.size() <= 1) {
         return new ArrayList<>();
      } else {
         return Settings.EnumSettings.DISPLAY_ON_ALL_MONITORS.is() ? devices.subList(1, devices.size()) : Arrays.asList(devices.get(1));
      }
   }

   @Override
   public void dispose() {
      this.exit = true;
      super.setVisible(false);
      super.dispose();
      this.getContentPane().removeAll();
   }

   private void updateBounds() {
      this.wallpaperBounds.x = this.wallpaperBounds.y = 0;
      this.wallpaperBounds.width = this.getWidth();
      this.wallpaperBounds.height = this.getHeight();
      AlertSettings as = Settings.SET.getAlertSettings();
      if (this.alertPanelAnimated.isShow()) {
         this.alertFooterBounds.width = this.getWidth();
         this.alertFooterBounds.height = as.getAlertHeight(this.getHeight());
         this.alertFooterBounds.y = as.isTop() ? 0 : this.getHeight() - this.alertFooterBounds.height;
         if (!as.isOverlap()) {
            this.wallpaperBounds.height = this.wallpaperBounds.height - this.alertFooterBounds.height;
            this.wallpaperBounds.y = as.isBottom() ? 0 : this.alertFooterBounds.height;
         }
      } else {
         this.alertFooterBounds.x = this.alertFooterBounds.y = this.alertFooterBounds.width = this.alertFooterBounds.height = 0;
      }
   }

   private static Background createWallpaperThemeBackground() {
      return new Background() {
         @Override
         public String getId() {
            return "wallpaper-static-background-id" + System.currentTimeMillis() / 10000L;
         }

         @Override
         public BufferedImage getImage() {
            String img64 = Settings.EnumSettings.WALLPAPER_IMAGE64.getString();
            BufferedImage wallpaper = Images.loadFrom64(img64);
            return wallpaper != null ? wallpaper : Images.imageNotFound();
         }

         @Override
         public Media getVideo() throws Exception {
            return null;
         }

         @Override
         public boolean isVideo() {
            return false;
         }

         @Override
         public Background.EnumBackgroundType[] getTypes() {
            return new Background.EnumBackgroundType[]{Background.EnumBackgroundType.ADJUST};
         }

         @Override
         public Background.EnumBackgroundType getDefaultType() {
            return Background.EnumBackgroundType.ADJUST;
         }
      };
   }
}
