package com.limagiran.holyrics.player;

import com.alee.extended.window.PopOverDirection;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.label.WebLabel;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Dir;
import java.awt.Component;
import java.io.File;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.swing.SwingUtilities;

public interface AudioPlayerSetUtils {
   AudioPlayerSettings getInstance();

   default boolean isSelected(File media) {
      try {
         return this.getInstance().getMedia().equals(media);
      } catch (Exception var3) {
         return false;
      }
   }

   static MediaPlayer createMP(File file) {
      try {
         Platform.setImplicitExit(false);
         return new MediaPlayer(new Media(file.toURI().toString()));
      } catch (Exception var2) {
         return null;
      }
   }

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
