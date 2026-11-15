package com.limagiran.holyrics.playback;

import com.alee.laf.list.WebList;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.scroll.WebScrollPane;
import com.limagiran.holyrics.control.FavoriteController;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.favorite.FavoriteUtils;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.hitem.item.HItemPlayBack;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.swing.ListCellRendererPlayBack;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.IScheduleTask;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WebLafUtils;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class PlayBackListView extends WebList {
   public final ListModelPlayBack model;
   private final PlayBackListView.PlayBackListViewListener listener;

   public PlayBackListView(PlayBackListView.PlayBackListViewListener playBackListViewListener) {
      super(new ListModelPlayBack());
      this.listener = playBackListViewListener;
      this.model = (ListModelPlayBack)this.getModel();
      this.setCellRenderer(new ListCellRendererPlayBack(this.model));
      this.init();
   }

   private void init() {
      this.model.setJlist(this);
      this.setSelectionBackgroundColor(StyleUI.COLOR_HIGHLIGHT_TEXT);
      this.setSelectionBorderColor(StyleUI.COLOR_HIGHLIGHT_TEXT.darker().darker());
      this.setWebColoredSelection(true);
      this.setVisibleRowCount(-1);
      this.setMultiplySelectionAllowed(true);
      this.onDoubleClick(e -> {
         if (SwingUtilities.isLeftMouseButton(e)) {
            this.listener.run(this.getSelectedAsFile());
         }
      });
      this.onMenuTrigger(e -> SwingUtils.popupMenuTrigger(e, this, obj -> {
            String id = PlayBackUtils.getId(String.valueOf(obj));
            boolean onlyOne = this.getSelectedValuesList().size() == 1;
            File itemAsFile = new File(id);
            WebPopupMenu popup = new WebPopupMenu();
            if (onlyOne) {
               HItemUtils.setupPopupMenuAction(popup, I18N.WORD_SHOW.val(), immediate -> this.listener.run(itemAsFile), new ArrayList<>(), false);
               WebMenuItem edit = new WebMenuItem(I18N.WORD_EDIT.val(), new ImageIcon(Images.ICON_EDIT_16));
               edit.addActionListener(ae -> this.listener.edit(itemAsFile));
               popup.add(edit);
               popup.addSeparator();
            }

            HItemUtils.setupPopupMenuAddPlaylistEvent(popup, actionListener -> this.addPlaylist());
            if (onlyOne) {
               HItemPlayBack hitem = new HItemPlayBack(itemAsFile.getName());
               HItemUtils.setupPopupMenuAddFavorite(popup, hitem);
               IScheduleTask.setupPopupMenu(popup, hitem);
            }

            if (onlyOne) {
               popup.addSeparator();
               WebMenuItem createVideo = new WebMenuItem(I18N.MSG_PLAYBACK_EXPORT_TO_VIDEO.val(), new ImageIcon(Images.load("movie16.png")));
               createVideo.addActionListener(evt -> this.listener.createVideo(itemAsFile));
               popup.add(createVideo);
            }

            popup.addSeparator();
            WebMenuItem delete = new WebMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
            delete.addActionListener(evt -> this.delete());
            popup.add(delete);
            if (onlyOne) {
               popup.add(HItemUtils.getOpenLocationMenuItem(null, itemAsFile));
            }

            popup.show(this, e.getX() - 20, e.getY());
         }));
      this.addListSelectionListener(e -> {
         String[] array = this.getSelectedAsArray();
         this.listener.setFavoriteEnabled(array.length == 1 && !FavoriteController.contains(PlayBackUtils.getId(array[0])));
         this.listener.setPlaylistMediaEnabled(array.length >= 1 && array.length <= 20);
      });
   }

   private String[] getSelectedAsArray() {
      return new ArrayList<>(this.getSelectedValuesList()).stream().map(String::valueOf).toArray(String[]::new);
   }

   private File[] getSelectedAsFileArray() {
      return new ArrayList<>(this.getSelectedValuesList()).stream().map(String::valueOf).map(PlayBackUtils::getFile).toArray(File[]::new);
   }

   private File getSelectedAsFile() {
      Object obj = this.getSelectedValue();
      return obj == null ? null : new File(Values.PATH_MEDIA_AUTOMATIC_PRESENTATIONS + obj.toString()).getAbsoluteFile();
   }

   public WebScrollPane getOnScroll() {
      WebScrollPane scroll = new WebScrollPane(this);
      scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
      scroll.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));
      scroll.setVerticalScrollBarPolicy(22);
      scroll.setHorizontalScrollBarPolicy(31);
      scroll.getVerticalScrollBar().setUnitIncrement(16);
      return scroll;
   }

   private void delete() {
      try {
         File[] files = this.getSelectedAsFileArray();
         String names = files.length == 1 ? files[0].getName() : I18N.WORD_FILES.val() + ": " + files.length;
         String text = String.format(
            "<html><b>%s:</b> %s<br><span style='color:red;font-size:90%%;font-weight:bold'>%s</span><br><br>%s",
            I18N.WORD_DELETE.val(),
            names,
            I18N.MSG_ALERT_DELETE_HITEM.val(),
            I18N.WORD_CONFIRM.val() + "?"
         );
         if (Utils.confirm(null, text, I18N.WORD_DELETE.val())) {
            Utils.startThreadDaemon(() -> {
               for (File file : files) {
                  if (file.isDirectory()) {
                     Dir.deleteDir(file);
                  } else {
                     file.delete();
                  }
               }

               this.model.checkPathModification(null);
            });
         }
      } catch (Exception var4) {
      }
   }

   protected void play() {
      this.play(this.getSelectedAsFile());
   }

   protected void play(File file) {
      if (file == null) {
         WebLafUtils.notificationDelay(I18N.MSG_ERROR_FILE_NOT_SELECTED.val(), 3L);
      } else {
         this.listener.run(file);
      }
   }

   public void addFavorite() {
      String[] values = this.getSelectedAsArray();
      if (values.length == 1) {
         this.addFavorite(values[0]);
      }
   }

   private void addFavorite(String item) {
      ImageIcon icon = new ImageIcon(PlayBackUtils.getThumb(ThumbMediaController.getThumbSize()));
      HItemPlayBack hitem = new HItemPlayBack(item);
      FavoriteUtils.getNameAndCreate(null, hitem, icon, hitem.getNameWithoutExtension());
   }

   public void addPlaylist() {
      String[] values = this.getSelectedAsArray();
      if (values.length <= 20) {
         this.addPlaylist(values);
      } else {
         Utils.error(I18N.MSG_ERROR_MAX_FILES_X.format(20));
      }
   }

   private void addPlaylist(String... item) {
      this.listener.addPlaylistMedia(Stream.of(item).map(HItemPlayBack::new).collect(Collectors.toList()));
   }

   public interface PlayBackListViewListener {
      void setFavoriteEnabled(boolean var1);

      void setPlaylistMediaEnabled(boolean var1);

      void edit(File var1);

      void addPlaylistMedia(List<IHItem> var1);

      void run(File var1);

      void createVideo(File var1);
   }
}
