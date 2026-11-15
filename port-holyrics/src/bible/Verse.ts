import type { Bible } from "./Bible";
import type { Book } from "./Book";

/**
 * Isomorphic TypeScript port of com.limagiran.bible.Verse
 */
export class Verse {
  private _id?: string;
  public bible?: Bible;
  public readonly book: number;
  public readonly chapter: number;
  public readonly verse: number;
  public readonly content: string;

  constructor(book: number, chapter: number, verse: number, content: string) {
    this.book = book;
    this.chapter = chapter;
    this.verse = verse;
    this.content = content;
  }

  public toString(): string {
    return `${this.getSmallDesc()} - ${this.content}`;
  }

  public getId(): string {
    if (this._id) {
      return this._id;
    }
    const b = String(this.book).padStart(2, "0");
    const c = String(this.chapter).padStart(3, "0");
    const v = String(this.verse).padStart(3, "0");
    this._id = `${b}${c}${v}`;
    return this._id;
  }

  public equals(obj: unknown): boolean {
    if (!(obj instanceof Verse)) {
      return false;
    }
    return (
      obj.book === this.book &&
      obj.chapter === this.chapter &&
      obj.verse === this.verse
    );
  }

  public setBible(bible: Bible): void {
    this.bible = bible;
  }

  public getSmallDesc(): string {
    const bookObj = this.bible?.books[this.book - 1];
    if (bookObj) {
      return `${bookObj.abbrev} ${this.chapter}:${this.verse}`;
    }
    return `${this.book} ${this.chapter}:${this.verse}`;
  }

  public getFullDesc(): string {
    const bookObj = this.bible?.books[this.book - 1];
    if (bookObj) {
      return `${bookObj.name} ${this.chapter}:${this.verse}`;
    }
    return `${this.book} ${this.chapter}:${this.verse}`;
  }

  public getVersion(): string {
    return this.bible?.version ?? "";
  }

  public getVersionAbbrev(): string {
    return this.bible?.abbrev ?? "";
  }

  public getBook(): Book | undefined {
    return this.bible?.books[this.book - 1];
  }

  public isOldTestament(): boolean {
    return this.book < 40;
  }

  public isNewTestament(): boolean {
    return this.book >= 40;
  }

  public compareTo(v: Verse): number {
    if (this.book !== v.book) {
      return this.book - v.book;
    }
    if (this.chapter !== v.chapter) {
      return this.chapter - v.chapter;
    }
    if (this.verse !== v.verse) {
      return this.verse - v.verse;
    }
    return this.getVersion().localeCompare(v.getVersion());
  }
}
