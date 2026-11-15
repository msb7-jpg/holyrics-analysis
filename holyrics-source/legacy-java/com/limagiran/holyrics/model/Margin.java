package com.limagiran.holyrics.model;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.Serializable;

public class Margin implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final int TOP = 0;
   public static final int RIGHT = 1;
   public static final int BOTTOM = 2;
   public static final int LEFT = 3;
   public final int[] margins = new int[]{0, 0, 0, 0};

   public Margin(int top, int right, int bottom, int left) {
      this.margins[0] = top;
      this.margins[1] = right;
      this.margins[2] = bottom;
      this.margins[3] = left;
   }

   public Margin() {
      this(0);
   }

   public Margin(int all) {
      this(all, all, all, all);
   }

   public int getTop() {
      return this.margins[0];
   }

   public Margin setTop(int top) {
      this.margins[0] = top;
      return this;
   }

   public int getRight() {
      return Math.min(99 - this.getLeft(), this.margins[1]);
   }

   public Margin setRight(int right) {
      this.margins[1] = right;
      return this;
   }

   public int getBottom() {
      return Math.min(99 - this.getTop(), this.margins[2]);
   }

   public Margin setBottom(int bottom) {
      this.margins[2] = bottom;
      return this;
   }

   public int getLeft() {
      return this.margins[3];
   }

   public Margin setLeft(int left) {
      this.margins[3] = left;
      return this;
   }

   public int getTopTo(int h) {
      return (int)((double)h * ((double)this.getTop() / 100.0));
   }

   public int getMarginRightTo(int w) {
      return (int)((double)w * ((double)this.getRight() / 100.0));
   }

   public int getMarginBottomTo(int h) {
      return (int)((double)h * ((double)this.getBottom() / 100.0));
   }

   public int getLeftTo(int w) {
      return (int)((double)w * ((double)this.getLeft() / 100.0));
   }

   public int getHorizontalTo(int w) {
      return (int)((double)w * ((double)(this.getRight() + this.getLeft()) / 100.0));
   }

   public int getVerticalTo(int h) {
      return (int)((double)h * ((double)(this.getTop() + this.getBottom()) / 100.0));
   }

   public Rectangle getRectWithMargin(Dimension d) {
      return new Rectangle(this.getLeftTo(d.width), this.getTopTo(d.height), d.width - this.getHorizontalTo(d.width), d.height - this.getVerticalTo(d.height));
   }

   @Override
   public String toString() {
      return "[" + "," + this.margins[0] + "," + this.margins[1] + "," + this.margins[2] + "," + this.margins[3] + "]";
   }
}
