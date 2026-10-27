package com.limagiran.holyrics.model;

import com.google.gson.Gson;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Security;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.lgsaveopen.LGSaveOpen;
import com.limagiran.lgsaveopen.SerialMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.xml.bind.DatatypeConverter;

public class Music implements MusicController, LGSaveOpen<Music>, Serializable {
   private static final long serialVersionUID = 12491L;
   private static final String KEY_OBJ = "$%#@keyOBJ*&¨123";
   public static final Music BLANK = create();
   private long id;
   private String artist;
   private String title;
   private String lyrics;
   private List<Integer> order;
   private String note;
   private String language;
   private String formatting;
   private transient String[] formats;
   private transient String vagalumeLink;
   private long version = 2L;

   private Music() {
      this(0L, "", "", "", "", "", "");
   }

   private Music(long id, String artist, String title, String lyrics, String note, String language, String formatting) {
      this.id = id;
      this.artist = artist;
      this.title = title;
      this.lyrics = lyrics;
      this.note = note;
      this.language = language;
      this.formatting = formatting;
      this.order = new ArrayList<>();
   }

   @Override
   public final Music getInstance() {
      return this;
   }

   public static Music create() {
      return new Music(0L, "", "", "", "", "", "");
   }

   public static Music create(long id) {
      return new Music(id, "", "", "", "", "", "");
   }

   public static Music create(String bytes64) throws Exception {
      byte[] b = DatatypeConverter.parseBase64Binary(bytes64);

      Music var7;
      try (
         ByteArrayInputStream is = new ByteArrayInputStream(Security.decryptBytes(b, "$%#@keyOBJ*&¨123"));
         ObjectInputStream ois = new ObjectInputStream(is);
      ) {
         Object o = ois.readObject();
         var7 = o != null && o instanceof Music ? (Music)o : null;
      }

      return var7;
   }

   public String getSerial() throws Exception {
      String var5;
      try (
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
      ) {
         oos.writeObject(this);
         var5 = DatatypeConverter.printBase64Binary(Security.encryptBytes(baos.toByteArray(), "$%#@keyOBJ*&¨123"));
      }

      return var5;
   }

   public static Music create(byte[] bytes) throws Exception {
      Music var6;
      try (
         ByteArrayInputStream is = new ByteArrayInputStream(Security.decryptBytes(bytes, "$%#@keyOBJ*&¨123"));
         ObjectInputStream ois = new ObjectInputStream(is);
      ) {
         Object o = ois.readObject();
         var6 = o != null && o instanceof Music ? (Music)o : null;
      }

      return var6;
   }

   public byte[] getBytes() throws Exception {
      byte[] var5;
      try (
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
      ) {
         oos.writeObject(this);
         var5 = Security.encryptBytes(baos.toByteArray(), "$%#@keyOBJ*&¨123");
      }

      return var5;
   }

   public long getId() {
      return this.id;
   }

   public Music setId(long id) {
      this.id = id;
      return this;
   }

   public String getArtist() {
      return this.artist;
   }

   public Music setArtist(String artist) {
      this.artist = Optional.ofNullable(artist).orElse("");
      return this;
   }

   public String getTitle() {
      return this.title;
   }

   public Music setTitle(String title) {
      this.title = Optional.ofNullable(title).orElse("");
      return this;
   }

   public String getLyrics() {
      return this.getLyrics(false);
   }

   public String getLyrics(boolean upperCase) {
      return upperCase ? this.lyrics.toUpperCase() : this.lyrics;
   }

   public Music setLyrics(String lyrics) {
      this.lyrics = Optional.ofNullable(lyrics).orElse("");
      return this;
   }

   public String getNote() {
      return this.note;
   }

   public Music setNote(String note) {
      this.note = Optional.ofNullable(note).orElse("");
      return this;
   }

   public String getLanguage() {
      return this.language;
   }

   public Music setLanguage(String language) {
      this.language = Optional.ofNullable(language).orElse("");
      return this;
   }

   public String getFormatting() {
      return this.formatting != null ? this.formatting : "";
   }

   public Music setFormatting(String formatting) {
      this.formatting = Optional.ofNullable(formatting).orElse("");
      this.formatting = StringUtils.maxLength(this.formatting, 1024);
      this.formats = this.formatting.split("\n");
      return this;
   }

   public String getFormat(int paragraphIndex) {
      try {
         if (this.formats == null) {
            this.formats = this.getFormatting().split("\n");
         }

         return paragraphIndex < this.formats.length ? this.formats[paragraphIndex] : "";
      } catch (Exception var3) {
         return "";
      }
   }

   public List<String> getParagraphs() {
      return new ArrayList<>(Arrays.asList(this.getLyrics().split("\n\n")));
   }

   public MusicController.Paragraph getInitialParagraph() {
      List<String> ps = this.getParagraphs();
      return ps.isEmpty() ? new MusicController.Paragraph() : new MusicController.Paragraph(ps.get(0), this.getFormat(0)).setPageCount(1, ps.size());
   }

   public List<Integer> getOrder() {
      if (this.order == null) {
         this.order = new ArrayList<>();
      }

      return this.order;
   }

   @Override
   public String toString() {
      return this.title + " (" + this.artist + ")";
   }

   public String getMusic() {
      return this.getMusic(false);
   }

   public Music getInstanceToSearch() {
      return create(this.id)
         .setTitle(Utils.removeAccent(this.getTitle().toLowerCase()).replaceAll("(,|\\.|\\?|!)", ""))
         .setArtist(Utils.removeAccent(this.getArtist().toLowerCase()).replaceAll("(,|\\.|\\?|!)", ""))
         .setLyrics(Utils.removeAccent(this.getLyrics().toLowerCase().replace("\n", " ").replaceAll("(,|\\.|\\?|!)", " ").replace("  ", " ")))
         .setNote(Utils.removeAccent(this.getNote().toLowerCase()).replaceAll("(,|\\.|\\?|!)", ""));
   }

   public String getMusic(boolean withNote) {
      StringBuilder sb = new StringBuilder();
      sb.append(I18N.WORD_ARTIST).append(": ").append(this.artist).append("\n");
      sb.append(I18N.WORD_TITLE).append(": ").append(this.title).append("\n");
      if (withNote) {
         sb.append(I18N.WORD_NOTE).append(": ").append(this.note).append("\n");
      }

      sb.append("\n").append(this.getLyrics());
      return sb.toString();
   }

   public String getVagalumeLink() {
      return this.vagalumeLink;
   }

   public Music setVagalumeLink(String vagalumeLink) {
      this.vagalumeLink = vagalumeLink;
      return this;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof Music ? ((Music)o).id == this.id : super.equals(o);
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 47 * hash + (int)(this.id ^ this.id >>> 32);
      hash = 47 * hash + Objects.hashCode(this.artist);
      return 47 * hash + Objects.hashCode(this.title);
   }

   @Override
   public long getHashCode() {
      return (long)Music.class.getName().hashCode();
   }

   @Override
   public String getExtension() {
      return ".muf";
   }

   @Override
   public String getFileDesc() {
      return "Music File (*.muf)";
   }

   public SerialMap toSerialMap(Music t) throws Exception {
      SerialMap map = new SerialMap(this);
      map.put("main", new Gson().toJson(this));
      return map;
   }

   public Music createFromSerialMap(SerialMap sm) throws Exception {
      return ((Music)new Gson().fromJson(sm.get("main", String.class), Music.class)).setId(0L);
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      if (this.version <= 1L) {
         this.formatting = "";
         this.version = 2L;
      }

      if (this.version > 2L) {
         this.version = 2L;
      }
   }
}
