package com.limagiran.holyrics.playback;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.Utils;
import java.awt.Window;
import java.io.File;
import java.util.function.Consumer;

public class ExportMediaWorker extends MySwingWorker<File, Void> {
   private final PlayBack playback;
   private final Consumer<File> callback;

   public ExportMediaWorker(PlayBack playback, Consumer<File> callback, Window owner, String msg) {
      super(owner, msg);
      this.playback = playback;
      this.callback = callback;
   }

   public ExportMediaWorker(PlayBack playback, Consumer<File> callback, Window owner) {
      super(owner);
      this.playback = playback;
      this.callback = callback;
   }

   public ExportMediaWorker(PlayBack playback, Consumer<File> callback) {
      this(playback, callback, null);
   }

   public static void main(PlayBack playback, Consumer<File> callback, Window owner, String msg) {
      PopUpWorker.exe(new ExportMediaWorker(playback, callback, owner, msg), true);
   }

   public static void main(PlayBack playback, Consumer<File> callback, Window owner) {
      PopUpWorker.exe(new ExportMediaWorker(playback, callback, owner), true);
   }

   public static void main(PlayBack playback, Consumer<File> callback) {
      PopUpWorker.exe(new ExportMediaWorker(playback, callback, null), true);
   }

   protected File doInBackground() throws Exception {
      try {
         File file = File.createTempFile("holyrics-apmedia", ".mp3");
         file.deleteOnExit();
         this.playback.exportMedia(file);
         return file;
      } catch (Exception var2) {
         return null;
      }
   }

   @Override
   protected void done() {
      super.done();

      try {
         if (this.isCancelled()) {
            return;
         }

         File file = this.get();
         if (file == null) {
            Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
            return;
         }

         this.callback.accept(file);
      } catch (Exception var2) {
      }
   }
}
