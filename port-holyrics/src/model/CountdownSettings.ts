/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.CountdownSettings
 */

export interface Theme {
  id?: number;
  [key: string]: unknown;
}

export const Utils = {
  range(value: number, min: number, max: number): number {
    return Math.max(min, Math.min(max, value));
  },
};

export class CountdownSettings {
  public static readonly serialVersionUID = 1;
  public static readonly SIZE_MIN_VALUE = 100;
  public static readonly SIZE_MAX_VALUE = 800;
  public static readonly SIZE_DEFAULT_VALUE = 250;
  public static readonly BREAK_LINE_TOKEN = "<brtk=t>";

  private textBefore: string = "";
  private textAfter: string = "";
  private determine: boolean = true;
  private minuteDetermine: number = 5;
  private secondDetermine: number = 0;
  private hourExact: number = 19;
  private minuteExact: number = 0;
  private size: number = 250;
  private zeroFill: boolean = false;
  private theme: number = 0;
  private version: number = 1;

  constructor() {
    // Default textAfter can be populated from I18N if available
  }

  public getTextBefore(replaceBreakLine: boolean = false): string {
    return replaceBreakLine
      ? this.textBefore.split("\n").join(CountdownSettings.BREAK_LINE_TOKEN)
      : this.textBefore;
  }

  public setTextBefore(textBefore: string): void {
    this.textBefore = textBefore;
  }

  public getTextAfter(replaceBreakLine: boolean = false): string {
    return replaceBreakLine
      ? this.textAfter.split("\n").join(CountdownSettings.BREAK_LINE_TOKEN)
      : this.textAfter;
  }

  public setTextAfter(textAfter: string): void {
    this.textAfter = textAfter;
  }

  public isDetermine(): boolean {
    return this.determine;
  }

  public setDetermine(determine: boolean): void {
    this.determine = determine;
  }

  public getMinuteDetermine(): number {
    return Utils.range(this.minuteDetermine, 0, 59);
  }

  public setMinuteDetermine(minuteDetermine: number): void {
    this.minuteDetermine = minuteDetermine;
  }

  public getSecondDetermine(): number {
    return Utils.range(this.secondDetermine, 0, 59);
  }

  public setSecondDetermine(secondDetermine: number): void {
    this.secondDetermine = secondDetermine;
  }

  public getHourExact(): number {
    return Utils.range(this.hourExact, 0, 23);
  }

  public setHourExact(hourExact: number): void {
    this.hourExact = hourExact;
  }

  public getMinuteExact(): number {
    return Utils.range(this.minuteExact, 0, 59);
  }

  public setMinuteExact(minuteExact: number): void {
    this.minuteExact = minuteExact;
  }

  public getSize(): number {
    return Utils.range(this.size, CountdownSettings.SIZE_MIN_VALUE, CountdownSettings.SIZE_MAX_VALUE);
  }

  public setSize(size: number): void {
    this.size = size;
  }

  public isZeroFill(): boolean {
    return this.zeroFill;
  }

  public setZeroFill(zeroFill: boolean): void {
    this.zeroFill = zeroFill;
  }

  public getTheme(): number {
    return this.theme;
  }

  public getThemeObject(): Theme | null {
    // Can hook into ThemeController.get(this.getTheme())
    return null;
  }

  public setTheme(theme: number): void {
    this.theme = theme;
  }

  public save(): void {
    // Settings.SET.saveTrue();
  }

  public getInstance(): CountdownSettings {
    return this;
  }
}

export default CountdownSettings;
