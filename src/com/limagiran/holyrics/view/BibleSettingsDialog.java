package com.limagiran.holyrics.view;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.EnumVerseDescription;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

public class BibleSettingsDialog extends JDialog {
   private final Runnable callback;
   private ButtonGroup buttonGroupBookPanel;
   private JButton jButtonCancel;
   private JButton jButtonOk;
   private JCheckBox jCheckBoxEmptyLineBetweenVerses;
   private JCheckBox jCheckBoxShowOnlyReference;
   private JCheckBox jCheckBoxShowVersion;
   private JCheckBox jCheckBoxUppercase;
   private JComboBox<EnumVerseDescription> jComboBoxVerseDescription;
   private JComboBox<String> jComboBoxVerseDescriptionFormat;
   private JLabel jLabelBookPanel;
   private JLabel jLabelVerseDescription;
   private JPanel jPanelBookPanel;
   private JPanel jPanelChecks;
   private JPanel jPanelGeneral;
   private JPanel jPanelOkCancel;
   private JPanel jPanelShowVerse;
   private JRadioButton jRadioButtonBookPanelGrid;
   private JRadioButton jRadioButtonBookPanelList;
   private JSeparator jSeparator1;

   private BibleSettingsDialog(Runnable callback) {
      this.callback = callback;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jComboBoxVerseDescriptionFormat.setSelectedIndex(Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION_FULL.is() ? 1 : 0);
      this.jComboBoxVerseDescription.setSelectedItem(EnumVerseDescription.valueOfOrDefault(Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION.getString()));
      this.jButtonOk.addActionListener(e -> this.ok(this.callback));
      this.jButtonCancel.addActionListener(e -> this.dispose());
      InsertShortcut.addEscToDispose(this);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, () -> this.ok(this.callback)));
   }

   private void initComponents() {
      this.buttonGroupBookPanel = new ButtonGroup();
      this.jPanelGeneral = new JPanel();
      this.jPanelChecks = new JPanel();
      this.jCheckBoxShowVersion = new JCheckBox();
      this.jCheckBoxUppercase = new JCheckBox();
      this.jCheckBoxShowOnlyReference = new JCheckBox();
      this.jPanelBookPanel = new JPanel();
      this.jLabelBookPanel = new JLabel();
      this.jRadioButtonBookPanelGrid = new JRadioButton();
      this.jRadioButtonBookPanelList = new JRadioButton();
      this.jPanelShowVerse = new JPanel();
      this.jLabelVerseDescription = new JLabel();
      this.jComboBoxVerseDescriptionFormat = new JComboBox<>();
      this.jComboBoxVerseDescription = new JComboBox<>();
      this.jCheckBoxEmptyLineBetweenVerses = new JCheckBox();
      this.jSeparator1 = new JSeparator();
      this.jPanelOkCancel = new JPanel();
      this.jButtonOk = new JButton();
      this.jButtonCancel = new JButton();
      this.setDefaultCloseOperation(2);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.settings"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setModal(true);
      this.setResizable(false);
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
      this.jPanelGeneral.setLayout(new GridBagLayout());
      this.jPanelChecks.setLayout(new GridLayout(3, 1, 0, 2));
      this.jCheckBoxShowVersion.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxShowVersion.setSelected(Settings.EnumSettings.BIBLE_SHOW_VERSION.is());
      this.jCheckBoxShowVersion.setText(bundle.getString("BibleMain.jCheckBoxShowVersion.text"));
      this.jCheckBoxShowVersion.setFocusable(false);
      this.jCheckBoxShowVersion.setHorizontalAlignment(2);
      this.jPanelChecks.add(this.jCheckBoxShowVersion);
      this.jCheckBoxUppercase.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxUppercase.setSelected(Settings.EnumSettings.BIBLE_UPPERCASE.is());
      this.jCheckBoxUppercase.setText(bundle.getString("word.uppercase"));
      this.jCheckBoxUppercase.setFocusable(false);
      this.jCheckBoxUppercase.setHorizontalAlignment(2);
      this.jPanelChecks.add(this.jCheckBoxUppercase);
      this.jCheckBoxShowOnlyReference.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxShowOnlyReference.setSelected(Settings.EnumSettings2.BIBLE_SHOW_ONLY_REFERENCE.is());
      this.jCheckBoxShowOnlyReference.setText(bundle.getString("msg.showOnlyReference"));
      this.jCheckBoxShowOnlyReference.setFocusable(false);
      this.jCheckBoxShowOnlyReference.setHorizontalAlignment(2);
      this.jPanelChecks.add(this.jCheckBoxShowOnlyReference);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 4, 0);
      this.jPanelGeneral.add(this.jPanelChecks, gridBagConstraints);
      this.jPanelBookPanel.setLayout(new GridBagLayout());
      this.jLabelBookPanel.setFont(new Font("Georgia", 0, 14));
      this.jLabelBookPanel.setText(bundle.getString("msg.bookPanel"));
      this.jPanelBookPanel.add(this.jLabelBookPanel, new GridBagConstraints());
      this.buttonGroupBookPanel.add(this.jRadioButtonBookPanelGrid);
      this.jRadioButtonBookPanelGrid.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonBookPanelGrid.setSelected(Settings.EnumSettings.BOOK_S_NAME_ABBREVIATED.is());
      this.jRadioButtonBookPanelGrid.setText(bundle.getString("word.grid"));
      this.jRadioButtonBookPanelGrid.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.insets = new Insets(0, 6, 0, 6);
      this.jPanelBookPanel.add(this.jRadioButtonBookPanelGrid, gridBagConstraints);
      this.buttonGroupBookPanel.add(this.jRadioButtonBookPanelList);
      this.jRadioButtonBookPanelList.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonBookPanelList.setSelected(!Settings.EnumSettings.BOOK_S_NAME_ABBREVIATED.is());
      this.jRadioButtonBookPanelList.setText(bundle.getString("word.list_"));
      this.jRadioButtonBookPanelList.setFocusable(false);
      this.jPanelBookPanel.add(this.jRadioButtonBookPanelList, new GridBagConstraints());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(4, 0, 4, 0);
      this.jPanelGeneral.add(this.jPanelBookPanel, gridBagConstraints);
      this.jPanelShowVerse.setLayout(new GridBagLayout());
      this.jLabelVerseDescription.setFont(new Font("Georgia", 0, 14));
      this.jLabelVerseDescription.setText(bundle.getString("msg.verseDescription"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.insets = new Insets(0, 2, 0, 2);
      this.jPanelShowVerse.add(this.jLabelVerseDescription, gridBagConstraints);
      this.jComboBoxVerseDescriptionFormat.setFont(new Font("Georgia", 0, 14));
      this.jComboBoxVerseDescriptionFormat
         .setModel(new DefaultComboBoxModel<>(new String[]{I18N.MSG_VERSE_EXAMPLE_SHORT.val(), I18N.MSG_VERSE_EXAMPLE_FULL.val()}));
      this.jComboBoxVerseDescriptionFormat.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.insets = new Insets(0, 2, 0, 2);
      this.jPanelShowVerse.add(this.jComboBoxVerseDescriptionFormat, gridBagConstraints);
      this.jComboBoxVerseDescription.setFont(new Font("Georgia", 0, 14));
      this.jComboBoxVerseDescription.setModel(new DefaultComboBoxModel<>(EnumVerseDescription.values()));
      this.jComboBoxVerseDescription.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.insets = new Insets(0, 2, 0, 2);
      this.jPanelShowVerse.add(this.jComboBoxVerseDescription, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(4, 0, 4, 0);
      this.jPanelGeneral.add(this.jPanelShowVerse, gridBagConstraints);
      this.jCheckBoxEmptyLineBetweenVerses.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxEmptyLineBetweenVerses.setSelected(Settings.EnumSettings.BIBLE_VERSE_EMPTY_LINE_BETWEEN.is());
      this.jCheckBoxEmptyLineBetweenVerses.setText(bundle.getString("msg.emptyLineBetweenVerses"));
      this.jCheckBoxEmptyLineBetweenVerses.setFocusable(false);
      this.jCheckBoxEmptyLineBetweenVerses.setHorizontalAlignment(2);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(4, 20, 4, 0);
      this.jPanelGeneral.add(this.jCheckBoxEmptyLineBetweenVerses, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 4, 0);
      this.jPanelGeneral.add(this.jSeparator1, gridBagConstraints);
      this.jPanelOkCancel.setLayout(new GridLayout(1, 2, 10, 0));
      this.jButtonOk.setFont(new Font("Georgia", 0, 12));
      this.jButtonOk.setText(bundle.getString("word.ok"));
      this.jPanelOkCancel.add(this.jButtonOk);
      this.jButtonCancel.setFont(new Font("Georgia", 0, 12));
      this.jButtonCancel.setText(bundle.getString("word.cancel"));
      this.jPanelOkCancel.add(this.jButtonCancel);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridwidth = 0;
      this.jPanelGeneral.add(this.jPanelOkCancel, gridBagConstraints);
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(Runnable callback) {
      SwingUtilities.invokeLater(() -> new BibleSettingsDialog(callback).setVisible(true));
   }

   private void ok(Runnable callback) {
      Settings.EnumSettings.BIBLE_UPPERCASE.set(this.jCheckBoxUppercase.isSelected());
      Settings.EnumSettings.BIBLE_SHOW_VERSION.set(this.jCheckBoxShowVersion.isSelected());
      Settings.EnumSettings.BOOK_S_NAME_ABBREVIATED.set(this.jRadioButtonBookPanelGrid.isSelected());
      Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION_FULL.set(this.jComboBoxVerseDescriptionFormat.getSelectedIndex() == 1);
      Settings.EnumSettings.BIBLE_VERSE_EMPTY_LINE_BETWEEN.set(this.jCheckBoxEmptyLineBetweenVerses.isSelected());
      Settings.EnumSettings2.BIBLE_SHOW_ONLY_REFERENCE.set(this.jCheckBoxShowOnlyReference.isSelected());
      if (this.jComboBoxVerseDescription.getSelectedIndex() != -1) {
         Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION.set(EnumVerseDescription.values()[this.jComboBoxVerseDescription.getSelectedIndex()].name());
      }

      Optional.ofNullable(callback).ifPresent(r -> r.run());
      this.dispose();
   }
}
