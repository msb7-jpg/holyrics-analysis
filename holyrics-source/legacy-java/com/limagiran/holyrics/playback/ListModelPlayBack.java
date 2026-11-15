package com.limagiran.holyrics.playback;

import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.VideoUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;

public class ListModelPlayBack extends AbstractListModel<String> {
   private final List<ListModelPlayBack.MyFile> listMyFile = Collections.synchronizedList(new ArrayList<>());
   private final Object listMyFilesLock = new Object();
   private final List<String> filteredList = Collections.synchronizedList(new ArrayList<>());
   private final Object filteredListLock = new Object();
   private String filter = "";
   private JList<String> jlist;

   public ListModelPlayBack() {
      this.filter();
      this.startCheckFileChange();
   }

   private void startCheckFileChange() {
      Utils.startThreadDaemon(() -> {
         Utils.sleep(500L);

         while (true) {
            if (!VideoUtils.staticIsAnyVisible()) {
               this.checkPathModification(null);
            }

            Utils.sleep(5000L);
         }
      });
   }

   public void checkPathModification(Runnable callback) {
      try {
         File path = new File(Values.PATH_MEDIA_AUTOMATIC_PRESENTATIONS);
         BooleanOptional.runIfTrue(!path.exists(), path::mkdirs);
         List<ListModelPlayBack.MyFile> _list = Arrays.stream(path.listFiles(f -> f.isFile() && f.getName().endsWith(".ap")))
            .map(ListModelPlayBack.MyFile::new)
            .collect(Collectors.toList());
         Collections.sort(_list);
         List<ListModelPlayBack.MyFile> listMyFileClone = new ArrayList<>();
         synchronized (this.listMyFilesLock) {
            listMyFileClone.addAll(this.listMyFile);
         }

         if (!ListModelPlayBack.MyFile.toString(listMyFileClone).equals(ListModelPlayBack.MyFile.toString(_list))) {
            synchronized (this.listMyFilesLock) {
               this.listMyFile.clear();
               this.listMyFile.addAll(_list);
            }

            this.fireUpdate(this.jlist != null ? this.jlist.getSelectedValue() : null);
            return;
         }
      } catch (Exception var15) {
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

   public List<ListModelPlayBack.MyFile> listFiles() {
      List<ListModelPlayBack.MyFile> _return = new ArrayList<>();
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

   public static class MyFile implements Comparable<ListModelPlayBack.MyFile> {
      public final File file;
      public final String name;
      public final String nameNormalized;

      public MyFile(File file) {
         this.file = file;
         this.name = this.file.getName();
         this.nameNormalized = Utils.removeAccent((this.name.endsWith(".ap") ? this.name.substring(0, this.name.length() - 3) : this.name).toLowerCase());
      }

      public int compareTo(ListModelPlayBack.MyFile f) {
         return this.nameNormalized.compareTo(f.nameNormalized);
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static String toString(List<ListModelPlayBack.MyFile> list) {
         return list.stream().map(mf -> mf.file.toString()).collect(Collectors.joining(";"));
      }
   }
}
