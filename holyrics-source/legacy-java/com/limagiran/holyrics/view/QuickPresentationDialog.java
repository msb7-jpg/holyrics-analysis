package com.limagiran.holyrics.view;

import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.panel.MultiSlidePreview;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class QuickPresentationDialog extends JDialog {
   private static QuickPresentationDialog INSTANCE;
   private final MusicMain parent;
   private final MultiSlidePreview multiSlidePreview = new MultiSlidePreview();
   private JButton jButtonShow;
   private JCheckBox jCheckBoxAutomaticSlideShow;
   private JCheckBox jCheckBoxRepeat;
   private JComboBox<Theme> jComboBoxThemes;
   private JLabel jLabelSeconds;
   private JPanel jPanelLeft;
   private JPanel jPanelLeftCenter;
   private JPanel jPanelLeftFooter;
   private JPanel jPanelSettings;
   private JPanel jPanelSettingsLeft;
   private JScrollPane jScrollPaneQuickPresentation;
   private JSeparator jSeparator;
   private JSeparator jSeparator1;
   private JSpinner jSpinnerSeconds;
   private JTextArea jTextAreaQuickPresentation;

   public QuickPresentationDialog(MusicMain parent) {
      super(parent, true);
      this.parent = parent;
      this.initComponents();
      this.init();
   }

   private void init() {
      ((WebSpinner)this.jSpinnerSeconds).setDrawFocus(false);
      this.jTextAreaQuickPresentation.setDocument(SwingUtils.createDocMaxSize(2048));
      this.jTextAreaQuickPresentation.setText(Settings.EnumSettings.QUICK_PRESENTATION_TEXT.getString());
      this.jTextAreaQuickPresentation.selectAll();
      this.jTextAreaQuickPresentation.getDocument().addDocumentListener(new DocumentListenerAuto(400L, () -> {
         String var10000 = Settings.EnumSettings.QUICK_PRESENTATION_TEXT.set(this.jTextAreaQuickPresentation.getText());
      }));
      this.jCheckBoxAutomaticSlideShow.setSelected(Settings.EnumSettings.QUICK_PRESENTATION_AUTOMATIC.is());
      this.jCheckBoxRepeat.setSelected(Settings.EnumSettings.QUICK_PRESENTATION_REPEAT.is());
      this.jSpinnerSeconds.setValue(Utils.range(Settings.EnumSettings.QUICK_PRESENTATION_SECONDS.getInt(), 2, 30));
      this.setEnabledSubItemsAutomaticSlideShow(Settings.EnumSettings.QUICK_PRESENTATION_AUTOMATIC.is());
      this.jCheckBoxAutomaticSlideShow
         .addItemListener(e -> this.setEnabledSubItemsAutomaticSlideShow(Settings.EnumSettings.QUICK_PRESENTATION_AUTOMATIC.set(e.getStateChange() == 1)));
      this.jCheckBoxRepeat.addItemListener(e -> {
         Boolean var10000 = Settings.EnumSettings.QUICK_PRESENTATION_REPEAT.set(e.getStateChange() == 1);
      });
      this.jSpinnerSeconds.addChangeListener(e -> {
         Integer var10000 = Settings.EnumSettings.QUICK_PRESENTATION_SECONDS.set(Integer.parseInt(this.jSpinnerSeconds.getValue().toString()));
      });
      this.jButtonShow.addActionListener(e -> this.showPresentation());
      this.jComboBoxThemes.setSelectedItem(Settings.SET.getQuickPresentationTheme());
      this.jTextAreaQuickPresentation.getDocument().addDocumentListener(new DocumentListenerAuto(500L, this::refreshPreview));
      SwingUtils.addHtmlShortcut(this.jButtonShow, "(Shift + Enter)");
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, 64, this::showPresentation));
      InsertShortcut.addEscToDispose(this);
      SwingUtils.setupCCPUAndRedo(this.jTextAreaQuickPresentation);
      this.add(this.multiSlidePreview, "Center");
      this.multiSlidePreview.setScreenType(PresentationSlideView.EnumScreenType.DISPLAY_MONITOR);
      this.refreshPreview();
      this.pack();
      this.setLocationRelativeTo(null);
      SwingUtils.maximize(this, 200);
   }

   private void initComponents() {
      this.jPanelLeft = new JPanel();
      this.jPanelLeftCenter = new JPanel();
      this.jScrollPaneQuickPresentation = new JScrollPane();
      this.jTextAreaQuickPresentation = new JTextArea();
      this.jPanelLeftFooter = new JPanel();
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
      this.setTitle(bundle.getString("msg.quickPresentation"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(800, 555));
      this.setPreferredSize(new Dimension(800, 555));
      this.jPanelLeft.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelLeft.setLayout(new BorderLayout(5, 0));
      this.jPanelLeftCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelLeftCenter.setLayout(new BorderLayout(0, 5));
      this.jTextAreaQuickPresentation.setColumns(20);
      this.jTextAreaQuickPresentation.setFont(new Font("Georgia", 0, 14));
      this.jTextAreaQuickPresentation.setRows(5);
      this.jScrollPaneQuickPresentation.setViewportView(this.jTextAreaQuickPresentation);
      this.jPanelLeftCenter.add(this.jScrollPaneQuickPresentation, "Center");
      this.jPanelLeft.add(this.jPanelLeftCenter, "Center");
      this.jPanelLeftFooter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelLeftFooter.setLayout(new BorderLayout(5, 0));
      this.jPanelLeftFooter.add(this.jSeparator, "First");
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
            QuickPresentationDialog.this.jComboBoxThemesItemStateChanged(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
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
      gridBagConstraints.gridy = 1;
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
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.jPanelSettingsLeft.add(this.jSpinnerSeconds, gridBagConstraints);
      this.jLabelSeconds.setFont(new Font("Georgia", 0, 14));
      this.jLabelSeconds.setText(bundle.getString("word.seconds"));
      this.jLabelSeconds.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
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
      this.jPanelLeftFooter.add(this.jPanelSettings, "Last");
      this.jPanelLeft.add(this.jPanelLeftFooter, "Last");
      this.getContentPane().add(this.jPanelLeft, "West");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jComboBoxThemesItemStateChanged(ItemEvent evt) {
      this.setTheme(this.jComboBoxThemes.getSelectedItem());
   }

   public static void main(MusicMain parent) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
            INSTANCE.removeAll();
         }

         INSTANCE = new QuickPresentationDialog(parent);
         INSTANCE.setVisible(true);
      });
   }

   private void setEnabledSubItemsAutomaticSlideShow(boolean flag) {
      Arrays.asList(this.jCheckBoxRepeat, this.jSpinnerSeconds, this.jLabelSeconds).forEach(c -> c.setEnabled(flag));
   }

   private void showPresentation() {
      List list = this.getPresentations();
      this.dispose();
      if (this.jCheckBoxAutomaticSlideShow.isSelected()) {
         AutomaticShowView.show(
            list,
            Settings.SET.getQuickPresentationTheme(),
            Integer.parseInt(this.jSpinnerSeconds.getValue().toString()),
            this.jCheckBoxRepeat.isSelected(),
            ScreenDeviceSettings.EnumDeviceType.TEXT
         );
      } else {
         GeneratePresentation.showPresentation(this.parent, Settings.SET.getQuickPresentationTheme(), list, 0, ScreenDeviceSettings.EnumDeviceType.TEXT);
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

   private void setTheme(Object item) {
      BooleanOptional.runIfTrue(item instanceof Theme, () -> {
         Settings.EnumSettings.QUICK_PRESENTATION_THEME.set(((Theme)item).id);
         this.multiSlidePreview.setTheme((Theme)item);
      });
   }

   private void refreshPreview() {
      this.multiSlidePreview.refreshPreview(this.getPresentations());
   }

   private List<Presentation> getPresentations() {
      String text = this.jTextAreaQuickPresentation.getText();
      text = text.replace("\n", "").isEmpty() ? text + " " : text;
      return Arrays.asList(text.split("\n\n"))
         .stream()
         .map(MusicUtils::createParagraph)
         .map(p -> Presentation.create(p.setSource("quick-presentation"), Settings.SET.getQuickPresentationTheme()))
         .collect(Collectors.toList());
   }
}
