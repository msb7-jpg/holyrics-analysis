package com.limagiran.holyrics.share;

import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.BackgroundVideo;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.Log;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.SQLite;
import com.limagiran.holyrics.util.Security;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.webservice.HolyricsWS;
import com.limagiran.holyrics.zip.Zip;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ShareFilesApp implements Serializable {
   private static final String KEY_OBJ = "$%#@keyOBJ*&¨123";
   private static final List<String> FILES_NAME = Arrays.asList("profiles", "schedule", "events", "worship", "staff", "history");
   private static final String MUSIC_DB = "musics_database.db";
   private static final String MUSIC_LIST = "music_list";
   private static final String THEME_LIST = "theme_list";
   public final Map<String, String> map = new HashMap<>();

   public String toJSon() {
      return HolyricsWS.GSON.toJson(this);
   }

   public static ShareFilesApp create() {
      try {
         ShareFilesApp _return = new ShareFilesApp();
         FILES_NAME.forEach(name -> {
            Object o = RWObj.read(new File(Values.PATH_FILES, name + ".hly"), Object.class, "&p4ssS3sp");
            byte[] bytesx = RWObj.getBytes(o);
            _return.map.put(name + ".hly", Utils.bytesToBase64(bytesx));
         });
         TreeSet<ShareFilesApp.MusicApp.Music> treeSet = new TreeSet<>(ShareFilesApp.MusicApp.Music.sort());
         MusicDao.getAll().stream().map(m -> new ShareFilesApp.MusicApp.Music(m)).forEach(treeSet::add);
         ShareFilesApp.MusicApp musicList = new ShareFilesApp.MusicApp(new ArrayList<>(treeSet));
         byte[] compressed = Utils.compress(musicList.getJson());
         _return.map.put("music_list", Utils.bytesToBase64(compressed));
         byte[] bytes = RWObj.getBytes(MusicDao.getAll());
         byte[] bytesZip = Zip.zipBytes(bytes);
         byte[] encrypted = Security.encryptBytes(bytesZip, "$%#@keyOBJ*&¨123");
         String base64 = Utils.bytesToBase64(encrypted);
         _return.map.put("musics_database.db", base64);
         ShareFilesApp.ThemeList themeList = new ShareFilesApp.ThemeList(new ArrayList<>());
         ThemeController.getThemes().stream().filter(t -> !BackgroundVideo.class.isInstance(t.getBackground())).forEach(themeList::put);
         _return.map.put("theme_list", themeList.getJSon());
         return _return;
      } catch (Exception var9) {
         return new ShareFilesApp();
      }
   }

   public String getHash() {
      try {
         return Security.getSHA256(this.toJSon());
      } catch (Exception var2) {
         return "";
      }
   }

   public void loadFiles() {
      this.map.forEach((key, value) -> {
         if (key.endsWith(".hly")) {
            File f = new File(Values.PATH_FILES + key);
            f.delete();

            try (FileOutputStream fos = new FileOutputStream(f)) {
               byte[] bytes = Utils.base64ToBytes(value);
               fos.write(Security.encryptBytes(bytes, "&p4ssS3sp"));
            } catch (Exception var17) {
               Log.send(var17);
            }
         } else if (key.equalsIgnoreCase("musics_database.db")) {
            File db = new File(SQLite.DIR_DB.getAbsolutePath());
            File backup = new File(db.getParentFile(), "database_backup.db");
            db.renameTo(backup);
            BooleanOptional.runIfTrue(SQLite.DIR_DB.exists(), SQLite.DIR_DB::delete);

            try {
               byte[] encrypted = Utils.base64ToBytes(value);
               byte[] bytesZip = Security.decryptBytes(encrypted, "$%#@keyOBJ*&¨123");
               byte[] bytes = Zip.unzipBytes(bytesZip);
               List<com.limagiran.holyrics.model.Music> list = RWObj.read(bytes, List.class);
               if (list == null || !SQLite.open()) {
                  throw new Exception();
               }

               MusicDao.updateDatabase(list);
            } catch (Exception var18) {
               backup.renameTo(db);
               Log.send(var18);
               Utils.error(I18N.MSG_ERROR_CORRUPTED_FILES.val());
            }
         }
      });
   }

   public static class MusicApp {
      public final List<ShareFilesApp.MusicApp.Music> list;

      public MusicApp(List<ShareFilesApp.MusicApp.Music> list) {
         this.list = list;
      }

      public String getJson() {
         return HolyricsWS.GSON.toJson(this);
      }

      public void put(com.limagiran.holyrics.model.Music m) {
         this.list.add(new ShareFilesApp.MusicApp.Music(m));
      }

      public static class Music {
         public final transient String aToSearch;
         public final transient String tToSearch;
         public final long id;
         public final String a;
         public final String t;
         public final String n;
         public final String o;
         public final String l;
         public final String f;

         public Music(com.limagiran.holyrics.model.Music m) {
            this.id = m.getId();
            this.a = m.getArtist();
            this.t = m.getTitle();
            this.n = m.getNote();
            this.o = getOrder(m);
            this.l = m.getLyrics();
            this.f = m.getFormatting();
            this.aToSearch = Utils.removeAccent(this.a);
            this.tToSearch = Utils.removeAccent(this.t);
         }

         public static Comparator<ShareFilesApp.MusicApp.Music> sort() {
            return (m1, m2) -> {
               if (m1 == null) {
                  return m2 == null ? 0 : -1;
               } else if (m2 == null) {
                  return 1;
               } else {
                  int value = m1.tToSearch.compareToIgnoreCase(m2.tToSearch);
                  if (value != 0) {
                     return value;
                  } else {
                     value = m1.aToSearch.compareToIgnoreCase(m2.aToSearch);
                     return value != 0 ? value : Long.compare(m1.id, m2.id);
                  }
               }
            };
         }

         private static String getOrder(com.limagiran.holyrics.model.Music m) {
            return m.getOrder().stream().map(String::valueOf).collect(Collectors.joining(";"));
         }
      }
   }

   public static class ThemeList {
      public final List<ShareFilesApp.ThemeList.Theme> list;

      public ThemeList(List<ShareFilesApp.ThemeList.Theme> list) {
         this.list = list;
      }

      public void put(com.limagiran.holyrics.model.theme.Theme theme) {
         ShareFilesApp.ThemeList.Theme t = create(theme);
         if (t != null) {
            this.list.add(t);
         }
      }

      public static ShareFilesApp.ThemeList.Theme create(com.limagiran.holyrics.model.theme.Theme theme) {
         try {
            String id = String.valueOf(theme.id);
            BufferedImage bi = PresentationSlideUtils.createImage(null, "Holyrics\nLima Giran", theme, new Dimension(448, 252), true);
            String preview = Utils.bytesToBase64(Img.getBytesJPG(bi, 0.7F));
            String theme64 = Utils.bytesToBase64(RWObj.getBytes(theme.toSerialMap(theme)));
            if (theme64.length() < 1048576) {
               return new ShareFilesApp.ThemeList.Theme(id, preview, theme64);
            }
         } catch (Exception var5) {
         }

         return null;
      }

      public String getJSon() {
         return HolyricsWS.GSON.toJson(this);
      }

      public static class Theme {
         public final String id;
         public final String preview;
         public final String theme64;

         public Theme(String id, String preview, String theme64) {
            this.id = id;
            this.preview = preview;
            this.theme64 = theme64;
         }
      }
   }
}
