package com.limagiran.holyrics.report.model;

import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.model.Schedule;
import com.limagiran.holyrics.util.DateLG;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportScheduleMember {
   public final Member member;
   private final List<Schedule> responsible = new ArrayList<>();
   private final List<Schedule> scheduled = new ArrayList<>();
   private LocalDate start;
   private LocalDate end;

   public ReportScheduleMember(Member member, Date start, Date end) {
      this.member = member;
      this.setStart(start);
      this.setEnd(end);
      this.init();
   }

   private void init() {
      ScheduleController.getAll().stream().forEach(s -> {
         if (s.isResponsible(this.member)) {
            this.responsible.add(s);
         }

         if (s.isScheduled(this.member)) {
            this.scheduled.add(s);
         }
      });
   }

   public final void setStart(Date start) {
      this.start = DateLG.toLocalDate(start);
   }

   public final void setEnd(Date end) {
      this.end = DateLG.toLocalDate(end);
   }

   public long getResponsibleCount() {
      return this.responsible
         .stream()
         .map(s -> DateLG.toLocalDate(s.getDate()))
         .filter(i -> this.start.compareTo((ChronoLocalDate)this.end) <= 0 && !i.isBefore(this.start) && !i.isAfter(this.end))
         .count();
   }

   public long getScheduledCount() {
      return this.scheduled
         .stream()
         .map(s -> DateLG.toLocalDate(s.getDate()))
         .filter(i -> this.start.compareTo((ChronoLocalDate)this.end) <= 0 && !i.isBefore(this.start) && !i.isAfter(this.end))
         .count();
   }
}
