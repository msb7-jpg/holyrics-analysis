package com.limagiran.holyrics.model;

import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.Utils;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;

public class ImageHL implements Serializable {
   private static final long serialVersionUID = 12491L;
   public final int id = Long.hashCode(Utils.generateMillisecondId());
   public final byte[] bytes;
   public final byte[] bytesThumb;
   private long version = 1L;

   public ImageHL(byte[] bytes, byte[] bytesThumb) {
      this.bytes = bytes;
      this.bytesThumb = bytesThumb;
   }

   public static ImageHL create(File file, Dimension thumbsSize) throws Exception {
      ImageHL var5;
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         BufferedImage img = ImageIO.read(file);
         BufferedImage bi = Img.resize(img, thumbsSize);
         ImageIO.write(bi, "PNG", baos);
         var5 = new ImageHL(Dir.getBytes(file), baos.toByteArray());
      } finally {
         ByteArrayOutputStream baosx = null;
         BufferedImage imgx = null;
      }

      return var5;
   }

   public static ImageHL create(BufferedImage bi, Dimension thumbsSize) throws Exception {
      ImageHL var5;
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
         BufferedImage biResized = Img.resize(bi, thumbsSize);
         ImageIO.write(bi, "PNG", baos);
         ImageIO.write(biResized, "PNG", baos2);
         var5 = new ImageHL(baos.toByteArray(), baos2.toByteArray());
      } finally {
         ByteArrayOutputStream baosx = null;
         ByteArrayOutputStream baos2x = null;
      }

      return var5;
   }

   public String getId() {
      return "ImageHL" + this.id;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof ImageHL ? ((ImageHL)obj).id == this.id : super.equals(obj);
   }

   @Override
   public int hashCode() {
      return 85 + this.id;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
