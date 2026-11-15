import { AlertSchedule } from "./AlertSchedule.ts";
import { Utils } from "../util/Utils.ts";
import { AlertController } from "../control/AlertController.ts";

export class Alert {
  public static readonly serialVersionUID = 1;
  public readonly id: number = Utils.generateMillisecondId();
  private text: string = "";
  private readonly schedules: AlertSchedule[] = [];
  private version: number = 1;

  constructor(text?: string) {
    if (text !== undefined) {
      this.setText(text);
    }
  }

  public getText(): string {
    return this.text;
  }

  public setText(text: string | null | undefined): void {
    this.text = text ?? "";
  }

  public addSchedule(alertSchedule: AlertSchedule): void {
    this.schedules.push(alertSchedule);
    this.schedules.sort((a, b) => a.compareTo(b));
  }

  public removeSchedule(alertSchedule: AlertSchedule): void {
    const index = this.schedules.findIndex((s) => s.equals(alertSchedule));
    if (index !== -1) {
      this.schedules.splice(index, 1);
    }
  }

  public getSchedules(): AlertSchedule[] {
    return [...this.schedules];
  }

  public getSchedulesFiltered(): AlertSchedule[] {
    return this.schedules.filter((s) => s.isEnabledAndToday());
  }

  public save(): void {
    AlertController.save();
  }

  public equals(obj: unknown): boolean {
    if (obj instanceof Alert) {
      return obj.id === this.id;
    }
    return obj === this;
  }

  public hashCode(): number {
    return (413 + (this.id ^ (this.id >>> 32))) | 0;
  }
}

export default Alert;
