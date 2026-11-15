import { describe, it, expect, beforeEach } from "bun:test";
import { ControllerFactory } from "../control/ControllerFactory.ts";
import { ScheduleController } from "../control/ScheduleController.ts";
import { EventController } from "../control/EventController.ts";
import { WorshipController } from "../control/WorshipController.ts";
import { MemberController } from "../control/MemberController.ts";

import { Schedule } from "../model/Schedule.ts";
import { Event } from "../model/Event.ts";
import { Worship, WorshipDay, WorshipWeek } from "../model/Worship.ts";
import { Member, EnumGroups } from "../model/Member.ts";
import { Music } from "../model/Music.ts";

describe("Holyrics Controllers Suite", () => {
  beforeEach(() => {
    ControllerFactory.setUseMemoryOnly(true);
    ControllerFactory.clearMemory();
    ScheduleController.saveList([]);
    EventController.saveList([]);
    WorshipController.saveList([]);
    MemberController.saveList([]);
  });

  describe("ScheduleController", () => {
    it("should format dateToID and parse idToDate correctly", () => {
      const date = new Date(2026, 10, 15, 19, 30, 0); // Nov 15, 2026, 19:30
      const id = ScheduleController.dateToID(date);
      expect(id).toBe("202611151930");

      const parsed = ScheduleController.idToDate(id);
      expect(parsed.getFullYear()).toBe(2026);
      expect(parsed.getMonth()).toBe(10);
      expect(parsed.getDate()).toBe(15);
      expect(parsed.getHours()).toBe(19);
      expect(parsed.getMinutes()).toBe(30);
    });

    it("should fallback gracefully on invalid id in idToDate", () => {
      const d = ScheduleController.idToDate("invalid");
      expect(d instanceof Date).toBe(true);
      expect(isNaN(d.getTime())).toBe(false);
    });

    it("should add, get and remove schedules", () => {
      const scheduleDate = new Date(2026, 5, 10, 20, 0);
      const sched = new Schedule("test_1", scheduleDate, "Reunião");

      ScheduleController.add(sched);
      expect(ScheduleController.getAll().length).toBe(1);

      // adding duplicate should not duplicate
      ScheduleController.add(sched);
      expect(ScheduleController.getAll().length).toBe(1);

      ScheduleController.remove(sched);
      expect(ScheduleController.getAll().length).toBe(0);
    });

    it("should get existing schedule for event or create a new one automatically", () => {
      const evDate = new Date(2026, 6, 20, 18, 0);
      const event = new Event("ev_101", "Culto Especial", evDate, "Desc");

      const schedule = ScheduleController.get(event);
      expect(schedule).toBeDefined();
      expect(schedule.id).toBe("ev_101");
      expect(ScheduleController.getAll().length).toBe(1);

      // Subsequent get should return the same schedule
      const same = ScheduleController.get(event);
      expect(same.id).toBe("ev_101");
      expect(ScheduleController.getAll().length).toBe(1);
    });

    it("should change event date in schedule", () => {
      const evDate = new Date(2026, 6, 20, 18, 0);
      const event = new Event("ev_102", "Culto", evDate, "Desc");
      const sched = ScheduleController.get(event);

      const newDate = new Date(2026, 6, 25, 19, 0);
      ScheduleController.change(event, newDate);
      expect(sched.getDate().getTime()).toBe(newDate.getTime());
    });

    it("should filter next schedules for a music item", () => {
      const music = new Music();
      music.setId(42);
      music.setTitle("Graça");
      music.setArtist("Artista");

      const futureDate1 = new Date(Date.now() + 1000 * 60 * 60 * 24);
      const futureDate2 = new Date(Date.now() + 1000 * 60 * 60 * 48);
      const pastDate = new Date(Date.now() - 1000 * 60 * 60 * 24);

      const sFuture1 = new Schedule("sf1", futureDate1);
      sFuture1.addMusic(music);
      const sFuture2 = new Schedule("sf2", futureDate2);
      sFuture2.addMusic(music);
      const sPast = new Schedule("spast", pastDate);
      sPast.addMusic(music);

      ScheduleController.add(sFuture2);
      ScheduleController.add(sPast);
      ScheduleController.add(sFuture1);

      const nextSchedules = ScheduleController.getNextSchedules(music);
      expect(nextSchedules.length).toBe(2);
      expect(nextSchedules[0]!.getTime()).toBe(futureDate1.getTime());
      expect(nextSchedules[1]!.getTime()).toBe(futureDate2.getTime());

      const nextSingle = ScheduleController.getNextSchedule(music);
      expect(nextSingle?.getTime()).toBe(futureDate1.getTime());
    });
  });

  describe("EventController", () => {
    it("should add and remove events while keeping schedule in sync", () => {
      const eventDate = new Date(2026, 3, 12, 10, 0);
      const event = new Event("ev_sync", "Culto Matutino", eventDate, "Notas");

      // Pre-create schedule
      ScheduleController.get(event);
      expect(ScheduleController.getAll().length).toBe(1);

      EventController.add(event);
      expect(EventController.getAll().length).toBe(1);

      EventController.remove(event);
      expect(EventController.getAll().length).toBe(0);
      expect(ScheduleController.getAll().length).toBe(0);
    });

    it("should search events by month and year", () => {
      const e1 = new Event("e1", "Ev 1", new Date(2026, 4, 1, 10, 0), ""); // May 2026
      const e2 = new Event("e2", "Ev 2", new Date(2026, 4, 15, 10, 0), ""); // May 2026
      const e3 = new Event("e3", "Ev 3", new Date(2026, 5, 1, 10, 0), ""); // June 2026

      EventController.add(e1);
      EventController.add(e2);
      EventController.add(e3);

      const mayEvents = EventController.search(4, 2026);
      expect(mayEvents.length).toBe(2);
      expect(mayEvents.map((e) => e.id)).toEqual(["e1", "e2"]);
    });

    it("should search events by day", () => {
      const dayDate = new Date(2026, 7, 20, 15, 0);
      const e1 = new Event("d1", "Culto 1", dayDate, "");
      const e2 = new Event("d2", "Culto 2", new Date(2026, 7, 20, 19, 0), "");
      const e3 = new Event("d3", "Culto Outro Dia", new Date(2026, 7, 21, 19, 0), "");

      const results = EventController.searchByDay([e1, e2, e3], dayDate);
      expect(results.length).toBe(2);
      expect(results.map((e) => e.id)).toEqual(["d1", "d2"]);
    });
  });

  describe("WorshipController", () => {
    it("should add and remove worship routines", () => {
      const worship = new Worship(
        WorshipDay.SUN,
        WorshipWeek.ALL,
        19,
        0,
        "Culto de Domingo",
        1001
      );

      const added = WorshipController.add(worship);
      expect(added).toBe(true);

      const addedAgain = WorshipController.add(worship);
      expect(addedAgain).toBe(false);

      expect(WorshipController.getWorships().length).toBe(1);

      WorshipController.remove(worship);
      expect(WorshipController.getWorships().length).toBe(0);
    });

    it("should calculate daysOfMonth for ALL Sundays in a month", () => {
      // November 2026 (Nov 1 is Sunday, Nov 8, 15, 22, 29) -> 5 Sundays!
      const worship = new Worship(
        WorshipDay.SUN,
        WorshipWeek.ALL,
        18,
        30,
        "Culto da Família",
        1002
      );

      const days = WorshipController.daysOfMonth(worship, 10, 2026);
      expect(days.length).toBe(5);
      expect(days[0]!.getDate()).toBe(1);
      expect(days[1]!.getDate()).toBe(8);
      expect(days[2]!.getDate()).toBe(15);
      expect(days[3]!.getDate()).toBe(22);
      expect(days[4]!.getDate()).toBe(29);
      expect(days[0]!.getHours()).toBe(18);
      expect(days[0]!.getMinutes()).toBe(30);
    });

    it("should calculate daysOfMonth for FIRST and LAST weeks", () => {
      const wFirst = new Worship(
        WorshipDay.SUN,
        WorshipWeek.FIRST,
        10,
        0,
        "Primeiro Domingo",
        1003
      );
      const daysFirst = WorshipController.daysOfMonth(wFirst, 10, 2026);
      expect(daysFirst.length).toBe(1);
      expect(daysFirst[0]!.getDate()).toBe(1);

      const wLast = new Worship(
        WorshipDay.SUN,
        WorshipWeek.LAST,
        10,
        0,
        "Último Domingo",
        1004
      );
      const daysLast = WorshipController.daysOfMonth(wLast, 10, 2026);
      expect(daysLast.length).toBe(1);
      expect(daysLast[0]!.getDate()).toBe(29);
    });

    it("should search worship routines as events for a month", () => {
      const worship = new Worship(
        WorshipDay.WED,
        WorshipWeek.ALL,
        19,
        30,
        "Culto de Oração",
        2001
      );
      WorshipController.add(worship);

      const events = WorshipController.searchAsEvent(10, 2026, true);
      // November 2026 Wednesdays: Nov 4, 11, 18, 25 -> 4 events
      expect(events.length).toBe(4);
      expect(events[0]!.id.startsWith("2001")).toBe(true);
      expect(events[0]!.getName()).toBe("Culto de Oração");
    });

    it("should setTimeAndDesc and propagate updates to matching schedules", () => {
      const worship = new Worship(
        WorshipDay.SAT,
        WorshipWeek.FIRST,
        19,
        0,
        "Jovens",
        5005
      );
      WorshipController.add(worship);

      // Create an event & schedule corresponding to this worship
      const date = new Date(2026, 10, 7, 19, 0);
      const schedId = WorshipController.createId(worship, date);
      const sched = new Schedule(schedId, date);
      ScheduleController.add(sched);

      const newValues = new Worship(
        WorshipDay.SAT,
        WorshipWeek.FIRST,
        20,
        15,
        "Culto de Jovens Conectados",
        5005
      );

      WorshipController.setTimeAndDesc(worship, newValues);

      expect(worship.hour).toBe(20);
      expect(worship.minute).toBe(15);
      expect(worship.desc).toBe("Culto de Jovens Conectados");

      // Verify schedule was updated
      const updatedSchedules = ScheduleController.getAll().filter((s) =>
        s.id.startsWith("5005")
      );
      expect(updatedSchedules.length).toBe(1);
      expect(updatedSchedules[0]!.getDate().getHours()).toBe(20);
      expect(updatedSchedules[0]!.getDate().getMinutes()).toBe(15);
    });
  });

  describe("MemberController", () => {
    it("should add, get and remove members", () => {
      const member = new Member("Daniel", "Violão", [EnumGroups.INSTRUMENT]);
      MemberController.add(member);

      expect(MemberController.getAll().length).toBe(1);
      expect(MemberController.get(member.id)?.getName()).toBe("Daniel");

      MemberController.remove(member);
      expect(MemberController.getAll().length).toBe(0);
      expect(MemberController.get(member.id)).toBeNull();
    });

    it("should filter active members and sort by groups properly", () => {
      const mVoice = new Member("Alice", "Soprano", [EnumGroups.VOICE]);
      const mInst = new Member("Carlos", "Guitarra", [EnumGroups.INSTRUMENT]);
      const mInactive = new Member("Bruno", "Baixo", [EnumGroups.INSTRUMENT]);
      mInactive.setActive(false);

      MemberController.add(mInst);
      MemberController.add(mInactive);
      MemberController.add(mVoice);

      const activeOnly = MemberController.getAll(true);
      expect(activeOnly.length).toBe(2);
      expect(activeOnly.some((m) => m.getName() === "Bruno")).toBe(false);

      MemberController.sort();
      const allSorted = MemberController.getAll(false);
      // Voice (group ordinal 0) comes before Instrument (group ordinal 1), and active comes before inactive
      expect(allSorted[0]!.getName()).toBe("Alice");
      expect(allSorted[1]!.getName()).toBe("Carlos");
      expect(allSorted[2]!.getName()).toBe("Bruno");
    });
  });
});
