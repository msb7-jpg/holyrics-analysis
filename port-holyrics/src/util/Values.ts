import * as path from "node:path";
import { MyProperties } from "./MyProperties";

export interface ColorRGB {
  r: number;
  g: number;
  b: number;
  a?: number;
}

export const Values = {
  APP_NAME: "Holyrics",
  OBJ_KEY: "#hly$k3y#",
  WHATSAPP: "+55 15 98127 8370",
  VAGALUME_API_KEYS: [
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
    "c39b7898a781fbd36e3fb6e254c8d718",
  ],
  PATH_APP: "Holyrics" + path.sep,
  get PATH_FILES(): string {
    return this.PATH_APP + "files" + path.sep;
  },
  get PATH_BACKGROUND_VIDEO(): string {
    return this.PATH_FILES + "videos" + path.sep;
  },
  get PATH_MEDIA(): string {
    return this.PATH_FILES + "media" + path.sep;
  },
  get PATH_MEDIA_AUDIO(): string {
    return this.PATH_MEDIA + "audio" + path.sep;
  },
  get PATH_MEDIA_VIDEO(): string {
    return this.PATH_MEDIA + "video" + path.sep;
  },
  get PATH_MEDIA_IMAGE(): string {
    return this.PATH_MEDIA + "image" + path.sep;
  },
  get PATH_MEDIA_APP(): string {
    return this.PATH_MEDIA + "app" + path.sep;
  },
  get PATH_MEDIA_AUTOMATIC_PRESENTATIONS(): string {
    return this.PATH_MEDIA + "automaticPresentations" + path.sep;
  },
  get PATH_MEDIA_FULL(): string {
    return path.resolve("") + path.sep + this.PATH_MEDIA;
  },
  get FILE_FFMPEG(): string {
    return path.join(this.PATH_FILES, "ffmpeg.exe");
  },
  HTML_HEAD_STYLE_SPAN_NOWRAP: "<html><head><style>span{white-space:nowrap}</style></head>",
  FORMAT_INFO_TITLE_AND_ARTIST:
    "<html><head><style>span{white-space:nowrap}</style></head><body><div><span><b>%s</b></span> <span><b>(%s)</b></span></body><html>",
  FORMAT_INFO_TITLE:
    "<html><head><style>span{white-space:nowrap}</style></head><body><div><span><b>%s</b></span></body><html>",
  URL_YOUTUBE_TUTORIAL_PLAYLIST:
    "https://www.youtube.com/watch?v=QZ-M3Ghjp8I&list=PL6NYsQBmGq-l-4_8t_XMBc9w9aATXpk0G",
  URL_YOUTUBE_ID_TO_STREAM: "http://sample-env.pgkdepiqqq.sa-east-1.elasticbeanstalk.com/yt-stream/getstreamlist.php?videoid=",
  URL_YOUTUBE_ID_TO_TITLE: "http://www.holyrics.com/api/youtube/gettitle.php?id=",
  EXTENSION_MUSIC: ".muf",
  FILE_DESC_MUSIC: "Music File (*.muf)",
  EXTENSION_MUSIC_LIST: ".mufl",
  FILE_DESC_MUSIC_LIST: "Music File List(*.mufl)",
  EXTENSION_THEME: ".thf",
  FILE_DESC_THEME: "Theme File (*.thf)",
  EXTENSION_MEDIA_PLAYLIST: ".hpl",
  FILE_DESC_MEDIA_PLAYLIST: "Holyrics Playlist File (*.hpl)",
  MAXIMUM_VERSES_TO_SAVE: 20,
  MAXIMUM_VERSES_TO_SHOW: 100,
  MAX_VISIBLE_ROW_COUNT_ORDER_LIST: 5,
  MIN_LENGTH_PASSWORD: 4,
  MIN_LENGTH_USERNAME: 4,
  MAX_LENGTH_MUSIC_TITLE: 80,
  MAX_LENGTH_MUSIC_ARTIST: 80,
  MAX_LENGTH_MUSIC_NOTE: 200,
  MAX_LENGTH_MUSIC_LYRICS: 12000,
  MAX_LENGTH_MUSIC_FORMATTING: 1024,
  MAX_LENGTH_ANNOUNCEMENT_TEXT: 2048,
  MAX_LENGTH_ANNOUNCEMENT_TITLE: 48,
  MAX_LENGTH_APP_MESSAGE_TEXT: 1024,
  MAX_LENGTH_APP_MESSAGE_NAME: 24,
  MAX_LENGHT_APP_MESSAGE_LIST: 256,
  get COMMUNICATION_PANEL_CLOCK_SIZE(): number {
    return MyProperties.EnumProperties.COMMUNICATION_PANEL_CLOCK_SIZE.getIntRange(50, 1000);
  },
  MAX_LENGTH_FOOTER_ALERT: 1024,
  MAX_BYTES_ANNOUNCEMENT: 16777216,
  MAX_AMOUNT_IMAGE_FILE: 60,
  MAX_FILE_SIZE_MY_IMAGES: 16777216,
  MAX_FILE_SIZE_MY_VIDEOS: 20971520,
  MAX_FILE_SIZE_THUMB_CACHE: 3145728,
  SHARE_THEME_MAX_BYTES: 1048576,
  MAX_VIDEO_DURATION_SECONDS: 600,
  VIDEO_DURATION_ENLARGE_SECONDS: 50,
  POWER_POINT_MAX_FILES: 10,
  WORD_MAX_FILES: 10,
  DURATION_MIN: 200,
  DURATION_MAX: 2400,
  DURATION_STEP: 100,
  FADE_IN_OUT_DURATION_MIN: 200,
  FADE_IN_OUT_DURATION_MAX: 1200,
  FADE_IN_OUT_DURATION_STEP: 100,
  CACHE_MAX_BYTES_BACKGROUND: 17000000,
  CACHE_MAX_BYTES_IMAGE: 17000000,
  CACHE_MAX_BYTES_THUMB_IMAGE: 8000000,
  CACHE_MAX_BYTES_PRESENTATION_SMALL: 12000000,
  CACHE_MAX_BYTES_PRESENTATION_BIG: 26000000,
  get DISPLAY_FONT_NAME_WITH_FONT(): boolean {
    return MyProperties.EnumProperties.DISPLAY_FONT_NAME_WITH_FONT.is();
  },
  COLOR_BOOK_AND_CHAPTER: [
    { r: 125, g: 78, b: 36 },
    { r: 216, g: 129, b: 23 },
    { r: 202, g: 33, b: 26 },
    { r: 140, g: 52, b: 126 },
    { r: 60, g: 70, b: 141 },
    { r: 0, g: 140, b: 167 },
    { r: 15, g: 129, b: 80 },
    { r: 125, g: 169, b: 30 },
  ],
  COLOR_BOOK_VERSE: [
    { r: 177, g: 127, b: 74 },
    { r: 232, g: 175, b: 96 },
    { r: 218, g: 95, b: 97 },
    { r: 156, g: 80, b: 145 },
    { r: 98, g: 105, b: 157 },
    { r: 94, g: 162, b: 171 },
    { r: 88, g: 155, b: 124 },
    { r: 169, g: 191, b: 118 },
  ],
  BIBLE_MAP_DIVISION: [5, 12, 5, 17, 4, 1, 21, 1],
  COLOR_TRANSPARENT: { r: 0, g: 0, b: 0, a: 0 },
  COLOR_TRANSPARENT_1: { r: 0, g: 0, b: 0, a: 1 },

  async loadUrlYoutubeLinkResource(url: string, propKey: string, urlDefault: string): Promise<string> {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 25000);
      const res = await fetch(url, { signal: controller.signal });
      clearTimeout(timeoutId);
      if (res.ok) {
        const text = (await res.text()).trim();
        if (text === urlDefault) {
          return urlDefault;
        }
        MyProperties.set(propKey, text);
        return text;
      }
    } catch {
      // ignore
    }
    return MyProperties.get(propKey, urlDefault);
  },

  async loadUrlYoutubeIdToStream(): Promise<string> {
    return this.loadUrlYoutubeLinkResource(
      "http://www.holyrics.com/download/doc/youtube-stream-id-to-url.txt",
      "yt-stream",
      "http://sample-env.pgkdepiqqq.sa-east-1.elasticbeanstalk.com/yt-stream/getstreamlist.php?videoid="
    );
  },

  async loadUrlYoutubeIdToTitle(): Promise<string> {
    return this.loadUrlYoutubeLinkResource(
      "http://www.holyrics.com/download/doc/youtube-stream-id-to-title.txt",
      "yt-title",
      "http://www.holyrics.com/api/youtube/gettitle.php?id="
    );
  },
};

export default Values;
