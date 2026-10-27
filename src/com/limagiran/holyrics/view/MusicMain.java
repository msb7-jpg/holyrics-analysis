package com.limagiran.holyrics.view;

import com.alee.extended.button.WebSwitch;
import com.alee.extended.label.WebVerticalLabel;
import com.alee.extended.panel.CollapsiblePaneAdapter;
import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.extended.panel.WebCollapsiblePaneStyle;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.panel.WebPanelStyle;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.tabbedpane.TabStretchType;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyData;
import com.alee.managers.language.data.TooltipWay;
import com.limagiran.bible.download.Updates;
import com.limagiran.holyrics.Dev;
import com.limagiran.holyrics.Holyrics;
import com.limagiran.holyrics.Update;
import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.edit.EditDialog;
import com.limagiran.holyrics.event.SchedulePanelGridListener;
import com.limagiran.holyrics.favorite.FavoritePanel;
import com.limagiran.holyrics.favorite.FavoriteUtils;
import com.limagiran.holyrics.help.EnumHelpPages;
import com.limagiran.holyrics.help.HelpView;
import com.limagiran.holyrics.hitem.EnumTabType;
import com.limagiran.holyrics.hitem.HItemTab;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.hitem.ListModelHItem;
import com.limagiran.holyrics.hitem.ToolTipProviderHItemListView;
import com.limagiran.holyrics.hitem.item.HItemAnnouncement;
import com.limagiran.holyrics.hitem.item.HItemMusic;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.model.History;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.ITheme;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.playback.PlayBackTab;
import com.limagiran.holyrics.playback.PlayBackUtils;
import com.limagiran.holyrics.report.view.ReportPlayedMusicsView;
import com.limagiran.holyrics.report.view.ReportScheduleView;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.settings.SettingsView;
import com.limagiran.holyrics.share.ReceiveUpdateFromInternetDialog;
import com.limagiran.holyrics.share.ReceiveUpdateFromMobileView;
import com.limagiran.holyrics.swing.ComboBoxModelEvent;
import com.limagiran.holyrics.swing.ComboBoxModelTheme;
import com.limagiran.holyrics.swing.ComboBoxRendererEvent;
import com.limagiran.holyrics.swing.ComboBoxRendererPlayListType;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.swing.ListCellRendererHItemPlayList;
import com.limagiran.holyrics.swing.ListCellRendererHistory;
import com.limagiran.holyrics.swing.ListCellRendererNextSchedules;
import com.limagiran.holyrics.swing.ListCellRendererOrder;
import com.limagiran.holyrics.swing.ListCellRendererPlayList;
import com.limagiran.holyrics.swing.ListCellRendererTheme;
import com.limagiran.holyrics.swing.ListDragAndDropHItem;
import com.limagiran.holyrics.swing.ListDragAndDropMusic;
import com.limagiran.holyrics.swing.ListModelHistory;
import com.limagiran.holyrics.swing.ListModelNextSchedules;
import com.limagiran.holyrics.swing.ListModelOrder;
import com.limagiran.holyrics.swing.ListModelTheme;
import com.limagiran.holyrics.util.Backup;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.EnumPlayList;
import com.limagiran.holyrics.util.Freeze;
import com.limagiran.holyrics.util.GeneratePresentation;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.ImportMusic;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.Log;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.PopUpAlert;
import com.limagiran.holyrics.util.PopUpMenu;
import com.limagiran.holyrics.util.PopUpOpenProgram;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.SystemUtils;
import com.limagiran.holyrics.util.UICreate;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.util.WebServiceUtils;
import com.limagiran.holyrics.util.WrapLayout;
import com.limagiran.holyrics.view.panel.AlertPanel;
import com.limagiran.holyrics.view.panel.MusicSearchPanel;
import com.limagiran.holyrics.view.panel.PresentationControllerViewFooter;
import com.limagiran.holyrics.view.panel.SchedulePanelGrid;
import com.limagiran.holyrics.vlc.VLC;
import com.limagiran.holyrics.vlc.VLCDownloadWorker;
import com.limagiran.holyrics.worker.BatchImportWorker;
import com.limagiran.holyrics.worker.CheckUploadAndExitWorker;
import com.limagiran.holyrics.worker.MusicImportWorker;
import com.limagiran.lgsaveopen.LGSaveOpen;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MusicMain
   extends JFrame
   implements SchedulePanelGridListener,
   MusicWebSearch.MusicWebSearchListener,
   HItemTab.HItemTabListener,
   PlayBackTab.PlayBackTabListener,
   Values {
   public static MusicMain INSTANCE;
   private final DefaultListModel<Music> listModelPlayList = new DefaultListModel<>();
   private final DefaultListModel<IHItem> listModelHItemPlayList = new DefaultListModel<>();
   private final SchedulePanelGrid schedulePane = SchedulePanelGrid.createToMusicMain(this);
   private final ListCellRendererPlayList listCellRendererPlayList = new ListCellRendererPlayList();
   private final ListCellRendererHItemPlayList listCellRendererHItemPlayList = new ListCellRendererHItemPlayList();
   private final ListModelOrder listModelOrder = new ListModelOrder(Settings.SET.getMusic());
   private final ListModelHistory listModelHistory = new ListModelHistory(Settings.SET.getMusic());
   private final ListModelNextSchedules listModelNextSchedules = new ListModelNextSchedules(Settings.SET.getMusic());
   private final ListModelTheme listModelTheme = new ListModelTheme();
   private final ComboBoxModelEvent comboBoxModelEvent = new ComboBoxModelEvent();
   private final MusicSearchPanel musicSearchPanel;
   private PresentationSlideView slidePreview;
   private PresentationControllerViewFooter footerControl;
   private final JMenu jMenuAnnouncementShortcut = new JMenu(I18N.WORD_SHOW.val());
   private final WebMenu jMenuAnnouncementAddPlaylist = new WebMenu(I18N.MSG_ADD_PLAY_LIST.val(), new ImageIcon(Images.ICON_LIST_16));
   private final WebMenu jMenuAnnouncementAddFavorite = new WebMenu(I18N.MSG_ADD_TO_FAVORITE.val(), HItemUtils.IC_ADD_FAVORITE);
   private final WebTabbedPane tabbedMedia = new WebTabbedPane(1);
   private final FavoritePanel favoritePanel = new FavoritePanel();
   private WebCollapsiblePane webCollapsiblePaneVLC;
   private final ComboBoxModelTheme comboBoxModelDefaultThemeToMusic = new ComboBoxModelTheme();
   private boolean preventChangeDefaultThemeToMusic;
   private final WebSwitch switchSaveThemeToMusic = UICreate.newWebSwitch();
   private final JLabel jLabelHistoryTitle = new JLabel(I18N.WORD_HISTORY.val(), 0);
   private Filler filler1;
   private JButton jButtonAddHistory;
   private JButton jButtonAddPlayList;
   private JButton jButtonDeleteMusic;
   private JButton jButtonDisplaySettings;
   private JButton jButtonEditMusic;
   private JButton jButtonGoToBible;
   private JButton jButtonNewTheme;
   private JButton jButtonPlayListShowAll;
   private JButton jButtonPlayListShowSelection;
   private JButton jButtonQuickPresentation;
   private JButton jButtonReorder;
   private JButton jButtonShowBeginning;
   private JButton jButtonShowCurrentSlide;
   private JCheckBoxMenuItem jCheckBoxCheckChangesOnExit;
   private JCheckBoxMenuItem jCheckBoxMenuItemFreezeScreen;
   private JCheckBoxMenuItem jCheckBoxMenuItemUseJavaFXVideo;
   private JCheckBoxMenuItem jCheckBoxMenuItemWindowsFavorites;
   private JComboBox<Event> jComboBoxEvent;
   private JComboBox<EnumPlayList> jComboBoxPlayListType;
   private JComboBox<PresentationSlideView.EnumScreenType> jComboBoxScreenType;
   private JComboBox<Theme> jComboBoxThemeDefault;
   private JLabel jLabelMusicNote;
   private JLabel jLabelMusicTitleAndArtist;
   private JLabel jLabelSlidePreview;
   private JLabel jLabelSlidePreviewIsAnimated;
   private JLabel jLabelTheme;
   private JList<Date> jListHistory;
   private JList<IHItem> jListMedia;
   private JList<Calendar> jListNextSchedule;
   private JList<MusicController.Paragraph> jListOrder;
   private JList<Music> jListPlayList;
   private JList<Theme> jListThemes;
   private JMenu jMenuAnnouncement;
   private JMenu jMenuBackup;
   private JMenuBar jMenuBarMenu;
   private JMenu jMenuEdit;
   private JMenu jMenuExportToPDF;
   private JMenu jMenuFile;
   private JMenuItem jMenuFileSaveMusic;
   private JMenuItem jMenuFileThemeExport;
   private JMenuItem jMenuFileThemeImport;
   private JMenu jMenuHelp;
   private JMenu jMenuHelpCompatibility;
   private JMenuItem jMenuItem1;
   private JMenuItem jMenuItemAbout;
   private JMenuItem jMenuItemAnnouncementEdit;
   private JMenuItem jMenuItemAnnouncementShow;
   private JMenuItem jMenuItemChangeWebServiceIP;
   private JMenuItem jMenuItemCheckUpdates;
   private JMenuItem jMenuItemCountdown;
   private JMenuItem jMenuItemCreateDesktopShortcur;
   private JMenuItem jMenuItemCreateImage;
   private JMenuItem jMenuItemDatabaseSearch;
   private JMenuItem jMenuItemDownloadVLCMediaPlayer;
   private JMenuItem jMenuItemEditEvent;
   private JMenuItem jMenuItemEditMember;
   private JMenuItem jMenuItemEditProfile;
   private JMenuItem jMenuItemEditWorship;
   private JMenuItem jMenuItemExit;
   private JMenuItem jMenuItemExport;
   private JMenuItem jMenuItemFileDisplaySettings;
   private JMenuItem jMenuItemFileFilesReceived;
   private JMenuItem jMenuItemFileSettings;
   private JMenuItem jMenuItemGoToBible;
   private JMenuItem jMenuItemHelp;
   private JMenuItem jMenuItemImageFileShow;
   private JMenuItem jMenuItemImport;
   private JMenuItem jMenuItemLyricsDataBase;
   private JMenuItem jMenuItemMusicBatchDelete;
   private JMenuItem jMenuItemMusicBathExport;
   private JMenuItem jMenuItemMusicBathImportFromCCLI_USR;
   private JMenuItem jMenuItemMusicBathImportFromDBO;
   private JMenuItem jMenuItemMusicBathImportFromEasiSlides;
   private JMenuItem jMenuItemMusicBathImportFromOpenLP;
   private JMenuItem jMenuItemMusicBathImportFromOpenSong;
   private JMenuItem jMenuItemMusicBathImportFromWord;
   private JMenuItem jMenuItemMusicImportFileBatch;
   private JMenuItem jMenuItemMusicImportFileOne;
   private JMenuItem jMenuItemMusicImportPPManyInOne;
   private JMenuItem jMenuItemMusicImportPPOneInOne;
   private JMenuItem jMenuItemMusicSavePresentationAs;
   private JMenuItem jMenuItemNewEvent;
   private JMenuItem jMenuItemNewMember;
   private JMenuItem jMenuItemNewMusic;
   private JMenuItem jMenuItemNewProfile;
   private JMenuItem jMenuItemNewTheme;
   private JMenuItem jMenuItemNewWorship;
   private JMenuItem jMenuItemPluginHolyrics;
   private JMenuItem jMenuItemReceiveUpdateFromInternet;
   private JMenuItem jMenuItemReceiveUpdateFromMobile;
   private JMenuItem jMenuItemReportPlayedMusic;
   private JMenuItem jMenuItemReportSchedule;
   private JMenuItem jMenuItemScheduleViewEdit;
   private JMenuItem jMenuItemShareInTheWeb;
   private JMenuItem jMenuItemSupportAndFeedBack;
   private JMenuItem jMenuItemToolsAutomaticShow;
   private JMenuItem jMenuItemTutorialYouTube;
   private JMenuItem jMenuItemWebSearch;
   private JMenuItem jMenuItemWindowsCommunicationPanel;
   private JMenuItem jMenuItemWindowsLive;
   private JMenu jMenuLanguage;
   private JMenu jMenuMusic;
   private JMenu jMenuMusicBatchImport;
   private JMenu jMenuMusicExport;
   private JMenu jMenuMusicImportFile;
   private JMenu jMenuMusicImportOthers;
   private JMenu jMenuMusicImportPP;
   private JMenu jMenuNew;
   private JMenu jMenuReport;
   private JMenu jMenuSchedule;
   private JMenu jMenuShare;
   private JMenu jMenuTheme;
   private JMenu jMenuTools;
   private JMenu jMenuWindows;
   private JPanel jPanelAddThemeToMusic;
   private JPanel jPanelDisplaySettingsButtons;
   private JPanel jPanelEvent;
   private JPanel jPanelEventSettings;
   private JPanel jPanelEventSettingsDate;
   private JPanel jPanelFooter;
   private JPanel jPanelGeneral;
   private JPanel jPanelLeft;
   private JPanel jPanelMusic;
   private JPanel jPanelMusicHistory;
   private JPanel jPanelMusicInfo;
   private JPanel jPanelMusicLyrics;
   private JPanel jPanelMusicMiddle;
   private JPanel jPanelMusicToolBar;
   private JPanel jPanelMusicTop;
   private JPanel jPanelMusicTopCenter;
   private JPanel jPanelOrder;
   private JPanel jPanelPlayList;
   private JPanel jPanelPlayListCard;
   private JPanel jPanelPlayListMedia;
   private JPanel jPanelPlayListMusic;
   private JPanel jPanelPlayListTemp;
   private JPanel jPanelPlayListTempFooter;
   private JPanel jPanelRight;
   private JPanel jPanelRightFooter;
   private JPanel jPanelScreenInfo;
   private JPanel jPanelScreenInfoEast;
   private JPanel jPanelSeparator;
   private JPanel jPanelSlideView;
   private JPanel jPanelSplitLeft;
   private JPanel jPanelSplitLeftHeader;
   private JPanel jPanelSplitLeftHeaderCenter;
   private JPanel jPanelSplitLeftHeaderHeader;
   private JPanel jPanelSplitRight;
   private JPanel jPanelTheme;
   private JPanel jPanelThemeDefault;
   private JPanel jPanelThemeHeader;
   private JScrollPane jScrollPaneHistory;
   private JScrollPane jScrollPaneLyrics;
   private JScrollPane jScrollPaneMedia;
   private JScrollPane jScrollPaneNextSchedule;
   private JScrollPane jScrollPaneOrder;
   private JScrollPane jScrollPanePlayList;
   private JScrollPane jScrollPaneThemes;
   private Separator jSeparator1;
   private Separator jSeparator10;
   private Separator jSeparator11;
   private JSeparator jSeparator12;
   private javax.swing.JToolBar.Separator jSeparator13;
   private javax.swing.JToolBar.Separator jSeparator14;
   private javax.swing.JToolBar.Separator jSeparator15;
   private javax.swing.JToolBar.Separator jSeparator16;
   private javax.swing.JToolBar.Separator jSeparator17;
   private javax.swing.JToolBar.Separator jSeparator18;
   private javax.swing.JToolBar.Separator jSeparator19;
   private Separator jSeparator2;
   private JSeparator jSeparator3;
   private Separator jSeparator4;
   private JSeparator jSeparator5;
   private Separator jSeparator6;
   private Separator jSeparator7;
   private JSeparator jSeparator8;
   private Separator jSeparator9;
   private JSpinner jSpinnerEventDate;
   private JSplitPane jSplitPaneMainAndRightColumn;
   private JSplitPane jSplitPaneMusicAndSlide;
   private JSplitPane jSplitPaneMusicsAndMain;
   private JTabbedPane jTabbedPanePlayListTemp;
   private JTextPane jTextPaneLyrics;
   private JToggleButton jToggleButtonBreakLine;
   private JToggleButton jToggleButtonCustomFormatting;
   private JToggleButton jToggleButtonTitleUnderline;
   private JToggleButton jToggleButtonUppercase;
   private JToolBar jToolBarMusic;
   private MusicWebSearch musicWebSearch;

   private MusicMain() {
      this.initComponents();
      this.musicSearchPanel = this.createMusicSearchPanel();
      this.init();
   }

   private void init() {
      this.getContentPane().add(this.favoritePanel, "First");
      this.jCheckBoxMenuItemWindowsFavorites.setSelected(this.favoritePanel.isVisible());
      this.favoritePanel.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            MusicMain.this.jCheckBoxMenuItemWindowsFavorites.setSelected(true);
         }

         @Override
         public void componentHidden(ComponentEvent e) {
            MusicMain.this.jCheckBoxMenuItemWindowsFavorites.setSelected(false);
         }
      });
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            MusicMain.this.jListThemes.ensureIndexIsVisible(MusicMain.this.jListThemes.getSelectedIndex());
         }
      });
      this.jMenuAnnouncement.add((JMenuItem)this.jMenuAnnouncementShortcut);
      this.jMenuAnnouncement.add(this.jMenuAnnouncementAddPlaylist);
      this.jMenuAnnouncement.add(this.jMenuAnnouncementAddFavorite);
      this.listModelNextSchedules.setPanel(this.jScrollPaneNextSchedule);
      ((WebList)this.jListNextSchedule).setDecorateSelection(false);
      this.jPanelEvent.add(this.schedulePane, "Center");
      this.jListPlayList
         .addListSelectionListener(e -> BooleanOptional.runIfTrue(!e.getValueIsAdjusting(), () -> this.musicChanged(this.jListPlayList.getSelectedValue())));
      this.setupMedia();
      Arrays.asList(this.jScrollPanePlayList, this.jScrollPaneThemes, this.jScrollPaneHistory, this.jScrollPaneNextSchedule)
         .forEach(scroll -> scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)));
      Arrays.asList(this.jScrollPaneHistory, this.jScrollPaneNextSchedule).forEach(s -> s.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0)));
      this.jScrollPaneThemes.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));
      this.addMouseListenerJList();
      this.jListThemes.setSelectedValue(Settings.SET.getTheme(), true);
      this.musicSearchPanel.focusOnTextFieldSearch();
      this.changeDateListEvent(new Date());
      this.selectNearestEvent();
      SwingUtils.addMnemonics(this.jMenuBarMenu);
      this.insertShortcuts();
      this.setupWebLaf();
      this.addToolTipButtons();
      Arrays.asList(
            this.schedulePane.jListEventPlayList,
            this.schedulePane.jListEventListMedia,
            this.jListHistory,
            this.jListOrder,
            this.jListPlayList,
            this.jListMedia,
            this.jListThemes
         )
         .forEach(this::setupJListKeyListenerPresentation);
      SwingUtils.setJTextPaneFont(this.jTextPaneLyrics, this.jTextPaneLyrics.getFont());
      SwingUtils.setJTextPaneLineSpacing(this.jTextPaneLyrics, 0.5F);
      ToolTipProviderHItemListView toolTipProvider = new ToolTipProviderHItemListView(null, true) {
         public TooltipWay getDirection(WebList component, Object value, int index, int column, boolean isSelected) {
            return TooltipWay.left;
         }
      };
      ((WebList)this.jListMedia).setToolTipProvider(toolTipProvider);
      this.musicSearchPanel.focusOnTextFieldSearch();
      this.listCellRendererPlayList.setDate(new Date());
      this.jScrollPanePlayList.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            if (MusicMain.this.jScrollPanePlayList.getHeight() > 420) {
               MusicMain.this.jListThemes.setVisibleRowCount(2);
               this.update();
            } else {
               if (MusicMain.this.jScrollPanePlayList.getHeight() < 300) {
                  MusicMain.this.jListThemes.setVisibleRowCount(1);
                  this.update();
               }
            }
         }

         private void update() {
            MusicMain.this.jPanelTheme.revalidate();
            MusicMain.this.jPanelTheme.repaint();
            MusicMain.this.jListThemes.revalidate();
            MusicMain.this.jListThemes.repaint();
         }
      });
      this.jLabelHistoryTitle.setFont(new Font("Arial", 0, 14));
      this.jLabelHistoryTitle.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
      final WebToggleButton hButton = new WebToggleButton(WebCollapsiblePaneStyle.collapseIcon, Settings.EnumSettings2.HISTORY_SHOW.is());
      hButton.setSelectedIcon(WebCollapsiblePaneStyle.expandIcon);
      hButton.setUndecorated(true);
      hButton.setFocusable(false);
      hButton.setMargin(0, 8, 0, 0);
      ActionListener actionListener = e -> {
         this.jScrollPaneHistory.setViewportView(Settings.EnumSettings2.HISTORY_SHOW.set(hButton.isSelected()) ? this.jListHistory : null);
         this.jScrollPaneNextSchedule.setViewportView(Settings.EnumSettings2.HISTORY_SHOW.is() ? this.jListNextSchedule : null);
         this.jSplitPaneMusicAndSlide.revalidate();
         this.jPanelSlideView.revalidate();
         this.jPanelSlideView.repaint();
      };
      hButton.addActionListener(actionListener);
      this.jLabelHistoryTitle.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            hButton.doClick(0);
         }
      });
      this.jScrollPaneHistory.setViewportView(Settings.EnumSettings2.HISTORY_SHOW.is() ? this.jListHistory : null);
      this.jScrollPaneNextSchedule.setViewportView(Settings.EnumSettings2.HISTORY_SHOW.is() ? this.jListNextSchedule : null);
      JPanel panel = new JPanel(new BorderLayout());
      panel.add(this.jLabelHistoryTitle, "Center");
      panel.add(hButton, "West");
      panel.add(new JSeparator(0), "Last");
      panel.setOpaque(false);
      this.jScrollPaneHistory.setColumnHeaderView(panel);
      JLabel header = new JLabel(I18N.WORD_SCHEDULE.val(), 0);
      header.setFont(new Font("Arial", 0, 14));
      header.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
      panel = new JPanel(new BorderLayout());
      panel.add(header, "Center");
      panel.add(new JSeparator(0), "Last");
      panel.setOpaque(false);
      this.jScrollPaneNextSchedule.setColumnHeaderView(panel);
      this.setupExportToPDFMenuBar();
      this.setupWebButtonsToolBar();
      this.jPanelSplitLeftHeaderCenter.setVisible(false);
      this.jMenuAnnouncementShortcut.setIcon(this.jMenuItemAnnouncementShow.getIcon());
      this.refreshAnnouncementMenuBar();
      if (VLC.ENABLED) {
         this.webCollapsiblePaneVLC = new WebCollapsiblePane("<html><span style='text-align:center'><b>VLC Player</b>", VLC.PLAYER_CONTROL);
         this.webCollapsiblePaneVLC.setBackground(new Color(192, 192, 192));
         this.webCollapsiblePaneVLC.setExpanded(Settings.EnumSettings2.PLAYER_VIEW_EXPANDED.is());
         this.webCollapsiblePaneVLC.addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
            public void collapsed(WebCollapsiblePane pane) {
               Settings.EnumSettings2.PLAYER_VIEW_EXPANDED.set(false);
               super.collapsed(pane);
            }

            public void expanded(WebCollapsiblePane pane) {
               Settings.EnumSettings2.PLAYER_VIEW_EXPANDED.set(true);
               super.expanded(pane);
            }
         });
         this.webCollapsiblePaneVLC.setFocusable(false);
         this.webCollapsiblePaneVLC.setTitlePanePostion(2);
         this.webCollapsiblePaneVLC.setTitleAlignment(0);
         this.webCollapsiblePaneVLC.setFont(new Font("Arial", 0, 12));
         this.webCollapsiblePaneVLC.setShadeWidth(0);
         Component c = this.webCollapsiblePaneVLC.getTitleComponent();
         this.webCollapsiblePaneVLC.getHeaderPanel().setOpaque(true);
         this.webCollapsiblePaneVLC.getHeaderPanel().setWebColoredBackground(false);
         this.webCollapsiblePaneVLC.getHeaderPanel().setBackground(WebPanelStyle.backgroundColor);
         if (c instanceof WebVerticalLabel) {
            VLC.PLAYER_CONTROL.setPlayerStateListener(newState -> {
            });
         }

         VLC.PLAYER_CONTROL.setMargin(2, 4, 2, 4);
         this.jPanelMusicTop.add(this.webCollapsiblePaneVLC, "West");
      }

      this.jButtonDisplaySettings.addActionListener(e -> openDisplaySettings(this));
      this.jMenuItemFileDisplaySettings.addActionListener(evt -> openDisplaySettings(this));
      Arrays.asList(this.jSplitPaneMainAndRightColumn, this.jSplitPaneMusicAndSlide, this.jSplitPaneMusicsAndMain)
         .forEach(c -> c.getInputMap(1).put(KeyStroke.getKeyStroke(117, 0), "none"));
      Predicate<? super Announcement> anPredicateAccept = hItem -> Collections.list(this.listModelHItemPlayList.elements())
            .stream()
            .anyMatch(i -> i.getId().equals(String.valueOf(hItem.id)));
      Consumer<IHItem> add = hItem -> this.addPlayList(Arrays.asList(hItem));
      SchedulePanelGrid.setupMediaTabTitle(this.jTabbedPanePlayListTemp, 1, anPredicateAccept, add);
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridy = 0;
      this.switchSaveThemeToMusic.setFocusable(false);
      String html = "<html><div style='text-align:center;padding:0px 6px;white-space:nowrap'>" + I18N.MSG_SAVE_THEME_TO_MUSIC_WITH_BR.val();
      this.jPanelAddThemeToMusic.add(new WebLabel(html).setFontName("Arial").setFontSize(14), gbc);
      gbc = new GridBagConstraints();
      gbc.gridy = 1;
      gbc.insets.top = 2;
      this.jPanelAddThemeToMusic.add(this.switchSaveThemeToMusic, gbc);
      this.switchSaveThemeToMusic.addActionListener(evt -> {
         if (!this.preventChangeDefaultThemeToMusic) {
            if (this.switchSaveThemeToMusic.isSelected()) {
               Object obj = this.jComboBoxThemeDefault.getSelectedItem();
               if (obj instanceof Theme) {
                  Settings.SET.getMusic().setDefaultTheme(((Theme)obj).id);
               }
            } else {
               Settings.SET.getMusic().removeDefaultTheme();
            }

            this.updateThemePreview();
            this.jComboBoxThemeDefault.setEnabled(this.switchSaveThemeToMusic.isSelected());
         }
      });
      this.jComboBoxThemeDefault.addItemListener(evt -> {
         if (!this.preventChangeDefaultThemeToMusic && this.switchSaveThemeToMusic.isSelected()) {
            if (evt.getStateChange() == 1) {
               Object obj = this.jComboBoxThemeDefault.getSelectedItem();
               if (obj instanceof Theme) {
                  Settings.SET.getMusic().setDefaultTheme(((Theme)obj).id);
               }

               this.updateThemePreview();
            }
         }
      });
      this.jPanelScreenInfo.setLayout(new WrapLayout(1, 24, 6));
      this.jPanelScreenInfo.revalidate();
      Settings.EnumLanguage.setupJMenuBar(this.jMenuLanguage);
      SwingUtils.setupComponentMinimumSize(this.jPanelScreenInfo, new Dimension());
      this.jMenuItemDownloadVLCMediaPlayer.setEnabled(!VLC.ENABLED);
      this.jMenuItemDownloadVLCMediaPlayer.setVisible(!VLC.ENABLED);
      Freeze.setup(this.jCheckBoxMenuItemFreezeScreen);
   }

   private void setupWebLaf() {
      WebLafUtils.setupScrollBarLargeThumbWithoutButton(((WebScrollPane)this.jScrollPaneThemes).getWebHorizontalScrollBar());
      WebLafUtils.setupWebLafBorderLayout(this.jPanelMusicInfo, this.jPanelRight, this.jPanelMusic);
      SwingUtils.setJPanelToOpaqueFalse(this);
      ((WebPanel)this.jPanelMusicInfo).setUndecorated(true);
      ((WebToolBar)this.jToolBarMusic).setUndecorated(true);
      ((WebToolBar)this.jToolBarMusic).setSpacing(5);
      ((WebComboBox)this.jComboBoxEvent).setShadeWidth(0);
      ((WebComboBox)this.jComboBoxScreenType).setShadeWidth(0);
      ((WebButton)this.jButtonGoToBible).setShadeColor(COLOR_TRANSPARENT);
      ((WebButton)this.jButtonQuickPresentation).setShadeColor(COLOR_TRANSPARENT);
      Arrays.asList(this.jButtonNewTheme, this.jButtonPlayListShowAll, this.jButtonPlayListShowSelection).forEach(b -> ((WebButton)b).setDrawShade(false));
      Arrays.asList(this.jScrollPaneHistory, this.jScrollPaneNextSchedule, this.jScrollPaneOrder, this.jScrollPaneLyrics)
         .forEach(s -> ((WebScrollPane)s).setShadeWidth(1));
   }

   private void addToolTipButtons() {
      WebLafUtils.addToolTipButton(this, this.jButtonShowBeginning, Hotkey.F5);
      WebLafUtils.addToolTipButton(this, this.jButtonGoToBible, Hotkey.CTRL_B, Hotkey.F7);
      WebLafUtils.addToolTipButton(this, this.jButtonQuickPresentation, Hotkey.CTRL_Q);
      WebLafUtils.addToolTipButton(this, this.jButtonShowCurrentSlide, new HotkeyData(false, false, true, 116));
      WebLafUtils.addToolTipButton(this, this.jButtonEditMusic, Hotkey.F2);
      WebLafUtils.addToolTipButton(this, this.jButtonAddPlayList);
      WebLafUtils.addToolTipButton(this, this.jButtonDeleteMusic);
      WebLafUtils.addToolTipButton(this, this.jButtonAddHistory, Hotkey.CTRL_P);
      Arrays.asList(this.jToggleButtonTitleUnderline, this.jToggleButtonBreakLine, this.jToggleButtonUppercase, this.jToggleButtonCustomFormatting)
         .forEach(b -> WebLafUtils.addToolTipButtonZeroDelay(this, ((WebToggleButton)b).setShadeWidth(0)));
   }

   private void setupWebButtonsToolBar() {
      Arrays.asList(this.jToolBarMusic.getComponents())
         .stream()
         .filter(WebButton.class::isInstance)
         .map(WebButton.class::cast)
         .forEach(c -> SwingUtils.setupButtonDisableIcon(c.setUndecorated(true)));
      this.musicChanged(Music.BLANK);
   }

   private void firstOpen() {
      HelpView.main(this, EnumHelpPages.HOME);
   }

   private void insertShortcuts() {
      List<InsertShortcut.Shortcut> list = new ArrayList<>();
      list.add(InsertShortcut.Shortcut.create(39, MusicMain::staticNext));
      list.add(InsertShortcut.Shortcut.create(37, MusicMain::staticPrevious));
      list.add(InsertShortcut.Shortcut.create(27, MusicMain::staticExit));
      list.add(InsertShortcut.Shortcut.create(70, 128, this::ctrlF));
      list.add(InsertShortcut.Shortcut.create(10, 512, this::altEnter));
      list.add(InsertShortcut.Shortcut.create(10, 640, this::ctrlAltEnter));
      list.add(InsertShortcut.Shortcut.create(55, 128, () -> this.changeTabMedia(0)));
      list.add(InsertShortcut.Shortcut.create(103, 128, () -> this.changeTabMedia(0)));
      list.add(InsertShortcut.Shortcut.create(48, 128, () -> this.changeTabMedia(3)));
      list.add(InsertShortcut.Shortcut.create(96, 128, () -> this.changeTabMedia(3)));
      if (VLC.ENABLED) {
         list.add(InsertShortcut.Shortcut.create(56, 128, () -> this.changeTabMedia(1)));
         list.add(InsertShortcut.Shortcut.create(104, 128, () -> this.changeTabMedia(1)));
         list.add(InsertShortcut.Shortcut.create(57, 128, () -> this.changeTabMedia(2)));
         list.add(InsertShortcut.Shortcut.create(105, 128, () -> this.changeTabMedia(2)));
      }

      InsertShortcut.add(this, list);
   }

   private void addMouseListenerJList() {
      List<Field> fields = new ArrayList<>();
      fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));
      fields.addAll(Arrays.asList(this.getClass().getFields()));

      for (Field f : fields) {
         try {
            f.setAccessible(true);
            Object ob = f.get(this);
            if (ob instanceof JList) {
               ((JList)ob).addMouseListener(new MouseAdapter() {
                  @Override
                  public void mousePressed(MouseEvent e) {
                     if (SwingUtilities.isRightMouseButton(e)) {
                        JList list = (JList)e.getSource();
                        int i = list.locationToIndex(e.getPoint());
                        if (i != -1 && !Arrays.stream(list.getSelectedIndices()).anyMatch(x -> x == i)) {
                           list.setSelectedIndex(list.locationToIndex(e.getPoint()));
                        }

                        MusicMain.this.popUpMenuJList(e, list);
                     }
                  }
               });
            }
         } catch (Exception var5) {
            Log.send(var5);
         }
      }
   }

   private void setupJListKeyListenerPresentation(JList jlist) {
      jlist.addKeyListener(new KeyListener() {
         @Override
         public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
               case 33:
               case 37:
                  BooleanOptional.runIfTrue(MusicMain.staticPrevious(), () -> e.consume());
                  break;
               case 34:
               case 39:
                  BooleanOptional.runIfTrue(MusicMain.staticNext(), () -> e.consume());
               case 35:
               case 36:
               case 38:
            }
         }

         @Override
         public void keyTyped(KeyEvent e) {
            e.consume();
         }

         @Override
         public void keyReleased(KeyEvent e) {
            e.consume();
         }
      });
   }

   private void initComponents() {
      this.jPanelDisplaySettingsButtons = new JPanel();
      this.jToggleButtonUppercase = new WebToggleButton();
      this.jToggleButtonTitleUnderline = new WebToggleButton();
      this.jToggleButtonBreakLine = new WebToggleButton();
      this.jToggleButtonCustomFormatting = new WebToggleButton();
      this.jPanelGeneral = new JPanel();
      this.jSplitPaneMusicsAndMain = new JSplitPane();
      this.jPanelSplitLeft = new JPanel();
      this.jPanelSplitLeftHeader = new JPanel();
      this.jPanelSplitLeftHeaderHeader = new JPanel();
      this.jButtonQuickPresentation = new WebButton();
      this.jButtonGoToBible = new WebButton();
      this.jPanelSplitLeftHeaderCenter = new JPanel();
      this.jPanelSplitRight = new JPanel();
      this.jSplitPaneMainAndRightColumn = new JSplitPane();
      this.jPanelLeft = new JPanel();
      this.jPanelMusic = new WebPanel(true);
      this.jPanelMusicTop = new JPanel();
      this.jPanelMusicTopCenter = new JPanel();
      this.jPanelMusicInfo = new WebPanel(true);
      this.jLabelMusicTitleAndArtist = new JLabel();
      this.jLabelMusicNote = new JLabel();
      this.jPanelMusicToolBar = new JPanel();
      this.jToolBarMusic = new WebToolBar();
      this.jSeparator19 = new javax.swing.JToolBar.Separator();
      this.jButtonShowBeginning = new WebButton();
      this.jSeparator13 = new javax.swing.JToolBar.Separator();
      this.jButtonShowCurrentSlide = new WebButton();
      this.jSeparator14 = new javax.swing.JToolBar.Separator();
      this.jButtonAddPlayList = new WebButton();
      this.jSeparator15 = new javax.swing.JToolBar.Separator();
      this.jButtonAddHistory = new WebButton();
      this.jSeparator16 = new javax.swing.JToolBar.Separator();
      this.jButtonEditMusic = new WebButton();
      this.jSeparator17 = new javax.swing.JToolBar.Separator();
      this.jButtonDeleteMusic = new WebButton();
      this.jSeparator18 = new javax.swing.JToolBar.Separator();
      this.jPanelSeparator = new JPanel();
      this.jSeparator12 = new JSeparator();
      this.jSeparator3 = new JSeparator();
      this.jPanelMusicMiddle = new JPanel();
      this.jSplitPaneMusicAndSlide = new JSplitPane();
      this.jPanelMusicLyrics = new JPanel();
      this.jScrollPaneLyrics = new WebScrollPane(null);
      this.jTextPaneLyrics = new JTextPane();
      this.jTextPaneLyrics.setBorder(new CompoundBorder(this.jTextPaneLyrics.getBorder(), new EmptyBorder(5, 5, 5, 5)));
      this.jPanelSlideView = new JPanel();
      this.jPanelMusicHistory = new JPanel();
      this.jScrollPaneHistory = new WebScrollPane(null);
      this.jListHistory = new JList<>();
      this.jScrollPaneNextSchedule = new WebScrollPane(null);
      this.jListNextSchedule = new WebList();
      this.jLabelSlidePreview = this.createSlidePreview();
      this.slidePreview = (PresentationSlideView)this.jLabelSlidePreview;
      this.jPanelScreenInfo = new JPanel();
      this.jPanelThemeDefault = new JPanel();
      this.jPanelAddThemeToMusic = new JPanel();
      this.jComboBoxThemeDefault = new JComboBox<>();
      this.jPanelScreenInfoEast = new JPanel();
      this.jLabelSlidePreviewIsAnimated = new JLabel();
      this.jButtonDisplaySettings = new JButton();
      this.jComboBoxScreenType = new WebComboBox();
      this.jPanelOrder = new JPanel();
      this.jScrollPaneOrder = new WebScrollPane(null);
      this.jListOrder = new JList<>();
      this.jPanelRight = new WebPanel(true);
      this.jPanelPlayList = new JPanel();
      this.jComboBoxPlayListType = new WebComboBox();
      this.jPanelPlayListCard = new JPanel();
      this.jPanelPlayListTemp = new JPanel();
      this.jTabbedPanePlayListTemp = new WebTabbedPane() {
         {
            this.setTabStretchType(TabStretchType.always);
            this.getTabInsets().left = this.getTabInsets().right = 4;
            this.setShadeWidth(2);
         }
      };
      this.jPanelPlayListMusic = new JPanel();
      this.jScrollPanePlayList = new JScrollPane();
      this.jListPlayList = new ListDragAndDropMusic(this.listModelPlayList, this.listCellRendererPlayList);
      this.jPanelPlayListTempFooter = new JPanel();
      this.jButtonPlayListShowAll = new WebButton();
      this.jButtonPlayListShowSelection = new WebButton();
      this.jPanelPlayListMedia = new JPanel();
      this.jScrollPaneMedia = new JScrollPane();
      this.jListMedia = new ListDragAndDropHItem(this.listModelHItemPlayList, this.listCellRendererHItemPlayList);
      this.jPanelEvent = new JPanel();
      this.jPanelEventSettings = new JPanel();
      this.jPanelEventSettingsDate = new JPanel();
      this.jSpinnerEventDate = new WebSpinner();
      this.jComboBoxEvent = new WebComboBox() {
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
      this.jPanelRightFooter = new JPanel();
      this.jSeparator5 = new JSeparator();
      this.jSeparator8 = new JSeparator();
      this.jPanelTheme = new JPanel();
      this.jPanelThemeHeader = new JPanel();
      this.filler1 = new Filler(new Dimension(20, 0), new Dimension(20, 0), new Dimension(20, 32767));
      this.jLabelTheme = new JLabel();
      this.jButtonNewTheme = new WebButton();
      this.jButtonReorder = new JButton();
      this.jScrollPaneThemes = new WebScrollPane(null);
      this.jListThemes = new JList<>();
      this.jPanelFooter = new JPanel();
      this.jMenuBarMenu = new JMenuBar();
      this.jMenuFile = new JMenu();
      this.jMenuItemGoToBible = new JMenuItem();
      this.jSeparator2 = new Separator();
      this.jMenuItemFileFilesReceived = new JMenuItem();
      this.jMenuItemLyricsDataBase = new JMenuItem();
      this.jMenuItemFileSettings = new JMenuItem();
      this.jMenuItemFileDisplaySettings = new JMenuItem();
      this.jSeparator6 = new Separator();
      this.jMenuItemExit = new JMenuItem();
      this.jMenuNew = new JMenu();
      this.jMenuItemNewMusic = new JMenuItem();
      this.jMenuItemNewTheme = new JMenuItem();
      this.jMenuItemNewProfile = new JMenuItem();
      this.jMenuItemNewWorship = new JMenuItem();
      this.jMenuItemNewEvent = new JMenuItem();
      this.jMenuItemNewMember = new JMenuItem();
      this.jMenuEdit = new JMenu();
      this.jMenuItemEditProfile = new JMenuItem();
      this.jMenuItemEditWorship = new JMenuItem();
      this.jMenuItemEditEvent = new JMenuItem();
      this.jMenuItemEditMember = new JMenuItem();
      this.jMenuMusic = new JMenu();
      this.jMenuItemWebSearch = new JMenuItem();
      this.jMenuItemDatabaseSearch = new JMenuItem();
      this.jSeparator11 = new Separator();
      this.jMenuItemMusicSavePresentationAs = new JMenuItem();
      this.jSeparator7 = new Separator();
      this.jMenuMusicBatchImport = new JMenu();
      this.jMenuMusicImportFile = new JMenu();
      this.jMenuItemMusicImportFileOne = new JMenuItem();
      this.jMenuItemMusicImportFileBatch = new JMenuItem();
      this.jMenuItemMusicBathImportFromDBO = new JMenuItem();
      this.jMenuMusicImportPP = new JMenu();
      this.jMenuItemMusicImportPPManyInOne = new JMenuItem();
      this.jMenuItemMusicImportPPOneInOne = new JMenuItem();
      this.jMenuMusicImportOthers = new JMenu();
      this.jMenuItemMusicBathImportFromEasiSlides = new JMenuItem();
      this.jMenuItemMusicBathImportFromOpenSong = new JMenuItem();
      this.jMenuItemMusicBathImportFromOpenLP = new JMenuItem();
      this.jMenuItemMusicBathImportFromCCLI_USR = new JMenuItem();
      this.jMenuItemMusicBathImportFromWord = new JMenuItem();
      this.jMenuMusicExport = new JMenu();
      this.jMenuFileSaveMusic = new JMenuItem();
      this.jMenuItemMusicBathExport = new JMenuItem();
      this.jSeparator10 = new Separator();
      this.jMenuItemMusicBatchDelete = new JMenuItem();
      this.jMenuTheme = new JMenu();
      this.jMenuFileThemeImport = new JMenuItem();
      this.jMenuFileThemeExport = new JMenuItem();
      this.jMenuAnnouncement = new JMenu();
      this.jMenuItemAnnouncementEdit = new JMenuItem();
      this.jMenuItemAnnouncementShow = new JMenuItem();
      this.jMenuSchedule = new JMenu();
      this.jMenuItemScheduleViewEdit = new JMenuItem();
      this.jMenuReport = new JMenu();
      this.jMenuItemReportPlayedMusic = new JMenuItem();
      this.jMenuItemReportSchedule = new JMenuItem();
      this.jMenuTools = new JMenu();
      this.jMenuItemImageFileShow = new JMenuItem();
      this.jMenuItemCreateImage = new JMenuItem();
      this.jMenuItemCountdown = new JMenuItem();
      this.jMenuItemToolsAutomaticShow = new JMenuItem();
      this.jCheckBoxMenuItemFreezeScreen = new JCheckBoxMenuItem();
      this.jMenuExportToPDF = new JMenu();
      this.jSeparator9 = new Separator();
      this.jMenuItemCreateDesktopShortcur = new JMenuItem();
      this.jSeparator4 = new Separator();
      this.jMenuItemPluginHolyrics = new JMenuItem();
      this.jMenuItemChangeWebServiceIP = new JMenuItem();
      this.jMenuBackup = new JMenu();
      this.jMenuItemImport = new JMenuItem();
      this.jMenuItemExport = new JMenuItem();
      this.jMenuWindows = new JMenu();
      this.jCheckBoxMenuItemWindowsFavorites = new JCheckBoxMenuItem();
      this.jMenuItemWindowsCommunicationPanel = new JMenuItem();
      this.jMenuItem1 = new JMenuItem();
      this.jMenuItemWindowsLive = new JMenuItem();
      this.jMenuShare = new JMenu();
      this.jMenuItemShareInTheWeb = new JMenuItem();
      this.jMenuItemReceiveUpdateFromMobile = new JMenuItem();
      this.jMenuItemReceiveUpdateFromInternet = new JMenuItem();
      this.jCheckBoxCheckChangesOnExit = new JCheckBoxMenuItem();
      this.jMenuHelp = new JMenu();
      this.jMenuItemHelp = new JMenuItem();
      this.jMenuItemTutorialYouTube = new JMenuItem();
      this.jMenuItemSupportAndFeedBack = new JMenuItem();
      this.jMenuItemCheckUpdates = new JMenuItem();
      this.jMenuItemDownloadVLCMediaPlayer = new JMenuItem();
      this.jMenuLanguage = new JMenu();
      this.jMenuHelpCompatibility = new JMenu();
      this.jCheckBoxMenuItemUseJavaFXVideo = new JCheckBoxMenuItem();
      this.jSeparator1 = new Separator();
      this.jMenuItemAbout = new JMenuItem();
      this.jPanelDisplaySettingsButtons.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelDisplaySettingsButtons.setFocusable(false);
      this.jPanelDisplaySettingsButtons.setOpaque(false);
      this.jPanelDisplaySettingsButtons.setLayout(new FlowLayout(1, 5, 2));
      this.jToggleButtonUppercase.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/toUpperCase16.png")));
      this.jToggleButtonUppercase.setSelected(Settings.EnumSettings.MUSIC_UPPERCASE.is());
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jToggleButtonUppercase.setToolTipText(bundle.getString("word.uppercase"));
      this.jToggleButtonUppercase.setFocusable(false);
      this.jToggleButtonUppercase.setMargin(new Insets(2, 2, 2, 2));
      this.jToggleButtonUppercase.setMinimumSize(new Dimension(0, 0));
      this.jToggleButtonUppercase.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicMain.this.jToggleButtonUppercaseItemStateChanged(evt);
         }
      });
      this.jPanelDisplaySettingsButtons.add(this.jToggleButtonUppercase);
      this.jToggleButtonTitleUnderline.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/underline16.png")));
      this.jToggleButtonTitleUnderline.setSelected(Settings.EnumSettings2.ARTIST_UNDERLINE.is());
      this.jToggleButtonTitleUnderline.setToolTipText(bundle.getString("MusicMain.jToggleButtonTitleUnderline.toolTipText"));
      this.jToggleButtonTitleUnderline.setFocusable(false);
      this.jToggleButtonTitleUnderline.setMargin(new Insets(2, 2, 2, 2));
      this.jToggleButtonTitleUnderline.setMinimumSize(new Dimension(0, 0));
      this.jToggleButtonTitleUnderline.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicMain.this.jToggleButtonTitleUnderlineItemStateChanged(evt);
         }
      });
      this.jPanelDisplaySettingsButtons.add(this.jToggleButtonTitleUnderline);
      this.jToggleButtonBreakLine.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/wrapText16.png")));
      this.jToggleButtonBreakLine.setSelected(Settings.EnumSettings2.BREAK_LINE.is());
      this.jToggleButtonBreakLine.setToolTipText(bundle.getString("msg.automaticallyBreakLine"));
      this.jToggleButtonBreakLine.setFocusable(false);
      this.jToggleButtonBreakLine.setMargin(new Insets(2, 2, 2, 2));
      this.jToggleButtonBreakLine.setMinimumSize(new Dimension(0, 0));
      this.jToggleButtonBreakLine.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicMain.this.jToggleButtonBreakLineItemStateChanged(evt);
         }
      });
      this.jPanelDisplaySettingsButtons.add(this.jToggleButtonBreakLine);
      this.jToggleButtonCustomFormatting.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/style16.png")));
      this.jToggleButtonCustomFormatting.setSelected(Settings.EnumSettings2.TEXT_FORMATTING_ENABLED.is());
      this.jToggleButtonCustomFormatting.setToolTipText(bundle.getString("msg.customFormatting"));
      this.jToggleButtonCustomFormatting.setFocusable(false);
      this.jToggleButtonCustomFormatting.setMargin(new Insets(2, 2, 2, 2));
      this.jToggleButtonCustomFormatting.setMinimumSize(new Dimension(0, 0));
      this.jToggleButtonCustomFormatting.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicMain.this.jToggleButtonCustomFormattingItemStateChanged(evt);
         }
      });
      this.jPanelDisplaySettingsButtons.add(this.jToggleButtonCustomFormatting);
      this.setDefaultCloseOperation(0);
      this.setTitle("Holyrics");
      this.setExtendedState(6);
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(950, 700));
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            MusicMain.this.formWindowClosing(evt);
         }
      });
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelGeneral.setPreferredSize(new Dimension(800, 600));
      this.jPanelGeneral.setLayout(new BorderLayout(0, 2));
      this.jSplitPaneMusicsAndMain.setDividerLocation(310);
      this.jPanelSplitLeft.setLayout(new BorderLayout());
      this.jPanelSplitLeftHeader.setLayout(new BorderLayout(0, 4));
      this.jPanelSplitLeftHeaderHeader.setLayout(new BorderLayout());
      this.jButtonQuickPresentation.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/message24.png")));
      this.jButtonQuickPresentation.setToolTipText(bundle.getString("msg.quickPresentation"));
      this.jButtonQuickPresentation.setFocusable(false);
      this.jButtonQuickPresentation.setMinimumSize(new Dimension(0, 0));
      this.jButtonQuickPresentation.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonQuickPresentationActionPerformed(evt);
         }
      });
      this.jPanelSplitLeftHeaderHeader.add(this.jButtonQuickPresentation, "West");
      this.jButtonGoToBible.setFont(new Font("Georgia", 0, 15));
      this.jButtonGoToBible.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/bible26.png")));
      this.jButtonGoToBible.setText(bundle.getString("MusicMain.jButtonGoToBible.text"));
      this.jButtonGoToBible.setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.goToBible")));
      this.jButtonGoToBible.setFocusable(false);
      this.jButtonGoToBible.setMinimumSize(new Dimension(0, 0));
      this.jButtonGoToBible.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonGoToBibleActionPerformed(evt);
         }
      });
      this.jPanelSplitLeftHeaderHeader.add(this.jButtonGoToBible, "Center");
      this.jPanelSplitLeftHeader.add(this.jPanelSplitLeftHeaderHeader, "First");
      this.jPanelSplitLeftHeaderCenter.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
      this.jPanelSplitLeftHeaderCenter.setLayout(new BorderLayout());
      this.jPanelSplitLeftHeader.add(this.jPanelSplitLeftHeaderCenter, "Last");
      this.jPanelSplitLeft.add(this.jPanelSplitLeftHeader, "First");
      this.jSplitPaneMusicsAndMain.setLeftComponent(this.jPanelSplitLeft);
      this.jPanelSplitRight.setLayout(new BorderLayout());
      this.jSplitPaneMainAndRightColumn.setDividerLocation(458);
      this.jSplitPaneMainAndRightColumn.setResizeWeight(1.0);
      this.jPanelLeft.setLayout(new BorderLayout());
      this.jPanelMusic.setLayout(new BorderLayout());
      this.jPanelMusicTop.setLayout(new BorderLayout());
      this.jPanelMusicTopCenter.setLayout(new BorderLayout());
      this.jPanelMusicInfo
         .setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(146, 151, 161)), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
      this.jPanelMusicInfo.setLayout(new BorderLayout());
      this.jLabelMusicTitleAndArtist.setFont(new Font("Georgia", 0, 20));
      this.jLabelMusicTitleAndArtist.setHorizontalAlignment(0);
      this.jLabelMusicTitleAndArtist.setVerticalAlignment(1);
      this.jLabelMusicTitleAndArtist.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
      this.jPanelMusicInfo.add(this.jLabelMusicTitleAndArtist, "First");
      this.jLabelMusicNote.setFont(new Font("Georgia", 0, 14));
      this.jLabelMusicNote.setHorizontalAlignment(0);
      this.jLabelMusicNote.setVerticalAlignment(1);
      this.jLabelMusicNote.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
      this.jPanelMusicInfo.add(this.jLabelMusicNote, "Center");
      this.jPanelMusicTopCenter.add(this.jPanelMusicInfo, "First");
      this.jPanelMusicToolBar.setLayout(new GridBagLayout());
      this.jToolBarMusic.setFloatable(false);
      this.jToolBarMusic.setRollover(true);
      this.jToolBarMusic.add(this.jSeparator19);
      this.jButtonShowBeginning.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonShowBeginning.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack24.png")));
      this.jButtonShowBeginning.setToolTipText(bundle.getString("MusicMain.jButtonShowBeginning.toolTipText"));
      this.jButtonShowBeginning.setFocusable(false);
      this.jButtonShowBeginning.setHorizontalAlignment(2);
      this.jButtonShowBeginning.setHorizontalTextPosition(4);
      this.jButtonShowBeginning.setIconTextGap(2);
      this.jButtonShowBeginning.setLabel(bundle.getString("MusicMain.jButtonShowBeginning.label"));
      this.jButtonShowBeginning.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonShowBeginning.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonShowBeginningActionPerformed(evt);
         }
      });
      this.jToolBarMusic.add(this.jButtonShowBeginning);
      this.jToolBarMusic.add(this.jSeparator13);
      this.jButtonShowCurrentSlide.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonShowCurrentSlide.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/skipNext24.png")));
      this.jButtonShowCurrentSlide.setToolTipText(bundle.getString("MusicMain.jButtonShowCurrentSlide.toolTipText"));
      this.jButtonShowCurrentSlide.setFocusable(false);
      this.jButtonShowCurrentSlide.setHorizontalAlignment(2);
      this.jButtonShowCurrentSlide.setHorizontalTextPosition(4);
      this.jButtonShowCurrentSlide.setIconTextGap(2);
      this.jButtonShowCurrentSlide.setLabel(bundle.getString("MusicMain.jButtonShowCurrentSlide.label"));
      this.jButtonShowCurrentSlide.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonShowCurrentSlide.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonShowCurrentSlideActionPerformed(evt);
         }
      });
      this.jToolBarMusic.add(this.jButtonShowCurrentSlide);
      this.jToolBarMusic.add(this.jSeparator14);
      this.jButtonAddPlayList.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonAddPlayList.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playlistAdd24.png")));
      this.jButtonAddPlayList.setToolTipText(I18N.MSG_TT_ADD_TO_PLAYLIST.val());
      this.jButtonAddPlayList.setFocusable(false);
      this.jButtonAddPlayList.setHorizontalTextPosition(0);
      this.jButtonAddPlayList.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonAddPlayList.setPreferredSize(new Dimension(36, 33));
      this.jButtonAddPlayList.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonAddPlayListActionPerformed(evt);
         }
      });
      this.jToolBarMusic.add(this.jButtonAddPlayList);
      this.jToolBarMusic.add(this.jSeparator15);
      this.jButtonAddHistory.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonAddHistory.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playedMusic24.png")));
      this.jButtonAddHistory.setToolTipText(I18N.MSG_SET_TO_MUSIC_PLAYED.val());
      this.jButtonAddHistory.setFocusable(false);
      this.jButtonAddHistory.setHorizontalTextPosition(0);
      this.jButtonAddHistory.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonAddHistory.setPreferredSize(new Dimension(36, 33));
      this.jButtonAddHistory.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonAddHistoryActionPerformed(evt);
         }
      });
      this.jToolBarMusic.add(this.jButtonAddHistory);
      this.jToolBarMusic.add(this.jSeparator16);
      this.jButtonEditMusic.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonEditMusic.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/edit24.png")));
      this.jButtonEditMusic.setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.edit")));
      this.jButtonEditMusic.setFocusable(false);
      this.jButtonEditMusic.setHorizontalTextPosition(0);
      this.jButtonEditMusic.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonEditMusic.setPreferredSize(new Dimension(36, 33));
      this.jButtonEditMusic.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonEditMusicActionPerformed(evt);
         }
      });
      this.jToolBarMusic.add(this.jButtonEditMusic);
      this.jToolBarMusic.add(this.jSeparator17);
      this.jButtonDeleteMusic.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonDeleteMusic.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/delete24.png")));
      this.jButtonDeleteMusic.setToolTipText(bundle.getString("word.delete"));
      this.jButtonDeleteMusic.setFocusable(false);
      this.jButtonDeleteMusic.setHorizontalTextPosition(0);
      this.jButtonDeleteMusic.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonDeleteMusic.setPreferredSize(new Dimension(36, 33));
      this.jButtonDeleteMusic.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonDeleteMusicActionPerformed(evt);
         }
      });
      this.jToolBarMusic.add(this.jButtonDeleteMusic);
      this.jToolBarMusic.add(this.jSeparator18);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      this.jPanelMusicToolBar.add(this.jToolBarMusic, gridBagConstraints);
      this.jPanelMusicTopCenter.add(this.jPanelMusicToolBar, "Center");
      this.jPanelMusicTop.add(this.jPanelMusicTopCenter, "Center");
      this.jPanelSeparator.setBorder(BorderFactory.createEmptyBorder(2, 1, 5, 1));
      this.jPanelSeparator.setLayout(new GridLayout(2, 1));
      this.jPanelSeparator.add(this.jSeparator12);
      this.jPanelSeparator.add(this.jSeparator3);
      this.jPanelMusicTop.add(this.jPanelSeparator, "Last");
      this.jPanelMusic.add(this.jPanelMusicTop, "First");
      this.jPanelMusicMiddle.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
      this.jPanelMusicMiddle.setPreferredSize(new Dimension(0, 0));
      this.jPanelMusicMiddle.setLayout(new BorderLayout());
      this.jSplitPaneMusicAndSlide.setDividerLocation(150);
      this.jSplitPaneMusicAndSlide.setResizeWeight(0.5);
      this.jPanelMusicLyrics.setLayout(new BorderLayout());
      this.jScrollPaneLyrics.setFocusable(false);
      this.jTextPaneLyrics.setEditable(false);
      this.jTextPaneLyrics.setFont(new Font("Georgia", 0, 12));
      this.jTextPaneLyrics.setText(Settings.SET.getMusic().getLyrics());
      this.jTextPaneLyrics.setToolTipText(bundle.getString("msg.ttDoubleClickToEdit"));
      this.jTextPaneLyrics.setFocusable(false);
      this.jTextPaneLyrics.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            MusicMain.this.jTextPaneLyricsMouseReleased(evt);
         }
      });
      this.jScrollPaneLyrics.setViewportView(this.jTextPaneLyrics);
      this.jPanelMusicLyrics.add(this.jScrollPaneLyrics, "Center");
      this.jSplitPaneMusicAndSlide.setLeftComponent(this.jPanelMusicLyrics);
      this.jPanelSlideView.setLayout(new BorderLayout());
      this.jPanelMusicHistory.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
      this.jPanelMusicHistory.setLayout(new BorderLayout());
      this.jListHistory.setFont(new Font("Trebuchet MS", 0, 13));
      this.jListHistory.setModel(this.listModelHistory);
      this.jListHistory.setSelectionMode(1);
      this.jListHistory.setCellRenderer(new ListCellRendererHistory());
      this.jListHistory.setFixedCellWidth(111);
      this.jListHistory.setFocusable(false);
      this.jListHistory.setLayoutOrientation(1);
      this.jListHistory.setOpaque(false);
      this.jListHistory.setVisibleRowCount(1);
      this.jScrollPaneHistory.setViewportView(this.jListHistory);
      this.jPanelMusicHistory.add(this.jScrollPaneHistory, "Center");
      this.jListNextSchedule.setFont(new Font("Trebuchet MS", 0, 13));
      this.jListNextSchedule.setModel(this.listModelNextSchedules);
      this.jListNextSchedule.setSelectionMode(1);
      this.jListNextSchedule.setCellRenderer(new ListCellRendererNextSchedules());
      this.jListNextSchedule.setFixedCellWidth(111);
      this.jListNextSchedule.setFocusable(false);
      this.jListNextSchedule.setOpaque(false);
      this.jListNextSchedule.setVisibleRowCount(1);
      this.jScrollPaneNextSchedule.setViewportView(this.jListNextSchedule);
      this.jPanelMusicHistory.add(this.jScrollPaneNextSchedule, "East");
      this.jPanelSlideView.add(this.jPanelMusicHistory, "First");
      this.jPanelSlideView.add(this.jLabelSlidePreview, "Center");
      this.jPanelScreenInfo.setLayout(new GridBagLayout());
      this.jPanelThemeDefault.setLayout(new GridBagLayout());
      this.jPanelAddThemeToMusic.setOpaque(false);
      this.jPanelAddThemeToMusic.setLayout(new GridBagLayout());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 3);
      this.jPanelThemeDefault.add(this.jPanelAddThemeToMusic, gridBagConstraints);
      this.jComboBoxThemeDefault.setModel(this.comboBoxModelDefaultThemeToMusic);
      this.jComboBoxThemeDefault.setFocusable(false);
      this.jComboBoxThemeDefault.setRenderer(new ComboBoxRendererTheme(80, 45));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new Insets(0, 3, 0, 0);
      this.jPanelThemeDefault.add(this.jComboBoxThemeDefault, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.ipadx = -91;
      gridBagConstraints.anchor = 18;
      gridBagConstraints.insets = new Insets(15, 0, 0, 0);
      this.jPanelScreenInfo.add(this.jPanelThemeDefault, gridBagConstraints);
      this.jPanelScreenInfoEast.setLayout(new GridBagLayout());
      this.jLabelSlidePreviewIsAnimated.setFont(new Font("Georgia", 0, 14));
      this.jLabelSlidePreviewIsAnimated.setForeground(new Color(102, 0, 0));
      this.jLabelSlidePreviewIsAnimated.setHorizontalAlignment(0);
      this.jLabelSlidePreviewIsAnimated.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      this.jPanelScreenInfoEast.add(this.jLabelSlidePreviewIsAnimated, gridBagConstraints);
      this.jButtonDisplaySettings.setFont(new Font("Georgia", 0, 12));
      this.jButtonDisplaySettings.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/desktop16.png")));
      this.jButtonDisplaySettings.setText(bundle.getString("msg.displaySettings"));
      this.jButtonDisplaySettings.setFocusable(false);
      this.jButtonDisplaySettings.setMargin(new Insets(2, 2, 2, 2));
      this.jButtonDisplaySettings.setMinimumSize(null);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.insets = new Insets(3, 0, 3, 0);
      this.jPanelScreenInfoEast.add(this.jButtonDisplaySettings, gridBagConstraints);
      this.jComboBoxScreenType.setMaximumRowCount(4);
      this.jComboBoxScreenType
         .setModel(
            new DefaultComboBoxModel<>(
               new PresentationSlideView.EnumScreenType[]{
                  PresentationSlideView.EnumScreenType.YOUR_DISPLAY,
                  PresentationSlideView.EnumScreenType.DISPLAY_MONITOR,
                  PresentationSlideView.EnumScreenType.WIDESCREEN,
                  PresentationSlideView.EnumScreenType.STANDARD
               }
            )
         );
      this.jComboBoxScreenType.setSelectedItem(PresentationSlideView.EnumScreenType.DISPLAY_MONITOR);
      this.jComboBoxScreenType.setFocusable(false);
      this.jComboBoxScreenType.setMinimumSize(null);
      this.jComboBoxScreenType.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicMain.this.jComboBoxScreenTypeItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      this.jPanelScreenInfoEast.add(this.jComboBoxScreenType, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.ipadx = -91;
      gridBagConstraints.anchor = 18;
      gridBagConstraints.insets = new Insets(0, 12, 0, 0);
      this.jPanelScreenInfo.add(this.jPanelScreenInfoEast, gridBagConstraints);
      this.jPanelSlideView.add(this.jPanelScreenInfo, "Last");
      this.jSplitPaneMusicAndSlide.setRightComponent(this.jPanelSlideView);
      this.jPanelMusicMiddle.add(this.jSplitPaneMusicAndSlide, "Center");
      this.jPanelMusic.add(this.jPanelMusicMiddle, "Center");
      this.jPanelOrder.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.jPanelOrder.setLayout(new BorderLayout());
      this.jScrollPaneOrder.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jListOrder.setFont(new Font("Franklin Gothic Medium", 0, 13));
      this.jListOrder.setModel(this.listModelOrder);
      this.jListOrder.setSelectionMode(1);
      this.jListOrder.setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.toolTipMusicOrder")));
      this.jListOrder.setCellRenderer(new ListCellRendererOrder());
      this.jListOrder.setFixedCellWidth(0);
      this.jListOrder.setVisibleRowCount(5);
      this.jListOrder.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent evt) {
            MusicMain.this.jListOrderValueChanged(evt);
         }
      });
      this.jScrollPaneOrder.setViewportView(this.jListOrder);
      this.jPanelOrder.add(this.jScrollPaneOrder, "Center");
      this.jPanelMusic.add(this.jPanelOrder, "Last");
      this.jPanelLeft.add(this.jPanelMusic, "Center");
      this.jSplitPaneMainAndRightColumn.setLeftComponent(this.jPanelLeft);
      this.jPanelRight.setMinimumSize(new Dimension(200, 92));
      this.jPanelRight.setLayout(new BorderLayout());
      this.jPanelPlayList.setMaximumSize(new Dimension(174, 265));
      this.jPanelPlayList.setLayout(new BorderLayout());
      this.jComboBoxPlayListType.setFont(new Font("Georgia", 0, 14));
      this.jComboBoxPlayListType.setModel(new DefaultComboBoxModel<>(EnumPlayList.values()));
      this.jComboBoxPlayListType.setFocusable(false);
      this.jComboBoxPlayListType.setRenderer(new ComboBoxRendererPlayListType());
      this.jComboBoxPlayListType.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicMain.this.jComboBoxPlayListTypeItemStateChanged(evt);
         }
      });
      this.jPanelPlayList.add(this.jComboBoxPlayListType, "First");
      this.jPanelPlayListCard.setMaximumSize(new Dimension(170, Integer.MAX_VALUE));
      this.jPanelPlayListCard.setLayout(new CardLayout());
      this.jPanelPlayListTemp.setLayout(new BorderLayout());
      this.jTabbedPanePlayListTemp.setFocusable(false);
      this.jPanelPlayListMusic.setLayout(new BorderLayout());
      this.jListPlayList.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.jListPlayList.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jListPlayList.setModel(this.listModelPlayList);
      this.jListPlayList.setSelectionMode(1);
      this.jListPlayList.setDragEnabled(true);
      this.jListPlayList.setDropMode(DropMode.INSERT);
      this.jListPlayList.setFixedCellWidth(162);
      this.jListPlayList.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            MusicMain.this.jListPlayListMouseReleased(evt);
         }
      });
      this.jListPlayList.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            MusicMain.this.jListPlayListKeyReleased(evt);
         }
      });
      this.jScrollPanePlayList.setViewportView(this.jListPlayList);
      this.jPanelPlayListMusic.add(this.jScrollPanePlayList, "Center");
      this.jPanelPlayListTempFooter.setLayout(new GridLayout(2, 0));
      this.jButtonPlayListShowAll.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonPlayListShowAll.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack16.png")));
      this.jButtonPlayListShowAll.setText(bundle.getString("MusicMain.jButtonPlayListShowAll.text"));
      this.jButtonPlayListShowAll.setFocusable(false);
      this.jButtonPlayListShowAll.setHorizontalAlignment(2);
      this.jButtonPlayListShowAll.setMargin(new Insets(1, 30, 1, 1));
      this.jButtonPlayListShowAll.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonPlayListShowAllActionPerformed(evt);
         }
      });
      this.jPanelPlayListTempFooter.add(this.jButtonPlayListShowAll);
      this.jButtonPlayListShowSelection.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonPlayListShowSelection.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack16.png")));
      this.jButtonPlayListShowSelection.setText(bundle.getString("MusicMain.jButtonPlayListShowSelection.text"));
      this.jButtonPlayListShowSelection.setFocusable(false);
      this.jButtonPlayListShowSelection.setHorizontalAlignment(2);
      this.jButtonPlayListShowSelection.setMargin(new Insets(1, 30, 1, 1));
      this.jButtonPlayListShowSelection.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonPlayListShowSelectionActionPerformed(evt);
         }
      });
      this.jPanelPlayListTempFooter.add(this.jButtonPlayListShowSelection);
      this.jPanelPlayListMusic.add(this.jPanelPlayListTempFooter, "Last");
      this.jTabbedPanePlayListTemp
         .addTab(
            bundle.getString("word.lyrics"), new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/list16.png")), this.jPanelPlayListMusic
         );
      this.jPanelPlayListMedia.setLayout(new BorderLayout());
      this.jListMedia.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.jListMedia.setSelectionMode(1);
      this.jListMedia.setDragEnabled(true);
      this.jListMedia.setDropMode(DropMode.INSERT);
      this.jListMedia.setFixedCellWidth(162);
      this.jListMedia.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            MusicMain.this.jListMediaMouseReleased(evt);
         }
      });
      this.jListMedia.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            MusicMain.this.jListMediaKeyReleased(evt);
         }
      });
      this.jScrollPaneMedia.setViewportView(this.jListMedia);
      this.jPanelPlayListMedia.add(this.jScrollPaneMedia, "Center");
      this.jTabbedPanePlayListTemp
         .addTab(
            bundle.getString("word.media"),
            new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/videoLibrary16.png")),
            this.jPanelPlayListMedia
         );
      this.jPanelPlayListTemp.add(this.jTabbedPanePlayListTemp, "Center");
      this.jPanelPlayListCard.add(this.jPanelPlayListTemp, "PLAY_LIST");
      this.jPanelEvent.setLayout(new BorderLayout());
      this.jPanelEventSettings.setLayout(new GridBagLayout());
      this.jPanelEventSettingsDate.setLayout(new FlowLayout(1, 5, 2));
      this.jSpinnerEventDate.setFont(new Font("Georgia", 0, 14));
      this.jSpinnerEventDate.setModel(new SpinnerDateModel(new Date(), null, null, 2));
      this.jSpinnerEventDate.setEditor(new DateEditor(this.jSpinnerEventDate, "MM/yyyy"));
      this.jSpinnerEventDate.setFocusable(false);
      this.jSpinnerEventDate.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            MusicMain.this.jSpinnerEventDateStateChanged(evt);
         }
      });
      this.jPanelEventSettingsDate.add(this.jSpinnerEventDate);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelEventSettings.add(this.jPanelEventSettingsDate, gridBagConstraints);
      this.jComboBoxEvent.setFont(new Font("Courier New", 1, 16));
      this.jComboBoxEvent.setMaximumRowCount(16);
      this.jComboBoxEvent.setModel(this.comboBoxModelEvent);
      this.jComboBoxEvent.setFocusable(false);
      this.jComboBoxEvent.setRenderer(new ComboBoxRendererEvent());
      this.jComboBoxEvent.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            MusicMain.this.jComboBoxEventItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 3, 0);
      this.jPanelEventSettings.add(this.jComboBoxEvent, gridBagConstraints);
      this.jPanelEvent.add(this.jPanelEventSettings, "First");
      this.jPanelPlayListCard.add(this.jPanelEvent, "WORSHIP_OR_EVENT");
      this.jPanelPlayList.add(this.jPanelPlayListCard, "Center");
      this.jPanelRight.add(this.jPanelPlayList, "Center");
      this.jPanelTheme.setLayout(new BorderLayout());
      this.jPanelThemeHeader.setLayout(new FlowLayout(1, 5, 1));
      this.jPanelThemeHeader.add(this.filler1);
      this.jLabelTheme.setFont(new Font("Georgia", 0, 14));
      this.jLabelTheme.setHorizontalAlignment(0);
      this.jLabelTheme.setText(bundle.getString("word.themes"));
      this.jLabelTheme.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            MusicMain.this.jLabelThemeMouseReleased(evt);
         }
      });
      this.jPanelThemeHeader.add(this.jLabelTheme);
      this.jButtonNewTheme.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/plus16.png")));
      this.jButtonNewTheme.setBorderPainted(false);
      this.jButtonNewTheme.setContentAreaFilled(false);
      this.jButtonNewTheme.setCursor(new Cursor(12));
      this.jButtonNewTheme.setFocusPainted(false);
      this.jButtonNewTheme.setFocusable(false);
      this.jButtonNewTheme.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonNewThemeActionPerformed(evt);
         }
      });
      this.jPanelThemeHeader.add(this.jButtonNewTheme);
      this.jButtonReorder.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/shuffle16.png")));
      this.jButtonReorder.setBorderPainted(false);
      this.jButtonReorder.setContentAreaFilled(false);
      this.jButtonReorder.setCursor(new Cursor(12));
      this.jButtonReorder.setFocusPainted(false);
      this.jButtonReorder.setFocusable(false);
      this.jButtonReorder.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jButtonReorderActionPerformed(evt);
         }
      });
      this.jPanelThemeHeader.add(this.jButtonReorder);
      this.jPanelTheme.add(this.jPanelThemeHeader, "First");
      this.jScrollPaneThemes.setHorizontalScrollBarPolicy(32);
      this.jListThemes.setModel(this.listModelTheme);
      this.jListThemes.setSelectionMode(0);
      this.jListThemes.setCellRenderer(new ListCellRendererTheme());
      this.jListThemes.setFixedCellHeight(45);
      this.jListThemes.setFixedCellWidth(80);
      this.jListThemes.setLayoutOrientation(1);
      this.jListThemes.setVisibleRowCount(VideoUtils.getSizeDisplay().height <= 800 ? 2 : 3);
      this.jListThemes.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            MusicMain.this.jListThemesMouseReleased(evt);
         }
      });
      this.jListThemes.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent evt) {
            MusicMain.this.jListThemesValueChanged(evt);
         }
      });
      this.jScrollPaneThemes.setViewportView(this.jListThemes);
      this.jPanelTheme.add(this.jScrollPaneThemes, "Center");
      GroupLayout jPanelRightFooterLayout = new GroupLayout(this.jPanelRightFooter);
      this.jPanelRightFooter.setLayout(jPanelRightFooterLayout);
      jPanelRightFooterLayout.setHorizontalGroup(
         jPanelRightFooterLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(this.jSeparator5)
            .addComponent(this.jSeparator8, Alignment.TRAILING)
            .addComponent(this.jPanelTheme, -1, -1, 32767)
      );
      jPanelRightFooterLayout.setVerticalGroup(
         jPanelRightFooterLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jPanelRightFooterLayout.createSequentialGroup()
                  .addGap(4, 4, 4)
                  .addComponent(this.jSeparator5, -2, -1, -2)
                  .addGap(0, 0, 0)
                  .addComponent(this.jSeparator8, -2, -1, -2)
                  .addGap(2, 2, 2)
                  .addComponent(this.jPanelTheme, -2, -1, -2)
            )
      );
      this.jPanelRight.add(this.jPanelRightFooter, "Last");
      this.jSplitPaneMainAndRightColumn.setRightComponent(this.jPanelRight);
      this.jPanelSplitRight.add(this.jSplitPaneMainAndRightColumn, "Center");
      this.jSplitPaneMusicsAndMain.setRightComponent(this.jPanelSplitRight);
      this.jPanelGeneral.add(this.jSplitPaneMusicsAndMain, "Center");
      this.jPanelFooter.setLayout(new BorderLayout());
      this.jPanelGeneral.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.jMenuFile.setText(bundle.getString("MusicMain.jMenuFile.text"));
      this.jMenuItemGoToBible.setAccelerator(KeyStroke.getKeyStroke(118, 0));
      this.jMenuItemGoToBible.setIcon(new ImageIcon(Img.resize(Images.load("bible26.png"), 16, 16)));
      this.jMenuItemGoToBible.setText(bundle.getString("MusicMain.jMenuItemGoToBible.text"));
      this.jMenuItemGoToBible.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemGoToBibleActionPerformed(evt);
         }
      });
      this.jMenuFile.add(this.jMenuItemGoToBible);
      this.jMenuFile.add(this.jSeparator2);
      this.jMenuItemFileFilesReceived.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/open16.png")));
      this.jMenuItemFileFilesReceived.setText(bundle.getString("msg.filesReceived"));
      this.jMenuItemFileFilesReceived.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemFileFilesReceivedActionPerformed(evt);
         }
      });
      this.jMenuFile.add(this.jMenuItemFileFilesReceived);
      this.jMenuItemLyricsDataBase.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/database16.png")));
      this.jMenuItemLyricsDataBase
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.lyricsDatabase")));
      this.jMenuItemLyricsDataBase.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemLyricsDataBaseActionPerformed(evt);
         }
      });
      this.jMenuFile.add(this.jMenuItemLyricsDataBase);
      this.jMenuItemFileSettings.setAccelerator(KeyStroke.getKeyStroke(67, 3));
      this.jMenuItemFileSettings.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/settings16.png")));
      this.jMenuItemFileSettings.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.settings")));
      this.jMenuItemFileSettings.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemFileSettingsActionPerformed(evt);
         }
      });
      this.jMenuFile.add(this.jMenuItemFileSettings);
      this.jMenuItemFileDisplaySettings.setAccelerator(KeyStroke.getKeyStroke(69, 3));
      this.jMenuItemFileDisplaySettings.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/desktop16.png")));
      this.jMenuItemFileDisplaySettings.setText(bundle.getString("msg.displaySettings"));
      this.jMenuFile.add(this.jMenuItemFileDisplaySettings);
      this.jMenuFile.add(this.jSeparator6);
      this.jMenuItemExit.setAccelerator(KeyStroke.getKeyStroke(115, 8));
      this.jMenuItemExit.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/close16.png")));
      this.jMenuItemExit.setText(bundle.getString("MusicMain.jMenuItemExit.text"));
      this.jMenuItemExit.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemExitActionPerformed(evt);
         }
      });
      this.jMenuFile.add(this.jMenuItemExit);
      this.jMenuBarMenu.add(this.jMenuFile);
      this.jMenuNew.setText(bundle.getString("MusicMain.jMenuNew.text"));
      this.jMenuItemNewMusic.setAccelerator(KeyStroke.getKeyStroke(49, 2));
      this.jMenuItemNewMusic.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/music16.png")));
      this.jMenuItemNewMusic.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.music")));
      this.jMenuItemNewMusic.setName("new;music");
      this.jMenuItemNewMusic.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuNew.add(this.jMenuItemNewMusic);
      this.jMenuItemNewTheme.setAccelerator(KeyStroke.getKeyStroke(50, 2));
      this.jMenuItemNewTheme.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/theme16.png")));
      this.jMenuItemNewTheme.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.theme")));
      this.jMenuItemNewTheme.setName("new;theme");
      this.jMenuItemNewTheme.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuNew.add(this.jMenuItemNewTheme);
      this.jMenuItemNewProfile.setAccelerator(KeyStroke.getKeyStroke(51, 2));
      this.jMenuItemNewProfile.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/profile16.png")));
      this.jMenuItemNewProfile.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.profile")));
      this.jMenuItemNewProfile.setName("new;profile");
      this.jMenuItemNewProfile.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuNew.add(this.jMenuItemNewProfile);
      this.jMenuItemNewWorship.setAccelerator(KeyStroke.getKeyStroke(52, 2));
      this.jMenuItemNewWorship.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/event16.png")));
      this.jMenuItemNewWorship.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.worshipEvent")));
      this.jMenuItemNewWorship.setName("new;worship");
      this.jMenuItemNewWorship.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuNew.add(this.jMenuItemNewWorship);
      this.jMenuItemNewEvent.setAccelerator(KeyStroke.getKeyStroke(53, 2));
      this.jMenuItemNewEvent.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/event16.png")));
      this.jMenuItemNewEvent.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.event")));
      this.jMenuItemNewEvent.setName("new;event");
      this.jMenuItemNewEvent.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuNew.add(this.jMenuItemNewEvent);
      this.jMenuItemNewMember.setAccelerator(KeyStroke.getKeyStroke(54, 2));
      this.jMenuItemNewMember.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/member16.png")));
      this.jMenuItemNewMember.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.member")));
      this.jMenuItemNewMember.setName("new;member");
      this.jMenuItemNewMember.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuNew.add(this.jMenuItemNewMember);
      this.jMenuBarMenu.add(this.jMenuNew);
      this.jMenuEdit.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.edit")));
      this.jMenuItemEditProfile.setAccelerator(KeyStroke.getKeyStroke(51, 2));
      this.jMenuItemEditProfile.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/profile16.png")));
      this.jMenuItemEditProfile.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.profiles")));
      this.jMenuItemEditProfile.setName("edit;profile");
      this.jMenuItemEditProfile.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuEdit.add(this.jMenuItemEditProfile);
      this.jMenuItemEditWorship.setAccelerator(KeyStroke.getKeyStroke(52, 2));
      this.jMenuItemEditWorship.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/event16.png")));
      this.jMenuItemEditWorship.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.worshipEvents")));
      this.jMenuItemEditWorship.setName("edit;worship");
      this.jMenuItemEditWorship.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuEdit.add(this.jMenuItemEditWorship);
      this.jMenuItemEditEvent.setAccelerator(KeyStroke.getKeyStroke(53, 2));
      this.jMenuItemEditEvent.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/event16.png")));
      this.jMenuItemEditEvent.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.events")));
      this.jMenuItemEditEvent.setName("edit;event");
      this.jMenuItemEditEvent.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuEdit.add(this.jMenuItemEditEvent);
      this.jMenuItemEditMember.setAccelerator(KeyStroke.getKeyStroke(54, 2));
      this.jMenuItemEditMember.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/member16.png")));
      this.jMenuItemEditMember.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.members")));
      this.jMenuItemEditMember.setName("edit;member");
      this.jMenuItemEditMember.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemNew(evt);
         }
      });
      this.jMenuEdit.add(this.jMenuItemEditMember);
      this.jMenuBarMenu.add(this.jMenuEdit);
      this.jMenuMusic.setText(bundle.getString("word.music"));
      this.jMenuItemWebSearch.setAccelerator(KeyStroke.getKeyStroke(87, 3));
      this.jMenuItemWebSearch.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/web16.png")));
      this.jMenuItemWebSearch.setText(bundle.getString("MusicMain.jMenuItemWebSearch.text"));
      this.jMenuItemWebSearch.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemWebSearchActionPerformed(evt);
         }
      });
      this.jMenuMusic.add(this.jMenuItemWebSearch);
      this.jMenuItemDatabaseSearch.setAccelerator(KeyStroke.getKeyStroke(68, 3));
      this.jMenuItemDatabaseSearch.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/database16.png")));
      this.jMenuItemDatabaseSearch
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.databaseSearch")));
      this.jMenuItemDatabaseSearch.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemDatabaseSearchActionPerformed(evt);
         }
      });
      this.jMenuMusic.add(this.jMenuItemDatabaseSearch);
      this.jMenuMusic.add(this.jSeparator11);
      this.jMenuItemMusicSavePresentationAs.setAccelerator(KeyStroke.getKeyStroke(83, 3));
      this.jMenuItemMusicSavePresentationAs.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/saveBlack16.png")));
      this.jMenuItemMusicSavePresentationAs.setText(bundle.getString("msg.savePresentationAs___"));
      this.jMenuItemMusicSavePresentationAs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicSavePresentationAsActionPerformed(evt);
         }
      });
      this.jMenuMusic.add(this.jMenuItemMusicSavePresentationAs);
      this.jMenuMusic.add(this.jSeparator7);
      this.jMenuMusicBatchImport.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/importExport16.png")));
      this.jMenuMusicBatchImport.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.import")));
      this.jMenuMusicImportFile.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/music16.png")));
      this.jMenuMusicImportFile.setText("Music File (*.muf)");
      this.jMenuItemMusicImportFileOne.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/file16.png")));
      this.jMenuItemMusicImportFileOne
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.singleFile")));
      this.jMenuItemMusicImportFileOne.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicImportFileOneActionPerformed(evt);
         }
      });
      this.jMenuMusicImportFile.add(this.jMenuItemMusicImportFileOne);
      this.jMenuItemMusicImportFileBatch.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/list16.png")));
      this.jMenuItemMusicImportFileBatch.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.batch")));
      this.jMenuItemMusicImportFileBatch.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicImportFileBatchActionPerformed(evt);
         }
      });
      this.jMenuMusicImportFile.add(this.jMenuItemMusicImportFileBatch);
      this.jMenuMusicBatchImport.add((JMenuItem)this.jMenuMusicImportFile);
      this.jMenuItemMusicBathImportFromDBO.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/database16.png")));
      this.jMenuItemMusicBathImportFromDBO
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.fromLyricsDatabase")));
      this.jMenuItemMusicBathImportFromDBO.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicBathImportFromDBOActionPerformed(evt);
         }
      });
      this.jMenuMusicBatchImport.add(this.jMenuItemMusicBathImportFromDBO);
      this.jMenuMusicImportPP.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/powerPointBlack16.png")));
      this.jMenuMusicImportPP.setText(bundle.getString("MusicMain.jMenuMusicImportPP.text"));
      this.jMenuItemMusicImportPPManyInOne.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/oneToMany16.png")));
      this.jMenuItemMusicImportPPManyInOne.setText(bundle.getString("MusicMain.jMenuItemMusicImportPPManyInOne.text"));
      this.jMenuItemMusicImportPPManyInOne.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicImportPPManyInOneActionPerformed(evt);
         }
      });
      this.jMenuMusicImportPP.add(this.jMenuItemMusicImportPPManyInOne);
      this.jMenuItemMusicImportPPOneInOne.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/oneToOne16.png")));
      this.jMenuItemMusicImportPPOneInOne
         .setText(
            MessageFormat.format(
               ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("MusicMain.jMenuItemjMenuItemMusicImportPPOneInOne.text")
            )
         );
      this.jMenuItemMusicImportPPOneInOne.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicImportPPOneInOneActionPerformed(evt);
         }
      });
      this.jMenuMusicImportPP.add(this.jMenuItemMusicImportPPOneInOne);
      this.jMenuMusicBatchImport.add((JMenuItem)this.jMenuMusicImportPP);
      this.jMenuMusicImportOthers.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/importExport16.png")));
      this.jMenuMusicImportOthers.setText(bundle.getString("word.others"));
      this.jMenuItemMusicBathImportFromEasiSlides.setText("EasiSlides (*.xml)");
      this.jMenuItemMusicBathImportFromEasiSlides.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicBathImportFromEasiSlidesActionPerformed(evt);
         }
      });
      this.jMenuMusicImportOthers.add(this.jMenuItemMusicBathImportFromEasiSlides);
      this.jMenuItemMusicBathImportFromOpenSong.setText("OpenSong (*.xml)");
      this.jMenuItemMusicBathImportFromOpenSong.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicBathImportFromOpenSongActionPerformed(evt);
         }
      });
      this.jMenuMusicImportOthers.add(this.jMenuItemMusicBathImportFromOpenSong);
      this.jMenuItemMusicBathImportFromOpenLP.setText("OpenLP (*.xml)");
      this.jMenuItemMusicBathImportFromOpenLP.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicBathImportFromOpenLPActionPerformed(evt);
         }
      });
      this.jMenuMusicImportOthers.add(this.jMenuItemMusicBathImportFromOpenLP);
      this.jMenuItemMusicBathImportFromCCLI_USR.setText("CCLI (*.usr)");
      this.jMenuItemMusicBathImportFromCCLI_USR.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicBathImportFromCCLI_USRActionPerformed(evt);
         }
      });
      this.jMenuMusicImportOthers.add(this.jMenuItemMusicBathImportFromCCLI_USR);
      this.jMenuItemMusicBathImportFromWord.setText("Word (*.doc, *.docx)");
      this.jMenuItemMusicBathImportFromWord.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicBathImportFromWordActionPerformed(evt);
         }
      });
      this.jMenuMusicImportOthers.add(this.jMenuItemMusicBathImportFromWord);
      this.jMenuMusicBatchImport.add((JMenuItem)this.jMenuMusicImportOthers);
      this.jMenuMusic.add((JMenuItem)this.jMenuMusicBatchImport);
      this.jMenuMusicExport.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/importExport16.png")));
      this.jMenuMusicExport.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.export")));
      this.jMenuFileSaveMusic.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/music16.png")));
      this.jMenuFileSaveMusic.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.currentMusic")));
      this.jMenuFileSaveMusic.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuFileSaveMusicActionPerformed(evt);
         }
      });
      this.jMenuMusicExport.add(this.jMenuFileSaveMusic);
      this.jMenuItemMusicBathExport.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/list16.png")));
      this.jMenuItemMusicBathExport.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.batchExport")));
      this.jMenuItemMusicBathExport.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicBathExportActionPerformed(evt);
         }
      });
      this.jMenuMusicExport.add(this.jMenuItemMusicBathExport);
      this.jMenuMusic.add((JMenuItem)this.jMenuMusicExport);
      this.jMenuMusic.add(this.jSeparator10);
      this.jMenuItemMusicBatchDelete.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/delete16.png")));
      this.jMenuItemMusicBatchDelete.setText(bundle.getString("MusicMain.jMenuItemMusicBatchDelete.text"));
      this.jMenuItemMusicBatchDelete.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemMusicBatchDeleteActionPerformed(evt);
         }
      });
      this.jMenuMusic.add(this.jMenuItemMusicBatchDelete);
      this.jMenuBarMenu.add(this.jMenuMusic);
      this.jMenuTheme.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.theme")));
      this.jMenuFileThemeImport.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/open16.png")));
      this.jMenuFileThemeImport.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.open")));
      this.jMenuFileThemeImport.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuFileThemeImportActionPerformed(evt);
         }
      });
      this.jMenuTheme.add(this.jMenuFileThemeImport);
      this.jMenuFileThemeExport.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/saveBlack16.png")));
      this.jMenuFileThemeExport
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.saveCurrentTheme")));
      this.jMenuFileThemeExport.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuFileThemeExportActionPerformed(evt);
         }
      });
      this.jMenuTheme.add(this.jMenuFileThemeExport);
      this.jMenuBarMenu.add(this.jMenuTheme);
      this.jMenuAnnouncement.setText(bundle.getString("word.announcement"));
      this.jMenuItemAnnouncementEdit.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/edit16.png")));
      this.jMenuItemAnnouncementEdit.setText(bundle.getString("msg.createOrEdit"));
      this.jMenuItemAnnouncementEdit.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemAnnouncementEditActionPerformed(evt);
         }
      });
      this.jMenuAnnouncement.add(this.jMenuItemAnnouncementEdit);
      this.jMenuItemAnnouncementShow.setAccelerator(KeyStroke.getKeyStroke(65, 3));
      this.jMenuItemAnnouncementShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/slideshow16.png")));
      this.jMenuItemAnnouncementShow.setText(bundle.getString("word.show"));
      this.jMenuItemAnnouncementShow.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemAnnouncementShowActionPerformed(evt);
         }
      });
      this.jMenuAnnouncement.add(this.jMenuItemAnnouncementShow);
      this.jMenuBarMenu.add(this.jMenuAnnouncement);
      this.jMenuSchedule.setText(bundle.getString("MusicMain.jMenuSchedule.text"));
      this.jMenuItemScheduleViewEdit.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/schedule16.png")));
      this.jMenuItemScheduleViewEdit.setText(bundle.getString("MusicMain.jMenuItemScheduleViewEdit.text"));
      this.jMenuItemScheduleViewEdit.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemScheduleViewEditActionPerformed(evt);
         }
      });
      this.jMenuSchedule.add(this.jMenuItemScheduleViewEdit);
      this.jMenuBarMenu.add(this.jMenuSchedule);
      this.jMenuReport.setText(bundle.getString("MusicMain.jMenuReport.text"));
      this.jMenuItemReportPlayedMusic.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playedMusic16.png")));
      this.jMenuItemReportPlayedMusic.setText(bundle.getString("MusicMain.jMenuItemReportPlayedMusic.text"));
      this.jMenuItemReportPlayedMusic.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemReportPlayedMusicActionPerformed(evt);
         }
      });
      this.jMenuReport.add(this.jMenuItemReportPlayedMusic);
      this.jMenuItemReportSchedule.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/schedule16.png")));
      this.jMenuItemReportSchedule.setText(bundle.getString("MusicMain.jMenuItemReportSchedule.text"));
      this.jMenuItemReportSchedule.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemReportScheduleActionPerformed(evt);
         }
      });
      this.jMenuReport.add(this.jMenuItemReportSchedule);
      this.jMenuBarMenu.add(this.jMenuReport);
      this.jMenuTools.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.tools")));
      this.jMenuItemImageFileShow.setAccelerator(KeyStroke.getKeyStroke(73, 3));
      this.jMenuItemImageFileShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/theme16.png")));
      this.jMenuItemImageFileShow.setText(bundle.getString("msg.imagePresentation"));
      this.jMenuItemImageFileShow.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemImageFileShowActionPerformed(evt);
         }
      });
      this.jMenuTools.add(this.jMenuItemImageFileShow);
      this.jMenuItemCreateImage.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/image16.png")));
      this.jMenuItemCreateImage.setText(bundle.getString("MusicMain.jMenuItemCreateImage.text"));
      this.jMenuItemCreateImage.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemCreateImageActionPerformed(evt);
         }
      });
      this.jMenuTools.add(this.jMenuItemCreateImage);
      this.jMenuItemCountdown.setAccelerator(KeyStroke.getKeyStroke(82, 3));
      this.jMenuItemCountdown.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/timer16.png")));
      this.jMenuItemCountdown.setText(bundle.getString("MusicMain.jMenuItemCountdown.text"));
      this.jMenuItemCountdown.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemCountdownActionPerformed(evt);
         }
      });
      this.jMenuTools.add(this.jMenuItemCountdown);
      this.jMenuItemToolsAutomaticShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/musicVideo16.png")));
      this.jMenuItemToolsAutomaticShow.setText(bundle.getString("msg.automaticShow"));
      this.jMenuItemToolsAutomaticShow.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemToolsAutomaticShowActionPerformed(evt);
         }
      });
      this.jMenuTools.add(this.jMenuItemToolsAutomaticShow);
      this.jCheckBoxMenuItemFreezeScreen.setAccelerator(KeyStroke.getKeyStroke(70, 3));
      this.jCheckBoxMenuItemFreezeScreen.setText(bundle.getString("MusicMain.jCheckBoxMenuItemFreezeScreen.text"));
      this.jCheckBoxMenuItemFreezeScreen.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/freeze16.png")));
      this.jCheckBoxMenuItemFreezeScreen.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jCheckBoxMenuItemFreezeScreenActionPerformed(evt);
         }
      });
      this.jMenuTools.add((JMenuItem)this.jCheckBoxMenuItemFreezeScreen);
      this.jMenuExportToPDF.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/pdf16.png")));
      this.jMenuExportToPDF.setText(bundle.getString("MusicMain.jMenuExportToPDF.text"));
      this.jMenuTools.add((JMenuItem)this.jMenuExportToPDF);
      this.jMenuTools.add(this.jSeparator9);
      this.jMenuItemCreateDesktopShortcur.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/desktop16.png")));
      this.jMenuItemCreateDesktopShortcur
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.createDesktopShortcut")));
      this.jMenuItemCreateDesktopShortcur.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemCreateDesktopShortcurActionPerformed(evt);
         }
      });
      this.jMenuTools.add(this.jMenuItemCreateDesktopShortcur);
      this.jMenuTools.add(this.jSeparator4);
      this.jMenuItemPluginHolyrics.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/lightbulb16.png")));
      this.jMenuItemPluginHolyrics.setText("Plugin Holyrics");
      this.jMenuItemPluginHolyrics.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemPluginHolyricsActionPerformed(evt);
         }
      });
      this.jMenuTools.add(this.jMenuItemPluginHolyrics);
      this.jMenuItemChangeWebServiceIP.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/remoteControl16.png")));
      this.jMenuItemChangeWebServiceIP.setText(bundle.getString("MusicMain.jMenuItemChangeWebServiceIP.text"));
      this.jMenuItemChangeWebServiceIP.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemChangeWebServiceIPActionPerformed(evt);
         }
      });
      this.jMenuTools.add(this.jMenuItemChangeWebServiceIP);
      this.jMenuBackup.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/backup16.png")));
      this.jMenuBackup.setText(bundle.getString("word.backup"));
      this.jMenuItemImport.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/importExport16.png")));
      this.jMenuItemImport.setText(bundle.getString("word.import"));
      this.jMenuItemImport.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemImportActionPerformed(evt);
         }
      });
      this.jMenuBackup.add(this.jMenuItemImport);
      this.jMenuItemExport.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/importExport16.png")));
      this.jMenuItemExport.setText(bundle.getString("word.export"));
      this.jMenuItemExport.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemExportActionPerformed(evt);
         }
      });
      this.jMenuBackup.add(this.jMenuItemExport);
      this.jMenuTools.add((JMenuItem)this.jMenuBackup);
      this.jMenuBarMenu.add(this.jMenuTools);
      this.jMenuWindows.setText(bundle.getString("MusicMain.jMenuWindows.text"));
      this.jCheckBoxMenuItemWindowsFavorites.setAccelerator(KeyStroke.getKeyStroke(66, 3));
      this.jCheckBoxMenuItemWindowsFavorites.setSelected(true);
      this.jCheckBoxMenuItemWindowsFavorites.setText(bundle.getString("word.favorites"));
      this.jCheckBoxMenuItemWindowsFavorites.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/favorite16.png")));
      this.jCheckBoxMenuItemWindowsFavorites.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jCheckBoxMenuItemWindowsFavoritesActionPerformed(evt);
         }
      });
      this.jMenuWindows.add((JMenuItem)this.jCheckBoxMenuItemWindowsFavorites);
      this.jMenuItemWindowsCommunicationPanel.setAccelerator(KeyStroke.getKeyStroke(84, 3));
      this.jMenuItemWindowsCommunicationPanel.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/member16.png")));
      this.jMenuItemWindowsCommunicationPanel.setText(bundle.getString("msg.communicationPanel"));
      this.jMenuItemWindowsCommunicationPanel.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemWindowsCommunicationPanelActionPerformed(evt);
         }
      });
      this.jMenuWindows.add(this.jMenuItemWindowsCommunicationPanel);
      this.jMenuItem1.setAccelerator(KeyStroke.getKeyStroke(89, 3));
      this.jMenuItem1.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/message16.png")));
      this.jMenuItem1.setText("Chat");
      this.jMenuItem1.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItem1ActionPerformed(evt);
         }
      });
      this.jMenuWindows.add(this.jMenuItem1);
      this.jMenuItemWindowsLive.setAccelerator(KeyStroke.getKeyStroke(76, 3));
      this.jMenuItemWindowsLive.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/desktop16.png")));
      this.jMenuItemWindowsLive.setText(bundle.getString("word.live"));
      this.jMenuItemWindowsLive.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemWindowsLiveActionPerformed(evt);
         }
      });
      this.jMenuWindows.add(this.jMenuItemWindowsLive);
      this.jMenuBarMenu.add(this.jMenuWindows);
      this.jMenuShare.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.share")));
      this.jMenuItemShareInTheWeb.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/web16.png")));
      this.jMenuItemShareInTheWeb.setText(bundle.getString("MusicMain.jMenuItemShareInTheWeb.text"));
      this.jMenuItemShareInTheWeb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemShareInTheWebActionPerformed(evt);
         }
      });
      this.jMenuShare.add(this.jMenuItemShareInTheWeb);
      this.jMenuItemReceiveUpdateFromMobile.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/download16.png")));
      this.jMenuItemReceiveUpdateFromMobile
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.receiveUpdateFromMobile")));
      this.jMenuItemReceiveUpdateFromMobile.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemReceiveUpdateFromMobileActionPerformed(evt);
         }
      });
      this.jMenuShare.add(this.jMenuItemReceiveUpdateFromMobile);
      this.jMenuItemReceiveUpdateFromInternet.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/download16.png")));
      this.jMenuItemReceiveUpdateFromInternet.setText(bundle.getString("MusicMain.jMenuItemReceiveUpdateFromInternet.text"));
      this.jMenuItemReceiveUpdateFromInternet.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemReceiveUpdateFromInternetActionPerformed(evt);
         }
      });
      this.jMenuShare.add(this.jMenuItemReceiveUpdateFromInternet);
      this.jCheckBoxCheckChangesOnExit.setSelected(Settings.EnumSettings.SYNC_CHECK_UPLOAD.is());
      this.jCheckBoxCheckChangesOnExit.setText(bundle.getString("MusicMain.jCheckBoxCheckChangesOnExit.text"));
      this.jCheckBoxCheckChangesOnExit.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/exit16.png")));
      this.jCheckBoxCheckChangesOnExit.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jCheckBoxCheckChangesOnExitActionPerformed(evt);
         }
      });
      this.jMenuShare.add((JMenuItem)this.jCheckBoxCheckChangesOnExit);
      this.jMenuBarMenu.add(this.jMenuShare);
      this.jMenuHelp.setText(bundle.getString("MusicMain.jMenuHelp.text"));
      this.jMenuItemHelp.setAccelerator(KeyStroke.getKeyStroke(112, 0));
      this.jMenuItemHelp.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/help16.png")));
      this.jMenuItemHelp.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.help")));
      this.jMenuItemHelp.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemHelpActionPerformed(evt);
         }
      });
      this.jMenuHelp.add(this.jMenuItemHelp);
      this.jMenuItemTutorialYouTube.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/youtube.png")));
      this.jMenuItemTutorialYouTube.setText("Tutorial YouTube");
      this.jMenuItemTutorialYouTube.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemTutorialYouTubeActionPerformed(evt);
         }
      });
      this.jMenuHelp.add(this.jMenuItemTutorialYouTube);
      this.jMenuItemSupportAndFeedBack.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/contact16.png")));
      this.jMenuItemSupportAndFeedBack.setText(bundle.getString("msg.supportAndFeedBack"));
      this.jMenuItemSupportAndFeedBack.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemSupportAndFeedBackActionPerformed(evt);
         }
      });
      this.jMenuHelp.add(this.jMenuItemSupportAndFeedBack);
      this.jMenuItemCheckUpdates.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/update16.png")));
      this.jMenuItemCheckUpdates.setText(bundle.getString("msg.checkUpdates"));
      this.jMenuItemCheckUpdates.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemCheckUpdatesActionPerformed(evt);
         }
      });
      this.jMenuHelp.add(this.jMenuItemCheckUpdates);
      this.jMenuItemDownloadVLCMediaPlayer.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/download16.png")));
      this.jMenuItemDownloadVLCMediaPlayer.setText(bundle.getString("MusicMain.jMenuItemDownloadVLCMediaPlayer.text"));
      this.jMenuItemDownloadVLCMediaPlayer.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemDownloadVLCMediaPlayerActionPerformed(evt);
         }
      });
      this.jMenuHelp.add(this.jMenuItemDownloadVLCMediaPlayer);
      this.jMenuLanguage.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/language16.png")));
      this.jMenuLanguage.setText(bundle.getString("word.language"));
      this.jMenuHelp.add((JMenuItem)this.jMenuLanguage);
      this.jMenuHelpCompatibility.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/reportAlert16.png")));
      this.jMenuHelpCompatibility.setText(bundle.getString("MusicMain.jMenuHelpCompatibility.text"));
      this.jCheckBoxMenuItemUseJavaFXVideo.setSelected(MyProperties.EnumProperties.FORCE_FX_VIDEO_THEME.is());
      this.jCheckBoxMenuItemUseJavaFXVideo.setText(bundle.getString("MusicMain.jCheckBoxMenuItemUseJavaFXVideo.text"));
      this.jCheckBoxMenuItemUseJavaFXVideo.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/slideshow16.png")));
      this.jCheckBoxMenuItemUseJavaFXVideo.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jCheckBoxMenuItemUseJavaFXVideoActionPerformed(evt);
         }
      });
      this.jMenuHelpCompatibility.add((JMenuItem)this.jCheckBoxMenuItemUseJavaFXVideo);
      this.jMenuHelp.add((JMenuItem)this.jMenuHelpCompatibility);
      this.jMenuHelp.add(this.jSeparator1);
      this.jMenuItemAbout.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/holyricsIconBlack16.png")));
      this.jMenuItemAbout.setText(bundle.getString("word.about"));
      this.jMenuItemAbout.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            MusicMain.this.jMenuItemAboutActionPerformed(evt);
         }
      });
      this.jMenuHelp.add(this.jMenuItemAbout);
      this.jMenuBarMenu.add(this.jMenuHelp);
      this.setJMenuBar(this.jMenuBarMenu);
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jMenuItemExitActionPerformed(ActionEvent evt) {
      this.exit();
   }

   private void jListOrderValueChanged(ListSelectionEvent evt) {
      if (!evt.getValueIsAdjusting()) {
         if (this.jListOrder.getSelectedValue() == null) {
            this.jListOrder.setSelectedIndex(this.jListOrder.getMaxSelectionIndex() >= evt.getLastIndex() ? evt.getLastIndex() : evt.getFirstIndex());
         }

         this.refreshSlideExample();
      }
   }

   private void jListThemesValueChanged(ListSelectionEvent evt) {
      if (!evt.getValueIsAdjusting()) {
         if (this.jListThemes.getSelectedValue() == null) {
            this.jListThemes.setSelectedIndex(evt.getLastIndex());
         } else {
            Theme theme = this.jListThemes.getSelectedValue();
            if (theme != null) {
               Settings.SET.setTheme(theme);
               if (!this.switchSaveThemeToMusic.isSelected()) {
                  this.jComboBoxThemeDefault.setSelectedItem(Settings.SET.getTheme());
                  this.jComboBoxThemeDefault.repaint();
               }

               this.updateThemePreview();
            }
         }
      }
   }

   private void formWindowClosing(WindowEvent evt) {
      this.exit();
   }

   private void jButtonAddPlayListActionPerformed(ActionEvent evt) {
      this.addPlayListMusic();
   }

   private void jButtonAddHistoryActionPerformed(ActionEvent evt) {
      this.playedMusic();
   }

   private void jButtonEditMusicActionPerformed(ActionEvent evt) {
      this.editMusic();
   }

   private void jButtonDeleteMusicActionPerformed(ActionEvent evt) {
      this.removeMusic();
   }

   private void jButtonShowBeginningActionPerformed(ActionEvent evt) {
      this.showFromBegin();
   }

   private void jButtonShowCurrentSlideActionPerformed(ActionEvent evt) {
      this.showFromCurrentSlide();
   }

   private void jComboBoxScreenTypeItemStateChanged(ItemEvent evt) {
      if (evt.getStateChange() == 1) {
         this.slidePreview.setScreenType((PresentationSlideView.EnumScreenType)evt.getItem());
         this.jTextPaneLyrics.repaint();
      }
   }

   private void jMenuItemScheduleViewEditActionPerformed(ActionEvent evt) {
      ScheduleView.main(this::update);
   }

   private void jComboBoxPlayListTypeItemStateChanged(ItemEvent evt) {
      if (evt.getStateChange() == 1) {
         this.changePlayListType((EnumPlayList)evt.getItem());
      }
   }

   private void jSpinnerEventDateStateChanged(ChangeEvent evt) {
      this.changeDateListEvent((Date)this.jSpinnerEventDate.getValue());
   }

   private void jComboBoxEventItemStateChanged(ItemEvent evt) {
      if (evt.getStateChange() == 1) {
         this.schedulePane.setEvent((Event)this.jComboBoxEvent.getSelectedItem());
      } else if (this.jComboBoxEvent.getSelectedItem() == null) {
         this.schedulePane.setEvent(null);
      }
   }

   private void jListPlayListMouseReleased(MouseEvent evt) {
      if (!this.jListPlayList.getSelectedValuesList().isEmpty()
         && Utils.isDoubleClick(evt)
         && SwingUtils.pointOnTheSelectedItem(this.jListPlayList, evt.getPoint())) {
         this.showMusic(this.jListPlayList.getSelectedValuesList());
      } else if (this.jListPlayList.getSelectedValue() != null && this.jListPlayList.locationToIndex(evt.getPoint()) == this.jListPlayList.getSelectedIndex()) {
         this.musicChanged(this.jListPlayList.getSelectedValue());
      }
   }

   private void jMenuItemImportActionPerformed(ActionEvent evt) {
      Backup.importBackup(this);
   }

   private void jMenuItemExportActionPerformed(ActionEvent evt) {
      Backup.exportBackup(this);
   }

   private void jMenuItemCheckUpdatesActionPerformed(ActionEvent evt) {
      Update.INSTANCE.update.check(this, true);
   }

   private void jMenuItemCreateDesktopShortcurActionPerformed(ActionEvent evt) {
      SystemUtils.createDesktopIcon(this);
   }

   private void jMenuItemAboutActionPerformed(ActionEvent evt) {
      AboutView.main(this);
   }

   private void jMenuItemMusicBathImportFromDBOActionPerformed(ActionEvent evt) {
      DBOffLineBatchImportView.main(this, this::update);
   }

   private void jMenuItemMusicBathExportActionPerformed(ActionEvent evt) {
      MusicBatchExport.main(this);
   }

   private void jButtonGoToBibleActionPerformed(ActionEvent evt) {
      this.goToBible();
   }

   private void jMenuItemGoToBibleActionPerformed(ActionEvent evt) {
      this.goToBible();
   }

   private void jMenuItemWebSearchActionPerformed(ActionEvent evt) {
      this.musicWebSearch();
   }

   private void jMenuItemShareInTheWebActionPerformed(ActionEvent evt) {
      ShareView.main(this, null);
   }

   private void jCheckBoxCheckChangesOnExitActionPerformed(ActionEvent evt) {
      Settings.EnumSettings.SYNC_CHECK_UPLOAD.set(this.jCheckBoxCheckChangesOnExit.isSelected());
   }

   private void jMenuItemReportPlayedMusicActionPerformed(ActionEvent evt) {
      ReportPlayedMusicsView.main();
   }

   private void jMenuItemReportScheduleActionPerformed(ActionEvent evt) {
      ReportScheduleView.main();
   }

   private void jMenuItemHelpActionPerformed(ActionEvent evt) {
      HelpView.main(this, EnumHelpPages.HOME);
   }

   private void jMenuItemNew(ActionEvent evt) {
      this.menuItemNewEdit(((JMenuItem)evt.getSource()).getName());
   }

   private void jMenuItemDatabaseSearchActionPerformed(ActionEvent evt) {
      MusicSearchDBOffLineView.main(this::update);
   }

   private void jButtonNewThemeActionPerformed(ActionEvent evt) {
      this.newTheme(Theme.create());
   }

   private void jMenuFileSaveMusicActionPerformed(ActionEvent evt) {
      Settings.SET.getMusic().save(this);
   }

   private void jMenuFileThemeExportActionPerformed(ActionEvent evt) {
      ITheme.save(Settings.SET.getTheme(), this);
   }

   private void jMenuItemMusicImportFileOneActionPerformed(ActionEvent evt) {
      Optional.ofNullable(LGSaveOpen.open(this, Music.class, Music.BLANK)).ifPresent(this::newMusic);
   }

   private void jMenuFileThemeImportActionPerformed(ActionEvent evt) {
      Optional.ofNullable(LGSaveOpen.open(this, Theme.class, Theme.BLACK_SCREEN)).ifPresent(this::newTheme);
   }

   private void jMenuItemFileSettingsActionPerformed(ActionEvent evt) {
      SettingsView.main(this::update);
   }

   private void jMenuItemMusicImportFileBatchActionPerformed(ActionEvent evt) {
      BatchImportWorker.main(this, this::update);
   }

   private void jMenuItemLyricsDataBaseActionPerformed(ActionEvent evt) {
      DBOffLineBatchImportView.main(this, this::update);
   }

   private void jListThemesMouseReleased(MouseEvent evt) {
      BooleanOptional.runIfTrue(Utils.isDoubleClick(evt), () -> this.editTheme(Settings.SET.getTheme()));
   }

   private void jMenuItemMusicImportPPManyInOneActionPerformed(ActionEvent evt) {
      ImportMusic.fromPowerPointManyInOne(this, this::update);
   }

   private void jMenuItemMusicImportPPOneInOneActionPerformed(ActionEvent evt) {
      ImportMusic.fromPowerPointOneInOne(this, this::update);
   }

   private void jMenuItemSupportAndFeedBackActionPerformed(ActionEvent evt) {
      SupportAndSuggestionsDialog.main(this);
   }

   private void jMenuItemAnnouncementEditActionPerformed(ActionEvent evt) {
      AnnouncementView.main(this, this::refreshAnnouncementMenuBar);
   }

   private void jMenuItemAnnouncementShowActionPerformed(ActionEvent evt) {
      AnnouncementShowDialog.main(this);
   }

   private void jMenuItemReceiveUpdateFromMobileActionPerformed(ActionEvent evt) {
      ReceiveUpdateFromMobileView.start();
   }

   private void jListPlayListKeyReleased(KeyEvent evt) {
      if (!evt.isShiftDown() && evt.getKeyCode() == 127) {
         this.removeSelectedMusicsFromPlayList(this.jListPlayList);
      }
   }

   private void jButtonPlayListShowAllActionPerformed(ActionEvent evt) {
      this.showMusic(Collections.list(this.listModelPlayList.elements()));
   }

   private void jButtonPlayListShowSelectionActionPerformed(ActionEvent evt) {
      this.showMusic(this.jListPlayList.getSelectedValuesList());
   }

   private void jTextPaneLyricsMouseReleased(MouseEvent evt) {
      BooleanOptional.runIfTrue(Utils.isDoubleClick(evt), this::editMusic);
   }

   private void jMenuItemMusicSavePresentationAsActionPerformed(ActionEvent evt) {
      MusicSaveAsDialog.main(this, Settings.SET.getMusic(), this.getThemeToMusic());
   }

   private void jToggleButtonUppercaseItemStateChanged(ItemEvent evt) {
      this.setUppercase(evt.getStateChange() == 1);
   }

   private void jButtonQuickPresentationActionPerformed(ActionEvent evt) {
      QuickPresentationDialog.main(this);
   }

   private void jMenuItemMusicBathImportFromEasiSlidesActionPerformed(ActionEvent evt) {
      MusicImportWorker.main(this, ImportMusic.Type.EASISLIDES_XML, this::update);
   }

   private void jMenuItemMusicBathImportFromOpenSongActionPerformed(ActionEvent evt) {
      MusicImportWorker.main(this, ImportMusic.Type.OPENSONG_XML, this::update);
   }

   private void jMenuItemMusicBathImportFromCCLI_USRActionPerformed(ActionEvent evt) {
      MusicImportWorker.main(this, ImportMusic.Type.CCLI_USR, this::update);
   }

   private void jMenuItemImageFileShowActionPerformed(ActionEvent evt) {
      ImageFileShowDialog.main(this);
   }

   private void jMenuItemTutorialYouTubeActionPerformed(ActionEvent evt) {
      Utils.openURL("https://www.youtube.com/watch?v=QZ-M3Ghjp8I&list=PL6NYsQBmGq-l-4_8t_XMBc9w9aATXpk0G");
   }

   private void jToggleButtonTitleUnderlineItemStateChanged(ItemEvent evt) {
      Settings.EnumSettings2.ARTIST_UNDERLINE.set(evt.getStateChange() == 1);
   }

   private void jMenuItemCreateImageActionPerformed(ActionEvent evt) {
      CreateImageDialog.main(this);
   }

   private void jToggleButtonBreakLineItemStateChanged(ItemEvent evt) {
      this.setBreakLine(evt.getStateChange() == 1);
   }

   private void jMenuItemChangeWebServiceIPActionPerformed(ActionEvent evt) {
      WebServiceUtils.popupChangeIP();
   }

   private void jListMediaMouseReleased(MouseEvent evt) {
      IHItem hitem = this.jListMedia.getSelectedValue();
      if (hitem != null) {
         if (Utils.isDoubleClick(evt) && SwingUtils.pointOnTheSelectedItem(this.jListMedia, evt.getPoint())) {
            hitem.favoriteAction(true);
         } else if (SwingUtilities.isLeftMouseButton(evt) && hitem instanceof HItemMusic) {
            Music m = ((HItemMusic)hitem).getMusic();
            if (m != null) {
               this.musicChanged(m);
            }
         }
      }
   }

   private void jListMediaKeyReleased(KeyEvent evt) {
      if (!evt.isShiftDown() && evt.getKeyCode() == 127) {
         SwingUtils.removeSelectedIndices(this.jListMedia, true);
      }
   }

   private void jMenuItemMusicBathImportFromOpenLPActionPerformed(ActionEvent evt) {
      MusicImportWorker.main(this, ImportMusic.Type.OPENLP_XML, this::update);
   }

   private void jToggleButtonCustomFormattingItemStateChanged(ItemEvent evt) {
      this.setCustomFormatting(evt.getStateChange() == 1);
   }

   private void jMenuItemCountdownActionPerformed(ActionEvent evt) {
      CountdownDialog.main();
   }

   private void jMenuItemDownloadVLCMediaPlayerActionPerformed(ActionEvent evt) {
      VLCDownloadWorker.download();
   }

   private void jMenuItemWindowsCommunicationPanelActionPerformed(ActionEvent evt) {
      CommunicationPanel.open();
   }

   private void jMenuItemWindowsLiveActionPerformed(ActionEvent evt) {
      LiveView.open();
   }

   private void jCheckBoxMenuItemWindowsFavoritesActionPerformed(ActionEvent evt) {
      this.favoritePanel.setVisible(!this.favoritePanel.isVisible());
   }

   private void jCheckBoxMenuItemUseJavaFXVideoActionPerformed(ActionEvent evt) {
      PresentationJDialog.FORCE_FX_THEME_VIDEO = this.jCheckBoxMenuItemUseJavaFXVideo.isSelected();
      MyProperties.EnumProperties.FORCE_FX_VIDEO_THEME.set(PresentationJDialog.FORCE_FX_THEME_VIDEO);
   }

   private void jLabelThemeMouseReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt)) {
         Dev.showPropInc();
      }
   }

   private void jMenuItemFileFilesReceivedActionPerformed(ActionEvent evt) {
      Utils.openAppFilesReceivedFolder();
   }

   private void jMenuItemMusicBatchDeleteActionPerformed(ActionEvent evt) {
      MusicBatchDelete.main(this, this.musicSearchPanel::filter);
   }

   private void jMenuItemToolsAutomaticShowActionPerformed(ActionEvent evt) {
      PlayBackUtils.openTab(this);
   }

   private void jCheckBoxMenuItemFreezeScreenActionPerformed(ActionEvent evt) {
      Freeze.freeze();
   }

   private void jMenuItemReceiveUpdateFromInternetActionPerformed(ActionEvent evt) {
      ReceiveUpdateFromInternetDialog.main(this);
   }

   private void jMenuItem1ActionPerformed(ActionEvent evt) {
      CommunicationPanel.openChat();
   }

   private void jMenuItemPluginHolyricsActionPerformed(ActionEvent evt) {
      Holyrics.openPluginNewThread();
   }

   private void jMenuItemMusicBathImportFromWordActionPerformed(ActionEvent evt) {
      ImportMusic.fromWordOneInOne(this, this::update);
   }

   private void jButtonReorderActionPerformed(ActionEvent evt) {
      ThemeReorderDialog.main(this, () -> SwingUtilities.invokeLater(() -> {
            this.listModelTheme.update();
            this.jListThemes.setSelectedValue(Settings.SET.getTheme(), true);
            this.comboBoxModelDefaultThemeToMusic.update();
            this.updateComboBoxDefaultThemeToMusic();
         }));
   }

   public static void main(boolean checkUpdate, PopUpOpenProgram popup) {
      SwingUtilities.invokeLater(
         () -> {
            BooleanOptional.runIfTrue(INSTANCE == null, () -> INSTANCE = new MusicMain());
            Optional.ofNullable(popup).ifPresent(p -> SwingUtils.runOnUIThread(() -> p.dispose()));
            INSTANCE.setVisible(true);
            BooleanOptional.runIfTrue(checkUpdate, Updates::checkUpdates);
            BooleanOptional.runIfTrue(
               checkUpdate && Settings.EnumSettings.LONGTIME_UPDATE_DISABLED.getLong() < System.currentTimeMillis(),
               new Thread(() -> Update.INSTANCE.update.check(INSTANCE, false))::start
            );
         }
      );
   }

   @Override
   public void setVisible(boolean flag) {
      Theme t = this.jListThemes.getSelectedValue();
      this.listModelTheme.update();
      this.comboBoxModelDefaultThemeToMusic.update();
      this.updateComboBoxDefaultThemeToMusic();
      this.jListThemes.setSelectedValue(t, true);
      super.setVisible(flag);
      if (flag) {
         if (VLC.ENABLED) {
            VLC.PLAYER_CONTROL.setupToolbarButton(this);
         }

         AlertPanel.INSTANCE.setup(this, AlertPanel.EnumSetup.HOLYRICS);
         this.jPanelSplitLeft.add(AlertPanel.INSTANCE, "Last");
         this.jPanelSplitLeft.revalidate();
         if (Settings.EnumSettings2.FIRST_OPEN_2_0_0.is()) {
            Settings.EnumSettings2.FIRST_OPEN_2_0_0.set(false);
            Settings.EnumSettings2.PASSWORD_REMOTE_CONTROL_MEDIA_HOLYRICS.set(Utils.lowPassRandom(6));
         }

         if (Settings.EnumSettings.FIRST_OPEN.is()) {
            String pass = Utils.lowPassRandom(6);
            Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_HOLYRICS.set(pass);
            Settings.EnumSettings.PASSWORD_REMOTE_UPDATE.set(pass);
            Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_BIBLE.set(pass);
            String firstOpen = "<html> <div width=\"300px\" align=\"center\">" + I18N.MSG_SETTINGS_FIRST_OPEN.val() + "</div>";
            JOptionPane.showMessageDialog(this, new JLabel(firstOpen), I18N.WORD_MESSAGE.val(), -1);
            SettingsView.main(() -> {
               Utils.startThreadDaemon(this::firstOpen);
               Settings.EnumSettings.FIRST_OPEN.set(false);
            });
         } else if (Settings.EnumSettings.SHOW_UPDATE_NEWS.is()) {
            Settings.EnumSettings.SHOW_UPDATE_NEWS.set(false);
            Update.showNews();
         }

         Optional.ofNullable(this.jComboBoxScreenType.getSelectedItem())
            .ifPresent(i -> this.slidePreview.setScreenType((PresentationSlideView.EnumScreenType)i));
         this.jTextPaneLyrics.repaint();
      }
   }

   @Override
   public void requestFocus() {
      super.requestFocus();
      this.musicSearchPanel.focusOnTextFieldSearch();
   }

   @Override
   public void createMusic(Music music) {
      this.newMusic(music);
   }

   @Override
   public void show(List<Music> musics) {
      this.showMusic(musics);
   }

   @Override
   public void musicChanged(Music music) {
      music = Optional.ofNullable(music).orElse(Settings.SET.getMusic());
      Settings.SET.setMusic(music);
      this.updateComboBoxDefaultThemeToMusic();
      this.updateThemePreview();
      MusicUtils.MusicFilter mf = this.musicSearchPanel.musicFilter;
      this.refreshTextPaneLyrics(music, mf);
      String title = MusicUtils.setupHighlight(music.getTitle(), mf);
      String artist = MusicUtils.setupHighlight(music.getArtist(), mf);
      this.jLabelMusicTitleAndArtist
         .setText(
            artist.isEmpty()
               ? String.format("<html><head><style>span{white-space:nowrap}</style></head><body><div><span><b>%s</b></span></body><html>", title)
               : String.format(
                  "<html><head><style>span{white-space:nowrap}</style></head><body><div><span><b>%s</b></span> <span><b>(%s)</b></span></body><html>",
                  title,
                  artist
               )
         );
      this.jLabelMusicNote.setText(music.getNote().isEmpty() ? "" : "<html>" + MusicUtils.setupHighlight(music.getNote(), mf));
      this.jLabelMusicTitleAndArtist.setMinimumSize(new Dimension());
      this.jLabelMusicNote.setMinimumSize(new Dimension());
      this.listModelOrder.setMusic(music);
      this.jListOrder.setVisibleRowCount(5);
      this.jListOrder.setSelectedIndex(0);
      this.listModelHistory.setMusic(music);
      this.jLabelHistoryTitle.setText(I18N.WORD_HISTORY.val() + " (" + History.count(music) + ")");
      this.listModelNextSchedules.setMusic(music);
      Arrays.asList(this.jToolBarMusic.getComponents())
         .stream()
         .filter(WebButton.class::isInstance)
         .forEach(c -> ((JButton)c).setEnabled(!Settings.SET.musicIsBlank()));
      this.refreshButtonAddPlayList();
      this.refreshButtonAddHistory();
   }

   private void exit() {
      if (!Settings.EnumSettings.SYNC_CHECK_UPLOAD.is()) {
         System.exit(0);
      } else {
         BooleanOptional.runIfTrue(Settings.SET.getAccount().getUsername().isEmpty(), () -> System.exit(0));
         PopUpWorker.exe(new CheckUploadAndExitWorker(this));
      }
   }

   public static void safeExit() {
      Optional.ofNullable(INSTANCE).ifPresent(MusicMain::exit);
   }

   private void popUpMenuJList(MouseEvent e, JList list) {
      if (list.equals(this.jListOrder)) {
         PopUpMenu.mainPopUpOrder(this, this.jListOrder, e);
      } else if (list.equals(this.jListHistory)) {
         PopUpMenu.mainPopUpHistory(this, this.jListHistory, e);
      } else if (list.equals(this.jListThemes)) {
         PopUpMenu.mainPopUpThemes(this, this.jListThemes, e);
      } else if (list.equals(this.jListPlayList)) {
         PopUpMenu.mainPopUpPlayList(this, this.jListPlayList, e);
      } else if (list.equals(this.jListMedia)) {
         PopUpMenu.schedulePopUpHItemPlayList(true, this.jListMedia, e);
      }
   }

   private void refreshSlideExample(MusicController.Paragraph p) {
      if (p == null) {
         this.slidePreview.setText("");
      } else {
         String text = Settings.EnumSettings.MUSIC_UPPERCASE.is() ? p.toString().toUpperCase() : p.toString();
         MusicController.Paragraph paragraph = RWObj.clone(p, MusicController.Paragraph.class);
         if (paragraph == null) {
            return;
         }

         paragraph.setText(text);
         this.slidePreview.setText(paragraph);
      }
   }

   private void refreshSlideExample() {
      this.refreshSlideExample(this.jListOrder.getSelectedValue());
   }

   private void updateThemePreview() {
      Theme theme = this.getThemeToMusic();
      this.slidePreview.setTheme(theme);
      this.jLabelSlidePreviewIsAnimated.setText(theme.getBackground().isVideo() ? I18N.MSG_ANIMATED_BACKGROUND.val() : "");
   }

   private Theme getThemeToMusic() {
      Object obj = this.jComboBoxThemeDefault.getSelectedItem();
      return this.switchSaveThemeToMusic.isSelected() && obj instanceof Theme ? (Theme)obj : Settings.SET.getTheme();
   }

   private void refreshTextPaneLyrics(Music music, MusicUtils.MusicFilter mf) {
      music.setTextAndApplyFormatting(this.jTextPaneLyrics);
      MusicUtils.setupHighlight(this.jTextPaneLyrics, mf);
      this.jTextPaneLyrics.setCaretPosition(0);
   }

   public static void staticUpdateSlidePreview() {
      if (INSTANCE != null) {
         INSTANCE.refreshSlideExample();
      }
   }

   public void addPlayList(Music music, EnumPlayList enumPlaylist) {
      if (music != null && !this.playListContains(music, enumPlaylist)) {
         switch (enumPlaylist) {
            case PLAY_LIST:
               this.listModelPlayList.addElement(music);
               this.jListPlayList.ensureIndexIsVisible(this.jListPlayList.getModel().getSize() - 1);
               break;
            case WORSHIP_OR_EVENT:
               BooleanOptional.runIfTrue(this.isEventSelected(), () -> this.schedulePane.addPlayList(music));
               this.schedulePane.jListEventPlayList.ensureIndexIsVisible(this.schedulePane.jListEventPlayList.getModel().getSize() - 1);
         }

         this.listModelNextSchedules.fire();
         this.changeTab(true);
         this.refreshButtonAddPlayList();
      }
   }

   public void addPlayList(Music music) {
      this.addPlayList(music, EnumPlayList.selected);
   }

   public boolean playListContains(Music m, EnumPlayList enumPlaylist) {
      switch (enumPlaylist) {
         case PLAY_LIST:
            return m == null || this.listModelPlayList.contains(m);
         case WORSHIP_OR_EVENT:
            return m == null || this.schedulePane.containsInPlayList(m);
         default:
            return false;
      }
   }

   private boolean playListContains2(Music m, EnumPlayList enumPlaylist) {
      Predicate<IHItem> predicate = hitem -> {
         if (hitem instanceof HItemMusic) {
            Music music = ((HItemMusic)hitem).getMusic();
            return music == null ? false : music.equals(m);
         } else {
            return false;
         }
      };
      switch (enumPlaylist) {
         case PLAY_LIST:
            return m == null || this.listModelPlayList.contains(m) || Collections.list(this.listModelHItemPlayList.elements()).stream().anyMatch(predicate);
         case WORSHIP_OR_EVENT:
            return m == null || this.schedulePane.containsInPlayList(m) || this.schedulePane.getPlayListHItem().stream().anyMatch(predicate);
         default:
            return false;
      }
   }

   public boolean playListContains(Music m) {
      return this.playListContains(m, EnumPlayList.selected);
   }

   private boolean playListContains2(Music m) {
      return this.playListContains2(m, EnumPlayList.selected);
   }

   public void addPlayList(List<IHItem> hItemList) {
      for (IHItem hItem : hItemList) {
         if (!this.playListContains(hItem)) {
            switch (EnumPlayList.selected) {
               case PLAY_LIST:
                  this.listModelHItemPlayList.addElement(hItem);
                  this.jListMedia.ensureIndexIsVisible(this.jListMedia.getModel().getSize() - 1);
                  break;
               case WORSHIP_OR_EVENT:
                  BooleanOptional.runIfTrue(this.isEventSelected(), () -> this.schedulePane.addPlayList(hItem));
                  this.schedulePane.jListEventListMedia.ensureIndexIsVisible(this.schedulePane.jListEventListMedia.getModel().getSize() - 1);
            }
         }

         this.changeTab(false);
      }
   }

   public void addPlayList(IHItem... hItemList) {
      this.addPlayList(Arrays.asList(hItemList));
   }

   private void changeTab(boolean lyrics) {
      try {
         switch (EnumPlayList.selected) {
            case PLAY_LIST:
               this.jTabbedPanePlayListTemp.setSelectedIndex(lyrics ? 0 : 1);
               break;
            case WORSHIP_OR_EVENT:
               this.schedulePane.changeSelectedTab(lyrics);
         }
      } catch (Exception var3) {
      }
   }

   public static void staticAddPlayList(IHItem... hItemList) {
      if (INSTANCE != null) {
         INSTANCE.addPlayList(Arrays.asList(hItemList));
      }
   }

   public static void staticAddPlayList(List<Music> musics) {
      staticAddPlayList(musics, false);
   }

   public static void staticAddPlayList(List<Music> musics, boolean ask) {
      if (INSTANCE != null && !musics.isEmpty()) {
         if (!ask) {
            musics.forEach(INSTANCE::addPlayList);
            return;
         }

         PopupUtils.staticAddPlayListMusicMain(INSTANCE, musics);
      }
   }

   public Event getCurrentEvent() {
      return this.schedulePane.getEvent();
   }

   public boolean playListMediaContains(String hItemID) {
      switch (EnumPlayList.selected) {
         case PLAY_LIST:
            return hItemID != null && Collections.list(this.listModelHItemPlayList.elements()).stream().anyMatch(i -> i.getId().equals(hItemID));
         case WORSHIP_OR_EVENT:
            return hItemID != null && this.schedulePane.containsInPlayListMedia(hItemID);
         default:
            return false;
      }
   }

   public boolean playListContains(IHItem hItem) {
      return hItem != null && this.playListMediaContains(hItem.getId());
   }

   public void removeSelectedMusicsFromPlayList(JList jlist) {
      SwingUtils.removeSelectedIndices(jlist, false);
      this.refreshButtonAddPlayList();
   }

   private void playedMusic() {
      this.playedMusic(Settings.SET.getMusic());
   }

   @Override
   public boolean playedMusic(Music m) {
      Date d = new Date();
      if (History.minutesBetweenLastPlayed(m, d) >= 120L
         && (this.playListContains2(m) || Utils.confirm(this, I18N.MSG_ASK_SET_PLAYED_MUSIC_NOT_IN_PLAYLIST.format(m.getTitle())))) {
         if (Settings.SET.getMusic().equals(m)) {
            this.listModelHistory.addElement(d);
         } else {
            History.addHistory(m, d);
         }

         this.refreshButtonAddHistory();
         return true;
      } else {
         return false;
      }
   }

   public boolean playedMusic(Music m, Date d) {
      if (!History.isPlayed(m, d)) {
         if (Settings.SET.getMusic().equals(m)) {
            this.listModelHistory.addElement(d);
            this.listModelHistory.fire();
         } else {
            History.addHistory(m, d);
         }

         this.refreshButtonAddHistory();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void playlistChanged() {
      this.listModelNextSchedules.fire();
   }

   public void removeSelectedHistories() {
      SwingUtils.removeSelectedIndices(this.jListHistory, true);
      this.refreshButtonAddHistory();
      this.jPanelSlideView.revalidate();
      this.jPanelMusicHistory.repaint();
   }

   private void newTheme(Theme t) {
      ThemeView.main(t, true, () -> SwingUtilities.invokeLater(() -> {
            if (ThemeController.getThemes().contains(t)) {
               this.listModelTheme.update();
               this.jListThemes.setSelectedValue(t, true);
               this.comboBoxModelDefaultThemeToMusic.update();
               this.updateComboBoxDefaultThemeToMusic();
            }
         }));
   }

   public void editMusic() {
      if (!Settings.SET.getMusic().equals(Music.BLANK)) {
         int order = Settings.SET.getMusic().getParagraphs().size();
         MusicView.main(Settings.SET.getMusic(), false, this.getThemeToMusic(), () -> SwingUtilities.invokeLater(() -> {
               Music m = Settings.SET.getMusic();
               this.musicSearchPanel.update(m);
               if (m.getParagraphs().size() != order) {
                  if (Utils.confirm(I18N.MSG_ORDER_RESTART_ASK.val())) {
                     PopupUtils.showLastParagraphSequence(this, m, Collections.list(this.listModelOrder.elements()));
                  }

                  PopUpAlert.show(I18N.MSG_ORDER_RESTART.val(), new Font("Tahoma", 1, 28), 4000L, this.jListOrder);
                  m.setOrder(new ArrayList<>());
                  m.save();
                  this.listModelOrder.setMusic(m);
                  this.jListOrder.setSelectedIndex(0);
               }

               this.repaint();
            }));
      }
   }

   public void editMusic(Music music) {
      if (music != null) {
         Settings.SET.setMusic(music);
         this.editMusic();
      }
   }

   private void removeMusic() {
      Music m = Settings.SET.getMusic();
      if (Utils.confirm(this, I18N.MSG_CONFIRM_REMOVE_MUSIC.format(m.toString()))) {
         this.listModelPlayList.removeElement(m);
         this.schedulePane.removePlayList(m);
         MusicDao.remove(m);
         this.musicSearchPanel.filter();
      }
   }

   private void newMusic(Music m) {
      MusicView.main(m, true, Settings.SET.getTheme(), () -> SwingUtilities.invokeLater(() -> this.musicSearchPanel.update(m.getId() > 0L ? m : null)));
   }

   public void removeTheme(Theme theme) {
      if (Utils.confirm(this, I18N.MSG_CONFIRM_REMOVE_THEME.val())) {
         ThemeController.remove(theme);
         this.listModelTheme.update();
         this.jListThemes.setSelectedIndex(0);
         this.jListThemes.ensureIndexIsVisible(0);
         this.comboBoxModelDefaultThemeToMusic.update();
         this.updateComboBoxDefaultThemeToMusic();
      }
   }

   public void editTheme(Theme theme) {
      if (theme != null && ThemeController.exists(theme.id)) {
         Settings.SET.setTheme(theme);
         ThemeView.main(Settings.SET.getTheme(), false, () -> SwingUtilities.invokeLater(() -> {
               this.listModelTheme.update();
               this.jListThemes.setSelectedValue(Settings.SET.getTheme(), true);
               this.comboBoxModelDefaultThemeToMusic.update();
               this.updateComboBoxDefaultThemeToMusic();
            }));
      }
   }

   public void showMusic(List<Music> music) {
      this.showMusic(music, 0);
   }

   public void showMusic(List<Music> music, int initIndex) {
      if (!music.isEmpty()) {
         this.removeFooter();
         GeneratePresentation.show(this, Settings.SET.getTheme(), music, initIndex);
      }
   }

   public void setFooterControl(PresentationControllerViewFooter footerControl) {
      this.removeFooter();
      this.footerControl = footerControl;
      Optional.ofNullable(this.footerControl).ifPresent(footer -> {
         this.jPanelOrder.setVisible(Settings.EnumSettings2.FOOTER_CONTROL_MINIMIZE.is());
         this.jPanelFooter.add(footer, "Center");
         this.jPanelFooter.revalidate();
         this.jPanelFooter.repaint();
      });
   }

   public void setPanelOrderVisible(boolean visible) {
      this.jPanelOrder.setVisible(visible);
   }

   public void showMusic(Music music) {
      Optional.ofNullable(music).ifPresent(m -> this.showMusic(Arrays.asList(m)));
   }

   private void setUppercase(boolean uppercase) {
      Settings.EnumSettings.MUSIC_UPPERCASE.set(uppercase);
      this.refreshSlideExample();
   }

   private void setBreakLine(boolean breakLine) {
      Settings.EnumSettings2.BREAK_LINE.set(breakLine);
      this.refreshSlideExample();
   }

   private void setCustomFormatting(boolean customFormatting) {
      Settings.EnumSettings2.TEXT_FORMATTING_ENABLED.set(customFormatting);
      this.refreshSlideExample();
   }

   private void changePlayListType(EnumPlayList en) {
      ((CardLayout)this.jPanelPlayListCard.getLayout()).show(this.jPanelPlayListCard, en.name());
      EnumPlayList.selected = en;
      this.refreshButtonAddPlayList();
   }

   private void refreshButtonAddPlayList() {
      this.jButtonAddPlayList.setEnabled(!Settings.SET.musicIsBlank() && !this.playListContains(Settings.SET.getMusic()));
      this.revalidateHistorySchedulePanel();
   }

   private void refreshButtonAddHistory() {
      this.jButtonAddHistory.setEnabled(!Settings.SET.musicIsBlank() && History.minutesBetweenLastPlayed(Settings.SET.getMusic(), new Date()) >= 120L);
      this.revalidateHistorySchedulePanel();
      this.jListPlayList.repaint();
      this.schedulePane.jListEventPlayList.repaint();
   }

   private void revalidateHistorySchedulePanel() {
      this.jSplitPaneMusicAndSlide.revalidate();
      this.jPanelSlideView.revalidate();
      this.jPanelSlideView.repaint();
      this.jListHistory.repaint();
   }

   private void changeDateListEvent(Date date) {
      Calendar c = Calendar.getInstance();
      c.setTime(date);
      this.comboBoxModelEvent.set(c.get(2), c.get(1));
   }

   private void selectNearestEvent() {
      Calendar c = Calendar.getInstance();
      long min = Long.MAX_VALUE;
      Event ev = null;

      for (Event e : this.comboBoxModelEvent.getAllElements()) {
         if (Math.abs(c.getTimeInMillis() - e.getDate().getTimeInMillis()) < min) {
            min = Math.abs(c.getTimeInMillis() - e.getDate().getTimeInMillis());
            ev = e;
         }
      }

      if (ev != null) {
         this.comboBoxModelEvent.setSelectedItem(ev);
         if (ev.getDate().get(5) == Calendar.getInstance().get(5)) {
            this.jComboBoxPlayListType.setSelectedItem(EnumPlayList.WORSHIP_OR_EVENT);
         }
      } else {
         this.schedulePane.setEvent(null);
      }
   }

   private boolean isEventSelected() {
      if (this.jComboBoxEvent.getSelectedItem() == null) {
         Utils.error(this, I18N.MSG_ERROR_EVENT_NOT_SELECTED.val());
         return false;
      } else {
         return true;
      }
   }

   private void update() {
      MusicDao.getAll();
      this.jListThemes.repaint();
      this.musicSearchPanel.update(Settings.SET.getMusic());
      Event e = (Event)this.jComboBoxEvent.getSelectedItem();
      this.changeDateListEvent((Date)this.jSpinnerEventDate.getValue());
      if (this.comboBoxModelEvent.getAllElements().contains(e)) {
         this.comboBoxModelEvent.setSelectedItem(e);
      }

      this.schedulePane.setEvent((Event)this.jComboBoxEvent.getSelectedItem());
   }

   private void goToBible() {
      super.setVisible(false);
      BibleMain.main();
   }

   private MusicSearchPanel createMusicSearchPanel() {
      return new MusicSearchPanel(this, false, Settings.SET.getMusic(), true, true) {
         @Override
         public void musicChanged(Music music) {
            MusicMain.this.musicChanged(music);
         }

         @Override
         public void doubleClick(Music music) {
            MusicMain.this.addPlayList(music);
         }

         @Override
         public void rightButtonClicked(MouseEvent evt, JList<Music> jlist) {
            PopUpMenu.mainPopUpMusics(MusicMain.this, jlist, evt);
         }

         @Override
         public void newMusic() {
            MusicMain.this.newMusic(Music.create());
         }

         @Override
         public boolean keyLeftEvent() {
            return MusicMain.staticPrevious();
         }

         @Override
         public boolean keyRightEvent() {
            return MusicMain.staticNext();
         }

         @Override
         public Window getOwner() {
            return MusicMain.this;
         }

         @Override
         public void openEditProfile() {
            MusicMain.this.menuItemNewEdit("edit;profile");
         }
      };
   }

   private void menuItemNewEdit(String value) {
      try {
         String type = value.split(";")[0];
         String name = value.split(";")[1];
         switch (name) {
            case "music":
               if (type.equals("new")) {
                  this.newMusic(Music.create());
               } else if (type.equals("edit")) {
                  this.editMusic();
               }
               break;
            case "profile":
               EditDialog.main(this, 0, this::update);
               break;
            case "theme":
               if (type.equals("new")) {
                  this.newTheme(Theme.create());
               } else if (type.equals("edit")) {
                  this.editTheme(this.getThemeToMusic());
               }
               break;
            case "worship":
               EditDialog.main(this, 1, this::update);
               break;
            case "event":
               EditDialog.main(this, 2, this::update);
               break;
            case "member":
               EditDialog.main(this, 3, this::update);
         }
      } catch (Exception var6) {
      }
   }

   private void showFromCurrentSlide() {
      this.showMusic(Arrays.asList(Settings.SET.getMusic()), this.jListOrder.getSelectedIndex() + 1);
   }

   private void showFromBegin() {
      this.showMusic(Settings.SET.getMusic());
   }

   private void musicWebSearch() {
      SwingUtilities.invokeLater(() -> {
         if (this.musicWebSearch == null) {
            this.musicWebSearch = new MusicWebSearch(this, this);
         }

         this.musicWebSearch.setVisible(true);
      });
   }

   public static void staticRemoveFooter() {
      Optional.ofNullable(INSTANCE).ifPresent(MusicMain::removeFooter);
   }

   public void removeFooter() {
      if (this.footerControl != null) {
         SwingUtils.runOnUIThread(() -> {
            this.jPanelOrder.setVisible(true);
            this.jPanelFooter.removeAll();
            this.jPanelFooter.revalidate();
            this.jPanelFooter.repaint();
            this.footerControl = null;
         });
      }
   }

   public void refreshAnnouncementMenuBar() {
      SwingUtilities.invokeLater(() -> {
         Map<JMenu, Consumer<Announcement>> map = new HashMap<>();
         map.put(this.jMenuAnnouncementShortcut, an -> {
            List<Presentation> list = new ArrayList<>(Arrays.asList(an.createPresentation()));
            GeneratePresentation.showPresentation(this, Theme.BLACK_SCREEN, list, 0, ScreenDeviceSettings.EnumDeviceType.ANNOUNCEMENT);
         });
         map.put(this.jMenuAnnouncementAddPlaylist, an -> this.addPlayList(Arrays.asList(new HItemAnnouncement(an.id))));
         map.put(this.jMenuAnnouncementAddFavorite, an -> {
            HItemAnnouncement hItem = new HItemAnnouncement(an.id);
            ImageIcon icon = new ImageIcon(hItem.getThumb(ThumbMediaController.getThumbSize()));
            FavoriteUtils.getNameAndCreate(null, hItem, icon, hItem.getName());
         });
         List<Announcement> announcements = AnnouncementController.getAll();
         map.forEach((jmenu, consumer) -> {
            jmenu.removeAll();
            announcements.forEach(an -> {
               JMenuItem item = new JMenuItem(an.getName());
               item.addActionListener(e -> consumer.accept(an));
               jmenu.add(item);
            });
            jmenu.setVisible(!announcements.isEmpty());
         });
         map.clear();
      });
   }

   public static void searchDBOffLine() {
      Optional.ofNullable(INSTANCE).ifPresent(i -> MusicSearchDBOffLineView.main(() -> i.update()));
   }

   public static boolean staticPrevious() {
      if (BiblePresentationView.staticPrevious()) {
         return true;
      } else if (INSTANCE != null && INSTANCE.footerControl != null) {
         INSTANCE.footerControl.previous();
         return true;
      } else {
         return false;
      }
   }

   public static boolean staticNext() {
      if (BiblePresentationView.staticNext()) {
         return true;
      } else if (INSTANCE != null && INSTANCE.footerControl != null) {
         INSTANCE.footerControl.next();
         return true;
      } else {
         return false;
      }
   }

   public static void staticExit() {
      if (!BiblePresentationView.staticExit() && !AutomaticShowView.staticExit()) {
         if (INSTANCE != null && INSTANCE.footerControl != null) {
            INSTANCE.footerControl.exit();
         } else {
            INSTANCE.musicSearchPanel.clearFilter();
         }
      }
   }

   public static void staticPlayedMusic(Music music) {
      Optional.ofNullable(INSTANCE).ifPresent(i -> i.playedMusic(music));
   }

   public static void staticPlayedMusic(Music music, Date date) {
      Optional.ofNullable(INSTANCE).ifPresent(i -> i.playedMusic(music, date));
   }

   public static List<Music> staticGetPlayList() {
      if (INSTANCE != null) {
         switch (EnumPlayList.selected) {
            case PLAY_LIST:
               return Collections.list(INSTANCE.listModelPlayList.elements());
            case WORSHIP_OR_EVENT:
               ListModel<Music> model = INSTANCE.schedulePane.jListEventPlayList.getModel();
               return IntStream.range(0, model.getSize()).mapToObj(model::getElementAt).collect(Collectors.toList());
         }
      }

      return new ArrayList<>();
   }

   public static List<IHItem> staticGetPlayListMedia() {
      if (INSTANCE != null) {
         switch (EnumPlayList.selected) {
            case PLAY_LIST:
               return Collections.list(INSTANCE.listModelHItemPlayList.elements());
            case WORSHIP_OR_EVENT:
               ListModel<IHItem> model = INSTANCE.schedulePane.jListEventListMedia.getModel();
               return IntStream.range(0, model.getSize()).mapToObj(model::getElementAt).collect(Collectors.toList());
         }
      }

      return new ArrayList<>();
   }

   private void setupMedia() {
      this.musicSearchPanel.setUndecorated(true);
      this.musicSearchPanel.setMargin(0);
      this.tabbedMedia.addTab(I18N.WORD_LYRICS.val(), this.musicSearchPanel);
      this.tabbedMedia.setTabStretchType(TabStretchType.always);
      this.tabbedMedia.getTabInsets().left = this.tabbedMedia.getTabInsets().right = 4;
      this.tabbedMedia.setFocusable(false);
      this.tabbedMedia.setShadeWidth(2);
      this.tabbedMedia.getContentInsets().set(5, 4, 2, 4);
      this.tabbedMedia.addChangeListener(e -> this.ctrlF());
      if (VLC.ENABLED) {
         Stream.of(EnumTabType.values()).forEach(en -> {
            HItemTab tabx = new HItemTab(en, this);
            this.setupJListKeyListenerPresentation(tabx.listView);
            this.tabbedMedia.addTab(en.title, tabx);
         });
      } else {
         HItemTab tab = new HItemTab(EnumTabType.IMAGE, this);
         this.setupJListKeyListenerPresentation(tab.listView);
         this.tabbedMedia.addTab(EnumTabType.IMAGE.title, tab);
      }

      this.jPanelSplitLeft.add(this.tabbedMedia, "Center");

      try {
         this.tabbedMedia.setToolTipTextAt(0, "<html><b> (Ctrl + 7) </b>");
         if (this.tabbedMedia.getTabCount() == 2) {
            this.tabbedMedia.setToolTipTextAt(1, "<html><b> (Ctrl + 0) </b>");
         } else if (this.tabbedMedia.getTabCount() == 4) {
            this.tabbedMedia.setToolTipTextAt(1, "<html><b> (Ctrl + 8) </b>");
            this.tabbedMedia.setToolTipTextAt(2, "<html><b> (Ctrl + 9) </b>");
            this.tabbedMedia.setToolTipTextAt(3, "<html><b> (Ctrl + 0) </b>");
         }
      } catch (Exception var2) {
      }

      this.tabbedMedia
         .addChangeListener(
            evt -> {
               if (Settings.EnumSettings2.MEDIA_TAB_FIRST_CLICK.is()) {
                  Settings.EnumSettings2.MEDIA_TAB_FIRST_CLICK.set(false);
                  String msg = "<html>Cada lista representa uma pasta interna do programa Holyrics: Áudio, Vídeo e Imagem.<br>Para adicionar arquivos, clique no ícone "
                     + Images.getImageHTMLTagBase64("open16.png")
                     + " da respectiva aba para abrir a pasta e cole os arquivos que desejar.<br>A lista no programa será atualizada automaticamente com os arquivos da pasta.";
                  Utils.msg(this, msg);
               }
            }
         );
   }

   public static void staticExpandPlayerPanel() {
      if (INSTANCE != null && INSTANCE.webCollapsiblePaneVLC != null && !INSTANCE.webCollapsiblePaneVLC.isExpanded()) {
         INSTANCE.webCollapsiblePaneVLC.setExpanded(true);
      }
   }

   private HItemTab getSelectedTabMedia() {
      try {
         Component c = this.tabbedMedia.getSelectedComponent();
         return c instanceof HItemTab ? (HItemTab)c : null;
      } catch (Exception var2) {
         return null;
      }
   }

   private void ctrlF() {
      try {
         Component c = this.tabbedMedia.getSelectedComponent();
         if (c == this.musicSearchPanel) {
            this.musicSearchPanel.ctrlF();
         } else if (c instanceof HItemTab) {
            ((HItemTab)c).ctrlF();
         }
      } catch (Exception var2) {
      }
   }

   private void altEnter() {
      try {
         Component c = this.tabbedMedia.getSelectedComponent();
         if (c == this.musicSearchPanel) {
            this.addPlayListMusic();
         } else if (c instanceof HItemTab) {
            ((HItemTab)c).addPlaylist();
         }
      } catch (Exception var2) {
      }
   }

   private void ctrlAltEnter() {
      if (!Settings.SET.getMusic().equals(Music.BLANK)) {
         this.addPlayList(new HItemMusic(Settings.SET.getMusic().getId()));
      }
   }

   private void addPlayListMusic() {
      BooleanOptional.runIfTrue(!Settings.SET.getMusic().equals(Music.BLANK), () -> this.addPlayList(Settings.SET.getMusic()));
   }

   @Override
   public void playlistHItemChanged() {
   }

   @Override
   public void addPlaylistMedia(List<IHItem> hItem) {
      this.addPlayList(hItem);
   }

   private void changeTabMedia(int index) {
      try {
         this.tabbedMedia.setSelectedIndex(index);
      } catch (Exception var3) {
      }
   }

   private void updateComboBoxDefaultThemeToMusic() {
      this.preventChangeDefaultThemeToMusic = true;
      Theme theme = Settings.SET.getMusic().getDefaultTheme();
      this.switchSaveThemeToMusic.setSelected(theme != null, false);
      this.jComboBoxThemeDefault.setSelectedItem(null);
      Theme t = this.slidePreview.getTheme();
      this.slidePreview.setTheme(ThemeController.getOrDefault(t.id, Settings.SET.getTheme()));
      this.jComboBoxThemeDefault.setSelectedItem(theme != null ? theme : Settings.SET.getTheme());
      this.jComboBoxThemeDefault.setEnabled(theme != null);
      this.jComboBoxThemeDefault.repaint();
      this.preventChangeDefaultThemeToMusic = false;
   }

   public static void openDisplaySettings(Window parent) {
      if (INSTANCE != null) {
         VideoUtils.openDialogSettings(parent, INSTANCE.jPanelDisplaySettingsButtons);
      }
   }

   private void setupExportToPDFMenuBar() {
      this.jMenuExportToPDF.setVisible(false);
      this.jMenuExportToPDF.setEnabled(false);
   }

   public static List<ListModelHItem.MyFile> staticGetMediaList(EnumTabType type) {
      if (INSTANCE == null) {
         return Collections.EMPTY_LIST;
      } else {
         for (Component c : INSTANCE.tabbedMedia.getComponents()) {
            if (c instanceof HItemTab) {
               HItemTab tab = (HItemTab)c;
               if (tab.tabType == type) {
                  return tab.listView.model.listFiles();
               }
            }
         }

         return Collections.EMPTY_LIST;
      }
   }

   private JLabel createSlidePreview() {
      return new PresentationSlideView(
         Settings.SET.getTheme(), Settings.SET.getMusic().getInitialParagraph(), PresentationSlideView.EnumScreenType.YOUR_DISPLAY, true
      );
   }
}
