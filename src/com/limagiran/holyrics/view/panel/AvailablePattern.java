package com.limagiran.holyrics.view.panel;

import com.limagiran.holyrics.swing.SelectablePanel;
import com.limagiran.holyrics.swing.WebDecoratedImageSelectableCustom;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

public class AvailablePattern extends WebDecoratedImageSelectableCustom implements SelectablePanel {
   public static final String URL = "/com/limagiran/holyrics/model/theme/img/PATTERN%02d.png";
   public static final Dimension SIZE = new Dimension(320, 180);
   public final String url;
   public final BufferedImage image;

   public static List<AvailablePattern> getAllAvailablePatterns() {
      return IntStream.rangeClosed(1, 14).mapToObj(AvailablePattern::load).filter(Objects::nonNull).collect(Collectors.toList());
   }

   private static AvailablePattern load(int index) {
      try {
         return new AvailablePattern(String.format("/com/limagiran/holyrics/model/theme/img/PATTERN%02d.png", index));
      } catch (Exception var2) {
         return null;
      }
   }

   public AvailablePattern(String url) throws Exception {
      this.url = url;
      this.image = ImageIO.read(AvailablePattern.class.getResource(url));
      this.init();
   }

   private void init() {
      BufferedImage bi = new BufferedImage(SIZE.width, SIZE.height, 2);
      Graphics2D g2 = bi.createGraphics();
      g2.setPaint(new TexturePaint(this.image, new Rectangle(0, 0, this.image.getWidth(), this.image.getHeight())));
      g2.fill(new Rectangle(0, 0, SIZE.width, SIZE.height));
      g2.dispose();
      this.setImage(bi);
      this.setMaximumSize(SIZE);
      this.setMinimumSize(SIZE);
      this.setPreferredSize(SIZE);
   }

   public WebDecoratedImageSelectableCustom getPanel() {
      return this;
   }

   @Override
   public String getId() {
      return "Pattern" + this.url;
   }
}
