package com.limagiran.holyrics.view.panel;

import com.alee.extended.window.PopOverAdapter;
import com.alee.extended.window.PopOverDirection;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.text.WebTextFieldStyle;
import com.alee.managers.hotkey.HotkeyData;
import com.alee.managers.hotkey.HotkeyInfo;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Alert;
import com.limagiran.holyrics.model.AlertSettings;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.view.AlertSettingsDialog;
import com.limagiran.holyrics.view.MusicMain;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class AlertPanel extends JPanel implements DocumentListenerAuto.DocumentListenerAutoListener {
   public static final AlertPanel INSTANCE = new AlertPanel();
   private HotkeyInfo hotkeyInfo;
   private final AlertSettings alertSettings;
   private final WebPopOver popOver = new WebPopOver();
   private final JTextField jTextFieldPopUp = new JTextField();
   private final MouseAdapter jTextFieldAlertMouseListener = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
         AlertPanel.this.jTextFieldPopUp.setText(AlertPanel.this.jTextFieldAlert.getText());
         AlertPanel.this.popOver.show(AlertPanel.this.jTextFieldAlert);
         AlertPanel.this.popOver.addPopOverListener(new PopOverAdapter() {
            public void closed(WebPopOver popOver) {
               super.closed(popOver);
               AlertPanel.this.jTextFieldAlert.setText(AlertPanel.this.jTextFieldPopUp.getText());
            }
         });
      }
   };
   private JButton jButtonAlertList;
   private JButton jButtonSettings;
   private JPanel jPanelButtons;
   private JTextField jTextFieldAlert;
   private JToggleButton jToggleButtonShow;

   private AlertPanel() {
      this.alertSettings = Settings.SET.getAlertSettings();
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jTextFieldAlert.setDocument(SwingUtils.createDocMaxSize(1024));
      this.jTextFieldAlert.setText(this.alertSettings.getFullText());
      this.jTextFieldAlert.getDocument().addDocumentListener(new DocumentListenerAuto(800L, this));
      this.jToggleButtonShow.addItemListener(e -> this.update());
      this.jTextFieldAlert.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
               case 10:
                  e.consume();
                  break;
               case 33:
                  BooleanOptional.runIfTrue(MusicMain.staticPrevious(), e::consume);
                  break;
               case 34:
                  BooleanOptional.runIfTrue(MusicMain.staticNext(), e::consume);
                  break;
               case 37:
               case 39:
                  e.consume();
            }
         }
      });
      this.jButtonSettings.addActionListener(e -> AlertSettingsDialog.main(this.alertSettings));
      this.jButtonAlertList.addActionListener(e -> this.showAlertList());
      ((WebToggleButton)this.jToggleButtonShow).setBottomSelectedBgColor(new Color(0, 255, 0));
      ((WebToggleButton)this.jToggleButtonShow).setTopSelectedBgColor(new Color(0, 200, 0));
      InsertShortcut.addEscToDispose(this.popOver);
      this.popOver.setDefaultCloseOperation(2);
      this.popOver.setCloseOnFocusLoss(true);
      this.popOver.setMargin(10);
      this.popOver.setLayout(new BorderLayout());
      this.jTextFieldPopUp.setDocument(SwingUtils.createDocMaxSize(1024));
      this.jTextFieldPopUp.setFont(this.jTextFieldAlert.getFont());
      this.jTextFieldPopUp.setColumns(this.jTextFieldAlert.getColumns() * 2);
      this.jTextFieldPopUp.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            BooleanOptional.runIfTrue(e.getKeyCode() == 10, AlertPanel.this.popOver::dispose);
         }
      });
      this.popOver.add(this.jTextFieldPopUp);
   }

   private void initComponents() {
      this.jToggleButtonShow = new WebToggleButton();
      this.jTextFieldAlert = new JTextField();
      this.jPanelButtons = new JPanel();
      this.jButtonAlertList = new JButton();
      this.jButtonSettings = new JButton();
      this.setLayout(new BorderLayout());
      this.jToggleButtonShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/checkBlack16.png")));
      this.jToggleButtonShow.setSelected(this.alertSettings.isShow());
      this.jToggleButtonShow.setFocusable(false);
      this.add(this.jToggleButtonShow, "West");
      this.jTextFieldAlert.setColumns(30);
      this.jTextFieldAlert.setFont(new Font("Tahoma", 0, 12));
      this.add(this.jTextFieldAlert, "Center");
      this.jPanelButtons.setLayout(new GridLayout(1, 0));
      this.jButtonAlertList.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/list16.png")));
      this.jButtonAlertList.setFocusable(false);
      this.jPanelButtons.add(this.jButtonAlertList);
      this.jButtonSettings.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/settings16.png")));
      this.jButtonSettings.setFocusable(false);
      this.jPanelButtons.add(this.jButtonSettings);
      this.add(this.jPanelButtons, "East");
   }

   private void update() {
      this.alertSettings.setText(this.jTextFieldAlert.getText());
      this.alertSettings.setShow(this.jToggleButtonShow.isSelected());
      this.alertSettings.save();
      this.jTextFieldAlert.setBackground(this.jToggleButtonShow.isSelected() ? StyleUI.COLOR_ALERT_SHOW : WebTextFieldStyle.backgroundColor);
   }

   public Component getComponentToFocus() {
      return this.jTextFieldAlert;
   }

   public String getText() {
      return this.jTextFieldAlert.getText();
   }

   public void setText(String text) {
      this.jTextFieldAlert.setText(text);
   }

   public boolean isShow() {
      return this.jToggleButtonShow.isSelected();
   }

   public void setShow(boolean flag) {
      this.jToggleButtonShow.setSelected(flag);
   }

   public void setup(Frame parent, AlertPanel.EnumSetup en) {
      switch (en) {
         case BIBLE:
            this.jTextFieldAlert.setFocusable(false);
            this.jTextFieldAlert.setEditable(false);
            this.jTextFieldAlert.addMouseListener(this.jTextFieldAlertMouseListener);
            break;
         case HOLYRICS:
            this.jTextFieldAlert.removeMouseListener(this.jTextFieldAlertMouseListener);
            this.jTextFieldAlert.setFocusable(true);
            this.jTextFieldAlert.setEditable(true);
      }

      if (parent != null) {
         if (this.hotkeyInfo != null) {
            HotkeyManager.unregisterHotkey(this.hotkeyInfo);
         }

         this.hotkeyInfo = HotkeyManager.registerHotkey(parent, this.jToggleButtonShow, new HotkeyData(true, false, false, 87), TooltipWay.up);
         TooltipManager.setTooltip(this.jToggleButtonShow, I18N.MSG_DISPLAY_ON_SCREEN.val(), TooltipWay.up, 500);
      }
   }

   private void showAlertList() {
      final WebPopOver wpo = new WebPopOver();
      wpo.setDefaultCloseOperation(2);
      wpo.setMovable(false);
      wpo.setPreferredDirection(PopOverDirection.up);
      InsertShortcut.addEscToDispose(wpo);
      wpo.setCloseOnFocusLoss(true);
      wpo.setMargin(4);
      wpo.setLayout(new BorderLayout());
      wpo.add(new AlertListPanel(this.jTextFieldAlert.getText(), new AlertListPanel.AlertListPanelListener() {
         @Override
         public void selected(Alert alert) {
            AlertPanel.this.jTextFieldAlert.setText(alert.getText());
            wpo.dispose();
         }

         @Override
         public void fireDataChanged() {
            wpo.show(AlertPanel.this.jButtonAlertList);
         }
      }), "Center");
      wpo.show(this.jButtonAlertList);
   }

   @Override
   public void trigger() {
      this.alertSettings.setText(this.jTextFieldAlert.getText());
      this.alertSettings.save();
      BooleanOptional.runIfTrue(this.alertSettings.isShow(), this::update);
   }

   public static enum EnumSetup {
      HOLYRICS,
      BIBLE;
   }
}
