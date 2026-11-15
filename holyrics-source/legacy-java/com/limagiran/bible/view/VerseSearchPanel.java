package com.limagiran.bible.view;

import com.limagiran.bible.Bible;
import com.limagiran.bible.Book;
import com.limagiran.bible.Verse;
import com.limagiran.bible.i18n.I18N;
import com.limagiran.bible.util.MySwingWorker;
import com.limagiran.bible.util.PopUpWorker;
import com.limagiran.bible.util.SwingUtils;
import com.limagiran.bible.util.VerseFilter;
import com.limagiran.bible.util.WrapLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

public final class VerseSearchPanel extends JPanel implements TabResult.TabResultListener {
   private final VerseFilter vf = new VerseFilter();
   private final Window owner;
   private final VerseSearchPanel.VerseSearchPanelListener listener;
   private final List<Bible> bibles = new ArrayList<>();
   private TabResult tab;
   private ButtonGroup buttonGroupAllOldNewBook;
   private ButtonGroup buttonGroupAllOrAnyWord;
   private JButton jButtonFind;
   private JCheckBox jCheckBoxExactPhrase;
   private JCheckBox jCheckBoxWholeWord;
   private JComboBox<Book> jComboBoxBooks;
   private JComboBox<Integer> jComboBoxMaxList;
   private JLabel jLabelFooter;
   private JLabel jLabelList;
   private JPanel jPanelContent;
   private JPanel jPanelFooter;
   private JPanel jPanelFooterRight;
   private JPanel jPanelGeneral;
   private JPanel jPanelParam1;
   private JPanel jPanelParam2;
   private JPanel jPanelParam3;
   private JRadioButton jRadioButtonAllBible;
   private JRadioButton jRadioButtonAllWords;
   private JRadioButton jRadioButtonAnyWord;
   private JRadioButton jRadioButtonBook;
   private JRadioButton jRadioButtonNewTestament;
   private JRadioButton jRadioButtonOldTestament;
   private JSeparator jSeparator3;
   private JSeparator jSeparator4;
   private JSeparator jSeparator5;
   private JSeparator jSeparator6;
   private JSeparator jSeparator7;
   private JTextField jTextFieldSearch;

   public VerseSearchPanel(Window owner, List<Bible> bibles, VerseSearchPanel.VerseSearchPanelListener listener) {
      this.owner = owner;
      this.bibles.addAll(bibles.subList(0, Math.min(3, bibles.size())));
      this.listener = listener;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonFind.addActionListener(e -> this.search());
      this.jRadioButtonBook.addItemListener(e -> this.jComboBoxBooks.setEnabled(e.getStateChange() == 1));
      this.jCheckBoxExactPhrase
         .addItemListener(
            e -> Arrays.asList(this.jRadioButtonAllWords, this.jRadioButtonAnyWord, this.jCheckBoxWholeWord)
                  .forEach(c -> c.setEnabled(e.getStateChange() != 1))
         );
      this.tab = new TabResult(this.bibles, this.vf, this.jLabelFooter, this);
      this.jPanelContent.add(this.tab, "Center");
      this.jPanelParam3.setLayout(new WrapLayout(1, 20, 5));
      this.bibles.forEach(b -> {
         JCheckBox chkbx = new JCheckBox(b.version);
         chkbx.setHorizontalAlignment(0);
         chkbx.setFont(this.jRadioButtonAllBible.getFont());
         chkbx.setFocusable(false);
         chkbx.setSelected(true);
         this.vf.setVersion(b.abbrev, true);
         chkbx.addItemListener(e -> {
            this.vf.setVersion(b.abbrev, e.getStateChange() == 1);
            this.tab.refresh();
         });
         this.jPanelParam3.add(chkbx);
      });
      Arrays.asList(
            this.jRadioButtonAllBible,
            this.jRadioButtonOldTestament,
            this.jRadioButtonNewTestament,
            this.jRadioButtonBook,
            this.jComboBoxBooks,
            this.jComboBoxMaxList
         )
         .forEach(b -> ((ItemSelectable)b).addItemListener(e -> {
               if (e.getStateChange() == 1) {
                  this.vf.setEnum(this.getSelectedBookFilter());
                  this.vf.setMaxResults(Integer.parseInt(this.jComboBoxMaxList.getSelectedItem().toString()));
                  this.tab.refresh();
               }
            }));
   }

   private void initComponents() {
      this.buttonGroupAllOrAnyWord = new ButtonGroup();
      this.buttonGroupAllOldNewBook = new ButtonGroup();
      this.jPanelGeneral = new JPanel();
      this.jTextFieldSearch = SwingUtils.createJTextFieldPlaceHolder(I18N.MSG_TOOL_TIP_TEXT_JTEXT.val());
      this.jButtonFind = new JButton();
      this.jPanelParam1 = new JPanel();
      this.jCheckBoxExactPhrase = new JCheckBox();
      this.jSeparator6 = new JSeparator();
      this.jSeparator5 = new JSeparator();
      this.jCheckBoxWholeWord = new JCheckBox();
      this.jRadioButtonAllWords = new JRadioButton();
      this.jRadioButtonAnyWord = new JRadioButton();
      this.jSeparator3 = new JSeparator();
      this.jSeparator4 = new JSeparator();
      this.jSeparator7 = new JSeparator();
      this.jPanelParam2 = new JPanel();
      this.jRadioButtonAllBible = new JRadioButton();
      this.jRadioButtonOldTestament = new JRadioButton();
      this.jRadioButtonNewTestament = new JRadioButton();
      this.jRadioButtonBook = new JRadioButton();
      this.jComboBoxBooks = new JComboBox<>();
      this.jPanelParam3 = new JPanel();
      this.jPanelContent = new JPanel();
      this.jPanelFooter = new JPanel();
      this.jLabelFooter = new JLabel();
      this.jPanelFooterRight = new JPanel();
      this.jLabelList = new JLabel();
      this.jComboBoxMaxList = new JComboBox<>();
      this.setMaximumSize(new Dimension(800, 700));
      this.setMinimumSize(new Dimension(800, 700));
      this.setPreferredSize(new Dimension(800, 700));
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelGeneral.setLayout(new GridBagLayout());
      this.jTextFieldSearch.setFont(new Font("Georgia", 0, 14));
      this.jTextFieldSearch.setHorizontalAlignment(0);
      this.jTextFieldSearch.setNextFocusableComponent(this.jTextFieldSearch);
      this.jTextFieldSearch.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent evt) {
            VerseSearchPanel.this.jTextFieldSearchKeyPressed(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 4);
      this.jPanelGeneral.add(this.jTextFieldSearch, gridBagConstraints);
      this.jButtonFind.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/bible/img/search16.png")));
      this.jButtonFind.setFocusable(false);
      this.jButtonFind.setMargin(new Insets(2, 4, 2, 4));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 4);
      this.jPanelGeneral.add(this.jButtonFind, gridBagConstraints);
      this.jPanelParam1.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
      this.jPanelParam1.setLayout(new GridBagLayout());
      this.jCheckBoxExactPhrase.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxExactPhrase.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/bible/i18n/Bundle").getString("msg.exactFrase")));
      this.jCheckBoxExactPhrase.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      this.jPanelParam1.add(this.jCheckBoxExactPhrase, gridBagConstraints);
      this.jSeparator6.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(0, 10, 0, 0);
      this.jPanelParam1.add(this.jSeparator6, gridBagConstraints);
      this.jSeparator5.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 10);
      this.jPanelParam1.add(this.jSeparator5, gridBagConstraints);
      this.jCheckBoxWholeWord.setFont(new Font("Georgia", 0, 12));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/bible/i18n/Bundle");
      this.jCheckBoxWholeWord.setText(bundle.getString("VerseSearchPanel.jCheckBoxWholeWord.text"));
      this.jCheckBoxWholeWord.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 4);
      this.jPanelParam1.add(this.jCheckBoxWholeWord, gridBagConstraints);
      this.buttonGroupAllOrAnyWord.add(this.jRadioButtonAllWords);
      this.jRadioButtonAllWords.setFont(new Font("Georgia", 0, 12));
      this.jRadioButtonAllWords.setSelected(true);
      this.jRadioButtonAllWords.setText(bundle.getString("VerseSearchPanel.jRadioButtonAllWords.text"));
      this.jRadioButtonAllWords.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 4);
      this.jPanelParam1.add(this.jRadioButtonAllWords, gridBagConstraints);
      this.buttonGroupAllOrAnyWord.add(this.jRadioButtonAnyWord);
      this.jRadioButtonAnyWord.setFont(new Font("Georgia", 0, 12));
      this.jRadioButtonAnyWord.setText(bundle.getString("VerseSearchPanel.jRadioButtonAnyWord.text"));
      this.jRadioButtonAnyWord.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 4);
      this.jPanelParam1.add(this.jRadioButtonAnyWord, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 4);
      this.jPanelGeneral.add(this.jPanelParam1, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(1, 0, 1, 0);
      this.jPanelGeneral.add(this.jSeparator3, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(1, 0, 1, 0);
      this.jPanelGeneral.add(this.jSeparator4, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(1, 0, 1, 0);
      this.jPanelGeneral.add(this.jSeparator7, gridBagConstraints);
      this.jPanelParam2.setLayout(new FlowLayout(1, 5, 2));
      this.buttonGroupAllOldNewBook.add(this.jRadioButtonAllBible);
      this.jRadioButtonAllBible.setFont(new Font("Georgia", 0, 12));
      this.jRadioButtonAllBible.setSelected(true);
      this.jRadioButtonAllBible.setText(bundle.getString("VerseSearchPanel.jRadioButtonAllBible.text"));
      this.jRadioButtonAllBible.setFocusable(false);
      this.jPanelParam2.add(this.jRadioButtonAllBible);
      this.buttonGroupAllOldNewBook.add(this.jRadioButtonOldTestament);
      this.jRadioButtonOldTestament.setFont(new Font("Georgia", 0, 12));
      this.jRadioButtonOldTestament.setText(bundle.getString("VerseSearchPanel.jRadioButtonOldTestament.text"));
      this.jRadioButtonOldTestament.setFocusable(false);
      this.jPanelParam2.add(this.jRadioButtonOldTestament);
      this.buttonGroupAllOldNewBook.add(this.jRadioButtonNewTestament);
      this.jRadioButtonNewTestament.setFont(new Font("Georgia", 0, 12));
      this.jRadioButtonNewTestament.setText(bundle.getString("VerseSearchPanel.jRadioButtonNewTestament.text"));
      this.jRadioButtonNewTestament.setFocusable(false);
      this.jPanelParam2.add(this.jRadioButtonNewTestament);
      this.buttonGroupAllOldNewBook.add(this.jRadioButtonBook);
      this.jRadioButtonBook.setFont(new Font("Georgia", 0, 12));
      this.jRadioButtonBook.setText(bundle.getString("VerseSearchPanel.jRadioButtonBook.text"));
      this.jRadioButtonBook.setFocusable(false);
      this.jPanelParam2.add(this.jRadioButtonBook);
      this.jComboBoxBooks.setFont(new Font("Georgia", 0, 13));
      this.jComboBoxBooks.setModel(new DefaultComboBoxModel<>(this.bibles.get(0).books.toArray(new Book[0])));
      this.jComboBoxBooks.setEnabled(false);
      this.jComboBoxBooks.setFocusable(false);
      this.jPanelParam2.add(this.jComboBoxBooks);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 4);
      this.jPanelGeneral.add(this.jPanelParam2, gridBagConstraints);
      this.jPanelParam3.setLayout(new GridLayout(1, 0));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelGeneral.add(this.jPanelParam3, gridBagConstraints);
      this.jPanelContent.setLayout(new BorderLayout());
      this.jPanelFooter.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.jPanelFooter.setLayout(new GridBagLayout());
      this.jLabelFooter.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jLabelFooter.setHorizontalAlignment(0);
      this.jLabelFooter.setText(I18N.MSG_RESULTS_X.format(0));
      this.jLabelFooter.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelFooter.add(this.jLabelFooter, gridBagConstraints);
      this.jPanelFooterRight.setLayout(new BorderLayout());
      this.jLabelList.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jLabelList.setText(bundle.getString("VerseSearchPanel.jLabelList.text"));
      this.jPanelFooterRight.add(this.jLabelList, "West");
      this.jComboBoxMaxList.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jComboBoxMaxList.setModel(new DefaultComboBoxModel<>(VerseFilter.getMaxResultsList()));
      this.jComboBoxMaxList.setSelectedItem(VerseFilter.MAX_RESULT_LIST_DEFAULT_VALUE);
      this.jComboBoxMaxList.setFocusable(false);
      this.jPanelFooterRight.add(this.jComboBoxMaxList, "Center");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelFooter.add(this.jPanelFooterRight, gridBagConstraints);
      this.jPanelContent.add(this.jPanelFooter, "Last");
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanelGeneral, -1, -1, 32767).addComponent(this.jPanelContent, -1, -1, 32767)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup().addComponent(this.jPanelGeneral, -2, -1, -2).addGap(2, 2, 2).addComponent(this.jPanelContent, -1, -1, 32767)
            )
      );
   }

   private void jTextFieldSearchKeyPressed(KeyEvent evt) {
      switch (evt.getKeyCode()) {
         case 10:
            this.search();
            break;
         case 27:
            this.listener.canceled();
      }
   }

   private void search() {
      this.tab.clear();
      final boolean search = !this.jTextFieldSearch.getText().replace(" ", "").isEmpty();
      if (search) {
         this.vf.setEnum(this.getSelectedBookFilter());
         this.vf.setAnyWord(this.jRadioButtonAnyWord.isSelected());
         this.vf.setWholeWord(this.jCheckBoxWholeWord.isSelected());
         this.vf.setExactPhrase(this.jCheckBoxExactPhrase.isSelected());
         this.vf.setSearch(this.jTextFieldSearch.getText());
      }

      PopUpWorker.exe(new MySwingWorker<Void, Void>(this.owner, I18N.WORD_SEARCHING.val()) {
         protected Void doInBackground() throws Exception {
            if (search) {
               VerseSearchPanel.this.tab.search(VerseSearchPanel.this.vf);
            }

            return null;
         }

         @Override
         protected void done() {
            super.done();
            VerseSearchPanel.this.tab.fillList();
            VerseSearchPanel.this.jTextFieldSearch.requestFocus();
         }
      });
   }

   @Override
   public void verseSelected(Verse verse) {
      this.listener.verseSelected(verse);
   }

   private VerseFilter.EnumParams getSelectedBookFilter() {
      if (this.jRadioButtonOldTestament.isSelected()) {
         return VerseFilter.EnumParams.OLD_TESTAMENT;
      } else if (this.jRadioButtonNewTestament.isSelected()) {
         return VerseFilter.EnumParams.NEW_TESTAMENT;
      } else {
         return this.jRadioButtonBook.isSelected()
            ? VerseFilter.EnumParams.BOOK.setValue(((Book)this.jComboBoxBooks.getSelectedItem()).id)
            : VerseFilter.EnumParams.ALL_BIBLE;
      }
   }

   public interface VerseSearchPanelListener {
      void verseSelected(Verse var1);

      void canceled();
   }
}
