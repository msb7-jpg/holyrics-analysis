package com.limagiran.holyrics.vlc;

import com.alee.extended.window.PopOverDirection;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.label.WebLabel;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Dir;
import java.awt.Component;
import java.io.File;
import javax.swing.SwingUtilities;

public interface VLCSetUtils {
   VLCSettings getInstance();

   static void showNotificationError(Component c, File file) {
      SwingUtilities.invokeLater(() -> {
         WebPopOver popOver = new WebPopOver();
         popOver.setDefaultCloseOperation(2);
         popOver.setCloseOnFocusLoss(true);
         popOver.setMargin(10);
         popOver.add(new WebLabel("<html>" + (!file.exists() ? Dir.MSG_FILE_NOT_FOUND : I18N.MSG_ERROR_INVALID_FILE.val()) + " " + file.getAbsolutePath()));
         popOver.setCurrentDirection(PopOverDirection.down);
         popOver.show(c);
      });
   }
}
