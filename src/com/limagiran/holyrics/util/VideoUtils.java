package com.limagiran.holyrics.util;

import com.alee.extended.window.PopOverAdapter;
import com.alee.extended.window.PopOverDirection;
import com.alee.extended.window.PopOverLocation;
import com.alee.extended.window.WebPopOver;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ComboBoxModelTheme;
import com.limagiran.holyrics.swing.ComboBoxRendererTheme;
import com.limagiran.holyrics.transition.TransitionSettingsDialog;
import com.limagiran.holyrics.view.AutomaticShowView;
import com.limagiran.holyrics.view.BibleMain;
import com.limagiran.holyrics.view.BiblePresentationView;
import com.limagiran.holyrics.view.FontEditThemeDefaultDialog;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.view.PresentationView;
import com.limagiran.holyrics.view.PresentationViewRemoteControl;
import com.limagiran.holyrics.view.WallpaperView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class VideoUtils implements Values {
   private static final GraphicsEnvironment GE = GraphicsEnvironment.getLocalGraphicsEnvironment();

   public static List<GraphicsDevice> list() {
      try {
         List<GraphicsDevice> devices = new ArrayList<>(Arrays.asList(GE.getScreenDevices()));
         if (devices.size() <= 1) {
            return devices;
         } else {
            GraphicsDevice primary = GE.getDefaultScreenDevice();
            devices.remove(primary);
            int secondMonitorIndex = Settings.EnumSettings2.SECOND_MONITOR_INDEX.getInt();
            if (secondMonitorIndex < devices.size()) {
               GraphicsDevice secondary = devices.get(secondMonitorIndex);
               devices.remove(secondMonitorIndex);
               devices.add(0, secondary);
            }

            devices.add(0, primary);
            return devices;
         }
      } catch (Exception var4) {
         return new ArrayList<>(Arrays.asList(GE.getScreenDevices()));
      }
   }

   private static Dimension getSize(boolean primary) {
      List<GraphicsDevice> devices = list();
      int index = primary ? 0 : (devices.size() <= 1 ? 0 : 1);
      return devices.get(index).getDefaultConfiguration().getBounds().getSize();
   }

   public static Dimension getSizeMonitorDisplay() {
      return getSize(false);
   }

   public static Dimension getSizeDisplay() {
      return getSize(true);
   }

   public static ScreenDeviceSettings.GraphicsDeviceCustom getDeviceToPresentation(ScreenDeviceSettings.EnumDeviceType deviceType) {
      List<GraphicsDevice> devices = list();
      return new ScreenDeviceSettings.GraphicsDeviceCustom(devices.get(devices.size() == 1 ? 0 : 1), deviceType, 1);
   }

   public static List<ScreenDeviceSettings.GraphicsDeviceCustom> getOtherDevicesToPresentation(ScreenDeviceSettings.EnumDeviceType deviceType) {
      List<GraphicsDevice> list = list();
      if (Settings.EnumSettings.DISPLAY_ON_ALL_MONITORS.is() && list.size() >= 2) {
         List<ScreenDeviceSettings.GraphicsDeviceCustom> devices = new ArrayList<>();

         for (int index = 0; index < list.size(); index++) {
            devices.add(new ScreenDeviceSettings.GraphicsDeviceCustom(list.get(index), deviceType, index));
         }

         return devices.subList(2, devices.size());
      } else {
         return new ArrayList<>();
      }
   }

   public static boolean isSingleMonitor() {
      return GE.getScreenDevices().length <= 1;
   }

   public static void staticExitAllExcept(Class clazz) {
      if (!clazz.equals(BiblePresentationView.class)) {
         BiblePresentationView.staticExit();
      }

      if (!clazz.equals(PresentationView.class)) {
         PresentationView.staticExit();
      }

      if (!clazz.equals(PresentationViewRemoteControl.class)) {
         PresentationViewRemoteControl.staticExit();
      }

      if (!clazz.equals(AutomaticShowView.class)) {
         AutomaticShowView.staticExit();
      }
   }

   public static boolean staticIsAnyVisible() {
      return BiblePresentationView.staticIsVisible()
         || PresentationView.staticIsVisible()
         || PresentationViewRemoteControl.staticIsVisible()
         || AutomaticShowView.staticIsVisible();
   }

   public static ScreenDeviceSettings getScreenDeviceSettingsStream() {
      return Settings.SET.getScreenDeviceSettings(-1);
   }

   public static void openDialogSettings(Window parent, JPanel displaySettingsPanel) {
      SwingUtilities.invokeLater(
         () -> {
            List<GraphicsDevice> devices = list();
            int deviceLength = devices.size();
            final WebPopOver wpo = new WebPopOver(parent);
            WebTabbedPane tabPane = new WebTabbedPane();
            tabPane.setTabInsets(new Insets(5, 8, 7, 8));
            tabPane.setFocusable(false);
            tabPane.setShadeWidth(2);
            tabPane.setFontSize(14);
            tabPane.setFontName("Arial");
            Consumer<Integer> applyPanel = index -> {
               final ScreenDeviceSettings sds = Settings.SET.getScreenDeviceSettings(index);
               WebComboBox cmbbxSecondaryIndex = new WebComboBox(IntStream.range(0, list().size() - 1).mapToObj(Integer::new).toArray());
               WebButton buttonTransitionEffect = new WebButton(I18N.MSG_TRANSITION_EFFECT.val(), e -> TransitionSettingsDialog.main());
               JCheckBox chkbxStageView = new JCheckBox(I18N.MSG_STAGE_VIEW.val(), sds.getStageViewSettings().isEnabled());
               WebComboBox cmbbxStageViewModes = new WebComboBox(new DefaultComboBoxModel<>(StageViewUtils.EnumStageViewMode.values())) {
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
                        dim.width = Math.max(dim.width, super.getPreferredSize().width);
                     }

                     return dim;
                  }

                  public Dimension getPreferredSize() {
                     return new Dimension(450, super.getPreferredSize().height);
                  }
               };
               JCheckBox chkbxStageViewUpperCase = new JCheckBox(I18N.WORD_UPPERCASE.val(), sds.getStageViewSettings().isUpperCase());
               JCheckBox chkbxStageViewRemoveLineBreak = new JCheckBox(I18N.MSG_REMOVE_LINE_BREAK.val(), sds.getStageViewSettings().isRemoveLineBreak());
               JCheckBox chkbxStageViewThemeEnabled = new JCheckBox(I18N.MSG_CUSTOM_THEME.val(), sds.getStageViewSettings().isThemeEnabled());
               JCheckBox chkbxStageViewBibleEnabled = new JCheckBox(I18N.MSG_ENABLE_FOR_BIBLE.val(), sds.getStageViewSettings().isBibleEnabled());
               WebComboBox cmbbxStageViewTheme = new WebComboBox(new ComboBoxModelTheme());
               JCheckBox chkbxShowPageCount = new JCheckBox(I18N.MSG_SHOW_SLIDE_COUNT.val(), sds.isShowPageCount());
               JCheckBox chkbxShowTitleAndArtistAllSlides = new JCheckBox(I18N.MSG_SHOW_TITLE_AND_ARTIST_ALL_SLIDES.val(), sds.isShowTitleAndArtistAllSlides());
               WebPanel panelPageCountPos = new WebPanel(new GridLayout(2, 3));
               WebToggleButton[] togglesPageCountPos = new WebToggleButton[6];
               WebPanel panelTitAndArtPos = new WebPanel(new GridLayout(2, 3));
               WebToggleButton[] togglesTitAndArtPos = new WebToggleButton[6];
               Integer[] intArray = IntStream.rangeClosed(4, 15).mapToObj(Integer.class::cast).toArray(Integer[]::new);
               WebComboBox cmbbxAdditionalInfoLineHeight = new WebComboBox(intArray);
               final WebLabel labelAdditionalInfoFont = new WebLabel("", new ImageIcon(Images.ICON_EDIT_16), 0, new Object[0]);
               WebCheckBox chkbxAddInfoTwoLines = new WebCheckBox(I18N.MSG_SHOW_TITLE_ARTIST_TWO_LINES.val(), new ImageIcon(Images.ICON_ADD_INFO_TWO_LINES));
               WebCheckBox chkbxAddInfoEffects = new WebCheckBox(I18N.MSG_USING_THEME_EFFECTS.val(), new ImageIcon(Images.ICON_USING_THEME_EFFECTS));
               WebButton buttonEditResolution = new WebButton(I18N.MSG_EDIT_SCREEN_POSITION.val());
               cmbbxSecondaryIndex.setRenderer(WebLafUtils.createComboBoxRendererSimple(obj -> String.valueOf(Utils.intOrElse(obj, 0) + 1)));
               cmbbxSecondaryIndex.setFontSize(14);
               Integer value = Integer.min(cmbbxSecondaryIndex.getModel().getSize() - 1, Settings.EnumSettings2.SECOND_MONITOR_INDEX.getInt());
               cmbbxSecondaryIndex.setSelectedItem(value);
               cmbbxSecondaryIndex.setFocusable(false);
               cmbbxStageViewModes.setSelectedItem(StageViewUtils.EnumStageViewMode.valueOfById(sds.getStageViewSettings().getId()));
               cmbbxStageViewTheme.setMaximumRowCount(5);
               cmbbxStageViewTheme.setModel(new ComboBoxModelTheme());
               cmbbxStageViewTheme.setSelectedItem(ThemeController.get(sds.getStageViewSettings().getThemeId()));
               cmbbxStageViewTheme.setRenderer(new ComboBoxRendererTheme(64, 36));
               setupGridPosition(togglesPageCountPos, panelPageCountPos, sds.isShowPageCount(), i -> i == sds.getShowPageCountPosition());
               setupGridPosition(
                  togglesTitAndArtPos, panelTitAndArtPos, sds.isShowTitleAndArtistAllSlides(), i -> i == sds.getShowTitleAndArtistAllSlidesPosition()
               );
               panelPageCountPos.setOpaque(false);
               panelTitAndArtPos.setOpaque(false);
               cmbbxAdditionalInfoLineHeight.setSelectedItem(Utils.range(sds.getAdditionalInfoLineHeight(), 4, 15));
               cmbbxAdditionalInfoLineHeight.setRenderer(WebLafUtils.createComboBoxRendererSimple(obj -> " " + obj + "%"));
               final Runnable labelUpdateView = () -> {
                  Font font = labelAdditionalInfoFont.getFont();
                  labelAdditionalInfoFont.setFont(sds.getFont("Arial", font.getSize(), false, false));
                  String name = sds.getAdditionalInfoFont();
                  labelAdditionalInfoFont.setText(name != null ? name : I18N.MSG_THEME_FONT.val());
                  Color color = sds.getAdditionalInfoColor();
                  labelAdditionalInfoFont.setForeground(color != null ? color : Color.BLACK);
               };
               labelAdditionalInfoFont.setBorder(
                  BorderFactory.createCompoundBorder(LineBorder.createBlackLineBorder(), BorderFactory.createEmptyBorder(1, 4, 1, 4))
               );
               labelAdditionalInfoFont.setMaximumWidth(250);
               labelAdditionalInfoFont.setHorizontalTextPosition(2);
               labelAdditionalInfoFont.setCursor(Cursor.getPredefinedCursor(12));
               labelUpdateView.run();
               Runnable setupComponentsStageViewEnabled = () -> Arrays.asList(
                        cmbbxStageViewModes,
                        chkbxStageViewUpperCase,
                        chkbxStageViewRemoveLineBreak,
                        chkbxStageViewThemeEnabled,
                        chkbxStageViewBibleEnabled,
                        cmbbxStageViewTheme
                     )
                     .forEach(c -> c.setEnabled(sds.getStageViewSettings().isEnabled()));
               setupComponentsStageViewEnabled.run();
               chkbxAddInfoTwoLines.setSelected(sds.isAdditionalInfoTwoLines());
               chkbxAddInfoEffects.setSelected(sds.isAdditionalInfoPaintEffects());
               cmbbxSecondaryIndex.addItemListener(e -> {
                  Integer var10000 = Settings.EnumSettings2.SECOND_MONITOR_INDEX.set(Utils.intOrElse(cmbbxSecondaryIndex.getSelectedItem(), 0));
               });
               chkbxStageView.addItemListener(evt -> {
                  sds.getStageViewSettings().setEnabled(evt.getStateChange() == 1);
                  setupComponentsStageViewEnabled.run();
               });
               cmbbxStageViewModes.addItemListener(e -> {
                  Object obj = cmbbxStageViewModes.getSelectedItem();
                  if (e.getStateChange() == 1 && obj instanceof StageViewUtils.EnumStageViewMode) {
                     sds.getStageViewSettings().setId(((StageViewUtils.EnumStageViewMode)obj).id);
                  }
               });
               cmbbxAdditionalInfoLineHeight.addItemListener(e -> {
                  Object obj = cmbbxAdditionalInfoLineHeight.getSelectedItem();
                  if (e.getStateChange() == 1 && obj instanceof Integer) {
                     sds.setAdditionalInfoLineHeight((Integer)obj);
                     MusicMain.staticUpdateSlidePreview();
                  }
               });
               labelAdditionalInfoFont.addMouseListener(
                  new MouseAdapter() {
                     @Override
                     public void mouseReleased(MouseEvent e) {
                        wpo.setCloseOnFocusLoss(false);
                        FontEditThemeDefaultDialog.FontEditThemeDefaultListener listener = new FontEditThemeDefaultDialog.FontEditThemeDefaultListener() {
                           @Override
                           public void setFontName(String fontName) {
                              sds.setAdditionalInfoFont(fontName);
                              this.update();
                           }

                           @Override
                           public void setBold(Boolean bold) {
                              sds.setAdditionalInfoBold(bold);
                              this.update();
                           }

                           @Override
                           public void setItalic(Boolean italic) {
                              sds.setAdditionalInfoItalic(italic);
                              this.update();
                           }

                           @Override
                           public void setColor(Color color) {
                              sds.setAdditionalInfoColor(color);
                              this.update();
                           }

                           private void update() {
                              labelUpdateView.run();
                              labelAdditionalInfoFont.repaint();
                              MusicMain.staticUpdateSlidePreview();
                           }
                        };
                        Runnable callback = () -> {
                           wpo.requestFocus();
                           wpo.setCloseOnFocusLoss(true);
                        };
                        FontEditThemeDefaultDialog.main(
                           sds.getAdditionalInfoFont(),
                           sds.getAdditionalInfoBold(),
                           sds.getAdditionalInfoItalic(),
                           sds.getAdditionalInfoColor(),
                           listener,
                           callback
                        );
                     }
                  }
               );
               chkbxAddInfoTwoLines.addActionListener(evt -> {
                  sds.setAdditionalInfoTwoLines(chkbxAddInfoTwoLines.isSelected());
                  MusicMain.staticUpdateSlidePreview();
               });
               chkbxAddInfoEffects.addActionListener(evt -> {
                  sds.setAdditionalInfoPaintEffects(chkbxAddInfoEffects.isSelected());
                  MusicMain.staticUpdateSlidePreview();
               });
               chkbxStageViewUpperCase.addItemListener(evt -> sds.getStageViewSettings().setUpperCase(evt.getStateChange() == 1));
               chkbxStageViewRemoveLineBreak.addItemListener(evt -> sds.getStageViewSettings().setRemoveLineBreak(evt.getStateChange() == 1));
               chkbxStageViewThemeEnabled.addItemListener(evt -> {
                  sds.getStageViewSettings().setThemeEnabled(evt.getStateChange() == 1);
                  cmbbxStageViewTheme.setEnabled(sds.getStageViewSettings().isEnabled());
                  Object obj = cmbbxStageViewTheme.getSelectedItem();
                  if (obj instanceof Theme) {
                     sds.getStageViewSettings().setThemeId(((Theme)obj).id);
                  }
               });
               chkbxStageViewBibleEnabled.addItemListener(evt -> sds.getStageViewSettings().setBibleEnabled(evt.getStateChange() == 1));
               cmbbxStageViewTheme.addItemListener(evt -> {
                  Object obj = cmbbxStageViewTheme.getSelectedItem();
                  if (evt.getStateChange() == 1 && obj instanceof Theme) {
                     sds.getStageViewSettings().setThemeId(((Theme)obj).id);
                  }
               });
               chkbxShowPageCount.addItemListener(evt -> {
                  sds.setShowPageCount(evt.getStateChange() == 1);

                  for (WebToggleButton tg : togglesPageCountPos) {
                     tg.setEnabled(sds.isShowPageCount());
                  }

                  MusicMain.staticUpdateSlidePreview();
               });

               for (WebToggleButton tg : togglesPageCountPos) {
                  tg.addActionListener(al -> {
                     sds.setShowPageCountPosition(Utils.intOrElse(tg.getName(), 5));
                     MusicMain.staticUpdateSlidePreview();
                  });
               }

               chkbxShowTitleAndArtistAllSlides.addItemListener(evt -> {
                  sds.setShowTitleAndArtistAllSlides(evt.getStateChange() == 1);

                  for (WebToggleButton tg : togglesTitAndArtPos) {
                     tg.setEnabled(sds.isShowTitleAndArtistAllSlides());
                  }

                  MusicMain.staticUpdateSlidePreview();
               });

               for (WebToggleButton tg : togglesTitAndArtPos) {
                  tg.addActionListener(al -> {
                     sds.setShowTitleAndArtistAllSlidesPosition(Utils.intOrElse(tg.getName(), 5));
                     MusicMain.staticUpdateSlidePreview();
                  });
               }

               buttonEditResolution.addActionListener(evt -> {
                  if (!sds.isStream()) {
                     wpo.setCloseOnFocusLoss(false);
                     PopupUtils.editScreenResolution(sds, devices.get(devices.size() == 1 ? 0 : index), () -> {
                        WallpaperView.restart();
                        wpo.requestFocus();
                        wpo.setCloseOnFocusLoss(true);
                     });
                  }
               });
               WebPanel gridItem = new WebPanel(new GridBagLayout());
               gridItem.setOpaque(false);
               gridItem.setMargin(2, 6, 8, 6);
               buttonTransitionEffect.setShadeWidth(0);
               buttonEditResolution.setShadeWidth(0);
               cmbbxStageViewModes.setShadeWidth(1);
               cmbbxAdditionalInfoLineHeight.setShadeWidth(0);
               Arrays.asList(
                     buttonTransitionEffect,
                     buttonEditResolution,
                     chkbxStageView,
                     cmbbxStageViewModes,
                     chkbxStageViewUpperCase,
                     chkbxStageViewRemoveLineBreak,
                     chkbxStageViewThemeEnabled,
                     chkbxStageViewBibleEnabled,
                     cmbbxStageViewTheme,
                     chkbxShowPageCount,
                     chkbxShowTitleAndArtistAllSlides,
                     cmbbxAdditionalInfoLineHeight,
                     labelAdditionalInfoFont,
                     chkbxAddInfoTwoLines,
                     chkbxAddInfoEffects
                  )
                  .forEach(c -> c.setFocusable(false));
               chkbxStageView.setEnabled(!sds.isPrimary());
               int y = -1;
               GridBagConstraints gbc = new GridBagConstraints();
               gbc.gridx = 0;
               gbc.gridy = ++y;
               gbc.gridwidth = 0;
               gbc.weightx = 1.0;
               gbc.fill = 2;
               gbc.insets.bottom = 6;
               gridItem.add(WebSeparator.createHorizontal(), gbc);
               if (sds.isPrimary() && cmbbxSecondaryIndex.getModel().getSize() > 1) {
                  gbc = new GridBagConstraints();
                  gbc.gridx = 0;
                  gbc.gridy = ++y;
                  gbc.ipadx = 4;
                  gbc.weightx = 1.0;
                  gbc.insets.bottom = 4;
                  gbc.insets.right = 3;
                  gbc.anchor = 13;
                  gridItem.add(new WebLabel(I18N.MSG_PRIMARY_PRESENTATION_MONITOR.val(), 4, new Object[0]), gbc);
                  gbc = new GridBagConstraints();
                  gbc.gridx = 1;
                  gbc.gridy = y;
                  gbc.ipadx = 4;
                  gbc.weightx = 1.0;
                  gbc.insets.bottom = 4;
                  gbc.insets.left = 3;
                  gbc.anchor = 17;
                  gridItem.add(cmbbxSecondaryIndex, gbc);
               }

               if (!sds.isPrimary()) {
                  WebLabel help = new WebLabel("<html>?&nbsp;").setFontName("Courier New").setFontSizeAndStyle(18, 1);
                  help.setForeground(Color.DARK_GRAY);
                  help.setMargin(0, 6, 0, 0);
                  help.setCursor(Cursor.getPredefinedCursor(12));
                  help.addMouseListener(new MouseAdapter() {
                     @Override
                     public void mouseReleased(MouseEvent e) {
                        wpo.setCloseOnFocusLoss(false);
                        PopupUtils.helpStageView(() -> {
                           wpo.requestFocus();
                           wpo.setCloseOnFocusLoss(true);
                        });
                     }
                  });
                  gbc = new GridBagConstraints();
                  gbc.gridx = 0;
                  gbc.gridy = ++y;
                  gbc.gridwidth = 0;
                  WebPanel wp = new WebPanel(new FlowLayout(1, 0, 0), new Component[]{chkbxStageView, help});
                  wp.setOpaque(false);
                  gridItem.add(wp, gbc);
                  gbc = new GridBagConstraints();
                  gbc.gridx = 0;
                  gbc.gridy = ++y;
                  gbc.gridwidth = 0;
                  gbc.insets.top = 3;
                  gridItem.add(cmbbxStageViewModes, gbc);
                  gbc = new GridBagConstraints();
                  gbc.gridx = 0;
                  gbc.gridy = ++y;
                  gbc.gridwidth = 0;
                  gbc.insets.right = gbc.insets.left = gbc.insets.top = 4;
                  gbc.insets.bottom = 8;
                  wp = new WebPanel(
                     new FlowLayout(1, 6, 0), new Component[]{chkbxStageViewUpperCase, Box.createHorizontalStrut(24), chkbxStageViewRemoveLineBreak}
                  );
                  wp.setOpaque(false);
                  gridItem.add(wp, gbc);
                  gbc = new GridBagConstraints();
                  gbc.gridx = 0;
                  gbc.gridy = ++y;
                  gbc.anchor = 13;
                  gbc.weightx = 1.0;
                  gbc.insets.right = 6;
                  gridItem.add(chkbxStageViewThemeEnabled, gbc);
                  gbc = new GridBagConstraints();
                  gbc.gridx = 1;
                  gbc.gridy = y;
                  gbc.gridheight = 2;
                  gbc.weightx = 1.0;
                  gbc.anchor = 17;
                  gridItem.add(cmbbxStageViewTheme, gbc);
                  gbc = new GridBagConstraints();
                  gbc.gridx = 0;
                  gbc.gridy = ++y;
                  gbc.anchor = 13;
                  gbc.weightx = 1.0;
                  gbc.insets.right = 6;
                  gridItem.add(chkbxStageViewBibleEnabled, gbc);
               }

               if (sds.isPrimary()) {
                  gbc = new GridBagConstraints();
                  gbc.gridx = 0;
                  gbc.gridy = ++y;
                  gbc.gridwidth = 0;
                  gbc.weightx = 1.0;
                  gridItem.add(buttonTransitionEffect, gbc);
               }

               gbc = new GridBagConstraints();
               gbc.gridx = 0;
               gbc.gridy = ++y;
               gbc.gridwidth = 0;
               gbc.weightx = 1.0;
               gbc.fill = 2;
               gbc.insets.bottom = gbc.insets.top = 10;
               gridItem.add(WebSeparator.createHorizontal(), gbc);
               gbc = new GridBagConstraints();
               gbc.gridx = 0;
               gbc.gridy = ++y;
               gbc.gridwidth = 0;
               gbc.weightx = 1.0;
               gbc.fill = 2;
               gbc.insets.bottom = 4;
               WebPanel wp = new WebPanel(
                  new FlowLayout(1, 6, 0),
                  new Component[]{panelPageCountPos, chkbxShowPageCount, Box.createHorizontalStrut(24), panelTitAndArtPos, chkbxShowTitleAndArtistAllSlides}
               );
               wp.setOpaque(false);
               gridItem.add(wp, gbc);
               gbc = new GridBagConstraints();
               gbc.gridx = 0;
               gbc.gridy = ++y;
               gbc.gridwidth = 0;
               gbc.weightx = 1.0;
               gbc.insets.top = 8;
               wp = new WebPanel(
                  new FlowLayout(0, 6, 0),
                  new Component[]{
                     new WebLabel(I18N.WORD_HEIGHT.val(), 4, new Object[0]),
                     cmbbxAdditionalInfoLineHeight,
                     Box.createHorizontalStrut(2),
                     new WebLabel(I18N.WORD_FONT.val(), 4, new Object[0]),
                     labelAdditionalInfoFont
                  }
               );
               wp.setOpaque(false);
               gridItem.add(wp, gbc);
               gbc = new GridBagConstraints();
               gbc.gridx = 0;
               gbc.gridy = ++y;
               gbc.gridwidth = 0;
               gbc.weightx = 1.0;
               gbc.insets.top = 8;
               wp = new WebPanel(new FlowLayout(1, 6, 0), new Component[]{chkbxAddInfoEffects, Box.createHorizontalStrut(8), chkbxAddInfoTwoLines});
               wp.setOpaque(false);
               gridItem.add(wp, gbc);
               if (!sds.isStream()) {
                  gbc = new GridBagConstraints();
                  gbc.gridx = 0;
                  gbc.gridy = ++y;
                  gbc.gridwidth = 0;
                  gbc.weightx = 1.0;
                  gbc.fill = 2;
                  gbc.insets.bottom = gbc.insets.top = 10;
                  gridItem.add(WebSeparator.createHorizontal(), gbc);
                  gbc = new GridBagConstraints();
                  gbc.gridx = 0;
                  gbc.gridy = ++y;
                  gbc.gridwidth = 0;
                  gbc.weightx = 1.0;
                  gridItem.add(buttonEditResolution, gbc);
               }

               gbc = new GridBagConstraints();
               gbc.gridx = 0;
               gbc.gridwidth = 0;
               gbc.gridy = ++y;
               gbc.weighty = 1.0;
               gbc.weightx = 1.0;
               gridItem.add(Box.createVerticalGlue(), gbc);
               JLabel title = new JLabel(sds.getName(), 0);
               title.setFont(new Font("Arial", 0, 24));
               WebLabel fill = new WebLabel("<html>&nbsp;&nbsp;").setFontName("Courier New").setFontSize(20);
               WebPanel header = new WebPanel(new BorderLayout());
               header.setOpaque(false);
               header.add(fill, "West");
               header.add(title, "Center");
               WebPanel generalPanel = new WebPanel(new BorderLayout(0, 2));
               generalPanel.setOpaque(false);
               generalPanel.setRound(StyleConstants.mediumRound);
               generalPanel.setShadeWidth(0);
               generalPanel.add(gridItem, "Center");
               generalPanel.add((Component)(sds.isPrimary() ? title : header), "First");
               generalPanel.setMargin(4, 2, 4, 2);
               tabPane.addTab(sds.getName(), generalPanel);
            };
            applyPanel.accept(1);
            IntStream.range(2, deviceLength).forEach(applyPanel::accept);
            applyPanel.accept(-1);
            wpo.setDefaultCloseOperation(2);
            wpo.getPainter().setTransparency(1.0F);
            wpo.setMovable(true);
            wpo.setResizable(false);
            wpo.setPreferredDirection(PopOverDirection.up);
            InsertShortcut.addEscToDispose(wpo);
            wpo.setCloseOnFocusLoss(true);
            wpo.setMargin(8, 16, 6, 16);
            WebPanel buttonPanel = new WebPanel(new FlowLayout());
            buttonPanel.setOpaque(false);
            WebButton buttonClose = new WebButton(I18N.WORD_CLOSE.val(), e -> wpo.dispose());
            buttonClose.setFocusable(false);
            buttonClose.setShadeWidth(0);
            buttonClose.setMargin(2, 6, 2, 6);
            buttonPanel.add(buttonClose);
            WebLabel jLabelHelpDisplaySettingsButtons = new WebLabel();
            jLabelHelpDisplaySettingsButtons.setFont(new Font("Courier New", 1, 16));
            jLabelHelpDisplaySettingsButtons.setForeground(new Color(51, 51, 51));
            jLabelHelpDisplaySettingsButtons.setText(" ?");
            jLabelHelpDisplaySettingsButtons.setVerticalAlignment(1);
            jLabelHelpDisplaySettingsButtons.setCursor(new Cursor(12));
            jLabelHelpDisplaySettingsButtons.addMouseListener(new MouseAdapter() {
               @Override
               public void mouseReleased(MouseEvent evt) {
                  wpo.setCloseOnFocusLoss(false);
                  PopupUtils.helpDisplaySettingsButtons(() -> {
                     wpo.requestFocus();
                     wpo.setCloseOnFocusLoss(true);
                  });
               }
            });
            WebPanel headerPanel = new WebPanel(new BorderLayout());
            headerPanel.setOpaque(false);
            headerPanel.add(new WebLabel(I18N.MSG_DISPLAY_SETTINGS.val(), 0, new Object[0]).setFontSize(20), "First");
            headerPanel.add(new WebPanel(new FlowLayout(1, 0, 0), displaySettingsPanel, jLabelHelpDisplaySettingsButtons) {
               {
                  this.setOpaque(false);
               }
            }, "Center");
            wpo.setLayout(new BorderLayout(0, 8));
            wpo.add(headerPanel, "First");
            wpo.add(tabPane, "Center");
            wpo.add(buttonPanel, "Last");
            wpo.addPopOverListener(new PopOverAdapter() {
               public void closed(WebPopOver popOver) {
                  Settings.SET.saveTrue();
                  WallpaperView.update();
               }
            });
            wpo.setSize(new Dimension(850, 650));
            wpo.setPreferredSize(wpo.getSize());
            wpo.show(PopOverLocation.center);
         }
      );
   }

   private static void setupGridPosition(WebToggleButton[] toggles, WebPanel panelGrid, boolean enabled, Predicate<Integer> isSelected) {
      ButtonGroup group = new ButtonGroup();
      String[] arrows = "<html>&#x2196;,↑,<html>&#x2197;,<html>&#x2199;,↓,<html>&#x2198;".split(",");
      Font fontDefault = new Font("Arial", 0, 9);
      IntStream.range(0, Math.min(toggles.length, arrows.length)).forEach(i -> {
         toggles[i] = new WebToggleButton(arrows[i]);
         WebToggleButton tg = toggles[i];
         tg.setMargin(0);
         tg.setLeftRightSpacing(2);
         tg.setFont(fontDefault);
         tg.setName(String.valueOf(i));
         tg.setSelected(isSelected.test(i));
         tg.setFocusable(false);
         tg.setShineColor(Values.COLOR_TRANSPARENT);
         tg.setShadeWidth(0);
         tg.setEnabled(enabled);
         group.add(tg);
         panelGrid.add(tg);
      });
   }

   public static void requestFocusInMainIfSingleMonitor() {
      if (isSingleMonitor()) {
         if (MusicMain.INSTANCE != null && MusicMain.INSTANCE.isVisible()) {
            MusicMain.INSTANCE.requestFocus();
         } else if (BibleMain.getInstance().isVisible()) {
            BibleMain.getInstance().requestFocus();
         }
      }
   }

   public static JFrame createFullscreenJFrame(GraphicsDevice device) {
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(0);
      f.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
      f.setAlwaysOnTop(true);
      f.setUndecorated(true);
      f.setFocusable(false);
      f.setFocusableWindowState(false);
      f.setBounds(device.getDefaultConfiguration().getBounds());
      f.setLayout(new BorderLayout());
      return f;
   }
}
