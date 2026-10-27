package com.limagiran.bible;

import com.google.gson.Gson;
import com.limagiran.bible.util.Security;
import com.limagiran.bible.util.Utils;
import java.io.File;
import java.util.Arrays;

public class FactoryBible {
   private static final String KEY_OBJ = Arrays.asList('$', '%', '#', '@', 'k', 'e', 'y', 'O', 'B', 'J', '*', '&', '¨', 'b', 'i', 'b')
      .stream()
      .map(c -> c.toString())
      .reduce("", String::concat);

   public static final Bible create(File path) throws Exception {
      return (Bible)saveOrCreate(FactoryBible.Type.LOAD_BIBLE, path);
   }

   public static final String getLanguageAndVersion(File path) throws Exception {
      return (String)saveOrCreate(FactoryBible.Type.GET_LANGUAGE_AND_VERSION, path);
   }

   public static final boolean save(Bible bible, File file) throws Exception {
      return (Boolean)saveOrCreate(FactoryBible.Type.SAVE, bible, file);
   }

   private static synchronized Object saveOrCreate(FactoryBible.Type en, Object... obj) throws Exception {
      Gson gson = new Gson();
      switch (en) {
         case LOAD_BIBLE: {
            byte[] bytes = Utils.getBytes((File)obj[0]);
            byte[] bytesDec = Security.decryptBytes(bytes, KEY_OBJ);
            String json = Utils.decompress(bytesDec);
            Bible bible = (Bible)gson.fromJson(json, Bible.class);
            json = Utils.removeAccent(json.toLowerCase());
            json = json.toLowerCase();
            json = json.replace("testamentabbrev", "testamentAbbrev");
            Bible bibleFind = (Bible)gson.fromJson(json, Bible.class);
            bible.setBibleFind(bibleFind);
            bible.verses.parallelStream().forEach(v -> v.setBible(bible));
            bible.books.stream().forEach(b -> b.setBible(bible));
            bibleFind.verses.parallelStream().forEach(v -> v.setBible(bible));
            bibleFind.books.stream().forEach(b -> b.setBible(bible));
            return bible;
         }
         case SAVE: {
            Bible bible = (Bible)obj[0];
            byte[] bytes = Utils.compress(gson.toJson(bible));
            byte[] bytesEnc = Security.encryptBytes(bytes, KEY_OBJ);
            File path = (File)obj[1];
            File temp = new File(path.getParent() + File.separator + "tmp.rw");

            try {
               Utils.write(temp, bytesEnc);
               if (path.delete()) {
                  temp.renameTo(path);
               }
            } finally {
               temp.delete();
            }

            return true;
         }
         case GET_LANGUAGE_AND_VERSION: {
            byte[] bytes = Utils.getBytes((File)obj[0]);
            byte[] bytesDec = Security.decryptBytes(bytes, KEY_OBJ);
            String json = Utils.decompress(bytesDec);
            Bible bible = (Bible)gson.fromJson(json, Bible.class);
            return String.format("(%s) %s", bible.language, bible.version);
         }
         default:
            throw new Exception();
      }
   }

   private static enum Type {
      LOAD_BIBLE,
      GET_LANGUAGE_AND_VERSION,
      SAVE;
   }
}
