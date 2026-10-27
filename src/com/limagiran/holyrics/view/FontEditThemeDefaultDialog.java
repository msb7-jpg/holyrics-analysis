package com.limagiran.holyrics.view;

import com.alee.extended.window.PopOverAdapter;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.colorchooser.WebColorChooserDialog;
import com.alee.laf.combobox.WebComboBox;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.WebLafUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

public class FontEditThemeDefaultDialog extends WebPopOver {
   private final WebColorChooserDialog webColorChooserDialog = new WebColorChooserDialog(this);
   private final String fontName;
   private final Boolean bold;
   private final Boolean italic;
   private final Color color;
   private final FontEditThemeDefaultDialog.FontEditThemeDefaultListener listener;
   private Runnable callback;
   private JButton jButtonColor;
   private JButton jButtonOk;
   private JCheckBox jCheckBoxBold;
   private JCheckBox jCheckBoxColor;
   private JCheckBox jCheckBoxFontName;
   private JCheckBox jCheckBoxItalic;
   private JComboBox<String> jComboBoxFontName;
   private JLabel jLabelBold;
   private JLabel jLabelColor;
   private JLabel jLabelFontName;
   private JLabel jLabelItalic;
   private JLabel jLabelTitle;
   private JPanel jPanel;
   private JPanel jPanelFooter;
   private JToggleButton jToggleButtonBold;
   private JToggleButton jToggleButtonItalic;

   private FontEditThemeDefaultDialog(
      String fontName, Boolean bold, Boolean italic, Color color, FontEditThemeDefaultDialog.FontEditThemeDefaultListener listener, Runnable callback
   ) {
      this.fontName = fontName;
      this.bold = bold;
      this.italic = italic;
      this.color = color;
      this.listener = listener;
      this.callback = callback;
      this.initComponents();
      this.init();
   }

   private void init() {
      Component c = SwingUtils.searchComponent(this.webColorChooserDialog, JCheckBox.class);
      if (c instanceof JCheckBox) {
         ((JCheckBox)c).setVisible(false);
      }

      this.getPainter().setTransparency(1.0F);
      this.setMovable(true);
      this.setResizable(false);
      InsertShortcut.addEscToDispose(this);
      this.setCloseOnFocusLoss(true);
      this.setMargin(8, 16, 6, 16);
      this.setupDialogListener();
      Arrays.asList(this.jCheckBoxFontName, this.jCheckBoxBold, this.jCheckBoxItalic, this.jCheckBoxColor).forEach(chk -> ((WebCheckBox)chk).setShadeWidth(0));
      Arrays.asList(this.jToggleButtonBold, this.jToggleButtonItalic).forEach(chk -> ((WebToggleButton)chk).setShadeWidth(0));
      ((WebComboBox)this.jComboBoxFontName).setShadeWidth(0);
      Arrays.asList(this.jButtonOk, this.jButtonColor).forEach(b -> ((WebButton)b).setShadeWidth(0));
      this.jCheckBoxFontName.setSelected(this.fontName == null);
      this.jCheckBoxBold.setSelected(this.bold == null);
      this.jCheckBoxItalic.setSelected(this.italic == null);
      this.jCheckBoxColor.setSelected(this.color == null);
      this.jComboBoxFontName.setEnabled(this.fontName != null);
      this.jToggleButtonBold.setEnabled(this.bold != null);
      this.jToggleButtonItalic.setEnabled(this.italic != null);
      this.jButtonColor.setEnabled(this.color != null);
      if (this.jComboBoxFontName.isEnabled()) {
         this.jComboBoxFontName.setSelectedItem(this.fontName);
      }

      this.jToggleButtonBold.setSelected(this.bold != null && this.bold);
      this.jToggleButtonItalic.setSelected(this.italic != null && this.italic);
      this.webColorChooserDialog.setColor(this.color != null ? this.color : Color.BLACK);
      this.jCheckBoxFontName.addActionListener(evt -> {
         this.jComboBoxFontName.setEnabled(!this.jCheckBoxFontName.isSelected());
         this.listener.setFontName(this.jCheckBoxFontName.isSelected() ? null : String.valueOf(this.jComboBoxFontName.getSelectedItem()));
      });
      this.jCheckBoxBold.addActionListener(evt -> {
         this.jToggleButtonBold.setEnabled(!this.jCheckBoxBold.isSelected());
         this.listener.setBold(this.jCheckBoxBold.isSelected() ? null : this.jToggleButtonBold.isSelected());
      });
      this.jCheckBoxItalic.addActionListener(evt -> {
         this.jToggleButtonItalic.setEnabled(!this.jCheckBoxItalic.isSelected());
         this.listener.setItalic(this.jCheckBoxItalic.isSelected() ? null : this.jToggleButtonItalic.isSelected());
      });
      this.jCheckBoxColor.addActionListener(evt -> {
         this.jButtonColor.setEnabled(!this.jCheckBoxColor.isSelected());
         this.listener.setColor(this.jCheckBoxColor.isSelected() ? null : this.webColorChooserDialog.getColor());
      });
      this.jComboBoxFontName.addActionListener(evt -> this.listener.setFontName(String.valueOf(this.jComboBoxFontName.getSelectedItem())));
      this.jToggleButtonBold.addActionListener(evt -> this.listener.setBold(this.jToggleButtonBold.isSelected()));
      this.jToggleButtonItalic.addActionListener(evt -> this.listener.setItalic(this.jToggleButtonItalic.isSelected()));
      this.jButtonColor.addActionListener(evt -> this.changeColor());
      this.jButtonOk.addActionListener(evt -> this.dispose());
   }

   private void initComponents() {
      this.jPanel = new JPanel();
      this.jLabelTitle = new JLabel();
      this.jLabelFontName = new JLabel();
      this.jLabelBold = new JLabel();
      this.jLabelItalic = new JLabel();
      this.jCheckBoxFontName = new WebCheckBox();
      this.jCheckBoxBold = new WebCheckBox();
      this.jCheckBoxItalic = new WebCheckBox();
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
            return new Dimension(200, super.getPreferredSize().height);
         }
      };
      this.jToggleButtonBold = new WebToggleButton();
      this.jToggleButtonItalic = new WebToggleButton();
      this.jLabelColor = new JLabel();
      this.jCheckBoxColor = new WebCheckBox();
      this.jButtonColor = new WebButton() {
         public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(FontEditThemeDefaultDialog.this.webColorChooserDialog.getColor());
            g2.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(6, 6, this.getWidth() - 13, this.getHeight() - 13);
            g2.dispose();
         }
      };
      this.jPanelFooter = new JPanel();
      this.jButtonOk = new WebButton();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.font"));
      this.jPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      this.jPanel.setOpaque(false);
      this.jPanel.setLayout(new GridBagLayout());
      this.jLabelTitle.setFont(new Font("Tahoma", 1, 18));
      this.jLabelTitle.setText(bundle.getString("word.font"));
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.insets = new Insets(2, 0, 9, 0);
      this.jPanel.add(this.jLabelTitle, gridBagConstraints);
      this.jLabelFontName.setFont(new Font("Arial", 0, 16));
      this.jLabelFontName.setHorizontalAlignment(4);
      this.jLabelFontName.setText(bundle.getString("word.font"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 22;
      this.jPanel.add(this.jLabelFontName, gridBagConstraints);
      this.jLabelBold.setFont(new Font("Arial", 0, 16));
      this.jLabelBold.setHorizontalAlignment(4);
      this.jLabelBold.setText(bundle.getString("word.bold"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.insets = new Insets(3, 0, 3, 0);
      this.jPanel.add(this.jLabelBold, gridBagConstraints);
      this.jLabelItalic.setFont(new Font("Arial", 0, 16));
      this.jLabelItalic.setHorizontalAlignment(4);
      this.jLabelItalic.setText(bundle.getString("word.italic"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.insets = new Insets(0, 0, 3, 0);
      this.jPanel.add(this.jLabelItalic, gridBagConstraints);
      this.jCheckBoxFontName.setFont(new Font("Arial", 0, 11));
      this.jCheckBoxFontName.setText(bundle.getString("msg.defaultOfTheme"));
      this.jCheckBoxFontName.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.insets = new Insets(0, 8, 0, 8);
      this.jPanel.add(this.jCheckBoxFontName, gridBagConstraints);
      this.jCheckBoxBold.setFont(new Font("Arial", 0, 11));
      this.jCheckBoxBold.setText(bundle.getString("msg.defaultOfTheme"));
      this.jCheckBoxBold.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.insets = new Insets(3, 8, 3, 8);
      this.jPanel.add(this.jCheckBoxBold, gridBagConstraints);
      this.jCheckBoxItalic.setFont(new Font("Arial", 0, 11));
      this.jCheckBoxItalic.setText(bundle.getString("msg.defaultOfTheme"));
      this.jCheckBoxItalic.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.insets = new Insets(0, 0, 3, 0);
      this.jPanel.add(this.jCheckBoxItalic, gridBagConstraints);
      this.jComboBoxFontName.setFont(new Font("Arial", 0, 14));
      this.jComboBoxFontName.setMaximumRowCount(12);
      this.jComboBoxFontName.setModel(new DefaultComboBoxModel<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
      this.jComboBoxFontName.setFocusable(false);
      this.jComboBoxFontName.setRenderer(WebLafUtils.createComboBoxRendererFontName(36));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 21;
      this.jPanel.add(this.jComboBoxFontName, gridBagConstraints);
      this.jToggleButtonBold.setFont(new Font("Georgia", 1, 14));
      this.jToggleButtonBold.setText(bundle.getString("word.firstCharBold"));
      this.jToggleButtonBold.setFocusable(false);
      this.jToggleButtonBold.setPreferredSize(new Dimension(40, 25));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(3, 0, 3, 0);
      this.jPanel.add(this.jToggleButtonBold, gridBagConstraints);
      this.jToggleButtonItalic.setFont(new Font("Georgia", 2, 14));
      this.jToggleButtonItalic.setText(bundle.getString("word.firstCharItalic"));
      this.jToggleButtonItalic.setFocusable(false);
      this.jToggleButtonItalic.setPreferredSize(new Dimension(40, 25));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(0, 0, 3, 0);
      this.jPanel.add(this.jToggleButtonItalic, gridBagConstraints);
      this.jLabelColor.setFont(new Font("Arial", 0, 16));
      this.jLabelColor.setHorizontalAlignment(4);
      this.jLabelColor.setText(bundle.getString("word.color"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = 22;
      this.jPanel.add(this.jLabelColor, gridBagConstraints);
      this.jCheckBoxColor.setFont(new Font("Arial", 0, 11));
      this.jCheckBoxColor.setText(bundle.getString("msg.defaultOfTheme"));
      this.jCheckBoxColor.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.insets = new Insets(0, 8, 0, 8);
      this.jPanel.add(this.jCheckBoxColor, gridBagConstraints);
      this.jButtonColor.setFont(new Font("Georgia", 0, 14));
      this.jButtonColor.setText("    ");
      this.jButtonColor.setFocusable(false);
      this.jButtonColor.setOpaque(false);
      this.jButtonColor.setPreferredSize(new Dimension(40, 25));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = 21;
      this.jPanel.add(this.jButtonColor, gridBagConstraints);
      this.jPanelFooter.setOpaque(false);
      this.jButtonOk.setFont(new Font("Georgia", 0, 14));
      this.jButtonOk.setText(bundle.getString("word.ok"));
      this.jButtonOk.setFocusable(false);
      this.jButtonOk.setPreferredSize(new Dimension(80, 32));
      this.jPanelFooter.add(this.jButtonOk);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.insets = new Insets(12, 0, 0, 0);
      this.jPanel.add(this.jPanelFooter, gridBagConstraints);
      this.getContentPane().add(this.jPanel, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(
      String fontName, Boolean bold, Boolean italic, Color color, FontEditThemeDefaultDialog.FontEditThemeDefaultListener listener, Runnable callback
   ) {
      SwingUtilities.invokeLater(() -> {
         Point p = MouseInfo.getPointerInfo().getLocation();
         FontEditThemeDefaultDialog dialog = new FontEditThemeDefaultDialog(fontName, bold, italic, color, listener, callback);
         p.x = p.x - (dialog.getWidth() - dialog.getShadeWidth());
         p.y = p.y - (dialog.getHeight() - dialog.getShadeWidth());
         dialog.show(p);
      });
   }

   public void dispose() {
      Optional.ofNullable(this.callback).ifPresent(Runnable::run);
      this.callback = null;
      super.dispose();
   }

   private void setupDialogListener() {
      this.addPopOverListener(new PopOverAdapter() {
         public void closed(WebPopOver popOver) {
            Optional.ofNullable(FontEditThemeDefaultDialog.this.callback).ifPresent(Runnable::run);
            FontEditThemeDefaultDialog.this.callback = null;
         }
      });
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            FontEditThemeDefaultDialog.this.dispose();
         }
      });
   }

   private void changeColor() {
      this.setCloseOnFocusLoss(false);

      try {
         this.webColorChooserDialog.setColor(this.color);
         this.webColorChooserDialog.setVisible(true);
         if (this.webColorChooserDialog.getResult() == 0) {
            this.listener.setColor(this.webColorChooserDialog.getColor());
            this.jButtonColor.repaint();
            return;
         }
      } finally {
         this.setCloseOnFocusLoss(true);
      }
   }

   public interface FontEditThemeDefaultListener {
      void setFontName(String var1);

      void setBold(Boolean var1);

      void setItalic(Boolean var1);

      void setColor(Color var1);
   }
}
