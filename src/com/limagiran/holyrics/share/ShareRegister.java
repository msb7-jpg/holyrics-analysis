package com.limagiran.holyrics.share;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Account;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ShareRegister {
   public static void main(Window owner, Account account, Runnable callback) {
      if (System.currentTimeMillis() - Settings.EnumSettings.DATETIME_LAST_REGISTER.getLong() < 3600000L) {
         Utils.error(owner, I18N.MSG_WAIT_TO_REGISTER.val());
      } else {
         JPanel general = new JPanel(new BorderLayout());
         JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
         JTextField username = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_USERNAME.val());
         JTextField accessKey = SwingUtils.createJTextFieldPlaceHolder(I18N.MSG_PH_ACCESS_KEY.val());
         JTextField security = SwingUtils.createJTextFieldPlaceHolder(I18N.MSG_PH_SECURITY_UPLOAD.val());
         security.setToolTipText(I18N.MSG_TK_SECURITY_UPLOAD.val());
         accessKey.setToolTipText(I18N.MSG_TK_ACCESS_KEY.val());
         accessKey.setColumns(20);
         Arrays.asList(username, accessKey, security).forEach(textField -> {
            textField.setDocument(SwingUtils.createDocOnlyWordNumberAndSpace(20));
            textField.setHorizontalAlignment(0);
            panel.add(textField);
         });
         JLabel header = new JLabel(I18N.MSG_REGISTER_HEADER.val());
         header.setFont(new Font("Georgia", 0, 14));
         general.add(header, "First");
         general.add(panel, "Center");
         show(owner, panel, username, accessKey, security, account, callback);
      }
   }

   private static void show(
      Window owner,
      final JPanel panel,
      final JTextField username,
      final JTextField accessKey,
      final JTextField security,
      final Account account,
      final Runnable callback
   ) {
      SwingUtils.requestFocusOnVisible(username);
      if (JOptionPane.showConfirmDialog(owner, panel, I18N.WORD_REGISTER.val(), 2, -1) == 0) {
         PopUpWorker.exe(new MySwingWorker<String, Void>(owner) {
            protected String doInBackground() throws Exception {
               try {
                  Account create = new Account(username.getText(), accessKey.getText(), security.getText());
                  create.validate();
                  SharePHP.createLogin(this.owner, create);
                  BooleanOptional.runIfTrue(Settings.EnumSettings.SAVE_USERNAME.is(), () -> Settings.SET.setAccount(account));
                  account.set(create);
                  Settings.EnumSettings.DATETIME_LAST_REGISTER.set(System.currentTimeMillis());
                  return "t" + I18N.MSG_LOGIN_CREATE_SUCCESS.val();
               } catch (Exception var2) {
                  return "f" + var2.getMessage();
               }
            }

            @Override
            protected void done() {
               super.done();
               this.showResult();
               if (!this.isOk()) {
                  SwingUtilities.invokeLater(() -> ShareRegister.show(this.owner, panel, username, accessKey, security, account, callback));
               } else {
                  callback.run();
               }
            }
         });
      }
   }
}
