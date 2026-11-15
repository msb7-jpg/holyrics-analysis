package com.limagiran.holyrics.view.panel;

import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.combobox.WebComboBoxCellRenderer;
import com.alee.laf.combobox.WebComboBoxElement;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.SaveSlides;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class SaveAsPanel extends JPanel {
   private Theme theme;
   private final SaveAsPanel.SaveAsPanelListener listener;
   private ButtonGroup buttonGroupScreenSize;
   private JButton jButtonSave;
   private JComboBox<SaveSlides.EnumType> jComboBoxSaveAs;
   private JComboBox<Theme> jComboBoxThemes;
   private JPanel jPanelSaveAs;
   private JPanel jPanelScreenType;
   private JRadioButton jRadioButtonBoth;
   private JRadioButton jRadioButtonStandard;
   private JRadioButton jRadioButtonWideScreen;

   public SaveAsPanel(Theme theme, SaveAsPanel.SaveAsPanelListener listener) {
      this.theme = theme;
      this.listener = listener;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.setupComboBoxType();
      this.jButtonSave.addActionListener(e -> this.save());
      this.jComboBoxThemes.setSelectedItem(this.theme);
   }

   private void setupComboBoxType() {
      this.jComboBoxSaveAs.setRenderer(new WebComboBoxCellRenderer() {
         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            SaveSlides.EnumType type = (SaveSlides.EnumType)value;
            WebComboBoxElement renderer = (WebComboBoxElement)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            renderer.setIcon(new ImageIcon(type.icon24));
            renderer.setText(type.name);
            return renderer;
         }
      });
   }

   private void initComponents() {
      this.buttonGroupScreenSize = new ButtonGroup();
      this.jComboBoxThemes = new JComboBox<>();
      this.jPanelScreenType = new JPanel();
      this.jRadioButtonWideScreen = new JRadioButton();
      this.jRadioButtonStandard = new JRadioButton();
      this.jRadioButtonBoth = new JRadioButton();
      this.jPanelSaveAs = new JPanel();
      this.jComboBoxSaveAs = new WebComboBox(SaveSlides.EnumType.values());
      this.jButtonSave = new JButton();
      this.setLayout(new GridBagLayout());
      this.jComboBoxThemes.setMaximumRowCount(3);
      this.jComboBoxThemes.setModel(new DefaultComboBoxModel<>(ThemeController.getThemes().toArray(new Theme[0])));
      this.jComboBoxThemes.setFocusable(false);
      this.jComboBoxThemes.setPreferredSize(new Dimension(180, 96));
      this.jComboBoxThemes.setRenderer(new ComboBoxRendererTheme(160, 90));
      this.jComboBoxThemes.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            SaveAsPanel.this.jComboBoxThemesItemStateChanged(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.insets = new Insets(0, 0, 10, 0);
      this.add(this.jComboBoxThemes, gridBagConstraints);
      this.jPanelScreenType.setBorder(BorderFactory.createEmptyBorder(2, 1, 4, 1));
      this.jPanelScreenType.setLayout(new GridBagLayout());
      this.buttonGroupScreenSize.add(this.jRadioButtonWideScreen);
      this.jRadioButtonWideScreen.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jRadioButtonWideScreen.setSelected(true);
      this.jRadioButtonWideScreen.setText("Widescreen (16:9)");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 6, 0, 6);
      this.jPanelScreenType.add(this.jRadioButtonWideScreen, gridBagConstraints);
      this.buttonGroupScreenSize.add(this.jRadioButtonStandard);
      this.jRadioButtonStandard.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jRadioButtonStandard.setText("Standard (4:3)");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 6, 0, 6);
      this.jPanelScreenType.add(this.jRadioButtonStandard, gridBagConstraints);
      this.buttonGroupScreenSize.add(this.jRadioButtonBoth);
      this.jRadioButtonBoth.setFont(new Font("Franklin Gothic Medium", 0, 14));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jRadioButtonBoth.setText(bundle.getString("word.both"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 6, 0, 6);
      this.jPanelScreenType.add(this.jRadioButtonBoth, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      this.add(this.jPanelScreenType, gridBagConstraints);
      this.jPanelSaveAs.setLayout(new FlowLayout(1, 5, 0));
      this.jComboBoxSaveAs.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jComboBoxSaveAs.setFocusable(false);
      this.jPanelSaveAs.add(this.jComboBoxSaveAs);
      this.jButtonSave.setFont(new Font("Georgia", 0, 14));
      this.jButtonSave.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/saveBlack24.png")));
      this.jButtonSave.setFocusable(false);
      this.jPanelSaveAs.add(this.jButtonSave);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 0;
      this.add(this.jPanelSaveAs, gridBagConstraints);
   }

   private void jComboBoxThemesItemStateChanged(ItemEvent evt) {
      this.theme = (Theme)this.jComboBoxThemes.getSelectedItem();
   }

   public Theme getTheme() {
      return Optional.ofNullable(this.theme).orElse(ThemeController.getDefault());
   }

   public SaveSlides.EnumType getType() {
      return (SaveSlides.EnumType)this.jComboBoxSaveAs.getSelectedItem();
   }

   public List<Dimension> getSizes() {
      return this.jRadioButtonBoth.isSelected()
         ? Arrays.asList(HolyricsStreaming.SIZE_WIDESCREEN, HolyricsStreaming.SIZE_STANDARD)
         : Arrays.asList(this.jRadioButtonWideScreen.isSelected() ? HolyricsStreaming.SIZE_WIDESCREEN : HolyricsStreaming.SIZE_STANDARD);
   }

   private void save() {
      BooleanOptional.runIfTrue(
         this.listener.validateSave(),
         () -> Optional.ofNullable(Dir.windowsSave(I18N.MSG_SAVE_AS___.val(), this.getType().name, this.getType().extension))
               .ifPresent(this.listener::buttonSaveClicked)
      );
   }

   public interface SaveAsPanelListener {
      void buttonSaveClicked(File var1);

      boolean validateSave();
   }
}
