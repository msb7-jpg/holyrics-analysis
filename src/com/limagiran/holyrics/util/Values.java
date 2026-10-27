package com.limagiran.holyrics.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

public interface Values {
   String APP_NAME = "Holyrics";
   String OBJ_KEY = "#hly$k3y#";
   String WHATSAPP = "+55 15 98127 8370";
   String[] VAGALUME_API_KEYS = new String[]{
      "34db1ee757d414ed544882cb36bdfd6f",
      "9529412d2d16f91340f6318844d34e9d",
      "b0a1f5b3a9ad17e456f4ad4ac8cea48a",
      "d4efb601babe34a69fbdfd8948097587",
      "e7a05292cd7076c52c40cb576635ba92",
      "41e21a0f32c9f9f49c677f5bfd0cb473",
      "60f47bc3926b981f9eb93bf24c71db55",
      "80fa127cebd5aae502e72eb0608b8783",
      "8794e73350be34edb2de22cd93ce0fe4",
      "b69490a22b5adc71cb8e14cd4a75272b",
      "2bb8e18e4f20e22839ce796ed06dc086",
      "755fb1cf0188ad530a9731b663a04286",
      "b857e91a3853d37af0e9c942ddd68c0b",
      "cf5e06ac6c6d0ace3d662fa9c8dbd931",
      "d234902083f8054b9b265f4428d4366b",
      "4ae64c53322469c010147856039a34b9",
      "574de467bb4fb34d471a5b81c4d9e235",
      "631e7339b4e53ae8e375724620814ed6",
      "9025b8b8c84c671eca349f3aee64a415",
      "c39b7898a781fbd36e3fb6e254c8d718"
   };
   String PATH_APP = "Holyrics" + File.separator;
   String PATH_FILES = PATH_APP + "files" + File.separator;
   String PATH_BACKGROUND_VIDEO = PATH_FILES + "videos" + File.separator;
   String PATH_MEDIA = PATH_FILES + "media" + File.separator;
   String PATH_MEDIA_AUDIO = PATH_MEDIA + "audio" + File.separator;
   String PATH_MEDIA_VIDEO = PATH_MEDIA + "video" + File.separator;
   String PATH_MEDIA_IMAGE = PATH_MEDIA + "image" + File.separator;
   String PATH_MEDIA_APP = PATH_MEDIA + "app" + File.separator;
   String PATH_MEDIA_AUTOMATIC_PRESENTATIONS = PATH_MEDIA + "automaticPresentations" + File.separator;
   String PATH_MEDIA_FULL = new File("").getAbsolutePath() + File.separator + PATH_MEDIA;
   File FILE_FFMPEG = new File(PATH_FILES + "ffmpeg.exe");
   String HTML_HEAD_STYLE_SPAN_NOWRAP = "<html><head><style>span{white-space:nowrap}</style></head>";
   String FORMAT_INFO_TITLE_AND_ARTIST = "<html><head><style>span{white-space:nowrap}</style></head><body><div><span><b>%s</b></span> <span><b>(%s)</b></span></body><html>";
   String FORMAT_INFO_TITLE = "<html><head><style>span{white-space:nowrap}</style></head><body><div><span><b>%s</b></span></body><html>";
   String URL_YOUTUBE_TUTORIAL_PLAYLIST = "https://www.youtube.com/watch?v=QZ-M3Ghjp8I&list=PL6NYsQBmGq-l-4_8t_XMBc9w9aATXpk0G";
   String URL_YOUTUBE_ID_TO_STREAM = loadUrlYoutubeIdToStream();
   String URL_YOUTUBE_ID_TO_TITLE = loadUrlYoutubeIdToTitle();
   String EXTENSION_MUSIC = ".muf";
   String FILE_DESC_MUSIC = "Music File (*.muf)";
   String EXTENSION_MUSIC_LIST = ".mufl";
   String FILE_DESC_MUSIC_LIST = "Music File List(*.mufl)";
   String EXTENSION_THEME = ".thf";
   String FILE_DESC_THEME = "Theme File (*.thf)";
   String EXTENSION_MEDIA_PLAYLIST = ".hpl";
   String FILE_DESC_MEDIA_PLAYLIST = "Holyrics Playlist File (*.hpl)";
   int MAXIMUM_VERSES_TO_SAVE = 20;
   int MAXIMUM_VERSES_TO_SHOW = 100;
   int MAX_VISIBLE_ROW_COUNT_ORDER_LIST = 5;
   int MIN_LENGTH_PASSWORD = 4;
   int MIN_LENGTH_USERNAME = 4;
   int MAX_LENGTH_MUSIC_TITLE = 80;
   int MAX_LENGTH_MUSIC_ARTIST = 80;
   int MAX_LENGTH_MUSIC_NOTE = 200;
   int MAX_LENGTH_MUSIC_LYRICS = 12000;
   int MAX_LENGTH_MUSIC_FORMATTING = 1024;
   int MAX_LENGTH_ANNOUNCEMENT_TEXT = 2048;
   int MAX_LENGTH_ANNOUNCEMENT_TITLE = 48;
   int MAX_LENGTH_APP_MESSAGE_TEXT = 1024;
   int MAX_LENGTH_APP_MESSAGE_NAME = 24;
   int MAX_LENGHT_APP_MESSAGE_LIST = 256;
   int COMMUNICATION_PANEL_CLOCK_SIZE = MyProperties.EnumProperties.COMMUNICATION_PANEL_CLOCK_SIZE.getIntRange(50, 1000);
   int MAX_LENGTH_FOOTER_ALERT = 1024;
   long MAX_BYTES_ANNOUNCEMENT = 16777216L;
   int MAX_AMOUNT_IMAGE_FILE = 60;
   int MAX_FILE_SIZE_MY_IMAGES = 16777216;
   int MAX_FILE_SIZE_MY_VIDEOS = 20971520;
   int MAX_FILE_SIZE_THUMB_CACHE = 3145728;
   int SHARE_THEME_MAX_BYTES = 1048576;
   int MAX_VIDEO_DURATION_SECONDS = 600;
   int VIDEO_DURATION_ENLARGE_SECONDS = 50;
   int POWER_POINT_MAX_FILES = 10;
   int WORD_MAX_FILES = 10;
   int DURATION_MIN = 200;
   int DURATION_MAX = 2400;
   int DURATION_STEP = 100;
   int FADE_IN_OUT_DURATION_MIN = 200;
   int FADE_IN_OUT_DURATION_MAX = 1200;
   int FADE_IN_OUT_DURATION_STEP = 100;
   long CACHE_MAX_BYTES_BACKGROUND = 17000000L;
   long CACHE_MAX_BYTES_IMAGE = 17000000L;
   long CACHE_MAX_BYTES_THUMB_IMAGE = 8000000L;
   long CACHE_MAX_BYTES_PRESENTATION_SMALL = 12000000L;
   long CACHE_MAX_BYTES_PRESENTATION_BIG = 26000000L;
   boolean DISPLAY_FONT_NAME_WITH_FONT = MyProperties.EnumProperties.DISPLAY_FONT_NAME_WITH_FONT.is();
   Graphics2D GRAPHICS = new BufferedImage(1, 1, 2).createGraphics();
   Color[] COLOR_BOOK_AND_CHAPTER = new Color[]{
      new Color(125, 78, 36),
      new Color(216, 129, 23),
      new Color(202, 33, 26),
      new Color(140, 52, 126),
      new Color(60, 70, 141),
      new Color(0, 140, 167),
      new Color(15, 129, 80),
      new Color(125, 169, 30)
   };
   Color[] COLOR_BOOK_VERSE = new Color[]{
      new Color(177, 127, 74),
      new Color(232, 175, 96),
      new Color(218, 95, 97),
      new Color(156, 80, 145),
      new Color(98, 105, 157),
      new Color(94, 162, 171),
      new Color(88, 155, 124),
      new Color(169, 191, 118)
   };
   int[] BIBLE_MAP_DIVISION = new int[]{5, 12, 5, 17, 4, 1, 21, 1};
   Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
   Color COLOR_TRANSPARENT_1 = new Color(0, 0, 0, 1);

   static Font getFontBaramond(int style, int size) {
      return Utils.loadFont(Values.class.getResourceAsStream("/com/limagiran/holyrics/file/baramond.ttf"), style, size);
   }

   static String loadUrlYoutubeLinkResource(String url, String propKey, String urlDefault) {
      Download d = Download.download(url);
      int count = 0;

      while (!d.isFinish() && count < 500) {
         count += 50;
         Utils.sleep(50L);
      }

      if (d.isFinish()) {
         String response = d.getText();
         if (response.equals(urlDefault)) {
            return urlDefault;
         }

         MyProperties.set(propKey, d.getText());
      } else {
         d.cancel();
      }

      return MyProperties.get(propKey, urlDefault);
   }

   static String loadUrlYoutubeIdToStream() {
      String url = "http://www.holyrics.com/download/doc/youtube-stream-id-to-url.txt";
      String _default = "http://sample-env.pgkdepiqqq.sa-east-1.elasticbeanstalk.com/yt-stream/getstreamlist.php?videoid=";
      return loadUrlYoutubeLinkResource(
         "http://www.holyrics.com/download/doc/youtube-stream-id-to-url.txt",
         "yt-stream",
         "http://sample-env.pgkdepiqqq.sa-east-1.elasticbeanstalk.com/yt-stream/getstreamlist.php?videoid="
      );
   }

   static String loadUrlYoutubeIdToTitle() {
      String url = "http://www.holyrics.com/download/doc/youtube-stream-id-to-title.txt";
      String _default = "http://www.holyrics.com/api/youtube/gettitle.php?id=";
      return loadUrlYoutubeLinkResource(
         "http://www.holyrics.com/download/doc/youtube-stream-id-to-title.txt", "yt-title", "http://www.holyrics.com/api/youtube/gettitle.php?id="
      );
   }
}
