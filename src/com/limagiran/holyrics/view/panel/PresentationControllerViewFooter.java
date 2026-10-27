package com.limagiran.holyrics.view.panel;

import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.toolbar.ToolbarStyle;
import com.alee.laf.toolbar.WebToolBar;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.BackgroundImageFile;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ListCellRendererPresentationImage;
import com.limagiran.holyrics.swing.ListCellRendererPresentationText;
import com.limagiran.holyrics.swing.ListCellRendererTheme;
import com.limagiran.holyrics.swing.ListModelTheme;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.ReflectionUtils;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.view.PresentationJDialog;
import com.limagiran.holyrics.view.PresentationView;
import com.limagiran.holyrics.view.control.PresentationControl;
import com.limagiran.holyrics.view.control.PresentationUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class PresentationControllerViewFooter extends WebPanel implements PresentationControl {
   private static final String RENDERER_MODE_TEXT = "text";
   private static final String RENDERER_MODE_IMAGE = "image";
   private static final boolean SHOW_PREVIOUS_NEXT_BUTTON = MyProperties.EnumProperties.DISPLAY_PREVIOUS_NEXT_BUTTON_FOOTER_CONTROL.is();
   private final MusicMain parent;
   private final PresentationView display;
   private final List<Presentation> presentations;
   private final DefaultListModel<Presentation> listModelPresentation = new DefaultListModel<>();
   private final Dimension presentationSize;
   private boolean exit;
   private ButtonGroup buttonGroupControlMode;
   private Filler filler1;
   private JButton jButtonNext;
   private JButton jButtonPrevious;
   private JComboBox<String> jComboBoxNumberOfLines;
   private JLabel jLabelHeader;
   private JLabel jLabelMinimizeMaximize;
   private JList<Presentation> jListPresentations;
   private JPanel jPanelLeftToolBar;
   private JPanel jPanelLeftToolBarBottom;
   private JPanel jPanelSlideOnDisplay;
   private JPanel jPanelThemeListFooter;
   private JScrollPane jScrollPanePresentations;
   private JToggleButton jToggleButtonThemeList;
   private JToggleButton jToggleButtonViewOnlyText;
   private JToggleButton jToggleButtonViewSlide;
   private JToolBar jToolBar;

   public PresentationControllerViewFooter(
      MusicMain parent, List<Presentation> presentations, int initIndex, Theme theme, ScreenDeviceSettings.EnumDeviceType deviceType
   ) {
      super(true);
      this.parent = parent;
      this.presentations = presentations;
      this.display = PresentationView.create(presentations, this.getInstance(), initIndex, true, theme, deviceType);
      this.display.setVisible(true);
      ScreenDeviceSettings.GraphicsDeviceCustom device = VideoUtils.getDeviceToPresentation(deviceType);
      this.presentationSize = device.getBounds().getSize();
      this.presentations.forEach(this.listModelPresentation::addElement);
      this.setIndex(initIndex);
      this.initComponents();
      this.init(deviceType);
   }

   private void init(ScreenDeviceSettings.EnumDeviceType deviceType) {
      this.jListPresentations.setVisibleRowCount(Settings.EnumSettings.FOOTER_PANEL_NUMBER_OF_LINES.getInt());
      this.jButtonPrevious.addActionListener(e -> this.previous());
      this.jButtonNext.addActionListener(e -> this.next());
      if (!SHOW_PREVIOUS_NEXT_BUTTON) {
         this.jToolBar.remove(this.jButtonPrevious);
         this.jToolBar.remove(this.jButtonNext);
      }

      this.jToggleButtonViewSlide.addActionListener(e -> this.changeRendererMode("image"));
      this.jToggleButtonViewOnlyText.addActionListener(e -> this.changeRendererMode("text"));
      this.setMargin(2).setRound(5);
      ((WebToolBar)this.jToolBar).setToolbarStyle(ToolbarStyle.attached);
      ((WebToolBar)this.jToolBar).setUndecorated(true);
      this.updateListRenderer();
      this.jListPresentations.setSelectedIndex(this.display.getIndex());
      this.insertShortcuts();
      WebLafUtils.setupWebLafBorderLayout(this.jPanelSlideOnDisplay);
      this.jListPresentations
         .addKeyListener(
            new KeyAdapter() {
               @Override
               public void keyPressed(KeyEvent e) {
                  switch (e.getKeyCode()) {
                     case 33:
                        PresentationControllerViewFooter.this.previous();
                        e.consume();
                        break;
                     case 34:
                        PresentationControllerViewFooter.this.next();
                        e.consume();
                        break;
                     case 39:
                        BooleanOptional.runIfTrue(
                           PresentationControllerViewFooter.this.jListPresentations.getSelectedIndex()
                              == PresentationControllerViewFooter.this.presentations.size() - 1,
                           PresentationControllerViewFooter.this::next
                        );
                  }
               }
            }
         );
      this.changePresentation(this.display.getIndex());
      this.minimize(Settings.EnumSettings2.FOOTER_CONTROL_MINIMIZE.is());
      if (deviceType != null && deviceType.isMusicTextOrBible()) {
         this.setupThemeListFooter();
      }
   }

   private void insertShortcuts() {
      List<InsertShortcut.Shortcut> list = new ArrayList<>();
      list.add(InsertShortcut.Shortcut.create(39, this::next));
      list.add(InsertShortcut.Shortcut.create(10, this::next));
      list.add(InsertShortcut.Shortcut.create(34, this::next));
      list.add(InsertShortcut.Shortcut.create(37, this::previous));
      list.add(InsertShortcut.Shortcut.create(33, this::previous));
      list.add(InsertShortcut.Shortcut.create(27, this::exit));
      list.add(InsertShortcut.Shortcut.create(119, () -> this.wallpaper(this.isWallpaper())));
      list.add(InsertShortcut.Shortcut.create(120, () -> this.blank(this.isBlank())));
      list.add(InsertShortcut.Shortcut.create(121, () -> this.black(this.isBlack())));
      list.add(InsertShortcut.Shortcut.create(113, 64, () -> {
         Presentation p = this.jListPresentations.getSelectedValue();
         if (PresentationUtils.checkEditRealTime(p)) {
            PresentationUtils.editRealTime(p, this.jListPresentations::repaint);
         }
      }));
      list.add(InsertShortcut.Shortcut.create(77, 128, () -> this.minimize(this.isWebColoredBackground())));
      InsertShortcut.add(this, list);
   }

   private PresentationControllerViewFooter getInstance() {
      return this;
   }

   private void initComponents() {
      this.buttonGroupControlMode = new ButtonGroup();
      this.jPanelSlideOnDisplay = new WebPanel();
      this.jLabelHeader = new JLabel();
      this.jLabelMinimizeMaximize = new JLabel();
      this.jScrollPanePresentations = new JScrollPane();
      this.jListPresentations = new WebList();
      this.jPanelLeftToolBar = new JPanel();
      this.filler1 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
      this.jPanelLeftToolBarBottom = new JPanel();
      this.jToolBar = new WebToolBar(0);
      this.jButtonNext = new JButton();
      this.jButtonPrevious = new JButton();
      this.jToggleButtonViewOnlyText = new JToggleButton();
      this.jToggleButtonViewSlide = new JToggleButton();
      this.jToggleButtonThemeList = new JToggleButton();
      this.jComboBoxNumberOfLines = new JComboBox<>();
      this.jPanelThemeListFooter = new JPanel();
      this.jPanelSlideOnDisplay.setOpaque(false);
      this.jPanelSlideOnDisplay.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            PresentationControllerViewFooter.this.jPanelSlideOnDisplayMouseReleased(evt);
         }
      });
      this.jPanelSlideOnDisplay.setLayout(new BorderLayout(8, 0));
      this.jLabelHeader.setFont(new Font("Georgia", 0, 13));
      this.jLabelHeader.setHorizontalAlignment(0);
      this.jLabelHeader.setText(PresentationControl.descriptionOfShortcuts());
      this.jPanelSlideOnDisplay.add(this.jLabelHeader, "Center");
      this.jLabelMinimizeMaximize.setFont(new Font("Arial", 0, 12));
      this.jLabelMinimizeMaximize.setForeground(new Color(50, 50, 50));
      this.jPanelSlideOnDisplay.add(this.jLabelMinimizeMaximize, "East");
      this.add(this.jPanelSlideOnDisplay, "First");
      this.jScrollPanePresentations.setHorizontalScrollBarPolicy(32);
      this.jScrollPanePresentations.setVerticalScrollBarPolicy(21);
      this.jScrollPanePresentations.setFocusable(false);
      this.jListPresentations.setFont(new Font("Georgia", 0, 14));
      this.jListPresentations.setModel(this.listModelPresentation);
      this.jListPresentations.setSelectionMode(0);
      this.jListPresentations.setFocusable(false);
      this.jListPresentations.setLayoutOrientation(1);
      this.jListPresentations.setVisibleRowCount(1);
      this.jListPresentations.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            PresentationControllerViewFooter.this.jListPresentationsMouseReleased(evt);
         }
      });
      this.jListPresentations.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent evt) {
            PresentationControllerViewFooter.this.jListPresentationsValueChanged(evt);
         }
      });
      this.jScrollPanePresentations.setViewportView(this.jListPresentations);
      this.add(this.jScrollPanePresentations, "Center");
      this.jPanelLeftToolBar.setOpaque(false);
      this.jPanelLeftToolBar.setLayout(new BorderLayout());
      this.jPanelLeftToolBar.add(this.filler1, "Center");
      this.jPanelLeftToolBarBottom.setOpaque(false);
      this.jPanelLeftToolBarBottom.setLayout(new BorderLayout());
      this.jToolBar.setFloatable(false);
      this.jToolBar.setOrientation(1);
      this.jToolBar.setRollover(true);
      this.jButtonNext.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/next24.png")));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jButtonNext.setToolTipText(bundle.getString("word.next"));
      this.jButtonNext.setFocusable(false);
      this.jButtonNext.setHorizontalTextPosition(0);
      this.jButtonNext.setMargin(new Insets(0, 4, 0, 4));
      this.jButtonNext.setVerticalTextPosition(3);
      this.jToolBar.add(this.jButtonNext);
      this.jButtonPrevious.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/previous24.png")));
      this.jButtonPrevious.setToolTipText(bundle.getString("word.previous"));
      this.jButtonPrevious.setFocusable(false);
      this.jButtonPrevious.setHorizontalTextPosition(0);
      this.jButtonPrevious.setMargin(new Insets(0, 4, 0, 4));
      this.jButtonPrevious.setVerticalTextPosition(3);
      this.jToolBar.add(this.jButtonPrevious);
      this.buttonGroupControlMode.add(this.jToggleButtonViewOnlyText);
      this.jToggleButtonViewOnlyText.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/textFields24.png")));
      this.jToggleButtonViewOnlyText.setSelected(Settings.EnumSettings.PRESENTATION_CONTROL_RENDERER_MODE.getString().equals("text"));
      this.jToggleButtonViewOnlyText.setToolTipText(bundle.getString("msg.textMode"));
      this.jToggleButtonViewOnlyText.setFocusable(false);
      this.jToggleButtonViewOnlyText.setHorizontalTextPosition(0);
      this.jToggleButtonViewOnlyText.setMargin(new Insets(0, 4, 0, 4));
      this.jToggleButtonViewOnlyText.setVerticalTextPosition(3);
      this.jToolBar.add(this.jToggleButtonViewOnlyText);
      this.buttonGroupControlMode.add(this.jToggleButtonViewSlide);
      this.jToggleButtonViewSlide.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/theme24.png")));
      this.jToggleButtonViewSlide.setSelected(Settings.EnumSettings.PRESENTATION_CONTROL_RENDERER_MODE.getString().equals("image"));
      this.jToggleButtonViewSlide.setToolTipText(bundle.getString("msg.originalMode"));
      this.jToggleButtonViewSlide.setFocusable(false);
      this.jToggleButtonViewSlide.setHorizontalTextPosition(0);
      this.jToggleButtonViewSlide.setMargin(new Insets(0, 4, 0, 4));
      this.jToggleButtonViewSlide.setVerticalTextPosition(3);
      this.jToolBar.add(this.jToggleButtonViewSlide);
      this.jToggleButtonThemeList.setFont(new Font("Arial", 0, 10));
      this.jToggleButtonThemeList.setText(bundle.getString("word.themes"));
      this.jToggleButtonThemeList.setFocusable(false);
      this.jToggleButtonThemeList.setHorizontalTextPosition(0);
      this.jToggleButtonThemeList.setMargin(new Insets(0, 0, 0, 0));
      this.jToggleButtonThemeList.setVerticalTextPosition(3);
      this.jToolBar.add(this.jToggleButtonThemeList);
      this.jPanelLeftToolBarBottom.add(this.jToolBar, "Center");
      this.jComboBoxNumberOfLines.setFont(new Font("Georgia", 1, 18));
      this.jComboBoxNumberOfLines.setMaximumRowCount(3);
      this.jComboBoxNumberOfLines.setModel(new DefaultComboBoxModel<>(new String[]{"1", "2", "3"}));
      this.jComboBoxNumberOfLines.setSelectedItem(String.valueOf(Settings.EnumSettings.FOOTER_PANEL_NUMBER_OF_LINES.getInt()));
      this.jComboBoxNumberOfLines.setFocusable(false);
      this.jComboBoxNumberOfLines.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            PresentationControllerViewFooter.this.jComboBoxNumberOfLinesItemStateChanged(evt);
         }
      });
      this.jPanelLeftToolBarBottom.add(this.jComboBoxNumberOfLines, "First");
      this.jPanelLeftToolBar.add(this.jPanelLeftToolBarBottom, "Last");
      this.add(this.jPanelLeftToolBar, "Before");
      this.jPanelThemeListFooter.setOpaque(false);
      this.jPanelThemeListFooter.setLayout(new BorderLayout());
      this.add(this.jPanelThemeListFooter, "Last");
   }

   private void jListPresentationsValueChanged(ListSelectionEvent evt) {
   }

   private void jListPresentationsMouseReleased(MouseEvent evt) {
      if (SwingUtilities.isLeftMouseButton(evt)) {
         this.changePresentation(this.jListPresentations.getSelectedIndex());
      } else if (SwingUtilities.isRightMouseButton(evt)) {
         this.rightButton(evt);
      }

      this.requestFocus();
   }

   private void jComboBoxNumberOfLinesItemStateChanged(ItemEvent evt) {
      if (evt.getStateChange() == 1) {
         Settings.EnumSettings.FOOTER_PANEL_NUMBER_OF_LINES.set(Utils.intOrElse(evt.getItem(), 1));
         SwingUtilities.invokeLater(() -> {
            this.jListPresentations.setVisibleRowCount(Settings.EnumSettings.FOOTER_PANEL_NUMBER_OF_LINES.getInt());
            this.jListPresentations.revalidate();
            this.jListPresentations.repaint();
            Optional.ofNullable(this.getParent()).ifPresent(p -> {
               p.revalidate();
               p.repaint();
            });
         });
      }
   }

   private void jPanelSlideOnDisplayMouseReleased(MouseEvent evt) {
      this.minimize(Settings.EnumSettings2.FOOTER_CONTROL_MINIMIZE.set(this.jScrollPanePresentations.isVisible()));
   }

   @Override
   public final void setIndex(int index) {
      this.display.setIndex(index);
      if (this.jListPresentations != null) {
         this.jListPresentations.getSelectionModel().setValueIsAdjusting(true);
         this.jListPresentations.setSelectedIndex(this.display.getIndex());
         this.jListPresentations.getSelectionModel().setValueIsAdjusting(false);
      }
   }

   @Override
   public void changePresentation(Presentation p, boolean transition) {
      PresentationControl.super.changePresentation(p, transition);
      if (this.jListPresentations != null) {
         this.jListPresentations.getSelectionModel().setValueIsAdjusting(true);
         this.jListPresentations.setSelectedIndex(this.display.getIndex());
         this.jListPresentations.getSelectionModel().setValueIsAdjusting(false);
      }

      this.jButtonPrevious.setEnabled(this.display.getIndex() > 0);
      this.jButtonNext.setEnabled(this.display.getIndex() < this.presentations.size());
   }

   @Override
   public void next() {
      PresentationControl.super.next();
      this.jListPresentations.ensureIndexIsVisible(this.display.getIndex() + 1);
   }

   @Override
   public void previous() {
      PresentationControl.super.previous();
      this.jListPresentations.ensureIndexIsVisible(this.display.getIndex() - 1);
   }

   @Override
   public void exit() {
      this.exit = true;
      this.display.exit();
      this.parent.removeFooter();
   }

   @Override
   public boolean isWallpaper() {
      return this.display.isWallpaper();
   }

   @Override
   public boolean isBlack() {
      return this.display.isBlack();
   }

   @Override
   public boolean isBlank() {
      return this.display.isBlank();
   }

   @Override
   public void setWallpaper(boolean wallpaper) {
      this.display.setWallpaper(wallpaper);
   }

   @Override
   public void setBlank(boolean blank) {
      this.display.setBlank(blank);
   }

   @Override
   public void setBlack(boolean black) {
      this.display.setBlack(black);
   }

   @Override
   public int getIndex() {
      return this.display.getIndex();
   }

   @Override
   public List<Presentation> getPresentations() {
      return this.display.getPresentations();
   }

   @Override
   public void paintPresentation(Presentation p, boolean transition) {
      this.display.paintPresentation(p, transition);
   }

   @Override
   public void realTimeUpdate() {
      this.jListPresentations.repaint();
   }

   private void updateListRenderer() {
      if (!this.presentations.isEmpty() && this.presentations.get(0).theme.getBackground() instanceof BackgroundImageFile) {
         this.jListPresentations.setCellRenderer(new ListCellRendererPresentationImage(this.presentationSize));
         this.jListPresentations.setFixedCellHeight(150);
         ((WebList)this.jListPresentations).setDecorateSelection(false);
         ((WebList)this.jListPresentations).setHighlightRolloverCell(false);
         this.jToggleButtonViewOnlyText.setEnabled(false);
         this.jToggleButtonViewSlide.setEnabled(false);
      } else {
         String mode = Settings.EnumSettings.PRESENTATION_CONTROL_RENDERER_MODE.getString();
         byte var3 = -1;
         switch (mode.hashCode()) {
            case 3556653:
               if (mode.equals("text")) {
                  var3 = 0;
               }
            default:
               switch (var3) {
                  case 0:
                     this.jListPresentations.setCellRenderer(new ListCellRendererPresentationText());
                     this.jListPresentations.setFixedCellHeight(-1);
                     ((WebList)this.jListPresentations).setDecorateSelection(true);
                     ((WebList)this.jListPresentations).setHighlightRolloverCell(true);
                     break;
                  default:
                     this.jListPresentations.setCellRenderer(new ListCellRendererPresentationImage(this.presentationSize));
                     this.jListPresentations.setFixedCellHeight(240);
                     ((WebList)this.jListPresentations).setDecorateSelection(false);
                     ((WebList)this.jListPresentations).setHighlightRolloverCell(false);
               }
         }
      }

      this.jListPresentations.repaint();
      this.revalidate();
      this.repaint();
   }

   private void changeRendererMode(String mode) {
      Settings.EnumSettings.PRESENTATION_CONTROL_RENDERER_MODE.set(mode);
      this.updateListRenderer();
   }

   @Override
   public List<PresentationJDialog> getFramesToPaint() {
      return this.display.getFramesToPaint();
   }

   private void rightButton(MouseEvent evt) {
      int listIndex = this.jListPresentations.locationToIndex(evt.getPoint());
      if (listIndex >= 0) {
         Presentation p = this.jListPresentations.getModel().getElementAt(listIndex);
         if (PresentationUtils.checkEditRealTime(p)) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem edit = new JMenuItem(I18N.MSG_EDIT_TEXT.val());
            edit.addActionListener(ae -> PresentationUtils.editRealTime(p, this.jListPresentations::repaint));
            menu.add(edit);
            menu.show(this.jListPresentations, evt.getX(), evt.getY());
         }
      }
   }

   private void minimize(boolean flag) {
      this.setWebColoredBackground(!flag);
      this.setBackground(flag ? StyleUI.COLOR_ALERT_SHOW : StyleConstants.backgroundColor);
      this.setBorderColor(flag ? StyleUI.COLOR_ALERT_SHOW.darker().darker() : StyleConstants.darkBorderColor);
      this.jScrollPanePresentations.setVisible(!flag);
      this.jPanelLeftToolBar.setVisible(!flag);
      this.jPanelThemeListFooter.setVisible(!flag && this.jToggleButtonThemeList.isSelected());
      this.jLabelMinimizeMaximize.setText(flag ? I18N.MSG_CLICK_TO_MAXIMIZE.val() : I18N.MSG_CLICK_TO_MINIMIZE.val());
      if (MusicMain.INSTANCE != null && !this.exit) {
         MusicMain.INSTANCE.setPanelOrderVisible(flag);
      }

      this.revalidate();
      this.repaint();
   }

   private void setupThemeListFooter() {
      this.jToggleButtonThemeList.setSelected(Settings.EnumSettings2.FOOTER_CONTROL_SHOW_THEME_LIST.is());
      JList<Theme> listThemes = this.createListThemes();
      WebScrollPane scroll = this.createScrollListThemes(listThemes);
      WebPanel panelButtons = this.createButtonsThemeList(listThemes);
      this.jPanelThemeListFooter.setVisible(this.jToggleButtonThemeList.isSelected());
      this.jPanelThemeListFooter.add(panelButtons, "West");
      this.jPanelThemeListFooter.add(scroll, "Center");
      this.jToggleButtonThemeList.addActionListener(evt -> {
         Settings.EnumSettings2.FOOTER_CONTROL_SHOW_THEME_LIST.set(this.jToggleButtonThemeList.isSelected());
         this.jPanelThemeListFooter.setVisible(this.jToggleButtonThemeList.isSelected());
      });
   }

   private void setTheme(Theme theme) {
      if (theme != null) {
         try {
            this.presentations.forEach(p -> ReflectionUtils.setField(p, "theme", theme));
            this.display.prepareRenderer(theme);
            this.display.getFramesToPaint().forEach(f -> f.prepareRenderer(theme));
            if (this.display.isBlack()) {
               this.display.black(false);
            } else if (this.display.isBlank()) {
               this.display.blank(false);
            } else {
               this.display.paintPresentation(this.display.getPresentation(), false);
            }
         } catch (Exception var3) {
         }
      }
   }

   private JList<Theme> createListThemes() {
      final JList<Theme> list = new JList<>(new ListModelTheme());
      list.setFocusable(false);
      list.setSelectionMode(0);
      list.setCellRenderer(new ListCellRendererTheme());
      list.setFixedCellHeight(45);
      list.setFixedCellWidth(80);
      list.setLayoutOrientation(1);
      list.setVisibleRowCount(Settings.EnumSettings2.FOOTER_CONTROL_SHOW_THEME_LIST_ROWS.getInt());
      list.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            if (Utils.isDoubleClick(evt)) {
               PresentationControllerViewFooter.this.setTheme(list.getSelectedValue());
            }
         }
      });
      list.addListSelectionListener(evt -> "".hashCode());
      list.setCellRenderer(new ListCellRendererTheme());
      list.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
      return list;
   }

   private WebScrollPane createScrollListThemes(JList list) {
      WebScrollPane scroll = new WebScrollPane(list);
      scroll.setHorizontalScrollBarPolicy(32);
      scroll.setFocusable(false);
      scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
      scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));
      WebLafUtils.setupScrollBarLargeThumbWithoutButton(scroll.getWebHorizontalScrollBar());
      return scroll;
   }

   private WebPanel createButtonsThemeList(JList<Theme> list) {
      WebPanel buttons = new WebPanel(new GridLayout(2, 1));
      WebButton add = new WebButton("+");
      WebButton remove = new WebButton("-");
      ActionListener al = e -> {
         int i = Settings.EnumSettings2.FOOTER_CONTROL_SHOW_THEME_LIST_ROWS.getInt();
         i += e.getSource() == add ? 1 : -1;
         i = Utils.range(i, 1, 4);
         Settings.EnumSettings2.FOOTER_CONTROL_SHOW_THEME_LIST_ROWS.set(i);
         list.setVisibleRowCount(i);
         this.jPanelThemeListFooter.revalidate();
         this.jPanelThemeListFooter.repaint();
         list.revalidate();
         list.repaint();
      };
      Arrays.asList(add, remove).forEach(b -> {
         b.setFontSizeAndStyle(18, 1);
         b.setFocusable(false);
         b.setShadeWidth(0);
         b.setUndecorated(true);
         b.setLeftRightSpacing(2);
         b.addActionListener(al);
      });
      buttons.setMargin(0, 2, 4, 2);
      buttons.add(add);
      buttons.add(remove);
      WebPanel panel = new WebPanel(new BorderLayout());
      panel.add(new Filler(new Dimension(), new Dimension(), new Dimension(0, 32767)), "Center");
      panel.add(buttons, "Last");
      return panel;
   }
}
