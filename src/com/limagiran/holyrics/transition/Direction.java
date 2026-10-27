package com.limagiran.holyrics.transition;

import com.alee.laf.combobox.WebComboBox;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Utils;

public enum Direction {
   RANDOM(I18N.WORD_RANDOM.val()),
   HORIZONTAL(I18N.WORD_HORIZONTAL.val()),
   VERTICAL(I18N.WORD_VERTICAL.val()),
   UP(I18N.WORD_UP.val()),
   DOWN(I18N.WORD_DOWN.val()),
   LEFT(I18N.WORD_LEFT.val()),
   RIGHT(I18N.WORD_RIGHT.val());

   public final String name;

   private Direction(String name) {
      this.name = name;
   }

   public boolean isHorizontal() {
      return this.equals(HORIZONTAL) || this.equals(LEFT) || this.equals(RIGHT);
   }

   public boolean isVertical() {
      return this.equals(VERTICAL) || this.equals(UP) || this.equals(DOWN);
   }

   public boolean isUp() {
      return this.equals(UP);
   }

   public boolean isDown() {
      return this.equals(DOWN);
   }

   public boolean isLeft() {
      return this.equals(LEFT);
   }

   public boolean isRight() {
      return this.equals(RIGHT);
   }

   public static Direction getOrDefault(String name, Direction _default) {
      try {
         return valueOf(name);
      } catch (Exception var3) {
         return _default;
      }
   }

   public Direction getSpecific() {
      switch (this) {
         case RANDOM:
            return random(UP, DOWN, LEFT, RIGHT);
         case HORIZONTAL:
            return random(LEFT, RIGHT);
         case VERTICAL:
            return random(UP, DOWN);
         default:
            return this;
      }
   }

   public static Direction random(Direction... values) {
      try {
         return values[Utils.random(values.length) - 1];
      } catch (Exception var2) {
         return HORIZONTAL;
      }
   }

   public static Direction random() {
      return random(UP, DOWN, LEFT, RIGHT);
   }

   public static Direction getOrDefault(TransitionSettings set, Direction _default) {
      return set.isAllRandom() ? RANDOM : getOrDefault(set.getString(Direction.class.getName(), "---"), _default);
   }

   @Override
   public String toString() {
      return this.name;
   }

   public static void setupDefaultComboBox(DefaultPanelSet p, TransitionSettings set, Direction[] values, Direction _default) {
      Direction dir = getOrDefault(set, _default);
      set.setValue(Direction.class.getName(), dir.name());
      WebComboBox cmbbx = new WebComboBox(values, dir);
      cmbbx.addItemListener(e -> {
         if (cmbbx.getSelectedItem() instanceof Direction) {
            set.setValue(Direction.class.getName(), ((Direction)cmbbx.getSelectedItem()).name());
         }
      });
      p.put(I18N.WORD_DIRECTION.val(), cmbbx);
   }
}
