package com.limagiran.holyrics.util;

import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.filechooser.WebFileChooserUI;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.swing.ListModelAddRemove;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.GlyphView.GlyphPainter;
import javax.swing.text.Position.Bias;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;
import javax.swing.tree.TreePath;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.xml.bind.DatatypeConverter;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

public class SwingUtils {
   public static boolean hackHTMLBase64() {
      try {
         new JLabel("<html>");
         JLabel e = null;
         Field modifiersField = Field.class.getDeclaredField("modifiers");
         modifiersField.setAccessible(true);
         Field field = HTMLEditorKit.class.getDeclaredField("defaultFactory");
         field.setAccessible(true);
         modifiersField.setInt(field, field.getModifiers() & -17);
         field.set(null, new HTMLFactory() {
            @Override
            public View create(Element elem) {
               AttributeSet attrs = elem.getAttributes();
               Object elementName = attrs.getAttribute("$ename");
               Object o = elementName != null ? null : attrs.getAttribute(StyleConstants.NameAttribute);
               if (o instanceof Tag) {
                  Tag kind = (Tag)o;
                  if (kind == Tag.IMG) {
                     return new SwingUtils.ImageViewCustom(elem);
                  }
               }

               return super.create(elem);
            }
         });
         field = BasicHTML.class.getDeclaredField("basicHTMLViewFactory");
         field.setAccessible(true);
         modifiersField.setInt(field, field.getModifiers() & -17);
         field.set(null, new SwingUtils.BasicHTMLViewFactoryCustom());
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   public static void changeLAF(String clazz, Component c, boolean pack) {
      try {
         UIManager.setLookAndFeel(clazz);
         if (clazz.contains("nimbus")) {
            UIDefaults ui = UIManager.getLookAndFeelDefaults();
            ui.put("Table.gridColor", new Color(200, 200, 200));
            ui.put("Table.alternateRowColor", StyleUI.COLOR_ALTERNATE_ROW);
            ui.put("Table.disabled", false);
            ui.put("textForeground", StyleUI.FOREGROUND);
            ui.put("Table.showGrid", true);
            ui.put("Table.intercellSpacing", new Dimension(1, 1));
            ui.put("ScrollBar.minimumThumbSize", new Dimension(30, 30));
            ui.put("TextPane.contentMargins", new Insets(0, 3, 0, 3));
            ui.put("Button.contentMargins", new Insets(5, 8, 5, 8));
            ui.put("ToggleButton.contentMargins", new Insets(5, 3, 5, 3));
            ui.put("nimbusSelectionBackground", new Color(51, 153, 255));
            ui.put("textBackground", new Color(51, 153, 255));
            ui.put("RadioButtonMenuItem.font", new Font("SansSerif", 0, 14));
            ui.put("CheckBoxMenuItem.font", new Font("SansSerif", 0, 14));
            ui.put("Menu.font", new Font("SansSerif", 0, 14));
            ui.put("PopupMenu.font", new Font("SansSerif", 0, 14));
            ui.put("MenuItem.font", new Font("SansSerif", 0, 14));
            ui.put("RadioButtonMenuItem.textIconGap", 8);
            ui.put("CheckBoxMenuItem.textIconGap", 8);
            ui.put("Menu.textIconGap", 8);
            ui.put("PopupMenu.textIconGap", 8);
            ui.put("MenuItem.textIconGap", 8);
            ui.put("List.background", StyleUI.BACKGROUND);
            ui.put("TextArea.background", StyleUI.BACKGROUND);
            ui.put("Table.background", StyleUI.BACKGROUND);
            ui.put("TextField.background", StyleUI.BACKGROUND);
            ui.put("ComboBox:\"ComboBox.listRenderer\".background", StyleUI.BACKGROUND);
         }

         if (c != null) {
            runOnUIThread(() -> {
               c.setVisible(false);
               if (c instanceof Window) {
                  ((Window)c).dispose();
               }

               SwingUtilities.updateComponentTreeUI(c);
               c.setVisible(true);
               if (pack && c instanceof Window) {
                  ((Window)c).pack();
               }
            });
         }
      } catch (Exception var4) {
         Utils.error(var4.toString());
      }
   }

   public static void changeLAF(String clazz) {
      changeLAF(clazz, null, false);
   }

   public static boolean pointOnTheSelectedItem(JList list, Point point) {
      Rectangle r = list.getCellBounds(list.getMinSelectionIndex(), list.getMaxSelectionIndex());
      return r != null && r.contains(point);
   }

   public static boolean pointOnTheSelectedItem(JTree tree, Point point) {
      TreePath[] selection = tree.getSelectionPaths();
      if (selection == null) {
         return false;
      } else {
         TreePath path = tree.getClosestPathForLocation(point.x, point.y);
         return Arrays.stream(selection).anyMatch(tp -> tp.equals(path));
      }
   }

   public static void removeSelectedIndices(JList list, boolean clearSelection) {
      int[] i = list.getSelectedIndices();
      if (i.length != 0) {
         try {
            Object o = list.getModel();
            if (o instanceof DefaultListModel) {
               ((DefaultListModel)list.getModel()).removeRange(i[0], i[i.length - 1]);
            } else if (o instanceof ListModelAddRemove) {
               ((ListModelAddRemove)list.getModel()).removeElements(list.getSelectedValuesList());
            }

            if (clearSelection) {
               list.clearSelection();
            }
         } catch (Exception var4) {
         }
      }
   }

   public static void runOnUIThread(Runnable runnable) {
      try {
         if (EventQueue.isDispatchThread()) {
            runnable.run();
         } else {
            SwingUtilities.invokeAndWait(runnable);
         }
      } catch (Exception var2) {
         Log.send(var2);
      }
   }

   public static void cloneBounds(AbsoluteConstraints ac, Rectangle r) {
      ac.x = r.x;
      ac.y = r.y;
      ac.width = r.width;
      ac.height = r.height;
   }

   public static void cloneBounds(AbsoluteConstraints of, AbsoluteConstraints to) {
      to.x = of.x;
      to.y = of.y;
      to.width = of.width;
      to.height = of.height;
   }

   public static JTextField createJTextFieldPlaceHolder(final String ph, final Color phColor) {
      return new JTextField() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.getText().replace(" ", "").isEmpty()) {
               Graphics2D g2 = (Graphics2D)g.create();
               g2.setColor(phColor);
               g2.setFont(this.getFont());
               G2Utils.drawString(this.getSize(), g2, 0.0, G2Utils.VerticalAlign.MIDDLE, ph);
               g2.dispose();
            }
         }
      };
   }

   public static JTextField createJTextFieldPlaceHolder(String ph) {
      return createJTextFieldPlaceHolder(ph, Color.GRAY);
   }

   public static JTextArea createJTextAreaPlaceHolder(final String ph, final Color phColor) {
      final JTextArea _return = new JTextArea() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.getText().replace(" ", "").replace("\n", "").isEmpty()) {
               Graphics2D g2 = (Graphics2D)g.create();
               g2.setColor(phColor);
               g2.setFont(this.getFont());
               G2Utils.drawString(this.getSize(), g2, 0.0, G2Utils.VerticalAlign.MIDDLE, ph);
               g2.dispose();
            }
         }
      };
      _return.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            _return.repaint();
         }
      });
      return _return;
   }

   public static JTextPane createJTextPanePlaceHolder(String ph) {
      return createJTextPanePlaceHolder(ph, Color.GRAY);
   }

   public static JTextPane createJTextPanePlaceHolder(final String ph, final Color phColor) {
      final JTextPane _return = new JTextPane() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.getText().replace(" ", "").replace("\n", "").isEmpty()) {
               Graphics2D g2 = (Graphics2D)g.create();
               g2.setColor(phColor);
               g2.setFont(this.getFont());
               G2Utils.drawString(this.getSize(), g2, 0.0, G2Utils.VerticalAlign.MIDDLE, ph);
               g2.dispose();
            }
         }
      };
      _return.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            _return.repaint();
         }
      });
      return _return;
   }

   public static JPasswordField createJPasswordFieldPlaceHolder(final String ph, final Color phColor) {
      return new JPasswordField() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.getPassword().length == 0) {
               Graphics2D g2 = (Graphics2D)g.create();
               g2.setColor(phColor);
               g2.setFont(this.getFont());
               G2Utils.drawString(this.getSize(), g2, 0.0, G2Utils.VerticalAlign.MIDDLE, ph);
               g2.dispose();
            }
         }
      };
   }

   public static JPasswordField createJPasswordFieldPlaceHolder(String ph) {
      return createJPasswordFieldPlaceHolder(ph, Color.GRAY);
   }

   public static JTextArea createJTextAreaPlaceHolder(String ph) {
      return createJTextAreaPlaceHolder(ph, Color.GRAY);
   }

   public static void maximize(JDialog dialog, int margin) {
      Dimension d = VideoUtils.getSizeDisplay();
      d.width -= margin;
      d.height -= margin;
      dialog.setSize(d);
      dialog.setPreferredSize(d);
      dialog.setLocationRelativeTo(null);
   }

   public static void maximize(JDialog dialog) {
      maximize(dialog, 0);
   }

   public static void autoResize(JTable table, int min, int max) {
      TableColumnModel tcm = table.getColumnModel();

      for (int column = 0; column < table.getColumnCount(); column++) {
         int width = min;

         for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component comp = table.prepareRenderer(renderer, row, column);
            width = Math.max(comp.getPreferredSize().width + 5, width);
         }

         tcm.getColumn(column).setPreferredWidth(Math.min(width, max));
      }
   }

   public static int autoResizeByHeader(JTable table, int min) {
      table.setAutoResizeMode(0);
      TableColumnModel tcm = table.getColumnModel();
      int w = 0;

      for (int col = 0; col < tcm.getColumnCount(); col++) {
         TableColumn tableColumn = tcm.getColumn(col);
         TableCellRenderer renderer = tableColumn.getHeaderRenderer();
         if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
         }

         Component c = renderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, -1, col);
         Dimension d = c.getPreferredSize();
         w = d.width < min ? min : d.width;
         tcm.getColumn(col).setPreferredWidth(w);
      }

      return w;
   }

   public static String getHTMLCenterJLabel(String v) {
      return String.format("<html><style> div {text-align:center;}</style><div>%s</div>", v.replace("\n", "<br>"));
   }

   public static void blockSpinnerUpDown(JSpinner spinner) {
      ((DefaultEditor)spinner.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
               case 38:
               case 40:
                  e.consume();
            }
         }
      });
   }

   public static int[] getGrid(int l, Dimension s, Dimension r) {
      for (int x = 1; x < Integer.MAX_VALUE; x++) {
         for (int y = 1; y <= x; y++) {
            if (x * y >= l) {
               Dimension d1 = ScreenUtils.adjust(new Dimension(s.width / x, s.height / y), r);
               Dimension d2 = ScreenUtils.adjust(new Dimension(s.width / y, s.height / x), r);
               return d1.width * d1.height < d2.width * d2.height ? new int[]{x, y} : new int[]{y, x};
            }
         }
      }

      return null;
   }

   public static int[] getBestSquareGrid(Dimension size, int length) {
      if (length == 1) {
         return new int[]{1, 1};
      } else {
         int l = Math.min(size.width, size.height);

         while (true) {
            int w = size.width / l;
            int h = size.height / l;
            if (w * h >= length) {
               return new int[]{w, h};
            }

            l--;
         }
      }
   }

   public static <T> DefaultListModel<T> createListModel(List<T> list) {
      DefaultListModel dlm = new DefaultListModel();
      list.stream().forEach(dlm::addElement);
      return dlm;
   }

   public static void setJTextPaneFont(JTextPane textPane, Font font) {
      try {
         MutableAttributeSet attrs = textPane.getInputAttributes();
         StyleConstants.setFontFamily(attrs, font.getFamily());
         StyleConstants.setFontSize(attrs, font.getSize());
         StyleConstants.setItalic(attrs, (font.getStyle() & 2) != 0);
         StyleConstants.setBold(attrs, (font.getStyle() & 1) != 0);
         StyledDocument doc = textPane.getStyledDocument();
         doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
      } catch (Exception var4) {
      }
   }

   public static void setJTextPaneLineSpacing(JTextPane textPane, float f) {
      try {
         SimpleAttributeSet set = new SimpleAttributeSet();
         StyleConstants.setLineSpacing(set, f);
         int start = textPane.getSelectionStart();
         int end = textPane.getSelectionEnd();
         textPane.selectAll();
         textPane.setParagraphAttributes(set, true);
         textPane.setSelectionStart(start);
         textPane.setSelectionEnd(end);
      } catch (Exception var5) {
      }
   }

   public static int adjustFontOfAllJLabel(JPanel panel, int maxSize, float marginPercent) {
      List<JLabel> labels = Arrays.asList(panel.getComponents())
         .stream()
         .map(o -> o instanceof JLabel ? (JLabel)o : null)
         .filter(Objects::nonNull)
         .collect(Collectors.toList());
      JLabel l = labels.stream()
         .sorted((l1, l2) -> l2.getFontMetrics(l2.getFont()).stringWidth(l2.getText()) - l1.getFontMetrics(l1.getFont()).stringWidth(l1.getText()))
         .findFirst()
         .orElse(null);

      int var7;
      try {
         if (l == null) {
            return maxSize;
         }

         Font f = new Font(l.getFont().getName(), l.getFont().getStyle(), maxSize);

         while ((float)l.getFontMetrics(f).stringWidth(l.getText()) > (float)l.getWidth() - (float)l.getWidth() * marginPercent) {
            f = new Font(f.getName(), f.getStyle(), f.getSize() - 1);
         }

         Font fFinal = f;
         labels.stream().forEach(lbl -> lbl.setFont(fFinal));
         var7 = fFinal.getSize();
      } finally {
         panel.revalidate();
         panel.repaint();
      }

      return var7;
   }

   public static PlainDocument createDocMaxSize(final int max) {
      return new PlainDocument() {
         @Override
         public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (this.getLength() < max) {
               int free = max - this.getLength();
               if (str != null && str.length() > free) {
                  str = str.substring(0, free);
               }

               super.insertString(offs, str, a);
            } else {
               Toolkit.getDefaultToolkit().beep();
            }
         }
      };
   }

   public static PlainDocument createDocOnlyWordNumberAndSpace(final int max) {
      return new PlainDocument() {
         @Override
         public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str != null) {
               str = str.replaceAll("[^\\d\\w _àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇ]", "");
               if (max == -1) {
                  super.insertString(offs, str, a);
               }

               if (this.getLength() < max) {
                  int free = max - this.getLength();
                  if (str.length() > free) {
                     str = str.substring(0, free);
                  }

                  super.insertString(offs, str, a);
               } else {
                  Toolkit.getDefaultToolkit().beep();
               }
            }
         }
      };
   }

   public static PlainDocument createDocOnlyNumber(final int max) {
      return new PlainDocument() {
         @Override
         public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str != null) {
               str = str.replaceAll("[^\\d]", "");
               if (max == -1) {
                  super.insertString(offs, str, a);
               }

               if (this.getLength() < max) {
                  int free = max - this.getLength();
                  if (str.length() > free) {
                     str = str.substring(0, free);
                  }

                  super.insertString(offs, str, a);
               } else {
                  Toolkit.getDefaultToolkit().beep();
               }
            }
         }
      };
   }

   public static void toMiddleHorizontalScrollBar(JScrollPane scroll) {
      JScrollBar hsb = scroll.getHorizontalScrollBar();
      SwingUtilities.invokeLater(() -> hsb.setValue((hsb.getMaximum() - hsb.getVisibleAmount() - hsb.getMinimum()) / 2));
   }

   public static PlainDocument createDocOnlyWordNumberAndSpace() {
      return createDocOnlyWordNumberAndSpace(-1);
   }

   public static PlainDocument createDocOnlyNumber() {
      return createDocOnlyNumber(-1);
   }

   public static void setJPanelToOpaqueFalse(Container cs) {
      for (Component c : cs.getComponents()) {
         if (c instanceof JPanel && !(c instanceof WebPanel)) {
            ((JPanel)c).setOpaque(false);
         }

         if (c instanceof Container) {
            setJPanelToOpaqueFalse((Container)c);
         }
      }
   }

   public static void setupComponentMinimumSize(Container cs, Dimension min) {
      for (Component c : cs.getComponents()) {
         c.setMinimumSize(min);
         if (c instanceof Container) {
            setupComponentMinimumSize((Container)c, min);
         }
      }
   }

   public static Component searchComponent(Container cs, Class clazz) {
      for (Component c : cs.getComponents()) {
         if (clazz.isInstance(c)) {
            return c;
         }

         if (c instanceof Container) {
            Component cc = searchComponent((Container)c, clazz);
            if (clazz.isInstance(cc)) {
               return cc;
            }
         }
      }

      return null;
   }

   public static <T> void setupComponentIf(Container cs, Predicate<Component> predicate, Consumer<Component> consumer) {
      for (Component c : cs.getComponents()) {
         if (predicate.test(c)) {
            consumer.accept(c);
         }

         if (c instanceof Container) {
            setupComponentIf((Container)c, predicate, consumer);
         }
      }
   }

   public static void setupUIDefaults() {
      Arrays.asList(UIManager.getLookAndFeelDefaults(), UIManager.getLookAndFeel().getDefaults()).forEach(ui -> {
         ui.put("Table.gridColor", new Color(200, 200, 200));
         ui.put("Table.alternateRowColor", StyleUI.COLOR_ALTERNATE_ROW);
         ui.put("Table.disabled", false);
         ui.put("textForeground", Color.RED);
         ui.put("Table.showGrid", true);
         ui.put("Table.intercellSpacing", new Dimension(1, 1));
         ui.put("ScrollBar.minimumThumbSize", new Dimension(30, 30));
         ui.put("TextPane.contentMargins", new Insets(0, 3, 0, 3));
         ui.put("Button.contentMargins", new Insets(5, 8, 5, 8));
         ui.put("ToggleButton.contentMargins", new Insets(5, 3, 5, 3));
         ui.put("nimbusSelectionBackground", new Color(51, 153, 255));
         ui.put("textBackground", new Color(51, 153, 255));
         ui.put("RadioButtonMenuItem.font", new Font("SansSerif", 0, 14));
         ui.put("CheckBoxMenuItem.font", new Font("SansSerif", 0, 14));
         ui.put("Menu.font", new Font("SansSerif", 0, 14));
         ui.put("PopupMenu.font", new Font("SansSerif", 0, 14));
         ui.put("MenuItem.font", new Font("SansSerif", 0, 14));
         ui.put("RadioButtonMenuItem.textIconGap", 8);
         ui.put("CheckBoxMenuItem.textIconGap", 8);
         ui.put("Menu.textIconGap", 8);
         ui.put("PopupMenu.textIconGap", 8);
         ui.put("MenuItem.textIconGap", 8);
         ui.put("List.background", StyleUI.BACKGROUND);
         ui.put("TextArea.background", StyleUI.BACKGROUND);
         ui.put("Table.background", StyleUI.BACKGROUND);
         ui.put("TextField.background", StyleUI.BACKGROUND);
         ui.put("ComboBox:\"ComboBox.listRenderer\".background", StyleUI.BACKGROUND);
         ui.put("Panel.opaque", false);
      });
   }

   public static UndoManager applyUndoAndRedo(JTextComponent c, boolean discardOnEnable) {
      final UndoManager manager = new UndoManager();
      Document doc = c.getDocument();
      doc.addUndoableEditListener(e -> manager.addEdit(e.getEdit()));
      c.addPropertyChangeListener(evt -> {
         if (discardOnEnable && evt.getPropertyName().equals("enabled") && Boolean.parseBoolean(evt.getNewValue().toString())) {
            manager.discardAllEdits();
         }
      });
      InputMap im = c.getInputMap(0);
      ActionMap am = c.getActionMap();
      im.put(KeyStroke.getKeyStroke(90, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
      im.put(KeyStroke.getKeyStroke(89, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");
      am.put("Undo", new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent e) {
            try {
               if (manager.canUndo()) {
                  manager.undo();
               }
            } catch (CannotUndoException var3) {
            }
         }
      });
      am.put("Redo", new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent e) {
            try {
               if (manager.canRedo()) {
                  manager.redo();
               }
            } catch (CannotUndoException var3) {
            }
         }
      });
      return manager;
   }

   public static UndoManager applyUndoAndRedo(JTextComponent c) {
      return applyUndoAndRedo(c, true);
   }

   public static void setupCCPUAndRedo(final JTextComponent c) {
      final UndoManager manager = applyUndoAndRedo(c);
      c.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            SwingUtils.show(c, manager, e);
         }
      });
   }

   public static JPopupMenu getCutCopyPasteUndoAndRedo(JTextComponent c, UndoManager undoManager) {
      JMenuItem cut = new JMenuItem(I18N.WORD_CUT.val());
      JMenuItem copy = new JMenuItem(I18N.WORD_COPY.val());
      JMenuItem paste = new JMenuItem(I18N.WORD_PASTE.val());
      JMenuItem undo = new JMenuItem(I18N.WORD_UNDO.val());
      JMenuItem redo = new JMenuItem(I18N.WORD_REDO.val());
      JPopupMenu popup = new JPopupMenu();
      cut.addActionListener(ae -> c.cut());
      copy.addActionListener(ae -> c.copy());
      paste.addActionListener(ae -> c.paste());
      undo.addActionListener(ae -> {
         try {
            if (undoManager.canUndo()) {
               undoManager.undo();
            }
         } catch (CannotUndoException var3x) {
         }
      });
      redo.addActionListener(ae -> {
         try {
            if (undoManager.canRedo()) {
               undoManager.redo();
            }
         } catch (CannotUndoException var3x) {
         }
      });
      Arrays.asList(cut, copy, paste, new JSeparator(0), undo, redo).forEach(popup::add);
      undo.setEnabled(undoManager.canUndo());
      redo.setEnabled(undoManager.canRedo());
      return popup;
   }

   public static void show(JTextComponent c, JPopupMenu popup, Point p, MouseEvent e) {
      if (e == null || SwingUtilities.isRightMouseButton(e)) {
         popup.show(c, p.x, p.y);
      }
   }

   public static void show(JTextComponent c, UndoManager um, MouseEvent e) {
      show(c, getCutCopyPasteUndoAndRedo(c, um), e.getPoint(), e);
   }

   public static void setupCutCopyPaste(final JTextComponent c) {
      c.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            SwingUtils.show(c, SwingUtils.getCutCopyPaste(c), e.getPoint(), e);
         }
      });
   }

   public static JPopupMenu getCutCopyPaste(JTextComponent c) {
      JMenuItem cut = new JMenuItem(I18N.WORD_CUT.val());
      JMenuItem copy = new JMenuItem(I18N.WORD_COPY.val());
      JMenuItem paste = new JMenuItem(I18N.WORD_PASTE.val());
      JPopupMenu popup = new JPopupMenu();
      cut.addActionListener(ae -> c.cut());
      copy.addActionListener(ae -> c.copy());
      paste.addActionListener(ae -> c.paste());
      Arrays.asList(cut, copy, paste).forEach(popup::add);
      return popup;
   }

   public static void requestFocusOnVisible(Component c) {
      Utils.startThreadDaemon(() -> {
         long start = System.currentTimeMillis();

         while (System.currentTimeMillis() - start < 10000L) {
            try {
               Utils.sleep(100L);
               c.getLocationOnScreen();
               Utils.sleep(10L);
               SwingUtilities.invokeLater(c::requestFocus);
               break;
            } catch (Exception var4) {
            }
         }
      });
   }

   public static void redAlert(Component c) {
      Color c1 = new Color(255, 71, 71);
      Color c2 = new Color(153, 0, 0);
      Utils.startThreadDaemon(() -> {
         for (int time = 0; time < 4; time++) {
            int timef = time;
            Utils.sleep(100L);
            runOnUIThread(() -> c.setForeground(timef % 2 == 0 ? c1 : c2));
         }
      });
   }

   public static void addMnemonics(JMenuBar menuBar) {
      List<Character> chars = new ArrayList<>();

      for (int i = 0; i < menuBar.getMenuCount(); i++) {
         JMenu menu = menuBar.getMenu(i);

         try {
            char[] var4 = menu.getText().toLowerCase().toCharArray();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; var6++) {
               Character c = var4[var6];
               if (!chars.contains(c) && c >= 'a' && c <= 'z') {
                  menu.setMnemonic(c);
                  chars.add(c);
                  break;
               }
            }
         } catch (Exception var8) {
         }

         addMnemonics(menuBar.getMenu(i));
      }
   }

   private static void addMnemonics(JMenu menu) {
      List<Character> chars = new ArrayList<>();

      for (int i = 0; i < menu.getItemCount(); i++) {
         JMenuItem m = menu.getItem(i);

         try {
            char[] var4 = m.getText().toLowerCase().toCharArray();
            int c = var4.length;

            for (int var6 = 0; var6 < c; var6++) {
               Character cx = var4[var6];
               if (!chars.contains(cx) && cx >= 'a' && cx <= 'z') {
                  m.setMnemonic(cx);
                  chars.add(cx);
                  break;
               }
            }
         } catch (Exception var8) {
         }
      }

      for (Component c : menu.getComponents()) {
         if (c instanceof JMenu) {
            addMnemonics((JMenuBar)c);
         }
      }
   }

   public static KeyAdapter escToExit(final Runnable run) {
      return new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            BooleanOptional.runIfTrue(e.getKeyCode() == 27, run::run);
         }
      };
   }

   public static void addHtmlShortcut(AbstractButton button, String text) {
      button.setText(String.format("<html>%s <span style=\"font-size:80%%\"><code>%s</code></span>", button.getText(), text));
   }

   public static void addHtmlTooltip(AbstractButton button, String text) {
      button.setText(String.format("<html>%s <b><span style='color:#333333'>%s</span></b>", button.getText(), text));
   }

   public static <T> void popupMenuTrigger(MouseEvent e, JList<T> jlist, Consumer<T> consumer) {
      if (!pointOnTheSelectedItem(jlist, e.getPoint())) {
         int index = jlist.locationToIndex(e.getPoint());
         if (index >= 0 && index != jlist.getSelectedIndex()) {
            jlist.setSelectedIndex(index);
         }
      }

      Optional.<T>ofNullable(jlist.getSelectedValue()).ifPresent(consumer);
   }

   public static void popupMenuTrigger(MouseEvent e, JTree jtree, Consumer<TreePath> consumer) {
      if (!pointOnTheSelectedItem(jtree, e.getPoint())) {
         int index = jtree.getClosestRowForLocation(e.getPoint().x, e.getPoint().y);
         int[] selection = jtree.getSelectionRows();
         if (index >= 0 && (selection == null || Arrays.stream(selection).anyMatch(i -> i == index))) {
            jtree.setSelectionRow(index);
         }
      }

      Optional.ofNullable(jtree.getSelectionPath()).ifPresent(consumer);
   }

   public static void blockKeyboardSpinner(JSpinner spinner) {
      try {
         NumberEditor editor = (NumberEditor)spinner.getEditor();
         editor.getTextField().setEnabled(true);
         editor.getTextField().setEditable(false);
         spinner.setFocusable(false);
         if (spinner instanceof WebSpinner) {
            ((WebSpinner)spinner).setDrawFocus(false);
         }
      } catch (Exception var2) {
      }
   }

   public static boolean equals(AbsoluteConstraints ac1, AbsoluteConstraints ac2) {
      return ac1.x == ac2.x && ac1.y == ac2.y && ac1.width == ac2.width && ac1.height == ac2.height;
   }

   public static void setupButtonDisableIcon(JButton b, float opacity) {
      try {
         b.setDisabledIcon(new ImageIcon(Img.opacity(((ImageIcon)b.getIcon()).getImage(), opacity)));
      } catch (Exception var3) {
      }
   }

   public static void setupButtonDisableIcon(JButton b) {
      setupButtonDisableIcon(b, 0.25F);
   }

   public static JFileChooser generateJFileChooser() {
      return new JFileChooser() {
         {
            try {
               if (this.getUI() instanceof WebFileChooserUI) {
                  ((WebFileChooserUI)this.getUI()).getFileChooserPanel().getFileList().setThumbnailProvider(WebLafUtils.createFileThumbnailProvider());
               }
            } catch (Exception var2) {
            }
         }

         @Override
         public void addActionListener(ActionListener l) {
            for (ActionListener listener : super.getActionListeners()) {
               super.removeActionListener(listener);
            }

            super.addActionListener(l);
         }
      };
   }

   public static WebFileChooser generateWebFileChooser() {
      return new WebFileChooser() {
         {
            this.getFileChooserPanel().getFileList().setThumbnailProvider(WebLafUtils.createFileThumbnailProvider());
         }

         public void addActionListener(ActionListener l) {
            for (ActionListener listener : super.getActionListeners()) {
               super.removeActionListener(listener);
            }

            super.addActionListener(l);
         }
      };
   }

   public static Font deriveFont(Font font, int size, boolean bold, boolean italic) {
      int style;
      if (bold) {
         style = italic ? 3 : 1;
      } else if (italic) {
         style = 2;
      } else {
         style = 0;
      }

      return font.deriveFont(style, (float)size);
   }

   public static Font deriveFont(String fontName, int size, boolean bold, boolean italic) {
      int style;
      if (bold) {
         style = italic ? 3 : 1;
      } else if (italic) {
         style = 2;
      } else {
         style = 0;
      }

      return new Font(fontName, style, size);
   }

   public static void commitEdit(JSpinner c) {
      try {
         c.commitEdit();
      } catch (Exception var2) {
      }
   }

   public static void toEndPosition(JTextComponent input) {
      try {
         input.setCaretPosition(input.getDocument().getLength());
      } catch (Exception var2) {
      }
   }

   public static boolean scrollIsEnd(JScrollPane scroll, int margin) {
      try {
         JScrollBar jsb = scroll.getVerticalScrollBar();
         return jsb.getValue() + jsb.getModel().getExtent() + margin > jsb.getMaximum();
      } catch (Exception var3) {
         return false;
      }
   }

   public static boolean scrollIsEnd(JScrollPane scroll) {
      return scrollIsEnd(scroll, 20);
   }

   public static void scrollToEnd(JScrollPane scroll) {
      Utils.safeException(() -> {
         JScrollBar jsb = scroll.getVerticalScrollBar();
         jsb.setValue(jsb.getMaximum());
      });
   }

   public static void scrollToStart(JScrollPane scroll) {
      Utils.safeException(() -> {
         JScrollBar jsb = scroll.getVerticalScrollBar();
         jsb.setValue(jsb.getMinimum());
      });
   }

   public static void fade(JDialog d, int duration, Runnable callback, boolean in) {
      long fps = 50L;
      float[] array = new float[Math.max((int)((long)duration / 50L), 2)];
      array[0] = 1.0F;
      int lastIndex = array.length - 1;
      array[array.length - 1] = 0.0F;
      float ratio = 1.0F / (float)lastIndex;

      for (int i = 1; i < lastIndex; i++) {
         array[i] = Math.max(array[i - 1] - ratio, 0.0F);
      }

      if (!in) {
         Utils.startThreadDaemon(() -> {
            for (float f : array) {
               if (!(f > d.getOpacity())) {
                  SwingUtilities.invokeLater(() -> d.setOpacity(f));
                  Utils.sleep(f > 0.5F ? 60L : 41L);
               }
            }

            runOnUIThread(() -> Optional.ofNullable(callback).ifPresent(Runnable::run));
         });
      } else {
         Utils.startThreadDaemon(() -> {
            Arrays.sort(array);

            for (float f : array) {
               SwingUtilities.invokeLater(() -> d.setOpacity(f));
               Utils.sleep(50L);
            }

            runOnUIThread(() -> Optional.ofNullable(callback).ifPresent(Runnable::run));
         });
      }
   }

   public static void fadeIn(JDialog d, int duration, Runnable callback) {
      fade(d, duration, callback, true);
   }

   public static void fadeOut(JDialog d, int duration, Runnable callback) {
      fade(d, duration, callback, false);
   }

   static class BasicHTMLViewFactoryCustom extends HTMLFactory {
      @Override
      public View create(Element elem) {
         View view = null;
         AttributeSet attrs = elem.getAttributes();
         Object elementName = attrs.getAttribute("$ename");
         Object o = elementName != null ? null : attrs.getAttribute(StyleConstants.NameAttribute);
         if (o instanceof Tag) {
            Tag kind = (Tag)o;
            if (kind == Tag.IMG) {
               view = new SwingUtils.ImageViewCustom(elem);
            }
         }

         if (view == null) {
            view = super.create(elem);
         }

         if (view instanceof ImageView) {
            ((ImageView)view).setLoadsSynchronously(true);
         }

         return view;
      }
   }

   public static class ImageViewCustom extends View {
      private static boolean sIsInc = false;
      private static int sIncRate = 100;
      private static final String PENDING_IMAGE = "html.pendingImage";
      private static final String MISSING_IMAGE = "html.missingImage";
      private static final String IMAGE_CACHE_PROPERTY = "imageCache";
      private static final int DEFAULT_WIDTH = 38;
      private static final int DEFAULT_HEIGHT = 38;
      private static final int DEFAULT_BORDER = 2;
      private static final int LOADING_FLAG = 1;
      private static final int LINK_FLAG = 2;
      private static final int WIDTH_FLAG = 4;
      private static final int HEIGHT_FLAG = 8;
      private static final int RELOAD_FLAG = 16;
      private static final int RELOAD_IMAGE_FLAG = 32;
      private static final int SYNC_LOAD_FLAG = 64;
      private AttributeSet attr;
      private Image image;
      private Image disabledImage;
      private int width;
      private int height;
      private int state;
      private Container container;
      private Rectangle fBounds = new Rectangle();
      private Color borderColor;
      private short borderSize;
      private short leftInset;
      private short rightInset;
      private short topInset;
      private short bottomInset;
      private ImageObserver imageObserver = new SwingUtils.ImageViewCustom.ImageHandler();
      private View altView;
      private float vAlign;

      public ImageViewCustom(Element elem) {
         super(elem);
         this.state = 48;
      }

      public String getAltText() {
         return (String)this.getElement().getAttributes().getAttribute(Attribute.ALT);
      }

      public URL getImageURL() {
         String src = (String)this.getElement().getAttributes().getAttribute(Attribute.SRC);
         if (src == null) {
            return null;
         } else {
            URL reference = ((HTMLDocument)this.getDocument()).getBase();

            try {
               return new URL(reference, src);
            } catch (MalformedURLException var4) {
               return null;
            }
         }
      }

      public Icon getNoImageIcon() {
         return (Icon)UIManager.getLookAndFeelDefaults().get("html.missingImage");
      }

      public Icon getLoadingImageIcon() {
         return (Icon)UIManager.getLookAndFeelDefaults().get("html.pendingImage");
      }

      public Image getImage() {
         this.sync();
         return this.image;
      }

      private Image getImage(boolean enabled) {
         Image img = this.getImage();
         if (!enabled) {
            if (this.disabledImage == null) {
               this.disabledImage = GrayFilter.createDisabledImage(img);
            }

            img = this.disabledImage;
         }

         return img;
      }

      public void setLoadsSynchronously(boolean newValue) {
         synchronized (this) {
            if (newValue) {
               this.state |= 64;
            } else {
               this.state = (this.state | 64) ^ 64;
            }
         }
      }

      public boolean getLoadsSynchronously() {
         return (this.state & 64) != 0;
      }

      protected StyleSheet getStyleSheet() {
         HTMLDocument doc = (HTMLDocument)this.getDocument();
         return doc.getStyleSheet();
      }

      @Override
      public AttributeSet getAttributes() {
         this.sync();
         return this.attr;
      }

      @Override
      public String getToolTipText(float x, float y, Shape allocation) {
         return this.getAltText();
      }

      protected void setPropertiesFromAttributes() {
         StyleSheet sheet = this.getStyleSheet();
         this.attr = sheet.getViewAttributes(this);
         this.borderSize = (short)this.getIntAttr(Attribute.BORDER, this.isLink() ? 2 : 0);
         this.leftInset = this.rightInset = (short)(this.getIntAttr(Attribute.HSPACE, 0) + this.borderSize);
         this.topInset = this.bottomInset = (short)(this.getIntAttr(Attribute.VSPACE, 0) + this.borderSize);
         this.borderColor = ((StyledDocument)this.getDocument()).getForeground(this.getAttributes());
         AttributeSet attr = this.getElement().getAttributes();
         Object alignment = attr.getAttribute(Attribute.ALIGN);
         this.vAlign = 1.0F;
         if (alignment != null) {
            alignment = alignment.toString();
            if ("top".equals(alignment)) {
               this.vAlign = 0.0F;
            } else if ("middle".equals(alignment)) {
               this.vAlign = 0.5F;
            }
         }

         AttributeSet anchorAttr = (AttributeSet)attr.getAttribute(Tag.A);
         if (anchorAttr != null && anchorAttr.isDefined(Attribute.HREF)) {
            synchronized (this) {
               this.state |= 2;
            }
         } else {
            synchronized (this) {
               this.state = (this.state | 2) ^ 2;
            }
         }
      }

      @Override
      public void setParent(View parent) {
         View oldParent = this.getParent();
         super.setParent(parent);
         this.container = parent != null ? this.getContainer() : null;
         if (oldParent != parent) {
            synchronized (this) {
               this.state |= 16;
            }
         }
      }

      @Override
      public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
         super.changedUpdate(e, a, f);
         synchronized (this) {
            this.state |= 48;
         }

         this.preferenceChanged(null, true, true);
      }

      @Override
      public void paint(Graphics g, Shape a) {
         this.sync();
         Rectangle rect = a instanceof Rectangle ? (Rectangle)a : a.getBounds();
         Rectangle clip = g.getClipBounds();
         this.fBounds.setBounds(rect);
         this.paintHighlights(g, a);
         this.paintBorder(g, rect);
         if (clip != null) {
            g.clipRect(
               rect.x + this.leftInset, rect.y + this.topInset, rect.width - this.leftInset - this.rightInset, rect.height - this.topInset - this.bottomInset
            );
         }

         Container host = this.getContainer();
         Image img = this.getImage(host == null || host.isEnabled());
         if (img != null) {
            if (!this.hasPixels(img)) {
               Icon icon = this.getLoadingImageIcon();
               if (icon != null) {
                  icon.paintIcon(host, g, rect.x + this.leftInset, rect.y + this.topInset);
               }
            } else {
               g.drawImage(img, rect.x + this.leftInset, rect.y + this.topInset, this.width, this.height, this.imageObserver);
            }
         } else {
            Icon icon = this.getNoImageIcon();
            if (icon != null) {
               icon.paintIcon(host, g, rect.x + this.leftInset, rect.y + this.topInset);
            }

            View view = this.getAltView();
            if (view != null && ((this.state & 4) == 0 || this.width > 38)) {
               Rectangle altRect = new Rectangle(
                  rect.x + this.leftInset + 38,
                  rect.y + this.topInset,
                  rect.width - this.leftInset - this.rightInset - 38,
                  rect.height - this.topInset - this.bottomInset
               );
               view.paint(g, altRect);
            }
         }

         if (clip != null) {
            g.setClip(clip.x, clip.y, clip.width, clip.height);
         }
      }

      private void paintHighlights(Graphics g, Shape shape) {
         if (this.container instanceof JTextComponent) {
            JTextComponent tc = (JTextComponent)this.container;
            Highlighter h = tc.getHighlighter();
            if (h instanceof LayeredHighlighter) {
               ((LayeredHighlighter)h).paintLayeredHighlights(g, this.getStartOffset(), this.getEndOffset(), shape, tc, this);
            }
         }
      }

      private void paintBorder(Graphics g, Rectangle rect) {
         Color color = this.borderColor;
         if ((this.borderSize > 0 || this.image == null) && color != null) {
            int xOffset = this.leftInset - this.borderSize;
            int yOffset = this.topInset - this.borderSize;
            g.setColor(color);
            int n = this.image == null ? 1 : this.borderSize;

            for (int counter = 0; counter < n; counter++) {
               g.drawRect(
                  rect.x + xOffset + counter,
                  rect.y + yOffset + counter,
                  rect.width - counter - counter - xOffset - xOffset - 1,
                  rect.height - counter - counter - yOffset - yOffset - 1
               );
            }
         }
      }

      @Override
      public float getPreferredSpan(int axis) {
         this.sync();
         if (axis == 0 && (this.state & 4) == 4) {
            this.getPreferredSpanFromAltView(axis);
            return (float)(this.width + this.leftInset + this.rightInset);
         } else if (axis == 1 && (this.state & 8) == 8) {
            this.getPreferredSpanFromAltView(axis);
            return (float)(this.height + this.topInset + this.bottomInset);
         } else {
            Image image = this.getImage();
            if (image != null) {
               switch (axis) {
                  case 0:
                     return (float)(this.width + this.leftInset + this.rightInset);
                  case 1:
                     return (float)(this.height + this.topInset + this.bottomInset);
                  default:
                     throw new IllegalArgumentException("Invalid axis: " + axis);
               }
            } else {
               View view = this.getAltView();
               float retValue = 0.0F;
               if (view != null) {
                  retValue = view.getPreferredSpan(axis);
               }

               switch (axis) {
                  case 0:
                     return retValue + (float)(this.width + this.leftInset + this.rightInset);
                  case 1:
                     return retValue + (float)(this.height + this.topInset + this.bottomInset);
                  default:
                     throw new IllegalArgumentException("Invalid axis: " + axis);
               }
            }
         }
      }

      @Override
      public float getAlignment(int axis) {
         switch (axis) {
            case 1:
               return this.vAlign;
            default:
               return super.getAlignment(axis);
         }
      }

      @Override
      public Shape modelToView(int pos, Shape a, Bias b) throws BadLocationException {
         int p0 = this.getStartOffset();
         int p1 = this.getEndOffset();
         if (pos >= p0 && pos <= p1) {
            Rectangle r = a.getBounds();
            if (pos == p1) {
               r.x = r.x + r.width;
            }

            r.width = 0;
            return r;
         } else {
            return null;
         }
      }

      @Override
      public int viewToModel(float x, float y, Shape a, Bias[] bias) {
         Rectangle alloc = (Rectangle)a;
         if (x < (float)(alloc.x + alloc.width)) {
            bias[0] = Bias.Forward;
            return this.getStartOffset();
         } else {
            bias[0] = Bias.Backward;
            return this.getEndOffset();
         }
      }

      @Override
      public void setSize(float width, float height) {
         this.sync();
         if (this.getImage() == null) {
            View view = this.getAltView();
            if (view != null) {
               view.setSize(
                  Math.max(0.0F, width - (float)(38 + this.leftInset + this.rightInset)), Math.max(0.0F, height - (float)(this.topInset + this.bottomInset))
               );
            }
         }
      }

      private boolean isLink() {
         return (this.state & 2) == 2;
      }

      private boolean hasPixels(Image image) {
         return image != null && image.getHeight(this.imageObserver) > 0 && image.getWidth(this.imageObserver) > 0;
      }

      private float getPreferredSpanFromAltView(int axis) {
         if (this.getImage() == null) {
            View view = this.getAltView();
            if (view != null) {
               return view.getPreferredSpan(axis);
            }
         }

         return 0.0F;
      }

      private void repaint(long delay) {
         if (this.container != null && this.fBounds != null) {
            this.container.repaint(delay, this.fBounds.x, this.fBounds.y, this.fBounds.width, this.fBounds.height);
         }
      }

      private int getIntAttr(Attribute name, int deflt) {
         AttributeSet attr = this.getElement().getAttributes();
         if (attr.isDefined(name)) {
            String val = (String)attr.getAttribute(name);
            int i;
            if (val == null) {
               i = deflt;
            } else {
               try {
                  i = Math.max(0, Integer.parseInt(val));
               } catch (NumberFormatException var7) {
                  i = deflt;
               }
            }

            return i;
         } else {
            return deflt;
         }
      }

      private void sync() {
         int s = this.state;
         if ((s & 32) != 0) {
            this.refreshImage();
         }

         s = this.state;
         if ((s & 16) != 0) {
            synchronized (this) {
               this.state = (this.state | 16) ^ 16;
            }

            this.setPropertiesFromAttributes();
         }
      }

      private void refreshImage() {
         synchronized (this) {
            this.state = (this.state | 1 | 32 | 4 | 8) ^ 44;
            this.image = null;
            this.width = this.height = 0;
         }

         try {
            this.loadImage();
            this.updateImageSize();
         } finally {
            synchronized (this) {
               this.state = (this.state | 1) ^ 1;
            }
         }
      }

      private void loadImage() {
         URL src = this.getImageURL();
         Image newImage = null;
         if (src != null) {
            Dictionary cache = (Dictionary)this.getDocument().getProperty("imageCache");
            if (cache != null) {
               newImage = (Image)cache.get(src);
            } else {
               newImage = Toolkit.getDefaultToolkit().createImage(src);
               if (newImage != null && this.getLoadsSynchronously()) {
                  ImageIcon ii = new ImageIcon();
                  ii.setImage(newImage);
               }
            }
         } else {
            String b64 = this.getBASE64Image();
            BufferedImage newBufferedImage = null;

            try (ByteArrayInputStream bais = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(b64))) {
               newBufferedImage = ImageIO.read(bais);
            } catch (Exception var18) {
               System.out.println("[ERROR] in loadImage() \n\t" + var18);
            }

            newImage = this.resize(newBufferedImage);
         }

         this.image = newImage;
      }

      private BufferedImage resize(BufferedImage img) {
         try {
            AttributeSet atts = this.getElement().getAttributes();
            String _width = String.valueOf(atts.getAttribute(Attribute.WIDTH));
            String _height = String.valueOf(atts.getAttribute(Attribute.HEIGHT));
            boolean quality = !String.valueOf(atts.getAttribute("quality")).trim().equalsIgnoreCase("false");
            if (_width.equals("null") && _height.equals("null")) {
               return img;
            } else {
               Dimension d = new Dimension(img.getWidth(), img.getHeight());
               if (_width.endsWith("%")) {
                  int wScale = Math.max(Integer.parseInt(_width.replace("%", "").trim()), 1);
                  d.width = (int)((double)d.width * ((double)wScale / 100.0));
               }

               if (_height.endsWith("%")) {
                  int hScale = Math.max(Integer.parseInt(_height.replace("%", "").trim()), 1);
                  d.height = (int)((double)d.height * ((double)hScale / 100.0));
               }

               return Img.resize(img, d.width, d.height, quality);
            }
         } catch (Exception var8) {
            return img;
         }
      }

      private String getBASE64Image() {
         String src = (String)this.getElement().getAttributes().getAttribute(Attribute.SRC);
         return src == null ? null : src.replaceFirst("^.*;base64,", "");
      }

      private void updateImageSize() {
         int newWidth = 0;
         int newHeight = 0;
         int newState = 0;
         Image newImage = this.getImage();
         if (newImage != null) {
            Element elem = this.getElement();
            AttributeSet attr = elem.getAttributes();
            newWidth = this.getIntAttr(Attribute.WIDTH, -1);
            if (newWidth > 0) {
               newState |= 4;
            }

            newHeight = this.getIntAttr(Attribute.HEIGHT, -1);
            if (newHeight > 0) {
               newState |= 8;
            }

            if (newWidth <= 0) {
               newWidth = newImage.getWidth(this.imageObserver);
               if (newWidth <= 0) {
                  newWidth = 38;
               }
            }

            if (newHeight <= 0) {
               newHeight = newImage.getHeight(this.imageObserver);
               if (newHeight <= 0) {
                  newHeight = 38;
               }
            }

            if ((newState & 12) != 0) {
               Toolkit.getDefaultToolkit().prepareImage(newImage, newWidth, newHeight, this.imageObserver);
            } else {
               Toolkit.getDefaultToolkit().prepareImage(newImage, -1, -1, this.imageObserver);
            }

            boolean createText = false;
            synchronized (this) {
               if (this.image == null) {
                  createText = true;
                  if ((newState & 4) == 4) {
                     this.width = newWidth;
                  }

                  if ((newState & 8) == 8) {
                     this.height = newHeight;
                  }
               } else {
                  if ((newState & 4) == 4 || this.width == 0) {
                     this.width = newWidth;
                  }

                  if ((newState & 8) == 8 || this.height == 0) {
                     this.height = newHeight;
                  }
               }

               this.state |= newState;
               this.state = (this.state | 1) ^ 1;
            }

            if (createText) {
               this.updateAltTextView();
            }
         } else {
            this.width = this.height = 38;
            this.updateAltTextView();
         }
      }

      private void updateAltTextView() {
         String text = this.getAltText();
         if (text != null) {
            SwingUtils.ImageViewCustom.ImageLabelView newView = new SwingUtils.ImageViewCustom.ImageLabelView(this.getElement(), text);
            synchronized (this) {
               this.altView = newView;
            }
         }
      }

      private View getAltView() {
         View view;
         synchronized (this) {
            view = this.altView;
         }

         if (view != null && view.getParent() == null) {
            view.setParent(this.getParent());
         }

         return view;
      }

      private void safePreferenceChanged() {
         if (SwingUtilities.isEventDispatchThread()) {
            Document doc = this.getDocument();
            if (doc instanceof AbstractDocument) {
               ((AbstractDocument)doc).readLock();
            }

            this.preferenceChanged(null, true, true);
            if (doc instanceof AbstractDocument) {
               ((AbstractDocument)doc).readUnlock();
            }
         } else {
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  ImageViewCustom.this.safePreferenceChanged();
               }
            });
         }
      }

      private class ImageHandler implements ImageObserver {
         private ImageHandler() {
         }

         @Override
         public boolean imageUpdate(Image img, int flags, int x, int y, int newWidth, int newHeight) {
            if ((img == ImageViewCustom.this.image || img == ImageViewCustom.this.disabledImage)
               && ImageViewCustom.this.image != null
               && ImageViewCustom.this.getParent() != null) {
               if ((flags & 192) != 0) {
                  ImageViewCustom.this.repaint(0L);
                  synchronized (ImageViewCustom.this) {
                     if (ImageViewCustom.this.image == img) {
                        ImageViewCustom.this.image = null;
                        if ((ImageViewCustom.this.state & 4) != 4) {
                           ImageViewCustom.this.width = 38;
                        }

                        if ((ImageViewCustom.this.state & 8) != 8) {
                           ImageViewCustom.this.height = 38;
                        }
                     } else {
                        ImageViewCustom.this.disabledImage = null;
                     }

                     if ((ImageViewCustom.this.state & 1) == 1) {
                        return false;
                     }
                  }

                  ImageViewCustom.this.updateAltTextView();
                  ImageViewCustom.this.safePreferenceChanged();
                  return false;
               } else {
                  if (ImageViewCustom.this.image == img) {
                     short changed = 0;
                     if ((flags & 2) != 0 && !ImageViewCustom.this.getElement().getAttributes().isDefined(Attribute.HEIGHT)) {
                        changed = (short)(changed | 1);
                     }

                     if ((flags & 1) != 0 && !ImageViewCustom.this.getElement().getAttributes().isDefined(Attribute.WIDTH)) {
                        changed = (short)(changed | 2);
                     }

                     synchronized (ImageViewCustom.this) {
                        if ((changed & 1) == 1 && (ImageViewCustom.this.state & 4) == 0) {
                           ImageViewCustom.this.width = newWidth;
                        }

                        if ((changed & 2) == 2 && (ImageViewCustom.this.state & 8) == 0) {
                           ImageViewCustom.this.height = newHeight;
                        }

                        if ((ImageViewCustom.this.state & 1) == 1) {
                           return true;
                        }
                     }

                     if (changed != 0) {
                        ImageViewCustom.this.safePreferenceChanged();
                        return true;
                     }
                  }

                  if ((flags & 48) != 0) {
                     ImageViewCustom.this.repaint(0L);
                  } else if ((flags & 8) != 0 && SwingUtils.ImageViewCustom.sIsInc) {
                     ImageViewCustom.this.repaint((long)SwingUtils.ImageViewCustom.sIncRate);
                  }

                  return (flags & 32) == 0;
               }
            } else {
               return false;
            }
         }
      }

      private class ImageLabelView extends InlineView {
         private Segment segment;
         private Color fg;

         ImageLabelView(Element e, String text) {
            super(e);
            this.reset(text);
         }

         public void reset(String text) {
            this.segment = new Segment(text.toCharArray(), 0, text.length());
         }

         @Override
         public void paint(Graphics g, Shape a) {
            GlyphPainter painter = this.getGlyphPainter();
            if (painter != null) {
               g.setColor(this.getForeground());
               painter.paint(this, g, a, this.getStartOffset(), this.getEndOffset());
            }
         }

         @Override
         public Segment getText(int p0, int p1) {
            if (p0 >= 0 && p1 <= this.segment.array.length) {
               this.segment.offset = p0;
               this.segment.count = p1 - p0;
               return this.segment;
            } else {
               throw new RuntimeException("ImageLabelView: Stale view");
            }
         }

         @Override
         public int getStartOffset() {
            return 0;
         }

         @Override
         public int getEndOffset() {
            return this.segment.array.length;
         }

         @Override
         public View breakView(int axis, int p0, float pos, float len) {
            return this;
         }

         @Override
         public Color getForeground() {
            View parent;
            if (this.fg == null && (parent = this.getParent()) != null) {
               Document doc = this.getDocument();
               AttributeSet attr = parent.getAttributes();
               if (attr != null && doc instanceof StyledDocument) {
                  this.fg = ((StyledDocument)doc).getForeground(attr);
               }
            }

            return this.fg;
         }
      }
   }
}
