package com.limagiran.holyrics.report.model;

import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.model.History;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.EnumWorship;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayedMusics {
   public final String name;
   public final List<PlayedMusics.PlayedMusic> list = new ArrayList<>();

   public PlayedMusics(String name, List<Music> musics, Date start, Date end) {
      this.name = name;
      this.init(musics, start, end);
      this.sort();
   }

   private void init(List<Music> musics, Date start, Date end) {
      musics.stream().forEach(m -> {
         List<LocalDateTime> dates = History.getHistory(m).stream().map(d -> DateLG.toLocalDateTime(d.toInstant())).collect(Collectors.toList());
         this.list.add(new PlayedMusics.PlayedMusic(m, this.filter(dates), start, end));
      });
   }

   private Map<EnumWorship.EnumWorshipDays, List<LocalDateTime>> filter(List<LocalDateTime> dates) {
      Map<EnumWorship.EnumWorshipDays, List<LocalDateTime>> _return = new HashMap<>();
      Arrays.asList(EnumWorship.EnumWorshipDays.values())
         .stream()
         .forEach(en -> _return.put(en, dates.stream().filter(i -> i.get(ChronoField.DAY_OF_WEEK) == en.day.getValue()).collect(Collectors.toList())));
      return _return;
   }

   public void filter(EnumWorship.EnumWorshipDays en, boolean flag) {
      this.list.stream().forEach(p -> p.filter(en, flag));
      this.sort();
   }

   public final void setStart(Date start) {
      this.list.stream().forEach(p -> p.setStart(start));
      this.sort();
   }

   public final void setEnd(Date end) {
      this.list.stream().forEach(p -> p.setEnd(end));
      this.sort();
   }

   private void sort() {
      this.list
         .sort(
            (o1, o2) -> o1.getSize() != o2.getSize()
                  ? o2.getSize() - o1.getSize()
                  : o1.musicToSearch.toString().compareToIgnoreCase(o2.musicToSearch.toString())
         );
   }

   public static class PlayedMusic {
      private final Music music;
      private final Music musicToSearch;
      private final Map<EnumWorship.EnumWorshipDays, List<LocalDateTime>> map;
      private final Map<EnumWorship.EnumWorshipDays, Boolean> filter = new HashMap<>();
      private LocalDate start;
      private LocalDate end;

      private PlayedMusic(Music music, Map<EnumWorship.EnumWorshipDays, List<LocalDateTime>> map, Date start, Date end) {
         this.music = music;
         this.musicToSearch = MusicDao.getLazyToSearch(this.music.getId());
         this.map = map;
         this.setStart(start);
         this.setEnd(end);
         Arrays.asList(EnumWorship.EnumWorshipDays.values()).forEach(en -> {
            Boolean var10000 = this.filter.put(en, true);
         });
      }

      private void filter(EnumWorship.EnumWorshipDays en, boolean flag) {
         this.filter.put(en, flag);
      }

      private void setStart(Date start) {
         this.start = DateLG.toLocalDate(start);
      }

      private void setEnd(Date end) {
         this.end = DateLG.toLocalDate(end);
      }

      private List<LocalDate> getDate(EnumWorship.EnumWorshipDays en) {
         return this.map
            .get(en)
            .stream()
            .map(LocalDateTime::toLocalDate)
            .filter(i -> this.start.compareTo((ChronoLocalDate)this.end) <= 0 && !i.isBefore(this.start) && !i.isAfter(this.end))
            .collect(Collectors.toList());
      }

      public int getSize() {
         List list = new ArrayList();
         this.filter.forEach((en, b) -> list.addAll((Collection)(b ? this.getDate(en) : new ArrayList())));
         return list.size();
      }

      public String getToolTipText() {
         StringBuilder sb = new StringBuilder("<html>");
         Arrays.asList(EnumWorship.EnumWorshipDays.values()).stream().filter(this.filter::get).forEach(en -> {
            int size = this.getDate(en).size();
            if (size > 0) {
               sb.append(en).append(" - ").append(String.format("%3sx<br>", size));
            }
         });
         String s = sb.toString().replaceFirst("<br>$", "");
         return s.equalsIgnoreCase("<html>") ? "" : s;
      }

      @Override
      public String toString() {
         return String.format("%3s - %s", this.getSize(), this.music.toString());
      }
   }
}
