package com.limagiran.holyrics.model;

import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.AlertSettingsUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class AlertSettings implements Serializable, AlertSettingsUtils {
   private static final long serialVersionUID = 1L;
   public static final int LINE_HEIGHT_MIN = 5;
   public static final int LINE_HEIGHT_MAX = 15;
   public static final int DURATION_MIN = 3;
   public static final int DURATION_MAX = 30;
   public static final double VELOCITY_DEFAULT = 2.0;
   public static final double VELOCITY_MIN = 0.2;
   public static final double VELOCITY_MAX = 5.0;
   public static final double VELOCITY_STEP = 0.2;
   public static final int FRAME_SKIP_DEFAULT = 15;
   public static final int FRAME_SKIP_MIN = 0;
   public static final int FRAME_SKIP_MAX = 60;
   private String text = "";
   private Font font = new Font("Arial", 0, 8);
   private Color background = Color.BLACK;
   private boolean backgroundEnabled = true;
   private Color foreground = Color.WHITE;
   private int lineHeight = 7;
   private int duration = 7;
   private boolean bottom = true;
   private boolean overlap = true;
   private double velocity = 2.0;
   private int frameSkip = 15;
   private transient boolean show;
   private long version = 3L;

   public boolean isShow() {
      return this.show;
   }

   public void setShow(boolean show) {
      this.show = show;
   }

   public String getFullText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public Font getFont(float size) {
      return this.font.deriveFont(size);
   }

   public Font getFont() {
      return this.font;
   }

   public String getFontName() {
      return this.font.getName();
   }

   public void setFontName(String fontName) {
      this.font = new Font(fontName, this.font.getStyle(), this.font.getSize());
   }

   public void setBold(boolean bold) {
      this.font = this.font.deriveFont(bold ? 1 : 0);
   }

   public Color getBackground() {
      return this.background;
   }

   public Color getBackgroundToPaint() {
      Color c = this.background;
      int transparency = !this.backgroundEnabled && this.isOverlap() ? 0 : 255;
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), transparency);
   }

   public void setBackground(Color background) {
      this.background = background;
   }

   public boolean isBackground() {
      return this.backgroundEnabled;
   }

   public void setBackground(boolean backgroundEnabled) {
      this.backgroundEnabled = backgroundEnabled;
   }

   public Color getForeground() {
      return this.foreground;
   }

   public void setForeground(Color foreground) {
      this.foreground = foreground;
   }

   public int getLineHeight() {
      return this.lineHeight;
   }

   public void setLineHeight(int lineHeight) {
      this.lineHeight = Utils.range(lineHeight, 5, 15);
   }

   public int getDuration() {
      return 7;
   }

   public void setDuration(int duration) {
      this.duration = 7;
   }

   public void setLineHeight(String lineHeight) {
      try {
         this.setLineHeight(Integer.parseInt(lineHeight.replaceAll("[^\\d]", "")));
      } catch (Exception var3) {
      }
   }

   public boolean isBottom() {
      return this.bottom;
   }

   public void setBottom(boolean bottom) {
      this.bottom = bottom;
   }

   public boolean isTop() {
      return !this.bottom;
   }

   public void setTop(boolean top) {
      this.bottom = !top;
   }

   public boolean isOverlap() {
      return this.overlap;
   }

   public void setOverlap(boolean overlap) {
      this.overlap = overlap;
   }

   public double getVelocity() {
      return Utils.range(this.velocity, 0.2, 5.0);
   }

   public void setVelocity(double velocity) {
      this.velocity = Utils.range(velocity, 0.2, 5.0);
   }

   public int getFrameSkip() {
      return Utils.range(this.frameSkip, 0, 60);
   }

   public void setFrameSkip(int frameSkip) {
      this.frameSkip = Utils.range(frameSkip, 0, 60);
   }

   public void save() {
      Settings.SET.saveTrue();
   }

   @Override
   public AlertSettings getInstance() {
      return this;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      if (this.version == 1L) {
         this.backgroundEnabled = true;
         this.version = 2L;
      }

      if (this.version == 2L) {
         this.overlap = true;
         this.velocity = 2.0;
         this.frameSkip = 15;
         this.version = 3L;
      }

      if (this.version > 3L) {
         this.version = 3L;
      }
   }
}
