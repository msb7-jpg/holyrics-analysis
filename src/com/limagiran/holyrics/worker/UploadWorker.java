package com.limagiran.holyrics.worker;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Account;
import com.limagiran.holyrics.share.ShareFilesApp;
import com.limagiran.holyrics.share.SharePHP;
import com.limagiran.holyrics.util.MySwingWorker;
import java.awt.Window;

public class UploadWorker extends MySwingWorker<String, String> {
   private final Account account;

   public UploadWorker(Window owner, Account account) {
      super(owner);
      this.account = account;
   }

   protected String doInBackground() throws Exception {
      try {
         ShareFilesApp files = ShareFilesApp.create();
         this.publish(new String[]{"Uploading..."});
         SharePHP.upload(this.account, files.getHash(), files.toJSon());
         return "t" + I18N.MSG_UPLOAD_SUCCESS.val();
      } catch (Exception var2) {
         return "f" + var2.getMessage();
      }
   }

   @Override
   protected void done() {
      super.done();
      this.showResult();
   }
}
