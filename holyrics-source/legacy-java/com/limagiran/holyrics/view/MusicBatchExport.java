package com.limagiran.holyrics.view;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.swing.ListCellRendererSelectedMusicsBatchExport;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.view.panel.MusicSearchPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

public class MusicBatchExport extends JDialog {
   private static MusicBatchExport INSTANCE;
   private final DefaultListModel<Music> listModelMusics = new DefaultListModel<>();
   private final MusicSearchPanel musicSearchPanel = this.createMusicSearchPanel();
   private JButton jButtonAdd;
   private JButton jButtonCancel;
   private JButton jButtonRemove;
   private JButton jButtonSave;
   private JCheckBox jCheckBoxSingleFile;
   private JLabel jLabelTitle;
   private JList<Music> jListMusicsToSave;
   private JPanel jPanelButtons;
   private JPanel jPanelGeneral;
   private JPanel jPanelMusicSearchPanel;
   private JPanel jPanelRight;
   private JPanel jPanelRightBottom;
   private JPanel jPanelRightLeft;
   private JScrollPane jScrollPaneMusicsToSave;
   private JSplitPane jSplitPane;

   public MusicBatchExport(Frame parent) {
      super(parent);
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonSave.addActionListener(e -> this.save());
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
      this.jLabelTitle = new JLabel();
      this.jSplitPane = new JSplitPane();
      this.jPanelMusicSearchPanel = new JPanel();
      this.jPanelRight = new JPanel();
      this.jScrollPaneMusicsToSave = new JScrollPane();
      this.jListMusicsToSave = new JList<>();
      this.jPanelRightLeft = new JPanel();
      this.jButtonAdd = new JButton();
      this.jButtonRemove = new JButton();
      this.jPanelRightBottom = new JPanel();
      this.jCheckBoxSingleFile = new JCheckBox();
      this.jPanelButtons = new JPanel();
      this.jButtonSave = new JButton();
      this.jButtonCancel = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.batchExport")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(800, 500));
      this.setModal(true);
      this.setPreferredSize(new Dimension(1000, 600));
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeneral.setLayout(new BorderLayout());
      this.jLabelTitle.setFont(new Font("Georgia", 0, 24));
      this.jLabelTitle.setHorizontalAlignment(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelTitle.setText(bundle.getString("msg.selectMusics"));
      this.jLabelTitle.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelGeneral.add(this.jLabelTitle, "First");
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
      this.jPanelRightBottom.setLayout(new FlowLayout(1, 15, 5));
      this.jCheckBoxSingleFile.setFont(new Font("Arial", 0, 11));
      this.jCheckBoxSingleFile.setText(bundle.getString("msg.singleFile"));
      this.jCheckBoxSingleFile.setFocusable(false);
      this.jPanelRightBottom.add(this.jCheckBoxSingleFile);
      this.jPanelButtons.setLayout(new GridLayout(1, 0, 25, 0));
      this.jButtonSave.setFont(new Font("Georgia", 0, 18));
      this.jButtonSave.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.save")));
      this.jButtonSave.setFocusable(false);
      this.jPanelButtons.add(this.jButtonSave);
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

   public static void main(Frame parent) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new MusicBatchExport(parent);
         }

         INSTANCE.setVisible(true);
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
               int i = MusicBatchExport.this.jListMusicsToSave.locationToIndex(e.getPoint());
               if (i != -1 && !Arrays.stream(MusicBatchExport.this.jListMusicsToSave.getSelectedIndices()).anyMatch(x -> x == i)) {
                  MusicBatchExport.this.jListMusicsToSave.setSelectedIndex(MusicBatchExport.this.jListMusicsToSave.locationToIndex(e.getPoint()));
               }

               MusicBatchExport.this.popupMenuList(e);
            }
         }
      });
   }

   private void save() {
      if (this.checkSave()) {
         if (this.jCheckBoxSingleFile.isSelected()) {
            this.saveSingle();
         } else {
            this.saveMultiple();
         }
      }
   }

   private boolean checkSave() {
      if (this.listModelMusics.size() <= 0) {
         Utils.error(this, I18N.MSG_ERROR_FILE_NOT_SELECTED.val());
         return false;
      } else {
         return true;
      }
   }

   private void saveSingle() {
      Optional.ofNullable(Dir.windowsSave(I18N.WORD_FILE.val(), (File)null, "Music File List(*.mufl)", ".mufl")).ifPresent(f -> {
         List<Music> musics = new ArrayList<>(Collections.list(this.listModelMusics.elements()));
         RWObj.write(musics, f);
         Utils.msg(this, com.limagiran.lgsaveopen.I18N.WORD_DONE);
      });
   }

   private void saveMultiple() {
      Optional.ofNullable(Dir.windowsDir(Dir.WORD_DIRECTORY, null))
         .filter(File::isDirectory)
         .ifPresent(
            f -> {
               String format = f.getAbsolutePath() + File.separator + "%s - %s" + ".muf";
               Collections.list(this.listModelMusics.elements())
                  .forEach(m -> m.save(new File(String.format(format, m.getId(), Dir.removeInvalidChar(m.toString())))));
               Utils.msg(this, com.limagiran.lgsaveopen.I18N.WORD_DONE);
            }
         );
   }

   private MusicSearchPanel createMusicSearchPanel() {
      return new MusicSearchPanel(this, true, false) {
         @Override
         public void musicChanged(Music music) {
         }

         @Override
         public void doubleClick(Music music) {
            MusicBatchExport.this.add(music);
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
            return MusicBatchExport.this;
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
      JPopupMenu popup = new JPopupMenu();
      if (SwingUtils.pointOnTheSelectedItem(this.jListMusicsToSave, evt.getPoint())) {
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val());
         remove.addActionListener(e -> this.remove(this.jListMusicsToSave.getSelectedValuesList()));
         popup.add(remove);
      }

      if (!this.listModelMusics.isEmpty()) {
         JMenuItem clear = new JMenuItem(I18N.MSG_REMOVE_ALL.val());
         clear.addActionListener(e -> this.remove(Collections.list(this.listModelMusics.elements())));
         popup.add(clear);
      }

      if (popup.getComponentCount() > 0) {
         popup.show(this.jListMusicsToSave, evt.getX(), evt.getY());
      }
   }
}
