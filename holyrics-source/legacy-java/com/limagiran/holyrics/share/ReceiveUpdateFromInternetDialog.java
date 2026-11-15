package com.limagiran.holyrics.share;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.worker.UpdateFromInternetWorker;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.util.Arrays;
import java.util.Optional;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ReceiveUpdateFromInternetDialog {
   public static void main(Window owner, String _username, String _accessKey) {
      SwingUtilities.invokeLater(() -> {
         String username = Optional.ofNullable(_username).orElse(Settings.SET.getAccount().getUsername());
         String accessKey = Optional.ofNullable(_accessKey).orElse(Settings.SET.getAccount().getAccessKey());
         JTextField tfUsername = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_USERNAME.val());
         JTextField tfAccessKey = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_ACCESS_KEY.val());
         Arrays.asList(tfUsername, tfAccessKey).forEach(tf -> {
            tf.setColumns(20);
            tf.setHorizontalAlignment(0);
            tf.setDocument(SwingUtils.createDocOnlyWordNumberAndSpace(20));
         });
         tfUsername.setText(username);
         tfAccessKey.setText(accessKey);
         JPanel pGrid = new JPanel(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.anchor = 13;
         gbc.gridwidth = 1;
         gbc.fill = 2;
         pGrid.add(tfUsername, gbc);
         gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = 1;
         gbc.anchor = 13;
         gbc.gridwidth = 1;
         gbc.fill = 2;
         pGrid.add(tfAccessKey, gbc);
         JLabel labelInfo = new JLabel(I18N.MSG_RECEIVE_UPDATE_FROM_INTERNET.val());
         labelInfo.setFont(new Font("Arial", 0, 14));
         JPanel panel = new JPanel(new BorderLayout(0, 16));
         panel.add(labelInfo, "First");
         panel.add(pGrid, "Center");
         SwingUtils.requestFocusOnVisible(tfUsername);
         int op = JOptionPane.showConfirmDialog(owner, panel, I18N.WORD_UPDATE.val(), 2, -1);
         if (op == 0) {
            PopUpWorker.exe(new UpdateFromInternetWorker(owner, tfUsername.getText(), tfAccessKey.getText()), true);
         }
      });
   }

   public static void main(Frame owner) {
      main(owner, null, null);
   }
}
