package com.limagiran.holyrics.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Log {
   private static final String URL = "http://www.holyrics.com/log.php";

   public static void send(Exception e) {
      new Thread(
            () -> {
               try {
                  StringBuilder txt = new StringBuilder();
                  String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + " - " + e;
                  txt.append("\n").append(date).append("\n\n");

                  for (int i = e.getStackTrace().length - 1; i >= 0; i--) {
                     txt.append(e.getStackTrace()[i]).append("\n");
                  }

                  Map<String, String> map = new HashMap<>();
                  map.put("log", txt.toString());
                  Download.sendPost("http://www.holyrics.com/log.php", map);
                  String msg = "<html>Oh, desculpe!\nOcorreu um erro crítico no programa.\nVocê pode entrar em contato conosco para tentar solucioná-lo.\nE-mail: suporte@holyrics.com.br\nWhatsapp: +55 15 98127 8370";
                  SwingUtilities.invokeLater(
                     () -> JOptionPane.showMessageDialog(
                           null,
                           "<html>Oh, desculpe!\nOcorreu um erro crítico no programa.\nVocê pode entrar em contato conosco para tentar solucioná-lo.\nE-mail: suporte@holyrics.com.br\nWhatsapp: +55 15 98127 8370",
                           "Erro Crítico",
                           0
                        )
                  );
               } catch (Exception var5) {
               }
            }
         )
         .start();
   }
}
