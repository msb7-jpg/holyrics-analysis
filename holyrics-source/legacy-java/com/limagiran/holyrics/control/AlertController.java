package com.limagiran.holyrics.control;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotification;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Alert;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.view.CommunicationPanel;
import com.limagiran.holyrics.view.panel.AlertPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class AlertController {
   private static final Map<Alert, List<Integer>> SCHEDULED_ALERTS_TEMP = new HashMap<>();
   private static final String FILE_NAME = "alerts.hly";
   private static final List<Alert> ALERTS = ControllerFactory.load("alerts.hly", Alert.class);

   public static void fireMinuteEvent(int min) {
      getCurrentAlerts(min).forEach(AlertController::showNotification);
   }

   public static Alert add(Alert a) {
      if (!ALERTS.contains(a)) {
         ALERTS.add(a);
         save();
      }

      return a;
   }

   public static Alert remove(Alert a) {
      ALERTS.remove(a);
      save();
      return a;
   }

   public static List<Alert> getAll() {
      return new ArrayList<>(ALERTS);
   }

   public static void save() {
      ControllerFactory.save(ALERTS, "alerts.hly");
   }

   public static synchronized void addAlertScheduleTemp(Alert a, int hour, int min) {
      List<Integer> list = SCHEDULED_ALERTS_TEMP.getOrDefault(a, new ArrayList<>());
      BooleanOptional.runIfTrue(!list.contains(hour * 60 + min), () -> list.add(hour * 60 + min));
      SCHEDULED_ALERTS_TEMP.put(a, list);
   }

   private static synchronized List<Alert> getCurrentAlerts(Integer totalMinute) {
      List<Alert> _return = new ArrayList<>();
      ALERTS.stream().forEach(a -> a.getSchedulesFiltered().forEach(as -> {
            if (as.getTotalMinute() == totalMinute) {
               _return.add(a);
            }
         }));
      SCHEDULED_ALERTS_TEMP.forEach((k, v) -> {
         if (v.contains(totalMinute)) {
            _return.add(k);
            v.remove(totalMinute);
         }
      });
      return _return;
   }

   private static void showNotification(Alert a) {
      try {
         WebNotification wn = new WebNotification();
         wn.setIcon(new ImageIcon(Images.load("notifications48.png")));
         WebLabel label = new WebLabel("<html>" + StringUtils.maxLength(a.getText(), 128, true));
         label.setMaximumWidth(350);
         WebButton display = new WebButton(I18N.MSG_DISPLAY_ON_SCREEN.val());
         WebButton sendToCP = new WebButton(I18N.MSG_SEND_TO_COMMUNICATION_PANEL.val());
         WebButton close = new WebButton(I18N.WORD_CLOSE.val());
         Arrays.asList(display, sendToCP, close).forEach(b -> {
            b.setShadeColor(Values.COLOR_TRANSPARENT);
            b.setFont(b.getFont().deriveFont(1));
            b.addActionListener(e -> wn.hidePopup());
         });
         display.addActionListener(e -> {
            AlertPanel.INSTANCE.setText(a.getText());
            AlertPanel.INSTANCE.setShow(true);
         });
         sendToCP.addActionListener(e -> {
            CommunicationPanel.setAlertText(a.getText());
            CommunicationPanel.openCommunication();
         });
         JPanel buttonsPanel = new JPanel(new FlowLayout(2, 8, 0));
         buttonsPanel.add(display);
         buttonsPanel.add(sendToCP);
         buttonsPanel.add(close);
         buttonsPanel.setOpaque(false);
         JPanel content = new JPanel(new BorderLayout(0, 8));
         content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
         content.setOpaque(false);
         content.add(label, "Center");
         content.add(buttonsPanel, "Last");
         wn.setContent(content);
         NotificationManager.showNotification(wn);
      } catch (Exception var8) {
      }
   }
}
