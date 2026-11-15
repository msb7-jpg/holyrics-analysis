package com.limagiran.holyrics.event;

import com.limagiran.bible.Bible;

public interface BibleFastSearchListener {
   Bible getBible();

   void fastSearchVerseFound(int var1, int var2, int var3);
}
