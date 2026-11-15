package com.limagiran.holyrics.util;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.webservice.HolyricsWS;
import com.limagiran.holyrics.webservice.RemoteControlBibleWS;
import java.awt.Font;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class WebServiceUtils {
   private static WebServiceUtils.IP CURRENT_IP = load();

   private static WebServiceUtils.IP load() {
      try {
         String last = Settings.EnumSettings2.WEB_SERVICE_IP_LAST_NETWORK_NAME.getString();
         WebServiceUtils.IP found = getIPs().stream().filter(ip -> ip.name.equals(last)).findFirst().orElse(null);
         return found != null ? found : getFirst();
      } catch (Exception var2) {
         return getFirst();
      }
   }

   private static WebServiceUtils.IP getFirst() {
      return getIPs().stream().findFirst().orElse(null);
   }

   public static WebServiceUtils.IP getIP() {
      List<WebServiceUtils.IP> ips = getIPs();
      return CURRENT_IP != null && ips.contains(CURRENT_IP) ? CURRENT_IP : (CURRENT_IP = getFirst());
   }

   public static List<WebServiceUtils.IP> getIPs() {
      List<WebServiceUtils.IP> _return = new ArrayList<>();

      try {
         Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

         while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            Enumeration<InetAddress> ee = ni.getInetAddresses();

            while (ee.hasMoreElements()) {
               InetAddress i = ee.nextElement();
               if (!i.isLoopbackAddress()) {
                  String ip = i.getHostAddress();
                  if (checkIPV4(ip)) {
                     _return.add(new WebServiceUtils.IP(i.getHostAddress(), ni.getName()));
                  }
               }
            }
         }
      } catch (Exception var6) {
      }

      return _return;
   }

   public static boolean checkIPV4(String ip) {
      return ip.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
   }

   public static void popupChangeIP() {
      PopUpWorker.exe(new MySwingWorker<Void, Void>(null) {
         protected Void doInBackground() throws Exception {
            List<WebServiceUtils.IP> ips = WebServiceUtils.getIPs();
            if (ips.isEmpty()) {
               Utils.error(I18N.MSG_WS_EMPTY_IP.val());
               return null;
            } else if (ips.size() == 1) {
               Utils.msg(I18N.MSG_WS_UNIQUE_IP_X.format(ips.get(0)));
               return null;
            } else {
               SwingUtilities.invokeLater(() -> {
                  JList<WebServiceUtils.IP> list = new JList<>(ips.toArray(new WebServiceUtils.IP[0]));
                  list.setSelectedValue(WebServiceUtils.CURRENT_IP, true);
                  list.setSelectionMode(0);
                  list.setFont(new Font("Courier New", 0, 24));
                  list.setVisibleRowCount(Math.min(ips.size(), 8));
                  list.setFocusable(false);
                  list.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
                  if (JOptionPane.showConfirmDialog(null, new JScrollPane(list), "IPs", 2, -1) == 0) {
                     WebServiceUtils.CURRENT_IP = Optional.ofNullable(list.getSelectedValue()).orElse(WebServiceUtils.getFirst());
                     if (WebServiceUtils.CURRENT_IP != null) {
                        Settings.EnumSettings2.WEB_SERVICE_IP_LAST_NETWORK_NAME.set(WebServiceUtils.CURRENT_IP.name);
                     }

                     HolyricsWS.close();
                     HolyricsWS.open();
                     RemoteControlBibleWS.close();
                     RemoteControlBibleWS.open();
                  }
               });
               return null;
            }
         }
      });
   }

   public static class IP {
      public final String ip;
      public final String name;

      public IP(String ip, String name) {
         this.ip = ip;
         this.name = name;
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof WebServiceUtils.IP)) {
            return super.equals(obj);
         } else {
            WebServiceUtils.IP _ip = (WebServiceUtils.IP)obj;
            return _ip.ip.equals(this.ip) && _ip.name.equals(this.name);
         }
      }

      @Override
      public int hashCode() {
         int hash = 5;
         hash = 97 * hash + Objects.hashCode(this.ip);
         return 97 * hash + Objects.hashCode(this.name);
      }

      @Override
      public String toString() {
         return String.valueOf(this.ip);
      }
   }
}
