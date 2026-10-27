package com.limagiran.holyrics.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.limagiran.bible.Verse;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.zip.Zip;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

public class SaveSlides {
   private static void savePDF(Window owner, final File file, final SaveSlides.ISave save, final Dimension size) {
      PopUpWorker.exe(new MySwingWorker<String, Void>(owner) {
         protected String doInBackground() throws Exception {
            try {
               Document document = new Document();
               document.setMargins(0.0F, 0.0F, 0.0F, 0.0F);
               document.setPageSize(new Rectangle((float)size.width, (float)size.height));

               try (FileOutputStream fos = new FileOutputStream(file)) {
                  PdfWriter.getInstance(document, fos);
                  document.open();

                  for (Zip.Entry img : save.getImages()) {
                     document.add(Image.getInstance(img.bytes));
                  }

                  document.close();
               }

               return "t" + I18N.MSG_SAVED_SUCCESSFULLY.val();
            } catch (Exception var16) {
               file.delete();
               return "f" + I18N.MSG_ERROR_TRY_AGAIN.val() + "\n" + var16.getMessage();
            }
         }

         @Override
         protected void done() {
            super.done();
            this.showResult();
         }
      });
   }

   private static void saveImage(Window owner, final File file, final SaveSlides.ISave save) {
      PopUpWorker.exe(new MySwingWorker<String, Void>(owner) {
         protected String doInBackground() throws Exception {
            try {
               Zip.zipBytes(save.getImages(), file);
               return "t" + I18N.MSG_SAVED_SUCCESSFULLY.val();
            } catch (Exception var2) {
               return "f" + I18N.MSG_ERROR_TRY_AGAIN.val() + "\n" + var2.getMessage();
            }
         }

         @Override
         protected void done() {
            super.done();
            this.showResult();
         }
      });
   }

   private static void savePowerPoint(Window owner, final File file, final SaveSlides.ISave save, final Dimension size) {
      PopUpWorker.exe(new MySwingWorker<String, Void>(owner) {
         protected String doInBackground() throws Exception {
            try {
               XMLSlideShow ppt = new XMLSlideShow();

               for (Zip.Entry img : save.getImages()) {
                  XSLFSlide slide = ppt.createSlide();
                  ppt.setPageSize(size);
                  int idx = ppt.addPicture(img.bytes, 5);
                  slide.createPicture(idx);
               }

               try (FileOutputStream out = new FileOutputStream(file)) {
                  ppt.write(out);
               }

               return "t" + I18N.MSG_SAVED_SUCCESSFULLY.val();
            } catch (Exception var16) {
               return "f" + I18N.MSG_ERROR_TRY_AGAIN.val() + "\n" + var16.getMessage();
            }
         }

         @Override
         protected void done() {
            super.done();
            this.showResult();
         }
      });
   }

   public static void save(Window parent, File file, Music music, Theme theme, Dimension size, SaveSlides.EnumType type) {
      type.save(parent, file, SaveSlides.ISave.createFromMusic(music, theme, size), size);
   }

   public static void save(Window parent, File file, List<Verse> slides, Theme theme, Dimension size, SaveSlides.EnumType type) {
      type.save(parent, file, SaveSlides.ISave.createFromVerse(slides, theme, size), size);
   }

   public static enum EnumType {
      IMG(I18N.WORD_IMAGE.val() + " (*.zip)", ".zip", "image"),
      PDF("PDF (*.pdf)", ".pdf", "pdf"),
      PPT("Power Point (*.pptx)", ".pptx", "powerPointBlack");

      public final String name;
      public final String extension;
      public final BufferedImage icon16;
      public final BufferedImage icon24;

      private EnumType(String name, String extension, String iconName) {
         this.name = name;
         this.extension = extension;
         this.icon16 = Images.load(iconName + "16.png");
         this.icon24 = Images.load(iconName + "24.png");
      }

      @Override
      public String toString() {
         return this.name;
      }

      private void save(Window parent, File file, SaveSlides.ISave isave, Dimension size) {
         file = this.renameFile(file, size);
         switch (this) {
            case PDF:
               SaveSlides.savePDF(parent, file, isave, size);
               break;
            case IMG:
               SaveSlides.saveImage(parent, file, isave);
               break;
            case PPT:
               SaveSlides.savePowerPoint(parent, file, isave, size);
         }
      }

      private File renameFile(File f, Dimension d) {
         try {
            Function<String, File> function = s -> new File(f.getParentFile(), f.getName().replace(s, String.format(" %sx%s%s", d.width, d.height, s)));
            switch (this) {
               case PDF:
                  return function.apply(".pdf");
               case IMG:
                  return function.apply(".zip");
               case PPT:
                  return function.apply(".pptx");
            }
         } catch (Exception var4) {
         }

         return f;
      }
   }

   private interface ISave {
      List<Zip.Entry> getImages() throws Exception;

      static SaveSlides.ISave createFromMusic(Music m, Theme theme, Dimension size) {
         return () -> {
            List<Presentation> list = GeneratePresentation.createPresentations(m, theme);
            List<Zip.Entry> _return = new ArrayList<>();
            String format = "%s (%03d).jpg";

            for (int i = 0; i < list.size(); i++) {
               Presentation p = list.get(i);
               BufferedImage img = PresentationSlideUtils.createImage(null, p.text, p.theme, size, true);
               _return.add(new Zip.Entry(String.format(format, I18N.WORD_IMAGE.val(), i + 1), Img.getBytesJPG(img, 0.95F)));
            }

            return _return;
         };
      }

      static SaveSlides.ISave createFromVerse(List<Verse> verses, Theme theme, Dimension size) {
         return () -> {
            List<Zip.Entry> _return = new ArrayList<>();
            String format = "%s (%03d).jpg";
            List<Verse> list = new ArrayList<>(verses);
            int i = 0;

            while (!list.isEmpty()) {
               List<Verse> save = BibleSlideUtils.getVerses(list.get(0), list, Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt());
               BufferedImage img = BibleSlideUtils.createImage(save, theme, size, true, true, Settings.EnumSettings.BIBLE_UPPERCASE.is(), true);
               list.removeAll(save);
               _return.add(new Zip.Entry(String.format(format, I18N.WORD_VERSE.val(), ++i), Img.getBytesJPG(img, 0.95F)));
            }

            return _return;
         };
      }
   }
}
