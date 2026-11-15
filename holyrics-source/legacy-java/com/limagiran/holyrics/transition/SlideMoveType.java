package com.limagiran.holyrics.transition;

import com.alee.laf.combobox.WebComboBox;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Utils;

public enum SlideMoveType {
   RANDOM(I18N.WORD_RANDOM.val()),
   NEW(I18N.MSG_MOVE_NEW.val()),
   OLD(I18N.MSG_MOVE_OLD.val()),
   BOTH(I18N.MSG_MOVE_BOTH.val());

   public final String name;

   private SlideMoveType(String name) {
      this.name = name;
   }

   public SlideMoveType getSpecific() {
      return this.equals(RANDOM) ? random() : this;
   }

   public static SlideMoveType getOrDefault(String name, SlideMoveType _default) {
      try {
         return valueOf(name);
      } catch (Exception var3) {
         return _default;
      }
   }

   public static SlideMoveType getOrDefault(TransitionSettings set, SlideMoveType _default) {
      return set.isAllRandom() ? RANDOM : getOrDefault(set.getString(SlideMoveType.class.getName(), "---"), _default);
   }

   public boolean isNew() {
      return this == NEW;
   }

   public boolean isOld() {
      return this == OLD;
   }

   public boolean isBoth() {
      return this == BOTH;
   }

   public static SlideMoveType random(SlideMoveType... values) {
      try {
         return values[Utils.random(values.length) - 1];
      } catch (Exception var2) {
         return BOTH;
      }
   }

   public static SlideMoveType random() {
      return random(NEW, OLD, BOTH);
   }

   public static void setupDefaultComboBox(DefaultPanelSet p, TransitionSettings set, SlideMoveType[] values, SlideMoveType _default) {
      SlideMoveType dir = getOrDefault(set, _default);
      set.setValue(SlideMoveType.class.getName(), dir.name());
      WebComboBox cmbbx = new WebComboBox(values, dir);
      cmbbx.addItemListener(e -> {
         if (cmbbx.getSelectedItem() instanceof SlideMoveType) {
            set.setValue(SlideMoveType.class.getName(), ((SlideMoveType)cmbbx.getSelectedItem()).name());
         }
      });
      p.put(I18N.WORD_TYPE.val(), cmbbx);
   }

   public static void setupDefaultComboBox(DefaultPanelSet p, TransitionSettings set, SlideMoveType _default) {
      setupDefaultComboBox(p, set, values(), _default);
   }

   @Override
   public String toString() {
      return this.name;
   }
}
