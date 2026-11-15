package com.limagiran.holyrics.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.swing.JTable;
import javax.swing.JTable.PrintMode;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class TablePrintableCustom implements Printable {
   public final JTable table;
   protected JTableHeader header;
   protected TableColumnModel colModel;
   protected int totalColWidth;
   public final PrintMode printMode = PrintMode.NORMAL;
   protected int last = -1;
   protected int row = 0;
   protected int col = 0;
   protected final Rectangle clip = new Rectangle(0, 0, 0, 0);
   protected final Rectangle hclip = new Rectangle(0, 0, 0, 0);
   protected final Rectangle tempRect = new Rectangle(0, 0, 0, 0);

   public TablePrintableCustom(JTable table) {
      this.table = table;
      this.header = table.getTableHeader();
      this.colModel = table.getColumnModel();
      this.totalColWidth = this.colModel.getTotalColumnWidth();
      if (this.header != null) {
         this.hclip.height = this.header.getHeight();
      }
   }

   @Override
   public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
      int imgWidth = (int)pageFormat.getImageableWidth();
      int imgHeight = (int)pageFormat.getImageableHeight();
      if (imgWidth <= 0) {
         throw new PrinterException("Width of printable area is too small.");
      } else {
         int availableSpace = imgHeight;
         if (imgHeight <= 0) {
            throw new PrinterException("Height of printable area is too small.");
         } else {
            double sf = 1.0;
            if (this.printMode == PrintMode.FIT_WIDTH && this.totalColWidth > imgWidth) {
               assert imgWidth > 0;

               assert this.totalColWidth > 1;

               sf = (double)imgWidth / (double)this.totalColWidth;
            }

            assert sf > 0.0;

            while (this.last < pageIndex) {
               if (this.row >= this.table.getRowCount() && this.col == 0) {
                  return 1;
               }

               int scaledWidth = (int)((double)imgWidth / sf);
               int scaledHeight = (int)((double)(availableSpace - this.hclip.height) / sf);
               this.findNextClip(scaledWidth, scaledHeight);
               this.last++;
            }

            Graphics2D g2d = (Graphics2D)graphics.create();
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            this.tempRect.x = 0;
            this.tempRect.y = 0;
            this.tempRect.width = imgWidth;
            this.tempRect.height = availableSpace;
            g2d.clip(this.tempRect);
            if (sf != 1.0) {
               g2d.scale(sf, sf);
            } else {
               int diff = (imgWidth - this.clip.width) / 2;
               g2d.translate(diff, 0);
            }

            AffineTransform oldTrans = g2d.getTransform();
            Shape oldClip = g2d.getClip();
            if (this.header != null) {
               this.hclip.x = this.clip.x;
               this.hclip.width = this.clip.width;
               g2d.translate(-this.hclip.x, 0);
               g2d.clip(this.hclip);
               this.header.print(g2d);
               g2d.setTransform(oldTrans);
               g2d.setClip(oldClip);
               g2d.translate(0, this.hclip.height);
            }

            g2d.translate(-this.clip.x, -this.clip.y);
            g2d.clip(this.clip);
            this.table.print(g2d);
            g2d.setTransform(oldTrans);
            g2d.setClip(oldClip);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, this.clip.width, this.hclip.height + this.clip.height);
            g2d.dispose();
            return 0;
         }
      }
   }

   private void findNextClip(int pw, int ph) {
      boolean ltr = this.table.getComponentOrientation().isLeftToRight();
      if (this.col == 0) {
         if (ltr) {
            this.clip.x = 0;
         } else {
            this.clip.x = this.totalColWidth;
         }

         this.clip.y = this.clip.y + this.clip.height;
         this.clip.width = 0;
         this.clip.height = 0;
         int rowCount = this.table.getRowCount();
         int rowHeight = this.table.getRowHeight(this.row);

         do {
            this.clip.height += rowHeight;
            if (++this.row >= rowCount) {
               break;
            }

            rowHeight = this.table.getRowHeight(this.row);
         } while (this.clip.height + rowHeight <= ph);
      }

      if (this.printMode == PrintMode.FIT_WIDTH) {
         this.clip.x = 0;
         this.clip.width = this.totalColWidth;
      } else {
         if (ltr) {
            this.clip.x = this.clip.x + this.clip.width;
         }

         this.clip.width = 0;
         int colCount = this.table.getColumnCount();
         int colWidth = this.colModel.getColumn(this.col).getWidth();

         do {
            this.clip.width += colWidth;
            if (!ltr) {
               this.clip.x -= colWidth;
            }

            if (++this.col >= colCount) {
               this.col = 0;
               break;
            }

            colWidth = this.colModel.getColumn(this.col).getWidth();
         } while (this.clip.width + colWidth <= pw);
      }
   }
}
