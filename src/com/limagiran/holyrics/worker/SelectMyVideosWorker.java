package com.limagiran.holyrics.worker;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.VideoHL;
import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.FFMPEG;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class SelectMyVideosWorker extends MySwingWorker<String, String> implements FFMPEG.FFMPEGListener {
   private static final DecimalFormat DF_PERCENTAGE = new DecimalFormat("##0.0'%'");
   private String progress = I18N.WORD_WAITING___.val();
   private final Consumer<VideoHL> onSelected;
   private final FFMPEG ffmpeg = new FFMPEG();
   private final File file;
   private File videoFile;
   private long lastUpdateProgress = 0L;

   public SelectMyVideosWorker(Window owner, File file, Consumer<VideoHL> onSelected) {
      super(owner);
      this.onSelected = onSelected;
      this.file = file;
      this.init();
   }

   private void init() {
      int i = 1;
      this.videoFile = new File(Values.PATH_BACKGROUND_VIDEO + String.format("MY_VIDEO_%03d.mp4", i));

      while (this.videoFile.exists()) {
         this.videoFile = new File(Values.PATH_BACKGROUND_VIDEO + String.format("MY_VIDEO_%03d.mp4", ++i));
      }
   }

   protected String doInBackground() throws Exception {
      try {
         if (this.ffmpeg.duration(this.file) > 600) {
            return "f" + I18N.MSG_ERROR_LARGE_VIDEO.format(600);
         } else if (!this.file.exists()) {
            return "f" + I18N.MSG_ERROR_FILE_NOT_SELECTED.val();
         } else if (!this.ffmpeg.isH264(this.file)) {
            this.convert();
            return "true";
         } else {
            return this.getFrameAndGif();
         }
      } catch (Exception var2) {
         return "f" + I18N.MSG_FILE_ERROR.val();
      }
   }

   @Override
   protected void done() {
      super.done();
      BooleanOptional.runIfTrue(!this.isOk() && !this.isCancelled(), this::showResult);
   }

   @Override
   public void progress(double value) {
      if (System.currentTimeMillis() - this.lastUpdateProgress > 1000L) {
         this.lastUpdateProgress = System.currentTimeMillis();
         this.publish(new String[]{this.progress});
         this.setProgress((int)value);
      }
   }

   @Override
   public void progress(String msg) {
      this.progress = msg;
   }

   @Override
   public void error(String msg) {
      BooleanOptional.runIfTrue(!this.isCancelled(), () -> Utils.error(msg));
      this.cancel(true);
      this.videoFile.delete();
   }

   @Override
   public void success() {
   }

   private String getFrameAndGif() throws Exception {
      if (!Dir.copy(this.file, this.videoFile)) {
         return "f" + I18N.MSG_FILE_ERROR.val();
      } else {
         File frame = File.createTempFile("img", ".jpg");
         File gif = File.createTempFile("gif", ".gif");
         frame.deleteOnExit();
         gif.deleteOnExit();
         this.ffmpeg.getImage(this.videoFile, frame, new Dimension(1920, 1080), 1, this);
         this.ffmpeg.toGIF(this.videoFile, gif, new Dimension(160, 90), 0, 10, 5, this);
         VideoHL vb = new VideoHL(this.videoFile.getName(), Dir.getHash(this.videoFile), Dir.getBytes(frame), Dir.getBytes(gif));
         Background.createLongVideo(this.videoFile, this.videoFile.getName());
         this.onSelected.accept(vb);
         return "true";
      }
   }

   private void convert() {
      PopUpWorker.exe(new MySwingWorker<Void, String>(this.owner) {
         protected Void doInBackground() throws Exception {
            final File convertedVideo = File.createTempFile("convert", ".mp4");
            SelectMyVideosWorker.this.ffmpeg.convertToH264(SelectMyVideosWorker.this.file, convertedVideo, null, new FFMPEG.FFMPEGListener() {
               private String progress;

               @Override
               public void progress(double value) {
                  if (System.currentTimeMillis() - SelectMyVideosWorker.this.lastUpdateProgress > 1000L) {
                     SelectMyVideosWorker.this.lastUpdateProgress = System.currentTimeMillis();
                     publish(new String[]{this.progress + ": " + SelectMyVideosWorker.DF_PERCENTAGE.format(value)});
                  }
               }

               @Override
               public void progress(String msg) {
                  this.progress = msg;
               }

               @Override
               public void error(String msg) {
                  BooleanOptional.runIfTrue(!isCancelled(), () -> Utils.error(msg));
                  cancel(true);
                  convertedVideo.delete();
               }

               @Override
               public void success() {
                  PopUpWorker.exe(new SelectMyVideosWorker(owner, convertedVideo, SelectMyVideosWorker.this.onSelected));
               }
            });
            return null;
         }

         @Override
         protected void done() {
            super.done();
            BooleanOptional.runIfTrue(this.isCancelled(), SelectMyVideosWorker.this.ffmpeg::kill);
         }
      }, true);
   }
}
