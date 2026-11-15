package com.limagiran.holyrics.help;

import com.limagiran.holyrics.i18n.I18N;
import java.net.URL;

public enum EnumHelpPages {
   HOME("index", I18N.WORD_HOME.val()),
   HOME_MUSIC("music", I18N.WORD_MUSIC.val()),
   HOME_THEME("theme", I18N.WORD_THEME.val()),
   HOME_PLAYLIST("playlist", I18N.WORD_PLAY_LIST.val()),
   HOME_PROFILE("profile", I18N.WORD_PROFILE.val()),
   HOME_WORSHIP("worship", I18N.WORD_WORSHIP_EVENT.val()),
   HOME_EVENT("event", I18N.WORD_EVENT.val()),
   HOME_MEMBER("member", I18N.WORD_MEMBER.val()),
   HOME_SCHEDULE("schedule", I18N.WORD_SCHEDULE.val()),
   HOME_REPORT("report", I18N.WORD_REPORT.val()),
   HOME_HISTORY("history", I18N.WORD_HISTORY.val()),
   HOME_SHARE("share", I18N.WORD_SHARE.val()),
   HOME_BIBLE("bible", I18N.WORD_BIBLE.val()),
   HOME_ANNOUNCEMENT("announcement", I18N.WORD_ANNOUNCEMENT.val()),
   HOME_AUTOMATIC_AN("automatic_show", I18N.MSG_AUTOMATIC_SHOW.val()),
   HOME_SAVE_PRESENTATION("save_presentation", I18N.MSG_SAVE_PRESENTATION.val()),
   HOME_HOLYRICS_APP("holyrics_app", I18N.MSG_APP_ON_MOBILE.val()),
   HOME_SETTINGS("settings", I18N.WORD_SETTINGS.val()),
   HOME_QUESTIONS_AND_ANSWERS("questions_and_answers", I18N.MSG_QUESTIONS_AND_ANSWERS.val());

   private static final String URL_FORMAT = "/com/limagiran/holyrics/help/html/%s_%s.html";
   public final URL url;
   public final String name;

   private EnumHelpPages(String url, String name) {
      this.url = this.getClass().getResource(String.format("/com/limagiran/holyrics/help/html/%s_%s.html", url, "pt_BR"));
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }
}
