package com.limagiran.holyrics.worker;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.share.SharePHP;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.Security;
import com.limagiran.holyrics.view.ShareView;
import java.awt.Window;

public class UpdateAndExitWorker extends MySwingWorker<String, Void> {
   private final String json;

   public UpdateAndExitWorker(Window owner, String json) {
      super(owner);
      this.json = json;
   }

   protected String doInBackground() throws Exception {
      try {
         SharePHP.upload(Settings.SET.getAccount(), Security.getSHA256(this.json), this.json);
         return "t" + I18N.MSG_UPLOAD_SUCCESS.val();
      } catch (Exception var2) {
         return "f" + var2.getMessage();
      }
   }

   @Override
   protected void done() {
      super.done();
      this.showResult();
      if (this.isOk()) {
         System.exit(0);
      } else {
         ShareView.main(this.owner, () -> System.exit(0));
      }
   }
}
