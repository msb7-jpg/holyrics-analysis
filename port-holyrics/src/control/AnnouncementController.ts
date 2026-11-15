import fs from 'node:fs';
import path from 'node:path';

export interface ImageHL {
  bytes?: Uint8Array | number[] | null;
  bytesThumbs?: Uint8Array | number[] | null;
  path?: string;
  [key: string]: any;
}

export interface BackgroundAnnouncement {
  imageHL?: ImageHL;
  getImageHL?: () => ImageHL;
  [key: string]: any;
}

export interface Theme {
  background?: BackgroundAnnouncement | any;
  getBackground?: () => any;
  [key: string]: any;
}

export interface Announcement {
  id: number | string;
  name?: string;
  theme?: Theme;
  getTheme?: () => Theme;
  [key: string]: any;
}

export class AnnouncementController {
  private static readonly FILE_NAME = 'announcement.hly';
  private static ANNOUNCEMENTS: Announcement[] = [];
  private static dataDir: string = path.resolve(process.cwd(), 'data');

  static {
    AnnouncementController.load();
  }

  public static setDataDir(dir: string): void {
    AnnouncementController.dataDir = dir;
    AnnouncementController.load();
  }

  public static getFilePath(): string {
    return path.join(AnnouncementController.dataDir, AnnouncementController.FILE_NAME);
  }

  public static load(): Announcement[] {
    try {
      const filePath = AnnouncementController.getFilePath();
      if (fs.existsSync(filePath)) {
        const content = fs.readFileSync(filePath, 'utf-8');
        AnnouncementController.ANNOUNCEMENTS = JSON.parse(content) as Announcement[];
      } else {
        const jsonPath = path.join(AnnouncementController.dataDir, 'announcement.json');
        if (fs.existsSync(jsonPath)) {
          const content = fs.readFileSync(jsonPath, 'utf-8');
          AnnouncementController.ANNOUNCEMENTS = JSON.parse(content) as Announcement[];
        }
      }
    } catch {
      AnnouncementController.ANNOUNCEMENTS = [];
    }
    return AnnouncementController.ANNOUNCEMENTS;
  }

  public static add(a: Announcement): void {
    const exists = AnnouncementController.ANNOUNCEMENTS.some(
      (item) => item === a || item.id === a.id
    );

    if (!exists) {
      AnnouncementController.ANNOUNCEMENTS.push(a);
      AnnouncementController.save();
    }
  }

  public static remove(a: Announcement): void {
    const index = AnnouncementController.ANNOUNCEMENTS.findIndex(
      (item) => item === a || item.id === a.id
    );

    if (index !== -1) {
      const removed = AnnouncementController.ANNOUNCEMENTS.splice(index, 1)[0] || a;
      const theme = removed.getTheme ? removed.getTheme() : removed.theme;
      const bg = theme ? (theme.getBackground ? theme.getBackground() : theme.background) : null;

      if (bg) {
        const imageHL = bg.getImageHL ? bg.getImageHL() : bg.imageHL;
        if (imageHL) {
          imageHL.bytes = new Uint8Array(0);
          imageHL.bytesThumbs = new Uint8Array(0);
        }
      }

      AnnouncementController.save();
    }
  }

  public static getAll(): Announcement[] {
    return [...AnnouncementController.ANNOUNCEMENTS];
  }

  public static find(id: number | string): Announcement | null {
    for (const an of AnnouncementController.getAll()) {
      if (an.id === id || String(an.id) === String(id)) {
        return an;
      }
    }
    return null;
  }

  public static save(): void {
    try {
      if (!fs.existsSync(AnnouncementController.dataDir)) {
        fs.mkdirSync(AnnouncementController.dataDir, { recursive: true });
      }
      const filePath = AnnouncementController.getFilePath();
      fs.writeFileSync(filePath, JSON.stringify(AnnouncementController.ANNOUNCEMENTS, null, 2), 'utf-8');
    } catch {
      // In-memory fallback if file system is inaccessible
    }
  }

  public static set(tabsOrList: any[] | { getTabCount: () => number; getTabComponentAt: (i: number) => any }): void {
    try {
      const list: Announcement[] = [];

      if (Array.isArray(tabsOrList)) {
        for (const item of tabsOrList) {
          if (item && typeof item === 'object') {
            const announcement = item.getAnnouncement ? item.getAnnouncement() : item.announcement || item;
            if (announcement) {
              list.push(announcement);
            }
          }
        }
      } else if (tabsOrList && typeof tabsOrList.getTabCount === 'function') {
        const count = tabsOrList.getTabCount();
        for (let i = 0; i < count; i++) {
          const tab = tabsOrList.getTabComponentAt(i);
          if (tab && typeof tab.getAnnouncement === 'function') {
            list.push(tab.getAnnouncement());
          } else if (tab && tab.announcement) {
            list.push(tab.announcement);
          }
        }
      }

      AnnouncementController.ANNOUNCEMENTS = list;
      AnnouncementController.save();
    } catch {
      // ignore error
    }
  }
}

export default AnnouncementController;
