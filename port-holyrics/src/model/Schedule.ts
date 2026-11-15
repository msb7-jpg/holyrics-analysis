import { Member } from "./Member.ts";
import { Music } from "./Music.ts";
import { Worship } from "./Worship.ts";

export interface IHItem {
  getId(): string;
  [key: string]: any;
}

export const MemberController = {
  get(id: number): Member | null {
    return null;
  },
};

export const ScheduleController = {
  save(): void {},
};

export const WorshipController = {
  getWorships(activeOnly: boolean = false): Worship[] {
    return [];
  },
};

export const MusicDao = {
  getLazy(id: number): Music | null {
    return null;
  },
};

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.Schedule
 */
export class Schedule {
  public static readonly serialVersionUID = 12491;

  private date: Date | null = null;
  public readonly id: string;
  private longtime: number;
  private readonly musics: number[] = [];
  private notes: string;
  private medias: Uint8Array[] = [];
  private mediasBuff: IHItem[] | null = null;
  private readonly staff: Map<number, boolean> = new Map();
  private responsible: number | null = null;
  private version: number = 2;

  constructor(
    id: string,
    date: Date,
    notes: string = "",
    responsible: number | null = null,
    musics: number[] = [],
    medias: Uint8Array[] = []
  ) {
    this.id = id;
    this.date = date;
    this.notes = notes ?? "";
    this.longtime = this.date ? this.date.getTime() : 0;
    this.responsible = responsible;
    if (musics && musics.length > 0) {
      this.musics.push(...musics);
    }
    if (medias && medias.length > 0) {
      this.medias.push(...medias);
    }
  }

  public static sort(): (o1: Schedule, o2: Schedule) => number {
    return (o1: Schedule, o2: Schedule) => o1.getDate().getTime() - o2.getDate().getTime();
  }

  public equals(o: unknown): boolean {
    if (o instanceof Schedule) {
      return this.id.toLowerCase() === o.id.toLowerCase();
    }
    return false;
  }

  public hashCode(): number {
    let hash = 413;
    for (let i = 0; i < this.id.length; i++) {
      hash = (Math.imul(31, hash) + this.id.charCodeAt(i)) | 0;
    }
    return hash;
  }

  public getDate(): Date {
    if (this.date == null) {
      this.date = new Date(this.longtime);
    }
    return new Date(this.date.getTime());
  }

  public setDate(date: Date): void {
    this.date = date;
    this.longtime = this.date ? this.date.getTime() : 0;
  }

  public getResponsible(): number | null {
    return this.responsible;
  }

  public isResponsible(m: Member): boolean {
    return this.responsible != null && m.id === this.responsible;
  }

  public getResponsibleAsMember(): Member | null {
    if (this.responsible == null) {
      return null;
    }
    return MemberController.get(this.responsible);
  }

  public setResponsible(responsible: number | null): void {
    this.responsible = responsible;
  }

  public isScheduled(m: Member): boolean {
    return this.staff.get(m.id) ?? false;
  }

  public setScheduled(m: Member, scheduled: boolean, save: boolean = false): void {
    this.staff.set(m.id, scheduled);
    if (save) {
      ScheduleController.save();
    }
  }

  public getNotes(): string {
    return this.notes;
  }

  public setNotes(notes: string, save: boolean = false): void {
    this.notes = notes ?? "";
    if (save) {
      ScheduleController.save();
    }
  }

  public getMusics(): Music[] {
    return this.musics
      .map((id) => MusicDao.getLazy(id))
      .filter((m): m is Music => m !== null && m !== undefined);
  }

  public contains(item: Music | IHItem): boolean {
    if (typeof (item as any)?.getArtist === "function") {
      return this.musics.includes((item as Music).getId());
    }
    return this.containsMedia(item as IHItem);
  }

  public setMusics(musics: Music[]): void {
    this.musics.length = 0;
    musics.forEach((m) => this.musics.push(m.getId()));
  }

  public addMusic(music: Music): void {
    if (!this.musics.includes(music.getId())) {
      this.musics.push(music.getId());
    }
  }

  public getMedias(): IHItem[] {
    return [...this.getMediasBuff()];
  }

  public containsMedia(hItem: IHItem): boolean {
    return this.getMediasBuff().some((item) => item.getId() === hItem.getId());
  }

  public setMedias(medias: IHItem[]): void {
    this.medias = [];
    this.mediasBuff = null;
    medias.forEach((item) => {
      this.medias.push(new TextEncoder().encode(JSON.stringify(item)));
    });
    this.getMediasBuff();
  }

  public addMedia(hItem: IHItem): void {
    if (!this.containsMedia(hItem)) {
      const bytes = new TextEncoder().encode(JSON.stringify(hItem));
      if (bytes.length > 0) {
        this.medias.push(bytes);
        this.getMediasBuff().push(hItem);
      }
    }
  }

  private getMediasBuff(): IHItem[] {
    if (this.mediasBuff == null) {
      this.mediasBuff = [];
      this.medias.forEach((bytes) => {
        try {
          const parsed = JSON.parse(new TextDecoder().decode(bytes));
          if (parsed && typeof parsed.getId === "function") {
            this.mediasBuff!.push(parsed);
          } else if (parsed && parsed.id) {
            this.mediasBuff!.push({
              getId: () => parsed.id,
              ...parsed,
            });
          }
        } catch {
          // ignore invalid media bytes
        }
      });
    }
    return this.mediasBuff;
  }

  public isActive(): boolean {
    const worships = WorshipController.getWorships(false);
    for (const w of worships) {
      if (this.id.startsWith(String(w.id))) {
        return typeof w.isActive === "function" ? w.isActive() : true;
      }
    }
    return true;
  }
}

export default Schedule;
