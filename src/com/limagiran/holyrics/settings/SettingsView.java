package com.limagiran.holyrics.settings;

import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.view.ClockSettingsDialog;
import com.limagiran.holyrics.view.WallpaperView;
import com.limagiran.holyrics.webservice.HolyricsWS;
import com.limagiran.holyrics.webservice.RemoteControlBibleWS;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.JSpinner.NumberEditor;

public class SettingsView extends JDialog implements Values {
   private static SettingsView INSTANCE;
   private Runnable callback;
   private Filler filler1;
   private JButton jButtonClockSettings;
   private JButton jButtonOk;
   private JButton jButtonWallpaper;
   private JCheckBox jCheckBoxDisplayClock;
   private JCheckBox jCheckBoxDisplayOnAllMonitors;
   private JCheckBox jCheckBoxDisplayWallpaper;
   private JCheckBox jCheckBoxEnableRemoteControl;
   private JCheckBox jCheckBoxEnableStream;
   private JCheckBox jCheckBoxFadeInOut;
   private JLabel jLabelAccessCodeControlMedia;
   private JLabel jLabelAccessCodePresentation;
   private JLabel jLabelAccessCodeRemoteControlBible;
   private JLabel jLabelAccessCodeUpdate;
   private JLabel jLabelThemeSampleText;
   private JPanel jPanelButtons;
   private JPanel jPanelClock;
   private JPanel jPanelFadeInOut;
   private JPanel jPanelGeneral;
   private JPanel jPanelGeral;
   private JPanel jPanelSet1;
   private JPanel jPanelThemeSampleText;
   private JScrollPane jScrollPaneThemeSampleText;
   private JSeparator jSeparator1;
   private JSeparator jSeparator2;
   private JSeparator jSeparator3;
   private JSeparator jSeparator4;
   private JSpinner jSpinnerFadeInOutDuration;
   private JTextArea jTextAreaThemeSampleText;
   private JTextField jTextFieldAccessCodeControlMedia;
   private JTextField jTextFieldAccessCodePresentation;
   private JTextField jTextFieldAccessCodeRemoteControlBible;
   private JTextField jTextFieldAccessCodeUpdate;

   public SettingsView() {
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonOk.addActionListener(e -> this.exit());
      this.jButtonWallpaper.addActionListener(e -> this.changeBackgroundImage());
      this.jButtonClockSettings.addActionListener(e -> this.clockSettings());
      this.jCheckBoxEnableStream.addItemListener(e -> {
         Boolean var10000 = Settings.EnumSettings.STREAMING_ON.set(this.jCheckBoxEnableStream.isSelected());
      });
      this.jCheckBoxEnableRemoteControl.addItemListener(e -> {
         Settings.EnumSettings2.WEB_SERVICE_ENABLED.set(this.jCheckBoxEnableRemoteControl.isSelected());
         if (this.jCheckBoxEnableRemoteControl.isSelected()) {
            HolyricsWS.open();
            RemoteControlBibleWS.open();
         } else {
            HolyricsWS.close();
            RemoteControlBibleWS.close();
         }
      });
      this.jCheckBoxDisplayOnAllMonitors.addItemListener(e -> {
         Boolean var10000 = Settings.EnumSettings.DISPLAY_ON_ALL_MONITORS.set(this.jCheckBoxDisplayOnAllMonitors.isSelected());
      });
      this.jCheckBoxFadeInOut
         .addItemListener(e -> this.jSpinnerFadeInOutDuration.setEnabled(Settings.EnumSettings2.FADE_IN_OUT.set(this.jCheckBoxFadeInOut.isSelected())));
      this.jSpinnerFadeInOutDuration.setEnabled(this.jCheckBoxFadeInOut.isSelected());
      this.jCheckBoxDisplayClock.addItemListener(e -> Settings.SET.getClockSettings().setShow(this.jCheckBoxDisplayClock.isSelected()));
      this.jSpinnerFadeInOutDuration.addChangeListener(e -> {
         Integer var10000 = Settings.EnumSettings2.FADE_IN_OUT_DURATION.set(Integer.parseInt(this.jSpinnerFadeInOutDuration.getValue().toString()));
      });
      ((WebSpinner)this.jSpinnerFadeInOutDuration).setDrawFocus(false);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(121, () -> MyProperties.editDialog(this)));
      this.updateImageButton();
   }

   private void initComponents() {
      this.jPanelThemeSampleText = new JPanel();
      this.jLabelThemeSampleText = new JLabel();
      this.jScrollPaneThemeSampleText = new JScrollPane();
      this.jTextAreaThemeSampleText = new JTextArea();
      this.jPanelGeral = new JPanel();
      this.jPanelGeneral = new JPanel();
      this.jLabelAccessCodePresentation = new JLabel();
      this.jTextFieldAccessCodePresentation = SwingUtils.createJTextFieldPlaceHolder("(" + I18N.WORD_OPTIONAL.val() + ")");
      this.jLabelAccessCodeRemoteControlBible = new JLabel();
      this.jTextFieldAccessCodeRemoteControlBible = SwingUtils.createJTextFieldPlaceHolder("(" + I18N.WORD_OPTIONAL.val() + ")");
      this.jLabelAccessCodeUpdate = new JLabel();
      this.jTextFieldAccessCodeUpdate = SwingUtils.createJTextFieldPlaceHolder("(" + I18N.WORD_OPTIONAL.val() + ")");
      this.jLabelAccessCodeControlMedia = new JLabel();
      this.jTextFieldAccessCodeControlMedia = SwingUtils.createJTextFieldPlaceHolder("(" + I18N.WORD_OPTIONAL.val() + ")");
      this.jCheckBoxEnableRemoteControl = new JCheckBox();
      this.jCheckBoxEnableStream = new JCheckBox();
      this.jSeparator1 = new JSeparator();
      this.jSeparator2 = new JSeparator();
      this.jPanelSet1 = new JPanel();
      this.jCheckBoxDisplayOnAllMonitors = new JCheckBox();
      this.jPanelFadeInOut = new JPanel();
      this.jCheckBoxFadeInOut = new JCheckBox();
      this.filler1 = new Filler(new Dimension(32, 0), new Dimension(32, 0), new Dimension(32, 32767));
      this.jSpinnerFadeInOutDuration = new WebSpinner();
      this.jSeparator3 = new JSeparator();
      this.jSeparator4 = new JSeparator();
      this.jCheckBoxDisplayWallpaper = new JCheckBox();
      this.jButtonWallpaper = new JButton();
      this.jPanelClock = new JPanel();
      this.jCheckBoxDisplayClock = new JCheckBox();
      this.jButtonClockSettings = new JButton();
      this.jPanelButtons = new JPanel();
      this.jButtonOk = new JButton();
      this.jPanelThemeSampleText.setLayout(new BorderLayout());
      this.jLabelThemeSampleText.setFont(new Font("Georgia", 0, 12));
      this.jLabelThemeSampleText.setHorizontalAlignment(0);
      this.jLabelThemeSampleText.setText("Theme Sample Text");
      this.jPanelThemeSampleText.add(this.jLabelThemeSampleText, "First");
      this.jTextAreaThemeSampleText.setColumns(30);
      this.jTextAreaThemeSampleText.setRows(10);
      this.jTextAreaThemeSampleText.setText(Settings.EnumSettings.THEME_SAMPLE_TEXT.getString());
      this.jScrollPaneThemeSampleText.setViewportView(this.jTextAreaThemeSampleText);
      this.jPanelThemeSampleText.add(this.jScrollPaneThemeSampleText, "Center");
      this.setDefaultCloseOperation(0);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.settings")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setModal(true);
      this.setResizable(false);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            SettingsView.this.formWindowClosing(evt);
         }
      });
      this.jPanelGeral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeral.setLayout(new BorderLayout());
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeneral.setLayout(new GridBagLayout());
      this.jLabelAccessCodePresentation.setFont(new Font("Georgia", 0, 12));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelAccessCodePresentation.setText(bundle.getString("msg.passToDisplayLyricsAlertEtcByMobile"));
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jLabelAccessCodePresentation, gridBagConstraints);
      this.jTextFieldAccessCodePresentation.setColumns(10);
      this.jTextFieldAccessCodePresentation.setDocument(SwingUtils.createDocMaxSize(8));
      this.jTextFieldAccessCodePresentation.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldAccessCodePresentation.setHorizontalAlignment(0);
      this.jTextFieldAccessCodePresentation.setText(Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_HOLYRICS.getString());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jTextFieldAccessCodePresentation, gridBagConstraints);
      this.jLabelAccessCodeRemoteControlBible.setFont(new Font("Georgia", 0, 12));
      this.jLabelAccessCodeRemoteControlBible.setText(bundle.getString("msg.passToControlTheBibleByMobile"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jLabelAccessCodeRemoteControlBible, gridBagConstraints);
      this.jTextFieldAccessCodeRemoteControlBible.setColumns(10);
      this.jTextFieldAccessCodeRemoteControlBible.setDocument(SwingUtils.createDocMaxSize(8));
      this.jTextFieldAccessCodeRemoteControlBible.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldAccessCodeRemoteControlBible.setHorizontalAlignment(0);
      this.jTextFieldAccessCodeRemoteControlBible.setText(Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_BIBLE.getString());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jTextFieldAccessCodeRemoteControlBible, gridBagConstraints);
      this.jLabelAccessCodeUpdate.setFont(new Font("Georgia", 0, 12));
      this.jLabelAccessCodeUpdate.setText(bundle.getString("msg.passToUpdateFilesOnTheMobile"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jLabelAccessCodeUpdate, gridBagConstraints);
      this.jTextFieldAccessCodeUpdate.setColumns(10);
      this.jTextFieldAccessCodeUpdate.setDocument(SwingUtils.createDocMaxSize(8));
      this.jTextFieldAccessCodeUpdate.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldAccessCodeUpdate.setHorizontalAlignment(0);
      this.jTextFieldAccessCodeUpdate.setText(Settings.EnumSettings.PASSWORD_REMOTE_UPDATE.getString());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jTextFieldAccessCodeUpdate, gridBagConstraints);
      this.jLabelAccessCodeControlMedia.setFont(new Font("Georgia", 0, 12));
      this.jLabelAccessCodeControlMedia.setText(bundle.getString("msg.passToDisplayMedia"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jLabelAccessCodeControlMedia, gridBagConstraints);
      this.jTextFieldAccessCodeControlMedia.setColumns(10);
      this.jTextFieldAccessCodeControlMedia.setDocument(SwingUtils.createDocMaxSize(8));
      this.jTextFieldAccessCodeControlMedia.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldAccessCodeControlMedia.setHorizontalAlignment(0);
      this.jTextFieldAccessCodeControlMedia.setText(Settings.EnumSettings2.PASSWORD_REMOTE_CONTROL_MEDIA_HOLYRICS.getString());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jTextFieldAccessCodeControlMedia, gridBagConstraints);
      this.jCheckBoxEnableRemoteControl.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxEnableRemoteControl.setSelected(Settings.EnumSettings2.WEB_SERVICE_ENABLED.is());
      this.jCheckBoxEnableRemoteControl.setText(bundle.getString("msg.enableAppRemoteControl"));
      this.jCheckBoxEnableRemoteControl.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 4, 0);
      this.jPanelGeneral.add(this.jCheckBoxEnableRemoteControl, gridBagConstraints);
      this.jCheckBoxEnableStream.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxEnableStream.setSelected(Settings.EnumSettings.STREAMING_ON.is());
      this.jCheckBoxEnableStream.setText(bundle.getString("msg.enableSlideStreamToApp"));
      this.jCheckBoxEnableStream.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 4, 0);
      this.jPanelGeneral.add(this.jCheckBoxEnableStream, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jSeparator1, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jSeparator2, gridBagConstraints);
      this.jPanelSet1.setLayout(new GridLayout(2, 1, 0, 6));
      this.jCheckBoxDisplayOnAllMonitors.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxDisplayOnAllMonitors.setSelected(Settings.EnumSettings.DISPLAY_ON_ALL_MONITORS.is());
      this.jCheckBoxDisplayOnAllMonitors.setText(bundle.getString("msg.displayOnAllAuxiliaryMonitors"));
      this.jCheckBoxDisplayOnAllMonitors.setFocusable(false);
      this.jPanelSet1.add(this.jCheckBoxDisplayOnAllMonitors);
      this.jPanelFadeInOut.setOpaque(false);
      this.jPanelFadeInOut.setLayout(new FlowLayout(0, 0, 0));
      this.jCheckBoxFadeInOut.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxFadeInOut.setSelected(Settings.EnumSettings2.FADE_IN_OUT.is());
      this.jCheckBoxFadeInOut.setText("Fade in/out");
      this.jCheckBoxFadeInOut.setFocusable(false);
      this.jPanelFadeInOut.add(this.jCheckBoxFadeInOut);
      this.jPanelFadeInOut.add(this.filler1);
      this.jSpinnerFadeInOutDuration.setFont(new Font("Georgia", 0, 12));
      this.jSpinnerFadeInOutDuration.setModel(new SpinnerNumberModel(Settings.EnumSettings2.FADE_IN_OUT_DURATION.getInt(), 200, 1200, 100));
      this.jSpinnerFadeInOutDuration.setEditor(new NumberEditor(this.jSpinnerFadeInOutDuration, "# 'ms'"));
      this.jPanelFadeInOut.add(this.jSpinnerFadeInOutDuration);
      this.jPanelSet1.add(this.jPanelFadeInOut);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 8;
      gridBagConstraints.gridwidth = 2;
      this.jPanelGeneral.add(this.jPanelSet1, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 9;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jSeparator3, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 2);
      this.jPanelGeneral.add(this.jSeparator4, gridBagConstraints);
      this.jCheckBoxDisplayWallpaper.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxDisplayWallpaper.setSelected(Settings.EnumSettings.DISPLAY_WALLPAPER.is());
      this.jCheckBoxDisplayWallpaper.setText(bundle.getString("msg.displayWallpaper"));
      this.jCheckBoxDisplayWallpaper.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 11;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(4, 0, 4, 0);
      this.jPanelGeneral.add(this.jCheckBoxDisplayWallpaper, gridBagConstraints);
      this.jButtonWallpaper.setFont(new Font("Georgia", 0, 12));
      this.jButtonWallpaper.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/addFile16.png")));
      this.jButtonWallpaper.setText(bundle.getString("msg.clickToChange"));
      this.jButtonWallpaper.setCursor(new Cursor(12));
      this.jButtonWallpaper.setFocusable(false);
      this.jButtonWallpaper.setHorizontalTextPosition(0);
      this.jButtonWallpaper.setPreferredSize(new Dimension(180, 130));
      this.jButtonWallpaper.setVerticalTextPosition(3);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 12;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.weightx = 1.0;
      this.jPanelGeneral.add(this.jButtonWallpaper, gridBagConstraints);
      this.jCheckBoxDisplayClock.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxDisplayClock.setSelected(Settings.SET.getClockSettings().isShow());
      this.jCheckBoxDisplayClock.setText(bundle.getString("msg.displayClock"));
      this.jCheckBoxDisplayClock.setFocusable(false);
      this.jPanelClock.add(this.jCheckBoxDisplayClock);
      this.jButtonClockSettings.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/settings16.png")));
      this.jButtonClockSettings.setFocusable(false);
      this.jPanelClock.add(this.jButtonClockSettings);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 13;
      gridBagConstraints.gridwidth = 0;
      this.jPanelGeneral.add(this.jPanelClock, gridBagConstraints);
      this.jPanelGeral.add(this.jPanelGeneral, "Center");
      this.jPanelButtons.setLayout(new FlowLayout(1, 30, 2));
      this.jButtonOk.setFont(new Font("Georgia", 0, 14));
      this.jButtonOk.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.ok")));
      this.jButtonOk.setFocusable(false);
      this.jButtonOk.setPreferredSize(new Dimension(80, 31));
      this.jPanelButtons.add(this.jButtonOk);
      this.jPanelGeral.add(this.jPanelButtons, "Last");
      this.getContentPane().add(this.jPanelGeral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.exit();
   }

   public static void main(Runnable callback) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new SettingsView();
         }

         INSTANCE.show(callback);
      });
   }

   public void show(Runnable callback) {
      this.callback = callback;
      super.setVisible(true);
   }

   private void exit() {
      Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_BIBLE.set(Utils.notOnlySpace(this.jTextFieldAccessCodeRemoteControlBible.getText()));
      Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_HOLYRICS.set(Utils.notOnlySpace(this.jTextFieldAccessCodePresentation.getText()));
      Settings.EnumSettings2.PASSWORD_REMOTE_CONTROL_MEDIA_HOLYRICS.set(Utils.notOnlySpace(this.jTextFieldAccessCodeControlMedia.getText()));
      Settings.EnumSettings.PASSWORD_REMOTE_UPDATE.set(Utils.notOnlySpace(this.jTextFieldAccessCodeUpdate.getText()));
      Settings.EnumSettings.DISPLAY_ON_ALL_MONITORS.set(this.jCheckBoxDisplayOnAllMonitors.isSelected());
      Settings.EnumSettings.DISPLAY_WALLPAPER.set(this.jCheckBoxDisplayWallpaper.isSelected());
      WallpaperView.update();
      Optional.ofNullable(this.callback).ifPresent(r -> r.run());
      this.dispose();
   }

   private void changeBackgroundImage() {
      Optional.ofNullable(Dir.windowsImage(I18N.WORD_WALLPAPER.val())).ifPresent(f -> PopUpWorker.exe(new MySwingWorker<String, Void>(this) {
            protected String doInBackground() throws Exception {
               try {
                  Settings.EnumSettings.WALLPAPER_IMAGE64.set(Img.encodeToString(ImageIO.read(f), "PNG"));
                  return "t";
               } catch (IOException var2) {
                  return "f" + I18N.MSG_ERROR_INVALID_FILE.val();
               } catch (Exception var3) {
                  return "f" + I18N.MSG_ERROR_TRY_AGAIN.val();
               }
            }

            @Override
            protected void done() {
               super.done();
               if (this.isOk()) {
                  SettingsView.this.updateImageButton();
                  WallpaperView.update();
               } else {
                  this.showResult();
               }
            }
         }));
   }

   private void updateImageButton() {
      BufferedImage bi = Images.loadFrom64(Settings.EnumSettings.WALLPAPER_IMAGE64.getString());
      this.jButtonWallpaper.setIcon(new ImageIcon(Img.resize(bi, new Dimension(160, 90))));
   }

   private void clockSettings() {
      ClockSettingsDialog.main(Settings.SET.getClockSettings(), WallpaperView::update);
   }
}
