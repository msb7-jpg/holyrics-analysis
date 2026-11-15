package com.limagiran.holyrics.model;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.view.WallpaperView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Presentation implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final Presentation BLACK_SCREEN = create(new MusicController.Paragraph(), Theme.BLACK_SCREEN, false);
   public final MusicController.Paragraph text;
   public final Theme theme;
   public final boolean separatorOrEnd;
   private long version = 1L;

   private Presentation(MusicController.Paragraph text, Theme theme, boolean separatorOrEnd) {
      this.text = text;
      this.theme = theme;
      this.separatorOrEnd = separatorOrEnd;
   }

   public static Presentation create(MusicController.Paragraph text, Theme theme, boolean separatorOrEnd) {
      return new Presentation(text, theme, separatorOrEnd);
   }

   public static Presentation create(MusicController.Paragraph text, Theme theme) {
      return new Presentation(text, theme, false);
   }

   public Presentation blank() {
      return create(new MusicController.Paragraph(), this.theme, false);
   }

   public Presentation wallpaper() {
      return create(new MusicController.Paragraph(), WallpaperView.WALLPAPER_THEME, false);
   }

   public String hash() {
      return this.text.hash() + ":" + this.theme.hash() + ":" + this.separatorOrEnd;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
