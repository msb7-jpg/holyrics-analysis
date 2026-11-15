package com.limagiran.holyrics.view.control;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.PresentationRendererUtils;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.StageViewUtils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.view.PresentationJDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.RootPaneContainer;

public interface PresentationControl {
   int getIndex();

   void setIndex(int var1);

   default void next() {
      this.changePresentation(this.getIndex() + 1);
   }

   default void previous() {
      this.changePresentation(this.getIndex() - 1);
   }

   void exit();

   void setWallpaper(boolean var1);

   boolean isWallpaper();

   default void wallpaper(boolean isWallpaper) {
      if (isWallpaper) {
         this.changePresentation(this.getPresentation(), true);
      } else {
         this.paintPresentation(this.getPresentation().wallpaper(), true);
      }

      this.setWallpaper(!isWallpaper);
      this.setBlank(false);
      this.setBlack(false);
   }

   void setBlank(boolean var1);

   boolean isBlank();

   default void blank(boolean isBlank) {
      if (isBlank) {
         this.changePresentation(this.getPresentation(), true);
      } else {
         this.paintPresentation(this.getPresentation().blank(), true);
      }

      this.setBlank(!isBlank);
      this.setWallpaper(false);
      this.setBlack(false);
   }

   void setBlack(boolean var1);

   boolean isBlack();

   default void black(boolean isBlack) {
      if (isBlack) {
         this.changePresentation(this.getPresentation(), true);
      } else {
         this.paintPresentation(Presentation.BLACK_SCREEN, true);
      }

      this.setBlack(!isBlack);
      this.setWallpaper(false);
      this.setBlank(false);
   }

   default void changePresentation(Presentation p, boolean transition) {
      this.setWallpaper(false);
      this.setBlack(false);
      this.setBlank(false);
      this.paintPresentation(p, transition);
   }

   default void changePresentation(int index) {
      this.setIndex(index);
      if (this.getIndex() < this.getPresentations().size() && this.getIndex() >= 0) {
         this.changePresentation(this.getPresentation(), true);
      } else {
         this.exit();
      }
   }

   List<PresentationJDialog> getFramesToPaint();

   default void paintPresentation(Presentation p, boolean transition) {
      List<Presentation> list = new ArrayList<>(this.getPresentations());
      List<Presentation> subList = list.subList(Math.max(0, this.getIndex()), list.size());
      subList.add(0, p);
      PresentationRendererUtils.put(() -> {
         final List<PresentationJDialog> frames = new ArrayList<>(this.getFramesToPaint());
         frames.forEach(frame -> frame.prepareRenderer(subList, p.theme, transition));
         if (Settings.EnumSettings.STREAMING_ON.is() && frames.stream().anyMatch(Component::isVisible)) {
            HolyricsStreaming.HolyricsStreamRenderer holyricsStreamRenderer = new HolyricsStreaming.HolyricsStreamRenderer() {
               private Presentation getPresentation() {
                  ScreenDeviceSettings.EnumDeviceType devType = frames.isEmpty() ? ScreenDeviceSettings.EnumDeviceType.TEXT : frames.get(0).device.deviceType;
                  ScreenDeviceSettings.StageViewSettings svs = VideoUtils.getScreenDeviceSettingsStream().getStageViewSettings();
                  Presentation _p = subList.isEmpty() ? p : StageViewUtils.apply(p, subList.subList(1, subList.size()), devType, svs);
                  Theme theme = StageViewUtils.getTheme(_p.theme, devType, svs);
                  return Presentation.create(_p.text, theme, _p.separatorOrEnd);
               }

               @Override
               public BufferedImage renderer(Dimension size) {
                  return PresentationSlideUtils.createImage(VideoUtils.getScreenDeviceSettingsStream(), this.getPresentation(), size, true);
               }

               @Override
               public String getHash(Dimension size) {
                  Presentation p = this.getPresentation();
                  return PresentationSlideUtils.getKey(VideoUtils.getScreenDeviceSettingsStream(), p.text, p.theme, size, true);
               }
            };
            HolyricsStreaming.setRenderer(holyricsStreamRenderer);
         }
      });
   }

   default Presentation getPresentation() {
      try {
         return Optional.ofNullable(this.getPresentations().get(this.getIndex())).orElse(Presentation.BLACK_SCREEN);
      } catch (Exception var2) {
         return Presentation.BLACK_SCREEN;
      }
   }

   List<Presentation> getPresentations();

   default void realTimeUpdate() {
   }

   static <T extends PresentationControl & RootPaneContainer> void addBasicShortcuts(T t) {
      List<InsertShortcut.Shortcut> list = new ArrayList<>();
      list.add(InsertShortcut.Shortcut.create(39, t::next));
      list.add(InsertShortcut.Shortcut.create(10, t::next));
      list.add(InsertShortcut.Shortcut.create(34, t::next));
      list.add(InsertShortcut.Shortcut.create(37, t::previous));
      list.add(InsertShortcut.Shortcut.create(33, t::previous));
      list.add(InsertShortcut.Shortcut.create(27, t::exit));
      list.add(InsertShortcut.Shortcut.create(119, () -> t.wallpaper(t.isWallpaper())));
      list.add(InsertShortcut.Shortcut.create(120, () -> t.blank(t.isBlank())));
      list.add(InsertShortcut.Shortcut.create(121, () -> t.black(t.isBlack())));
      list.add(InsertShortcut.Shortcut.create(113, 64, () -> PresentationUtils.editRealTime(t.getPresentation(), t::realTimeUpdate)));
      InsertShortcut.add(t, list);
   }

   static String descriptionOfShortcuts() {
      return I18N.MSG_DESCRIPTION_DEFAULT_SHORTCUTS.val();
   }
}
