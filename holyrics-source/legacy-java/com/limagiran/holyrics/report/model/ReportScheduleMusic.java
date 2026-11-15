package com.limagiran.holyrics.report.model;

import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Schedule;
import com.limagiran.holyrics.util.DateLG;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReportScheduleMusic {
   public final Music music;
   public final Music musicToSearch;
   public final List<Schedule> list = new ArrayList<>();
   private LocalDate start;
   private LocalDate end;

   public ReportScheduleMusic(Music music, Date start, Date end) {
      this.music = music;
      this.musicToSearch = MusicDao.getLazyToSearch(music.getId());
      this.setStart(start);
      this.setEnd(end);
      this.init();
   }

   private void init() {
      ScheduleController.getAll().stream().filter(s -> s.getMusics().contains(this.music)).forEach(this.list::add);
   }

   public final void setStart(Date start) {
      this.start = DateLG.toLocalDate(start);
   }

   public final void setEnd(Date end) {
      this.end = DateLG.toLocalDate(end);
   }

   public long size() {
      return this.list
         .stream()
         .map(s -> DateLG.toLocalDate(s.getDate()))
         .filter(i -> this.start.compareTo((ChronoLocalDate)this.end) <= 0 && !i.isBefore(this.start) && !i.isAfter(this.end))
         .count();
   }

   private List<LocalDate> filtered() {
      return this.list
         .stream()
         .map(s -> DateLG.toLocalDate(s.getDate()))
         .filter(i -> this.start.compareTo((ChronoLocalDate)this.end) <= 0 && !i.isBefore(this.start) && !i.isAfter(this.end))
         .sorted()
         .collect(Collectors.toList());
   }

   public String getToolTipText() {
      StringBuilder sb = new StringBuilder("<html>");
      this.filtered().stream().map(d -> String.format("%s<br>", DateLG.SDF_D_F.format(DateLG.toDate(d)))).forEach(sb::append);
      String s = sb.toString().replaceFirst("<br>$", "");
      return s.equalsIgnoreCase("<html>") ? "" : s;
   }

   public static void sort(List<ReportScheduleMusic> list) {
      list.sort(
         (o1, o2) -> o1.size() != o2.size() ? (int)(o2.size() - o1.size()) : o1.musicToSearch.toString().compareToIgnoreCase(o2.musicToSearch.toString())
      );
   }
}
