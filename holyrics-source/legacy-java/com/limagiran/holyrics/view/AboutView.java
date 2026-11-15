package com.limagiran.holyrics.view;

import com.limagiran.holyrics.Update;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

public class AboutView extends JDialog {
   private static final String ABOUT;
   private static AboutView INSTANCE;
   private JTextPane jTextPaneAbout;

   private static String libraryTableHTML() {
      StringBuilder sb = new StringBuilder("<table>");
      BiConsumer<String, String> consumer = (name, info) -> sb.append("<tr><td class='align-right'><b>")
            .append(name)
            .append("</b></td><td><a href='" + info + "'>" + info + "</a>")
            .append("</td></tr>");
      consumer.accept("WebLaF - Java Look and Feel 1.29", "http://weblookandfeel.com/");
      consumer.accept("SQLite", "http://www.sqlite.org/");
      consumer.accept("SQLite JDBC Driver 3.8.11.2", "https://github.com/xerial/sqlite-jdbc");
      consumer.accept("VLC Media Player", "http://www.videolan.org/vlc/");
      consumer.accept("VLCJ 2.4.1", "https://github.com/caprica/vlcj");
      consumer.accept("Gson 2.4", "https://github.com/google/gson");
      consumer.accept("Apache POI", "https://poi.apache.org/");
      consumer.accept("iText PDF 5.5.7", "https://github.com/itext/itextpdf/");
      return sb.append("</table>").toString();
   }

   private AboutView(Frame parent) {
      super(parent);
      this.initComponents();
      this.init();
   }

   private void init() {
      InsertShortcut.addEscToDispose(this);
   }

   private void initComponents() {
      this.jTextPaneAbout = new JTextPane();
      this.setDefaultCloseOperation(2);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.about")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setModal(true);
      this.setResizable(false);
      this.jTextPaneAbout.setEditable(false);
      this.jTextPaneAbout.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jTextPaneAbout.setContentType("text/html; charset=UTF-8");
      this.jTextPaneAbout.setText(ABOUT);
      this.jTextPaneAbout.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent evt) {
            AboutView.this.jTextPaneAboutHyperlinkUpdate(evt);
         }
      });
      this.jTextPaneAbout.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent evt) {
            AboutView.this.jTextPaneAboutComponentResized(evt);
         }
      });
      this.getContentPane().add(this.jTextPaneAbout, "First");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jTextPaneAboutHyperlinkUpdate(HyperlinkEvent evt) {
      BooleanOptional.runIfTrue(evt.getEventType() == EventType.ACTIVATED, () -> this.hyperlink(evt.getURL()));
   }

   private void jTextPaneAboutComponentResized(ComponentEvent evt) {
      SwingUtils.runOnUIThread(() -> {
         this.pack();
         this.setLocationRelativeTo(null);
      });
   }

   public static void main(Frame parent) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
         }

         INSTANCE = new AboutView(parent);
         INSTANCE.setVisible(true);
      });
   }

   private void hyperlink(URL url) {
      try {
         String var2 = url.toString();
         byte var3 = -1;
         switch (var2.hashCode()) {
            case 73501570:
               if (var2.equals("http://showNews")) {
                  var3 = 0;
               }
            default:
               switch (var3) {
                  case 0:
                     Update.showNews();
                     break;
                  default:
                     Desktop.getDesktop().browse(url.toURI());
               }
         }
      } catch (Exception var4) {
      }
   }

   static {
      StringBuilder sb = new StringBuilder();
      sb.append("<html>")
         .append("<style>")
         .append("li {margin:2px}")
         .append("body {font-family:Trebuchet MS;font-size:10px; text-align:center}")
         .append("td.align-right{text-align:right}")
         .append("</style>")
         .append("<div style='width:500px'>")
         .append("<div style='text-align:center'>")
         .append(Images.getImageHTMLTag("holyricsIconLabel128.png"))
         .append("<br>")
         .append("Holyrics - Lima Giran<br>© 2016-2017")
         .append("<a href='http://showNews'>")
         .append("<br>Versão: ")
         .append(Update.INSTANCE.app.getVersion())
         .append("</a><br>")
         .append("</div>")
         .append("<br>")
         .append("Desenvolvido por:<br>")
         .append("<b>Vinicius Lima da Silva</b><br>")
         .append("<b>Miriã Machado Giran</b><br>")
         .append("<br>")
         .append("<a href='mailto:holyrics@holyrics.com'>holyrics@holyrics.com</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
         .append("<a href='http://www.holyrics.com/'>http://www.holyrics.com/</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
         .append("Whatsapp: +55 15 98127 8370<br><br>")
         .append("<br>")
         .append("<b>Bibliotecas e componentes adicionais que integram Holyrics:</b><br><br>")
         .append(libraryTableHTML())
         .append("<br><br>")
         .append("<div style='text-align:justify;font-family:Georgia;font-size:14px'>")
         .append(
            "Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito, para que todo aquele que nele crê não pereça, mas tenha a vida eterna.&nbsp;&nbsp;&nbsp;&nbsp;João 3:16"
         )
         .append("</div>")
         .append("</div>");
      ABOUT = sb.toString();
   }
}
