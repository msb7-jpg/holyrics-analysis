package com.limagiran.bible.download;

import com.google.gson.Gson;
import com.limagiran.bible.i18n.I18N;
import com.limagiran.bible.util.Download;
import com.limagiran.bible.util.InsertShortcut;
import com.limagiran.bible.util.MySwingWorker;
import com.limagiran.bible.util.PopUpWorker;
import com.limagiran.bible.util.Utils;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;

public class DownloadView extends JDialog {
   private static final String URL = "http://www.holyrics.com/download/bible/availableVersions.json";
   private final Runnable callback;
   private final Frame parent;
   private JPanel jPanelGeneral;
   private JPanel jPanelVersions;
   private JScrollPane jScrollPaneVersions;

   private DownloadView(Frame parent, Runnable callback) {
      super(parent);
      this.parent = parent;
      this.callback = callback;
      this.initComponents();
      this.init();
   }

   private void init() {
      JLabel title = new JLabel("Download");
      title.setFont(new Font("Georgia", 0, 18));
      title.setHorizontalAlignment(0);
      title.setOpaque(true);
      this.jScrollPaneVersions.setColumnHeaderView(title);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            super.componentShown(e);
            PopUpWorker.exe(new MySwingWorker<Download, Void>(DownloadView.this.parent, I18N.WORD_SEARCHING.val()) {
               protected Download doInBackground() throws Exception {
                  try {
                     Download d = Download.download("http://www.holyrics.com/download/bible/availableVersions.json");
                     d.waitFinish();
                     return d;
                  } catch (Exception var2) {
                     return null;
                  }
               }

               @Override
               protected void done() {
                  super.done();

                  try {
                     Download d = this.get();
                     if (d == null) {
                        Utils.error(DownloadView.this.parent, I18N.MSG_ERROR_TRY_AGAIN.val());
                        DownloadView.this.exit();
                        return;
                     }

                     byte[] bytes = d.getBytes();
                     if (bytes == null && !d.isOk()) {
                        Utils.error(DownloadView.this.parent, I18N.MSG_ERROR_CONNECT.val());
                        DownloadView.this.exit();
                        return;
                     }

                     String txt = Utils.decompress(bytes);
                     AvailableDownloads list = (AvailableDownloads)new Gson().fromJson(txt, AvailableDownloads.class);
                     list.getList().stream().map(ItemVersionsView::new).forEach(DownloadView.this.jPanelVersions::add);
                     DownloadView.this.adjustJPanel(DownloadView.this.jPanelVersions, ItemVersionsView.SIZE, DownloadView.this.jScrollPaneVersions);
                  } catch (Exception var5) {
                     Utils.error(DownloadView.this.parent, I18N.MSG_ERROR_TRY_AGAIN.val());
                     DownloadView.this.exit();
                  }
               }
            });
         }
      });
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jScrollPaneVersions = new JScrollPane();
      this.jPanelVersions = new JPanel();
      this.setDefaultCloseOperation(0);
      this.setTitle("Download");
      this.setIconImage(new ImageIcon(this.getClass().getResource("/com/limagiran/bible/img/download16.png")).getImage());
      this.setMaximumSize(new Dimension(312, 300));
      this.setMinimumSize(new Dimension(312, 300));
      this.setModal(true);
      this.setResizable(false);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            DownloadView.this.formWindowClosing(evt);
         }
      });
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
      this.jScrollPaneVersions.setVerticalScrollBarPolicy(22);
      this.jScrollPaneVersions.setViewportView(this.jPanelVersions);
      GroupLayout jPanelGeneralLayout = new GroupLayout(this.jPanelGeneral);
      this.jPanelGeneral.setLayout(jPanelGeneralLayout);
      jPanelGeneralLayout.setHorizontalGroup(jPanelGeneralLayout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPaneVersions, -2, 300, -2));
      jPanelGeneralLayout.setVerticalGroup(jPanelGeneralLayout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPaneVersions, -1, 287, 32767));
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanelGeneral, -2, -1, -2));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanelGeneral, -1, -1, 32767));
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.exit();
   }

   public static void main(Frame parent, Runnable callback) {
      SwingUtilities.invokeLater(() -> new DownloadView(parent, callback).setVisible(true));
   }

   @Override
   public void setVisible(boolean flag) {
      if (flag) {
         super.setVisible(flag);
      } else {
         this.exit();
      }
   }

   private void adjustJPanel(JPanel panel, Dimension size, JScrollPane scroll) {
      int amount = panel.getComponentCount();
      panel.setSize(size.width + 10, amount * size.height + (amount + 1) * 5);
      panel.setPreferredSize(panel.getSize());
      panel.revalidate();
      scroll.getVerticalScrollBar().setUnitIncrement(size.height / 2);
   }

   private void exit() {
      this.callback.run();
      this.dispose();
   }
}
