package com.limagiran.holyrics.view.panel;

import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.swing.SelectablePanel;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Download;
import com.limagiran.holyrics.util.EnumAvailableVideos;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class AvailableVideo extends JPanel implements SelectablePanel {
   private static final String CARD_BUTTON_DOWNLOAD = "Download";
   private static final String CARD_AVAILABLE = "Available";
   private static final String CARD_STATUS_DOWNLOADING = "Downloading";
   private static final String CARD_ERROR = "Error";
   private static final int STATUS_AVAILABLE = 0;
   private static final int STATUS_ERROR = 1;
   private static final int STATUS_DOWNLOAD = 2;
   private static final int STATUS_DOWNLOADING = 3;
   public static final Dimension SIZE = new Dimension(170, 155);
   public final EnumAvailableVideos video;
   public final File path;
   private int status;
   private JButton jButtonDownload;
   private JButton jButtonRetry;
   private JLabel jLabelAvailable;
   private JLabel jLabelError;
   private JLabel jLabelNameInfo;
   private JLabel jLabelPreview;
   private JPanel jPanelDownloading;
   private JPanel jPanelError;
   private JPanel jPanelStatus;
   private JProgressBar jProgressBarDownload;

   public static List<AvailableVideo> getAllAvailableVideos() {
      return Arrays.asList(EnumAvailableVideos.values()).stream().map(AvailableVideo::new).collect(Collectors.toList());
   }

   public AvailableVideo(EnumAvailableVideos video) {
      this.video = video;
      this.path = new File(Values.PATH_BACKGROUND_VIDEO + this.video.fileName);
      this.initComponents();
      this.init();
   }

   private void init() {
      try {
         this.changeCard(this.path.exists() && Dir.getHash(this.path).equals(this.video.hash) ? "Available" : "Download");
      } catch (Exception var2) {
         this.changeCard("Download");
      }
   }

   private void initComponents() {
      this.jLabelPreview = new JLabel();
      this.jLabelNameInfo = new JLabel();
      this.jPanelStatus = new JPanel();
      this.jButtonDownload = new JButton();
      this.jLabelAvailable = new JLabel();
      this.jPanelDownloading = new JPanel();
      this.jProgressBarDownload = new JProgressBar();
      this.jPanelError = new JPanel();
      this.jLabelError = new JLabel();
      this.jButtonRetry = new JButton();
      this.setBackground(new Color(222, 222, 222));
      this.setBorder(StyleUI.BORDER_UNSELECTED);
      this.setMaximumSize(SIZE);
      this.setMinimumSize(SIZE);
      this.setPreferredSize(SIZE);
      this.setLayout(new AbsoluteLayout());
      this.jLabelPreview.setIcon(this.video.gif);
      this.add(this.jLabelPreview, new AbsoluteConstraints(5, 5, 160, 90));
      this.jLabelNameInfo.setFont(new Font("Georgia", 0, 12));
      this.jLabelNameInfo.setHorizontalAlignment(0);
      this.jLabelNameInfo.setText(this.video.name);
      this.add(this.jLabelNameInfo, new AbsoluteConstraints(5, 95, 160, 20));
      this.jPanelStatus.setOpaque(false);
      this.jPanelStatus.setLayout(new CardLayout());
      this.jButtonDownload.setFont(new Font("Georgia", 0, 12));
      this.jButtonDownload
         .setText(
            MessageFormat.format(
               ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("AvailableVideo.jButtonDownload.text"), this.video.size
            )
         );
      this.jButtonDownload.addActionListener(evt -> this.download());
      this.jPanelStatus.add(this.jButtonDownload, "Download");
      this.jLabelAvailable.setFont(new Font("Georgia", 0, 12));
      this.jLabelAvailable.setHorizontalAlignment(0);
      this.jLabelAvailable.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/check16.png")));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelAvailable.setText(bundle.getString("AvailableVideo.jLabelAvailable.text"));
      this.jLabelAvailable.setBorder(BorderFactory.createEtchedBorder());
      this.jPanelStatus.add(this.jLabelAvailable, "Available");
      this.jPanelDownloading.setOpaque(false);
      this.jPanelDownloading.setLayout(new AbsoluteLayout());
      this.jPanelDownloading.add(this.jProgressBarDownload, new AbsoluteConstraints(5, 8, 150, 14));
      this.jPanelStatus.add(this.jPanelDownloading, "Downloading");
      this.jPanelError.setOpaque(false);
      this.jPanelError.setLayout(new FlowLayout(1, 5, 2));
      this.jLabelError.setFont(new Font("Book Antiqua", 1, 18));
      this.jLabelError.setForeground(new Color(204, 0, 0));
      this.jLabelError.setHorizontalAlignment(4);
      this.jLabelError.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.error")));
      this.jPanelError.add(this.jLabelError);
      this.jButtonRetry.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/refresh16.png")));
      this.jButtonRetry.addActionListener(evt -> this.download());
      this.jPanelError.add(this.jButtonRetry);
      this.jPanelStatus.add(this.jPanelError, "Error");
      this.add(this.jPanelStatus, new AbsoluteConstraints(5, 115, 160, 30));
   }

   public void download() {
      Download d = Download.download(this.video.urlDownload, this.path, this.video.hash);
      Utils.startThreadDaemon(() -> {
         SwingUtilities.invokeLater(() -> this.changeCard("Downloading"));

         while (!d.isFinish()) {
            SwingUtilities.invokeLater(() -> this.jProgressBarDownload.setValue((int)d.getPercentage()));
            Utils.sleep(1000L);
         }

         Background.createLongVideo(this.path, this.video.fileName);
         SwingUtilities.invokeLater(() -> {
            switch (d.getStatus()) {
               case 0:
                  this.changeCard("Available");
                  break;
               case 1:
                  this.changeCard("Error");
            }
         });
      });
   }

   private void changeCard(String cardName) {
      ((CardLayout)this.jPanelStatus.getLayout()).show(this.jPanelStatus, cardName);
      switch (cardName) {
         case "Available":
            this.status = 0;
            break;
         case "Download":
            this.status = 2;
            break;
         case "Error":
            this.status = 1;
            break;
         case "Downloading":
            this.status = 3;
      }
   }

   public boolean isAvailable() {
      return this.status == 0;
   }

   public boolean isError() {
      return this.status == 1;
   }

   public boolean isDownloading() {
      return this.status == 3;
   }

   public boolean isDownload() {
      return this.status == 2;
   }

   @Override
   public JComponent getPanel() {
      return this;
   }

   @Override
   public String getId() {
      return "Video" + Values.PATH_BACKGROUND_VIDEO + this.video.fileName;
   }
}
