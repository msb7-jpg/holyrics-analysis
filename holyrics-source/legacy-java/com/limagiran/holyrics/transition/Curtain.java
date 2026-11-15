package com.limagiran.holyrics.transition;

import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.alee.utils.GraphicsUtils;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Curtain extends RendererEffect {
   private static final String THICKNESS_NAME = "c-thickness";
   private static final int THICKNESS_DEFAULT = 40;
   private static final int THICKNESS_MIN = 1;
   private static final int THICKNESS_MAX = 334;
   private float thicknessPercent = 0.04F;
   private Direction direction = Direction.VERTICAL;
   private Curtain.CurtainLineDirection lineDirection = Curtain.CurtainLineDirection.DOWN_RIGHT;
   private SlideMoveType slideMoveType = SlideMoveType.BOTH;
   private static final Direction[] DIRECTIONS = new Direction[]{Direction.RANDOM, Direction.HORIZONTAL, Direction.VERTICAL};

   @Override
   public void prepare2(MyImageTransition imageTransition, CustomTransitionEffect owner) {
      this.thicknessPercent = (float)getThickness(owner.transitionSettings) / 1000.0F;
      this.direction = Direction.getOrDefault(owner.transitionSettings, Direction.VERTICAL).getSpecific();
      this.lineDirection = Curtain.CurtainLineDirection.getOrDefault(owner.transitionSettings, Curtain.CurtainLineDirection.DOWN_RIGHT).getSpecific();
      this.slideMoveType = SlideMoveType.getOrDefault(owner.transitionSettings, SlideMoveType.BOTH).getSpecific();
   }

   private static int getThickness(TransitionSettings set) {
      return Utils.range(set.getInt("c-thickness", 40), 1, 334);
   }

   private void perform(int[] rowsOrCols, int maxSize, float progress) {
      int size = maxSize * 5;
      int midSize = size / 2;
      Arrays.fill(rowsOrCols, size);
      float inverseProgress = 1.0F - progress;

      for (int i = 0; i < rowsOrCols.length; i++) {
         rowsOrCols[i] = (int)((float)rowsOrCols[i] - (float)midSize * ((float)(i + 1) / (float)rowsOrCols.length));
         rowsOrCols[i] = (int)((float)rowsOrCols[i] - (float)size * inverseProgress);
         rowsOrCols[i] = rowsOrCols[i] < 0 ? 0 : (rowsOrCols[i] > maxSize ? maxSize : rowsOrCols[i]);
      }
   }

   @Override
   public void paint(Graphics2D g, BufferedImage current, BufferedImage other, CustomTransitionEffect owner) {
      int w = other.getWidth();
      int h = other.getHeight();
      int val = this.direction.isVertical() ? w : h;
      int maxProgress = Math.max(1, this.direction.isHorizontal() ? w : h);
      int thickness = Math.max(1, (int)(this.thicknessPercent * (float)val));
      int rowsOrCols = (val % thickness == 0 ? val : val / thickness * thickness + thickness) / thickness;
      int[] progress = new int[Math.max(rowsOrCols, 1)];
      boolean fade = owner.deviceType.isMusicTextOrBible();
      this.perform(progress, maxProgress, owner.progress);
      if (!this.slideMoveType.isBoth()) {
         boolean flag = this.slideMoveType.isNew();
         BooleanOptional.runIfTrue(
            fade, () -> G2Utils.setupAlphaComposite(g, flag ? 1.0F - owner.progress * 2.5F : (float)Math.pow((double)owner.progress, 2.0))
         );
         g.drawImage(flag ? current : other, 0, 0, w, h, null);
         BooleanOptional.runIfTrue(fade, () -> G2Utils.setupAlphaComposite(g, 1.0F));
      }

      BufferedImage otherImage = other;
      int rows = progress.length;
      boolean isOld = this.slideMoveType.isOld();
      boolean isNew = this.slideMoveType.isNew();

      for (int i = 0; i < rows; i++) {
         int _progress = progress[i];
         boolean flag = this.direction.isHorizontal();
         int mark = this.lineDirection == Curtain.CurtainLineDirection.DOWN_RIGHT
            ? 1
            : (this.lineDirection == Curtain.CurtainLineDirection.UP_LEFT ? -1 : (i % 2 == 0 ? 1 : -1));
         int x1 = flag ? mark * Math.round((float)w * ((float)_progress / (float)maxProgress)) - mark * w : i * thickness;
         int x2 = flag ? x1 + w : x1 + Math.min(w - i * thickness, thickness);
         int y1 = flag ? i * thickness : mark * Math.round((float)h * ((float)_progress / (float)maxProgress)) - mark * h;
         int y2 = flag ? y1 + Math.min(h - i * thickness, thickness) : y1 + h;
         int x3 = flag ? 0 : x1;
         int y3 = flag ? y1 : 0;
         int x4 = flag ? w : x2;
         int y4 = flag ? y2 : h;
         Composite old = GraphicsUtils.setupAlphaComposite(g, (float)_progress / (float)maxProgress, _progress < maxProgress);
         if (_progress > 0 && !isOld) {
            g.drawImage(otherImage, x1, y1, x2, y2, x3, y3, x4, y4, null);
         }

         GraphicsUtils.restoreComposite(g, old, _progress < maxProgress);
         if (!isNew) {
            int diff = mark == 1 ? (flag ? w : h) : (flag ? -w : -h);
            int _x1 = x1 + (flag ? diff : 0);
            int _y1 = y1 + (flag ? 0 : diff);
            int _x2 = x2 + (flag ? diff : 0);
            int _y2 = y2 + (flag ? 0 : diff);
            g.drawImage(current, _x1, _y1, _x2, _y2, x3, y3, x4, y4, null);
         }
      }
   }

   public static WebPanel getPanelSet(TransitionSettings set) {
      DefaultPanelSet p = new DefaultPanelSet(set);
      TransitionType.setupDefaultSlider(
         p, set, "c-thickness", getThickness(set), 1, 334, I18N.WORD_WIDTH.val(), val -> String.format("%.1f%%", (float)val.intValue() / 10.0F)
      );
      Direction.setupDefaultComboBox(p, set, DIRECTIONS, Direction.VERTICAL);
      Curtain.CurtainLineDirection.setupDefaultComboBox(p, set, Curtain.CurtainLineDirection.DOWN_RIGHT);
      SlideMoveType.setupDefaultComboBox(p, set, SlideMoveType.BOTH);
      return p;
   }

   public static enum CurtainLineDirection {
      RANDOM(I18N.WORD_RANDOM.val()),
      DOWN_RIGHT(I18N.WORD_DOWN.val() + " / " + I18N.WORD_RIGHT.val()),
      UP_LEFT(I18N.WORD_UP.val() + " / " + I18N.WORD_LEFT.val()),
      ALTERNATE(I18N.WORD_ALTERNATE.val());

      public final String name;

      private CurtainLineDirection(String name) {
         this.name = name;
      }

      public Curtain.CurtainLineDirection getSpecific() {
         return this.equals(RANDOM) ? random() : this;
      }

      public static Curtain.CurtainLineDirection getOrDefault(String name, Curtain.CurtainLineDirection _default) {
         try {
            return valueOf(name);
         } catch (Exception var3) {
            return _default;
         }
      }

      public static Curtain.CurtainLineDirection getOrDefault(TransitionSettings set, Curtain.CurtainLineDirection _default) {
         return set.isAllRandom() ? RANDOM : getOrDefault(set.getString(Curtain.CurtainLineDirection.class.getName(), "---"), _default);
      }

      public static Curtain.CurtainLineDirection random(Curtain.CurtainLineDirection... values) {
         try {
            return values[Utils.random(values.length) - 1];
         } catch (Exception var2) {
            return DOWN_RIGHT;
         }
      }

      public static Curtain.CurtainLineDirection random() {
         return random(DOWN_RIGHT, UP_LEFT, ALTERNATE);
      }

      public boolean isDown() {
         return this == DOWN_RIGHT;
      }

      public boolean isRight() {
         return this == DOWN_RIGHT;
      }

      public boolean isUp() {
         return this == UP_LEFT;
      }

      public boolean isLeft() {
         return this == UP_LEFT;
      }

      public boolean isBoth() {
         return this == ALTERNATE;
      }

      private static void setupDefaultComboBox(DefaultPanelSet p, TransitionSettings set, Curtain.CurtainLineDirection _default) {
         Curtain.CurtainLineDirection csd = getOrDefault(set, _default);
         set.setValue(Curtain.CurtainLineDirection.class.getName(), csd.name());
         WebComboBox cmbbx = new WebComboBox(values(), csd);
         cmbbx.addItemListener(e -> {
            if (cmbbx.getSelectedItem() instanceof Curtain.CurtainLineDirection) {
               set.setValue(Curtain.CurtainLineDirection.class.getName(), ((Curtain.CurtainLineDirection)cmbbx.getSelectedItem()).name());
            }
         });
         p.put(I18N.WORD_DIRECTION.val() + " (" + I18N.WORD_LINES + ")", cmbbx);
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
