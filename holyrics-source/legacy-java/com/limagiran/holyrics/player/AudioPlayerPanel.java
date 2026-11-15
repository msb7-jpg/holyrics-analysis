package com.limagiran.holyrics.player;

import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.managers.hotkey.HotkeyData;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.view.MusicMain;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import javafx.scene.media.MediaPlayer;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

public class AudioPlayerPanel extends JPanel {
   public final AudioPlayerSettings set;
   private final ListModelAudioPlayer listModel;
   public final AudioPlayerTitlePanel titlePanel;
   private JButton jButtonAdd;
   private JButton jButtonEqualizer;
   private JButton jButtonRemove;
   private JComboBox<String> jComboBoxPlay;
   private JLabel jLabelPlay;
   private JList<File> jListMusics;
   private JPanel jPanelBottom;
   private JPanel jPanelBottomRight;
   private JPanel jPanelCenter;
   private JPanel jPanelContent;
   private JPanel jPanelHeader;
   private JPanel jPanelTop;
   private JPanel jPanelTopRight;
   private JScrollPane jScrollPaneMusics;
   private JSlider jSliderVolume;
   private JToggleButton jToggleButtonRepeat;
   private JToggleButton jToggleButtonShuffler;
   private JToggleButton jToggleButtonVolume;

   private AudioPlayerPanel(final AudioPlayerSettings set) {
      this.set = set;
      this.listModel = new ListModelAudioPlayer(set);
      this.titlePanel = new AudioPlayerTitlePanel() {
         @Override
         void previous(boolean playing) {
            this.nextPrevious(-1, playing);
         }

         @Override
         void next(boolean playing) {
            this.nextPrevious(1, playing);
         }

         private void nextPrevious(int i, boolean playing) {
            if (set.isShuffle()) {
               this.shuffle(playing);
            } else {
               BiConsumer<MediaPlayer, File> bi = (mpx, fx) -> {
                  set.setMedia(fx);
                  this.mediaChanged(playing, mpx);
                  Optional.ofNullable(AudioPlayerPanel.this.jListMusics).ifPresent(lx -> lx.repaint());
               };
               int index = set.list.indexOf(set.getMedia());
               if (index >= 0) {
                  int newIndex = (set.list.size() + index + i) % set.list.size();
                  File f = set.list.get(newIndex);
                  MediaPlayer mp = AudioPlayerSetUtils.createMP(f);
                  if (mp == null && playing) {
                     List<File> l = new ArrayList<>(set.list);
                     l.remove(f);

                     while (!l.isEmpty() && mp == null) {
                        int _i = Utils.range((l.size() + index++ + i) % l.size(), 0, l.size() - 1);
                        f = l.remove(_i);
                        mp = AudioPlayerSetUtils.createMP(f);
                     }

                     bi.accept(mp, f);
                  } else {
                     bi.accept(mp, f);
                  }
               }
            }
         }

         @Override
         void shuffle(boolean playing) {
            List<File> toSort = new ArrayList<>(set.list);
            List<File> subPlayeds = AudioPlayerPanel.this.titlePanel
               .playeds
               .subList(Math.max(0, AudioPlayerPanel.this.titlePanel.playeds.size() - (toSort.size() - 1)), AudioPlayerPanel.this.titlePanel.playeds.size());
            MediaPlayer mp = null;
            File f = null;

            while (!toSort.isEmpty()) {
               f = toSort.remove(Utils.random(toSort.size()) - 1);
               mp = AudioPlayerSetUtils.createMP(f);
               if (!subPlayeds.contains(f) && mp != null) {
                  break;
               }
            }

            Optional.ofNullable(AudioPlayerPanel.this.jListMusics).ifPresent(l -> l.repaint());
            set.setMedia(f);
            this.mediaChanged(playing, mp);
         }

         @Override
         AudioPlayerSettings getSet() {
            return set;
         }
      };
      this.initComponents();
      this.init();
   }

   private void init() {
      WebCollapsiblePane collapsible = new WebCollapsiblePane();
      collapsible.setExpandIcon(new ImageIcon(Img.createTriangle(24, new Dimension(18, 10), Color.BLACK, true)));
      collapsible.setCollapseIcon(new ImageIcon(Img.createTriangle(24, new Dimension(18, 10), Color.BLACK)));
      collapsible.setStateIconMargin(new Insets(4, 4, 4, 4));
      JPanel p = new JPanel(new BorderLayout());
      p.setOpaque(false);
      p.add(this.titlePanel, "Center");
      JPanel sep = new JPanel(new GridLayout(1, 2));
      sep.setOpaque(false);
      sep.add(new JSeparator(1));
      sep.add(new JSeparator(1));
      p.add(sep, "East");
      collapsible.setTitleComponent(p);
      collapsible.setFocusable(false);
      collapsible.setExpanded(false);
      collapsible.setPaintFocus(false);
      collapsible.setAnimate(false);
      ((WebButton)this.jButtonEqualizer).setShadeColor(Values.COLOR_TRANSPARENT);
      Arrays.asList(this.jToggleButtonVolume, this.jToggleButtonRepeat, this.jToggleButtonShuffler)
         .forEach(b -> ((WebToggleButton)b).setShadeColor(Values.COLOR_TRANSPARENT));
      collapsible.setContent(this.jPanelContent);
      this.add(collapsible, "Center");
      JTextField search = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_TO_SEARCH.val());
      search.getDocument().addDocumentListener(new DocumentListenerAuto(500L, () -> this.listModel.setFilter(search.getText())));
      this.jScrollPaneMusics.setColumnHeaderView(search);
      this.jListMusics
         .addMouseListener(
            new MouseAdapter() {
               @Override
               public void mousePressed(MouseEvent e) {
                  if (SwingUtilities.isRightMouseButton(e)) {
                     JList list = (JList)e.getSource();
                     int i = list.locationToIndex(e.getPoint());
                     if (i != -1 && !Arrays.stream(list.getSelectedIndices()).anyMatch(x -> x == i)) {
                        list.setSelectedIndex(list.locationToIndex(e.getPoint()));
                     }
                  }
               }

               @Override
               public void mouseReleased(MouseEvent e) {
                  if (Utils.isDoubleClick(e)
                     && AudioPlayerPanel.this.jListMusics.getSelectedValue() != null
                     && SwingUtils.pointOnTheSelectedItem(AudioPlayerPanel.this.jListMusics, e.getPoint())) {
                     AudioPlayerPanel.this.set.setMedia(AudioPlayerPanel.this.jListMusics.getSelectedValue());
                     AudioPlayerPanel.this.listModel.fire();
                     MediaPlayer mp = AudioPlayerSetUtils.createMP(AudioPlayerPanel.this.set.getMedia());
                     AudioPlayerPanel.this.titlePanel.mediaChanged(true, mp);
                     BooleanOptional.runIfTrue(mp == null, () -> AudioPlayerPanel.this.titlePanel.showNotificationError(AudioPlayerPanel.this.set.getMedia()));
                  } else if (SwingUtilities.isRightMouseButton(e)
                     && AudioPlayerPanel.this.jListMusics.getSelectedValue() != null
                     && SwingUtils.pointOnTheSelectedItem(AudioPlayerPanel.this.jListMusics, e.getPoint())) {
                     JPopupMenu popup = new JPopupMenu();
                     JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val());
                     remove.addActionListener(ev -> AudioPlayerPanel.this.remove());
                     popup.add(remove);
                     popup.show(AudioPlayerPanel.this.jListMusics, e.getX(), e.getY());
                  }
               }
            }
         );
      this.titlePanel.mediaChanged(false, AudioPlayerSetUtils.createMP(this.set.getMedia()));
      this.jToggleButtonRepeat.setVisible(this.set.isPlayOneFile());
      this.jToggleButtonShuffler.setVisible(!this.set.isPlayOneFile());
      this.jButtonAdd.addActionListener(e -> this.addFiles());
      this.jButtonRemove.addActionListener(e -> this.remove());
      this.jSliderVolume.setValue(this.set.getVolumeInt());
      this.jSliderVolume.addChangeListener(e -> {
         this.jToggleButtonVolume.setSelected(false);
         this.set.setVolumeInt(this.jSliderVolume.getValue());
         this.titlePanel.volumeChanged();
      });
      this.jButtonEqualizer.addActionListener(e -> this.titlePanel.showEqualizer(this.jButtonEqualizer));

      try {
         Map<AbstractButton, HotkeyData> map = new HashMap<>();
         map.put(this.jButtonAdd, null);
         map.put(this.jButtonRemove, null);
         map.put(this.jToggleButtonRepeat, null);
         map.put(this.jToggleButtonShuffler, null);
         map.put(this.jButtonEqualizer, null);
         map.forEach((button, key) -> {
            Optional.ofNullable(key).ifPresent(k -> HotkeyManager.registerHotkey(MusicMain.INSTANCE, button, k, TooltipWay.up));
            TooltipManager.setTooltip(button, button.getToolTipText(), TooltipWay.up, 0);
            button.setToolTipText(null);
         });
      } catch (Exception var6) {
      }
   }

   private void initComponents() {
      this.jPanelContent = new JPanel();
      this.jPanelHeader = new JPanel();
      this.jPanelTop = new JPanel();
      this.jToggleButtonVolume = new WebToggleButton();
      this.jSliderVolume = new JSlider();
      this.jPanelTopRight = new JPanel();
      this.jToggleButtonRepeat = new WebToggleButton();
      this.jToggleButtonShuffler = new WebToggleButton();
      this.jButtonEqualizer = new WebButton();
      this.jPanelBottom = new JPanel();
      this.jLabelPlay = new JLabel();
      this.jComboBoxPlay = new JComboBox<>();
      this.jPanelBottomRight = new JPanel();
      this.jButtonAdd = new JButton();
      this.jButtonRemove = new JButton();
      this.jPanelCenter = new JPanel();
      this.jScrollPaneMusics = new JScrollPane();
      this.jListMusics = new ListDragAndDropAudioPlayer(this.listModel, new ListCellRendererAudioPlayer(this.set));
      this.setFocusable(false);
      this.setLayout(new BorderLayout());
      this.jPanelContent.setBorder(BorderFactory.createEmptyBorder(10, 8, 6, 8));
      this.jPanelContent.setLayout(new BorderLayout(0, 6));
      this.jPanelHeader.setOpaque(false);
      this.jPanelHeader.setLayout(new BorderLayout(0, 4));
      this.jPanelTop.setOpaque(false);
      this.jPanelTop.setLayout(new BorderLayout());
      this.jToggleButtonVolume.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/volumeUp16.png")));
      this.jToggleButtonVolume.setFocusable(false);
      this.jToggleButtonVolume.setMinimumSize(new Dimension(0, 0));
      this.jToggleButtonVolume.setSelectedIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/volumeOff16.png")));
      this.jToggleButtonVolume.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            AudioPlayerPanel.this.jToggleButtonVolumeItemStateChanged(evt);
         }
      });
      this.jPanelTop.add(this.jToggleButtonVolume, "West");
      this.jSliderVolume.setMajorTickSpacing(50);
      this.jSliderVolume.setMinorTickSpacing(10);
      this.jSliderVolume.setPaintTicks(true);
      this.jSliderVolume.setFocusable(false);
      this.jSliderVolume.setMinimumSize(new Dimension(0, 0));
      this.jSliderVolume.setPreferredSize(new Dimension(50, 31));
      this.jPanelTop.add(this.jSliderVolume, "Center");
      this.jPanelTopRight.setOpaque(false);
      this.jPanelTopRight.setLayout(new BorderLayout());
      this.jToggleButtonRepeat.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/repeat16.png")));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jToggleButtonRepeat.setToolTipText(bundle.getString("word.repeat"));
      this.jToggleButtonRepeat.setFocusable(false);
      this.jToggleButtonRepeat.setMinimumSize(new Dimension(0, 0));
      this.jToggleButtonRepeat.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            AudioPlayerPanel.this.jToggleButtonRepeatItemStateChanged(evt);
         }
      });
      this.jPanelTopRight.add(this.jToggleButtonRepeat, "West");
      this.jToggleButtonShuffler.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/shuffle16.png")));
      this.jToggleButtonShuffler.setToolTipText(bundle.getString("word.shuffle"));
      this.jToggleButtonShuffler.setFocusable(false);
      this.jToggleButtonShuffler.setMinimumSize(new Dimension(0, 0));
      this.jToggleButtonShuffler.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            AudioPlayerPanel.this.jToggleButtonShufflerItemStateChanged(evt);
         }
      });
      this.jPanelTopRight.add(this.jToggleButtonShuffler, "Center");
      this.jButtonEqualizer.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/equalizer16.png")));
      this.jButtonEqualizer.setToolTipText(bundle.getString("word.equalizer"));
      this.jButtonEqualizer.setFocusable(false);
      this.jButtonEqualizer.setMinimumSize(new Dimension(0, 0));
      this.jPanelTopRight.add(this.jButtonEqualizer, "East");
      this.jPanelTop.add(this.jPanelTopRight, "East");
      this.jPanelHeader.add(this.jPanelTop, "First");
      this.jPanelBottom.setOpaque(false);
      this.jPanelBottom.setLayout(new BorderLayout());
      this.jLabelPlay.setFont(new Font("Georgia", 0, 12));
      this.jLabelPlay.setText(bundle.getString("AudioPlayerPanel.jLabelPlay.text"));
      this.jLabelPlay.setMinimumSize(new Dimension(0, 0));
      this.jPanelBottom.add(this.jLabelPlay, "West");
      this.jComboBoxPlay.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxPlay.setModel(new DefaultComboBoxModel<>(new String[]{I18N.MSG_CURRENT_MUSIC.val(), I18N.MSG_FULL_LIST.val()}));
      this.jComboBoxPlay.setSelectedIndex(this.set.isPlayOneFile() ? 0 : 1);
      this.jComboBoxPlay.setFocusable(false);
      this.jComboBoxPlay.setMinimumSize(new Dimension(0, 0));
      this.jComboBoxPlay.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            AudioPlayerPanel.this.jComboBoxPlayItemStateChanged(evt);
         }
      });
      this.jPanelBottom.add(this.jComboBoxPlay, "Center");
      this.jPanelBottomRight.setOpaque(false);
      this.jPanelBottomRight.setLayout(new GridLayout());
      this.jButtonAdd.setFont(new Font("Georgia", 0, 12));
      this.jButtonAdd.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/plus16.png")));
      this.jButtonAdd.setToolTipText(bundle.getString("word.add"));
      this.jButtonAdd.setFocusable(false);
      this.jButtonAdd.setMinimumSize(new Dimension(0, 0));
      this.jPanelBottomRight.add(this.jButtonAdd);
      this.jButtonRemove.setFont(new Font("Georgia", 0, 12));
      this.jButtonRemove.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/delete16.png")));
      this.jButtonRemove.setToolTipText(bundle.getString("word.remove"));
      this.jButtonRemove.setFocusable(false);
      this.jButtonRemove.setMinimumSize(new Dimension(0, 0));
      this.jPanelBottomRight.add(this.jButtonRemove);
      this.jPanelBottom.add(this.jPanelBottomRight, "East");
      this.jPanelHeader.add(this.jPanelBottom, "Last");
      this.jPanelContent.add(this.jPanelHeader, "First");
      this.jPanelCenter.setLayout(new BorderLayout());
      this.jScrollPaneMusics.setFocusable(false);
      this.jListMusics.setFont(new Font("Arial", 0, 12));
      this.jListMusics.setModel(this.listModel);
      this.jListMusics.setSelectionMode(1);
      this.jListMusics.setDragEnabled(true);
      this.jListMusics.setDropMode(DropMode.INSERT);
      this.jListMusics.setFocusable(false);
      this.jScrollPaneMusics.setViewportView(this.jListMusics);
      this.jPanelCenter.add(this.jScrollPaneMusics, "Center");
      this.jPanelContent.add(this.jPanelCenter, "Center");
      this.add(this.jPanelContent, "Center");
   }

   private void jComboBoxPlayItemStateChanged(ItemEvent evt) {
      this.set.setPlayOneFile(this.jComboBoxPlay.getSelectedIndex() != 1);
      this.jToggleButtonRepeat.setVisible(this.set.isPlayOneFile());
      this.jToggleButtonShuffler.setVisible(!this.set.isPlayOneFile());
   }

   private void jToggleButtonRepeatItemStateChanged(ItemEvent evt) {
      this.set.setRepeat(this.jToggleButtonRepeat.isSelected());
   }

   private void jToggleButtonShufflerItemStateChanged(ItemEvent evt) {
      this.set.setShuffle(this.jToggleButtonShuffler.isSelected());
   }

   private void jToggleButtonVolumeItemStateChanged(ItemEvent evt) {
      this.set.setMute(evt.getStateChange() == 1);
      this.titlePanel.volumeChanged();
   }

   private void addFiles() {
      List<File> l = Dir.windowsFileMultiple(I18N.MSG_SELECT_FILES.val(), (File)null, "Audio (*.mp3, *.wav)", ".mp3", ".wav");
      l.stream().filter(f -> !this.set.list.contains(f)).forEach(this.set.list::add);
      this.set.save();
      this.listModel.fire();
      this.jListMusics.repaint();
      if (this.set.getMedia() == null && !this.set.list.isEmpty()) {
         this.set.setMedia(this.jListMusics.getSelectedValue());
         this.listModel.fire();
         MediaPlayer mp = AudioPlayerSetUtils.createMP(this.set.getMedia());
         this.titlePanel.mediaChanged(false, mp);
         BooleanOptional.runIfTrue(mp == null, () -> this.titlePanel.showNotificationError(this.set.getMedia()));
      }
   }

   private void remove() {
      this.set.list.removeAll(this.jListMusics.getSelectedValuesList());
      if (!this.set.list.contains(this.set.getMedia())) {
         this.set.setMedia(this.set.list.isEmpty() ? null : this.set.list.get(0));
         this.titlePanel.mediaChanged(false, AudioPlayerSetUtils.createMP(this.set.getMedia()));
      }

      this.set.save();
      this.listModel.fire();
      this.jListMusics.repaint();
   }
}
