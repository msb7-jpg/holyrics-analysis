package com.limagiran.holyrics.hitem;

import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.VideoUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;

public class ListModelHItem extends AbstractListModel<String> {
   private final transient List<ListModelHItem.MyFile> listMyFile = Collections.synchronizedList(new ArrayList<>());
   private final transient Object listMyFilesLock = new Object();
   private final transient List<String> filteredList = Collections.synchronizedList(new ArrayList<>());
   private final transient Object filteredListLock = new Object();
   private transient String filter = "";
   private transient JList<String> jlist;
   public final EnumTabType tabType;

   public ListModelHItem(EnumTabType tabType) {
      this.tabType = tabType;
      this.filter();
      this.startCheckFileChange();
   }

   private void startCheckFileChange() {
      Utils.startThreadDaemon(() -> {
         Utils.sleep(1000L);

         while (true) {
            if (!VideoUtils.staticIsAnyVisible()) {
               this.checkPathModification(null);
            }

            Utils.sleep(3000L);
         }
      });
   }

   public void checkPathModification(Runnable callback) {
      try {
         List<ListModelHItem.MyFile> _list = Arrays.stream(this.tabType.listFiles()).map(ListModelHItem.MyFile::new).collect(Collectors.toList());
         Collections.sort(_list);
         List<ListModelHItem.MyFile> listMyFileClone = new ArrayList<>();
         synchronized (this.listMyFilesLock) {
            listMyFileClone.addAll(this.listMyFile);
         }

         if (!ListModelHItem.MyFile.toString(listMyFileClone).equals(ListModelHItem.MyFile.toString(_list))) {
            ListModelHItem.MyFile.createThumbIfNotExists(_list, this.tabType);
            BooleanOptional.runIfTrue(!_list.isEmpty(), ThumbMediaController::save);
            synchronized (this.listMyFilesLock) {
               this.listMyFile.clear();
               this.listMyFile.addAll(_list);
            }

            this.fireUpdate(this.jlist != null ? this.jlist.getSelectedValue() : null);
            return;
         }
      } catch (Exception var14) {
         return;
      } finally {
         Optional.ofNullable(callback).ifPresent(Runnable::run);
      }
   }

   @Override
   public int getSize() {
      return this.filteredList.size();
   }

   public synchronized String getElementAt(int index) {
      try {
         return this.filteredList.get(Utils.range(index, 0, this.filteredList.size() - 1));
      } catch (Exception var3) {
         return "";
      }
   }

   public void fireUpdate(String selected) {
      this.filter();
      SwingUtilities.invokeLater(() -> {
         try {
            this.fireContentsChanged(this, 0, this.getSize());
            if (this.jlist != null && selected != null && this.filteredList.contains(selected)) {
               this.jlist.setSelectedValue(selected, true);
            } else if (!this.filteredList.isEmpty()) {
               this.jlist.setSelectedIndex(0);
            }
         } catch (Exception var3) {
         }
      });
   }

   public void fireUpdate() {
      this.fireUpdate(null);
   }

   public void setFilter(String filter) {
      this.filter = filter;
      this.fireUpdate(null);
   }

   public String getFilter() {
      return this.filter;
   }

   private void filter() {
      synchronized (this.filteredListLock) {
         this.filteredList.clear();
         String filterNormalized = Utils.removeAccent(this.filter.trim().toLowerCase());
         synchronized (this.listMyFilesLock) {
            this.listMyFile.forEach(myFile -> {
               if (filterNormalized.isEmpty() || myFile.nameNormalized.contains(filterNormalized)) {
                  this.filteredList.add(myFile.name);
               }
            });
         }
      }
   }

   public List<ListModelHItem.MyFile> listFiles() {
      List<ListModelHItem.MyFile> _return = new ArrayList<>();
      synchronized (this.listMyFilesLock) {
         _return.addAll(this.listMyFile);
         return _return;
      }
   }

   public JList<String> getJlist() {
      return this.jlist;
   }

   public void setJlist(JList<String> jlist) {
      this.jlist = jlist;
   }

   public static class MyFile implements Comparable<ListModelHItem.MyFile> {
      public final File file;
      public final boolean dir;
      public final String name;
      public final String nameNormalized;
      public final long lastModified;

      public MyFile(File file) {
         this.file = file;
         this.dir = this.file.isDirectory();
         this.name = this.file.getName();
         this.nameNormalized = Utils.removeAccent(this.name.toLowerCase());
         this.lastModified = this.file.lastModified();
      }

      public int compareTo(ListModelHItem.MyFile f) {
         return this.dir == f.dir ? this.nameNormalized.compareTo(f.nameNormalized) : (this.dir ? -1 : 1);
      }

      @Override
      public String toString() {
         return this.name;
      }

      static String toString(List<ListModelHItem.MyFile> list) {
         StringBuilder sb = new StringBuilder();
         list.forEach(mf -> {
            sb.append(mf.file).append(":").append(mf.lastModified).append(";");
            if (mf.dir) {
               sb.append("[");
               Optional.ofNullable(mf.file.listFiles()).ifPresent(files -> {
                  for (File f : files) {
                     sb.append(f).append(":").append(f.lastModified()).append(";");
                  }
               });
               sb.append("]");
            }
         });
         return sb.toString();
      }

      static void createThumbIfNotExists(List<ListModelHItem.MyFile> _list, EnumTabType tabType) {
         BiConsumer<String, File> createIfNotLocked = (id, file) -> {
            long l = System.currentTimeMillis();

            while (file.exists() && Dir.canDelete(file) != null && System.currentTimeMillis() - l <= 10000L) {
               Utils.sleep(50L);
            }

            ThumbMediaController.createIfNotExists(id, file);
         };
         _list.forEach(myFile -> {
            String id = tabType.path + myFile.name;
            createIfNotLocked.accept(id, myFile.file);
            if (myFile.dir && !tabType.isAudio()) {
               File[] files = Optional.ofNullable(myFile.file.listFiles()).orElse(new File[0]);
               Stream.of(files).filter(tabType.getFileFilterDirectory()).forEach(file -> {
                  String subId = tabType.path + myFile.name + File.separator + file.getName();
                  createIfNotLocked.accept(subId, file);
               });
               if (files.length == 0) {
                  ThumbMediaController.remove(id);
               }
            }
         });
      }

      public String getThumb64(EnumTabType tabType) {
         String id = tabType.path + this.name;
         return Utils.bytesToBase64(ThumbMediaController.getOrDefault(id, ToolTipProviderHItemListView.THUMB_NOT_FOUND));
      }
   }
}
