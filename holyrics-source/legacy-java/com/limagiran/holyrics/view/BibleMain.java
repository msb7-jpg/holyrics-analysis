package com.limagiran.holyrics.view;

import com.alee.extended.panel.CollapsiblePaneAdapter;
import com.alee.extended.panel.WebButtonGroup;
import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.list.WebList;
import com.alee.laf.list.WebListStyle;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.TabStretchType;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyData;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.limagiran.bible.AvailableVersions;
import com.limagiran.bible.Bible;
import com.limagiran.bible.Book;
import com.limagiran.bible.ReportError;
import com.limagiran.bible.Verse;
import com.limagiran.bible.download.DownloadView;
import com.limagiran.bible.swingmodel.ListCellRendererFoundVerses;
import com.limagiran.bible.view.VerseSearchView;
import com.limagiran.holyrics.Holyrics;
import com.limagiran.holyrics.Update;
import com.limagiran.holyrics.event.BibleFastSearchListener;
import com.limagiran.holyrics.help.EnumHelpPages;
import com.limagiran.holyrics.help.HelpView;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.settings.SettingsView;
import com.limagiran.holyrics.swing.BibleMainSelectableJLabel;
import com.limagiran.holyrics.swing.BibleTabbedPaneTitle;
import com.limagiran.holyrics.swing.ComboBoxModelTheme;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.swing.JLabelToolTip;
import com.limagiran.holyrics.swing.ListCellRendererBibleVerses;
import com.limagiran.holyrics.swing.ListCellRendererFavoriteVerse;
import com.limagiran.holyrics.swing.ListModelFavoriteVerses;
import com.limagiran.holyrics.swing.ListModelVerse;
import com.limagiran.holyrics.util.BibleMainUtils;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Freeze;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.Log;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpMenu;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.SystemUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.util.WebServiceUtils;
import com.limagiran.holyrics.util.WrapLayout;
import com.limagiran.holyrics.view.panel.AlertPanel;
import com.limagiran.holyrics.vlc.VLC;
import com.limagiran.holyrics.webservice.RemoteControlBibleWS;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPopupMenu.Separator;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class BibleMain extends JFrame implements BibleFastSearchListener {
   private static BibleMain INSTANCE;
   public static final String PANEL_NEXT_PAGE = "»";
   public static final String PANEL_PREVIOUS_PAGE = "«";
   private int book = 1;
   private int chapter = 1;
   private int verse = 1;
   private final List<BibleMainSelectableJLabel> booksAbbrev = new ArrayList<>();
   private final List<BibleMainSelectableJLabel> booksName = new ArrayList<>();
   private final List<BibleMainSelectableJLabel> chaptersLabel = new ArrayList<>();
   private final List<BibleMainSelectableJLabel> versesLabel = new ArrayList<>();
   private final ListModelFavoriteVerses listModelFavorite;
   private Bible bible;
   private Bible bible2;
   private Bible bible3;
   private final WebToggleButton[] toggleButtonDisplayedButton = new WebToggleButton[]{
      new WebToggleButton("1"), new WebToggleButton("2"), new WebToggleButton("3")
   };
   private final ListModelVerse listModelBible = new ListModelVerse();
   private final ListModelVerse listModelBible2 = new ListModelVerse();
   private final ListModelVerse listModelBible3 = new ListModelVerse();
   private boolean blockFastSearch = false;
   private final BibleFastSearchView bibleFastSearchView = new BibleFastSearchView(this);
   private final WebCollapsiblePane collapsiblePlayList = new WebCollapsiblePane(new ImageIcon(Images.load("favorite16.png")), I18N.WORD_VERSES.val());
   private final JLabelToolTip toolTip = new JLabelToolTip();
   private final WebButton buttonBibleSettings;
   private JButton jButtonAddPlayList;
   private JButton jButtonCustomShow;
   private JButton jButtonGoToHolyrics;
   private JButton jButtonHelp;
   private JButton jButtonReportError;
   private JButton jButtonSearch;
   private JButton jButtonShow;
   private JCheckBoxMenuItem jCheckBoxMenuItemFreezeScreen;
   private JComboBox<Theme> jComboBoxTheme;
   private JLabel jLabelBookAndChapter;
   private JLabel jLabelTip;
   private JLabel jLabelTitle;
   private JList<Verse> jListPlayList;
   private JList<Verse> jListVersesAux1;
   private JList<Verse> jListVersesAux2;
   private JList<Verse> jListVersesDefault;
   private JMenu jMenuAjuda;
   private JMenuBar jMenuBarMenu;
   private JMenu jMenuFile;
   private JMenuItem jMenuItemAbout;
   private JMenuItem jMenuItemChangeWebServiceIP;
   private JMenuItem jMenuItemCheckUpdates;
   private JMenuItem jMenuItemCreateShortcurDesktop;
   private JMenuItem jMenuItemExit;
   private JMenuItem jMenuItemFileDisplaySettings;
   private JMenuItem jMenuItemFileFilesReceived;
   private JMenuItem jMenuItemGoToMusics;
   private JMenuItem jMenuItemHelp;
   private JMenuItem jMenuItemPluginHolyrics;
   private JMenuItem jMenuItemSavePresentationAs;
   private JMenuItem jMenuItemSettings;
   private JMenuItem jMenuItemSupportAndFeedBack;
   private JMenuItem jMenuItemTutorialYouTube;
   private JMenuItem jMenuItemVersionDownload;
   private JMenuItem jMenuItemWindowsLive;
   private JMenu jMenuLanguage;
   private JMenu jMenuSettings;
   private JMenu jMenuVersion;
   private JMenu jMenuVersionAux1Change;
   private JMenu jMenuVersionAux2Change;
   private JMenu jMenuVersionDefaultChange;
   private JMenu jMenuWindows;
   private JMenuItem jMenuWindowsItemCommunicationPanel;
   private JPanel jPanelAlert;
   private JPanel jPanelBible;
   private JPanel jPanelBibleToolButtons;
   private JPanel jPanelBooks;
   private JPanel jPanelBooksAbbrev;
   private JPanel jPanelBooksChaptersAndVerses;
   private JPanel jPanelBooksName;
   private JPanel jPanelChapterAndVerse;
   private JPanel jPanelChapters;
   private JPanel jPanelChaptersBorder;
   private JPanel jPanelChaptersPage1;
   private JPanel jPanelChaptersPage2;
   private JPanel jPanelGeneral;
   private JPanel jPanelGoToHolyrics;
   private JPanel jPanelListVerses;
   private JPanel jPanelPlaylist;
   private JPanel jPanelRightTop;
   private JPanel jPanelSearch;
   private JPanel jPanelSettings;
   private JPanel jPanelTitle;
   private JPanel jPanelVerses;
   private JPanel jPanelVersesBorder;
   private JPanel jPanelVersesHeader;
   private JPanel jPanelVersesHeader2;
   private JPanel jPanelVersesPage1;
   private JPanel jPanelVersesPage2;
   private JScrollPane jScrollPaneBooksName;
   private JScrollPane jScrollPanePlayList;
   private JScrollPane jScrollPaneVersesAux1;
   private JScrollPane jScrollPaneVersesAux2;
   private JScrollPane jScrollPaneVersesDefault;
   private Separator jSeparator1;
   private JSeparator jSeparator10;
   private Separator jSeparator11;
   private Separator jSeparator2;
   private Separator jSeparator3;
   private Separator jSeparator4;
   private JSeparator jSeparator5;
   private Separator jSeparator6;
   private JSeparator jSeparator7;
   private JSeparator jSeparator8;
   private JSeparator jSeparator9;
   private JSplitPane jSplitPaneBibleAndSearch;
   private JTabbedPane jTabbedPaneBible;

   public static void load() {
      SwingUtils.runOnUIThread(() -> INSTANCE = new BibleMain());
   }

   public static BibleMain getInstance() {
      return INSTANCE;
   }

   private BibleMain() {
      this.toolTip.setBackground(new Color(StyleUI.FOREGROUND.getRed(), StyleUI.FOREGROUND.getGreen(), StyleUI.FOREGROUND.getBlue(), 150));
      this.toolTip.setForeground(StyleUI.BACKGROUND);
      this.toolTip.atLeft();
      this.buttonBibleSettings = new WebButton(I18N.WORD_SETTINGS.val(), e -> BibleSettingsDialog.main(this::updateAfterSettings));
      this.bible = Settings.SET.getBible(this.getUploadedBible());
      this.bible2 = Settings.SET.getBible2(this.getUploadedBible());
      this.bible3 = Settings.SET.getBible3(this.getUploadedBible());
      this.listModelFavorite = new ListModelFavoriteVerses(this.bible);
      this.initComponents();
      this.init();
   }

   private void init() {
      ListCellRendererFoundVerses.setFontName(Settings.EnumSettings.BIBLE_FONT_NAME.getString());
      ListCellRendererFoundVerses.setFontSize(Settings.EnumSettings.BIBLE_FONT_SIZE.getInt());
      Arrays.asList(this.jListVersesDefault, this.jListVersesAux1, this.jListVersesAux2).forEach(l -> {
         ((WebList)l).setRolloverSelectionEnabled(false);
         ((WebList)l).setDecorateSelection(false);
      });
      this.addActions();
      ((CardLayout)this.jPanelBooks.getLayout()).show(this.jPanelBooks, Settings.EnumSettings.BOOK_S_NAME_ABBREVIATED.is() ? "books_abbrev" : "books_name");
      this.jPanelBooksName.setLayout(new WrapLayout(1, 0, 0));
      this.jScrollPaneBooksName.getVerticalScrollBar().setUnitIncrement(40);
      this.jScrollPanePlayList.setHorizontalScrollBarPolicy(32);
      WebLafUtils.setupScrollBarLargeThumbWithoutButton(((WebScrollPane)this.jScrollPanePlayList).getWebHorizontalScrollBar());
      this.updateJMenuVersions();
      this.addComponentListener(
         new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
               SwingUtils.adjustFontOfAllJLabel(BibleMain.this.jPanelBooksAbbrev, 28, 0.2F);
               BibleMainSelectableJLabel.adjustFontOfAllJLabelFooter(BibleMain.this.jPanelBooksAbbrev);
               Arrays.asList(
                     BibleMain.this.jPanelChaptersPage1, BibleMain.this.jPanelChaptersPage2, BibleMain.this.jPanelVersesPage1, BibleMain.this.jPanelVersesPage2
                  )
                  .forEach(BibleMainUtils::addAndResizePanels);
            }
         }
      );
      this.addBooks();
      Arrays.asList(this.jScrollPanePlayList, this.jScrollPaneVersesDefault, this.jScrollPaneVersesAux1, this.jScrollPaneVersesAux2)
         .forEach(scroll -> scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)));
      this.addMouseListenerJList();
      SwingUtils.addMnemonics(this.jMenuBarMenu);
      this.insertShortcuts();
      this.updateBook();
      this.addToolTipButtons();
      this.setupWebLaf();
      this.updateTabbedPaneTitle();
      List<InsertShortcut.Shortcut> shortcuts = new ArrayList<>();
      IntStream.rangeClosed(65, 90).forEach(c -> shortcuts.add(InsertShortcut.Shortcut.create(c, () -> this.fastSearch((char)c))));
      IntStream.rangeClosed(48, 57).forEach(c -> shortcuts.add(InsertShortcut.Shortcut.create(c, () -> this.fastSearch((char)c))));
      IntStream.rangeClosed(96, 105).forEach(c -> shortcuts.add(InsertShortcut.Shortcut.create(c, () -> this.fastSearch((char)(c - 48)))));
      InsertShortcut.add(this, shortcuts);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(119, BiblePresentationView::staticWallpaper));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(120, BiblePresentationView::staticBlank));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(121, BiblePresentationView::staticBlack));
      this.jMenuItemFileDisplaySettings.addActionListener(evt -> MusicMain.openDisplaySettings(this));
      Settings.EnumLanguage.setupJMenuBar(this.jMenuLanguage);
      Freeze.setup(this.jCheckBoxMenuItemFreezeScreen);
   }

   private void addActions() {
      this.jButtonCustomShow.addActionListener(e -> BibleAutomaticShowDialog.main(this.getCurrentBible()));
      this.jButtonHelp.addActionListener(e -> this.showHelp());
      this.jButtonReportError.addActionListener(e -> this.reportError());
      this.jButtonShow.addActionListener(e -> this.showVerse());
      this.jButtonAddPlayList.addActionListener(e -> this.addPlayList(this.jListVersesDefault.getSelectedValue()));
      this.jButtonSearch.addActionListener(e -> this.search());
      Arrays.asList(this.jButtonCustomShow, this.jButtonHelp, this.jButtonReportError, this.jButtonShow, this.jButtonAddPlayList, this.jButtonSearch)
         .forEach(b -> ((WebButton)b).setUndecorated(true));
   }

   private void setupWebLaf() {
      this.collapsiblePlayList.setAnimate(false);
      this.collapsiblePlayList.setContent(this.jPanelPlaylist);
      this.collapsiblePlayList.setExpanded(!this.listModelFavorite.isEmpty());
      this.collapsiblePlayList.setFocusable(false);
      this.collapsiblePlayList.setTitleAlignment(0);
      this.collapsiblePlayList
         .addCollapsiblePaneListener(
            new CollapsiblePaneAdapter() {
               public void collapsed(WebCollapsiblePane pane) {
                  this.refresh();
               }

               public void expanded(WebCollapsiblePane pane) {
                  this.refresh();
               }

               private void refresh() {
                  SwingUtilities.invokeLater(
                     () -> Arrays.asList(
                              BibleMain.this.jPanelChaptersPage1,
                              BibleMain.this.jPanelChaptersPage2,
                              BibleMain.this.jPanelVersesPage1,
                              BibleMain.this.jPanelVersesPage2
                           )
                           .forEach(BibleMainUtils::addAndResizePanels)
                  );
               }
            }
         );
      this.jPanelSearch.add(this.collapsiblePlayList, "First");
      this.jPanelSearch.revalidate();
      WebLafUtils.setupWebLafBorderLayout(this.jPanelBible, this.jPanelRightTop, this.jPanelChaptersBorder, this.jPanelVersesBorder);
      SwingUtils.setJPanelToOpaqueFalse(this);
      Arrays.asList(this.jScrollPaneVersesDefault, this.jScrollPaneVersesAux1, this.jScrollPaneVersesAux2).forEach(s -> ((WebScrollPane)s).setShadeWidth(1));
      ((WebComboBox)this.jComboBoxTheme).setShadeWidth(0);
      ((WebButton)this.jButtonGoToHolyrics).setShadeColor(Values.COLOR_TRANSPARENT);
      WebButton fontSettings = new WebButton(new ImageIcon(Images.load("textFields16.png")), e -> BibleFontSettings.main(this, this.bible, this::fontChanged));
      WebButtonGroup group = new WebButtonGroup(0, true, new JComponent[0]);
      group.setButtonsDrawFocus(false);
      HotkeyData[] hotkeys = new HotkeyData[]{
         new HotkeyData(true, false, false, 49), new HotkeyData(true, false, false, 50), new HotkeyData(true, false, false, 51)
      };
      IntStream.rangeClosed(1, this.toggleButtonDisplayedButton.length).forEach(i -> {
         WebToggleButton b = this.toggleButtonDisplayedButton[i - 1];
         b.setSelected(Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt() == i);
         b.addItemListener(e -> BooleanOptional.runIfTrue(e.getStateChange() == 1, () -> {
               Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.set(i);
               BiblePresentationView.staticRefresh(false);
               Arrays.asList(this.listModelBible, this.listModelBible2, this.listModelBible3).forEach(ListModelVerse::refresh);
            }));
         group.add(b.setShadeColor(Values.COLOR_TRANSPARENT));
         b.setFocusable(false);
         this.addToolTipButton(b, hotkeys[i - 1]);
      });
      Arrays.asList(fontSettings, this.buttonBibleSettings).forEach(b -> b.setShadeColor(Values.COLOR_TRANSPARENT).setFocusable(false));
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridy = 0;
      gbc.weightx = 1.0;
      gbc.insets = new Insets(2, 0, 2, 0);
      this.jPanelSettings.add(group, gbc);
      gbc = new GridBagConstraints();
      gbc.gridy = 1;
      gbc.weightx = 1.0;
      gbc.insets = new Insets(2, 0, 2, 0);
      this.jPanelSettings.add(this.buttonBibleSettings, gbc);
      WebPanel p = new WebPanel(new FlowLayout(0, 2, 0), new Component[]{fontSettings});
      p.setOpaque(false);
      this.jPanelVersesHeader2.add(p, "West");
   }

   private void updateAfterSettings() {
      ((CardLayout)this.jPanelBooks.getLayout()).show(this.jPanelBooks, Settings.EnumSettings.BOOK_S_NAME_ABBREVIATED.is() ? "books_abbrev" : "books_name");
      BiblePresentationView.staticRefresh(false);
   }

   private void addToolTipButtons() {
      this.addToolTipButton(this.jButtonShow, Hotkey.ENTER, Hotkey.F5);
      this.addToolTipButton(this.buttonBibleSettings, Hotkey.F12);
      this.addToolTipButton(this.jButtonGoToHolyrics, Hotkey.CTRL_H, Hotkey.F7);
      this.addToolTipButton(this.jButtonSearch, Hotkey.CTRL_F);
      this.addToolTipButton(this.jButtonHelp);
      this.addToolTipButton(this.jButtonReportError);
      this.addToolTipButton(this.jButtonAddPlayList, Hotkey.ALT_ENTER);
   }

   private void addToolTipButton(AbstractButton b, HotkeyData... keys) {
      Arrays.asList(keys).forEach(k -> HotkeyManager.registerHotkey(this, b, k, TooltipWay.up));
      TooltipManager.setTooltip(b, b.getToolTipText(), TooltipWay.up, 300);
      b.setToolTipText(null);
   }

   private void insertShortcuts() {
      List<InsertShortcut.Shortcut> list = new ArrayList<>();
      list.add(InsertShortcut.Shortcut.create(38, () -> this.jListVersesDefault.setSelectedIndex(Math.max(0, this.jListVersesDefault.getSelectedIndex() - 1))));
      list.add(
         InsertShortcut.Shortcut.create(
            40,
            () -> this.jListVersesDefault
                  .setSelectedIndex(Math.min(this.jListVersesDefault.getModel().getSize() - 1, this.jListVersesDefault.getSelectedIndex() + 1))
         )
      );
      list.add(InsertShortcut.Shortcut.create(39, this::next));
      list.add(InsertShortcut.Shortcut.create(34, this::next));
      list.add(InsertShortcut.Shortcut.create(37, this::previous));
      list.add(InsertShortcut.Shortcut.create(33, this::previous));
      list.add(InsertShortcut.Shortcut.create(27, this::staticExit));
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

                        BibleMain.this.popUpMenuJList(e, list);
                     }
                  }
               });
            }
         } catch (Exception var5) {
            Log.send(var5);
         }
      }
   }

   private void initComponents() {
      this.jPanelPlaylist = new JPanel();
      this.jScrollPanePlayList = new WebScrollPane(null);
      this.jListPlayList = new JList<>();
      this.jLabelTitle = new JLabel();
      this.jPanelGeneral = new JPanel();
      this.jSplitPaneBibleAndSearch = new JSplitPane();
      this.jPanelBible = new WebPanel(true);
      this.jPanelListVerses = new JPanel();
      this.jPanelVersesHeader = new JPanel();
      this.jPanelTitle = new JPanel();
      this.jPanelGoToHolyrics = new JPanel();
      this.jButtonGoToHolyrics = new WebButton();
      this.jPanelVersesHeader2 = new JPanel();
      this.jLabelBookAndChapter = new JLabel();
      this.jTabbedPaneBible = new WebTabbedPane() {
         {
            this.setTabStretchType(TabStretchType.always);
         }
      };
      this.jScrollPaneVersesDefault = new WebScrollPane(null);
      this.jListVersesDefault = new WebList();
      this.jScrollPaneVersesAux1 = new WebScrollPane(null);
      this.jListVersesAux1 = new WebList();
      this.jScrollPaneVersesAux2 = new WebScrollPane(null);
      this.jListVersesAux2 = new WebList();
      this.jPanelBibleToolButtons = new JPanel();
      this.jButtonShow = new WebButton();
      this.jSeparator5 = new JSeparator();
      this.jButtonAddPlayList = new WebButton();
      this.jSeparator7 = new JSeparator();
      this.jButtonSearch = new WebButton();
      this.jSeparator10 = new JSeparator();
      this.jButtonCustomShow = new WebButton();
      this.jSeparator8 = new JSeparator();
      this.jButtonReportError = new WebButton();
      this.jSeparator9 = new JSeparator();
      this.jButtonHelp = new WebButton();
      this.jComboBoxTheme = new WebComboBox();
      this.jPanelSettings = new JPanel();
      this.jPanelAlert = new JPanel();
      this.jPanelSearch = new JPanel();
      this.jPanelBooksChaptersAndVerses = new JPanel();
      this.jPanelRightTop = new WebPanel(true);
      this.jPanelBooks = new JPanel();
      this.jPanelBooksAbbrev = new JPanel();
      this.jScrollPaneBooksName = new JScrollPane();
      this.jPanelBooksName = new JPanel();
      this.jPanelChapterAndVerse = new JPanel();
      this.jPanelChaptersBorder = new WebPanel(true);
      this.jPanelChapters = new JPanel();
      this.jPanelChaptersPage1 = new JPanel();
      this.jPanelChaptersPage2 = new JPanel();
      this.jPanelVersesBorder = new WebPanel(true);
      this.jPanelVerses = new JPanel();
      this.jPanelVersesPage1 = new JPanel();
      this.jPanelVersesPage2 = new JPanel();
      this.jLabelTip = new JLabel();
      this.jMenuBarMenu = new JMenuBar();
      this.jMenuFile = new JMenu();
      this.jMenuItemGoToMusics = new JMenuItem();
      this.jSeparator6 = new Separator();
      this.jMenuItemFileFilesReceived = new JMenuItem();
      this.jMenuItemSettings = new JMenuItem();
      this.jMenuItemFileDisplaySettings = new JMenuItem();
      this.jSeparator2 = new Separator();
      this.jMenuItemExit = new JMenuItem();
      this.jMenuVersion = new JMenu();
      this.jMenuVersionDefaultChange = new JMenu();
      this.jMenuVersionAux1Change = new JMenu();
      this.jMenuVersionAux2Change = new JMenu();
      this.jSeparator4 = new Separator();
      this.jMenuItemVersionDownload = new JMenuItem();
      this.jMenuSettings = new JMenu();
      this.jMenuItemSavePresentationAs = new JMenuItem();
      this.jCheckBoxMenuItemFreezeScreen = new JCheckBoxMenuItem();
      this.jSeparator3 = new Separator();
      this.jMenuItemCreateShortcurDesktop = new JMenuItem();
      this.jSeparator11 = new Separator();
      this.jMenuItemPluginHolyrics = new JMenuItem();
      this.jMenuItemChangeWebServiceIP = new JMenuItem();
      this.jMenuWindows = new JMenu();
      this.jMenuWindowsItemCommunicationPanel = new JMenuItem();
      this.jMenuItemWindowsLive = new JMenuItem();
      this.jMenuAjuda = new JMenu();
      this.jMenuItemHelp = new JMenuItem();
      this.jMenuItemTutorialYouTube = new JMenuItem();
      this.jMenuItemSupportAndFeedBack = new JMenuItem();
      this.jMenuItemCheckUpdates = new JMenuItem();
      this.jMenuLanguage = new JMenu();
      this.jSeparator1 = new Separator();
      this.jMenuItemAbout = new JMenuItem();
      this.jPanelPlaylist.setFocusable(false);
      this.jPanelPlaylist.setOpaque(false);
      this.jPanelPlaylist.setLayout(new BorderLayout());
      this.jScrollPanePlayList.setVerticalScrollBarPolicy(21);
      this.jListPlayList.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.jListPlayList.setFont(new Font("Franklin Gothic Medium", 0, 14));
      this.jListPlayList.setModel(this.listModelFavorite);
      this.jListPlayList.setSelectionMode(0);
      this.jListPlayList.setToolTipText(null);
      this.jListPlayList.setCellRenderer(new ListCellRendererFavoriteVerse());
      this.jListPlayList.setCursor(new Cursor(0));
      this.jListPlayList.setFocusable(false);
      this.jListPlayList.setLayoutOrientation(1);
      this.jListPlayList.setVisibleRowCount(1);
      this.jListPlayList.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BibleMain.this.jListPlayListMouseReleased(evt);
         }
      });
      this.jScrollPanePlayList.setViewportView(this.jListPlayList);
      this.jPanelPlaylist.add(this.jScrollPanePlayList, "Center");
      this.jLabelTitle.setFont(Values.getFontBaramond(0, 40));
      this.jLabelTitle.setForeground(StyleUI.FOREGROUND_LIGHT);
      this.jLabelTitle.setHorizontalAlignment(0);
      this.jLabelTitle.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.bible")));
      this.setDefaultCloseOperation(0);
      this.setTitle("Holyrics");
      this.setExtendedState(6);
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(1000, 700));
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            BibleMain.this.formWindowClosing(evt);
         }
      });
      this.jPanelGeneral.setLayout(new BorderLayout());
      this.jSplitPaneBibleAndSearch.setDividerLocation(385);
      this.jPanelBible.setLayout(new BorderLayout());
      this.jPanelVersesHeader.setLayout(new BorderLayout(0, -2));
      this.jPanelTitle.setLayout(new BorderLayout());
      this.jPanelGoToHolyrics.setLayout(new GridBagLayout());
      this.jButtonGoToHolyrics.setFont(new Font("Georgia", 0, 15));
      this.jButtonGoToHolyrics.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/holyricsIcon24.png")));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jButtonGoToHolyrics.setText(bundle.getString("BibleMain.jButtonGoToHolyrics.text"));
      this.jButtonGoToHolyrics
         .setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.goToHolyrics")));
      this.jButtonGoToHolyrics.setFocusable(false);
      this.jButtonGoToHolyrics.setIconTextGap(8);
      this.jButtonGoToHolyrics.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonGoToHolyrics.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jButtonGoToHolyricsActionPerformed(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.insets = new Insets(2, 0, 4, 0);
      this.jPanelGoToHolyrics.add(this.jButtonGoToHolyrics, gridBagConstraints);
      this.jPanelTitle.add(this.jPanelGoToHolyrics, "First");
      this.jPanelVersesHeader.add(this.jPanelTitle, "First");
      this.jPanelVersesHeader2.setOpaque(false);
      this.jPanelVersesHeader2.setLayout(new BorderLayout());
      this.jLabelBookAndChapter.setFont(new Font("Georgia", 0, 18));
      this.jLabelBookAndChapter.setHorizontalAlignment(0);
      this.jLabelBookAndChapter.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
      this.jPanelVersesHeader2.add(this.jLabelBookAndChapter, "Center");
      this.jTabbedPaneBible.setFocusable(false);
      this.jScrollPaneVersesDefault.setHorizontalScrollBarPolicy(31);
      this.jScrollPaneVersesDefault.setVerticalScrollBarPolicy(22);
      this.jListVersesDefault.setModel(this.listModelBible);
      this.jListVersesDefault.setSelectionMode(0);
      this.jListVersesDefault
         .setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.toolTipDoubleClickToShow")));
      this.jListVersesDefault.setCellRenderer(new ListCellRendererBibleVerses());
      this.jListVersesDefault.setFocusable(false);
      this.jListVersesDefault.setMinimumSize(new Dimension(1, 1));
      this.jListVersesDefault.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BibleMain.this.jListVersesDefaultMouseReleased(evt);
         }
      });
      this.jListVersesDefault.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent evt) {
            BibleMain.this.jListVersesDefaultValueChanged(evt);
         }
      });
      this.jScrollPaneVersesDefault.setViewportView(this.jListVersesDefault);
      this.jTabbedPaneBible.addTab("", this.jScrollPaneVersesDefault);
      this.jScrollPaneVersesAux1.setHorizontalScrollBarPolicy(31);
      this.jScrollPaneVersesAux1.setVerticalScrollBarPolicy(22);
      this.jScrollPaneVersesAux1.setFocusable(false);
      this.jListVersesAux1.setModel(this.listModelBible2);
      this.jListVersesAux1.setSelectionMode(0);
      this.jListVersesAux1.setToolTipText(bundle.getString("BibleMain.jListVersesAux1.toolTipText"));
      this.jListVersesAux1.setCellRenderer(new ListCellRendererBibleVerses());
      this.jListVersesAux1.setEnabled(false);
      this.jListVersesAux1.setFocusable(false);
      this.jListVersesAux1.setMinimumSize(new Dimension(1, 1));
      this.jScrollPaneVersesAux1.setViewportView(this.jListVersesAux1);
      this.jTabbedPaneBible.addTab("", this.jScrollPaneVersesAux1);
      this.jScrollPaneVersesAux2.setHorizontalScrollBarPolicy(31);
      this.jScrollPaneVersesAux2.setVerticalScrollBarPolicy(22);
      this.jScrollPaneVersesAux2.setFocusable(false);
      this.jListVersesAux2.setModel(this.listModelBible3);
      this.jListVersesAux2.setSelectionMode(0);
      this.jListVersesAux2.setToolTipText(bundle.getString("BibleMain.jListVersesAux2.toolTipText"));
      this.jListVersesAux2.setCellRenderer(new ListCellRendererBibleVerses());
      this.jListVersesAux2.setEnabled(false);
      this.jListVersesAux2.setFocusable(false);
      this.jListVersesAux2.setMinimumSize(new Dimension(1, 1));
      this.jScrollPaneVersesAux2.setViewportView(this.jListVersesAux2);
      this.jTabbedPaneBible.addTab("", this.jScrollPaneVersesAux2);
      GroupLayout jPanelListVersesLayout = new GroupLayout(this.jPanelListVerses);
      this.jPanelListVerses.setLayout(jPanelListVersesLayout);
      jPanelListVersesLayout.setHorizontalGroup(
         jPanelListVersesLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(this.jPanelVersesHeader, -1, -1, 32767)
            .addComponent(this.jTabbedPaneBible, Alignment.TRAILING)
            .addComponent(this.jPanelVersesHeader2, -1, -1, 32767)
      );
      jPanelListVersesLayout.setVerticalGroup(
         jPanelListVersesLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jPanelListVersesLayout.createSequentialGroup()
                  .addComponent(this.jPanelVersesHeader, -2, -1, -2)
                  .addGap(2, 2, 2)
                  .addComponent(this.jPanelVersesHeader2, -2, -1, -2)
                  .addGap(2, 2, 2)
                  .addComponent(this.jTabbedPaneBible, -1, 392, 32767)
            )
      );
      this.jTabbedPaneBible.getAccessibleContext().setAccessibleName("");
      this.jPanelBible.add(this.jPanelListVerses, "Center");
      this.jPanelBibleToolButtons.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelBibleToolButtons.setLayout(new GridBagLayout());
      this.jButtonShow.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/playBlack24.png")));
      this.jButtonShow.setToolTipText(bundle.getString("BibleMain.jButtonShow.toolTipText"));
      this.jButtonShow.setFocusable(false);
      this.jButtonShow.setHorizontalTextPosition(0);
      this.jButtonShow.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonShow.setMaximumSize(new Dimension(40, 33));
      this.jButtonShow.setMinimumSize(new Dimension(40, 33));
      this.jButtonShow.setPreferredSize(new Dimension(40, 33));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.ipady = 5;
      gridBagConstraints.anchor = 15;
      gridBagConstraints.weighty = 1.0;
      this.jPanelBibleToolButtons.add(this.jButtonShow, gridBagConstraints);
      this.jSeparator5.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 3;
      this.jPanelBibleToolButtons.add(this.jSeparator5, gridBagConstraints);
      this.jButtonAddPlayList.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonAddPlayList.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/favorite24.png")));
      this.jButtonAddPlayList.setToolTipText(bundle.getString("msg.addToFavorite"));
      this.jButtonAddPlayList.setFocusable(false);
      this.jButtonAddPlayList.setHorizontalTextPosition(0);
      this.jButtonAddPlayList.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonAddPlayList.setMaximumSize(new Dimension(40, 33));
      this.jButtonAddPlayList.setMinimumSize(new Dimension(40, 33));
      this.jButtonAddPlayList.setPreferredSize(new Dimension(40, 33));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.ipady = 5;
      gridBagConstraints.anchor = 15;
      gridBagConstraints.weighty = 1.0;
      this.jPanelBibleToolButtons.add(this.jButtonAddPlayList, gridBagConstraints);
      this.jSeparator7.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.fill = 3;
      this.jPanelBibleToolButtons.add(this.jSeparator7, gridBagConstraints);
      this.jButtonSearch.setFont(new Font("Georgia", 0, 12));
      this.jButtonSearch.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/search24.png")));
      this.jButtonSearch.setToolTipText(bundle.getString("word.search"));
      this.jButtonSearch.setFocusable(false);
      this.jButtonSearch.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonSearch.setMaximumSize(new Dimension(40, 33));
      this.jButtonSearch.setMinimumSize(new Dimension(40, 33));
      this.jButtonSearch.setPreferredSize(new Dimension(40, 33));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.ipady = 5;
      gridBagConstraints.anchor = 15;
      this.jPanelBibleToolButtons.add(this.jButtonSearch, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 5;
      gridBagConstraints.fill = 2;
      this.jPanelBibleToolButtons.add(this.jSeparator10, gridBagConstraints);
      this.jButtonCustomShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/repeat24.png")));
      this.jButtonCustomShow.setToolTipText(bundle.getString("msg.customAndAutomaticPresentation"));
      this.jButtonCustomShow.setFocusable(false);
      this.jButtonCustomShow.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonCustomShow.setMaximumSize(new Dimension(40, 33));
      this.jButtonCustomShow.setMinimumSize(new Dimension(40, 33));
      this.jButtonCustomShow.setPreferredSize(new Dimension(40, 33));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.ipady = 5;
      gridBagConstraints.anchor = 11;
      this.jPanelBibleToolButtons.add(this.jButtonCustomShow, gridBagConstraints);
      this.jSeparator8.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 3;
      this.jPanelBibleToolButtons.add(this.jSeparator8, gridBagConstraints);
      this.jButtonReportError.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jButtonReportError.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/reportAlert24.png")));
      this.jButtonReportError.setToolTipText(bundle.getString("BibleMain.jButtonReportError.toolTipText"));
      this.jButtonReportError.setFocusable(false);
      this.jButtonReportError.setHorizontalTextPosition(0);
      this.jButtonReportError.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonReportError.setMaximumSize(new Dimension(40, 33));
      this.jButtonReportError.setMinimumSize(new Dimension(40, 33));
      this.jButtonReportError.setPreferredSize(new Dimension(40, 33));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.ipady = 5;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weighty = 1.0;
      this.jPanelBibleToolButtons.add(this.jButtonReportError, gridBagConstraints);
      this.jSeparator9.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 3;
      this.jPanelBibleToolButtons.add(this.jSeparator9, gridBagConstraints);
      this.jButtonHelp.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/help24.png")));
      this.jButtonHelp.setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.help")));
      this.jButtonHelp.setFocusable(false);
      this.jButtonHelp.setMargin(new Insets(2, 5, 2, 5));
      this.jButtonHelp.setMaximumSize(new Dimension(40, 33));
      this.jButtonHelp.setMinimumSize(new Dimension(40, 33));
      this.jButtonHelp.setPreferredSize(new Dimension(40, 33));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.ipady = 5;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weighty = 1.0;
      this.jPanelBibleToolButtons.add(this.jButtonHelp, gridBagConstraints);
      this.jComboBoxTheme.setMaximumRowCount(5);
      this.jComboBoxTheme.setModel(new ComboBoxModelTheme());
      this.jComboBoxTheme.setSelectedItem(Settings.SET.getBibleTheme());
      this.jComboBoxTheme.setFocusable(false);
      this.jComboBoxTheme.setPreferredSize(new Dimension(132, 65));
      this.jComboBoxTheme.setRenderer(new ComboBoxRendererTheme(112, 63));
      this.jComboBoxTheme.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            BibleMain.this.jComboBoxThemeItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.insets = new Insets(0, 4, 0, 4);
      this.jPanelBibleToolButtons.add(this.jComboBoxTheme, gridBagConstraints);
      this.jPanelSettings.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelSettings.setLayout(new GridBagLayout());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 6;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelBibleToolButtons.add(this.jPanelSettings, gridBagConstraints);
      this.jPanelAlert.setLayout(new BorderLayout());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.jPanelBibleToolButtons.add(this.jPanelAlert, gridBagConstraints);
      this.jPanelBible.add(this.jPanelBibleToolButtons, "Last");
      this.jSplitPaneBibleAndSearch.setLeftComponent(this.jPanelBible);
      this.jPanelSearch.setMinimumSize(new Dimension(300, 0));
      this.jPanelSearch.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent evt) {
            BibleMain.this.jPanelSearchComponentResized(evt);
         }
      });
      this.jPanelSearch.setLayout(new BorderLayout());
      this.jPanelBooksChaptersAndVerses.setLayout(new GridLayout(2, 1));
      this.jPanelRightTop.setLayout(new BorderLayout());
      this.jPanelBooks.setLayout(new CardLayout());
      this.jPanelBooksAbbrev.setName("books_abbrev");
      this.jPanelBooksAbbrev.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent evt) {
            BibleMain.this.jPanelBooksAbbrevMouseMoved(evt);
         }
      });
      this.jPanelBooksAbbrev.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseExited(MouseEvent evt) {
            BibleMain.this.jPanelBooksAbbrevMouseExited(evt);
         }

         @Override
         public void mousePressed(MouseEvent evt) {
            BibleMain.this.jPanelBookChapterVerseMousePressed(evt);
         }
      });
      this.jPanelBooksAbbrev.setLayout(new GridLayout(6, 11));
      this.jPanelBooks.add(this.jPanelBooksAbbrev, "books_abbrev");
      this.jPanelBooksName.setName("books_name");
      this.jPanelBooksName.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            BibleMain.this.jPanelBookChapterVerseMousePressed(evt);
         }
      });
      this.jPanelBooksName.setLayout(new GridLayout(11, 6));
      this.jScrollPaneBooksName.setViewportView(this.jPanelBooksName);
      this.jPanelBooks.add(this.jScrollPaneBooksName, "books_name");
      this.jPanelRightTop.add(this.jPanelBooks, "Center");
      this.jPanelBooksChaptersAndVerses.add(this.jPanelRightTop);
      this.jPanelChapterAndVerse.setLayout(new GridLayout(1, 2));
      this.jPanelChaptersBorder.setLayout(new BorderLayout());
      this.jPanelChapters.setLayout(new CardLayout());
      this.jPanelChaptersPage1.setName("chapter");
      this.jPanelChaptersPage1.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            BibleMain.this.jPanelBookChapterVerseMousePressed(evt);
         }
      });
      this.jPanelChaptersPage1.setLayout(new AbsoluteLayout());
      this.jPanelChapters.add(this.jPanelChaptersPage1, "chapterPage1");
      this.jPanelChaptersPage2.setName("chapter");
      this.jPanelChaptersPage2.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            BibleMain.this.jPanelBookChapterVerseMousePressed(evt);
         }
      });
      this.jPanelChaptersPage2.setLayout(new AbsoluteLayout());
      this.jPanelChapters.add(this.jPanelChaptersPage2, "chapterPage2");
      this.jPanelChaptersBorder.add(this.jPanelChapters, "Center");
      this.jPanelChapterAndVerse.add(this.jPanelChaptersBorder);
      this.jPanelVersesBorder.setLayout(new BorderLayout());
      this.jPanelVerses.setLayout(new CardLayout());
      this.jPanelVersesPage1
         .setToolTipText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.toolTipDoubleClickToShow")));
      this.jPanelVersesPage1.setName("verse");
      this.jPanelVersesPage1.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            BibleMain.this.jPanelBookChapterVerseMousePressed(evt);
         }
      });
      this.jPanelVersesPage1.setLayout(new AbsoluteLayout());
      this.jPanelVerses.add(this.jPanelVersesPage1, "versePage1");
      this.jPanelVersesPage2.setToolTipText(bundle.getString("BibleMain.jPanelVersesPage2.toolTipText"));
      this.jPanelVersesPage2.setName("verse");
      this.jPanelVersesPage2.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            BibleMain.this.jPanelBookChapterVerseMousePressed(evt);
         }
      });
      this.jPanelVersesPage2.setLayout(new AbsoluteLayout());
      this.jPanelVerses.add(this.jPanelVersesPage2, "versePage2");
      this.jPanelVersesBorder.add(this.jPanelVerses, "Center");
      this.jPanelChapterAndVerse.add(this.jPanelVersesBorder);
      this.jPanelBooksChaptersAndVerses.add(this.jPanelChapterAndVerse);
      this.jPanelSearch.add(this.jPanelBooksChaptersAndVerses, "Center");
      this.jLabelTip.setFont(new Font("Arial", 1, 14));
      this.jLabelTip.setForeground(new Color(255, 0, 0));
      this.jLabelTip.setHorizontalAlignment(0);
      this.jLabelTip.setText("Digite uma tecla para localizar rapidamente o versículo");
      this.jLabelTip.setBorder(BorderFactory.createEmptyBorder(1, 1, 4, 1));
      this.jPanelSearch.add(this.jLabelTip, "Last");
      this.jSplitPaneBibleAndSearch.setRightComponent(this.jPanelSearch);
      this.jPanelGeneral.add(this.jSplitPaneBibleAndSearch, "Center");
      this.jMenuFile.setText(bundle.getString("BibleMain.jMenuFile.text"));
      this.jMenuItemGoToMusics.setAccelerator(KeyStroke.getKeyStroke(118, 0));
      this.jMenuItemGoToMusics.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/holyricsIconBlack16.png")));
      this.jMenuItemGoToMusics.setText(bundle.getString("BibleMain.jMenuItemGoToMusics.text"));
      this.jMenuItemGoToMusics.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemGoToMusicsActionPerformed(evt);
         }
      });
      this.jMenuFile.add(this.jMenuItemGoToMusics);
      this.jMenuFile.add(this.jSeparator6);
      this.jMenuItemFileFilesReceived.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/open16.png")));
      this.jMenuItemFileFilesReceived.setText(bundle.getString("msg.filesReceived"));
      this.jMenuItemFileFilesReceived.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemFileFilesReceivedActionPerformed(evt);
         }
      });
      this.jMenuFile.add(this.jMenuItemFileFilesReceived);
      this.jMenuItemSettings.setAccelerator(KeyStroke.getKeyStroke(67, 3));
      this.jMenuItemSettings.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/settings16.png")));
      this.jMenuItemSettings.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.settings")));
      this.jMenuItemSettings.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemSettingsActionPerformed(evt);
         }
      });
      this.jMenuFile.add(this.jMenuItemSettings);
      this.jMenuItemFileDisplaySettings.setAccelerator(KeyStroke.getKeyStroke(69, 3));
      this.jMenuItemFileDisplaySettings.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/desktop16.png")));
      this.jMenuItemFileDisplaySettings.setText(bundle.getString("msg.displaySettings"));
      this.jMenuFile.add(this.jMenuItemFileDisplaySettings);
      this.jMenuFile.add(this.jSeparator2);
      this.jMenuItemExit.setAccelerator(KeyStroke.getKeyStroke(115, 8));
      this.jMenuItemExit.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/close16.png")));
      this.jMenuItemExit.setText(bundle.getString("BibleMain.jMenuItemExit.text"));
      this.jMenuItemExit.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemExitActionPerformed(evt);
         }
      });
      this.jMenuFile.add(this.jMenuItemExit);
      this.jMenuBarMenu.add(this.jMenuFile);
      this.jMenuVersion.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.version")));
      this.jMenuVersionDefaultChange.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/swap16.png")));
      this.jMenuVersionDefaultChange.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.primary")));
      this.jMenuVersion.add((JMenuItem)this.jMenuVersionDefaultChange);
      this.jMenuVersionAux1Change.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/swap16.png")));
      this.jMenuVersionAux1Change.setText(bundle.getString("msg.changeAuxiliary1"));
      this.jMenuVersion.add((JMenuItem)this.jMenuVersionAux1Change);
      this.jMenuVersionAux2Change.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/swap16.png")));
      this.jMenuVersionAux2Change.setText(bundle.getString("msg.changeAuxiliary2"));
      this.jMenuVersion.add((JMenuItem)this.jMenuVersionAux2Change);
      this.jMenuVersion.add(this.jSeparator4);
      this.jMenuItemVersionDownload.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/download16.png")));
      this.jMenuItemVersionDownload.setText(bundle.getString("BibleMain.jMenuItemVersionDownload.text"));
      this.jMenuItemVersionDownload.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemVersionDownloadActionPerformed(evt);
         }
      });
      this.jMenuVersion.add(this.jMenuItemVersionDownload);
      this.jMenuBarMenu.add(this.jMenuVersion);
      this.jMenuSettings.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.tools")));
      this.jMenuItemSavePresentationAs.setAccelerator(KeyStroke.getKeyStroke(83, 3));
      this.jMenuItemSavePresentationAs.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/saveBlack16.png")));
      this.jMenuItemSavePresentationAs.setText(bundle.getString("msg.savePresentationAs___"));
      this.jMenuItemSavePresentationAs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemSavePresentationAsActionPerformed(evt);
         }
      });
      this.jMenuSettings.add(this.jMenuItemSavePresentationAs);
      this.jCheckBoxMenuItemFreezeScreen.setAccelerator(KeyStroke.getKeyStroke(70, 3));
      this.jCheckBoxMenuItemFreezeScreen.setText(bundle.getString("BibleMain.jCheckBoxMenuItemFreezeScreen.text"));
      this.jCheckBoxMenuItemFreezeScreen.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/freeze16.png")));
      this.jCheckBoxMenuItemFreezeScreen.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jCheckBoxMenuItemFreezeScreenActionPerformed(evt);
         }
      });
      this.jMenuSettings.add((JMenuItem)this.jCheckBoxMenuItemFreezeScreen);
      this.jMenuSettings.add(this.jSeparator3);
      this.jMenuItemCreateShortcurDesktop.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/desktop16.png")));
      this.jMenuItemCreateShortcurDesktop
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.createDesktopShortcut")));
      this.jMenuItemCreateShortcurDesktop.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemCreateShortcurDesktopActionPerformed(evt);
         }
      });
      this.jMenuSettings.add(this.jMenuItemCreateShortcurDesktop);
      this.jMenuSettings.add(this.jSeparator11);
      this.jMenuItemPluginHolyrics.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/lightbulb16.png")));
      this.jMenuItemPluginHolyrics.setText("Plugin Holyrics");
      this.jMenuItemPluginHolyrics.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemPluginHolyricsActionPerformed(evt);
         }
      });
      this.jMenuSettings.add(this.jMenuItemPluginHolyrics);
      this.jMenuItemChangeWebServiceIP.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/remoteControl16.png")));
      this.jMenuItemChangeWebServiceIP.setText(bundle.getString("BibleMain.jMenuItemChangeWebServiceIP.text"));
      this.jMenuItemChangeWebServiceIP.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemChangeWebServiceIPActionPerformed(evt);
         }
      });
      this.jMenuSettings.add(this.jMenuItemChangeWebServiceIP);
      this.jMenuBarMenu.add(this.jMenuSettings);
      this.jMenuWindows.setText(bundle.getString("BibleMain.jMenuWindows.text"));
      this.jMenuWindowsItemCommunicationPanel.setAccelerator(KeyStroke.getKeyStroke(84, 3));
      this.jMenuWindowsItemCommunicationPanel.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/member16.png")));
      this.jMenuWindowsItemCommunicationPanel.setText(bundle.getString("msg.communicationPanel"));
      this.jMenuWindowsItemCommunicationPanel.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuWindowsItemCommunicationPanelActionPerformed(evt);
         }
      });
      this.jMenuWindows.add(this.jMenuWindowsItemCommunicationPanel);
      this.jMenuItemWindowsLive.setAccelerator(KeyStroke.getKeyStroke(76, 3));
      this.jMenuItemWindowsLive.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/desktop16.png")));
      this.jMenuItemWindowsLive.setText(bundle.getString("word.live"));
      this.jMenuItemWindowsLive.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemWindowsLiveActionPerformed(evt);
         }
      });
      this.jMenuWindows.add(this.jMenuItemWindowsLive);
      this.jMenuBarMenu.add(this.jMenuWindows);
      this.jMenuAjuda.setText(bundle.getString("BibleMain.jMenuAjuda.text"));
      this.jMenuItemHelp.setAccelerator(KeyStroke.getKeyStroke(112, 0));
      this.jMenuItemHelp.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/help16.png")));
      this.jMenuItemHelp.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.help")));
      this.jMenuItemHelp.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemHelpActionPerformed(evt);
         }
      });
      this.jMenuAjuda.add(this.jMenuItemHelp);
      this.jMenuItemTutorialYouTube.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/youtube.png")));
      this.jMenuItemTutorialYouTube.setText("Tutorial YouTube");
      this.jMenuItemTutorialYouTube.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemTutorialYouTubeActionPerformed(evt);
         }
      });
      this.jMenuAjuda.add(this.jMenuItemTutorialYouTube);
      this.jMenuItemSupportAndFeedBack.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/contact16.png")));
      this.jMenuItemSupportAndFeedBack.setText(bundle.getString("msg.supportAndFeedBack"));
      this.jMenuItemSupportAndFeedBack.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemSupportAndFeedBackActionPerformed(evt);
         }
      });
      this.jMenuAjuda.add(this.jMenuItemSupportAndFeedBack);
      this.jMenuItemCheckUpdates.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/update16.png")));
      this.jMenuItemCheckUpdates.setText(bundle.getString("msg.checkUpdates"));
      this.jMenuItemCheckUpdates.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemCheckUpdatesActionPerformed(evt);
         }
      });
      this.jMenuAjuda.add(this.jMenuItemCheckUpdates);
      this.jMenuLanguage.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/language16.png")));
      this.jMenuLanguage.setText(bundle.getString("word.language"));
      this.jMenuAjuda.add((JMenuItem)this.jMenuLanguage);
      this.jMenuAjuda.add(this.jSeparator1);
      this.jMenuItemAbout.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/holyricsIconBlack16.png")));
      this.jMenuItemAbout.setText(bundle.getString("word.about"));
      this.jMenuItemAbout.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BibleMain.this.jMenuItemAboutActionPerformed(evt);
         }
      });
      this.jMenuAjuda.add(this.jMenuItemAbout);
      this.jMenuBarMenu.add(this.jMenuAjuda);
      this.setJMenuBar(this.jMenuBarMenu);
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanelGeneral, -1, -1, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanelGeneral, -1, -1, 32767));
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jMenuItemExitActionPerformed(ActionEvent evt) {
      this.exit();
   }

   private void formWindowClosing(WindowEvent evt) {
      this.exit();
   }

   private void jMenuItemSettingsActionPerformed(ActionEvent evt) {
      SettingsView.main(this::refreshAfterSettings);
   }

   private void jListPlayListMouseReleased(MouseEvent evt) {
      Verse v = this.jListPlayList.getSelectedValue();
      if (Utils.isDoubleClick(evt)) {
         this.showVerse(v);
      } else if (v != null) {
         if (this.jListPlayList.locationToIndex(evt.getPoint()) == this.jListPlayList.getSelectedIndex() && SwingUtilities.isRightMouseButton(evt)) {
            this.popUpMenuJList(evt, this.jListPlayList);
         } else {
            this.goTo(v);
         }
      }
   }

   private void jMenuItemCheckUpdatesActionPerformed(ActionEvent evt) {
      Update.INSTANCE.update.check(this, true);
   }

   private void jMenuItemCreateShortcurDesktopActionPerformed(ActionEvent evt) {
      SystemUtils.createDesktopIcon(this);
   }

   private void jMenuItemAboutActionPerformed(ActionEvent evt) {
      AboutView.main(this);
   }

   private void jButtonGoToHolyricsActionPerformed(ActionEvent evt) {
      this.goToMusics();
   }

   private void jMenuItemGoToMusicsActionPerformed(ActionEvent evt) {
      this.goToMusics();
   }

   private void jMenuItemVersionDownloadActionPerformed(ActionEvent evt) {
      DownloadView.main(this, this::updateJMenuVersions);
   }

   private void jListVersesDefaultValueChanged(ListSelectionEvent evt) {
      Optional.ofNullable(this.jListVersesDefault.getSelectedValue()).ifPresent(v -> this.verseListChanged(v));
      BooleanOptional.runIfTrue(this.jListVersesDefault.getSelectedIndex() == -1, () -> this.jListVersesDefault.setSelectedIndex(this.verse - 1));
      this.jListVersesDefault.ensureIndexIsVisible(this.verse - 1);
      IntStream.rangeClosed(1, 2).forEach(i -> this.jListVersesDefault.ensureIndexIsVisible(this.jListVersesDefault.getSelectedIndex() + i));
      this.updateEnsureAux(1, 2);
      Arrays.asList(this.jListVersesDefault, this.jListVersesAux1, this.jListVersesAux2).forEach(Component::repaint);
   }

   private void jListVersesDefaultMouseReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt)) {
         this.showVerse();
      }
   }

   private void jPanelBooksAbbrevMouseExited(MouseEvent evt) {
      this.toolTip.setVisible(false);
   }

   private void jPanelBooksAbbrevMouseMoved(MouseEvent evt) {
      Component c = this.jPanelBooksAbbrev.getComponentAt(evt.getPoint());
      if (!(c instanceof BibleMainSelectableJLabel)) {
         this.toolTip.setVisible(false);
      } else {
         BibleMainSelectableJLabel sl = (BibleMainSelectableJLabel)c;
         if (!sl.isPaintedFooter()) {
            Book b = this.bible.findBookByAbbrev(sl.getText());
            this.toolTip.setText(b.name);
            this.toolTip.refresh(this);
         } else {
            this.toolTip.setVisible(false);
         }
      }
   }

   private void jMenuItemHelpActionPerformed(ActionEvent evt) {
      HelpView.main(this, EnumHelpPages.HOME_BIBLE);
   }

   private void jPanelBookChapterVerseMousePressed(MouseEvent evt) {
      JPanel panel = (JPanel)evt.getSource();
      if (Utils.isDoubleClick(evt) && !panel.getName().contains("book")) {
         this.showVerse();
      } else {
         this.jPanelBookChapterVerseMousePressed(panel.getName(), panel.getComponentAt(evt.getPoint()));
      }
   }

   private void jPanelSearchComponentResized(ComponentEvent evt) {
      SwingUtils.adjustFontOfAllJLabel(this.jPanelBooksAbbrev, 28, 0.2F);
      BibleMainSelectableJLabel.adjustFontOfAllJLabelFooter(this.jPanelBooksAbbrev);
      Arrays.asList(this.jPanelChaptersPage1, this.jPanelChaptersPage2, this.jPanelVersesPage1, this.jPanelVersesPage2)
         .forEach(BibleMainUtils::addAndResizePanels);
   }

   private void jComboBoxThemeItemStateChanged(ItemEvent evt) {
      this.changeTheme(this.jComboBoxTheme.getSelectedItem());
   }

   private void jMenuItemSavePresentationAsActionPerformed(ActionEvent evt) {
      BibleSaveVersesDialog.main(this);
   }

   private void jMenuItemSupportAndFeedBackActionPerformed(ActionEvent evt) {
      SupportAndSuggestionsDialog.main(this);
   }

   private void jMenuItemTutorialYouTubeActionPerformed(ActionEvent evt) {
      if (Desktop.isDesktopSupported()) {
         try {
            Desktop.getDesktop().browse(new URL("https://www.youtube.com/watch?v=0XDWtKTTNxY&list=PL6NYsQBmGq-l-4_8t_XMBc9w9aATXpk0G&index=5").toURI());
         } catch (Exception var3) {
         }
      }
   }

   private void jMenuItemChangeWebServiceIPActionPerformed(ActionEvent evt) {
      WebServiceUtils.popupChangeIP();
   }

   private void jMenuWindowsItemCommunicationPanelActionPerformed(ActionEvent evt) {
      CommunicationPanel.open();
   }

   private void jMenuItemWindowsLiveActionPerformed(ActionEvent evt) {
      LiveView.open();
   }

   private void jMenuItemFileFilesReceivedActionPerformed(ActionEvent evt) {
      Utils.openAppFilesReceivedFolder();
   }

   private void jCheckBoxMenuItemFreezeScreenActionPerformed(ActionEvent evt) {
      Freeze.freeze();
   }

   private void jMenuItemPluginHolyricsActionPerformed(ActionEvent evt) {
      Holyrics.openPluginNewThread();
   }

   public static void main() {
      SwingUtilities.invokeLater(() -> INSTANCE.setVisible(true));
   }

   @Override
   public void setVisible(boolean flag) {
      if (flag) {
         if (VLC.ENABLED) {
            VLC.PLAYER_CONTROL.setupToolbarButton(this);
         }

         ((ComboBoxModelTheme)this.jComboBoxTheme.getModel()).update();
         this.jComboBoxTheme.setSelectedItem(Settings.SET.getBibleTheme());
         this.jPanelAlert.add(AlertPanel.INSTANCE, "Center");
         this.jPanelAlert.revalidate();
         AlertPanel.INSTANCE.setup(this, AlertPanel.EnumSetup.BIBLE);
      } else {
         RemoteControlBibleWS.close();
      }

      super.setVisible(flag);
      BooleanOptional.runIfTrue(flag && Settings.EnumSettings.FIRST_OPEN_BIBLE.is(), () -> {
         Settings.EnumSettings.FIRST_OPEN_BIBLE.set(false);
         this.showHelp();
      });
   }

   private void exit() {
      MusicMain.safeExit();
   }

   private void showHelp() {
      String helpText = String.format("<html><div width='400px'>%s</div>", I18N.MSG_HELP_BIBLE_MAIN.val().replace("\n", "<br>"));
      JLabel label = new JLabel(helpText);
      label.setFont(new Font("Georgia", 0, 16));
      JOptionPane.showMessageDialog(null, label, I18N.WORD_HELP.val(), -1);
   }

   private void popUpMenuJList(MouseEvent e, JList list) {
      if (list.equals(this.jListPlayList)) {
         PopUpMenu.biblePopUpPlayList(this, this.jListPlayList, e);
      } else if (list.equals(this.jListVersesDefault)) {
         PopUpMenu.biblePopUpVerses(this, this.jListVersesDefault, e);
      }
   }

   private void changeVersion(final AvailableVersions av, final int i) {
      PopUpWorker.exe(new MySwingWorker<Void, Void>(this) {
         protected Void doInBackground() throws Exception {
            switch (i) {
               case 0:
                  Settings.EnumSettings.BIBLE_VERSION_KEY.set(av.getVersion());
                  BibleMain.this.bible = Settings.SET.getBible(BibleMain.this.getUploadedBible());
                  break;
               case 1:
                  Settings.EnumSettings.BIBLE_VERSION_KEY_2.set(av.getVersion());
                  BibleMain.this.bible2 = Settings.SET.getBible2(BibleMain.this.getUploadedBible());
                  break;
               case 2:
                  Settings.EnumSettings.BIBLE_VERSION_KEY_3.set(av.getVersion());
                  BibleMain.this.bible3 = Settings.SET.getBible3(BibleMain.this.getUploadedBible());
            }

            return null;
         }

         @Override
         protected void done() {
            super.done();
            BibleMain.this.versionChanged();
         }
      });
   }

   private List<Bible> getUploadedBible() {
      return Arrays.asList(this.bible, this.bible2, this.bible3).stream().filter(Objects::nonNull).collect(Collectors.toList());
   }

   private void versionChanged() {
      try {
         Verse v = this.getCurrentVerse();
         this.updateTabbedPaneTitle();
         this.updateBooks();
         this.goTo(v);
         BiblePresentationView.staticUpdateVersion();
      } catch (Exception var2) {
         Log.send(var2);
         Utils.error(this, I18N.MSG_ERROR_TRY_AGAIN.val());
      }
   }

   public void goTo(Verse verse) {
      Optional.ofNullable(verse).ifPresent(v -> this.goTo(v.book, v.chapter, v.verse));
   }

   public void goTo(int book, int chapter, int verse) {
      this.setVerse(this.bible.getVerse(book, chapter, verse, true));
   }

   private void setVerse(Verse v) {
      this.setBookInt(v.book);
      this.setChapterInt(v.chapter);
      this.setVerseInt(v.verse);
      this.updateBook();
   }

   public void makePrimary(int i) {
      try {
         Component c = this.jTabbedPaneBible.getTabComponentAt(Utils.range(--i, 0, 2));
         BooleanOptional.runIfTrue(c instanceof BibleTabbedPaneTitle, () -> ((BibleTabbedPaneTitle)c).makePrimary());
      } catch (Exception var3) {
      }
   }

   private void updateTabbedPaneTitle() {
      this.jTabbedPaneBible.setTabComponentAt(0, new BibleTabbedPaneTitle(this.bible, true) {
         @Override
         public void makePrimary() {
         }

         @Override
         public void changeVersion(AvailableVersions av) {
            BibleMain.this.changeVersion(av, 0);
         }
      });
      this.jTabbedPaneBible.setTabComponentAt(1, new BibleTabbedPaneTitle(this.bible2, false) {
         @Override
         public void makePrimary() {
            Bible b = BibleMain.this.bible;
            BibleMain.this.bible = BibleMain.this.bible2;
            BibleMain.this.bible2 = b;
            Settings.EnumSettings.BIBLE_VERSION_KEY.set(BibleMain.this.bible.abbrev);
            Settings.EnumSettings.BIBLE_VERSION_KEY_2.set(BibleMain.this.bible2.abbrev);
            BibleMain.this.versionChanged();
            BibleMain.this.updateJMenuVersions();
            BibleMain.this.jTabbedPaneBible.setSelectedIndex(0);
         }

         @Override
         public void changeVersion(AvailableVersions av) {
            BibleMain.this.changeVersion(av, 1);
         }
      });
      this.jTabbedPaneBible.setTabComponentAt(2, new BibleTabbedPaneTitle(this.bible3, false) {
         @Override
         public void makePrimary() {
            Bible b = BibleMain.this.bible;
            BibleMain.this.bible = BibleMain.this.bible3;
            BibleMain.this.bible3 = b;
            Settings.EnumSettings.BIBLE_VERSION_KEY.set(BibleMain.this.bible.abbrev);
            Settings.EnumSettings.BIBLE_VERSION_KEY_3.set(BibleMain.this.bible3.abbrev);
            BibleMain.this.versionChanged();
            BibleMain.this.updateJMenuVersions();
            BibleMain.this.jTabbedPaneBible.setSelectedIndex(0);
         }

         @Override
         public void changeVersion(AvailableVersions av) {
            BibleMain.this.changeVersion(av, 2);
         }
      });
      this.jTabbedPaneBible.setToolTipTextAt(0, this.bible.version);
      this.jTabbedPaneBible.setToolTipTextAt(1, this.bible2.version);
      this.jTabbedPaneBible.setToolTipTextAt(2, this.bible3.version);
   }

   private void fastSearch(char c) {
      BooleanOptional.runIfTrue(!this.blockFastSearch, () -> this.bibleFastSearchView.start(c));
   }

   public void addPlayList(Verse verse) {
      SwingUtilities.invokeLater(() -> {
         if (verse != null) {
            this.addVerseInModel(verse);
            this.jTabbedPaneBible.setSelectedIndex(0);
            this.jListPlayList.setSelectedValue(verse, true);
         }
      });
   }

   public void addPlayList(List<Verse> verses) {
      SwingUtilities.invokeLater(() -> verses.stream().filter(Objects::nonNull).forEach(this::addVerseInModel));
   }

   private void addVerseInModel(Verse v) {
      if (v != null) {
         while (this.listModelFavorite.contains(v)) {
            this.listModelFavorite.removeElement(v);
         }

         this.listModelFavorite.add(0, v);

         while (this.listModelFavorite.size() > 50) {
            this.listModelFavorite.remove(this.listModelFavorite.getSize() - 1);
         }

         BooleanOptional.runIfTrue(!this.collapsiblePlayList.isExpanded(), () -> this.collapsiblePlayList.setExpanded(true));
         this.collapsiblePlayList.revalidate();
         this.collapsiblePlayList.repaint();
         this.jPanelChapterAndVerse.revalidate();
         this.jPanelChapterAndVerse.repaint();
      }
   }

   public boolean favoriteContains(Verse v) {
      return v == null || this.listModelFavorite.contains(v);
   }

   public void removeFromFavorite(Verse verse) {
      this.listModelFavorite.removeElement(verse);
   }

   public void clearPlayList() {
      this.listModelFavorite.removeAllElements();
   }

   public void showVerse(Verse verse) {
      Optional.ofNullable(verse).ifPresent(v -> {
         if (BiblePresentationView.staticIsVisible() && BiblePresentationView.staticIsAllVerses()) {
            if (!BiblePresentationView.staticSetVerse(v, true)) {
               BiblePresentationView.show(this.bible.verses, v, Settings.SET.getBibleTheme());
            }
         } else {
            BiblePresentationView.show(this.bible.verses, v, Settings.SET.getBibleTheme());
         }
      });
   }

   private void showVerse() {
      this.showVerse(this.getCurrentVerse());
   }

   public static void staticShow() {
      BooleanOptional.runIfTrue(!PresentationView.staticIsVisible(), INSTANCE::showVerse);
   }

   public Verse getCurrentVerse() {
      return this.bible.getVerse(this.book, this.chapter, this.verse, true);
   }

   public Bible getCurrentBible() {
      return this.bible;
   }

   private void refreshAfterSettings() {
   }

   private void goToMusics() {
      super.setVisible(false);
      MusicMain.main(false, null);
   }

   private VerseSearchView createVerseSearchDialog() {
      return new VerseSearchView(Stream.of(this.bible, this.bible2, this.bible3).distinct().collect(Collectors.toList()), this::goTo);
   }

   private void addBooks() {
      for (int i = 0; i < 66; i++) {
         BibleMainSelectableJLabel sl = BibleMainSelectableJLabel.createDefault(this.bible.books.get(i).abbrev, this.bible.books.get(i).name);
         sl.setBackground(this.getPanelColor(Values.COLOR_BOOK_AND_CHAPTER, i));
         this.booksAbbrev.add(sl);
      }

      this.booksAbbrev.stream().forEach(this.jPanelBooksAbbrev::add);
      SwingUtils.adjustFontOfAllJLabel(this.jPanelBooksAbbrev, 28, 0.2F);
      BibleMainSelectableJLabel.adjustFontOfAllJLabelFooter(this.jPanelBooksAbbrev);
      this.jPanelBooksAbbrev.setMinimumSize(new Dimension());

      for (int i = 0; i < 66; i++) {
         BibleMainSelectableJLabel sl = BibleMainSelectableJLabel.createDefault(this.bible.books.get(i).name);
         sl.setBackground(this.getPanelColor(Values.COLOR_BOOK_AND_CHAPTER, i));
         sl.setSize(225, 40);
         sl.setPreferredSize(sl.getSize());
         this.booksName.add(sl);
         sl.setFontSize(26);
      }

      this.booksName.stream().forEach(this.jPanelBooksName::add);
      SwingUtils.adjustFontOfAllJLabel(this.jPanelBooksName, 26, 0.15F);
      this.jPanelBooksName.setMinimumSize(new Dimension());
   }

   private void updateBooks() throws Exception {
      for (int i = 0; i < 66; i++) {
         this.booksAbbrev.get(i).setText(this.bible.books.get(i).abbrev);
         this.booksName.get(i).setText(this.bible.books.get(i).name);
      }

      SwingUtils.adjustFontOfAllJLabel(this.jPanelBooksAbbrev, 28, 0.2F);
      BibleMainSelectableJLabel.adjustFontOfAllJLabelFooter(this.jPanelBooksAbbrev);
      SwingUtils.adjustFontOfAllJLabel(this.jPanelBooksName, 26, 0.15F);
   }

   private Color getPanelColor(Color[] colors, int index) {
      int total = 0;

      for (int i = 0; i < Values.BIBLE_MAP_DIVISION.length; i++) {
         total += Values.BIBLE_MAP_DIVISION[i];
         if (index < total) {
            return colors[i];
         }
      }

      return Color.BLACK;
   }

   private void search() {
      WebListStyle.decorateSelection = false;
      WebListStyle.highlightRolloverCell = false;
      VerseSearchView dialog = this.createVerseSearchDialog();
      WebListStyle.decorateSelection = true;
      WebListStyle.highlightRolloverCell = true;
      dialog.setVisible(true);
   }

   private void updateJMenuVersions() {
      this.updateJMenuVersions(this.jMenuVersionDefaultChange, this.bible, 0);
      this.updateJMenuVersions(this.jMenuVersionAux1Change, this.bible2, 1);
      this.updateJMenuVersions(this.jMenuVersionAux2Change, this.bible3, 2);
   }

   private void updateJMenuVersions(JMenu jmenu, Bible bible, int index) {
      jmenu.removeAll();
      ButtonGroup group = new ButtonGroup();
      String lang = Locale.getDefault().getLanguage().toLowerCase();
      AvailableVersions.getVersions()
         .forEach(
            av -> {
               Component[] cs = jmenu.getMenuComponents();
               if (cs.length > 0
                  && cs[cs.length - 1] instanceof JRadioButtonMenuItem
                  && !av.getVersion().startsWith(lang)
                  && ((JRadioButtonMenuItem)cs[cs.length - 1]).getName().startsWith(lang)) {
                  jmenu.add(new Separator());
               }

               JRadioButtonMenuItem version = new JRadioButtonMenuItem(av.toString());
               version.setName(av.getVersion());
               group.add(version);
               version.setSelected(av.languageAndVersion.contains(bible.version));
               version.addActionListener(e -> this.changeVersion(av, index));
               jmenu.add((JMenuItem)version);
            }
         );
   }

   private void reportError() {
      ReportError.main(this, this.jListVersesDefault.getSelectedValue());
   }

   public void setBookInt(int book) {
      this.book = Utils.range(book, 1, 66);
      this.chapter = 1;
      this.verse = 1;
   }

   public void setChapterInt(int chapter) {
      this.chapter = Utils.range(chapter, 1, this.bible.getMap().get(this.book).size());
      this.verse = 1;
   }

   public void setVerseInt(int verse) {
      this.verse = Utils.range(verse, 1, this.bible.getMap().get(this.book).getOrDefault(this.chapter, 1));
   }

   protected Book getBook() {
      return this.bible.books.get(this.book - 1);
   }

   protected List<Verse> getChapter() {
      return this.getBook().getVerses(this.chapter);
   }

   private void jPanelBookChapterVerseMousePressed(String name, Component c) {
      try {
         if (c != null && c instanceof BibleMainSelectableJLabel) {
            BibleMainSelectableJLabel label = (BibleMainSelectableJLabel)c;
            switch (name) {
               case "books_abbrev":
                  this.setBookIntByAbbrev(label.getText());
                  this.updateBook();
                  break;
               case "books_name":
                  this.setBookIntByName(label.getText());
                  this.updateBook();
                  break;
               case "chapter":
                  String var9 = label.getText();
                  switch (var9) {
                     case "»":
                        ((CardLayout)this.jPanelChapters.getLayout()).show(this.jPanelChapters, "chapterPage2");
                        Utils.clearDoubleClick();
                        return;
                     case "«":
                        ((CardLayout)this.jPanelChapters.getLayout()).show(this.jPanelChapters, "chapterPage1");
                        Utils.clearDoubleClick();
                        return;
                     default:
                        this.setChapterInt(Integer.parseInt(label.getText()));
                        this.updateChapter();
                        return;
                  }
               case "verse":
                  String var6 = label.getText();
                  switch (var6) {
                     case "»":
                        ((CardLayout)this.jPanelVerses.getLayout()).show(this.jPanelVerses, "versePage2");
                        Utils.clearDoubleClick();
                        break;
                     case "«":
                        ((CardLayout)this.jPanelVerses.getLayout()).show(this.jPanelVerses, "versePage1");
                        Utils.clearDoubleClick();
                        break;
                     default:
                        this.setVerseInt(Integer.parseInt(label.getText()));
                        this.updateVerse();
                  }
            }
         }
      } catch (Exception var8) {
      }
   }

   private void updateBook() {
      Arrays.asList(this.booksName, this.booksAbbrev).forEach(b -> {
         b.stream().filter(BibleMainSelectableJLabel::isSelected).forEach(l -> l.setSelected(false));
         ((BibleMainSelectableJLabel)b.get(this.book - 1)).setSelected(true);
      });
      this.chaptersLabel.clear();
      IntStream.rangeClosed(1, this.bible.getMap().get(this.book).size())
         .mapToObj(String::valueOf)
         .map(BibleMainSelectableJLabel::createDefault)
         .forEach(this.chaptersLabel::add);
      BibleMainUtils.addPages(
         this.chaptersLabel, this.jPanelChaptersPage1, this.jPanelChaptersPage2, this.getPanelColor(Values.COLOR_BOOK_AND_CHAPTER, this.book - 1)
      );
      this.jPanelBooksAbbrev.repaint();
      this.jPanelBooksName.repaint();
      ((CardLayout)this.jPanelChapters.getLayout()).show(this.jPanelChapters, "chapterPage1");
      this.updateChapter();
   }

   private void setBookIntByAbbrev(String abbrev) {
      try {
         this.setBookInt(this.bible.findBookByAbbrev(abbrev).getIdInt());
      } catch (Exception var3) {
      }
   }

   private void setBookIntByName(String name) {
      try {
         this.setBookInt(this.bible.findBookByName(name).getIdInt());
      } catch (Exception var3) {
      }
   }

   private void updateChapter() {
      this.chaptersLabel.stream().filter(BibleMainSelectableJLabel::isSelected).forEach(l -> l.setSelected(false));
      this.chaptersLabel.get(this.chapter - 1).setSelected(true);
      this.versesLabel.clear();
      List<Verse> verses = this.getChapter();
      IntStream.rangeClosed(1, verses.size()).mapToObj(String::valueOf).map(BibleMainSelectableJLabel::createDefault).forEach(this.versesLabel::add);
      BibleMainUtils.addPages(this.versesLabel, this.jPanelVersesPage1, this.jPanelVersesPage2, this.getPanelColor(Values.COLOR_BOOK_VERSE, this.book - 1));
      ((CardLayout)this.jPanelVerses.getLayout()).show(this.jPanelVerses, "versesPage1");
      this.jLabelBookAndChapter.setText(this.getBook().name + ": " + this.chapter);
      this.listModelBible.removeAllElements(false);
      this.listModelBible.addElements(verses, true);
      this.jSplitPaneBibleAndSearch.setEnabled(false);
      this.updateAux(this.listModelBible2, this.bible2);
      this.updateAux(this.listModelBible3, this.bible3);
      this.updateVerse();
   }

   private void updateAux(ListModelVerse model, Bible bible) {
      List<Verse> list = bible.books.get(this.book - 1).getVerses(this.chapter);
      model.removeAllElements(false);
      model.addElements(list, true);
   }

   private void updateVerse() {
      this.versesLabel.stream().filter(BibleMainSelectableJLabel::isSelected).forEach(l -> l.setSelected(false));
      this.versesLabel.get(this.verse - 1).setSelected(true);
      this.jListVersesDefault.setSelectedIndex(this.verse - 1);
      IntStream.rangeClosed(-1, 1).forEach(i -> this.jListVersesDefault.ensureIndexIsVisible(this.verse + i));
      this.updateEnsureAux(-1, 1);
      this.repaint();
   }

   public static void verseDisplayedChanged(Verse verse) {
      Verse selected = INSTANCE.getCurrentVerse();
      if (verse != null && INSTANCE != null) {
         if (selected.book != verse.book) {
            if (Math.abs(selected.book - verse.book) != 1) {
               return;
            }

            int ind = INSTANCE.bible.verses.indexOf(selected);
            int ind2 = INSTANCE.bible.verses.indexOf(verse);
            if (Math.abs(ind - ind2) <= 3) {
               INSTANCE.setVerse(verse);
            }
         }

         if (selected.chapter == verse.chapter) {
            INSTANCE.jListVersesDefault.setSelectedValue(verse, true);
            INSTANCE.jPanelChapterAndVerse.repaint();
         } else {
            if (selected.chapter + 1 == verse.chapter && verse.verse <= 3
               || selected.chapter - 1 == verse.chapter
                  && verse.verse >= INSTANCE.bible.getMap().get(verse.getBook().getIdInt()).getOrDefault(verse.chapter, 1) - 3) {
               INSTANCE.setChapterInt(verse.chapter);
               INSTANCE.setVerseInt(verse.verse);
               INSTANCE.updateChapter();
               INSTANCE.jPanelChapterAndVerse.repaint();
            }
         }
      }
   }

   private void verseListChanged(Verse v) {
      Optional.ofNullable(v)
         .ifPresent(
            i -> {
               this.setVerseInt(i.verse);
               this.versesLabel.stream().filter(BibleMainSelectableJLabel::isSelected).forEach(l -> l.setSelected(false));
               BibleMainSelectableJLabel vl = this.versesLabel.get(this.verse - 1);
               vl.setSelected(true);
               ((CardLayout)this.jPanelVerses.getLayout())
                  .show(this.jPanelVerses, Arrays.asList(this.jPanelVersesPage1.getComponents()).contains(vl) ? "versePage1" : "versePage2");
               vl = this.chaptersLabel.get(this.chapter - 1);
               ((CardLayout)this.jPanelChapters.getLayout())
                  .show(this.jPanelChapters, Arrays.asList(this.jPanelChaptersPage1.getComponents()).contains(vl) ? "chapterPage1" : "chapterPage2");
            }
         );
   }

   private void fontChanged() {
      Arrays.asList(this.listModelBible, this.listModelBible2, this.listModelBible3).forEach(ListModelVerse::refresh);
      ListCellRendererFoundVerses.setFontName(Settings.EnumSettings.BIBLE_FONT_NAME.getString());
      ListCellRendererFoundVerses.setFontSize(Settings.EnumSettings.BIBLE_FONT_SIZE.getInt());
   }

   private void next() {
      BooleanOptional.runIfTrue(!BiblePresentationView.staticNext(), MusicMain::staticNext);
   }

   private void previous() {
      BooleanOptional.runIfTrue(!BiblePresentationView.staticPrevious(), MusicMain::staticPrevious);
   }

   private void staticExit() {
      BooleanOptional.runIfTrue(!BiblePresentationView.staticExit() && !AutomaticShowView.staticExit(), MusicMain::staticExit);
   }

   private void changeTheme(Object obj) {
      if (obj instanceof Theme) {
         Settings.EnumSettings.BIBLE_THEME.set(((Theme)obj).id);
         BiblePresentationView.staticSetTheme((Theme)obj);
      } else {
         this.jComboBoxTheme.setSelectedItem(Settings.SET.getBibleTheme());
      }
   }

   private void updateEnsureAux(int i1, int i2) {
      Arrays.asList(this.jListVersesAux1, this.jListVersesAux2).forEach(list -> {
         list.setSelectedIndex(Math.min(this.jListVersesDefault.getSelectedIndex(), list.getModel().getSize() - 1));
         list.ensureIndexIsVisible(list.getSelectedIndex() + i1);
         list.ensureIndexIsVisible(list.getSelectedIndex() + i2);
      });
   }

   @Override
   public Bible getBible() {
      return this.bible;
   }

   @Override
   public void fastSearchVerseFound(int book, int chapter, int verse) {
      this.blockFastSearch = true;
      this.goTo(book, chapter, verse);
      Utils.startThreadDaemon(() -> {
         Utils.sleep(2000L);
         this.blockFastSearch = false;
      });
   }

   public void fastSearchVerse(BibleFastSearchListener listener) {
      this.bibleFastSearchView.start('\u0000', listener);
   }

   public void setDisplayedVerses(int i) {
      this.toggleButtonDisplayedButton[Utils.range(--i, 0, this.toggleButtonDisplayedButton.length - 1)].doClick();
   }

   public List<String> getTabVersions() {
      return this.getUploadedBible().stream().map(b -> b.version).collect(Collectors.toList());
   }

   public void repaintVerseList() {
      Arrays.asList(this.jListVersesDefault, this.jListVersesAux1, this.jListVersesAux2).forEach(Component::repaint);
   }
}
