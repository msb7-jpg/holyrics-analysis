package com.limagiran.holyrics.view.panel;

import com.alee.extended.panel.CollapsiblePaneAdapter;
import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyData;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.event.MusicSearchPanelListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Profile;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ComboBoxModelProfile;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.swing.ListCellRendererMusic;
import com.limagiran.holyrics.swing.ListModelAddRemove;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.view.MusicMain;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public abstract class MusicSearchPanel extends WebPanel implements MusicSearchPanelListener {
   private static final ImageIcon ICON_CHECK_16 = new ImageIcon(Images.load("checkBlack16.png"));
   private static final transient Map<String, List<JCheckBox>> MAP_CHECKED_FIELDS = new HashMap<>();
   private final JLabel webCollapsibleTitle = new JLabel();
   private final ComboBoxModelProfile comboBoxModelProfile = new ComboBoxModelProfile();
   private final ListModelAddRemove listModel = new ListModelAddRemove();
   public final MusicUtils.MusicFilter musicFilter = new MusicUtils.MusicFilter();
   private final ListCellRendererMusic listRenderer = new ListCellRendererMusic(this.musicFilter);
   private JToggleButton buttonsLetterAll;
   private final WebToggleButton[] buttonsLetter = new WebToggleButton[26];
   private final List<Music> filtered = new ArrayList<>();
   private DefaultListModel<Music> removeIfExists;
   private boolean filterBlocked = true;
   private final boolean links;
   private ButtonGroup buttonGroupLetters;
   private JButton jButtonFilterClear;
   private JButton jButtonNewMusic;
   private JCheckBox jCheckBoxFilterArtist;
   private JCheckBox jCheckBoxFilterLyrics;
   private JCheckBox jCheckBoxFilterNote;
   private JCheckBox jCheckBoxFilterTitle;
   private JCheckBox jCheckBoxOrderByInverse;
   private JComboBox<Profile> jComboBoxFilterProfiles;
   private JComboBox<String> jComboBoxMaxResult;
   private JComboBox<MusicUtils.EnumOrderBy> jComboBoxOrderBy;
   private JLabel jLabelList;
   private JLabel jLabelMusics;
   private JLabel jLabelOrderBy;
   private JLabel jLabelOrderBy2;
   private JLabel jLabelProfile;
   private JList<Music> jListMusics;
   private JPanel jPanelButtonsLetters;
   private JPanel jPanelCenter;
   private JPanel jPanelCollapsibleContent;
   private JPanel jPanelFilterOrderBy;
   private JPanel jPanelFilterProfile;
   private JPanel jPanelFirst;
   private JPanel jPanelFooter;
   private JPanel jPanelFooterRight;
   private JPanel jPanelListMusic;
   private JPanel jPanelMusicFilter;
   private JPanel jPanelMusicsHeader;
   private JPanel jPanelSearchOptions;
   private JPanel jPanelSeparator;
   private JScrollPane jScrollPaneMusics;
   private JSeparator jSeparator1;
   private JSeparator jSeparator2;
   private JTextField jTextFieldFilterSearch;

   public MusicSearchPanel(RootPaneContainer parent, boolean multipleSelection, Music _default, boolean shouldScroll, boolean links) {
      super(true);
      this.links = links;
      this.initComponents();
      this.jListMusics.setCellRenderer(this.listRenderer);
      this.init();
      BooleanOptional.runIfTrue(_default != null, () -> this.jListMusics.setSelectedValue(_default, shouldScroll));
      BooleanOptional.runIfTrue(multipleSelection, () -> this.jListMusics.setSelectionMode(2));
      if (parent != null) {
         InsertShortcut.add(parent, InsertShortcut.Shortcut.create(76, 128, this::clearFilter));
      }
   }

   public MusicSearchPanel(RootPaneContainer parent, boolean multipleSelection, boolean links) {
      this(parent, multipleSelection, null, false, links);
   }

   private void init() {
      WebCollapsiblePane col = new WebCollapsiblePane();
      col.setPaintFocus(false);
      col.setOpaque(false);
      this.jPanelMusicFilter.setFocusable(false);
      WebPanel wp = (WebPanel)this.jPanelCollapsibleContent;
      wp.setOpaque(true);
      wp.setBackground(new Color(249, 249, 249));
      wp.setFocusable(false);
      col.setContent(wp);
      col.setShadeWidth(0);
      col.setExpanded(Settings.EnumSettings.MUSIC_FILTER_EXPANDED.is(), false);
      col.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
         public void collapsed(WebCollapsiblePane pane) {
            Settings.EnumSettings.MUSIC_FILTER_EXPANDED.set(false);
            super.collapsed(pane);
         }

         public void expanded(WebCollapsiblePane pane) {
            Settings.EnumSettings.MUSIC_FILTER_EXPANDED.set(true);
            super.expanded(pane);
         }
      });
      col.setTitleComponent(this.jTextFieldFilterSearch);
      col.setFocusable(false);
      col.setAnimate(false);
      this.jPanelFirst.add(col, "Center");
      ((WebButton)this.jButtonFilterClear).setMargin(0);
      List<JCheckBox> chkbxList = Arrays.asList(this.jCheckBoxFilterTitle, this.jCheckBoxFilterArtist, this.jCheckBoxFilterNote, this.jCheckBoxFilterLyrics);
      chkbxList.forEach(chkbx -> chkbx.addItemListener(e -> this.filter()));
      this.jListMusics.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
               JList list = (JList)e.getSource();
               int i = list.locationToIndex(e.getPoint());
               if (i != -1 && !Arrays.stream(list.getSelectedIndices()).anyMatch(x -> x == i)) {
                  list.setSelectedIndex(list.locationToIndex(e.getPoint()));
               }

               MusicSearchPanel.this.rightButtonClicked(e, MusicSearchPanel.this.jListMusics);
            }
         }
      });
      this.buttonsLetterAll = this.createDefaultJToogleButtonLetter("");
      this.buttonsLetterAll.setIcon(ICON_CHECK_16);
      this.buttonsLetterAll.setSelected(true);
      this.buttonGroupLetters.add(this.buttonsLetterAll);
      this.jPanelButtonsLetters.add(this.buttonsLetterAll);

      for (int i = 0; i < this.buttonsLetter.length; i++) {
         char c = (char)(65 + i);
         this.buttonsLetter[i] = this.createDefaultJToogleButtonLetter(String.valueOf(c));
         this.buttonGroupLetters.add(this.buttonsLetter[i]);
         this.jPanelButtonsLetters.add(this.buttonsLetter[i]);
      }

      this.jTextFieldFilterSearch.getDocument().addDocumentListener(new DocumentListenerAuto(500L, this::filter));
      this.jTextFieldFilterSearch.setToolTipText(I18N.WORD_MUSICS.val() + ": " + MusicDao.getSize());
      this.clearFilter();
      this.jButtonNewMusic.setVisible(this.links);
      this.setupWebLaf();
      this.addToolTipButtons();
      this.jListMusics.addKeyListener(new KeyListener() {
         @Override
         public void keyTyped(KeyEvent e) {
            e.consume();
         }

         @Override
         public void keyPressed(KeyEvent e) {
            this.event(e);
         }

         @Override
         public void keyReleased(KeyEvent e) {
            e.consume();
         }

         private void event(KeyEvent e) {
            switch (e.getKeyCode()) {
               case 33:
               case 37:
                  BooleanOptional.runIfTrue(MusicSearchPanel.this.keyLeftEvent(), e::consume);
                  break;
               case 34:
               case 39:
                  BooleanOptional.runIfTrue(MusicSearchPanel.this.keyRightEvent(), e::consume);
               case 35:
               case 36:
               case 38:
            }
         }
      });
      chkbxList.forEach(chkbx -> {
         String name = chkbx.getName();
         if (name != null) {
            chkbx.setSelected(Settings.EnumSettings2.MUSIC_SEARCH_PANEL_CHECKED_FIELDS.getString().contains(";" + name + ";"));
            List<JCheckBox> list = MAP_CHECKED_FIELDS.get(name);
            if (list == null) {
               list = new ArrayList<>();
            }

            list.add(chkbx);
            MAP_CHECKED_FIELDS.put(name, list);
            chkbx.addActionListener(evt -> {
               String value = chkbxList.stream().filter(AbstractButton::isSelected).map(ix -> ix.getName()).collect(Collectors.joining(";", ";", ";"));
               Settings.EnumSettings2.MUSIC_SEARCH_PANEL_CHECKED_FIELDS.set(value);
               List<JCheckBox> _list = MAP_CHECKED_FIELDS.get(name);
               if (_list != null) {
                  _list.forEach(_chkbx -> {
                     if (_chkbx != chkbx) {
                        _chkbx.setSelected(chkbx.isSelected());
                     }
                  });
               }
            });
         }
      });
   }

   private void addToolTipButtons() {
      try {
         Map<JButton, HotkeyData> map = new HashMap<>();
         map.put(this.jButtonNewMusic, Hotkey.CTRL_N);
         map.forEach((button, key) -> {
            Optional.ofNullable(key).ifPresent(k -> HotkeyManager.registerHotkey(MusicMain.INSTANCE, button, k, TooltipWay.up));
            TooltipManager.setTooltip(button, button.getToolTipText(), TooltipWay.up, 0);
            button.setToolTipText(null);
         });
      } catch (Exception var2) {
      }
   }

   private void setupWebLaf() {
      WebLafUtils.setupWebLafBorderLayout(this);
      JPanel p = new JPanel(new GridLayout(3, 9));
      Arrays.asList(this.jPanelButtonsLetters.getComponents()).forEach(p::add);
      this.jPanelButtonsLetters.setLayout(new BorderLayout());
      WebCollapsiblePane collapsibleButtonsLetter = new WebCollapsiblePane(ICON_CHECK_16, "", p);
      this.webCollapsibleTitle.setFont(new Font("Georgia", 0, 14));
      this.webCollapsibleTitle.setIcon(ICON_CHECK_16);
      this.webCollapsibleTitle.setHorizontalAlignment(0);
      this.webCollapsibleTitle.setFocusable(false);
      collapsibleButtonsLetter.setTitleComponent(this.webCollapsibleTitle);
      collapsibleButtonsLetter.setExpanded(false);
      collapsibleButtonsLetter.setFocusable(false);
      this.jPanelButtonsLetters.add(collapsibleButtonsLetter, "Center");
      ((WebButton)this.jButtonFilterClear).setDrawShade(false);
      ((WebButton)this.jButtonNewMusic).setDrawShade(false);
      ((WebComboBox)this.jComboBoxFilterProfiles).setShadeWidth(0);
      ((WebComboBox)this.jComboBoxMaxResult).setShadeWidth(0);
   }

   private void initComponents() {
      this.buttonGroupLetters = new ButtonGroup();
      this.jLabelOrderBy = new JLabel();
      this.jLabelMusics = new JLabel();
      this.jTextFieldFilterSearch = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_TO_SEARCH.val() + " (Ctrl+F)");
      this.jPanelSeparator = new JPanel();
      this.jSeparator1 = new JSeparator();
      this.jSeparator2 = new JSeparator();
      this.jPanelMusicsHeader = new JPanel();
      this.jButtonNewMusic = new WebButton();
      this.jPanelFirst = new JPanel();
      this.jPanelCollapsibleContent = new WebPanel();
      this.jPanelMusicFilter = new JPanel();
      this.jPanelSearchOptions = new JPanel();
      this.jCheckBoxFilterTitle = new JCheckBox();
      this.jCheckBoxFilterArtist = new JCheckBox();
      this.jCheckBoxFilterNote = new JCheckBox();
      this.jCheckBoxFilterLyrics = new JCheckBox();
      this.jPanelFilterProfile = new JPanel();
      this.jLabelProfile = new JLabel();
      this.jComboBoxFilterProfiles = new WebComboBox() {
         private boolean layingOut = false;

         public void doLayout() {
            try {
               this.layingOut = true;
               super.doLayout();
            } finally {
               this.layingOut = false;
            }
         }

         public Dimension getSize() {
            Dimension dim = super.getSize();
            if (!this.layingOut) {
               dim.width = Math.max(dim.width, this.getPreferredSize().width);
            }

            return dim;
         }
      };
      this.jPanelFilterOrderBy = new JPanel();
      this.jLabelOrderBy2 = new JLabel();
      this.jComboBoxOrderBy = new WebComboBox() {
         private boolean layingOut = false;

         public void doLayout() {
            try {
               this.layingOut = true;
               super.doLayout();
            } finally {
               this.layingOut = false;
            }
         }

         public Dimension getSize() {
            Dimension dim = super.getSize();
            if (!this.layingOut) {
               dim.width = Math.max(dim.width, this.getPreferredSize().width);
            }

            return dim;
         }
      };
      this.jCheckBoxOrderByInverse = new JCheckBox();
      this.jPanelButtonsLetters = new JPanel();
      this.jPanelFooter = new JPanel();
      this.jButtonFilterClear = new WebButton();
      this.jPanelFooterRight = new JPanel();
      this.jComboBoxMaxResult = new WebComboBox();
      this.jLabelList = new JLabel();
      this.jPanelCenter = new JPanel();
      this.jPanelListMusic = new JPanel();
      this.jScrollPaneMusics = new JScrollPane();
      this.jListMusics = new JList<>();
      this.jLabelOrderBy.setFont(new Font("Georgia", 0, 11));
      this.jLabelOrderBy.setHorizontalAlignment(0);
      this.jLabelOrderBy.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.orderBy")));
      this.jLabelOrderBy.setMinimumSize(new Dimension(0, 0));
      this.jLabelMusics.setFont(new Font("Georgia", 0, 18));
      this.jLabelMusics.setHorizontalAlignment(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelMusics.setText(bundle.getString("word.musics"));
      this.jLabelMusics.setHorizontalTextPosition(2);
      this.jLabelMusics.setMinimumSize(new Dimension(0, 0));
      this.jTextFieldFilterSearch.setFont(new Font("Georgia", 0, 14));
      this.jTextFieldFilterSearch.setHorizontalAlignment(0);
      this.jTextFieldFilterSearch.setMinimumSize(new Dimension(0, 0));
      this.jTextFieldFilterSearch.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            MusicSearchPanel.this.jTextFieldFilterSearchKeyReleased(evt);
         }
      });
      this.jPanelSeparator.setBorder(BorderFactory.createEmptyBorder(7, 1, 4, 1));
      this.jPanelSeparator.setOpaque(false);
      this.jPanelSeparator.setLayout(new GridLayout(2, 1));
      this.jPanelSeparator.add(this.jSeparator1);
      this.jPanelSeparator.add(this.jSeparator2);
      this.jPanelMusicsHeader.setMinimumSize(new Dimension(0, 0));
      this.jPanelMusicsHeader.setOpaque(false);
      this.jPanelMusicsHeader.setLayout(new FlowLayout(1, 5, 2));
      this.jButtonNewMusic.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/plus16.png")));
      this.jButtonNewMusic.setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.create")));
      this.jButtonNewMusic.setBorderPainted(false);
      this.jButtonNewMusic.setContentAreaFilled(false);
      this.jButtonNewMusic.setCursor(new Cursor(12));
      this.jButtonNewMusic.setFocusPainted(false);
      this.jButtonNewMusic.setFocusable(false);
      this.jButtonNewMusic.setMinimumSize(new Dimension(0, 0));
      this.jButtonNewMusic.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicSearchPanel.this.jButtonNewMusicActionPerformed(evt);
         }
      });
      this.jPanelMusicsHeader.add(this.jButtonNewMusic);
      this.setPreferredSize(new Dimension(270, 650));
      this.setLayout(new BorderLayout(0, 4));
      this.jPanelFirst.setOpaque(false);
      this.jPanelFirst.setLayout(new BorderLayout());
      this.jPanelCollapsibleContent.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
      this.jPanelCollapsibleContent.setLayout(new BorderLayout(0, 2));
      this.jPanelMusicFilter.setOpaque(false);
      this.jPanelMusicFilter.setLayout(new GridLayout(3, 1, 0, 4));
      this.jPanelSearchOptions.setOpaque(false);
      this.jPanelSearchOptions.setLayout(new GridLayout(1, 4));
      this.jCheckBoxFilterTitle.setFont(new Font("Georgia", 0, 11));
      this.jCheckBoxFilterTitle.setSelected(true);
      this.jCheckBoxFilterTitle.setText(bundle.getString("word.title"));
      this.jCheckBoxFilterTitle.setFocusable(false);
      this.jCheckBoxFilterTitle.setHorizontalAlignment(0);
      this.jCheckBoxFilterTitle.setIconTextGap(2);
      this.jCheckBoxFilterTitle.setMinimumSize(new Dimension(0, 0));
      this.jCheckBoxFilterTitle.setName("getTitle");
      this.jPanelSearchOptions.add(this.jCheckBoxFilterTitle);
      this.jCheckBoxFilterArtist.setFont(new Font("Georgia", 0, 11));
      this.jCheckBoxFilterArtist.setSelected(true);
      this.jCheckBoxFilterArtist.setText(bundle.getString("word.artist"));
      this.jCheckBoxFilterArtist.setFocusable(false);
      this.jCheckBoxFilterArtist.setHorizontalAlignment(0);
      this.jCheckBoxFilterArtist.setIconTextGap(2);
      this.jCheckBoxFilterArtist.setMinimumSize(new Dimension(0, 0));
      this.jCheckBoxFilterArtist.setName("getArtist");
      this.jPanelSearchOptions.add(this.jCheckBoxFilterArtist);
      this.jCheckBoxFilterNote.setFont(new Font("Georgia", 0, 11));
      this.jCheckBoxFilterNote.setSelected(true);
      this.jCheckBoxFilterNote.setText(bundle.getString("word.note"));
      this.jCheckBoxFilterNote.setFocusable(false);
      this.jCheckBoxFilterNote.setHorizontalAlignment(0);
      this.jCheckBoxFilterNote.setIconTextGap(2);
      this.jCheckBoxFilterNote.setMinimumSize(new Dimension(0, 0));
      this.jCheckBoxFilterNote.setName("getNote");
      this.jPanelSearchOptions.add(this.jCheckBoxFilterNote);
      this.jCheckBoxFilterLyrics.setFont(new Font("Georgia", 0, 11));
      this.jCheckBoxFilterLyrics.setText(bundle.getString("word.lyrics"));
      this.jCheckBoxFilterLyrics.setFocusable(false);
      this.jCheckBoxFilterLyrics.setHorizontalAlignment(0);
      this.jCheckBoxFilterLyrics.setIconTextGap(2);
      this.jCheckBoxFilterLyrics.setMinimumSize(new Dimension(0, 0));
      this.jCheckBoxFilterLyrics.setName("getLyrics");
      this.jPanelSearchOptions.add(this.jCheckBoxFilterLyrics);
      this.jPanelMusicFilter.add(this.jPanelSearchOptions);
      this.jPanelFilterProfile.setOpaque(false);
      this.jPanelFilterProfile.setLayout(new BorderLayout(8, 0));
      this.jLabelProfile.setFont(new Font("Georgia", 0, 18));
      this.jLabelProfile.setHorizontalAlignment(0);
      this.jLabelProfile.setText(bundle.getString("Main.jLabelProfile.text"));
      this.jLabelProfile.setCursor(new Cursor(this.links ? 12 : 0));
      this.jLabelProfile.setMinimumSize(new Dimension(0, 0));
      this.jLabelProfile.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent evt) {
            MusicSearchPanel.this.jLabelProfileMouseEntered(evt);
         }

         @Override
         public void mouseExited(MouseEvent evt) {
            MusicSearchPanel.this.jLabelProfileMouseExited(evt);
         }

         @Override
         public void mousePressed(MouseEvent evt) {
            MusicSearchPanel.this.jLabelProfileMousePressed(evt);
         }
      });
      this.jPanelFilterProfile.add(this.jLabelProfile, "West");
      this.jComboBoxFilterProfiles.setFont(new Font("Georgia", 0, 14));
      this.jComboBoxFilterProfiles.setMaximumRowCount(12);
      this.jComboBoxFilterProfiles.setModel(this.comboBoxModelProfile);
      this.jComboBoxFilterProfiles.setFocusable(false);
      this.jComboBoxFilterProfiles.setMinimumSize(new Dimension(0, 0));
      this.jComboBoxFilterProfiles.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicSearchPanel.this.jComboBoxFilterProfilesfilterComboBoxStateChanged(evt);
         }
      });
      this.jPanelFilterProfile.add(this.jComboBoxFilterProfiles, "Center");
      this.jPanelMusicFilter.add(this.jPanelFilterProfile);
      this.jPanelFilterOrderBy.setMaximumSize(new Dimension(32767, 60));
      this.jPanelFilterOrderBy.setMinimumSize(new Dimension(0, 0));
      this.jPanelFilterOrderBy.setOpaque(false);
      this.jPanelFilterOrderBy.setLayout(new BorderLayout(4, 0));
      this.jLabelOrderBy2.setFont(new Font("Georgia", 0, 12));
      this.jLabelOrderBy2.setText(bundle.getString("msg.orderBy"));
      this.jLabelOrderBy2.setMinimumSize(new Dimension(0, 0));
      this.jPanelFilterOrderBy.add(this.jLabelOrderBy2, "Before");
      this.jComboBoxOrderBy.setFont(new Font("Georgia", 0, 11));
      this.jComboBoxOrderBy.setModel(new DefaultComboBoxModel<>(MusicUtils.EnumOrderBy.values()));
      this.jComboBoxOrderBy.setFocusable(false);
      this.jComboBoxOrderBy.setMinimumSize(new Dimension(0, 0));
      this.jComboBoxOrderBy.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicSearchPanel.this.jComboBoxOrderByItemStateChanged(evt);
         }
      });
      this.jPanelFilterOrderBy.add(this.jComboBoxOrderBy, "Center");
      this.jCheckBoxOrderByInverse.setFont(new Font("Arial", 0, 11));
      this.jCheckBoxOrderByInverse.setText(bundle.getString("word.descending"));
      this.jCheckBoxOrderByInverse.setFocusable(false);
      this.jCheckBoxOrderByInverse.setMinimumSize(new Dimension(0, 0));
      this.jCheckBoxOrderByInverse.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicSearchPanel.this.jCheckBoxOrderByInverseItemStateChanged(evt);
         }
      });
      this.jPanelFilterOrderBy.add(this.jCheckBoxOrderByInverse, "After");
      this.jPanelMusicFilter.add(this.jPanelFilterOrderBy);
      this.jPanelCollapsibleContent.add(this.jPanelMusicFilter, "First");
      this.jPanelButtonsLetters.setOpaque(false);
      this.jPanelButtonsLetters.setLayout(new GridLayout(3, 9));
      this.jPanelCollapsibleContent.add(this.jPanelButtonsLetters, "Center");
      this.jPanelFooter.setMinimumSize(new Dimension(0, 0));
      this.jPanelFooter.setOpaque(false);
      this.jPanelFooter.setLayout(new BorderLayout(8, 0));
      this.jButtonFilterClear.setFont(new Font("Georgia", 0, 10));
      this.jButtonFilterClear.setText(bundle.getString("Main.jButtonFilterClear.text"));
      this.jButtonFilterClear.setFocusable(false);
      this.jButtonFilterClear.setMargin(new Insets(1, 0, 1, 0));
      this.jButtonFilterClear.setMinimumSize(new Dimension(0, 0));
      this.jButtonFilterClear.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicSearchPanel.this.jButtonFilterClearActionPerformed(evt);
         }
      });
      this.jPanelFooter.add(this.jButtonFilterClear, "Center");
      this.jPanelFooterRight.setOpaque(false);
      this.jPanelFooterRight.setLayout(new BorderLayout(2, 0));
      this.jComboBoxMaxResult.setFont(new Font("Geometr212 BkCn BT", 0, 12));
      this.jComboBoxMaxResult.setModel(new DefaultComboBoxModel<>(new String[]{"100", "250", "500", "1000"}));
      this.jComboBoxMaxResult.setSelectedIndex(1);
      this.jComboBoxMaxResult.setMinimumSize(new Dimension(0, 0));
      this.jComboBoxMaxResult.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicSearchPanel.this.jComboBoxMaxResultItemStateChanged(evt);
         }
      });
      this.jPanelFooterRight.add(this.jComboBoxMaxResult, "Center");
      this.jLabelList.setFont(new Font("Georgia", 0, 12));
      this.jLabelList.setHorizontalAlignment(0);
      this.jLabelList.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.list")));
      this.jLabelList.setMinimumSize(new Dimension(0, 0));
      this.jPanelFooterRight.add(this.jLabelList, "West");
      this.jPanelFooter.add(this.jPanelFooterRight, "East");
      this.jPanelCollapsibleContent.add(this.jPanelFooter, "Last");
      this.jPanelFirst.add(this.jPanelCollapsibleContent, "Center");
      this.add(this.jPanelFirst, "First");
      this.jPanelCenter.setOpaque(false);
      this.jPanelCenter.setLayout(new BorderLayout());
      this.jPanelListMusic.setOpaque(false);
      this.jScrollPaneMusics.setFocusable(false);
      this.jScrollPaneMusics.setMinimumSize(new Dimension(0, 0));
      this.jListMusics.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.jListMusics.setFont(new Font("Franklin Gothic Medium", 0, 15));
      this.jListMusics.setModel(this.listModel);
      this.jListMusics.setSelectionMode(0);
      this.jListMusics.setToolTipText(null);
      this.jListMusics.setCursor(new Cursor(0));
      this.jListMusics.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            MusicSearchPanel.this.jListMusicsMouseReleased(evt);
         }
      });
      this.jListMusics.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent evt) {
            MusicSearchPanel.this.jListMusicsValueChanged(evt);
         }
      });
      this.jScrollPaneMusics.setViewportView(this.jListMusics);
      GroupLayout jPanelListMusicLayout = new GroupLayout(this.jPanelListMusic);
      this.jPanelListMusic.setLayout(jPanelListMusicLayout);
      jPanelListMusicLayout.setHorizontalGroup(
         jPanelListMusicLayout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPaneMusics, -1, 270, 32767)
      );
      jPanelListMusicLayout.setVerticalGroup(
         jPanelListMusicLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanelListMusicLayout.createSequentialGroup().addGap(2, 2, 2).addComponent(this.jScrollPaneMusics, -1, 533, 32767).addGap(0, 0, 0))
      );
      this.jPanelCenter.add(this.jPanelListMusic, "Center");
      this.add(this.jPanelCenter, "Center");
   }

   private void jComboBoxFilterProfilesfilterComboBoxStateChanged(ItemEvent evt) {
      this.filter();
   }

   private void jButtonFilterClearActionPerformed(ActionEvent evt) {
      this.clearFilter();
   }

   private void jListMusicsMouseReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt) && this.jListMusics.getSelectedValue() != null) {
         this.doubleClick(this.jListMusics.getSelectedValue());
      }
   }

   private void jListMusicsValueChanged(ListSelectionEvent evt) {
      BooleanOptional.runIfTrue(!evt.getValueIsAdjusting(), () -> this.musicChanged(this.jListMusics.getSelectedValue()));
   }

   private void jLabelProfileMouseEntered(MouseEvent evt) {
      BooleanOptional.runIfTrue(this.links, () -> this.jLabelProfile.setForeground(Color.RED));
   }

   private void jLabelProfileMouseExited(MouseEvent evt) {
      BooleanOptional.runIfTrue(this.links, () -> this.jLabelProfile.setForeground(Color.BLACK));
   }

   private void jLabelProfileMousePressed(MouseEvent evt) {
      BooleanOptional.runIfTrue(this.links, () -> this.openEditProfile());
   }

   private void jButtonNewMusicActionPerformed(ActionEvent evt) {
      this.newMusic();
   }

   private void jComboBoxMaxResultItemStateChanged(ItemEvent evt) {
      BooleanOptional.runIfTrue(evt.getStateChange() == 1, () -> this.updateFilteredList(this.jListMusics.getSelectedValue()));
   }

   private void jTextFieldFilterSearchKeyReleased(KeyEvent evt) {
      switch (evt.getKeyCode()) {
         case 33:
         case 37:
            this.keyLeftEvent();
            break;
         case 34:
         case 39:
            this.keyRightEvent();
         case 35:
         case 36:
         default:
            break;
         case 38:
            if (!this.keyRightEvent()) {
               this.jListMusics.setSelectedIndex(Math.max(0, this.jListMusics.getSelectedIndex() - 1));
            }
            break;
         case 40:
            if (!this.keyLeftEvent()) {
               this.jListMusics.setSelectedIndex(Math.min(this.jListMusics.getModel().getSize() - 1, this.jListMusics.getSelectedIndex() + 1));
            }
      }
   }

   private void jComboBoxOrderByItemStateChanged(ItemEvent evt) {
      BooleanOptional.runIfTrue(evt.getStateChange() == 1, this::filter);
   }

   private void jCheckBoxOrderByInverseItemStateChanged(ItemEvent evt) {
      this.filter();
   }

   public synchronized void filter(Music selected) {
      if (!this.filterBlocked) {
         this.musicFilter.clear();
         this.musicFilter.setSearch(this.jTextFieldFilterSearch.getText());
         this.musicFilter.setProfile((Profile)this.jComboBoxFilterProfiles.getSelectedItem());
         this.musicFilter.setTitle(this.jCheckBoxFilterTitle.isSelected());
         this.musicFilter.setArtist(this.jCheckBoxFilterArtist.isSelected());
         this.musicFilter.setLyrics(this.jCheckBoxFilterLyrics.isSelected());
         this.musicFilter.setNote(this.jCheckBoxFilterNote.isSelected());
         this.musicFilter
            .setStartWith(Arrays.asList(this.buttonsLetter).stream().filter(AbstractButton::isSelected).map(Component::getName).findFirst().orElse(""));
         if (this.jComboBoxOrderBy.getSelectedItem() == MusicUtils.EnumOrderBy.ARTIST) {
            this.musicFilter.toStartWithArtist();
         } else {
            this.musicFilter.toStartWithTitle();
         }

         this.filtered.clear();
         List<Music> founds = MusicUtils.filter(MusicDao.getAllToSearch(), this.musicFilter, false);
         founds.sort(this.getSort());
         int max = Integer.parseInt(this.jComboBoxMaxResult.getItemAt(this.jComboBoxMaxResult.getItemCount() - 1));
         this.filtered.addAll(founds.subList(0, Math.min(founds.size(), max)));
         this.updateFilteredList(selected);
         SwingUtilities.invokeLater(() -> this.jTextFieldFilterSearch.setToolTipText(I18N.WORD_MUSICS.val() + ": " + MusicDao.getSize()));
      }
   }

   public synchronized void filter() {
      this.filter(null);
   }

   public synchronized void updateFilteredList(Music selected) {
      SwingUtilities.invokeLater(() -> {
         int max = Integer.parseInt(this.jComboBoxMaxResult.getSelectedItem().toString());
         this.listModel.removeAllElements(false);
         List<Music> add = new ArrayList<>(this.filtered);
         Optional.ofNullable(this.removeIfExists).ifPresent(r -> add.removeIf(r::contains));
         this.listModel.addElements(add.subList(0, Math.min(add.size(), max)));
         if (selected != null) {
            if (this.listModel.contains(selected)) {
               this.jListMusics.setSelectedValue(selected, true);
            } else {
               this.musicChanged(selected);
            }
         } else if (this.listModel.isEmpty()) {
            this.musicChanged(Music.BLANK);
         } else {
            this.jListMusics.setSelectedIndex(0);
         }
      });
   }

   public void setRemoveIfExists(DefaultListModel<Music> removeIfExists) {
      this.removeIfExists = removeIfExists;
   }

   public void clearFilter() {
      this.filterBlocked = true;
      this.jComboBoxFilterProfiles.setSelectedItem(null);
      this.jTextFieldFilterSearch.setText("");
      this.buttonsLetterAll.setSelected(true);
      this.webCollapsibleTitle.setIcon(ICON_CHECK_16);
      this.webCollapsibleTitle.setText("");
      this.jComboBoxOrderBy.setSelectedItem(MusicUtils.EnumOrderBy.TITLE);
      this.jCheckBoxOrderByInverse.setSelected(false);
      this.filterBlocked = false;
      this.filter(null);
   }

   public void focusOnTextFieldSearch() {
      this.jTextFieldFilterSearch.requestFocus();
   }

   public void update() {
      this.comboBoxModelProfile.update();
      this.filter(null);
   }

   public void update(Music selected) {
      this.comboBoxModelProfile.update();
      this.filter(selected);
   }

   public boolean setSelectedMusic(Music music) {
      if (this.listModel.contains(music)) {
         this.jListMusics.setSelectedValue(music, true);
         return true;
      } else {
         this.jListMusics.setSelectedIndex(-1);
         return false;
      }
   }

   public Music getSelectedMusic() {
      return this.jListMusics.getSelectedValue();
   }

   public Music getPreviousMusic() {
      int index = this.jListMusics.getMaxSelectionIndex();
      index--;
      return index >= 0 ? this.jListMusics.getModel().getElementAt(index) : null;
   }

   public Music getNextMusic() {
      int index = this.jListMusics.getMaxSelectionIndex();
      index++;
      return index > 0 && index < this.jListMusics.getModel().getSize() ? this.jListMusics.getModel().getElementAt(index) : null;
   }

   public List<Music> getSelectedMusics() {
      return this.jListMusics.getSelectedValuesList();
   }

   private WebToggleButton createDefaultJToogleButtonLetter(String text) {
      WebToggleButton _return = new WebToggleButton(text);
      _return.setName(text.toLowerCase());
      _return.setFont(new Font("Tahoma", 0, 12));
      _return.addActionListener(e -> {
         this.filter();
         this.webCollapsibleTitle.setText(text.isEmpty() ? "" : I18N.WORD_LETTER.val() + ": " + text);
         this.webCollapsibleTitle.setIcon(text.isEmpty() ? ICON_CHECK_16 : null);
      });
      _return.setMinimumSize(new Dimension());
      _return.setMargin(new Insets(0, 0, 0, 0));
      _return.setFocusable(false);
      _return.setShadeColor(Values.COLOR_TRANSPARENT);
      return _return;
   }

   public void ctrlF() {
      SwingUtilities.invokeLater(() -> {
         this.jTextFieldFilterSearch.selectAll();
         this.jTextFieldFilterSearch.requestFocus();
      });
   }

   private Comparator<? super Music> getSort() {
      Object obj = this.jComboBoxOrderBy.getSelectedItem();
      return obj instanceof MusicUtils.EnumOrderBy
         ? ((MusicUtils.EnumOrderBy)obj).sort.apply(this.jCheckBoxOrderByInverse.isSelected())
         : MusicUtils.EnumOrderBy.sortTitle();
   }
}
