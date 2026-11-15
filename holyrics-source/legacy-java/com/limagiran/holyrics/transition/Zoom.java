package com.limagiran.holyrics.transition;

import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.ScreenUtils;
import com.limagiran.holyrics.util.UICreate;
import com.limagiran.holyrics.util.Utils;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Zoom extends RendererEffect {
   private static final String DIRECTION_NAME = "z-direction";
   private static final String DIRECTION_DEFAULT = "4";
   private static final int DIRECTION_DEFAULT_INT = 4;
   private Zoom.ZoomType zoom = Zoom.ZoomType.INCREASE;
   private int direction;

   @Override
   public void prepare2(MyImageTransition imageTransition, CustomTransitionEffect owner) {
      this.zoom = Zoom.ZoomType.getOrDefault(owner.transitionSettings, Zoom.ZoomType.INCREASE).getSpecific();
      this.direction = sortDirection(owner.transitionSettings);
   }

   @Override
   public void paint(Graphics2D g, BufferedImage current, BufferedImage other, CustomTransitionEffect owner) {
      boolean in = this.zoom.isIn();
      double inverseProgress = Math.abs((in ? 0.0 : 1.0) - (double)owner.progress);
      boolean fade = owner.deviceType.isMusicTextOrBible();
      BufferedImage max = in ? current : other;
      BufferedImage min = in ? other : current;
      BooleanOptional.runIfTrue(fade, () -> G2Utils.setupAlphaComposite(g, 1.0F - (float)inverseProgress));
      g.drawImage(max, 0, 0, null);
      G2Utils.setupAlphaComposite(g, (float)inverseProgress * 1.2F);
      Dimension maxSize = new Dimension(max.getWidth(), max.getHeight());
      Dimension minSize = new Dimension(
         (int)Math.max(1.0, (double)min.getWidth() * inverseProgress), (int)Math.max(1.0, (double)min.getHeight() * inverseProgress)
      );
      Rectangle r = ScreenUtils.getNineDirectionsLocation(minSize.width, minSize.height, maxSize, this.direction);
      g.drawImage(min, r.x, r.y, minSize.width, minSize.height, null);
      G2Utils.setupAlphaComposite(g, 1.0F);
   }

   public static String getDirections(TransitionSettings set) {
      return set.isAllRandom() ? "" : set.getString("z-direction", "4");
   }

   public static synchronized void addDirection(int directionInt, TransitionSettings set) {
      String direction = String.valueOf(directionInt);
      String directions = getDirections(set);
      if (!directions.contains(direction)) {
         set.setValue("z-direction", directions + direction);
      }
   }

   public static synchronized void removeDirection(int directionInt, TransitionSettings set) {
      String directions = getDirections(set);
      set.setValue("z-direction", directions.replace(String.valueOf(directionInt), ""));
   }

   public static int sortDirection(TransitionSettings set) {
      String directions = getDirections(set);
      if (directions.isEmpty()) {
         return Utils.random(9) - 1;
      } else {
         int index = directions.length() == 1 ? 0 : Utils.random(directions.length()) - 1;
         return Utils.intOrElse(directions.charAt(index), 4);
      }
   }

   public static WebPanel getPanelSet(TransitionSettings set) {
      DefaultPanelSet p = new DefaultPanelSet(set);
      Zoom.ZoomType.setupDefaultComboBox(p, set);
      List<Integer> selected = new ArrayList<>();

      for (char c : getDirections(set).toCharArray()) {
         int val = Utils.intOrElse(c, 4);
         if (!selected.contains(val)) {
            selected.add(val);
         }
      }

      p.put(I18N.WORD_DIRECTION.val(), UICreate.nineDirectionsPanel(selected, 10, false, (direction, isSelected) -> {
         if (isSelected) {
            addDirection(direction, set);
         } else {
            removeDirection(direction, set);
         }
      }));
      return p;
   }

   public static enum ZoomType {
      RANDOM(I18N.WORD_RANDOM.val()),
      INCREASE(I18N.WORD_INCREASE.val()),
      DECREASE(I18N.WORD_DECREASE.val());

      public final String name;

      private ZoomType(String name) {
         this.name = name;
      }

      public Zoom.ZoomType getSpecific() {
         return this.equals(RANDOM) ? random() : this;
      }

      public static Zoom.ZoomType getOrDefault(String name, Zoom.ZoomType _default) {
         try {
            return valueOf(name);
         } catch (Exception var3) {
            return _default;
         }
      }

      public static Zoom.ZoomType getOrDefault(TransitionSettings set, Zoom.ZoomType _default) {
         return set.isAllRandom() ? RANDOM : getOrDefault(set.getString(Zoom.ZoomType.class.getName(), "---"), _default);
      }

      public static Zoom.ZoomType random(Zoom.ZoomType... values) {
         try {
            return values[Utils.random(values.length) - 1];
         } catch (Exception var2) {
            return INCREASE;
         }
      }

      public static Zoom.ZoomType random() {
         return random(INCREASE, DECREASE);
      }

      public boolean isIn() {
         return this == INCREASE;
      }

      public boolean isOut() {
         return this == DECREASE;
      }

      public boolean isRandom() {
         return this == RANDOM;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static void setupDefaultComboBox(DefaultPanelSet p, TransitionSettings set) {
         Zoom.ZoomType dir = getOrDefault(set, INCREASE).getSpecific();
         if (!Arrays.asList(values()).contains(dir)) {
            dir = INCREASE;
         }

         set.setValue(Zoom.ZoomType.class.getName(), dir.name());
         WebComboBox cmbbxZoom = new WebComboBox(values(), dir);
         cmbbxZoom.addItemListener(e -> {
            if (cmbbxZoom.getSelectedItem() instanceof Zoom.ZoomType) {
               set.setValue(Zoom.ZoomType.class.getName(), ((Zoom.ZoomType)cmbbxZoom.getSelectedItem()).name());
            }
         });
         p.put("Zoom", cmbbxZoom);
      }
   }
}
