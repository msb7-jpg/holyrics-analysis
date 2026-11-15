import ClockSettings from "../model/ClockSettings";

export interface ClockSettingsUtils {
  getInstance(): ClockSettings;
  update?(): void;
  getLineHeight?(h: number): number;
  getKey?(s: string, show: boolean): string;
  isTop?(): boolean;
  isMiddle?(): boolean;
  isBottom?(): boolean;
  isLeft?(): boolean;
  isCenter?(): boolean;
  isRight?(): boolean;
}

export namespace ClockSettingsUtils {
  export function getTime(): string {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, "0");
    const minutes = String(now.getMinutes()).padStart(2, "0");
    return `${hours}:${minutes}`;
  }

  export function getLineHeight(settings: ClockSettings, h: number): number {
    try {
      return Math.trunc((h * settings.getLineHeight()) / 100.0);
    } catch {
      return Math.trunc(h * 0.08);
    }
  }

  export function isTop(position: number): boolean {
    return position >= 0 && position <= 2;
  }

  export function isMiddle(position: number): boolean {
    return position >= 3 && position <= 5;
  }

  export function isBottom(position: number): boolean {
    return position >= 6 && position <= 8;
  }

  export function isLeft(position: number): boolean {
    return position === 0 || position === 3 || position === 6;
  }

  export function isCenter(position: number): boolean {
    return position === 1 || position === 4 || position === 7;
  }

  export function isRight(position: number): boolean {
    return position === 2 || position === 5 || position === 8;
  }
}

export default ClockSettingsUtils;
