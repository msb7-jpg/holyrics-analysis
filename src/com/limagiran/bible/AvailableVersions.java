package com.limagiran.bible;

import com.limagiran.bible.util.Dir;
import com.limagiran.bible.util.Log;
import com.limagiran.bible.util.Values;
import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

public class AvailableVersions implements Serializable {
   private static final long serialVersionUID = 12491L;
   private static final TreeSet<AvailableVersions> VERSIONS = new TreeSet<>((o1, o2) -> {
      boolean b1 = o1.getVersion().startsWith(Locale.getDefault().getLanguage().toLowerCase());
      boolean b2 = o2.getVersion().startsWith(Locale.getDefault().getLanguage().toLowerCase());
      return b1 == b2 ? o1.toString().compareToIgnoreCase(o2.toString()) : (b1 ? -1 : 1);
   });
   public static final Map<String, String> MAP_VERSIONS = new HashMap<>();
   public final File pathBible;
   public final String languageAndVersion;

   public static List<AvailableVersions> getVersions() {
      return new ArrayList<>(VERSIONS);
   }

   public static boolean addVersion(File path) {
      try {
         VERSIONS.add(new AvailableVersions(path));
         return true;
      } catch (Exception var2) {
         Log.send(var2);
         return false;
      }
   }

   private AvailableVersions(File pathBible) throws Exception {
      this.pathBible = pathBible;
      this.languageAndVersion = this.getLanguageAndVersion();
   }

   public final String getVersion() {
      return this.pathBible.getName().replace(".bib", "");
   }

   @Override
   public String toString() {
      return this.languageAndVersion;
   }

   public final Bible loadBible() throws Exception {
      return FactoryBible.create(this.pathBible);
   }

   private String getLanguageAndVersion() throws Exception {
      String v = this.getVersion();
      String lav;
      if (MAP_VERSIONS.get(v) != null) {
         lav = MAP_VERSIONS.get(v);
      } else if (!Values.getOrDefault(v, "").isEmpty()) {
         lav = Values.getOrDefault(v, "");
      } else {
         lav = FactoryBible.getLanguageAndVersion(this.pathBible);
         Values.put(v, lav);
      }

      return lav;
   }

   static {
      MAP_VERSIONS.put("en_asv", "(English) American Standard-ASV1901");
      MAP_VERSIONS.put("en_bbe", "(English) Bible in Basic English");
      MAP_VERSIONS.put("en_dby", "(English) Darby English Bible");
      MAP_VERSIONS.put("en_kjv", "(English) King James Version");
      MAP_VERSIONS.put("en_web", "(English) World English Bible");
      MAP_VERSIONS.put("en_ylt", "(English) Young's Literal Translation");
      MAP_VERSIONS.put("pt_acrf", "(Português) Almeida Corrigida e Revisada Fiel");
      MAP_VERSIONS.put("pt_arib", "(Português) Almeida Revisada Imprensa Bíblica");
      MAP_VERSIONS.put("pt_nvi", "(Português) Nova Versão Internacional");
      MAP_VERSIONS.put("pt_sbb", "(Português) Sociedade Bíblica Britânica");
      MAP_VERSIONS.put("pt_ara", "(Português) Almeida Revista e Atualizada");
      MAP_VERSIONS.put("pt_arc", "(Português) Almeida Revisada e Corrigida");
      MAP_VERSIONS.put("pt_ntlh", "(Português) Nova Tradução na Linguagem de Hoje");
      String url = "/com/limagiran/bible/database/?.bib";
      List<String> _default = Arrays.asList("pt_acrf");
      _default.stream().forEach(abbrev -> {
         File fx = new File(Values.PATH_DATABASE + abbrev + ".bib");
         if (!fx.exists()) {
            Dir.exportFromJar("/com/limagiran/bible/database/?.bib".replace("?", abbrev), fx);
         }
      });
      FileFilter ff = pathname -> pathname.getName().endsWith(".bib");

      for (File f : new File(Values.PATH_DATABASE).listFiles(ff)) {
         try {
            VERSIONS.add(new AvailableVersions(f));
         } catch (Exception var8) {
            Log.send(var8);
         }
      }
   }
}
