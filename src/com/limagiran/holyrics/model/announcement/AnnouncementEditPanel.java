package com.limagiran.holyrics.model.announcement;

import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.event.ThemeSettingsPanelListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.view.PresentationSlideView;
import com.limagiran.holyrics.view.panel.ThemeSettingsPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AnnouncementEditPanel extends JPanel implements ThemeSettingsPanelListener {
   private final ThemeSettingsPanel themeSettingsPanel;
   private final PresentationSlideView preview;
   private final Announcement announcement;
   private final AnnouncementTitlePanel announcementTitlePanel;
   private final MusicController.Paragraph paragraph;
   private JLabel jLabelPreview;
   private JPanel jPanelHeader;
   private JPanel jPanelPreview;
   private JPanel jPanelPreviewHeader;
   private JPanel jPanelSettings;
   private JPanel jPanelThemeSettings;
   private JScrollPane jScrollPaneText;
   private JSplitPane jSplitPane;
   private JTextArea jTextAreaText;
   private JTextField jTextFieldName;

   public AnnouncementEditPanel(Announcement announcement, AnnouncementTitlePanel announcementTitlePanel) {
      this.announcement = announcement;
      this.announcementTitlePanel = announcementTitlePanel;
      this.themeSettingsPanel = new ThemeSettingsPanel(announcement.getTheme(), false, this, ThemeSettingsPanel.EnumModality.ANNOUNCEMENT);
      this.paragraph = new MusicController.Paragraph(announcement.getText(), "");
      this.preview = PresentationSlideView.builder()
         .paintBorder(true)
         .screenType(this.themeSettingsPanel.getEnumScreenType())
         .theme(announcement.getTheme())
         .text(this.paragraph)
         .getSlideView();
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jPanelPreview.add(this.preview, "Center");
      WebLafUtils.setupWebLafBorderLayout(this.jPanelThemeSettings);
      this.jTextFieldName.setDocument(SwingUtils.createDocMaxSize(48));
      this.jTextFieldName.setText(this.announcement.getName());
      this.jTextFieldName.getDocument().addDocumentListener(new DocumentListenerAuto(400L, () -> {
         this.announcement.setName(this.jTextFieldName.getText());
         AnnouncementController.save();
         this.announcementTitlePanel.update();
      }));
      this.jTextAreaText.setDocument(SwingUtils.createDocMaxSize(2048));
      this.jTextAreaText.setText(this.announcement.getText());
      this.jTextAreaText.getDocument().addDocumentListener(new DocumentListenerAuto(400L, () -> {
         this.announcement.setText(this.jTextAreaText.getText());
         AnnouncementController.save();
         this.paragraph.setText(this.announcement.getText());
         this.preview.repaint();
      }));
      this.jPanelThemeSettings.add(this.themeSettingsPanel, "Center");
      this.jPanelPreviewHeader.add(this.themeSettingsPanel.getComboBoxScreenType());
      SwingUtils.setupCCPUAndRedo(this.jTextAreaText);
      SwingUtils.setJPanelToOpaqueFalse(this);
   }

   @Override
   public void requestFocus() {
      this.jTextFieldName.requestFocus();
   }

   private void initComponents() {
      this.jPanelHeader = new JPanel();
      this.jPanelThemeSettings = new WebPanel(true);
      this.jSplitPane = new JSplitPane();
      this.jPanelSettings = new JPanel();
      this.jTextFieldName = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_TITLE.val());
      this.jScrollPaneText = new JScrollPane();
      this.jTextAreaText = SwingUtils.createJTextAreaPlaceHolder(I18N.WORD_MESSAGE.val());
      this.jPanelPreview = new JPanel();
      this.jPanelPreviewHeader = new JPanel();
      this.jLabelPreview = new JLabel();
      this.setLayout(new BorderLayout());
      this.jPanelHeader.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jPanelThemeSettings.setLayout(new BorderLayout());
      this.jPanelHeader.add(this.jPanelThemeSettings, "Center");
      this.add(this.jPanelHeader, "First");
      this.jPanelSettings.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelSettings.setLayout(new BorderLayout(0, 4));
      this.jTextFieldName.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jTextFieldName.setHorizontalAlignment(0);
      this.jPanelSettings.add(this.jTextFieldName, "First");
      this.jTextAreaText.setColumns(30);
      this.jTextAreaText.setRows(5);
      this.jScrollPaneText.setViewportView(this.jTextAreaText);
      this.jPanelSettings.add(this.jScrollPaneText, "Center");
      this.jSplitPane.setLeftComponent(this.jPanelSettings);
      this.jPanelPreview.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.jPanelPreview.setLayout(new BorderLayout());
      this.jLabelPreview.setFont(new Font("Georgia", 0, 14));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelPreview.setText(bundle.getString("word.preview"));
      this.jPanelPreviewHeader.add(this.jLabelPreview);
      this.jPanelPreview.add(this.jPanelPreviewHeader, "First");
      this.jSplitPane.setRightComponent(this.jPanelPreview);
      this.add(this.jSplitPane, "Center");
   }

   private void update() {
      this.preview.setScreenType(this.themeSettingsPanel.getEnumScreenType());
   }

   @Override
   public void themeChanged() {
      this.update();
   }

   @Override
   public void backgroundChanged() {
      this.update();
   }

   @Override
   public void saveButtonClicked() {
   }

   @Override
   public void setPaintMargin(boolean paint) {
      this.paragraph.setPaintMargin(paint);
      this.update();
   }
}
