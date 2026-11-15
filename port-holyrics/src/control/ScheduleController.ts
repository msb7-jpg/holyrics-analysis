import { ControllerFactory } from "./ControllerFactory.ts";
import { Event, ScheduleController as EventScheduleStub } from "../model/Event.ts";
import { Music } from "../model/Music.ts";
import { Schedule, ScheduleController as ScheduleSaveStub } from "../model/Schedule.ts";
import { Worship } from "../model/Worship.ts";

// Wire stubs from models to real controller
EventScheduleStub.setListener((event: Event, date: Date) => {
  ScheduleController.change(event, date);
});
(ScheduleSaveStub as any).save = () => {
  ScheduleController.save();
};

/**
 * Revives raw JSON objects into Schedule instances.
 */
function reviveSchedule(data: any): Schedule {
  if (data instanceof Schedule) {
    return data;
  }
  const date = data.date
    ? new Date(data.date)
    : data.longtime
    ? new Date(data.longtime)
    : new Date();
  const schedule = new Schedule(
    data.id,
    date,
    data.notes ?? "",
    data.responsible ?? null,
    Array.isArray(data.musics) ? data.musics : [],
    Array.isArray(data.medias) ? data.medias : []
  );
  if (data.staff) {
    if (data.staff instanceof Map) {
      for (const [k, v] of data.staff.entries()) {
        (schedule as any).staff.set(Number(k), Boolean(v));
      }
    } else if (typeof data.staff === "object") {
      for (const [k, v] of Object.entries(data.staff)) {
        (schedule as any).staff.set(Number(k), Boolean(v));
      }
    }
  }
  return schedule;
}

/**
 * Controller for Schedule management.
 * Isomorphic TypeScript port of com.limagiran.holyrics.control.ScheduleController
 */
export class ScheduleController {
  private static saveNeeded: boolean = false;
  private static readonly FILE_NAME: string = "schedule.hly";
  private static readonly SCHEDULES: Schedule[] = ControllerFactory.load<Schedule>(
    ScheduleController.FILE_NAME,
    reviveSchedule
  );

  public static reload(): void {
    const list = ControllerFactory.load<Schedule>(
      ScheduleController.FILE_NAME,
      reviveSchedule
    );
    ScheduleController.saveNeeded = false;
    ScheduleController.SCHEDULES.length = 0;
    ScheduleController.SCHEDULES.push(...list);
    ScheduleController.saveNeeded = true;
  }

  public static checkSave(): void {
    if (ScheduleController.saveNeeded) {
      ScheduleController.saveNeeded = false;
      ScheduleController.save();
    }
  }

  public static dateToID(c: Date): string {
    const year = String(c.getFullYear()).padStart(4, "0");
    const month = String(c.getMonth() + 1).padStart(2, "0");
    const day = String(c.getDate()).padStart(2, "0");
    const hour = String(c.getHours()).padStart(2, "0");
    const min = String(c.getMinutes()).padStart(2, "0");
    return `${year}${month}${day}${hour}${min}`;
  }

  public static idToDate(id: string): Date {
    try {
      if (!id || id.length < 12) {
        return new Date();
      }
      const year = parseInt(id.substring(0, 4), 10);
      const month = parseInt(id.substring(4, 6), 10) - 1;
      const day = parseInt(id.substring(6, 8), 10);
      const hour = parseInt(id.substring(8, 10), 10);
      const min = parseInt(id.substring(10, 12), 10);
      const d = new Date(year, month, day, hour, min, 0, 0);
      if (isNaN(d.getTime())) {
        return new Date();
      }
      return d;
    } catch {
      return new Date();
    }
  }

  public static add(s: Schedule): void {
    const exists = ScheduleController.SCHEDULES.some((item) =>
      item.equals ? item.equals(s) : item.id.toLowerCase() === s.id.toLowerCase()
    );
    if (!exists) {
      ScheduleController.SCHEDULES.push(s);
    }
    ScheduleController.save();
  }

  public static remove(s: Schedule): void {
    const index = ScheduleController.SCHEDULES.findIndex((item) =>
      item.equals ? item.equals(s) : item.id.toLowerCase() === s.id.toLowerCase()
    );
    if (index !== -1) {
      ScheduleController.SCHEDULES.splice(index, 1);
      ScheduleController.save();
    }
  }

  public static removeByWorship(w: Worship): void {
    const worshipIdPrefix = String(w.id);
    const toRemove = ScheduleController.SCHEDULES.filter((s) =>
      s.id.startsWith(worshipIdPrefix)
    );
    for (const r of toRemove) {
      const idx = ScheduleController.SCHEDULES.indexOf(r);
      if (idx !== -1) {
        ScheduleController.SCHEDULES.splice(idx, 1);
      }
    }
    ScheduleController.save();
  }

  public static get(e: Event): Schedule {
    let result = ScheduleController.SCHEDULES.find((s) =>
      s.id.toLowerCase() === e.id.toLowerCase()
    );
    if (!result) {
      result = new Schedule(e.id, e.getDate(), "", null, [], []);
      ScheduleController.SCHEDULES.push(result);
      ScheduleController.saveNeeded = true;
    }
    return result;
  }

  public static getNextSchedules(m: Music | null): Date[] {
    if (!m) {
      return [];
    }
    const now = Date.now();
    return ScheduleController.SCHEDULES.filter(
      (s) => s.getDate().getTime() > now && s.contains(m)
    )
      .map((s) => s.getDate())
      .sort((a, b) => a.getTime() - b.getTime());
  }

  public static getNextSchedule(m: Music | null): Date | null {
    if (!m) {
      return null;
    }
    const nextList = ScheduleController.getNextSchedules(m);
    return nextList.length > 0 ? (nextList[0] ?? null) : null;
  }

  public static getAll(): Schedule[] {
    return [...ScheduleController.SCHEDULES];
  }

  public static change(e: Event, newDate: Date): void {
    ScheduleController.get(e).setDate(newDate);
    ScheduleController.save();
  }

  public static save(): void {
    ScheduleController.sort();
    ControllerFactory.save(ScheduleController.SCHEDULES, ScheduleController.FILE_NAME);
  }

  public static sort(): void {
    ScheduleController.SCHEDULES.sort(Schedule.sort());
  }

  public static saveList(list: Schedule[]): void {
    ScheduleController.SCHEDULES.length = 0;
    const seen = new Set<string>();
    const distinct: Schedule[] = [];
    for (const item of list) {
      const key = item.id.toLowerCase();
      if (!seen.has(key)) {
        seen.add(key);
        distinct.push(item);
      }
    }
    ScheduleController.SCHEDULES.push(...distinct);
    ScheduleController.save();
  }
}

export default ScheduleController;
