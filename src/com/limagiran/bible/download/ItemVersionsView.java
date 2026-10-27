package com.limagiran.bible.download;

import com.limagiran.bible.AvailableVersions;
import com.limagiran.bible.util.Download;
import com.limagiran.bible.util.SwingUtils;
import com.limagiran.bible.util.Utils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class ItemVersionsView extends JPanel {
   public static final Dimension SIZE = new Dimension(260, 40);
   public final AvailableDownloads.AvailableDownloadsItem version;
   private boolean selected = false;
   private ItemVersionsView.EnumStatus status;
   private JButton jButtonDownload;
   private JButton jButtonRetry;
   private JLabel jLabelAvailable;
   private JLabel jLabelError;
   private JLabel jLabelNameInfo;
   private JPanel jPanelDownloading;
   private JPanel jPanelError;
   private JPanel jPanelStatus;
   private JProgressBar jProgressBarDownload;

   public ItemVersionsView(AvailableDownloads.AvailableDownloadsItem version) {
      this.version = version;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonDownload.addActionListener(e -> this.download());
      this.jButtonRetry.addActionListener(e -> this.download());
      this.changeCard(this.version.getPath().exists() ? ItemVersionsView.EnumStatus.AVAILABLE : ItemVersionsView.EnumStatus.DOWNLOAD);
   }

   private void initComponents() {
      this.jLabelNameInfo = new JLabel();
      this.jPanelStatus = new JPanel();
      this.jButtonDownload = new JButton();
      this.jLabelAvailable = new JLabel();
      this.jPanelDownloading = new JPanel();
      this.jProgressBarDownload = new JProgressBar();
      this.jPanelError = new JPanel();
      this.jButtonRetry = new JButton();
      this.jLabelError = new JLabel();
      this.setBorder(BorderFactory.createEtchedBorder());
      this.setMaximumSize(SIZE);
      this.setMinimumSize(SIZE);
      this.setPreferredSize(SIZE);
      this.setLayout(new AbsoluteLayout());
      this.jLabelNameInfo.setFont(new Font("Georgia", 0, 12));
      this.jLabelNameInfo.setHorizontalAlignment(2);
      this.jLabelNameInfo.setText(String.format("<html><div style=\"width:200\">%s (%s)</div>", this.version.name, this.version.language));
      this.jLabelNameInfo.setHorizontalTextPosition(2);
      this.jLabelNameInfo.setIconTextGap(0);
      this.add(this.jLabelNameInfo, new AbsoluteConstraints(60, 5, 210, 30));
      this.jPanelStatus.setOpaque(false);
      this.jPanelStatus.setLayout(new CardLayout());
      this.jButtonDownload.setFont(new Font("Georgia", 0, 12));
      this.jButtonDownload.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/bible/img/download20.png")));
      this.jButtonDownload.setFocusable(false);
      this.jPanelStatus.add(this.jButtonDownload, "DOWNLOAD");
      this.jLabelAvailable.setFont(new Font("Georgia", 0, 12));
      this.jLabelAvailable.setHorizontalAlignment(0);
      this.jLabelAvailable.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/bible/img/check24.png")));
      this.jPanelStatus.add(this.jLabelAvailable, "AVAILABLE");
      this.jPanelDownloading.setOpaque(false);
      this.jPanelDownloading.setLayout(new AbsoluteLayout());
      this.jPanelDownloading.add(this.jProgressBarDownload, new AbsoluteConstraints(5, 10, 35, 14));
      this.jPanelStatus.add(this.jPanelDownloading, "DOWNLOADING");
      this.jPanelError.setOpaque(false);
      this.jPanelError.setLayout(new BorderLayout());
      this.jButtonRetry.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/bible/img/refresh16.png")));
      this.jButtonRetry.setMargin(new Insets(3, 2, 3, 2));
      this.jPanelError.add(this.jButtonRetry, "Center");
      this.jLabelError.setFont(new Font("Arial", 0, 8));
      this.jLabelError.setForeground(new Color(204, 0, 0));
      this.jLabelError.setHorizontalAlignment(0);
      this.jLabelError.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/bible/i18n/Bundle").getString("word.error")));
      this.jPanelError.add(this.jLabelError, "Last");
      this.jPanelStatus.add(this.jPanelError, "ERROR");
      this.add(this.jPanelStatus, new AbsoluteConstraints(5, 3, 45, 33));
   }

   public void download() {
      Download d = Download.download(this.version.getUrlDownload(), this.version.getPath(), this.version.hash);
      new Thread(() -> {
         SwingUtils.runOnUIThread(() -> this.changeCard(ItemVersionsView.EnumStatus.DOWNLOADING));

         while (!d.isFinish()) {
            SwingUtilities.invokeLater(() -> this.jProgressBarDownload.setValue((int)d.getPercentage()));
            Utils.sleep(1000L);
         }

         switch (d.getStatus()) {
            case 0:
               AvailableVersions.addVersion(this.version.getPath());
               this.changeCard(ItemVersionsView.EnumStatus.AVAILABLE);
               break;
            case 1:
               this.changeCard(ItemVersionsView.EnumStatus.ERROR);
         }
      }).start();
   }

   public boolean isSelected() {
      return this.selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
      this.setBorder(!selected ? BorderFactory.createEtchedBorder() : BorderFactory.createEtchedBorder(Color.RED, Color.RED.brighter()));
   }

   private void changeCard(ItemVersionsView.EnumStatus status) {
      this.status = status;
      SwingUtils.runOnUIThread(() -> ((CardLayout)this.jPanelStatus.getLayout()).show(this.jPanelStatus, this.status.name()));
   }

   public boolean isAvailable() {
      return this.status == ItemVersionsView.EnumStatus.AVAILABLE;
   }

   public boolean isError() {
      return this.status == ItemVersionsView.EnumStatus.ERROR;
   }

   public boolean isDownloading() {
      return this.status == ItemVersionsView.EnumStatus.DOWNLOADING;
   }

   public boolean isDownload() {
      return this.status == ItemVersionsView.EnumStatus.DOWNLOAD;
   }

   public static enum EnumStatus {
      AVAILABLE,
      ERROR,
      DOWNLOADING,
      DOWNLOAD;
   }
}
