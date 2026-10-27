package com.limagiran.holyrics.favorite;

import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.limagiran.holyrics.control.FavoriteController;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.hitem.item.HItemAnnouncement;
import com.limagiran.holyrics.hitem.item.HItemAudio;
import com.limagiran.holyrics.hitem.item.HItemImage;
import com.limagiran.holyrics.hitem.item.HItemMusic;
import com.limagiran.holyrics.hitem.item.HItemURL;
import com.limagiran.holyrics.hitem.item.HItemVerse;
import com.limagiran.holyrics.hitem.item.HItemVideo;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class FavoriteUtils {
   private static WebPanel PANEL;
   private static WebRadioButton RADIO_INIT;

   private static WebPanel getPanelNameAndCreate() {
      if (PANEL == null) {
         PANEL = new WebPanel(new GridLayout(2, 1, 0, 12));
         PANEL.setMargin(0, 0, 6, 0);
         PANEL.add(new WebLabel(I18N.WORD_NAME.val()).setFontSizeAndStyle(14, 1));
         RADIO_INIT = new WebRadioButton(I18N.WORD_START.val());
         WebRadioButton radioEnd = new WebRadioButton(I18N.WORD_END.val());
         Arrays.asList(RADIO_INIT, radioEnd).forEach(b -> {
            b.setMargin(0, 4, 0, 4);
            b.setFocusable(false);
         });
         RADIO_INIT.setSelected(Settings.EnumSettings2.ADD_TO_FAVORITE_IS_START.is());
         radioEnd.setSelected(!RADIO_INIT.isSelected());
         PANEL.add(new WebButtonGroup(0, true, new JComponent[]{new WebLabel(I18N.MSG_LIST_POSITION.val() + ": "), RADIO_INIT, radioEnd}));
      }

      return PANEL;
   }

   public static void getNameAndCreate(Component parent, Favoritable favoritable, Icon icon, String name) {
      FavoriteItem fi = FavoriteController.get(favoritable.getId());
      if (fi != null) {
         Utils.error(parent, I18N.MSG_ALREADY_ADDED.val() + "\n" + I18N.WORD_NAME.val() + ": " + fi.getName());
      } else {
         SwingUtils.runOnUIThread(
            () -> {
               Object input = JOptionPane.showInputDialog(parent, getPanelNameAndCreate(), I18N.MSG_ADD_TO_FAVORITE.val(), -1, icon, null, name);
               if (input != null) {
                  FavoriteController.add(
                     new FavoriteItem(favoritable, input.toString()), Settings.EnumSettings2.ADD_TO_FAVORITE_IS_START.set(RADIO_INIT.isSelected())
                  );
               }
            }
         );
      }
   }

   public static String getWSType(Favoritable favorite) {
      if (favorite instanceof HItemAudio) {
         return "fav-audio";
      } else if (favorite instanceof HItemVideo) {
         return "fav-video";
      } else if (favorite instanceof HItemImage) {
         return "fav-image";
      } else if (favorite instanceof HItemAnnouncement) {
         return "fav-announcement";
      } else if (favorite instanceof HItemURL) {
         return "fav-url";
      } else if (favorite instanceof HItemMusic) {
         return "fav-music";
      } else {
         return favorite instanceof HItemVerse ? "fav-verse" : "";
      }
   }

   public static String getWSThumb64(Favoritable favorite) {
      String var3;
      try {
         BufferedImage bi = favorite.getFavoriteThumb(ThumbMediaController.getThumbSize());
         return Img.encodeToString(bi, "png");
      } catch (Exception var7) {
         var3 = "";
      } finally {
         BufferedImage bi = null;
      }

      return var3;
   }
}
