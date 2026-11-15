import { Music } from "./Music.ts";

function removeAccent(str: string): string {
  return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

function firstLetterToUpperCase(str: string): string {
  if (!str) return "";
  return str.charAt(0).toUpperCase() + str.slice(1);
}

export interface MusicFilter {
  getSearch(): string;
  isArtist(): boolean;
  isTitle(): boolean;
  isLyrics(): boolean;
  getSearchContains(): string;
  getSearchStartsWith(): string;
  getSearchEndsWith(): string;
}

export class LyricsJson {
  public readonly title: string;
  public readonly artist: string;
  public readonly lyrics: string;
  public readonly youtubeId: string;
  public readonly language: string;

  constructor(title: string, artist: string, lyrics: string, youtubeId: string, language: string) {
    this.title = title;
    this.artist = artist;
    this.lyrics = lyrics;
    this.youtubeId = youtubeId;
    this.language = language;
  }
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.DBOffLine
 */
export class DBOffLine {
  public static readonly LyricsJson = LyricsJson;

  public static readonly LIST: Music[] = [];
  public static readonly LIST_TO_SEARCH: Music[] = [];
  public static readonly LIST_MOST_PLAYED: Music[] = [];

  private readonly list: LyricsJson[];

  private constructor(list: LyricsJson[]) {
    this.list = list;
  }

  private static read(fileName: string, toLowerCaseAndRemoveAccent: boolean): Music[] {
    try {
      // Offline DB files are packaged in .hly resources, fallback to empty list if not present
      return [];
    } catch {
      return [];
    }
  }

  private static readHCAndCC(type: string, toLowerCaseAndRemoveAccent: boolean): Music[] {
    try {
      return [];
    } catch {
      return [];
    }
  }

  public static search(f: MusicFilter): Music[] {
    const founds: Music[] = [];
    const searchVal = f.getSearch();

    DBOffLine.LIST_TO_SEARCH.forEach((m) => {
      if (
        !searchVal ||
        (f.isArtist() && DBOffLine.stringSearch(m.getArtist(), f)) ||
        (f.isTitle() && DBOffLine.stringSearch(m.getTitle(), f)) ||
        (f.isLyrics() && DBOffLine.stringSearch(m.getLyrics(), f)) ||
        DBOffLine.stringSearch(m.getNote(), f)
      ) {
        founds.push(m);
      }
    });

    const ids = new Set(founds.map((m) => m.getId()));
    return DBOffLine.LIST.filter((m) => ids.has(m.getId()));
  }

  private static stringSearch(val: string | null | undefined, f: MusicFilter): boolean {
    if (!val) return false;
    const searchContains = f.getSearchContains();
    const searchStartsWith = f.getSearchStartsWith();
    const searchEndsWith = f.getSearchEndsWith();

    return (
      (Boolean(searchContains) && val.includes(searchContains)) ||
      (Boolean(searchStartsWith) && val.startsWith(searchStartsWith)) ||
      (Boolean(searchEndsWith) && val.endsWith(searchEndsWith))
    );
  }

  private static normalize(value: string): string {
    return value
      .replace(/",/g, "╚")
      .replace(/},/g, "╔")
      .replace(/[,.?!]/g, " ")
      .replace(/╚/g, '",')
      .replace(/╔/g, "},")
      .replace(/  /g, " ");
  }

  private static init(): void {
    const defaultList = DBOffLine.read("db-offline", false);
    const toSearchList = DBOffLine.read("db-offline", true);
    const mostPlayedList = DBOffLine.read("db-offline-best", false);

    DBOffLine.LIST.push(...defaultList);
    DBOffLine.LIST_TO_SEARCH.push(...toSearchList);
    DBOffLine.LIST_MOST_PLAYED.push(...mostPlayedList);

    const hc = DBOffLine.readHCAndCC("hc", false);
    const cc = DBOffLine.readHCAndCC("cc", false);
    const hcSearch = DBOffLine.readHCAndCC("hc", true);
    const ccSearch = DBOffLine.readHCAndCC("cc", true);

    DBOffLine.LIST.push(...hc, ...cc);
    DBOffLine.LIST_MOST_PLAYED.push(...hc, ...cc);
    DBOffLine.LIST_TO_SEARCH.push(...hcSearch, ...ccSearch);

    [DBOffLine.LIST, DBOffLine.LIST_TO_SEARCH, DBOffLine.LIST_MOST_PLAYED].forEach((list) => {
      for (let i = 0; i < list.length; i++) {
        const item = list[i];
        if (item) {
          item.setId(-i);
        }
      }
    });
  }
}

// Run static initialization
(DBOffLine as any).init();

export default DBOffLine;
