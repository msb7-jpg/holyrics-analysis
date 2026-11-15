package com.limagiran.holyrics.view;

import com.alee.laf.button.WebToggleButton;
import com.alee.laf.colorchooser.WebColorChooserDialog;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.ClockSettings;
import com.limagiran.holyrics.util.ClockSettingsUtils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Values;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

public class ClockSettingsDialog extends JDialog {
   private final Runnable callback;
   private final ClockSettings clockSettings;
   private final WebToggleButton[] toggles = new WebToggleButton[9];
   private JButton jButtonBackgroundColor;
   private JButton jButtonCancel;
   private JButton jButtonFontColor;
   private JButton jButtonOk;
   private JCheckBox jCheckBoxBackground;
   private JComboBox<String> jComboBoxLineHeight;
   private JLabel jLabelBackgroundColor;
   private JLabel jLabelExample;
   private JLabel jLabelFontColor;
   private JLabel jLabelLineHeight;
   private JLabel jLabelLocation;
   private JPanel jPanelGeneral;
   private JPanel jPanelLocations;
   private JPanel jPanelOkCancel;
   private JPanel jPanelSet1;
   private JSeparator jSeparator1;

   private ClockSettingsDialog(ClockSettings clockSettings, Runnable callback) {
      this.clockSettings = clockSettings;
      this.callback = callback;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jLabelExample.setForeground(this.clockSettings.getForeground());
      this.jLabelExample.setBackground(this.clockSettings.getBackground());
      this.jLabelExample.setFont(ClockSettings.getFont(36.0F));
      this.jLabelExample.setOpaque(this.clockSettings.isBackground());
      ButtonGroup group = new ButtonGroup();
      String[] arrows = "<html>&#x2196;,↑,<html>&#x2197;,←,■,→,<html>&#x2199;,↓,<html>&#x2198;".split(",");
      IntStream.range(0, this.toggles.length).forEach(i -> {
         this.toggles[i] = new WebToggleButton(arrows[i]);
         WebToggleButton tg = this.toggles[i];
         tg.setFont(new Font("Arial", 0, 14));
         tg.setName(String.valueOf(i));
         tg.setSelected(i == this.clockSettings.getPosition());
         tg.setFocusable(false);
         tg.setShineColor(Values.COLOR_TRANSPARENT);
         group.add(tg);
         this.jPanelLocations.add(tg);
      });
      this.jComboBoxLineHeight.setSelectedItem(this.clockSettings.getLineHeight() + "%");
      this.jButtonBackgroundColor.addActionListener(e -> {
         this.jLabelExample.setBackground(this.getColor(this.jLabelExample.getBackground()));
         this.jButtonBackgroundColor.repaint();
      });
      this.jCheckBoxBackground.setSelected(this.clockSettings.isBackground());
      this.jCheckBoxBackground.addItemListener(e -> {
         this.jLabelExample.setOpaque(e.getStateChange() == 1);
         this.jLabelExample.revalidate();
         this.jLabelExample.repaint();
      });
      this.jButtonFontColor.addActionListener(e -> {
         this.jLabelExample.setForeground(this.getColor(this.jLabelExample.getForeground()));
         this.jButtonFontColor.repaint();
      });
      this.jButtonOk.addActionListener(e -> this.ok(this.callback));
      this.jButtonCancel.addActionListener(e -> this.dispose());
      InsertShortcut.addEscToDispose(this);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, () -> this.ok(this.callback)));
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jLabelFontColor = new JLabel();
      this.jButtonFontColor = new JButton() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(ClockSettingsDialog.this.jLabelExample.getForeground());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jLabelBackgroundColor = new JLabel();
      this.jPanelSet1 = new JPanel();
      this.jButtonBackgroundColor = new JButton() {
         @Override
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(ClockSettingsDialog.this.jLabelExample.getBackground());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jCheckBoxBackground = new JCheckBox();
      this.jLabelLineHeight = new JLabel();
      this.jComboBoxLineHeight = new JComboBox<>();
      this.jLabelLocation = new JLabel();
      this.jPanelLocations = new JPanel();
      this.jLabelExample = new JLabel() {
         @Override
         public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 50);
         }
      };
      this.jSeparator1 = new JSeparator();
      this.jPanelOkCancel = new JPanel();
      this.jButtonOk = new JButton();
      this.jButtonCancel = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle(I18N.WORD_CLOCK.val());
      this.setIconImages(Images.HOLYRICS_ICON);
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
      this.jPanelSet1.setLayout(flowLayout1);
      this.jButtonBackgroundColor.setFont(new Font("Georgia", 0, 12));
      this.jButtonBackgroundColor.setText("    ");
      this.jButtonBackgroundColor.setFocusable(false);
      this.jPanelSet1.add(this.jButtonBackgroundColor);
      this.jCheckBoxBackground.setFocusable(false);
      this.jCheckBoxBackground.setMargin(new Insets(2, 6, 2, 2));
      this.jPanelSet1.add(this.jCheckBoxBackground);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jPanelSet1, gridBagConstraints);
      this.jLabelLineHeight.setFont(new Font("Georgia", 0, 12));
      this.jLabelLineHeight.setText(bundle.getString("word.lineHeight"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelGeneral.add(this.jLabelLineHeight, gridBagConstraints);
      this.jComboBoxLineHeight.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxLineHeight.setModel(new DefaultComboBoxModel<>(new String[]{"6%", "8%", "10%", "12%", "15%", "20%", "25%", "30%", "35%", "40%"}));
      this.jComboBoxLineHeight.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jComboBoxLineHeight, gridBagConstraints);
      this.jLabelLocation.setFont(new Font("Georgia", 0, 12));
      this.jLabelLocation.setText(bundle.getString("word.location"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelGeneral.add(this.jLabelLocation, gridBagConstraints);
      this.jPanelLocations.setLayout(new GridLayout(3, 3, 1, 1));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 4, 0, 0);
      this.jPanelGeneral.add(this.jPanelLocations, gridBagConstraints);
      this.jLabelExample.setFont(new Font("Tahoma", 0, 36));
      this.jLabelExample.setHorizontalAlignment(0);
      this.jLabelExample.setText(ClockSettingsUtils.getTime());
      this.jLabelExample.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jLabelExample.setOpaque(true);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(4, 4, 4, 4);
      this.jPanelGeneral.add(this.jLabelExample, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
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
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 0;
      this.jPanelGeneral.add(this.jPanelOkCancel, gridBagConstraints);
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(ClockSettings clockSettings, Runnable callback) {
      SwingUtilities.invokeLater(() -> new ClockSettingsDialog(clockSettings, callback).setVisible(true));
   }

   private void ok(Runnable callback) {
      this.clockSettings.setBackground(this.jLabelExample.getBackground());
      this.clockSettings.setForeground(this.jLabelExample.getForeground());
      this.clockSettings.setLineHeight(String.valueOf(this.jComboBoxLineHeight.getSelectedItem()));
      this.clockSettings
         .setPosition(
            Arrays.asList(this.toggles)
               .stream()
               .filter(Objects::nonNull)
               .filter(AbstractButton::isSelected)
               .map(b -> Integer.parseInt(b.getName()))
               .findFirst()
               .orElse(2)
         );
      this.clockSettings.setBackground(this.jCheckBoxBackground.isSelected());
      this.clockSettings.save();
      Optional.ofNullable(callback).ifPresent(r -> r.run());
      this.dispose();
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
}
