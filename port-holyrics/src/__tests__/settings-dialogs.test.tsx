import { describe, it, expect } from 'bun:test';
import React from 'react';
import ClockSettings from '../model/ClockSettings';
import AlertSettings from '../model/AlertSettings';
import ClockSettingsUtils from '../util/ClockSettingsUtils';
import ClockSettingsDialog from '../view/ClockSettingsDialog';
import AlertSettingsDialog from '../view/AlertSettingsDialog';
import SettingsView from '../view/SettingsView';

describe('Settings Dialogs and Utils', () => {
  it('ClockSettingsUtils produces correct time format', () => {
    const time = ClockSettingsUtils.getTime();
    expect(time).toMatch(/^\d{2}:\d{2}$/);
  });

  it('ClockSettingsUtils position helpers work as expected', () => {
    expect(ClockSettingsUtils.isTop(0)).toBe(true);
    expect(ClockSettingsUtils.isTop(1)).toBe(true);
    expect(ClockSettingsUtils.isTop(2)).toBe(true);
    expect(ClockSettingsUtils.isTop(3)).toBe(false);

    expect(ClockSettingsUtils.isMiddle(4)).toBe(true);
    expect(ClockSettingsUtils.isBottom(8)).toBe(true);

    expect(ClockSettingsUtils.isLeft(0)).toBe(true);
    expect(ClockSettingsUtils.isLeft(3)).toBe(true);
    expect(ClockSettingsUtils.isCenter(4)).toBe(true);
    expect(ClockSettingsUtils.isRight(5)).toBe(true);
  });

  it('ClockSettingsDialog and AlertSettingsDialog are defined React components', () => {
    expect(typeof ClockSettingsDialog).toBe('function');
    expect(typeof AlertSettingsDialog).toBe('function');
    expect(typeof SettingsView).toBe('function');
  });

  it('ClockSettings model can be instantiated and modified', () => {
    const clock = new ClockSettings();
    clock.setPosition(4);
    clock.setLineHeight('20%');
    clock.setBackground(false);
    expect(clock.getPosition()).toBe(4);
    expect(clock.getLineHeight()).toBe(20);
    expect(clock.isBackground()).toBe(false);
  });

  it('AlertSettings model can be instantiated and modified', () => {
    const alert = new AlertSettings();
    alert.setFontName('Courier New');
    alert.setBold(true);
    alert.setVelocity(3.2);
    alert.setTop(true);
    expect(alert.getFontName()).toBe('Courier New');
    expect(alert.getFont().isBold()).toBe(true);
    expect(alert.getVelocity()).toBe(3.2);
    expect(alert.isTop()).toBe(true);
  });
});
