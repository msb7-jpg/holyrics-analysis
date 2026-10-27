package com.limagiran.bible;

import com.limagiran.bible.i18n.I18N;
import com.limagiran.bible.util.Download;
import com.limagiran.bible.util.InsertShortcut;
import com.limagiran.bible.util.MySwingWorker;
import com.limagiran.bible.util.PopUpWorker;
import com.limagiran.bible.util.Utils;
import com.limagiran.bible.util.Values;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class ReportError extends JDialog {
   private final Verse verse;
   private JButton jButtonCancel;
   private JButton jButtonSend;
   private JLabel jLabelCurrent;
   private JLabel jLabelDesc;
   private JLabel jLabelSuggest;
   private JPanel jPanelButtons;
   private JPanel jPanelCurrent;
   private JPanel jPanelSuggest;
   private JScrollPane jScrollPaneCurrent;
   private JScrollPane jScrollPaneSuggest;
   private JTextArea jTextAreaCurrent;
   private JTextArea jTextAreaSuggest;

   public ReportError(Frame parent, Verse verse) {
      super(parent);
      this.verse = verse;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonSend.addActionListener(e -> this.send());
      this.jButtonCancel.addActionListener(e -> this.dispose());
      this.jTextAreaCurrent.setCaretPosition(0);
      this.jTextAreaSuggest.setCaretPosition(0);
      this.jTextAreaSuggest.requestFocus();
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, () -> this.dispose()));
   }

   private void initComponents() {
      this.jLabelDesc = new JLabel();
      this.jPanelButtons = new JPanel();
      this.jButtonCancel = new JButton();
      this.jButtonSend = new JButton();
      this.jPanelCurrent = new JPanel();
      this.jLabelCurrent = new JLabel();
      this.jScrollPaneCurrent = new JScrollPane();
      this.jTextAreaCurrent = new JTextArea();
      this.jPanelSuggest = new JPanel();
      this.jLabelSuggest = new JLabel();
      this.jScrollPaneSuggest = new JScrollPane();
      this.jTextAreaSuggest = new JTextArea();
      this.setDefaultCloseOperation(2);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/bible/i18n/Bundle").getString("word.reportError")));
      this.setIconImages(Values.ICONS_REPORT_ERROR);
      this.setMinimumSize(new Dimension(650, 354));
      this.setModal(true);
      this.setResizable(false);
      this.getContentPane().setLayout(new AbsoluteLayout());
      this.jLabelDesc.setFont(new Font("Georgia", 0, 18));
      this.jLabelDesc.setHorizontalAlignment(0);
      this.jLabelDesc.setText(this.verse.getFullDesc() + " - " + this.verse.getVersion());
      this.getContentPane().add(this.jLabelDesc, new AbsoluteConstraints(13, 14, 620, -1));
      this.jPanelButtons.setLayout(new FlowLayout(1, 50, 10));
      this.jButtonCancel.setFont(new Font("Georgia", 0, 12));
      this.jButtonCancel.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/bible/i18n/Bundle").getString("word.cancel")));
      this.jButtonCancel.setMargin(new Insets(2, 8, 2, 8));
      this.jButtonCancel.setMaximumSize(new Dimension(110, 30));
      this.jButtonCancel.setMinimumSize(new Dimension(110, 30));
      this.jButtonCancel.setPreferredSize(new Dimension(110, 30));
      this.jPanelButtons.add(this.jButtonCancel);
      this.jButtonSend.setFont(new Font("Georgia", 0, 12));
      this.jButtonSend.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/bible/i18n/Bundle").getString("word.send")));
      this.jButtonSend.setMargin(new Insets(2, 8, 2, 8));
      this.jButtonSend.setMaximumSize(new Dimension(110, 30));
      this.jButtonSend.setMinimumSize(new Dimension(110, 30));
      this.jButtonSend.setPreferredSize(new Dimension(110, 30));
      this.jPanelButtons.add(this.jButtonSend);
      this.getContentPane().add(this.jPanelButtons, new AbsoluteConstraints(0, 305, 640, -1));
      this.jPanelCurrent.setLayout(new BorderLayout());
      this.jLabelCurrent.setFont(new Font("Georgia", 0, 14));
      this.jLabelCurrent.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/bible/i18n/Bundle").getString("msg.currentVerse")));
      this.jLabelCurrent.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
      this.jPanelCurrent.add(this.jLabelCurrent, "First");
      this.jTextAreaCurrent.setEditable(false);
      this.jTextAreaCurrent.setColumns(20);
      this.jTextAreaCurrent.setFont(new Font("Times New Roman", 0, 16));
      this.jTextAreaCurrent.setLineWrap(true);
      this.jTextAreaCurrent.setRows(3);
      this.jTextAreaCurrent.setText(this.verse.content);
      this.jTextAreaCurrent.setWrapStyleWord(true);
      this.jScrollPaneCurrent.setViewportView(this.jTextAreaCurrent);
      this.jPanelCurrent.add(this.jScrollPaneCurrent, "Center");
      this.getContentPane().add(this.jPanelCurrent, new AbsoluteConstraints(10, 50, 630, 110));
      this.jPanelSuggest.setLayout(new BorderLayout());
      this.jLabelSuggest.setFont(new Font("Georgia", 0, 14));
      this.jLabelSuggest.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/bible/i18n/Bundle").getString("msg.suggestedEdition")));
      this.jLabelSuggest.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
      this.jPanelSuggest.add(this.jLabelSuggest, "First");
      this.jTextAreaSuggest.setColumns(20);
      this.jTextAreaSuggest.setFont(new Font("Times New Roman", 0, 16));
      this.jTextAreaSuggest.setLineWrap(true);
      this.jTextAreaSuggest.setRows(3);
      this.jTextAreaSuggest.setText(this.verse.content);
      this.jTextAreaSuggest.setWrapStyleWord(true);
      this.jScrollPaneSuggest.setViewportView(this.jTextAreaSuggest);
      this.jPanelSuggest.add(this.jScrollPaneSuggest, "Center");
      this.getContentPane().add(this.jPanelSuggest, new AbsoluteConstraints(10, 190, 630, 110));
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(Frame parent, Verse verse) {
      SwingUtilities.invokeLater(() -> new ReportError(parent, verse).setVisible(true));
   }

   private void send() {
      final String content = this.jTextAreaSuggest.getText();

      try {
         this.validate(content);
         PopUpWorker.exe(new MySwingWorker<String, Void>(this, I18N.WORD_WAITING___.val()) {
            protected String doInBackground() throws Exception {
               String url = "http://www.holyrics.com/bible/reportError.php";
               Map<String, Object> map = new HashMap<>();
               map.put("id", ReportError.this.verse.getId());
               map.put("version", ReportError.this.verse.getVersionAbbrev());
               map.put("content", content);
               return new String(Download.sendPost(url, map), Charset.forName("UTF-8"));
            }

            @Override
            protected void done() {
               super.done();
               if (this.getResult().equals("true")) {
                  JOptionPane.showMessageDialog(ReportError.this, I18N.MSG_REPORT_SUCCESS.val());
                  ReportError.super.dispose();
               } else {
                  Utils.error(ReportError.this, I18N.MSG_ERROR_TRY_AGAIN.val());
               }
            }
         });
      } catch (IllegalArgumentException var3) {
         Utils.error(this, var3.getMessage());
      }
   }

   private void validate(String value) throws IllegalArgumentException {
      if (value.replace(" ", "").isEmpty()) {
         throw new IllegalArgumentException(I18N.MSG_CONTENT_IS_EMPTY.val());
      } else if (value.length() > 1000) {
         throw new IllegalArgumentException(I18N.MSG_CONTENT_VERY_LARGE_TEXT.val());
      }
   }
}
