package com.limagiran.holyrics.vlc;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Download;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.Utils;
import java.awt.Desktop;
import java.awt.Window;
import java.io.File;
import javax.swing.SwingUtilities;

public class VLCDownloadWorker extends MySwingWorker<String, String> {
   private static final String URL = "http://www.holyrics.com.br/download/app/VLCMediaPlayer.exe";
   private static final File FILE = new File("VLC-setup.exe");
   private Download d;

   private VLCDownloadWorker(Window owner) {
      super(owner, "Downloading...");
   }

   protected String doInBackground() throws Exception {
      try {
         this.d = Download.download("http://www.holyrics.com.br/download/app/VLCMediaPlayer.exe", FILE);

         while (!this.d.isFinish()) {
            Utils.sleep(500L);
            this.setProgress(Utils.range((int)this.d.getPercentage(), 0, 100));
         }

         if (this.d.isOk()) {
            return "t<html><span style='font-size:11px'>" + I18N.MSG_DOWNLOAD_VLC_SUCCESSFULLY.val();
         } else {
            throw this.d.getError();
         }
      } catch (Exception var2) {
         return "f" + I18N.MSG_ERROR_TRY_AGAIN.val() + "\n" + var2;
      }
   }

   @Override
   protected void done() {
      super.done();
      if (!this.isCancelled()) {
         this.showResult();
         if (this.isOk()) {
            try {
               Desktop.getDesktop().open(FILE);
            } catch (Exception var2) {
            }
         }
      } else if (this.d != null) {
         this.d.cancel();
      }
   }

   public static void download() {
      PopUpWorker worker = new PopUpWorker.PopUpWaitingBuilder().worker(new VLCDownloadWorker(null)).undecorated(false).cancellable(true).create();
      SwingUtilities.invokeLater(() -> worker.setVisible(true));
   }
}
