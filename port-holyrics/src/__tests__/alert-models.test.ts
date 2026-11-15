import { describe, it, expect } from "bun:test";
import { Alert } from "../model/Alert.ts";
import { AlertSchedule } from "../model/AlertSchedule.ts";
import { AlertSettings } from "../model/AlertSettings.ts";
import { Color, Font } from "../util/awt.ts";

describe("Alert models", () => {
  it("Alert properties and methods", () => {
    const alert = new Alert("Notice 1");
    expect(alert.getText()).toBe("Notice 1");

    alert.setText("Updated Notice");
    expect(alert.getText()).toBe("Updated Notice");

    alert.setText(null);
    expect(alert.getText()).toBe("");

    const s1 = new AlertSchedule(true, 10, 30);
    const s2 = new AlertSchedule(true, 8, 15);
    alert.addSchedule(s1);
    alert.addSchedule(s2);

    const schedules = alert.getSchedules();
    expect(schedules.length).toBe(2);
    // Should be sorted by total minute
    expect(schedules[0]!.getTotalMinute()).toBe(8 * 60 + 15);
    expect(schedules[1]!.getTotalMinute()).toBe(10 * 60 + 30);

    alert.removeSchedule(s1);
    expect(alert.getSchedules().length).toBe(1);
    expect(alert.getSchedules()[0]!.getTotalMinute()).toBe(8 * 60 + 15);

    expect(alert.equals(alert)).toBe(true);
    expect(alert.equals(new Alert("Other"))).toBe(false);
    expect(typeof alert.hashCode()).toBe("number");
    expect(() => alert.save()).not.toThrow();
  });

  it("AlertSchedule properties and methods", () => {
    const schedule = new AlertSchedule(true, 14, 45);
    expect(schedule.isEnabled()).toBe(true);
    expect(schedule.getHour()).toBe(14);
    expect(schedule.getMinute()).toBe(45);
    expect(schedule.getTotalMinute()).toBe(14 * 60 + 45);
    expect(schedule.getFormatedTime()).toBe("14:45");
    expect(schedule.getFormattedTime()).toBe("14:45");

    // Clamping
    schedule.setHour(25);
    expect(schedule.getHour()).toBe(23);
    schedule.setHour(-5);
    expect(schedule.getHour()).toBe(0);

    schedule.setMinute(70);
    expect(schedule.getMinute()).toBe(59);
    schedule.setMinute(-10);
    expect(schedule.getMinute()).toBe(0);

    // Days (1 = Sun, 7 = Sat)
    schedule.setDay(1, true);
    schedule.setDay(2, false);
    schedule.setDay(8, true); // out of range, ignored
    expect(schedule.isDay(1)).toBe(true);
    expect(schedule.isDay(2)).toBe(false);
    expect(schedule.isDay(3)).toBe(false);
    expect(schedule.isDay(8)).toBe(false);

    // isEnabledAndToday
    const today = new Date().getDay() + 1;
    schedule.setDay(today, true);
    schedule.setEnabled(true);
    expect(schedule.isEnabledAndToday()).toBe(true);
    schedule.setEnabled(false);
    expect(schedule.isEnabledAndToday()).toBe(false);

    expect(schedule.equals(schedule)).toBe(true);
    expect(schedule.equals(new AlertSchedule(true, 1, 1))).toBe(false);
    expect(typeof schedule.hashCode()).toBe("number");
  });

  it("AlertSettings properties and methods", () => {
    const settings = new AlertSettings();
    expect(settings.getFullText()).toBe("");
    settings.setText("Broadcast Alert");
    expect(settings.getFullText()).toBe("Broadcast Alert");

    // Font
    expect(settings.getFontName()).toBe("Arial");
    settings.setFontName("Helvetica");
    expect(settings.getFontName()).toBe("Helvetica");
    settings.setBold(true);
    expect(settings.getFont().isBold()).toBe(true);
    const derived = settings.getFont(14);
    expect(derived.getSize()).toBe(14);

    // Color and Background
    expect(settings.getBackground().equals(Color.BLACK)).toBe(true);
    expect(settings.getForeground().equals(Color.WHITE)).toBe(true);

    const blue = new Color(0, 0, 255);
    settings.setBackground(blue);
    expect(settings.getBackground().equals(blue)).toBe(true);

    const yellow = new Color(255, 255, 0);
    settings.setForeground(yellow);
    expect(settings.getForeground().equals(yellow)).toBe(true);

    settings.setBackground(false);
    expect(settings.isBackground()).toBe(false);
    expect(settings.isBackgroundEnabled()).toBe(false);

    // Background to paint transparency
    settings.setOverlap(true);
    const bgPaint = settings.getBackgroundToPaint();
    expect(bgPaint.getAlpha()).toBe(0);

    settings.setBackground(true);
    const bgPaintOpaque = settings.getBackgroundToPaint();
    expect(bgPaintOpaque.getAlpha()).toBe(255);

    // LineHeight clamping (5 to 15)
    settings.setLineHeight(3);
    expect(settings.getLineHeight()).toBe(5);
    settings.setLineHeight(20);
    expect(settings.getLineHeight()).toBe(15);
    settings.setLineHeight("12px");
    expect(settings.getLineHeight()).toBe(12);

    // Duration is fixed 7 in Java
    expect(settings.getDuration()).toBe(7);
    settings.setDuration(15);
    expect(settings.getDuration()).toBe(7);

    // Top / Bottom
    expect(settings.isBottom()).toBe(true);
    expect(settings.isTop()).toBe(false);
    settings.setTop(true);
    expect(settings.isTop()).toBe(true);
    expect(settings.isBottom()).toBe(false);

    // Velocity clamping (0.2 to 5.0)
    settings.setVelocity(0.1);
    expect(settings.getVelocity()).toBe(0.2);
    settings.setVelocity(6.0);
    expect(settings.getVelocity()).toBe(5.0);

    // FrameSkip clamping (0 to 60)
    settings.setFrameSkip(-1);
    expect(settings.getFrameSkip()).toBe(0);
    settings.setFrameSkip(100);
    expect(settings.getFrameSkip()).toBe(60);

    // Show
    expect(settings.isShow()).toBe(false);
    settings.setShow(true);
    expect(settings.isShow()).toBe(true);

    // AlertHeight calculation
    settings.setLineHeight(10);
    expect(settings.getAlertHeight(1000)).toBe(100);

    expect(settings.getInstance()).toBe(settings);
    expect(() => settings.save()).not.toThrow();
  });
});
