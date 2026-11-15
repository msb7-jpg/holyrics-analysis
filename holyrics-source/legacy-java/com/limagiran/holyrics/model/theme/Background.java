package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.FFMPEG;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.Utils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import javafx.scene.media.Media;

public abstract class Background implements Serializable {
   private static final long serialVersionUID = 12491L;
   private static final LinkedHashMap<String, BufferedImage> MAP = new LinkedHashMap<>();
   public static final int OPACITY_DEFAULT = 100;
   public static final int OPACITY_MIN_VALUE = 0;
   public static final int OPACITY_MAX_VALUE = 100;
   private Background.EnumBackgroundType backgroundType = this.getDefaultType();
   private int opacity = 100;
   private long version = 1L;

   public Background.EnumBackgroundType getType() {
      return this.backgroundType;
   }

   public Background setType(Background.EnumBackgroundType backgroundType) {
      this.backgroundType = backgroundType;
      return this;
   }

   public int getOpacity() {
      return this.opacity;
   }

   public double getOpacityPercent() {
      return (double)this.opacity / 100.0;
   }

   public Background setOpacity(int opacity) {
      this.opacity = Utils.range(opacity, 0, 100);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof Background ? ((Background)obj).getId().equals(this.getId()) : super.equals(obj);
   }

   @Override
   public int hashCode() {
      return this.getId().hashCode();
   }

   public abstract String getId();

   public abstract BufferedImage getImage();

   public abstract Media getVideo() throws Exception;

   public abstract boolean isVideo();

   public abstract Background.EnumBackgroundType[] getTypes();

   public abstract Background.EnumBackgroundType getDefaultType();

   public String getHash() {
      return this.getId().hashCode() + ":" + this.backgroundType.name + ":" + this.opacity;
   }

   public final synchronized void paintBackground(Graphics2D g, Dimension d, Color color) {
      Composite c = g.getComposite();

      try {
         g.setColor(color);
         g.fillRect(0, 0, d.width, d.height);
         g.setComposite(AlphaComposite.getInstance(3, (float)this.getOpacity() / 100.0F));
         if (!(this instanceof BackgroundColor)) {
            BufferedImage bi = this.getImage();
            switch (this.getType()) {
               case SIDE_BY_SIDE:
                  TexturePaint texture = new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
                  g.setPaint(texture);
                  texture = null;
                  g.fill(new Rectangle(d));
                  return;
               case ADJUST:
                  Rectangle r = new Rectangle();
                  double ratio;
                  if ((double)bi.getWidth() / (double)bi.getHeight() > d.getWidth() / d.getHeight()) {
                     ratio = d.getWidth() / (double)bi.getWidth();
                  } else {
                     ratio = d.getHeight() / (double)bi.getHeight();
                  }

                  r.width = (int)((double)bi.getWidth() * ratio);
                  r.height = (int)((double)bi.getHeight() * ratio);
                  r.x = (d.width - r.width) / 2;
                  r.y = (d.height - r.height) / 2;
                  g.drawImage(this.getCache(bi, r.getSize()), r.x, r.y, null);
                  return;
               case CENTER:
                  g.drawImage(bi, (d.width - bi.getWidth()) / 2, (d.height - bi.getHeight()) / 2, null);
                  return;
               case EXTEND:
                  g.drawImage(this.getCache(bi, d), 0, 0, null);
                  return;
               case FILL:
                  double ratio = d.getWidth() / d.getHeight();
                  Rectangle r;
                  if ((double)bi.getWidth() / (double)bi.getHeight() > ratio) {
                     int w = (int)((double)bi.getWidth() * (d.getHeight() / (double)bi.getHeight()));
                     r = new Rectangle((d.width - w) / 2, 0, w, d.height);
                  } else {
                     int h = (int)((double)bi.getHeight() * (d.getWidth() / (double)bi.getWidth()));
                     r = new Rectangle(0, (d.height - h) / 2, d.width, h);
                  }

                  g.drawImage(this.getCache(bi, r.getSize()), r.x, r.y, null);
                  return;
               default:
                  return;
            }
         }

         g.setColor(((BackgroundColor)this).color);
         g.fillRect(0, 0, d.width, d.height);
      } catch (Exception var14) {
         return;
      } finally {
         BufferedImage bi = null;
         g.setComposite(c);
      }
   }

   private BufferedImage getCache(BufferedImage bi, Dimension d) {
      String id = this.getId() + d;
      BufferedImage _return = MAP.get(id);
      if (_return == null) {
         while (!MAP.isEmpty() && size(MAP) > 17000000L) {
            BufferedImage remove = MAP.remove(MAP.keySet().iterator().next());
            if (remove != null) {
               Utils.startThreadDaemon(remove::flush);
               remove = null;
            }
         }

         _return = Img.resize(bi, d.width, d.height);
      }

      MAP.remove(id);
      MAP.put(id, _return);
      return _return;
   }

   private static long size(LinkedHashMap<String, BufferedImage> map) {
      return map.values().stream().map(i -> (long)(i.getWidth() * i.getHeight()) * 4L).reduce(0L, Long::sum);
   }

   public static File createLongPathVideo(File path) {
      return new File(path.getParent(), "long-" + path.getName());
   }

   public static boolean checkLongVideo(File path, String name) {
      try {
         File fileLong = createLongPathVideo(path);
         if (!fileLong.exists()) {
            return false;
         } else {
            String length = String.valueOf(fileLong.length());
            String key = "bgv_long_hash-" + name;
            if (MyProperties.getFlag(key, "0").equals(length)) {
               return true;
            } else {
               String longHash = Settings.SET.get(key, "");
               boolean flag = Dir.getHash(fileLong).equals(longHash);
               if (flag) {
                  MyProperties.setFlag(key, length);
               }

               return flag;
            }
         }
      } catch (Exception var7) {
         return false;
      }
   }

   public static boolean createLongVideo(File path, String name) {
      try {
         if (new FFMPEG().enlarge(path, createLongPathVideo(path), 50)) {
            Settings.SET.put("bgv_long_hash-" + name, Dir.getHash(createLongPathVideo(path)));
            return true;
         }
      } catch (Exception var3) {
      }

      return false;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }

   public static enum EnumBackgroundType {
      FILL(I18N.WORD_FILL.val()),
      EXTEND(I18N.WORD_EXTEND.val()),
      ADJUST(I18N.WORD_ADJUST.val()),
      SIDE_BY_SIDE(I18N.WORD_SIDE_BY_SIDE.val()),
      CENTER(I18N.WORD_CENTER.val());

      private static final long serialVersionUID = 12491L;
      private final String name;

      private EnumBackgroundType(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
