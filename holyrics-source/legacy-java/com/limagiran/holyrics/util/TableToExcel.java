package com.limagiran.holyrics.util;

import com.alee.laf.button.WebButton;
import com.limagiran.holyrics.i18n.I18N;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.Arrays;
import java.util.function.BiFunction;
import javax.swing.ImageIcon;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;

public class TableToExcel {
   private static final XSSFColor COLOR_BORDER = new XSSFColor(new Color(32, 32, 32));
   private static final Color COLOR_ALTERNATE_ROW = new Color(230, 230, 230);
   private static final Color COLOR_BG_TITLES = new Color(225, 225, 225);

   private static XSSFCellStyle createStyleDefault(XSSFWorkbook wb, TableToExcel.TableToExcelModel model) {
      XSSFCellStyle cs = wb.createCellStyle();
      cs.setAlignment(model != null && !model.isAlignHCenter() ? HorizontalAlignment.LEFT : HorizontalAlignment.CENTER);
      cs.setVerticalAlignment(model != null && !model.isAlignHCenter() ? VerticalAlignment.TOP : VerticalAlignment.CENTER);
      cs.setWrapText(true);
      Arrays.stream(BorderSide.values()).forEach(en -> cs.setBorderColor(en, COLOR_BORDER));
      cs.setBorderRight((short)1);
      cs.setBorderLeft((short)1);
      return cs;
   }

   private static XSSFCellStyle createStyleRow1(XSSFWorkbook wb, TableToExcel.TableToExcelModel model) {
      XSSFCellStyle cs = createStyleDefault(wb, model);
      cs.setFillPattern((short)1);
      cs.setFillForegroundColor(new XSSFColor(new Color(252, 252, 252)));
      return cs;
   }

   private static XSSFCellStyle createStyleRow2(XSSFWorkbook wb, TableToExcel.TableToExcelModel model) {
      XSSFCellStyle cs = createStyleDefault(wb, model);
      cs.setFillPattern((short)1);
      cs.setFillForegroundColor(new XSSFColor(COLOR_ALTERNATE_ROW));
      return cs;
   }

   private static XSSFCellStyle createStyleRow1Bold(XSSFWorkbook wb, TableToExcel.TableToExcelModel model) {
      XSSFCellStyle cs = createStyleRow1(wb, model);
      XSSFFont font = wb.createFont();
      font.setBold(true);
      cs.setFont(font);
      return cs;
   }

   private static XSSFCellStyle createStyleRow2Bold(XSSFWorkbook wb, TableToExcel.TableToExcelModel model) {
      XSSFCellStyle cs = createStyleRow2(wb, model);
      XSSFFont font = wb.createFont();
      font.setBold(true);
      cs.setFont(font);
      return cs;
   }

   private static XSSFCellStyle createStyleTitle(XSSFWorkbook wb) {
      XSSFCellStyle cs = createStyleDefault(wb, null);
      XSSFFont csFont = wb.createFont();
      csFont.setBold(true);
      csFont.setFontHeight(13.0);
      cs.setFont(csFont);
      cs.setFillPattern((short)1);
      cs.setFillForegroundColor(new XSSFColor(COLOR_BG_TITLES));
      Arrays.stream(BorderSide.values()).forEach(en -> cs.setBorderColor(en, COLOR_BORDER));
      cs.setBorderTop((short)1);
      cs.setBorderRight((short)1);
      cs.setBorderBottom((short)1);
      cs.setBorderLeft((short)1);
      return cs;
   }

   private static XSSFCellStyle createStyleHeader(XSSFWorkbook wb) {
      XSSFCellStyle cs = createStyleDefault(wb, null);
      XSSFFont csFont = wb.createFont();
      csFont.setBold(true);
      csFont.setFontHeight(13.0);
      cs.setFont(csFont);
      cs.setFillPattern((short)1);
      cs.setFillForegroundColor(new XSSFColor(COLOR_BG_TITLES));
      cs.setBorderTop((short)0);
      cs.setBorderRight((short)0);
      cs.setBorderBottom((short)0);
      cs.setBorderLeft((short)0);
      return cs;
   }

   private static XSSFCellStyle createStyleWhite(XSSFWorkbook wb) {
      XSSFCellStyle cs = wb.createCellStyle();
      cs.setFillPattern((short)1);
      cs.setFillForegroundColor(new XSSFColor(Color.WHITE));
      return cs;
   }

   public static void create(final TableToExcel.TableToExcelModel... models) {
      try {
         if (models.length <= 0) {
            return;
         }

         final File file = Dir.windowsSave(I18N.WORD_SAVE.val(), "Excel File (*.xlsx)", ".xlsx");
         if (file == null) {
            return;
         }

         file.delete();
         final Excel excel = new Excel(file, models[0].getSheetName());
         PopUpWorker.exe(
            new MySwingWorker<Void, Void>(null) {
               protected Void doInBackground() throws Exception {
                  try {
                     for (TableToExcel.TableToExcelModel model : models) {
                        excel.selectSheet(model.getSheetName());
                        XSSFWorkbook wb = excel.getWorkbook();
                        XSSFCellStyle csTitle = TableToExcel.createStyleTitle(wb);
                        XSSFCellStyle csRow1 = TableToExcel.createStyleRow1(wb, model);
                        XSSFCellStyle csRow2 = TableToExcel.createStyleRow2(wb, model);
                        XSSFCellStyle csRow1B = TableToExcel.createStyleRow1Bold(wb, model);
                        XSSFCellStyle csRow2B = TableToExcel.createStyleRow2Bold(wb, model);
                        BiFunction<Integer, Object, XSSFCellStyle> getCellStyle = (index, valuex) -> valuex.toString().equals("✓")
                              ? (index % 2 == 0 ? csRow1B : csRow2B)
                              : (index % 2 == 0 ? csRow1 : csRow2);
                        int totalCell = model.getColumnCount() * model.getRowCount();

                        for (int col = 0; col < model.getColumnCount(); col++) {
                           int cCol = col + 1;
                           excel.setCell(model.getColumnName(col), 2, cCol);
                           excel.getCell(2, cCol).setCellStyle(csTitle);

                           for (int row = 0; row < model.getRowCount(); row++) {
                              int cRow = row + 3;
                              Object value = model.getValueFormated(row, col);
                              excel.setCell(value, cRow, cCol);
                              excel.getCell(cRow, cCol).setCellStyle((CellStyle)getCellStyle.apply(row, value));
                              if (row + 1 == model.getRowCount()) {
                                 XSSFCell c = excel.getCell(cRow, cCol);
                                 XSSFCellStyle csLastRow = wb.createCellStyle();
                                 csLastRow.cloneStyleFrom(c.getCellStyle());
                                 c.setCellStyle(csLastRow);
                                 csLastRow.setBorderBottom((short)1);
                              }

                              int progress = col * row;
                              if (progress % 10 == 0) {
                                 this.setProgress(Utils.getPercentageInt((double)progress, (double)totalCell));
                              }
                           }
                        }

                        excel.setCell(model.getHeader(), 1, 1);
                        CellRangeAddress range = new CellRangeAddress(1, 1, 1, model.getColumnCount());
                        XSSFSheet sheet = excel.getSheet();
                        sheet.addMergedRegion(range);
                        excel.getCell(1, 1).setCellStyle(TableToExcel.createStyleHeader(wb));
                        XSSFRow r = excel.getRow(1);
                        r.setHeight((short)((int)((double)r.getHeight() * 1.5)));
                        sheet.setColumnWidth(0, 1024);
                        sheet.setColumnWidth(model.getColumnCount() + 1, 1024);
                        sheet.setFitToPage(true);
                        excel.autosize();
                        int sheetIndex = excel.getSheetIndex();
                        if (sheetIndex >= 0) {
                           wb.setPrintArea(sheetIndex, 0, model.getColumnCount() + 1, 0, model.getRowCount() + 3);
                        }
                     }
                  } catch (Exception var20) {
                  }

                  return null;
               }

               @Override
               protected void done() {
                  super.done();
                  if (!this.isCancelled()) {
                     if (!excel.save()) {
                        Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
                        return;
                     }

                     Utils.msg(I18N.MSG_SAVED_SUCCESSFULLY.val());
                     Utils.openLocation(file);
                  }
               }
            },
            true
         );
      } catch (Exception var3) {
         Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
      }
   }

   public static WebButton createDefaultButton(boolean small) {
      WebButton button = new WebButton();
      button.setFont(new Font("Arial", 0, 12));
      button.setIcon(new ImageIcon(TableToExcel.class.getResource("/com/limagiran/holyrics/img/excel" + (small ? 16 : 24) + ".png")));
      button.setText("<html><div style='text-align:center;padding:4px 6px'>" + I18N.MSG_SAVE_AS___.val() + " (*.xlsx)");
      button.setFocusable(false);
      button.setHorizontalTextPosition(2);
      button.setIconTextGap(8);
      button.setMargin(0, 4, 0, 4);
      return button;
   }

   public interface TableToExcelModel {
      int getRowCount();

      int getColumnCount();

      String getColumnName(int var1);

      Object getValue(int var1, int var2);

      default Object getValueFormated(int row, int col) {
         Object obj = this.getValue(row, col);
         if (obj == null) {
            return "";
         } else if (obj instanceof Boolean) {
            return (Boolean)obj ? "✓" : " ";
         } else {
            return obj instanceof Number ? ((Number)obj).doubleValue() : obj.toString();
         }
      }

      String getHeader();

      String getSheetName();

      default boolean isAlignHCenter() {
         return true;
      }

      default boolean isAlignVCenter() {
         return true;
      }
   }
}
