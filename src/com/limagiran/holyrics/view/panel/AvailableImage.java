package com.limagiran.holyrics.view.panel;

import com.limagiran.holyrics.swing.SelectablePanel;
import com.limagiran.holyrics.swing.WebDecoratedImageSelectableCustom;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

public class AvailableImage extends WebDecoratedImageSelectableCustom implements SelectablePanel {
   public static final String URL = "/com/limagiran/holyrics/model/theme/img/IMAGE%02d.jpg";
   public static final String URL_THUMBS = "/com/limagiran/holyrics/model/theme/img/IMAGE%02d_min.jpg";
   public static final Dimension SIZE = new Dimension(320, 180);
   public final String url;
   public final BufferedImage image;

   public static List<AvailableImage> getAllAvailableImages() {
      return IntStream.rangeClosed(1, 20).mapToObj(AvailableImage::load).filter(Objects::nonNull).collect(Collectors.toList());
   }

   private static AvailableImage load(int index) {
      try {
         return new AvailableImage(
            String.format("/com/limagiran/holyrics/model/theme/img/IMAGE%02d.jpg", index),
            String.format("/com/limagiran/holyrics/model/theme/img/IMAGE%02d_min.jpg", index)
         );
      } catch (Exception var2) {
         return null;
      }
   }

   public AvailableImage(String url, String urlThumbs) throws Exception {
      this.url = url;
      this.image = ImageIO.read(AvailableImage.class.getResource(urlThumbs));
      this.init();
   }

   private void init() {
      this.setImage(this.image);
      this.setMaximumSize(SIZE);
      this.setMinimumSize(SIZE);
      this.setPreferredSize(SIZE);
      this.setSize(SIZE);
      this.revalidate();
   }

   public WebDecoratedImageSelectableCustom getPanel() {
      return this;
   }

   @Override
   public String getId() {
      return "Image" + this.url;
   }
}
