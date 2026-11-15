package com.limagiran.holyrics.view;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Download;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SupportAndSuggestionsDialog extends JDialog {
   private static final String URL = "http://www.holyrics.com/help/support.php";
   private static final int MAX_LENGTH_BODY = 4096;
   private JButton jButtonCancel;
   private JButton jButtonSend;
   private JLabel jLabelBodyInfo;
   private JLabel jLabelHeader;
   private JPanel jPanelBody;
   private JPanel jPanelButtons;
   private JPanel jPanelButtonsGrid;
   private JPanel jPanelFooter;
   private JPanel jPanelGeneral;
   private JPanel jPanelHeader;
   private JPanel jPanelInfo;
   private JScrollPane jScrollPaneMessage;
   private JTextArea jTextAreaMessage;
   private JTextField jTextFieldSubject;
   private JTextField jTextFieldWhatsAppOrEmail;

   private SupportAndSuggestionsDialog(Frame parent) {
      super(parent);
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonSend.addActionListener(e -> this.send());
      this.jButtonCancel.addActionListener(e -> this.dispose());
      this.jTextFieldSubject.setDocument(SwingUtils.createDocMaxSize(128));
      this.jTextFieldWhatsAppOrEmail.setDocument(SwingUtils.createDocMaxSize(128));
      this.jTextAreaMessage.setDocument(SwingUtils.createDocMaxSize(4096));
      this.jTextAreaMessage.getDocument().addDocumentListener(new DocumentListener() {
         @Override
         public void insertUpdate(DocumentEvent e) {
            SupportAndSuggestionsDialog.this.updateBodyInfo();
         }

         @Override
         public void removeUpdate(DocumentEvent e) {
            SupportAndSuggestionsDialog.this.updateBodyInfo();
         }

         @Override
         public void changedUpdate(DocumentEvent e) {
            SupportAndSuggestionsDialog.this.updateBodyInfo();
         }
      });
      this.updateBodyInfo();
      SwingUtils.setupCCPUAndRedo(this.jTextAreaMessage);
      InsertShortcut.addEscToDispose(this);
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelHeader = new JPanel();
      this.jLabelHeader = new JLabel();
      this.jPanelBody = new JPanel();
      this.jScrollPaneMessage = new JScrollPane();
      this.jTextAreaMessage = SwingUtils.createJTextAreaPlaceHolder(I18N.WORD_MESSAGE.val());
      this.jPanelInfo = new JPanel();
      this.jTextFieldSubject = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_SUBJECT.val());
      this.jTextFieldWhatsAppOrEmail = SwingUtils.createJTextFieldPlaceHolder(I18N.MSG_WHATSAPP_OR_EMAIL.val());
      this.jLabelBodyInfo = new JLabel();
      this.jPanelFooter = new JPanel();
      this.jPanelButtons = new JPanel();
      this.jPanelButtonsGrid = new JPanel();
      this.jButtonCancel = new JButton();
      this.jButtonSend = new JButton();
      this.setDefaultCloseOperation(2);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("msg.supportAndFeedBack"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setModal(true);
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      this.jPanelGeneral.setLayout(new BorderLayout());
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jLabelHeader.setFont(new Font("Franklin Gothic Medium", 0, 15));
      this.jLabelHeader.setHorizontalAlignment(0);
      this.jLabelHeader.setText("Entre em contato através do formulário abaixo ou pelo Whatsapp: +55 15 98127 8370");
      this.jPanelHeader.add(this.jLabelHeader, "First");
      this.jPanelGeneral.add(this.jPanelHeader, "First");
      this.jPanelBody.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelBody.setLayout(new BorderLayout());
      this.jTextAreaMessage.setColumns(60);
      this.jTextAreaMessage.setFont(new Font("Georgia", 0, 12));
      this.jTextAreaMessage.setLineWrap(true);
      this.jTextAreaMessage.setRows(15);
      this.jTextAreaMessage.setWrapStyleWord(true);
      this.jScrollPaneMessage.setViewportView(this.jTextAreaMessage);
      this.jPanelBody.add(this.jScrollPaneMessage, "Center");
      this.jPanelInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
      this.jPanelInfo.setLayout(new GridLayout(1, 0, 5, 0));
      this.jTextFieldSubject.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldSubject.setHorizontalAlignment(0);
      this.jPanelInfo.add(this.jTextFieldSubject);
      this.jTextFieldWhatsAppOrEmail.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldWhatsAppOrEmail.setHorizontalAlignment(0);
      this.jPanelInfo.add(this.jTextFieldWhatsAppOrEmail);
      this.jPanelBody.add(this.jPanelInfo, "First");
      this.jLabelBodyInfo.setFont(new Font("Georgia", 0, 12));
      this.jLabelBodyInfo.setHorizontalAlignment(4);
      this.jLabelBodyInfo.setText(" ");
      this.jPanelBody.add(this.jLabelBodyInfo, "Last");
      this.jPanelGeneral.add(this.jPanelBody, "Center");
      this.jPanelFooter.setLayout(new BorderLayout());
      this.jPanelButtons.setLayout(new FlowLayout(1, 25, 2));
      this.jPanelButtonsGrid.setLayout(new GridLayout(1, 0, 25, 0));
      this.jButtonCancel.setFont(new Font("Georgia", 0, 14));
      this.jButtonCancel.setText(bundle.getString("word.cancel"));
      this.jButtonCancel.setFocusable(false);
      this.jPanelButtonsGrid.add(this.jButtonCancel);
      this.jButtonSend.setFont(new Font("Georgia", 0, 14));
      this.jButtonSend.setText(bundle.getString("word.send"));
      this.jButtonSend.setFocusable(false);
      this.jPanelButtonsGrid.add(this.jButtonSend);
      this.jPanelButtons.add(this.jPanelButtonsGrid);
      this.jPanelFooter.add(this.jPanelButtons, "Last");
      this.jPanelGeneral.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(Frame parent) {
      SwingUtilities.invokeLater(() -> new SupportAndSuggestionsDialog(parent).setVisible(true));
   }

   public void send() {
      if (this.validateInput()) {
         PopUpWorker.exe(
            new MySwingWorker<String, Void>(this, I18N.WORD_SENDING___.val()) {
               protected String doInBackground() throws Exception {
                  Map<String, String> map = new HashMap<>();
                  map.put("subject", SupportAndSuggestionsDialog.this.jTextFieldSubject.getText());
                  map.put(
                     "message",
                     "[WhatsApp or Email]"
                        + SupportAndSuggestionsDialog.this.jTextFieldWhatsAppOrEmail.getText()
                        + "\n\n"
                        + SupportAndSuggestionsDialog.this.jTextAreaMessage.getText()
                  );
                  String[] _return = Download.sendPostGetString("http://www.holyrics.com/help/support.php", map).split(";");
                  return _return[0].equals("success") ? "t" + I18N.MSG_SEND_SUPPORT_AND_SUGGESTION_SUCCESS.val() : "f" + I18N.MSG_ERROR_TRY_AGAIN.val();
               }

               @Override
               protected void done() {
                  super.done();
                  this.showResult();
                  if (this.isOk()) {
                     SupportAndSuggestionsDialog.this.dispose();
                  }
               }
            }
         );
      }
   }

   public void updateBodyInfo() {
      this.jLabelBodyInfo.setText(this.jTextAreaMessage.getText().length() + "/" + 4096);
   }

   private boolean validateInput() {
      List<String> errors = new ArrayList<>();
      if (this.jTextFieldWhatsAppOrEmail.getText().trim().isEmpty()) {
         errors.add(I18N.MSG_ERROR_FIELD_X_EMPTY.format(I18N.MSG_WHATSAPP_OR_EMAIL.val()));
      }

      if (this.jTextAreaMessage.getText().trim().isEmpty()) {
         errors.add(I18N.MSG_ERROR_FIELD_X_EMPTY.format(I18N.WORD_MESSAGE.val()));
      }

      if (this.jTextFieldSubject.getText().trim().isEmpty()) {
         errors.add(I18N.MSG_ERROR_FIELD_X_EMPTY.format(I18N.WORD_SUBJECT.val()));
      }

      if (!errors.isEmpty()) {
         Utils.error(this, errors.stream().collect(Collectors.joining("\n")));
         return false;
      } else {
         return true;
      }
   }
}
