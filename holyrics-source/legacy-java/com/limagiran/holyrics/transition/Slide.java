package com.limagiran.holyrics.transition;

import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Slide extends RendererEffect {
   private Slide.SlideType type;
   private Direction direction;
   private static final Direction[] DIRECTIONS = new Direction[]{Direction.RANDOM, Direction.LEFT, Direction.UP};

   @Override
   public void prepare2(MyImageTransition imageTransition, CustomTransitionEffect owner) {
      this.type = Slide.SlideType.getOrDefault(owner.transitionSettings, Slide.SlideType.MOVE_BOTH).getSpecific();
      this.direction = Direction.getOrDefault(owner.transitionSettings, Direction.UP).getSpecific();
      if (this.direction.isHorizontal()) {
         this.direction = Direction.LEFT;
      } else {
         this.direction = Direction.UP;
      }
   }

   @Override
   public void paint(Graphics2D g, BufferedImage current, BufferedImage other, CustomTransitionEffect owner) {
      int width = other.getWidth();
      int height = other.getHeight();
      this.type = this.type != null ? this.type : Slide.SlideType.random();
      this.direction = this.direction != null ? this.direction : Direction.random();
      int slideProgress = (int)((float)(this.direction.isHorizontal() ? width : height) * owner.getProgress());
      switch (this.type) {
         case MOVE_NEW:
            Point np = new Point(0, 0);
            switch (this.direction) {
               case LEFT:
                  np.x = width - slideProgress;
                  break;
               case RIGHT:
                  np.x = -width + slideProgress;
                  break;
               case UP:
                  np.y = height - slideProgress;
                  break;
               case DOWN:
                  np.y = -height + slideProgress;
            }

            Composite old = G2Utils.setupAlphaComposite(g, 1.0F - owner.getProgress());
            g.drawImage(current, 0, 0, width, height, null);
            g.setComposite(old);
            g.drawImage(other, np.x, np.y, width, height, null);
            break;
         case MOVE_OLD:
            Point cp = new Point(0, 0);
            switch (this.direction) {
               case LEFT:
                  cp.x = -slideProgress;
                  break;
               case RIGHT:
                  cp.x = slideProgress;
                  break;
               case UP:
                  cp.y = -slideProgress;
                  break;
               case DOWN:
                  cp.y = slideProgress;
            }

            G2Utils.setupAlphaComposite(g, owner.getProgress());
            g.drawImage(other, 0, 0, width, height, null);
            Composite old = G2Utils.setupAlphaComposite(g, 1.0F - owner.getProgress());
            g.drawImage(current, cp.x, cp.y, width, height, null);
            g.setComposite(old);
            break;
         case MOVE_BOTH:
            Point cp = new Point(0, 0);
            Point np = new Point(0, 0);
            switch (this.direction) {
               case LEFT:
                  cp.x = -slideProgress;
                  np.x = width - slideProgress;
                  break;
               case RIGHT:
                  cp.x = slideProgress;
                  np.x = -width + slideProgress;
                  break;
               case UP:
                  cp.y = -slideProgress;
                  np.y = height - slideProgress;
                  break;
               case DOWN:
                  cp.y = slideProgress;
                  np.y = -height + slideProgress;
            }

            g.drawImage(current, cp.x, cp.y, width, height, null);
            g.drawImage(other, np.x, np.y, width, height, null);
      }
   }

   public static WebPanel getPanelSet(TransitionSettings set) {
      DefaultPanelSet p = new DefaultPanelSet(set);
      Direction.setupDefaultComboBox(p, set, DIRECTIONS, Direction.UP);
      Slide.SlideType slideType = Slide.SlideType.getOrDefault(set, Slide.SlideType.MOVE_BOTH);
      if (!Arrays.asList(Slide.SlideType.values()).contains(slideType)) {
         slideType = Slide.SlideType.MOVE_BOTH;
      }

      set.setValue(Slide.SlideType.class.getName(), slideType.name());
      WebComboBox cmbbxSlideType = new WebComboBox(Slide.SlideType.values(), slideType);
      cmbbxSlideType.addItemListener(e -> {
         if (cmbbxSlideType.getSelectedItem() instanceof Slide.SlideType) {
            set.setValue(Slide.SlideType.class.getName(), ((Slide.SlideType)cmbbxSlideType.getSelectedItem()).name());
         }
      });
      p.put(I18N.WORD_TYPE.val(), cmbbxSlideType);
      return p;
   }

   public static enum SlideType {
      RANDOM(I18N.WORD_RANDOM.val()),
      MOVE_NEW(I18N.MSG_MOVE_NEW.val()),
      MOVE_OLD(I18N.MSG_MOVE_OLD.val()),
      MOVE_BOTH(I18N.MSG_MOVE_BOTH.val());

      public final String name;

      private SlideType(String name) {
         this.name = name;
      }

      public Slide.SlideType getSpecific() {
         return this.equals(RANDOM) ? random() : this;
      }

      public static Slide.SlideType getOrDefault(String name, Slide.SlideType _default) {
         try {
            return valueOf(name);
         } catch (Exception var3) {
            return _default;
         }
      }

      public static Slide.SlideType getOrDefault(TransitionSettings set, Slide.SlideType _default) {
         return set.isAllRandom() ? RANDOM : getOrDefault(set.getString(Slide.SlideType.class.getName(), "---"), _default);
      }

      public static Slide.SlideType random(Slide.SlideType... values) {
         try {
            return values[Utils.random(values.length) - 1];
         } catch (Exception var2) {
            return MOVE_BOTH;
         }
      }

      public static Slide.SlideType random() {
         return random(MOVE_NEW, MOVE_OLD, MOVE_BOTH);
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
