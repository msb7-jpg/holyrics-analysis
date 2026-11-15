import fs from 'node:fs';
import path from 'node:path';

export interface AlertSchedule {
  hour?: number;
  min?: number;
  totalMinute?: number;
  getTotalMinute?: () => number;
  [key: string]: any;
}

export interface Alert {
  id?: string | number;
  text?: string;
  schedules?: AlertSchedule[];
  schedulesFiltered?: AlertSchedule[];
  getText?: () => string;
  getSchedulesFiltered?: () => AlertSchedule[];
  [key: string]: any;
}

export type AlertNotificationListener = (alert: Alert) => void;

export class AlertController {
  private static readonly SCHEDULED_ALERTS_TEMP: Map<Alert | string, number[]> = new Map();
  private static readonly FILE_NAME = 'alerts.hly';
  private static ALERTS: Alert[] = [];
  private static listeners: AlertNotificationListener[] = [];
  private static dataDir: string = path.resolve(process.cwd(), 'data');

  static {
    AlertController.load();
  }

  public static setDataDir(dir: string): void {
    AlertController.dataDir = dir;
    AlertController.load();
  }

  public static getFilePath(): string {
    return path.join(AlertController.dataDir, AlertController.FILE_NAME);
  }

  public static load(): Alert[] {
    try {
      const filePath = AlertController.getFilePath();
      if (fs.existsSync(filePath)) {
        const content = fs.readFileSync(filePath, 'utf-8');
        AlertController.ALERTS = JSON.parse(content) as Alert[];
      } else {
        const jsonPath = path.join(AlertController.dataDir, 'alerts.json');
        if (fs.existsSync(jsonPath)) {
          const content = fs.readFileSync(jsonPath, 'utf-8');
          AlertController.ALERTS = JSON.parse(content) as Alert[];
        }
      }
    } catch {
      AlertController.ALERTS = [];
    }
    return AlertController.ALERTS;
  }

  public static fireMinuteEvent(min: number): void {
    const currentAlerts = AlertController.getCurrentAlerts(min);
    currentAlerts.forEach((alert) => AlertController.showNotification(alert));
  }

  public static add(a: Alert): Alert {
    const exists = AlertController.ALERTS.some((item) => {
      if (item === a) return true;
      if (item.id !== undefined && a.id !== undefined && item.id === a.id) return true;
      const textA = a.getText ? a.getText() : a.text;
      const textItem = item.getText ? item.getText() : item.text;
      return textA === textItem;
    });

    if (!exists) {
      AlertController.ALERTS.push(a);
      AlertController.save();
    }

    return a;
  }

  public static remove(a: Alert): Alert {
    const index = AlertController.ALERTS.findIndex((item) => {
      if (item === a) return true;
      if (item.id !== undefined && a.id !== undefined && item.id === a.id) return true;
      const textA = a.getText ? a.getText() : a.text;
      const textItem = item.getText ? item.getText() : item.text;
      return textA === textItem;
    });

    if (index !== -1) {
      AlertController.ALERTS.splice(index, 1);
      AlertController.save();
    }
    return a;
  }

  public static getAll(): Alert[] {
    return [...AlertController.ALERTS];
  }

  public static save(): void {
    try {
      if (!fs.existsSync(AlertController.dataDir)) {
        fs.mkdirSync(AlertController.dataDir, { recursive: true });
      }
      const filePath = AlertController.getFilePath();
      fs.writeFileSync(filePath, JSON.stringify(AlertController.ALERTS, null, 2), 'utf-8');
    } catch {
      // In-memory fallback if file system is inaccessible
    }
  }

  public static addAlertScheduleTemp(a: Alert, hour: number, min: number): void {
    const totalMinute = hour * 60 + min;
    const key = a;
    const list = AlertController.SCHEDULED_ALERTS_TEMP.get(key) || [];
    if (!list.includes(totalMinute)) {
      list.push(totalMinute);
    }
    AlertController.SCHEDULED_ALERTS_TEMP.set(key, list);
  }

  private static getCurrentAlerts(totalMinute: number): Alert[] {
    const result: Alert[] = [];

    AlertController.ALERTS.forEach((a) => {
      const schedules = a.getSchedulesFiltered
        ? a.getSchedulesFiltered()
        : a.schedulesFiltered || a.schedules || [];

      schedules.forEach((as) => {
        const scheduleMin = as.getTotalMinute ? as.getTotalMinute() : as.totalMinute;
        if (scheduleMin === totalMinute) {
          result.push(a);
        }
      });
    });

    AlertController.SCHEDULED_ALERTS_TEMP.forEach((minutes, key) => {
      const idx = minutes.indexOf(totalMinute);
      if (idx !== -1) {
        if (typeof key !== 'string') {
          result.push(key);
        }
        minutes.splice(idx, 1);
      }
    });

    return result;
  }

  public static addNotificationListener(listener: AlertNotificationListener): void {
    AlertController.listeners.push(listener);
  }

  public static removeNotificationListener(listener: AlertNotificationListener): void {
    const idx = AlertController.listeners.indexOf(listener);
    if (idx !== -1) {
      AlertController.listeners.splice(idx, 1);
    }
  }

  private static showNotification(a: Alert): void {
    try {
      AlertController.listeners.forEach((listener) => {
        try {
          listener(a);
        } catch {
          // ignore notification listener error
        }
      });
    } catch {
      // notification error suppressed as in Java
    }
  }
}

export default AlertController;
