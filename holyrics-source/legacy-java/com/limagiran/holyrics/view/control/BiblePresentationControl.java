package com.limagiran.holyrics.view.control;

import com.limagiran.bible.Verse;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BibleSlideUtils;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.PresentationRendererUtils;
import com.limagiran.holyrics.util.StageViewUtils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.view.PresentationJDialog;
import com.limagiran.holyrics.view.WallpaperView;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.RootPaneContainer;

public interface BiblePresentationControl {
   List<Verse> getVerses();

   Verse getVerse();

   Theme getTheme();

   List<PresentationJDialog> getFramesToPaint();

   void setVerse(Verse var1);

   default void next() {
      this.setVerse(nextVerse(this.getVerses(), this.getVerse()));
      this.paintPresentation(this.getVerse(), true);
   }

   default void previous() {
      this.setVerse(previousVerse(this.getVerses(), this.getVerse()));
      this.paintPresentation(this.getVerse(), true);
   }

   void exit();

   void wallpaper();

   void blank();

   void black();

   boolean isWallpaper();

   boolean isBlank();

   boolean isBlack();

   default void paintPresentation(Verse v, boolean transition) {
      PresentationRendererUtils.put(
         () -> {
            List<PresentationJDialog> frames = this.getFramesToPaint();
            final List<Verse> verses = this.getVerses(v);
            this.versesOnDisplay(verses);
            final Theme th = this.getTheme();
            final boolean wallpaper = this.isWallpaper();
            final boolean blank = this.isBlank();
            final boolean black = this.isBlack();
            Object renderer = !wallpaper && !blank && !black ? verses : Collections.EMPTY_LIST;
            Theme theme = black ? Theme.BLACK_SCREEN : (wallpaper ? WallpaperView.WALLPAPER_THEME : th);
            frames.forEach(frame -> frame.prepareRenderer(renderer, theme, transition));
            if (Settings.EnumSettings.STREAMING_ON.is() && frames.stream().anyMatch(Component::isVisible)) {
               HolyricsStreaming.HolyricsStreamRenderer holyricsStreamRenderer = new HolyricsStreaming.HolyricsStreamRenderer() {
                  @Override
                  public BufferedImage renderer(Dimension size) {
                     ScreenDeviceSettings.StageViewSettings svs = VideoUtils.getScreenDeviceSettingsStream().getStageViewSettings();
                     Theme theme = (black || wallpaper) && (!svs.isEnabled() || !svs.isBibleEnabled())
                        ? (black ? Theme.BLACK_SCREEN : WallpaperView.WALLPAPER_THEME)
                        : StageViewUtils.getTheme(th, ScreenDeviceSettings.EnumDeviceType.BIBLE, svs);
                     boolean uc = svs.isEnabled() && svs.isBibleEnabled() && svs.isUpperCase() || Settings.EnumSettings.BIBLE_UPPERCASE.is();
                     List versesStream = !blank && !black && !wallpaper || svs.isEnabled() && svs.isBibleEnabled() ? verses : Collections.EMPTY_LIST;
                     return BibleSlideUtils.createImage(versesStream, theme, size, true, true, uc, true);
                  }

                  @Override
                  public String getHash(Dimension size) {
                     ScreenDeviceSettings.StageViewSettings svs = VideoUtils.getScreenDeviceSettingsStream().getStageViewSettings();
                     Theme theme = (black || wallpaper) && (!svs.isEnabled() || !svs.isBibleEnabled())
                        ? (black ? Theme.BLACK_SCREEN : WallpaperView.WALLPAPER_THEME)
                        : StageViewUtils.getTheme(th, ScreenDeviceSettings.EnumDeviceType.BIBLE, svs);
                     boolean uc = svs.isEnabled() && svs.isBibleEnabled() && svs.isUpperCase() || Settings.EnumSettings.BIBLE_UPPERCASE.is();
                     List versesStream = !blank && !black && !wallpaper || svs.isEnabled() && svs.isBibleEnabled() ? verses : Collections.EMPTY_LIST;
                     return BibleSlideUtils.getKey(versesStream, theme, size, true, true, uc, true, false);
                  }
               };
               HolyricsStreaming.setRenderer(holyricsStreamRenderer);
            }
         }
      );
   }

   static <T extends BiblePresentationControl & RootPaneContainer> void addBasicShortcutss(T t) {
      List<InsertShortcut.Shortcut> list = new ArrayList<>();
      list.add(InsertShortcut.Shortcut.create(39, t::next));
      list.add(InsertShortcut.Shortcut.create(10, t::next));
      list.add(InsertShortcut.Shortcut.create(34, t::next));
      list.add(InsertShortcut.Shortcut.create(37, t::previous));
      list.add(InsertShortcut.Shortcut.create(33, t::previous));
      list.add(InsertShortcut.Shortcut.create(27, t::exit));
      list.add(InsertShortcut.Shortcut.create(119, t::wallpaper));
      list.add(InsertShortcut.Shortcut.create(120, t::blank));
      list.add(InsertShortcut.Shortcut.create(121, t::black));
      InsertShortcut.add(t, list);
   }

   static Verse nextVerse(List<Verse> list, Verse v) {
      int index = list.indexOf(v);
      int var3;
      return index != -1 && (var3 = index + Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt()) < list.size()
         ? list.get(var3)
         : list.get(list.size() - 1);
   }

   static Verse previousVerse(List<Verse> list, Verse v) {
      int index = list.indexOf(v);
      int var3;
      return index != -1 && (var3 = index - Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt()) >= 0 ? list.get(var3) : list.get(0);
   }

   default List<Verse> getVerses(Verse v) {
      return BibleSlideUtils.getVerses(v, this.getVerses(), Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt());
   }

   void versesOnDisplay(List<Verse> var1);
}
