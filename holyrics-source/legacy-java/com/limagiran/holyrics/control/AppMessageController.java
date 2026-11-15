package com.limagiran.holyrics.control;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotification;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.AppMessage;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.view.CommunicationPanel;
import com.limagiran.holyrics.webservice.HolyricsWS;
import com.limagiran.holyrics.webservice.Pack;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AppMessageController {
   private static final List<AppMessage> MESSAGES = new ArrayList<>();
   private static final List<AppMessageController.AppMessageListener> LISTENERS = new ArrayList<>();
   private static final Object LISTENERS_LOCK = new Object();
   private static volatile WebNotification lastNotification;

   public static synchronized void newMessage(String userId, String name, String message) {
      if (!message.replace(" ", "").isEmpty()) {
         if (MESSAGES.size() >= 256) {
            reduce();
         }

         AppMessage msg = AppMessage.create(userId, name, message);
         MESSAGES.add(msg);
         insert(msg);
      }
   }

   private static void reduce() {
      synchronized (LISTENERS_LOCK) {
         int tmp = 204;

         while (MESSAGES.size() > 204) {
            AppMessage msg = MESSAGES.remove(0);
            LISTENERS.forEach(l -> l.remove(msg));
         }
      }
   }

   private static void insert(AppMessage msg) {
      synchronized (LISTENERS_LOCK) {
         LISTENERS.forEach(l -> l.add(msg));
      }
   }

   public static void addListener(AppMessageController.AppMessageListener listener) {
      synchronized (LISTENERS_LOCK) {
         LISTENERS.add(listener);
      }
   }

   public static void removeListener(AppMessageController.AppMessageListener listener) {
      synchronized (LISTENERS_LOCK) {
         LISTENERS.remove(listener);
      }
   }

   public static void notification(AppMessage msg) {
      if (Settings.EnumSettings2.CHAT_NOTIFICATION.is()) {
         SwingUtilities.invokeLater(() -> {
            WebNotification wn = new WebNotification();
            wn.setIcon(new ImageIcon(Images.load("notifications48.png")));
            WebButton openChat = new WebButton(I18N.MSG_OPEN_CHAT.val());
            WebButton close = new WebButton(I18N.WORD_CLOSE.val());
            Arrays.asList(openChat, close).forEach(b -> {
               b.setShadeColor(Values.COLOR_TRANSPARENT);
               b.setFont(b.getFont().deriveFont(1));
               b.addActionListener(e -> wn.hidePopup());
            });
            openChat.addActionListener(e -> CommunicationPanel.openChat(true));
            JPanel buttonsPanel = new JPanel(new FlowLayout(2, 8, 0));
            buttonsPanel.add(openChat);
            buttonsPanel.add(close);
            buttonsPanel.setOpaque(false);
            JPanel content = new JPanel(new BorderLayout(0, 8));
            content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            content.setOpaque(false);
            content.add(new WebLabel(msg.toStringHTML()), "Center");
            content.add(buttonsPanel, "Last");
            wn.setContent(content);
            cancelNotification();
            lastNotification = wn;
            NotificationManager.showNotification(wn);
         });
      }
   }

   public static synchronized List<AppMessage> get(long lastDatetime) {
      List<AppMessage> _return = new ArrayList<>();

      for (AppMessage msg : MESSAGES) {
         if (msg.datetime > lastDatetime) {
            _return.add(msg);
         }
      }

      return _return;
   }

   public static String getAsJson(long lastDatetime) {
      String _return = "[]";

      try {
         List<AppMessage> list = get(lastDatetime);
         _return = HolyricsWS.GSON.toJson(list);
         list.clear();
      } catch (Exception var4) {
      }

      return _return;
   }

   public static String getAsJson(Pack pack) {
      return getAsJson(pack.getLong("last-datetime", Long.MAX_VALUE));
   }

   public static void cancelNotification() {
      Optional.ofNullable(lastNotification).ifPresent(ln -> {
         ln.setAnimate(false);
         ln.hidePopup();
      });
   }

   public interface AppMessageListener {
      void add(AppMessage var1);

      void remove(AppMessage var1);
   }
}
