import { ControllerFactory } from "./ControllerFactory.ts";
import { ScheduleController } from "./ScheduleController.ts";
import { Event } from "../model/Event.ts";
import { Schedule, WorshipController as ScheduleWorshipStub } from "../model/Schedule.ts";
import { Worship, WorshipDay, WorshipWeek } from "../model/Worship.ts";

(ScheduleWorshipStub as any).getWorships = (activeOnly: boolean = false) => {
  return WorshipController.getWorships(activeOnly);
};

/**
 * Revives raw JSON objects into Worship instances.
 */
function reviveWorship(data: any): Worship {
  if (data instanceof Worship) {
    return data;
  }
  const day = typeof data.day === "string"
    ? WorshipDay.valueOf(data.day)
    : data.day?.key
    ? WorshipDay.valueOf(data.day.key)
    : WorshipDay.SUN;

  const week = typeof data.week === "string"
    ? WorshipWeek.valueOf(data.week)
    : data.week?.key
    ? WorshipWeek.valueOf(data.week.key)
    : WorshipWeek.ALL;

  const worship = new Worship(
    day,
    week,
    Number(data.hour ?? 0),
    Number(data.minute ?? 0),
    data.desc ?? "",
    data.id !== undefined ? Number(data.id) : undefined
  );
  if (data.active !== undefined) {
    worship.setActive(Boolean(data.active));
  }
  if (data.version !== undefined) {
    worship.setVersion(Number(data.version));
  }
  return worship;
}

/**
 * Controller for Worship management.
 * Isomorphic TypeScript port of com.limagiran.holyrics.control.WorshipController
 */
export class WorshipController {
  private static readonly FILE_NAME: string = "worship.hly";
  private static readonly WORSHIP: Worship[] = ControllerFactory.load<Worship>(
    WorshipController.FILE_NAME,
    reviveWorship
  );

  public static add(w: Worship): boolean {
    const exists = WorshipController.WORSHIP.some((item) =>
      item.equals ? item.equals(w) : item.id === w.id
    );
    if (exists) {
      return false;
    }
    WorshipController.WORSHIP.push(w);
    WorshipController.save();
    return true;
  }

  public static remove(w: Worship): void {
    const index = WorshipController.WORSHIP.findIndex((item) =>
      item.equals ? item.equals(w) : item.id === w.id
    );
    if (index !== -1) {
      WorshipController.WORSHIP.splice(index, 1);
      ScheduleController.removeByWorship(w);
      WorshipController.save();
    }
  }

  public static getWorships(onlyActive: boolean = false): Worship[] {
    return WorshipController.WORSHIP.filter((w) => !onlyActive || w.isActive());
  }

  public static searchAsEvent(
    month: number,
    year: number,
    onlyEnabled: boolean
  ): Event[] {
    const result: Event[] = [];
    WorshipController.WORSHIP
      .filter((w) => !onlyEnabled || w.isActive())
      .forEach((w) => {
        WorshipController.daysOfMonthAsEvent(w, month, year).forEach((e) =>
          result.push(e)
        );
      });
    return result;
  }

  public static searchAsEventMonthAndPreviousAndNext(
    month: number,
    year: number,
    onlyEnabled: boolean
  ): Event[] {
    const list = WorshipController.WORSHIP.filter(
      (w) => !onlyEnabled || w.isActive()
    );
    const result: Event[] = [];

    // month - 1
    const prevDate = new Date(year, month - 1, 1);
    list.forEach((w) => {
      WorshipController.daysOfMonthAsEvent(
        w,
        prevDate.getMonth(),
        prevDate.getFullYear()
      ).forEach((e) => result.push(e));
    });

    // month
    const currDate = new Date(year, month, 1);
    list.forEach((w) => {
      WorshipController.daysOfMonthAsEvent(
        w,
        currDate.getMonth(),
        currDate.getFullYear()
      ).forEach((e) => result.push(e));
    });

    // month + 1
    const nextDate = new Date(year, month + 1, 1);
    list.forEach((w) => {
      WorshipController.daysOfMonthAsEvent(
        w,
        nextDate.getMonth(),
        nextDate.getFullYear()
      ).forEach((e) => result.push(e));
    });

    return result;
  }

  public static save(): void {
    WorshipController.sort();
    ControllerFactory.save(WorshipController.WORSHIP, WorshipController.FILE_NAME);
  }

  public static sort(): void {
    WorshipController.WORSHIP.sort(Worship.sort());
  }

  public static saveList(list: Worship[]): void {
    WorshipController.WORSHIP.length = 0;
    const seen = new Set<number>();
    const distinct: Worship[] = [];
    for (const item of list) {
      if (!seen.has(item.id)) {
        seen.add(item.id);
        distinct.push(item);
      }
    }
    WorshipController.WORSHIP.push(...distinct);
    WorshipController.save();
  }

  public static daysOfMonth(w: Worship, month: number, year: number): Date[] {
    const days = WorshipController.getDaysOfMonth(w, month, year);
    if (w.week.equals(WorshipWeek.ALL)) {
      return days;
    }
    if (w.week.equals(WorshipWeek.LAST)) {
      const last = days[days.length - 1];
      return last ? [last] : [];
    }
    const targetIndex = w.week.ordinal() - 1;
    const target = days[targetIndex];
    return targetIndex >= 0 && targetIndex < days.length && target
      ? [target]
      : [];
  }

  private static getDaysOfMonth(w: Worship, month: number, year: number): Date[] {
    const result: Date[] = [];
    const current = new Date(year, month, 1, w.hour, w.minute, 0, 0);

    while (current.getMonth() === month) {
      if (current.getDay() === w.day.ordinal()) {
        result.push(new Date(current.getTime()));
      }
      current.setDate(current.getDate() + 1);
    }

    return result;
  }

  public static daysOfMonthAsEvent(
    w: Worship,
    month: number,
    year: number
  ): Event[] {
    return WorshipController.daysOfMonth(w, month, year).map(
      (c) => new Event(WorshipController.createId(w, c), w.desc, c, "")
    );
  }

  public static createId(w: Worship, c: Date): string {
    return `${w.id}${ScheduleController.dateToID(c)}`;
  }

  public static setTimeAndDesc(w: Worship, newValues: Worship): void {
    (w as any).hour = newValues.hour;
    (w as any).minute = newValues.minute;
    (w as any).desc = newValues.desc;

    const prefix = String(w.id);
    ScheduleController.getAll()
      .filter((s) => s.id.startsWith(prefix))
      .forEach((s) => {
        const c = s.getDate();
        c.setHours(w.hour, w.minute, 0, 0);
        s.setDate(c);
        (s as any).id = WorshipController.createId(w, c);
      });

    ScheduleController.save();
    WorshipController.save();
  }
}

export default WorshipController;
