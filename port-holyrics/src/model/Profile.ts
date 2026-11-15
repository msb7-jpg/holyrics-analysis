import { Music } from "./Music.ts";

/**
 * Optional handlers for ProfileController and MusicDao hooks.
 */
let profileControllerSaveHandler: (() => void) | null = null;
let musicDaoGetLazyHandler: ((ids: number[]) => Music[]) | null = null;

export const ProfileController = {
  save(): void {
    if (profileControllerSaveHandler) {
      profileControllerSaveHandler();
    }
  },
  setSaveHandler(fn: () => void): void {
    profileControllerSaveHandler = fn;
  },
};

export const MusicDao = {
  getLazy(ids: number[]): Music[] {
    if (musicDaoGetLazyHandler) {
      return musicDaoGetLazyHandler(ids);
    }
    return [];
  },
  setGetLazyHandler(fn: (ids: number[]) => Music[]): void {
    musicDaoGetLazyHandler = fn;
  },
};

function hashCodeString(str: string): number {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = ((hash << 5) - hash) + str.charCodeAt(i);
    hash |= 0;
  }
  return hash;
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.Profile
 */
export class Profile {
  public static readonly serialVersionUID = 12491;

  private name: string;
  private readonly musics: number[] = [];
  private version: number = 1;

  constructor(name: string = "") {
    this.name = name;
  }

  public getName(): string {
    return this.name;
  }

  public setName(name: string): void {
    this.name = name;
    this.save();
  }

  public getMusicsId(): number[] {
    return [...this.musics];
  }

  public getMusics(): Music[] {
    return MusicDao.getLazy(this.musics);
  }

  public addMusics(musicOrList: Music | Music[]): void {
    if (Array.isArray(musicOrList)) {
      this.addIds(
        musicOrList
          .filter((m) => m != null && !this.musics.includes(m.getId()))
          .map((m) => m.getId())
      );
    } else if (musicOrList != null && !this.musics.includes(musicOrList.getId())) {
      this.musics.push(musicOrList.getId());
      this.save();
    }
  }

  public addIds(list: number[]): void {
    list
      .filter((id) => !this.musics.includes(id))
      .forEach((id) => this.musics.push(id));
    this.save();
  }

  public removeMusic(music: Music | null): void {
    if (music != null) {
      const index = this.musics.indexOf(music.getId());
      if (index !== -1) {
        this.musics.splice(index, 1);
        this.save();
      }
    }
  }

  public removeMusics(list: Music[]): void {
    this.removeIds(list.map((m) => m.getId()));
  }

  public removeIds(list: number[]): void {
    const set = new Set(list);
    for (let i = this.musics.length - 1; i >= 0; i--) {
      const id = this.musics[i];
      if (id !== undefined && set.has(id)) {
        this.musics.splice(i, 1);
      }
    }
    this.save();
  }

  public removeIdsIfNotExists(ids: number[]): void {
    const set = new Set(ids);
    for (let i = this.musics.length - 1; i >= 0; i--) {
      const id = this.musics[i];
      if (id !== undefined && !set.has(id)) {
        this.musics.splice(i, 1);
      }
    }
  }

  public setMusics(ids: number[]): void {
    const idsSet = new Set(ids);
    for (let i = this.musics.length - 1; i >= 0; i--) {
      const id = this.musics[i];
      if (id !== undefined && !idsSet.has(id)) {
        this.musics.splice(i, 1);
      }
    }

    ids
      .filter((m) => !this.musics.includes(m))
      .forEach((m) => this.musics.push(m));
    this.save();
  }

  public containsMusic(m: Music | null): boolean {
    return m != null && this.musics.includes(m.getId());
  }

  public save(): void {
    ProfileController.save();
  }

  public toString(): string {
    return this.name;
  }

  public equals(obj: unknown): boolean {
    if (obj instanceof Profile) {
      return obj.name === this.name;
    }
    return false;
  }

  public hashCode(): number {
    return 679 + hashCodeString(this.name);
  }

  public static sort(): (p1: Profile | null, p2: Profile | null) => number {
    return (p1: Profile | null, p2: Profile | null) =>
      p1 == null
        ? Number.MIN_SAFE_INTEGER
        : p2 == null
        ? Number.MAX_SAFE_INTEGER
        : p2.getMusicsId().length - p1.getMusicsId().length;
  }
}

export default Profile;
