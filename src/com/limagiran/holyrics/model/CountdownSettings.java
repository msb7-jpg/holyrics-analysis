package com.limagiran.holyrics.model;

import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.CountdownUtils;
import com.limagiran.holyrics.util.Utils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class CountdownSettings implements Serializable, CountdownUtils {
   private static final long serialVersionUID = 1L;
   public static final int SIZE_MIN_VALUE = 100;
   public static final int SIZE_MAX_VALUE = 800;
   public static final int SIZE_DEFAULT_VALUE = 250;
   public static final String BREAK_LINE_TOKEN = "<brtk=t>";
   private String textBefore = "";
   private String textAfter = I18N.MSG_COUNTDOWN_DEFAULT_TEXT.val();
   private boolean determine = true;
   private int minuteDetermine = 5;
   private int secondDetermine = 0;
   private int hourExact = 19;
   private int minuteExact = 0;
   private int size = 250;
   private boolean zeroFill;
   private long theme;
   private long version = 1L;

   public String getTextBefore(boolean replaceBreakLine) {
      return replaceBreakLine ? this.textBefore.replace("\n", "<brtk=t>") : this.textBefore;
   }

   public void setTextBefore(String textBefore) {
      this.textBefore = textBefore;
   }

   public String getTextAfter(boolean replaceBreakLine) {
      return replaceBreakLine ? this.textAfter.replace("\n", "<brtk=t>") : this.textAfter;
   }

   public void setTextAfter(String textAfter) {
      this.textAfter = textAfter;
   }

   public boolean isDetermine() {
      return this.determine;
   }

   public void setDetermine(boolean determine) {
      this.determine = determine;
   }

   public int getMinuteDetermine() {
      return Utils.range(this.minuteDetermine, 0, 59);
   }

   public void setMinuteDetermine(int minuteDetermine) {
      this.minuteDetermine = minuteDetermine;
   }

   public int getSecondDetermine() {
      return Utils.range(this.secondDetermine, 0, 59);
   }

   public void setSecondDetermine(int secondDetermine) {
      this.secondDetermine = secondDetermine;
   }

   public int getHourExact() {
      return Utils.range(this.hourExact, 0, 23);
   }

   public void setHourExact(int hourExact) {
      this.hourExact = hourExact;
   }

   public int getMinuteExact() {
      return Utils.range(this.minuteExact, 0, 59);
   }

   public void setMinuteExact(int minuteExact) {
      this.minuteExact = minuteExact;
   }

   public int getSize() {
      return Utils.range(this.size, 100, 800);
   }

   public void setSize(int size) {
      this.size = size;
   }

   public boolean isZeroFill() {
      return this.zeroFill;
   }

   public void setZeroFill(boolean zeroFill) {
      this.zeroFill = zeroFill;
   }

   public long getTheme() {
      return this.theme;
   }

   public Theme getThemeObject() {
      return ThemeController.get(this.getTheme());
   }

   public void setTheme(long theme) {
      this.theme = theme;
   }

   public void save() {
      Settings.SET.saveTrue();
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      if (this.version == 1L) {
      }

      if (this.version > 1L) {
         this.version = 1L;
      }
   }

   @Override
   public CountdownSettings getInstance() {
      return this;
   }
}
