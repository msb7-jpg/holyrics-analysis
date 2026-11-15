package com.limagiran.bible.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public enum I18N {
   WORD_SEARCHING("word.searching"),
   MSG_TOOL_TIP_TEXT_JTEXT("msg.toolTipTextJText"),
   MSG_ERROR_CONNECT("msg.errorConnect"),
   WORD_WAITING___("word.waiting___"),
   WORD_SEARCH("word.search"),
   MSG_ERROR_TRY_AGAIN("msg.errorTryAgain"),
   MSG_CONTENT_VERY_LARGE_TEXT("msg.errorContVeryLarge"),
   MSG_REPORT_SUCCESS("msg.reportSuccess"),
   MSG_CONTENT_IS_EMPTY("msg.errorContIsEmpty"),
   MSG_TT_JLIST_TAB_RESULT("msg.toolTipTextJListResult"),
   MSG_RESULTS_X("msg.results_x");

   public static final String NAME = "com/limagiran/bible/i18n/Bundle";
   private static final ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/bible/i18n/Bundle", Locale.getDefault());
   private final String key;

   private I18N(String key) {
      this.key = key;
   }

   public String val() {
      return bundle.getString(this.key);
   }

   public String question() {
      return bundle.getString(this.key) + "?";
   }

   @Override
   public String toString() {
      return this.val();
   }

   public String format(Object... args) {
      return args != null && args.length > 0 ? MessageFormat.format(this.val(), args) : this.val();
   }
}
