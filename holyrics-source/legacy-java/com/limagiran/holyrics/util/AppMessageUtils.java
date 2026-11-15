package com.limagiran.holyrics.util;

import com.limagiran.holyrics.model.AppMessage;
import java.util.Date;
import java.util.function.Supplier;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument;

public class AppMessageUtils {
   private static final String HTML_MODEL = "<html lang='pt-br'>  <head>    <title>Chat</title>    <style>      body {        font-family: Lucida Sans Unicode;        font-size: #FONT_SIZE#px;        margin: 2px 1px      }      div.msg-ctt-default {        margin: 2px 3px;        padding: 2px 4px;        background-color: #FFFFFF;        border-style: solid;        border-width: 0px 1px 1px 0px;        border-color: #BBBBBB;      }      div.msg-ctt-myself {        margin: 2px 3px;        padding: 2px 4px;        background-color: #C8EDFC;        border-style: solid;        border-width: 0px 1px 1px 0px;        border-color: #A0BDC9;      }      div.desc {        font-family: Courier New;        font-weight: bold;        color: #444444;        font-size: 90%;      }      div.desc a {        font-family: Arial;        color: #444444;        text-decoration: none;      }      div.msg {        margin-left: 16px      }      div.content {        margin: 0px;        padding: 0px      }    </style>  </head>  <body>    <div id='flag'>    </div>  </body></html>";

   public static String htmlModel(int fontSize) {
      return "<html lang='pt-br'>  <head>    <title>Chat</title>    <style>      body {        font-family: Lucida Sans Unicode;        font-size: #FONT_SIZE#px;        margin: 2px 1px      }      div.msg-ctt-default {        margin: 2px 3px;        padding: 2px 4px;        background-color: #FFFFFF;        border-style: solid;        border-width: 0px 1px 1px 0px;        border-color: #BBBBBB;      }      div.msg-ctt-myself {        margin: 2px 3px;        padding: 2px 4px;        background-color: #C8EDFC;        border-style: solid;        border-width: 0px 1px 1px 0px;        border-color: #A0BDC9;      }      div.desc {        font-family: Courier New;        font-weight: bold;        color: #444444;        font-size: 90%;      }      div.desc a {        font-family: Arial;        color: #444444;        text-decoration: none;      }      div.msg {        margin-left: 16px      }      div.content {        margin: 0px;        padding: 0px      }    </style>  </head>  <body>    <div id='flag'>    </div>  </body></html>"
         .replace("#FONT_SIZE#", String.valueOf(Utils.range(fontSize, 8, 36)));
   }

   public static String format(AppMessage msg) {
      return String.format(
         "<div id='id-%d' class='%s'><div class='desc'>[%s] <a href='http://name=%s'>%s</a>:</div><div class='msg'>%s</div></div>",
         msg.datetime,
         "msg-ctt-" + (msg.isMyself() ? "myself" : "default"),
         DateLG.SDF_HH_MM.format(new Date(msg.datetime)),
         StringUtils.escapeHTML(msg.name),
         StringUtils.escapeHTML(msg.name),
         StringUtils.escapeHTML(msg.message)
      );
   }

   public static void insert(JTextPane textPane, AppMessage msg) {
      try {
         JViewport viewPort = (JViewport)textPane.getParent();
         JScrollPane scroll = (JScrollPane)viewPort.getParent();
         boolean toEnd = SwingUtils.scrollIsEnd(scroll, 36);
         long len = (long)textPane.getText().length();
         HTMLDocument doc = (HTMLDocument)textPane.getStyledDocument();
         doc.insertBeforeStart(doc.getElement("flag"), format(msg));
         if (!toEnd) {
            return;
         }

         Supplier<Boolean> check = () -> (long)textPane.getText().length() != len;
         Runnable runnable = () -> SwingUtilities.invokeLater(() -> {
               SwingUtils.scrollToEnd(scroll);
               textPane.setCaretPosition(textPane.getDocument().getLength());
            });
         Utils.startThreadDaemonMaxSleep(check, runnable, 60000L, true);
      } catch (Exception var10) {
      }
   }

   public static void remove(JTextPane textPane, AppMessage msg) {
      try {
         HTMLDocument doc = (HTMLDocument)textPane.getStyledDocument();
         doc.removeElement(doc.getElement("id-" + msg.datetime));
      } catch (Exception var3) {
      }
   }
}
