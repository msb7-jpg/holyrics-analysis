package com.limagiran.holyrics.view.control;

import com.limagiran.bible.Verse;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.PresentationRendererUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.PresentationJDialog;
import com.limagiran.holyrics.view.WallpaperView;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.RootPaneContainer;

public interface AutomaticShowControl {
   int getIndex();

   void setIndex(int var1);

   default void next() {
      this.changePresentation(this.getIndex() + this.step());
   }

   default void previous() {
      this.changePresentation(this.getIndex() - this.step());
   }

   int getStep();

   default int step() {
      return this.getPresentation() instanceof Verse ? this.getStep() : 1;
   }

   void exit();

   void setWallpaper(boolean var1);

   boolean isWallpaper();

   default void wallpaper(boolean isWallpaper) {
      if (isWallpaper) {
         this.changePresentation(this.getPresentation(), true);
      } else {
         this.paintPresentation(this.getPresentationWallpaper(), true);
      }

      this.setWallpaper(!isWallpaper);
      this.setBlank(false);
      this.setBlack(false);
   }

   default Presentation getPresentationWallpaper() {
      return Presentation.create(new MusicController.Paragraph(), WallpaperView.WALLPAPER_THEME);
   }

   void setBlank(boolean var1);

   boolean isBlank();

   default void blank(boolean isBlank) {
      if (isBlank) {
         this.changePresentation(this.getPresentation(), true);
      } else {
         this.paintPresentation(this.getPresentationBlank(), true);
      }

      this.setBlank(!isBlank);
      this.setWallpaper(false);
      this.setBlack(false);
   }

   Presentation getPresentationBlank();

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

   default void changePresentation(Object o, boolean transition) {
      this.setWallpaper(false);
      this.setBlack(false);
      this.setBlank(false);
      this.paintPresentation(o, transition);
   }

   default void changePresentation(int index) {
      this.setIndex(index);
      if (this.getIndex() < this.getPresentations().size() && this.getIndex() >= 0) {
         this.changePresentation(this.getPresentation(), true);
      } else {
         this.exit();
      }
   }

   void renderer(Object var1, PresentationJDialog var2, boolean var3);

   BufferedImage rendererStream(Object var1, Dimension var2);

   String rendererStreamHash(Object var1, Dimension var2);

   List<PresentationJDialog> getFramesToPaint();

   default void paintPresentation(Object o, boolean transition) {
      PresentationRendererUtils.put(() -> {
         List<PresentationJDialog> frames = this.getFramesToPaint();
         frames.forEach(frame -> this.renderer(o, frame, transition));
         if (Settings.EnumSettings.STREAMING_ON.is() && !frames.stream().noneMatch(f -> f.isVisible())) {
            HolyricsStreaming.HolyricsStreamRenderer holyricsStreamRenderer = new HolyricsStreaming.HolyricsStreamRenderer() {
               @Override
               public BufferedImage renderer(Dimension size) {
                  return AutomaticShowControl.this.rendererStream(o, size);
               }

               @Override
               public String getHash(Dimension size) {
                  return AutomaticShowControl.this.rendererStreamHash(o, size);
               }
            };
            HolyricsStreaming.setRenderer(holyricsStreamRenderer);
         }
      });
   }

   boolean continueLoop();

   int getSeconds();

   default void startLoop() {
      Utils.startThreadDaemon(() -> {
         while (this.continueLoop()) {
            Utils.sleep((long)(this.getSeconds() * 1000));
            if (this.continueLoop()) {
               this.changePresentation(this.getIndex() + this.step());
               continue;
            }
            break;
         }
      });
   }

   default Object getPresentation() {
      try {
         return Optional.ofNullable(this.getPresentations().get(this.getIndex())).orElse(Presentation.BLACK_SCREEN);
      } catch (Exception var2) {
         return Presentation.BLACK_SCREEN;
      }
   }

   List getPresentations();

   static <T extends AutomaticShowControl & RootPaneContainer> void addBasicShortcuts(T t) {
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
      list.add(InsertShortcut.Shortcut.create(113, 64, () -> {
         Object p = t.getPresentation();
         if (p instanceof Presentation) {
            PresentationUtils.editRealTime((Presentation)p, null);
         }
      }));
      InsertShortcut.add(t, list);
   }

   static String descriptionOfShortcuts() {
      return I18N.MSG_DESCRIPTION_DEFAULT_SHORTCUTS.val();
   }
}
