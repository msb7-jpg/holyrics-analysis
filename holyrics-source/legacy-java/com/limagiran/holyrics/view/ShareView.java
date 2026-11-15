package com.limagiran.holyrics.view;

import com.alee.laf.button.WebButton;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Account;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.share.ShareRegister;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.worker.DeleteMyAccountWorker;
import com.limagiran.holyrics.worker.UploadWorker;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

public class ShareView extends JDialog implements DocumentListenerAuto.DocumentListenerAutoListener {
   private final Runnable callback;
   private Filler filler1;
   private Filler filler10;
   private Filler filler11;
   private Filler filler2;
   private Filler filler3;
   private Filler filler4;
   private Filler filler5;
   private Filler filler6;
   private Filler filler7;
   private Filler filler8;
   private Filler filler9;
   private JButton jButtonDeleteMyAccount;
   private JButton jButtonRegister;
   private JButton jButtonShare;
   private JCheckBox jCheckBoxSave;
   private JLabel jLabelDesc;
   private JPanel jPanelGeneral;
   private JPanel jPanelUsernameAndKey;
   private JPasswordField jPasswordFieldSecurityCode;
   private JTextField jTextFieldAccessKey;
   private JTextField jTextFieldUsername;

   public ShareView(Window parent, Runnable callback) {
      super(parent);
      this.callback = callback;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonShare.addActionListener(e -> this.upload());
      this.jButtonRegister.addActionListener(e -> this.register());
      this.jButtonDeleteMyAccount.addActionListener(e -> this.deleteMyAccount());
      this.jTextFieldUsername.setDocument(SwingUtils.createDocOnlyWordNumberAndSpace(20));
      this.jTextFieldAccessKey.setDocument(SwingUtils.createDocOnlyWordNumberAndSpace(20));
      this.jPasswordFieldSecurityCode.setDocument(SwingUtils.createDocOnlyWordNumberAndSpace(20));
      Arrays.asList(this.jTextFieldUsername, this.jTextFieldAccessKey, this.jPasswordFieldSecurityCode)
         .forEach(c -> c.getDocument().addDocumentListener(new DocumentListenerAuto(250L, this)));
      this.jTextFieldUsername.setText(Settings.SET.getAccount().getUsername());
      this.jTextFieldAccessKey.setText(Settings.SET.getAccount().getAccessKey());
      this.jPasswordFieldSecurityCode.setText(Settings.SET.getAccount().getSecurity());
      this.jCheckBoxSave.setSelected(Settings.EnumSettings.SAVE_USERNAME.is());
      this.jTextFieldUsername.requestFocus();
      InsertShortcut.addEscToDispose(this);
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jLabelDesc = new JLabel();
      this.jPanelUsernameAndKey = new JPanel();
      this.filler1 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.jTextFieldUsername = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_USERNAME.val());
      this.filler2 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.filler3 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.jTextFieldAccessKey = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_ACCESS_KEY.val());
      this.filler4 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.filler5 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.jPasswordFieldSecurityCode = SwingUtils.createJPasswordFieldPlaceHolder(I18N.WORD_SECURITY_CODE.val());
      this.filler8 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.filler6 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.jCheckBoxSave = new JCheckBox();
      this.filler7 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.filler9 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.jButtonShare = new JButton();
      this.jButtonRegister = new WebButton();
      this.filler10 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.jButtonDeleteMyAccount = new WebButton();
      this.filler11 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
      this.setDefaultCloseOperation(2);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.share")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setModal(true);
      this.setResizable(false);
      this.getContentPane().setLayout(new AbsoluteLayout());
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      this.jPanelGeneral.setLayout(new BorderLayout(0, 10));
      this.jLabelDesc.setFont(new Font("Georgia", 0, 16));
      this.jLabelDesc.setHorizontalAlignment(0);
      this.jLabelDesc.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.shareDesc")));
      this.jPanelGeneral.add(this.jLabelDesc, "First");
      this.jPanelUsernameAndKey.setLayout(new GridLayout(6, 3, 0, 4));
      this.jPanelUsernameAndKey.add(this.filler1);
      this.jTextFieldUsername.setColumns(12);
      this.jTextFieldUsername.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldUsername.setHorizontalAlignment(0);
      this.jPanelUsernameAndKey.add(this.jTextFieldUsername);
      this.jPanelUsernameAndKey.add(this.filler2);
      this.jPanelUsernameAndKey.add(this.filler3);
      this.jTextFieldAccessKey.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldAccessKey.setHorizontalAlignment(0);
      this.jPanelUsernameAndKey.add(this.jTextFieldAccessKey);
      this.jPanelUsernameAndKey.add(this.filler4);
      this.jPanelUsernameAndKey.add(this.filler5);
      this.jPasswordFieldSecurityCode.setFont(new Font("Georgia", 0, 12));
      this.jPasswordFieldSecurityCode.setHorizontalAlignment(0);
      this.jPanelUsernameAndKey.add(this.jPasswordFieldSecurityCode);
      this.jPanelUsernameAndKey.add(this.filler8);
      this.jPanelUsernameAndKey.add(this.filler6);
      this.jCheckBoxSave.setFont(new Font("Georgia", 0, 11));
      this.jCheckBoxSave.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.save")));
      this.jCheckBoxSave.setFocusable(false);
      this.jCheckBoxSave.setHorizontalAlignment(0);
      this.jCheckBoxSave.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent evt) {
            ShareView.this.jCheckBoxSaveItemStateChanged(evt);
         }
      });
      this.jPanelUsernameAndKey.add(this.jCheckBoxSave);
      this.jPanelUsernameAndKey.add(this.filler7);
      this.jPanelUsernameAndKey.add(this.filler9);
      this.jButtonShare.setFont(new Font("Georgia", 0, 12));
      this.jButtonShare.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.share")));
      this.jButtonShare.setFocusPainted(false);
      this.jButtonShare.setFocusable(false);
      this.jPanelUsernameAndKey.add(this.jButtonShare);
      this.jButtonRegister.setFont(new Font("Georgia", 0, 12));
      this.jButtonRegister.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.registerLink")));
      this.jButtonRegister.setBorderPainted(false);
      this.jButtonRegister.setContentAreaFilled(false);
      this.jButtonRegister.setCursor(new Cursor(12));
      this.jButtonRegister.setFocusPainted(false);
      this.jButtonRegister.setFocusable(false);
      this.jPanelUsernameAndKey.add(this.jButtonRegister);
      this.jPanelUsernameAndKey.add(this.filler10);
      this.jButtonDeleteMyAccount.setFont(new Font("Georgia", 0, 12));
      this.jButtonDeleteMyAccount
         .setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.deleteMyAccountLink")));
      this.jButtonDeleteMyAccount.setBorderPainted(false);
      this.jButtonDeleteMyAccount.setContentAreaFilled(false);
      this.jButtonDeleteMyAccount.setCursor(new Cursor(12));
      this.jButtonDeleteMyAccount.setFocusPainted(false);
      this.jButtonDeleteMyAccount.setFocusable(false);
      this.jPanelUsernameAndKey.add(this.jButtonDeleteMyAccount);
      this.jPanelUsernameAndKey.add(this.filler11);
      this.jPanelGeneral.add(this.jPanelUsernameAndKey, "Center");
      this.getContentPane().add(this.jPanelGeneral, new AbsoluteConstraints(0, 0, -1, -1));
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jCheckBoxSaveItemStateChanged(ItemEvent evt) {
      Settings.EnumSettings.SAVE_USERNAME.set(evt.getStateChange() == 1);
      Settings.SET.setAccount(Settings.EnumSettings.SAVE_USERNAME.is() ? this.getAccount() : new Account());
   }

   public static void main(Window parent, Runnable callback) {
      SwingUtilities.invokeLater(() -> new ShareView(parent, callback).setVisible(true));
   }

   private Account getAccount() {
      return new Account(this.jTextFieldUsername.getText(), this.jTextFieldAccessKey.getText(), String.valueOf(this.jPasswordFieldSecurityCode.getPassword()));
   }

   private void register() {
      Account account = this.getAccount();
      ShareRegister.main(this, account, () -> {
         this.jTextFieldUsername.setText(account.getUsername());
         this.jTextFieldAccessKey.setText(account.getAccessKey());
         this.jPasswordFieldSecurityCode.setText(account.getSecurity());
      });
   }

   @Override
   public void dispose() {
      Optional.ofNullable(this.callback).ifPresent(c -> c.run());
      super.dispose();
   }

   private void upload() {
      Account account = this.getAccount();

      try {
         account.validate();
         PopUpWorker.exe(new UploadWorker(this, account));
      } catch (Exception var3) {
         Utils.error(this, var3.getMessage());
      }
   }

   private void deleteMyAccount() {
      Account account = this.getAccount();

      try {
         account.validate();
         PopUpWorker.exe(new DeleteMyAccountWorker(this, account));
      } catch (Exception var3) {
         Utils.error(this, var3.getMessage());
      }
   }

   @Override
   public void trigger() {
      Settings.SET.setAccount(Settings.EnumSettings.SAVE_USERNAME.is() ? this.getAccount() : new Account());
   }
}
