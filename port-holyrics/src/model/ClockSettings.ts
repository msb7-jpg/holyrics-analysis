/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.ClockSettings
 */

export class Color {
  public static readonly BLACK = new Color(0, 0, 0, 255);
  public static readonly RED = new Color(255, 0, 0, 255);
  public static readonly WHITE = new Color(255, 255, 255, 255);

  private r: number;
  private g: number;
  private b: number;
  private a: number;

  constructor(r: number, g: number, b: number, a: number = 255) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }

  public getRed(): number {
    return this.r;
  }

  public getGreen(): number {
    return this.g;
  }

  public getBlue(): number {
    return this.b;
  }

  public getAlpha(): number {
    return this.a;
  }

  public toString(): string {
    return this.a === 255
      ? `rgb(${this.r}, ${this.g}, ${this.b})`
      : `rgba(${this.r}, ${this.g}, ${this.b}, ${this.a / 255})`;
  }
}

export class Font {
  private name: string;
  private style: number;
  private size: number;

  constructor(name: string, style: number = 0, size: number = 8) {
    this.name = name;
    this.style = style;
    this.size = size;
  }

  public getName(): string {
    return this.name;
  }

  public getStyle(): number {
    return this.style;
  }

  public getSize(): number {
    return this.size;
  }

  public isBold(): boolean {
    return (this.style & 1) !== 0;
  }

  public isItalic(): boolean {
    return (this.style & 2) !== 0;
  }

  public deriveFont(size: number): Font {
    return new Font(this.name, this.style, size);
  }
}

export const Utils = {
  range(value: number, min: number, max: number): number {
    return Math.max(min, Math.min(max, value));
  },
};

export class ClockSettings {
  public static readonly serialVersionUID = 1;
  public static readonly FONT: Font = new Font("digital-7_mono", 0, 8);
  public static readonly LINE_HEIGHT_MIN = 6;
  public static final_LINE_HEIGHT_MAX = 50;
  public static readonly LINE_HEIGHT_MAX = 50;

  private background: Color = Color.BLACK;
  private backgroundEnabled: boolean = true;
  private foreground: Color = Color.RED;
  private lineHeight: number = 12;
  private position: number = 2;
  private show: boolean = false;
  private version: number = 2;

  public isShow(): boolean {
    return this.show;
  }

  public setShow(show: boolean): void {
    this.show = show;
    this.update();
  }

  public getBackground(): Color {
    return this.background;
  }

  public getBackgroundToPaint(): Color {
    const c = this.background;
    return this.backgroundEnabled
      ? this.background
      : new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
  }

  public setBackground(background: Color): void;
  public setBackground(backgroundEnabled: boolean): void;
  public setBackground(arg: Color | boolean): void {
    if (typeof arg === "boolean") {
      this.backgroundEnabled = arg;
    } else {
      this.background = arg;
    }
  }

  public isBackground(): boolean {
    return this.backgroundEnabled;
  }

  public getForeground(): Color {
    return this.foreground;
  }

  public setForeground(foreground: Color): void {
    this.foreground = foreground;
  }

  public getLineHeight(): number {
    return this.lineHeight;
  }

  public setLineHeight(lineHeight: number): void;
  public setLineHeight(lineHeight: string): void;
  public setLineHeight(lineHeight: number | string): void {
    if (typeof lineHeight === "string") {
      try {
        const parsed = parseInt(lineHeight.replace(/[^\d]/g, ""), 10);
        if (!isNaN(parsed)) {
          this.setLineHeight(parsed);
        }
      } catch {
        // ignore parse error
      }
    } else {
      this.lineHeight = Utils.range(lineHeight, ClockSettings.LINE_HEIGHT_MIN, ClockSettings.LINE_HEIGHT_MAX);
    }
  }

  public getPosition(): number {
    return this.position;
  }

  public setPosition(position: number): void {
    this.position = position;
  }

  public update(): void {
    // Overridden or attached via ClockSettingsUtils
  }

  public save(): void {
    // Settings.SET.saveTrue();
  }

  public getInstance(): ClockSettings {
    return this;
  }

  public static getFont(size: number): Font {
    return ClockSettings.FONT.deriveFont(size);
  }
}

export default ClockSettings;
