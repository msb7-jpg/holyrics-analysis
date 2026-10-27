package com.limagiran.holyrics.view;

import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.swing.JCheckBoxTreeBatchImport;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.panel.AddToProfilesPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class MusicBatchImportView extends JDialog {
   private final int size = MusicDao.getAll().size();
   private final Runnable callback;
   private final List<Music> list;
   private final TreeModel model;
   private final JCheckBoxTreeBatchImport tree;
   private final AddToProfilesPanel addToProfilesPanel = new AddToProfilesPanel();
   private Filler filler1;
   private JButton jButtonCancel;
   private JButton jButtonImport;
   private JLabel jLabelInfo;
   private JPanel jPanelButtons;
   private JPanel jPanelFooter;
   private JPanel jPanelFooterLeft;
   private JPanel jPanelFooterRight;
   private JPanel jPanelJTree;
   private JPanel jPanelProfilesList;
   private JScrollPane jScrollPaneTree;
   private JTree jTreeList;

   private MusicBatchImportView(Frame parent, List<Music> list, Runnable callback) {
      super(parent);
      this.list = list;
      this.callback = callback;
      this.model = createModel(this.list);
      this.initComponents();
      this.tree = (JCheckBoxTreeBatchImport)this.jTreeList;
      this.init();
   }

   private void init() {
      this.jPanelProfilesList.add(this.addToProfilesPanel, "Center");
      this.jButtonImport.addActionListener(e -> this.importMusics());
      this.jButtonCancel.addActionListener(e -> this.exit());
      this.tree.addCheckChangeEventListener(e -> this.updateInfo());
      this.updateInfo();
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      SwingUtils.maximize(this, 200);
   }

   private void initComponents() {
      this.jPanelJTree = new JPanel();
      this.jScrollPaneTree = new JScrollPane();
      this.jTreeList = new JCheckBoxTreeBatchImport(this.model, false);
      this.jPanelProfilesList = new JPanel();
      this.jPanelFooter = new JPanel();
      this.jPanelFooterLeft = new JPanel();
      this.jLabelInfo = new JLabel();
      this.jPanelFooterRight = new JPanel();
      this.jPanelButtons = new JPanel();
      this.jButtonImport = new JButton();
      this.jButtonCancel = new JButton();
      this.filler1 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
      this.setDefaultCloseOperation(0);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.batchImport")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(600, 400));
      this.setModal(true);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            MusicBatchImportView.this.formWindowClosing(evt);
         }
      });
      this.jPanelJTree.setLayout(new BorderLayout());
      this.jScrollPaneTree.setBackground(new Color(248, 248, 248));
      this.jTreeList.setBackground(new Color(248, 248, 248));
      this.jTreeList.setFont(new Font("Georgia", 0, 18));
      this.jTreeList.setRowHeight(24);
      this.jScrollPaneTree.setViewportView(this.jTreeList);
      this.jPanelJTree.add(this.jScrollPaneTree, "Center");
      this.jPanelProfilesList.setLayout(new BorderLayout());
      this.jPanelJTree.add(this.jPanelProfilesList, "Last");
      this.getContentPane().add(this.jPanelJTree, "Center");
      this.jPanelFooter.setLayout(new GridLayout(1, 0));
      this.jPanelFooterLeft.setLayout(new BorderLayout());
      this.jLabelInfo.setHorizontalAlignment(0);
      this.jPanelFooterLeft.add(this.jLabelInfo, "Center");
      this.jPanelFooter.add(this.jPanelFooterLeft);
      this.jPanelButtons.setLayout(new GridLayout(1, 2, 20, 0));
      this.jButtonImport.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.import")));
      this.jPanelButtons.add(this.jButtonImport);
      this.jButtonCancel.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.cancel")));
      this.jPanelButtons.add(this.jButtonCancel);
      this.jPanelFooterRight.add(this.jPanelButtons);
      this.jPanelFooter.add(this.jPanelFooterRight);
      this.jPanelFooter.add(this.filler1);
      this.getContentPane().add(this.jPanelFooter, "Last");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.exit();
   }

   public static void main(Frame parent, List<Music> list, Runnable callback) {
      SwingUtilities.invokeLater(() -> new MusicBatchImportView(parent, list, callback).setVisible(true));
   }

   @Override
   public void setVisible(boolean b) {
      if (b && this.list.isEmpty()) {
         Utils.msg(this, I18N.MSG_ERROR_EMPTY_LIST.val());
      } else {
         super.setVisible(b);
      }
   }

   private void exit() {
      this.dispose();
   }

   private void importMusics() {
      if (Utils.confirm(this, I18N.MSG_CONFIRM_IMPORT_DB_OFFLINE.val())) {
         super.dispose();
         PopUpWorker.exe(
            new MySwingWorker<Void, Void>(this, I18N.MSG_IN_PROGRESS.val()) {
               final List<Music> alreadyAdded = new ArrayList<>();

               protected Void doInBackground() throws Exception {
                  List<Music> musics = MusicBatchImportView.this.getSelectedMusics();
                  this.alreadyAdded.addAll(musics.parallelStream().filter(MusicDao::contains).collect(Collectors.toList()));
                  musics.removeAll(this.alreadyAdded);
                  MusicDao.add(musics);
                  MusicBatchImportView.this.addToProfilesPanel.addMusicsInCheckedProfiles(musics);
                  return null;
               }

               @Override
               protected void done() {
                  super.done();
                  Utils.msg(
                     this.owner,
                     com.limagiran.lgsaveopen.I18N.WORD_DONE
                        + "\n"
                        + I18N.MSG_IMPORTED_MUSICS.format(MusicDao.getAll().size() - MusicBatchImportView.this.size)
                  );
                  if (!this.alreadyAdded.isEmpty()) {
                     MusicAlreadyExistsDialog.main(
                        this.owner, this.alreadyAdded, MusicBatchImportView.this.callback, MusicBatchImportView.this.addToProfilesPanel
                     );
                  } else {
                     Optional.ofNullable(MusicBatchImportView.this.callback).ifPresent(r -> r.run());
                  }
               }
            }
         );
      }
   }

   private static TreeModel createModel(List<Music> l) {
      DefaultMutableTreeNode nodeRoot = new DefaultMutableTreeNode(I18N.WORD_MUSIC.val());
      List<String> artists = l.parallelStream().map(Music::getArtist).distinct().collect(Collectors.toList());
      artists.sort((o1, o2) -> Utils.removeAccent(o1).compareToIgnoreCase(Utils.removeAccent(o2)));
      artists.forEach(artist -> {
         DefaultMutableTreeNode nodeArt = new DefaultMutableTreeNode(artist);
         List<Music> add = l.parallelStream().filter(m -> m.getArtist().equalsIgnoreCase(artist)).collect(Collectors.toList());
         add.sort((o1, o2) -> Utils.removeAccent(o1.getTitle()).compareToIgnoreCase(Utils.removeAccent(o2.getTitle())));
         if (!add.isEmpty()) {
            nodeRoot.add(nodeArt);
         }

         add.stream().map(DefaultMutableTreeNode::new).forEach(nodeArt::add);
      });
      return new DefaultTreeModel(nodeRoot);
   }

   private void updateInfo() {
      this.jLabelInfo.setText(I18N.MSG_SELECTED_SONGS.format(this.getSelectedMusics().size()));
      this.jButtonImport.setEnabled(!this.getSelectedMusics().isEmpty());
   }

   private List<Music> getSelectedMusics() {
      return this.tree.getSelectedObjects().parallelStream().filter(Music.class::isInstance).map(Music.class::cast).collect(Collectors.toList());
   }
}
