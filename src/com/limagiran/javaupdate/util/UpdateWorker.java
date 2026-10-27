package com.limagiran.javaupdate.util;

import com.limagiran.javaupdate.Update;
import java.awt.Window;

public class UpdateWorker extends MySwingWorker<String, String> {
   private final Update update;

   public UpdateWorker(Window owner, String msg, Update app) {
      super(owner, msg);
      this.update = app;
   }

   protected String doInBackground() throws Exception {
      Download d = Download.download(this.update.app.getUrlLinkApp());
      if (!d.isOk()) {
         return "f" + Update.MSG_ERROR_UPDATE;
      } else {
         String downloadLink = d.getText();
         d = Download.download(downloadLink, this.update.file, this.update.getHash());

         while (!d.isFinish()) {
            if (this.isCancelled()) {
               d.cancel();
               return null;
            }

            Utils.sleep(1000L);
            this.setProgress((int)d.getPercentage());
         }

         return !d.isOk() && !d.isCancelled() ? "f" + Update.MSG_ERROR_UPDATE : "t" + Update.MSG_RESTART;
      }
   }

   @Override
   protected void done() {
      super.done();
      this.showResult();
      if (this.isOk()) {
         this.update.app.runBeforeRestart();
         PopUpWorker.exe(new RestartWorker(this.owner, this.update), this.update.app.getAppName(), false);
      }
   }
}
