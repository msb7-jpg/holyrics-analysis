package com.limagiran.holyrics.worker;

import com.alee.global.StyleConstants;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.MusicAlreadyExistsDialog;
import com.limagiran.holyrics.view.panel.AddToProfilesPanel;
import com.limagiran.lgsaveopen.LGSaveOpen;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

public class BatchImportWorker extends MySwingWorker<List<Music>, Void> {
   private static final JFileChooser FILE_CHOOSER_STATIC = SwingUtils.generateJFileChooser();
   private final int size = MusicDao.getAll().size();
   private final List<Music> alreadyAdded = new ArrayList<>();
   private final List<File> files;
   private final AddToProfilesPanel addToProfilesPanel;
   private final Runnable callback;

   public BatchImportWorker(Frame owner, List<File> files, AddToProfilesPanel addToProfilesPanel, Runnable callback) {
      super(owner);
      this.files = files;
      this.addToProfilesPanel = addToProfilesPanel;
      this.callback = callback;
   }

   public static void main(Frame owner, Runnable callback) {
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
      FILE_CHOOSER_STATIC.setFileFilter(new FileFilter() {
         @Override
         public boolean accept(File f) {
            String name = f.getName().toLowerCase();
            return f.isDirectory() || name.endsWith(".muf") || name.endsWith(".mufl");
         }

         @Override
         public String getDescription() {
            return "Music File (*.muf)";
         }
      });
      JDialog d = new JDialog(owner, true);
      AddToProfilesPanel addToProfilesPanel = new AddToProfilesPanel();
      FILE_CHOOSER_STATIC.addActionListener(e -> {
         if (e.paramString().contains("ApproveSelection")) {
            List<File> files = Arrays.asList(FILE_CHOOSER_STATIC.getSelectedFiles()).stream().filter(File::isFile).collect(Collectors.toList());
            if (!files.isEmpty()) {
               PopUpWorker.exe(new BatchImportWorker(owner, files, addToProfilesPanel, callback));
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

   protected List<Music> doInBackground() throws Exception {
      List<Music> musics = new ArrayList<>();
      this.files
         .stream()
         .filter(f -> f.getName().toLowerCase().endsWith(".muf"))
         .map(f -> LGSaveOpen.open(Music.class, Music.BLANK, f))
         .filter(Objects::nonNull)
         .forEach(musics::add);
      this.files
         .stream()
         .filter(f -> f.getName().toLowerCase().endsWith(".mufl"))
         .map(f -> RWObj.read(f, List.class))
         .filter(Objects::nonNull)
         .filter(l -> Utils.checkGenerics(l, Music.class))
         .forEach(musics::addAll);

      for (int i = 0; i < musics.size(); i++) {
         musics.get(i).setId((long)(-i));
      }

      this.alreadyAdded.addAll(musics.parallelStream().filter(MusicDao::contains).collect(Collectors.toList()));
      musics.removeAll(this.alreadyAdded);
      MusicDao.add(musics);
      this.addToProfilesPanel.addMusicsInCheckedProfiles(musics);
      return this.alreadyAdded;
   }

   @Override
   protected void done() {
      super.done();
      Utils.msg(this.owner, com.limagiran.lgsaveopen.I18N.WORD_DONE + "\n" + I18N.MSG_IMPORTED_MUSICS.format(MusicDao.getAll().size() - this.size));
      if (!this.alreadyAdded.isEmpty()) {
         MusicAlreadyExistsDialog.main(this.owner, this.alreadyAdded, this.callback, this.addToProfilesPanel);
      } else {
         Optional.ofNullable(this.callback).ifPresent(Runnable::run);
      }
   }
}
