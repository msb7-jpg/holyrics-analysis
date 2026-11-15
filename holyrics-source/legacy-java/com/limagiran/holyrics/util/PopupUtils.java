package com.limagiran.holyrics.util;

import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.spinner.WebSpinner;
import com.alee.laf.text.WebEditorPane;
import com.alee.laf.text.WebTextField;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Schedule;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ComboBoxModelEvent;
import com.limagiran.holyrics.swing.ComboBoxRendererEvent;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.swing.ListCellRendererOrder;
import com.limagiran.holyrics.swing.ListCellRendererTheme;
import com.limagiran.holyrics.swing.ListModelTheme;
import com.limagiran.holyrics.swing.MarginSetDialog;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.view.panel.MusicSearchPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.DateEditor;
import javax.swing.event.HyperlinkEvent.EventType;

public class PopupUtils {
   private static final String STYLE_FONT = "font-size:11px;font-family:Verdana";
   private static PopupUtils.MusicSearchPanelChooser musicChooser;

   public static void showDefault(Component parent, String htmlText, String title, int width, Runnable callback, Map<String, Runnable> links) {
      JTextPane tp = new JTextPane();
      tp.setContentType("text/html;charset=UTF-8");
      tp.setEditable(false);
      tp.setFocusable(false);
      tp.setText(
         "<html><style>  body {"
            + (width > 0 ? "    width: " + width + "px;" : "")
            + "    text-align: justify;    "
            + "font-size:11px;font-family:Verdana"
            + "  }</style><div>"
            + htmlText
            + "</div>"
      );
      tp.addHyperlinkListener(e -> Utils.safeException(() -> {
            if (links != null && e.getEventType() == EventType.ACTIVATED) {
               Runnable run = links.get(e.getURL().toString());
               if (run != null) {
                  run.run();
               }
            }
         }));
      JOptionPane.showMessageDialog(parent, tp, title, -1);
      Optional.ofNullable(callback).ifPresent(Runnable::run);
   }

   private static void showDefault(String htmlText, String title, int width, Runnable callback) {
      showDefault(null, htmlText, title, width, callback, null);
   }

   private static void showDefault(Component parent, String htmlText, String title, int width, Runnable callback) {
      showDefault(parent, htmlText, title, width, callback, null);
   }

   private static void showDefault(String htmlText, String title, int width, Runnable callback, Map<String, Runnable> links) {
      showDefault(null, htmlText, title, width, callback, links);
   }

   private static void dialogPopupShow(Component component, int width, int height) {
      WebDialog dialog = new WebDialog((Frame)null, true);
      dialog.setDefaultCloseOperation(2);
      InsertShortcut.addEscToDispose(dialog);
      dialog.setIconImage(Images.ICON_HOLYRICS_16);
      dialog.setLayout(new BorderLayout());
      dialog.add(component, "Center");
      dialog.setSize(width, height);
      dialog.setPreferredSize(dialog.getSize());
      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);
   }

   private static String spanAlert(String value, int size) {
      return "<span style='color:#BF0000;font-size:" + size + "px;font-family:Verdana'>" + value + "</span>";
   }

   private static String spanAlert(String value) {
      return spanAlert(value, 9);
   }

   public static void helpStageView(Runnable callback) {
      JTextPane tp = new JTextPane();
      tp.setContentType("text/html;charset=UTF-8");
      tp.setEditable(false);
      tp.setText(
         "<html><div style='width: 600px;font-size:11px;font-family:Verdana'><b>Visão do Palco</b> é uma configuração que altera a exibição dos slides para um monitor específico.<br>Se você tem uma saída de vídeo para a projeção vista pelo público e tem outra saída de vídeo para quem está no <b>palco</b>, você pode configurar o monitor de <b>retorno</b> de modo diferente do que está sendo exibido para o público.<br>A transmissão para o aplicativo também é configurável.<p>"
            + spanAlert(
               "Obs.: A função <b>visão do palco</b> funciona apenas com saídas de vídeos diferentes diretamente da placa de vídeo do computador. Mesmo que você utilize 2 ou mais monitores para projeção mas eles utilizem a mesma saída de vídeo (com um adaptador que duplica o cabo, por exemplo), não é possível utilizar o modo <b>visão do palco</b>."
            )
            + "</p><p><b>Caixa de seleção:</b> Define uma alteração no texto do slide. Ex.: Apresentar a primeira linha do próximo slide no final do slide atual.</p><p><b>Maiúsculo:</b> Apresenta o texto todo em maiúsculo.</p><p><b>Remover quebra de linha:</b> Apresenta o texto de forma contínua, quebrando linha automaticamente no limite do slide e ocupando o máximo de espaço lateral possível.</p><p><b>Tema Personalizado:</b> Altera o tema de exibição do slide.</p><p><b>Habilitar para Bíblia:</b> Habilita as configurações também para versículos da Bíblia (exceto a configuração da caixa de seleção).</p><br>"
            + spanAlert("Obs.: Temas de vídeo não funcionam em monitores de retorno, somente no monitor do público.")
            + "</div>"
      );
      JOptionPane.showMessageDialog(null, tp, I18N.WORD_HELP.val(), -1);
      Optional.ofNullable(callback).ifPresent(Runnable::run);
   }

   public static void customParagraph(Runnable callback) {
      JTextPane tp = new JTextPane();
      tp.setContentType("text/html;charset=UTF-8");
      tp.setEditable(false);
      tp.setFocusable(false);
      tp.setText(
         "<html><div style='width: 600px;font-size:11px;font-family:Verdana;padding: 0px 12px 0px 12px;background-color:#f8f8f8;text-align:justify'><p>  Você pode adicionar uma edição personalizada para um parágrafo específico da letra. Por exemplo, aumentar <b>proporcionalmente</b> o tamanho da fonte do coro.<br>  E também colocar um texto de identificação (coro, verso, etc...) que irá aparecer apenas no rodapé de slides. <a href='http://exampleSlideLabel'>(Ver exemplo)</a><br>  <span style='color:#333333;font-size:85%'><br>*Esta funcionalidade também funciona na janela de Apresentação Rápida</span></p><p>  Configurações disponíveis:<br>  <b>Negrito:</b> código = n<br>  <b>Itálico:</b> código = i<br>  <b>Todo o texto em maiúsculo:</b> código = m<br>  <b>Tamanho da fonte:</b> porcentagem = 20, 30, -15, etc... (colocar o caractere '%' após o número é desnecessário).<br>  <b>Identificação:</b> texto dentro de parênteses = (coro), (verso), (repetir), (final), etc...<br></p><p>  Para adicionar uma formatação personalizada, adicione uma linha no início do parágrafo começando com duas <span style='white-space:nowrap'>\"hashtags\" ##</span> e os códigos que quiser logo em seguida (a ordem dos códigos não importa).<br>  Exemplo:<br>  <br>  <span style='font-size:90%'>    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Pois prazer maior não há<br>    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Que me render e Te adorar<br>    <br>    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;##n15<br>    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Tudo que há em mim<br>    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Quero Te ofertar<br>    <br>    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Mas ainda é pouco eu sei<br>    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Se comparado ao que ganhei<br>  </span></p><p>  No exemplo, ao apresentar a letra para projeção, o segundo parágrafo será apresentado em negrito e com o tamanho da fonte 15% maior. Os outros parágrafos serão apresentados normalmente com as configurações do Tema.<br>  "
            + spanAlert("<b>Obs.: Caso o tema utilizado já esteja configurado para negrito, por exemplo, essa formatação personalizada não fará diferença.</b>")
            + "</p><br>Alguns exemplos de formatação:<br><ul style='list-style-type:none;'>  <li><code>Negrito e itálico = ##ni ou ##in</code></li>  <li><code>Maiúsculo = ##m</code></li>  <li><code>25% maior = ##25</code></li>  <li><code>itálico e 5% menor = ##-5i ou ##i-5</code></li></ul><br></div>"
      );
      tp.addHyperlinkListener(e -> {
         if (e.getEventType() == EventType.ACTIVATED) {
            JOptionPane.showMessageDialog(null, new ImageIcon(Images.load("exampleSlideLabel.jpg")), "", -1);
         }
      });
      WebTextField textFieldDefaultFormat = new WebTextField(10);
      textFieldDefaultFormat.setDocument(SwingUtils.createDocMaxSize(8));
      textFieldDefaultFormat.setText(Settings.EnumSettings2.TEXT_FORMATTING_DEFAULT_FORMAT.getString());
      textFieldDefaultFormat.getDocument().addDocumentListener(new DocumentListenerAuto(400L, () -> {
         String var10000 = Settings.EnumSettings2.TEXT_FORMATTING_DEFAULT_FORMAT.set(textFieldDefaultFormat.getText());
      }));
      WebPanel footer = new WebPanel(new GridLayout(2, 1));
      footer.setOpaque(false);
      footer.setMargin(0, 4, 6, 0);
      footer.add(
         new WebLabel(
            "<html>Você pode utilizar o código <code>##p</code> para utilizar uma formatação <b>padrão</b>.<br>Todos os parágrafos definidos com <code>##p</code> utilizarão a formatação definida no campo abaixo."
         )
      );
      footer.add(
         new WebPanel(
            new FlowLayout(0, 0, 0), new Component[]{new WebLabel("<html><b>Formatação Personalizada Padrão:&nbsp;&nbsp;</b>##"), textFieldDefaultFormat}
         )
      );
      WebScrollPane scroll = new WebScrollPane(tp);
      scroll.setMaximumHeight(450);
      scroll.setVerticalScrollBarPolicy(22);
      tp.setCaretPosition(0);
      WebLabel title = new WebLabel(I18N.MSG_CUSTOM_FORMATTING.val(), new ImageIcon(Images.load("style24.png")), 0, new Object[0]).setFontSize(24);
      WebPanel content = new WebPanel(new BorderLayout(0, 6));
      content.add(new WebPanel(new FlowLayout(1, 15, 0), new Component[]{title}), "First");
      content.add(scroll, "Center");
      content.add(footer, "Last");
      JOptionPane.showMessageDialog(null, content, I18N.MSG_CUSTOM_FORMATTING.val(), -1);
      Optional.ofNullable(callback).ifPresent(Runnable::run);
   }

   public static void helpHItemTab(Runnable callback) {
      String text = "<html>\n<div style='font-size:11px;font-family:Verdana;width: 500px;text-align:justify;background-color:#eeeeee; border: 4px #eeeeee solid'>\n<p>&nbsp;&nbsp;&nbsp;&nbsp;\nCada lista representa uma pasta interna do programa Holyrics: Áudio, Vídeo e Imagem. Para adicionar arquivos: clique no ícone "
         + Images.getImageHTMLTag("open16.png")
         + " da respectiva aba para abrir a pasta no Windows e cole os arquivos na pasta. A lista no programa é atualizada automaticamente com os arquivos da pasta (exceto se alguma apresentação estiver ativa).\n</p>\n<p>&nbsp;&nbsp;&nbsp;&nbsp;\n<b>Observação:</b> EVITE renomear os arquivos na pasta, pois se você criar um atalho para um arquivo numa lista de reprodução, favoritos, etc. e depois renomeá-lo na pasta, o atalho no programa não irá funcionar mais, você precisará criar um novo atalho.\n</p>\n<p>&nbsp;&nbsp;&nbsp;&nbsp;\nEm cada uma das pastas (Áudio, Vídeo e Imagem), você pode criar outras pastas para serem uma lista de arquivos no programa. Por exemplo: Você pode criar uma pasta chamada <b>Apresentação</b> dentro da pasta de <b>Imagens</b> e adicionar imagens nela. A pasta será um item da lista no programa Holyrics e ao clicar nela todas as imagens serão abertas para apresentação de slides.\n</p>\n<p>&nbsp;&nbsp;&nbsp;&nbsp;\nVocê pode adicionar um atalho para estas pastas na sua área de trabalho clicando <a href='http://addShortcutDesktop'>AQUI</a>.\n</p>\n</div>";
      WebEditorPane editorPaneHelp = new WebEditorPane("text/html;charset=UTF-8", text);
      editorPaneHelp.setEditable(false);
      editorPaneHelp.addHyperlinkListener(e -> {
         if (e.getEventType() == EventType.ACTIVATED) {
            if (String.valueOf(e.getURL()).equals("http://addShortcutDesktop")) {
               File path = new File(Values.PATH_MEDIA).getAbsoluteFile();
               if (DesktopShortcut.create("Biblioteca Holyrics", path).workingDirectory(path).createDesktop()) {
                  Utils.msg(I18N.WORD_DONE.val() + "!");
               } else {
                  Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
               }
            }
         }
      });
      JOptionPane.showMessageDialog(null, editorPaneHelp, I18N.WORD_HELP.val(), -1);
      Optional.ofNullable(callback).ifPresent(Runnable::run);
   }

   public static void helpDisplaySettingsButtons(Runnable callback) {
      JTextPane tp = new JTextPane();
      tp.setContentType("text/html;charset=UTF-8");
      tp.setEditable(false);
      tp.setText(
         "<html><style>  td {    text-align: center;  }  .slide {    border: 1px solid black;    width: 128px;    height: 72px;    background-color: #f8f8f8;  }</style><div style='text-align:justify;width: 600px;font-size:11px;font-family:Verdana'><p>"
            + Images.getImageHTMLTag("toUpperCase24.png")
            + "&nbsp;&nbsp;Exibe a letra da música em maiúsculo.<br></p><p>"
            + Images.getImageHTMLTag("underline24.png")
            + "&nbsp;&nbsp;Ativa/desativa o sublinhado no título da música do primeiro slide.<br></p><p>"
            + Images.getImageHTMLTag("wrapText24.png")
            + "&nbsp;&nbsp;Com a opção <b>ativada</b>, o programa irá quebrar as linhas compridas automaticamente, evitando diminuir o tamanho original da fonte. Com a opção <b>desativada</b>, caso tenha alguma linha comprida que ultrapasse a lateral do slide, o programa irá diminuir o tamanho da fonte até que toda a linha fique dentro do slide.<br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Exemplo:</b><br><span style='font-size:90%'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Te louvarei<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Não importam as circunstâncias<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Adorarei, somente a Ti, Jesus</span><table cellspacing='5'>  <tr>    <td>Ativado</td>    <td>Desativado</td>  </tr>  <tr>    <td class='slide' style='font-size:100%'>Te louvarei<br>Não importam as circunstâncias<br>Adorarei, somente a Ti, Jesus</td>    <td class='slide' style='font-size:90%'>Te louvarei<br>Não importam as circunstâncias<br>Adorarei, somente a Ti, Jesus</td>  </tr></table></p><p>"
            + Images.getImageHTMLTag("style24.png")
            + "&nbsp;&nbsp;Ativa/desativa a <a href='http://saiba-mais-custom-formatting'>formatação personalizada</a> de parágrafo.<br></p></div>"
      );
      tp.addHyperlinkListener(e -> {
         if (e.getEventType() == EventType.ACTIVATED) {
            String var1x = e.getURL().toString();
            byte var2 = -1;
            switch (var1x.hashCode()) {
               case 1088131543:
                  if (var1x.equals("http://saiba-mais-custom-formatting")) {
                     var2 = 0;
                  }
               default:
                  switch (var2) {
                     case 0:
                        customParagraph(null);
                  }
            }
         }
      });
      JOptionPane.showMessageDialog(null, tp, I18N.WORD_HELP.val(), -1);
      Optional.ofNullable(callback).ifPresent(Runnable::run);
   }

   public static void showLastParagraphSequence(MusicMain parent, Music music, List<MusicController.Paragraph> paragraphs) {
      SwingUtilities.invokeLater(() -> {
         final JFrame frame = new JFrame(music.getTitle() + " (" + music.getArtist() + ")");
         frame.setDefaultCloseOperation(0);
         frame.setIconImages(Images.HOLYRICS_ICON);
         frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               if (Utils.confirm(I18N.WORD_CLOSE.val())) {
                  frame.setVisible(false);
                  frame.dispose();
               }
            }
         });
         InsertShortcut.add(frame, InsertShortcut.Shortcut.create(27, () -> {
            if (Utils.confirm(I18N.WORD_CLOSE.question())) {
               frame.setVisible(false);
               frame.dispose();
            }
         }));
         JList jListOrder = new JList<>(paragraphs.toArray(new MusicController.Paragraph[paragraphs.size()]));
         jListOrder.setCellRenderer(new ListCellRendererOrder());
         jListOrder.setFont(new Font("Franklin Gothic Medium", 0, 16));
         jListOrder.setSelectionMode(0);
         jListOrder.setDragEnabled(true);
         jListOrder.setDropMode(DropMode.INSERT);
         jListOrder.setFixedCellWidth(0);
         jListOrder.setVisibleRowCount(Math.min(jListOrder.getModel().getSize(), 16));
         frame.setLayout(new BorderLayout());
         frame.add(new WebScrollPane(jListOrder).setMargin(16, 24, 16, 24).setMinimumWidth(750).setMinimumHeight(400));
         frame.pack();
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);
      });
   }

   public static void helpVagalumeTutorialApiKey(Runnable callback) {
      JTextPane tp = new JTextPane();
      tp.setContentType("text/html;charset=UTF-8");
      tp.setEditable(false);
      tp.setFocusable(false);
      int[] imageCountHack = new int[]{1};
      Supplier<String> imageHtmlTag = () -> "<div style='padding: 8px 0px 0px 0px'>"
            + Images.getImageHTMLTagWithBorder(String.format("vagalume-tutorial-%02d.jpg", Integer.valueOf((int)(imageCountHack[0]++))), 2)
            + "</div><br>";
      tp.setText(
         "<html><style>  body {    padding: 8px 12px;    font-size: 14px;    text-align: justify;  }</style><div>  <p>    Acesse a página <a href='https://auth.vagalume.com.br/'>https://auth.vagalume.com.br/</a> e clique em <b>Cadastre-se</b>.  </p>"
            + imageHtmlTag.get()
            + "  <p>    Selecione o tipo de perfil <b>Pessoa</b>.  </p>"
            + imageHtmlTag.get()
            + "  <p>    Selecione a forma de cadastro (qualquer uma que quiser).  </p>"
            + imageHtmlTag.get()
            + "  <p>    Após realizar o cadastro, confirme o link enviado ao seu e-mail para evitar bloqueio posteriormente.<br>    Na página principal do site Vagalume, clique no ícone do seu perfil.  </p>"
            + imageHtmlTag.get()
            + "  <p>    Selecione a opção <b>Meus Dados</b>.  </p>"
            + imageHtmlTag.get()
            + "  <p>    Selecione a opção <b>API</b>.  </p>"
            + imageHtmlTag.get()
            + "  <p>    Clique no botão <b>Adicionar Credencial »</b>.  </p>"
            + imageHtmlTag.get()
            + "  <p>    Digite o <b>Nome da aplicação</> e a <b>Descrição da aplicação</b> (não precisa ser igual, pode colocar o que quiser).  </p>"
            + imageHtmlTag.get()
            + "  <p>    Atualize a página (F5) para visualizar a credencial criada.<br>    Copie o valor da sua credencial e cole no campo <b>Credencial Vagalume</b> para utilizar sua própria credencial.  </p>"
            + imageHtmlTag.get()
            + "  <p>PRONTO!</p></div>"
      );
      tp.addHyperlinkListener(e -> {
         try {
            if (e.getEventType() != EventType.ACTIVATED) {
               return;
            }

            String var1x = e.getURL().toString();
            byte var2x = -1;
            switch (var1x.hashCode()) {
               case -1634180799:
                  if (var1x.equals("https://auth.vagalume.com.br/")) {
                     var2x = 0;
                  }
               default:
                  switch (var2x) {
                     case 0:
                        Desktop.getDesktop().browse(e.getURL().toURI());
                  }
            }
         } catch (Exception var3x) {
         }
      });
      tp.setCaretPosition(0);
      dialogPopupShow(new WebScrollPane(tp), 750, 600);
      Optional.ofNullable(callback).ifPresent(Runnable::run);
   }

   public static void helpLive(Component parent, Runnable callback) {
      String htmlText = "O painel <b>AO VIVO</b> mostra o que está sendo apresentado atualmente na projeção principal.<br>(Plano de fundo e vídeo executado pelo VLC Media Player não aparecem no painel.)";
      showDefault(
         parent,
         "<p>O painel <b>AO VIVO</b> mostra o que está sendo apresentado atualmente na projeção principal.<br>(Plano de fundo e vídeo executado pelo VLC Media Player não aparecem no painel.)</p>",
         I18N.WORD_LIVE.val(),
         -1,
         callback
      );
   }

   public static void helpCommunicationPanel(Runnable callback) {
      String htmlText = "<p>  Se você utiliza um monitor de retorno exclusivo para quem está no 'palco', você pode utilizar o painel de comunicação para apresentar mensagens somente neste monitor.<br>  Nas configurações de exibição você pode definir um monitor como <b>visão do palco</b> (exceto o monitor principal do público), e as mensagens no painel de comunicação serão exibidas somente nos monitores definidos como <b>visão do palco</b>.</p><p>"
         + spanAlert(
            "Obs.: A função <b>visão do palco</b> funciona apenas com saídas de vídeos diferentes diretamente da placa de vídeo do computador. Mesmo que você utilize 2 ou mais monitores para projeção mas eles utilizem a mesma saída de vídeo (com um adaptador que duplica o cabo, por exemplo), não é possível utilizar o modo <b>visão do palco</b>."
         )
         + "</p>";
      showDefault(htmlText, I18N.MSG_COMMUNICATION_PANEL.val(), 400, callback);
   }

   public static void update2_0_0Feedback(Runnable callback) {
      String htmlText = "<p>  Olá! Obrigado por utilizar Holyrics!<br>  Finalmente a versão 2.0 saiu!</p><p>  O aplicativo para o celular será atualizado em breve para utilização dos novos recursos do programa.<br></p><p>  Peço, encarecidamente, que entre em contato o mais rápido possível caso encontre alguma falha no programa, principalmente se algo que <b>já funcionava antes</b> parou de funcionar <b>após a atualização</b>.<br></p><br>  Whatsapp: +55 15 98127 8370<br>  Facebook: <a href='https://www.facebook.com/Holyrics/'> https://www.facebook.com/Holyrics/</a><br>  E-mail: <a href='mailto:suporte@holyrics.com'> suporte@holyrics.com</a><br><br>Obrigado.<br>Paz!<br>";
      Map<String, Runnable> map = new HashMap<>();
      map.put("mailto:suporte@holyrics.com", () -> Utils.openURL("mailto:suporte@holyrics.com"));
      map.put("https://www.facebook.com/Holyrics/", () -> Utils.openURL("https://www.facebook.com/Holyrics/"));
      showDefault(
         "<p>  Olá! Obrigado por utilizar Holyrics!<br>  Finalmente a versão 2.0 saiu!</p><p>  O aplicativo para o celular será atualizado em breve para utilização dos novos recursos do programa.<br></p><p>  Peço, encarecidamente, que entre em contato o mais rápido possível caso encontre alguma falha no programa, principalmente se algo que <b>já funcionava antes</b> parou de funcionar <b>após a atualização</b>.<br></p><br>  Whatsapp: +55 15 98127 8370<br>  Facebook: <a href='https://www.facebook.com/Holyrics/'> https://www.facebook.com/Holyrics/</a><br>  E-mail: <a href='mailto:suporte@holyrics.com'> suporte@holyrics.com</a><br><br>Obrigado.<br>Paz!<br>",
         "Palavra do desenvolvedor",
         600,
         callback,
         map
      );
   }

   public static void staticAddPlayListMusicMain(MusicMain parent, List<Music> musics) {
      Event currentEvent = parent.getCurrentEvent();
      String htmlFormat = "<b>%s</b> <span style='color: " + Utils.getHexColor(StyleUI.FOREGROUND_LIGHT) + "'> (%s)</span>";
      String htmlList = musics.stream().map(m -> String.format(htmlFormat, m.getTitle(), m.getArtist())).collect(Collectors.joining("</p><p>", "<p>", "</p>"));
      String msg = String.format(
         "<html><body style='font-size:12px'>%s<br>%s<br>%s", htmlList, I18N.MSG_LIST_SONG_RECEIVED.val(), I18N.MSG_ADD_PLAY_LIST.question()
      );
      WebComboBox jComboBoxEvent = new WebComboBox() {
         private boolean layingOut = false;

         public void doLayout() {
            try {
               this.layingOut = true;
               super.doLayout();
            } finally {
               this.layingOut = false;
            }
         }

         public Dimension getSize() {
            Dimension dim = super.getSize();
            if (!this.layingOut) {
               dim.width = Math.max(dim.width, this.getPreferredSize().width);
            }

            return dim;
         }
      };
      Event[] event = new Event[]{currentEvent};
      ComboBoxModelEvent comboBoxModelEvent = new ComboBoxModelEvent();
      jComboBoxEvent.setFont(new Font("Courier New", 1, 16));
      jComboBoxEvent.setMaximumRowCount(16);
      jComboBoxEvent.setModel(comboBoxModelEvent);
      jComboBoxEvent.setFocusable(false);
      jComboBoxEvent.setRenderer(new ComboBoxRendererEvent());
      jComboBoxEvent.addItemListener(evt -> {
         if (evt.getStateChange() == 1) {
            event[0] = (Event)jComboBoxEvent.getSelectedItem();
         } else if (jComboBoxEvent.getSelectedItem() == null) {
            event[0] = null;
         }
      });
      jComboBoxEvent.setSelectedItem(currentEvent);
      WebSpinner jSpinnerEventDate = new WebSpinner();
      jSpinnerEventDate.setFont(new Font("Georgia", 0, 14));
      jSpinnerEventDate.setModel(new SpinnerDateModel(new Date(), null, null, 2));
      jSpinnerEventDate.setEditor(new DateEditor(jSpinnerEventDate, "MM/yyyy"));
      jSpinnerEventDate.setFocusable(false);
      jSpinnerEventDate.addChangeListener(evt -> {
         Calendar c = Calendar.getInstance();
         c.setTime((Date)jSpinnerEventDate.getValue());
         comboBoxModelEvent.set(c.get(2), c.get(1));
      });
      WebCheckBox chkbxPlaylist = new WebCheckBox(I18N.WORD_PLAY_LIST.val());
      WebCheckBox chkbxEvent = new WebCheckBox(I18N.WORD_WORSHIP_AND_EVENT.val());
      chkbxPlaylist.setSelected(EnumPlayList.selected == EnumPlayList.PLAY_LIST);
      chkbxEvent.setSelected(EnumPlayList.selected == EnumPlayList.WORSHIP_OR_EVENT);
      jSpinnerEventDate.setEnabled(chkbxEvent.isSelected());
      jComboBoxEvent.setEnabled(chkbxEvent.isSelected());
      Arrays.asList(chkbxPlaylist, chkbxEvent).forEach(chkbx -> {
         chkbx.setFocusable(false);
         chkbx.addItemListener(e -> {
            if (e.getStateChange() == 1) {
               jSpinnerEventDate.setEnabled(e.getSource() == chkbxEvent);
               jComboBoxEvent.setEnabled(e.getSource() == chkbxEvent);
            }
         });
      });
      WebPanel settings = new WebPanel(new BorderLayout(0, 8));
      settings.add(new WebButtonGroup(1, true, new JComponent[]{chkbxPlaylist, chkbxEvent}), "First");
      settings.add(new WebPanel(new FlowLayout(), jSpinnerEventDate) {
         {
            this.setOpaque(false);
         }
      }, "Center");
      settings.add(new WebPanel(new FlowLayout(), jComboBoxEvent) {
         {
            this.setOpaque(false);
         }
      }, "Last");
      WebPanel panel = new WebPanel(new BorderLayout(0, 8));
      panel.add(new WebLabel(msg), "First");
      panel.add(settings, "Center");

      while (true) {
         int op = JOptionPane.showConfirmDialog(null, panel, I18N.WORD_CONFIRM.val(), 0, -1);
         if (op != 0) {
            break;
         }

         if (chkbxPlaylist.isSelected() || Objects.nonNull(event[0]) && Objects.equals(event[0], currentEvent)) {
            EnumPlayList enumPlayList = chkbxPlaylist.isSelected() ? EnumPlayList.PLAY_LIST : EnumPlayList.WORSHIP_OR_EVENT;
            musics.forEach(music -> parent.addPlayList(music, enumPlayList));
            break;
         }

         if (Objects.nonNull(event[0])) {
            Schedule schedule = ScheduleController.get(event[0]);
            musics.forEach(schedule::addMusic);
            ScheduleController.save();
            break;
         }

         Utils.error(I18N.MSG_ERROR_EVENT_NOT_SELECTED.val());
      }
   }

   public static void editScreenResolution(final ScreenDeviceSettings sds, GraphicsDevice device, final Runnable callback) {
      final boolean[] callbackRun = new boolean[]{false};
      final JFrame screen = new JFrame();
      screen.setUndecorated(true);
      screen.setLayout(new BorderLayout());
      screen.setDefaultCloseOperation(0);
      screen.setAlwaysOnTop(true);
      Rectangle r = device.getDefaultConfiguration().getBounds();
      screen.setBounds(r);
      final JPanel position = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            G2Utils.setupGraphics(g2);
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            Rectangle rect = sds.getMargin().getRectWithMargin(this.getSize());
            g2.setColor(Color.GRAY);
            G2Utils.fillRect(rect, g2);
            int size = (int)Math.max(Math.min((float)rect.width / 80.0F, (float)rect.height / 80.0F), 2.0F);
            g2.setColor(new Color(128, 0, 0));
            g2.setStroke(new BasicStroke((float)size, 0, 2, 5.0F, new float[]{50.0F, 50.0F}, 0.0F));
            g2.drawRect((int)((float)rect.x + (float)size / 2.0F), (int)((float)rect.y + (float)size / 2.0F), rect.width - size, rect.height - size);
            g2.setColor(new Color(128, 0, 0, 92));
            g2.setStroke(new BasicStroke(Math.max((float)size * 0.75F, 2.0F), 2, 2, 5.0F, new float[]{50.0F, 50.0F}, 0.0F));
            g2.drawLine(rect.x, rect.y, (int)rect.getMaxX(), (int)rect.getMaxY());
            g2.drawLine((int)rect.getMaxX(), rect.y, rect.x, (int)rect.getMaxY());
            Font f = G2Utils.getBestFont(
               (int)((double)rect.width * 0.9), (int)((double)rect.height * 0.8), I18N.MSG_EDIT_SCREEN_POSITION.val(), new Font("Courier New", 1, 8), g2
            );
            g2.setFont(f);
            g2.setColor(Color.BLACK);
            G2Utils.drawString(rect, g2, 0.0, G2Utils.VerticalAlign.MIDDLE, I18N.MSG_EDIT_SCREEN_POSITION.val());
         }
      };
      screen.add(position, "Center");
      Component c = MarginSetDialog.createContent(new MarginSetDialog.MarginSetListener() {
         private final int[] minMaxAndStep = new int[]{0, 80, 1};

         @Override
         public void changed() {
            position.repaint();
         }

         @Override
         public int[] getMargins() {
            return sds.getMargin().margins;
         }

         @Override
         public int[] getMinMaxAndStepValue() {
            return this.minMaxAndStep;
         }
      }, false);
      WebLabel title = new WebLabel(I18N.MSG_VALUES_IN_PERCENTAGE.val(), 0, new Object[0]);
      title.setFontSize(16);
      screen.add(title, "First");
      final JDialog dialog = new JDialog(screen, true) {
         @Override
         public void dispose() {
            super.dispose();
            if (screen.isVisible()) {
               screen.setVisible(false);
               screen.dispose();
            }

            BooleanOptional.runIfTrue(!callbackRun[0], () -> {
               Optional.ofNullable(callback).ifPresent(Runnable::run);
               Settings.SET.saveTrue();
            });
            callbackRun[0] = true;
         }
      };
      dialog.setDefaultCloseOperation(0);
      dialog.setIconImages(Images.HOLYRICS_ICON);
      dialog.setTitle(I18N.MSG_EDIT_SCREEN_POSITION.val());
      dialog.setLayout(new BorderLayout());
      WebButton buttonOk = new WebButton("OK", evt -> dialog.dispose()).setMargin(4, 24, 4, 24).setFontSize(24);
      buttonOk.setFocusable(false);
      WebPanel components = new WebPanel(new BorderLayout(0, 8)).setMargin(16);
      components.add(title, "First");
      components.add(c, "Center");
      components.add(buttonOk, "Last");
      dialog.add(components, "Center");
      dialog.pack();
      dialog.setLocationRelativeTo(null);
      dialog.setResizable(false);
      dialog.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            dialog.dispose();
         }
      });
      InsertShortcut.addEscToDispose(dialog);
      screen.setVisible(true);
      dialog.setVisible(true);
   }

   public static void themeChooser(Theme _default, Consumer<Theme> callback) {
      SwingUtilities.invokeLater(() -> {
         BufferedImage bi = PresentationSlideUtils.createThumb(_default, EnumSlideSizePreview.MEDIUM.size);
         WebLabel slidePreview = new WebLabel(new ImageIcon(bi), 0);
         slidePreview.setFontSizeAndStyle(18, 1);
         slidePreview.setHorizontalTextPosition(0);
         slidePreview.setVerticalTextPosition(1);
         WebList list = new WebList(new ListModelTheme());
         list.setSelectedValue(_default, true);
         list.setCellRenderer(new ListCellRendererTheme());
         list.setSelectionMode(0);
         list.setFixedCellHeight(63);
         list.setFixedCellWidth(112);
         list.setLayoutOrientation(1);
         list.setVisibleRowCount(Math.min((int)Math.ceil(Math.sqrt((double)list.getModelSize())), 7));
         list.setFocusable(false);
         list.getSelectionModel().addListSelectionListener(e -> {
            Object obj = list.getSelectedValue();
            if (obj instanceof Theme) {
               BufferedImage bi1 = PresentationSlideUtils.createThumb((Theme)obj, EnumSlideSizePreview.MEDIUM.size);
               slidePreview.setIcon(new ImageIcon(bi1));
            }
         });
         WebScrollPane scroll = new WebScrollPane(list, false, false);
         scroll.setHorizontalScrollBarPolicy(32);
         scroll.setMaximumWidth(450);
         scroll.setMaximumHeight(500);
         WebLafUtils.setupScrollBarLargeThumbWithoutButton(scroll.getWebHorizontalScrollBar());
         JDialog dialog = new JDialog((Frame)null, true);
         final Consumer<Boolean> consumerAction = flag -> {
            Object obj = list.getSelectedValue();
            if (flag && !(obj instanceof Theme)) {
               Utils.error(I18N.MSG_ERROR_FILE_NOT_SELECTED.val());
            } else {
               dialog.setVisible(false);
               dialog.dispose();
               if (flag) {
                  callback.accept((Theme)obj);
               }
            }
         };
         dialog.setDefaultCloseOperation(0);
         dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               consumerAction.accept(false);
            }
         });
         dialog.setTitle(I18N.WORD_THEME.val());
         dialog.setIconImages(Images.HOLYRICS_ICON);
         dialog.setLayout(new BorderLayout());
         WebButton ok = new WebButton("Ok", evt -> consumerAction.accept(true));
         WebButton cancel = new WebButton(I18N.WORD_CANCEL.val(), evt -> consumerAction.accept(false));
         Arrays.asList(ok, cancel).forEach(b -> {
            b.setShadeWidth(0);
            b.setSize(120, 40);
            b.setPreferredSize(100, 30);
            b.setFontName("Georgia");
            b.setFontSize(14);
         });
         WebPanel buttonPanel = new WebPanel(new FlowLayout(1, 32, 0), new Component[]{ok, cancel});
         buttonPanel.setOpaque(false);
         WebPanel p = new WebPanel(new BorderLayout(16, 16));
         p.setOpaque(false);
         p.setMargin(16);
         p.add(scroll, "Center");
         p.add(buttonPanel, "Last");
         p.add(slidePreview, "West");
         dialog.add(p);
         dialog.pack();
         dialog.setLocationRelativeTo(null);
         InsertShortcut.add(dialog, InsertShortcut.Shortcut.create(27, () -> consumerAction.accept(false)));
         list.onDoubleClick(e -> {
            if (SwingUtilities.isLeftMouseButton(e)) {
               consumerAction.accept(true);
            }
         });
         dialog.setVisible(true);
      });
   }

   public static void musicChooser(Consumer<Music> callback) {
      SwingUtilities.invokeLater(() -> {
         JDialog dialog = new JDialog((Frame)null, true);
         dialog.setDefaultCloseOperation(2);
         dialog.setTitle(I18N.MSG_SELECT_A_LYRICS.val());
         dialog.setIconImages(Images.HOLYRICS_ICON);
         dialog.setLayout(new BorderLayout());
         WebPanel p = new WebPanel(new BorderLayout());
         p.setOpaque(false);
         p.setMargin(16);
         if (musicChooser == null) {
            musicChooser = new PopupUtils.MusicSearchPanelChooser();
            musicChooser.setUndecorated(true);
            musicChooser.setSize(450, (int)((double)VideoUtils.getSizeDisplay().height * 0.7));
            musicChooser.setPreferredSize(musicChooser.getSize());
         } else {
            musicChooser.dialog.setVisible(false);
            musicChooser.dialog.dispose();
         }

         musicChooser.dialog = dialog;
         musicChooser.callback = callback;
         p.add(musicChooser, "Center");
         dialog.add(p);
         dialog.pack();
         dialog.setLocationRelativeTo(null);
         InsertShortcut.addEscToDispose(dialog);
         dialog.setVisible(true);
      });
   }

   public static void dialogOkCancel(Component component, String title, Consumer<Boolean> callback) {
      SwingUtilities.invokeLater(() -> {
         JDialog dialog = new JDialog((Frame)null, true);
         final Consumer<Boolean> consumerAction = flag -> {
            dialog.setVisible(false);
            dialog.dispose();
            callback.accept(flag);
         };
         dialog.setDefaultCloseOperation(0);
         dialog.setAlwaysOnTop(true);
         dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               consumerAction.accept(false);
            }
         });
         dialog.setTitle(title);
         dialog.setIconImages(Images.HOLYRICS_ICON);
         dialog.setLayout(new BorderLayout());
         WebButton ok = new WebButton("Ok", evt -> consumerAction.accept(true));
         WebButton cancel = new WebButton(I18N.WORD_CANCEL.val(), evt -> consumerAction.accept(false));
         Arrays.asList(ok, cancel).forEach(b -> {
            b.setShadeWidth(0);
            b.setSize(120, 40);
            b.setPreferredSize(100, 30);
            b.setFontName("Georgia");
            b.setFontSize(14);
         });
         WebPanel buttonPanel = new WebPanel(new FlowLayout(1, 32, 0), new Component[]{ok, cancel});
         buttonPanel.setOpaque(false);
         WebPanel panel = new WebPanel(new BorderLayout(0, 16));
         panel.setOpaque(false);
         panel.setMargin(16);
         panel.add(component, "Center");
         panel.add(buttonPanel, "Last");
         dialog.add(panel);
         dialog.pack();
         dialog.setLocationRelativeTo(null);
         InsertShortcut.add(dialog, InsertShortcut.Shortcut.create(27, () -> consumerAction.accept(false)));
         dialog.setVisible(true);
      });
   }

   public static void helpCaptureDialog(Runnable callback) {
      String HELP_MSG = "<b>Ajuda</b><br>Coloque o áudio para tocar e clique no slide que você quer que esteja sendo apresentado enquanto a música está tocando.<br>Você pode pausar e voltar um trecho da música para recomeçar de onde parou.<br>Você também pode excluir um item da lista de capturados clicando com o botão direito do mouse.<br><br><b>Obs.:<br></b>O slide que será apresentado antes do primeiro slide que você clicar é o que está definido na janela de edição:&nbsp;&nbsp;&nbsp;&nbsp;Começar com: [<b>"
         + I18N.MSG_TITLE_AND_ARTIST.val()
         + "</b> ou <b>"
         + I18N.WORD_BLANK.val()
         + "</b>]<br><br><b>Dica:<br></b>Não espere iniciar o trecho da música para clicar no slide específico, procure clicar no slide alguns milésimos de segundo antes, evitando atrasos na transição entre os slides.";
      showDefault(null, HELP_MSG, I18N.MSG_CAPTURE_METHOD.val(), 600, callback);
   }

   public static void aboutAutomaticShow(Runnable callback) {
      String HELP_MSG = "Com <b>Apresentação Automática</b> você pode programar a duração de cada slide integrado a um áudio MP3 (um playback, por exemplo).<br>Após criada, você pode escolher qualquer tema que quiser e iniciar a apresentação.<br>O áudio será executado pelo Media Player e os slides serão exibidos automaticamente enquanto a música é tocada.";
      showDefault(
         null,
         "Com <b>Apresentação Automática</b> você pode programar a duração de cada slide integrado a um áudio MP3 (um playback, por exemplo).<br>Após criada, você pode escolher qualquer tema que quiser e iniciar a apresentação.<br>O áudio será executado pelo Media Player e os slides serão exibidos automaticamente enquanto a música é tocada.",
         I18N.MSG_CAPTURE_METHOD.val(),
         400,
         callback
      );
   }

   public static void helpAutomaticShow(Runnable callback) {
      String HELP_MSG = "Com <b>Apresentação Automática</b> você pode programar a duração de cada slide integrado a um áudio MP3 (um playback, por exemplo).<br>Após criada, você pode escolher qualquer tema que quiser e iniciar a apresentação.<br>O áudio será executado pelo Media Player e os slides serão exibidos automaticamente enquanto a música é tocada.<br><br><div style='padding-left:16px'>  <p>    <b>Defina o slide inicial</b> com a opção <i>Começar com</i> e a <i>duração</i> (em segundos) do slide inicial.  </p>  <p>    <b>Organize os slides</b> clicando com o botão direito para adicionar novos slides e arraste os slides para ordená-los.  </p>  <p>    <b>Defina a duração de cada slide</b> (em segundos) no campo <i>Duração</i>. Você também pode alterar o tempo exato em que o slide inicia clicando no texto que apresenta os minutos, segundos e milésimos.  </p>  <p>    <b>►</b> toca a música a partir do slide correspondente até o final da música.  </p>  <p>    <b>|►|</b> toca somente o trecho do slide correspondente.  </p></div><br><br>Você pode utilizar o <i>Método de captura</i> para facilitar a criação da ordem dos slides e duração de cada um.<br><br><b>Obs.:</b><br>Ao salvar a <i>Apresentação Automática</i>, o arquivo gerado <b>[nome do arquivo].ap</b> já irá incluir o áudio MP3 selecionado. Você não precisa se preocupar em copiar o áudio junto com o arquivo <b>(*.ap)</b>.";
      showDefault(
         null,
         "Com <b>Apresentação Automática</b> você pode programar a duração de cada slide integrado a um áudio MP3 (um playback, por exemplo).<br>Após criada, você pode escolher qualquer tema que quiser e iniciar a apresentação.<br>O áudio será executado pelo Media Player e os slides serão exibidos automaticamente enquanto a música é tocada.<br><br><div style='padding-left:16px'>  <p>    <b>Defina o slide inicial</b> com a opção <i>Começar com</i> e a <i>duração</i> (em segundos) do slide inicial.  </p>  <p>    <b>Organize os slides</b> clicando com o botão direito para adicionar novos slides e arraste os slides para ordená-los.  </p>  <p>    <b>Defina a duração de cada slide</b> (em segundos) no campo <i>Duração</i>. Você também pode alterar o tempo exato em que o slide inicia clicando no texto que apresenta os minutos, segundos e milésimos.  </p>  <p>    <b>►</b> toca a música a partir do slide correspondente até o final da música.  </p>  <p>    <b>|►|</b> toca somente o trecho do slide correspondente.  </p></div><br><br>Você pode utilizar o <i>Método de captura</i> para facilitar a criação da ordem dos slides e duração de cada um.<br><br><b>Obs.:</b><br>Ao salvar a <i>Apresentação Automática</i>, o arquivo gerado <b>[nome do arquivo].ap</b> já irá incluir o áudio MP3 selecionado. Você não precisa se preocupar em copiar o áudio junto com o arquivo <b>(*.ap)</b>.",
         I18N.MSG_CAPTURE_METHOD.val(),
         600,
         callback
      );
   }

   public static void aboutEditTextRealTime(Runnable callback) {
      String HELP_MSG = "Você pode editar o texto de determinados slides enquanto estão sendo apresentados.<br> - Clicando com o botão direito no slide que deseja editar (lista de slides no rodapé da janela principal);<br> - ou pressionando <b>SHIFT + F12</b> para editar o slide sendo exibido no momento.";
      showDefault(
         null,
         "Você pode editar o texto de determinados slides enquanto estão sendo apresentados.<br> - Clicando com o botão direito no slide que deseja editar (lista de slides no rodapé da janela principal);<br> - ou pressionando <b>SHIFT + F12</b> para editar o slide sendo exibido no momento.",
         I18N.WORD_HELP.val(),
         400,
         callback
      );
   }

   public static void helpChat(Runnable callback) {
      String HELP_MSG = "O chat é um meio de comunicação entre o Holyrics no PC e o aplicativo Holyrics para celular.<br>Qualquer celular conectado na rede poderá acessar o chat pelo aplicativo e enviar mensagens ao PC.<br>";
      showDefault(
         null,
         "O chat é um meio de comunicação entre o Holyrics no PC e o aplicativo Holyrics para celular.<br>Qualquer celular conectado na rede poderá acessar o chat pelo aplicativo e enviar mensagens ao PC.<br>",
         "Chat (App)",
         400,
         callback
      );
   }

   public static void helpHItemNextPresentation(Runnable callback) {
      String helpMsg = "A apresentação selecionada será executada automaticamente.<br>Para escolher um item ele precisa estar na lista de reprodução ou na barra de favoritos.";
      showDefault(
         null,
         "A apresentação selecionada será executada automaticamente.<br>Para escolher um item ele precisa estar na lista de reprodução ou na barra de favoritos.",
         "Próxima apresentação",
         400,
         callback
      );
   }

   public static void helpReadyMessages(Runnable callback) {
      String helpMsg = "Clique na mensagem para selecioná-la.<br>Arraste uma mensagem para reordená-la na lista.<br>Digite a tecla destacada no início da mensagem para selecionar a mensagem correspondente. <b>(primeira imagem)</b><br>Clique com o botão direito do mouse na mensagem para mais opções. <b>(segunda imagem)</b<br><br>"
         + Images.getImageHTMLTagBase64WithBorder("exampleReadyMessagesShortcut.png", 2)
         + "&nbsp;&nbsp;&nbsp;&nbsp;"
         + Images.getImageHTMLTagBase64WithBorder("exampleReadyMessagesRightButton.png", 2);
      showDefault(null, helpMsg, I18N.MSG_READY_MESSAGES.val(), 400, callback);
   }

   private static class MusicSearchPanelChooser extends MusicSearchPanel {
      private JDialog dialog;
      private Consumer<Music> callback;

      public MusicSearchPanelChooser() {
         super(null, false, false);
      }

      @Override
      public void musicChanged(Music music) {
      }

      @Override
      public void doubleClick(Music music) {
         JDialog d = this.dialog;
         if (d != null) {
            d.setVisible(false);
            d.dispose();
         }

         Consumer<Music> cb = this.callback;
         if (cb != null) {
            cb.accept(music);
         }
      }

      @Override
      public void rightButtonClicked(MouseEvent evt, JList<Music> jlist) {
      }

      @Override
      public void newMusic() {
      }

      @Override
      public boolean keyLeftEvent() {
         return false;
      }

      @Override
      public boolean keyRightEvent() {
         return false;
      }

      @Override
      public Window getOwner() {
         return this.dialog;
      }

      @Override
      public void openEditProfile() {
      }
   }
}
