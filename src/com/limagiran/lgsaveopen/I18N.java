package com.limagiran.lgsaveopen;

import java.util.Locale;

public class I18N {
   public static final String WORD_SAVE;
   public static final String WORD_OPEN;
   public static final String WORD_DONE;
   public static final String MSG_ERROR_TRY_AGAIN;
   public static final String MSG_ERROR_INVALID_FILE;

   static {
      if (Locale.getDefault().toString().equalsIgnoreCase("pt_BR")) {
         WORD_SAVE = "Salvar...";
         WORD_OPEN = "Abrir...";
         WORD_DONE = "Feito!";
         MSG_ERROR_TRY_AGAIN = "Erro! Tente novamente.";
         MSG_ERROR_INVALID_FILE = "Erro! Arquivo inválido.";
      } else {
         WORD_SAVE = "Save...";
         WORD_OPEN = "Open...";
         WORD_DONE = "Done!";
         MSG_ERROR_TRY_AGAIN = "Error! Try again.";
         MSG_ERROR_INVALID_FILE = "Error! Invalid file.";
      }
   }
}
