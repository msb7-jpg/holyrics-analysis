/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.Worship
 */

import { generateMillisecondId } from "./Event.ts";

export class WorshipDay {
  public static readonly SUN = new WorshipDay(0, "SUN", "Domingo");
  public static readonly MON = new WorshipDay(1, "MON", "Segunda");
  public static readonly TUE = new WorshipDay(2, "TUE", "Terça");
  public static readonly WED = new WorshipDay(3, "WED", "Quarta");
  public static readonly THU = new WorshipDay(4, "THU", "Quinta");
  public static readonly FRI = new WorshipDay(5, "FRI", "Sexta");
  public static readonly SAT = new WorshipDay(6, "SAT", "Sábado");

  private constructor(
    public readonly index: number,
    public readonly key: string,
    public readonly label: string
  ) {}

  public ordinal(): number {
    return this.index;
  }

  public toString(): string {
    return this.label;
  }

  public equals(other: unknown): boolean {
    if (other instanceof WorshipDay) {
      return this.index === other.index;
    }
    return false;
  }

  public static values(): WorshipDay[] {
    return [
      WorshipDay.SUN,
      WorshipDay.MON,
      WorshipDay.TUE,
      WorshipDay.WED,
      WorshipDay.THU,
      WorshipDay.FRI,
      WorshipDay.SAT,
    ];
  }

  public static valueOf(name: string): WorshipDay {
    const found = WorshipDay.values().find(
      (v) =>
        v.key.toUpperCase() === name.toUpperCase() ||
        v.label.toUpperCase() === name.toUpperCase()
    );
    return found ?? WorshipDay.SUN;
  }
}

export class WorshipWeek {
  public static readonly ALL = new WorshipWeek(0, "ALL", "Todos");
  public static readonly FIRST = new WorshipWeek(1, "FIRST", "Primeiro(a)");
  public static readonly SECOND = new WorshipWeek(2, "SECOND", "Segundo(a)");
  public static readonly THIRD = new WorshipWeek(3, "THIRD", "Terceiro(a)");
  public static readonly FOURTH = new WorshipWeek(4, "FOURTH", "Quarto(a)");
  public static readonly LAST = new WorshipWeek(5, "LAST", "Último(a)");

  private constructor(
    public readonly index: number,
    public readonly key: string,
    public readonly label: string
  ) {}

  public ordinal(): number {
    return this.index;
  }

  public toString(): string {
    return this.label;
  }

  public equals(other: unknown): boolean {
    if (other instanceof WorshipWeek) {
      return this.index === other.index;
    }
    return false;
  }

  public static values(): WorshipWeek[] {
    return [
      WorshipWeek.ALL,
      WorshipWeek.FIRST,
      WorshipWeek.SECOND,
      WorshipWeek.THIRD,
      WorshipWeek.FOURTH,
      WorshipWeek.LAST,
    ];
  }

  public static valueOf(name: string): WorshipWeek {
    const found = WorshipWeek.values().find(
      (v) =>
        v.key.toUpperCase() === name.toUpperCase() ||
        v.label.toUpperCase() === name.toUpperCase()
    );
    return found ?? WorshipWeek.ALL;
  }
}

export const EnumWorship = {
  EnumWorshipDays: WorshipDay,
  EnumWorshipWeeks: WorshipWeek,
};

export { WorshipDay as EnumWorshipDays, WorshipWeek as EnumWorshipWeeks };

export class Worship {
  public static readonly serialVersionUID = 12491;

  public readonly id: number;
  public readonly day: WorshipDay;
  public readonly week: WorshipWeek;
  public readonly hour: number;
  public readonly minute: number;
  public readonly desc: string;
  private active: boolean;
  private version: number = 1;

  constructor(
    day: WorshipDay,
    week: WorshipWeek,
    hour: number,
    minute: number,
    desc: string = "",
    id?: number
  ) {
    this.id = id ?? generateMillisecondId();
    this.day = day;
    this.week = week;
    this.hour = hour;
    this.minute = minute;
    this.desc = desc ?? "";
    this.active = true;
  }

  public getTime(): string {
    const h = String(this.hour).padStart(2, "0");
    const m = String(this.minute).padStart(2, "0");
    return `${h}:${m}`;
  }

  public isActive(): boolean {
    return this.active;
  }

  public setActive(active: boolean): void {
    this.active = active;
  }

  public getVersion(): number {
    return this.version;
  }

  public setVersion(version: number): void {
    this.version = version;
  }

  public toString(): string {
    const arg1 = !this.week.equals(WorshipWeek.ALL) ? `${this.week.toString()} ` : "";
    const arg2 = this.day.toString();
    const arg3 = this.getTime();
    const arg4 = this.desc.length > 0 ? ` (${this.desc})` : "";
    return `${arg1}${arg2} - ${arg3}${arg4}`;
  }

  public equals(obj: unknown): boolean {
    if (!(obj instanceof Worship)) {
      return false;
    }
    const w = obj as Worship;
    return (
      w.id === this.id ||
      (w.day.equals(this.day) &&
        w.week.equals(this.week) &&
        w.hour === this.hour &&
        w.minute === this.minute)
    );
  }

  public hashCode(): number {
    let hash = 3;
    hash = 59 * hash + (this.id ^ (this.id >>> 32));
    hash = 59 * hash + this.day.ordinal();
    hash = 59 * hash + this.week.ordinal();
    hash = 59 * hash + this.hour;
    hash = 59 * hash + this.minute;
    let descHash = 0;
    for (let i = 0; i < this.desc.length; i++) {
      descHash = ((descHash << 5) - descHash + this.desc.charCodeAt(i)) | 0;
    }
    hash = (59 * hash + descHash) | 0;
    return hash;
  }

  public static sort(): (o1: Worship | null, o2: Worship | null) => number {
    return (o1: Worship | null, o2: Worship | null) => {
      if (o1 === null || o1 === undefined) {
        return o2 === null || o2 === undefined ? 0 : -1;
      } else if (o2 === null || o2 === undefined) {
        return 1;
      } else if (!o1.week.equals(o2.week)) {
        return o1.week.ordinal() - o2.week.ordinal();
      } else if (!o1.day.equals(o2.day)) {
        return o1.day.ordinal() - o2.day.ordinal();
      } else {
        return o1.hour !== o2.hour ? o1.hour - o2.hour : o1.minute - o2.minute;
      }
    };
  }

  public toJSON(): Record<string, any> {
    return {
      id: this.id,
      day: this.day.key,
      week: this.week.key,
      hour: this.hour,
      minute: this.minute,
      desc: this.desc,
      active: this.active,
      version: this.version,
    };
  }

  public static fromJSON(json: string | Record<string, any>): Worship {
    const data = typeof json === "string" ? JSON.parse(json) : json;
    const day = typeof data.day === "string" ? WorshipDay.valueOf(data.day) : (data.day ?? WorshipDay.SUN);
    const week = typeof data.week === "string" ? WorshipWeek.valueOf(data.week) : (data.week ?? WorshipWeek.ALL);
    const worship = new Worship(
      day,
      week,
      data.hour ?? 0,
      data.minute ?? 0,
      data.desc ?? "",
      data.id
    );
    if (typeof data.active === "boolean") {
      worship.active = data.active;
    }
    if (typeof data.version === "number") {
      worship.version = data.version;
    }
    return worship;
  }
}

export default Worship;
