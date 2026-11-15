package com.limagiran.holyrics.event;

import com.limagiran.holyrics.model.announcement.AnnouncementTitlePanel;

public interface AnnouncementTitlePanelListener {
   void removed(AnnouncementTitlePanel var1);

   void selected(AnnouncementTitlePanel var1);

   int indexOf(AnnouncementTitlePanel var1);

   int titleCount();

   default void upward(AnnouncementTitlePanel an) {
      int i = this.indexOf(an);
      if (i > 0) {
         this.changeTitlePosition(i, i - 1);
      }
   }

   default void downward(AnnouncementTitlePanel an) {
      int i = this.indexOf(an);
      if (i >= 0 && i < this.titleCount() - 1) {
         this.changeTitlePosition(i, i + 1);
      }
   }

   void changeTitlePosition(int var1, int var2);

   boolean isLast(AnnouncementTitlePanel var1);

   boolean isFirst(AnnouncementTitlePanel var1);
}
