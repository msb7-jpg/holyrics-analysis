package com.limagiran.holyrics.view;

import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.CountdownSettings;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ChangeEventDelay;
import com.limagiran.holyrics.swing.ComboBoxModelTheme;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.IScheduleTask;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.view.panel.HItemNextPresentationPanel;
import com.limagiran.holyrics.view.panel.MultiSlidePreview;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.DateEditor;

public class CountdownDialog extends JDialog {
   private static CountdownDialog INSTANCE;
   private final MultiSlidePreview multiSlide = new MultiSlidePreview();
   private final CountdownSettings set = Settings.SET.getCountdownSettings();
   private final HItemNextPresentationPanel nextPresentationPanel;
   private ButtonGroup buttonGroupType;
   private JButton jButtonShow;
   private JCheckBox jCheckBoxStartNow;
   private JComboBox<Theme> jComboBoxThemes;
   private JLabel jLabelCountdown;
   private JLabel jLabelPercent;
   private JLabel jLabelSeparator1;
   private JLabel jLabelSeparator2;
   private JLabel jLabelStart;
   private JLabel jLabelTimeExample;
   private JLabel jLabelZeroFill;
   private JPanel jPanel;
   private JPanel jPanelHeader;
   private JPanel jPanelLeft;
   private JPanel jPanelLeftBottom;
   private JPanel jPanelLeftCenter;
   private JPanel jPanelLeftCenterFooter;
   private JPanel jPanelLeftHeader;
   private JPanel jPanelLeftHeaderTypeSettings;
   private JPanel jPanelStartAt;
   private JPanel jPanelType;
   private JPanel jPanelTypeSetDetermine;
   private JPanel jPanelTypeSetExact;
   private JRadioButton jRadioButtonDetermine;
   private JRadioButton jRadioButtonExact;
   private JScrollPane jScrollPaneAfter;
   private JScrollPane jScrollPaneBefore;
   private JSeparator jSeparator1;
   private JSeparator jSeparator2;
   private JSpinner jSpinnerHourExact;
   private JSpinner jSpinnerMinDetermine;
   private JSpinner jSpinnerMinExact;
   private JSpinner jSpinnerSecDetermine;
   private JSpinner jSpinnerSize;
   private JSpinner jSpinnerStartTime;
   private JTextArea jTextAreaAfter;
   private JTextArea jTextAreaBefore;

   public CountdownDialog() {
      this.initComponents();
      this.nextPresentationPanel = HItemNextPresentationPanel.create(null);
      this.init();
   }

   private void init() {
      this.jPanelLeftCenterFooter.add(this.nextPresentationPanel, "Center");
      this.jTextAreaBefore.setText(this.set.getTextBefore(false));
      this.jTextAreaAfter.setText(this.set.getTextAfter(false));
      this.jSpinnerMinDetermine.setValue(this.set.getMinuteDetermine());
      this.jSpinnerSecDetermine.setValue(this.set.getSecondDetermine());
      this.jSpinnerHourExact.setValue(this.set.getHourExact());
      this.jSpinnerMinExact.setValue(this.set.getMinuteExact());
      this.jComboBoxThemes.setSelectedItem(this.set.getThemeObject());
      this.jPanel.add(this.multiSlide, "Center");
      this.multiSlide.setScreenType(PresentationSlideView.EnumScreenType.DISPLAY_MONITOR);
      this.jButtonShow.addActionListener(e -> this.showCountdown());
      this.jRadioButtonDetermine.addActionListener(this::radioChanged);
      this.jRadioButtonExact.addActionListener(this::radioChanged);
      SwingUtils.addHtmlShortcut(this.jButtonShow, "(Shift + Enter)");
      InsertShortcut.addEscToDispose(this);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, 64, this::showCountdown));
      this.jSpinnerMinDetermine.addChangeListener(new ChangeEventDelay(400L, () -> this.spinnerChange(0)));
      this.jSpinnerSecDetermine.addChangeListener(new ChangeEventDelay(400L, () -> this.spinnerChange(1)));
      this.jSpinnerHourExact.addChangeListener(new ChangeEventDelay(400L, () -> this.spinnerChange(2)));
      this.jSpinnerMinExact.addChangeListener(new ChangeEventDelay(400L, () -> this.spinnerChange(3)));
      this.jSpinnerSize.addChangeListener(new ChangeEventDelay(400L, () -> this.spinnerChange(4)));
      this.jLabelZeroFill.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            CountdownDialog.this.set.setZeroFill(!CountdownDialog.this.set.isZeroFill());
            CountdownDialog.this.set.save();
            CountdownDialog.this.jLabelZeroFill.repaint();
            CountdownDialog.this.refreshPreview();
         }
      });
      this.jTextAreaBefore.getDocument().addDocumentListener(new DocumentListenerAuto(500L, () -> {
         this.set.setTextBefore(this.jTextAreaBefore.getText());
         this.set.save();
         this.refreshPreview();
      }));
      this.jTextAreaAfter.getDocument().addDocumentListener(new DocumentListenerAuto(500L, () -> {
         this.set.setTextAfter(this.jTextAreaAfter.getText());
         this.set.save();
         this.refreshPreview();
      }));
      this.jCheckBoxStartNow.addItemListener(evt -> this.jSpinnerStartTime.setEnabled(evt.getStateChange() == 2));
      SwingUtils.setupCCPUAndRedo(this.jTextAreaBefore);
      SwingUtils.setupCCPUAndRedo(this.jTextAreaAfter);
      this.updateCardLayout();
      this.refreshPreview();
      this.pack();
      this.setLocationRelativeTo(null);
      SwingUtils.maximize(this, 200);
   }

   private void initComponents() {
      this.buttonGroupType = new ButtonGroup();
      this.jPanel = new JPanel();
      this.jPanelHeader = new JPanel();
      this.jLabelCountdown = new JLabel();
      this.jPanelLeft = new JPanel();
      this.jPanelLeftCenter = new JPanel();
      this.jPanelLeftHeader = new JPanel();
      this.jPanelType = new JPanel();
      this.jRadioButtonDetermine = new JRadioButton();
      this.jRadioButtonExact = new JRadioButton();
      this.jPanelLeftHeaderTypeSettings = new JPanel();
      this.jPanelTypeSetDetermine = new JPanel();
      this.jSpinnerMinDetermine = this.createDefaultSpinner();
      this.jLabelSeparator1 = new JLabel();
      this.jSpinnerSecDetermine = this.createDefaultSpinner();
      this.jPanelTypeSetExact = new JPanel();
      this.jSpinnerHourExact = this.createDefaultSpinner();
      this.jLabelSeparator2 = new JLabel();
      this.jSpinnerMinExact = this.createDefaultSpinner();
      this.jSeparator1 = new JSeparator();
      this.jSeparator2 = new JSeparator();
      this.jScrollPaneBefore = new JScrollPane();
      this.jTextAreaBefore = SwingUtils.createJTextAreaPlaceHolder(I18N.MSG_TEXT_BEFORE.val() + "...");
      this.jLabelTimeExample = new JLabel();
      this.jSpinnerSize = new JSpinner();
      this.jLabelPercent = new JLabel();
      this.jScrollPaneAfter = new JScrollPane();
      this.jTextAreaAfter = SwingUtils.createJTextAreaPlaceHolder(I18N.MSG_TEXT_AFTER.val() + "...");
      this.jLabelZeroFill = new JLabel() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!CountdownDialog.this.set.isZeroFill()) {
               Graphics2D g2 = (Graphics2D)g;
               G2Utils.setupGraphics(g2);
               Dimension d = this.getSize();
               g2.setColor(Color.RED);
               g2.drawLine(0, 0, d.width - 1, d.height - 1);
               g2.drawLine(d.width - 1, 0, 0, d.height - 1);
            }
         }
      };
      this.jPanelLeftCenterFooter = new JPanel();
      this.jPanelStartAt = new JPanel();
      this.jLabelStart = new JLabel();
      this.jCheckBoxStartNow = new JCheckBox();
      this.jSpinnerStartTime = new JSpinner();
      this.jPanelLeftBottom = new JPanel();
      this.jComboBoxThemes = new JComboBox<>();
      this.jButtonShow = new JButton();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.countdown"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setModal(true);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            CountdownDialog.this.formWindowClosing(evt);
         }
      });
      this.jPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      this.jPanel.setLayout(new BorderLayout());
      this.jPanelHeader.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelHeader.setLayout(new FlowLayout(1, 5, 4));
      this.jLabelCountdown.setFont(new Font("Arial", 1, 36));
      this.jLabelCountdown.setText(bundle.getString("word.countdown"));
      this.jPanelHeader.add(this.jLabelCountdown);
      this.jPanel.add(this.jPanelHeader, "First");
      this.jPanelLeft.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelLeft.setLayout(new BorderLayout());
      this.jPanelLeftCenter.setLayout(new GridBagLayout());
      this.jPanelLeftHeader.setLayout(new BorderLayout(0, 4));
      this.jPanelType.setLayout(new FlowLayout(1, 10, 0));
      this.buttonGroupType.add(this.jRadioButtonDetermine);
      this.jRadioButtonDetermine.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonDetermine.setSelected(this.set.isDetermine());
      this.jRadioButtonDetermine.setText(bundle.getString("msg.determineTime"));
      this.jRadioButtonDetermine.setFocusable(false);
      this.jPanelType.add(this.jRadioButtonDetermine);
      this.buttonGroupType.add(this.jRadioButtonExact);
      this.jRadioButtonExact.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonExact.setSelected(!this.set.isDetermine());
      this.jRadioButtonExact.setText(bundle.getString("msg.exactTime"));
      this.jRadioButtonExact.setFocusable(false);
      this.jPanelType.add(this.jRadioButtonExact);
      this.jPanelLeftHeader.add(this.jPanelType, "First");
      this.jPanelLeftHeaderTypeSettings.setLayout(new CardLayout());
      this.jPanelTypeSetDetermine.setLayout(new FlowLayout(1, 5, 3));
      this.jSpinnerMinDetermine.setFont(new Font("Courier New", 0, 14));
      this.jSpinnerMinDetermine.setModel(new SpinnerNumberModel(5, 0, 59, 1));
      this.jPanelTypeSetDetermine.add(this.jSpinnerMinDetermine);
      this.jLabelSeparator1.setFont(new Font("Courier New", 0, 14));
      this.jLabelSeparator1.setText(":");
      this.jPanelTypeSetDetermine.add(this.jLabelSeparator1);
      this.jSpinnerSecDetermine.setFont(new Font("Courier New", 0, 14));
      this.jSpinnerSecDetermine.setModel(new SpinnerNumberModel(0, 0, 59, 5));
      this.jPanelTypeSetDetermine.add(this.jSpinnerSecDetermine);
      this.jPanelLeftHeaderTypeSettings.add(this.jPanelTypeSetDetermine, "determine");
      this.jPanelTypeSetExact.setLayout(new FlowLayout(1, 5, 3));
      this.jSpinnerHourExact.setFont(new Font("Courier New", 0, 14));
      this.jSpinnerHourExact.setModel(new SpinnerNumberModel(19, 0, 23, 1));
      this.jPanelTypeSetExact.add(this.jSpinnerHourExact);
      this.jLabelSeparator2.setFont(new Font("Courier New", 0, 14));
      this.jLabelSeparator2.setText(":");
      this.jPanelTypeSetExact.add(this.jLabelSeparator2);
      this.jSpinnerMinExact.setFont(new Font("Courier New", 0, 14));
      this.jSpinnerMinExact.setModel(new SpinnerNumberModel(0, 0, 59, 5));
      this.jPanelTypeSetExact.add(this.jSpinnerMinExact);
      this.jPanelLeftHeaderTypeSettings.add(this.jPanelTypeSetExact, "exact");
      this.jPanelLeftHeader.add(this.jPanelLeftHeaderTypeSettings, "Last");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 0;
      this.jPanelLeftCenter.add(this.jPanelLeftHeader, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 12, 0);
      this.jPanelLeftCenter.add(this.jSeparator1, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(12, 0, 0, 0);
      this.jPanelLeftCenter.add(this.jSeparator2, gridBagConstraints);
      this.jTextAreaBefore.setColumns(20);
      this.jTextAreaBefore.setDocument(SwingUtils.createDocMaxSize(256));
      this.jTextAreaBefore.setFont(new Font("Arial", 0, 14));
      this.jTextAreaBefore.setLineWrap(true);
      this.jTextAreaBefore.setRows(3);
      this.jTextAreaBefore.setWrapStyleWord(true);
      this.jScrollPaneBefore.setViewportView(this.jTextAreaBefore);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelLeftCenter.add(this.jScrollPaneBefore, gridBagConstraints);
      this.jLabelTimeExample.setFont(new Font("Arial", 0, 16));
      this.jLabelTimeExample.setText("<html>M:SS");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelLeftCenter.add(this.jLabelTimeExample, gridBagConstraints);
      this.jSpinnerSize.setFont(new Font("Arial", 0, 16));
      this.jSpinnerSize.setModel(new SpinnerNumberModel(250, 100, 800, 50));
      this.jSpinnerSize.setValue(this.set.getSize());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.insets = new Insets(4, 4, 4, 0);
      this.jPanelLeftCenter.add(this.jSpinnerSize, gridBagConstraints);
      this.jLabelPercent.setFont(new Font("Arial", 0, 16));
      this.jLabelPercent.setHorizontalAlignment(2);
      this.jLabelPercent.setText("%");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelLeftCenter.add(this.jLabelPercent, gridBagConstraints);
      this.jTextAreaAfter.setColumns(20);
      this.jTextAreaAfter.setDocument(SwingUtils.createDocMaxSize(256));
      this.jTextAreaAfter.setFont(new Font("Arial", 0, 14));
      this.jTextAreaAfter.setRows(3);
      this.jScrollPaneAfter.setViewportView(this.jTextAreaAfter);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelLeftCenter.add(this.jScrollPaneAfter, gridBagConstraints);
      this.jLabelZeroFill.setFont(new Font("Arial", 0, 16));
      this.jLabelZeroFill.setHorizontalAlignment(0);
      this.jLabelZeroFill.setText("<html><u>0</u>");
      this.jLabelZeroFill.setCursor(new Cursor(12));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      this.jPanelLeftCenter.add(this.jLabelZeroFill, gridBagConstraints);
      this.jPanelLeftCenterFooter.setLayout(new BorderLayout(8, 0));
      this.jPanelStartAt.setBorder(BorderFactory.createEmptyBorder(48, 4, 4, 4));
      this.jPanelStartAt.setOpaque(false);
      this.jPanelStartAt.setLayout(new FlowLayout(1, 12, 5));
      this.jLabelStart.setFont(new Font("Georgia", 0, 14));
      this.jLabelStart.setText(I18N.WORD_START.val() + ": ");
      this.jPanelStartAt.add(this.jLabelStart);
      this.jCheckBoxStartNow.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxStartNow.setText(bundle.getString("word.now"));
      this.jCheckBoxStartNow.setFocusable(false);
      this.jPanelStartAt.add(this.jCheckBoxStartNow);
      this.jSpinnerStartTime.setFont(new Font("Courier New", 0, 14));
      this.jSpinnerStartTime.setModel(new SpinnerDateModel(new Date(), null, null, 12));
      this.jSpinnerStartTime.setEditor(new DateEditor(this.jSpinnerStartTime, "HH:mm"));
      this.jSpinnerStartTime.setEnabled(false);
      this.jSpinnerStartTime.setFocusable(false);
      this.jPanelStartAt.add(this.jSpinnerStartTime);
      this.jPanelLeftCenterFooter.add(this.jPanelStartAt, "Last");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(12, 0, 0, 0);
      this.jPanelLeftCenter.add(this.jPanelLeftCenterFooter, gridBagConstraints);
      this.jPanelLeft.add(this.jPanelLeftCenter, "Center");
      this.jPanelLeftBottom.setLayout(new FlowLayout(1, 25, 5));
      this.jComboBoxThemes.setMaximumRowCount(5);
      this.jComboBoxThemes.setModel(new ComboBoxModelTheme());
      this.jComboBoxThemes.setFocusable(false);
      this.jComboBoxThemes.setPreferredSize(new Dimension(100, 51));
      this.jComboBoxThemes.setRenderer(new ComboBoxRendererTheme(80, 45));
      this.jComboBoxThemes.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            CountdownDialog.this.jComboBoxThemesItemStateChanged(evt);
         }
      });
      this.jPanelLeftBottom.add(this.jComboBoxThemes);
      this.jButtonShow.setFont(new Font("Georgia", 0, 14));
      this.jButtonShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack24.png")));
      this.jButtonShow.setText(bundle.getString("word.show"));
      this.jButtonShow.setFocusable(false);
      this.jPanelLeftBottom.add(this.jButtonShow);
      this.jPanelLeft.add(this.jPanelLeftBottom, "Last");
      this.jPanel.add(this.jPanelLeft, "West");
      this.getContentPane().add(this.jPanel, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jComboBoxThemesItemStateChanged(ItemEvent evt) {
      this.setTheme(this.jComboBoxThemes.getSelectedItem());
   }

   private void formWindowClosing(WindowEvent evt) {
      this.dispose();
   }

   public static void main() {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new CountdownDialog();
         }

         INSTANCE.setVisible(true);
      });
   }

   private WebSpinner createDefaultSpinner() {
      WebSpinner ws = new WebSpinner();
      ws.setFocusable(false);
      ws.setDrawFocus(false);
      return ws;
   }

   private void showCountdown() {
      this.setVisible(false);
      this.dispose();
      if (!this.jCheckBoxStartNow.isSelected()) {
         scheduleCountdown(this.set, this.nextPresentationPanel, this.jSpinnerStartTime.getValue());
      } else {
         GeneratePresentation.showCountdown(this.set, this.nextPresentationPanel.getSelectedItem());
      }
   }

   @Override
   public void setVisible(boolean visible) {
      if (visible) {
         ((ComboBoxModelTheme)this.jComboBoxThemes.getModel()).update();
         this.nextPresentationPanel.setSelectedItem(null);
         this.nextPresentationPanel.update();
         this.jCheckBoxStartNow.setSelected(true);
         this.jSpinnerStartTime.setValue(new Date());
      }

      super.setVisible(visible);
   }

   @Override
   public void dispose() {
      Arrays.asList(this.jSpinnerMinDetermine, this.jSpinnerSecDetermine, this.jSpinnerHourExact, this.jSpinnerMinExact, this.jSpinnerStartTime)
         .forEach(SwingUtils::commitEdit);
      super.dispose();
   }

   private void refreshPreview() {
      this.multiSlide.refreshPreview(this.getPresentations());
   }

   private List<Presentation> getPresentations() {
      return new ArrayList<>(Arrays.asList(this.set.getPresentation()));
   }

   private void setTheme(Object item) {
      BooleanOptional.runIfTrue(item instanceof Theme, () -> {
         this.set.setTheme(((Theme)item).id);
         this.set.save();
         this.multiSlide.setTheme((Theme)item);
      });
   }

   private void spinnerChange(int index) {
      try {
         switch (index) {
            case 0:
               this.set.setMinuteDetermine(Integer.parseInt(this.jSpinnerMinDetermine.getValue().toString()));
               break;
            case 1:
               this.set.setSecondDetermine(Integer.parseInt(this.jSpinnerSecDetermine.getValue().toString()));
               break;
            case 2:
               this.set.setHourExact(Integer.parseInt(this.jSpinnerHourExact.getValue().toString()));
               break;
            case 3:
               this.set.setMinuteExact(Integer.parseInt(this.jSpinnerMinExact.getValue().toString()));
               break;
            case 4:
               this.set.setSize(Integer.parseInt(this.jSpinnerSize.getValue().toString()));
         }
      } catch (Exception var3) {
      }

      this.set.save();
      this.refreshPreview();
   }

   private void radioChanged(ActionEvent e) {
      if (e.getSource() == this.jRadioButtonDetermine) {
         this.set.setDetermine(this.jRadioButtonDetermine.isSelected());
      } else {
         this.set.setDetermine(!this.jRadioButtonExact.isSelected());
      }

      this.refreshPreview();
      this.updateCardLayout();
   }

   private void updateCardLayout() {
      ((CardLayout)this.jPanelLeftHeaderTypeSettings.getLayout()).show(this.jPanelLeftHeaderTypeSettings, this.set.isDetermine() ? "determine" : "exact");
   }

   public static void scheduleCountdown(final CountdownSettings set, final Object nextPresentation, Object startAt) {
      IScheduleTask schedule = new IScheduleTask() {
         @Override
         public String getTitle() {
            return I18N.WORD_COUNTDOWN.val();
         }

         @Override
         public ImageIcon getIcon() {
            return new ImageIcon(Images.load("timer24.png"));
         }

         @Override
         public void run() {
            GeneratePresentation.showCountdown(set, nextPresentation);
         }
      };
      IScheduleTask.scheduleTask(schedule, startAt);
   }
}
