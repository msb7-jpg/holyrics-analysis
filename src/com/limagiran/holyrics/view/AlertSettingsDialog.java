package com.limagiran.holyrics.view;

import com.alee.laf.colorchooser.WebColorChooserDialog;
import com.alee.laf.panel.WebPanelStyle;
import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.AlertSettings;
import com.limagiran.holyrics.swing.AlertPanelAnimated;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.WebLafUtils;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class AlertSettingsDialog extends JDialog {
   private final AlertSettings alertSettings;
   private boolean stopAnimation;
   private JComponent componentExample;
   private JButton jButtonBackgroundColor;
   private JButton jButtonCancel;
   private JButton jButtonFontColor;
   private JButton jButtonOk;
   private JCheckBox jCheckBoxBackground;
   private JCheckBox jCheckBoxOverlap;
   private JComboBox<String> jComboBoxFontName;
   private JComboBox<String> jComboBoxLineHeight;
   private JComboBox<String> jComboBoxLocation;
   private JLabel jLabelBackgroundColor;
   private JLabel jLabelFontColor;
   private JLabel jLabelFrameSkip;
   private JLabel jLabelLineHeight;
   private JLabel jLabelLocation;
   private JLabel jLabelOverlap;
   private JLabel jLabelVelocity;
   private JPanel jPanel1;
   private JPanel jPanelFooter;
   private JPanel jPanelGeneral;
   private JPanel jPanelOkCancel;
   private JSeparator jSeparator1;
   private JSpinner jSpinnerFrameSkip;
   private JSpinner jSpinnerVelocity;
   private JToggleButton jToggleButtonBold;

   private AlertSettingsDialog(AlertSettings alertSettings) {
      this.alertSettings = alertSettings;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.componentExample = this.createExample();
      this.componentExample.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.componentExample.setPreferredSize(new Dimension(60, 80));
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 8;
      gbc.gridwidth = 0;
      gbc.fill = 2;
      gbc.insets = new Insets(4, 4, 4, 4);
      this.jPanelGeneral.add(this.componentExample, gbc);
      this.componentExample.setForeground(this.alertSettings.getForeground());
      this.componentExample.setBackground(this.alertSettings.getBackground());
      this.componentExample.setOpaque(this.alertSettings.isBackground());
      this.jComboBoxFontName.setSelectedItem(this.alertSettings.getFontName());
      if (this.jComboBoxFontName.getSelectedIndex() == -1) {
         this.jComboBoxFontName.setSelectedItem("Arial");
      }

      this.jComboBoxLocation.setSelectedIndex(this.alertSettings.isTop() ? 0 : 1);
      this.jCheckBoxOverlap.setSelected(this.alertSettings.isOverlap());
      this.jComboBoxLineHeight.setSelectedItem(this.alertSettings.getLineHeight() + "%");
      this.jToggleButtonBold.setSelected(this.alertSettings.getFont().isBold());
      this.jButtonBackgroundColor.addActionListener(e -> {
         this.componentExample.setBackground(this.getColor(this.componentExample.getBackground()));
         this.jButtonBackgroundColor.repaint();
      });
      this.jCheckBoxBackground.setSelected(this.alertSettings.isBackground());
      this.jButtonFontColor.addActionListener(e -> {
         this.componentExample.setForeground(this.getColor(this.componentExample.getForeground()));
         this.jButtonFontColor.repaint();
      });
      this.jToggleButtonBold.addItemListener(e -> this.componentExample.setFont(this.getFontExample()));
      this.jComboBoxFontName.addItemListener(e -> this.componentExample.setFont(this.getFontExample()));
      this.componentExample.setFont(this.getFontExample());
      this.jButtonOk.addActionListener(e -> this.ok());
      this.jButtonCancel.addActionListener(e -> this.dispose());
      ((WebSpinner)this.jSpinnerVelocity).setDrawFocus(false);
      ((WebSpinner)this.jSpinnerFrameSkip).setDrawFocus(false);
      InsertShortcut.addEscToDispose(this);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, () -> this.ok()));
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            int op = JOptionPane.showConfirmDialog(AlertSettingsDialog.this, I18N.MSG_SAVE_CHANGES.question(), I18N.WORD_ALERT.val(), 1, -1);
            switch (op) {
               case 0:
                  AlertSettingsDialog.this.ok();
                  break;
               case 1:
                  AlertSettingsDialog.this.dispose();
            }
         }
      });
   }

   @Override
   public void dispose() {
      this.stopAnimation = true;
      super.dispose();
   }

   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      BooleanOptional.runIfTrue(!visible, () -> this.stopAnimation = true);
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jLabelFontColor = new JLabel();
      this.jButtonFontColor = new JButton() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(AlertSettingsDialog.this.componentExample.getForeground());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jLabelBackgroundColor = new JLabel();
      this.jPanel1 = new JPanel();
      this.jButtonBackgroundColor = new JButton() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(AlertSettingsDialog.this.componentExample.getBackground());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jCheckBoxBackground = new JCheckBox();
      this.jLabelLineHeight = new JLabel();
      this.jComboBoxLineHeight = new JComboBox<>();
      this.jLabelOverlap = new JLabel();
      this.jCheckBoxOverlap = new JCheckBox();
      this.jLabelLocation = new JLabel();
      this.jComboBoxLocation = new JComboBox<>();
      this.jLabelVelocity = new JLabel();
      this.jSpinnerVelocity = new WebSpinner();
      this.jLabelFrameSkip = new JLabel();
      this.jSpinnerFrameSkip = new WebSpinner();
      this.jPanelFooter = new JPanel();
      this.jComboBoxFontName = new JComboBox<>();
      this.jToggleButtonBold = new JToggleButton();
      this.jSeparator1 = new JSeparator();
      this.jPanelOkCancel = new JPanel();
      this.jButtonOk = new JButton();
      this.jButtonCancel = new JButton();
      this.setDefaultCloseOperation(0);
      this.setTitle(I18N.WORD_ALERT.val());
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(720, 400));
      this.setModal(true);
      this.setResizable(false);
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
      this.jPanelGeneral.setLayout(new GridBagLayout());
      this.jLabelFontColor.setFont(new Font("Georgia", 0, 12));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelFontColor.setText(bundle.getString("word.fontColor"));
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelGeneral.add(this.jLabelFontColor, gridBagConstraints);
      this.jButtonFontColor.setFont(new Font("Georgia", 0, 12));
      this.jButtonFontColor.setText("    ");
      this.jButtonFontColor.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jButtonFontColor, gridBagConstraints);
      this.jLabelBackgroundColor.setFont(new Font("Georgia", 0, 12));
      this.jLabelBackgroundColor.setText(bundle.getString("word.backgroundColor"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelGeneral.add(this.jLabelBackgroundColor, gridBagConstraints);
      FlowLayout flowLayout1 = new FlowLayout(3, 0, 0);
      flowLayout1.setAlignOnBaseline(true);
      this.jPanel1.setLayout(flowLayout1);
      this.jButtonBackgroundColor.setFont(new Font("Georgia", 0, 12));
      this.jButtonBackgroundColor.setText("    ");
      this.jButtonBackgroundColor.setFocusable(false);
      this.jPanel1.add(this.jButtonBackgroundColor);
      this.jCheckBoxBackground.setSelected(this.alertSettings.isBackground());
      this.jCheckBoxBackground.setFocusable(false);
      this.jCheckBoxBackground.setMargin(new Insets(2, 6, 2, 2));
      this.jPanel1.add(this.jCheckBoxBackground);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jPanel1, gridBagConstraints);
      this.jLabelLineHeight.setFont(new Font("Georgia", 0, 12));
      this.jLabelLineHeight.setText(bundle.getString("word.lineHeight"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelGeneral.add(this.jLabelLineHeight, gridBagConstraints);
      this.jComboBoxLineHeight.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxLineHeight.setModel(this.createLineHeightModel());
      this.jComboBoxLineHeight.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jComboBoxLineHeight, gridBagConstraints);
      this.jLabelOverlap.setFont(new Font("Georgia", 0, 12));
      this.jLabelOverlap.setText("<html><b>?</b>&nbsp;&nbsp;" + ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.overlap"));
      this.jLabelOverlap.setCursor(new Cursor(12));
      this.jLabelOverlap.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            AlertSettingsDialog.this.jLabelOverlapMouseReleased(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelGeneral.add(this.jLabelOverlap, gridBagConstraints);
      this.jCheckBoxOverlap.setFont(new Font("Georgia", 1, 12));
      this.jCheckBoxOverlap.setForeground(new Color(51, 51, 51));
      this.jCheckBoxOverlap.setFocusable(false);
      this.jCheckBoxOverlap.setIconTextGap(8);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jCheckBoxOverlap, gridBagConstraints);
      this.jLabelLocation.setFont(new Font("Georgia", 0, 12));
      this.jLabelLocation.setText(bundle.getString("word.location"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelGeneral.add(this.jLabelLocation, gridBagConstraints);
      this.jComboBoxLocation.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxLocation.setModel(new DefaultComboBoxModel<>(new String[]{I18N.WORD_TOP.val(), I18N.WORD_BOTTOM.val()}));
      this.jComboBoxLocation.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jComboBoxLocation, gridBagConstraints);
      this.jLabelVelocity.setFont(new Font("Georgia", 0, 12));
      this.jLabelVelocity.setText(bundle.getString("word.velocity"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelGeneral.add(this.jLabelVelocity, gridBagConstraints);
      this.jSpinnerVelocity.setFont(new Font("Georgia", 0, 12));
      this.jSpinnerVelocity.setModel(new SpinnerNumberModel(this.alertSettings.getVelocity(), 0.2, 5.0, 0.2));
      this.jSpinnerVelocity.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jSpinnerVelocity, gridBagConstraints);
      this.jLabelFrameSkip.setFont(new Font("Georgia", 0, 12));
      this.jLabelFrameSkip.setText(bundle.getString("msg.frameSkip"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelGeneral.add(this.jLabelFrameSkip, gridBagConstraints);
      this.jSpinnerFrameSkip.setFont(new Font("Georgia", 0, 12));
      this.jSpinnerFrameSkip.setModel(new SpinnerNumberModel(this.alertSettings.getFrameSkip(), 0, 60, 5));
      this.jSpinnerFrameSkip.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jSpinnerFrameSkip, gridBagConstraints);
      this.jPanelFooter.setLayout(new FlowLayout(1, 5, 0));
      this.jComboBoxFontName.setModel(new DefaultComboBoxModel<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
      this.jComboBoxFontName.setPrototypeDisplayValue("12345678901234567890123456789012");
      this.jComboBoxFontName.setRenderer(WebLafUtils.createComboBoxRendererFontName(36));
      this.jPanelFooter.add(this.jComboBoxFontName);
      this.jToggleButtonBold.setFont(new Font("Georgia", 1, 13));
      this.jToggleButtonBold.setText(bundle.getString("word.firstCharBold"));
      this.jToggleButtonBold.setFocusable(false);
      this.jToggleButtonBold.setMargin(new Insets(2, 12, 2, 12));
      this.jPanelFooter.add(this.jToggleButtonBold);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      this.jPanelGeneral.add(this.jPanelFooter, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 9;
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
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.gridwidth = 0;
      this.jPanelGeneral.add(this.jPanelOkCancel, gridBagConstraints);
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jLabelOverlapMouseReleased(MouseEvent evt) {
      JOptionPane.showMessageDialog(this, I18N.MSG_ALERT_OVERLAP_HELP.val(), I18N.WORD_OVERLAP.val(), 3);
   }

   public static void main(AlertSettings alertSettings) {
      SwingUtilities.invokeLater(() -> new AlertSettingsDialog(alertSettings).setVisible(true));
   }

   private void ok() {
      this.alertSettings.setBackground(this.componentExample.getBackground());
      this.alertSettings.setForeground(this.componentExample.getForeground());
      this.alertSettings.setFontName(String.valueOf(this.jComboBoxFontName.getSelectedItem()));
      this.alertSettings.setBold(this.jToggleButtonBold.isSelected());
      this.alertSettings.setLineHeight(String.valueOf(this.jComboBoxLineHeight.getSelectedItem()));
      this.alertSettings.setVelocity(((SpinnerNumberModel)this.jSpinnerVelocity.getModel()).getNumber().doubleValue());
      this.alertSettings.setFrameSkip(((SpinnerNumberModel)this.jSpinnerFrameSkip.getModel()).getNumber().intValue());
      this.alertSettings.setTop(this.jComboBoxLocation.getSelectedIndex() == 0);
      this.alertSettings.setOverlap(this.jCheckBoxOverlap.isSelected());
      this.alertSettings.setBackground(this.jCheckBoxBackground.isSelected());
      this.alertSettings.save();
      this.dispose();
      WallpaperView.update();
   }

   private Color getColor(Color def) {
      WebColorChooserDialog wccd = new WebColorChooserDialog(this);
      Component c = SwingUtils.searchComponent(wccd, JCheckBox.class);
      if (c instanceof JCheckBox) {
         ((JCheckBox)c).setVisible(false);
      }

      wccd.setColor(def);
      wccd.setVisible(true);
      return wccd.getResult() == 0 ? wccd.getColor() : def;
   }

   private Font getFontExample() {
      return new Font(String.valueOf(this.jComboBoxFontName.getSelectedItem()), this.jToggleButtonBold.isSelected() ? 1 : 0, 36);
   }

   private JComponent createExample() {
      return new AlertPanelAnimated() {
         private final String text = IntStream.range(0, 8).mapToObj(i -> "Holyrics").collect(Collectors.joining(" "));

         @Override
         public boolean paint() {
            return true;
         }

         @Override
         public String getContent() {
            return this.text;
         }

         @Override
         public Color getBackgroundColor() {
            return AlertSettingsDialog.this.jCheckBoxBackground.isSelected() ? this.getBackground() : WebPanelStyle.backgroundColor;
         }

         @Override
         public Color getForegroundColor() {
            return this.getForeground();
         }

         @Override
         public Font getTextFont() {
            return this.getFont();
         }

         @Override
         public double getVelocity() {
            return ((SpinnerNumberModel)AlertSettingsDialog.this.jSpinnerVelocity.getModel()).getNumber().doubleValue();
         }

         @Override
         public boolean stopAnimation() {
            return AlertSettingsDialog.this.stopAnimation;
         }

         @Override
         public void fireChange() {
         }

         @Override
         public boolean isShow() {
            return true;
         }

         @Override
         public boolean isOverlap() {
            return true;
         }

         @Override
         public int skipFrames() {
            return ((SpinnerNumberModel)AlertSettingsDialog.this.jSpinnerFrameSkip.getModel()).getNumber().intValue();
         }

         @Override
         public boolean isStageView() {
            return false;
         }
      };
   }

   private ComboBoxModel<String> createLineHeightModel() {
      return new DefaultComboBoxModel<>(IntStream.rangeClosed(5, 15).mapToObj(i -> i + "%").toArray(String[]::new));
   }
}
