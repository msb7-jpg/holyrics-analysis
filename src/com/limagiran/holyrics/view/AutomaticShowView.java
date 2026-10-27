package com.limagiran.holyrics.view;

import com.limagiran.bible.Verse;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BibleSlideUtils;
import com.limagiran.holyrics.util.GeneratePresentationThreadUtils;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.StageViewUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.view.control.AutomaticShowControl;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AutomaticShowView extends PresentationJDialog implements AutomaticShowControl {
   private static AutomaticShowView INSTANCE;
   public final long id;
   private final List<Object> presentations;
   private final List<PresentationJDialog> frames = new ArrayList<>();
   private final Theme theme;
   private int index;
   private final int seconds;
   private final boolean repeat;
   private boolean wallpaper;
   private boolean black;
   private boolean blank;
   private final ScreenDeviceSettings.EnumDeviceType deviceType;

   private AutomaticShowView(List<Object> presentations, Theme theme, int seconds, boolean repeat, ScreenDeviceSettings.EnumDeviceType deviceType) {
      super(null, theme, VideoUtils.getDeviceToPresentation(deviceType));
      this.theme = theme;
      this.seconds = seconds;
      this.repeat = repeat;
      this.id = Utils.generateMillisecondId();
      this.presentations = presentations;
      this.deviceType = deviceType;
      VideoUtils.getOtherDevicesToPresentation(this.deviceType).forEach(d -> this.frames.add(new PresentationJDialog(this, Theme.BLACK_SCREEN, d)));
      this.init();
   }

   private void init() {
      AutomaticShowControl.addBasicShortcuts(this);
      this.paintPresentation(this.presentations.get(0), false);
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            super.componentShown(e);
            AutomaticShowView.this.startLoop();
         }
      });
   }

   public static void show(List<Object> presentations, Theme theme, int seconds, boolean repeat, ScreenDeviceSettings.EnumDeviceType deviceType) {
      GeneratePresentationThreadUtils.put(() -> {
         if (INSTANCE != null && INSTANCE.isVisible()) {
            INSTANCE.exit();
         }

         if (!presentations.isEmpty()) {
            (INSTANCE = new AutomaticShowView(presentations, theme, seconds, repeat, deviceType)).setVisible(true);
         }
      });
   }

   public static void show(List<Object> presentations, int seconds, boolean repeat, ScreenDeviceSettings.EnumDeviceType deviceType) {
      show(presentations, Theme.BLACK_SCREEN, seconds, repeat, deviceType);
   }

   @Override
   public void setIndex(int index) {
      this.index = this.repeat
         ? (index < 0 ? Math.max(-this.step() + this.presentations.size(), 0) : (index < this.presentations.size() ? index : 0))
         : Math.max(index, 0);
   }

   @Override
   public int getIndex() {
      return this.index;
   }

   @Override
   public int getStep() {
      return Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt();
   }

   @Override
   public void setVisible(boolean flag) {
      SwingUtils.runOnUIThread(() -> {
         VideoUtils.staticExitAllExcept(this.getClass());
         if (flag) {
            if (!VideoUtils.isSingleMonitor()) {
               super.setFocusableWindowState(false);
               this.frames.forEach(f -> f.setFocusableWindowState(false));
            }

            super.setVisible(flag);
            this.frames.forEach(f -> f.setVisible(flag));
            super.setFocusableWindowState(true);
            this.frames.forEach(f -> f.setFocusableWindowState(true));
         } else {
            this.exit();
         }
      });
   }

   @Override
   public void dispose() {
      this.exit();
   }

   @Override
   public void exit() {
      HolyricsStreaming.clear();
      super.exit();
      this.frames.forEach(PresentationJDialog::exit);
   }

   public static boolean staticExit() {
      if (staticIsVisible()) {
         INSTANCE.exit();
         return true;
      } else {
         return false;
      }
   }

   public static boolean staticIsVisible() {
      return INSTANCE != null && INSTANCE.isVisible();
   }

   @Override
   public void renderer(Object o, PresentationJDialog f, boolean transition) {
      if (o instanceof Presentation) {
         Presentation p = (Presentation)o;
         f.prepareRenderer(p, p.theme, transition);
      } else {
         if (o instanceof Verse) {
            f.prepareRenderer(BibleSlideUtils.getVerses((Verse)o, this.getPresentations(), this.getStep()), this.theme, transition);
         }
      }
   }

   @Override
   public List<PresentationJDialog> getFramesToPaint() {
      List<PresentationJDialog> _return = new ArrayList<>(this.frames);
      _return.add(0, this);
      return _return;
   }

   @Override
   public boolean isWallpaper() {
      return this.wallpaper;
   }

   @Override
   public boolean isBlack() {
      return this.black;
   }

   @Override
   public boolean isBlank() {
      return this.blank;
   }

   @Override
   public void setWallpaper(boolean wallpaper) {
      this.wallpaper = wallpaper;
   }

   @Override
   public void setBlank(boolean blank) {
      this.blank = blank;
   }

   @Override
   public void setBlack(boolean black) {
      this.black = black;
   }

   @Override
   public BufferedImage rendererStream(Object o, Dimension d) {
      ScreenDeviceSettings.StageViewSettings svs = VideoUtils.getScreenDeviceSettingsStream().getStageViewSettings();
      Theme _theme = StageViewUtils.getTheme(this.theme, this.deviceType, svs);
      if (o instanceof Presentation) {
         Presentation p = (Presentation)o;
         p = Presentation.create(p.text, _theme, p.separatorOrEnd);
         return PresentationSlideUtils.createImage(VideoUtils.getScreenDeviceSettingsStream(), p, d, true);
      } else if (!(o instanceof Verse)) {
         return Img.createBlank(Color.BLACK, d);
      } else {
         boolean uc = svs.isEnabled() && svs.isBibleEnabled() && svs.isUpperCase() || Settings.EnumSettings.BIBLE_UPPERCASE.is();
         List<Verse> verses = BibleSlideUtils.getVerses((Verse)o, this.getPresentations(), this.getStep());
         return BibleSlideUtils.createImage(verses, _theme, d, true, true, uc, true);
      }
   }

   @Override
   public String rendererStreamHash(Object o, Dimension d) {
      ScreenDeviceSettings.StageViewSettings svs = VideoUtils.getScreenDeviceSettingsStream().getStageViewSettings();
      Theme _theme = StageViewUtils.getTheme(this.theme, this.deviceType, svs);
      if (o instanceof Presentation) {
         Presentation p = (Presentation)o;
         p = Presentation.create(p.text, _theme, p.separatorOrEnd);
         return PresentationSlideUtils.getKey(VideoUtils.getScreenDeviceSettingsStream(), p.text, p.theme, d, true);
      } else if (!(o instanceof Verse)) {
         return String.valueOf(System.nanoTime());
      } else {
         boolean uc = svs.isEnabled() && svs.isBibleEnabled() && svs.isUpperCase() || Settings.EnumSettings.BIBLE_UPPERCASE.is();
         List<Verse> verses = BibleSlideUtils.getVerses((Verse)o, this.getPresentations(), this.getStep());
         return BibleSlideUtils.getKey(verses, _theme, d, true, true, uc, true, false);
      }
   }

   @Override
   public List getPresentations() {
      return this.presentations;
   }

   @Override
   public boolean continueLoop() {
      return this.isVisible();
   }

   @Override
   public int getSeconds() {
      return this.seconds;
   }

   @Override
   public Presentation getPresentationBlank() {
      Theme t = this.theme;
      Object o = this.getPresentation();
      if (o instanceof Presentation) {
         t = ((Presentation)o).theme;
      }

      return Presentation.create(new MusicController.Paragraph(), t);
   }
}
