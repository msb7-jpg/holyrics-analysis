package com.limagiran.holyrics.view.panel;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.event.MusicSearchDBOffLinePanelListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.DBOffLine;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ListCellRendererMusic;
import com.limagiran.holyrics.swing.ListModelAddRemove;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

public final class MusicSearchDBOffLinePanel extends WebPanel {
   private static final List<String> MATCHES_HC = Arrays.asList("harpa crista[ -n]{0,3}[\\d]{1,3}", "harpa[ -n]{0,3}[\\d]{1,3}", "hc[ -n]{0,3}[\\d]{1,3}");
   private static final List<String> MATCHES_CC = Arrays.asList("cantor cristao[ -n]{0,3}[\\d]{1,3}", "cc[ -n]{0,3}[\\d]{1,3}");
   private final ListModelAddRemove<Music> listModelLyrics = new ListModelAddRemove<>();
   private final List<Music> result = new ArrayList<>();
   private final MusicUtils.MusicFilter musicFilter = new MusicUtils.MusicFilter();
   private final MusicSearchDBOffLinePanelListener listener;
   private final Window parent;
   private JButton jButtonSearch;
   private JCheckBox jCheckBoxFilterArtist;
   private JCheckBox jCheckBoxFilterLyrics;
   private JCheckBox jCheckBoxFilterTitle;
   private JComboBox<String> jComboBoxMaxResult;
   private JLabel jLabelList;
   private JLabel jLabelMusics;
   private JList<Music> jListMusics;
   private JPanel jPanelFilterSearch;
   private JPanel jPanelFooter;
   private JPanel jPanelFooterBottom;
   private JPanel jPanelListMusic;
   private JPanel jPanelMusicFilter;
   private JPanel jPanelMusicsHeader;
   private JPanel jPanelSearchOptions;
   private JPanel jPanelSeparator;
   private JScrollPane jScrollPaneMusics;
   private JSeparator jSeparator1;
   private JSeparator jSeparator2;
   private JTextField jTextFieldFilterSearch;

   public MusicSearchDBOffLinePanel(Window parent, boolean multipleSelection, MusicSearchDBOffLinePanelListener listener) {
      super(true);
      this.parent = parent;
      this.listener = listener;
      this.initComponents();
      this.init();
      BooleanOptional.runIfTrue(multipleSelection, () -> this.jListMusics.setSelectionMode(2));
   }

   private void init() {
      List<JCheckBox> chkbxList = Arrays.asList(this.jCheckBoxFilterTitle, this.jCheckBoxFilterArtist, this.jCheckBoxFilterLyrics);
      chkbxList.forEach(
         chkbx -> {
            String name = chkbx.getName();
            if (name != null) {
               String checked = Settings.EnumSettings2.MUSIC_SEARCH_PANEL_OFFLINE_CHECKED_FIELDS.getString();
               chkbx.setSelected(checked.contains(";" + name + ";"));
               chkbx.addActionListener(
                  evt -> {
                     String value = chkbxList.stream()
                        .filter(AbstractButton::isSelected)
                        .map(Component::getName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(";", ";", ";"));
                     Settings.EnumSettings2.MUSIC_SEARCH_PANEL_OFFLINE_CHECKED_FIELDS.set(value);
                  }
               );
            }
         }
      );
      this.jListMusics.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
               JList list = (JList)e.getSource();
               int i = list.locationToIndex(e.getPoint());
               if (i != -1 && !Arrays.stream(list.getSelectedIndices()).anyMatch(x -> x == i)) {
                  list.setSelectedIndex(list.locationToIndex(e.getPoint()));
               }

               MusicSearchDBOffLinePanel.this.listener.rightButtonClicked(e, MusicSearchDBOffLinePanel.this.jListMusics);
            }
         }
      });
      ((WebButton)this.jButtonSearch).setDrawShade(false);
   }

   private void initComponents() {
      this.jPanelMusicFilter = new JPanel();
      this.jPanelFilterSearch = new JPanel();
      this.jTextFieldFilterSearch = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_TO_SEARCH.val());
      this.jPanelSearchOptions = new JPanel();
      this.jCheckBoxFilterTitle = new JCheckBox();
      this.jCheckBoxFilterArtist = new JCheckBox();
      this.jCheckBoxFilterLyrics = new JCheckBox();
      this.jButtonSearch = new WebButton();
      this.jPanelSeparator = new JPanel();
      this.jSeparator1 = new JSeparator();
      this.jSeparator2 = new JSeparator();
      this.jPanelListMusic = new JPanel();
      this.jPanelMusicsHeader = new JPanel();
      this.jLabelMusics = new JLabel();
      this.jScrollPaneMusics = new JScrollPane();
      this.jListMusics = new JList<>();
      this.jPanelFooter = new JPanel();
      this.jPanelFooterBottom = new JPanel();
      this.jLabelList = new JLabel();
      this.jComboBoxMaxResult = new JComboBox<>();
      this.setPreferredSize(new Dimension(270, 650));
      this.jPanelMusicFilter.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
      this.jPanelMusicFilter.setMaximumSize(new Dimension(243, 32767));
      this.jPanelMusicFilter.setOpaque(false);
      this.jPanelMusicFilter.setLayout(new BorderLayout());
      this.jPanelFilterSearch.setOpaque(false);
      this.jPanelFilterSearch.setLayout(new BorderLayout());
      this.jTextFieldFilterSearch.setFont(new Font("Georgia", 0, 14));
      this.jTextFieldFilterSearch.setHorizontalAlignment(0);
      this.jTextFieldFilterSearch.setMinimumSize(new Dimension(0, 0));
      this.jTextFieldFilterSearch.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            MusicSearchDBOffLinePanel.this.jTextFieldFilterSearchKeyReleased(evt);
         }
      });
      this.jPanelFilterSearch.add(this.jTextFieldFilterSearch, "First");
      this.jPanelSearchOptions.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
      this.jPanelSearchOptions.setOpaque(false);
      this.jPanelSearchOptions.setLayout(new GridLayout(1, 3));
      this.jCheckBoxFilterTitle.setFont(new Font("Georgia", 0, 11));
      this.jCheckBoxFilterTitle.setSelected(true);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jCheckBoxFilterTitle.setText(bundle.getString("word.title"));
      this.jCheckBoxFilterTitle.setFocusable(false);
      this.jCheckBoxFilterTitle.setHorizontalAlignment(0);
      this.jCheckBoxFilterTitle.setIconTextGap(2);
      this.jCheckBoxFilterTitle.setMargin(new Insets(2, 1, 2, 1));
      this.jCheckBoxFilterTitle.setMinimumSize(new Dimension(0, 0));
      this.jCheckBoxFilterTitle.setName("getTitle");
      this.jPanelSearchOptions.add(this.jCheckBoxFilterTitle);
      this.jCheckBoxFilterArtist.setFont(new Font("Georgia", 0, 11));
      this.jCheckBoxFilterArtist.setSelected(true);
      this.jCheckBoxFilterArtist.setText(bundle.getString("word.artist"));
      this.jCheckBoxFilterArtist.setFocusable(false);
      this.jCheckBoxFilterArtist.setHorizontalAlignment(0);
      this.jCheckBoxFilterArtist.setIconTextGap(2);
      this.jCheckBoxFilterArtist.setMargin(new Insets(2, 1, 2, 1));
      this.jCheckBoxFilterArtist.setMinimumSize(new Dimension(0, 0));
      this.jCheckBoxFilterArtist.setName("getArtist");
      this.jPanelSearchOptions.add(this.jCheckBoxFilterArtist);
      this.jCheckBoxFilterLyrics.setFont(new Font("Georgia", 0, 11));
      this.jCheckBoxFilterLyrics.setText(bundle.getString("word.lyrics"));
      this.jCheckBoxFilterLyrics.setFocusable(false);
      this.jCheckBoxFilterLyrics.setHorizontalAlignment(0);
      this.jCheckBoxFilterLyrics.setIconTextGap(2);
      this.jCheckBoxFilterLyrics.setMargin(new Insets(2, 1, 2, 1));
      this.jCheckBoxFilterLyrics.setMinimumSize(new Dimension(0, 0));
      this.jCheckBoxFilterLyrics.setName("getLyrics");
      this.jPanelSearchOptions.add(this.jCheckBoxFilterLyrics);
      this.jPanelFilterSearch.add(this.jPanelSearchOptions, "Center");
      this.jButtonSearch.setFont(new Font("Georgia", 0, 14));
      this.jButtonSearch.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/search16.png")));
      this.jButtonSearch.setText(bundle.getString("word.search"));
      this.jButtonSearch.setFocusable(false);
      this.jButtonSearch.setMargin(new Insets(1, 0, 1, 0));
      this.jButtonSearch.setMinimumSize(new Dimension(0, 0));
      this.jButtonSearch.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicSearchDBOffLinePanel.this.jButtonSearchActionPerformed(evt);
         }
      });
      this.jPanelFilterSearch.add(this.jButtonSearch, "Last");
      this.jPanelMusicFilter.add(this.jPanelFilterSearch, "Center");
      this.jPanelSeparator.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
      this.jPanelSeparator.setOpaque(false);
      this.jPanelSeparator.setLayout(new GridLayout(2, 1));
      this.jPanelSeparator.add(this.jSeparator1);
      this.jPanelSeparator.add(this.jSeparator2);
      this.jPanelMusicFilter.add(this.jPanelSeparator, "Last");
      this.add(this.jPanelMusicFilter, "First");
      this.jPanelListMusic.setOpaque(false);
      this.jPanelListMusic.setLayout(new BorderLayout());
      this.jPanelMusicsHeader.setOpaque(false);
      this.jPanelMusicsHeader.setLayout(new FlowLayout(1, 20, 2));
      this.jLabelMusics.setFont(new Font("Georgia", 0, 18));
      this.jLabelMusics.setHorizontalAlignment(0);
      this.jLabelMusics.setText(bundle.getString("word.musics"));
      this.jLabelMusics.setHorizontalTextPosition(2);
      this.jLabelMusics.setMinimumSize(new Dimension(0, 0));
      this.jPanelMusicsHeader.add(this.jLabelMusics);
      this.jPanelListMusic.add(this.jPanelMusicsHeader, "First");
      this.jScrollPaneMusics.setMinimumSize(new Dimension(0, 0));
      this.jListMusics.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.jListMusics.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jListMusics.setModel(this.listModelLyrics);
      this.jListMusics.setSelectionMode(0);
      this.jListMusics.setToolTipText(null);
      this.jListMusics.setCellRenderer(new ListCellRendererMusic(this.musicFilter));
      this.jListMusics.setCursor(new Cursor(0));
      this.jListMusics.setFocusable(false);
      this.jListMusics.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            MusicSearchDBOffLinePanel.this.jListMusicsMouseReleased(evt);
         }
      });
      this.jListMusics.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent evt) {
            MusicSearchDBOffLinePanel.this.jListMusicsValueChanged(evt);
         }
      });
      this.jScrollPaneMusics.setViewportView(this.jListMusics);
      this.jPanelListMusic.add(this.jScrollPaneMusics, "Center");
      this.jPanelFooter.setOpaque(false);
      this.jPanelFooter.setLayout(new BorderLayout());
      this.jPanelFooterBottom.setOpaque(false);
      this.jPanelFooterBottom.setLayout(new FlowLayout(1, 10, 5));
      this.jLabelList.setFont(new Font("Tahoma", 0, 14));
      this.jLabelList.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.list")));
      this.jPanelFooterBottom.add(this.jLabelList);
      this.jComboBoxMaxResult.setFont(new Font("Tahoma", 0, 14));
      this.jComboBoxMaxResult.setModel(new DefaultComboBoxModel<>(new String[]{"50", "100", "200", "300"}));
      this.jComboBoxMaxResult.setSelectedIndex(1);
      this.jComboBoxMaxResult.setFocusable(false);
      this.jComboBoxMaxResult.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicSearchDBOffLinePanel.this.jComboBoxMaxResultItemStateChanged(evt);
         }
      });
      this.jPanelFooterBottom.add(this.jComboBoxMaxResult);
      this.jPanelFooter.add(this.jPanelFooterBottom, "Center");
      this.jPanelListMusic.add(this.jPanelFooter, "Last");
      this.add(this.jPanelListMusic, "Center");
   }

   private void jButtonSearchActionPerformed(ActionEvent evt) {
      this.search();
   }

   private void jListMusicsMouseReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt)) {
         if (this.jListMusics.getSelectedValue() != null) {
            this.listener.doubleClick(this.clone(this.jListMusics.getSelectedValue()));
         }
      } else {
         this.listener.musicChanged(this.clone(this.jListMusics.getSelectedValue()));
      }
   }

   private void jListMusicsValueChanged(ListSelectionEvent evt) {
      if (!evt.getValueIsAdjusting()) {
         if (this.jListMusics.getSelectedValue() == null) {
            this.jListMusics.setSelectedIndex(evt.getLastIndex());
         }

         this.listener.musicChanged(this.clone(this.jListMusics.getSelectedValue()));
      }
   }

   private void jTextFieldFilterSearchKeyReleased(KeyEvent evt) {
      switch (evt.getKeyCode()) {
         case 10:
            this.search();
            break;
         case 38:
            this.jListMusics.setSelectedIndex(Math.max(0, this.jListMusics.getSelectedIndex() - 1));
            break;
         case 40:
            this.jListMusics.setSelectedIndex(Math.min(this.jListMusics.getModel().getSize() - 1, this.jListMusics.getSelectedIndex() + 1));
      }
   }

   private void jComboBoxMaxResultItemStateChanged(ItemEvent evt) {
      BooleanOptional.runIfTrue(evt.getStateChange() == 1, this::updateFilteredList);
   }

   private void search() {
      if (!this.jTextFieldFilterSearch.getText().replace(" ", "").isEmpty()) {
         this.musicFilter.clear();
         this.musicFilter.setSearch(this.jTextFieldFilterSearch.getText());
         if (MATCHES_HC.stream().anyMatch(this.musicFilter.getSearch()::matches)) {
            String number = this.musicFilter.getSearch().replaceAll("[^\\d]", "");
            this.musicFilter.setSearch(String.format("harpa crista - %03d", Integer.parseInt(number)));
         }

         if (MATCHES_CC.stream().anyMatch(this.musicFilter.getSearch()::matches)) {
            String number = this.musicFilter.getSearch().replaceAll("[^\\d]", "");
            this.musicFilter.setSearch(String.format("cantor cristao - %03d", Integer.parseInt(number)));
         }

         this.musicFilter.setTitle(this.jCheckBoxFilterTitle.isSelected());
         this.musicFilter.setArtist(this.jCheckBoxFilterArtist.isSelected());
         this.musicFilter.setLyrics(this.jCheckBoxFilterLyrics.isSelected());
         final int max = Integer.parseInt(this.jComboBoxMaxResult.getItemAt(this.jComboBoxMaxResult.getItemCount() - 1));
         PopUpWorker.exe(new MySwingWorker<Void, Void>(this.parent, com.limagiran.bible.i18n.I18N.WORD_SEARCHING.val()) {
            protected Void doInBackground() throws Exception {
               List<Music> founds = DBOffLine.search(MusicSearchDBOffLinePanel.this.musicFilter);
               MusicSearchDBOffLinePanel.this.result.clear();
               MusicSearchDBOffLinePanel.this.result.addAll(founds.subList(0, Math.min(founds.size(), max)));
               MusicSearchDBOffLinePanel.this.result.sort(MusicUtils.EnumOrderBy.sortTitle());
               return null;
            }

            @Override
            protected void done() {
               super.done();
               MusicSearchDBOffLinePanel.this.updateFilteredList();
            }
         });
      }
   }

   public synchronized void updateFilteredList() {
      SwingUtilities.invokeLater(() -> {
         int max = Integer.parseInt(this.jComboBoxMaxResult.getSelectedItem().toString());
         this.listModelLyrics.removeAllElements(false);
         this.listModelLyrics.addElements(this.result.subList(0, Math.min(this.result.size(), max)));
         if (this.listModelLyrics.isEmpty()) {
            this.listener.musicChanged(Music.BLANK);
         } else {
            this.jListMusics.setSelectedIndex(0);
         }
      });
   }

   public Music getSelectedMusic() {
      return this.clone(this.jListMusics.getSelectedValue());
   }

   private Music clone(Music m) {
      return Optional.ofNullable(RWObj.clone(m, Music.class)).orElse(m);
   }

   public void setupHighlight(JTextComponent t) {
      MusicUtils.setupHighlight(t, this.musicFilter);
   }
}
