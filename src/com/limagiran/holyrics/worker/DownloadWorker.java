package com.limagiran.holyrics.worker;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Download;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.zip.Zip;
import java.awt.Window;
import java.io.File;
import java.util.Optional;

public class DownloadWorker extends MySwingWorker<Void, String> {
   private final Download download;
   private final boolean zip;
   private final File path;
   private final Runnable callback;

   public DownloadWorker(Window owner, String url, File path, String hash, boolean zip, Runnable callback) {
      super(owner, "Downloading...");
      this.download = Download.download(url, null, hash);
      this.path = path;
      this.zip = zip;
      this.callback = callback;
   }

   protected Void doInBackground() throws Exception {
      while (!this.download.isFinish()) {
         this.publish(new String[]{"Downloading... "});
         this.setProgress((int)this.download.getPercentage());
         Utils.sleep(500L);
      }

      return null;
   }

   @Override
   protected void done() {
      super.done();
      if (this.isCancelled()) {
         this.download.cancel();
      } else if (this.download.isError()) {
         Utils.error(this.owner, this.download.getError().getMessage());
      } else {
         try {
            byte[] bytes = this.zip ? Zip.unzipBytes(this.download.getBytes()) : this.download.getBytes();
            Dir.saveBytes(bytes, this.path);
         } catch (Exception var2) {
            Utils.error(this.owner, I18N.MSG_ERROR_SAVING_FILE.val());
            return;
         }

         Optional.ofNullable(this.callback).ifPresent(r -> r.run());
      }
   }
}
