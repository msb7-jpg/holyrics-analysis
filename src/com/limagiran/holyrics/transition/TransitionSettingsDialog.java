package com.limagiran.holyrics.transition;

import com.alee.extended.button.WebSwitch;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.limagiran.holyrics.control.TransitionController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.BackgroundImage;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.UICreate;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class TransitionSettingsDialog extends JDialog implements Values {
   private static TransitionSettingsDialog INSTANCE;
   private volatile boolean threadFireTransitionStarted;
   private int count = 0;
   private static final String[] URLS_BG = new String[]{
      "/com/limagiran/holyrics/model/theme/img/IMAGE01_min.jpg", "/com/limagiran/holyrics/model/theme/img/IMAGE08_min.jpg"
   };
   private static final BufferedImage[] BG = new BufferedImage[]{Images.loadAbsoluteURL(URLS_BG[0]), Images.loadAbsoluteURL(URLS_BG[1])};
   private static final Dimension TRANSITION_PANEL_SIZE = new Dimension(384, 216);
   private static final ImageIcon IC_BLACK_LAYER = new ImageIcon(Img.createBlank(Color.BLACK, TRANSITION_PANEL_SIZE));
   private static final Theme[] THEMES = new Theme[]{
      Theme.create()
         .setSize(0.16)
         .setFontColor(new Color(252, 252, 252))
         .setLineSpacing(0.0)
         .setFont(new Font("Palatino Linotype", 1, 8))
         .setMargin(new double[]{0.1, 0.05, 0.1, 0.05})
         .setBackground(new BackgroundImage(URLS_BG[0])),
      Theme.create()
         .setSize(0.14)
         .setFontColor(new Color(252, 252, 252))
         .setShadowColor(Color.DARK_GRAY.darker())
         .setShadowX(0.05)
         .setShadowY(-0.05)
         .setFont(new Font("Trebuchet MS", 1, 8))
         .setMargin(new double[]{0.1, 0.05, 0.1, 0.05})
         .setBackground(new BackgroundImage(URLS_BG[1]))
   };
   private final Map<ScreenDeviceSettings.EnumDeviceType, MyImageTransition> mapImageTransition = new HashMap<>();
   private final Map<ScreenDeviceSettings.EnumDeviceType, TransitionSettings> mapTransitionSet = new HashMap<>();
   private JButton jButtonCancel;
   private JButton jButtonOk;
   private JPanel jPanel;
   private JPanel jPanelGrid;
   private JPanel jPanelLast;
   private JTabbedPane jTabbedPane;
   private static final String[] TEXT = new String[]{"Holyrics - Lima Giran\nDivulgue, compartilhe\nÉ grátis!", "Instale o aplicativo\nno seu celular"};

   private TransitionSettingsDialog() {
      this.initComponents();
      this.init();
   }

   private void init() {
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            TransitionSettingsDialog.this.exit(false);
         }
      });
      ((WebPanel)this.jPanel).setMargin(2);
      ((WebTabbedPane)this.jTabbedPane).setShadeWidth(2);
      ((WebTabbedPane)this.jTabbedPane).setFontSize(12);
      ((WebTabbedPane)this.jTabbedPane).setTabInsets(new Insets(5, 12, 7, 12));
      this.jTabbedPane
         .addTab(
            I18N.WORD_LYRICS.val() + " / " + I18N.WORD_TEXT.val(),
            new ImageIcon(Images.load("holyricsIcon24.png")),
            this.createTab(ScreenDeviceSettings.EnumDeviceType.MUSIC)
         );
      this.jTabbedPane.addTab(I18N.WORD_BIBLE.val(), new ImageIcon(Images.load("bible26.png")), this.createTab(ScreenDeviceSettings.EnumDeviceType.BIBLE));
      this.jTabbedPane.addTab(I18N.WORD_IMAGE.val(), new ImageIcon(Images.load("image24.png")), this.createTab(ScreenDeviceSettings.EnumDeviceType.IMAGE));
      this.jTabbedPane
         .addTab(
            I18N.WORD_ANNOUNCEMENT.val(), new ImageIcon(Images.load("checkedText24.png")), this.createTab(ScreenDeviceSettings.EnumDeviceType.ANNOUNCEMENT)
         );
      ((WebButton)this.jButtonOk).setMargin(4, 8, 4, 8);
      this.jButtonOk.addActionListener(e -> this.exit(true));
      this.jButtonCancel.addActionListener(e -> this.exit(false));
      InsertShortcut.addEscToDispose(this);
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            if (!TransitionSettingsDialog.this.threadFireTransitionStarted) {
               TransitionSettingsDialog.this.threadFireTransitionStarted = true;
               Utils.startThreadDaemon(() -> {
                  while (true) {
                     TransitionSettingsDialog.this.count++;
                     Utils.sleep(3000L);
                     if (TransitionSettingsDialog.this.isVisible()) {
                        ScreenDeviceSettings.EnumDeviceType type = null;
                        switch (TransitionSettingsDialog.this.jTabbedPane.getSelectedIndex()) {
                           case 0:
                              type = ScreenDeviceSettings.EnumDeviceType.MUSIC;
                              break;
                           case 1:
                              type = ScreenDeviceSettings.EnumDeviceType.BIBLE;
                              break;
                           case 2:
                              type = ScreenDeviceSettings.EnumDeviceType.IMAGE;
                              break;
                           case 3:
                              type = ScreenDeviceSettings.EnumDeviceType.ANNOUNCEMENT;
                        }

                        if (type == null) {
                           return;
                        }

                        MyImageTransition it = TransitionSettingsDialog.this.mapImageTransition.get(type);
                        if (it != null && !it.isAnimating()) {
                           it.putImage(TransitionSettingsDialog.this.createFG(type), true);
                        }
                     }
                  }
               });
            }
         }
      });
   }

   private void initComponents() {
      this.jPanel = new WebPanel();
      this.jTabbedPane = new WebTabbedPane();
      this.jPanelLast = new JPanel();
      this.jPanelGrid = new JPanel();
      this.jButtonOk = new WebButton();
      this.jButtonCancel = new WebButton();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("msg.transitionEffect"));
      this.setMinimumSize(new Dimension(750, 400));
      this.setModal(true);
      this.setPreferredSize(new Dimension(750, 400));
      this.setResizable(false);
      this.jPanel.setLayout(new BorderLayout());
      this.jTabbedPane.setFocusable(false);
      this.jPanel.add(this.jTabbedPane, "Center");
      this.jPanelLast.setLayout(new FlowLayout(1, 50, 5));
      this.jPanelGrid.setLayout(new GridLayout(1, 2, 40, 0));
      this.jButtonOk.setFont(new Font("Georgia", 0, 12));
      this.jButtonOk.setText(bundle.getString("word.ok"));
      this.jPanelGrid.add(this.jButtonOk);
      this.jButtonCancel.setFont(new Font("Georgia", 0, 12));
      this.jButtonCancel.setText(bundle.getString("word.cancel"));
      this.jPanelGrid.add(this.jButtonCancel);
      this.jPanelLast.add(this.jPanelGrid);
      this.jPanel.add(this.jPanelLast, "Last");
      this.getContentPane().add(this.jPanel, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main() {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new TransitionSettingsDialog();
         }

         INSTANCE.setVisible(true);
      });
   }

   private WebPanel createTab(ScreenDeviceSettings.EnumDeviceType type) {
      TransitionSettings ts = RWObj.clone(TransitionController.get(type), TransitionSettings.class);
      if (ts == null) {
         ts = new TransitionSettings();
      }

      TransitionSettings set = ts;
      WebPanel p = new WebPanel(new BorderLayout(16, 0));
      p.setMargin(2, 4, 2, 4);
      WebPanel panelSet = new WebPanel(new GridBagLayout());
      panelSet.setMargin(8, 2, 8, 2);
      JLabel label = new JLabel(I18N.WORD_TRANSITION.val());
      label.setFont(label.getFont().deriveFont(18.0F));
      panelSet.add(label, new GridBagConstraints());
      WebSwitch switchTransitionEnable = UICreate.newWebSwitch();
      switchTransitionEnable.setSelected(set.isEnabled());
      switchTransitionEnable.setFocusable(false);
      switchTransitionEnable.addActionListener(evt -> set.setEnabled(switchTransitionEnable.isSelected()));
      WebCheckBox chkbxOnlyMarginArea = new WebCheckBox(I18N.MSG_ONLY_AREA_WITHIN_MARGIN.val(), set.isOnlyAreaWithinMargin());
      chkbxOnlyMarginArea.setFocusable(false);
      chkbxOnlyMarginArea.addItemListener(evt -> set.setOnlyAreaWithinMargin(evt.getStateChange() == 1));
      GridBagConstraints gbc = new GridBagConstraints();
      int y = 1;
      gbc.insets.top = 6;
      gbc.insets.bottom = 6;
      gbc.gridwidth = 2;
      gbc.gridy = y++;
      panelSet.add(switchTransitionEnable, gbc);
      WebComboBox cmbbxType = new WebComboBox(TransitionType.values(), set.getType());
      cmbbxType.setFocusable(false);
      gbc = new GridBagConstraints();
      gbc.insets.top = gbc.insets.bottom = 4;
      gbc.gridwidth = 2;
      gbc.ipadx = 24;
      gbc.gridy = y++;
      panelSet.add(cmbbxType, gbc);
      if (type != ScreenDeviceSettings.EnumDeviceType.MUSIC && type != ScreenDeviceSettings.EnumDeviceType.BIBLE) {
         set.setOnlyAreaWithinMargin(false);
      } else {
         gbc = new GridBagConstraints();
         gbc.gridy = y++;
         panelSet.add(chkbxOnlyMarginArea, gbc);
      }

      WebPanel panelSetCenter = new WebPanel(new BorderLayout());
      gbc = new GridBagConstraints();
      gbc.gridy = 3;
      gbc.weighty = 2.0;
      gbc.gridy = y++;
      gbc.anchor = 11;
      panelSet.add(panelSetCenter, gbc);
      Consumer<TransitionType> setupPanelSet = flag -> {
         panelSetCenter.removeAll();
         panelSetCenter.add((Component)flag.panelSetFactory.apply(set), "Center");
         panelSetCenter.revalidate();
         panelSetCenter.repaint();
      };
      setupPanelSet.accept(set.getType());
      cmbbxType.addItemListener(e -> {
         if (cmbbxType.getSelectedItem() instanceof TransitionType) {
            set.setType((TransitionType)cmbbxType.getSelectedItem());
            set.setDuration(set.getType().durationDefault);
            setupPanelSet.accept(set.getType());
         }
      });
      p.add(panelSet, "Center");
      final WebPanel transitionExample = new WebPanel(new AbsoluteLayout());
      transitionExample.setMinimumWidth(TRANSITION_PANEL_SIZE.width);
      transitionExample.setMaximumWidth(TRANSITION_PANEL_SIZE.width);
      MyImageTransition transition = new MyImageTransition(this.createFG(type));
      this.mapImageTransition.put(type, transition);
      this.mapTransitionSet.put(type, set);
      CustomTransitionEffect effect = new CustomTransitionEffect(type, null, set, THEMES[0], true);
      transition.setTransitionEffect(effect);
      final AbsoluteConstraints absConstraints = new AbsoluteConstraints(0, 0);
      WebLabel blackLayer = new WebLabel();
      blackLayer.setIcon(IC_BLACK_LAYER);
      blackLayer.setOpaque(true);
      blackLayer.setBackground(Color.BLACK);
      transitionExample.add(transition, absConstraints);
      transitionExample.add(this.createBG(type), absConstraints);
      transitionExample.add(blackLayer, absConstraints);
      transitionExample.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            absConstraints.y = (transitionExample.getHeight() - TransitionSettingsDialog.TRANSITION_PANEL_SIZE.height) / 2;
            transitionExample.revalidate();
         }
      });
      p.add(transitionExample, "East");
      return p;
   }

   private void exit(boolean flag) {
      if (flag) {
         this.mapTransitionSet.forEach((type, set) -> TransitionController.get(type).clone(set));
         TransitionController.save();
      }

      super.dispose();
   }

   private JLabel createBG(ScreenDeviceSettings.EnumDeviceType type) {
      switch (type) {
         case MUSIC:
         case BIBLE:
            return new JLabel(new ImageIcon(Img.resize(BG[0], TRANSITION_PANEL_SIZE)));
         default:
            return new JLabel();
      }
   }

   private BufferedImage createFG(ScreenDeviceSettings.EnumDeviceType type) {
      int index = this.count % 2;
      String text = TEXT[index];
      switch (type) {
         case MUSIC:
         case BIBLE:
            return PresentationSlideUtils.createImage(null, text, THEMES[0], TRANSITION_PANEL_SIZE, false);
         default:
            return PresentationSlideUtils.createImage(null, text, THEMES[index], TRANSITION_PANEL_SIZE, true);
      }
   }
}
