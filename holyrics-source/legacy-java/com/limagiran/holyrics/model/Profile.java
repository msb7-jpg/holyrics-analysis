package com.limagiran.holyrics.model;

import com.limagiran.holyrics.control.ProfileController;
import com.limagiran.holyrics.dao.MusicDao;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Profile implements Serializable {
   private static final long serialVersionUID = 12491L;
   private String name;
   private final List<Long> musics = new ArrayList<>();
   private long version = 1L;

   public Profile(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
      this.save();
   }

   public List<Long> getMusicsId() {
      return new ArrayList<>(this.musics);
   }

   public List<Music> getMusics() {
      return MusicDao.getLazy(this.musics);
   }

   public void addMusics(Music music) {
      if (music != null && !this.musics.contains(music.getId())) {
         this.musics.add(music.getId());
         this.save();
      }
   }

   public void addIds(List<Long> list) {
      list.stream().filter(id -> !this.musics.contains(id)).forEach(id -> this.musics.add(id));
      this.save();
   }

   public void addMusics(List<Music> list) {
      this.addIds(list.stream().filter(m -> !this.musics.contains(m.getId())).map(Music::getId).collect(Collectors.toList()));
   }

   public void removeMusic(Music music) {
      if (music != null) {
         this.musics.remove(music.getId());
         this.save();
      }
   }

   public void removeMusics(List<Music> list) {
      this.removeIds(list.stream().map(Music::getId).collect(Collectors.toList()));
   }

   public void removeIds(List<Long> list) {
      this.musics.removeAll(list);
      this.save();
   }

   public void removeIdsIfNotExists(List<Long> ids) {
      List<Long> notExists = this.musics.parallelStream().filter(id -> !ids.contains(id)).collect(Collectors.toList());
      this.musics.removeAll(notExists);
   }

   public void setMusics(List<Long> ids) {
      for (int i = 0; i < this.musics.size(); i++) {
         if (!ids.contains(this.musics.get(i))) {
            this.musics.remove(i);
         }
      }

      ids.stream().filter(m -> !this.musics.contains(m)).forEach(m -> this.musics.add(m));
      this.save();
   }

   public boolean containsMusic(Music m) {
      return m != null && this.musics.contains(m.getId());
   }

   public void save() {
      ProfileController.save();
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof Profile ? ((Profile)obj).name.equals(this.name) : super.equals(obj);
   }

   @Override
   public int hashCode() {
      return 679 + Objects.hashCode(this.name);
   }

   public static Comparator<Profile> sort() {
      return (p1, p2) -> p1 == null ? Integer.MIN_VALUE : (p2 == null ? Integer.MAX_VALUE : p2.getMusicsId().size() - p1.getMusicsId().size());
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
