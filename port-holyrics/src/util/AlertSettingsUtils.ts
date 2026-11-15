import type { AlertSettings } from "../model/AlertSettings.ts";

export interface AlertSettingsUtils {
  getInstance(): AlertSettings;
  getAlertHeight(h: number): number;
}

export function calculateAlertHeight(settings: AlertSettings, h: number): number {
  try {
    return Math.trunc((h * settings.getLineHeight()) / 100.0);
  } catch {
    return Math.trunc(h * 0.08);
  }
}
