package com.limagiran.holyrics.view.panel;

import com.alee.extended.button.WebSplitButton;
import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.colorchooser.WebColorChooserDialog;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.limagiran.holyrics.event.ThemeSettingsPanelListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.model.theme.BackgroundColor;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ChangeEventDelay;
import com.limagiran.holyrics.swing.MarginSetDialog;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.view.BackgroundChoiceAnnouncement;
import com.limagiran.holyrics.view.BackgroundChoiceTheme;
import com.limagiran.holyrics.view.PresentationSlideView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ThemeSettingsPanel extends JPanel {
   private static final ImageIcon IC_TEXT_VALIGN_TOP = new ImageIcon(Images.load("verticalAlignTop16.png"));
   private static final ImageIcon IC_TEXT_VALIGN_MIDDLE = new ImageIcon(Images.load("verticalAlignMiddle16.png"));
   private static final ImageIcon IC_TEXT_VALIGN_BOTTOM = new ImageIcon(Images.load("verticalAlignBottom16.png"));
   private static final ImageIcon IC_TEXT_HALIGN_LEFT = new ImageIcon(Images.load("alignLeft16.png"));
   private static final ImageIcon IC_TEXT_HALIGN_CENTER = new ImageIcon(Images.load("alignCenter16.png"));
   private static final ImageIcon IC_TEXT_HALIGN_RIGHT = new ImageIcon(Images.load("alignRight16.png"));
   private static final ImageIcon IC_TEXT_HALIGN_JUSTIFY = new ImageIcon(Images.load("alignJustify16.png"));
   private final Theme theme;
   private final ThemeSettingsPanel.EnumModality modality;
   private final WebSplitButton splitButtonVelocity = new WebSplitButton(new ImageIcon(Images.load("timer16.png")));
   private final WebSplitButton splitButtonLineSpacing = new WebSplitButton(new ImageIcon(Images.load("lineSpacing16.png")));
   private final WebSplitButton splitButtonTextAlignH = new WebSplitButton();
   private final WebSplitButton splitButtonTextAlignV = new WebSplitButton();
   private final WebColorChooserDialog webColorChooserDialog = new WebColorChooserDialog(this);
   private final DecimalFormat df = new DecimalFormat("00.0");
   private boolean initSet;
   private final Map<Integer, Boolean> arrowMap = new HashMap<>();
   private final int[] margins = new int[4];
   private final int[] oldMargins = new int[]{-1, -1, -1, -1};
   private Color oldTransparencyColor;
   private final ThemeSettingsPanelListener listener;
   private JButton jButtonBrightnessColor;
   private JButton jButtonChangeBackground;
   private JButton jButtonChangeSampleText;
   private JButton jButtonFontColor;
   private JButton jButtonOutlineColor;
   private JButton jButtonSave;
   private JButton jButtonShadowColor;
   private JButton jButtonTransparencyColor;
   private JComboBox<Background.EnumBackgroundType> jComboBoxBackgroundPosition;
   private JComboBox<String> jComboBoxFontName;
   private JComboBox<PresentationSlideView.EnumScreenType> jComboBoxScreenType;
   private JLabel jLabelBrightness;
   private JLabel jLabelFontSize;
   private JLabel jLabelOpacity;
   private JLabel jLabelOutline;
   private JLabel jLabelShadow;
   private JLabel jLabelShadowValues;
   private JLabel jLabelShadowX;
   private JLabel jLabelShadowY;
   private JPanel jPanelBackground;
   private JPanel jPanelBackgroundFooter;
   private JPanel jPanelBackgroundHeader;
   private JPanel jPanelBrightness;
   private JPanel jPanelFont;
   private JPanel jPanelFontAppearance;
   private JPanel jPanelFontStyle;
   private JPanel jPanelFontStyleFooter;
   private JPanel jPanelFontStyleHeader;
   private JPanel jPanelOptions;
   private JPanel jPanelOutline;
   private JPanel jPanelSettingsHeader;
   private JPanel jPanelShadow;
   private JSeparator jSeparator1;
   private JSeparator jSeparator10;
   private JSeparator jSeparator2;
   private JSeparator jSeparator3;
   private JSeparator jSeparator4;
   private JSeparator jSeparator5;
   private JSeparator jSeparator6;
   private JSeparator jSeparator7;
   private JSeparator jSeparator8;
   private JSeparator jSeparator9;
   private JSlider jSliderBrightnessSize;
   private JSlider jSliderFontSize;
   private JSlider jSliderOutlineSize;
   private JSlider jSliderShadowX;
   private JSlider jSliderShadowY;
   private JSpinner jSpinnerOpacity;
   private JToggleButton jToggleButtonFontBold;
   private JToggleButton jToggleButtonFontItalic;
   private JToggleButton jToggleButtonMarginPaint;

   public ThemeSettingsPanel(Theme theme, boolean newTheme, ThemeSettingsPanelListener listener, ThemeSettingsPanel.EnumModality modality) {
      this.modality = modality;
      this.theme = theme;
      this.listener = listener;
      this.initSet = true;
      this.initComponents();
      this.init();
      this.jButtonSave.setEnabled(newTheme);
      this.initSet = false;
   }

   private void init() {
      this.jSpinnerOpacity.addChangeListener(new ChangeEventDelay(500L, () -> this.propertyChanged(ThemeSettingsPanelListener.EnumChange.OPACITY)));
      Arrays.asList(this.jSliderFontSize, this.jSliderOutlineSize, this.jSliderBrightnessSize, this.jSliderShadowX, this.jSliderShadowY)
         .forEach(c -> c.addKeyListener(new KeyAdapter() {
               @Override
               public void keyPressed(KeyEvent e) {
                  ((JSlider)e.getSource()).setValueIsAdjusting(ThemeSettingsPanel.this.isArrowPressed());
                  ThemeSettingsPanel.this.setArrows(e, true);
               }

               @Override
               public void keyReleased(KeyEvent e) {
                  ThemeSettingsPanel.this.setArrows(e, false);
                  ((JSlider)e.getSource()).setValueIsAdjusting(ThemeSettingsPanel.this.isArrowPressed());
               }
            }));
      this.jComboBoxBackgroundPosition.setModel(new DefaultComboBoxModel<>(this.theme.getBackground().getTypes()));
      this.jComboBoxBackgroundPosition.setSelectedItem(this.theme.getBackground().getType());
      this.jComboBoxBackgroundPosition.setEnabled(this.theme.getBackground().getTypes().length > 1);
      this.jSpinnerOpacity.setValue(this.theme.getBackground().getOpacity());
      this.jComboBoxFontName.setSelectedItem(this.theme.getFontName());
      this.jToggleButtonFontBold.setSelected(this.theme.getFont().isBold());
      this.jToggleButtonFontItalic.setSelected(this.theme.getFont().isItalic());
      this.jSliderFontSize.setValue((int)(this.theme.getSize() * 1000.0));
      this.jLabelFontSize.setText(this.df.format((double)this.jSliderFontSize.getValue() / 10.0));
      this.jSliderOutlineSize.setValue((int)(this.theme.getOutlineThickness() * 100.0));
      this.jSliderBrightnessSize.setValue((int)(this.theme.getBrightnessThickness() * 100.0));
      this.jSliderShadowX.setValue((int)(this.theme.getShadowX() * 100.0));
      this.jSliderShadowY.setValue((int)(this.theme.getShadowY() * 100.0));
      this.splitButtonVelocity.setText(String.valueOf(this.theme.getVideoRate()));
      this.splitButtonVelocity.setToolTipText(I18N.WORD_VELOCITY.val());
      WebPopupMenu popupMenu = new WebPopupMenu();
      Stream.of(0.25F, 0.5F, 0.75F, 1.0F, 1.25F, 1.5F, 1.75F, 2.0F, 2.5F, 3.0F, 3.5F, 4.0F).forEach(f -> {
         WebMenuItem item = new WebMenuItem(String.valueOf(f));
         item.addActionListener(e -> {
            this.splitButtonVelocity.setText(String.valueOf(f));
            this.propertyChanged(ThemeSettingsPanelListener.EnumChange.VELOCITY);
         });
         popupMenu.add(item);
      });
      this.splitButtonVelocity.setPopupMenu(popupMenu);
      this.splitButtonVelocity.setFocusable(false);
      this.splitButtonVelocity.setFocusPainted(false);
      this.jPanelBackgroundHeader.add(this.splitButtonVelocity);
      this.jComboBoxBackgroundPosition.setVisible(!this.theme.getBackground().isVideo());
      this.splitButtonVelocity.setVisible(this.theme.getBackground().isVideo());
      this.jButtonChangeSampleText.addActionListener(e -> this.changeSampleText());
      this.jToggleButtonMarginPaint.addActionListener(e -> this.listener.setPaintMargin(this.jToggleButtonMarginPaint.isSelected()));
      this.setupWebLaf();
      this.setupModality();

      for (int i = 0; i < this.theme.getMargins().length; i++) {
         this.margins[i] = (int)(this.theme.getMargins()[i] * 100.0);
      }

      SwingUtils.blockKeyboardSpinner(this.jSpinnerOpacity);
      SwingUtils.blockSpinnerUpDown(this.jSpinnerOpacity);
   }

   private void setupWebLaf() {
      Component c = SwingUtils.searchComponent(this.webColorChooserDialog, JCheckBox.class);
      if (c instanceof JCheckBox) {
         ((JCheckBox)c).setVisible(false);
      }

      SwingUtils.setJPanelToOpaqueFalse(this);
      WebButtonGroup group = new WebButtonGroup(false, new JComponent[]{this.jToggleButtonFontBold, this.jToggleButtonFontItalic});
      group.setLayout(new GridLayout(1, 2));
      this.jPanelFontStyleFooter.add(group, 0);
      this.splitButtonLineSpacing.setText(String.valueOf(this.theme.getLineSpacing()));
      this.splitButtonLineSpacing.setToolTipText(I18N.MSG_LINE_SPACING.val());
      WebPopupMenu popupMenu = new WebPopupMenu();
      DoubleStream.of(-0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0).forEach(d -> {
         WebMenuItem item = new WebMenuItem(String.valueOf(d));
         item.addActionListener(e -> {
            this.splitButtonLineSpacing.setText(String.valueOf(d));
            this.propertyChanged(ThemeSettingsPanelListener.EnumChange.FONT_LINE_SPACING);
         });
         popupMenu.add(item);
      });
      this.splitButtonLineSpacing.setPopupMenu(popupMenu);
      this.splitButtonLineSpacing.setFocusable(false);
      this.splitButtonLineSpacing.setFocusPainted(false);
      this.jPanelFontStyleHeader.add(this.splitButtonLineSpacing);
      this.splitButtonTextAlignH.setIcon(this.getHAlignAsIcon(this.theme.getTextAlign()));
      this.splitButtonTextAlignH.setToolTipText(I18N.MSG_HORIZONTAL_ALIGNMENT.val());
      WebPopupMenu popupMenuHAlign = new WebPopupMenu();
      Arrays.asList(IC_TEXT_HALIGN_LEFT, IC_TEXT_HALIGN_CENTER, IC_TEXT_HALIGN_RIGHT, IC_TEXT_HALIGN_JUSTIFY).forEach(ic -> {
         WebMenuItem wmi = new WebMenuItem(ic);
         wmi.addActionListener(e -> {
            this.splitButtonTextAlignH.setIcon(ic);
            this.propertyChanged(ThemeSettingsPanelListener.EnumChange.TEXT_ALIGN);
         });
         popupMenuHAlign.add(wmi);
      });
      this.splitButtonTextAlignH.setPopupMenu(popupMenuHAlign);
      this.splitButtonTextAlignH.setFocusable(false);
      this.splitButtonTextAlignH.setFocusPainted(false);
      this.jPanelFontStyleFooter.add(this.splitButtonTextAlignH);
      this.splitButtonTextAlignV.setIcon(this.getVAlignAsIcon(this.theme.getTextAlignV()));
      this.splitButtonTextAlignV.setToolTipText(I18N.MSG_VERTICAL_ALIGNMENT.val());
      WebPopupMenu popupMenuVAlign = new WebPopupMenu();
      Arrays.asList(IC_TEXT_VALIGN_TOP, IC_TEXT_VALIGN_MIDDLE, IC_TEXT_VALIGN_BOTTOM).forEach(ic -> {
         WebMenuItem wmi = new WebMenuItem(ic);
         wmi.addActionListener(e -> {
            this.splitButtonTextAlignV.setIcon(ic);
            this.propertyChanged(ThemeSettingsPanelListener.EnumChange.TEXT_ALIGNV);
         });
         popupMenuVAlign.add(wmi);
      });
      this.splitButtonTextAlignV.setPopupMenu(popupMenuVAlign);
      this.splitButtonTextAlignV.setFocusable(false);
      this.splitButtonTextAlignV.setFocusPainted(false);
      this.jPanelFontStyleFooter.add(this.splitButtonTextAlignV);
      JButton buttonMargin = new JButton(new ImageIcon(Images.load("margin16.png")));
      buttonMargin.addActionListener(this::setMargins);
      buttonMargin.setToolTipText(I18N.WORD_MARGIN.val());
      buttonMargin.setFocusable(false);
      buttonMargin.setMargin(new Insets(2, 12, 2, 12));
      this.jPanelFontStyleFooter.add(buttonMargin);
      SwingUtils.blockSpinnerUpDown(this.jSpinnerOpacity);
   }

   private void setupModality() {
      this.jButtonSave.setEnabled(!this.modality.equals(ThemeSettingsPanel.EnumModality.ANNOUNCEMENT));
      this.jButtonSave.setVisible(!this.modality.equals(ThemeSettingsPanel.EnumModality.ANNOUNCEMENT));
      this.jButtonChangeSampleText.setEnabled(!this.modality.equals(ThemeSettingsPanel.EnumModality.ANNOUNCEMENT));
      Arrays.asList(this.jSeparator3, this.jSeparator4, this.jSeparator9, this.jSeparator10, this.jPanelOptions)
         .forEach(c -> c.setVisible(!this.modality.equals(ThemeSettingsPanel.EnumModality.ANNOUNCEMENT)));
   }

   private void initComponents() {
      this.jPanelSettingsHeader = new JPanel();
      this.jPanelBackground = new JPanel();
      this.jPanelBackgroundHeader = new JPanel();
      this.jButtonChangeBackground = new JButton();
      this.jComboBoxBackgroundPosition = new JComboBox<>();
      this.jPanelBackgroundFooter = new JPanel();
      this.jLabelOpacity = new JLabel();
      this.jSpinnerOpacity = new JSpinner();
      this.jButtonTransparencyColor = new JButton() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(ThemeSettingsPanel.this.theme.getTransparencyColorReal());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jSeparator1 = new JSeparator();
      this.jSeparator2 = new JSeparator();
      this.jPanelFont = new JPanel();
      this.jPanelFontStyle = new JPanel();
      this.jPanelFontStyleHeader = new JPanel();
      this.jComboBoxFontName = new WebComboBox() {
         private boolean layingOut = false;

         public void doLayout() {
            try {
               this.layingOut = true;
               super.doLayout();
            } finally {
               this.layingOut = false;
            }
         }

         public Dimension getSize() {
            Dimension dim = super.getSize();
            if (!this.layingOut) {
               dim.width = Math.max(dim.width, super.getPreferredSize().width);
            }

            return dim;
         }

         public Dimension getPreferredSize() {
            return new Dimension(130, super.getPreferredSize().height);
         }
      };
      this.jButtonFontColor = new JButton() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(ThemeSettingsPanel.this.theme.getFontColor());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jSliderFontSize = new JSlider();
      this.jLabelFontSize = new JLabel();
      this.jPanelFontStyleFooter = new JPanel();
      this.jToggleButtonFontBold = new JToggleButton();
      this.jToggleButtonFontItalic = new JToggleButton();
      this.jPanelFontAppearance = new JPanel();
      this.jPanelOutline = new JPanel();
      this.jLabelOutline = new JLabel();
      this.jButtonOutlineColor = new JButton() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(ThemeSettingsPanel.this.theme.getOutlineColor());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jSliderOutlineSize = new JSlider();
      this.jSeparator7 = new JSeparator();
      this.jSeparator5 = new JSeparator();
      this.jPanelBrightness = new JPanel();
      this.jLabelBrightness = new JLabel();
      this.jButtonBrightnessColor = new JButton() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(ThemeSettingsPanel.this.theme.getBrightnessColor());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jSliderBrightnessSize = new JSlider();
      this.jSeparator6 = new JSeparator();
      this.jSeparator8 = new JSeparator();
      this.jPanelShadow = new JPanel();
      this.jLabelShadow = new JLabel();
      this.jButtonShadowColor = new JButton() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(ThemeSettingsPanel.this.theme.getShadowColor());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jLabelShadowValues = new JLabel();
      this.jSliderShadowX = new JSlider();
      this.jLabelShadowX = new JLabel();
      this.jSliderShadowY = new JSlider();
      this.jLabelShadowY = new JLabel();
      this.jSeparator3 = new JSeparator();
      this.jSeparator4 = new JSeparator();
      this.jPanelOptions = new JPanel();
      this.jComboBoxScreenType = new JComboBox<>();
      this.jButtonChangeSampleText = new JButton();
      this.jToggleButtonMarginPaint = new WebToggleButton() {
         {
            this.setLeftRightSpacing(0);
            this.setMargin(1);
            this.setShadeWidth(0);
         }
      };
      this.jSeparator9 = new JSeparator();
      this.jSeparator10 = new JSeparator();
      this.jButtonSave = new JButton() {
         @Override
         public void setEnabled(boolean flag) {
            super.setEnabled(!ThemeSettingsPanel.this.modality.equals(ThemeSettingsPanel.EnumModality.ANNOUNCEMENT) && flag);
         }
      };
      this.setMinimumSize(new Dimension(1050, 0));
      this.setLayout(new BorderLayout());
      this.jPanelSettingsHeader.setLayout(new GridBagLayout());
      this.jPanelBackground
         .setBorder(
            BorderFactory.createTitledBorder(
               BorderFactory.createEmptyBorder(0, 0, 0, 0),
               MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.background")),
               2,
               1,
               new Font("Georgia", 0, 12)
            )
         );
      this.jPanelBackground.setLayout(new GridBagLayout());
      this.jPanelBackgroundHeader.setLayout(new FlowLayout(1, 3, 0));
      this.jButtonChangeBackground.setFont(new Font("Georgia", 0, 12));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jButtonChangeBackground.setText(bundle.getString("word.change"));
      this.jButtonChangeBackground.setFocusable(false);
      this.jButtonChangeBackground.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ThemeSettingsPanel.this.jButtonChangeBackgroundActionPerformed(evt);
         }
      });
      this.jPanelBackgroundHeader.add(this.jButtonChangeBackground);
      this.jComboBoxBackgroundPosition.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxBackgroundPosition.setModel(new DefaultComboBoxModel<>());
      this.jComboBoxBackgroundPosition.setFocusable(false);
      this.jComboBoxBackgroundPosition.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            ThemeSettingsPanel.this.jComboBoxBackgroundPositionItemStateChanged(evt);
         }
      });
      this.jPanelBackgroundHeader.add(this.jComboBoxBackgroundPosition);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.ipady = 2;
      this.jPanelBackground.add(this.jPanelBackgroundHeader, gridBagConstraints);
      this.jPanelBackgroundFooter.setLayout(new FlowLayout(1, 5, 0));
      this.jLabelOpacity.setFont(new Font("Georgia", 0, 12));
      this.jLabelOpacity.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.opacity")));
      this.jPanelBackgroundFooter.add(this.jLabelOpacity);
      this.jSpinnerOpacity.setFont(new Font("Georgia", 0, 14));
      this.jSpinnerOpacity.setModel(new SpinnerNumberModel(100, 0, 100, 5));
      this.jSpinnerOpacity.setEditor(new NumberEditor(this.jSpinnerOpacity, "#'%'"));
      this.jPanelBackgroundFooter.add(this.jSpinnerOpacity);
      this.jButtonTransparencyColor.setFont(new Font("Georgia", 0, 12));
      this.jButtonTransparencyColor.setText("    ");
      this.jButtonTransparencyColor.setFocusable(false);
      this.jButtonTransparencyColor.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ThemeSettingsPanel.this.jButtonTransparencyColorActionPerformed(evt);
         }
      });
      this.jPanelBackgroundFooter.add(this.jButtonTransparencyColor);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      this.jPanelBackground.add(this.jPanelBackgroundFooter, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      this.jPanelSettingsHeader.add(this.jPanelBackground, gridBagConstraints);
      this.jSeparator1.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 2;
      this.jPanelSettingsHeader.add(this.jSeparator1, gridBagConstraints);
      this.jSeparator2.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 2;
      this.jPanelSettingsHeader.add(this.jSeparator2, gridBagConstraints);
      this.jPanelFont
         .setBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), bundle.getString("word.font"), 2, 1, new Font("Georgia", 0, 12))
         );
      this.jPanelFont.setLayout(new BorderLayout(0, 2));
      this.jPanelFontStyle.setLayout(new GridBagLayout());
      this.jPanelFontStyleHeader.setLayout(new FlowLayout(1, 3, 0));
      this.jComboBoxFontName.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxFontName.setModel(new DefaultComboBoxModel<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
      this.jComboBoxFontName.setRenderer(WebLafUtils.createComboBoxRendererFontName(36));
      this.jComboBoxFontName.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            ThemeSettingsPanel.this.jComboBoxFontNameItemStateChanged(evt);
         }
      });
      this.jPanelFontStyleHeader.add(this.jComboBoxFontName);
      this.jButtonFontColor.setFont(new Font("Georgia", 0, 12));
      this.jButtonFontColor.setText("    ");
      this.jButtonFontColor.setFocusable(false);
      this.jButtonFontColor.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ThemeSettingsPanel.this.jButtonFontColorActionPerformed(evt);
         }
      });
      this.jPanelFontStyleHeader.add(this.jButtonFontColor);
      this.jSliderFontSize.setMaximum(400);
      this.jSliderFontSize.setMinimum(40);
      this.jSliderFontSize.setValue(100);
      this.jSliderFontSize.setPreferredSize(new Dimension(60, 23));
      this.jSliderFontSize.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            ThemeSettingsPanel.this.jSliderFontSizeStateChanged(evt);
         }
      });
      this.jPanelFontStyleHeader.add(this.jSliderFontSize);
      this.jLabelFontSize.setFont(new Font("Courier New", 0, 12));
      this.jLabelFontSize.setForeground(new Color(51, 51, 51));
      this.jPanelFontStyleHeader.add(this.jLabelFontSize);
      this.jPanelFontStyle.add(this.jPanelFontStyleHeader, new GridBagConstraints());
      this.jPanelFontStyleFooter.setLayout(new FlowLayout(1, 4, 0));
      this.jToggleButtonFontBold.setFont(new Font("Georgia", 1, 13));
      this.jToggleButtonFontBold.setText(bundle.getString("word.firstCharBold"));
      this.jToggleButtonFontBold.setToolTipText(bundle.getString("word.bold"));
      this.jToggleButtonFontBold.setFocusable(false);
      this.jToggleButtonFontBold.setMargin(new Insets(2, 12, 2, 12));
      this.jToggleButtonFontBold.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ThemeSettingsPanel.this.jToggleButtonFontBoldjToggleButtonFontStyleAction(evt);
         }
      });
      this.jPanelFontStyleFooter.add(this.jToggleButtonFontBold);
      this.jToggleButtonFontItalic.setFont(new Font("Georgia", 2, 13));
      this.jToggleButtonFontItalic.setText(bundle.getString("word.firstCharItalic"));
      this.jToggleButtonFontItalic.setToolTipText(bundle.getString("word.italic"));
      this.jToggleButtonFontItalic.setFocusable(false);
      this.jToggleButtonFontItalic.setMargin(new Insets(2, 12, 2, 12));
      this.jToggleButtonFontItalic.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ThemeSettingsPanel.this.jToggleButtonFontItalicjToggleButtonFontStyleAction(evt);
         }
      });
      this.jPanelFontStyleFooter.add(this.jToggleButtonFontItalic);
      this.jPanelFontStyle.add(this.jPanelFontStyleFooter, new GridBagConstraints());
      this.jPanelFont.add(this.jPanelFontStyle, "First");
      this.jPanelFontAppearance.setLayout(new GridBagLayout());
      this.jPanelOutline.setLayout(new FlowLayout(1, 3, 0));
      this.jLabelOutline.setFont(new Font("Georgia", 0, 12));
      this.jLabelOutline.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.outline")));
      this.jPanelOutline.add(this.jLabelOutline);
      this.jButtonOutlineColor.setFont(new Font("Georgia", 0, 12));
      this.jButtonOutlineColor.setText("    ");
      this.jButtonOutlineColor.setFocusable(false);
      this.jButtonOutlineColor.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ThemeSettingsPanel.this.jButtonOutlineColorActionPerformed(evt);
         }
      });
      this.jPanelOutline.add(this.jButtonOutlineColor);
      this.jSliderOutlineSize.setMaximum(10);
      this.jSliderOutlineSize.setMinimum(0);
      this.jSliderOutlineSize.setValue(0);
      this.jSliderOutlineSize.setPreferredSize(new Dimension(50, 23));
      this.jSliderOutlineSize.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            ThemeSettingsPanel.this.jSliderOutlineSizeStateChanged(evt);
         }
      });
      this.jPanelOutline.add(this.jSliderOutlineSize);
      this.jPanelFontAppearance.add(this.jPanelOutline, new GridBagConstraints());
      this.jSeparator7.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 1;
      this.jPanelFontAppearance.add(this.jSeparator7, gridBagConstraints);
      this.jSeparator5.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 1;
      this.jPanelFontAppearance.add(this.jSeparator5, gridBagConstraints);
      this.jPanelBrightness.setLayout(new FlowLayout(1, 3, 0));
      this.jLabelBrightness.setFont(new Font("Georgia", 0, 12));
      this.jLabelBrightness.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.brightness")));
      this.jPanelBrightness.add(this.jLabelBrightness);
      this.jButtonBrightnessColor.setFont(new Font("Georgia", 0, 12));
      this.jButtonBrightnessColor.setText("    ");
      this.jButtonBrightnessColor.setFocusable(false);
      this.jButtonBrightnessColor.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ThemeSettingsPanel.this.jButtonBrightnessColorActionPerformed(evt);
         }
      });
      this.jPanelBrightness.add(this.jButtonBrightnessColor);
      this.jSliderBrightnessSize.setMaximum(20);
      this.jSliderBrightnessSize.setMinimum(0);
      this.jSliderBrightnessSize.setValue(0);
      this.jSliderBrightnessSize.setPreferredSize(new Dimension(50, 23));
      this.jSliderBrightnessSize.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            ThemeSettingsPanel.this.jSliderBrightnessSizeStateChanged(evt);
         }
      });
      this.jPanelBrightness.add(this.jSliderBrightnessSize);
      this.jPanelFontAppearance.add(this.jPanelBrightness, new GridBagConstraints());
      this.jSeparator6.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 1;
      this.jPanelFontAppearance.add(this.jSeparator6, gridBagConstraints);
      this.jSeparator8.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 1;
      this.jPanelFontAppearance.add(this.jSeparator8, gridBagConstraints);
      this.jPanelShadow.setLayout(new FlowLayout(1, 5, 0));
      this.jLabelShadow.setFont(new Font("Georgia", 0, 12));
      this.jLabelShadow.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.shadow")));
      this.jPanelShadow.add(this.jLabelShadow);
      this.jButtonShadowColor.setFont(new Font("Georgia", 0, 12));
      this.jButtonShadowColor.setText("    ");
      this.jButtonShadowColor.setFocusable(false);
      this.jButtonShadowColor.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ThemeSettingsPanel.this.jButtonShadowColorActionPerformed(evt);
         }
      });
      this.jPanelShadow.add(this.jButtonShadowColor);
      this.jLabelShadowValues.setFont(new Font("Georgia", 0, 11));
      this.jLabelShadowValues.setForeground(new Color(51, 51, 51));
      this.jLabelShadowValues.setText("reset");
      this.jLabelShadowValues.setCursor(new Cursor(12));
      this.jLabelShadowValues.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            ThemeSettingsPanel.this.jLabelShadowValuesMouseReleased(evt);
         }
      });
      this.jPanelShadow.add(this.jLabelShadowValues);
      this.jSliderShadowX.setMaximum(5);
      this.jSliderShadowX.setMinimum(-5);
      this.jSliderShadowX.setValue(0);
      this.jSliderShadowX.setPreferredSize(new Dimension(50, 23));
      this.jSliderShadowX.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            ThemeSettingsPanel.this.jSliderShadowXStateChanged(evt);
         }
      });
      this.jPanelShadow.add(this.jSliderShadowX);
      this.jLabelShadowX.setFont(new Font("Georgia", 0, 12));
      this.jLabelShadowX.setText("x");
      this.jPanelShadow.add(this.jLabelShadowX);
      this.jSliderShadowY.setMaximum(5);
      this.jSliderShadowY.setMinimum(-5);
      this.jSliderShadowY.setValue(0);
      this.jSliderShadowY.setPreferredSize(new Dimension(50, 23));
      this.jSliderShadowY.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            ThemeSettingsPanel.this.jSliderShadowYStateChanged(evt);
         }
      });
      this.jPanelShadow.add(this.jSliderShadowY);
      this.jLabelShadowY.setFont(new Font("Georgia", 0, 12));
      this.jLabelShadowY.setText("y");
      this.jPanelShadow.add(this.jLabelShadowY);
      this.jPanelFontAppearance.add(this.jPanelShadow, new GridBagConstraints());
      this.jPanelFont.add(this.jPanelFontAppearance, "Last");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 0;
      this.jPanelSettingsHeader.add(this.jPanelFont, gridBagConstraints);
      this.jSeparator3.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 2;
      this.jPanelSettingsHeader.add(this.jSeparator3, gridBagConstraints);
      this.jSeparator4.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 2;
      this.jPanelSettingsHeader.add(this.jSeparator4, gridBagConstraints);
      this.jPanelOptions
         .setBorder(
            BorderFactory.createTitledBorder(
               BorderFactory.createEmptyBorder(0, 0, 0, 0),
               MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.preview")),
               2,
               1,
               new Font("Georgia", 0, 12)
            )
         );
      this.jPanelOptions.setLayout(new GridBagLayout());
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
      this.jComboBoxScreenType.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            ThemeSettingsPanel.this.jComboBoxScreenTypeItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.jPanelOptions.add(this.jComboBoxScreenType, gridBagConstraints);
      this.jButtonChangeSampleText.setFont(new Font("Georgia", 0, 11));
      this.jButtonChangeSampleText.setText(bundle.getString("msg.changeSampleText"));
      this.jButtonChangeSampleText.setFocusable(false);
      this.jButtonChangeSampleText.setMargin(new Insets(2, 4, 2, 4));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.jPanelOptions.add(this.jButtonChangeSampleText, gridBagConstraints);
      this.jToggleButtonMarginPaint.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/margin16.png")));
      this.jToggleButtonMarginPaint.setToolTipText(bundle.getString("word.margin"));
      this.jToggleButtonMarginPaint.setFocusable(false);
      this.jPanelOptions.add(this.jToggleButtonMarginPaint, new GridBagConstraints());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 6;
      gridBagConstraints.gridy = 0;
      this.jPanelSettingsHeader.add(this.jPanelOptions, gridBagConstraints);
      this.jSeparator9.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 7;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 2;
      this.jPanelSettingsHeader.add(this.jSeparator9, gridBagConstraints);
      this.jSeparator10.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 8;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.ipadx = 2;
      this.jPanelSettingsHeader.add(this.jSeparator10, gridBagConstraints);
      this.jButtonSave.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/saveBlack32.png")));
      this.jButtonSave.setToolTipText(bundle.getString("word.saveAndExit"));
      this.jButtonSave.setFocusable(false);
      this.jButtonSave.setMargin(new Insets(2, 10, 2, 10));
      this.jButtonSave.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            ThemeSettingsPanel.this.jButtonSaveActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 9;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelSettingsHeader.add(this.jButtonSave, gridBagConstraints);
      this.add(this.jPanelSettingsHeader, "Center");
   }

   private void jButtonChangeBackgroundActionPerformed(ActionEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.BACKGROUND_IMAGE);
   }

   private void jComboBoxBackgroundPositionItemStateChanged(ItemEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.BACKGROUND_POSITION);
   }

   private void jComboBoxFontNameItemStateChanged(ItemEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.FONT_NAME);
   }

   private void jButtonFontColorActionPerformed(ActionEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.FONT_COLOR);
   }

   private void jSliderFontSizeStateChanged(ChangeEvent evt) {
      BooleanOptional.runIfTrue(!this.jSliderFontSize.getValueIsAdjusting(), () -> this.propertyChanged(ThemeSettingsPanelListener.EnumChange.FONT_SIZE));
      this.jLabelFontSize.setText(this.df.format((double)this.jSliderFontSize.getValue() / 10.0));
   }

   private void jToggleButtonFontBoldjToggleButtonFontStyleAction(ActionEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.FONT_STYLE);
   }

   private void jToggleButtonFontItalicjToggleButtonFontStyleAction(ActionEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.FONT_STYLE);
   }

   private void jButtonOutlineColorActionPerformed(ActionEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.OUTLINE_COLOR);
   }

   private void jSliderOutlineSizeStateChanged(ChangeEvent evt) {
      BooleanOptional.runIfTrue(!this.jSliderOutlineSize.getValueIsAdjusting(), () -> this.propertyChanged(ThemeSettingsPanelListener.EnumChange.OUTLINE_SIZE));
   }

   private void jButtonBrightnessColorActionPerformed(ActionEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.BRIGHTNESS_COLOR);
   }

   private void jSliderBrightnessSizeStateChanged(ChangeEvent evt) {
      BooleanOptional.runIfTrue(
         !this.jSliderBrightnessSize.getValueIsAdjusting(), () -> this.propertyChanged(ThemeSettingsPanelListener.EnumChange.BRIGHTNESS_SIZE)
      );
   }

   private void jButtonShadowColorActionPerformed(ActionEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.SHADOW_COLOR);
   }

   private void jSliderShadowXStateChanged(ChangeEvent evt) {
      BooleanOptional.runIfTrue(!this.jSliderShadowX.getValueIsAdjusting(), () -> this.propertyChanged(ThemeSettingsPanelListener.EnumChange.SHADOW_X));
   }

   private void jSliderShadowYStateChanged(ChangeEvent evt) {
      BooleanOptional.runIfTrue(!this.jSliderShadowY.getValueIsAdjusting(), () -> this.propertyChanged(ThemeSettingsPanelListener.EnumChange.SHADOW_Y));
   }

   private void jComboBoxScreenTypeItemStateChanged(ItemEvent evt) {
      BooleanOptional.runIfTrue(evt.getStateChange() == 1, () -> this.listener.themeChanged());
   }

   private void jButtonSaveActionPerformed(ActionEvent evt) {
      this.listener.saveButtonClicked();
   }

   private void jLabelShadowValuesMouseReleased(MouseEvent evt) {
      this.jSliderShadowX.setValue(0);
      this.jSliderShadowY.setValue(0);
   }

   private void jButtonTransparencyColorActionPerformed(ActionEvent evt) {
      this.propertyChanged(ThemeSettingsPanelListener.EnumChange.TRANSPARENCY_COLOR);
   }

   private boolean isArrowPressed() {
      return this.arrowMap.values().stream().anyMatch(b -> b);
   }

   private void propertyChanged(ThemeSettingsPanelListener.EnumChange en) {
      try {
         if (!this.initSet) {
            switch (en) {
               case BACKGROUND_IMAGE:
                  this.backgroundChoice(this.theme.getBackground());
                  break;
               case VELOCITY:
                  this.theme.setVideoRate(Utils.floatOrElse(this.splitButtonVelocity.getText(), 1.0F));
                  break;
               case OPACITY:
                  this.theme.getBackground().setOpacity(Integer.parseInt(this.jSpinnerOpacity.getValue().toString()));
                  break;
               case BACKGROUND_POSITION:
                  Object o = this.jComboBoxBackgroundPosition.getSelectedItem();
                  BooleanOptional.runIfTrue(
                     o instanceof Background.EnumBackgroundType, () -> this.theme.getBackground().setType((Background.EnumBackgroundType)o)
                  );
                  break;
               case FONT_NAME:
               case FONT_STYLE:
                  this.setFont();
                  break;
               case FONT_COLOR:
                  this.theme.setFontColor(this.getColor(this.theme.getFontColor()));
                  break;
               case TRANSPARENCY_COLOR:
                  this.theme.setTransparencyColor(this.getColor(this.theme.getTransparencyColorReal()));
                  break;
               case FONT_SIZE:
                  this.theme.setSize((double)this.jSliderFontSize.getValue() / 1000.0);
                  break;
               case FONT_LINE_SPACING:
                  this.theme.setLineSpacing(Double.parseDouble(this.splitButtonLineSpacing.getText()));
                  break;
               case TEXT_ALIGN:
                  this.theme.setTextAlign(this.getHAlignIconAsInt(this.splitButtonTextAlignH.getIcon()));
                  break;
               case TEXT_ALIGNV:
                  this.theme.setTextAlignV(this.getVAlignIconAsInt(this.splitButtonTextAlignV.getIcon()));
                  break;
               case MARGIN:
                  this.theme.setMargin(Arrays.stream(this.margins).mapToDouble(i -> (double)i / 100.0).toArray());
                  break;
               case OUTLINE_COLOR:
                  this.theme.setOutlineColor(this.getColor(this.theme.getOutlineColor()));
                  break;
               case OUTLINE_SIZE:
                  this.theme.setOutlineThickness((double)this.jSliderOutlineSize.getValue() / 100.0);
                  break;
               case BRIGHTNESS_COLOR:
                  this.theme.setBrightnessColor(this.getColor(this.theme.getBrightnessColor()));
                  break;
               case BRIGHTNESS_SIZE:
                  this.theme.setBrightnessThickness((double)this.jSliderBrightnessSize.getValue() / 100.0);
                  break;
               case SHADOW_COLOR:
                  this.theme.setShadowColor(this.getColor(this.theme.getShadowColor()));
                  break;
               case SHADOW_X:
                  this.theme.setShadowX((double)this.jSliderShadowX.getValue() / 100.0);
                  break;
               case SHADOW_Y:
                  this.theme.setShadowY((double)this.jSliderShadowY.getValue() / 100.0);
            }

            BooleanOptional.runIfTrue(!en.equals(ThemeSettingsPanelListener.EnumChange.BACKGROUND_IMAGE), () -> this.jButtonSave.setEnabled(true));
            this.listener.themeChanged();
         }
      } catch (Exception var3) {
      }
   }

   private void backgroundChoice(Background init) {
      if (init instanceof BackgroundColor && ((BackgroundColor)init).isTransparent()) {
         if (this.oldTransparencyColor == null) {
            this.oldTransparencyColor = Color.WHITE;
         }

         if (this.oldMargins[0] < 0 || this.oldMargins[1] < 0 || this.oldMargins[2] < 0 || this.oldMargins[3] < 0) {
            Arrays.fill(this.oldMargins, 3);
         }
      } else {
         System.arraycopy(this.margins, 0, this.oldMargins, 0, 4);
         this.oldTransparencyColor = this.theme.getTransparencyColorReal();
      }

      Consumer<Background> defaultConsumer = bg -> {
         this.theme.setBackground(bg);
         this.jComboBoxBackgroundPosition.setModel(new DefaultComboBoxModel<>(bg.getTypes()));
         this.jComboBoxBackgroundPosition.setSelectedItem(bg.getDefaultType());
         this.jComboBoxBackgroundPosition.setEnabled(bg.getTypes().length > 1);
         if (bg instanceof BackgroundColor && ((BackgroundColor)bg).isTransparent()) {
            this.theme.setTransparencyColor(Color.BLACK);
            this.jButtonTransparencyColor.repaint();
            this.margins[0] = 60;
            this.margins[1] = 2;
            this.margins[2] = 4;
            this.margins[3] = 2;
            this.theme.setMargin(Arrays.stream(this.margins).mapToDouble(i -> (double)i / 100.0).toArray());
            this.listener.themeChanged();
         } else {
            this.jButtonTransparencyColor.setEnabled(true);
            System.arraycopy(this.oldMargins, 0, this.margins, 0, this.margins.length);
            this.theme.setMargin(Arrays.stream(this.margins).mapToDouble(i -> (double)i / 100.0).toArray());
            this.listener.themeChanged();
            this.theme.setTransparencyColor(this.oldTransparencyColor);
            this.jButtonTransparencyColor.repaint();
         }

         this.jSpinnerOpacity.setValue(this.theme.getBackground().getOpacity());
         this.jComboBoxBackgroundPosition.setVisible(!this.theme.getBackground().isVideo());
         this.splitButtonVelocity.setVisible(this.theme.getBackground().isVideo());
         this.splitButtonVelocity.setText("1.0");
         this.theme.setVideoRate(1.0F);
         this.listener.backgroundChanged();
      };
      switch (this.modality) {
         case DEFAULT:
            BackgroundChoiceTheme.show(init, bg -> {
               defaultConsumer.accept(bg);
               this.jButtonSave.setEnabled(true);
            });
            break;
         case ANNOUNCEMENT:
            BackgroundChoiceAnnouncement.show(init, defaultConsumer::accept);
      }
   }

   private void setFont() {
      Object o = this.jComboBoxFontName.getSelectedItem();
      if (o != null) {
         int style;
         if (this.jToggleButtonFontBold.isSelected()) {
            style = this.jToggleButtonFontItalic.isSelected() ? 3 : 1;
         } else if (this.jToggleButtonFontItalic.isSelected()) {
            style = 2;
         } else {
            style = 0;
         }

         this.theme.setFont(new Font(o.toString(), style, 8));
      }
   }

   private Color getColor(Color def) {
      this.webColorChooserDialog.setColor(def);
      this.webColorChooserDialog.setVisible(true);
      return this.webColorChooserDialog.getResult() == 0 ? this.webColorChooserDialog.getColor() : def;
   }

   public boolean buttonSaveEnabled() {
      return this.jButtonSave.isEnabled();
   }

   public void setButtonSaveEnabled(boolean flag) {
      this.jButtonSave.setEnabled(flag);
   }

   public JComboBox getComboBoxScreenType() {
      return this.jComboBoxScreenType;
   }

   public PresentationSlideView.EnumScreenType getEnumScreenType() {
      return (PresentationSlideView.EnumScreenType)this.jComboBoxScreenType.getSelectedItem();
   }

   private void setArrows(KeyEvent e, boolean flag) {
      switch (e.getKeyCode()) {
         case 37:
         case 38:
         case 39:
         case 40:
            this.arrowMap.put(e.getKeyCode(), flag);
      }
   }

   private void changeSampleText() {
      SwingUtilities.invokeLater(() -> {
         String sampleText = Utils.getTextArea(null, I18N.MSG_CHANGE_SAMPLE_TEXT.val(), Settings.EnumSettings.THEME_SAMPLE_TEXT.getString());
         if (sampleText != null) {
            Settings.EnumSettings.THEME_SAMPLE_TEXT.set(sampleText);
            this.listener.repaint();
         }
      });
   }

   private ImageIcon getVAlignAsIcon(int align) {
      return align == -1 ? IC_TEXT_VALIGN_TOP : (align == 1 ? IC_TEXT_VALIGN_BOTTOM : IC_TEXT_VALIGN_MIDDLE);
   }

   private int getVAlignIconAsInt(Icon icon) {
      return icon == IC_TEXT_VALIGN_TOP ? -1 : (icon == IC_TEXT_VALIGN_BOTTOM ? 1 : 0);
   }

   private ImageIcon getHAlignAsIcon(int align) {
      switch (align) {
         case -1:
            return IC_TEXT_HALIGN_LEFT;
         case 0:
         default:
            return IC_TEXT_HALIGN_CENTER;
         case 1:
            return IC_TEXT_HALIGN_RIGHT;
         case 2:
            return IC_TEXT_HALIGN_JUSTIFY;
      }
   }

   private int getHAlignIconAsInt(Icon icon) {
      if (icon == IC_TEXT_HALIGN_LEFT) {
         return -1;
      } else if (icon == IC_TEXT_HALIGN_RIGHT) {
         return 1;
      } else {
         return icon == IC_TEXT_HALIGN_JUSTIFY ? 2 : 0;
      }
   }

   private void setMargins(ActionEvent e) {
      MarginSetDialog.main(this, (Component)e.getSource(), new MarginSetDialog.MarginSetListener() {
         private final int[] minMaxStep = new int[]{0, 90, 1};

         @Override
         public void changed() {
            SwingUtilities.invokeLater(() -> ThemeSettingsPanel.this.propertyChanged(ThemeSettingsPanelListener.EnumChange.MARGIN));
         }

         @Override
         public int[] getMargins() {
            return ThemeSettingsPanel.this.margins;
         }

         @Override
         public int[] getMinMaxAndStepValue() {
            return this.minMaxStep;
         }
      });
   }

   public static enum EnumModality {
      DEFAULT,
      ANNOUNCEMENT;
   }
}
