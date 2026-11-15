package com.limagiran.holyrics.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

public class Images {
   public static final String IMAGE_EMPTY_64 = "iVBORw0KGgoAAAANSUhEUgAAAPUAAAC9CAYAAABrujtkAAAHqElEQVR42u3d227bOhBGYb//e+auaQ5tr7vBDQhwXCuWZJIzpL6Lhbax48qSlv7hQdTlz58/fwHMw8VOAEgNgNQASA2A1ABIDZAaAKkBkBoAqQGQGiA1AFIDmEzqX79+Aafl9+/fc0hdvsjypV5eXoDTEiH3pZXQDijwr9zDSS2dgcdyDyO1hAZyiH2pmdIOGBAv9oXQQIzUrTrPSA1MltaXGm1pBwjII/ZFSgOkJjWQuG1NaiAYUgOkJjVA6h04KACpAZAaIDWpAVLHSv3jx4+/r6+v//95y/XPy98X9rx37f2PPmPPa4+25ZnPW9639bvce8+j37v+7D3fc893uf38Lfv6yLYc3fY9+6hA6gdSW18KI1HkJjWpQerzSF12kBMFpJbUAKlJDZBa+Q2QWlJjfgxpSWqQWlIDym9SA5Ja+Q1IakkNSGpSg9SkBkhN6prPBfv58+fq7ZPlz/L65+enk5vUpM7cUVZEXcTdus3lveX3ej5UHKSW1JVlXhO8fI6TndSkDkzqUj4vK1nU/B4fHx9OekNapO6d1EW8mjLfiv329ubEl9Sk7nUwinCthFaOk5rUncvv9/f35kITm9Sk7pTUSxu65/KvSvHHQ4ekltRP9XJHrOtcDr7Os/VRh1I9kVpSH2pHRy7Y7oaVf4VepCj7ZsRxfr3fgVKXMq932a0M39evMWIZLqkDpY5OaWn9eChxxDKc1EEnfNlh0Sl9feE6c1o/OhajXfRIHZTUJRkyPQztzGm9paNypDKc1EFSZym9zy711gk/I5XhpA6SOmoY6zvONry1d8LPKBc+UgdInaHX+x5nalcfPQYjlOGkDpI640PGz1SCH72olvMhexlunDrgRC/TQkmdu2Ns5P1E6oCkzir1GdZjq3EnXPYbYpTfkvo0SV3zXvXMZTipA070rG3qmW/HbNE5mfUiqPwOKEkzzSY7S+93i/29LDlFalKnHaeedWnh2uu9ZS/Dld9BUpcTQXu6373RZ9p3pA6SOmK1k7O1p3stEZWtDCd14FU4SwlevmNE6V06r6LuvGqxD7NMs9WmDhy7bbkccPbycRkvbnUxqZVWI5bhkjp4QkZ0WkckTCmLr5cMqp3YLTvGRpiUQurgq29kWkechCWZb0+68u9aYl+vMXaWiySpE06d7LGIf5Zyce2EqyH2mZszpE44H7p3GR6xUuYj4Z7dpsiEzrSYo46yRFfdHmOqrdqxW060LdId3d9ZhI4eUSB1wjuXWotdPr+30HvbuXvFfvZxv7ON/Su/E96O2KKNHdUze+S7lH2+Veyo/ojMZTipk95jXDp9aqT2InNEj+z10NWR/f5I7Gyz8jI1dUideOGAo3JHylxLuEfTL7MLHVWGS+pBVgMpV/siaCnnyklyLfoicKG8XhKydzrcTtGsdWKtJXbUBJOjF6eed3KReuAlfspOz/gAtxb9Addi9xolGHUIkdQW4xtiiubyuSMK3XsRCm1qi/GFDV2djV5TSCU1qYeaODM6PTrNSE3qKmPREjpPGU5qUk9xE8VoZXjLKaSkJvVTw2wSOl8ZTmq931MtczxSWrcqw0ktqdOso31GsVtMEjKkRephlgtShktq5behq/S0eBgAqSV1+mWXlOHKb1Ibujp1GU5qUhN6sju5lN+k7v4IWLS9k0tSk9rQ1WRluKQm9VCP23UnF6lJrad7SLFJTWpCK8NJbfKJnu5Zy3BSS+qhltxVhpN6mqRuvQyOoaucUh8pw0k9QFIvbdyWK2YQep4ynNTJpb5O0FZiG7qaqww3+SS51LcHqPYzsdx1NcadXHvEJnViqdeGlmqJbehqLLG3luGkTir1o46rZ8UuJ4j1xcYTW5t6YKm3tHOPim3oau7ecEmdcEhrT1m8V2xDV/N3mknqZEl9RLo9YhOa1JK6c1IfHV7aIrYFA8/RriZ1oqQuK188I13Z1jWxDV2RmtSdk7rW4vj3EtvQlfKb1AFJXXNW15LYpZe7pL+hK1KTurPUbncEqSeS2jOpoE09mdSlvetkBakn6ShTdqPF+WhGWVBSK7vR6nwkdZDUym6QeiKpld1Qfk8mNaGho2yijjJlNyT1REmt7IaknkxqQoPUE0mt7IZpohNJreyGpJ5MakJDUk8ktYXyofd7IqmV3TCjbLJxakKD1BMltTXBoKNsIqmV3dCmnkxqQoPUE0ldym4L/cGQ1iRSP7tuNyCpk/V+Exp6vyeS2iNtoPyeqPz2BAwY0ppI6uUplUAvlN9JFx4EWiGpSQ1SkxogNamBbmhTkxqkJjWg/CY1IKkzSf0dZQeu/fz6tdu/L/++9+e91++9d+3/v/2stZ/fvn67fWvs+Q5r79nye9/th9vt/m77vjs2a/ti7Wdr+2htv+/dv2v7asvvX/8OqQGQGiA1qQFSkxog9ZcbLRwYgNQASA2QmtQAqfdTNsiBAY5RQrH2TLdLjQ+R1kCOlCY1MFlKV5Oa2ECOlK4qddlAYgOxKV1VamID8UJXl3opw4kNxAjdRGqJDdyXuYfQzaSW2sBXmVt1inWX+ja5r3HAMbPEETJ3lRoAqQGQGgCpAVIDIDUAUgMgNQBSA6QGQGoApAZAagCkBkgNgNQASA2A1AC+8B85eYnLeMXAkQAAAABJRU5ErkJggg==";
   public static final String BLACK_SCREEN = Img.createBlank64(Color.BLACK, VideoUtils.getSizeDisplay().width, VideoUtils.getSizeDisplay().height);
   public static final List<Image> HOLYRICS_ICON = loadHolyricsIcon();
   private static final String URL_PATH = "/com/limagiran/holyrics/img/";
   public static final BufferedImage NULL1X1 = Img.createBlank(new Color(0, 0, 0, 0), 1, 1);
   public static final BufferedImage MEMBER_GROUP_VOICE = load("mic16.png");
   public static final BufferedImage MEMBER_GROUP_INSTRUMENT = load("music16.png");
   public static final BufferedImage MEMBER_GROUP_ORCHESTRA = load("violin16.png");
   public static final BufferedImage MEMBER_GROUP_OTHERS = load("others16.png");
   public static final BufferedImage ICON_HOLYRICS_16 = load("holyricsIcon16.png");
   public static final BufferedImage ICON_CHECK_BLACK_16 = load("checkBlack16.png");
   public static final BufferedImage ICON_CHECK_BLACK_24 = load("checkBlack24.png");
   public static final BufferedImage ICON_LIST_16 = load("list16.png");
   public static final BufferedImage ICON_FAVORITE_16 = load("favorite16.png");
   public static final BufferedImage ICON_EDIT_16 = load("edit16.png");
   public static final BufferedImage ICON_EDIT_24 = load("edit16.png");
   public static final BufferedImage ICON_PROFILE_16 = load("profile16.png");
   public static final BufferedImage ICON_IMAGE_16 = load("image16.png");
   public static final BufferedImage ICON_WEB_16 = load("web16.png");
   public static final BufferedImage ICON_BIBLE_16 = Img.resize(load("bible26.png"), new Dimension(16, 16));
   public static final BufferedImage ICON_SETTINGS_16 = load("settings16.png");
   public static final BufferedImage ICON_CLEAR_ALL_16 = load("clearAll16.png");
   public static final BufferedImage ICON_REMOVE_16 = load("delete16.png");
   public static final BufferedImage ICON_REMOVE_24 = load("delete24.png", Color.DARK_GRAY);
   public static final BufferedImage ICON_REMOVE_36 = load("delete32.png", Color.DARK_GRAY);
   public static final BufferedImage ICON_PLAYED_MUSIC_16 = load("playedMusic16.png");
   public static final BufferedImage ICON_YOUTUBE_32 = load("youtube32.png");
   public static final BufferedImage ICON_PLAY_16 = load("play16.png");
   public static final BufferedImage ICON_SKIP_NEXT_16 = load("skipNext16.png");
   public static final BufferedImage ICON_STOP_16 = load("stop16.png");
   public static final BufferedImage ICON_PLAY_LIST_PLAYER_16 = load("playlistPlay16.png");
   public static final BufferedImage ICON_ADD_INFO_TWO_LINES = load("wrapText16.png");
   public static final BufferedImage ICON_USING_THEME_EFFECTS = load("style16.png");
   public static final BufferedImage ICON_MUSIC_VIDEO_32 = load("musicVideo32.png");
   public static final BufferedImage ICON_CHAT_BACKGROUND_PATTERN = load("chatBackgroundPattern.png");
   public static final BufferedImage ICON_SEND_16 = load("send16.png");
   public static final BufferedImage ICON_SEND_24 = load("send24.png");

   public static void load() {
   }

   public static BufferedImage imageNotFound() {
      try {
         return ImageIO.read(Images.class.getResource("/com/limagiran/holyrics/img/imageNotFound.jpg"));
      } catch (IOException var1) {
         return new BufferedImage(1, 1, 2);
      }
   }

   public static BufferedImage load(String name) {
      try {
         return ImageIO.read(Images.class.getResource("/com/limagiran/holyrics/img/" + name));
      } catch (Exception var2) {
         return new BufferedImage(50, 50, 2);
      }
   }

   public static BufferedImage loadAbsoluteURL(String url) {
      try {
         return ImageIO.read(Images.class.getResource(url));
      } catch (Exception var2) {
         return new BufferedImage(50, 50, 2);
      }
   }

   public static BufferedImage loadWithPadding(String name, int horizontal, int vertical) {
      try {
         BufferedImage img = ImageIO.read(Images.class.getResource("/com/limagiran/holyrics/img/" + name));
         BufferedImage bi = new BufferedImage(img.getWidth() + horizontal * 2, img.getHeight() + vertical * 2, 2);
         Graphics2D g = bi.createGraphics();
         g.drawImage(img, horizontal, vertical, null);
         g.dispose();
         return bi;
      } catch (Exception var6) {
         return new BufferedImage(50, 50, 2);
      }
   }

   public static BufferedImage load(String name, Color tint) {
      try {
         BufferedImage bi = ImageIO.read(Images.class.getResource("/com/limagiran/holyrics/img/" + name));
         int r = tint.getRed();
         int g = tint.getGreen();
         int b = tint.getBlue();

         for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
               Color c = new Color(bi.getRGB(x, y), bi.getTransparency() != 1);
               bi.setRGB(x, y, new Color(r, g, b, c.getAlpha()).getRGB());
            }
         }

         return bi;
      } catch (Exception var9) {
         return new BufferedImage(50, 50, 2);
      }
   }

   public static String loadAs64(String name) {
      try {
         return Img.encodeToString(ImageIO.read(Images.class.getResource("/com/limagiran/holyrics/img/" + name)), "PNG");
      } catch (Exception var2) {
         return "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";
      }
   }

   public static String loadAs64(String name, float opacity) {
      try {
         return Img.encodeToString(Img.opacity(ImageIO.read(Images.class.getResource("/com/limagiran/holyrics/img/" + name)), opacity), "PNG");
      } catch (Exception var3) {
         return "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";
      }
   }

   public static BufferedImage loadFrom64(String img) {
      try {
         return Img.decodeToImage(img);
      } catch (Exception var2) {
         return imageNotFound();
      }
   }

   public static BufferedImage load(String name, float opacity) {
      try {
         return Img.opacity(ImageIO.read(Images.class.getResource("/com/limagiran/holyrics/img/" + name)), opacity);
      } catch (Exception var3) {
         return new BufferedImage(50, 50, 2);
      }
   }

   private static List<Image> loadHolyricsIcon() {
      return IntStream.of(16, 20, 24, 32, 40, 48, 64, 72, 96, 128, 196, 256)
         .mapToObj(i -> String.format("holyricsIcon%s.png", i))
         .map(Images::load)
         .collect(Collectors.toList());
   }

   public static List<Image> loadIcons(String prefix, String suffix, int... sizes) {
      return IntStream.of(sizes).mapToObj(i -> String.format("%s%s%s", prefix, i, suffix)).map(Images::load).collect(Collectors.toList());
   }

   public static String getImageHTMLTag(String name) {
      return "<img src='" + Images.class.getResource("/com/limagiran/holyrics/img/" + name) + "'>";
   }

   public static String getImageHTMLTagWithBorder(String name, int border) {
      return "<img src='" + Images.class.getResource("/com/limagiran/holyrics/img/" + name) + "' border='" + border + "'>";
   }

   public static String getImageHTMLTagBase64(String name) {
      return "<img src='" + loadAs64(name) + "'>";
   }

   public static String getImageHTMLTagBase64WithBorder(String name, int border) {
      return "<img src='" + loadAs64(name) + "' border='" + border + "'>";
   }
}
