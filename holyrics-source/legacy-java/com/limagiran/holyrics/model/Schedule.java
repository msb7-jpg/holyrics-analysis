package com.limagiran.holyrics.model;

import com.limagiran.holyrics.control.MemberController;
import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.control.WorshipController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.RWObj;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Schedule implements Serializable {
   private static final long serialVersionUID = 12491L;
   private transient Calendar date;
   public final String id;
   private long longtime;
   private final List<Long> musics = new ArrayList<>();
   private String notes;
   private List<byte[]> medias = new ArrayList<>();
   private transient List<IHItem> mediasBuff;
   private final Map<Long, Boolean> staff = new HashMap<>();
   private Long responsible;
   private long version = 2L;

   public Schedule(String id, Calendar date, String notes, Long responsible, List<Long> musics, List<byte[]> medias) {
      this.id = id;
      this.date = date;
      this.notes = notes;
      this.longtime = this.date.getTimeInMillis();
      this.responsible = responsible;
      this.musics.addAll(musics);
      this.medias.addAll(medias);
   }

   public static Comparator<Schedule> sort() {
      return (o1, o2) -> o1.getDate().compareTo(o2.getDate());
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof Schedule ? ((Schedule)o).id.equalsIgnoreCase(this.id) : super.equals(o);
   }

   @Override
   public int hashCode() {
      return 413 + Objects.hashCode(this.id);
   }

   public Calendar getDate() {
      if (this.date == null) {
         Calendar c = Calendar.getInstance();
         c.setTimeInMillis(this.longtime);
         this.date = c;
      }

      return (Calendar)this.date.clone();
   }

   public void setDate(Calendar date) {
      this.date = date;
      this.longtime = this.date.getTimeInMillis();
   }

   public Long getResponsible() {
      return this.responsible;
   }

   public boolean isResponsible(Member m) {
      return this.responsible != null && m.id == this.responsible;
   }

   public Member getResponsibleAsMember() {
      return this.responsible == null ? null : MemberController.get(this.responsible);
   }

   public void setResponsible(Long responsible) {
      this.responsible = responsible;
   }

   public boolean isScheduled(Member m) {
      return this.staff.getOrDefault(m.id, Boolean.FALSE);
   }

   public void setScheduled(Member m, boolean scheduled, boolean save) {
      this.staff.put(m.id, scheduled);
      BooleanOptional.runIfTrue(save, ScheduleController::save);
   }

   public String getNotes() {
      return this.notes;
   }

   public void setNotes(String notes, boolean save) {
      this.notes = Optional.ofNullable(notes).orElse("");
      BooleanOptional.runIfTrue(save, ScheduleController::save);
   }

   public void setScheduled(Member m, boolean scheduled) {
      this.setScheduled(m, scheduled, false);
   }

   public List<Music> getMusics() {
      return this.musics.stream().map(MusicDao::getLazy).filter(Objects::nonNull).collect(Collectors.toList());
   }

   public boolean contains(Music m) {
      return this.musics.contains(m.getId());
   }

   public void setMusics(List<Music> musics) {
      this.musics.clear();
      musics.stream().map(Music::getId).forEach(this.musics::add);
   }

   public void addMusic(Music music) {
      if (!this.musics.contains(music.getId())) {
         this.musics.add(music.getId());
      }
   }

   public synchronized List<IHItem> getMedias() {
      return new ArrayList<>(this.getMediasBuff());
   }

   public synchronized boolean contains(IHItem hItem) {
      return this.getMediasBuff().stream().anyMatch(item -> item.getId().equals(hItem.getId()));
   }

   public synchronized void setMedias(List<IHItem> medias) {
      this.medias.clear();
      this.mediasBuff = null;
      medias.stream().map(RWObj::getBytes).forEach(this.medias::add);
      this.getMediasBuff();
   }

   public synchronized void addMedia(IHItem hItem) {
      if (!this.contains(hItem)) {
         byte[] bytes = RWObj.getBytes(hItem);
         if (bytes.length > 0) {
            this.medias.add(bytes);
            this.getMediasBuff().add(hItem);
         }
      }
   }

   private synchronized List<IHItem> getMediasBuff() {
      if (this.mediasBuff == null) {
         this.mediasBuff = new ArrayList<>();
         this.medias.stream().map(bytes -> RWObj.read(bytes, IHItem.class)).filter(Objects::nonNull).forEach(this.mediasBuff::add);
      }

      return this.mediasBuff;
   }

   public boolean isActive() {
      for (Worship w : WorshipController.getWorships(false)) {
         if (this.id.startsWith(Long.toString(w.id))) {
            return w.isActive();
         }
      }

      return true;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      if (this.version <= 1L) {
         this.medias = new ArrayList<>();
         this.version = 2L;
      }

      if (this.version > 2L) {
         this.version = 2L;
      }
   }
}
