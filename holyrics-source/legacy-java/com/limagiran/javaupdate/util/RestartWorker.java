package com.limagiran.javaupdate.util;

import com.limagiran.javaupdate.Update;
import java.awt.Window;
import java.io.File;

public class RestartWorker extends MySwingWorker<String, Void> {
   private static final String BAT_FORMAT = "@echo on\n@echo _____________________________ \n@echo  O programa será atualizado.\n@echo  Não feche esta janela.\n@echo off\nsetlocal enableextensions enabledelayedexpansion\nset /a \"x = 0\"\n:LOOP\ntimeout /t 2 >nul /nobreak\nrename \"%1$s\" \"%2$s\" >nul 2>nul\nIF EXIST \"%1$s\" (\n    IF %%x%% leq 5 (\n        set /a \"x = x + 1\"\n        GOTO LOOP\n    ) ELSE (\n        del \"%3$s\" >nul 2>nul\n        cscript \"%6$s\"\n        exit\n    )\n)\nrename \"%3$s\" \"%4$s\" >nul 2>nul\ndel \"%5$s\" >nul 2>nul\nstart \"\" \"%1$s\" >nul 2>nul\nexit";
   private static final String VBS = "Set objArgs = WScript.Arguments\nmessageText = \"ERRO\" & vbCrLf & vbCrLf &\"Atualização: Holyrics\" & vbCrLf & \"Não foi possível atualizar o programa.\" & vbCrLf & \"Acesso negado.\"\nMsgBox messageText";
   private final Update update;

   public RestartWorker(Window owner, Update update) {
      super(owner, Update.WORD_RESTARTING);
      this.update = update;
   }

   protected String doInBackground() throws Exception {
      try {
         this.update.file.renameTo(this.update.app.getAbsoluteFileApp(this.update.getLatestVersion()));
         File vbs = File.createTempFile("popup", ".vbs");
         Utils.write(
            vbs,
            "Set objArgs = WScript.Arguments\nmessageText = \"ERRO\" & vbCrLf & vbCrLf &\"Atualização: Holyrics\" & vbCrLf & \"Não foi possível atualizar o programa.\" & vbCrLf & \"Acesso negado.\"\nMsgBox messageText",
            "Cp1252"
         );
         String arg1 = Utils.getJarFile().getAbsolutePath();
         String arg2 = "hlyToDel.tmp";
         String arg3 = this.update.app.getAbsoluteFileApp(this.update.getLatestVersion()).getAbsolutePath();
         String arg4 = Utils.getJarFile().getName();
         String arg5 = new File(Utils.getJarFile().getAbsoluteFile().getParent(), "hlyToDel.tmp").getAbsolutePath();
         String arg6 = vbs.getAbsolutePath();
         File f = File.createTempFile("temp", ".bat");
         Utils.write(
            f,
            String.format(
               "@echo on\n@echo _____________________________ \n@echo  O programa será atualizado.\n@echo  Não feche esta janela.\n@echo off\nsetlocal enableextensions enabledelayedexpansion\nset /a \"x = 0\"\n:LOOP\ntimeout /t 2 >nul /nobreak\nrename \"%1$s\" \"%2$s\" >nul 2>nul\nIF EXIST \"%1$s\" (\n    IF %%x%% leq 5 (\n        set /a \"x = x + 1\"\n        GOTO LOOP\n    ) ELSE (\n        del \"%3$s\" >nul 2>nul\n        cscript \"%6$s\"\n        exit\n    )\n)\nrename \"%3$s\" \"%4$s\" >nul 2>nul\ndel \"%5$s\" >nul 2>nul\nstart \"\" \"%1$s\" >nul 2>nul\nexit",
               arg1,
               arg2,
               arg3,
               arg4,
               arg5,
               arg6
            ),
            "cp850"
         );
         Utils.openFile(f);
         System.exit(0);
         return "true";
      } catch (Exception var9) {
         return "f" + Update.WORD_ERROR;
      }
   }

   @Override
   protected void done() {
      super.done();
      this.showResult();
   }
}
