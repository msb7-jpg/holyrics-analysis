import { Utils } from "../util/Utils.ts";

export class AlertSchedule {
  public static readonly serialVersionUID = 1;
  public readonly id: number = Utils.generateMillisecondId();
  private enabled: boolean;
  private hour: number = 0;
  private minute: number = 0;
  private readonly days: Map<number, boolean> = new Map();
  private version: number = 1;

  constructor(enabled: boolean = false, hour: number = 0, minute: number = 0) {
    this.enabled = enabled;
    this.setHour(hour);
    this.setMinute(minute);
  }

  public setDay(dayOfWeek: number, flag: boolean): void {
    if (dayOfWeek >= 1 && dayOfWeek <= 7) {
      this.days.set(dayOfWeek, flag);
    }
  }

  public isDay(dayOfWeek: number): boolean {
    return this.days.get(dayOfWeek) ?? false;
  }

  public isEnabled(): boolean {
    return this.enabled;
  }

  public setEnabled(enabled: boolean): void {
    this.enabled = enabled;
  }

  public getHour(): number {
    return this.hour;
  }

  public setHour(hour: number): void {
    this.hour = Utils.range(hour, 0, 23);
  }

  public getMinute(): number {
    return this.minute;
  }

  public setMinute(minute: number): void {
    this.minute = Utils.range(minute, 0, 59);
  }

  public getTotalMinute(): number {
    return this.hour * 60 + this.minute;
  }

  public isEnabledAndToday(): boolean {
    // Java Calendar.DAY_OF_WEEK: Sunday=1, Monday=2, ..., Saturday=7
    // JS Date.getDay(): Sunday=0, Monday=1, ..., Saturday=6
    const dayOfWeek = new Date().getDay() + 1;
    return this.enabled && this.isDay(dayOfWeek);
  }

  public getFormatedTime(): string {
    const h = String(this.hour).padStart(2, "0");
    const m = String(this.minute).padStart(2, "0");
    return `${h}:${m}`;
  }

  public getFormattedTime(): string {
    return this.getFormatedTime();
  }

  public equals(obj: unknown): boolean {
    if (obj instanceof AlertSchedule) {
      return obj.id === this.id;
    }
    return obj === this;
  }

  public hashCode(): number {
    return (427 + (this.id ^ (this.id >>> 32))) | 0;
  }

  public compareTo(o: AlertSchedule): number {
    return this.getTotalMinute() - o.getTotalMinute();
  }
}

export default AlertSchedule;
