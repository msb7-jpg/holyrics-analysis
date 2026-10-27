package com.limagiran.holyrics.view;

import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.view.panel.AutomaticSettingsPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageFileShowDialog extends JDialog {
   private static final JFileChooser FILE_CHOOSER_STATIC = SwingUtils.generateJFileChooser();
   private static final AutomaticSettingsPanel.AutomaticSettings AUTOMATIC_SETTINGS = Settings.SET.getAutomaticSettings("image");
   private static final AutomaticSettingsPanel AUTOMATIC_SETTINGS_PANEL = new AutomaticSettingsPanel(AUTOMATIC_SETTINGS, Settings.SET::saveTrue);
   private final MusicMain parent;
   private JButton jButtonShow;
   private JFileChooser jFileChooser;
   private JLabel jLabelImagePresentation;
   private JPanel jPanelAutomaticSettings;
   private JPanel jPanelCenter;
   private JPanel jPanelFooter;
   private JPanel jPanelGeneral;
   private JPanel jPanelSettings;
   private JSeparator jSeparator;
   private JSeparator jSeparator1;

   public ImageFileShowDialog(MusicMain parent) {
      super(parent, true);
      this.parent = parent;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jPanelAutomaticSettings.add(AUTOMATIC_SETTINGS_PANEL);
      this.jFileChooser.addActionListener(e -> BooleanOptional.runIfTrue(e.getActionCommand().equalsIgnoreCase("ApproveSelection"), this::showSelected));
      String path = Settings.EnumSettings.IMAGE_FILE_AUTOMATIC_SLIDE_SHOW_LAST_PATH.getString();
      File f = new File(path);
      BooleanOptional.runIfTrue(!path.isEmpty() && f.exists() && f.isDirectory(), () -> this.jFileChooser.setCurrentDirectory(f));
      this.jButtonShow.addActionListener(e -> this.showSelected());
      this.jFileChooser.addPropertyChangeListener(e -> BooleanOptional.runIfTrue(e.getPropertyName().equals("directoryChanged"), () -> {
            String var10000 = Settings.EnumSettings.IMAGE_FILE_AUTOMATIC_SLIDE_SHOW_LAST_PATH.set(e.getNewValue().toString());
         }));
      InsertShortcut.addEscToDispose(this);
      SwingUtils.maximize(this, 100);
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelCenter = new JPanel();
      this.jLabelImagePresentation = new JLabel();
      this.jFileChooser = this.getFileChooser();
      this.jPanelFooter = new JPanel();
      this.jSeparator = new JSeparator();
      this.jPanelSettings = new JPanel();
      this.jPanelAutomaticSettings = new JPanel();
      this.jSeparator1 = new JSeparator();
      this.jButtonShow = new JButton();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.announcement"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(650, 550));
      this.setPreferredSize(new Dimension(650, 550));
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            ImageFileShowDialog.this.formWindowClosing(evt);
         }
      });
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeneral.setLayout(new BorderLayout(5, 0));
      this.jPanelCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelCenter.setLayout(new BorderLayout(0, 8));
      this.jLabelImagePresentation.setFont(new Font("Georgia", 0, 18));
      this.jLabelImagePresentation.setHorizontalAlignment(0);
      this.jLabelImagePresentation.setText(bundle.getString("msg.imagePresentation"));
      this.jPanelCenter.add(this.jLabelImagePresentation, "First");
      this.jFileChooser.setAcceptAllFileFilterUsed(false);
      this.jFileChooser.setControlButtonsAreShown(false);
      this.jFileChooser.setFileFilter(fileFilter());
      this.jFileChooser.setBorder(BorderFactory.createEtchedBorder());
      this.jFileChooser.setMultiSelectionEnabled(true);
      this.jPanelCenter.add(this.jFileChooser, "Center");
      this.jPanelGeneral.add(this.jPanelCenter, "Center");
      this.jPanelFooter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelFooter.setLayout(new BorderLayout(5, 0));
      this.jPanelFooter.add(this.jSeparator, "First");
      this.jPanelSettings.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.jPanelSettings.setLayout(new GridBagLayout());
      this.jPanelAutomaticSettings.setLayout(new BorderLayout());
      this.jPanelSettings.add(this.jPanelAutomaticSettings, new GridBagConstraints());
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.jPanelSettings.add(this.jSeparator1, gridBagConstraints);
      this.jButtonShow.setFont(new Font("Georgia", 0, 14));
      this.jButtonShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack24.png")));
      this.jButtonShow.setText(bundle.getString("word.show"));
      this.jButtonShow.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 0;
      this.jPanelSettings.add(this.jButtonShow, gridBagConstraints);
      this.jPanelFooter.add(this.jPanelSettings, "Center");
      this.jPanelGeneral.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.dispose();
   }

   public static void main(MusicMain parent) {
      SwingUtilities.invokeLater(() -> new ImageFileShowDialog(parent).setVisible(true));
   }

   private void showSelected() {
      GeneratePresentation.showImages(
         this, AUTOMATIC_SETTINGS.isAutomatic(), AUTOMATIC_SETTINGS.isRepeat(), AUTOMATIC_SETTINGS.getDuration(), this.jFileChooser.getSelectedFiles()
      );
   }

   @Override
   public void dispose() {
      AUTOMATIC_SETTINGS_PANEL.commit();
      super.dispose();
   }

   public static FileFilter fileFilter() {
      String[] exs = new String[]{"jpg", "png", "gif", "bmp"};
      return new FileNameExtensionFilter("Image File " + Arrays.stream(exs).collect(Collectors.joining(", *", "(*", ")")), exs);
   }

   private JFileChooser getFileChooser() {
      return FILE_CHOOSER_STATIC;
   }
}
