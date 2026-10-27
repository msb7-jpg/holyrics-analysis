package com.limagiran.holyrics.worker;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.share.ReceiveUpdateFromInternetDialog;
import com.limagiran.holyrics.share.ShareFilesApp;
import com.limagiran.holyrics.share.SharePHP;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.webservice.HolyricsWS;
import java.awt.Window;

public class UpdateFromInternetWorker extends MySwingWorker<String, Void> {
   private final String username;
   private final String accessKey;

   public UpdateFromInternetWorker(Window owner, String username, String accessKey) {
      super(owner, I18N.WORD_UPDATING___.val());
      this.username = username;
      this.accessKey = accessKey;
   }

   protected String doInBackground() throws Exception {
      try {
         String json = SharePHP.download(this.owner, this.username, this.accessKey);
         ((ShareFilesApp)HolyricsWS.GSON.fromJson(json, ShareFilesApp.class)).loadFiles();
         return "t";
      } catch (Exception var2) {
         return "f" + var2.getMessage();
      }
   }

   @Override
   protected void done() {
      super.done();
      if (!this.isCancelled()) {
         if (this.isOk()) {
            Utils.msg(I18N.MSG_UPDATE_SUCCESS.val());
            System.exit(1);
         } else {
            this.showResult();
            ReceiveUpdateFromInternetDialog.main(this.owner, this.username, this.accessKey);
         }
      }
   }
}
