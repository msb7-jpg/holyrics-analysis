package com.limagiran.holyrics.util;

import com.limagiran.holyrics.i18n.I18N;

public enum EnumPlayList {
   PLAY_LIST(I18N.WORD_PLAY_LIST.val()),
   WORSHIP_OR_EVENT(I18N.WORD_WORSHIP_AND_EVENT.val());

   public static EnumPlayList selected = PLAY_LIST;
   public final String name;

   private EnumPlayList(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }
}
