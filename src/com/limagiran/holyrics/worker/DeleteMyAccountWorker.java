package com.limagiran.holyrics.worker;

import com.limagiran.holyrics.model.Account;
import com.limagiran.holyrics.share.SharePHP;
import com.limagiran.holyrics.util.MySwingWorker;
import java.awt.Window;

public class DeleteMyAccountWorker extends MySwingWorker<String, Void> {
   private final Account account;

   public DeleteMyAccountWorker(Window owner, Account account) {
      super(owner);
      this.account = account;
   }

   protected String doInBackground() throws Exception {
      return SharePHP.deleteMyAccount(this.owner, this.account);
   }

   @Override
   protected void done() {
      super.done();
      this.showResult();
   }
}
