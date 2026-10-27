package com.limagiran.holyrics.playback;

import com.limagiran.holyrics.control.MusicFavoriteSetController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ComboBoxModelTheme;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.transition.TransitionSettingsDialog;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.MusicMain;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.event.HyperlinkEvent.EventType;

public class ExportToVideoDialog extends JDialog {
   private static final String[] ARRAY_SIZES = new String[]{"Widescreen", "Standard", I18N.WORD_CUSTOM.val()};
   private final PlayBack playback;
   private Theme theme;
   private final Dimension size = new Dimension(-1, -1);
   private File file;
   private Filler filler1;
   private JButton jButtonCancel;
   private JButton jButtonCreateVideo;
   private JButton jButtonFile;
   private JComboBox<String> jComboBoxSize;
   private JComboBox<Theme> jComboBoxTheme;
   private JEditorPane jEditorPaneInfo;
   private JLabel jLabelFile;
   private JLabel jLabelHeight;
   private JLabel jLabelInfo;
   private JLabel jLabelSelectedFile;
   private JLabel jLabelSize;
   private JLabel jLabelTheme;
   private JLabel jLabelWidth;
   private JLabel jLabeltitle;
   private JPanel jPanel;
   private JPanel jPanelCenter;
   private JPanel jPanelFile;
   private JPanel jPanelFooter;
   private JPanel jPanelSize;
   private JTextField jTextFieldHeight;
   private JTextField jTextFieldWidth;

   private ExportToVideoDialog(PlayBack playback) {
      this.playback = playback;
      this.initComponents();
      this.init();
   }

   private void init() {
      Theme _theme = MusicFavoriteSetController.getDefaultTheme(this.playback.getMusic().getId());
      if (_theme == null) {
         _theme = Settings.SET.getTheme();
      }

      this.jLabelInfo.setText(SwingUtils.getHTMLCenterJLabel(this.playback.toString()));
      this.jComboBoxTheme.setSelectedItem(_theme);
      if (this.jComboBoxTheme.getSelectedIndex() < 0) {
         this.jComboBoxTheme.setSelectedIndex(0);
      }

      this.jTextFieldWidth.setDocument(SwingUtils.createDocOnlyNumber(4));
      this.jTextFieldHeight.setDocument(SwingUtils.createDocOnlyNumber(4));
      this.changeSize(0);
      this.jComboBoxSize.addItemListener(e -> {
         if (e.getStateChange() == 1) {
            this.changeSize(this.jComboBoxSize.getSelectedIndex());
         }
      });
      ScreenDeviceSettings sds = Settings.SET.getScreenDeviceSettings(1);
      this.jButtonFile.addActionListener(evt -> this.selectFile());
      this.jButtonCreateVideo.addActionListener(evt -> this.create());
      this.jButtonCancel.addActionListener(evt -> this.dispose());
      this.jEditorPaneInfo.addHyperlinkListener(e -> Utils.safeException(() -> {
            if (e.getEventType() == EventType.ACTIVATED) {
               String var2x = e.getURL().toString();
               switch (var2x) {
                  case "http://goto-transition":
                     TransitionSettingsDialog.main();
                     break;
                  case "http://goto-displaySet":
                     MusicMain.openDisplaySettings(this);
               }
            }
         }));
      InsertShortcut.addEscToDispose(this);
   }

   private void initComponents() {
      this.jPanel = new JPanel();
      this.jLabeltitle = new JLabel();
      this.jPanelCenter = new JPanel();
      this.jLabelInfo = new JLabel();
      this.jLabelTheme = new JLabel();
      this.jComboBoxTheme = new JComboBox<>();
      this.jLabelSize = new JLabel();
      this.jPanelSize = new JPanel();
      this.jComboBoxSize = new JComboBox<>();
      this.filler1 = new Filler(new Dimension(8, 0), new Dimension(8, 0), new Dimension(8, 32767));
      this.jLabelWidth = new JLabel();
      this.jTextFieldWidth = new JTextField();
      this.jLabelHeight = new JLabel();
      this.jTextFieldHeight = new JTextField();
      this.jLabelFile = new JLabel();
      this.jPanelFile = new JPanel();
      this.jButtonFile = new JButton();
      this.jLabelSelectedFile = new JLabel();
      this.jEditorPaneInfo = new JEditorPane();
      this.jPanelFooter = new JPanel();
      this.jButtonCancel = new JButton();
      this.jButtonCreateVideo = new JButton();
      this.setDefaultCloseOperation(2);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.export"));
      this.setIconImage(null);
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(650, 500));
      this.setModal(true);
      this.setPreferredSize(new Dimension(650, 500));
      this.setResizable(false);
      this.setSize(new Dimension(650, 500));
      this.jPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      this.jPanel.setLayout(new BorderLayout());
      this.jLabeltitle.setFont(new Font("Arial", 1, 18));
      this.jLabeltitle.setHorizontalAlignment(0);
      this.jLabeltitle.setText(bundle.getString("msg.exportToVideoTT"));
      this.jLabeltitle.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.jPanel.add(this.jLabeltitle, "First");
      this.jPanelCenter.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
      this.jPanelCenter.setLayout(new GridBagLayout());
      this.jLabelInfo.setFont(new Font("Trebuchet MS", 0, 18));
      this.jLabelInfo.setHorizontalAlignment(0);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(16, 0, 16, 0);
      this.jPanelCenter.add(this.jLabelInfo, gridBagConstraints);
      this.jLabelTheme.setFont(new Font("Georgia", 0, 14));
      this.jLabelTheme.setText(bundle.getString("word.theme"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 4, 4);
      this.jPanelCenter.add(this.jLabelTheme, gridBagConstraints);
      this.jComboBoxTheme.setFont(new Font("Georgia", 0, 14));
      this.jComboBoxTheme.setMaximumRowCount(4);
      this.jComboBoxTheme.setModel(new ComboBoxModelTheme());
      this.jComboBoxTheme.setFocusable(false);
      this.jComboBoxTheme.setRenderer(new ComboBoxRendererTheme(112, 63));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 4, 4, 0);
      this.jPanelCenter.add(this.jComboBoxTheme, gridBagConstraints);
      this.jLabelSize.setFont(new Font("Georgia", 0, 14));
      this.jLabelSize.setText(bundle.getString("word.size"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 4, 4);
      this.jPanelCenter.add(this.jLabelSize, gridBagConstraints);
      this.jPanelSize.setLayout(new BoxLayout(this.jPanelSize, 2));
      this.jComboBoxSize.setFont(new Font("Georgia", 0, 14));
      this.jComboBoxSize.setModel(new DefaultComboBoxModel<>(ARRAY_SIZES));
      this.jComboBoxSize.setFocusable(false);
      this.jPanelSize.add(this.jComboBoxSize);
      this.jPanelSize.add(this.filler1);
      this.jLabelWidth.setFont(new Font("Georgia", 0, 14));
      this.jLabelWidth.setText(bundle.getString("word.width"));
      this.jLabelWidth.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
      this.jPanelSize.add(this.jLabelWidth);
      this.jTextFieldWidth.setEditable(false);
      this.jTextFieldWidth.setColumns(4);
      this.jTextFieldWidth.setFont(new Font("Georgia", 0, 14));
      this.jTextFieldWidth.addFocusListener(new FocusAdapter() {
         @Override
         public void focusGained(FocusEvent evt) {
            ExportToVideoDialog.this.jTextFieldSizeFocusGained(evt);
         }
      });
      this.jPanelSize.add(this.jTextFieldWidth);
      this.jLabelHeight.setFont(new Font("Georgia", 0, 14));
      this.jLabelHeight.setText(bundle.getString("word.height"));
      this.jLabelHeight.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 2));
      this.jPanelSize.add(this.jLabelHeight);
      this.jTextFieldHeight.setEditable(false);
      this.jTextFieldHeight.setColumns(4);
      this.jTextFieldHeight.setFont(new Font("Georgia", 0, 14));
      this.jTextFieldHeight.addFocusListener(new FocusAdapter() {
         @Override
         public void focusGained(FocusEvent evt) {
            ExportToVideoDialog.this.jTextFieldSizeFocusGained(evt);
         }
      });
      this.jPanelSize.add(this.jTextFieldHeight);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 4, 4, 0);
      this.jPanelCenter.add(this.jPanelSize, gridBagConstraints);
      this.jLabelFile.setFont(new Font("Georgia", 0, 14));
      this.jLabelFile.setText(bundle.getString("word.save"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 4, 4);
      this.jPanelCenter.add(this.jLabelFile, gridBagConstraints);
      this.jPanelFile.setLayout(new BoxLayout(this.jPanelFile, 2));
      this.jButtonFile.setFont(new Font("Georgia", 0, 14));
      this.jButtonFile.setText(bundle.getString("word.selectVerb"));
      this.jButtonFile.setFocusable(false);
      this.jPanelFile.add(this.jButtonFile);
      this.jLabelSelectedFile.setFont(new Font("Georgia", 0, 14));
      this.jLabelSelectedFile.setText("...");
      this.jLabelSelectedFile.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
      this.jPanelFile.add(this.jLabelSelectedFile);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 4, 4, 0);
      this.jPanelCenter.add(this.jPanelFile, gridBagConstraints);
      this.jEditorPaneInfo.setEditable(false);
      this.jEditorPaneInfo.setContentType("text/html;charset=utf-8");
      this.jEditorPaneInfo.setFont(new Font("Arial", 0, 12));
      this.jEditorPaneInfo.setText(bundle.getString("msg.exportToVideoHtmlInfo"));
      this.jEditorPaneInfo.setOpaque(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(32, 8, 8, 8);
      this.jPanelCenter.add(this.jEditorPaneInfo, gridBagConstraints);
      this.jPanel.add(this.jPanelCenter, "Center");
      this.jPanelFooter.setLayout(new FlowLayout(1, 30, 5));
      this.jButtonCancel.setFont(new Font("Georgia", 0, 16));
      this.jButtonCancel.setText(bundle.getString("word.cancel"));
      this.jPanelFooter.add(this.jButtonCancel);
      this.jButtonCreateVideo.setFont(new Font("Georgia", 0, 16));
      this.jButtonCreateVideo.setText(bundle.getString("word.create"));
      this.jButtonCreateVideo.setEnabled(false);
      this.jPanelFooter.add(this.jButtonCreateVideo);
      this.jPanel.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanel, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jTextFieldSizeFocusGained(FocusEvent evt) {
      if (evt.getSource() == this.jTextFieldWidth) {
         this.jTextFieldWidth.selectAll();
      } else if (evt.getSource() == this.jTextFieldHeight) {
         this.jTextFieldHeight.selectAll();
      }
   }

   public static void main(PlayBack playback) {
      SwingUtilities.invokeLater(() -> new ExportToVideoDialog(playback).setVisible(true));
   }

   @Override
   public void dispose() {
      super.dispose();
      this.removeAll();
   }

   private void changeSize(int index) {
      boolean flag = index != 0 && index != 1;
      this.jTextFieldWidth.setEditable(flag);
      this.jTextFieldHeight.setEditable(flag);
      this.jTextFieldWidth.setFocusable(flag);
      this.jTextFieldHeight.setFocusable(flag);
      switch (index) {
         case 0:
         case 1:
            this.jTextFieldWidth.setText(index == 0 ? "1920" : "1600");
            this.jTextFieldHeight.setText(index == 0 ? "1080" : "1200");
            break;
         default:
            this.jTextFieldWidth.requestFocus();
            this.jTextFieldWidth.selectAll();
      }
   }

   private void selectFile() {
      File f = Dir.windowsSave(I18N.MSG_PLAYBACK_EXPORT_TO_VIDEO.val(), (File)null, "Video File", ".mp4");
      if (f != null) {
         this.file = f;
         SwingUtilities.invokeLater(() -> {
            this.jLabelSelectedFile.setText(StringUtils.maxLengthGetEnd(this.file.getAbsolutePath(), 32, true));
            this.jButtonCreateVideo.setEnabled(true);
         });
      }
   }

   private synchronized void capture() {
      Object obj = this.jComboBoxTheme.getSelectedItem();
      this.theme = obj instanceof Theme ? (Theme)obj : null;
      this.size.width = Utils.intOrElse(this.jTextFieldWidth.getText(), -1);
      this.size.height = Utils.intOrElse(this.jTextFieldHeight.getText(), -1);
   }

   private synchronized boolean check() {
      if (this.theme == null) {
         Utils.error(I18N.MSG_UNSELECTED_THEME.val());
         return false;
      } else if (this.size.width <= 0 || this.size.height <= 0) {
         Utils.error(I18N.MSG_INVALID_SIZE.val() + " [" + this.size.width + "x" + this.size.height + "]");
         return false;
      } else if (this.size.width > 1920 || this.size.height > 1080) {
         Utils.error(I18N.MSG_ERROR_LARGE_IMAGE.format(1920, 1080));
         return false;
      } else if (this.file == null) {
         Utils.error(I18N.MSG_ERROR_FILE_NOT_SELECTED.val());
         return false;
      } else {
         return true;
      }
   }

   private synchronized void create() {
      this.capture();
      if (this.check()) {
         PlayBackUtils.exportToVideo(this.playback, this.theme, this.file, this.size);
      }
   }
}
