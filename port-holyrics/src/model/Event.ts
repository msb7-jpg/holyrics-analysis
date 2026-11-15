/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.Event
 */

let lastMillisecondId = 0;
export function generateMillisecondId(): number {
  const id = Date.now();
  if (id <= lastMillisecondId) {
    return ++lastMillisecondId;
  }
  lastMillisecondId = id;
  return id;
}

export type ScheduleChangeListener = (event: Event, date: Date) => void;

export const ScheduleController = {
  listener: null as ScheduleChangeListener | null,
  change(event: Event, date: Date): void {
    if (this.listener) {
      this.listener(event, date);
    }
  },
  setListener(listener: ScheduleChangeListener | null): void {
    this.listener = listener;
  },
};

export class Event {
  public static readonly serialVersionUID = 12491;

  public readonly id: string;
  private name: string;
  private datetime: number;
  private desc: string;
  private date: Date | null = null;
  private version: number = 1;

  constructor(date: Date);
  constructor(id: string, name: string, date: Date, desc: string);
  constructor(idOrDate: string | Date, name: string = "", date?: Date, desc: string = "") {
    if (idOrDate instanceof Date) {
      this.id = generateMillisecondId().toString();
      this.name = "";
      const d = new Date(idOrDate.getTime());
      d.setSeconds(0, 0);
      this.datetime = d.getTime();
      this.desc = "";
      this.date = d;
    } else {
      this.id = idOrDate;
      this.name = name;
      const targetDate = date ? new Date(date.getTime()) : new Date();
      targetDate.setSeconds(0, 0);
      this.datetime = targetDate.getTime();
      this.desc = desc;
      this.date = targetDate;
    }
  }

  public getId(): string {
    return this.id;
  }

  public getName(): string {
    return this.name;
  }

  public setName(name: string): void {
    this.name = name;
  }

  public getDate(): Date {
    if (this.date === null) {
      this.date = new Date(this.datetime);
    }
    return this.date;
  }

  public setDate(date: Date): void {
    const d = new Date(date.getTime());
    d.setSeconds(0, 0);
    ScheduleController.change(this, d);
    this.date = d;
    this.datetime = d.getTime();
  }

  public getDatetime(): number {
    return this.datetime;
  }

  public getDesc(): string {
    return this.desc;
  }

  public setDesc(desc: string): void {
    this.desc = desc;
  }

  public getVersion(): number {
    return this.version;
  }

  public setVersion(version: number): void {
    this.version = version;
  }

  public toString(): string {
    const d = this.getDate();
    const formatted = d.toLocaleString();
    return `${this.name} - ${formatted}`;
  }

  public static sort(): (o1: Event, o2: Event) => number {
    return (o1: Event, o2: Event) => o1.getDate().getTime() - o2.getDate().getTime();
  }

  public equals(o: unknown): boolean {
    if (o instanceof Event) {
      return o.id.toLowerCase() === this.id.toLowerCase();
    }
    return false;
  }

  public hashCode(): number {
    let hash = 0;
    if (this.id) {
      for (let i = 0; i < this.id.length; i++) {
        hash = (hash << 5) - hash + this.id.charCodeAt(i);
        hash |= 0;
      }
    }
    return 57 + hash;
  }

  public toJSON(): Record<string, any> {
    return {
      id: this.id,
      name: this.name,
      datetime: this.datetime,
      desc: this.desc,
      version: this.version,
    };
  }

  public static fromJSON(json: string | Record<string, any>): Event {
    const data = typeof json === "string" ? JSON.parse(json) : json;
    const date = new Date(typeof data.datetime === "number" ? data.datetime : Date.now());
    const event = new Event(data.id ? String(data.id) : generateMillisecondId().toString(), data.name || "", date, data.desc || "");
    if (typeof data.version === "number") {
      event.version = data.version;
    }
    return event;
  }

  public static create(date: Date): Event;
  public static create(id: string, name: string, date: Date, desc: string): Event;
  public static create(idOrDate: string | Date, name: string = "", date?: Date, desc: string = ""): Event {
    if (idOrDate instanceof Date) {
      return new Event(idOrDate);
    }
    return new Event(idOrDate, name, date ?? new Date(), desc);
  }
}

export default Event;
