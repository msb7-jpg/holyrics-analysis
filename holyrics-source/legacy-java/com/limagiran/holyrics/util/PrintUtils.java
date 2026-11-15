package com.limagiran.holyrics.util;

import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.slider.WebSlider;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.table.WebTable;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.limagiran.holyrics.Holyrics;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ChangeEventDelay;
import com.limagiran.holyrics.swing.MarginSetDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

class PrintUtils {
   private static final Border PAGE_PREVIEW_BORDER = new CompoundBorder(
      new LineBorder(new Color(198, 198, 198), 1), new LineBorder(new Color(220, 220, 220), 1)
   );

   public static void openDialog(Supplier<Printable> printable, final Runnable... callback) {
      final JDialog dialog = new JDialog((Window)null, I18N.WORD_PRINTING_NOUN.val(), ModalityType.APPLICATION_MODAL) {
         @Override
         public void dispose() {
            super.dispose();
            Optional.ofNullable(callback).ifPresent(calls -> {
               for (Runnable run : calls) {
                  run.run();
               }
            });
         }
      };
      dialog.setDefaultCloseOperation(0);
      dialog.setLayout(new BorderLayout());
      dialog.setLocationRelativeTo(null);
      WebPanel content = new WebPanel(new BorderLayout());
      dialog.add(content, "Center");
      List<BufferedImage> preview = new ArrayList<>();
      WebPanel panelPreview = new WebPanel(new WrapLayout(1, 32, 32));
      WebScrollPane scrollPreview = new WebScrollPane(panelPreview);
      scrollPreview.getVerticalScrollBar().setUnitIncrement(64);
      WebComboBox cmbbxPageSize = new WebComboBox(PrintUtils.PageSize.values(), PrintUtils.PageSize.A4);
      WebComboBox cmbbxOrientation = new WebComboBox(PrintUtils.Orientation.values(), PrintUtils.Orientation.PORTRAIT);
      SpinnerNumberModel spinnerScaleModel = new SpinnerNumberModel(100, 20, 200, 10);
      WebSpinner spinnerScale = new WebSpinner(spinnerScaleModel);
      WebSlider sliderZoom = new WebSlider(20, 200, 100);
      final int[] margins = new int[]{6, 4, 6, 4};
      Supplier<PrintUtils.MyPageFormat> pageFormat = () -> {
         PrintUtils.MyPageFormat page = new PrintUtils.MyPageFormat(
            (PrintUtils.PageSize)cmbbxPageSize.getSelectedItem(), Math.max((double)spinnerScaleModel.getNumber().intValue() / 100.0, 0.1)
         );
         page.setOrientation(cmbbxOrientation.getSelectedItem() == PrintUtils.Orientation.PORTRAIT ? 1 : 0);
         Dimension dx = page.getSizeScaled();
         Paper paper = new Paper();
         paper.setSize((double)dx.width, (double)dx.height);
         double x = (double)dx.width * ((double)margins[3] / 100.0);
         double y = (double)dx.height * ((double)margins[0] / 100.0);
         paper.setImageableArea(x, y, (double)dx.width * (1.0 - (double)margins[1] / 100.0) - x, (double)dx.height * (1.0 - (double)margins[2] / 100.0) - y);
         page.setPaper(paper);
         return page;
      };
      final Runnable fireUpdate = () -> {
         try {
            panelPreview.removeAll();
            preview.forEach(Image::flush);
            preview.clear();
            preview.addAll(getPreview(printable.get(), pageFormat.get(), 4));
            double zoom = (double)sliderZoom.getValue() / 100.0;

            for (int i = 0; i < preview.size(); i++) {
               panelPreview.add(createPagePreview(Img.resize(preview.get(i), zoom), i));
            }

            panelPreview.revalidate();
            panelPreview.repaint();
         } catch (Exception var9x) {
            Utils.error(dialog, I18N.MSG_ERROR_TRY_AGAIN.val());
         }
      };
      BiFunction<String, ActionListener, WebButton> createDefaultButton = (imageName, evt) -> {
         WebButton _return = new WebButton(new ImageIcon(Images.load(imageName)), evt);
         _return.setFocusable(false);
         _return.setLeftRightSpacing(8);
         _return.setMargin(4);
         return _return;
      };
      cmbbxPageSize.setFocusable(false);
      cmbbxPageSize.addActionListener(evt -> fireUpdate.run());
      cmbbxOrientation.setFocusable(false);
      cmbbxOrientation.addActionListener(evt -> fireUpdate.run());
      final WebButton buttonPrint = createDefaultButton.apply("print24.png", evt -> print(new PrintUtils.ImageListPrintable(preview), pageFormat.get()));
      buttonPrint.setToolTipText(I18N.WORD_PRINT_VERB.val());
      final WebButton buttonSavePDF = createDefaultButton.apply("pdf24.png", evt -> savePDF(preview));
      buttonSavePDF.setToolTipText(I18N.MSG_SAVE_AS___.val() + " (*.pdf)");
      buttonSavePDF.setHorizontalTextPosition(2);
      fireUpdate.run();
      spinnerScale.setFocusable(false);
      spinnerScale.addChangeListener(new ChangeEventDelay(400L, fireUpdate::run));
      WebPanel panelHeader = new WebPanel(new FlowLayout(1, 25, 5));
      panelHeader.add(new Component[]{cmbbxOrientation, cmbbxPageSize, MarginSetDialog.createContent(new MarginSetDialog.MarginSetListener() {
         private final int[] minMaxStep = new int[]{1, 30, 1};

         @Override
         public void changed() {
            SwingUtilities.invokeLater(fireUpdate::run);
         }

         @Override
         public int[] getMargins() {
            return margins;
         }

         @Override
         public int[] getMinMaxAndStepValue() {
            return this.minMaxStep;
         }
      }, true), spinnerScale, Box.createHorizontalStrut(50), buttonPrint, buttonSavePDF});
      WebPanel scrollHeader = new WebPanel(new FlowLayout(1, 15, 4));
      sliderZoom.setMajorTickSpacing(40);
      sliderZoom.setMinorTickSpacing(10);
      sliderZoom.setPaintTicks(true);
      sliderZoom.setPaintLabels(true);
      sliderZoom.setSnapToTicks(true);
      sliderZoom.addChangeListener(evt -> {
         if (!sliderZoom.getValueIsAdjusting()) {
            fireUpdate.run();
         }
      });
      sliderZoom.setFocusable(false);
      scrollHeader.add(new WebLabel("Zoom"));
      scrollHeader.add(sliderZoom);
      scrollPreview.setColumnHeaderView(scrollHeader);
      content.add(panelHeader, "First");
      content.add(scrollPreview, "Center");
      Dimension d = VideoUtils.getSizeDisplay();
      d.width -= 70;
      d.height -= 100;
      dialog.setSize(d);
      dialog.setPreferredSize(d);
      dialog.setLocationRelativeTo(null);
      InsertShortcut.addEscToDispose(dialog);
      WebLafUtils.addToolTipButtonDown(dialog, buttonPrint, Hotkey.CTRL_P);
      WebLafUtils.addToolTipButtonDown(dialog, buttonSavePDF, Hotkey.CTRL_S);
      dialog.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentHidden(ComponentEvent e) {
            HotkeyManager.unregisterHotkeys(buttonPrint);
            HotkeyManager.unregisterHotkeys(buttonSavePDF);
         }
      });
      dialog.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            dialog.dispose();
         }
      });
      dialog.setVisible(true);
   }

   private static List<BufferedImage> getPreview(Printable print, PrintUtils.MyPageFormat page, int amountPages) throws Exception {
      int i = 0;
      List<BufferedImage> _return = new ArrayList<>();

      while (i < amountPages) {
         BufferedImage bi = new BufferedImage((int)page.getWidth(), (int)page.getHeight(), 2);
         Graphics2D g = bi.createGraphics();
         G2Utils.setupGraphics(g);
         int status = print.print(g, page, i++);
         g.dispose();
         if (status == 1) {
            break;
         }

         _return.add(Img.resize(bi, page.scale));
      }

      return _return;
   }

   private static WebPanel createPagePreview(BufferedImage bi, int index) {
      WebLabel title = new WebLabel(I18N.WORD_PAGE.val() + ": " + (index + 1), 0, new Object[0]);
      title.setFontSize(16);
      WebLabel image = new WebLabel(new ImageIcon(bi));
      image.setBackground(Color.WHITE);
      image.setOpaque(true);
      image.setBorder(PAGE_PREVIEW_BORDER);
      WebPanel panel = new WebPanel(new BorderLayout(0, 8));
      panel.add(title, "First");
      panel.add(image, "Center");
      return panel;
   }

   private static void print(Printable printable, PageFormat pageFormat) {
      PrinterJob printerJob = PrinterJob.getPrinterJob();
      printerJob.setPrintable(printable, pageFormat);
      if (printerJob.printDialog()) {
         try {
            printerJob.print();
         } catch (Exception var4) {
            Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
         }
      }
   }

   private static void savePDF(List<BufferedImage> list) {
      if (!list.isEmpty()) {
         final List<BufferedImage> _list = new ArrayList<>(list);
         File init = null;
         if (!Settings.EnumSettings2.PRINT_LAST_DIR.getString().isEmpty()) {
            init = new File(Settings.EnumSettings2.PRINT_LAST_DIR.getString());
         }

         final File file = Dir.windowsSave(I18N.MSG_SAVE_AS___.val(), "", init, "PDF File", ".pdf");
         if (file != null) {
            PopUpWorker.exe(new MySwingWorker<String, Void>(null) {
               protected String doInBackground() throws Exception {
                  try {
                     Document document = new Document();
                     document.setMargins(0.0F, 0.0F, 0.0F, 0.0F);
                     document.setPageSize(new Rectangle((float)_list.get(0).getWidth(), (float)_list.get(0).getHeight()));

                     try (FileOutputStream fos = new FileOutputStream(file)) {
                        PdfWriter.getInstance(document, fos);
                        document.open();

                        for (BufferedImage img : _list) {
                           document.add(com.itextpdf.text.Image.getInstance(img, Color.WHITE));
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
                  if (this.isOk()) {
                     try {
                        Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
                     } catch (Exception var2) {
                     }
                  }
               }
            });
         }
      }
   }

   public static WebTable createTableTest() {
      int colsSize = 8;
      final String[] names = IntStream.range(0, colsSize).mapToObj(i -> "Coluna " + (i + 1)).toArray(String[]::new);
      final List<List<String>> rows = new ArrayList<>();
      IntStream.range(0, 50).forEach(i -> {
         List<String> cols = new ArrayList<>();
         IntStream.range(0, colsSize).forEach(ii -> cols.add(Utils.random(99999) + String.valueOf(Utils.random(99999)) + Utils.random(99999)));
         rows.add(cols);
      });
      TableModel model = new AbstractTableModel() {
         @Override
         public int getRowCount() {
            return rows.size();
         }

         @Override
         public int getColumnCount() {
            return names.length;
         }

         @Override
         public String getColumnName(int column) {
            return names[column];
         }

         @Override
         public Object getValueAt(int rowIndex, int columnIndex) {
            return rows.get(rowIndex).get(columnIndex);
         }
      };
      return new WebTable(model).setFontName("Lucida Console").setFontName("Monospaced").setFontSize(16);
   }

   public static WebButton createDefaultButton() {
      WebButton wb = new WebButton();
      wb.setFocusable(false);
      wb.setMargin(2, 4, 2, 4);
      wb.setShadeWidth(0);
      wb.setToolTipText(I18N.WORD_PRINT_VERB.val());
      return wb;
   }

   static {
      Holyrics.applyLAF();
   }

   public static class ImageListPrintable implements Printable {
      private final List<BufferedImage> list = new ArrayList<>();

      public ImageListPrintable(List<BufferedImage> list) {
         this.list.addAll(list);
      }

      @Override
      public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
         if (page >= this.list.size()) {
            return 1;
         } else {
            Graphics2D g2 = (Graphics2D)g;
            G2Utils.setupGraphics(g2);
            g2.drawImage(this.list.get(page), 0, 0, null);
            return 0;
         }
      }
   }

   public static class MyPageFormat extends PageFormat {
      public final PrintUtils.PageSize size;
      public final double scale;

      public MyPageFormat(PrintUtils.PageSize size, double scale) {
         this.size = size;
         this.scale = scale;
      }

      public Dimension getSizeScaled() {
         Dimension d = this.size.getPortrait();
         d.width = (int)((double)d.width / this.scale);
         d.height = (int)((double)d.height / this.scale);
         return d;
      }
   }

   public static enum Orientation {
      PORTRAIT(I18N.WORD_PORTRAIT.val()),
      LANDSCAPE(I18N.WORD_LANDSCAPE.val());

      public final String name;

      private Orientation(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }

   public static enum PageSize {
      A3(842, 1191),
      A4(595, 842),
      A5(420, 595);

      public final int width;
      public final int height;

      private PageSize(int width, int height) {
         this.width = width;
         this.height = height;
      }

      public Dimension getPortrait() {
         return new Dimension(this.width, this.height);
      }

      public Dimension getLandscape() {
         return new Dimension(this.height, this.width);
      }

      @Override
      public String toString() {
         return " " + super.toString() + "    ";
      }
   }
}
