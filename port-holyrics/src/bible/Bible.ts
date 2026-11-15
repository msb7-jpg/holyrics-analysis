import { BibleFind } from "./BibleFind";
import { Book } from "./Book";
import { Verse } from "./Verse";

/**
 * Isomorphic TypeScript port of com.limagiran.bible.Bible
 */
export class Bible extends BibleFind {
  private bibleFind: Bible | null = null;
  private map: Map<number, Map<number, number>> | null = null;
  private mapAsList: string[] | null = null;

  public readonly version: string;
  public readonly abbrev: string;
  public readonly update: number;
  public readonly language: string;
  public readonly books: Book[];
  public readonly verses: Verse[];

  constructor(
    version: string,
    abbrev: string,
    update: number,
    language: string,
    books: Book[],
    verses: Verse[]
  ) {
    super();
    this.version = version;
    this.abbrev = abbrev;
    this.update = update;
    this.language = language;
    this.books = books;
    this.verses = verses;

    for (const b of this.books) {
      b.setBible(this);
    }
    for (const v of this.verses) {
      v.setBible(this);
    }
  }

  public setBibleFind(bibleFind: Bible): void {
    this.bibleFind = bibleFind;
  }

  protected getBible(): Bible {
    return this;
  }

  protected getBibleFind(): Bible {
    return this.bibleFind || this;
  }

  public getMap(): Map<number, Map<number, number>> {
    if (!this.map) {
      this.map = BibleFind.loadMap(this);
    }
    return new Map(this.map);
  }

  public getMapAsList(): string[] {
    if (!this.mapAsList) {
      this.mapAsList = BibleFind.loadMapAsList(this.getMap());
    }
    return [...this.mapAsList];
  }

  public equals(obj: unknown): boolean {
    if (obj instanceof Bible) {
      return obj.version === this.version;
    }
    return false;
  }
}
