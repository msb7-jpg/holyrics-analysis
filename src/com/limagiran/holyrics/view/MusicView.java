package com.limagiran.holyrics.view;

import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.toolbar.WebToolBar;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.swing.CaretListenerAuto;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.view.panel.AddToProfilesPanel;
import com.limagiran.holyrics.view.panel.MultiSlidePreview;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

public class MusicView extends JDialog implements DocumentListenerAuto.DocumentListenerAutoListener, Values {
   private final Music music;
   private final boolean newMusic;
   private final Runnable callback;
   private final SimpleAttributeSet alignText = new SimpleAttributeSet();
   private final AddToProfilesPanel addToProfilesPanel = new AddToProfilesPanel();
   private final MultiSlidePreview multiSlidePreview = new MultiSlidePreview();
   private final String oldTitle;
   private final String oldArtist;
   private ButtonGroup buttonGroupAlignText;
   private ButtonGroup buttonGroupPreviewAmount;
   private Filler filler1;
   private JButton jButtonCustomFormatting;
   private JButton jButtonRemoveLastPoints;
   private JButton jButtonSave;
   private JButton jButtonToUpperCase;
   private JComboBox<PresentationSlideView.EnumScreenType> jComboBoxScreenType;
   private JComboBox<Theme> jComboBoxThemes;
   private JLabel jLabelPreview;
   private JPanel jPanelLeft;
   private JPanel jPanelLeftFooter;
   private JPanel jPanelLeftHeader;
   private JPanel jPanelMusic;
   private JPanel jPanelPreview;
   private JPanel jPanelPreviewAmount;
   private JPanel jPanelScreenType;
   private JPanel jPanelSettingsInput;
   private JPanel jPanelSlideViewSettings;
   private JPanel jPanelSlideViewSettingsHeader;
   private JPanel jPanelToolBar;
   private JRadioButton jRadioButtonPreviewAll;
   private JRadioButton jRadioButtonPreviewCurrent;
   private JScrollPane jScrollPaneLyrics;
   private JSplitPane jSplitPane;
   private JTextField jTextFieldArtist;
   private JTextField jTextFieldNote;
   private JTextField jTextFieldTitle;
   private JTextPane jTextPaneLyrics;
   private JToggleButton jToggleButtonAlignCenter;
   private JToggleButton jToggleButtonAlignLeft;
   private JToolBar jToolBar;
   private BindingGroup bindingGroup;

   private MusicView(Music music, boolean newMusic, Theme theme, Runnable callback) {
      this.music = music;
      this.newMusic = newMusic;
      this.callback = callback;
      this.oldTitle = StringUtils.stringToSearch(music.getTitle()).trim();
      this.oldArtist = StringUtils.stringToSearch(music.getArtist()).trim();
      this.initComponents();
      this.init();
      this.setupWebLaf();
      this.jComboBoxThemes.setSelectedItem(theme);
      this.multiSlidePreview.refreshPreview(this.getPresentations());
   }

   private void init() {
      this.jPanelSlideViewSettings.add(this.multiSlidePreview, "Center");
      this.jButtonSave.addActionListener(e -> this.saveAndExit());
      this.jButtonToUpperCase.addActionListener(e -> this.toUpperCase());
      this.jButtonRemoveLastPoints.addActionListener(e -> this.removeLastPoints());
      this.jButtonCustomFormatting.addActionListener(e -> PopupUtils.customParagraph(this::trigger));
      this.jToggleButtonAlignLeft.addActionListener(e -> this.changeAlign());
      this.jToggleButtonAlignCenter.addActionListener(e -> this.changeAlign());
      BooleanOptional.runIfTrue(this.newMusic, () -> this.jPanelLeftFooter.add(this.addToProfilesPanel, "Center"));
      SwingUtils.maximize(this, 50);
      this.changeAlign();
      this.jTextFieldTitle.setDocument(SwingUtils.createDocMaxSize(80));
      this.jTextFieldTitle.setText(this.music.getTitle());
      this.jTextFieldArtist.setDocument(SwingUtils.createDocMaxSize(80));
      this.jTextFieldArtist.setText(this.music.getArtist());
      this.jTextFieldNote.setDocument(SwingUtils.createDocMaxSize(200));
      this.jTextFieldNote.setText(this.music.getNote());
      this.jScrollPaneLyrics.remove(this.jTextPaneLyrics);
      JPanel noWrapPanel = new JPanel(new BorderLayout());
      noWrapPanel.add(this.jTextPaneLyrics);
      this.jScrollPaneLyrics.setViewportView(noWrapPanel);
      this.jTextPaneLyrics.setText(this.music.getLyricsWithFormatting());
      this.jTextPaneLyrics.setCaretPosition(0);
      SwingUtils.setupCCPUAndRedo(this.jTextPaneLyrics);
      this.jButtonSave.setEnabled(this.newMusic);
      this.jRadioButtonPreviewAll.addChangeListener(e -> this.refreshPreview());
      this.jRadioButtonPreviewCurrent.addChangeListener(e -> this.refreshPreview());
      CaretListener caretListener = new CaretListenerAuto(
         700L, () -> BooleanOptional.runIfTrue(this.jRadioButtonPreviewCurrent.isSelected(), this::refreshPreview)
      );
      Arrays.asList(this.jTextFieldArtist, this.jTextFieldTitle, this.jTextPaneLyrics).forEach(c -> {
         c.getDocument().addDocumentListener(new DocumentListenerAuto(500L, this));
         c.addCaretListener(caretListener);
      });
      this.jComboBoxThemes.addItemListener(e -> BooleanOptional.runIfTrue(e.getStateChange() == 1, () -> this.multiSlidePreview.setTheme(this.getTheme())));
      BooleanOptional.runIfTrue(this.newMusic, this.jTextFieldTitle::requestFocus);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::close));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(83, 128, this::saveAndExit));
      this.jScrollPaneLyrics.getVerticalScrollBar().setUnitIncrement(20);
      SwingUtils.setJTextPaneLineSpacing(this.jTextPaneLyrics, 0.35F);
      SwingUtils.setJTextPaneFont(this.jTextPaneLyrics, new Font("Georgia", 0, 14));
      this.jTextPaneLyrics.setBorder(new CompoundBorder(this.jTextPaneLyrics.getBorder(), new EmptyBorder(4, 6, 4, 6)));
   }

   private void setupWebLaf() {
      ((WebToolBar)this.jToolBar).setUndecorated(true);
      ((WebScrollPane)this.jScrollPaneLyrics).setShadeWidth(1);
      WebLafUtils.setupWebLafBorderLayout(this.jPanelLeft, this.jPanelSlideViewSettingsHeader);
      SwingUtils.setJPanelToOpaqueFalse(this);
      WebButtonGroup buttonGroup = new WebButtonGroup(true, new JComponent[]{this.jToggleButtonAlignLeft, this.jToggleButtonAlignCenter});
      buttonGroup.setButtonsShadeColor(Values.COLOR_TRANSPARENT);
      this.jToolBar.add(buttonGroup, 1);
      Arrays.asList(this.jToolBar.getComponents())
         .stream()
         .filter(c -> c instanceof WebButton)
         .forEach(c -> ((WebButton)c).setShadeColor(Values.COLOR_TRANSPARENT));
      Arrays.asList(this.jComboBoxScreenType, this.jComboBoxThemes).forEach(c -> ((WebComboBox)c).setShadeWidth(0));
   }

   private void initComponents() {
      this.bindingGroup = new BindingGroup();
      this.buttonGroupAlignText = new ButtonGroup();
      this.buttonGroupPreviewAmount = new ButtonGroup();
      this.jPanelMusic = new JPanel();
      this.jSplitPane = new JSplitPane();
      this.jPanelLeft = new WebPanel(true);
      this.jPanelLeftHeader = new JPanel();
      this.jPanelToolBar = new JPanel();
      this.jToolBar = new WebToolBar();
      this.jButtonSave = new WebButton();
      this.jToggleButtonAlignLeft = new WebToggleButton();
      this.jToggleButtonAlignCenter = new WebToggleButton();
      this.jButtonToUpperCase = new WebButton();
      this.jButtonRemoveLastPoints = new WebButton();
      this.filler1 = new Filler(new Dimension(8, 0), new Dimension(8, 0), new Dimension(8, 32767));
      this.jButtonCustomFormatting = new WebButton();
      this.jPanelSettingsInput = new JPanel();
      this.jTextFieldTitle = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_TITLE.val());
      this.jTextFieldArtist = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_ARTIST.val());
      this.jTextFieldNote = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_NOTE.val());
      this.jScrollPaneLyrics = new WebScrollPane(null);
      this.jTextPaneLyrics = SwingUtils.createJTextPanePlaceHolder(I18N.MSG_PH_TYPE_MUSIC_LYRICS.val());
      this.jTextPaneLyrics.setDocument(new DefaultStyledDocument() {
         @Override
         public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str.contains("§")) {
               str = str.replace("§", "");
            }

            super.insertString(offs, str, a);
         }
      });
      this.jPanelLeftFooter = new JPanel();
      this.jPanelSlideViewSettings = new JPanel();
      this.jPanelSlideViewSettingsHeader = new WebPanel(true);
      this.jPanelPreview = new JPanel();
      this.jLabelPreview = new JLabel();
      this.jComboBoxThemes = new WebComboBox();
      this.jPanelScreenType = new JPanel();
      this.jComboBoxScreenType = new WebComboBox();
      this.jPanelPreviewAmount = new JPanel();
      this.jRadioButtonPreviewAll = new JRadioButton();
      this.jRadioButtonPreviewCurrent = new JRadioButton();
      this.setDefaultCloseOperation(0);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.music")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(750, 500));
      this.setModal(true);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            MusicView.this.formWindowClosing(evt);
         }
      });
      this.jPanelMusic.setLayout(new BorderLayout());
      this.jPanelLeft.setLayout(new BorderLayout());
      this.jPanelLeftHeader.setBorder(BorderFactory.createEmptyBorder(4, 4, 12, 4));
      this.jPanelLeftHeader.setLayout(new BorderLayout(0, 5));
      this.jPanelToolBar.setLayout(new FlowLayout(1, 0, 0));
      this.jToolBar.setBorder(new LineBorder(new Color(159, 164, 172), 2, true));
      this.jToolBar.setFloatable(false);
      this.jToolBar.setRollover(true);
      this.jToolBar.setAlignmentY(0.5F);
      this.jButtonSave.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/saveBlack24.png")));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jButtonSave.setToolTipText(bundle.getString("word.saveAndExit"));
      this.jButtonSave.setFocusable(false);
      this.jButtonSave.setHorizontalTextPosition(0);
      this.jButtonSave.setMargin(new Insets(2, 4, 2, 4));
      this.jButtonSave.setVerticalTextPosition(3);
      this.jToolBar.add(this.jButtonSave);
      this.buttonGroupAlignText.add(this.jToggleButtonAlignLeft);
      this.jToggleButtonAlignLeft.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/alignLeft24.png")));
      this.jToggleButtonAlignLeft.setSelected(true);
      this.jToggleButtonAlignLeft.setFocusable(false);
      this.jToggleButtonAlignLeft.setHorizontalTextPosition(0);
      this.jToggleButtonAlignLeft.setMargin(new Insets(2, 4, 2, 4));
      this.jToggleButtonAlignLeft.setVerticalTextPosition(3);
      this.jToolBar.add(this.jToggleButtonAlignLeft);
      this.buttonGroupAlignText.add(this.jToggleButtonAlignCenter);
      this.jToggleButtonAlignCenter.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/alignCenter24.png")));
      this.jToggleButtonAlignCenter.setFocusable(false);
      this.jToggleButtonAlignCenter.setHorizontalTextPosition(0);
      this.jToggleButtonAlignCenter.setMargin(new Insets(2, 4, 2, 4));
      this.jToggleButtonAlignCenter.setVerticalTextPosition(3);
      this.jToolBar.add(this.jToggleButtonAlignCenter);
      this.jButtonToUpperCase.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/toUpperCase24.png")));
      this.jButtonToUpperCase.setToolTipText(bundle.getString("MusicView.jButtonToUpperCase.toolTipText"));
      this.jButtonToUpperCase.setFocusable(false);
      this.jButtonToUpperCase.setHorizontalTextPosition(0);
      this.jButtonToUpperCase.setMargin(new Insets(2, 4, 2, 4));
      this.jButtonToUpperCase.setVerticalTextPosition(3);
      this.jToolBar.add(this.jButtonToUpperCase);
      this.jButtonRemoveLastPoints.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/checkedText24.png")));
      this.jButtonRemoveLastPoints.setToolTipText(bundle.getString("MusicView.jButtonRemoveLastPoints.toolTipText"));
      this.jButtonRemoveLastPoints.setFocusable(false);
      this.jButtonRemoveLastPoints.setHorizontalTextPosition(0);
      this.jButtonRemoveLastPoints.setMargin(new Insets(2, 4, 2, 4));
      this.jButtonRemoveLastPoints.setVerticalTextPosition(3);
      this.jToolBar.add(this.jButtonRemoveLastPoints);
      this.jToolBar.add(this.filler1);
      this.jButtonCustomFormatting.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/style24.png")));
      this.jButtonCustomFormatting.setToolTipText(bundle.getString("msg.customFormatting"));
      this.jButtonCustomFormatting.setFocusable(false);
      this.jButtonCustomFormatting.setHorizontalTextPosition(0);
      this.jButtonCustomFormatting.setMargin(new Insets(2, 4, 2, 4));
      this.jButtonCustomFormatting.setVerticalTextPosition(3);
      this.jToolBar.add(this.jButtonCustomFormatting);
      this.jPanelToolBar.add(this.jToolBar);
      this.jPanelLeftHeader.add(this.jPanelToolBar, "First");
      this.jPanelSettingsInput.setLayout(new GridBagLayout());
      this.jTextFieldTitle.setColumns(35);
      this.jTextFieldTitle.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldTitle.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            MusicView.this.musicChanged(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.jPanelSettingsInput.add(this.jTextFieldTitle, gridBagConstraints);
      this.jTextFieldArtist.setColumns(20);
      this.jTextFieldArtist.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldArtist.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            MusicView.this.musicChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.jPanelSettingsInput.add(this.jTextFieldArtist, gridBagConstraints);
      this.jTextFieldNote.setColumns(20);
      this.jTextFieldNote.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldNote.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            MusicView.this.musicChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.jPanelSettingsInput.add(this.jTextFieldNote, gridBagConstraints);
      this.jPanelLeftHeader.add(this.jPanelSettingsInput, "Center");
      this.jPanelLeft.add(this.jPanelLeftHeader, "First");
      Binding binding = Bindings.createAutoBinding(
         UpdateStrategy.READ_WRITE, this.jPanelLeftHeader, ELProperty.create("${preferredSize}"), this.jScrollPaneLyrics, BeanProperty.create("preferredSize")
      );
      this.bindingGroup.addBinding(binding);
      this.jTextPaneLyrics.setFont(new Font("Georgia", 0, 13));
      this.jTextPaneLyrics.setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.ttPanelLyrics")));
      this.jTextPaneLyrics.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            MusicView.this.musicChanged(evt);
         }
      });
      this.jScrollPaneLyrics.setViewportView(this.jTextPaneLyrics);
      this.jPanelLeft.add(this.jScrollPaneLyrics, "Center");
      this.jPanelLeftFooter.setLayout(new BorderLayout());
      this.jPanelLeft.add(this.jPanelLeftFooter, "Last");
      this.jSplitPane.setLeftComponent(this.jPanelLeft);
      this.jPanelSlideViewSettings.setMinimumSize(new Dimension(0, 0));
      this.jPanelSlideViewSettings.setLayout(new BorderLayout());
      this.jPanelSlideViewSettingsHeader.setLayout(new BorderLayout());
      this.jLabelPreview.setFont(new Font("Georgia", 0, 18));
      this.jLabelPreview.setHorizontalAlignment(0);
      this.jLabelPreview.setText(bundle.getString("word.preview"));
      this.jLabelPreview.setMinimumSize(new Dimension(0, 0));
      this.jPanelPreview.add(this.jLabelPreview);
      this.jComboBoxThemes.setMaximumRowCount(5);
      this.jComboBoxThemes.setModel(new DefaultComboBoxModel<>(ThemeController.getThemes().toArray(new Theme[0])));
      this.jComboBoxThemes.setFocusable(false);
      this.jComboBoxThemes.setMinimumSize(new Dimension(0, 0));
      this.jComboBoxThemes.setPreferredSize(new Dimension(100, 47));
      this.jComboBoxThemes.setRenderer(new ComboBoxRendererTheme(80, 45));
      this.jComboBoxThemes.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicView.this.jComboBoxThemesItemStateChanged(evt);
         }
      });
      this.jPanelPreview.add(this.jComboBoxThemes);
      this.jComboBoxScreenType.setMaximumRowCount(4);
      this.jComboBoxScreenType
         .setModel(
            new DefaultComboBoxModel<>(
               new PresentationSlideView.EnumScreenType[]{
                  PresentationSlideView.EnumScreenType.YOUR_DISPLAY,
                  PresentationSlideView.EnumScreenType.DISPLAY_MONITOR,
                  PresentationSlideView.EnumScreenType.WIDESCREEN,
                  PresentationSlideView.EnumScreenType.STANDARD
               }
            )
         );
      this.jComboBoxScreenType.setSelectedItem(PresentationSlideView.EnumScreenType.DISPLAY_MONITOR);
      this.jComboBoxScreenType.setFocusable(false);
      this.jComboBoxScreenType.setMinimumSize(new Dimension(0, 20));
      this.jComboBoxScreenType.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicView.this.jComboBoxScreenTypeItemStateChanged(evt);
         }
      });
      this.jPanelScreenType.add(this.jComboBoxScreenType);
      this.jPanelPreview.add(this.jPanelScreenType);
      this.jPanelPreviewAmount.setLayout(new GridLayout(2, 0));
      this.buttonGroupPreviewAmount.add(this.jRadioButtonPreviewAll);
      this.jRadioButtonPreviewAll.setSelected(true);
      this.jRadioButtonPreviewAll.setText(bundle.getString("word.allSlides"));
      this.jRadioButtonPreviewAll.setFocusable(false);
      this.jRadioButtonPreviewAll.setHorizontalAlignment(2);
      this.jPanelPreviewAmount.add(this.jRadioButtonPreviewAll);
      this.buttonGroupPreviewAmount.add(this.jRadioButtonPreviewCurrent);
      this.jRadioButtonPreviewCurrent.setText(bundle.getString("word.currentSlide"));
      this.jRadioButtonPreviewCurrent.setFocusable(false);
      this.jRadioButtonPreviewCurrent.setHorizontalAlignment(2);
      this.jPanelPreviewAmount.add(this.jRadioButtonPreviewCurrent);
      this.jPanelPreview.add(this.jPanelPreviewAmount);
      this.jPanelSlideViewSettingsHeader.add(this.jPanelPreview, "Center");
      this.jPanelSlideViewSettings.add(this.jPanelSlideViewSettingsHeader, "First");
      this.jSplitPane.setRightComponent(this.jPanelSlideViewSettings);
      this.jPanelMusic.add(this.jSplitPane, "Center");
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanelMusic, -1, 750, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanelMusic, -1, -1, 32767));
      this.bindingGroup.bind();
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.close();
   }

   private void jComboBoxThemesItemStateChanged(ItemEvent evt) {
      this.refreshPreview();
   }

   private void jComboBoxScreenTypeItemStateChanged(ItemEvent evt) {
      if (evt.getStateChange() == 1) {
         this.multiSlidePreview.setScreenType((PresentationSlideView.EnumScreenType)evt.getItem());
      }
   }

   private void musicChanged(KeyEvent evt) {
      this.jButtonSave.setEnabled(true);
   }

   public static void main(Music music, boolean newMusic, Theme theme, Runnable callback) {
      SwingUtilities.invokeLater(() -> new MusicView(music, newMusic, theme, callback).setVisible(true));
   }

   private void superdispose() {
      super.dispose();
      this.removeAll();
   }

   private void close() {
      if (this.jButtonSave.isEnabled()) {
         switch (JOptionPane.showConfirmDialog(this, I18N.MSG_CONFIRM_SAVE_CHANGES.val(), I18N.WORD_CONFIRM.val(), 1)) {
            case 0:
               this.saveAndExit();
               break;
            case 1:
               this.superdispose();
         }
      } else {
         this.superdispose();
      }
   }

   private void refreshPreview() {
      this.multiSlidePreview.refreshPreview(this.getPresentations());
   }

   private void saveAndExit() {
      if (this.check()) {
         List<MusicController.Paragraph> list = this.getParagraphs();
         this.music
            .setTitle(this.jTextFieldTitle.getText())
            .setArtist(this.jTextFieldArtist.getText())
            .setNote(this.jTextFieldNote.getText())
            .setLyrics(list.stream().map(MusicController.Paragraph::getText).collect(Collectors.joining("\n\n")))
            .setFormatting(list.stream().map(MusicController.Paragraph::getFormatting).collect(Collectors.joining("\n")));
         if (this.newMusic) {
            BooleanOptional.runIfTrue(MusicDao.add(this.music), () -> this.addToProfilesPanel.addMusicsInCheckedProfiles(Arrays.asList(this.music)));
         } else {
            MusicDao.set(this.music);
         }

         Optional.ofNullable(this.callback).ifPresent(r -> r.run());
         this.superdispose();
      }
   }

   private void changeAlign() {
      StyledDocument doc = this.jTextPaneLyrics.getStyledDocument();
      StyleConstants.setAlignment(this.alignText, this.jToggleButtonAlignCenter.isSelected() ? 1 : 0);
      doc.setParagraphAttributes(0, doc.getLength() + 1, this.alignText, false);
   }

   private List<Presentation> getPresentations() {
      Theme theme = this.getTheme();
      if (this.jRadioButtonPreviewAll.isSelected()) {
         List<Presentation> _return = new ArrayList<>();
         _return.add(this.getTitlePresentation());
         this.getParagraphs().stream().map(px -> Presentation.create(px, theme)).forEach(_return::add);
         return _return;
      } else if (!this.jTextFieldArtist.isFocusOwner() && !this.jTextFieldTitle.isFocusOwner()) {
         int pos = this.jTextPaneLyrics.getCaretPosition();
         int index = -2;

         for (MusicController.Paragraph p : this.getParagraphs()) {
            index += p.getText().length() + 2;
            if (index >= pos) {
               return Arrays.asList(Presentation.create(p, theme));
            }
         }

         return Arrays.asList(Presentation.create(new MusicController.Paragraph(), theme));
      } else {
         return Arrays.asList(this.getTitlePresentation());
      }
   }

   private Presentation getTitlePresentation() {
      MusicController.Paragraph p = new MusicController.Paragraph(String.format("%s%n%s", this.jTextFieldTitle.getText(), this.jTextFieldArtist.getText()))
         .setSlideType(PresentationSlideView.EnumSlideType.TITLE);
      return Presentation.create(p, this.getTheme());
   }

   private Theme getTheme() {
      return (Theme)Optional.ofNullable((Theme)this.jComboBoxThemes.getSelectedItem()).orElse(Theme.BLACK_SCREEN);
   }

   private String getText() {
      try {
         String lyrics = this.jTextPaneLyrics.getDocument().getText(0, this.jTextPaneLyrics.getDocument().getLength());

         while (lyrics.contains("\n\n\n")) {
            lyrics = lyrics.replace("\n\n\n", "\n\n");
         }

         if (lyrics.replace("\n", "").isEmpty()) {
            lyrics = lyrics + " ";
         }

         return lyrics.substring(0, Math.min(lyrics.length(), 12000));
      } catch (Exception var2) {
         return "";
      }
   }

   private List<MusicController.Paragraph> getParagraphs() {
      return Arrays.stream(this.getText().split("\n{2}")).map(MusicUtils::createParagraph).peek(p -> p.setForceFormatting(true)).collect(Collectors.toList());
   }

   private void toUpperCase() {
      JPanel panel = new JPanel(new GridLayout(3, 1, 0, 5));
      JRadioButton first = new JRadioButton(I18N.MSG_THE_FIRST_LETTER_OF_EACH_LINE.val(), true);
      JRadioButton allSelection = new JRadioButton(I18N.MSG_CURRENTLY_SELECTED_TEXT.val(), false);
      ButtonGroup group = new ButtonGroup();
      JLabel title = new JLabel(I18N.MSG_CHANGE_TO_UPPER_CASE.val(), 0);
      title.setBorder(new EmptyBorder(0, 0, 8, 0));
      title.setFont(new Font("Georgia", 1, 16));
      panel.add(title);
      Arrays.asList(first, allSelection).forEach(b -> {
         b.setFont(new Font("Georgia", 0, 14));
         b.setFocusable(false);
         group.add(b);
         panel.add(b);
      });
      JOptionPane.showMessageDialog(this.jButtonToUpperCase, panel, I18N.MSG_CHANGE_TO_UPPER_CASE.val(), -1);
      if (first.isSelected()) {
         this.firstLetterToUpperCase();
      } else {
         this.selectedTextToUpperCase();
      }

      this.jButtonSave.setEnabled(true);
   }

   private void selectedTextToUpperCase() {
      try {
         int start = this.jTextPaneLyrics.getSelectionStart();
         int end = this.jTextPaneLyrics.getSelectionEnd();
         this.jTextPaneLyrics.replaceSelection(this.jTextPaneLyrics.getSelectedText().toUpperCase());
         this.jTextPaneLyrics.select(start, end);
      } catch (Exception var3) {
      }
   }

   private void firstLetterToUpperCase() {
      try {
         int start = this.jTextPaneLyrics.getSelectionStart();
         int end = this.jTextPaneLyrics.getSelectionEnd();
         StringBuilder sb = new StringBuilder(this.getText());
         if (sb.length() > 0) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
         }

         for (int i = 0; i < sb.length() - 1; i++) {
            if (sb.charAt(i) == '\n') {
               sb.setCharAt(i + 1, Character.toUpperCase(sb.charAt(i + 1)));
            }
         }

         this.jTextPaneLyrics.setText(sb.toString());
         this.jTextPaneLyrics.select(start, end);
      } catch (Exception var5) {
      }
   }

   private void removeLastPoints() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.getText().replace("...", "└"));
      List<Character> chars = Arrays.asList(',', '.', ' ', ';');

      for (int i = 1; i < sb.length(); i++) {
         int iFinal = i;
         if (sb.charAt(i) == '\n' && chars.stream().anyMatch(c -> c == sb.charAt(iFinal - 1))) {
            sb.deleteCharAt(--i);
            i--;
         }
      }

      while (!sb.toString().isEmpty() && chars.stream().anyMatch(c -> c == sb.charAt(sb.length() - 1))) {
         sb.deleteCharAt(sb.length() - 1);
      }

      this.jTextPaneLyrics.setText(sb.toString().replace("└", "..."));
      this.jButtonSave.setEnabled(true);
   }

   @Override
   public void trigger() {
      this.multiSlidePreview.refreshPreview(this.getPresentations());
   }

   private boolean check() {
      String title = StringUtils.stringToSearch(this.jTextFieldTitle.getText().trim());
      String artist = StringUtils.stringToSearch(this.jTextFieldArtist.getText().trim());
      if (title.isEmpty()) {
         Utils.error(this, I18N.MSG_ERROR_FIELD_X_EMPTY.format(I18N.WORD_TITLE.val()));
         this.jTextFieldTitle.requestFocus();
         return false;
      } else if (artist.isEmpty()) {
         Utils.error(this, I18N.MSG_ERROR_FIELD_X_EMPTY.format(I18N.WORD_ARTIST.val()));
         this.jTextFieldArtist.requestFocus();
         return false;
      } else {
         String _title = StringUtils.stringToSearch(title);
         String _artist = StringUtils.stringToSearch(artist);
         if (!MusicDao.contains(_title, _artist) || Objects.equals(_title, this.oldTitle) && Objects.equals(_artist, this.oldArtist)) {
            return true;
         } else {
            String txt = I18N.MSG_MUSIC_ALREADY_EXISTS_DUPLICATE_FORMAT.format(title, artist);
            return Utils.confirm(txt);
         }
      }
   }

   private static enum EnumChange {
      TITLE,
      ARTIST,
      TYPE,
      NOTE,
      LYRICS;
   }
}
