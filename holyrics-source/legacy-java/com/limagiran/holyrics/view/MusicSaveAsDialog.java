package com.limagiran.holyrics.view;

import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SaveSlides;
import com.limagiran.holyrics.view.panel.SaveAsPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

public class MusicSaveAsDialog extends JDialog implements SaveAsPanel.SaveAsPanelListener {
   private final SaveAsPanel saveAsPanel;
   private final Music music;
   private JLabel jLabelArtist;
   private JLabel jLabelTitle;
   private JPanel jPanelCenter;
   private JPanel jPanelFooter;
   private JPanel jPanelGeneral;
   private JPanel jPanelSettings;
   private JSeparator jSeparator;

   public MusicSaveAsDialog(MusicMain parent, Music music, Theme theme) {
      super(parent, true);
      this.music = music;
      this.saveAsPanel = new SaveAsPanel(theme, this);
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jPanelSettings.add(this.saveAsPanel);
      this.jLabelTitle.setText(this.music.getTitle());
      this.jLabelArtist.setText(this.music.getArtist());
      InsertShortcut.addEscToDispose(this);
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelCenter = new JPanel();
      this.jLabelTitle = new JLabel();
      this.jLabelArtist = new JLabel();
      this.jPanelFooter = new JPanel();
      this.jSeparator = new JSeparator();
      this.jPanelSettings = new JPanel();
      this.setDefaultCloseOperation(2);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("msg.saveAs___"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(350, 200));
      this.setResizable(false);
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeneral.setLayout(new BorderLayout(5, 0));
      this.jPanelCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelCenter.setLayout(new GridBagLayout());
      this.jLabelTitle.setFont(new Font("Georgia", 0, 18));
      this.jLabelTitle.setHorizontalAlignment(0);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelCenter.add(this.jLabelTitle, gridBagConstraints);
      this.jLabelArtist.setFont(new Font("Georgia", 0, 16));
      this.jLabelArtist.setHorizontalAlignment(0);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelCenter.add(this.jLabelArtist, gridBagConstraints);
      this.jPanelGeneral.add(this.jPanelCenter, "Center");
      this.jPanelFooter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelFooter.setLayout(new BorderLayout(5, 0));
      this.jPanelFooter.add(this.jSeparator, "First");
      this.jPanelSettings.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelSettings.setLayout(new BorderLayout());
      this.jPanelFooter.add(this.jPanelSettings, "Last");
      this.jPanelGeneral.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(MusicMain parent, Music music, Theme theme) {
      SwingUtilities.invokeLater(() -> new MusicSaveAsDialog(parent, music, theme).setVisible(true));
   }

   @Override
   public void buttonSaveClicked(File file) {
      this.saveAsPanel.getSizes().forEach(s -> SaveSlides.save(this, file, this.music, this.saveAsPanel.getTheme(), s, this.saveAsPanel.getType()));
      this.dispose();
   }

   @Override
   public boolean validateSave() {
      return true;
   }
}
