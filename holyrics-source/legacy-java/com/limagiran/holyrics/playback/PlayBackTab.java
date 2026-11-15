package com.limagiran.holyrics.playback;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebLafUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PlayBackTab extends WebPanel implements PlayBackListView.PlayBackListViewListener {
   public Window owner;
   public final PlayBackListView listView;
   public final JTextField jTextFieldSearch = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_TO_SEARCH.val());
   public final WebButton buttonCreate = this.createDefaultButton("plus24.png");
   public final WebButton buttonEdit = this.createDefaultButton("edit24.png");
   public final WebButton buttonPlay = this.createDefaultButton("play24.png");
   public final WebButton buttonAddFavorite = this.createDefaultButton("favorite24.png");
   public final WebButton buttonAddPlaylist = this.createDefaultButton("playlistAdd24.png");
   public final WebButton buttonOpenDir = this.createDefaultButton("open24.png");
   public final WebButton buttonRefresh = this.createDefaultButton("refresh24.png");
   public final WebButton buttonCreateVideo = this.createDefaultButton("movie24.png");
   private final PlayBackTab.PlayBackTabListener listener;

   public PlayBackTab(PlayBackTab.PlayBackTabListener listener) {
      super(new BorderLayout());
      this.listener = listener;
      this.listView = new PlayBackListView(this);
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
                        PlayBackTab.this.listView.setSelectedIndex(Math.max(0, PlayBackTab.this.listView.getSelectedIndex() - 1));
                        break;
                     case 40:
                        PlayBackTab.this.listView
                           .setSelectedIndex(Math.min(PlayBackTab.this.listView.getModel().getSize() - 1, PlayBackTab.this.listView.getSelectedIndex() + 1));
                  }
               }
            }
         );
      this.buttonCreate.addActionListener(e -> this.create());
      this.buttonCreate.setToolTipText(I18N.WORD_CREATE.val());
      this.buttonEdit.addActionListener(e -> this.edit());
      this.buttonEdit.setToolTipText(I18N.WORD_EDIT.val());
      this.buttonPlay.addActionListener(e -> this.listView.play());
      this.buttonPlay.setToolTipText(I18N.WORD_SHOW.val());
      this.buttonAddPlaylist.addActionListener(e -> this.addPlaylist());
      this.buttonAddPlaylist.setToolTipText("<html>" + I18N.MSG_ADD_PLAY_LIST.val() + " <b>(" + I18N.WORD_WORSHIP_AND_EVENT.val() + ")</b>");
      SwingUtils.setupButtonDisableIcon(this.buttonAddPlaylist);
      this.buttonAddFavorite.addActionListener(e -> this.listView.addFavorite());
      this.buttonAddFavorite.setToolTipText(I18N.MSG_ADD_TO_FAVORITE.val());
      SwingUtils.setupButtonDisableIcon(this.buttonAddFavorite);
      this.buttonOpenDir.addActionListener(e -> PlayBackUtils.openDir());
      this.buttonOpenDir.setToolTipText(I18N.MSG_OPEN_THE_FOLDER.val());
      this.buttonRefresh.addActionListener(e -> this.refresh());
      this.buttonRefresh.setToolTipText(I18N.MSG_UPDATE_LIST.val());
      this.buttonCreateVideo.addActionListener(e -> this.createVideo());
      Insets i = this.buttonCreateVideo.getMargin();
      this.buttonCreateVideo.setMargin(i.top, 8, i.bottom, i.right);
      this.buttonCreateVideo.setToolTipText(I18N.MSG_PLAYBACK_EXPORT_TO_VIDEO.val());
      WebPanel panelButtons = new WebPanel(new GridBagLayout());
      List<WebButton> listButtons = Arrays.asList(
         this.buttonCreate,
         this.buttonEdit,
         this.buttonPlay,
         this.buttonAddPlaylist,
         this.buttonAddFavorite,
         this.buttonOpenDir,
         this.buttonRefresh,
         this.buttonCreateVideo
      );
      listButtons.forEach(b -> panelButtons.add(b, new GridBagConstraints()));
      WebPanel headerFooter = new WebPanel(new BorderLayout(6, 6));
      headerFooter.add(panelButtons, "Center");
      WebPanel header = new WebPanel(new BorderLayout(4, 0));
      header.add(this.jTextFieldSearch, "Center");
      header.add(headerFooter, "Last");
      this.add(header, "First");
      this.add(this.listView.getOnScroll(), "Center");
      listButtons.forEach(b -> WebLafUtils.addToolTipButton(this, b));
   }

   public void ctrlF() {
      SwingUtilities.invokeLater(() -> {
         this.jTextFieldSearch.selectAll();
         this.jTextFieldSearch.requestFocus();
      });
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

   @Override
   public void edit(File file) {
      try {
         EditPlayBackDialog.main(PlayBack.load(file), this::refresh);
      } catch (Exception var3) {
         Utils.error(I18N.MSG_ERROR_INVALID_FILE.format(String.valueOf(file)));
      }
   }

   private void create() {
      PlayBackUtils.create(pb -> {
         this.buttonRefresh.setEnabled(false);
         Runnable callback = () -> {
            SwingUtilities.invokeLater(() -> this.buttonRefresh.setEnabled(true));
            this.listView.setSelectedValue(pb.getFile().getAbsolutePath(), true);
         };
         Utils.startThreadDaemon(() -> this.listView.model.checkPathModification(callback));
      });
   }

   @Override
   public void run(File file) {
      Optional.ofNullable(this.owner).ifPresent(w -> {
         w.setVisible(false);
         w.dispose();
      });
      PlayBackUtils.run(file);
   }

   public void addPlaylist() {
      this.listView.addPlaylist();
   }

   private void refresh() {
      this.buttonRefresh.setEnabled(false);
      Runnable callback = () -> SwingUtilities.invokeLater(() -> this.buttonRefresh.setEnabled(true));
      Utils.startThreadDaemon(() -> this.listView.model.checkPathModification(callback));
   }

   private void edit() {
      try {
         Object obj = this.listView.getSelectedValue();
         if (obj == null) {
            Utils.error(I18N.MSG_ERROR_FILE_NOT_SELECTED.val());
            return;
         }

         this.edit(PlayBackUtils.getFile(obj.toString()));
      } catch (Exception var2) {
      }
   }

   public void createVideo() {
      try {
         Object obj = this.listView.getSelectedValue();
         if (obj == null) {
            Utils.error(I18N.MSG_ERROR_FILE_NOT_SELECTED.val());
            return;
         }

         this.createVideo(PlayBackUtils.getFile(obj.toString()));
      } catch (Exception var2) {
      }
   }

   @Override
   public void createVideo(File file) {
      try {
         ExportToVideoDialog.main(PlayBack.load(file));
      } catch (Exception var3) {
         Utils.error(I18N.MSG_ERROR_INVALID_FILE.format(String.valueOf(file)));
      }
   }

   public interface PlayBackTabListener {
      void addPlaylistMedia(List<IHItem> var1);
   }
}
