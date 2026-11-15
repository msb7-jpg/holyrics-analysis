package com.limagiran.holyrics.swing;

import com.alee.laf.button.WebButton;
import com.alee.laf.menu.WebPopupMenu;
import com.limagiran.bible.AvailableVersions;
import com.limagiran.bible.Bible;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.Values;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu.Separator;

public abstract class BibleTabbedPaneTitle extends JPanel {
   private static final ImageIcon ICON_PRIMARY = new ImageIcon(Img.resize(Images.load("bible26.png"), 16, 16));
   private static final ImageIcon ICON_MAKE_PRIMARY = new ImageIcon(Images.load("checkBlack16.png"));
   private static final ImageIcon ICON_POPUP_CHANGE = new ImageIcon(Img.createTriangle(16, new Dimension(7, 4), Color.BLACK, true));
   private final JLabel title = new JLabel("", 0);
   private final WebButton buttonMakePrimary = new WebButton();
   private final WebButton buttonPopupChange = new WebButton();
   private final Bible bible;
   private final boolean primary;

   public abstract void makePrimary();

   public abstract void changeVersion(AvailableVersions var1);

   public BibleTabbedPaneTitle(Bible bible, boolean _default) {
      this.bible = bible;
      this.primary = _default;
      this.init();
   }

   private void init() {
      this.setLayout(new BoxLayout(this, 0));
      this.buttonMakePrimary.setIcon(this.primary ? ICON_PRIMARY : ICON_MAKE_PRIMARY);
      this.buttonPopupChange.setIcon(ICON_POPUP_CHANGE);
      this.setOpaque(false);
      this.title.setFont(new Font("Franklin Gothic Medium", 0, 12));
      String ab = this.bible.abbrev;
      this.title.setText(ab.substring(ab.indexOf("_") + 1).toUpperCase());
      this.buttonMakePrimary.setFocusable(false);
      if (!this.primary) {
         this.buttonMakePrimary.setToolTipText(I18N.MSG_MAKE_PRIMARY.val());
         this.buttonMakePrimary.addActionListener(e -> this.makePrimary());
      }

      this.buttonMakePrimary.setUndecorated(this.primary);
      this.buttonMakePrimary.setLeftRightSpacing(0);
      this.buttonMakePrimary.setShadeColor(Values.COLOR_TRANSPARENT);
      this.buttonPopupChange.setFocusable(false);
      this.buttonPopupChange.setUndecorated(true);
      this.buttonPopupChange.setLeftRightSpacing(0);
      this.buttonPopupChange.addActionListener(e -> this.popupChange());
      this.add(Box.createHorizontalStrut(8));
      this.add(this.buttonMakePrimary);
      this.add(Box.createHorizontalStrut(6));
      this.add(this.title);
      this.add(Box.createHorizontalStrut(20));
      this.add(this.buttonPopupChange);
   }

   private void popupChange() {
      WebPopupMenu popup = new WebPopupMenu();
      String lang = Locale.getDefault().getLanguage().toLowerCase();
      AvailableVersions.getVersions()
         .forEach(
            av -> {
               if (!av.getVersion().equalsIgnoreCase(this.bible.abbrev)) {
                  Component[] cs = popup.getComponents();
                  if (cs.length > 0
                     && cs[cs.length - 1] instanceof JMenuItem
                     && !av.getVersion().startsWith(lang)
                     && ((JMenuItem)cs[cs.length - 1]).getName().startsWith(lang)) {
                     popup.add(new Separator());
                  }

                  JMenuItem item = new JMenuItem(av.toString());
                  item.setName(av.getVersion());
                  item.addActionListener(e -> this.changeVersion(av));
                  popup.add(item);
               }
            }
         );
      popup.showBelow(this.buttonPopupChange);
   }
}
