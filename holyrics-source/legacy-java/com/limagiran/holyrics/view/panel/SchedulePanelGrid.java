package com.limagiran.holyrics.view.panel;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.managers.language.data.TooltipWay;
import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.event.SchedulePanelGridListener;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.hitem.ToolTipProviderHItemListView;
import com.limagiran.holyrics.hitem.item.HItemMusic;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.swing.ListCellRendererHItemPlayList;
import com.limagiran.holyrics.swing.ListCellRendererPlayList;
import com.limagiran.holyrics.swing.ListDragAndDropHItem;
import com.limagiran.holyrics.swing.ListDragAndDropMusic;
import com.limagiran.holyrics.swing.ListModelHItemPlayList;
import com.limagiran.holyrics.swing.ListModelPlayListEvent;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.PopUpMenu;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SchedulePanelGrid extends JPanel implements SchedulePanelGridListener {
   private final SchedulePanelGridListener listener;
   private final ListModelPlayListEvent listModelPlayListEvent = new ListModelPlayListEvent() {
      @Override
      public void playlistChanged() {
         SchedulePanelGrid.this.playlistChanged();
      }
   };
   private final ListCellRendererPlayList listCellRendererEventPlayList = new ListCellRendererPlayList();
   private final ListModelHItemPlayList listModelHItemPlayList = new ListModelHItemPlayList() {
      @Override
      public void playlistHItemChanged() {
         SchedulePanelGrid.this.playlistHItemChanged();
      }
   };
   private final ListCellRendererHItemPlayList listCellRendererHItemPlayList = new ListCellRendererHItemPlayList();
   public final SchedulePanelGrid.EnumScheduleType scheduleType;
   private Event event;
   private JButton jButtonListMedia1;
   private JButton jButtonListMedia2;
   private JButton jButtonPlayListShowAll;
   private JButton jButtonPlayListShowSelection;
   public final JList<IHItem> jListEventListMedia = new ListDragAndDropHItem(this.listModelHItemPlayList, this.listCellRendererHItemPlayList);
   public final JList<Music> jListEventPlayList = new ListDragAndDropMusic(this.listModelPlayListEvent, this.listCellRendererEventPlayList);
   private JPanel jPanelEventListMedia;
   private JPanel jPanelEventNotes;
   private JPanel jPanelEventPlayList;
   private JPanel jPanelEventSchedule;
   private JPanel jPanelGeneral;
   private JPanel jPanelListMediaFooter;
   private JPanel jPanelPlayListFooter;
   private JScrollPane jScrollPaneEventListMedia;
   private JScrollPane jScrollPaneEventNotes;
   private JScrollPane jScrollPaneEventPlayList;
   private JScrollPane jScrollPaneEventSchedule;
   private JTabbedPane jTabbedPaneEventsPlayListAndSchedule;
   private JTextArea jTextAreaEventNotes;

   private SchedulePanelGrid(Event event, SchedulePanelGrid.EnumScheduleType scheduleType, SchedulePanelGridListener listener) {
      this.event = event;
      this.scheduleType = scheduleType;
      this.listener = listener;
      this.initComponents();
      this.init();
   }

   public static SchedulePanelGrid createToMusicMain(SchedulePanelGridListener listener) {
      return new SchedulePanelGrid(null, SchedulePanelGrid.EnumScheduleType.MUSIC_MAIN, listener);
   }

   public static SchedulePanelGrid createToEvent(Event e) {
      return new SchedulePanelGrid(e, SchedulePanelGrid.EnumScheduleType.EVENT, null);
   }

   private void init() {
      this.jTextAreaEventNotes.setDocument(SwingUtils.createDocMaxSize(8192));
      this.jTextAreaEventNotes
         .getDocument()
         .addDocumentListener(
            new DocumentListenerAuto(
               500L, () -> Optional.ofNullable(this.listModelPlayListEvent.getSchedule()).ifPresent(s -> s.setNotes(this.jTextAreaEventNotes.getText(), true))
            )
         );
      Arrays.asList(this.jScrollPaneEventSchedule, this.jScrollPaneEventPlayList, this.jScrollPaneEventListMedia).forEach(jscroll -> {
         jscroll.getVerticalScrollBar().setUnitIncrement(16);
         jscroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
         jscroll.getVerticalScrollBar().addAdjustmentListener(e -> jscroll.repaint());
      });
      this.jButtonPlayListShowAll.setEnabled(this.scheduleType.withShowButtons);
      this.jButtonPlayListShowSelection.setEnabled(this.scheduleType.withShowButtons);
      this.jPanelPlayListFooter.setVisible(this.scheduleType.withShowButtons);
      this.setEvent(this.event);
      if (!this.scheduleType.tabbedForm) {
         JTabbedPane tab = this.jTabbedPaneEventsPlayListAndSchedule;
         WebPanel grid = new WebPanel(true, new GridLayout(1, tab.getTabCount()));
         grid.setUndecorated(false).setMargin(4).setPaintFocus(false);

         while (tab.getTabCount() > 0) {
            JLabel title = new JLabel(tab.getTitleAt(0), tab.getIconAt(0), 0);
            Component content = tab.getComponentAt(0);
            JPanel panel = new JPanel(new BorderLayout(0, 2));
            panel.add(title, "First");
            panel.add(content, "Center");
            grid.add(panel);
            panel.setOpaque(false);
         }

         this.jPanelGeneral.removeAll();
         this.jPanelGeneral.add(grid, "Center");
         this.jPanelGeneral.revalidate();
      }

      this.jListEventPlayList.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
               JList list = (JList)e.getSource();
               int i = list.locationToIndex(e.getPoint());
               if (i != -1 && !Arrays.stream(list.getSelectedIndices()).anyMatch(x -> x == i)) {
                  list.setSelectedIndex(list.locationToIndex(e.getPoint()));
               }

               PopUpMenu.schedulePopUpPlayList(SchedulePanelGrid.this, SchedulePanelGrid.this.jListEventPlayList, e);
            }
         }
      });
      this.jListEventListMedia.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
               JList list = (JList)e.getSource();
               int i = list.locationToIndex(e.getPoint());
               if (i != -1 && !Arrays.stream(list.getSelectedIndices()).anyMatch(x -> x == i)) {
                  list.setSelectedIndex(list.locationToIndex(e.getPoint()));
               }

               PopUpMenu.schedulePopUpHItemPlayList(SchedulePanelGrid.this.scheduleType.withShowButtons, SchedulePanelGrid.this.jListEventListMedia, e);
            }
         }
      });
      ((WebButton)this.jButtonPlayListShowAll).setDrawShade(false);
      ((WebButton)this.jButtonPlayListShowSelection).setDrawShade(false);
      ((WebScrollPane)this.jScrollPaneEventPlayList).setShadeWidth(1);
      ((WebScrollPane)this.jScrollPaneEventListMedia).setShadeWidth(1);
      ((WebScrollPane)this.jScrollPaneEventNotes).setShadeWidth(1);
      ((WebScrollPane)this.jScrollPaneEventSchedule).setShadeWidth(1);
      this.jPanelListMediaFooter.setVisible(false);
      ToolTipProviderHItemListView toolTipProvider = new ToolTipProviderHItemListView(null, true) {
         public TooltipWay getDirection(WebList component, Object value, int index, int column, boolean isSelected) {
            return TooltipWay.left;
         }
      };
      ((WebList)this.jListEventListMedia).setToolTipProvider(toolTipProvider);
      setupMediaTabTitle(
         this.jTabbedPaneEventsPlayListAndSchedule, 1, an -> this.containsInPlayListMedia(String.valueOf(an.id)), this.listModelHItemPlayList::addElement
      );
   }

   public static void setupMediaTabTitle(JTabbedPane tabbed, int index, Predicate<? super Announcement> announcementPredicateAccept, Consumer<IHItem> add) {
      try {
         String title = tabbed.getTitleAt(index);
         Icon icon = tabbed.getIconAt(index);
         WebPanel tabTitle = new WebPanel(new BorderLayout(6, 0));
         tabTitle.add(new WebLabel(title, icon), "Center");
         tabTitle.setOpaque(false);
         WebButton moreButton = new WebButton(new ImageIcon(Images.load("plus16.png")), ae -> {
            tabbed.setSelectedIndex(index);
            tabbed.revalidate();
            ((Component)ae.getSource()).revalidate();
            WebPopupMenu popup = new WebPopupMenu();
            HItemUtils.setupPopupMenuAddHItem(popup, (Component)ae.getSource(), announcementPredicateAccept, (name, hItem) -> add.accept(hItem));
            popup.showBelowEnd((Component)ae.getSource());
         });
         moreButton.setUndecorated(true);
         tabTitle.add(moreButton, "East");
         tabbed.setTabComponentAt(index, tabTitle);
      } catch (Exception var8) {
      }
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jTabbedPaneEventsPlayListAndSchedule = new WebTabbedPane();
      this.jPanelEventPlayList = new JPanel();
      this.jScrollPaneEventPlayList = new WebScrollPane(null);
      this.jPanelPlayListFooter = new JPanel();
      this.jButtonPlayListShowAll = new WebButton();
      this.jButtonPlayListShowSelection = new WebButton();
      this.jPanelEventListMedia = new JPanel();
      this.jScrollPaneEventListMedia = new WebScrollPane(null);
      this.jPanelListMediaFooter = new JPanel();
      this.jButtonListMedia1 = new WebButton();
      this.jButtonListMedia2 = new WebButton();
      this.jScrollPaneEventSchedule = new WebScrollPane(null);
      this.jPanelEventSchedule = new EventSchedulePanel();
      this.jPanelEventNotes = new JPanel();
      this.jScrollPaneEventNotes = new WebScrollPane(null);
      this.jTextAreaEventNotes = new JTextArea();
      this.setLayout(new BorderLayout());
      this.jPanelGeneral.setOpaque(false);
      this.jPanelGeneral.setLayout(new BorderLayout());
      this.jTabbedPaneEventsPlayListAndSchedule.setFocusable(false);
      this.jPanelEventPlayList.setLayout(new BorderLayout());
      this.jScrollPaneEventPlayList.setHorizontalScrollBarPolicy(31);
      this.jListEventPlayList.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.jListEventPlayList.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jListEventPlayList.setSelectionMode(1);
      this.jListEventPlayList.setCursor(new Cursor(0));
      this.jListEventPlayList.setDragEnabled(true);
      this.jListEventPlayList.setDropMode(DropMode.INSERT);
      this.jListEventPlayList.setFixedCellWidth(0);
      this.jListEventPlayList.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            SchedulePanelGrid.this.jListEventPlayListjListPlayListMouseReleased(evt);
         }
      });
      this.jListEventPlayList.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            SchedulePanelGrid.this.jListEventPlayListKeyReleased(evt);
         }
      });
      this.jListEventPlayList.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent evt) {
            SchedulePanelGrid.this.jListEventPlayListjListPlayListValueChanged(evt);
         }
      });
      this.jScrollPaneEventPlayList.setViewportView(this.jListEventPlayList);
      this.jPanelEventPlayList.add(this.jScrollPaneEventPlayList, "Center");
      this.jPanelPlayListFooter.setLayout(new GridLayout(2, 0));
      this.jButtonPlayListShowAll.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonPlayListShowAll.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack16.png")));
      this.jButtonPlayListShowAll.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.all")));
      this.jButtonPlayListShowAll.setFocusable(false);
      this.jButtonPlayListShowAll.setHorizontalAlignment(2);
      this.jButtonPlayListShowAll.setMargin(new Insets(1, 30, 1, 1));
      this.jButtonPlayListShowAll.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            SchedulePanelGrid.this.jButtonPlayListShowAllActionPerformed(evt);
         }
      });
      this.jPanelPlayListFooter.add(this.jButtonPlayListShowAll);
      this.jButtonPlayListShowSelection.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonPlayListShowSelection.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack16.png")));
      this.jButtonPlayListShowSelection
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.selected_female_plural")));
      this.jButtonPlayListShowSelection.setFocusable(false);
      this.jButtonPlayListShowSelection.setHorizontalAlignment(2);
      this.jButtonPlayListShowSelection.setMargin(new Insets(1, 30, 1, 1));
      this.jButtonPlayListShowSelection.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            SchedulePanelGrid.this.jButtonPlayListShowSelectionActionPerformed(evt);
         }
      });
      this.jPanelPlayListFooter.add(this.jButtonPlayListShowSelection);
      this.jPanelEventPlayList.add(this.jPanelPlayListFooter, "Last");
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jTabbedPaneEventsPlayListAndSchedule
         .addTab(
            bundle.getString("word.lyrics"), new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/list16.png")), this.jPanelEventPlayList
         );
      this.jPanelEventListMedia.setLayout(new BorderLayout());
      this.jScrollPaneEventListMedia.setHorizontalScrollBarPolicy(31);
      this.jListEventListMedia.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.jListEventListMedia.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jListEventListMedia.setSelectionMode(1);
      this.jListEventListMedia.setCursor(new Cursor(0));
      this.jListEventListMedia.setDragEnabled(true);
      this.jListEventListMedia.setDropMode(DropMode.INSERT);
      this.jListEventListMedia.setFixedCellWidth(0);
      this.jListEventListMedia.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            SchedulePanelGrid.this.jListEventListMediajListPlayListMouseReleased(evt);
         }
      });
      this.jListEventListMedia.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            SchedulePanelGrid.this.jListEventListMediaKeyReleased(evt);
         }
      });
      this.jScrollPaneEventListMedia.setViewportView(this.jListEventListMedia);
      this.jPanelEventListMedia.add(this.jScrollPaneEventListMedia, "Center");
      this.jPanelListMediaFooter.setLayout(new GridLayout(2, 0));
      this.jButtonListMedia1.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonListMedia1.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack16.png")));
      this.jButtonListMedia1.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.all")));
      this.jButtonListMedia1.setFocusable(false);
      this.jButtonListMedia1.setHorizontalAlignment(2);
      this.jButtonListMedia1.setMargin(new Insets(1, 30, 1, 1));
      this.jButtonListMedia1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            SchedulePanelGrid.this.jButtonListMedia1ActionPerformed(evt);
         }
      });
      this.jPanelListMediaFooter.add(this.jButtonListMedia1);
      this.jButtonListMedia2.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonListMedia2.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack16.png")));
      this.jButtonListMedia2
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.selected_female_plural")));
      this.jButtonListMedia2.setFocusable(false);
      this.jButtonListMedia2.setHorizontalAlignment(2);
      this.jButtonListMedia2.setMargin(new Insets(1, 30, 1, 1));
      this.jButtonListMedia2.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            SchedulePanelGrid.this.jButtonListMedia2ActionPerformed(evt);
         }
      });
      this.jPanelListMediaFooter.add(this.jButtonListMedia2);
      this.jPanelEventListMedia.add(this.jPanelListMediaFooter, "Last");
      this.jTabbedPaneEventsPlayListAndSchedule
         .addTab(
            bundle.getString("word.media"),
            new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/videoLibrary16.png")),
            this.jPanelEventListMedia
         );
      this.jScrollPaneEventSchedule.setHorizontalScrollBarPolicy(31);
      this.jPanelEventSchedule.setLayout(new GridBagLayout());
      this.jScrollPaneEventSchedule.setViewportView(this.jPanelEventSchedule);
      this.jTabbedPaneEventsPlayListAndSchedule
         .addTab(
            bundle.getString("word.schedule"),
            new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/schedule16.png")),
            this.jScrollPaneEventSchedule
         );
      this.jTextAreaEventNotes.setFont(new Font("Georgia", 0, 14));
      this.jTextAreaEventNotes.setLineWrap(true);
      this.jTextAreaEventNotes.setWrapStyleWord(true);
      this.jScrollPaneEventNotes.setViewportView(this.jTextAreaEventNotes);
      GroupLayout jPanelEventNotesLayout = new GroupLayout(this.jPanelEventNotes);
      this.jPanelEventNotes.setLayout(jPanelEventNotesLayout);
      jPanelEventNotesLayout.setHorizontalGroup(jPanelEventNotesLayout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPaneEventNotes));
      jPanelEventNotesLayout.setVerticalGroup(
         jPanelEventNotesLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanelEventNotesLayout.createSequentialGroup().addComponent(this.jScrollPaneEventNotes).addGap(0, 0, 0))
      );
      this.jTabbedPaneEventsPlayListAndSchedule
         .addTab(
            bundle.getString("word.notes"), new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/eventNote16.png")), this.jPanelEventNotes
         );
      this.jPanelGeneral.add(this.jTabbedPaneEventsPlayListAndSchedule, "Center");
      this.jTabbedPaneEventsPlayListAndSchedule.getAccessibleContext().setAccessibleName(bundle.getString("word.media"));
      this.add(this.jPanelGeneral, "Center");
   }

   private void jListEventPlayListjListPlayListMouseReleased(MouseEvent evt) {
      if (this.scheduleType.withShowButtons
         && !this.jListEventPlayList.getSelectedValuesList().isEmpty()
         && Utils.isDoubleClick(evt)
         && SwingUtils.pointOnTheSelectedItem(this.jListEventPlayList, evt.getPoint())) {
         this.show(this.jListEventPlayList.getSelectedValuesList());
      } else if (this.jListEventPlayList.getSelectedValue() != null
         && this.jListEventPlayList.locationToIndex(evt.getPoint()) == this.jListEventPlayList.getSelectedIndex()) {
         this.musicChanged(this.jListEventPlayList.getSelectedValue());
      }
   }

   private void jListEventPlayListKeyReleased(KeyEvent evt) {
      if (!evt.isShiftDown() && evt.getKeyCode() == 127) {
         this.removeSelectedMusicsFromPlayList(this.jListEventPlayList);
      }
   }

   private void jListEventPlayListjListPlayListValueChanged(ListSelectionEvent evt) {
      if (!evt.getValueIsAdjusting()) {
         this.musicChanged(this.jListEventPlayList.getSelectedValue());
      }
   }

   private void jButtonPlayListShowAllActionPerformed(ActionEvent evt) {
      this.show(Collections.list(this.listModelPlayListEvent.elements()));
   }

   private void jButtonPlayListShowSelectionActionPerformed(ActionEvent evt) {
      this.show(this.jListEventPlayList.getSelectedValuesList());
   }

   private void jListEventListMediajListPlayListMouseReleased(MouseEvent evt) {
      IHItem hitem = this.jListEventListMedia.getSelectedValue();
      if (hitem != null && this.scheduleType.withShowButtons) {
         if (Utils.isDoubleClick(evt) && SwingUtils.pointOnTheSelectedItem(this.jListEventListMedia, evt.getPoint())) {
            hitem.favoriteAction(true);
         } else if (SwingUtilities.isLeftMouseButton(evt) && hitem instanceof HItemMusic) {
            Music m = ((HItemMusic)hitem).getMusic();
            if (m != null) {
               this.musicChanged(m);
            }
         }
      }
   }

   private void jListEventListMediaKeyReleased(KeyEvent evt) {
      if (!evt.isShiftDown() && evt.getKeyCode() == 127) {
         this.removeSelectedMediasFromPlayList(this.jListEventListMedia);
      }
   }

   private void jButtonListMedia1ActionPerformed(ActionEvent evt) {
   }

   private void jButtonListMedia2ActionPerformed(ActionEvent evt) {
   }

   public void setEvent(Event event) {
      this.event = event;
      this.listModelPlayListEvent.setEvent(event);
      this.listModelHItemPlayList.setEvent(event);
      this.listCellRendererEventPlayList.setDate(event == null ? null : event.getDate().getTime());
      ((EventSchedulePanel)this.jPanelEventSchedule).setEvent(event);
      this.jTextAreaEventNotes.setText(event == null ? "" : ScheduleController.get(event).getNotes());
      this.jTextAreaEventNotes.setCaretPosition(0);
      this.jTextAreaEventNotes.setEnabled(event != null);
   }

   public Event getEvent() {
      return this.event;
   }

   @Override
   public void show(List<Music> musics) {
      Optional.ofNullable(this.listener).ifPresent(l -> l.show(musics));
   }

   public void removeSelectedMusicsFromPlayList(JList<Music> jlist) {
      SwingUtils.removeSelectedIndices(jlist, false);
      this.musicChanged(this.jListEventPlayList.getSelectedValue());
   }

   public void removeSelectedMediasFromPlayList(JList<IHItem> jlist) {
      SwingUtils.removeSelectedIndices(jlist, false);
   }

   @Override
   public void musicChanged(Music music) {
      Optional.ofNullable(this.listener).ifPresent(l -> l.musicChanged(music));
   }

   public void addPlayList(Music music) {
      this.listModelPlayListEvent.addElement(music);
   }

   public void removePlayList(Music music) {
      this.listModelPlayListEvent.removeElement(music);
   }

   public boolean containsInPlayList(Music m) {
      return this.listModelPlayListEvent.contains(m);
   }

   public List<Music> getAllLyrics() {
      return Collections.list(this.listModelPlayListEvent.elements());
   }

   public void addPlayList(IHItem hItem) {
      this.listModelHItemPlayList.addElement(hItem);
   }

   public void removePlayList(IHItem hItem) {
      this.listModelHItemPlayList.removeElement(hItem);
   }

   public List<IHItem> getPlayListHItem() {
      return Collections.list(this.listModelHItemPlayList.elements());
   }

   public boolean containsInPlayListMedia(String hItemID) {
      return hItemID != null && Collections.list(this.listModelHItemPlayList.elements()).stream().anyMatch(i -> i.getId().equals(hItemID));
   }

   public boolean containsInPlayList(IHItem hItem) {
      return hItem != null && this.containsInPlayListMedia(hItem.getId());
   }

   @Override
   public boolean playedMusic(Music music) {
      Optional.ofNullable(this.listener).ifPresent(l -> l.playedMusic(music));
      return true;
   }

   @Override
   public void playlistChanged() {
      Optional.ofNullable(this.listener).ifPresent(l -> l.playlistChanged());
   }

   @Override
   public void playlistHItemChanged() {
      Optional.ofNullable(this.listener).ifPresent(l -> l.playlistHItemChanged());
   }

   public void changeSelectedTab(boolean lyrics) {
      try {
         this.jTabbedPaneEventsPlayListAndSchedule.setSelectedComponent(lyrics ? this.jPanelEventPlayList : this.jPanelEventListMedia);
      } catch (Exception var3) {
      }
   }

   public static enum EnumScheduleType {
      MUSIC_MAIN(true, true),
      EVENT(false, false);

      public final boolean withShowButtons;
      public final boolean tabbedForm;

      private EnumScheduleType(boolean withShowButtons, boolean tabbedForm) {
         this.withShowButtons = withShowButtons;
         this.tabbedForm = tabbedForm;
      }
   }
}
