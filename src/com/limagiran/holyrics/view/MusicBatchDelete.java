package com.limagiran.holyrics.view;

import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.swing.ListCellRendererSelectedMusicsBatchExport;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.panel.MusicSearchPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

public class MusicBatchDelete extends JDialog {
   private static MusicBatchDelete INSTANCE;
   private final DefaultListModel<Music> listModelMusics = new DefaultListModel<>();
   private final MusicSearchPanel musicSearchPanel = this.createMusicSearchPanel();
   private Runnable callback;
   private JButton jButtonAdd;
   private JButton jButtonCancel;
   private JButton jButtonDelete;
   private JButton jButtonRemove;
   private JList<Music> jListMusicsToSave;
   private JPanel jPanelButtons;
   private JPanel jPanelGeneral;
   private JPanel jPanelMusicSearchPanel;
   private JPanel jPanelRight;
   private JPanel jPanelRightBottom;
   private JPanel jPanelRightLeft;
   private JScrollPane jScrollPaneMusicsToSave;
   private JSplitPane jSplitPane;

   public MusicBatchDelete(Frame parent) {
      super(parent);
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonDelete.addActionListener(e -> this.delete());
      this.jButtonCancel.addActionListener(e -> this.exit());
      this.jButtonAdd.addActionListener(e -> this.add(this.musicSearchPanel.getSelectedMusics()));
      this.jButtonRemove.addActionListener(e -> this.remove(this.jListMusicsToSave.getSelectedValuesList()));
      this.jPanelMusicSearchPanel.add(this.musicSearchPanel, "Center");
      this.musicSearchPanel.clearFilter();
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      this.jListMouseListenerDefault();
      SwingUtils.maximize(this, 150);
      this.musicSearchPanel.setRemoveIfExists(this.listModelMusics);
      SwingUtils.setJPanelToOpaqueFalse(this);
      this.musicSearchPanel.focusOnTextFieldSearch();
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jSplitPane = new JSplitPane();
      this.jPanelMusicSearchPanel = new JPanel();
      this.jPanelRight = new JPanel();
      this.jScrollPaneMusicsToSave = new JScrollPane();
      this.jListMusicsToSave = new JList<>();
      this.jPanelRightLeft = new JPanel();
      this.jButtonAdd = new JButton();
      this.jButtonRemove = new JButton();
      this.jPanelRightBottom = new JPanel();
      this.jPanelButtons = new JPanel();
      this.jButtonDelete = new JButton();
      this.jButtonCancel = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.batchDelete")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(800, 500));
      this.setModal(true);
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeneral.setLayout(new BorderLayout());
      this.jSplitPane.setDividerLocation(320);
      this.jPanelMusicSearchPanel.setLayout(new BorderLayout());
      this.jSplitPane.setLeftComponent(this.jPanelMusicSearchPanel);
      this.jPanelRight.setLayout(new BorderLayout());
      this.jScrollPaneMusicsToSave.setFocusable(false);
      this.jListMusicsToSave.setFont(new Font("Franklin Gothic Medium", 0, 16));
      this.jListMusicsToSave.setModel(this.listModelMusics);
      this.jListMusicsToSave.setCellRenderer(new ListCellRendererSelectedMusicsBatchExport());
      this.jListMusicsToSave.setFixedCellWidth(280);
      this.jListMusicsToSave.setLayoutOrientation(1);
      this.jListMusicsToSave.setVisibleRowCount(-1);
      this.jScrollPaneMusicsToSave.setViewportView(this.jListMusicsToSave);
      this.jPanelRight.add(this.jScrollPaneMusicsToSave, "Center");
      this.jPanelRightLeft.setLayout(new GridBagLayout());
      this.jButtonAdd.setFont(new Font("Times New Roman", 1, 24));
      this.jButtonAdd.setText(" » ");
      this.jButtonAdd.setFocusable(false);
      this.jButtonAdd.setMargin(new Insets(0, 5, 3, 5));
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(10, 10, 10, 10);
      this.jPanelRightLeft.add(this.jButtonAdd, gridBagConstraints);
      this.jButtonRemove.setFont(new Font("Times New Roman", 1, 24));
      this.jButtonRemove.setText(" « ");
      this.jButtonRemove.setFocusable(false);
      this.jButtonRemove.setMargin(new Insets(0, 5, 3, 5));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.insets = new Insets(10, 10, 10, 10);
      this.jPanelRightLeft.add(this.jButtonRemove, gridBagConstraints);
      this.jPanelRight.add(this.jPanelRightLeft, "Before");
      this.jPanelButtons.setLayout(new GridLayout(1, 0, 25, 0));
      this.jButtonDelete.setFont(new Font("Georgia", 0, 18));
      this.jButtonDelete.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.delete")));
      this.jButtonDelete.setFocusable(false);
      this.jPanelButtons.add(this.jButtonDelete);
      this.jButtonCancel.setFont(new Font("Georgia", 0, 18));
      this.jButtonCancel.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.cancel")));
      this.jButtonCancel.setFocusable(false);
      this.jPanelButtons.add(this.jButtonCancel);
      this.jPanelRightBottom.add(this.jPanelButtons);
      this.jPanelRight.add(this.jPanelRightBottom, "Last");
      this.jSplitPane.setRightComponent(this.jPanelRight);
      this.jPanelGeneral.add(this.jSplitPane, "Center");
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(Frame parent, Runnable callback) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new MusicBatchDelete(parent);
         }

         INSTANCE.callback = callback;
         INSTANCE.setVisible(true);
         INSTANCE.musicSearchPanel.clearFilter();
      });
   }

   private void exit() {
      this.dispose();
   }

   private void jListMouseListenerDefault() {
      this.jListMusicsToSave.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
               int i = MusicBatchDelete.this.jListMusicsToSave.locationToIndex(e.getPoint());
               if (i != -1 && !Arrays.stream(MusicBatchDelete.this.jListMusicsToSave.getSelectedIndices()).anyMatch(x -> x == i)) {
                  MusicBatchDelete.this.jListMusicsToSave.setSelectedIndex(MusicBatchDelete.this.jListMusicsToSave.locationToIndex(e.getPoint()));
               }

               MusicBatchDelete.this.popupMenuList(e);
            }
         }
      });
   }

   private void delete() {
      if (this.listModelMusics.isEmpty()) {
         Utils.error(this, I18N.MSG_ERROR_FILE_NOT_SELECTED.val());
      } else if (Utils.confirm(I18N.MSG_CONFIRM_REMOVE_ALL_MUSICS.val())) {
         PopUpWorker.exe(new MySwingWorker<Void, Void>(this) {
            protected Void doInBackground() throws Exception {
               MusicDao.remove(Collections.list(MusicBatchDelete.this.listModelMusics.elements()));
               return null;
            }

            @Override
            protected void done() {
               super.done();
               MusicBatchDelete.this.listModelMusics.clear();
               Optional.ofNullable(MusicBatchDelete.this.callback).ifPresent(Runnable::run);
               Utils.msg(MusicBatchDelete.this, com.limagiran.lgsaveopen.I18N.WORD_DONE);
            }
         });
      }
   }

   private MusicSearchPanel createMusicSearchPanel() {
      return new MusicSearchPanel(this, true, false) {
         @Override
         public void musicChanged(Music music) {
         }

         @Override
         public void doubleClick(Music music) {
            MusicBatchDelete.this.add(music);
         }

         @Override
         public void rightButtonClicked(MouseEvent evt, JList<Music> jlist) {
         }

         @Override
         public void newMusic() {
         }

         @Override
         public boolean keyLeftEvent() {
            return false;
         }

         @Override
         public boolean keyRightEvent() {
            return false;
         }

         @Override
         public Window getOwner() {
            return MusicBatchDelete.this;
         }

         @Override
         public void openEditProfile() {
         }
      };
   }

   private void add(List<Music> musics) {
      musics.stream().filter(m -> !this.listModelMusics.contains(m)).forEach(this.listModelMusics::addElement);
      this.musicSearchPanel.updateFilteredList(this.musicSearchPanel.getNextMusic());
      this.musicSearchPanel.revalidate();
      this.musicSearchPanel.repaint();
   }

   private void remove(List<Music> musics) {
      int index = this.jListMusicsToSave.getMaxSelectionIndex();
      musics.forEach(this.listModelMusics::removeElement);
      this.jListMusicsToSave.setSelectedIndex(Math.min(index, this.jListMusicsToSave.getModel().getSize() - 1));
      this.musicSearchPanel.updateFilteredList(this.musicSearchPanel.getSelectedMusic());
      this.musicSearchPanel.revalidate();
      this.musicSearchPanel.repaint();
   }

   private void add(Music musics) {
      Optional.ofNullable(musics).map(xva$0 -> Arrays.asList(xva$0)).ifPresent(this::add);
   }

   private void popupMenuList(MouseEvent evt) {
      if (SwingUtils.pointOnTheSelectedItem(this.jListMusicsToSave, evt.getPoint())) {
         JPopupMenu popup = new JPopupMenu();
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val());
         remove.addActionListener(e -> this.remove(this.jListMusicsToSave.getSelectedValuesList()));
         popup.add(remove);
         popup.show(this.jListMusicsToSave, evt.getX(), evt.getY());
      }
   }
}
