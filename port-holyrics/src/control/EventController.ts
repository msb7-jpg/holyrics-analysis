import { ControllerFactory } from "./ControllerFactory.ts";
import { ScheduleController } from "./ScheduleController.ts";
import { Event } from "../model/Event.ts";
import { DateLG } from "../util/DateLG.ts";

/**
 * Revives raw JSON objects into Event instances.
 */
function reviveEvent(data: any): Event {
  if (data instanceof Event) {
    return data;
  }
  const date = data.date
    ? new Date(data.date)
    : data.datetime
    ? new Date(data.datetime)
    : new Date();
  const event = new Event(
    data.id,
    data.name ?? "",
    date,
    data.desc ?? ""
  );
  if (data.version !== undefined) {
    event.setVersion(data.version);
  }
  return event;
}

/**
 * Controller for Event management.
 * Isomorphic TypeScript port of com.limagiran.holyrics.control.EventController
 */
export class EventController {
  private static readonly FILE_NAME: string = "events.hly";
  private static readonly EVENTS: Event[] = ControllerFactory.load<Event>(
    EventController.FILE_NAME,
    reviveEvent
  );

  public static add(e: Event): void {
    const exists = EventController.EVENTS.some((item) =>
      item.equals ? item.equals(e) : item.id.toLowerCase() === e.id.toLowerCase()
    );
    if (!exists) {
      EventController.EVENTS.push(e);
      EventController.save();
    }
  }

  public static remove(e: Event): void {
    const index = EventController.EVENTS.findIndex((item) =>
      item.equals ? item.equals(e) : item.id.toLowerCase() === e.id.toLowerCase()
    );
    if (index !== -1) {
      EventController.EVENTS.splice(index, 1);
      ScheduleController.remove(ScheduleController.get(e));
      EventController.save();
    }
  }

  public static getAll(): Event[] {
    return [...EventController.EVENTS];
  }

  public static search(month: number, year: number): Event[] {
    return EventController.EVENTS.filter((e) => {
      const d = e.getDate();
      return d.getMonth() === month && d.getFullYear() === year;
    });
  }

  public static search3Months(month: number, year: number): Event[] {
    const start = new Date();
    start.setFullYear(year, month, 1);
    start.setMonth(start.getMonth() - 1);

    const end = new Date();
    end.setFullYear(year, month, 1);
    end.setMonth(end.getMonth() + 1);

    return EventController.searchPeriod(start, end);
  }

  public static searchPeriod(start: Date, end: Date): Event[] {
    return EventController.EVENTS.filter((e) =>
      DateLG.isBetween(e.getDate(), start, end)
    );
  }

  public static searchByDay(events: Event[], date: Date): Event[] {
    const targetDay = date.getDate();
    const targetMonth = date.getMonth();
    const targetYear = date.getFullYear();

    return events.filter((e) => {
      const d = e.getDate();
      return (
        d.getDate() === targetDay &&
        d.getMonth() === targetMonth &&
        d.getFullYear() === targetYear
      );
    });
  }

  public static save(): void {
    ControllerFactory.save(EventController.EVENTS, EventController.FILE_NAME);
  }

  public static saveList(list: Event[]): void {
    EventController.EVENTS.length = 0;
    const seen = new Set<string>();
    const distinct: Event[] = [];
    for (const item of list) {
      const key = item.id.toLowerCase();
      if (!seen.has(key)) {
        seen.add(key);
        distinct.push(item);
      }
    }
    EventController.EVENTS.push(...distinct);
    EventController.save();
  }
}

export default EventController;
