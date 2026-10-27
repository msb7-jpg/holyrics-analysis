package com.limagiran.holyrics.hitem;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyData;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebLafUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class HItemTab extends WebPanel implements HItemListView.HItemListViewListener {
   public final EnumTabType tabType;
   public final HItemListView listView;
   public final JTextField jTextFieldSearch = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_TO_SEARCH.val() + " (Ctrl+F)");
   public final WebButton buttonPlay = this.createDefaultButton("play24.png");
   public final WebButton buttonAddFavorite = this.createDefaultButton("favorite24.png");
   public final WebButton buttonAddPlaylist = this.createDefaultButton("playlistAdd24.png");
   public final WebButton buttonOpenDir = this.createDefaultButton("open24.png");
   public final WebButton buttonRefresh = this.createDefaultButton("refresh24.png");
   public final WebButton buttonHelp = this.createDefaultButton(null);
   private final HItemTab.HItemTabListener listener;

   public HItemTab(EnumTabType tabType, HItemTab.HItemTabListener listener) {
      super(new BorderLayout());
      this.tabType = tabType;
      this.listener = listener;
      this.listView = new HItemListView(this.tabType, this);
      this.init();
   }

   private void init() {
      this.jTextFieldSearch.setFont(new Font("Arial", 0, 13));
      this.jTextFieldSearch.setHorizontalAlignment(0);
      this.jTextFieldSearch
         .getDocument()
         .addDocumentListener(new DocumentListenerAuto(250L, () -> this.listView.model.setFilter(this.jTextFieldSearch.getText())));
      this.jTextFieldSearch
         .addKeyListener(
            new KeyAdapter() {
               @Override
               public void keyReleased(KeyEvent e) {
                  switch (e.getKeyCode()) {
                     case 38:
                        HItemTab.this.listView.setSelectedIndex(Math.max(0, HItemTab.this.listView.getSelectedIndex() - 1));
                        break;
                     case 40:
                        HItemTab.this.listView
                           .setSelectedIndex(Math.min(HItemTab.this.listView.getModel().getSize() - 1, HItemTab.this.listView.getSelectedIndex() + 1));
                  }
               }
            }
         );
      this.buttonPlay.addActionListener(e -> this.listView.play(true));
      this.buttonPlay.setToolTipText(this.tabType.actionName);
      this.buttonAddPlaylist.addActionListener(e -> this.addPlaylist());
      this.buttonAddPlaylist.setToolTipText("<html>" + I18N.MSG_ADD_PLAY_LIST.val() + " <b>(" + I18N.WORD_WORSHIP_AND_EVENT.val() + ")</b>");
      SwingUtils.setupButtonDisableIcon(this.buttonAddPlaylist);
      this.buttonAddFavorite.addActionListener(e -> this.listView.addFavorite());
      this.buttonAddFavorite.setToolTipText(I18N.MSG_ADD_TO_FAVORITE.val());
      SwingUtils.setupButtonDisableIcon(this.buttonAddFavorite);
      this.buttonOpenDir.addActionListener(e -> this.openDir());
      this.buttonOpenDir.setToolTipText(I18N.MSG_OPEN_THE_FOLDER.val());
      this.buttonRefresh.addActionListener(e -> this.refresh());
      this.buttonRefresh.setToolTipText(I18N.MSG_UPDATE_LIST.val());
      this.buttonHelp.setText("?");
      this.buttonHelp.setFont(this.buttonHelp.getFont().deriveFont(1, 14.0F));
      this.buttonHelp.setForeground(Color.DARK_GRAY);
      this.buttonHelp.addActionListener(e -> PopupUtils.helpHItemTab(null));
      this.buttonHelp.setToolTipText(I18N.WORD_HELP.val());
      WebPanel panelButtons = new WebPanel(new GridBagLayout());
      panelButtons.add(this.buttonPlay, new GridBagConstraints());
      panelButtons.add(this.buttonAddPlaylist, new GridBagConstraints());
      panelButtons.add(this.buttonAddFavorite, new GridBagConstraints());
      panelButtons.add(this.buttonOpenDir, new GridBagConstraints());
      panelButtons.add(this.buttonRefresh, new GridBagConstraints());
      WebPanel headerFooter = new WebPanel(new BorderLayout(6, 6));
      headerFooter.add(panelButtons, "Center");
      headerFooter.add(this.buttonHelp, "East");
      WebPanel header = new WebPanel(new BorderLayout(4, 0));
      header.add(this.jTextFieldSearch, "Center");
      header.add(headerFooter, "Last");
      this.add(header, "First");
      this.add(this.listView.getOnScroll(), "Center");
      WebLafUtils.addToolTipButton(this, this.buttonPlay, Hotkey.F6);
      WebLafUtils.addToolTipButton(this, new WebButton("play-with-popup", e -> this.listView.play(false)), new HotkeyData(false, false, true, 117));
      WebLafUtils.addToolTipButton(this, this.buttonAddPlaylist);
      WebLafUtils.addToolTipButton(this, this.buttonAddFavorite);
      WebLafUtils.addToolTipButton(this, this.buttonOpenDir);
      WebLafUtils.addToolTipButton(this, this.buttonRefresh);
      WebLafUtils.addToolTipButton(this, this.buttonHelp);
   }

   public void ctrlF() {
      SwingUtilities.invokeLater(() -> {
         this.jTextFieldSearch.selectAll();
         this.jTextFieldSearch.requestFocus();
      });
   }

   private void openDir() {
      try {
         BooleanOptional.runIfTrue(!this.tabType.pathAsFile.exists(), this.tabType.pathAsFile::mkdirs);
         Desktop.getDesktop().open(this.tabType.pathAsFile);
      } catch (Exception var2) {
         Utils.error(I18N.MSG_ERROR_TRY_AGAIN.val());
      }
   }

   private WebButton createDefaultButton(String imgName) {
      ImageIcon ic = null;
      if (imgName != null) {
         ic = new ImageIcon(Images.load(imgName));
      }

      WebButton _return = new WebButton(ic);
      _return.setMargin(3, 6, 3, 6);
      _return.setFocusable(false);
      _return.setShadeColor(Values.COLOR_TRANSPARENT);
      _return.setUndecorated(true);
      _return.setMinimumSize(new Dimension());
      return _return;
   }

   @Override
   public void setFavoriteEnabled(boolean flag) {
      if (this.buttonAddFavorite.isEnabled() != flag) {
         this.buttonAddFavorite.setEnabled(flag);
      }
   }

   @Override
   public void setPlaylistMediaEnabled(boolean flag) {
      if (this.buttonAddPlaylist.isEnabled() != flag) {
         this.buttonAddPlaylist.setEnabled(flag);
      }
   }

   @Override
   public void addPlaylistMedia(List<IHItem> hItem) {
      this.listener.addPlaylistMedia(hItem);
   }

   public void addPlaylist() {
      this.listView.addPlaylist();
   }

   private void refresh() {
      this.buttonRefresh.setEnabled(false);
      Runnable callback = () -> SwingUtilities.invokeLater(() -> this.buttonRefresh.setEnabled(true));
      Utils.startThreadDaemon(() -> this.listView.model.checkPathModification(callback));
   }

   public interface HItemTabListener {
      void addPlaylistMedia(List<IHItem> var1);
   }
}
