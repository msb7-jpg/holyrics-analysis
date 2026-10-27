package com.limagiran.holyrics.vlc;

import com.limagiran.holyrics.util.G2Utils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;

class VLCVideoBackgroundBaseUtils {
   public static Map generateMapAlphaColor() {
      Map<Float, Map<Float, Integer>> mapOpacity = new TreeMap<>();
      float[] fOpacityArray = new float[]{
         0.05F, 0.1F, 0.15F, 0.2F, 0.25F, 0.3F, 0.35F, 0.4F, 0.45F, 0.5F, 0.55F, 0.6F, 0.65F, 0.7F, 0.75F, 0.8F, 0.85F, 0.9F, 0.95F
      };

      for (float fOpacity : fOpacityArray) {
         Map<Float, Integer> map = new TreeMap<>();

         for (float _progress = 1.0F; _progress >= 0.0F; _progress -= 0.025F) {
            float progress = Float.parseFloat(String.format("%.3f", _progress).replace(",", "."));
            int opacity = (int)(255.0F * fOpacity);
            Integer solve = solve(opacity, progress);
            if (solve == null) {
               solve = solveNull(fOpacity, progress);
            }

            if (solve != null) {
               map.put(progress, solve);
            }
         }

         mapOpacity.put(fOpacity, map);
      }

      return mapOpacity;
   }

   private static Integer solve(int realOp, float progress) {
      Color real = join(realOp, progress);

      for (int i = 0; i <= 4; i++) {
         for (int alpha = 0; alpha < 256; alpha++) {
            Color c = generate(realOp, progress, alpha);
            if (equals(c, real, i)) {
               return alpha;
            }
         }
      }

      return null;
   }

   private static Color cbg(int alpha) {
      return new Color(255, 128, 0, alpha);
   }

   private static Color cfg(int alpha) {
      return new Color(0, 50, 128, alpha);
   }

   private static Color join(int realOp, float progress) {
      BufferedImage bi = new BufferedImage(1, 1, 2);
      Graphics2D g = bi.createGraphics();
      G2Utils.setupGraphics(g);
      g.setColor(cbg(255));
      g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
      g.setColor(cfg(realOp));
      g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
      g.dispose();
      BufferedImage bi2 = new BufferedImage(1, 1, 2);
      Graphics2D g2 = bi2.createGraphics();
      G2Utils.setupGraphics(g2);
      G2Utils.setupAlphaComposite(g2, progress);
      g2.drawImage(bi, null, 0, 0);
      g2.dispose();
      return new Color(bi2.getRGB(0, 0), true);
   }

   private static Color generate(int realOp, float progress, int alpha) {
      BufferedImage bi = new BufferedImage(1, 1, 2);
      Graphics2D g = bi.createGraphics();
      G2Utils.setupGraphics(g);
      g.setColor(cbg(alpha));
      g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
      g.setColor(cfg((int)((float)realOp * progress)));
      g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
      g.dispose();
      return new Color(bi.getRGB(0, 0), true);
   }

   private static boolean equals(Color c1, Color c2, int dif) {
      BiFunction<Integer, Integer, Boolean> check = (n1, n2) -> {
         for (int _n1 = n1 - dif; _n1 <= n1 + dif; _n1++) {
            if (n2 == _n1) {
               return true;
            }
         }

         return false;
      };
      return check.apply(c1.getRed(), c2.getRed())
         && check.apply(c1.getGreen(), c2.getGreen())
         && check.apply(c1.getBlue(), c2.getBlue())
         && check.apply(c1.getAlpha(), c2.getAlpha());
   }

   private static Integer solveNull(float o, float p) {
      if (o == 0.05F && p == 0.15F) {
         return 38;
      } else if (o == 0.05F && p == 0.125F) {
         return 33;
      } else if (o == 0.05F && p == 0.075F) {
         return 20;
      } else if (o == 0.05F && p == 0.05F) {
         return 15;
      } else if (o == 0.1F && p == 0.15F) {
         return 36;
      } else if (o == 0.1F && p == 0.1F) {
         return 18;
      } else if (o == 0.1F && p == 0.075F) {
         return 15;
      } else if (o == 0.1F && p == 0.025F) {
         return 8;
      } else if (o == 0.15F && p == 0.15F) {
         return 30;
      } else if (o == 0.15F && p == 0.1F) {
         return 20;
      } else if (o == 0.15F && p == 0.075F) {
         return 15;
      } else if (o == 0.15F && p == 0.05F) {
         return 10;
      } else if (o == 0.15F && p == 0.025F) {
         return 5;
      } else if (o == 0.2F && p == 0.075F) {
         return 15;
      } else if (o == 0.2F && p == 0.05F) {
         return 10;
      } else if (o == 0.25F && p == 0.125F) {
         return 22;
      } else if (o == 0.25F && p == 0.075F) {
         return 14;
      } else if (o == 0.3F && p == 0.075F) {
         return 14;
      } else if (o == 0.3F && p == 0.05F) {
         return 11;
      } else if (o == 0.3F && p == 0.025F) {
         return 4;
      } else if (o == 0.35F && p == 0.1F) {
         return 16;
      } else if (o == 0.35F && p == 0.075F) {
         return 12;
      } else if (o == 0.35F && p == 0.05F) {
         return 7;
      } else if (o == 0.4F && p == 0.075F) {
         return 13;
      } else if (o == 0.45F && p == 0.15F) {
         return 22;
      } else if (o == 0.45F && p == 0.1F) {
         return 16;
      } else if (o == 0.45F && p == 0.075F) {
         return 13;
      } else if (o == 0.45F && p == 0.05F) {
         return 8;
      } else if (o == 0.45F && p == 0.025F) {
         return 5;
      } else if (o == 0.5F && p == 0.1F) {
         return 12;
      } else if (o == 0.5F && p == 0.05F) {
         return 4;
      } else if (o == 0.5F && p == 0.025F) {
         return 3;
      } else if (o == 0.55F && p == 0.15F) {
         return 18;
      } else if (o == 0.55F && p == 0.125F) {
         return 14;
      } else if (o == 0.55F && p == 0.075F) {
         return 8;
      } else if (o == 0.55F && p == 0.025F) {
         return 3;
      } else if (o == 0.6F && p == 0.15F) {
         return 13;
      } else if (o == 0.6F && p == 0.125F) {
         return 12;
      } else if (o == 0.6F && p == 0.05F) {
         return 6;
      } else if (o == 0.6F && p == 0.025F) {
         return 3;
      } else if (o == 0.65F && p == 0.05F) {
         return 5;
      } else if (o == 0.7F && p == 0.1F) {
         return 9;
      } else if (o == 0.7F && p == 0.05F) {
         return 3;
      } else if (o == 0.75F && p == 0.1F) {
         return 7;
      } else if (o == 0.75F && p == 0.075F) {
         return 5;
      } else if (o == 0.75F && p == 0.05F) {
         return 3;
      } else if (o == 0.8F && p == 0.05F) {
         return 2;
      } else if (o == 0.8F && p == 0.025F) {
         return 1;
      } else if (o == 0.85F && p == 0.05F) {
         return 1;
      } else if (o == 0.85F && p == 0.025F) {
         return 0;
      } else if (o == 0.9F && p == 0.175F) {
         return 5;
      } else if (o == 0.9F && p == 0.1F) {
         return 2;
      } else if (o == 0.9F && p == 0.05F) {
         return 1;
      } else if (o == 0.9F && p == 0.025F) {
         return 0;
      } else if (o == 0.95F && p == 0.175F) {
         return 3;
      } else {
         return o == 0.95F && p == 0.075F ? 1 : null;
      }
   }
}
