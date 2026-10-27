package com.limagiran.holyrics.worker;

import com.alee.global.StyleConstants;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.ImportMusic;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.MusicBatchImportView;
import com.limagiran.holyrics.view.panel.AddToProfilesPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

public class MusicImportWorker extends MySwingWorker<ImportMusic.Response, Void> {
   private static final JFileChooser FILE_CHOOSER_STATIC = SwingUtils.generateJFileChooser();
   private final Frame parent;
   private final ImportMusic.Type type;
   private final List<File> files;
   private final Runnable callback;

   public MusicImportWorker(Frame parent, ImportMusic.Type type, List<File> files, AddToProfilesPanel addToProfilesPanel, Runnable callback) {
      super(parent);
      this.parent = parent;
      this.type = type;
      this.files = files;
      this.callback = callback;
   }

   public static void main(Frame owner, ImportMusic.Type type, Runnable callback) {
      FILE_CHOOSER_STATIC.setFileFilter(type.filter);
      JDialog d = new JDialog(owner, true);
      AddToProfilesPanel addToProfilesPanel = new AddToProfilesPanel();
      FILE_CHOOSER_STATIC.addActionListener(e -> {
         if (e.paramString().contains("ApproveSelection")) {
            List<File> files = Arrays.asList(FILE_CHOOSER_STATIC.getSelectedFiles()).stream().filter(File::isFile).collect(Collectors.toList());
            if (!files.isEmpty()) {
               PopUpWorker.exe(new MusicImportWorker(owner, type, files, addToProfilesPanel, callback));
            }
         }

         d.dispose();
      });
      d.setLayout(new BorderLayout());
      WebPanel content = new WebPanel(true, new BorderLayout(5, 25));
      content.setRound(StyleConstants.smallRound);
      content.setMargin(5);
      content.setBorder(new EmptyBorder(8, 8, 8, 8));
      content.add(FILE_CHOOSER_STATIC, "Center");
      content.add(addToProfilesPanel, "Last");
      d.add(content, "Center");
      d.pack();
      d.setLocationRelativeTo(null);
      d.setIconImages(Images.HOLYRICS_ICON);
      d.setTitle(I18N.WORD_IMPORT.val());
      d.setMinimumSize(new Dimension(500, 500));
      d.setVisible(true);
   }

   protected ImportMusic.Response doInBackground() throws Exception {
      return this.type.importMusics(this.files);
   }

   @Override
   protected void done() {
      super.done();

      try {
         ImportMusic.Response r = this.get();
         if (!r.error.isEmpty()) {
            JList list = new JList<>(r.error.stream().map(File::getName).toArray());
            JOptionPane.showMessageDialog(this.parent, list, I18N.WORD_ERROR.val(), 0);
         }

         MusicBatchImportView.main(this.parent, r.getMusics(), this.callback);
      } catch (Exception var3) {
         Utils.error(this.parent, I18N.MSG_ERROR_TRY_AGAIN.val());
      }
   }

   static {
      FILE_CHOOSER_STATIC.setPreferredSize(new Dimension(800, 400));
      FILE_CHOOSER_STATIC.setFileView(new FileView() {
         @Override
         public Icon getIcon(File f) {
            return FileSystemView.getFileSystemView().getSystemIcon(f);
         }
      });
      FILE_CHOOSER_STATIC.setMultiSelectionEnabled(true);
      FILE_CHOOSER_STATIC.setAcceptAllFileFilterUsed(false);
      FILE_CHOOSER_STATIC.setDialogTitle(I18N.WORD_MUSICS.val());
   }
}
