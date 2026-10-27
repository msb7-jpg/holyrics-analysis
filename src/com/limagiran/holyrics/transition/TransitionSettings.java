package com.limagiran.holyrics.transition;

import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TransitionSettings implements Serializable, Values {
   private static final long serialVersionUID = 1L;
   private boolean enabled;
   private String type;
   private boolean onlyAreaWithinMargin;
   private int duration;
   private final Map<String, String> values = new HashMap<>();
   private transient List<TransitionType> listRandomCheckeds;
   private long version = 1L;

   public TransitionSettings() {
      this(false, TransitionType.FADE);
   }

   public TransitionSettings(boolean enabled, TransitionType type) {
      this.enabled = enabled;
      this.type = type.name();
      this.duration = type.durationDefault;
   }

   public synchronized void clone(TransitionSettings ts) {
      this.enabled = ts.enabled;
      this.type = ts.type;
      this.onlyAreaWithinMargin = ts.onlyAreaWithinMargin;
      this.duration = ts.duration;
      this.values.clear();
      this.values.putAll(ts.values);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public TransitionType getType() {
      return TransitionType.getOrDefault(this.type);
   }

   public void setType(TransitionType type) {
      this.type = type.name();
   }

   public boolean isOnlyAreaWithinMargin() {
      return this.onlyAreaWithinMargin;
   }

   public void setOnlyAreaWithinMargin(boolean onlyAreaWithinMargin) {
      this.onlyAreaWithinMargin = onlyAreaWithinMargin;
   }

   public int getDuration() {
      return Utils.range(this.duration, 200, 2400);
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public synchronized void setValue(String key, String value) {
      this.values.put(key, value);
      if (Objects.equals(key, "checkeds-values")) {
         this.getListRandomCheckeds(true);
      }
   }

   public String getString(String key, String _default) {
      return this.values.getOrDefault(key, _default);
   }

   public int getInt(String key, int _default) {
      String s = this.values.getOrDefault(key, "a");
      return Utils.intOrElse(s, _default);
   }

   public long getLong(String key, long _default) {
      String s = this.values.getOrDefault(key, "a");
      return Utils.longOrElse(s, _default);
   }

   public float getFloat(String key, float _default) {
      String s = this.values.getOrDefault(key, "a");
      return Utils.floatOrElse(s, _default);
   }

   public double getDouble(String key, double _default) {
      String s = this.values.getOrDefault(key, "a");
      return Utils.doubleOrElse(s, _default);
   }

   public boolean getBoolean(String key, boolean _default) {
      String s = this.values.get(key);
      return s != null ? Boolean.parseBoolean(s) : _default;
   }

   public boolean isAllRandom() {
      return this.getBoolean("all-random", false);
   }

   private synchronized List<TransitionType> getListRandomCheckeds(boolean update) {
      if (this.listRandomCheckeds == null) {
         this.listRandomCheckeds = new ArrayList<>();
         update = true;
      }

      if (update) {
         this.listRandomCheckeds.clear();
         Arrays.stream(this.getString("checkeds-values", ";").split(";"))
            .map(TransitionType::getOrDefault)
            .filter(t -> !t.isRandom())
            .distinct()
            .forEach(this.listRandomCheckeds::add);
      }

      return this.listRandomCheckeds;
   }

   private synchronized List<TransitionType> getListRandomCheckeds2() {
      return this.getListRandomCheckeds(false);
   }

   public List<TransitionType> getListRandomCheckeds() {
      return new ArrayList<>(this.getListRandomCheckeds2());
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
   }
}
