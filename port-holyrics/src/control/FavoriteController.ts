import fs from 'node:fs';
import path from 'node:path';

export interface Favoritable {
  id?: string;
  getId?: () => string;
  [key: string]: any;
}

export interface FavoriteItem {
  position?: number;
  favorite?: Favoritable;
  getPosition?: () => number;
  setPosition?: (pos: number) => void;
  getFavorite?: () => Favoritable;
  [key: string]: any;
}

export type FavoriteChangeListener = () => void;

export class FavoriteController {
  private static readonly FILE_NAME = 'favorites.hly';
  private static FAVORITES: FavoriteItem[] = [];
  private static changeListeners: FavoriteChangeListener[] = [];
  private static dataDir: string = path.resolve(process.cwd(), 'data');

  static {
    FavoriteController.load();
  }

  public static setDataDir(dir: string): void {
    FavoriteController.dataDir = dir;
    FavoriteController.load();
  }

  public static getFilePath(): string {
    return path.join(FavoriteController.dataDir, FavoriteController.FILE_NAME);
  }

  public static load(): FavoriteItem[] {
    try {
      const filePath = FavoriteController.getFilePath();
      if (fs.existsSync(filePath)) {
        const content = fs.readFileSync(filePath, 'utf-8');
        FavoriteController.FAVORITES = JSON.parse(content) as FavoriteItem[];
      } else {
        const jsonPath = path.join(FavoriteController.dataDir, 'favorites.json');
        if (fs.existsSync(jsonPath)) {
          const content = fs.readFileSync(jsonPath, 'utf-8');
          FavoriteController.FAVORITES = JSON.parse(content) as FavoriteItem[];
        }
      }
    } catch {
      FavoriteController.FAVORITES = [];
    }
    return FavoriteController.FAVORITES;
  }

  private static getFavoriteId(item: FavoriteItem): string | undefined {
    const fav = item.getFavorite ? item.getFavorite() : item.favorite;
    if (fav) {
      return fav.getId ? fav.getId() : fav.id;
    }
    return undefined;
  }

  private static getItemPosition(item: FavoriteItem): number {
    if (typeof item.getPosition === 'function') {
      return item.getPosition();
    }
    return item.position ?? 0;
  }

  private static setItemPosition(item: FavoriteItem, pos: number): void {
    if (typeof item.setPosition === 'function') {
      item.setPosition(pos);
    } else {
      item.position = pos;
    }
  }

  public static add(f: FavoriteItem, start: boolean = false): void {
    const targetId = FavoriteController.getFavoriteId(f);
    const exists = FavoriteController.FAVORITES.some((item) => {
      if (item === f) return true;
      const itemId = FavoriteController.getFavoriteId(item);
      return targetId !== undefined && itemId !== undefined && itemId === targetId;
    });

    if (!exists) {
      const positions = FavoriteController.FAVORITES.map((item) => FavoriteController.getItemPosition(item));
      if (positions.length === 0) {
        FavoriteController.setItemPosition(f, 0);
      } else {
        const min = Math.min(...positions);
        const max = Math.max(...positions);
        FavoriteController.setItemPosition(f, start ? min - 1 : max + 1);
      }

      FavoriteController.FAVORITES.push(f);
      FavoriteController.save();
    }
  }

  public static remove(f: FavoriteItem): void {
    const targetId = FavoriteController.getFavoriteId(f);
    const index = FavoriteController.FAVORITES.findIndex((item) => {
      if (item === f) return true;
      const itemId = FavoriteController.getFavoriteId(item);
      return targetId !== undefined && itemId !== undefined && itemId === targetId;
    });

    if (index !== -1) {
      FavoriteController.FAVORITES.splice(index, 1);
      FavoriteController.save();
    }
  }

  public static clear(): void {
    FavoriteController.FAVORITES = [];
    FavoriteController.save();
  }

  public static getAll(): FavoriteItem[] {
    return [...FavoriteController.FAVORITES];
  }

  public static save(): void {
    try {
      if (!fs.existsSync(FavoriteController.dataDir)) {
        fs.mkdirSync(FavoriteController.dataDir, { recursive: true });
      }
      const filePath = FavoriteController.getFilePath();
      fs.writeFileSync(filePath, JSON.stringify(FavoriteController.FAVORITES, null, 2), 'utf-8');
    } catch {
      // In-memory fallback
    }

    FavoriteController.fireChangeListeners();
  }

  public static get(id: string): FavoriteItem | null {
    for (let i = 0; i < FavoriteController.FAVORITES.length; i++) {
      const item = FavoriteController.FAVORITES[i];
      if (item && FavoriteController.getFavoriteId(item) === id) {
        return item;
      }
    }
    return null;
  }

  public static contains(idOrItem: string | Favoritable): boolean {
    const id = typeof idOrItem === 'string'
      ? idOrItem
      : (idOrItem.getId ? idOrItem.getId() : idOrItem.id);

    if (id === undefined) return false;

    for (let i = 0; i < FavoriteController.FAVORITES.length; i++) {
      const item = FavoriteController.FAVORITES[i];
      if (item && FavoriteController.getFavoriteId(item) === id) {
        return true;
      }
    }

    return false;
  }

  public static addChangeListener(listener: FavoriteChangeListener): void {
    FavoriteController.changeListeners.push(listener);
  }

  public static removeChangeListener(listener: FavoriteChangeListener): void {
    const idx = FavoriteController.changeListeners.indexOf(listener);
    if (idx !== -1) {
      FavoriteController.changeListeners.splice(idx, 1);
    }
  }

  private static fireChangeListeners(): void {
    FavoriteController.changeListeners.forEach((listener) => {
      try {
        listener();
      } catch {
        // ignore listener error
      }
    });
  }
}

export default FavoriteController;
