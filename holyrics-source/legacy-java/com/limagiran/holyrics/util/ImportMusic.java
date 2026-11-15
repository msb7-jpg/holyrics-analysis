package com.limagiran.holyrics.util;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.view.MusicView;
import com.limagiran.holyrics.view.PowerPointImportView;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImportMusic {
   private static String formatLyrics(String l) {
      while (l.contains("\n\n\n")) {
         l = l.replace("\n\n\n", "\n\n");
      }

      while (l.startsWith("\n")) {
         l = l.substring(1);
      }

      return l;
   }

   private static List<String> getContentInXMLTag(String text, String tagIni, String tagEnd) {
      List<String> _return = new ArrayList<>();

      int indexIni;
      int indexEnd;
      try {
         while ((indexIni = text.indexOf(tagIni)) != -1 && (indexEnd = text.indexOf(tagEnd, indexIni)) >= indexIni) {
            _return.add(text.substring(indexIni + tagIni.length(), indexEnd));
            text = text.substring(indexEnd + tagEnd.length());
         }
      } catch (Exception var6) {
      }

      if (_return.isEmpty()) {
         _return.add("");
      }

      return _return;
   }

   private static List<String> getContentInXMLTag(String text, String tag) {
      return getContentInXMLTag(text, "<" + tag + ">", "</" + tag + ">");
   }

   private static String max(String val, int maxLength) {
      return val.substring(0, Math.min(maxLength, val.length()));
   }

   private static Music create(String t, String a, String l) {
      return Music.create().setTitle(max(t, 80)).setArtist(max(a, 80)).setLyrics(max(l, 12000)).setLanguage("undefined");
   }

   private static Music create(String l) {
      return Music.create().setLyrics(max(l, 12000)).setLanguage("undefined");
   }

   private static ImportMusic.Response fromEasiSlides_XML(List<File> xml) {
      ImportMusic.Response _return = new ImportMusic.Response();
      xml.forEach(f -> {
         try {
            NodeList items = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f).getElementsByTagName("Item");

            for (int i = 0; i < items.getLength(); i++) {
               Node node = items.item(i);
               if (node.getNodeType() == 1) {
                  Element e = (Element)node;

                  try {
                     String l = formatLyrics(e.getElementsByTagName("Contents").item(0).getTextContent().replaceAll("\\[(chorus|\\d|prechorus)\\]", ""));
                     String t = e.getElementsByTagName("Title1").item(0).getTextContent();
                     String a = e.getElementsByTagName("Folder").item(0).getTextContent();
                     _return.musics.add(create(t, a, l));
                  } catch (Exception var9) {
                  }
               }
            }
         } catch (Exception var10) {
            _return.error.add(f);
         }
      });
      return _return;
   }

   private static ImportMusic.Response fromOpenSong_XML(List<File> xml) {
      ImportMusic.Response _return = new ImportMusic.Response();
      xml.forEach(f -> {
         try {
            NodeList items = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f).getElementsByTagName("song");

            for (int index = 0; index < items.getLength(); index++) {
               Node node = items.item(index);
               if (node.getNodeType() == 1) {
                  Element e = (Element)node;

                  try {
                     String content = formatLyrics(e.getElementsByTagName("lyrics").item(0).getTextContent());
                     String order = e.getElementsByTagName("presentation").item(0).getTextContent();
                     List<String> orderList = new ArrayList<>();
                     List<String> lines = new ArrayList<>(Arrays.asList(content.split("\n")));
                     LinkedHashMap<String, String> map = new LinkedHashMap<>();
                     String key = "";

                     while (!lines.isEmpty()) {
                        String line = lines.remove(0);
                        if (line.startsWith("[")) {
                           key = line;
                        } else if (!line.trim().isEmpty()) {
                           String p = map.getOrDefault(key, "");
                           map.put(key, (p.isEmpty() ? p : p + "\n") + line.trim());
                        }
                     }

                     Arrays.asList(order.split(" ")).stream().map(o -> "[" + o + "]").map(map::get).forEach(orderList::add);
                     String t = e.getElementsByTagName("title").item(0).getTextContent();
                     String a = e.getElementsByTagName("author").item(0).getTextContent();
                     String l = map.values().stream().collect(Collectors.joining("\n\n"));
                     Music m = create(t, a, l);
                     m.setOrder(orderList);
                     _return.musics.add(m);
                  } catch (Exception var17) {
                  }
               }
            }
         } catch (Exception var18) {
            _return.error.add(f);
         }
      });
      return _return;
   }

   private static ImportMusic.Response fromOpenLP_XML(List<File> xmlList) {
      ImportMusic.Response _return = new ImportMusic.Response();
      xmlList.forEach(xmlFile -> {
         try {
            String xml = RW.read(xmlFile);
            String title = getContentInXMLTag(xml, "title").get(0);
            String artist = getContentInXMLTag(xml, "author").get(0);
            if (artist.isEmpty()) {
               artist = getContentInXMLTag(xml, "<author type=\"music\">", "</author>").get(0);
            }

            String notes = getContentInXMLTag(xml, "<songbook name=\"", "\"/>").get(0);
            String orderTag = getContentInXMLTag(xml, "verseOrder").get(0);
            String lyricsTag = getContentInXMLTag(xml, "lyrics").get(0);
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            int count = 2;
            String name = "ex1";
            List<String> lines = new ArrayList<>();

            for (String s : lyricsTag.split("\n")) {
               s = s.trim().replace("<br/>", "\n");
               if (s.startsWith("</verse>")) {
                  map.put(name, String.join("\n", lines));
                  lines = new ArrayList<>();
               } else if (!s.trim().isEmpty()) {
                  if (!s.startsWith("<verse>") && !s.startsWith("<verse name=\"")) {
                     if (s.startsWith("<lines>")) {
                        lines.add(s.substring(7, s.length() - 8));
                     } else if (s.startsWith("<lines break=\"optional\">")) {
                        lines.add(s.substring(24, s.length() - 8));
                     }
                  } else {
                     name = s.startsWith("<verse>") ? "ex" + count++ : getContentInXMLTag(s, "<verse name=\"", "\">").get(0);
                  }
               }
            }

            String lyrics = map.values().stream().collect(Collectors.joining("\n\n"));
            List<String> order = new ArrayList<>();
            if (!orderTag.trim().isEmpty()) {
               Stream.of(orderTag.split(" ")).map(map::get).filter(sx -> Objects.nonNull(sx) && !sx.isEmpty()).forEach(order::add);
            }

            Music music = Music.create().setTitle(title).setArtist(artist).setNote(notes).setLyrics(lyrics);
            music.setOrder(order);
            _return.musics.add(music);
         } catch (Exception var16) {
            _return.error.add(xmlFile);
         }
      });
      return _return;
   }

   private static ImportMusic.Response fromCCLI_USR(List<File> usr) {
      ImportMusic.Response _return = new ImportMusic.Response();
      usr.forEach(f -> {
         try {
            Properties prop = new Properties();

            try (
               FileInputStream fis = new FileInputStream(f);
               InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            ) {
               prop.load(isr);
            }

            String t = prop.getProperty("Title");
            String a = prop.getProperty("Author");
            String l = formatLyrics(prop.getProperty("Words").replace("/n", "\n").replace("/t", "\n"));
            _return.musics.add(create(t, a, l));
         } catch (Exception var35) {
            _return.error.add(f);
         }
      });
      return _return;
   }

   public static void fromPowerPointManyInOne(Frame parent, Runnable callback) {
      Optional.ofNullable(Dir.windowsFile(I18N.WORD_IMPORT.val(), "PowerPoint File (*.pptx, *.ppt)", ".pptx", ".ppt"))
         .ifPresent(f -> PopUpWorker.exe(new MySwingWorker<String, Void>(parent, I18N.WORD_LOADING.val()) {
               protected String doInBackground() throws Exception {
                  try {
                     return "t" + MSOfficeUtils.readPowerPoint(f).stream().collect(Collectors.joining("\n\n"));
                  } catch (Exception var2) {
                     return "f" + I18N.MSG_ERROR_INVALID_FILE.format(f.getName());
                  }
               }

               @Override
               protected void done() {
                  super.done();
                  if (this.isOk()) {
                     PowerPointImportView.main(parent, this.getResult().substring(1), callback);
                  } else {
                     this.showResult();
                  }
               }
            }));
   }

   public static void fromPowerPointOneInOne(Frame parent, final Runnable callback) {
      final List<File> files = Dir.windowsFileMultiple(I18N.WORD_IMPORT.val(), "PowerPoint File (*.pptx, *.ppt)", ".pptx", ".ppt");
      if (!files.isEmpty()) {
         if (files.size() > 10) {
            Utils.error(parent, I18N.MSG_ERROR_MAX_FILES_X.format(10));
            fromPowerPointOneInOne(parent, callback);
         } else {
            PopUpWorker.exe(
               new MySwingWorker<List<String>, Void>(parent, I18N.WORD_LOADING.val()) {
                  protected List<String> doInBackground() throws Exception {
                     List<String> lyricsList = new ArrayList<>();
                     List<String> invalidFiles = new ArrayList<>();
                     files.stream().forEach(f -> {
                        try {
                           lyricsList.add(MSOfficeUtils.readPowerPoint(f).stream().collect(Collectors.joining("\n\n")));
                        } catch (Exception var4) {
                           invalidFiles.add(I18N.MSG_ERROR_INVALID_FILE.format(f.getName()).replace("§", ""));
                        }
                     });
                     lyricsList.add(0, invalidFiles.stream().collect(Collectors.joining("§")));
                     return lyricsList;
                  }

                  @Override
                  protected void done() {
                     super.done();

                     try {
                        List<String> response = this.get();
                        String error = response.remove(0);
                        if (!error.isEmpty()) {
                           for (String s : error.split("§")) {
                              Utils.error(this.owner, s);
                           }
                        }

                        IntStream.range(0, response.size())
                           .mapToObj(i -> Music.create((long)(-i)).setLyrics(response.get(i)))
                           .forEach(m -> MusicView.main(m, true, Settings.SET.getTheme(), callback));
                     } catch (Exception var7) {
                     }
                  }
               }
            );
         }
      }
   }

   public static void fromWordOneInOne(Frame parent, final Runnable callback) {
      final List<File> files = Dir.windowsFileMultiple(I18N.WORD_IMPORT.val(), "Word File (*.docx, *.doc)", ".docx", ".doc");
      if (!files.isEmpty()) {
         if (files.size() > 10) {
            Utils.error(parent, I18N.MSG_ERROR_MAX_FILES_X.format(10));
            fromWordOneInOne(parent, callback);
         } else {
            PopUpWorker.exe(
               new MySwingWorker<List<String>, Void>(parent, I18N.WORD_LOADING.val()) {
                  protected List<String> doInBackground() throws Exception {
                     List<String> lyricsList = new ArrayList<>();
                     List<String> invalidFiles = new ArrayList<>();
                     files.stream().forEach(f -> {
                        try {
                           lyricsList.add(MSOfficeUtils.readWord(f));
                        } catch (Exception var4) {
                           invalidFiles.add(I18N.MSG_ERROR_INVALID_FILE.format(f.getName()).replace("§", ""));
                        }
                     });
                     lyricsList.add(0, invalidFiles.stream().collect(Collectors.joining("§")));
                     return lyricsList;
                  }

                  @Override
                  protected void done() {
                     super.done();

                     try {
                        List<String> response = this.get();
                        String error = response.remove(0);
                        if (!error.isEmpty()) {
                           for (String s : error.split("§")) {
                              Utils.error(this.owner, s);
                           }
                        }

                        IntStream.range(0, response.size())
                           .mapToObj(i -> Music.create((long)(-i)).setLyrics(response.get(i)))
                           .forEach(m -> MusicView.main(m, true, Settings.SET.getTheme(), callback));
                     } catch (Exception var7) {
                     }
                  }
               }
            );
         }
      }
   }

   public static class Response {
      private final List<Music> musics = new ArrayList<>();
      public final List<File> error = new ArrayList<>();

      public List<Music> getMusics() {
         IntStream.range(0, this.musics.size()).forEach(i -> this.musics.get(i).setId((long)(-i)));
         return new ArrayList<>(this.musics);
      }
   }

   public static enum Type {
      EASISLIDES_XML("EasiSlides XML File", "xml"),
      OPENSONG_XML("OpenSong XML File", "xml"),
      OPENLP_XML("OpenLP XML File", "xml"),
      CCLI_USR("CCLI USR File", "usr");

      public final FileFilter filter;

      private Type(String desc, String... extensions) {
         this.filter = new FileNameExtensionFilter(desc, extensions);
      }

      public ImportMusic.Response importMusics(List<File> files) {
         switch (this) {
            case EASISLIDES_XML:
               return ImportMusic.fromEasiSlides_XML(files);
            case OPENSONG_XML:
               return ImportMusic.fromOpenSong_XML(files);
            case OPENLP_XML:
               return ImportMusic.fromOpenLP_XML(files);
            case CCLI_USR:
               return ImportMusic.fromCCLI_USR(files);
            default:
               return new ImportMusic.Response();
         }
      }
   }
}
