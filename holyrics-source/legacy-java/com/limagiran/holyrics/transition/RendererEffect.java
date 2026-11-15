package com.limagiran.holyrics.transition;

import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.EnumVerseDescription;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class RendererEffect {
   protected final Rectangle withinMargin = new Rectangle(0, 0, 1, 1);
   private Rectangle rectTheme = new Rectangle();

   public final void prepare(MyImageTransition image, CustomTransitionEffect owner) {
      boolean onlyArea = owner.transitionSettings.isOnlyAreaWithinMargin();
      this.rectTheme = owner.getTheme().getRectWithMargin(new Dimension(image.getWidth(), image.getHeight()));
      if (onlyArea) {
         this.withinMargin.setBounds(this.rectTheme);
      } else {
         this.withinMargin.setBounds(0, 0, image.getWidth(), image.getHeight());
      }

      switch (owner.deviceType) {
         case MUSIC:
            if (owner.screenDeviceSettings != null) {
               ScreenDeviceSettings sds = owner.screenDeviceSettings;
               if (sds.isShowPageCount() || sds.isShowTitleAndArtistAllSlides()) {
                  int height = sds.getAdditionalInfoLineHeightTo(image.getHeight());
                  if (sds.isShowPageCount() && sds.isShowTitleAndArtistAllSlides() && sds.isPageCountPositionTop() == sds.isTitleAndArtistPositionTop()) {
                     if (sds.isAdditionalInfoTwoLines()) {
                        height *= 2;
                     }

                     this.translateRect(sds.isPageCountPositionTop(), onlyArea, height, image);
                  } else {
                     if (sds.isShowPageCount()) {
                        this.translateRect(sds.isPageCountPositionTop(), onlyArea, height, image);
                     }

                     if (sds.isShowTitleAndArtistAllSlides()) {
                        if (sds.isAdditionalInfoTwoLines()) {
                           height *= 2;
                        }

                        this.translateRect(sds.isTitleAndArtistPositionTop(), onlyArea, height, image);
                     }
                  }
               }
            }
            break;
         case BIBLE:
            EnumVerseDescription en = EnumVerseDescription.valueOfOrDefault(Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION.getString());
            if (en.isTop() || Settings.EnumSettings.BIBLE_SHOW_VERSION.is()) {
               int h = (int)((double)image.getHeight() / 14.0);
               if (onlyArea) {
                  if (this.rectTheme.y < h) {
                     this.withinMargin.y = h;
                     this.withinMargin.height = this.withinMargin.height - (h - this.rectTheme.y);
                  }
               } else {
                  this.withinMargin.y = h;
                  this.withinMargin.height -= h;
               }
            }
      }

      this.prepare2(image, owner);
   }

   private void translateRect(boolean top, boolean onlyArea, int height, MyImageTransition image) {
      if (top) {
         this.withinMargin.y = this.withinMargin.y + (onlyArea ? height : this.rectTheme.y + height);
         this.withinMargin.height = this.withinMargin.height - (onlyArea ? height : this.rectTheme.y + height);
      } else {
         this.withinMargin.height = (int)(
            (double)this.withinMargin.height - (onlyArea ? (double)height : (double)image.getHeight() - this.rectTheme.getMaxY() + (double)height)
         );
      }
   }

   public abstract void prepare2(MyImageTransition var1, CustomTransitionEffect var2);

   public final void paint(Graphics2D g, MyImageTransition image, CustomTransitionEffect owner) {
      BufferedImage current = image.getCurrentImage();
      BufferedImage other = image.getOtherImage();

      try {
         current = current.getSubimage(this.withinMargin.x, this.withinMargin.y, this.withinMargin.width, this.withinMargin.height);
         other = other.getSubimage(this.withinMargin.x, this.withinMargin.y, this.withinMargin.width, this.withinMargin.height);
         g = (Graphics2D)g.create(this.withinMargin.x, this.withinMargin.y, this.withinMargin.width, this.withinMargin.height);
      } catch (Exception var8) {
         current = image.getCurrentImage();
         other = image.getOtherImage();
         g = g;
      }

      try {
         this.paint(g, current, other, owner);
      } catch (Exception var7) {
      }
   }

   public abstract void paint(Graphics2D var1, BufferedImage var2, BufferedImage var3, CustomTransitionEffect var4);
}
