package com.limagiran.holyrics.view;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.HolyricsStreaming;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.view.panel.MultiSlidePreview;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class CreateImageDialog extends JDialog {
   private static CreateImageDialog INSTANCE;
   private final MultiSlidePreview multiSlidePreview = new MultiSlidePreview();
   private ButtonGroup buttonGroupFormat;
   private ButtonGroup buttonGroupScreenType;
   private JButton jButtonSave;
   private JComboBox<Theme> jComboBoxThemes;
   private JPanel jPanelFormat;
   private JPanel jPanelLeft;
   private JPanel jPanelLeftCenter;
   private JPanel jPanelLeftFooter;
   private JPanel jPanelScreenType;
   private JPanel jPanelSettings;
   private JPanel jPanelSettingsLeft;
   private JRadioButton jRadioButtonJPG;
   private JRadioButton jRadioButtonPNG;
   private JRadioButton jRadioButtonStandard;
   private JRadioButton jRadioButtonWidescreen;
   private JScrollPane jScrollPaneQuickPresentation;
   private JSeparator jSeparator;
   private JTextArea jTextAreaQuickPresentation;

   public CreateImageDialog(Frame parent) {
      super(parent, true);
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jTextAreaQuickPresentation.setDocument(SwingUtils.createDocMaxSize(2048));
      this.jTextAreaQuickPresentation.setText(Settings.EnumSettings2.CREATE_IMAGE_TEXT.getString());
      this.jTextAreaQuickPresentation.selectAll();
      this.jTextAreaQuickPresentation.getDocument().addDocumentListener(new DocumentListenerAuto(400L, () -> {
         String var10000 = Settings.EnumSettings2.CREATE_IMAGE_TEXT.set(this.jTextAreaQuickPresentation.getText());
      }));
      this.jButtonSave.addActionListener(e -> this.save());
      this.jComboBoxThemes.setSelectedItem(Settings.SET.getCreateImageTheme());
      this.jTextAreaQuickPresentation.getDocument().addDocumentListener(new DocumentListenerAuto(500L, this::refreshPreview));
      this.jRadioButtonWidescreen.addItemListener(this::changeScreenType);
      this.jRadioButtonStandard.addItemListener(this::changeScreenType);
      InsertShortcut.addEscToDispose(this);
      SwingUtils.setupCCPUAndRedo(this.jTextAreaQuickPresentation);
      this.add(this.multiSlidePreview, "Center");
      this.refreshPreview();
      this.pack();
      this.setLocationRelativeTo(null);
      SwingUtils.maximize(this, 200);
   }

   private void initComponents() {
      this.buttonGroupFormat = new ButtonGroup();
      this.buttonGroupScreenType = new ButtonGroup();
      this.jPanelLeft = new JPanel();
      this.jPanelLeftCenter = new JPanel();
      this.jScrollPaneQuickPresentation = new JScrollPane();
      this.jTextAreaQuickPresentation = new JTextArea();
      this.jPanelLeftFooter = new JPanel();
      this.jSeparator = new JSeparator();
      this.jPanelSettings = new JPanel();
      this.jPanelSettingsLeft = new JPanel();
      this.jComboBoxThemes = new JComboBox<>();
      this.jButtonSave = new JButton();
      this.jPanelFormat = new JPanel();
      this.jRadioButtonPNG = new JRadioButton();
      this.jRadioButtonJPG = new JRadioButton();
      this.jPanelScreenType = new JPanel();
      this.jRadioButtonWidescreen = new JRadioButton();
      this.jRadioButtonStandard = new JRadioButton();
      this.setDefaultCloseOperation(2);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("msg.saveAs"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(800, 555));
      this.jPanelLeft.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelLeft.setLayout(new BorderLayout(5, 0));
      this.jPanelLeftCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelLeftCenter.setLayout(new BorderLayout(0, 5));
      this.jTextAreaQuickPresentation.setColumns(25);
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
            CreateImageDialog.this.jComboBoxThemesItemStateChanged(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 10);
      this.jPanelSettingsLeft.add(this.jComboBoxThemes, gridBagConstraints);
      this.jButtonSave.setFont(new Font("Georgia", 0, 14));
      this.jButtonSave.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/saveBlack32.png")));
      this.jButtonSave.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      this.jPanelSettingsLeft.add(this.jButtonSave, gridBagConstraints);
      this.jPanelFormat.setLayout(new FlowLayout(1, 25, 3));
      this.buttonGroupFormat.add(this.jRadioButtonPNG);
      this.jRadioButtonPNG.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonPNG.setSelected(true);
      this.jRadioButtonPNG.setText("PNG");
      this.jPanelFormat.add(this.jRadioButtonPNG);
      this.buttonGroupFormat.add(this.jRadioButtonJPG);
      this.jRadioButtonJPG.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonJPG.setText("JPG");
      this.jPanelFormat.add(this.jRadioButtonJPG);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      this.jPanelSettingsLeft.add(this.jPanelFormat, gridBagConstraints);
      this.jPanelScreenType.setLayout(new FlowLayout(1, 5, 3));
      this.buttonGroupScreenType.add(this.jRadioButtonWidescreen);
      this.jRadioButtonWidescreen.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonWidescreen.setSelected(true);
      this.jRadioButtonWidescreen.setText("Widescreen");
      this.jPanelScreenType.add(this.jRadioButtonWidescreen);
      this.buttonGroupScreenType.add(this.jRadioButtonStandard);
      this.jRadioButtonStandard.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonStandard.setText("Standard");
      this.jPanelScreenType.add(this.jRadioButtonStandard);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      this.jPanelSettingsLeft.add(this.jPanelScreenType, gridBagConstraints);
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

   public static void main(Frame parent) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
            INSTANCE.removeAll();
         }

         INSTANCE = new CreateImageDialog(parent);
         INSTANCE.setVisible(true);
      });
   }

   private void setTheme(Object item) {
      BooleanOptional.runIfTrue(item instanceof Theme, () -> {
         Settings.EnumSettings2.CREATE_IMAGE_THEME.set(((Theme)item).id);
         this.multiSlidePreview.setTheme((Theme)item);
      });
   }

   private void refreshPreview() {
      this.multiSlidePreview.refreshPreview(this.getPresentation());
   }

   private List<Presentation> getPresentation() {
      return Arrays.asList(this.jTextAreaQuickPresentation.getText())
         .stream()
         .map(p -> Presentation.create(new MusicController.Paragraph(p), Settings.SET.getCreateImageTheme()))
         .collect(Collectors.toList());
   }

   private void save() {
      String format = this.jRadioButtonPNG.isSelected() ? ".png" : ".jpg";
      Optional.ofNullable(Dir.windowsSave(I18N.MSG_SAVE_AS___.val(), (File)null, "Image", format))
         .ifPresent(
            f -> PopUpWorker.exe(
                  new MySwingWorker<String, Void>(this) {
                     protected String doInBackground() throws Exception {
                        try {
                           Presentation p = CreateImageDialog.this.getPresentation().get(0);
                           Dimension size = CreateImageDialog.this.jRadioButtonWidescreen.isSelected()
                              ? HolyricsStreaming.SIZE_WIDESCREEN
                              : HolyricsStreaming.SIZE_STANDARD;
                           BufferedImage bi = PresentationSlideUtils.createImage(null, p, size, true);
                           if (format.equals(".png")) {
                              ImageIO.write(bi, "png", f);
                           } else {
                              Dir.saveBytes(Img.getBytesJPG(bi, 0.9F), f);
                           }

                           return "t" + I18N.MSG_SAVED_SUCCESSFULLY.val();
                        } catch (Exception var4) {
                           return "f" + I18N.MSG_ERROR_SAVING_FILE.val() + "\n" + I18N.MSG_ERROR_TRY_AGAIN.val();
                        }
                     }

                     @Override
                     protected void done() {
                        super.done();
                        this.showResult();
                     }
                  }
               )
         );
   }

   private void changeScreenType(ItemEvent e) {
      if (e.getStateChange() == 1) {
         this.multiSlidePreview
            .setScreenType(
               e.getSource() == this.jRadioButtonStandard ? PresentationSlideView.EnumScreenType.STANDARD : PresentationSlideView.EnumScreenType.WIDESCREEN
            );
      }
   }
}
