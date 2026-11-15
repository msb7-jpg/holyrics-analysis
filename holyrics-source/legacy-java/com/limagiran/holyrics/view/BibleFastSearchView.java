package com.limagiran.holyrics.view;

import com.limagiran.bible.Book;
import com.limagiran.holyrics.event.BibleFastSearchListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.VideoUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public final class BibleFastSearchView extends JDialog {
   private static final int BOOK = 0;
   private static final int CHAPTER = 1;
   private static final int VERSE = 2;
   private static final Font FONT_TITLE = new Font("Georgia", 1, 36);
   private static final Font FONT_VALUES = new Font("Lucida Console", 0, 36);
   private static final Font FONT_HINT = new Font("Trebuchet MS", 1, 18);
   private final BibleFastSearchListener listener;
   private volatile BibleFastSearchListener currentListener;
   private JTextField textField;
   private int inUse = 0;
   private final String[] fields = new String[]{"", "", ""};
   private final List<BibleFastSearchView.BookSearch> booksSearch = new ArrayList<>();
   private final List<String> booksHint = new ArrayList<>();
   private Book book;
   private int chapterSize = 0;
   private int chapter = 0;
   private int verseSize = 0;
   private int verse = 0;
   private boolean paintUnderline = true;
   private boolean alert = false;
   private Color alertColor = new Color(153, 0, 0);
   private Thread alertThread;
   private static final Color COLOR_ALERT_C1 = new Color(153, 0, 0);
   private static final Color COLOR_ALERT_C2 = new Color(255, 71, 71);

   public BibleFastSearchView(BibleFastSearchListener listener) {
      this.listener = listener;
      this.init();
   }

   private void init() {
      this.setModal(true);
      this.setUndecorated(true);
      this.setLayout(new BorderLayout());
      this.textField = new JTextField() {
         @Override
         protected void paintComponent(Graphics g) {
            BibleFastSearchView.this.paintTextField((Graphics2D)g);
         }
      };
      this.textField.setDocument(this.createDocument());
      this.textField.addKeyListener(this.createKeyListener());
      this.add(this.textField, "Center");
      this.textField.setFocusTraversalKeys(0, Collections.EMPTY_SET);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, this.fields::toString));
      Utils.startThreadDaemon(() -> {
         while (true) {
            Utils.sleep(500L);
            this.paintUnderline = !this.paintUnderline;
            SwingUtilities.invokeLater(() -> BooleanOptional.runIfTrue(this.isVisible(), this.textField::repaint));
         }
      });
   }

   public void start(char startsWith, BibleFastSearchListener alternativeListener) {
      if (startsWith >= '4' && startsWith <= '9') {
         this.start(BibleMain.getInstance().getBook(), startsWith, alternativeListener);
      } else {
         this.start(null, startsWith, alternativeListener);
      }
   }

   private void start(Book book, char startsWith, BibleFastSearchListener alternativeListener) {
      synchronized (this) {
         this.currentListener = alternativeListener != null ? alternativeListener : this.listener;
      }

      SwingUtilities.invokeLater(
         () -> {
            this.requestFocus();
            this.getContentPane().requestFocus();
            this.textField.setFocusable(false);
            this.inUse = 0;
            Arrays.fill(this.fields, "");
            this.book = null;
            this.chapterSize = 0;
            this.chapter = 0;
            this.verseSize = 0;
            this.verse = 0;
            this.alert = false;
            this.booksSearch.clear();
            this.booksSearch
               .addAll(
                  this.currentListener
                     .getBible()
                     .books
                     .stream()
                     .map(BibleFastSearchView.BookSearch::new)
                     .filter(b -> !b.name.equalsIgnoreCase("jó"))
                     .collect(Collectors.toList())
               );
            Optional.ofNullable(book).ifPresent(this::bookFound);
            this.textField.setText(String.valueOf(startsWith != 0 ? startsWith : ""));
            if (!this.textField.getText().isEmpty() || this.book != null || startsWith == 0) {
               this.setPreferredSize(new Dimension(Math.min(900, VideoUtils.getSizeDisplay().width - 50), 500));
               this.pack();
               this.setLocationRelativeTo(null);
               this.textField.setFocusable(true);
               this.textField.requestFocus();
               super.setVisible(true);
            }
         }
      );
   }

   public void start(char startsWith) {
      this.start(startsWith, null);
   }

   private void paintTextField(Graphics2D g2) {
      Dimension d = this.getSize();
      G2Utils.setupGraphics(g2);
      g2.setColor(StyleUI.COLOR_FAST_SEARCH_BACKGROUND);
      g2.fillRect(0, 0, d.width, d.height);
      g2.setColor(StyleUI.COLOR_FAST_SEARCH_FOREGROUND_TITLE);
      g2.setFont(FONT_TITLE.deriveFont(1));
      G2Utils.drawString(d, g2, 0.0, G2Utils.VerticalAlign.MIDDLE, I18N.WORD_BOOK.val(), "", "", I18N.WORD_CHAPTER.val(), "", "", I18N.WORD_VERSE.val(), "", "");
      FontMetrics fm = g2.getFontMetrics();
      g2.setColor(StyleUI.COLOR_FAST_SEARCH_FOREGROUND_VALUE);
      g2.setFont(FONT_VALUES);
      G2Utils.drawString(
         d, g2, 0.0, fm, G2Utils.VerticalAlign.MIDDLE, "", this.getTextToDraw(0), "", "", this.getTextToDraw(1), "", "", this.getTextToDraw(2), ""
      );
      g2.setColor(StyleUI.COLOR_FAST_SEARCH_FOREGROUND_HINT);
      g2.setFont(FONT_HINT);
      String hint = this.booksHint.stream().collect(Collectors.joining(" - "));

      while ((double)g2.getFontMetrics().stringWidth(hint) > (double)d.width * 0.98) {
         g2.setFont(g2.getFont().deriveFont((float)g2.getFont().getSize() - 1.0F));
      }

      G2Utils.drawString(d, g2, 0.0, fm, G2Utils.VerticalAlign.MIDDLE, "", "", hint, "", "", "", "", "", "");
      String format = "%s: %s";
      if (this.inUse == 1) {
         G2Utils.drawString(
            d, g2, 0.0, fm, G2Utils.VerticalAlign.MIDDLE, "", "", "", "", "", String.format("%s: %s", I18N.WORD_CHAPTERS.val(), this.chapterSize), "", "", ""
         );
      }

      if (this.inUse == 2) {
         G2Utils.drawString(
            d, g2, 0.0, fm, G2Utils.VerticalAlign.MIDDLE, "", "", "", "", "", "", "", "", String.format("%s: %s", I18N.WORD_VERSES.val(), this.verseSize)
         );
      }

      g2.setColor(StyleUI.COLOR_FAST_SEARCH_FOREGROUND_VALUE);
      g2.setFont(FONT_TITLE.deriveFont(1, 16.0F));
      Rectangle r = new Rectangle();
      r.height = g2.getFontMetrics().getHeight() + 10;
      r.width = g2.getFontMetrics().stringWidth(I18N.MSG_ESC_TO_CANCEL.val()) + 10;
      r.y += 10;
      r.x = d.width - r.width - 10;
      G2Utils.drawString(r, g2, 0.0, G2Utils.VerticalAlign.MIDDLE, I18N.MSG_ESC_TO_CANCEL.val());
      G2Utils.drawRoundRect(r, g2, 3.0F);
      if (this.alert) {
         g2.setColor(this.alertColor);
         g2.setFont(FONT_TITLE.deriveFont(22.0F));
         r.setSize(new Dimension(d.width, g2.getFontMetrics().getHeight()));
         r.x = 0;
         r.y = d.height - 50;
         G2Utils.drawString(r, g2, 0.0, G2Utils.VerticalAlign.MIDDLE, I18N.MSG_NO_RESULTS_FOUND.val());
      }

      g2.setColor(Color.GRAY);
      g2.drawRect(0, 0, d.width - 1, d.height - 1);
      g2.dispose();
   }

   private void bookFound(Book b) {
      this.book = b;
      this.fields[0] = this.book.name;
      this.chapterSize = this.book.getAmountChapter();
      this.inUse++;
      this.alert = false;
      this.booksHint.clear();
   }

   private void chapterFound(int number) {
      this.chapter = number;
      this.fields[1] = String.valueOf(this.chapter);
      this.verseSize = this.book.getVerses(this.chapter).size();
      this.inUse++;
   }

   private void verseFound(int number) {
      this.verse = number;
      this.fields[2] = String.valueOf(this.verse);
      this.inUse = 0;
      Arrays.fill(this.fields, "");
      this.currentListener.fastSearchVerseFound(this.book.getIdInt(), this.chapter, this.verse);
      this.exit();
   }

   private String getTextToDraw(int text) {
      try {
         return this.fields[text] + (this.inUse == text ? (this.paintUnderline ? "_" : " ") : "");
      } catch (Exception var3) {
         return "";
      }
   }

   private PlainDocument createDocument() {
      return new PlainDocument() {
         private final List<String> SAFE_CHAR = Arrays.asList(" ", ",", ";", ":");

         @Override
         public void remove(int offs, int len) throws BadLocationException {
            super.remove(offs, len);
            this.search(super.getText(0, super.getLength()));
         }

         @Override
         public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            String currentText = super.getText(0, super.getLength());
            if (str != null && str.length() == 1 && this.SAFE_CHAR.stream().noneMatch(str::equals)) {
               this.search(currentText + str);
            }
         }

         private void search(String value) throws BadLocationException {
            try {
               switch (BibleFastSearchView.this.inUse) {
                  case 0:
                     this.searchBook(value);
                     break;
                  case 1:
                  case 2:
                     this.search(
                        value,
                        BibleFastSearchView.this.inUse == 1 ? BibleFastSearchView.this.chapterSize : BibleFastSearchView.this.verseSize,
                        BibleFastSearchView.this.inUse
                     );
               }
            } catch (BadLocationException var3) {
               throw var3;
            } catch (Exception var4) {
            }
         }

         private void setText(String text) throws BadLocationException {
            super.remove(0, super.getLength());
            super.insertString(0, text, null);
         }

         private void searchBook(String value) throws BadLocationException, Exception {
            String search = Utils.removeAccent(value).toLowerCase();
            if (value.equalsIgnoreCase("jó") && BibleFastSearchView.this.currentListener.getBible().books.stream().anyMatch(b -> b.name.equalsIgnoreCase("jó"))
               )
             {
               BibleFastSearchView.this.bookFound(
                  BibleFastSearchView.this.currentListener
                     .getBible()
                     .books
                     .stream()
                     .filter(b -> b.name.equalsIgnoreCase("jó"))
                     .findFirst()
                     .orElseThrow(Exception::new)
               );
               this.setText("");
            } else {
               List<BibleFastSearchView.BookSearch> namesFound = BibleFastSearchView.this.booksSearch
                  .stream()
                  .filter(b -> b.namesToSearch.stream().anyMatch(i -> i.startsWith(search)))
                  .collect(Collectors.toList());
               List<BibleFastSearchView.BookSearch> abbrevFound = BibleFastSearchView.this.booksSearch
                  .stream()
                  .filter(b -> b.abbrevToSearch.stream().anyMatch(i -> i.startsWith(search)))
                  .collect(Collectors.toList());
               if (abbrevFound.size() != 1 || !namesFound.isEmpty() && (namesFound.size() != 1 || abbrevFound.get(0).id != namesFound.get(0).id)) {
                  if (namesFound.size() != 1 || !abbrevFound.isEmpty() && (abbrevFound.size() != 1 || namesFound.get(0).id != abbrevFound.get(0).id)) {
                     BibleFastSearchView.BookSearch bs = BibleFastSearchView.this.booksSearch
                        .stream()
                        .filter(b -> b.namesToSearch.stream().anyMatch(i -> i.equals(search)))
                        .findFirst()
                        .orElse(null);
                     if (bs != null) {
                        BibleFastSearchView.this.bookFound(bs.book);
                        this.setText("");
                     } else if (namesFound.size() >= 1) {
                        BibleFastSearchView.this.booksHint.clear();
                        if (namesFound.size() + abbrevFound.size() <= 24) {
                           BibleFastSearchView.this.booksHint.addAll(namesFound.stream().map(i -> i.name).collect(Collectors.toList()));
                           BibleFastSearchView.this.booksHint.addAll(abbrevFound.stream().map(i -> i.abbrev).collect(Collectors.toList()));
                        }

                        this.setText(value);
                        BibleFastSearchView.this.fields[0] = value;
                        BibleFastSearchView.this.alert = false;
                     } else {
                        BibleFastSearchView.this.alert();
                     }
                  } else {
                     BibleFastSearchView.this.bookFound(namesFound.get(0).book);
                     this.setText("");
                  }
               } else {
                  BibleFastSearchView.this.bookFound(abbrevFound.get(0).book);
                  this.setText("");
               }
            }
         }

         private void search(String value, int size, int type) throws BadLocationException {
            BibleFastSearchView.this.booksHint.clear();
            if (!value.isEmpty()) {
               value = value.replaceAll("^[^\\d]$", "");
               if (value.isEmpty()) {
                  BibleFastSearchView.this.alert();
               } else {
                  int number = Integer.parseInt(value);
                  if (number >= 0 && number <= size) {
                     if (value.length() != String.valueOf(size).length()) {
                        BibleFastSearchView.this.alert = false;
                        if (number * 10 > size) {
                           this.found(number, type);
                           super.remove(0, super.getLength());
                        } else {
                           this.setText(value);
                           BibleFastSearchView.this.fields[type] = value;
                        }
                     } else if (number == 0) {
                        BibleFastSearchView.this.alert();
                     } else {
                        BibleFastSearchView.this.alert = false;
                        this.found(number, type);
                        super.remove(0, super.getLength());
                     }
                  } else {
                     BibleFastSearchView.this.alert();
                  }
               }
            }
         }

         private void found(int value, int type) {
            switch (type) {
               case 1:
                  BibleFastSearchView.this.chapterFound(value);
                  break;
               case 2:
                  BibleFastSearchView.this.verseFound(value);
            }
         }
      };
   }

   private void alert() {
      this.alert = true;
      Toolkit.getDefaultToolkit().beep();
      BooleanOptional.runIfTrue(this.alertThread == null || !this.alertThread.isAlive(), this.alertThread = this.createAlertThread()::start);
   }

   private Thread createAlertThread() {
      Thread thread = new Thread(() -> IntStream.rangeClosed(1, 10).forEach(i -> {
            this.alertColor = i % 2 == 0 ? COLOR_ALERT_C1 : COLOR_ALERT_C2;
            SwingUtilities.invokeLater(this.textField::repaint);
            Utils.sleep(100L);
         }));
      thread.setDaemon(true);
      return thread;
   }

   private KeyListener createKeyListener() {
      return new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            try {
               JTextField field = (JTextField)e.getSource();
               switch (e.getKeyCode()) {
                  case 8:
                     String s = field.getText();
                     BibleFastSearchView.this.fields[BibleFastSearchView.this.inUse] = s.isEmpty() ? "" : s.substring(0, s.length() - 1);
                     field.repaint();
                     break;
                  case 9:
                  case 10:
                  case 32:
                  case 40:
                  case 44:
                  case 59:
                  case 513:
                     if (BibleFastSearchView.this.inUse == 1) {
                        BibleFastSearchView.this.chapterFound(Integer.parseInt(field.getText()));
                        field.setText("");
                     } else if (BibleFastSearchView.this.inUse == 2) {
                        BibleFastSearchView.this.verseFound(Integer.parseInt(field.getText()));
                        field.setText("");
                     }
                     break;
                  case 35:
                  case 36:
                  case 37:
                  case 39:
                     e.consume();
               }

               field.setCaretPosition(field.getText().length());
               field.repaint();
            } catch (Exception var4) {
            }
         }
      };
   }

   @Override
   public void setVisible(boolean flag) {
   }

   public void exit() {
      super.setVisible(false);
      super.dispose();
   }

   private static class BookSearch {
      private final Book book;
      private final String name;
      private final List<String> namesToSearch = new ArrayList<>();
      private final String abbrev;
      private final List<String> abbrevToSearch = new ArrayList<>();
      private final int id;

      public BookSearch(Book book) {
         this.book = book;
         this.name = book.name;
         this.namesToSearch.add(Utils.removeAccent(this.name).toLowerCase());
         if (this.name.charAt(0) >= '0' && this.name.charAt(0) <= '9' && this.name.charAt(1) == ' ') {
            this.namesToSearch.add(new StringBuilder(Utils.removeAccent(this.name).toLowerCase()).deleteCharAt(1).toString());
         }

         this.abbrev = book.abbrev;
         this.abbrevToSearch.add(Utils.removeAccent(book.abbrev).toLowerCase());
         if (this.abbrev.charAt(0) >= '0' && this.abbrev.charAt(0) <= '9' && this.abbrev.charAt(1) != ' ') {
            this.abbrevToSearch.add(new StringBuilder(Utils.removeAccent(this.abbrev).toLowerCase()).insert(1, ' ').toString());
         }

         this.id = book.getIdInt();
      }
   }
}
