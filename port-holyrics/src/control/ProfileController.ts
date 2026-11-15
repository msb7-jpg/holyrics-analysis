import fs from 'node:fs';
import path from 'node:path';

export interface Profile {
  id?: string | number;
  name: string;
  musicsId?: (number | string)[];
  getName?: () => string;
  setName?: (name: string) => void;
  getMusicsId?: () => (number | string)[];
  removeIdsIfNotExists?: (existingIds: (number | string)[]) => void;
  [key: string]: any;
}

export class ProfileImpl implements Profile {
  public id?: string | number;
  public name: string;
  public musicsId: (number | string)[];

  constructor(name: string, musicsId: (number | string)[] = []) {
    this.name = name;
    this.musicsId = musicsId;
  }

  public getName(): string {
    return this.name;
  }

  public setName(name: string): void {
    this.name = name;
  }

  public getMusicsId(): (number | string)[] {
    return this.musicsId;
  }

  public removeIdsIfNotExists(existingIds: (number | string)[]): void {
    const existingSet = new Set(existingIds.map((id) => String(id)));
    this.musicsId = this.musicsId.filter((id) => existingSet.has(String(id)));
  }
}

export class ProfileController {
  private static readonly FILE_NAME = 'profiles.hly';
  private static PROFILES: (Profile | null)[] = [null];
  private static dataDir: string = path.resolve(process.cwd(), 'data');

  static {
    ProfileController.load();
    ProfileController.init();
  }

  public static setDataDir(dir: string): void {
    ProfileController.dataDir = dir;
    ProfileController.load();
    ProfileController.init();
  }

  public static getFilePath(): string {
    return path.join(ProfileController.dataDir, ProfileController.FILE_NAME);
  }

  public static load(): (Profile | null)[] {
    try {
      const filePath = ProfileController.getFilePath();
      if (fs.existsSync(filePath)) {
        const content = fs.readFileSync(filePath, 'utf-8');
        ProfileController.PROFILES = JSON.parse(content) as (Profile | null)[];
      } else {
        const jsonPath = path.join(ProfileController.dataDir, 'profiles.json');
        if (fs.existsSync(jsonPath)) {
          const content = fs.readFileSync(jsonPath, 'utf-8');
          ProfileController.PROFILES = JSON.parse(content) as (Profile | null)[];
        }
      }
    } catch {
      ProfileController.PROFILES = [null];
    }

    if (!ProfileController.PROFILES.includes(null)) {
      ProfileController.PROFILES.unshift(null);
    }

    return ProfileController.PROFILES;
  }

  private static init(): void {
    if (!ProfileController.PROFILES.includes(null)) {
      ProfileController.PROFILES.unshift(null);
    }
  }

  public static add(p: Profile): void {
    const pName = p.getName ? p.getName() : p.name;
    const exists = ProfileController.PROFILES.some((item) => {
      if (!item) return false;
      if (item === p) return true;
      const itemName = item.getName ? item.getName() : item.name;
      return itemName === pName;
    });

    if (!exists) {
      ProfileController.PROFILES.push(p);
      ProfileController.save();
    }
  }

  public static remove(p: Profile): void {
    const pName = p.getName ? p.getName() : p.name;
    const index = ProfileController.PROFILES.findIndex((item) => {
      if (!item) return false;
      if (item === p) return true;
      const itemName = item.getName ? item.getName() : item.name;
      return itemName === pName;
    });

    if (index !== -1) {
      ProfileController.PROFILES.splice(index, 1);
      ProfileController.save();
    }
  }

  public static create(defaultName: string = 'Novo perfil'): Profile {
    if (!ProfileController.contains(defaultName)) {
      return new ProfileImpl(defaultName);
    }

    let i = 0;
    let candidate = '';
    do {
      i++;
      candidate = `${defaultName} (${i})`;
    } while (ProfileController.contains(candidate));

    return new ProfileImpl(candidate);
  }

  public static getProfiles(): Profile[] {
    return ProfileController.PROFILES.filter((p): p is Profile => p !== null && p !== undefined);
  }

  public static getProfilesInOrderOfAmount(nullable: boolean = false): (Profile | null)[] {
    const list = nullable ? [...ProfileController.PROFILES] : ProfileController.getProfiles();

    return list.sort((p1, p2) => {
      if (p1 === null || p1 === undefined) return Number.MIN_SAFE_INTEGER;
      if (p2 === null || p2 === undefined) return Number.MAX_SAFE_INTEGER;

      const p1Size = p1.getMusicsId ? p1.getMusicsId().length : (p1.musicsId ? p1.musicsId.length : 0);
      const p2Size = p2.getMusicsId ? p2.getMusicsId().length : (p2.musicsId ? p2.musicsId.length : 0);

      return p2Size - p1Size;
    });
  }

  public static save(list?: (Profile | null)[]): void {
    if (list !== undefined) {
      ProfileController.PROFILES = [];
      const distinctList: (Profile | null)[] = [];
      for (const item of list) {
        if (item === null) {
          if (!distinctList.includes(null)) distinctList.push(null);
        } else {
          const name = item.getName ? item.getName() : item.name;
          const exists = distinctList.some((d) => d !== null && (d.getName ? d.getName() : d.name) === name);
          if (!exists) {
            distinctList.push(item);
          }
        }
      }
      ProfileController.PROFILES = distinctList;
    }

    if (!ProfileController.PROFILES.includes(null)) {
      ProfileController.PROFILES.unshift(null);
    }

    try {
      if (!fs.existsSync(ProfileController.dataDir)) {
        fs.mkdirSync(ProfileController.dataDir, { recursive: true });
      }
      const filePath = ProfileController.getFilePath();
      fs.writeFileSync(filePath, JSON.stringify(ProfileController.PROFILES, null, 2), 'utf-8');
    } catch {
      // In-memory fallback
    }
  }

  public static contains(name: string): boolean {
    return ProfileController.getProfiles().some((p) => {
      const pName = p.getName ? p.getName() : p.name;
      return pName === name;
    });
  }

  public static syncWithMusicIds(existingIds: (number | string)[]): void {
    ProfileController.getProfiles().forEach((p) => {
      if (typeof p.removeIdsIfNotExists === 'function') {
        p.removeIdsIfNotExists(existingIds);
      } else if (Array.isArray(p.musicsId)) {
        const idSet = new Set(existingIds.map((id) => String(id)));
        p.musicsId = p.musicsId.filter((id) => idSet.has(String(id)));
      }
    });
  }
}

export default ProfileController;
