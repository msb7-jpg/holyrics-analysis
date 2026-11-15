package com.limagiran.holyrics.control;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.PopUpOpenProgram;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class ThemeController {
   private static final String FILE_NAME = "themes.hly";
   private static final List<Theme> THEMES = ControllerFactory.load(
      "themes.hly",
      Theme.class,
      () -> BooleanOptional.runIfTrue(
            !ControllerFactory.getFile("themes.hly").exists(),
            () -> Dir.exportFromJar("/com/limagiran/holyrics/file/themes.hly", ControllerFactory.getFile("themes.hly"))
         )
   );

   public static void add(Theme t) {
      if (!THEMES.contains(t)) {
         THEMES.add(t);
         save();
      }
   }

   public static void remove(Theme t) {
      THEMES.remove(t);
      save();
   }

   public static List<Theme> getThemes() {
      return new ArrayList<>(THEMES);
   }

   public static void save() {
      if (THEMES.isEmpty()) {
         THEMES.add(Theme.create());
      }

      ControllerFactory.save(THEMES, "themes.hly");
   }

   public static void set(Theme t) {
      if (THEMES.contains(t)) {
         THEMES.set(THEMES.indexOf(t), t);
      }

      ControllerFactory.save(THEMES, "themes.hly");
   }

   public static void set(List<Theme> list) {
      try {
         if (list.isEmpty()) {
            return;
         }

         List<Theme> backup = new ArrayList<>(THEMES);
         THEMES.clear();
         THEMES.addAll(list);
         if (!ControllerFactory.save(THEMES, "themes.hly")) {
            THEMES.clear();
            THEMES.addAll(backup);
            ControllerFactory.save(THEMES, "themes.hly");
         }

         backup.clear();
      } catch (Exception var2) {
      }
   }

   public static Theme getDefault() {
      return !THEMES.isEmpty() ? THEMES.get(0) : Theme.create();
   }

   public static Theme get(long id) {
      return THEMES.stream().filter(t -> t.id == id).findFirst().orElse(getDefault());
   }

   public static Theme getOrDefault(long id, Theme _def) {
      for (int i = 0; i < THEMES.size(); i++) {
         Theme t = THEMES.get(i);
         if (t.id == id) {
            return t;
         }
      }

      return _def;
   }

   public static boolean exists(long id) {
      for (int i = 0; i < THEMES.size(); i++) {
         if (THEMES.get(i).id == id) {
            return true;
         }
      }

      return false;
   }

   public static void load(PopUpOpenProgram popup) {
      Dimension d = new Dimension(80, 45);
      String text = Settings.EnumSettings.THEME_SAMPLE_TEXT.getString();
      List<Theme> themes = getThemes();

      for (int i = 0; i < themes.size(); i++) {
         PresentationSlideUtils.createImage(null, text, themes.get(i), d, true);
         popup.setProgress(String.format("%s (%s)", I18N.WORD_THEMES.val(), Utils.getPercentage((double)(i + 1), (double)themes.size())));
      }
   }

   static {
      BooleanOptional.runIfTrue(THEMES.isEmpty(), () -> add(Theme.create()));
   }
}
