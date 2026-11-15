package com.limagiran.holyrics.view;

import com.limagiran.bible.Bible;
import com.limagiran.bible.Verse;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.GeneratePresentationThreadUtils;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.view.control.BiblePresentationControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BiblePresentationView extends PresentationJDialog implements BiblePresentationControl {
   private static BiblePresentationView INSTANCE;
   private static final String[] VERSES_ON_DISPLAY = new String[]{null, null, null};
   private final List<PresentationJDialog> frames = new ArrayList<>();
   private List<Verse> verses;
   private Verse verse;
   private Theme theme;
   private boolean wallpaper;
   private boolean blank;
   private boolean black;

   private BiblePresentationView(List<Verse> verses, Verse verse, Theme theme) {
      super(null, theme, VideoUtils.getDeviceToPresentation(ScreenDeviceSettings.EnumDeviceType.BIBLE));
      this.verses = new ArrayList<>(verses);
      this.verse = verse;
      this.theme = theme;
      VideoUtils.getOtherDevicesToPresentation(ScreenDeviceSettings.EnumDeviceType.BIBLE)
         .forEach(d -> this.frames.add(new PresentationJDialog(this, theme, d)));
      this.init();
   }

   private void init() {
      BiblePresentationControl.addBasicShortcutss(this);
      this.paintPresentation(this.verse, false);
   }

   public static void show(List<Verse> verses, Verse verse, Theme theme) {
      GeneratePresentationThreadUtils.put(() -> {
         BooleanOptional.runIfTrue(INSTANCE != null, () -> INSTANCE.exit());
         INSTANCE = null;
         INSTANCE = new BiblePresentationView(verses, verse, theme);
         INSTANCE.setVisible(true);
      });
   }

   @Override
   public void setVisible(boolean flag) {
      SwingUtils.runOnUIThread(() -> {
         VideoUtils.staticExitAllExcept(this.getClass());
         if (!VideoUtils.isSingleMonitor()) {
            super.setFocusableWindowState(false);
            this.frames.forEach(f -> f.setFocusableWindowState(false));
         }

         super.setVisible(flag);
         this.frames.forEach(f -> f.setVisible(flag));
         super.setFocusableWindowState(true);
         this.frames.forEach(f -> f.setFocusableWindowState(true));
      });
   }

   @Override
   public void dispose() {
      this.exit();
   }

   @Override
   public Verse getVerse() {
      return this.verse;
   }

   @Override
   public void exit() {
      VERSES_ON_DISPLAY[0] = VERSES_ON_DISPLAY[1] = VERSES_ON_DISPLAY[2] = null;
      BibleMain.getInstance().repaint();
      HolyricsStreaming.clear();
      super.exit();
      this.frames.forEach(PresentationJDialog::exit);
   }

   public static void safeExit() {
      BooleanOptional.runIfTrue(INSTANCE != null, () -> INSTANCE.exit());
   }

   @Override
   public List<PresentationJDialog> getFramesToPaint() {
      List<PresentationJDialog> _return = new ArrayList<>(this.frames);
      _return.add(0, this);
      return _return;
   }

   @Override
   public List<Verse> getVerses() {
      return this.verses;
   }

   @Override
   public Theme getTheme() {
      return this.theme;
   }

   @Override
   public void setVerse(Verse verse) {
      this.wallpaper = false;
      this.blank = false;
      this.black = false;
      this.verse = verse;
      BibleMain.verseDisplayedChanged(this.verse);
   }

   public void setTheme(Theme theme) {
      this.theme = theme;
      super.prepareRenderer(theme);
      this.frames.forEach(f -> f.prepareRenderer(theme));
      this.paintPresentation(this.getVerse(), false);
   }

   @Override
   public boolean isWallpaper() {
      return this.wallpaper;
   }

   @Override
   public boolean isBlank() {
      return this.blank;
   }

   @Override
   public boolean isBlack() {
      return this.black;
   }

   @Override
   public void wallpaper() {
      this.black = false;
      this.blank = false;
      this.wallpaper = !this.wallpaper;
      this.paintPresentation(this.getVerse(), true);
   }

   @Override
   public void blank() {
      this.black = false;
      this.wallpaper = false;
      this.blank = !this.blank;
      this.paintPresentation(this.getVerse(), true);
   }

   @Override
   public void black() {
      this.wallpaper = false;
      this.blank = false;
      this.black = !this.black;
      this.paintPresentation(this.getVerse(), true);
   }

   public static boolean staticIsVisible() {
      return INSTANCE != null && INSTANCE.isVisible();
   }

   public static boolean staticNext() {
      if (!staticIsVisible()) {
         return false;
      } else {
         INSTANCE.next();
         return true;
      }
   }

   public static boolean staticPrevious() {
      if (!staticIsVisible()) {
         return false;
      } else {
         INSTANCE.previous();
         return true;
      }
   }

   public static boolean staticExit() {
      if (!staticIsVisible()) {
         return false;
      } else {
         INSTANCE.exit();
         return true;
      }
   }

   public static Verse staticGetVerse() {
      return staticIsVisible() ? INSTANCE.getVerse() : null;
   }

   public static boolean staticSetVerse(Verse v, boolean transition) {
      if (staticIsVisible() && !INSTANCE.isClosed()) {
         INSTANCE.setVerse(v);
         INSTANCE.paintPresentation(INSTANCE.getVerse(), transition);
         return true;
      } else {
         return false;
      }
   }

   public static boolean staticSetTheme(Theme t) {
      if (!staticIsVisible()) {
         return false;
      } else {
         INSTANCE.setTheme(t);
         return true;
      }
   }

   public static boolean staticUpdateVersion() {
      if (!staticIsVisible()) {
         return false;
      } else {
         INSTANCE.updateVersion();
         return true;
      }
   }

   public static void staticRefresh(boolean transition) {
      if (staticIsVisible()) {
         INSTANCE.paintPresentation(INSTANCE.getVerse(), transition);
      }
   }

   private synchronized void updateVersion() {
      Bible b = BibleMain.getInstance().getCurrentBible();
      List<Verse> list = new ArrayList<>();
      Verse currentVerse;
      if (this.verses.size() > 100) {
         list.addAll(b.verses);
         currentVerse = b.getVerse(this.verse.book, this.verse.chapter, this.verse.verse, true);
      } else {
         int index = Math.max(this.verses.indexOf(this.verse), 0);
         this.verses.stream().map(v -> b.getVerse(v.book, v.chapter, v.verse, true)).forEach(list::add);
         currentVerse = list.get(index);
         list = list.stream().distinct().collect(Collectors.toList());
      }

      this.verses = list;
      this.setVerse(currentVerse);
      this.paintPresentation(this.getVerse(), false);
   }

   public static boolean staticIsAllVerses() {
      return staticIsVisible() && INSTANCE.verses.size() > 100;
   }

   public static void staticWallpaper() {
      if (staticIsVisible()) {
         INSTANCE.wallpaper();
      }
   }

   public static void staticBlank() {
      if (staticIsVisible()) {
         INSTANCE.blank();
      }
   }

   public static void staticBlack() {
      if (staticIsVisible()) {
         INSTANCE.black();
      }
   }

   public static boolean staticIsBlack() {
      return staticIsVisible() ? INSTANCE.black : false;
   }

   public static boolean staticIsBlank() {
      return staticIsVisible() ? INSTANCE.blank : false;
   }

   public static boolean staticIsWallpaper() {
      return staticIsVisible() ? INSTANCE.wallpaper : false;
   }

   @Override
   public void versesOnDisplay(List<Verse> onDisplay) {
      try {
         VERSES_ON_DISPLAY[0] = VERSES_ON_DISPLAY[1] = VERSES_ON_DISPLAY[2] = null;
         if (staticIsVisible()) {
            VERSES_ON_DISPLAY[0] = onDisplay.get(0).getId();
            VERSES_ON_DISPLAY[1] = onDisplay.get(1).getId();
            VERSES_ON_DISPLAY[2] = onDisplay.get(2).getId();
         }
      } catch (Exception var6) {
      } finally {
         BibleMain.getInstance().repaintVerseList();
      }
   }

   public static boolean staticOnDisplay(Verse verse) {
      if (!staticIsVisible()) {
         return false;
      } else {
         try {
            for (String id : VERSES_ON_DISPLAY) {
               if (Objects.equals(id, verse.getId())) {
                  return true;
               }
            }
         } catch (Exception var5) {
         }

         return false;
      }
   }
}
