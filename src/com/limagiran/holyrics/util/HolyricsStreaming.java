package com.limagiran.holyrics.util;

import com.limagiran.holyrics.model.AlertSettings;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.view.CommunicationPanel;
import com.limagiran.holyrics.webservice.HolyricsWS;
import com.limagiran.holyrics.webservice.Pack;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.xml.bind.DatatypeConverter;

public class HolyricsStreaming {
   public static final Dimension SIZE_WIDESCREEN = new Dimension(1920, 1080);
   public static final Dimension SIZE_STANDARD = new Dimension(1200, 900);
   public static final Dimension[] SIZES = new Dimension[]{SIZE_WIDESCREEN, SIZE_STANDARD};
   public static final String[] DEFAULT_STREAMING = new String[]{createDefaultStreaming(true), createDefaultStreaming(false)};
   private static final String[] STREAMING = new String[SIZES.length];
   private static final String[] STREAMING_HASH = new String[SIZES.length];
   private static final boolean[] DEMAND = new boolean[]{false, false};
   private static final ScreenDeviceSettings.StageViewSettings SVS = Settings.SET.getScreenDeviceSettings(-1).getStageViewSettings();
   private static HolyricsStreaming.HolyricsStreamRenderer RENDERER;
   private static long UPDATE = 0L;
   private static final HolyricsStreaming.HolyricsStreamRenderer CP_RENDERER = new HolyricsStreaming.HolyricsStreamRenderer() {
      @Override
      public BufferedImage renderer(Dimension size) {
         return CommunicationPanel.getPresentationImage(size);
      }

      @Override
      public String getHash(Dimension size) {
         return CommunicationPanel.getHash(size);
      }
   };
   private static final HolyricsStreaming.HolyricsStreamRenderer EMPTY_RENDERER = new HolyricsStreaming.HolyricsStreamRenderer() {
      @Override
      public BufferedImage renderer(Dimension size) {
         return Img.createBlank(Color.BLACK, size);
      }

      @Override
      public String getHash(Dimension size) {
         return "empty-" + size;
      }
   };

   private static void startThread() {
      Utils.startThreadDaemon(() -> {
         boolean flag = false;

         while (true) {
            try {
               if (isDemand() && HolyricsWS.isOpenFast()) {
                  HolyricsStreaming.HolyricsStreamRenderer renderer = RENDERER;
                  boolean isCP = SVS.isEnabled() && CommunicationPanel.isDisplayPresentation();
                  if (isCP && (renderer == null || CommunicationPanel.isPresentationDisplayAhead())) {
                     flag = true;
                     updateStreaming(CP_RENDERER);
                  } else if (renderer != null) {
                     flag = true;
                     updateStreaming(renderer);
                  } else if (SVS.isEnabled() && CommunicationPanel.isDisplayAlert()) {
                     flag = true;
                     updateStreaming(EMPTY_RENDERER);
                  } else {
                     Arrays.fill(STREAMING, null);
                     Arrays.fill(STREAMING_HASH, null);
                     if (flag) {
                        flag = false;
                        UPDATE = System.currentTimeMillis();
                     }
                  }
               }
            } catch (Exception var6) {
            } finally {
               Utils.sleep(isDemand() ? 100L : 2500L);
            }
         }
      });
   }

   public static synchronized void setRenderer(HolyricsStreaming.HolyricsStreamRenderer renderer) {
      RENDERER = null;
      RENDERER = renderer;
   }

   public static synchronized HolyricsStreaming.HolyricsStreamRenderer getRenderer() {
      return RENDERER;
   }

   private static int getIndex(boolean widescreen) {
      return widescreen ? 0 : 1;
   }

   public static synchronized void request(boolean widescreen) {
      DEMAND[getIndex(widescreen)] = true;
   }

   public static boolean isDemand(boolean widescreen) {
      return DEMAND[getIndex(widescreen)];
   }

   public static boolean isDemandWidescreen() {
      return DEMAND[0];
   }

   public static boolean isDemandStandard() {
      return DEMAND[1];
   }

   public static boolean isDemand() {
      return DEMAND[0] || DEMAND[1];
   }

   private static void updateStreaming(HolyricsStreaming.HolyricsStreamRenderer renderer) {
      for (int i = 0; i < SIZES.length; i++) {
         try {
            boolean alert = SVS.isEnabled() && CommunicationPanel.isDisplayAlert();
            if (isDemand(i == 0)) {
               if (renderer == null) {
                  STREAMING[i] = STREAMING_HASH[i] = null;
                  UPDATE = 1L;
               } else {
                  String hash = renderer.getHash(SIZES[i]) + (alert ? CommunicationPanel.getAlertHash() : "false");
                  if (!Objects.equals(STREAMING_HASH[i], hash)) {
                     STREAMING_HASH[i] = hash;
                     BufferedImage biRenderer = null;

                     try {
                        Dimension size = SIZES[i];

                        try {
                           Dimension rendererSize = new Dimension(size.width, size.height);
                           AlertSettings as = Settings.SET.getAlertSettings();
                           boolean isOverlap = as.isOverlap();
                           if (alert && !isOverlap) {
                              rendererSize.height = rendererSize.height - as.getAlertHeight(size.height);
                           }

                           biRenderer = renderer.renderer(rendererSize);
                           BufferedImage biAlert = null;
                           if (alert) {
                              biAlert = CommunicationPanel.getAlertImage(size);
                              if (biAlert != null) {
                                 BufferedImage bi = new BufferedImage(size.width, size.height, 2);
                                 Graphics2D g = bi.createGraphics();
                                 g.drawImage(biRenderer, 0, !isOverlap && !as.isBottom() ? biAlert.getHeight() : 0, null);
                                 g.drawImage(biAlert, 0, as.isTop() ? 0 : size.height - biAlert.getHeight(), null);
                                 g.dispose();
                                 biRenderer = bi;
                                 biAlert.flush();
                                 biAlert = null;
                              }
                           }
                        } catch (Exception var17) {
                        }

                        biRenderer = Img.toBufferedImage2(biRenderer, true);
                        STREAMING[i] = biRenderer != null ? encode(biRenderer) : null;
                     } catch (Exception var18) {
                        STREAMING[i] = STREAMING_HASH[i] = null;
                     } finally {
                        biRenderer = null;
                        UPDATE = System.currentTimeMillis();
                     }
                  }
               }
            }
         } catch (Exception var20) {
         }
      }
   }

   public static void clear() {
      setRenderer(null);
   }

   public static Pack getStreamingPack(boolean widescreen) {
      int index = getIndex(widescreen);
      String img = STREAMING[index];
      return Pack.create(true).put("image", img != null ? img : DEFAULT_STREAMING[index]).put("update", getUpdate());
   }

   public static String getUpdate() {
      return String.valueOf(UPDATE);
   }

   private static String createDefaultStreaming(boolean widescreen) {
      try {
         String format = "/com/limagiran/holyrics/img/defaultStreaming%s.png";
         String path = String.format("/com/limagiran/holyrics/img/defaultStreaming%s.png", widescreen ? "Widescreen" : "Standard");
         BufferedImage bi = ImageIO.read(HolyricsStreaming.class.getResource(path));
         String _return = Img.encodeToString(bi, "PNG");
         bi.flush();
         return _return;
      } catch (Exception var5) {
         return Img.createBlank64(new Color(50, 50, 50), 1920, 1080);
      }
   }

   private static String encode(BufferedImage bi) throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      JPEGImageWriteParam params = new JPEGImageWriteParam(null);
      params.setCompressionMode(2);
      params.setCompressionQuality(0.85F);
      ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

      try (MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(baos)) {
         writer.setOutput(os);
         writer.write(null, new IIOImage(bi, null, null), params);
      } finally {
         bi.flush();
      }

      return DatatypeConverter.printBase64Binary(baos.toByteArray());
   }

   static {
      startThread();
   }

   public interface HolyricsStreamRenderer {
      BufferedImage renderer(Dimension var1);

      String getHash(Dimension var1);
   }
}
