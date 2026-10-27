package com.limagiran.holyrics.control;

import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.PresentationSlideView;
import java.awt.Font;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public interface MusicController {
   Music getInstance();

   default MusicController.Paragraph getTitleAndArtistParagraph() {
      Music m = this.getInstance();
      return MusicController.Paragraph.create(m, String.format("%s%n%s", m.getTitle(), m.getArtist()), "")
         .setSlideType(PresentationSlideView.EnumSlideType.TITLE)
         .setPageCount(Integer.MAX_VALUE, Integer.MAX_VALUE)
         .setTitleAndArtist(" ");
   }

   default List<MusicController.Paragraph> getParagraphsToPresentation() {
      List<MusicController.Paragraph> _return = new ArrayList<>();
      Music music = this.getInstance();
      String titleAndArtist = this.getTitleAndArtistToShow();
      List<String> list = music.getParagraphs();
      List<Integer> listOrder = music.getOrder();
      if (!listOrder.isEmpty()) {
         Map<Integer, MusicController.Paragraph> temp = new HashMap<>();

         for (int i = 0; i < listOrder.size(); i++) {
            int order = listOrder.get(i);
            if (order >= 0 && order < list.size()) {
               MusicController.Paragraph p = temp.get(order);
               if (p == null) {
                  p = MusicController.Paragraph.create(music, list.get(order), music.getFormat(order))
                     .setPageCount(i + 1, listOrder.size())
                     .setTitleAndArtist(titleAndArtist);
                  temp.put(order, p);
               }

               _return.add(p);
            }
         }

         return _return;
      } else {
         for (int ix = 0; ix < list.size(); ix++) {
            _return.add(
               MusicController.Paragraph.create(music, list.get(ix), music.getFormat(ix)).setPageCount(ix + 1, list.size()).setTitleAndArtist(titleAndArtist)
            );
         }

         return _return;
      }
   }

   default List<String> getParagraphsInOrder() {
      List<String> _return = new ArrayList<>();
      Music music = this.getInstance();
      List<String> list = music.getParagraphs();
      if (music.getOrder().isEmpty()) {
         return list;
      } else {
         List<Integer> orderList = music.getOrder();

         for (int i = 0; i < orderList.size(); i++) {
            int order = orderList.get(i);
            if (order >= 0 && order < list.size()) {
               _return.add(list.get(order));
            }
         }

         return _return;
      }
   }

   default String getTitleAndArtistToShow() {
      Music m = this.getInstance();
      return m.getTitle() + "\n" + m.getArtist();
   }

   default void setOrder(List<String> order) {
      Music music = this.getInstance();
      music.getOrder().clear();
      List<String> paragraphs = music.getParagraphs();
      order.stream().filter(paragraphs::contains).map(paragraphs::indexOf).forEach(music.getOrder()::add);
   }

   default boolean save() {
      return MusicDao.set(this.getInstance());
   }

   default String getLyricsWithFormatting() {
      Music m = this.getInstance();

      try {
         List<String> paragraphs = m.getParagraphs();

         for (int i = 0; i < paragraphs.size(); i++) {
            String formatting = m.getFormat(i);
            if (!formatting.isEmpty()) {
               paragraphs.set(i, formatting + "\n" + paragraphs.get(i));
            }
         }

         return String.join("\n\n", paragraphs);
      } catch (Exception var5) {
         return m.getLyrics();
      }
   }

   default void setTextAndApplyFormatting(JTextPane tp) {
      Music m = this.getInstance();

      try {
         tp.setText("");
         StyledDocument doc = tp.getStyledDocument();
         doc.remove(0, doc.getLength());
         Font font = tp.getFont();
         List<String> ps = m.getParagraphs();

         for (int index = 0; index < ps.size(); index++) {
            MusicController.Paragraph p = MusicController.Paragraph.create().setText(ps.get(index)).setFormatting(m.getFormat(index)).setForceFormatting(true);
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attr, font.getFamily());
            StyleConstants.setFontSize(attr, p.getScale(font));
            StyleConstants.setItalic(attr, p.isItalic());
            StyleConstants.setBold(attr, p.isBold());
            doc.insertString(doc.getLength(), (p.isUppercase() ? p.getText().toUpperCase() : p.getText()) + (index + 1 < ps.size() ? "" : ""), attr);
            StyleConstants.setFontSize(attr, font.getSize());
            doc.insertString(doc.getLength(), index + 1 < ps.size() ? "\n\n" : "", attr);
         }
      } catch (Exception var9) {
         tp.setText(m.getLyrics());
      }
   }

   default void setDefaultTheme(long id) {
      MusicFavoriteSetController.setDefaultTheme(this.getInstance().getId(), id);
   }

   default Theme getDefaultTheme() {
      return MusicFavoriteSetController.getDefaultTheme(this.getInstance().getId());
   }

   default Theme getDefaultThemeOr(Theme theme) {
      Theme _return = MusicFavoriteSetController.getDefaultTheme(this.getInstance().getId());
      return _return != null ? _return : theme;
   }

   default void removeDefaultTheme() {
      MusicFavoriteSetController.removeDefaultTheme(this.getInstance().getId());
   }

   public static class Paragraph implements Serializable {
      private static final long serialVersionUID = 1L;
      public static final String TOKEN = "##";
      private Object source;
      private String text;
      private String formatting;
      private boolean forceFormatting;
      private boolean hackBreakLine;
      private Point pageCount;
      private String titleAndArtist;
      private PresentationSlideView.EnumSlideType slideType;
      private transient boolean skipPaintAddInfo = false;
      private int[] intArray;
      private boolean paintMargin;

      public Paragraph(Object source, String text, String formatting) {
         this.source = source;
         this.text = text;
         this.formatting = formatting;
         this.forceFormatting = false;
         this.hackBreakLine = false;
         this.pageCount = new Point();
         this.titleAndArtist = "";
         this.slideType = PresentationSlideView.EnumSlideType.LYRICS;
         this.intArray = new int[MusicController.Paragraph.EnumParagraphIntValues.values().length];
         this.paintMargin = false;
      }

      public Paragraph(String text, String formatting) {
         this(null, text, formatting);
      }

      public Paragraph(String text) {
         this(null, text, "");
      }

      public Paragraph() {
         this(null, "", "");
      }

      public static MusicController.Paragraph create(Object source, String text, String formatting) {
         return new MusicController.Paragraph(source, text, formatting);
      }

      public static MusicController.Paragraph create(String text, String formatting) {
         return new MusicController.Paragraph(text, formatting);
      }

      public static MusicController.Paragraph create(String text) {
         return new MusicController.Paragraph(text);
      }

      public static MusicController.Paragraph create() {
         return new MusicController.Paragraph();
      }

      public MusicController.Paragraph getClone() {
         MusicController.Paragraph p = create();
         p.source = this.source;
         p.text = this.text;
         p.formatting = this.formatting;
         p.forceFormatting = this.forceFormatting;
         p.hackBreakLine = this.hackBreakLine;
         p.pageCount.setLocation(this.pageCount);
         p.titleAndArtist = this.titleAndArtist;
         p.slideType = this.slideType;
         p.skipPaintAddInfo = this.skipPaintAddInfo;
         p.intArray = Arrays.copyOf(this.intArray, this.intArray.length);
         p.paintMargin = this.paintMargin;
         return p;
      }

      public String[] getLines() {
         String[] array = this.getText().split("\n");
         return array.length > 0 ? array : new String[]{""};
      }

      public String getText() {
         return this.text != null ? this.text : "";
      }

      public MusicController.Paragraph setText(String text) {
         this.text = text;
         return this;
      }

      public Object getSource() {
         return this.source;
      }

      public MusicController.Paragraph setSource(Object source) {
         this.source = source;
         return this;
      }

      public boolean isForceFormatting() {
         return this.forceFormatting;
      }

      public MusicController.Paragraph setForceFormatting(boolean forceFormatting) {
         this.forceFormatting = forceFormatting;
         return this;
      }

      public String getFormatting() {
         return this.formatting != null ? this.formatting : "";
      }

      public String getFormattingToPresentation() {
         if (!Settings.EnumSettings2.TEXT_FORMATTING_ENABLED.is() && !this.forceFormatting) {
            return "";
         } else {
            String format = this.getFormatting();
            String label = this.getFormattingLabel();
            if (!format.replace(label, "").toLowerCase().contains("p")) {
               return format;
            } else {
               format = Settings.EnumSettings2.TEXT_FORMATTING_DEFAULT_FORMAT.getString();
               return label.isEmpty() ? format : format + "(" + label + ")";
            }
         }
      }

      public String getFormattingToPresentationWithoutLabel() {
         return this.getFormattingToPresentation().replace("(" + this.getFormattingLabel() + ")", "");
      }

      public MusicController.Paragraph setFormatting(String formatting) {
         this.formatting = formatting;
         return this;
      }

      public boolean isHackBreakLine() {
         return this.hackBreakLine;
      }

      public MusicController.Paragraph setHackBreakLine(boolean hackBreakLine) {
         this.hackBreakLine = hackBreakLine;
         return this;
      }

      public Point getPageCount() {
         return this.pageCount != null ? this.pageCount : (this.pageCount = new Point());
      }

      public MusicController.Paragraph setPageCount(int indexOf, int total) {
         this.getPageCount().setLocation(indexOf, total);
         return this;
      }

      public MusicController.Paragraph setPageCount(Point pageCount) {
         this.getPageCount().setLocation(pageCount.x, pageCount.y);
         return this;
      }

      public boolean isPageCount() {
         Point p = this.getPageCount();
         return p.x > 0 && p.y > 0 && p.x <= p.y || p.x == Integer.MAX_VALUE && p.y == Integer.MAX_VALUE;
      }

      public String getPageCountFormated() {
         Point p = this.getPageCount();
         return p.x == Integer.MAX_VALUE && p.y == Integer.MAX_VALUE ? " " : p.x + "/" + p.y;
      }

      public String getTitleAndArtist() {
         return this.titleAndArtist != null ? this.titleAndArtist : "";
      }

      public MusicController.Paragraph setTitleAndArtist(String titleAndArtist) {
         this.titleAndArtist = titleAndArtist;
         return this;
      }

      public PresentationSlideView.EnumSlideType getSlideType() {
         return this.slideType;
      }

      public MusicController.Paragraph setSlideType(PresentationSlideView.EnumSlideType slideType) {
         this.slideType = slideType;
         return this;
      }

      public int getInt(MusicController.Paragraph.EnumParagraphIntValues key) {
         return key.getInt(this);
      }

      public MusicController.Paragraph setInt(MusicController.Paragraph.EnumParagraphIntValues key, int value) {
         return key.setInt(this, value);
      }

      public boolean getBoolean(MusicController.Paragraph.EnumParagraphIntValues key) {
         return key.getBoolean(this);
      }

      public MusicController.Paragraph setBoolean(MusicController.Paragraph.EnumParagraphIntValues key, boolean value) {
         return key.setBoolean(this, value);
      }

      public synchronized boolean isSkipPaintAddInfoAndToFalse() {
         boolean _return = this.skipPaintAddInfo;
         this.skipPaintAddInfo = false;
         return _return;
      }

      public synchronized boolean isSkipPaintAddInfo() {
         return this.skipPaintAddInfo;
      }

      public MusicController.Paragraph setSkipPaintAddInfo(boolean skipPaintAddInfo) {
         this.skipPaintAddInfo = skipPaintAddInfo;
         return this;
      }

      public boolean isPaintMargin() {
         return this.paintMargin;
      }

      public MusicController.Paragraph setPaintMargin(boolean paintMargin) {
         this.paintMargin = paintMargin;
         return this;
      }

      @Override
      public String toString() {
         return this.getText();
      }

      public String hash() {
         return this.getText()
            + ":"
            + this.getSource()
            + ":"
            + this.getFormattingToPresentation()
            + ":"
            + this.getFormatting()
            + ":"
            + this.isForceFormatting()
            + ":"
            + this.isHackBreakLine()
            + ":"
            + this.getPageCount()
            + ":"
            + this.getTitleAndArtist()
            + ":"
            + this.getSlideType()
            + ":"
            + Arrays.toString(this.intArray)
            + ":"
            + this.isPaintMargin();
      }

      public boolean isFormat() {
         return this.formatting != null && this.formatting.startsWith("##");
      }

      public boolean isUppercase() {
         return this.getFormattingToPresentationWithoutLabel().toLowerCase().contains("m");
      }

      public boolean isBold() {
         return this.getFormattingToPresentationWithoutLabel().toLowerCase().contains("n");
      }

      public boolean isItalic() {
         return this.getFormattingToPresentationWithoutLabel().toLowerCase().contains("i");
      }

      private String getFormattingLabel(String f) {
         int idxStart = f.indexOf("(");
         if (idxStart < 0) {
            return "";
         } else {
            int idxEnd = f.lastIndexOf(")");
            return idxStart >= 0 && idxEnd >= idxStart ? f.substring(idxStart + 1, idxEnd) : "";
         }
      }

      public String getFormattingLabel() {
         return this.getFormattingLabel(this.getFormatting());
      }

      public int getScale(Font font) {
         int scale = 100 + Utils.range(StringUtils.firstNumber(this.getFormattingToPresentation(), 0), -90, 100);
         return (int)((double)font.getSize() * ((double)scale / 100.0));
      }

      public boolean isCountdown() {
         return this.getSlideType().isCountdown();
      }

      public boolean isAutomaticShow() {
         return MusicController.Paragraph.EnumParagraphIntValues.MUSIC_AUTOMATIC_SHOW.getBoolean(this.intArray);
      }

      public static enum EnumParagraphIntValues {
         COUNTDOWN_FONT_TIME_SIZE(0),
         MUSIC_AUTOMATIC_SHOW(1);

         public final int index;

         private EnumParagraphIntValues(int index) {
            this.index = index;
         }

         public int getInt(int[] array) {
            return this.index >= 0 && this.index < array.length ? array[this.index] : 1;
         }

         public MusicController.Paragraph setInt(MusicController.Paragraph p, int value) {
            if (this.index >= 0 && this.index < p.intArray.length) {
               p.intArray[this.index] = value;
            }

            return p;
         }

         public int getInt(MusicController.Paragraph p) {
            return this.getInt(p.intArray);
         }

         public boolean getBoolean(int[] array) {
            return this.index >= 0 && this.index < array.length ? array[this.index] == 1 : false;
         }

         public MusicController.Paragraph setBoolean(MusicController.Paragraph p, boolean value) {
            if (this.index >= 0 && this.index < p.intArray.length) {
               p.intArray[this.index] = value ? 1 : 0;
            }

            return p;
         }

         public boolean getBoolean(MusicController.Paragraph p) {
            return this.getBoolean(p.intArray);
         }
      }
   }
}
