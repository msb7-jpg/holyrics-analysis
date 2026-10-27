package com.limagiran.holyrics.view;

import com.alee.extended.list.CheckBoxListModel;
import com.alee.extended.list.WebCheckBoxList;
import com.alee.laf.scroll.WebScrollPane;
import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.panel.AutomaticSettingsPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class AnnouncementShowDialog extends JDialog {
   private static final AutomaticSettingsPanel.AutomaticSettings AUTOMATIC_SETTINGS = Settings.SET.getAutomaticSettings("announcement");
   private static final AutomaticSettingsPanel AUTOMATIC_SETTINGS_PANEL = new AutomaticSettingsPanel(AUTOMATIC_SETTINGS, Settings.SET::saveTrue);
   private final MusicMain parent;
   private JButton jButtonShow;
   private JLabel jLabelAnnouncement;
   private JPanel jPanelAutomaticSettings;
   private JPanel jPanelCenter;
   private JPanel jPanelFooter;
   private JPanel jPanelGeneral;
   private JPanel jPanelSettings;
   private JSeparator jSeparator;
   private JSeparator jSeparator1;

   public AnnouncementShowDialog(MusicMain parent) {
      super(parent, true);
      this.parent = parent;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jPanelAutomaticSettings.add(AUTOMATIC_SETTINGS_PANEL, "Center");
      final CheckBoxListModel model = new CheckBoxListModel();
      model.addCheckBoxElement("", true);
      AnnouncementController.getAll().forEach(an -> model.addCheckBoxElement(an, true));
      final WebCheckBoxList chkbx = new WebCheckBoxList(model);
      model.addListDataListener(new ListDataListener() {
         @Override
         public void intervalAdded(ListDataEvent e) {
         }

         @Override
         public void intervalRemoved(ListDataEvent e) {
         }

         @Override
         public void contentsChanged(ListDataEvent e) {
            if (e.getIndex0() == e.getIndex1() && chkbx.getSelectedIndex() == 0) {
               boolean flag = model.isCheckBoxSelected(0);

               for (int i = 0; i < model.getSize(); i++) {
                  chkbx.setCheckBoxSelected(i, flag);
               }
            }
         }
      });
      chkbx.setFont(new Font("Courier New", 1, 16));
      chkbx.setVisibleRowCount(Math.max(model.getSize(), 16));
      chkbx.setEditable(false);
      chkbx.setFocusable(false);
      this.jPanelCenter.add(new WebScrollPane(chkbx), "Center");
      this.jButtonShow.addActionListener(e -> this.showAnnouncements(chkbx.getCheckedValues()));
      SwingUtils.addHtmlShortcut(this.jButtonShow, "(Enter)");
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, () -> this.showAnnouncements(chkbx.getCheckedValues())));
      InsertShortcut.addEscToDispose(this);
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelCenter = new JPanel();
      this.jLabelAnnouncement = new JLabel();
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
      this.setMinimumSize(new Dimension(350, 400));
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            AnnouncementShowDialog.this.formWindowClosing(evt);
         }
      });
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeneral.setLayout(new BorderLayout(5, 0));
      this.jPanelCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelCenter.setLayout(new BorderLayout());
      this.jLabelAnnouncement.setFont(new Font("Georgia", 0, 18));
      this.jLabelAnnouncement.setHorizontalAlignment(0);
      this.jLabelAnnouncement.setText(bundle.getString("word.announcement"));
      this.jPanelCenter.add(this.jLabelAnnouncement, "First");
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
      if (AnnouncementController.getAll().isEmpty()) {
         Utils.error(parent, I18N.MSG_ERROR_EMPTY_LIST.val());
      } else {
         SwingUtilities.invokeLater(() -> new AnnouncementShowDialog(parent).setVisible(true));
      }
   }

   private void showAnnouncements(List<Object> obj) {
      List list = obj.stream()
         .filter(Announcement.class::isInstance)
         .map(Announcement.class::cast)
         .map(Announcement::createPresentation)
         .collect(Collectors.toList());
      if (list.isEmpty()) {
         Utils.error(this, I18N.MSG_ERROR_EMPTY_LIST.val());
      } else {
         this.dispose();
         if (AUTOMATIC_SETTINGS.isAutomatic()) {
            AutomaticShowView.show(list, AUTOMATIC_SETTINGS.getDuration(), AUTOMATIC_SETTINGS.isRepeat(), ScreenDeviceSettings.EnumDeviceType.ANNOUNCEMENT);
         } else {
            GeneratePresentation.showPresentation(this.parent, Theme.BLACK_SCREEN, list, 0, ScreenDeviceSettings.EnumDeviceType.ANNOUNCEMENT);
         }
      }
   }

   @Override
   public void dispose() {
      AUTOMATIC_SETTINGS_PANEL.commit();
      super.dispose();
   }
}
