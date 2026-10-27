package com.limagiran.holyrics.worker;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.share.ShareFilesApp;
import com.limagiran.holyrics.util.Log;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.webservice.HolyricsWS;

public class UpdateFromJsonWorker extends MySwingWorker<Void, Void> {
   private final String json;

   public UpdateFromJsonWorker(String json) {
      super(null);
      this.json = json;
   }

   protected Void doInBackground() throws Exception {
      try {
         ((ShareFilesApp)HolyricsWS.GSON.fromJson(this.json, ShareFilesApp.class)).loadFiles();
      } catch (Exception var2) {
         Log.send(var2);
      }

      return null;
   }

   @Override
   protected void done() {
      super.done();
      Utils.msg(I18N.MSG_UPDATE_SUCCESS.val());
      System.exit(1);
   }
}
