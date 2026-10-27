package com.limagiran.javaupdate;

import com.limagiran.javaupdate.util.Download;
import com.limagiran.javaupdate.util.MySwingWorker;
import com.limagiran.javaupdate.util.PopUpWorker;
import com.limagiran.javaupdate.util.UpdateWorker;
import com.limagiran.javaupdate.util.Utils;
import java.awt.BorderLayout;
import java.awt.Window;
import java.io.File;
import java.util.Locale;
import java.util.Optional;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class Update {
   public static final String MSG_CHECKING;
   public static final String MSG_APP_UPDATED;
   public static final String MSG_CONFIRM_UPDATE;
   public static final String MSG_DO_NOT_ASK_AGAIN;
   public static final String MSG_ERROR_UPDATE;
   public static final String MSG_RESTART;
   public static final String WORD_UPDATING;
   public static final String WORD_RESTARTING;
   public static final String WORD_ERROR;
   public static final String WORD_CONFIRM;
   public static final String WORD_MESSAGE;
   public final IUpdate app;
   private String latestVersion;
   public final File file;

   protected Update(IUpdate app) {
      this.app = app;
      this.file = new File(Utils.getJarFile().getParent(), "temp.exe");
   }

   public void check(Window owner, boolean modal) {
      if (modal) {
         this.checkModal(owner);
      } else {
         this.check(owner);
      }
   }

   private String getLastVersion() {
      Download d = Download.download(this.app.getUrlVersion());
      d.waitFinish();
      return d.isOk() ? d.getText() : null;
   }

   public String getHash() {
      Download d = Download.download(this.app.getUrlHash());
      d.waitFinish();
      return d.isOk() ? d.getText() : null;
   }

   private boolean validateVersion() {
      this.latestVersion = this.getLastVersion();
      return this.latestVersion == null
         || this.latestVersion.isEmpty()
         || !this.latestVersion.matches("^\\d\\.\\d\\.\\d$")
         || this.latestVersion.equals(this.app.getVersion());
   }

   public String getLatestVersion() {
      return Optional.ofNullable(this.latestVersion).orElse("");
   }

   private void checkModal(Window owner) {
      PopUpWorker.exe(new MySwingWorker<String, Void>(owner, MSG_CHECKING) {
         protected String doInBackground() throws Exception {
            return Update.this.validateVersion() ? "already_updated" : "confirm_update";
         }

         @Override
         protected void done() {
            super.done();
            String var1 = this.getResult();
            switch (var1) {
               case "already_updated":
                  JOptionPane.showMessageDialog(this.owner, Update.MSG_APP_UPDATED);
                  break;
               case "confirm_update":
                  if (Utils.confirm(this.owner, Update.MSG_CONFIRM_UPDATE)) {
                     PopUpWorker.exe(new UpdateWorker(this.owner, Update.WORD_UPDATING, Update.this), Update.this.app.getAppName(), true);
                  }
            }
         }
      }, this.app.getAppName(), false);
   }

   private void check(final Window owner) {
      (new SwingWorker<String, Void>() {
         protected String doInBackground() throws Exception {
            return Update.this.validateVersion() ? "already_updated" : "confirm_update";
         }

         @Override
         protected void done() {
            super.done();

            try {
               String var1 = this.get();
               byte var2 = -1;
               switch (var1.hashCode()) {
                  case 1207056168:
                     if (var1.equals("confirm_update")) {
                        var2 = 0;
                     }
                  default:
                     switch (var2) {
                        case 0:
                           JPanel panel = new JPanel(new BorderLayout(0, 15));
                           JCheckBox chkbx = new JCheckBox(Update.MSG_DO_NOT_ASK_AGAIN);
                           panel.add(new JLabel(Update.MSG_CONFIRM_UPDATE), "Center");
                           panel.add(chkbx, "Last");
                           if (JOptionPane.showConfirmDialog(owner, panel, Update.WORD_CONFIRM, 0) == 0) {
                              PopUpWorker.exe(new UpdateWorker(owner, Update.WORD_UPDATING, Update.this), Update.this.app.getAppName(), true);
                           }

                           if (chkbx.isSelected()) {
                              Update.this.app.disableCheckUpdate();
                           }
                     }
               }
            } catch (Exception var5) {
            }
         }
      }).execute();
   }

   static {
      if (Locale.getDefault().toString().equalsIgnoreCase("pt_BR")) {
         WORD_ERROR = "Erro";
         WORD_CONFIRM = "Confirmar";
         WORD_MESSAGE = "Mensagem";
         MSG_CHECKING = "Verificando...";
         MSG_APP_UPDATED = "O Programa já está atualizado.";
         WORD_UPDATING = "Atualizando...";
         WORD_RESTARTING = "Reiniciando...";
         MSG_CONFIRM_UPDATE = "Atualização disponível. Deseja baixar agora?";
         MSG_DO_NOT_ASK_AGAIN = "Não perguntar novamente";
         MSG_RESTART = "Versão atualizada. O programa será reiniciado.";
         MSG_ERROR_UPDATE = "Erro ao realizar o download, tente novamente.";
      } else {
         WORD_ERROR = "Error";
         WORD_CONFIRM = "Confirm";
         WORD_MESSAGE = "Message";
         MSG_CHECKING = "Checking...";
         MSG_APP_UPDATED = "Application is already updated";
         WORD_UPDATING = "Updating...";
         WORD_RESTARTING = "Restarting...";
         MSG_CONFIRM_UPDATE = "Update available. Do you want to download now?";
         MSG_DO_NOT_ASK_AGAIN = "Do not ask again";
         MSG_RESTART = "Updated version. The program will be restarted.";
         MSG_ERROR_UPDATE = "Error performing the download, try again.";
      }
   }
}
