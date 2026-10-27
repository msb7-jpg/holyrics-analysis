package com.limagiran.holyrics;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.PopUpOpenProgram;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.javaupdate.IUpdate;
import com.limagiran.javaupdate.JavaUpdate;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class Update implements IUpdate {
   public static final JavaUpdate INSTANCE = new JavaUpdate(new Update());

   public static void showNews() {
      SwingUtilities.invokeLater(() -> {
         try {
            JDialog d = new JDialog();
            d.setIconImages(Images.HOLYRICS_ICON);
            d.setTitle(I18N.WORD_UPDATE.val());
            d.setModal(true);
            d.setLayout(new BorderLayout());
            JFXPanel fx = new JFXPanel();
            fx.setFocusable(false);
            fx.setPreferredSize(new Dimension(750, 650));
            d.add(fx);
            d.pack();
            d.setLocationRelativeTo(null);
            d.addComponentListener(new ComponentAdapter() {
               @Override
               public void componentShown(ComponentEvent e) {
                  if (Settings.EnumSettings2.SHOW_UPDATE_2_0_0_FEEDBACK.is()) {
                     Settings.EnumSettings2.SHOW_UPDATE_2_0_0_FEEDBACK.set(false);
                  }
               }
            });
            InsertShortcut.addEscToDispose(d);
            Platform.setImplicitExit(false);
            String html = new String(Dir.getBytesJar("/com/limagiran/holyrics/file/news.txt"), "UTF-8").replace("\ufeff", "");
            Platform.runLater(() -> {
               try {
                  StackPane root = new StackPane();
                  Scene scene = new Scene(root);
                  WebView browser = new WebView();
                  WebEngine webEngine = browser.getEngine();
                  webEngine.loadContent(html);
                  webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
                     if (newState == State.SUCCEEDED) {
                        EventListener listener = ev -> {
                           String domEventType = ev.getType();
                           if (domEventType.equals("click")) {
                              String href = ((Element)ev.getTarget()).getAttribute("href");
                              ev.preventDefault();
                              switch (href) {
                                 case "http://stageView":
                                    PopupUtils.helpStageView(null);
                                    break;
                                 case "http://customFormatting":
                                    PopupUtils.customParagraph(null);
                                    break;
                                 case "http://live":
                                    PopupUtils.helpLive(null, null);
                                    break;
                                 case "http://communicationPanel":
                                    PopupUtils.helpCommunicationPanel(null);
                                    break;
                                 case "http://automaticShow":
                                    PopupUtils.aboutAutomaticShow(null);
                                    break;
                                 case "http://editTextRealTime":
                                    PopupUtils.aboutEditTextRealTime(null);
                                    break;
                                 case "http://slideLabelExample":
                                    JOptionPane.showMessageDialog(null, new ImageIcon(Images.load("exampleSlideLabel.jpg")), "", -1);
                              }
                           }
                        };
                        Document doc = browser.getEngine().getDocument();
                        NodeList nodeList = doc.getElementsByTagName("a");

                        for (int i = 0; i < nodeList.getLength(); i++) {
                           ((EventTarget)nodeList.item(i)).addEventListener("click", listener, false);
                        }
                     }
                  });
                  root.getChildren().add(browser);
                  fx.setScene(scene);
               } catch (Exception var6) {
               }
            });
            d.setVisible(true);
         } catch (Exception var3) {
         }
      });
   }

   @Override
   public String getAppName() {
      return "Holyrics";
   }

   @Override
   public String getAppDesc() {
      return "Este software é uma ferramenta de apoio para as Igrejas Evangélicas.\n - Exibir o Texto Sagrado;\n - Criar e editar letras de músicas para exibição;\n - Executar áudio e vídeo;\n - Alterar ordem das estrofes das músicas de forma dinâmica, sem alterar a letra original;\n - Personalizar temas com imagens ou animações para exibição dos slides;\n - Salvar músicas que serão tocadas nos próximos cultos e eventos;\n - Salvar apresentações em PDF, PowerPoint, JPG, etc.; - Consultar relatório e histórico das músicas tocadas;\n - Administrar a escala dos integrantes nos cultos e eventos;\n - Consultar dados do programa e controlar apresentações pelo aplicativo <b>Holyrics</b> no celular.\n";
   }

   @Override
   public String getVersion() {
      return "2.2.0";
   }

   @Override
   public String getUrlVersion() {
      return "http://www.holyrics.com/download/doc/lastVersion.txt";
   }

   @Override
   public String getUrlLinkApp() {
      return "http://www.holyrics.com/download/doc/link.php";
   }

   @Override
   public String getUrlHash() {
      return "http://www.holyrics.com/download/doc/lastVersionHash.txt";
   }

   @Override
   public void runBeforeRestart() {
      Settings.EnumSettings.SHOW_UPDATE_NEWS.set(true);
      Settings.SET.saveNow();
   }

   @Override
   public void disableCheckUpdate() {
      Settings.EnumSettings.LONGTIME_UPDATE_DISABLED.set(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(13L));
   }

   public static void apply(PopUpOpenProgram popup) {
      try {
         String format = I18N.WORD_UPDATE.val() + " 1.1.0 (%02d%%)";
         List<File> files = Arrays.asList(new File(Values.PATH_BACKGROUND_VIDEO).listFiles());
         files.stream().filter(f -> !f.getName().startsWith("long") && f.getName().endsWith(".mp4")).forEach(f -> {
            try {
               String fileName = f.getName();
               File path = new File(Values.PATH_BACKGROUND_VIDEO + fileName);
               if (Settings.SET.get("bgv_long_hash-" + fileName, "").isEmpty() || !Background.createLongPathVideo(path).exists()) {
                  Background.createLongVideo(path, fileName);
               }

               int progress = (int)((double)(files.indexOf(f) + 1) / (double)files.size() * 100.0);
               popup.setProgress(String.format(format, progress));
            } catch (Exception var7) {
            }
         });
      } catch (Exception var3) {
      }
   }
}
