package com.limagiran.holyrics.view;

import com.alee.laf.tabbedpane.WebTabbedPane;
import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.event.AnnouncementTitlePanelListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.model.announcement.AnnouncementEditPanel;
import com.limagiran.holyrics.model.announcement.AnnouncementTitlePanel;
import com.limagiran.holyrics.model.theme.BackgroundAnnouncement;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;

public class AnnouncementView extends JDialog implements AnnouncementTitlePanelListener {
   private final WebTabbedPane webTabbedPane = new WebTabbedPane(2, 1);
   private final Runnable callback;
   private JButton jButtonCreate;
   private JLabel jLabelMaximumAlert;
   private JPanel jPanelCenter;
   private JPanel jPanelHeader;
   private JPanel jPanelList;
   private JPanel jPanelMusic;
   private JScrollPane jScrollPaneList;

   private AnnouncementView(Window owner, Runnable callback) {
      super(owner);
      this.callback = callback;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jLabelMaximumAlert.setText(I18N.MSG_MEMORY_LIMIT_REACHED.val());
      this.setupMaximum();
      this.webTabbedPane.setFocusable(false);
      this.jPanelMusic.add(this.webTabbedPane, "Center");
      SwingUtils.maximize(this, 100);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::close));
      AnnouncementController.getAll().forEach(a -> {
         AnnouncementTitlePanel title = new AnnouncementTitlePanel(a, this);
         AnnouncementEditPanel panel = new AnnouncementEditPanel(a, title);
         this.webTabbedPane.add(panel);
         this.webTabbedPane.setTabComponentAt(this.webTabbedPane.getTabCount() - 1, title);
         this.webTabbedPane.setSelectedComponent(panel);
         this.webTabbedPane.revalidate();
         this.webTabbedPane.repaint();
         panel.requestFocus();
      });
      this.updateTitles();
      this.jButtonCreate.addActionListener(e -> this.create());
      Optional.ofNullable(this.webTabbedPane.getSelectedComponent())
         .filter(AnnouncementEditPanel.class::isInstance)
         .map(AnnouncementEditPanel.class::cast)
         .ifPresent(c -> c.requestFocus());
   }

   private void initComponents() {
      this.jScrollPaneList = new JScrollPane();
      this.jPanelList = new JPanel();
      this.jPanelCenter = new JPanel();
      this.jPanelMusic = new JPanel();
      this.jPanelHeader = new JPanel();
      this.jButtonCreate = new JButton();
      this.jLabelMaximumAlert = new JLabel();
      this.jPanelList.setPreferredSize(new Dimension(250, 622));
      this.jPanelList.setLayout(new GridBagLayout());
      this.jScrollPaneList.setViewportView(this.jPanelList);
      GroupLayout jPanelCenterLayout = new GroupLayout(this.jPanelCenter);
      this.jPanelCenter.setLayout(jPanelCenterLayout);
      jPanelCenterLayout.setHorizontalGroup(jPanelCenterLayout.createParallelGroup(Alignment.LEADING).addGap(0, 1081, 32767));
      jPanelCenterLayout.setVerticalGroup(jPanelCenterLayout.createParallelGroup(Alignment.LEADING).addGap(0, 624, 32767));
      this.setDefaultCloseOperation(0);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.announcement")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(1050, 500));
      this.setModal(true);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            AnnouncementView.this.formWindowClosing(evt);
         }
      });
      this.jPanelMusic.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelMusic.setLayout(new BorderLayout(10, 0));
      this.jButtonCreate.setFont(new Font("Georgia", 0, 14));
      this.jButtonCreate.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/addFile24.png")));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jButtonCreate.setText(bundle.getString("word.create"));
      this.jButtonCreate.setFocusable(false);
      this.jPanelHeader.add(this.jButtonCreate);
      this.jLabelMaximumAlert.setFont(new Font("Georgia", 0, 12));
      this.jLabelMaximumAlert.setForeground(new Color(153, 0, 0));
      this.jPanelHeader.add(this.jLabelMaximumAlert);
      this.jPanelMusic.add(this.jPanelHeader, "First");
      this.getContentPane().add(this.jPanelMusic, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.close();
   }

   public static void main(Window owner, Runnable callback) {
      SwingUtilities.invokeLater(() -> new AnnouncementView(owner, callback).setVisible(true));
   }

   private void close() {
      AnnouncementController.save();
      this.webTabbedPane.removeAll();
      super.dispose();
      Optional.ofNullable(this.callback).ifPresent(Runnable::run);
   }

   private void create() {
      Announcement a = new Announcement();
      AnnouncementController.add(a);
      AnnouncementTitlePanel title = new AnnouncementTitlePanel(a, this);
      AnnouncementEditPanel panel = new AnnouncementEditPanel(a, title);
      this.webTabbedPane.add(panel);
      this.webTabbedPane.setTabComponentAt(this.webTabbedPane.getTabCount() - 1, title);
      this.webTabbedPane.setSelectedComponent(panel);
      this.webTabbedPane.revalidate();
      this.webTabbedPane.repaint();
      this.updateTitles();
      panel.requestFocus();
      this.setupMaximum();
   }

   private void updateTitles() {
      for (int i = 0; i < this.webTabbedPane.getTabCount(); i++) {
         if (this.webTabbedPane.getTabComponentAt(i) instanceof AnnouncementTitlePanel) {
            ((AnnouncementTitlePanel)this.webTabbedPane.getTabComponentAt(i)).updateButtons();
         }
      }
   }

   @Override
   public void removed(AnnouncementTitlePanel an) {
      try {
         for (int i = 0; i < this.webTabbedPane.getTabCount(); i++) {
            if (an.equals(this.webTabbedPane.getTabComponentAt(i))) {
               this.webTabbedPane.remove(i);
               this.webTabbedPane.revalidate();
               break;
            }
         }

         this.setupMaximum();
         this.updateTitles();
      } catch (Exception var3) {
      }
   }

   @Override
   public void changeTitlePosition(int from, int to) {
      try {
         Component selected = this.webTabbedPane.getSelectedComponent();
         Component panel = this.webTabbedPane.getComponentAt(from);
         Component title = this.webTabbedPane.getTabComponentAt(from);
         this.webTabbedPane.removeTabAt(from);
         this.webTabbedPane.add(panel, to);
         this.webTabbedPane.setTabComponentAt(to, title);
         this.webTabbedPane.setSelectedComponent(selected);
         this.webTabbedPane.revalidate();
         AnnouncementController.set(this.webTabbedPane);
         this.updateTitles();
      } catch (Exception var6) {
      }
   }

   @Override
   public void selected(AnnouncementTitlePanel an) {
      this.setupMaximum();
      this.updateTitles();
      int i = this.webTabbedPane.indexOfTabComponent(an);
      BooleanOptional.runIfTrue(i >= 0, () -> this.webTabbedPane.setSelectedIndex(i));
   }

   @Override
   public boolean isLast(AnnouncementTitlePanel an) {
      int sz = this.webTabbedPane.getTabCount();
      return sz > 0 && an.equals(this.webTabbedPane.getTabComponentAt(sz - 1));
   }

   @Override
   public boolean isFirst(AnnouncementTitlePanel an) {
      return this.webTabbedPane.getTabCount() > 0 && an.equals(this.webTabbedPane.getTabComponentAt(0));
   }

   @Override
   public int titleCount() {
      return this.webTabbedPane.getTabCount();
   }

   @Override
   public int indexOf(AnnouncementTitlePanel an) {
      return this.webTabbedPane.indexOfTabComponent(an);
   }

   private void setupMaximum() {
      long size = (long)AnnouncementController.getAll()
         .stream()
         .map(an -> an.getTheme().getBackground())
         .filter(BackgroundAnnouncement.class::isInstance)
         .map(BackgroundAnnouncement.class::cast)
         .map(BackgroundAnnouncement::getImageHL)
         .map(img -> img.bytes.length)
         .reduce(0, Integer::sum)
         .intValue();
      this.jButtonCreate.setEnabled(size < 16777216L);
      this.jLabelMaximumAlert.setVisible(!this.jButtonCreate.isEnabled());
   }
}
