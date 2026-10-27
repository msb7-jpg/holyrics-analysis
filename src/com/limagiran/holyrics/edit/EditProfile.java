package com.limagiran.holyrics.edit;

import com.alee.laf.toolbar.WebToolBar;
import com.limagiran.holyrics.control.ProfileController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Profile;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.swing.ListCellRendererMusic;
import com.limagiran.holyrics.swing.ListCellRendererProfile;
import com.limagiran.holyrics.swing.ListModelAddRemove;
import com.limagiran.holyrics.swing.ListModelProfile;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.PopUpMenu;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class EditProfile extends JPanel implements IEditView {
   private Profile profile;
   private final ListModelProfile listModelProfile = new ListModelProfile();
   private final ListModelAddRemove listModelMusicList = new ListModelAddRemove();
   private final ListModelAddRemove listModelMusicProfile = new ListModelAddRemove();
   private final List<Music> filteredProfile = new ArrayList<>();
   private final List<Music> filteredMusic = new ArrayList<>();
   private final MusicUtils.MusicFilter musicFilterProfile = new MusicUtils.MusicFilter();
   private final MusicUtils.MusicFilter musicFilterMusics = new MusicUtils.MusicFilter();
   private JButton jButtonAdd;
   private JButton jButtonAddMusic;
   private JButton jButtonRemove;
   private JButton jButtonremoveMusic;
   private JComboBox<String> jComboBoxMaxResultMusic;
   private JComboBox<String> jComboBoxMaxResultProfile;
   private JLabel jLabelAlert;
   private JLabel jLabelMusicList;
   private JLabel jLabelMusics;
   private JLabel jLabelProfile;
   private JLabel jLabelProfileList;
   private JList<Music> jListMusicsList;
   private JList<Music> jListMusicsProfile;
   private JList<Profile> jListProfiles;
   private JPanel jPanelCenter;
   private JPanel jPanelCenterButtonAdd;
   private JPanel jPanelCenterButtonsRemove;
   private JPanel jPanelList;
   private JPanel jPanelMusics;
   private JPanel jPanelMusicsHeader;
   private JPanel jPanelMusicsListFooter;
   private JPanel jPanelProfile;
   private JPanel jPanelProfileHeader;
   private JPanel jPanelProfileList;
   private JPanel jPanelProfileListFooter;
   private JScrollPane jScrollPaneMusics;
   private JScrollPane jScrollPaneMusicsProfile;
   private JScrollPane jScrollPaneProfiles;
   private JTextField jTextFieldFilterMusicList;
   private JTextField jTextFieldFilterMusicProfile;
   private JTextField jTextFieldProfileName;
   private JToolBar jToolBar;

   public EditProfile() {
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jTextFieldProfileName.setDocument(SwingUtils.createDocMaxSize(32));
      Arrays.asList(this.musicFilterMusics, this.musicFilterProfile).forEach(mf -> mf.setLyrics(false).setNote(false));
      this.jTextFieldFilterMusicList.getDocument().addDocumentListener(new DocumentListenerAuto(500L, this::filterMusicsList));
      this.jTextFieldFilterMusicProfile.getDocument().addDocumentListener(new DocumentListenerAuto(500L, this::filterMusicsProfile));
      this.jListProfiles.setSelectedIndex(0);
      this.profileChanged(this.jListProfiles.getSelectedValue());
      this.jListProfiles.addMouseListener(this.jListMouseListenerDefault(this.jListProfiles));
      this.jListMusicsProfile.addMouseListener(this.jListMouseListenerDefault(this.jListMusicsProfile));
      this.jListMusicsList.addMouseListener(this.jListMouseListenerDefault(this.jListMusicsList));
      ((WebToolBar)this.jToolBar).setUndecorated(true);
   }

   private MouseAdapter jListMouseListenerDefault(final JList list) {
      return new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
               int i = list.locationToIndex(e.getPoint());
               if (i != -1 && !Arrays.stream(list.getSelectedIndices()).anyMatch(x -> x == i)) {
                  list.setSelectedIndex(list.locationToIndex(e.getPoint()));
               }

               EditProfile.this.popUpMenuJList(e, list);
            }
         }
      };
   }

   private void popUpMenuJList(MouseEvent e, JList list) {
      if (list.equals(this.jListProfiles)) {
         PopUpMenu.profileEditPopUpProfile(this, this.jListProfiles, e);
      } else if (list.equals(this.jListMusicsProfile)) {
         PopUpMenu.profileEditPopUpMusicsProfile(this, this.jListMusicsProfile, e);
      } else if (list.equals(this.jListMusicsList)) {
         PopUpMenu.profileEditPopUpMusics(this, this.jListMusicsList, e);
      }
   }

   private void initComponents() {
      this.jPanelProfileList = new JPanel();
      this.jToolBar = new WebToolBar();
      this.jButtonAdd = new JButton();
      this.jButtonRemove = new JButton();
      this.jScrollPaneProfiles = new JScrollPane();
      this.jListProfiles = new JList<>();
      this.jPanelList = new JPanel();
      this.jPanelProfile = new JPanel();
      this.jPanelProfileHeader = new JPanel();
      this.jLabelProfile = new JLabel();
      this.jTextFieldProfileName = new JTextField();
      this.jLabelAlert = new JLabel();
      this.jTextFieldFilterMusicProfile = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_FILTER.val());
      this.jScrollPaneMusicsProfile = new JScrollPane();
      this.jListMusicsProfile = new JList<>();
      this.jPanelProfileListFooter = new JPanel();
      this.jLabelProfileList = new JLabel();
      this.jComboBoxMaxResultProfile = new JComboBox<>();
      this.jPanelCenter = new JPanel();
      this.jPanelCenterButtonAdd = new JPanel();
      this.jButtonAddMusic = new JButton();
      this.jPanelCenterButtonsRemove = new JPanel();
      this.jButtonremoveMusic = new JButton();
      this.jPanelMusics = new JPanel();
      this.jScrollPaneMusics = new JScrollPane();
      this.jListMusicsList = new JList<>();
      this.jPanelMusicsHeader = new JPanel();
      this.jLabelMusics = new JLabel();
      this.jTextFieldFilterMusicList = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_FILTER.val());
      this.jPanelMusicsListFooter = new JPanel();
      this.jLabelMusicList = new JLabel();
      this.jComboBoxMaxResultMusic = new JComboBox<>();
      this.setBorder(BorderFactory.createEtchedBorder());
      this.setMinimumSize(new Dimension(720, 392));
      this.jPanelProfileList.setPreferredSize(new Dimension(150, 171));
      this.jPanelProfileList.setLayout(new BorderLayout());
      this.jToolBar.setFloatable(false);
      this.jToolBar.setRollover(true);
      this.jButtonAdd.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/plus32.png")));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jButtonAdd.setToolTipText(bundle.getString("word.add"));
      this.jButtonAdd.setFocusable(false);
      this.jButtonAdd.setHorizontalTextPosition(0);
      this.jButtonAdd.setVerticalTextPosition(3);
      this.jButtonAdd.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            EditProfile.this.jButtonAddActionPerformed(evt);
         }
      });
      this.jToolBar.add(this.jButtonAdd);
      this.jButtonRemove.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/delete32.png")));
      this.jButtonRemove.setToolTipText(bundle.getString("word.remove"));
      this.jButtonRemove.setEnabled(false);
      this.jButtonRemove.setFocusable(false);
      this.jButtonRemove.setHorizontalTextPosition(0);
      this.jButtonRemove.setVerticalTextPosition(3);
      this.jButtonRemove.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            EditProfile.this.jButtonRemoveActionPerformed(evt);
         }
      });
      this.jToolBar.add(this.jButtonRemove);
      this.jPanelProfileList.add(this.jToolBar, "First");
      this.jScrollPaneProfiles.setPreferredSize(new Dimension(200, 130));
      this.jListProfiles.setFont(new Font("Georgia", 0, 12));
      this.jListProfiles.setModel(this.listModelProfile);
      this.jListProfiles.setSelectionMode(0);
      this.jListProfiles.setCellRenderer(new ListCellRendererProfile());
      this.jListProfiles.setFixedCellWidth(160);
      this.jListProfiles.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            EditProfile.this.jListProfilesKeyReleased(evt);
         }
      });
      this.jListProfiles.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent evt) {
            EditProfile.this.jListProfilesValueChanged(evt);
         }
      });
      this.jScrollPaneProfiles.setViewportView(this.jListProfiles);
      this.jPanelProfileList.add(this.jScrollPaneProfiles, "Center");
      this.jPanelProfile.setLayout(new BorderLayout());
      this.jPanelProfileHeader.setLayout(new BorderLayout());
      this.jLabelProfile.setFont(new Font("Georgia", 0, 16));
      this.jLabelProfile.setHorizontalAlignment(0);
      this.jLabelProfile.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.profile")));
      this.jPanelProfileHeader.add(this.jLabelProfile, "First");
      this.jTextFieldProfileName.setFont(new Font("Georgia", 0, 14));
      this.jTextFieldProfileName.setHorizontalAlignment(0);
      this.jTextFieldProfileName.addFocusListener(new FocusAdapter() {
         @Override
         public void focusLost(FocusEvent evt) {
            EditProfile.this.jTextFieldProfileNameFocusLost(evt);
         }
      });
      this.jTextFieldProfileName.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            EditProfile.this.jTextFieldProfileNameKeyReleased(evt);
         }
      });
      this.jPanelProfileHeader.add(this.jTextFieldProfileName, "Center");
      this.jLabelAlert.setFont(new Font("Georgia", 0, 10));
      this.jLabelAlert.setHorizontalAlignment(2);
      this.jPanelProfileHeader.add(this.jLabelAlert, "East");
      this.jTextFieldFilterMusicProfile.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldFilterMusicProfile.setHorizontalAlignment(0);
      this.jPanelProfileHeader.add(this.jTextFieldFilterMusicProfile, "Last");
      this.jPanelProfile.add(this.jPanelProfileHeader, "First");
      this.jListMusicsProfile.setFont(new Font("Georgia", 0, 14));
      this.jListMusicsProfile.setModel(this.listModelMusicProfile);
      this.jListMusicsProfile
         .setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.ttDoubleClickToRemove")));
      this.jListMusicsProfile.setCellRenderer(new ListCellRendererMusic(this.musicFilterProfile));
      this.jListMusicsProfile.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            EditProfile.this.jListMusicsProfileMouseReleased(evt);
         }
      });
      this.jListMusicsProfile.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            EditProfile.this.jListMusicsProfileKeyReleased(evt);
         }
      });
      this.jScrollPaneMusicsProfile.setViewportView(this.jListMusicsProfile);
      this.jPanelProfile.add(this.jScrollPaneMusicsProfile, "Center");
      this.jPanelProfileListFooter.setLayout(new FlowLayout(1, 5, 2));
      this.jLabelProfileList.setFont(new Font("Georgia", 0, 12));
      this.jLabelProfileList.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.list")));
      this.jPanelProfileListFooter.add(this.jLabelProfileList);
      this.jComboBoxMaxResultProfile.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxMaxResultProfile.setModel(new DefaultComboBoxModel<>(new String[]{"100", "250", "500"}));
      this.jComboBoxMaxResultProfile.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            EditProfile.this.jComboBoxMaxResultProfileItemStateChanged(evt);
         }
      });
      this.jPanelProfileListFooter.add(this.jComboBoxMaxResultProfile);
      this.jPanelProfile.add(this.jPanelProfileListFooter, "Last");
      this.jPanelCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelCenter.setLayout(new GridLayout(2, 0, 0, 10));
      this.jPanelCenterButtonAdd.setLayout(new BorderLayout());
      this.jButtonAddMusic.setFont(new Font("Times New Roman", 1, 24));
      this.jButtonAddMusic.setText(" « ");
      this.jButtonAddMusic.setFocusable(false);
      this.jButtonAddMusic.setMargin(new Insets(0, 5, 3, 5));
      this.jButtonAddMusic.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            EditProfile.this.jButtonAddMusicActionPerformed(evt);
         }
      });
      this.jPanelCenterButtonAdd.add(this.jButtonAddMusic, "Last");
      this.jPanelCenter.add(this.jPanelCenterButtonAdd);
      this.jPanelCenterButtonsRemove.setLayout(new BorderLayout());
      this.jButtonremoveMusic.setFont(new Font("Times New Roman", 1, 24));
      this.jButtonremoveMusic.setText(" » ");
      this.jButtonremoveMusic.setFocusable(false);
      this.jButtonremoveMusic.setMargin(new Insets(0, 5, 3, 5));
      this.jButtonremoveMusic.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            EditProfile.this.jButtonremoveMusicActionPerformed(evt);
         }
      });
      this.jPanelCenterButtonsRemove.add(this.jButtonremoveMusic, "First");
      this.jPanelCenter.add(this.jPanelCenterButtonsRemove);
      this.jPanelMusics.setLayout(new BorderLayout());
      this.jListMusicsList.setFont(new Font("Georgia", 0, 14));
      this.jListMusicsList.setModel(this.listModelMusicList);
      this.jListMusicsList
         .setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.ttDoubleClickToAdd")));
      this.jListMusicsList.setCellRenderer(new ListCellRendererMusic(this.musicFilterMusics));
      this.jListMusicsList.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            EditProfile.this.jListMusicsListMouseReleased(evt);
         }
      });
      this.jScrollPaneMusics.setViewportView(this.jListMusicsList);
      this.jPanelMusics.add(this.jScrollPaneMusics, "Center");
      this.jPanelMusicsHeader.setLayout(new BorderLayout());
      this.jLabelMusics.setFont(new Font("Georgia", 0, 16));
      this.jLabelMusics.setHorizontalAlignment(0);
      this.jLabelMusics.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.musics")));
      this.jPanelMusicsHeader.add(this.jLabelMusics, "First");
      this.jTextFieldFilterMusicList.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldFilterMusicList.setHorizontalAlignment(0);
      this.jPanelMusicsHeader.add(this.jTextFieldFilterMusicList, "Center");
      this.jPanelMusics.add(this.jPanelMusicsHeader, "First");
      this.jPanelMusicsListFooter.setLayout(new FlowLayout(1, 5, 2));
      this.jLabelMusicList.setFont(new Font("Georgia", 0, 12));
      this.jLabelMusicList.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.list")));
      this.jPanelMusicsListFooter.add(this.jLabelMusicList);
      this.jComboBoxMaxResultMusic.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxMaxResultMusic.setModel(new DefaultComboBoxModel<>(new String[]{"100", "250", "500"}));
      this.jComboBoxMaxResultMusic.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            EditProfile.this.jComboBoxMaxResultMusicItemStateChanged(evt);
         }
      });
      this.jPanelMusicsListFooter.add(this.jComboBoxMaxResultMusic);
      this.jPanelMusics.add(this.jPanelMusicsListFooter, "Last");
      GroupLayout jPanelListLayout = new GroupLayout(this.jPanelList);
      this.jPanelList.setLayout(jPanelListLayout);
      jPanelListLayout.setHorizontalGroup(
         jPanelListLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jPanelListLayout.createSequentialGroup()
                  .addComponent(this.jPanelProfile, -1, 250, 32767)
                  .addGap(0, 0, 0)
                  .addComponent(this.jPanelCenter, -2, -1, -2)
                  .addGap(0, 0, 0)
                  .addComponent(this.jPanelMusics, -1, 260, 32767)
            )
      );
      jPanelListLayout.setVerticalGroup(
         jPanelListLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(this.jPanelProfile, -1, -1, 32767)
            .addComponent(this.jPanelCenter, -1, -1, 32767)
            .addComponent(this.jPanelMusics, -1, -1, 32767)
      );
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addComponent(this.jPanelProfileList, -2, 250, -2)
                  .addGap(4, 4, 4)
                  .addComponent(this.jPanelList, -1, -1, 32767)
                  .addGap(0, 0, 0)
            )
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addComponent(this.jPanelList, -1, -1, 32767)
            .addComponent(this.jPanelProfileList, Alignment.TRAILING, -1, -1, 32767)
      );
   }

   private void jListProfilesValueChanged(ListSelectionEvent evt) {
      if (!evt.getValueIsAdjusting()) {
         this.profileChanged(this.jListProfiles.getSelectedValue());
      }
   }

   private void jTextFieldProfileNameFocusLost(FocusEvent evt) {
      this.profileChangeName();
   }

   private void jTextFieldProfileNameKeyReleased(KeyEvent evt) {
      if (evt.getKeyCode() == 10) {
         this.requestFocus();
      } else if (evt.getKeyCode() == 27) {
         this.jTextFieldProfileName.setText(this.profile != null ? this.profile.getName() : "");
         this.requestFocus();
      }
   }

   private void jButtonAddMusicActionPerformed(ActionEvent evt) {
      this.addSelectedMusics();
   }

   private void jButtonAddActionPerformed(ActionEvent evt) {
      this.addProfile();
   }

   private void jButtonRemoveActionPerformed(ActionEvent evt) {
      this.removeSelectedProfile();
   }

   private void jListMusicsListMouseReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt)) {
         this.addSelectedMusics();
         Utils.clearDoubleClick();
      }
   }

   private void jButtonremoveMusicActionPerformed(ActionEvent evt) {
      this.removeSelectedMusics();
   }

   private void jListMusicsProfileMouseReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt)) {
         this.removeSelectedMusics();
         Utils.clearDoubleClick();
      }
   }

   private void jListProfilesKeyReleased(KeyEvent evt) {
      if (evt.getKeyCode() == 127) {
         this.removeSelectedProfile();
      }
   }

   private void jComboBoxMaxResultProfileItemStateChanged(ItemEvent evt) {
      this.updateFilteredList(this.jComboBoxMaxResultProfile, this.listModelMusicProfile, this.filteredProfile, this.jListMusicsProfile);
   }

   private void jComboBoxMaxResultMusicItemStateChanged(ItemEvent evt) {
      this.updateFilteredList(this.jComboBoxMaxResultMusic, this.listModelMusicList, this.filteredMusic, this.jListMusicsList);
   }

   private void jListMusicsProfileKeyReleased(KeyEvent evt) {
      if (evt.getKeyCode() == 127) {
         this.removeSelectedMusics();
      }
   }

   private void profileChanged(Profile p) {
      this.profile = p;
      this.jTextFieldProfileName.setText(this.profile != null ? this.profile.getName() : "");
      Arrays.asList(this.jButtonRemove, this.jTextFieldProfileName, this.jTextFieldFilterMusicProfile).forEach(c -> c.setEnabled(this.profile != null));
      this.filterMusicsList();
      this.filterMusicsProfile();
   }

   private void profileChangeName() {
      if (this.profile != null && !this.profile.getName().equals(this.jTextFieldProfileName.getText())) {
         String name = this.jTextFieldProfileName.getText().trim();
         if (this.validateName(name)) {
            this.profile.setName(name);
            this.listModelProfile.changed();
            this.jListProfiles.setSelectedValue(this.profile, true);
         } else {
            this.jTextFieldProfileName.setText(this.profile.getName());
         }
      } else {
         this.jLabelAlert.setText("");
         SwingUtils.redAlert(this.jLabelAlert);
      }
   }

   private boolean validateName(String name) {
      this.jLabelAlert.setText("");
      if (!name.replace(" ", "").isEmpty() && !ProfileController.contains(name)) {
         return true;
      } else {
         this.jLabelAlert.setText(I18N.WORD_INVALID.val());
         SwingUtils.redAlert(this.jLabelAlert);
         this.jTextFieldProfileName.requestFocus();
         return false;
      }
   }

   private void filterMusicsList() {
      List<Music> founds = new ArrayList<>();
      if (this.profile != null) {
         this.musicFilterMusics.setSearch(this.jTextFieldFilterMusicList.getText());
         founds.addAll(
            MusicUtils.filter(
               MusicDao.getAllToSearch().parallelStream().filter(m -> !this.profile.containsMusic(m)).collect(Collectors.toList()),
               this.musicFilterMusics,
               false
            )
         );
      }

      founds.sort(MusicUtils.EnumOrderBy.sortTitle());
      this.filteredMusic.clear();
      int max = Integer.parseInt(this.jComboBoxMaxResultMusic.getItemAt(this.jComboBoxMaxResultMusic.getItemCount() - 1));
      this.filteredMusic.addAll(founds.subList(0, Math.min(founds.size(), max)));
      this.updateFilteredList(this.jComboBoxMaxResultMusic, this.listModelMusicList, this.filteredMusic, this.jListMusicsList);
   }

   private void filterMusicsProfile() {
      List<Music> founds = new ArrayList<>();
      if (this.profile != null) {
         this.musicFilterProfile.setSearch(this.jTextFieldFilterMusicProfile.getText());
         founds.addAll(
            MusicUtils.filter(
               this.profile.getMusics().parallelStream().filter(m -> this.profile.containsMusic(m)).collect(Collectors.toList()), this.musicFilterProfile, true
            )
         );
      }

      founds.sort(MusicUtils.EnumOrderBy.sortTitle());
      this.filteredProfile.clear();
      int max = Integer.parseInt(this.jComboBoxMaxResultProfile.getItemAt(this.jComboBoxMaxResultProfile.getItemCount() - 1));
      this.filteredProfile.addAll(founds.subList(0, Math.min(founds.size(), max)));
      this.updateFilteredList(this.jComboBoxMaxResultProfile, this.listModelMusicProfile, this.filteredProfile, this.jListMusicsProfile);
   }

   public void updateFilteredList(JComboBox cmbbx, ListModelAddRemove listModel, List<Music> filtered, JList<Music> jlist) {
      SwingUtilities.invokeLater(() -> {
         int index = jlist.getSelectedIndex();
         int max = Integer.parseInt(cmbbx.getSelectedItem().toString());
         listModel.removeAllElements(false);
         listModel.addElements(filtered.subList(0, Math.min(filtered.size(), max)));
         int maxIndex = listModel.getSize() - 1;
         jlist.setSelectedIndex(index <= maxIndex ? index : maxIndex);
         jlist.ensureIndexIsVisible(index <= maxIndex ? index : maxIndex);
      });
   }

   public void addSelectedMusics() {
      if (this.jButtonAddMusic.isEnabled() && this.profile != null) {
         List<Long> ids = this.jListMusicsList.getSelectedValuesList().stream().map(Music::getId).collect(Collectors.toList());
         this.profile.addIds(ids);
         this.filterMusicsProfile();
         this.filterMusicsList();
      }
   }

   public void addProfile() {
      Profile p = ProfileController.create();
      this.listModelProfile.addElement(p);
      this.listModelProfile.sort();
      this.jListProfiles.setSelectedValue(p, true);
      this.jTextFieldProfileName.requestFocus();
      this.jTextFieldProfileName.selectAll();
   }

   public void removeSelectedProfile() {
      if (this.profile != null && Utils.confirm(this, I18N.MSG_CONFIRM_REMOVE_PROFILE.format(this.profile.getName()), I18N.WORD_CONFIRM.val())) {
         this.listModelProfile.removeElement(this.profile);
         this.jListProfiles.setSelectedIndex(0);
         this.profileChanged(this.jListProfiles.getSelectedValue());
      }
   }

   @Override
   public boolean exit() {
      return true;
   }

   @Override
   public String getTitle() {
      return I18N.WORD_PROFILES.val();
   }

   @Override
   public JPanel getPanel() {
      return this;
   }

   public void removeSelectedMusics() {
      this.profile.removeMusics(this.jListMusicsProfile.getSelectedValuesList());
      this.filterMusicsProfile();
      this.filterMusicsList();
   }

   @Override
   public ImageIcon getIcon() {
      return new ImageIcon(Images.load("profile16.png"));
   }
}
