package com.limagiran.holyrics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;

public class MSOfficeUtils {
   public static List<String> readPPTX(InputStream is) throws Exception {
      List<String> _return = new ArrayList<>();
      XMLSlideShow ppt = new XMLSlideShow(is);
      XSLFSlide[] slide = ppt.getSlides();

      for (XSLFSlide s : slide) {
         _return.add(formatPowerPointXML(s.getXmlObject()));
      }

      return _return;
   }

   public static List<String> readPPT(File f) throws Exception {
      List<String> _return = new ArrayList<>();

      try (FileInputStream fis = new FileInputStream(f)) {
         POIFSFileSystem fs = new POIFSFileSystem(fis);
         HSLFSlideShow show = new HSLFSlideShow(fs);
         SlideShow ss = new SlideShow(show);
         Slide[] slides = ss.getSlides();

         for (Slide slide : slides) {
            TextRun[] runs = slide.getTextRuns();
            List<String> rows = new ArrayList<>();

            for (TextRun run : runs) {
               rows.add(run.getText());
            }

            _return.add(rows.stream().collect(Collectors.joining("\n")));
         }
      }

      return _return;
   }

   public static List<String> readPowerPoint(File path) throws Exception {
      try (FileInputStream fis = new FileInputStream(path)) {
         return readPPTX(fis);
      } catch (Exception var15) {
         return readPPT(path);
      }
   }

   private static String formatPowerPointXML(CTSlide xml) {
      StringBuilder _return = new StringBuilder();
      String str = xml.toString();
      String pi = "♥";
      String pf = "♦";
      String ti = "♣";
      String tf = "♠";
      str = str.replace("<a:br>", "</a:p>").replace("</a:br>", "<a:p>");
      str = str.replace("\n", " ").replace("<a:p>", pi).replace("</a:p>", pf).replace("<a:t>", ti).replace("</a:t>", tf);
      Pattern p = Pattern.compile(pi + "[^" + pi + pf + "]*" + pf);
      Matcher m = p.matcher(str);
      List<String> groups = new ArrayList<>();

      while (m.find()) {
         groups.add(m.group());
      }

      for (String g : groups) {
         p = Pattern.compile(ti + "[^" + ti + tf + "]*" + tf);
         m = p.matcher(g);
         List<String> values = new ArrayList<>();

         while (m.find()) {
            values.add(m.group());
         }

         StringBuilder text = new StringBuilder();
         values.stream().forEach(text::append);
         _return.append("\n");
         _return.append(text.toString());
      }

      String r = _return.toString().replace("♠♣", " ").replace("♣", "").replace("♠", "");
      return r.isEmpty() ? "" : r.substring(1);
   }

   public static String readWord(File in) throws Exception {
      try {
         return readWordDocx(in);
      } catch (Exception var2) {
         return readWordDoc(in);
      }
   }

   public static String readWordDocx(File in) throws Exception {
      XWPFDocument docx = new XWPFDocument(new FileInputStream(in));
      XWPFWordExtractor we = new XWPFWordExtractor(docx);
      return we.getText();
   }

   public static String readWordDoc(File in) throws Exception {
      HWPFDocument docx = new HWPFDocument(new FileInputStream(in));
      WordExtractor we = new WordExtractor(docx);
      return we.getText();
   }
}
