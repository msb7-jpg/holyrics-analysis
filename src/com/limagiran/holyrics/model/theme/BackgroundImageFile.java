package com.limagiran.holyrics.model.theme;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.Values;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.media.Media;
import javax.imageio.ImageIO;

public class BackgroundImageFile extends Background implements Serializable {
   private static final long serialVersionUID = 12491L;
   private transient BufferedImage thumbs;
   public final transient Presentation presentation;
   public final File file;
   private static final Dimension THUMB_SIZE = new Dimension(320, 180);
   private long version = 1L;

   public BackgroundImageFile(File file) {
      this.file = file;
      BufferedImage bi = this.getThumbs(THUMB_SIZE);
      bi = null;
      this.presentation = Presentation.create(
         new MusicController.Paragraph(file.getName()),
         Theme.create().setFontColor(Values.COLOR_TRANSPARENT).setTransparencyColor(Color.BLACK).setBackground(this)
      );
   }

   public final BufferedImage getThumbs(Dimension d) {
      return BackgroundBufferedThumbImageCache.getImageCache(this, d, () -> {
         try {
            if (this.thumbs == null) {
               this.thumbs = Img.resize(this.getImage(), THUMB_SIZE);
            }

            return Img.resize(this.thumbs, d);
         } catch (Exception var3) {
            return Img.createBlank(Color.BLACK, d);
         }
      });
   }

   @Override
   public final BufferedImage getImage() {
      return BackgroundBufferedImageCache.getImageCache(this, () -> {
         try {
            BufferedImage _return = ImageIO.read(this.file);
            return _return != null ? _return : Images.imageNotFound();
         } catch (Exception var2) {
            return Images.imageNotFound();
         }
      });
   }

   @Override
   public Media getVideo() throws Exception {
      return null;
   }

   @Override
   public boolean isVideo() {
      return false;
   }

   @Override
   public Background.EnumBackgroundType[] getTypes() {
      return new Background.EnumBackgroundType[]{Background.EnumBackgroundType.ADJUST};
   }

   @Override
   public Background.EnumBackgroundType getDefaultType() {
      return Background.EnumBackgroundType.ADJUST;
   }

   @Override
   public String getId() {
      return "ImageFile" + this.file.getAbsolutePath();
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
