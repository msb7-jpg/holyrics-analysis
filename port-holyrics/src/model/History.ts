import * as fs from "node:fs";
import * as path from "node:path";
import { Music } from "./Music.ts";

function isToday(d: Date): boolean {
  const now = new Date();
  return (
    d.getDate() === now.getDate() &&
    d.getMonth() === now.getMonth() &&
    d.getFullYear() === now.getFullYear()
  );
}

class HistoryFactory {
  private static readonly FILE_PATH = path.join("Holyrics", "files", "history.json");
  private static readonly PASSWORD = "&p4ssS3sp";

  public static getInstance(): Map<number, Date[]> {
    try {
      if (fs.existsSync(HistoryFactory.FILE_PATH)) {
        const raw = fs.readFileSync(HistoryFactory.FILE_PATH, "utf-8");
        const json = JSON.parse(raw);
        const map = new Map<number, Date[]>();
        for (const [key, dates] of Object.entries(json)) {
          if (Array.isArray(dates)) {
            map.set(Number(key), dates.map((d: string | number | Date) => new Date(d)));
          }
        }
        return map;
      }
    } catch {
      // Fallback on read failure
    }
    return new Map<number, Date[]>();
  }

  public static save(map: Map<number, Date[]>): void {
    try {
      const dir = path.dirname(HistoryFactory.FILE_PATH);
      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
      }
      const obj: Record<string, string[]> = {};
      for (const [key, dates] of map.entries()) {
        obj[key.toString()] = dates.map((d) => d.toISOString());
      }
      fs.writeFileSync(HistoryFactory.FILE_PATH, JSON.stringify(obj, null, 2), "utf-8");
    } catch {
      // Fallback on write failure
    }
  }
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.History
 */
export class History {
  public static readonly serialVersionUID = 12491;
  private static readonly HISTORIES: Map<number, Date[]> = HistoryFactory.getInstance();
  private version: number = 1;

  public static getHistories(): Map<number, Date[]> {
    const copy = new Map<number, Date[]>();
    for (const [key, value] of History.HISTORIES.entries()) {
      copy.set(key, [...value]);
    }
    return copy;
  }

  public static getHistory(m: Music): Date[] {
    const id = m.getId();
    let list = History.HISTORIES.get(id);
    if (!list) {
      list = [];
      History.HISTORIES.set(id, list);
    }
    return list;
  }

  public static addHistory(m: Music, d: Date): void {
    const list = History.getHistory(m);
    list.push(d);
    History.save(m, list);
  }

  public static minutesBetweenLastPlayed(m: Music, d: Date): number {
    const date = History.lastPlayed(m);
    return date == null ? Number.MAX_SAFE_INTEGER : Math.floor((d.getTime() - date.getTime()) / 60000);
  }

  public static lastPlayed(m: Music): Date | null {
    const l = History.getHistory(m);
    return l.length === 0 ? null : (l[l.length - 1] ?? null);
  }

  public static isPlayed(m: Music, d?: Date | null): boolean {
    const list = History.getHistory(m);
    return d != null
      ? list
          .map((i) => Math.floor((d.getTime() - i.getTime()) / 60000))
          .some((diff) => diff <= 30 && diff >= -180)
      : list.some(isToday);
  }

  public static count(m: Music | null): number {
    if (m == null) {
      return 0;
    }
    const list = History.HISTORIES.get(m.getId());
    return list != null ? list.length : 0;
  }

  public static save(): void;
  public static save(music: Music, list: Date[]): void;
  public static save(music?: Music, list?: Date[]): void {
    if (music && list) {
      list.sort((a, b) => a.getTime() - b.getTime());
      History.HISTORIES.set(music.getId(), list);
    }
    HistoryFactory.save(History.HISTORIES);
  }
}

export default History;
