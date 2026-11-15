package com.limagiran.holyrics.model;

import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.ClockSettingsUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class ClockSettings implements Serializable, ClockSettingsUtils {
   private static final long serialVersionUID = 1L;
   public static final Font FONT = Utils.loadFont(ClockSettings.class.getResourceAsStream("/com/limagiran/holyrics/file/digital-7_mono.ttf"), 0, 8);
   public static final int LINE_HEIGHT_MIN = 6;
   public static final int LINE_HEIGHT_MAX = 50;
   private Color background = Color.BLACK;
   private boolean backgroundEnabled = true;
   private Color foreground = Color.RED;
   private int lineHeight = 12;
   private int position = 2;
   private boolean show;
   private long version = 2L;

   public boolean isShow() {
      return this.show;
   }

   public void setShow(boolean show) {
      this.show = show;
      this.update();
   }

   public Color getBackground() {
      return this.background;
   }

   public Color getBackgroundToPaint() {
      Color c = this.background;
      return this.backgroundEnabled ? this.background : new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
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
      this.lineHeight = Utils.range(lineHeight, 6, 50);
   }

   public void setLineHeight(String lineHeight) {
      try {
         this.setLineHeight(Integer.parseInt(lineHeight.replaceAll("[^\\d]", "")));
      } catch (Exception var3) {
      }
   }

   public int getPosition() {
      return this.position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public void save() {
      Settings.SET.saveTrue();
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      if (this.version == 1L) {
         this.backgroundEnabled = true;
         this.version = 2L;
      }

      if (this.version > 2L) {
         this.version = 2L;
      }
   }

   @Override
   public ClockSettings getInstance() {
      return this;
   }

   public static Font getFont(float size) {
      return FONT.deriveFont(size);
   }
}
