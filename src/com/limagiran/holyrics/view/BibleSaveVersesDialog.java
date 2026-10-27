package com.limagiran.holyrics.view;

import com.alee.laf.list.WebListCellRenderer;
import com.alee.laf.list.WebListModel;
import com.alee.laf.text.WebTextField;
import com.limagiran.bible.Bible;
import com.limagiran.bible.Book;
import com.limagiran.bible.Verse;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.AbstractWebListCellRendererWithSeparator;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.swing.ListDragAndDropVerse;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SaveSlides;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.panel.SaveAsPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class BibleSaveVersesDialog extends JDialog implements SaveAsPanel.SaveAsPanelListener {
   private final WebListModel<Verse> modelSelectedVerses = new WebListModel();
   private final SaveAsPanel saveAsPanel = new SaveAsPanel(Settings.SET.getBibleAutomaticShowTheme(), this);
   private final Bible bible;
   private Filler filler1;
   private Filler filler2;
   private Filler filler3;
   private Filler filler4;
   private JButton jButtonAdd;
   private JButton jButtonRemove;
   private JList<Book> jListBooks;
   private JList<Integer> jListChapters;
   private JList<Verse> jListSelectedVerses;
   private JList<Verse> jListVerses;
   private JPanel jPanelButtons;
   private JPanel jPanelCenter;
   private JPanel jPanelFooter;
   private JPanel jPanelGeneral;
   private JPanel jPanelSettings;
   private JPanel jPanelVerses;
   private JScrollPane jScrollPaneBooks;
   private JScrollPane jScrollPaneChapters;
   private JScrollPane jScrollPaneSelectedVerses;
   private JScrollPane jScrollPaneVerses;
   private JSeparator jSeparator;

   public BibleSaveVersesDialog(BibleMain parent) {
      super(parent, true);
      this.bible = parent.getCurrentBible();
      this.initComponents();
      this.init();
   }

   private void init() {
      this.updateBook("");
      this.setupJListBook();
      this.setupJListChapter();
      this.setupJListSelectedVerses();
      this.jButtonAdd.addActionListener(e -> this.add());
      this.jButtonRemove.addActionListener(e -> this.remove());
      Arrays.asList(this.jScrollPaneBooks, this.jScrollPaneChapters, this.jScrollPaneVerses)
         .forEach(s -> s.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)));
      this.jScrollPaneChapters.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
      this.jPanelSettings.add(this.saveAsPanel);
      InsertShortcut.addEscToDispose(this);
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void setupJListBook() {
      this.jListBooks.addListSelectionListener(e -> BooleanOptional.runIfTrue(!e.getValueIsAdjusting(), this::updateChapter));
      JViewport header = new JViewport();
      WebTextField bookFilter = new WebTextField();
      bookFilter.getDocument()
         .addDocumentListener(new DocumentListenerAuto(300L, () -> this.updateBook(Utils.removeAccent(bookFilter.getText().toLowerCase()))));
      bookFilter.setDrawFocus(false);
      bookFilter.setInputPrompt(I18N.WORD_FILTER.val());
      bookFilter.setHideInputPromptOnFocus(false);
      bookFilter.setFont(this.jListBooks.getFont());
      header.setView(bookFilter);
      this.jScrollPaneBooks.setColumnHeader(header);
   }

   private void setupJListChapter() {
      class WebListCellRendererCustom extends AbstractWebListCellRendererWithSeparator implements ListCellRenderer<Integer> {
         public WebListCellRendererCustom() {
            super(false);
            this.view.setHorizontalAlignment(0);
            this.content.setBorder(new CompoundBorder(new EmptyBorder(3, 8, 3, 8), this.content.getBorder()));
         }

         public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, String.valueOf(value), index, isSelected, cellHasFocus);
         }
      }

      this.jListChapters.addListSelectionListener(e -> BooleanOptional.runIfTrue(!e.getValueIsAdjusting(), this::updateVerse));
      this.jListChapters.setCellRenderer(new WebListCellRendererCustom());
   }

   private void setupJListSelectedVerses() {
      JLabel title = new JLabel(I18N.WORD_SAVE.val());
      title.setFocusable(false);
      title.setBorder(new EmptyBorder(2, 0, 2, 0));
      title.setFont(this.jListBooks.getFont().deriveFont(16.0F));
      title.setHorizontalAlignment(0);
      this.jScrollPaneSelectedVerses.setColumnHeaderView(title);
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelCenter = new JPanel();
      this.jPanelVerses = new JPanel();
      this.jScrollPaneBooks = new JScrollPane();
      this.jListBooks = new JList<>();
      this.filler1 = new Filler(new Dimension(5, 0), new Dimension(5, 0), new Dimension(5, 32767));
      this.jScrollPaneChapters = new JScrollPane();
      this.jListChapters = new JList<>();
      this.filler2 = new Filler(new Dimension(5, 0), new Dimension(5, 0), new Dimension(5, 32767));
      this.jScrollPaneVerses = new JScrollPane();
      this.jListVerses = new JList<>();
      this.filler3 = new Filler(new Dimension(5, 0), new Dimension(5, 0), new Dimension(5, 32767));
      this.jPanelButtons = new JPanel();
      this.jButtonAdd = new JButton();
      this.jButtonRemove = new JButton();
      this.filler4 = new Filler(new Dimension(5, 0), new Dimension(5, 0), new Dimension(5, 32767));
      this.jScrollPaneSelectedVerses = new JScrollPane();
      this.jListSelectedVerses = new ListDragAndDropVerse(this.modelSelectedVerses, new WebListCellRenderer());
      this.jPanelFooter = new JPanel();
      this.jSeparator = new JSeparator();
      this.jPanelSettings = new JPanel();
      this.setDefaultCloseOperation(2);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("msg.saveAs___"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(350, 400));
      this.setResizable(false);
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeneral.setLayout(new BorderLayout(5, 0));
      this.jPanelCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelCenter.setLayout(new BorderLayout(0, 5));
      this.jPanelVerses.setLayout(new BoxLayout(this.jPanelVerses, 2));
      this.jScrollPaneBooks.setFocusable(false);
      this.jListBooks.setFont(new Font("Georgia", 0, 14));
      this.jListBooks.setSelectionMode(0);
      this.jListBooks.setFocusable(false);
      this.jListBooks.setVisibleRowCount(18);
      this.jScrollPaneBooks.setViewportView(this.jListBooks);
      this.jPanelVerses.add(this.jScrollPaneBooks);
      this.jPanelVerses.add(this.filler1);
      this.jScrollPaneChapters.setFocusable(false);
      this.jListChapters.setFont(new Font("Georgia", 0, 18));
      this.jListChapters.setSelectionMode(0);
      this.jListChapters.setVisibleRowCount(-1);
      this.jScrollPaneChapters.setViewportView(this.jListChapters);
      this.jPanelVerses.add(this.jScrollPaneChapters);
      this.jPanelVerses.add(this.filler2);
      this.jScrollPaneVerses.setHorizontalScrollBarPolicy(31);
      this.jScrollPaneVerses.setVerticalScrollBarPolicy(22);
      this.jScrollPaneVerses.setFocusable(false);
      this.jListVerses.setFont(new Font("Georgia", 0, 14));
      this.jListVerses.setFixedCellWidth(200);
      this.jListVerses.setVisibleRowCount(16);
      this.jListVerses.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BibleSaveVersesDialog.this.jListVersesMouseReleased(evt);
         }
      });
      this.jScrollPaneVerses.setViewportView(this.jListVerses);
      this.jPanelVerses.add(this.jScrollPaneVerses);
      this.jPanelVerses.add(this.filler3);
      this.jPanelButtons.setLayout(new GridBagLayout());
      this.jButtonAdd.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/next24.png")));
      this.jButtonAdd.setFocusable(false);
      this.jButtonAdd.setMargin(new Insets(2, 2, 2, 2));
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 0, 10, 0);
      this.jPanelButtons.add(this.jButtonAdd, gridBagConstraints);
      this.jButtonRemove.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/previous24.png")));
      this.jButtonRemove.setFocusable(false);
      this.jButtonRemove.setMargin(new Insets(2, 2, 2, 2));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.insets = new Insets(10, 0, 0, 0);
      this.jPanelButtons.add(this.jButtonRemove, gridBagConstraints);
      this.jPanelVerses.add(this.jPanelButtons);
      this.jPanelVerses.add(this.filler4);
      this.jScrollPaneSelectedVerses.setHorizontalScrollBarPolicy(31);
      this.jScrollPaneSelectedVerses.setVerticalScrollBarPolicy(22);
      this.jListSelectedVerses.setFont(new Font("Georgia", 0, 12));
      this.jListSelectedVerses.setSelectionMode(1);
      this.jListSelectedVerses.setDragEnabled(true);
      this.jListSelectedVerses.setDropMode(DropMode.INSERT);
      this.jListSelectedVerses.setFixedCellWidth(300);
      this.jListSelectedVerses.setVisibleRowCount(20);
      this.jListSelectedVerses.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BibleSaveVersesDialog.this.jListSelectedVersesMouseReleased(evt);
         }
      });
      this.jScrollPaneSelectedVerses.setViewportView(this.jListSelectedVerses);
      this.jPanelVerses.add(this.jScrollPaneSelectedVerses);
      this.jPanelCenter.add(this.jPanelVerses, "Center");
      this.jPanelGeneral.add(this.jPanelCenter, "Center");
      this.jPanelFooter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelFooter.setLayout(new BorderLayout(5, 0));
      this.jPanelFooter.add(this.jSeparator, "First");
      this.jPanelSettings.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelSettings.setLayout(new BorderLayout());
      this.jPanelFooter.add(this.jPanelSettings, "Last");
      this.jPanelGeneral.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jListSelectedVersesMouseReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt) && SwingUtils.pointOnTheSelectedItem(this.jListSelectedVerses, evt.getPoint())) {
         this.remove();
         Utils.clearDoubleClick();
      }
   }

   private void jListVersesMouseReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt) && SwingUtils.pointOnTheSelectedItem(this.jListVerses, evt.getPoint())) {
         this.add();
         Utils.clearDoubleClick();
      }
   }

   public static void main(BibleMain parent) {
      SwingUtilities.invokeLater(() -> new BibleSaveVersesDialog(parent).setVisible(true));
   }

   private void updateChapter() {
      WebListModel<Integer> model = new WebListModel();
      Optional.ofNullable(this.jListBooks.getSelectedValue()).ifPresent(b -> IntStream.rangeClosed(1, b.getAmountChapter()).forEach(model::addElement));
      this.jListChapters.setModel(model);
      this.jListChapters.setSelectedIndex(0);
      this.jPanelVerses.revalidate();
      this.jPanelVerses.repaint();
      this.updateVerse();
   }

   private void updateVerse() {
      WebListModel<Verse> model = new WebListModel();
      Optional.ofNullable(this.jListBooks.getSelectedValue())
         .ifPresent(
            book -> Optional.ofNullable(this.jListChapters.getSelectedValue())
                  .ifPresent(c -> book.getVerses(c).stream().filter(v -> !this.modelSelectedVerses.contains(v)).forEach(model::addElement))
         );
      this.jListVerses.setModel(model);
      this.jListVerses.setSelectedIndex(0);
      this.jPanelVerses.revalidate();
      this.jPanelVerses.repaint();
   }

   private void updateBook(String search) {
      WebListModel<Book> modelBook = new WebListModel();
      this.bible.books.stream().filter(b -> Utils.removeAccent(b.name).toLowerCase().contains(search)).forEach(modelBook::addElement);
      this.jListBooks.setModel(modelBook);
      this.jListBooks.setSelectedIndex(0);
      this.jPanelVerses.revalidate();
      this.jPanelVerses.repaint();
      this.updateChapter();
   }

   private void add() {
      List<Verse> add = this.jListVerses.getSelectedValuesList();
      int limit = 20 - this.modelSelectedVerses.size();
      if (this.modelSelectedVerses.size() < 20 && add.size() <= limit) {
         int index = this.jListVerses.getSelectedIndex();
         this.modelSelectedVerses.addElements(add);
         this.updateVerse();
         this.jListVerses.setSelectedIndex(Math.min(index, this.jListVerses.getModel().getSize() - 1));
      } else {
         Utils.error(this, I18N.MSG_MAXIMUM_VERSES_TO_SAVE.format(20));
      }
   }

   private void remove() {
      int index = this.jListSelectedVerses.getSelectedIndex();
      Verse v1 = this.jListVerses.getSelectedValue();
      List<Verse> remove = this.jListSelectedVerses.getSelectedValuesList();
      this.modelSelectedVerses.removeElements(remove);
      this.updateVerse();
      this.jListSelectedVerses.revalidate();
      this.jListSelectedVerses.repaint();
      this.jListVerses.setSelectedValue(v1, true);
      this.jListSelectedVerses.setSelectedIndex(Math.min(index, this.jListSelectedVerses.getModel().getSize() - 1));
   }

   @Override
   public void buttonSaveClicked(File file) {
      this.saveAsPanel
         .getSizes()
         .forEach(s -> SaveSlides.save(this, file, this.modelSelectedVerses.getElements(), this.saveAsPanel.getTheme(), s, this.saveAsPanel.getType()));
      this.dispose();
   }

   @Override
   public boolean validateSave() {
      List<Verse> list = this.modelSelectedVerses.getElements();
      if (list.isEmpty()) {
         Utils.error(this, I18N.MSG_ERROR_EMPTY_LIST.val());
         return false;
      } else if (list.size() > 20) {
         Utils.error(this, I18N.MSG_MAXIMUM_VERSES_TO_SAVE.format(20));
         return false;
      } else {
         return true;
      }
   }
}
