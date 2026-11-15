package com.limagiran.holyrics.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.xml.bind.DatatypeConverter;

public class Img {
   public static BufferedImage resize(BufferedImage img, int newW, int newH) {
      return resize(img, newW, newH, true);
   }

   public static BufferedImage resize(BufferedImage img, int newW, int newH, boolean quality) {
      int type = img.getTransparency() == 1 ? 1 : 2;
      BufferedImage _return = img;

      BufferedImage h;
      try {
         newW = Math.max(newW, 1);
         newH = Math.max(newH, 1);
         int w = quality && newW < img.getWidth() ? img.getWidth() : newW;
         int hx = quality && newH < img.getHeight() ? img.getHeight() : newH;
         boolean bigToSmallW = img.getWidth() > newW;
         boolean bigToSmallH = img.getHeight() > newH;

         BufferedImage tmp;
         do {
            if (quality && w != newW) {
               w = bigToSmallW ? w / 2 : w * 2;
               w = bigToSmallW ? Math.max(w, newW) : Math.min(w, newW);
            }

            if (quality && hx != newH) {
               int var24 = bigToSmallH ? hx / 2 : hx * 2;
               hx = bigToSmallH ? Math.max(var24, newH) : Math.min(var24, newH);
            }

            tmp = new BufferedImage(w, hx, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, quality ? RenderingHints.VALUE_RENDER_QUALITY : RenderingHints.VALUE_RENDER_SPEED);
            g2.drawImage(_return, 0, 0, w, hx, null);
            g2.dispose();
            _return = tmp;
         } while (w != newW || hx != newH);

         return tmp;
      } catch (Exception var15) {
         h = img;
      } finally {
         BufferedImage var17 = null;
         _return = null;
         BufferedImage tmp = null;
      }

      return h;
   }

   public static BufferedImage resize(BufferedImage img, double ratio) {
      return resize(img, (int)((double)img.getWidth() * ratio), (int)((double)img.getHeight() * ratio));
   }

   public static BufferedImage resize(BufferedImage img, double ratio, boolean quality) {
      return resize(img, (int)((double)img.getWidth() * ratio), (int)((double)img.getHeight() * ratio), quality);
   }

   public static BufferedImage resize(BufferedImage img, Dimension max, boolean quality) {
      return (double)img.getWidth() / (double)img.getHeight() > max.getWidth() / max.getHeight()
         ? resize(img, max.getWidth() / (double)img.getWidth(), quality)
         : resize(img, max.getHeight() / (double)img.getHeight(), quality);
   }

   public static BufferedImage resize(BufferedImage img, Dimension max) {
      return resize(img, max, true);
   }

   public static BufferedImage decodeToImage(String image64) {
      byte[] imageByte = DatatypeConverter.parseBase64Binary(image64);

      try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {
         return ImageIO.read(bis);
      } catch (Exception var16) {
         return new BufferedImage(1, 1, 2);
      }
   }

   public static String encodeToString(File file, String type) throws IllegalArgumentException, Exception {
      if (!new ArrayList<>(Arrays.asList(ImageIO.getWriterFormatNames())).contains(type)) {
         throw new IllegalArgumentException("formato da codificação inválido.");
      } else {
         BufferedImage img = ImageIO.read(file);

         String var5;
         try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, type, baos);
            var5 = DatatypeConverter.printBase64Binary(baos.toByteArray());
         } finally {
            img.flush();
         }

         return var5;
      }
   }

   public static String encodeToString(BufferedImage img, String type) throws IllegalArgumentException, Exception {
      if (!new ArrayList<>(Arrays.asList(ImageIO.getWriterFormatNames())).contains(type)) {
         throw new IllegalArgumentException("formato da codificação inválido.");
      } else {
         String var4;
         try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(img, type, bos);
            var4 = DatatypeConverter.printBase64Binary(bos.toByteArray());
         }

         return var4;
      }
   }

   public static BufferedImage turn(BufferedImage bi, double angle) {
      if ((angle = angle % 360.0) < 0.0) {
         angle += 360.0;
      }

      AffineTransform tx = new AffineTransform();
      tx.rotate(Math.toRadians(angle), (double)bi.getWidth() / 2.0, (double)bi.getHeight() / 2.0);
      double ytrans;
      double xtrans;
      if (angle <= 90.0) {
         xtrans = tx.transform(new Double(0.0, (double)bi.getHeight()), null).getX();
         ytrans = tx.transform(new Double(0.0, 0.0), null).getY();
      } else if (angle <= 180.0) {
         xtrans = tx.transform(new Double((double)bi.getWidth(), (double)bi.getHeight()), null).getX();
         ytrans = tx.transform(new Double(0.0, (double)bi.getHeight()), null).getY();
      } else if (angle <= 270.0) {
         xtrans = tx.transform(new Double((double)bi.getWidth(), 0.0), null).getX();
         ytrans = tx.transform(new Double((double)bi.getWidth(), (double)bi.getHeight()), null).getY();
      } else {
         xtrans = tx.transform(new Double(0.0, 0.0), null).getX();
         ytrans = tx.transform(new Double((double)bi.getWidth(), 0.0), null).getY();
      }

      AffineTransform translationTransform = new AffineTransform();
      translationTransform.translate(-xtrans, -ytrans);
      tx.preConcatenate(translationTransform);
      return new AffineTransformOp(tx, 2).filter(bi, null);
   }

   public static BufferedImage toBufferedImage2(Image img, boolean isJPG) {
      BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), isJPG ? 1 : 2);
      Graphics g = bi.createGraphics();
      g.drawImage(img, 0, 0, null);
      g.dispose();
      return bi;
   }

   public static BufferedImage toBufferedImage2(Image img) {
      return toBufferedImage2(img, false);
   }

   public static BufferedImage getCircle(BufferedImage image, int x, int y, int w, int h) {
      try {
         BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
         Graphics2D g2d = bi.createGraphics();
         g2d.setClip(new java.awt.geom.Ellipse2D.Double((double)x, (double)y, (double)w, (double)h));
         g2d.drawImage(image, 0, 0, null);
         g2d.dispose();
         return bi.getSubimage(x, y, w, h);
      } catch (Exception var7) {
         return image;
      }
   }

   public static BufferedImage opacity(Image img, float f) {
      BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
      Graphics2D g = bi.createGraphics();
      g.setComposite(AlphaComposite.getInstance(3, Utils.range(f, 0.0F, 1.0F)));
      g.drawImage(img, 0, 0, null);
      g.dispose();
      return bi;
   }

   public static BufferedImage applyTransparency(BufferedImage image, Color keyColor) {
      try {
         int w = image.getWidth(null);
         int h = image.getHeight(null);
         int[] pxls = getPixels(image);

         for (int i = 0; i < pxls.length; i++) {
            if (pxls[i] == keyColor.getRGB()) {
               pxls[i] = 16777215;
            }
         }

         return toBufferedImage2(getImage(pxls, w, h));
      } catch (NullPointerException | InterruptedException var6) {
         return toBufferedImage2(image);
      }
   }

   private static Image getImage(int[] pixels, int w, int h) {
      MemoryImageSource source = new MemoryImageSource(w, h, pixels, 0, w);
      return Toolkit.getDefaultToolkit().createImage(source);
   }

   private static int[] getPixels(Image image) throws InterruptedException {
      int w = image.getWidth(null);
      int h = image.getHeight(null);
      int[] pix = new int[w * h];
      PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pix, 0, w);
      pg.grabPixels();
      return pix;
   }

   public static BufferedImage clip(BufferedImage img, int x, int y, int w, int h) {
      try {
         BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
         Graphics2D g2d = bi.createGraphics();
         g2d.setClip(x, y, w, h);
         g2d.drawImage(img, 0, 0, null);
         g2d.dispose();
         return bi.getSubimage(x, y, w, h);
      } catch (Exception var7) {
         return img;
      }
   }

   public static Color getColor(Image image, int x, int y) {
      try {
         int index = y * image.getWidth(null) + x;
         return new Color(getPixels(image)[index], true);
      } catch (Exception var4) {
         return null;
      }
   }

   public static void applyTexture(BufferedImage bi, JLabel l) {
      try {
         BufferedImage bi2 = new BufferedImage(l.getWidth(), l.getHeight(), 2);
         Graphics2D g2 = (Graphics2D)bi2.getGraphics();
         g2.setPaint(new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight())));
         g2.fill(new Rectangle(0, 0, l.getWidth(), l.getHeight()));
         g2.dispose();
         l.setIcon(new ImageIcon(bi2));
      } catch (Exception var4) {
      }
   }

   public static void applyTexture(BufferedImage bi, Component c) {
      Graphics2D g2 = (Graphics2D)c.getGraphics();
      g2.setPaint(new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight())));
      g2.fill(new Rectangle(0, 0, c.getWidth(), c.getHeight()));
      g2.dispose();
   }

   public static boolean applyTransparencyAndSave(File in, File out, Color c) {
      try {
         ImageIO.write(applyTransparency(ImageIO.read(in), c), "PNG", out);
         return true;
      } catch (Exception var4) {
         return false;
      }
   }

   public static String createBlank64(Color color, int w, int h) {
      try {
         return encodeToString(createBlank(color, w, h), "PNG");
      } catch (Exception var4) {
         return "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";
      }
   }

   public static BufferedImage createBlank(Color color, int w, int h) {
      BufferedImage bi = new BufferedImage(w, h, 2);
      Graphics g = bi.getGraphics();
      g.setColor(color);
      g.fillRect(0, 0, w, h);
      g.dispose();
      return bi;
   }

   public static BufferedImage createBlank(Color color, Dimension size) {
      return createBlank(color, size.width, size.height);
   }

   public static byte[] getBytes(BufferedImage img) throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(img, "PNG", baos);
      return baos.toByteArray();
   }

   public static byte[] getBytesJPG(BufferedImage bi, float f) throws Exception {
      try {
         JPEGImageWriteParam params = new JPEGImageWriteParam(null);
         params.setCompressionMode(2);
         params.setCompressionQuality(Utils.range(f, 0.0F, 1.0F));
         ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         writer.setOutput(new MemoryCacheImageOutputStream(baos));
         writer.write(null, new IIOImage(toBufferedImage2(bi, true), null, null), params);
         return baos.toByteArray();
      } catch (Exception var5) {
         throw var5;
      }
   }

   public static Image changeColor(Image image, Color c1, Color c2) {
      try {
         int w = image.getWidth(null);
         int h = image.getHeight(null);
         int[] pxls = getPixels(image);

         for (int i = 0; i < pxls.length; i++) {
            Color c = new Color(pxls[i], true);
            if (c.getRGB() == c1.getRGB()) {
               pxls[i] = c.getAlpha() << 24 | c2.getRed() << 16 | c2.getGreen() << 8 | c2.getBlue();
            }
         }

         return getImage(pxls, w, h);
      } catch (Exception var8) {
         return image;
      }
   }

   public static BufferedImage turn(BufferedImage bi, double angle, boolean translate) {
      if ((angle = angle % 360.0) < 0.0) {
         angle += 360.0;
      }

      AffineTransform tx = new AffineTransform();
      tx.rotate(Math.toRadians(angle), (double)bi.getWidth() / 2.0, (double)bi.getHeight() / 2.0);
      if (translate) {
         double ytrans;
         double xtrans;
         if (angle <= 90.0) {
            xtrans = tx.transform(new Double(0.0, (double)bi.getHeight()), null).getX();
            ytrans = tx.transform(new Double(0.0, 0.0), null).getY();
         } else if (angle <= 180.0) {
            xtrans = tx.transform(new Double((double)bi.getWidth(), (double)bi.getHeight()), null).getX();
            ytrans = tx.transform(new Double(0.0, (double)bi.getHeight()), null).getY();
         } else if (angle <= 270.0) {
            xtrans = tx.transform(new Double((double)bi.getWidth(), 0.0), null).getX();
            ytrans = tx.transform(new Double((double)bi.getWidth(), (double)bi.getHeight()), null).getY();
         } else {
            xtrans = tx.transform(new Double(0.0, 0.0), null).getX();
            ytrans = tx.transform(new Double((double)bi.getWidth(), 0.0), null).getY();
         }

         AffineTransform translationTransform = new AffineTransform();
         translationTransform.translate(-xtrans, -ytrans);
         tx.preConcatenate(translationTransform);
      }

      return new AffineTransformOp(tx, 3).filter(bi, null);
   }

   public static BufferedImage createTriangle(int area, Dimension size, Color c) {
      return createTriangle(area, size, c, false);
   }

   public static BufferedImage createTriangle(int area, Dimension size, Color c, boolean inverse) {
      BufferedImage bi = new BufferedImage(area, area, 2);
      Graphics2D g2 = bi.createGraphics();
      G2Utils.setupGraphics(g2);
      g2.setColor(c);
      Rectangle r = new Rectangle((area - size.width) / 2, (area - size.height) / 2, size.width, size.height);
      g2.fillPolygon(new int[]{r.x + r.width / 2, r.x, r.x + r.width}, new int[]{r.y, r.y + r.height, r.y + r.height}, 3);
      g2.dispose();
      return inverse ? turn(bi, 180.0, false) : bi;
   }

   public static BufferedImage invertColor(BufferedImage input) {
      try {
         BufferedImage bi = new BufferedImage(input.getWidth(), input.getHeight(), input.getTransparency() == 1 ? 1 : 2);
         Graphics2D g = bi.createGraphics();
         g.drawImage(input, 0, 0, null);

         for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
               int rgba = bi.getRGB(x, y);
               Color col = new Color(rgba, true);
               col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue(), col.getAlpha());
               bi.setRGB(x, y, col.getRGB());
            }
         }

         return bi;
      } catch (Exception var7) {
         return input;
      }
   }

   public static BufferedImage load(byte[] bytes) {
      try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
         return ImageIO.read(bais);
      } catch (Exception var15) {
         return null;
      }
   }

   public static void applyLinearLight(Graphics2D g, Rectangle r, float fadePercent, boolean horizontal) {
      fadePercent = Utils.range(fadePercent, 0.02F, 0.95F) / 2.0F;
      float[] fractions = new float[]{0.0F, 0.5F - fadePercent, 0.5F + fadePercent, 1.0F};
      Color color = Color.BLACK;
      Color[] colors = new Color[]{Values.COLOR_TRANSPARENT, color, color, Values.COLOR_TRANSPARENT};
      LinearGradientPaint paint = new LinearGradientPaint(
         horizontal ? (float)r.getCenterX() : (float)r.x,
         horizontal ? (float)r.y : (float)r.getCenterY(),
         horizontal ? (float)r.getCenterX() : (float)r.getMaxX(),
         horizontal ? (float)r.getMaxY() : (float)r.getCenterY(),
         fractions,
         colors
      );
      g.setPaint(paint);
      Composite c = g.getComposite();
      g.setComposite(AlphaComposite.DstOut);
      g.fillRect(r.x, r.y, r.width, r.height);
      g.setComposite(c);
   }

   public static void applyLinearLight(
      Graphics2D g, BufferedImage bi1, BufferedImage bi2, float progress, float distancePercent, float FadePercent, boolean horizontal, boolean inverse
   ) {
      if (inverse) {
         progress = 1.0F - progress;
         BufferedImage temp = bi1;
         bi1 = bi2;
         bi2 = temp;
      }

      distancePercent = Utils.range(distancePercent, 0.001F, 0.999F);
      int width = Math.max(bi1.getWidth(), bi2.getWidth());
      int height = Math.max(bi1.getHeight(), bi2.getHeight());
      int widthArea = (int)((float)width * distancePercent);
      int heightArea = (int)((float)height * distancePercent);
      int xPosition = (int)((float)(width + widthArea) * progress);
      int yPosition = (int)((float)(height + heightArea) * progress);
      Rectangle r = new Rectangle();
      r.x = horizontal ? 0 : xPosition - widthArea;
      r.y = horizontal ? yPosition - heightArea : 0;
      r.width = horizontal ? width : widthArea;
      r.height = horizontal ? heightArea : height;
      BufferedImage bi = new BufferedImage(width, height, 2);
      Graphics2D g2 = bi.createGraphics();
      int x = horizontal ? 0 : (int)r.getCenterX();
      int y = horizontal ? (int)r.getCenterY() : 0;
      g2.setClip(x, y, width - x, height - y);
      g2.drawImage(bi1, 0, 0, null);
      int w = horizontal ? width : (int)r.getCenterX();
      int h = horizontal ? (int)r.getCenterY() : height;
      g2.setClip(0, 0, w, h);
      g2.drawImage(bi2, 0, 0, null);
      g2.setClip(0, 0, width, height);
      applyLinearLight(g2, r, FadePercent, horizontal);
      g.drawImage(bi, 0, 0, null);
   }

   public static void applyLinearLight(
      Graphics2D g, BufferedImage bi1, BufferedImage bi2, float progress, float distancePercent, float FadePercent, boolean horizontal
   ) {
      applyLinearLight(g, bi1, bi2, progress, distancePercent, FadePercent, horizontal, false);
   }
}
