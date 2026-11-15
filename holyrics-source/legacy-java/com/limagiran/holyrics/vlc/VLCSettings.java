package com.limagiran.holyrics.vlc;

import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Utils;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VLCSettings implements Serializable, VLCSetUtils {
   private static final long serialVersionUID = 1L;
   public static final int NO_REPEAT = 0;
   public static final int REPEAT_ONE = 1;
   public static final int REPEAT_ALL = 9;
   public static final int MIN_VOLUME = 0;
   public static final int MAX_VOLUME = 120;
   public final List<File> list = new ArrayList<>();
   private int repeat = 0;
   private boolean mute;
   private int volume = 100;
   private long version = 1L;

   public boolean isNoRepeat() {
      return this.repeat == 0;
   }

   public boolean isRepeatOne() {
      return this.repeat == 1;
   }

   public boolean isRepeatAll() {
      return this.repeat == 9;
   }

   public String getRepeatChar() {
      return this.isNoRepeat() ? "" : (this.isRepeatOne() ? "1" : "∞");
   }

   public int nextRepeatOption() {
      this.repeat = this.isRepeatOne() ? 9 : (this.isRepeatAll() ? 0 : 1);
      this.save();
      return this.repeat;
   }

   public boolean isMute() {
      return this.mute;
   }

   public boolean setMute(boolean mute) {
      this.mute = mute;
      this.save();
      return this.mute;
   }

   public int getVolume() {
      return Utils.range(this.volume, 0, 120);
   }

   public int setVolume(int volume) {
      this.volume = Utils.range(volume, 0, 120);
      this.save();
      return this.volume;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }

   @Override
   public VLCSettings getInstance() {
      return this;
   }

   public void save() {
      Settings.SET.saveTrue();
   }
}
