package com.limagiran.holyrics.model;

import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class VideoHL implements Serializable {
   private static final long serialVersionUID = 12491L;
   public final int id = Long.hashCode(Utils.generateMillisecondId());
   public final String fileName;
   public final String hash;
   public final byte[] image;
   public final byte[] gif;
   private long version = 1L;

   public VideoHL(String fileName, String hash, byte[] image, byte[] gif) {
      this.fileName = fileName;
      this.hash = hash;
      this.image = image;
      this.gif = gif;
   }

   public File getFile() {
      return new File(Values.PATH_BACKGROUND_VIDEO + this.fileName);
   }

   public String getId() {
      return "VideoHL" + this.id;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }

   public void deleteVideo() {
      Utils.startThreadDaemon(() -> {
         while (this.getFile().exists()) {
            this.getFile().delete();
            Utils.sleep(3000L);
         }
      });
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof VideoHL ? ((VideoHL)obj).id == this.id : super.equals(obj);
   }

   @Override
   public int hashCode() {
      return 265 + this.id;
   }
}
