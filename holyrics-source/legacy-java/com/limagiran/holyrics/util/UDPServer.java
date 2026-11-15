package com.limagiran.holyrics.util;

import com.limagiran.holyrics.settings.Settings;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Optional;

public class UDPServer {
   private static final String PING = "#getServerHolyrics";
   private static final int PORT = 808;
   private static final WebServiceUtils.IP IP_EMPTY = new WebServiceUtils.IP("", "");

   public static void open() {
      Utils.startThreadDaemon(() -> {
         while (true) {
            if (!Settings.EnumSettings2.WEB_SERVICE_ENABLED.is()) {
               Utils.sleep(2000L);
            } else {
               try (DatagramSocket socket = new DatagramSocket(808)) {
                  socket.setSoTimeout(10000);
                  DatagramPacket in = new DatagramPacket(new byte[256], 256);
                  socket.receive(in);
                  String msg = new String(in.getData(), 0, in.getLength());
                  if (msg.equals("#getServerHolyrics")) {
                     byte[] outBuf = Optional.ofNullable(WebServiceUtils.getIP()).orElse(IP_EMPTY).ip.getBytes();
                     DatagramPacket out = new DatagramPacket(outBuf, 0, outBuf.length, in.getAddress(), in.getPort());
                     socket.send(out);
                  }
               } catch (Exception var16) {
               }
            }
         }
      });
   }
}
