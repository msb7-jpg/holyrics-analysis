package com.limagiran.holyrics.view;

import com.alee.extended.window.PopOverDirection;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextFieldStyle;
import com.alee.managers.hotkey.HotkeyData;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Alert;
import com.limagiran.holyrics.model.AlertSettings;
import com.limagiran.holyrics.model.CountdownSettings;
import com.limagiran.holyrics.model.theme.BackgroundColor;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.AlertPanelAnimatedDefault;
import com.limagiran.holyrics.swing.ComboBoxModelTheme;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.swing.CountdownValueInputDialog;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.CountdownUtils;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.view.panel.AlertListPanel;
import com.limagiran.holyrics.view.panel.AppMessagePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.text.Document;

public class CommunicationPanel extends JFrame implements AppMessagePanel.AppMessagePanelListener {
   private static CommunicationPanel INSTANCE;
   public static final int TAB_COMMUNICATION = 0;
   public static final int TAB_CHAT = 1;
   public static final int TAB_MIN = 0;
   public static final int TAB_MAX = 1;
   private static transient volatile boolean alertShow = false;
   private static transient volatile boolean presentationShow = false;
   private static transient volatile boolean displayClockBG = Settings.EnumSettings2.ADMIN_PANEL_SHOW_CLOCK_BG.is();
   private static transient volatile boolean displayClockAlert = Settings.EnumSettings2.ADMIN_PANEL_SHOW_CLOCK_ALERT.is();
   private static transient volatile boolean displayCountdown = false;
   private static transient volatile long countdownValue = Long.MIN_VALUE;
   private static transient volatile CountdownSettings countdownSet = Settings.SET.getCountdownSettings2();
   private static transient volatile long alertHash = 0L;
   private static final AlertPanelAnimatedDefault ALERT_PANEL = new AlertPanelAnimatedDefault() {
      private final Dimension size = new Dimension();

      {
         this.setVisible(false);
      }

      @Override
      public void fireChange() {
      }

      @Override
      public boolean isShow() {
         return CommunicationPanel.isDisplayAlert();
      }

      @Override
      public boolean isStageView() {
         return true;
      }

      @Override
      public boolean stopAnimation() {
         return false;
      }

      @Override
      public int skipFrames() {
         return 96;
      }

      @Override
      public String getContent() {
         return CommunicationPanel.getAlertTextToDisplay();
      }

      @Override
      public void updated() {
         CommunicationPanel.alertHash++;
      }

      @Override
      public double getVelocity() {
         return super.getVelocity() * 2.0;
      }

      @Override
      protected void paintComponent(Graphics g) {
      }

      @Override
      public void setSize(int width, int height) {
         this.size.setSize(width, height);
      }

      @Override
      public Dimension getSize() {
         return this.size;
      }

      @Override
      public AlertSettings getAs() {
         return Settings.SET.getAlertSettings2();
      }
   };
   private PresentationSlideView slidePreview;
   private final MusicController.Paragraph paragraph = new MusicController.Paragraph();
   private final AppMessagePanel appMessagePanel = new AppMessagePanel(this);
   private Filler filler1;
   private JButton jButtonAlertList;
   private JButton jButtonAlertSettings;
   private JButton jButtonCountdown;
   private JButton jButtonReadyMessagesAlert;
   private JButton jButtonReadyMessagesPresentation;
   private JCheckBox jCheckBoxDisplayAhead;
   private JCheckBox jCheckBoxDisplayClockAsBackground;
   private JCheckBox jCheckBoxDisplayClockOnAlert;
   private JComboBox<Theme> jComboBoxTheme;
   private JLabel jLabelAlert;
   private JLabel jLabelCountdown;
   private JLabel jLabelHelp;
   private JLabel jLabelHelpFiller;
   private JLabel jLabelSlidePreview;
   private JLabel jLabelTitle;
   private JPanel jPanelAlert;
   private JPanel jPanelAlertText;
   private JPanel jPanelAlertTextButtons;
   private JPanel jPanelCenter;
   private JPanel jPanelClock;
   private JPanel jPanelCommunicationPanel;
   private JPanel jPanelHeader;
   private JPanel jPanelPresentationCenter;
   private JPanel jPanelPresentationLeft;
   private JPanel jPanelPresentationSettings;
   private JPanel jPanelQuickPresentation;
   private JSeparator jSeparator1;
   private JTabbedPane jTabbedPane;
   private JTextArea jTextAreaPresentation;
   private JTextField jTextFieldAlert;
   private JToggleButton jToggleButtonAlert;
   private JToggleButton jToggleButtonPresentation;
   private static final Theme COUNTDOWN_THEME = Theme.create()
      .setBackground(new BackgroundColor(Color.BLACK))
      .setFontColor(Color.WHITE)
      .setFont(new Font("Arial", 0, 8))
      .setSize(0.2)
      .setLineSpacing(0.3);

   public CommunicationPanel() {
      this.initComponents();
      this.init();
   }

   private void init() {
      this.paragraph.setHackBreakLine(!MyProperties.EnumProperties.COMMUNICATION_PANEL_DISABLE_BREAK_LINE.is());
      this.slidePreview = (PresentationSlideView)this.jLabelSlidePreview;
      Arrays.asList(this.jButtonAlertList, this.jButtonReadyMessagesAlert, this.jButtonReadyMessagesPresentation)
         .forEach(b -> ((WebButton)b).setUndecorated(true).setFocusable(false));
      ((WebTabbedPane)this.jTabbedPane).setTabInsets(new Insets(5, 12, 7, 12));
      this.jTabbedPane.addTab("Chat", this.appMessagePanel);
      this.jTabbedPane.addChangeListener(e -> {
         if (this.jTabbedPane.getSelectedIndex() == 1) {
            this.appMessagePanel.requestFocusOnChat();
         }
      });
      this.jTextFieldAlert.setText(Settings.EnumSettings2.ADMIN_PANEL_ALERT_TEXT.getString());
      this.jTextAreaPresentation.setText(Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_TEXT.getString());
      this.jCheckBoxDisplayAhead.setSelected(Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_DISPLAY_AHEAD.is());
      this.jToggleButtonAlert.setSelected(alertShow);
      this.jToggleButtonPresentation.setSelected(presentationShow);
      this.jCheckBoxDisplayClockAsBackground.setSelected(displayClockBG);
      this.jCheckBoxDisplayClockOnAlert.setSelected(displayClockAlert);
      this.jButtonCountdown.setSelected(displayCountdown);
      ((WebComboBox)this.jComboBoxTheme).setShadeWidth(0);
      Arrays.asList(this.jToggleButtonAlert, this.jToggleButtonPresentation)
         .forEach(tb -> ((WebToggleButton)tb).setBottomSelectedBgColor(new Color(0, 255, 0)).setTopSelectedBgColor(new Color(0, 200, 0)));
      ((WebButton)this.jButtonCountdown).setBottomSelectedBgColor(new Color(0, 255, 0));
      ((WebButton)this.jButtonCountdown).setTopSelectedBgColor(new Color(0, 200, 0));
      this.jToggleButtonAlert.addItemListener(evt -> this.alert(this.jToggleButtonAlert.isSelected()));
      this.jTextFieldAlert.getDocument().addDocumentListener(new DocumentListenerAuto(800L, () -> {
         String var10000 = Settings.EnumSettings2.ADMIN_PANEL_ALERT_TEXT.set(this.jTextFieldAlert.getText());
      }));
      this.jToggleButtonPresentation.addItemListener(evt -> this.presentation(this.jToggleButtonPresentation.isSelected()));
      this.jTextAreaPresentation.getDocument().addDocumentListener(new DocumentListenerAuto(800L, () -> {
         Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_TEXT.set(this.jTextAreaPresentation.getText());
         this.updatePresentation();
      }));
      this.jCheckBoxDisplayAhead.addItemListener(evt -> {
         Boolean var10000 = Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_DISPLAY_AHEAD.set(this.jCheckBoxDisplayAhead.isSelected());
      });
      this.jCheckBoxDisplayClockAsBackground.addItemListener(evt -> {
         displayClockBG = Settings.EnumSettings2.ADMIN_PANEL_SHOW_CLOCK_BG.set(this.jCheckBoxDisplayClockAsBackground.isSelected());
         WallpaperView.update();
      });
      this.jCheckBoxDisplayClockOnAlert.addItemListener(evt -> {
         displayClockAlert = Settings.EnumSettings2.ADMIN_PANEL_SHOW_CLOCK_ALERT.set(this.jCheckBoxDisplayClockOnAlert.isSelected());
         alertHash++;
      });
      this.jComboBoxTheme.addItemListener(etv -> this.themeChanged());
      this.jLabelHelp.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            PopupUtils.helpCommunicationPanel(null);
         }
      });
      this.jButtonCountdown.addActionListener(evt -> this.countdown());
      this.jButtonAlertSettings.addActionListener(evt -> this.alertSettings());
      this.jButtonAlertList.addActionListener(evt -> this.showAlertList());
      this.jButtonReadyMessagesAlert.addActionListener(evt -> this.readyMessages(true));
      this.jButtonReadyMessagesPresentation.addActionListener(evt -> this.readyMessages(false));
      SwingUtils.addHtmlShortcut(this.jToggleButtonPresentation, "(Shift + Enter)");
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, 64, () -> {
         if (this.isCommunicationPanelTabSelected()) {
            this.jToggleButtonPresentation.doClick(0);
         }
      }));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(87, 128, () -> {
         if (this.isCommunicationPanelTabSelected()) {
            this.jToggleButtonAlert.doClick(0);
         }
      }));
      InsertShortcut.addEscToDispose(this);
      WebLafUtils.addToolTipButton(this, this.jButtonCountdown, new HotkeyData(true, false, true, 82));
      KeyAdapter keyAdapter = new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            if (e.getKeyCode() == 72 && e.isControlDown()) {
               CommunicationPanel.this.readyMessages(e.getSource() == CommunicationPanel.this.jTextFieldAlert);
            }
         }
      };
      this.jTextFieldAlert.addKeyListener(keyAdapter);
      this.jTextAreaPresentation.addKeyListener(keyAdapter);
   }

   private void initComponents() {
      this.jTabbedPane = new WebTabbedPane();
      this.jPanelCommunicationPanel = new JPanel();
      this.jPanelHeader = new JPanel();
      this.jLabelHelpFiller = new JLabel() {
         @Override
         protected void paintComponent(Graphics g) {
         }
      };
      this.jLabelTitle = new JLabel();
      this.jLabelHelp = new JLabel();
      this.jPanelCenter = new JPanel();
      this.jPanelQuickPresentation = new JPanel();
      this.jPanelPresentationLeft = new JPanel();
      this.jTextAreaPresentation = new JTextArea();
      this.jButtonReadyMessagesPresentation = new WebButton();
      this.jPanelPresentationCenter = new JPanel();
      this.jLabelSlidePreview = this.createSlidePreview();
      this.jPanelPresentationSettings = new JPanel();
      this.jToggleButtonPresentation = new WebToggleButton().setMargin(2, 4, 2, 4);
      this.filler1 = new Filler(new Dimension(48, 0), new Dimension(48, 0), new Dimension(48, 32767));
      this.jCheckBoxDisplayAhead = new JCheckBox();
      this.jComboBoxTheme = new WebComboBox();
      this.jPanelAlert = new JPanel();
      this.jLabelAlert = new JLabel();
      this.jPanelAlertText = new JPanel();
      this.jTextFieldAlert = new JTextField();
      this.jPanelAlertTextButtons = new JPanel();
      this.jButtonAlertList = new WebButton();
      this.jButtonReadyMessagesAlert = new WebButton();
      this.jToggleButtonAlert = new WebToggleButton();
      this.jButtonAlertSettings = new JButton();
      this.jPanelClock = new JPanel();
      this.jCheckBoxDisplayClockAsBackground = new JCheckBox();
      this.jCheckBoxDisplayClockOnAlert = new JCheckBox();
      this.jSeparator1 = new JSeparator();
      this.jButtonCountdown = new WebButton();
      this.jLabelCountdown = new JLabel();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("msg.communicationPanel"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(750, 600));
      this.setPreferredSize(new Dimension(750, 600));
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            CommunicationPanel.this.formWindowClosing(evt);
         }
      });
      this.jTabbedPane.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
      this.jTabbedPane.setFocusable(false);
      this.jTabbedPane.setFont(new Font("Arial", 0, 14));
      this.jPanelCommunicationPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
      this.jPanelCommunicationPanel.setLayout(new BorderLayout(0, 4));
      this.jPanelHeader.setOpaque(false);
      this.jPanelHeader.setLayout(new BorderLayout(4, 0));
      this.jLabelHelpFiller.setFont(new Font("Arial Black", 1, 18));
      this.jLabelHelpFiller.setForeground(new Color(92, 92, 92));
      this.jLabelHelpFiller.setText("?");
      this.jLabelHelpFiller.setVerticalAlignment(1);
      this.jLabelHelpFiller.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelHeader.add(this.jLabelHelpFiller, "West");
      this.jLabelTitle.setFont(new Font("Arial", 0, 12));
      this.jLabelTitle.setHorizontalAlignment(0);
      this.jLabelTitle.setText(bundle.getString("msg.communicationPanelTitleAndInfo"));
      this.jPanelHeader.add(this.jLabelTitle, "Center");
      this.jLabelTitle.getAccessibleContext().setAccessibleName("jLabelTitle");
      this.jLabelHelp.setFont(new Font("Arial Black", 1, 18));
      this.jLabelHelp.setForeground(new Color(92, 92, 92));
      this.jLabelHelp.setText("?");
      this.jLabelHelp.setVerticalAlignment(1);
      this.jLabelHelp.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jLabelHelp.setCursor(new Cursor(12));
      this.jPanelHeader.add(this.jLabelHelp, "After");
      this.jPanelCommunicationPanel.add(this.jPanelHeader, "First");
      this.jPanelCenter.setLayout(new GridBagLayout());
      this.jPanelQuickPresentation.setBackground(new Color(220, 220, 220));
      this.jPanelQuickPresentation
         .setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(154, 154, 154)), BorderFactory.createEmptyBorder(8, 12, 8, 12)));
      this.jPanelQuickPresentation.setLayout(new BorderLayout(16, 8));
      this.jPanelPresentationLeft.setOpaque(false);
      this.jPanelPresentationLeft.setLayout(new BorderLayout());
      this.jTextAreaPresentation.setColumns(24);
      this.jTextAreaPresentation.setFont(new Font("Arial", 0, 14));
      this.jTextAreaPresentation.setLineWrap(true);
      this.jTextAreaPresentation.setRows(5);
      this.jTextAreaPresentation.setWrapStyleWord(true);
      this.jTextAreaPresentation
         .setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)), BorderFactory.createEmptyBorder(3, 4, 3, 4)));
      this.jPanelPresentationLeft.add(this.jTextAreaPresentation, "Center");
      this.jButtonReadyMessagesPresentation.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/checkedText16.png")));
      this.jButtonReadyMessagesPresentation.setToolTipText(I18N.MSG_READY_MESSAGES.val() + " (Ctrl+H)");
      this.jPanelPresentationLeft.add(this.jButtonReadyMessagesPresentation, "Last");
      this.jPanelQuickPresentation.add(this.jPanelPresentationLeft, "West");
      this.jPanelPresentationCenter.setOpaque(false);
      this.jPanelPresentationCenter.setLayout(new BorderLayout());
      this.jPanelPresentationCenter.add(this.jLabelSlidePreview, "Center");
      this.jPanelQuickPresentation.add(this.jPanelPresentationCenter, "Center");
      this.jPanelPresentationSettings.setOpaque(false);
      this.jPanelPresentationSettings.setLayout(new FlowLayout(1, 16, 2));
      this.jToggleButtonPresentation.setFont(new Font("Georgia", 0, 14));
      this.jToggleButtonPresentation.setText(bundle.getString("word.show"));
      this.jToggleButtonPresentation.setFocusable(false);
      this.jPanelPresentationSettings.add(this.jToggleButtonPresentation);
      this.jPanelPresentationSettings.add(this.filler1);
      this.jCheckBoxDisplayAhead.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxDisplayAhead.setText(bundle.getString("msg.displayAhead"));
      this.jCheckBoxDisplayAhead.setFocusable(false);
      this.jCheckBoxDisplayAhead.setOpaque(false);
      this.jPanelPresentationSettings.add(this.jCheckBoxDisplayAhead);
      this.jComboBoxTheme.setModel(new ComboBoxModelTheme());
      this.jComboBoxTheme.setFocusable(false);
      this.jComboBoxTheme.setRenderer(new ComboBoxRendererTheme(80, 45));
      this.jPanelPresentationSettings.add(this.jComboBoxTheme);
      this.jPanelQuickPresentation.add(this.jPanelPresentationSettings, "Last");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(8, 0, 6, 0);
      this.jPanelCenter.add(this.jPanelQuickPresentation, gridBagConstraints);
      this.jPanelAlert.setBackground(new Color(220, 220, 220));
      this.jPanelAlert.setBorder(BorderFactory.createLineBorder(new Color(154, 154, 154)));
      this.jPanelAlert.setLayout(new FlowLayout(1, 6, 6));
      this.jLabelAlert.setFont(new Font("Georgia", 0, 14));
      this.jLabelAlert.setText(bundle.getString("word.alert"));
      this.jPanelAlert.add(this.jLabelAlert);
      this.jPanelAlertText.setOpaque(false);
      this.jPanelAlertText.setLayout(new BorderLayout(2, 0));
      this.jTextFieldAlert.setColumns(32);
      this.jTextFieldAlert.setFont(new Font("Arial", 0, 14));
      this.jPanelAlertText.add(this.jTextFieldAlert, "Center");
      this.jPanelAlertTextButtons.setOpaque(false);
      this.jPanelAlertTextButtons.setLayout(new GridLayout(1, 2, 2, 0));
      this.jButtonAlertList.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/list16.png")));
      this.jPanelAlertTextButtons.add(this.jButtonAlertList);
      this.jButtonReadyMessagesAlert.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/checkedText16.png")));
      this.jButtonReadyMessagesAlert.setToolTipText(I18N.MSG_READY_MESSAGES.val() + " (Ctrl+H)");
      this.jPanelAlertTextButtons.add(this.jButtonReadyMessagesAlert);
      this.jPanelAlertText.add(this.jPanelAlertTextButtons, "East");
      this.jPanelAlert.add(this.jPanelAlertText);
      this.jToggleButtonAlert.setFont(new Font("Georgia", 0, 14));
      this.jToggleButtonAlert.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/checkBlack16.png")));
      this.jToggleButtonAlert.setText("<html><span style='font-size:80%'><code>(Ctrl + W)</code></span>");
      this.jToggleButtonAlert.setFocusable(false);
      this.jPanelAlert.add(this.jToggleButtonAlert);
      this.jButtonAlertSettings.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/settings16.png")));
      this.jButtonAlertSettings.setFocusable(false);
      this.jPanelAlert.add(this.jButtonAlertSettings);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(6, 0, 8, 0);
      this.jPanelCenter.add(this.jPanelAlert, gridBagConstraints);
      this.jPanelClock.setBackground(new Color(220, 220, 220));
      this.jPanelClock.setBorder(BorderFactory.createLineBorder(new Color(154, 154, 154)));
      this.jPanelClock.setLayout(new FlowLayout(1, 8, 6));
      this.jCheckBoxDisplayClockAsBackground.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxDisplayClockAsBackground.setText(bundle.getString("msg.displayClockAsBackground"));
      this.jCheckBoxDisplayClockAsBackground.setFocusable(false);
      this.jCheckBoxDisplayClockAsBackground.setOpaque(false);
      this.jPanelClock.add(this.jCheckBoxDisplayClockAsBackground);
      this.jCheckBoxDisplayClockOnAlert.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxDisplayClockOnAlert.setText(bundle.getString("msg.displayClockOnAlert"));
      this.jCheckBoxDisplayClockOnAlert.setFocusable(false);
      this.jCheckBoxDisplayClockOnAlert.setOpaque(false);
      this.jPanelClock.add(this.jCheckBoxDisplayClockOnAlert);
      this.jSeparator1.setOrientation(1);
      this.jSeparator1.setMinimumSize(new Dimension(0, 16));
      this.jSeparator1.setPreferredSize(new Dimension(12, 16));
      this.jPanelClock.add(this.jSeparator1);
      this.jButtonCountdown.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/timer16.png")));
      this.jButtonCountdown.setToolTipText(bundle.getString("word.countdown"));
      this.jButtonCountdown.setFocusable(false);
      this.jPanelClock.add(this.jButtonCountdown);
      this.jLabelCountdown.setFont(new Font("Courier New", 1, 12));
      this.jPanelClock.add(this.jLabelCountdown);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 0, 0);
      this.jPanelCenter.add(this.jPanelClock, gridBagConstraints);
      this.jPanelCommunicationPanel.add(this.jPanelCenter, "Center");
      this.jTabbedPane.addTab(bundle.getString("word.communication"), this.jPanelCommunicationPanel);
      this.getContentPane().add(this.jTabbedPane, "Center");
      this.jTabbedPane.getAccessibleContext().setAccessibleName("communication-panel");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.close();
   }

   public static void open(int tab, Runnable callback) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new CommunicationPanel();
         }

         if (INSTANCE.isVisible()) {
            if (INSTANCE.getExtendedState() == 1) {
               INSTANCE.setExtendedState(0);
            }

            INSTANCE.toFront();
            INSTANCE.requestFocus();
         } else {
            INSTANCE.setVisible(true);
         }

         INSTANCE.setTab(tab);
         INSTANCE.appMessagePanel.requestFocusOnChat();
         Optional.ofNullable(callback).ifPresent(Runnable::run);
      });
   }

   public static void open(int tab) {
      open(tab, null);
   }

   public static void open() {
      open(-1);
   }

   public static void openCommunication() {
      open(0);
   }

   public static void openChat(boolean scrollToEnd) {
      open(1, () -> {
         if (scrollToEnd) {
            INSTANCE.appMessagePanel.scrollToEnd();
         }
      });
   }

   public static void openChat() {
      openChat(false);
   }

   private JLabel createSlidePreview() {
      return new PresentationSlideView(getPresentationTheme(), this.paragraph, PresentationSlideView.EnumScreenType.DISPLAY_MONITOR, true);
   }

   private void close() {
      super.setVisible(false);
      super.dispose();
   }

   @Override
   public void dispose() {
      super.setExtendedState(1);
   }

   @Override
   public void setVisible(boolean flag) {
      if (flag) {
         ((ComboBoxModelTheme)this.jComboBoxTheme.getModel()).update();
         this.jComboBoxTheme.setSelectedItem(getPresentationTheme());
      }

      super.setVisible(flag);
   }

   private void alert(boolean alertShow) {
      CommunicationPanel.alertShow = alertShow;
      this.jTextFieldAlert.setBackground(alertShow ? StyleUI.COLOR_ALERT_SHOW : WebTextFieldStyle.backgroundColor);
   }

   private void presentation(boolean presentationShow) {
      CommunicationPanel.presentationShow = presentationShow;
      this.jTextAreaPresentation.setBackground(presentationShow ? StyleUI.COLOR_ALERT_SHOW : WebTextFieldStyle.backgroundColor);
      WallpaperView.update();
   }

   private void updatePresentation() {
      this.paragraph.setText(Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_TEXT.getString());
      this.slidePreview.setTheme(getPresentationTheme());
      this.slidePreview.repaint();
      WallpaperView.update();
   }

   public static Theme getPresentationTheme() {
      return displayCountdown ? COUNTDOWN_THEME : ThemeController.get(Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_THEME.getLong());
   }

   private void themeChanged() {
      Object obj = this.jComboBoxTheme.getSelectedItem();
      if (obj instanceof Theme) {
         Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_THEME.set(((Theme)obj).id);
         this.updatePresentation();
      }
   }

   public static boolean isDisplayAlert() {
      return alertShow || displayClockAlert;
   }

   public static boolean isAlertShow() {
      return alertShow;
   }

   public static void setAlertShow(boolean alertShow) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new CommunicationPanel();
         }

         INSTANCE.jToggleButtonAlert.setSelected(alertShow);
      });
   }

   public static boolean isPresentationShow() {
      return presentationShow;
   }

   public static boolean isDisplayPresentation() {
      return presentationShow || displayClockBG || displayCountdown;
   }

   public static void setPresentationShow(boolean presentationShow) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new CommunicationPanel();
         }

         INSTANCE.jToggleButtonPresentation.setSelected(presentationShow);
      });
   }

   public static String getPresentationText() {
      return Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_TEXT.getString();
   }

   public static MusicController.Paragraph getPresentationTextAsParagraph() {
      MusicController.Paragraph p = MusicController.Paragraph.create();
      p.setHackBreakLine(true);
      if (displayCountdown) {
         long time = countdownValue - CountdownUtils.toSecondsNow();
         String countdownText = CountdownUtils.formatTime(Math.abs(time), true, true, time < 0L);
         p.setText(I18N.WORD_NOW.val() + ": " + DateLG.SDF_HH_MM.format(new Date()) + "\n" + (time < 0L ? "[red]" : "") + countdownText + "\n#null#");
         p.setSlideType(PresentationSlideView.EnumSlideType.COUNTDOWN);
         p.setInt(MusicController.Paragraph.EnumParagraphIntValues.COUNTDOWN_FONT_TIME_SIZE, 250);
         if (INSTANCE != null) {
            INSTANCE.updateCountdownLabel(countdownText);
         }
      } else if (!presentationShow && displayClockBG) {
         p.setText("\n" + DateLG.SDF_HH_MM.format(new Date()) + "\n#null#");
         p.setSlideType(PresentationSlideView.EnumSlideType.COUNTDOWN);
         p.setInt(MusicController.Paragraph.EnumParagraphIntValues.COUNTDOWN_FONT_TIME_SIZE, Values.COMMUNICATION_PANEL_CLOCK_SIZE);
      } else {
         p.setText(Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_TEXT.getString());
      }

      return p;
   }

   public static void setPresentationText(String presentationText) {
      Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_TEXT.set(presentationText);
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new CommunicationPanel();
         }

         INSTANCE.jTextAreaPresentation.setText(presentationText);
      });
   }

   public static String getAlertText() {
      return Settings.EnumSettings2.ADMIN_PANEL_ALERT_TEXT.getString();
   }

   public static String getAlertTextToDisplay() {
      return !alertShow && displayClockAlert ? DateLG.SDF_HH_MM.format(new Date()) : Settings.EnumSettings2.ADMIN_PANEL_ALERT_TEXT.getString();
   }

   public static void setAlertText(String alertText) {
      Settings.EnumSettings2.ADMIN_PANEL_ALERT_TEXT.set(alertText);
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new CommunicationPanel();
         }

         INSTANCE.jTextFieldAlert.setText(alertText);
      });
   }

   public static boolean isPresentationDisplayAhead() {
      return Settings.EnumSettings2.ADMIN_PANEL_PRESENTATION_DISPLAY_AHEAD.is() || displayCountdown;
   }

   public static void setPresentationDisplayAhead(boolean presentationDisplayAhead) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new CommunicationPanel();
         }

         INSTANCE.jCheckBoxDisplayAhead.setSelected(presentationDisplayAhead);
      });
   }

   public static boolean isDisplayClockAsBackground() {
      return displayClockBG;
   }

   public static void setDisplayClockBackground(boolean displayClockBackground) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new CommunicationPanel();
         }

         INSTANCE.jCheckBoxDisplayClockAsBackground.setSelected(displayClockBackground);
      });
   }

   public static boolean isDisplayClockOnAlert() {
      return displayClockAlert;
   }

   public static void setDisplayClockAlert(boolean displayClockAlert) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new CommunicationPanel();
         }

         INSTANCE.jCheckBoxDisplayClockOnAlert.setSelected(displayClockAlert);
      });
   }

   public static BufferedImage getPresentationImage(Dimension size) {
      MusicController.Paragraph p = getPresentationTextAsParagraph();
      Theme t = getPresentationTheme();
      return PresentationSlideUtils.createImage(null, p, t, size, true);
   }

   public static String getHash(Dimension size) {
      MusicController.Paragraph p = getPresentationTextAsParagraph();
      Theme t = getPresentationTheme();
      return PresentationSlideUtils.getKey(null, p, t, size, true);
   }

   public static String getAlertHash() {
      return isDisplayAlert() ? "true" + String.valueOf(alertHash) : "false";
   }

   public static BufferedImage getAlertImage(Dimension size) {
      BufferedImage _return = null;

      try {
         ALERT_PANEL.setSize(size.width, Settings.SET.getAlertSettings2().getAlertHeight(size.height));
         ALERT_PANEL.prepare();
         _return = ALERT_PANEL.getImage();
         ALERT_PANEL.setSize(-1, -1);
      } catch (Exception var3) {
      }

      return _return != null ? _return : Img.createBlank(Color.BLACK, size);
   }

   private void setTab(int tab) {
      this.jTabbedPane.setSelectedIndex(Utils.range(tab, 0, 1));
   }

   private boolean isCommunicationPanelTabSelected() {
      return this.jTabbedPane.getSelectedIndex() == 0;
   }

   @Override
   public void newMessageCount(int newMessageCount) {
      SwingUtilities.invokeLater(() -> Utils.safeException(() -> {
            if (newMessageCount <= 0) {
               this.jTabbedPane.setTitleAt(1, "Chat");
            } else {
               this.jTabbedPane.setTitleAt(1, "<html><span style='color:blue'><b>Chat (" + newMessageCount + ")</b></span>");
            }
         }));
   }

   private void countdown() {
      this.countdown(!((WebButton)this.jButtonCountdown).isSelected());
   }

   private void countdown(boolean enable) {
      WebButton b = (WebButton)this.jButtonCountdown;
      if (!enable) {
         displayCountdown = false;
         b.setSelected(false);
         WallpaperView.update();
      } else {
         CountdownValueInputDialog.main(countdownSet, () -> {
            try {
               Calendar c = Calendar.getInstance();
               if (countdownSet.isDetermine()) {
                  c.add(12, countdownSet.getMinuteDetermine());
                  c.add(13, countdownSet.getSecondDetermine());
               } else {
                  c.set(11, countdownSet.getHourExact());
                  c.set(12, countdownSet.getMinuteExact());
                  c.set(13, 0);
                  c.set(14, 0);
               }

               countdownValue = CountdownUtils.toSeconds(c);
               displayCountdown = true;
               b.setSelected(true);
            } catch (Exception var2x) {
            }
         });
      }
   }

   private void updateCountdownLabel(String text) {
      SwingUtilities.invokeLater(() -> {
         this.jLabelCountdown.setForeground(text.startsWith("-") ? Color.RED : Color.BLACK);
         this.jLabelCountdown.setText(text);
      });
   }

   private void alertSettings() {
      AlertSettingsDialog.main(Settings.SET.getAlertSettings2());
   }

   private void showAlertList() {
      final WebPopOver wpo = new WebPopOver();
      wpo.setDefaultCloseOperation(2);
      wpo.setMovable(false);
      wpo.setPreferredDirection(PopOverDirection.up);
      InsertShortcut.addEscToDispose(wpo);
      wpo.setCloseOnFocusLoss(true);
      wpo.setMargin(4);
      wpo.setLayout(new BorderLayout());
      wpo.add(new AlertListPanel(this.jTextFieldAlert.getText(), new AlertListPanel.AlertListPanelListener() {
         @Override
         public void selected(Alert alert) {
            CommunicationPanel.this.jTextFieldAlert.setText(alert.getText());
            wpo.dispose();
         }

         @Override
         public void fireDataChanged() {
            wpo.show(CommunicationPanel.this.jButtonAlertList);
         }
      }), "Center");
      wpo.show(this.jButtonAlertList);
   }

   private void readyMessages(boolean alert) {
      BiConsumer<String, Boolean> consumer = (message, send) -> SwingUtilities.invokeLater(() -> Utils.safeException2(() -> {
               if (alert) {
                  Document doc = this.jTextFieldAlert.getDocument();
                  if (doc != null) {
                     doc.insertString(this.jTextFieldAlert.getCaretPosition(), message, null);
                  }
               } else {
                  this.jTextAreaPresentation.insert(message, this.jTextAreaPresentation.getCaretPosition());
               }
            }));
      ReadyMessagesChooser.main(consumer, alert ? "cpAlert" : "cpPresentation", false);
   }
}
