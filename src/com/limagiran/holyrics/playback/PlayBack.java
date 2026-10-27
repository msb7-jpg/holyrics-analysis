package com.limagiran.holyrics.playback;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.Security;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.PresentationSlideView;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.xml.bind.DatatypeConverter;

public class PlayBack implements Serializable {
   private static final long serialVersionUID = 1L;
   public static int MIN_DURATION = 500;
   public static int MAX_DURATION = 999000;
   public static int MIN_DURATION_START_WITH = 0;
   public static int MAX_DURATION_START_WITH = 6000000;
   public static int DURATION_DEFAULT = 30000;
   public static int START_WITH_TITLE_AND_ARTIST = 0;
   public static int START_WITH_EMPTY = 1;
   public static int START_WITH_DEFAULT_DURATION = 2000;
   public static final int BYTES_OBJECT_INDEX_START = 0;
   public static final int BYTES_OBJECT_LENGTH = 262144;
   public static final int BYTES_MEDIA_INDEX_START = 262144;
   private static final Charset CHARSET = Charset.forName("UTF-8");
   private transient File file;
   public final Music music;
   private final List<Integer> durations = new ArrayList<>();
   private int startWith;
   private int startWithDuration;
   private static final transient Object RAF_LOCK = new Object();
   private transient volatile Object durationsLock;
   private static final String KEY_OBJ = "4321$%#@key&¨123";

   private PlayBack(Music music) {
      Music _music = RWObj.clone(music, Music.class);
      this.music = _music != null ? _music : Music.create().setTitle(I18N.WORD_ERROR.val()).setArtist(I18N.WORD_ERROR.val()).setLyrics(I18N.WORD_ERROR.val());
      this.music.setId(Long.MIN_VALUE);
      this.fillDefaultValues();
   }

   protected static PlayBack create(File file, Music music) {
      PlayBack playBack = new PlayBack(music);
      playBack.file = file;
      return playBack;
   }

   public static PlayBack load(File in) throws Exception {
      String bytes64 = read(in, 0, 262144);
      PlayBack playBack = Objects.requireNonNull(create(bytes64));
      playBack.file = in;
      return playBack;
   }

   public final void fillDefaultValues() {
      this.startWith = START_WITH_TITLE_AND_ARTIST;
      this.startWithDuration = START_WITH_DEFAULT_DURATION;
      this.durations.clear();
      if (this.music.getParagraphs().stream().noneMatch(p -> !p.isEmpty() && p.replace(" ", "").isEmpty())) {
         try {
            String lyrics = this.music.getLyrics();

            while (lyrics.startsWith("\n")) {
               lyrics = lyrics.substring(1);
            }

            this.music.setLyrics(" \n\n" + lyrics);
            String formatting = this.music.getFormatting();

            while (formatting.startsWith("\n")) {
               formatting = formatting.substring(1);
            }

            this.music.setFormatting(" \n" + formatting);
            List<Integer> order = this.music.getOrder();
            if (!order.isEmpty()) {
               for (int i = 0; i < order.size(); i++) {
                  Integer newValue = order.get(i) + 1;
                  order.set(i, newValue);
               }
            } else {
               IntStream.range(1, this.music.getParagraphs().size()).forEach(order::add);
            }

            order.add(0);
         } catch (Exception var6) {
         }
      }

      IntStream.range(0, this.music.getParagraphsInOrder().size()).forEach(ix -> this.durations.add(DURATION_DEFAULT));
   }

   public Music getMusic() {
      return this.music;
   }

   public synchronized Object getDurationsLock() {
      return this.durationsLock == null ? (this.durationsLock = new Object()) : this.durationsLock;
   }

   public List<Integer> getDurations() {
      synchronized (this.getDurationsLock()) {
         return new ArrayList<>(this.durations);
      }
   }

   public int getDuration(int i) {
      try {
         synchronized (this.getDurationsLock()) {
            return Utils.range(this.durations.get(i), MIN_DURATION, MAX_DURATION);
         }
      } catch (Exception var5) {
         return DURATION_DEFAULT;
      }
   }

   public void addDuration(int duration) {
      synchronized (this.getDurationsLock()) {
         this.durations.add(duration);
      }
   }

   public void removeDuration(int index) {
      synchronized (this.getDurationsLock()) {
         this.durations.remove(index);
      }
   }

   public void resetMusicAndDuration() {
      synchronized (this.getDurationsLock()) {
         this.durations.clear();
         List<Integer> order = this.music.getOrder();
         order.clear();
         List<String> paragraphs = this.music.getParagraphs();
         if (paragraphs.size() >= 1 && paragraphs.get(0).equals(" ")) {
            IntStream.range(1, paragraphs.size()).forEach(order::add);
            order.add(0);
         }

         IntStream.range(0, this.music.getParagraphsInOrder().size()).forEach(i -> this.durations.add(DURATION_DEFAULT));
      }
   }

   public void setDuration(List<Integer> durations) {
      synchronized (this.getDurationsLock()) {
         this.durations.clear();
         this.durations.addAll(durations);
      }
   }

   public void setOrderAndStartAt(List<int[]> list, int duration) {
      synchronized (this.getDurationsLock()) {
         try {
            List<Integer> order = this.music.getOrder();
            order.clear();
            this.durations.clear();

            for (int i = 0; i < list.size(); i++) {
               int[] array = list.get(i);
               if (i == 0) {
                  this.setStartWithDuration(array[1]);
               }

               int subtract = i + 1 == list.size() ? duration : list.get(i + 1)[1];
               order.add(array[0]);
               this.durations.add(subtract - array[1]);
            }
         } catch (Exception var9) {
         }
      }
   }

   public List<int[]> getOrderAndStartAt() {
      List<int[]> _return = new ArrayList<>();
      synchronized (this.getDurationsLock()) {
         try {
            int duration = this.getStartWithDuration();
            List<Integer> order = this.music.getOrder();
            int max = !order.isEmpty() ? Math.min(order.size(), this.durations.size()) : this.durations.size();

            for (int i = 0; i < max; i++) {
               _return.add(new int[]{!order.isEmpty() ? order.get(i) : i, duration});
               duration += this.durations.get(i);
            }
         } catch (Exception var8) {
         }

         return _return;
      }
   }

   public void startWithTitleAndArtist() {
      this.startWith = START_WITH_TITLE_AND_ARTIST;
   }

   public void startWithEmpty() {
      this.startWith = START_WITH_EMPTY;
   }

   public boolean isStartWithTitleAndArtist() {
      return this.startWith == START_WITH_TITLE_AND_ARTIST;
   }

   public boolean isStartWithEmpty() {
      return this.startWith == START_WITH_EMPTY;
   }

   public int getStartWithDuration() {
      return Utils.range(this.startWithDuration, MIN_DURATION_START_WITH, MAX_DURATION_START_WITH);
   }

   public void setStartWithDuration(int startWithDuration) {
      this.startWithDuration = startWithDuration;
   }

   public File getFile() {
      return this.file == null ? new File("mp3.mp3") : this.file;
   }

   public void setMedia(File media) throws Exception {
      synchronized (RAF_LOCK) {
         try (
            FileInputStream fis = new FileInputStream(media);
            RandomAccessFile raf = new RandomAccessFile(this.file, "rw");
         ) {
            raf.setLength(262144L + media.length());
            raf.seek(262144L);
            byte[] buf = new byte[2048];

            int i;
            while ((i = fis.read(buf)) >= 0) {
               raf.write(buf, 0, i);
            }
         }
      }
   }

   public int getIndexAt(long milli) {
      try {
         int _startWith = this.getStartWithDuration();
         int duration = _startWith;
         if (_startWith >= 250 && milli <= (long)_startWith) {
            return -1;
         } else {
            List<MusicController.Paragraph> paragraphs = this.music.getParagraphsToPresentation();
            int max = Math.max(this.durations.size(), paragraphs.size());
            synchronized (this.getDurationsLock()) {
               for (int i = 0; i < max; i++) {
                  duration += this.durations.get(i);
                  if (milli <= (long)duration) {
                     return i;
                  }
               }
            }

            return Integer.MIN_VALUE;
         }
      } catch (Exception var11) {
         return 0;
      }
   }

   public int getStartAt(int index) {
      try {
         if (index == -1) {
            return 0;
         } else {
            int _startWith = this.getStartWithDuration();
            int duration = _startWith;
            synchronized (this.getDurationsLock()) {
               for (int i = 0; i < this.durations.size(); i++) {
                  if (i == index) {
                     return duration;
                  }

                  duration += this.durations.get(i);
               }
            }

            return duration;
         }
      } catch (Exception var8) {
         return 0;
      }
   }

   public MusicController.Paragraph getSlideAt(int milli) {
      try {
         int _startWith = this.getStartWithDuration();
         int duration = _startWith;
         if (_startWith >= 250 && milli <= _startWith) {
            MusicController.Paragraph p = MusicController.Paragraph.create()
               .setPageCount(Integer.MAX_VALUE, Integer.MAX_VALUE)
               .setTitleAndArtist(" ")
               .setBoolean(MusicController.Paragraph.EnumParagraphIntValues.MUSIC_AUTOMATIC_SHOW, true);
            if (this.isStartWithEmpty()) {
               return p;
            }

            return p.setText(String.format("%s%n%s", this.music.getTitle(), this.music.getArtist())).setSlideType(PresentationSlideView.EnumSlideType.TITLE);
         }

         List<MusicController.Paragraph> paragraphs = this.music.getParagraphsToPresentation();
         int max = Math.max(this.durations.size(), paragraphs.size());
         synchronized (this.getDurationsLock()) {
            for (int i = 0; i < max; i++) {
               duration += this.durations.get(i);
               if (milli <= duration) {
                  return paragraphs.get(i).setBoolean(MusicController.Paragraph.EnumParagraphIntValues.MUSIC_AUTOMATIC_SHOW, true);
               }
            }
         }
      } catch (Exception var10) {
      }

      return MusicController.Paragraph.create()
         .setPageCount(Integer.MAX_VALUE, Integer.MAX_VALUE)
         .setTitleAndArtist(" ")
         .setBoolean(MusicController.Paragraph.EnumParagraphIntValues.MUSIC_AUTOMATIC_SHOW, true);
   }

   public MusicController.Paragraph getSlideAtFromIndex(int index) {
      try {
         if (index == Integer.MIN_VALUE) {
            return MusicController.Paragraph.create()
               .setPageCount(Integer.MAX_VALUE, Integer.MAX_VALUE)
               .setTitleAndArtist(" ")
               .setBoolean(MusicController.Paragraph.EnumParagraphIntValues.MUSIC_AUTOMATIC_SHOW, true);
         }

         if (index < 0) {
            MusicController.Paragraph p = MusicController.Paragraph.create()
               .setPageCount(Integer.MAX_VALUE, Integer.MAX_VALUE)
               .setTitleAndArtist(" ")
               .setBoolean(MusicController.Paragraph.EnumParagraphIntValues.MUSIC_AUTOMATIC_SHOW, true);
            if (this.isStartWithEmpty()) {
               return p;
            }

            return p.setText(String.format("%s%n%s", this.music.getTitle(), this.music.getArtist())).setSlideType(PresentationSlideView.EnumSlideType.TITLE);
         }

         List<MusicController.Paragraph> list = this.music.getParagraphsToPresentation();
         if (index < list.size()) {
            return list.get(index).setBoolean(MusicController.Paragraph.EnumParagraphIntValues.MUSIC_AUTOMATIC_SHOW, true);
         }
      } catch (Exception var3) {
      }

      return MusicController.Paragraph.create()
         .setPageCount(Integer.MAX_VALUE, Integer.MAX_VALUE)
         .setTitleAndArtist(" ")
         .setBoolean(MusicController.Paragraph.EnumParagraphIntValues.MUSIC_AUTOMATIC_SHOW, true);
   }

   public Presentation getPresentationAt(int milli, Theme theme) {
      return Presentation.create(this.getSlideAt(milli), theme);
   }

   public Presentation getPresentationAtFromIndex(int index, Theme theme) {
      return Presentation.create(this.getSlideAtFromIndex(index), theme);
   }

   @Override
   public String toString() {
      return this.file == null ? this.music.toString() : this.file.getName();
   }

   public void save() throws Exception {
      StringBuilder sb = new StringBuilder(Utils.bytesToBase64(this.getSerial().getBytes(CHARSET)));

      while (sb.length() < 262144) {
         sb.append(' ');
      }

      byte[] valueBytes = sb.substring(0, 262144).getBytes(CHARSET);
      byte[] bytes = new byte[262144];
      System.arraycopy(valueBytes, 0, bytes, 0, bytes.length);
      synchronized (RAF_LOCK) {
         try (RandomAccessFile raf = new RandomAccessFile(this.file, "rw")) {
            raf.seek(0L);
            raf.write(bytes);
         }
      }
   }

   public String getSerial() throws Exception {
      String var5;
      try (
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
      ) {
         oos.writeObject(this);
         var5 = DatatypeConverter.printBase64Binary(Security.encryptBytes(baos.toByteArray(), "4321$%#@key&¨123"));
      }

      return var5;
   }

   public static PlayBack create(String bytes64) throws Exception {
      byte[] b = DatatypeConverter.parseBase64Binary(bytes64);

      PlayBack var7;
      try (
         ByteArrayInputStream is = new ByteArrayInputStream(Security.decryptBytes(b, "4321$%#@key&¨123"));
         ObjectInputStream ois = new ObjectInputStream(is);
      ) {
         Object o = ois.readObject();
         var7 = o != null && o instanceof PlayBack ? (PlayBack)o : null;
      }

      return var7;
   }

   private static String read(File in, int off, int len) throws Exception {
      byte[] bytes = new byte[len];
      synchronized (RAF_LOCK) {
         try (RandomAccessFile raf = new RandomAccessFile(in, "r")) {
            raf.seek((long)off);
            raf.readFully(bytes, 0, bytes.length);
         }
      }

      String base64 = new String(bytes, CHARSET);
      return new String(Utils.base64ToBytes(base64), CHARSET);
   }

   public void exportMedia(File out) throws Exception {
      synchronized (RAF_LOCK) {
         try (
            FileOutputStream fos = new FileOutputStream(out);
            RandomAccessFile raf = new RandomAccessFile(this.file, "r");
         ) {
            raf.seek(262144L);
            byte[] buf = new byte[2048];

            int i;
            while ((i = raf.read(buf)) >= 0) {
               fos.write(buf, 0, i);
            }
         }
      }
   }
}
