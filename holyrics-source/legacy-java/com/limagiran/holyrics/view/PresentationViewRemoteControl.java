package com.limagiran.holyrics.view;

import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.GeneratePresentationThreadUtils;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.ReflectionUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.view.control.PresentationControl;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PresentationViewRemoteControl extends PresentationJDialog implements PresentationControl {
   private static PresentationViewRemoteControl INSTANCE;
   private final long id;
   private final List<Presentation> presentations;
   private final Object presentationLock = new Object();
   private final List<PresentationJDialog> frames = new ArrayList<>();
   private final Music music;
   private Theme theme;
   private int index;
   private boolean wallpaper;
   private boolean blank;
   private boolean black;

   private PresentationViewRemoteControl(long id, List<Presentation> presentations, Music music, Theme theme, ScreenDeviceSettings.EnumDeviceType deviceType) {
      super(null, theme, VideoUtils.getDeviceToPresentation(deviceType));
      this.id = id;
      this.presentations = presentations;
      this.music = music;
      this.theme = theme;
      VideoUtils.getOtherDevicesToPresentation(deviceType).forEach(d -> this.frames.add(new PresentationJDialog(this, this.theme, d)));
      this.init();
   }

   private void init() {
      PresentationControl.addBasicShortcuts(this);
      this.changePresentation(this.index);
   }

   public static void show(long id, List<Presentation> presentations, Music music, Theme theme, ScreenDeviceSettings.EnumDeviceType deviceType) {
      GeneratePresentationThreadUtils.put(() -> {
         BooleanOptional.runIfTrue(INSTANCE != null && INSTANCE.isVisible(), () -> INSTANCE.exit());
         (INSTANCE = new PresentationViewRemoteControl(id, presentations, music, theme, deviceType)).setVisible(true);
      });
   }

   private void setTheme(Theme theme) {
      if (this.theme != theme) {
         this.theme = theme;
         synchronized (this.presentationLock) {
            this.presentations.forEach(p -> ReflectionUtils.setField(p, "theme", this.theme));
         }

         super.prepareRenderer(theme);
         this.frames.forEach(f -> f.prepareRenderer(theme));
         if (this.isBlack()) {
            this.black(false);
         } else if (this.isBlank()) {
            this.blank(false);
         } else {
            this.paintPresentation(this.getPresentation(), false);
         }
      }
   }

   public static boolean staticSetSinglePresentation(Presentation p, ScreenDeviceSettings.EnumDeviceType deviceType, boolean transition) {
      try {
         if (!staticIsVisible()) {
            return false;
         }

         if (INSTANCE.presentations.size() == 1 && INSTANCE.device.deviceType == deviceType && !INSTANCE.isClosed()) {
            synchronized (INSTANCE.presentationLock) {
               INSTANCE.presentations.clear();
               INSTANCE.presentations.add(p);
            }

            staticSetTheme(p.theme);
            INSTANCE.paintPresentation(INSTANCE.getPresentation(), transition);
            return true;
         }
      } catch (Exception var6) {
      }

      return false;
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
   public int getIndex() {
      return this.index;
   }

   @Override
   public final void setIndex(int index) {
      this.index = Math.max(index, 0);
   }

   @Override
   public void next() {
      Presentation p = this.getPresentation();
      if (!p.text.isCountdown() && !p.text.isAutomaticShow()) {
         PresentationControl.super.next();
      }
   }

   @Override
   public void previous() {
      Presentation p = this.getPresentation();
      if (!p.text.isCountdown() && !p.text.isAutomaticShow()) {
         PresentationControl.super.previous();
      }
   }

   @Override
   public void exit() {
      HolyricsStreaming.clear();
      super.exit();
      this.frames.forEach(PresentationJDialog::exit);
   }

   @Override
   public void setWallpaper(boolean wallpaper) {
      this.wallpaper = wallpaper;
   }

   @Override
   public boolean isWallpaper() {
      return this.wallpaper;
   }

   @Override
   public void setBlank(boolean blank) {
      this.blank = blank;
   }

   @Override
   public boolean isBlank() {
      return this.blank;
   }

   @Override
   public void setBlack(boolean black) {
      this.black = black;
   }

   @Override
   public boolean isBlack() {
      return this.black;
   }

   @Override
   public List<Presentation> getPresentations() {
      return this.presentations;
   }

   public static boolean staticIsVisible() {
      return INSTANCE != null && INSTANCE.isVisible();
   }

   private static void runIfIsVisible(BooleanOptional.BooleanConsumer consumer) {
      BooleanOptional.runIfTrue(staticIsVisible(), consumer);
   }

   public static void staticExit() {
      BooleanOptional.runIfTrue(staticIsVisible(), () -> INSTANCE.exit());
   }

   public static void staticExitCountdown() {
      BooleanOptional.runIfTrue(staticIsVisible() && INSTANCE.getPresentation().text.isCountdown(), () -> INSTANCE.exit());
   }

   public static void staticExitAutomaticShow() {
      BooleanOptional.runIfTrue(staticIsVisible() && INSTANCE.getPresentation().text.isAutomaticShow(), () -> INSTANCE.exit());
   }

   public static long staticId() {
      return staticIsVisible() ? INSTANCE.id : 0L;
   }

   public static List<String> staticGetPresentations() {
      synchronized (INSTANCE.presentationLock) {
         return (List<String>)(staticIsVisible() ? INSTANCE.presentations.stream().map(p -> p.text.toString()).collect(Collectors.toList()) : new ArrayList<>());
      }
   }

   public static void staticSetIndex(int index) {
      runIfIsVisible(() -> INSTANCE.changePresentation(index));
   }

   public static int staticGetIndex() {
      return staticIsVisible() ? INSTANCE.getIndex() : -1;
   }

   public static int staticGetSize() {
      return staticIsVisible() ? INSTANCE.getPresentations().size() : 0;
   }

   public static void staticBlank(boolean blank) {
      runIfIsVisible(() -> INSTANCE.blank(!blank));
   }

   public static void staticBlack(boolean black) {
      runIfIsVisible(() -> INSTANCE.black(!black));
   }

   public static void staticSetTheme(Theme theme) {
      runIfIsVisible(() -> INSTANCE.setTheme(theme));
   }

   public static Music staticGetMusic() {
      return staticIsVisible() ? INSTANCE.music : Music.BLANK;
   }

   @Override
   public List<PresentationJDialog> getFramesToPaint() {
      List<PresentationJDialog> _return = new ArrayList<>(this.frames);
      _return.add(0, this);
      return _return;
   }

   public static void staticSleep(long id, long maxDelay) {
      for (long delay = 0L; staticId() != id && delay < maxDelay; delay += 200L) {
         Utils.sleep(200L);
      }
   }

   public static void staticRequestFocus(long id) {
      if (staticIsVisible() && INSTANCE.id == id) {
         INSTANCE.requestFocus();
      }
   }
}
