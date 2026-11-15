package com.limagiran.holyrics.worker;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.share.ShareFilesApp;
import com.limagiran.holyrics.share.SharePHP;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.Utils;
import java.awt.Window;

public class CheckUploadAndExitWorker extends MySwingWorker<String, Void> {
   public CheckUploadAndExitWorker(Window owner) {
      super(owner);
   }

   protected String doInBackground() throws Exception {
      try {
         ShareFilesApp files = ShareFilesApp.create();
         String hashUpdate = SharePHP.getHashUpdate(Settings.SET.getAccount());
         if (hashUpdate.equals(files.getHash())) {
            System.exit(0);
         }

         return "t" + files.toJSon();
      } catch (Exception var3) {
         return "f" + var3.getMessage();
      }
   }

   @Override
   protected void done() {
      super.done();
      if (this.isOk()) {
         if (!Utils.confirm(I18N.MSG_CONFIRM_CHECK_SYNC_UPLOAD.val())) {
            System.exit(0);
         }

         PopUpWorker.exe(new UpdateAndExitWorker(this.owner, this.getResult().substring(1)));
      } else {
         System.exit(0);
      }
   }
}
