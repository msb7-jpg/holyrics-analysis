package com.limagiran.holyrics.util;

import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.zip.Zip;
import java.awt.Window;
import java.io.File;
import java.util.Locale;
import java.util.Optional;

public class Backup {
   private static final String WORD_EXPORT_IN_PROGRESS;
   private static final String WORD_IMPORT_IN_PROGRESS;
   private static final String WORD_DONE;
   private static final String WORD_RESTARTING;
   private static final String WORD_DONE_RESTART;
   private static final String MSG_ERROR_TRY_AGAIN;
   private static final String MSG_ERROR_ACCESS_DENIED;
   private static final String MSG_CONFIRM_IMPORT_BACKUP;
   private static final File PATH_BACKUP = new File(Values.PATH_FILES).getAbsoluteFile();
   private static final String FILE_EXTENSION = ".hbac";
   private static final String FILE_DESCRIPTION = String.format("Backup File (*%s)", ".hbac");

   public static void exportBackup(Window owner) {
      Optional.ofNullable(Dir.windowsSave(WORD_EXPORT_IN_PROGRESS, FILE_DESCRIPTION, ".hbac"))
         .ifPresent(f -> PopUpWorker.exe(new MySwingWorker<String, Void>(owner, WORD_EXPORT_IN_PROGRESS) {
               protected String doInBackground() throws Exception {
                  try {
                     f.delete();
                     Zip.zip(Backup.PATH_BACKUP, f, new Zip.ZipProgress() {
                        @Override
                        public void update(int length, int total, String file) {
                           setProgress(Utils.range(Utils.getPercentageInt((double)length, (double)total), 0, 100));
                        }
                     });
                     return "t" + Backup.WORD_DONE;
                  } catch (Exception var2) {
                     f.delete();
                     f.deleteOnExit();
                     Log.send(var2);
                     return "f" + Backup.MSG_ERROR_TRY_AGAIN;
                  }
               }

               @Override
               protected void done() {
                  super.done();
                  this.showResult();
               }
            }));
   }

   public static void importBackup(Window owner) {
      File noDelete = Dir.canDelete(PATH_BACKUP);
      if (noDelete != null) {
         Utils.error(owner, "<html>Não é possível restaurar o backup neste momento.<br>O arquivo <b>" + noDelete.getAbsolutePath() + "</b> está em uso.");
      } else {
         final File f = Dir.windowsFile(WORD_IMPORT_IN_PROGRESS, FILE_DESCRIPTION, ".hbac");
         if (f != null && Utils.confirm(MSG_CONFIRM_IMPORT_BACKUP)) {
            PopUpWorker.exe(new MySwingWorker<String, Void>(owner, WORD_IMPORT_IN_PROGRESS) {
               protected String doInBackground() throws Exception {
                  File zipTemp = null;

                  String ex;
                  try {
                     zipTemp = File.createTempFile("holyrics-backup-import", ".zip");
                     zipTemp.deleteOnExit();
                     Dir.copy(f, zipTemp);
                     if (!Backup.PATH_BACKUP.exists() || Dir.deleteDir(Backup.PATH_BACKUP)) {
                        Zip.unzip(Backup.PATH_BACKUP.getParentFile(), zipTemp);
                        return "t" + Backup.WORD_DONE_RESTART;
                     }

                     ex = "f" + Backup.MSG_ERROR_ACCESS_DENIED;
                  } catch (Exception var7) {
                     Log.send(var7);
                     return "f" + Backup.MSG_ERROR_TRY_AGAIN;
                  } finally {
                     if (zipTemp != null) {
                        zipTemp.delete();
                     }
                  }

                  return ex;
               }

               @Override
               protected void done() {
                  super.done();
                  this.showResult();
                  if (this.isOk()) {
                     PopUpWorker.exe(new MySwingWorker<Void, Void>(this.owner, Backup.WORD_RESTARTING) {
                        protected Void doInBackground() throws Exception {
                           Backup.beforeRestart();
                           System.exit(1);
                           return null;
                        }
                     });
                  }
               }
            });
         }
      }
   }

   private static void beforeRestart() {
      Settings.SET.saveNow();
   }

   static {
      if (Locale.getDefault().toString().equalsIgnoreCase("pt_BR")) {
         WORD_EXPORT_IN_PROGRESS = "Exportação em andamento...";
         WORD_IMPORT_IN_PROGRESS = "Importação em andamento...";
         WORD_DONE = "Feito!";
         WORD_RESTARTING = "Reiniciando...";
         WORD_DONE_RESTART = "Feito! O programa será reiniciado.";
         MSG_ERROR_TRY_AGAIN = "Erro! Tente novamente.";
         MSG_ERROR_ACCESS_DENIED = "Erro! Acesso negado.";
         MSG_CONFIRM_IMPORT_BACKUP = "Os dados atuais serão apagados.\nVocê tem certeza que deseja importar o backup?\n(Esta operação não poderá ser desfeita)";
      } else {
         WORD_EXPORT_IN_PROGRESS = "Export in progress...";
         WORD_IMPORT_IN_PROGRESS = "Import in progress...";
         WORD_DONE = "Done!";
         WORD_RESTARTING = "Restarting...";
         WORD_DONE_RESTART = "Done! The program will be restarted.";
         MSG_ERROR_TRY_AGAIN = "Error! Try again.";
         MSG_ERROR_ACCESS_DENIED = "Error! Access denied.";
         MSG_CONFIRM_IMPORT_BACKUP = "The current data will be deleted.\nAre you sure want to import the backup?\n(This operation cannot be undone)";
      }
   }
}
