package com.limagiran.holyrics.view;

import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.view.control.PresentationControl;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class PresentationView extends PresentationJDialog implements PresentationControl {
   private static PresentationView INSTANCE;
   private final List<Presentation> presentations;
   private final List<PresentationJDialog> frames = new ArrayList<>();
   private int index;
   private boolean wallpaper;
   private boolean blank;
   private boolean black;

   private PresentationView(
      List<Presentation> presentations, Component focus, int initIndex, boolean secondDisplay, Theme theme, ScreenDeviceSettings.EnumDeviceType deviceType
   ) {
      super(focus, theme, VideoUtils.getDeviceToPresentation(deviceType));
      this.presentations = presentations;
      VideoUtils.getOtherDevicesToPresentation(deviceType).forEach(d -> this.frames.add(new PresentationJDialog(focus, theme, d)));
      this.setIndex(initIndex);
      if (!secondDisplay) {
         PresentationControl.addBasicShortcuts(this);
         this.changePresentation(this.index);
      }
   }

   public static PresentationView create(
      List<Presentation> presentations, Component focus, int initIndex, boolean secondDisplay, Theme theme, ScreenDeviceSettings.EnumDeviceType deviceType
   ) {
      BooleanOptional.runIfTrue(INSTANCE != null, () -> INSTANCE.exit());
      return INSTANCE = new PresentationView(presentations, focus, initIndex, secondDisplay, theme, deviceType);
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
   public void setVisible(boolean flag) {
      SwingUtils.runOnUIThread(() -> {
         VideoUtils.staticExitAllExcept(this.getClass());
         super.setVisible(flag);
         this.frames.forEach(f -> f.setVisible(flag));
      });
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

   @Override
   public List<PresentationJDialog> getFramesToPaint() {
      List<PresentationJDialog> _return = new ArrayList<>(this.frames);
      _return.add(0, this);
      return _return;
   }

   public static boolean staticIsVisible() {
      return INSTANCE != null && INSTANCE.isVisible();
   }

   public static void staticExit() {
      BooleanOptional.runIfTrue(staticIsVisible(), () -> INSTANCE.exit());
   }
}
