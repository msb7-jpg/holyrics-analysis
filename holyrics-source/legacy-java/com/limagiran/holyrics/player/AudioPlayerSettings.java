package com.limagiran.holyrics.player;

import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Utils;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioPlayerSettings implements Serializable, AudioPlayerSetUtils {
   private static final long serialVersionUID = 1L;
   public final List<File> list = new ArrayList<>();
   private File media = null;
   private boolean repeat;
   private boolean shuffle;
   private boolean playOneFile = true;
   private boolean mute;
   private double volume = 0.7;
   private boolean equalization = true;
   private Equalizer.EnumEqualizer enumEqualizer = Equalizer.EnumEqualizer.DEFAULT;
   private final Map<Integer, Integer> equalizer = new HashMap<>();
   private long version = 1L;

   public File getMedia() {
      return this.media;
   }

   public void setMedia(File media) {
      this.media = media;
      this.save();
   }

   public void setEqualizer(int hz, int gain) {
      this.equalizer.put(hz, gain);
      this.save();
   }

   public int getEqualizer(int hz) {
      return this.equalizer.getOrDefault(hz, 0);
   }

   public Equalizer.EnumEqualizer getEnumEqualizer() {
      return this.enumEqualizer;
   }

   public void setEnumEqualizer(Equalizer.EnumEqualizer enumEqualizer) {
      this.enumEqualizer = enumEqualizer;
      this.save();
   }

   public boolean isRepeat() {
      return this.repeat;
   }

   public void setRepeat(boolean repeat) {
      this.repeat = repeat;
      this.save();
   }

   public boolean isShuffle() {
      return this.shuffle;
   }

   public void setShuffle(boolean shuffle) {
      this.shuffle = shuffle;
      this.save();
   }

   public boolean isPlayOneFile() {
      return this.playOneFile;
   }

   public void setPlayOneFile(boolean playOneFile) {
      this.playOneFile = playOneFile;
      this.save();
   }

   public boolean isEqualization() {
      return this.equalization;
   }

   public void setEqualization(boolean equalization) {
      this.equalization = equalization;
      this.save();
   }

   public boolean isMute() {
      return this.mute;
   }

   public void setMute(boolean mute) {
      this.mute = mute;
      this.save();
   }

   public double getVolume() {
      return this.volume;
   }

   public int getVolumeInt() {
      return (int)(this.volume * 100.0);
   }

   public void setVolumeInt(int volume) {
      this.volume = Utils.range((double)volume / 100.0, 0.0, 1.0);
      this.save();
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }

   @Override
   public AudioPlayerSettings getInstance() {
      return this;
   }

   public void save() {
      Settings.SET.saveTrue();
   }
}
