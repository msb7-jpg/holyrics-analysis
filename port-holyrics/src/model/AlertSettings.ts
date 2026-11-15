import { Color, Font } from "../util/awt.ts";
import { Utils } from "../util/Utils.ts";
import { Settings } from "../settings/Settings.ts";
import type { AlertSettingsUtils } from "../util/AlertSettingsUtils.ts";

export class AlertSettings implements AlertSettingsUtils {
  public static readonly serialVersionUID = 1;
  public static readonly LINE_HEIGHT_MIN = 5;
  public static readonly LINE_HEIGHT_MAX = 15;
  public static readonly DURATION_MIN = 3;
  public static readonly DURATION_MAX = 30;
  public static readonly VELOCITY_DEFAULT = 2.0;
  public static readonly VELOCITY_MIN = 0.2;
  public static readonly VELOCITY_MAX = 5.0;
  public static readonly VELOCITY_STEP = 0.2;
  public static readonly FRAME_SKIP_DEFAULT = 15;
  public static readonly FRAME_SKIP_MIN = 0;
  public static readonly FRAME_SKIP_MAX = 60;

  private text: string = "";
  private font: Font = new Font("Arial", Font.PLAIN, 8);
  private background: Color = Color.BLACK;
  private backgroundEnabled: boolean = true;
  private foreground: Color = Color.WHITE;
  private lineHeight: number = 7;
  private duration: number = 7;
  private bottom: boolean = true;
  private overlap: boolean = true;
  private velocity: number = 2.0;
  private frameSkip: number = 15;
  private show: boolean = false;
  private version: number = 3;

  public isShow(): boolean {
    return this.show;
  }

  public setShow(show: boolean): void {
    this.show = show;
  }

  public getFullText(): string {
    return this.text;
  }

  public setText(text: string): void {
    this.text = text ?? "";
  }

  public getFont(size?: number): Font {
    if (size !== undefined) {
      return this.font.deriveFontSize(size);
    }
    return this.font;
  }

  public getFontName(): string {
    return this.font.getName();
  }

  public setFontName(fontName: string): void {
    this.font = new Font(fontName, this.font.getStyle(), this.font.getSize());
  }

  public setBold(bold: boolean): void {
    this.font = this.font.deriveFont(bold ? Font.BOLD : Font.PLAIN);
  }

  public getBackground(): Color {
    return this.background;
  }

  public getBackgroundToPaint(): Color {
    const c = this.background;
    const transparency = !this.backgroundEnabled && this.isOverlap() ? 0 : 255;
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), transparency);
  }

  public setBackground(background: Color): void;
  public setBackground(backgroundEnabled: boolean): void;
  public setBackground(value: Color | boolean): void {
    if (typeof value === "boolean") {
      this.backgroundEnabled = value;
    } else {
      this.background = value;
    }
  }

  public isBackground(): boolean {
    return this.backgroundEnabled;
  }

  public setBackgroundEnabled(backgroundEnabled: boolean): void {
    this.backgroundEnabled = backgroundEnabled;
  }

  public isBackgroundEnabled(): boolean {
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

  public setLineHeight(lineHeight: number | string): void {
    if (typeof lineHeight === "string") {
      try {
        const parsed = parseInt(lineHeight.replace(/[^\d]/g, ""), 10);
        if (!isNaN(parsed)) {
          this.setLineHeight(parsed);
        }
      } catch {
        // ignore
      }
      return;
    }
    this.lineHeight = Utils.range(lineHeight, AlertSettings.LINE_HEIGHT_MIN, AlertSettings.LINE_HEIGHT_MAX);
  }

  public getDuration(): number {
    return 7;
  }

  public setDuration(_duration: number): void {
    this.duration = 7;
  }

  public isBottom(): boolean {
    return this.bottom;
  }

  public setBottom(bottom: boolean): void {
    this.bottom = bottom;
  }

  public isTop(): boolean {
    return !this.bottom;
  }

  public setTop(top: boolean): void {
    this.bottom = !top;
  }

  public isOverlap(): boolean {
    return this.overlap;
  }

  public setOverlap(overlap: boolean): void {
    this.overlap = overlap;
  }

  public getVelocity(): number {
    return Utils.range(this.velocity, AlertSettings.VELOCITY_MIN, AlertSettings.VELOCITY_MAX);
  }

  public setVelocity(velocity: number): void {
    this.velocity = Utils.range(velocity, AlertSettings.VELOCITY_MIN, AlertSettings.VELOCITY_MAX);
  }

  public getFrameSkip(): number {
    return Utils.range(this.frameSkip, AlertSettings.FRAME_SKIP_MIN, AlertSettings.FRAME_SKIP_MAX);
  }

  public setFrameSkip(frameSkip: number): void {
    this.frameSkip = Utils.range(frameSkip, AlertSettings.FRAME_SKIP_MIN, AlertSettings.FRAME_SKIP_MAX);
  }

  public save(): void {
    Settings.SET.saveTrue();
  }

  public getInstance(): AlertSettings {
    return this;
  }

  public getAlertHeight(h: number): number {
    try {
      return Math.trunc((h * this.getInstance().getLineHeight()) / 100.0);
    } catch {
      return Math.trunc(h * 0.08);
    }
  }
}

export default AlertSettings;
