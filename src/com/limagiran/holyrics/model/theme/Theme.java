package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.control.MyImagesController;
import com.limagiran.holyrics.control.MyVideosController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.ImageHL;
import com.limagiran.holyrics.model.VideoHL;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.ReflectionUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.lgsaveopen.LGSaveOpen;
import com.limagiran.lgsaveopen.SerialMap;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

public class Theme implements Serializable, LGSaveOpen<Theme>, ITheme {
   private static final long serialVersionUID = 12491L;
   public static final double SIZE_DEFAULT = 0.1;
   public static final double SIZE_MIN_VALUE = 0.04;
   public static final double SIZE_MAX_VALUE = 0.4;
   public static final double LINE_SPACING_DEFAULT = 0.3;
   public static final double LINE_SPACING_MIN_VALUE = -0.5;
   public static final double LINE_SPACING_MAX_VALUE = 1.0;
   public static final double OUTLINE_DEFAULT = 0.0;
   public static final double OUTLINE_MIN_VALUE = 0.0;
   public static final double OUTLINE_MAX_VALUE = 0.1;
   public static final double BRIGHTNESS_DEFAULT = 0.0;
   public static final double BRIGHTNESS_MIN_VALUE = 0.0;
   public static final double BRIGHTNESS_MAX_VALUE = 0.2;
   public static final double SHADOW_DEFAULT = 0.0;
   public static final double SHADOW_MIN_VALUE = -0.05;
   public static final double SHADOW_MAX_VALUE = 0.05;
   public static final double[] MARGIN_DEFAULT = new double[]{0.03, 0.03, 0.03, 0.03};
   public static final double MARGIN_MIN_VALUE = 0.0;
   public static final double MARGIN_MAX_VALUE = 0.9;
   public static final int ALIGN_LEFT = -1;
   public static final int ALIGN_CENTER = 0;
   public static final int ALIGN_RIGHT = 1;
   public static final int ALIGN_JUSTIFY = 2;
   public static final int ALIGN_TOP = -1;
   public static final int ALIGN_MIDDLE = 0;
   public static final int ALIGN_BOTTOM = 1;
   public static final Theme BLACK_SCREEN = create().setBackground(new BackgroundColor(new Color(30, 30, 30)));
   public final long id;
   private Background background;
   private float videoRate;
   private final Font[] fonts;
   private double size;
   private double lineSpacing;
   private Color transparencyColor;
   private Color fontColor;
   private int textAlign;
   private int textAlignV;
   private double[] margin;
   private Color outlineColor;
   private double outlineThickness;
   private Color brightnessColor;
   private double brightnessThickness;
   private Color shadowColor;
   private double shadowX;
   private double shadowY;
   private long version = 4L;

   private Theme() {
      this.id = Utils.generateMillisecondId();
      this.background = new BackgroundColor(new Color(33, 33, 33));
      this.videoRate = 1.0F;
      this.fonts = new Font[300];
      this.setFont(new Font("Arial", 0, 8));
      this.size = 0.1;
      this.lineSpacing = 0.3;
      this.transparencyColor = Color.WHITE;
      this.fontColor = new Color(245, 245, 245);
      this.textAlign = 0;
      this.textAlignV = 0;
      this.margin = Arrays.copyOf(MARGIN_DEFAULT, MARGIN_DEFAULT.length);
      this.outlineColor = Color.DARK_GRAY;
      this.outlineThickness = 0.0;
      this.brightnessColor = Color.LIGHT_GRAY;
      this.brightnessThickness = 0.0;
      this.shadowColor = Color.GRAY;
      this.shadowX = 0.0;
      this.shadowY = 0.0;
   }

   public static Theme create() {
      return new Theme();
   }

   public Background getBackground() {
      return this.background;
   }

   public Theme setBackground(Background background) {
      this.background = background;
      return this;
   }

   public float getVideoRate() {
      return this.videoRate;
   }

   public Theme setVideoRate(float videoRate) {
      this.videoRate = Utils.range(videoRate, 0.25F, 4.0F);
      return this;
   }

   public Font getFont() {
      return this.fonts[0];
   }

   public Font getFont(Component c) {
      return this.getFont(c.getHeight(), c);
   }

   public String getFontName() {
      return this.fonts[0].getName();
   }

   public Font getFont(int height, Component c) {
      return this.getFont(height, (Graphics2D)c.getGraphics());
   }

   public Font getFont(int height, Graphics2D g) {
      return G2Utils.getBestFont((int)((double)height * this.size), this.fonts[0], g);
   }

   public final Theme setFont(Font font) {
      for (int i = 0; i < this.fonts.length; i++) {
         this.fonts[i] = font.deriveFont((float)(i + 1));
      }

      return this;
   }

   public Color getFontColor() {
      return this.fontColor;
   }

   public Color getTransparencyColor() {
      return this.isTransparent() ? new Color(0, 0, 0, 0) : this.transparencyColor;
   }

   public Color getTransparencyColorReal() {
      return this.transparencyColor;
   }

   public boolean isTransparent() {
      return this.background instanceof BackgroundColor && ((BackgroundColor)this.background).isTransparent();
   }

   public double getSize() {
      return this.size;
   }

   public Theme setSize(double size) {
      this.size = Utils.range(size, 0.04, 0.4);
      return this;
   }

   public double getLineSpacing() {
      return this.lineSpacing;
   }

   public Theme setLineSpacing(double lineSpacing) {
      this.lineSpacing = Utils.range(lineSpacing, -0.5, 1.0);
      return this;
   }

   public Theme setFontColor(Color fontColor) {
      this.fontColor = fontColor;
      return this;
   }

   public Theme setTransparencyColor(Color transparencyColor) {
      this.transparencyColor = transparencyColor;
      return this;
   }

   public int getTextAlign() {
      return this.textAlign;
   }

   public Theme setTextAlign(int textAlign) {
      this.textAlign = textAlign;
      return this;
   }

   public int getTextAlignV() {
      return this.textAlignV;
   }

   public Theme setTextAlignV(int textAlignV) {
      this.textAlignV = textAlignV;
      return this;
   }

   public double[] getMargins() {
      return this.margin != null ? this.margin : (this.margin = Arrays.copyOf(MARGIN_DEFAULT, MARGIN_DEFAULT.length));
   }

   public Theme setMargin(double[] margin) {
      if (margin.length == 4) {
         this.margin = margin;

         for (int i = 0; i < margin.length; i++) {
            margin[i] = Utils.range(margin[i], 0.0, 0.9);
         }
      }

      return this;
   }

   public Color getOutlineColor() {
      return this.outlineColor;
   }

   public Theme setOutlineColor(Color outlineColor) {
      this.outlineColor = outlineColor;
      return this;
   }

   public double getOutlineThickness() {
      return this.outlineThickness;
   }

   public Theme setOutlineThickness(double outlineThickness) {
      this.outlineThickness = Utils.range(outlineThickness, 0.0, 0.1);
      return this;
   }

   public Color getBrightnessColor() {
      return this.brightnessColor;
   }

   public Theme setBrightnessColor(Color brightnessColor) {
      this.brightnessColor = brightnessColor;
      return this;
   }

   public double getBrightnessThickness() {
      return this.brightnessThickness;
   }

   public Theme setBrightnessThickness(double brightnessThickness) {
      this.brightnessThickness = Utils.range(brightnessThickness, 0.0, 0.2);
      return this;
   }

   public Color getShadowColor() {
      return this.shadowColor;
   }

   public Theme setShadowColor(Color shadowColor) {
      this.shadowColor = shadowColor;
      return this;
   }

   public double getShadowX() {
      return this.shadowX;
   }

   public Theme setShadowX(double shadowX) {
      this.shadowX = Utils.range(shadowX, -0.05, 0.05);
      return this;
   }

   public double getShadowY() {
      return this.shadowY;
   }

   public Theme setShadowY(double shadowY) {
      this.shadowY = Utils.range(shadowY, -0.05, 0.05);
      return this;
   }

   public int getAlignAsSwing() {
      switch (this.textAlign) {
         case -1:
            return 2;
         case 1:
            return 4;
         default:
            return 0;
      }
   }

   public int getAlignVAsSwing() {
      switch (this.textAlignV) {
         case -1:
            return 1;
         case 1:
            return 3;
         default:
            return 0;
      }
   }

   public Theme cloneTheme() {
      return Optional.ofNullable(RWObj.clone(this, Theme.class)).orElse(create());
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof Theme ? ((Theme)o).id == this.id : super.equals(o);
   }

   @Override
   public int hashCode() {
      return 111 + (int)(this.id ^ this.id >>> 32);
   }

   @Override
   public long getHashCode() {
      return (long)Theme.class.getName().hashCode();
   }

   @Override
   public Theme getInstance() {
      return this;
   }

   @Override
   public String getExtension() {
      return ".thf";
   }

   @Override
   public String getFileDesc() {
      return "Theme File (*.thf)";
   }

   public SerialMap toSerialMap(Theme t) throws Exception {
      SerialMap map = new SerialMap(this);
      map.put("main", this);
      if (t.getBackground() instanceof BackgroundMyImage) {
         map.put("fileBytes", MyImagesController.get(((BackgroundMyImage)t.background).id));
      } else if (t.getBackground() instanceof BackgroundVideo) {
         map.put("fileBytes", Dir.getBytes(((BackgroundVideo)t.background).getPath()));
      } else if (t.getBackground() instanceof BackgroundMyVideo) {
         VideoHL video = MyVideosController.get(((BackgroundMyVideo)t.background).id);
         map.put("fileBytes_videoHL", video);
         map.put("fileBytes_mp4", Dir.getBytes(video.getFile()));
      }

      return map;
   }

   public Theme createFromSerialMap(SerialMap sm) throws Exception {
      Theme t = sm.get("main", Theme.class);
      if (t.getBackground() instanceof BackgroundMyImage) {
         MyImagesController.add(sm.get("fileBytes", ImageHL.class));
      } else if (t.getBackground() instanceof BackgroundVideo) {
         Dir.saveBytes(sm.get("fileBytes", byte[].class), ((BackgroundVideo)t.background).getPath());
      } else if (t.getBackground() instanceof BackgroundMyVideo) {
         VideoHL video = sm.get("fileBytes_videoHL", VideoHL.class);
         int i = 1;
         File f = new File(Values.PATH_BACKGROUND_VIDEO + String.format("MY_VIDEO_%03d.mp4", i));

         while (f.exists()) {
            f = new File(Values.PATH_BACKGROUND_VIDEO + String.format("MY_VIDEO_%03d.mp4", ++i));
         }

         if (!ReflectionUtils.setField(video, "fileName", f.getName())) {
            throw new Exception(I18N.MSG_ERROR_INVALID_FILE.val());
         }

         MyVideosController.add(video);
         Dir.saveBytes(sm.get("fileBytes_mp4", byte[].class), video.getFile());
      }

      ReflectionUtils.setField(t, "id", Utils.generateMillisecondId());
      return t;
   }

   public String hash() {
      return new StringBuilder(1024)
         .append(this.id)
         .append(":")
         .append(this.background)
         .append(":")
         .append(this.videoRate)
         .append(":")
         .append(this.fonts[0])
         .append(":")
         .append(this.lineSpacing)
         .append(":")
         .append(this.size)
         .append(":")
         .append(this.getTransparencyColor())
         .append(":")
         .append(this.getTransparencyColorReal())
         .append(":")
         .append(this.fontColor)
         .append(":")
         .append(this.textAlign)
         .append(":")
         .append(this.textAlignV)
         .append(":")
         .append(Arrays.toString(this.margin))
         .append(":")
         .append(this.outlineColor)
         .append(":")
         .append(this.outlineThickness)
         .append(":")
         .append(this.brightnessColor)
         .append(":")
         .append(this.brightnessThickness)
         .append(":")
         .append(this.shadowColor)
         .append(":")
         .append(this.shadowX)
         .append(":")
         .append(this.shadowY)
         .append(":")
         .append(this.background.getHash())
         .append(":")
         .toString();
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      if (this.version < 2L) {
         this.transparencyColor = Color.WHITE;
         this.version = 2L;
      }

      if (this.version < 3L) {
         this.textAlignV = 0;
         this.margin = Arrays.copyOf(MARGIN_DEFAULT, MARGIN_DEFAULT.length);
         this.version = 3L;
      }

      if (this.version < 4L) {
         this.videoRate = 1.0F;
         this.version = 4L;
      }

      if (this.version > 4L) {
         this.version = 4L;
      }
   }
}
