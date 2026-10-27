package com.limagiran.holyrics.hitem;

import com.alee.extended.window.PopOverAdapter;
import com.alee.extended.window.PopOverLocation;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.limagiran.holyrics.Holyrics;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ListCellRendererHItemGrid;
import com.limagiran.holyrics.swing.ListDragAndDropFile;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.view.panel.AutomaticSettingsPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

class ShowGridDialog extends WebPopOver {
   private static ShowGridDialog INSTANCE;
   private static final AutomaticSettingsPanel.AutomaticSettings AUTOMATIC_SETTINGS = Settings.SET.getAutomaticSettings("media-image");
   private static final AutomaticSettingsPanel AUTOMATIC_SETTINGS_PANEL = new AutomaticSettingsPanel(AUTOMATIC_SETTINGS, Settings.SET::saveTrue);
   private final JList<File> listView = new JList<>();
   private final WebScrollPane scroll = new WebScrollPane(this.listView);

   private ShowGridDialog() {
      super((Frame)null);
      this.init();
   }

   private void init() {
      this.listView.setLayoutOrientation(2);
      this.listView.setVisibleRowCount(-1);
      this.listView.setFixedCellWidth(160);
      this.listView.setCellRenderer(new ListCellRendererHItemGrid());
      ListDragAndDropFile.setup(this.listView);
      this.scroll.setFocusable(false);
      this.scroll.setDrawFocus(false);
      final WebButton buttonOk = new WebButton("Ok", new ImageIcon(Images.load("play16.png")), e -> this.exit(true));
      SwingUtils.addHtmlShortcut(buttonOk, " (Enter)");
      WebButton buttonCancel = new WebButton(I18N.WORD_CANCEL.val(), e -> this.exit(false));
      WebPanel gridButtons = new WebPanel(new GridLayout(1, 2, 25, 0), new Component[]{buttonOk, buttonCancel});
      gridButtons.setOpaque(false);
      WebPanel footer = new WebPanel(new GridBagLayout());
      footer.setOpaque(false);
      footer.add(AUTOMATIC_SETTINGS_PANEL, new GridBagConstraints());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridy = 1;
      footer.add(gridButtons, gbc);
      Arrays.asList(buttonOk, buttonCancel).forEach(b -> b.setMargin(2, 16, 2, 16).setDrawShade(false).setFont(new Font("Georgia", 0, 14)));
      this.setDefaultCloseOperation(2);
      this.getPainter().setTransparency(1.0F);
      this.setMovable(true);
      this.setResizable(true);
      InsertShortcut.addEscToDispose(this);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, () -> buttonOk.doClick(0)));
      this.setCloseOnFocusLoss(true);
      this.setMargin(12, 12, 12, 12);
      this.setLayout(new BorderLayout());
      WebLabel title = new WebLabel(I18N.MSG_SELECT_FILES.val(), 0, new Object[0]);
      title.setMargin(0, 0, 8, 0);
      title.setFont(new Font("Georgia", 0, 20));
      this.add(title, "First");
      this.add(this.scroll, "Center");
      this.add(footer, "Last");
      this.packAndCenter();
      this.addPopOverListener(new PopOverAdapter() {
         public void opened(WebPopOver popOver) {
            buttonOk.requestFocus();
         }
      });
   }

   private void exit(boolean okButton) {
      this.dispose();
      if (okButton) {
         if (AUTOMATIC_SETTINGS_PANEL.isVisible() && AUTOMATIC_SETTINGS.isAutomatic()) {
            GeneratePresentation.showImages(
               null,
               AUTOMATIC_SETTINGS.isAutomatic(),
               AUTOMATIC_SETTINGS.isRepeat(),
               AUTOMATIC_SETTINGS.getDuration(),
               this.listView.getSelectedValuesList().toArray(new File[0])
            );
         } else {
            HItemUtils.run(true, this.listView.getSelectedValuesList().toArray(new File[0]));
         }
      }
   }

   public static void main(File... files) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new ShowGridDialog();
         } else {
            INSTANCE.exit(false);
         }

         DefaultListModel<File> model = new DefaultListModel<>();
         Arrays.asList(files).forEach(model::addElement);
         INSTANCE.listView.setModel(model);
         INSTANCE.listView.setSelectedIndices(IntStream.range(0, model.size()).toArray());
         INSTANCE.listView.setVisibleRowCount((int)Math.ceil(Math.sqrt((double)model.getSize())));
         Dimension d = VideoUtils.getSizeDisplay();
         INSTANCE.scroll.setMaximumWidth(d.width - 150);
         INSTANCE.scroll.setMaximumHeight(d.height - 250);
         INSTANCE.packAndCenter();
         AUTOMATIC_SETTINGS_PANEL.setVisible(Stream.of(files).allMatch(HItemUtils::isImage));
         INSTANCE.show(PopOverLocation.center);
      });
   }

   static {
      Holyrics.applyLAF();
      AUTOMATIC_SETTINGS_PANEL.setOpaque(false);
      AUTOMATIC_SETTINGS_PANEL.setBorder(new EmptyBorder(4, 2, 4, 2));
   }
}
