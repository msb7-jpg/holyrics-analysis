package com.limagiran.holyrics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.IntStream;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
   private final File file;
   private boolean close = true;
   private XSSFWorkbook wb;
   private XSSFSheet s;
   public XSSFFormulaEvaluator evaluator;

   public Excel(File file, String defaultSheet) {
      this.file = file;

      try {
         if (this.file.exists()) {
            this.wb = new XSSFWorkbook(new FileInputStream(file));
            this.s = this.wb.getSheetAt(0);
            this.evaluator = this.wb.getCreationHelper().createFormulaEvaluator();
            this.close = false;
         } else {
            this.wb = new XSSFWorkbook();

            try {
               this.s = this.wb.createSheet(defaultSheet);
            } catch (Exception var4) {
               this.s = this.wb.getSheet(defaultSheet);
            }

            this.evaluator = this.wb.getCreationHelper().createFormulaEvaluator();
            this.close = false;
         }
      } catch (Exception var5) {
         this.close = true;
      }
   }

   public Excel(File file) {
      this(file, "Plan1");
   }

   public XSSFSheet getSheet() {
      return this.s;
   }

   public int getSheetIndex() {
      return this.wb.getSheetIndex(this.s);
   }

   public XSSFWorkbook getWorkbook() {
      return this.wb;
   }

   public void autosize() {
      try {
         int maxCol = this.getMaxColumn();

         for (int col = 0; col <= maxCol; col++) {
            this.s.autoSizeColumn(col);
         }
      } catch (Exception var3) {
      }
   }

   public boolean save() {
      if (!this.close) {
         try (FileOutputStream fos = new FileOutputStream(this.file)) {
            this.wb.write(fos);
            return true;
         } catch (Exception var15) {
         }
      }

      return false;
   }

   public boolean selectSheet(String sheet) {
      if (!this.close) {
         try {
            this.s = this.wb.createSheet(sheet);
         } catch (IllegalArgumentException var3) {
            this.s = this.wb.getSheet(sheet);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean selectSheet(int sheet) {
      if (!this.close) {
         this.s = this.wb.getSheetAt(sheet);
         return true;
      } else {
         return false;
      }
   }

   public XSSFRow getRow(int row) {
      try {
         XSSFRow r = this.s.getRow(row);
         if (r == null) {
            r = this.s.createRow(row);
         }

         return r;
      } catch (Exception var3) {
         return null;
      }
   }

   public XSSFCell getCell(int row, int col) {
      try {
         XSSFCell c = this.getRow(row).getCell(col);
         if (c == null) {
            c = this.getRow(row).createCell(col);
         }

         return c;
      } catch (Exception var4) {
         return null;
      }
   }

   public void setCell(Object value, int row, int col) {
      if (!this.close) {
         try {
            XSSFCell c = this.getCell(row, col);
            if (value instanceof String) {
               c.setCellType(1);
               c.setCellValue((String)value);
            } else if (value instanceof Double) {
               c.setCellType(0);
               c.setCellValue((Double)value);
            } else if (value instanceof Boolean) {
               c.setCellType(4);
               c.setCellValue((Boolean)value);
            } else if (value instanceof Calendar) {
               c.setCellValue((Calendar)value);
            } else if (value instanceof Date) {
               c.setCellValue((Date)value);
            } else if (value instanceof RichTextString) {
               c.setCellValue((RichTextString)value);
            }
         } catch (Exception var5) {
         }
      }
   }

   public String getStringCell(int row, int col) {
      return this.getStringCell(this.getCell(row, col));
   }

   public String getStringCell(XSSFCell c) {
      if (!this.close) {
         try {
            switch (c.getCellType()) {
               case 0:
                  if (HSSFDateUtil.isCellDateFormatted(c)) {
                     return new SimpleDateFormat("dd/MM/yyyy").format(c.getDateCellValue());
                  }

                  NumberFormat format = NumberFormat.getNumberInstance();
                  format.setMaximumFractionDigits(10);
                  format.setGroupingUsed(false);
                  return format.format(c.getNumericCellValue());
               case 1:
                  return String.valueOf(c.getStringCellValue());
               case 2:
                  return this.getFormulaValue(c);
               case 3:
               default:
                  return "";
               case 4:
                  return String.valueOf(c.getBooleanCellValue());
               case 5:
                  return String.valueOf(c.getErrorCellValue());
            }
         } catch (Exception var3) {
         }
      }

      return "";
   }

   public String getFormulaCell(int row, int col) {
      if (!this.close) {
         try {
            XSSFCell c = this.getCell(row, col);
            return c.getCellFormula();
         } catch (Exception var4) {
            return "";
         }
      } else {
         return "";
      }
   }

   private String getFormulaValue(XSSFCell cell) {
      CellValue value = this.evaluator.evaluate(cell);

      try {
         switch (value.getCellType()) {
            case 0:
               NumberFormat format = NumberFormat.getNumberInstance();
               format.setMaximumFractionDigits(10);
               format.setGroupingUsed(false);
               return format.format(value.getNumberValue());
            case 1:
               return value.getStringValue();
            case 2:
            case 3:
            default:
               break;
            case 4:
               return String.valueOf(value.getBooleanValue());
            case 5:
               return String.valueOf(value.getErrorValue());
         }
      } catch (Exception var4) {
      }

      return "";
   }

   public void alignCenter() {
      try {
         XSSFCellStyle cs = this.wb.createCellStyle();
         cs.setAlignment(HorizontalAlignment.CENTER);

         for (int row = 0; row <= this.s.getLastRowNum(); row++) {
            for (int col = 0; col <= this.s.getRow(row).getLastCellNum(); col++) {
               this.getCell(row, col).setCellStyle(cs);
            }
         }
      } catch (Exception var4) {
      }
   }

   public void bold(boolean flag) {
      try {
         for (int row = 0; row <= this.s.getLastRowNum(); row++) {
            for (int col = 0; col <= this.s.getRow(row).getLastCellNum(); col++) {
               this.bold(row, col, flag);
            }
         }
      } catch (Exception var4) {
      }
   }

   public void bold(int row, int col, boolean flag) {
      try {
         XSSFCell c = this.getCell(row, col);
         XSSFCellStyle cs = this.wb.createCellStyle();
         cs.cloneStyleFrom(c.getCellStyle());
         cs.setFont(this.wb.createFont());
         cs.getFont().setBold(flag);
         c.setCellStyle(cs);
      } catch (Exception var6) {
      }
   }

   private int getMaxColumn() {
      try {
         return IntStream.rangeClosed(0, this.s.getLastRowNum()).map(row -> this.getRow(row).getLastCellNum()).max().orElse(-1);
      } catch (Exception var2) {
         return -1;
      }
   }
}
