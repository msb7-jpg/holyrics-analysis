package com.limagiran.holyrics.view.panel;

import com.alee.extended.button.WebSwitch;
import com.alee.extended.window.PopOverAdapter;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotification;
import com.limagiran.holyrics.control.AlertController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Alert;
import com.limagiran.holyrics.model.AlertSchedule;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.UICreate;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.VideoUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Box.Filler;

public class AlertSchedulePanel extends JPanel {
   private final Alert alert;
   private final AlertSchedulePanel.ScheduleAlertPanelListener listener;
   private JLabel jLabelScheduleAlert;
   private JPanel jPanelGeneral;
   private JPanel jPanelHeader;
   private JPanel jPanelTimes;

   public AlertSchedulePanel(Alert alert, AlertSchedulePanel.ScheduleAlertPanelListener listener) {
      this.alert = alert;
      this.listener = listener;
      this.initComponents();
      this.init();
      this.setup();
   }

   private void init() {
      WebButton buttonEmpty = new WebButton(new ImageIcon(Img.createBlank(Values.COLOR_TRANSPARENT, 16, 16)));
      buttonEmpty.setUndecorated(true);
      WebButton buttonClose = new WebButton(new ImageIcon(Images.load("close16.png")));
      buttonClose.setUndecorated(true);
      buttonClose.addActionListener(e -> this.listener.close());
      this.jPanelHeader.add(buttonEmpty, "Before");
      this.jPanelHeader.add(buttonClose, "After");
      this.setup();
   }

   private void setup() {
      this.jPanelGeneral.removeAll();
      this.jPanelTimes.removeAll();
      JTextField tf = new JTextField(this.alert.getText(), 30);
      tf.setFont(new Font("Georgia", 0, 14));
      tf.setFocusable(false);
      tf.setEditable(false);
      tf.setCaretPosition(0);
      this.jPanelGeneral.add(tf, "First");
      this.jPanelGeneral.add(this.jPanelTimes, "Center");
      this.alert
         .getSchedules()
         .forEach(
            as -> this.jPanelTimes
                  .add(new AlertSchedulePanel.ScheduleAlertRowPanel(as, new AlertSchedulePanel.ScheduleAlertRowPanel.ScheduleAlertRowPanelListener() {
                     @Override
                     public void removed() {
                        AlertSchedulePanel.this.alert.removeSchedule(as);
                        AlertSchedulePanel.this.setup();
                        AlertSchedulePanel.this.listener.fireDataChanged();
                     }

                     @Override
                     public void add(boolean justOnce) {
                     }
                  }, true))
         );
      Calendar c = Calendar.getInstance();
      final AlertSchedule alertSchedule = new AlertSchedule(true, c.get(11), c.get(12));
      AlertSchedulePanel.ScheduleAlertRowPanel sarp = new AlertSchedulePanel.ScheduleAlertRowPanel(
         alertSchedule, new AlertSchedulePanel.ScheduleAlertRowPanel.ScheduleAlertRowPanelListener() {
            @Override
            public void removed() {
            }

            @Override
            public void add(boolean justOnce) {
               if (justOnce) {
                  AlertController.addAlertScheduleTemp(AlertSchedulePanel.this.alert, alertSchedule.getHour(), alertSchedule.getMinute());
                  WebNotification popup = new WebNotification();
                  popup.setDisplayTime(3000L);
                  popup.setContent(I18N.MSG_SCHEDULED_ALERT.val() + "  -  " + alertSchedule.getFormatedTime());
                  popup.setIcon(NotificationIcon.tip);
                  NotificationManager.showNotification(popup);
                  AlertSchedulePanel.this.listener.close();
               } else {
                  AlertSchedulePanel.this.alert.addSchedule(alertSchedule);
                  AlertSchedulePanel.this.setup();
                  AlertSchedulePanel.this.listener.fireDataChanged();
               }
            }
         }, false
      );
      this.jPanelGeneral.add(sarp, "Last");
   }

   private void initComponents() {
      this.jPanelHeader = new JPanel();
      this.jLabelScheduleAlert = new JLabel();
      this.jPanelGeneral = new JPanel();
      this.jPanelTimes = new JPanel();
      this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.setOpaque(false);
      this.setLayout(new BorderLayout(0, 8));
      this.jPanelHeader.setOpaque(false);
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jLabelScheduleAlert.setFont(new Font("Georgia", 0, 14));
      this.jLabelScheduleAlert.setHorizontalAlignment(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelScheduleAlert.setText(bundle.getString("msg.addAlert"));
      this.jPanelHeader.add(this.jLabelScheduleAlert, "Center");
      this.add(this.jPanelHeader, "First");
      this.jPanelGeneral.setOpaque(false);
      this.jPanelGeneral.setLayout(new BorderLayout(0, 16));
      this.jPanelTimes.setBorder(BorderFactory.createEmptyBorder(0, 0, 36, 0));
      this.jPanelTimes.setOpaque(false);
      this.jPanelTimes.setLayout(new GridLayout(0, 1, 0, 16));
      this.jPanelGeneral.add(this.jPanelTimes, "Center");
      this.add(this.jPanelGeneral, "Center");
   }

   public static void main(Alert a, Component invoker, final Runnable callback) {
      final WebPopOver wpo = new WebPopOver();
      wpo.setDefaultCloseOperation(2);
      InsertShortcut.addEscToDispose(wpo);
      wpo.setMovable(true);
      wpo.setCloseOnFocusLoss(true);
      wpo.setMargin(10);
      wpo.setLayout(new BorderLayout());
      final AlertSchedulePanel asp = new AlertSchedulePanel(a, new AlertSchedulePanel.ScheduleAlertPanelListener() {
         @Override
         public void fireDataChanged() {
            wpo.packAndCenter(false);
         }

         @Override
         public void close() {
            wpo.dispose();
         }
      });
      wpo.add(asp, "Center");
      wpo.addPopOverListener(new PopOverAdapter() {
         public void closed(WebPopOver popOver) {
            try {
               SwingUtils.setupComponentIf(asp, JSpinner.class::isInstance, c -> SwingUtils.commitEdit((JSpinner)c));
            } catch (Exception var3) {
            }

            Optional.ofNullable(callback).ifPresent(Runnable::run);
         }
      });
      Dimension d = VideoUtils.getSizeDisplay();
      Dimension d2 = wpo.getPreferredSize();
      d.width = d.width - (d.width / 2 + d2.width / 2);
      d.height = d.height - (d.height / 2 + d2.height / 2);
      Point p = new Point(d.width, d.height);

      try {
         p.setLocation(invoker.getLocationOnScreen());
         p.y = p.y - (d2.height - 85);
         p.x += 10;
      } catch (Exception var9) {
      }

      wpo.show(p);
   }

   public interface ScheduleAlertPanelListener {
      void fireDataChanged();

      void close();
   }

   public static class ScheduleAlertRowPanel extends JPanel {
      private final AlertSchedule alertSchedule;
      private final AlertSchedulePanel.ScheduleAlertRowPanel.ScheduleAlertRowPanelListener listener;
      private final boolean edit;

      public ScheduleAlertRowPanel(AlertSchedule alertSchedule, AlertSchedulePanel.ScheduleAlertRowPanel.ScheduleAlertRowPanelListener listener, boolean edit) {
         super(new GridBagLayout());
         this.alertSchedule = alertSchedule;
         this.listener = listener;
         this.edit = edit;
         this.init();
      }

      private void init() {
         this.setOpaque(false);
         Font font = new Font("Georgia", 0, 14);
         WebSwitch _switch = UICreate.newWebSwitch();
         _switch.setSelected(this.alertSchedule.isEnabled(), false);
         _switch.addActionListener(e -> this.alertSchedule.setEnabled(_switch.isSelected()));
         JSpinner jSpinnerHour = new JSpinner();
         jSpinnerHour.setFont(font);
         jSpinnerHour.setModel(new SpinnerNumberModel(this.alertSchedule.getHour(), 0, 23, 1));
         jSpinnerHour.addChangeListener(e -> this.alertSchedule.setHour(Integer.parseInt(jSpinnerHour.getValue().toString())));
         JSpinner jSpinnerMinute = new JSpinner();
         jSpinnerMinute.setFont(font);
         jSpinnerMinute.setModel(new SpinnerNumberModel(this.alertSchedule.getMinute(), 0, 59, 1));
         jSpinnerMinute.addChangeListener(e -> this.alertSchedule.setMinute(Integer.parseInt(jSpinnerMinute.getValue().toString())));
         JCheckBox chkbxJustOnce = new JCheckBox("Somente uma vez");
         chkbxJustOnce.setFont(font);
         chkbxJustOnce.setSelected(!this.edit);
         JPanel gridDays = new JPanel(new GridLayout(1, 7));
         gridDays.setOpaque(false);
         IntStream.rangeClosed(1, 7).forEach(day -> {
            WebToggleButton toggle = new WebToggleButton(DateFormatSymbols.getInstance().getShortWeekdays()[day].substring(0, 1));
            toggle.setShadeColor(Values.COLOR_TRANSPARENT);
            toggle.setBottomSelectedBgColor(new Color(0, 255, 0));
            toggle.setTopSelectedBgColor(new Color(0, 200, 0));
            toggle.setFont(font.deriveFont(1));
            toggle.addItemListener(e -> this.alertSchedule.setDay(day, e.getStateChange() == 1));
            toggle.setEnabled(this.edit);
            toggle.setFocusable(false);
            toggle.setSelected(this.alertSchedule.isDay(day));
            chkbxJustOnce.addItemListener(e -> toggle.setEnabled(!chkbxJustOnce.isSelected()));
            gridDays.add(toggle);
         });
         WebButton remove = new WebButton(new ImageIcon(Images.load("delete16.png")));
         remove.addActionListener(a -> this.lastButton(chkbxJustOnce.isSelected()));
         remove.setShadeColor(Values.COLOR_TRANSPARENT);
         WebButton initButton = new WebButton(I18N.WORD_ADD.val());
         initButton.addActionListener(a -> this.lastButton(chkbxJustOnce.isSelected()));
         initButton.setShadeColor(Values.COLOR_TRANSPARENT);
         Arrays.asList(_switch, chkbxJustOnce, initButton).forEach(c -> c.setFocusable(false));
         chkbxJustOnce.setVisible(!this.edit);
         _switch.setVisible(this.edit);
         if (!this.edit) {
            this.add(initButton, new GridBagConstraints());
            this.add(new Filler(new Dimension(8, 0), new Dimension(8, 0), new Dimension(8, 32767)), new GridBagConstraints());
         }

         if (this.edit) {
            this.add(_switch, new GridBagConstraints());
            this.add(new Filler(new Dimension(16, 0), new Dimension(16, 0), new Dimension(16, 32767)), new GridBagConstraints());
         }

         this.add(jSpinnerHour, new GridBagConstraints());
         this.add(new Filler(new Dimension(2, 0), new Dimension(2, 0), new Dimension(2, 32767)), new GridBagConstraints());
         this.add(jSpinnerMinute, new GridBagConstraints());
         this.add(new Filler(new Dimension(8, 0), new Dimension(8, 0), new Dimension(8, 32767)), new GridBagConstraints());
         if (!this.edit) {
            this.add(chkbxJustOnce, new GridBagConstraints());
            this.add(new Filler(new Dimension(8, 0), new Dimension(8, 0), new Dimension(8, 32767)), new GridBagConstraints());
         }

         this.add(gridDays, new GridBagConstraints());
         if (this.edit) {
            this.add(new Filler(new Dimension(8, 0), new Dimension(8, 0), new Dimension(8, 32767)), new GridBagConstraints());
            this.add(remove, new GridBagConstraints());
         }
      }

      private void lastButton(boolean justOnce) {
         if (this.edit) {
            this.listener.removed();
         } else {
            this.listener.add(justOnce);
         }
      }

      public interface ScheduleAlertRowPanelListener {
         void removed();

         void add(boolean var1);
      }
   }
}
