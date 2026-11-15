package com.limagiran.holyrics.settings;

import com.limagiran.bible.AvailableVersions;
import com.limagiran.bible.Bible;
import com.limagiran.holyrics.control.MusicFavoriteSetController;
import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.control.ThumbCacheController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Account;
import com.limagiran.holyrics.model.AlertSettings;
import com.limagiran.holyrics.model.ClockSettings;
import com.limagiran.holyrics.model.CountdownSettings;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.EnumVerseDescription;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Log;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.BibleFontSettings;
import com.limagiran.holyrics.view.panel.AutomaticSettingsPanel;
import com.limagiran.holyrics.vlc.VLCSettings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class Settings implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final Settings SET = SettingsFactory.getInstance();
   private static boolean save = false;
   private Music music = MusicUtils.getDefault();
   private Theme theme = ThemeController.getDefault();
   private Account account = new Account();
   private AlertSettings alertSettings = new AlertSettings();
   private AlertSettings alertSettings2 = new AlertSettings();
   private ClockSettings clockSettings = new ClockSettings();
   private VLCSettings vlcSettings = new VLCSettings();
   private Map<String, AutomaticSettingsPanel.AutomaticSettings> mapAutomaticSettings = new HashMap<>();
   private Map<Integer, ScreenDeviceSettings> mapScreenDeviceSettings = new HashMap<>();
   private CountdownSettings countdownSettings = new CountdownSettings();
   private CountdownSettings countdownSettings2 = new CountdownSettings();
   private final Map<Object, Boolean> mapB = new HashMap<>();
   private final Map<Object, String> mapS = new HashMap<>();
   private final Map<Object, Long> mapL = new HashMap<>();
   private final Map<Object, Integer> mapI = new HashMap<>();
   private static final transient Object MAP_LOCK = new Object();
   private Map<String, String> map = new HashMap<>();
   private long version = 6L;

   private static void threadSave() {
      Utils.startThreadDaemon(() -> {
         while (true) {
            Utils.sleep(3000L);
            if (save) {
               save = !save;
               save();
            }

            ScheduleController.checkSave();
            MusicFavoriteSetController.checkSave();
            ThumbCacheController.checkSave();
         }
      });
   }

   public Theme getBibleTheme() {
      return ThemeController.get(Settings.EnumSettings.BIBLE_THEME.getLong());
   }

   public Theme getBibleAutomaticShowTheme() {
      return ThemeController.get(Settings.EnumSettings.BIBLE_AUTOMATIC_SHOW_THEME.getLong());
   }

   public Theme getQuickPresentationTheme() {
      return ThemeController.get(Settings.EnumSettings.QUICK_PRESENTATION_THEME.getLong());
   }

   public Theme getCreateImageTheme() {
      return ThemeController.get(Settings.EnumSettings2.CREATE_IMAGE_THEME.getLong());
   }

   public Account getAccount() {
      return this.account;
   }

   public void setAccount(Account account) {
      this.account = account;
      save = true;
   }

   public AlertSettings getAlertSettings() {
      return this.alertSettings;
   }

   public AlertSettings getAlertSettings2() {
      if (this.alertSettings2 == null) {
         this.alertSettings2 = RWObj.clone(this.alertSettings, AlertSettings.class);
         if (this.alertSettings2 == null) {
            this.alertSettings2 = new AlertSettings();
         }
      }

      return this.alertSettings2;
   }

   public ClockSettings getClockSettings() {
      return this.clockSettings;
   }

   public CountdownSettings getCountdownSettings() {
      if (this.countdownSettings == null) {
         this.countdownSettings = new CountdownSettings();
      }

      return this.countdownSettings;
   }

   public CountdownSettings getCountdownSettings2() {
      if (this.countdownSettings2 == null) {
         this.countdownSettings2 = new CountdownSettings();
      }

      return this.countdownSettings2;
   }

   public VLCSettings getVLCSettings() {
      return this.vlcSettings;
   }

   public Theme getTheme() {
      return this.theme;
   }

   public void setTheme(Theme theme) {
      this.theme = theme;
      save = true;
   }

   public Music getMusic() {
      return Optional.ofNullable(this.music).orElse(Music.BLANK);
   }

   private Bible getBible(int i, List<Bible> uploadedBibles) {
      try {
         String key = i == 1
            ? Settings.EnumSettings.BIBLE_VERSION_KEY_2.getString()
            : (i == 2 ? Settings.EnumSettings.BIBLE_VERSION_KEY_3.getString() : Settings.EnumSettings.BIBLE_VERSION_KEY.getString());
         Bible b = uploadedBibles.stream().filter(f -> f.abbrev.equals(key)).findFirst().orElse(null);
         if (b != null) {
            return b;
         } else {
            AvailableVersions bible = AvailableVersions.getVersions()
               .stream()
               .filter(av -> av.getVersion().equals(key))
               .findFirst()
               .orElseThrow(Exception::new);
            return bible.loadBible();
         }
      } catch (Exception var7) {
         try {
            return AvailableVersions.getVersions().get(0).loadBible();
         } catch (Exception var6) {
            Log.send(var6);
            Utils.error(I18N.MSG_ERROR_CORRUPTED_FILES.val() + "\n" + I18N.MSG_ERROR_TRY_AGAIN.val());
            System.exit(0);
            return null;
         }
      }
   }

   public Bible getBible(List<Bible> uploadedBibles) {
      return this.getBible(0, uploadedBibles);
   }

   public Bible getBible2(List<Bible> uploadedBibles) {
      return this.getBible(1, uploadedBibles);
   }

   public Bible getBible3(List<Bible> uploadedBibles) {
      return this.getBible(2, uploadedBibles);
   }

   public boolean musicIsBlank() {
      return this.music.equals(Music.BLANK);
   }

   public Music setMusic(Music music) {
      this.music = music;
      save = true;
      return this.music;
   }

   public AutomaticSettingsPanel.AutomaticSettings getAutomaticSettings(String key) {
      try {
         if (this.mapAutomaticSettings == null) {
            this.mapAutomaticSettings = new HashMap<>();
         }

         AutomaticSettingsPanel.AutomaticSettings as = this.mapAutomaticSettings.get(key);
         if (as == null) {
            switch (key) {
               case "image":
                  as = new AutomaticSettingsPanel.AutomaticSettings(
                     Settings.EnumSettings.IMAGE_FILE_AUTOMATIC_SLIDE_SHOW.is(),
                     Settings.EnumSettings.IMAGE_FILE_AUTOMATIC_SLIDE_SHOW_REPEAT.is(),
                     Settings.EnumSettings.IMAGE_FILE_AUTOMATIC_SLIDE_SHOW_SECONDS.getInt()
                  );
                  break;
               case "announcement":
                  as = new AutomaticSettingsPanel.AutomaticSettings(
                     Settings.EnumSettings.AUTOMATIC_SLIDE_SHOW.is(),
                     Settings.EnumSettings.AUTOMATIC_SLIDE_SHOW_REPEAT.is(),
                     Settings.EnumSettings.AUTOMATIC_SLIDE_SHOW_SECONDS.getInt()
                  );
                  break;
               case "media-image":
                  as = new AutomaticSettingsPanel.AutomaticSettings(false, false, 6);
                  break;
               default:
                  as = new AutomaticSettingsPanel.AutomaticSettings();
            }

            this.mapAutomaticSettings.put(key, as);
            this.saveTrue();
         }

         return as;
      } catch (Exception var5) {
         return new AutomaticSettingsPanel.AutomaticSettings();
      }
   }

   public ScreenDeviceSettings getScreenDeviceSettings(int key) {
      try {
         if (this.mapScreenDeviceSettings == null) {
            this.mapScreenDeviceSettings = new HashMap<>();
         }

         ScreenDeviceSettings sds = this.mapScreenDeviceSettings.get(key);
         if (sds == null) {
            sds = new ScreenDeviceSettings();
            sds.setIndex(key);
            if (key == 1 && Settings.EnumSettings.SHOW_FOOTER.is()) {
               sds.setShowPageCount(true);
            }

            this.mapScreenDeviceSettings.put(key, sds);
            this.saveTrue();
         }

         return sds;
      } catch (Exception var3) {
         return new ScreenDeviceSettings();
      }
   }

   public String get(String key, String _default) {
      synchronized (MAP_LOCK) {
         return this.map.getOrDefault(key, _default);
      }
   }

   public void put(String key, String value) {
      synchronized (MAP_LOCK) {
         this.map.put(key, value);
         save = true;
      }
   }

   protected Settings() {
   }

   private static synchronized void save() {
      SettingsFactory.save(SET);
   }

   public void saveNow() {
      save = false;
      save();
      MusicFavoriteSetController.checkSave();
   }

   public void saveTrue() {
      save = true;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      if (this.version <= 1L) {
         this.map = new HashMap<>();
         this.version = 2L;
      }

      if (this.version <= 2L) {
         this.alertSettings = new AlertSettings();
         this.version = 3L;
      }

      if (this.version <= 3L) {
         this.clockSettings = new ClockSettings();
         this.version = 4L;
      }

      if (this.version <= 4L) {
         this.vlcSettings = new VLCSettings();
         this.version = 5L;
      }

      if (this.version <= 5L) {
         this.mapAutomaticSettings = new HashMap<>();
         this.mapScreenDeviceSettings = new HashMap<>();
         this.countdownSettings = new CountdownSettings();
         this.version = 6L;
      }

      if (this.version > 6L) {
         this.version = 6L;
      }

      this.refresh();
   }

   private void refresh() {
      this.music = Optional.ofNullable(MusicDao.get(this.music.getId())).orElse(MusicUtils.getDefault());
      this.theme = Optional.ofNullable(ThemeController.get(this.theme.id)).orElse(ThemeController.getDefault());
      this.account = Optional.ofNullable(this.account).orElse(new Account());
   }

   static {
      threadSave();
      Runtime.getRuntime().addShutdownHook(new Thread(SET::saveNow));
   }

   public static enum EnumLanguage {
      PT_BR("Português", "pt", "BR"),
      EN_US("English", "en", "US");

      public final String name;
      public final String language;
      public final String country;

      private EnumLanguage(String name, String language, String country) {
         this.name = name;
         this.language = language;
         this.country = country;
      }

      public static Locale getOrDefault(String locale) {
         try {
            for (Settings.EnumLanguage en : values()) {
               if (en.getLanguageAndCountry().equalsIgnoreCase(locale)) {
                  return new Locale(en.language, en.country);
               }
            }
         } catch (Exception var5) {
         }

         return Locale.getDefault();
      }

      @Override
      public String toString() {
         return this.name;
      }

      public String getLanguageAndCountry() {
         return this.language + "-" + this.country;
      }

      public static void setupJMenuBar(JMenu jMenuLanguage) {
         for (Settings.EnumLanguage en : values()) {
            JMenuItem item = new JMenuItem(en.toString());
            item.addActionListener(evt -> {
               MyProperties.EnumProperties.LOCALE.set(en.getLanguageAndCountry());
               Utils.msg(I18N.MSG_RESTART_TO_APPLY_CHANGES.val());
            });
            jMenuLanguage.add(item);
         }
      }
   }

   public static enum EnumSettings {
      LONGTIME_UPDATE_DISABLED(Long.class, 0L),
      SHOW_FOOTER(Boolean.class, false),
      MUSIC_UPPERCASE(Boolean.class, false),
      BIBLE_UPPERCASE(Boolean.class, false),
      BLANK_BETWEEN(Boolean.class, true),
      BIBLE_SHOW_VERSION(Boolean.class, true),
      BIBLE_FONT_SIZE(Integer.class, 16),
      BIBLE_FONT_NAME(String.class, BibleFontSettings.EnumFontName.BOOK_ANTIQUA.getFullName()),
      BIBLE_THEME(Long.class, -1L),
      SAVE_USERNAME(Boolean.class, true),
      BOOK_S_NAME_ABBREVIATED(Boolean.class, true),
      DISPLAY_ON_ALL_MONITORS(Boolean.class, true),
      DISPLAY_WALLPAPER(Boolean.class, false),
      STREAMING_ON(Boolean.class, false),
      BIBLE_VERSION_KEY(String.class, Locale.getDefault().getLanguage().equals("pt") ? "pt_acrf" : "en_kjv"),
      BIBLE_VERSION_KEY_2(String.class, Locale.getDefault().getLanguage().equals("pt") ? "pt_arib" : "en_bbe"),
      BIBLE_VERSION_KEY_3(String.class, Locale.getDefault().getLanguage().equals("pt") ? "pt_nvi" : "en_web"),
      WALLPAPER_IMAGE64(String.class, Images.loadAs64("defaultStreamingStandard.png", 0.5F)),
      PRESENTATION_CONTROL_RENDERER_MODE(String.class, "text"),
      PASSWORD_REMOTE_CONTROL_BIBLE(String.class, ""),
      PASSWORD_REMOTE_CONTROL_HOLYRICS(String.class, ""),
      PASSWORD_REMOTE_UPDATE(String.class, ""),
      ANNOUNCEMENTS(String.class, ""),
      AUTOMATIC_SLIDE_SHOW(Boolean.class, true),
      AUTOMATIC_SLIDE_SHOW_REPEAT(Boolean.class, true),
      AUTOMATIC_SLIDE_SHOW_SECONDS(Integer.class, 7),
      BIBLE_AUTOMATIC_SLIDE_SHOW(Boolean.class, true),
      BIBLE_AUTOMATIC_SLIDE_SHOW_REPEAT(Boolean.class, true),
      BIBLE_AUTOMATIC_SLIDE_SHOW_SECONDS(Integer.class, 7),
      BIBLE_AUTOMATIC_SHOW_THEME(Long.class, -1L),
      IMAGE_FILE_AUTOMATIC_SLIDE_SHOW(Boolean.class, false),
      IMAGE_FILE_AUTOMATIC_SLIDE_SHOW_REPEAT(Boolean.class, false),
      IMAGE_FILE_AUTOMATIC_SLIDE_SHOW_SECONDS(Integer.class, 6),
      IMAGE_FILE_AUTOMATIC_SLIDE_SHOW_LAST_PATH(String.class, ""),
      QUICK_PRESENTATION_AUTOMATIC(Boolean.class, false),
      QUICK_PRESENTATION_REPEAT(Boolean.class, true),
      QUICK_PRESENTATION_SECONDS(Integer.class, 7),
      QUICK_PRESENTATION_THEME(Long.class, -1L),
      DATETIME_LAST_REGISTER(Long.class, 0L),
      SYNC_CHECK_DOWNLOAD(Boolean.class, true),
      SYNC_CHECK_UPLOAD(Boolean.class, true),
      EVENT_TABLE_DISPLAY_ONLY_ENABLED(Boolean.class, true),
      SCHEDULE_VIEW_DISPLAY_ONLY_ENABLED_WORSHIP(Boolean.class, true),
      SCHEDULE_VIEW_DISPLAY_ONLY_ENABLED_MEMBER(Boolean.class, true),
      QUICK_PRESENTATION_TEXT(String.class, ""),
      THEME_SAMPLE_TEXT(
         String.class,
         "Holyrics - Lima Giran\nDivulgue, compartilhe\n(Este programa é gratuito)\nInstale o aplicativo no seu celular\nTeste de fonte: AaBbCc 1234567890\nÁáÉéÍíÓóÚú ÂâÔôÃãÔôÇç"
      ),
      FIRST_OPEN(Boolean.class, true),
      FIRST_OPEN_BIBLE(Boolean.class, true),
      BIBLE_VERSE_DESCRIPTION(String.class, EnumVerseDescription.TOP_SLIDE_DARKENED),
      BIBLE_VERSE_DESCRIPTION_FULL(Boolean.class, true),
      BIBLE_VERSE_EMPTY_LINE_BETWEEN(Boolean.class, true),
      BIBLE_SIZE_VERSE_PRESENTATION(Integer.class, 1),
      MUSIC_FILTER_EXPANDED(Boolean.class, false),
      FOOTER_PANEL_NUMBER_OF_LINES(Integer.class, 1),
      SHOW_UPDATE_NEWS(Boolean.class, false),
      NONE(String.class, ""),
      FIX_UPDATE_1_3_0(String.class, "");

      public final Class clazz;
      public final Object _default;

      private EnumSettings(Class clazz, Object _default) {
         this.clazz = clazz;
         this._default = _default;
      }

      public boolean is() {
         Boolean b = Settings.SET.mapB.get(this);
         return b != null ? b : this.getDefault(Boolean.class, Boolean.FALSE);
      }

      public String getString() {
         String s = Settings.SET.mapS.get(this);
         return s != null ? s : this.getDefault(String.class, "");
      }

      public long getLong() {
         Long l = Settings.SET.mapL.get(this);
         return l != null ? l : this.getDefault(Long.class, 0L);
      }

      public int getInt() {
         Integer i = Settings.SET.mapI.get(this);
         return i != null ? i : this.getDefault(Integer.class, 0);
      }

      private <T> T getDefault(Class<T> clazz, T _default) {
         try {
            return (T)(clazz.isInstance(this._default) ? this._default : _default);
         } catch (Exception var4) {
            return _default;
         }
      }

      public <T> T set(T value) {
         if (String.class.isInstance(value)) {
            Settings.SET.mapS.put(this, (String)value);
         } else if (Boolean.class.isInstance(value) || boolean.class.isInstance(value)) {
            Settings.SET.mapB.put(this, Boolean.valueOf(value.toString()));
         } else if (Long.class.isInstance(value) || long.class.isInstance(value)) {
            Settings.SET.mapL.put(this, Long.parseLong(value.toString()));
         } else if (Integer.class.isInstance(value) || int.class.isInstance(value)) {
            Settings.SET.mapI.put(this, Integer.parseInt(value.toString()));
         }

         Settings.save = true;
         return value;
      }
   }

   public static enum EnumSettings2 {
      CREATE_IMAGE_TEXT(String.class, ""),
      FADE_IN_OUT(Boolean.class, true),
      CREATE_IMAGE_THEME(Long.class, -1L),
      ARTIST_UNDERLINE(Boolean.class, true),
      BREAK_LINE(Boolean.class, false),
      HISTORY_SHOW(Boolean.class, true),
      PLAYER_VIEW_EXPANDED(Boolean.class, true),
      BIBLE_PLAY_LIST(String.class, ""),
      FADE_IN_OUT_DURATION(Integer.class, 600),
      WEB_SERVICE_IP_LAST_NETWORK_NAME(String.class, ""),
      VLC_PLAYLIST(String.class, ""),
      VLC_UTILS_LAST_DIR_MEDIA(String.class, ""),
      VLC_UTILS_LAST_DIR_PLAYLIST(String.class, ""),
      FAVORITE_PANEL_VISIBLE(Boolean.class, true),
      FAVORITE_ENUM_SIZE_NAME(String.class, ""),
      FAVORITE_SINGLE_LINE(Boolean.class, true),
      SECOND_MONITOR_INDEX(Integer.class, 0),
      ADD_TO_FAVORITE_IS_START(Boolean.class, true),
      TEXT_FORMATTING_ENABLED(Boolean.class, true),
      TEXT_FORMATTING_DEFAULT_FORMAT(String.class, "15ni"),
      MUSIC_ORDER_SLIDE_SIZE(Integer.class, 1),
      PLAYBACK_SLIDE_SIZE(Integer.class, 1),
      MUSIC_SEARCH_PANEL_CHECKED_FIELDS(String.class, ";getTitle;getArtist;getNote;"),
      MUSIC_SEARCH_PANEL_OFFLINE_CHECKED_FIELDS(String.class, ";getTitle;getArtist;"),
      WEB_SERVICE_ENABLED(Boolean.class, true),
      MEDIA_TAB_FIRST_CLICK(Boolean.class, true),
      PRINT_LAST_DIR(String.class, ""),
      LAST_DIR(String.class, ""),
      PASSWORD_REMOTE_CONTROL_MEDIA_HOLYRICS(String.class, ""),
      ADMIN_PANEL_ALERT_TEXT(String.class, ""),
      ADMIN_PANEL_PRESENTATION_TEXT(String.class, ""),
      ADMIN_PANEL_PRESENTATION_THEME(Long.class, -1L),
      ADMIN_PANEL_PRESENTATION_DISPLAY_AHEAD(Boolean.class, false),
      ADMIN_PANEL_SHOW_CLOCK_BG(Boolean.class, false),
      ADMIN_PANEL_SHOW_CLOCK_ALERT(Boolean.class, false),
      LIVE_VIEW_AUTO_DISPLAY(Boolean.class, false),
      LIVE_VIEW_AUTO_HIDE(Boolean.class, false),
      LIVE_VIEW_SIZE(String.class, "null"),
      SHOW_UPDATE_2_0_0_FEEDBACK(Boolean.class, false),
      FIRST_OPEN_2_0_0(Boolean.class, true),
      THUMB_UPDATE_2_1_0(Boolean.class, false),
      PLAYBACK_SELECT_AUDIO_FILE(String.class, ""),
      SHOW_HELP_PLAYBACK_EDIT(Boolean.class, true),
      SHOW_HELP_PLAYBACK_CAPTURE_METHOD(Boolean.class, true),
      CHAT_USERNAME(String.class, "Admin"),
      CHAT_NOTIFICATION(Boolean.class, true),
      FOOTER_CONTROL_MINIMIZE(Boolean.class, false),
      FOOTER_CONTROL_SHOW_THEME_LIST(Boolean.class, false),
      FOOTER_CONTROL_SHOW_THEME_LIST_ROWS(Integer.class, 1),
      BIBLE_SHOW_ONLY_REFERENCE(Boolean.class, false);

      public final Class clazz;
      public final Object _default;

      private EnumSettings2(Class clazz, Object _default) {
         this.clazz = clazz;
         this._default = _default;
      }

      public boolean is() {
         Boolean b = Settings.SET.mapB.get(this.name());
         return b != null ? b : this.getDefault(Boolean.class, Boolean.FALSE);
      }

      public String getString() {
         String s = Settings.SET.mapS.get(this.name());
         return s != null ? s : this.getDefault(String.class, "");
      }

      public long getLong() {
         Long l = Settings.SET.mapL.get(this.name());
         return l != null ? l : this.getDefault(Long.class, 0L);
      }

      public int getInt() {
         Integer i = Settings.SET.mapI.get(this.name());
         return i != null ? i : this.getDefault(Integer.class, 0);
      }

      private <T> T getDefault(Class<T> clazz, T _default) {
         try {
            return (T)(clazz.isInstance(this._default) ? this._default : _default);
         } catch (Exception var4) {
            return _default;
         }
      }

      public <T> T set(T value) {
         if (String.class.isInstance(value)) {
            Settings.SET.mapS.put(this.name(), (String)value);
         } else if (Boolean.class.isInstance(value) || boolean.class.isInstance(value)) {
            Settings.SET.mapB.put(this.name(), Boolean.valueOf(value.toString()));
         } else if (Long.class.isInstance(value) || long.class.isInstance(value)) {
            Settings.SET.mapL.put(this.name(), Long.parseLong(value.toString()));
         } else if (Integer.class.isInstance(value) || int.class.isInstance(value)) {
            Settings.SET.mapI.put(this.name(), Integer.parseInt(value.toString()));
         }

         Settings.save = true;
         return value;
      }
   }
}
