package com.limagiran.holyrics.hitem;

import com.alee.laf.list.WebList;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.managers.language.data.TooltipWay;
import com.limagiran.holyrics.control.FavoriteController;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.favorite.FavoriteUtils;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.swing.ListCellRendererHItem;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.IScheduleTask;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WebLafUtils;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class HItemListView extends WebList {
   public final ListModelHItem model;
   public final EnumTabType tabType;
   private final HItemListView.HItemListViewListener listener;

   public HItemListView(EnumTabType tabType, HItemListView.HItemListViewListener hItemListViewListener) {
      super(new ListModelHItem(tabType));
      this.tabType = tabType;
      this.listener = hItemListViewListener;
      this.model = (ListModelHItem)this.getModel();
      this.setCellRenderer(new ListCellRendererHItem(this.tabType, this.model));
      this.init();
   }

   private void init() {
      this.model.setJlist(this);
      this.setSelectionBackgroundColor(StyleUI.COLOR_HIGHLIGHT_TEXT);
      this.setSelectionBorderColor(StyleUI.COLOR_HIGHLIGHT_TEXT.darker().darker());
      this.setWebColoredSelection(true);
      this.setLayoutOrientation(2);
      this.setVisibleRowCount(-1);
      this.setMultiplySelectionAllowed(true);
      this.onDoubleClick(e -> {
         if (SwingUtilities.isLeftMouseButton(e)) {
            HItemUtils.run(true, this.getSelectedAsFileArray());
         }
      });
      this.setToolTipProvider(new ToolTipProviderHItemListView(this.tabType, false) {
         public TooltipWay getDirection(WebList component, Object value, int index, int column, boolean isSelected) {
            return TooltipWay.right;
         }
      });
      this.onMenuTrigger(e -> SwingUtils.popupMenuTrigger(e, this, obj -> {
            String id = this.tabType.path + String.valueOf(obj);
            boolean onlyOne = this.getSelectedValuesList().size() == 1;
            File itemAsFile = new File(id);
            WebPopupMenu popup = new WebPopupMenu();
            if (onlyOne) {
               List<File> files = HItemUtils.getListIfDirectory(itemAsFile, this.tabType.getFileFilterDirectory());
               HItemUtils.setupPopupMenuAction(popup, this.tabType.actionName, immediate -> HItemUtils.run(immediate, itemAsFile), files, true);
               popup.addSeparator();
            }

            HItemUtils.setupPopupMenuAddPlaylistEvent(popup, evt -> this.addPlaylist());
            if (this.tabType == EnumTabType.AUDIO || this.tabType == EnumTabType.VIDEO) {
               HItemUtils.setupPopupMenuAddPlaylistPlayer(popup, this.getSelectedAsFileArray());
            }

            if (onlyOne) {
               IHItem hitem = this.tabType.getHItem(itemAsFile.getName());
               HItemUtils.setupPopupMenuAddFavorite(popup, hitem);
               IScheduleTask.setupPopupMenu(popup, hitem);
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
         this.listener.setFavoriteEnabled(array.length == 1 && !FavoriteController.contains(this.tabType.getID(array[0])));
         this.listener.setPlaylistMediaEnabled(array.length >= 1 && array.length <= 20);
      });
   }

   private String[] getSelectedAsArray() {
      return new ArrayList<>(this.getSelectedValuesList()).stream().map(String::valueOf).toArray(String[]::new);
   }

   private File[] getSelectedAsFileArray() {
      return new ArrayList<>(this.getSelectedValuesList())
         .stream()
         .map(item -> new File(this.tabType.path + String.valueOf(item)).getAbsoluteFile())
         .toArray(File[]::new);
   }

   public WebScrollPane getOnScroll() {
      final WebScrollPane scroll = new WebScrollPane(this);
      scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
      scroll.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));
      scroll.setVerticalScrollBarPolicy(22);
      scroll.setHorizontalScrollBarPolicy(31);
      scroll.getVerticalScrollBar().setUnitIncrement(16);
      scroll.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            int w = scroll.getWidth() - 20;
            int max = 300;
            int cols = 6;

            do {
               cols--;
            } while (cols != 1 && w / cols <= max);

            HItemListView.this.setMaximumWidth(w);
            HItemListView.this.setFixedCellWidth((w - cols * 4) / cols);
         }
      });
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

               this.model.fireUpdate();
            });
         }
      } catch (Exception var4) {
      }
   }

   protected void play(boolean immediate) {
      this.play(immediate, this.getSelectedAsFileArray());
   }

   protected void play(boolean immediate, File... files) {
      if (files.length <= 0) {
         WebLafUtils.notificationDelay(I18N.MSG_ERROR_FILE_NOT_SELECTED.val(), 3L);
      } else {
         HItemUtils.run(immediate, files);
      }
   }

   public void addFavorite() {
      String[] values = this.getSelectedAsArray();
      if (values.length == 1) {
         this.addFavorite(values[0], new File(this.tabType.path, values[0]));
      }
   }

   private void addFavorite(String item, File itemAsFile) {
      ImageIcon icon = new ImageIcon(EnumTabType.getThumb(item, itemAsFile, ThumbMediaController.getThumbSize()));
      FavoriteUtils.getNameAndCreate(null, this.tabType.getFavoritable(item), icon, item);
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
      this.listener.addPlaylistMedia(Stream.of(item).map(this.tabType::getHItem).collect(Collectors.toList()));
   }

   public interface HItemListViewListener {
      void setFavoriteEnabled(boolean var1);

      void setPlaylistMediaEnabled(boolean var1);

      void addPlaylistMedia(List<IHItem> var1);
   }
}
