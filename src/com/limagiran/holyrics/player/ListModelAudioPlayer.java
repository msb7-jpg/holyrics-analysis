package com.limagiran.holyrics.player;

import com.limagiran.holyrics.util.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.AbstractListModel;

public class ListModelAudioPlayer extends AbstractListModel<File> {
   private final AudioPlayerSettings set;
   private final List<File> filtered = new ArrayList<>();
   private String filter = "";

   public ListModelAudioPlayer(AudioPlayerSettings set) {
      this.set = set;
      this.filtered.addAll(this.getFiltered());
   }

   public void setFilter(String filter) {
      this.filter = Utils.removeAccent(filter).toLowerCase();
      this.fire();
   }

   public final List<File> getFiltered() {
      return this.set.list.stream().filter(f -> Utils.removeAccent(getFileName(f)).toLowerCase().contains(this.filter)).collect(Collectors.toList());
   }

   public static String getFileName(File f) {
      return f.getName().contains(".") ? f.getName().substring(0, f.getName().lastIndexOf(".")) : f.getName();
   }

   @Override
   public int getSize() {
      return this.filtered.size();
   }

   public File getElementAt(int index) {
      return this.filtered.get(index);
   }

   public void add(int index, Object o) {
      if (o instanceof File) {
         this.set.list.add(index, (File)o);
         this.set.save();
         this.fire();
      }
   }

   public void remove(int index) {
      this.set.list.remove(index);
      this.set.save();
      this.fire();
   }

   public void fire() {
      this.filtered.clear();
      this.filtered.addAll(this.getFiltered());
      this.fireContentsChanged(this, 0, this.filtered.size());
   }
}
