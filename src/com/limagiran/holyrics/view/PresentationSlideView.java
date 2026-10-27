package com.limagiran.holyrics.view;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.VideoUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;

public class PresentationSlideView extends JLabel {
   private Theme theme;
   private MusicController.Paragraph text;
   private PresentationSlideView.EnumScreenType screenType;
   private boolean paintBorder;

   public PresentationSlideView() {
      this(Theme.BLACK_SCREEN, new MusicController.Paragraph(), PresentationSlideView.EnumScreenType.DISPLAY_MONITOR, true);
   }

   public PresentationSlideView(Theme theme, MusicController.Paragraph text, PresentationSlideView.EnumScreenType screenType, boolean paintBorder) {
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            PresentationSlideView.this.repaint();
         }
      });
      this.theme = theme;
      this.text = text;
      this.screenType = screenType;
      this.paintBorder = paintBorder;
   }

   public PresentationSlideView(Theme theme, MusicController.Paragraph text, Dimension screenType) {
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            PresentationSlideView.this.repaint();
         }
      });
      this.theme = theme;
      this.text = text;
      PresentationSlideView.EnumScreenType.CUSTOM.setWidth((double)screenType.width);
      PresentationSlideView.EnumScreenType.CUSTOM.setHeight((double)screenType.height);
      this.screenType = PresentationSlideView.EnumScreenType.CUSTOM;
   }

   public static PresentationSlideView.Factory builder() {
      return new PresentationSlideView.Factory();
   }

   @Override
   protected void paintComponent(Graphics g) {
      Dimension d = this.getSize();
      if (d.width > 0 && d.height > 0) {
         Rectangle rect = this.screenType.getDrawableArea(d);
         Graphics2D g2 = (Graphics2D)g.create(rect.x, rect.y, rect.width, rect.height);
         BufferedImage bi = PresentationSlideUtils.createImage(null, this.text, this.theme, rect.getSize(), true);
         g2.drawImage(bi, 0, 0, null);
         bi = null;
         BooleanOptional.runIfTrue(this.paintBorder, () -> {
            g2.setColor(Color.GRAY);
            g2.drawRect(0, 0, --rect.width, --rect.height);
         });
      }
   }

   public void set(Theme theme, MusicController.Paragraph text, PresentationSlideView.EnumScreenType screenType) {
      this.theme = theme;
      this.text = text;
      this.screenType = screenType;
      this.repaint();
   }

   public void set(Theme theme, MusicController.Paragraph text, Dimension screenType) {
      this.screenType = PresentationSlideView.EnumScreenType.CUSTOM;
      PresentationSlideView.EnumScreenType.CUSTOM.setWidth((double)screenType.width);
      PresentationSlideView.EnumScreenType.CUSTOM.setHeight((double)screenType.height);
      this.set(theme, text, this.screenType);
   }

   public void setText(MusicController.Paragraph text) {
      this.set(this.theme, text, this.screenType);
   }

   @Override
   public void setText(String text) {
      this.set(this.theme, new MusicController.Paragraph(text), this.screenType);
   }

   public void setTheme(Theme theme) {
      this.set(theme, this.text, this.screenType);
   }

   public void setScreenType(PresentationSlideView.EnumScreenType screenType) {
      this.set(this.theme, this.text, screenType);
   }

   public void setScreenType(Dimension screenType) {
      this.set(this.theme, this.text, screenType);
   }

   public Theme getTheme() {
      return this.theme;
   }

   public static enum EnumScreenType {
      WIDESCREEN("Widescreen", 16.0, 9.0),
      STANDARD("Standard", 4.0, 3.0),
      YOUR_DISPLAY(I18N.MSG_YOUR_DISPLAY.val(), 1.0, 1.0),
      DISPLAY_MONITOR(I18N.MSG_DISPLAY_MONITOR.val(), 1.0, 1.0),
      EXTEND(I18N.WORD_EXTEND.val(), 1.0, 1.0),
      CUSTOM(I18N.WORD_CUSTOM.val(), 1.0, 1.0);

      public final String name;
      private double width;
      private double height;

      private EnumScreenType(String name, double width, double height) {
         this.name = name;
         this.width = width;
         this.height = height;
      }

      public double getWidth() {
         switch (this) {
            case YOUR_DISPLAY:
               return (double)VideoUtils.getSizeDisplay().width;
            case DISPLAY_MONITOR:
               return (double)VideoUtils.getSizeMonitorDisplay().width;
            default:
               return this.width;
         }
      }

      public Dimension getSize() {
         return new Dimension((int)this.getWidth(), (int)this.getHeight());
      }

      public void setWidth(double width) {
         if (this.equals(CUSTOM) && width > 0.0) {
            this.width = width;
         }
      }

      public double getHeight() {
         switch (this) {
            case YOUR_DISPLAY:
               return (double)VideoUtils.getSizeDisplay().height;
            case DISPLAY_MONITOR:
               return (double)VideoUtils.getSizeMonitorDisplay().height;
            default:
               return this.height;
         }
      }

      public void setHeight(double height) {
         if (this.equals(CUSTOM) && height > 0.0) {
            this.height = height;
         }
      }

      public void set(double width, double height) {
         this.setWidth(width);
         this.setHeight(height);
      }

      public void set(Dimension d) {
         this.set((double)d.width, (double)d.height);
      }

      @Override
      public String toString() {
         return this.name;
      }

      private Rectangle getDrawableArea(Dimension size) {
         if (this.equals(EXTEND)) {
            return new Rectangle(size);
         } else {
            Rectangle area = new Rectangle(size);
            if (size.getWidth() / size.getHeight() < this.getWidth() / this.getHeight()) {
               area.height = (int)(size.getWidth() * this.getHeight() / this.getWidth());
               area.y = (size.height - area.height) / 2;
            } else {
               area.width = (int)(size.getHeight() * this.getWidth() / this.getHeight());
               area.x = (size.width - area.width) / 2;
            }

            return area;
         }
      }
   }

   public static enum EnumSlideType {
      TITLE,
      LYRICS,
      COUNTDOWN;

      public boolean isTitle() {
         return this.equals(TITLE);
      }

      public boolean isLyrics() {
         return this.equals(LYRICS);
      }

      public boolean isCountdown() {
         return this.equals(COUNTDOWN);
      }
   }

   public static class Factory {
      private Theme theme = Theme.BLACK_SCREEN;
      private MusicController.Paragraph text = new MusicController.Paragraph();
      private PresentationSlideView.EnumScreenType screenType = PresentationSlideView.EnumScreenType.WIDESCREEN;
      private boolean paintBorder = true;

      private Factory() {
      }

      public PresentationSlideView getSlideView() {
         return new PresentationSlideView(this.theme, this.text, this.screenType, this.paintBorder);
      }

      public PresentationSlideView.Factory theme(Theme theme) {
         this.theme = theme;
         return this;
      }

      public PresentationSlideView.Factory text(MusicController.Paragraph text) {
         this.text = text;
         return this;
      }

      public PresentationSlideView.Factory screenType(PresentationSlideView.EnumScreenType screenType) {
         this.screenType = screenType;
         return this;
      }

      public PresentationSlideView.Factory paintBorder(boolean paintBorder) {
         this.paintBorder = paintBorder;
         return this;
      }

      public PresentationSlideView.Factory screenType(Dimension size) {
         PresentationSlideView.EnumScreenType.CUSTOM.setWidth((double)size.width);
         PresentationSlideView.EnumScreenType.CUSTOM.setHeight((double)size.height);
         this.screenType(PresentationSlideView.EnumScreenType.CUSTOM);
         return this;
      }
   }
}
