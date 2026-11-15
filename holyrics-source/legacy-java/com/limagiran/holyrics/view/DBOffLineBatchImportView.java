package com.limagiran.holyrics.view;

import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.DBOffLine;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.swing.JCheckBoxTreeDBOffLine;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MusicUtils;
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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class DBOffLineBatchImportView extends JDialog {
   private final Runnable callback;
   private final List<DBOffLineBatchImportView.MusicJTree> list;
   private final TreeModel model;
   private final JCheckBoxTreeDBOffLine tree;
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

   public DBOffLineBatchImportView(Frame parent, Runnable callback) {
      super(parent);
      this.callback = callback;
      this.list = this.filterList(DBOffLine.LIST_MOST_PLAYED);
      this.model = createModel(this.list);
      this.initComponents();
      this.tree = (JCheckBoxTreeDBOffLine)this.jTreeList;
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
      this.jTreeList = new JCheckBoxTreeDBOffLine(this.model, false);
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
            DBOffLineBatchImportView.this.formWindowClosing(evt);
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

   public static void main(final Frame parent, final Runnable callback) {
      PopUpWorker.exe(new MySwingWorker(parent) {
         @Override
         protected Object doInBackground() throws Exception {
            return DBOffLine.LIST_MOST_PLAYED.size();
         }

         @Override
         protected void done() {
            super.done();
            SwingUtilities.invokeLater(() -> new DBOffLineBatchImportView(parent, callback).setVisible(true));
         }
      });
   }

   @Override
   public void setVisible(boolean b) {
      if (b && this.list.isEmpty()) {
         Utils.msg(this, I18N.MSG_ALL_LYRICS_ALREADY_BEEN_IMPORTED.val());
      } else {
         super.setVisible(b);
      }
   }

   private void exit() {
      this.dispose();
   }

   private void importMusics() {
      if (Utils.confirm(this, I18N.MSG_CONFIRM_IMPORT_DB_OFFLINE.val())) {
         PopUpWorker.exe(new MySwingWorker<Void, Void>(this, I18N.MSG_IN_PROGRESS.val()) {
            protected Void doInBackground() throws Exception {
               List<Music> musics = DBOffLineBatchImportView.this.getSelectedMusics().stream().map(m -> m.music).collect(Collectors.toList());
               MusicDao.add(musics);
               DBOffLineBatchImportView.this.addToProfilesPanel.addMusicsInCheckedProfiles(musics);
               return null;
            }

            @Override
            protected void done() {
               super.done();
               Utils.msg(this.owner, I18N.WORD_COMPLETED.val());
               Optional.ofNullable(DBOffLineBatchImportView.this.callback).ifPresent(r -> r.run());
               DBOffLineBatchImportView.this.exit();
            }
         });
      }
   }

   private List<DBOffLineBatchImportView.MusicJTree> filterList(List<Music> list) {
      List<String> artTit = MusicDao.getAll().parallelStream().map(m -> m.getArtist() + ";" + m.getTitle()).collect(Collectors.toList());
      return list.parallelStream()
         .map(m -> new DBOffLineBatchImportView.MusicJTree(m, artTit.contains(m.getArtist() + ";" + m.getTitle())))
         .collect(Collectors.toList());
   }

   private static TreeModel createModel(List<DBOffLineBatchImportView.MusicJTree> l) {
      JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom nodeRoot = new JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom(I18N.WORD_MUSIC.val());
      List<String> langs = l.parallelStream().map(DBOffLineBatchImportView.MusicJTree::getLanguage).distinct().collect(Collectors.toList());
      List<String> artists = l.parallelStream().map(DBOffLineBatchImportView.MusicJTree::getArtist).distinct().collect(Collectors.toList());
      artists.sort((o1, o2) -> Utils.removeAccent(o1).compareToIgnoreCase(Utils.removeAccent(o2)));
      langs.stream()
         .forEach(
            language -> {
               JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom nodeLang = new JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom(languageToAlias(language));
               if (!language.equals("Cantor Cristão") && !language.equals("Harpa Cristã")) {
                  artists.forEach(
                     artist -> {
                        JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom nodeArt = new JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom(artist);
                        List<DBOffLineBatchImportView.MusicJTree> addx = l.parallelStream()
                           .filter(m -> m.getLanguage().equals(language) && m.getArtist().equalsIgnoreCase(artist))
                           .collect(Collectors.toList());
                        addx.sort((o1, o2) -> Utils.removeAccent(o1.getTitle()).compareToIgnoreCase(Utils.removeAccent(o2.getTitle())));
                        if (!addx.isEmpty()) {
                           nodeLang.add(nodeArt);
                        }

                        addx.stream().map(JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom::new).forEach(nodeArt::add);
                     }
                  );
               } else {
                  List<DBOffLineBatchImportView.MusicJTree> add = l.parallelStream().filter(m -> m.getLanguage().equals(language)).collect(Collectors.toList());
                  add.sort((o1, o2) -> getNumberHCAndCC(o1.getArtist()).compareTo(getNumberHCAndCC(o2.getArtist())));
                  add.stream().map(JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom::new).forEach(nodeLang::add);
               }

               nodeRoot.add(nodeLang);
            }
         );
      return new DefaultTreeModel(nodeRoot);
   }

   private static String languageToAlias(String language) {
      switch (language) {
         case "pt":
            return "Português";
         case "en":
            return "English";
         default:
            return language;
      }
   }

   private static String getNumberHCAndCC(String t) {
      try {
         return t.substring(t.length() - 3, t.length());
      } catch (Exception var2) {
         return "000";
      }
   }

   private void updateInfo() {
      this.jLabelInfo.setText(I18N.MSG_SELECTED_SONGS.format(this.getSelectedMusics().size()));
      this.jButtonImport.setEnabled(!this.getSelectedMusics().isEmpty());
   }

   private List<DBOffLineBatchImportView.MusicJTree> getSelectedMusics() {
      return this.tree
         .getSelectedObjects()
         .parallelStream()
         .filter(DBOffLineBatchImportView.MusicJTree.class::isInstance)
         .map(DBOffLineBatchImportView.MusicJTree.class::cast)
         .collect(Collectors.toList());
   }

   public static class MusicJTree {
      public final Music music;
      public final String toString;
      public final boolean alreadyAdded;

      public MusicJTree(Music music, boolean alreadyAdded) {
         this.music = music;
         this.alreadyAdded = alreadyAdded;
         String s = MusicUtils.isCCorHC(music) ? DBOffLineBatchImportView.getNumberHCAndCC(music.getArtist()) + " - " + music.getTitle() : music.getTitle();
         if (alreadyAdded) {
            s = s + " [" + I18N.MSG_ALREADY_ADDED.val() + "]";
         }

         this.toString = s;
      }

      public String getLanguage() {
         return this.music.getLanguage();
      }

      public String getArtist() {
         return this.music.getArtist();
      }

      public String getTitle() {
         return this.music.getTitle();
      }

      @Override
      public String toString() {
         return this.toString;
      }
   }
}
