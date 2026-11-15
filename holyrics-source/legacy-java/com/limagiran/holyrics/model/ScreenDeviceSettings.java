package com.limagiran.holyrics.model;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.StageViewUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.view.PresentationJDialog;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class ScreenDeviceSettings implements Serializable {
   private static final long serialVersionUID = 12491L;
   public static final int STREAM_ID = -1;
   public static final int PRIMARY_ID = 1;
   public static final int ADDITIONAL_INFO_LINE_HTIGHT_MIN = 4;
   public static final int ADDITIONAL_INFO_LINE_HTIGHT_MAX = 15;
   private int index;
   private ScreenDeviceSettings.StageViewSettings stageViewSettings;
   private boolean showPageCount;
   private boolean showTitleAndArtistAllSlides;
   private int showTitleAndArtistAllSlidesPosition;
   private int showPageCountPosition;
   private int additionalInfoLineHeight;
   private String additionalInfoFont;
   private Boolean additionalInfoBold;
   private Boolean additionalInfoItalic;
   private Color additionalInfoColor;
   private volatile boolean additionalInfoTwoLines;
   private volatile boolean additionalInfoPaintEffects;
   private Margin margin;
   private long version = 3L;

   public ScreenDeviceSettings() {
      this.index = 0;
      this.stageViewSettings = new ScreenDeviceSettings.StageViewSettings();
      this.showPageCount = false;
      this.showTitleAndArtistAllSlides = false;
      this.showTitleAndArtistAllSlidesPosition = 5;
      this.showPageCountPosition = 5;
      this.additionalInfoLineHeight = 7;
      this.additionalInfoFont = null;
      this.additionalInfoBold = null;
      this.additionalInfoItalic = null;
      this.additionalInfoColor = null;
      this.additionalInfoTwoLines = false;
      this.additionalInfoPaintEffects = true;
      this.margin = new Margin();
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public ScreenDeviceSettings.StageViewSettings getStageViewSettings() {
      return this.stageViewSettings != null ? this.stageViewSettings : (this.stageViewSettings = new ScreenDeviceSettings.StageViewSettings());
   }

   public void setStageViewSettings(ScreenDeviceSettings.StageViewSettings stageViewSettings) {
      this.stageViewSettings = stageViewSettings;
   }

   public boolean isShowPageCount() {
      return this.showPageCount;
   }

   public void setShowPageCount(boolean showPageCount) {
      this.showPageCount = showPageCount;
   }

   public boolean isShowTitleAndArtistAllSlides() {
      return this.showTitleAndArtistAllSlides;
   }

   public void setShowTitleAndArtistAllSlides(boolean showTitleAndArtistAllSlides) {
      this.showTitleAndArtistAllSlides = showTitleAndArtistAllSlides;
   }

   public int getShowTitleAndArtistAllSlidesPosition() {
      return this.showTitleAndArtistAllSlidesPosition;
   }

   public void setShowTitleAndArtistAllSlidesPosition(int showTitleAndArtistAllSlidesPosition) {
      this.showTitleAndArtistAllSlidesPosition = Utils.range(showTitleAndArtistAllSlidesPosition, 0, 5);
   }

   public int getShowPageCountPosition() {
      return this.showPageCountPosition;
   }

   public void setShowPageCountPosition(int showPageCountPosition) {
      this.showPageCountPosition = Utils.range(showPageCountPosition, 0, 5);
   }

   public int getAdditionalInfoLineHeight() {
      return this.additionalInfoLineHeight;
   }

   public void setAdditionalInfoLineHeight(int additionalInfoLineHeight) {
      this.additionalInfoLineHeight = Utils.range(additionalInfoLineHeight, 4, 15);
   }

   public int getAdditionalInfoLineHeightTo(int height) {
      return Math.max((int)((double)height * ((double)this.getAdditionalInfoLineHeight() / 100.0)), 3);
   }

   public String getAdditionalInfoFont() {
      return this.additionalInfoFont;
   }

   public void setAdditionalInfoFont(String additionalInfoFont) {
      this.additionalInfoFont = additionalInfoFont;
   }

   public Boolean getAdditionalInfoBold() {
      return this.additionalInfoBold;
   }

   public void setAdditionalInfoBold(Boolean additionalInfoBold) {
      this.additionalInfoBold = additionalInfoBold;
   }

   public Boolean getAdditionalInfoItalic() {
      return this.additionalInfoItalic;
   }

   public void setAdditionalInfoItalic(Boolean additionalInfoItalic) {
      this.additionalInfoItalic = additionalInfoItalic;
   }

   public synchronized Color getAdditionalInfoColor() {
      return this.additionalInfoColor;
   }

   public synchronized void setAdditionalInfoColor(Color additionalInfoColor) {
      this.additionalInfoColor = additionalInfoColor;
   }

   public boolean isAdditionalInfoTwoLines() {
      return this.additionalInfoTwoLines;
   }

   public void setAdditionalInfoTwoLines(boolean additionalInfoTwoLines) {
      this.additionalInfoTwoLines = additionalInfoTwoLines;
   }

   public boolean isAdditionalInfoPaintEffects() {
      return this.additionalInfoPaintEffects;
   }

   public void setAdditionalInfoPaintEffects(boolean additionalInfoPaintEffects) {
      this.additionalInfoPaintEffects = additionalInfoPaintEffects;
   }

   public Margin getMargin() {
      return this.margin != null ? this.margin : (this.margin = new Margin());
   }

   public void translatePageCount(Rectangle rect, int height) {
      rect.height -= height;
      if (this.isPageCountPositionTop()) {
         rect.y += height;
      }
   }

   public void translateTitleAndArtist(Rectangle rect, int height) {
      rect.height -= height;
      if (this.isTitleAndArtistPositionTop()) {
         rect.y += height;
      }
   }

   public void translateAddInfo(Rectangle rect, int height) {
      if (this.isShowPageCount() && this.isShowTitleAndArtistAllSlides()) {
         this.translatePageCount(rect, height);
      } else {
         if (this.isShowPageCount()) {
            this.translatePageCount(rect, height);
         }

         if (this.isShowTitleAndArtistAllSlides()) {
            this.translateTitleAndArtist(rect, height);
         }
      }
   }

   public String getName() {
      switch (this.index) {
         case -1:
            return I18N.WORD_STREAMING.val() + " App";
         case 1:
            return String.format("%s (%s)", I18N.WORD_SCREEN.val(), I18N.WORD_PUBLIC.val());
         default:
            return I18N.WORD_SCREEN.val() + " " + this.index;
      }
   }

   public boolean isPrimary() {
      return this.index == 1;
   }

   public boolean isStream() {
      return this.index == -1;
   }

   public boolean isPageCountPositionTop() {
      return isTop(this.showPageCountPosition);
   }

   public boolean isTitleAndArtistPositionTop() {
      return isTop(this.showTitleAndArtistAllSlidesPosition);
   }

   public static boolean isTop(int position) {
      return position == 0 || position == 1 || position == 2;
   }

   public static boolean isLeft(int position) {
      return position == 0 || position == 3;
   }

   public static boolean isCenter(int position) {
      return position == 1 || position == 4;
   }

   public static boolean isRight(int position) {
      return position == 2 || position == 5;
   }

   public Font getFont(Font _default) {
      return this.getFont(_default.getName(), _default.getSize(), _default.isBold(), _default.isItalic());
   }

   public synchronized Color getColor(Color _default) {
      return this.additionalInfoColor != null ? this.additionalInfoColor : _default;
   }

   public Font getFont(String defaultName, int size, boolean defaultBold, boolean defaultItalic) {
      String name = this.getAdditionalInfoFont();
      Boolean bold = this.getAdditionalInfoBold();
      Boolean italic = this.getAdditionalInfoItalic();
      return SwingUtils.deriveFont(name != null ? name : defaultName, size, bold != null ? bold : defaultBold, italic != null ? italic : defaultItalic);
   }

   public String hash() {
      return !this.showPageCount && !this.showTitleAndArtistAllSlides
         ? ":false:"
         : this.showPageCount
            + ":"
            + this.showTitleAndArtistAllSlides
            + ":"
            + this.showPageCountPosition
            + ":"
            + this.showTitleAndArtistAllSlidesPosition
            + ":"
            + this.additionalInfoLineHeight
            + ":"
            + this.additionalInfoFont
            + ":"
            + this.additionalInfoBold
            + ":"
            + this.additionalInfoColor
            + ":"
            + this.additionalInfoTwoLines
            + ":"
            + this.additionalInfoPaintEffects
            + ":"
            + this.additionalInfoItalic
            + ":"
            + this.margin;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      if (this.version <= 1L) {
         this.additionalInfoPaintEffects = true;
         this.version = 2L;
      }

      if (this.version <= 2L) {
         this.margin = new Margin();
         this.version = 3L;
      }

      if (this.version > 3L) {
         this.version = 3L;
      }
   }

   public static enum EnumDeviceType {
      MUSIC,
      TEXT,
      IMAGE,
      BIBLE,
      ANNOUNCEMENT,
      MEDIA;

      public Color getBackgroundColor() {
         switch (this) {
            case MUSIC:
            case TEXT:
            case BIBLE:
               return Values.COLOR_TRANSPARENT_1;
            default:
               return Color.BLACK;
         }
      }

      public boolean isMusicTextOrBible() {
         switch (this) {
            case MUSIC:
            case TEXT:
            case BIBLE:
               return true;
            default:
               return false;
         }
      }
   }

   public static class GraphicsDeviceCustom implements Serializable {
      private static final long serialVersionUID = 1L;
      private transient ScreenDeviceSettings sds;
      private transient PresentationJDialog view;
      public final GraphicsDevice device;
      public final ScreenDeviceSettings.EnumDeviceType deviceType;
      public final int index;
      private long version = 1L;

      public GraphicsDeviceCustom(GraphicsDevice device, ScreenDeviceSettings.EnumDeviceType deviceType, int index) {
         this.device = device;
         this.deviceType = deviceType;
         this.index = index;
      }

      public ScreenDeviceSettings getScreenDeviceSettings() {
         return this.sds != null ? this.sds : (this.sds = Settings.SET.getScreenDeviceSettings(this.index));
      }

      public PresentationJDialog getView() {
         return this.view;
      }

      public void setView(PresentationJDialog view) {
         this.view = view;
      }

      public Rectangle getBounds() {
         return getBounds(this.device, this.getScreenDeviceSettings());
      }

      public static Rectangle getBounds(GraphicsDevice device, ScreenDeviceSettings sds) {
         Rectangle size = device.getDefaultConfiguration().getBounds();
         Rectangle screenPosition = sds.getMargin().getRectWithMargin(size.getSize());
         size.x = size.x + screenPosition.x;
         size.y = size.y + screenPosition.y;
         size.width = screenPosition.width;
         size.height = screenPosition.height;
         return size;
      }

      private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
         ois.defaultReadObject();
      }
   }

   public static class StageViewSettings implements Serializable {
      private static final long serialVersionUID = 1L;
      private boolean enabled;
      private int id;
      private boolean upperCase;
      private boolean removeLineBreak;
      private boolean themeEnabled;
      private boolean bibleEnabled;
      private long themeId;
      private long version = 1L;

      public StageViewSettings() {
         this.enabled = false;
         this.id = StageViewUtils.EnumStageViewMode.getDefault().id;
         this.upperCase = false;
         this.removeLineBreak = false;
         this.themeEnabled = false;
         this.bibleEnabled = true;
         this.themeId = -1L;
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public void setEnabled(boolean enabled) {
         this.enabled = enabled;
      }

      public int getId() {
         return this.id;
      }

      public void setId(int id) {
         this.id = id;
      }

      public boolean isUpperCase() {
         return this.upperCase;
      }

      public void setUpperCase(boolean upperCase) {
         this.upperCase = upperCase;
      }

      public boolean isRemoveLineBreak() {
         return this.removeLineBreak;
      }

      public void setRemoveLineBreak(boolean removeLineBreak) {
         this.removeLineBreak = removeLineBreak;
      }

      public boolean isThemeEnabled() {
         return this.themeEnabled;
      }

      public void setThemeEnabled(boolean themeEnabled) {
         this.themeEnabled = themeEnabled;
      }

      public boolean isBibleEnabled() {
         return this.bibleEnabled;
      }

      public void setBibleEnabled(boolean bibleEnabled) {
         this.bibleEnabled = bibleEnabled;
      }

      public long getThemeId() {
         return this.themeId;
      }

      public void setThemeId(long themeId) {
         this.themeId = themeId;
      }

      @Override
      public String toString() {
         return String.format(
            "[enabled=%s;type=%s;themeEnabled=%s;bibleEnabled=%s;themeId=%s]", this.enabled, this.id, this.themeEnabled, this.bibleEnabled, this.themeId
         );
      }

      public String hashKey() {
         return ":"
            + this.enabled
            + ":"
            + this.id
            + ":"
            + this.upperCase
            + ":"
            + this.removeLineBreak
            + ":"
            + this.themeEnabled
            + ":"
            + this.bibleEnabled
            + ":"
            + this.themeId;
      }

      private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
         ois.defaultReadObject();
      }
   }
}
