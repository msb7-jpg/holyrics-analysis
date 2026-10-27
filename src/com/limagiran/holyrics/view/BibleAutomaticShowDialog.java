package com.limagiran.holyrics.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.list.WebListCellRenderer;
import com.alee.laf.list.WebListModel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.text.WebTextField;
import com.limagiran.bible.Bible;
import com.limagiran.bible.Book;
import com.limagiran.bible.Verse;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.AbstractWebListCellRendererWithSeparator;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.swing.ListDragAndDropVerse;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class BibleAutomaticShowDialog extends JDialog implements Values {
   private static BibleAutomaticShowDialog INSTANCE;
   private static final File FILE_LIST = new File(Values.PATH_FILES + "bibleShowList.hly").getAbsoluteFile();
   private final WebListModel<Verse> modelSelectedVerses = new WebListModel();
   private final Bible bible;
   private Filler filler1;
   private Filler filler2;
   private Filler filler3;
   private Filler filler4;
   private JButton jButtonAdd;
   private JButton jButtonRemove;
   private JButton jButtonShow;
   private JCheckBox jCheckBoxAutomaticSlideShow;
   private JCheckBox jCheckBoxRepeat;
   private JComboBox<Theme> jComboBoxThemes;
   private JLabel jLabelSeconds;
   private JLabel jLabelTitle;
   private JList<Book> jListBooks;
   private JList<Integer> jListChapters;
   private JList<Verse> jListSelectedVerses;
   private JList<Verse> jListVerses;
   private JPanel jPanelButtons;
   private JPanel jPanelCenter;
   private JPanel jPanelFooter;
   private JPanel jPanelGeneral;
   private JPanel jPanelSettings;
   private JPanel jPanelSettingsLeft;
   private JPanel jPanelVerses;
   private JScrollPane jScrollPaneBooks;
   private JScrollPane jScrollPaneChapters;
   private JScrollPane jScrollPaneSelectedVerses;
   private JScrollPane jScrollPaneVerses;
   private JSeparator jSeparator;
   private JSeparator jSeparator1;
   private JSpinner jSpinnerSeconds;

   private BibleAutomaticShowDialog(Bible bible) {
      super((Frame)null, true);
      this.bible = bible;
      this.initComponents();
      this.init();
   }

   private void init() {
      ((WebSpinner)this.jSpinnerSeconds).setDrawFocus(false);
      this.load(RWObj.read(FILE_LIST, List.class, "#hly$k3y#"));
      this.updateBook("");
      this.setupJListBook();
      this.setupJListChapter();
      this.setupJListSelectedVerses();
      this.jCheckBoxAutomaticSlideShow.setSelected(Settings.EnumSettings.BIBLE_AUTOMATIC_SLIDE_SHOW.is());
      this.jCheckBoxRepeat.setSelected(Settings.EnumSettings.BIBLE_AUTOMATIC_SLIDE_SHOW_REPEAT.is());
      this.jSpinnerSeconds.setValue(Utils.range(Settings.EnumSettings.BIBLE_AUTOMATIC_SLIDE_SHOW_SECONDS.getInt(), 2, 30));
      this.setEnabledSubItemsAutomaticSlideShow(Settings.EnumSettings.BIBLE_AUTOMATIC_SLIDE_SHOW.is());
      this.jCheckBoxAutomaticSlideShow
         .addItemListener(e -> this.setEnabledSubItemsAutomaticSlideShow(Settings.EnumSettings.BIBLE_AUTOMATIC_SLIDE_SHOW.set(e.getStateChange() == 1)));
      this.jCheckBoxRepeat.addItemListener(e -> {
         Boolean var10000 = Settings.EnumSettings.BIBLE_AUTOMATIC_SLIDE_SHOW_REPEAT.set(e.getStateChange() == 1);
      });
      this.jSpinnerSeconds.addChangeListener(e -> {
         Integer var10000 = Settings.EnumSettings.BIBLE_AUTOMATIC_SLIDE_SHOW_SECONDS.set(Integer.parseInt(this.jSpinnerSeconds.getValue().toString()));
      });
      this.jButtonShow.setFocusable(true);
      this.jButtonShow.addActionListener(e -> this.showVerses(this.modelSelectedVerses.getElements()));
      this.jButtonAdd.addActionListener(e -> this.add());
      this.jButtonRemove.addActionListener(e -> this.remove());
      Arrays.asList(this.jScrollPaneBooks, this.jScrollPaneChapters, this.jScrollPaneVerses)
         .forEach(s -> s.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)));
      this.jScrollPaneChapters.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
      this.jComboBoxThemes.setSelectedItem(Settings.SET.getBibleAutomaticShowTheme());
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
      this.jListSelectedVerses
         .getModel()
         .addListDataListener(
            new ListDataListener() {
               @Override
               public void intervalAdded(ListDataEvent e) {
                  this.save();
               }

               @Override
               public void intervalRemoved(ListDataEvent e) {
                  this.save();
               }

               @Override
               public void contentsChanged(ListDataEvent e) {
                  this.save();
               }

               private void save() {
                  List<int[]> list = BibleAutomaticShowDialog.this.modelSelectedVerses
                     .getElements()
                     .stream()
                     .map(v -> new int[]{v.book, v.chapter, v.verse})
                     .collect(Collectors.toList());
                  RWObj.write(list, BibleAutomaticShowDialog.FILE_LIST, "#hly$k3y#");
               }
            }
         );
      JLabel label = new JLabel(I18N.WORD_SHOW.val());
      label.setFocusable(false);
      label.setBorder(new EmptyBorder(2, 0, 2, 0));
      label.setFont(this.jListBooks.getFont().deriveFont(16.0F));
      label.setHorizontalAlignment(0);
      WebButton open = new WebButton(new ImageIcon(Images.load("open16.png")), evt -> this.open());
      open.setFocusable(false);
      open.setUndecorated(true);
      WebButton save = new WebButton(new ImageIcon(Images.load("saveBlack16.png")), evt -> this.save());
      save.setFocusable(false);
      save.setUndecorated(true);
      WebPanel header = new WebPanel(new FlowLayout(1, 8, 1), new Component[]{label, open, save});
      header.setOpaque(false);
      this.jScrollPaneSelectedVerses.setColumnHeaderView(header);
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelCenter = new JPanel();
      this.jLabelTitle = new JLabel();
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
      this.jPanelSettingsLeft = new JPanel();
      this.jComboBoxThemes = new JComboBox<>();
      this.jCheckBoxAutomaticSlideShow = new JCheckBox();
      this.jCheckBoxRepeat = new JCheckBox();
      this.jSpinnerSeconds = new WebSpinner();
      this.jLabelSeconds = new JLabel();
      this.jSeparator1 = new JSeparator();
      this.jButtonShow = new JButton();
      this.setDefaultCloseOperation(2);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.bible"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(750, 600));
      this.setResizable(false);
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeneral.setLayout(new BorderLayout(5, 0));
      this.jPanelCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelCenter.setLayout(new BorderLayout(0, 5));
      this.jLabelTitle.setFont(new Font("Georgia", 0, 18));
      this.jLabelTitle.setHorizontalAlignment(0);
      this.jLabelTitle.setText(bundle.getString("msg.customAndAutomaticPresentation"));
      this.jPanelCenter.add(this.jLabelTitle, "First");
      this.jPanelVerses.setLayout(new BoxLayout(this.jPanelVerses, 2));
      this.jScrollPaneBooks.setFocusable(false);
      this.jListBooks.setFont(new Font("Georgia", 0, 14));
      this.jListBooks.setSelectionMode(0);
      this.jListBooks.setFocusable(false);
      this.jListBooks.setVisibleRowCount(16);
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
            BibleAutomaticShowDialog.this.jListVersesMouseReleased(evt);
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
      this.jListSelectedVerses.setVisibleRowCount(16);
      this.jListSelectedVerses.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BibleAutomaticShowDialog.this.jListSelectedVersesMouseReleased(evt);
         }
      });
      this.jListSelectedVerses.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent evt) {
            BibleAutomaticShowDialog.this.jListSelectedVersesKeyPressed(evt);
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
      this.jPanelSettings.setLayout(new GridBagLayout());
      this.jPanelSettingsLeft.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.jPanelSettingsLeft.setLayout(new GridBagLayout());
      this.jComboBoxThemes.setMaximumRowCount(5);
      this.jComboBoxThemes.setModel(new DefaultComboBoxModel<>(ThemeController.getThemes().toArray(new Theme[0])));
      this.jComboBoxThemes.setFocusable(false);
      this.jComboBoxThemes.setPreferredSize(new Dimension(100, 51));
      this.jComboBoxThemes.setRenderer(new ComboBoxRendererTheme(80, 45));
      this.jComboBoxThemes.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            BibleAutomaticShowDialog.this.jComboBoxThemesItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.insets = new Insets(0, 0, 0, 10);
      this.jPanelSettingsLeft.add(this.jComboBoxThemes, gridBagConstraints);
      this.jCheckBoxAutomaticSlideShow.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxAutomaticSlideShow.setText(bundle.getString("msg.automaticSlideShow"));
      this.jCheckBoxAutomaticSlideShow.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.anchor = 17;
      this.jPanelSettingsLeft.add(this.jCheckBoxAutomaticSlideShow, gridBagConstraints);
      this.jCheckBoxRepeat.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxRepeat.setText(bundle.getString("word.repeat"));
      this.jCheckBoxRepeat.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.jPanelSettingsLeft.add(this.jCheckBoxRepeat, gridBagConstraints);
      this.jSpinnerSeconds.setFont(new Font("Georgia", 0, 14));
      this.jSpinnerSeconds.setModel(new SpinnerNumberModel(2, 2, 60, 1));
      this.jSpinnerSeconds.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.jPanelSettingsLeft.add(this.jSpinnerSeconds, gridBagConstraints);
      this.jLabelSeconds.setFont(new Font("Georgia", 0, 14));
      this.jLabelSeconds.setText(bundle.getString("word.seconds"));
      this.jLabelSeconds.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 5, 0, 0);
      this.jPanelSettingsLeft.add(this.jLabelSeconds, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.jPanelSettingsLeft.add(this.jSeparator1, gridBagConstraints);
      this.jButtonShow.setFont(new Font("Georgia", 0, 14));
      this.jButtonShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack24.png")));
      this.jButtonShow.setText(bundle.getString("word.show"));
      this.jButtonShow.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 0;
      this.jPanelSettingsLeft.add(this.jButtonShow, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      this.jPanelSettings.add(this.jPanelSettingsLeft, gridBagConstraints);
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

   private void jComboBoxThemesItemStateChanged(ItemEvent evt) {
      this.setTheme(this.jComboBoxThemes.getSelectedItem());
   }

   private void jListSelectedVersesKeyPressed(KeyEvent evt) {
      if (evt.getKeyCode() == 127) {
         this.remove();
      }
   }

   public static void main(Bible bible) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
         }

         INSTANCE = new BibleAutomaticShowDialog(bible);
         INSTANCE.setVisible(true);
      });
   }

   private void setEnabledSubItemsAutomaticSlideShow(boolean flag) {
      Arrays.asList(this.jCheckBoxRepeat, this.jSpinnerSeconds, this.jLabelSeconds).forEach(c -> c.setEnabled(flag));
   }

   private void showVerses(List<Verse> list) {
      if (list.isEmpty()) {
         Utils.error(this, I18N.MSG_ERROR_EMPTY_LIST.val());
      } else if (list.size() > 100) {
         Utils.error(this, I18N.MSG_MAXIMUM_VERSES_TO_SHOW.format(100));
      } else {
         this.dispose();
         if (this.jCheckBoxAutomaticSlideShow.isSelected()) {
            AutomaticShowView.show(
               new ArrayList<>(list),
               Settings.SET.getBibleAutomaticShowTheme(),
               Integer.parseInt(this.jSpinnerSeconds.getValue().toString()),
               this.jCheckBoxRepeat.isSelected(),
               ScreenDeviceSettings.EnumDeviceType.BIBLE
            );
         } else {
            BiblePresentationView.show(list, list.get(0), Settings.SET.getBibleTheme());
         }
      }
   }

   @Override
   public void dispose() {
      try {
         this.jSpinnerSeconds.commitEdit();
      } catch (Exception var2) {
      }

      super.dispose();
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
      int limit = 100 - this.modelSelectedVerses.size();
      if (this.modelSelectedVerses.size() < 100 && add.size() <= limit) {
         int index = this.jListVerses.getSelectedIndex();
         this.modelSelectedVerses.addElements(add);
         this.updateVerse();
         this.jListVerses.setSelectedIndex(Math.min(index, this.jListVerses.getModel().getSize() - 1));
      } else {
         Utils.error(this, I18N.MSG_MAXIMUM_VERSES_TO_SHOW.format(100));
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

   private void setTheme(Object item) {
      BooleanOptional.runIfTrue(item instanceof Theme, () -> {
         Long var10000 = Settings.EnumSettings.BIBLE_AUTOMATIC_SHOW_THEME.set(((Theme)item).id);
      });
   }

   private void save() {
      File f = Dir.windowsSave(I18N.WORD_VERSES.val(), "Verse Playlist File (*.vpl)", ".vpl");
      if (f != null) {
         List<int[]> list = this.modelSelectedVerses.getElements().stream().map(v -> new int[]{v.book, v.chapter, v.verse}).collect(Collectors.toList());
         RWObj.write(list, f);
         Utils.msg(this, I18N.MSG_SAVED_SUCCESSFULLY.val());
      }
   }

   private void open() {
      File f = Dir.windowsFile(I18N.WORD_VERSES.val(), "Verse Playlist File (*.vpl)", ".vpl");
      if (f != null) {
         this.load(RWObj.read(f, List.class));
         this.updateVerse();
      }
   }

   private void load(List<int[]> list) {
      try {
         this.modelSelectedVerses.removeAllElements();
         if (list != null) {
            this.modelSelectedVerses.addElements(list.stream().map(v -> this.bible.getVerse(v[0], v[1], v[2], true)).distinct().collect(Collectors.toList()));
         }
      } catch (Exception var3) {
      }
   }
}
