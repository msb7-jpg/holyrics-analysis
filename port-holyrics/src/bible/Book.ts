import { Bible } from './Bible';
import { Verse } from './Verse';

export interface BookInfo {
  id: number;
  name: string;
  abbrev: string;
  testament: 'OT' | 'NT';
  chaptersCount?: number;
}

export const STANDARD_BOOKS: BookInfo[] = [
  // Antigo Testamento (39)
  { id: 1, name: 'Gênesis', abbrev: 'Gn', testament: 'OT', chaptersCount: 50 },
  { id: 2, name: 'Êxodo', abbrev: 'Êx', testament: 'OT', chaptersCount: 40 },
  { id: 3, name: 'Levítico', abbrev: 'Lv', testament: 'OT', chaptersCount: 27 },
  { id: 4, name: 'Números', abbrev: 'Nm', testament: 'OT', chaptersCount: 36 },
  { id: 5, name: 'Deuteronômio', abbrev: 'Dt', testament: 'OT', chaptersCount: 34 },
  { id: 6, name: 'Josué', abbrev: 'Js', testament: 'OT', chaptersCount: 24 },
  { id: 7, name: 'Juízes', abbrev: 'Jz', testament: 'OT', chaptersCount: 21 },
  { id: 8, name: 'Rute', abbrev: 'Rt', testament: 'OT', chaptersCount: 4 },
  { id: 9, name: '1 Samuel', abbrev: '1Sm', testament: 'OT', chaptersCount: 31 },
  { id: 10, name: '2 Samuel', abbrev: '2Sm', testament: 'OT', chaptersCount: 24 },
  { id: 11, name: '1 Reis', abbrev: '1Rs', testament: 'OT', chaptersCount: 22 },
  { id: 12, name: '2 Reis', abbrev: '2Rs', testament: 'OT', chaptersCount: 25 },
  { id: 13, name: '1 Crônicas', abbrev: '1Cr', testament: 'OT', chaptersCount: 29 },
  { id: 14, name: '2 Crônicas', abbrev: '2Cr', testament: 'OT', chaptersCount: 36 },
  { id: 15, name: 'Esdras', abbrev: 'Ed', testament: 'OT', chaptersCount: 10 },
  { id: 16, name: 'Neemias', abbrev: 'Ne', testament: 'OT', chaptersCount: 13 },
  { id: 17, name: 'Ester', abbrev: 'Et', testament: 'OT', chaptersCount: 10 },
  { id: 18, name: 'Jó', abbrev: 'Jó', testament: 'OT', chaptersCount: 42 },
  { id: 19, name: 'Salmos', abbrev: 'Sl', testament: 'OT', chaptersCount: 150 },
  { id: 20, name: 'Provérbios', abbrev: 'Pv', testament: 'OT', chaptersCount: 31 },
  { id: 21, name: 'Eclesiastes', abbrev: 'Ec', testament: 'OT', chaptersCount: 12 },
  { id: 22, name: 'Cantares', abbrev: 'Ct', testament: 'OT', chaptersCount: 8 },
  { id: 23, name: 'Isaías', abbrev: 'Is', testament: 'OT', chaptersCount: 66 },
  { id: 24, name: 'Jeremias', abbrev: 'Jr', testament: 'OT', chaptersCount: 52 },
  { id: 25, name: 'Lamentações', abbrev: 'Lm', testament: 'OT', chaptersCount: 5 },
  { id: 26, name: 'Ezequiel', abbrev: 'Ez', testament: 'OT', chaptersCount: 48 },
  { id: 27, name: 'Daniel', abbrev: 'Dn', testament: 'OT', chaptersCount: 12 },
  { id: 28, name: 'Oséias', abbrev: 'Os', testament: 'OT', chaptersCount: 14 },
  { id: 29, name: 'Joel', abbrev: 'Jl', testament: 'OT', chaptersCount: 3 },
  { id: 30, name: 'Amós', abbrev: 'Am', testament: 'OT', chaptersCount: 9 },
  { id: 31, name: 'Obadias', abbrev: 'Ob', testament: 'OT', chaptersCount: 1 },
  { id: 32, name: 'Jonas', abbrev: 'Jn', testament: 'OT', chaptersCount: 4 },
  { id: 33, name: 'Miqueias', abbrev: 'Mq', testament: 'OT', chaptersCount: 7 },
  { id: 34, name: 'Naum', abbrev: 'Na', testament: 'OT', chaptersCount: 3 },
  { id: 35, name: 'Habacuque', abbrev: 'Hc', testament: 'OT', chaptersCount: 3 },
  { id: 36, name: 'Sofonias', abbrev: 'Sf', testament: 'OT', chaptersCount: 3 },
  { id: 37, name: 'Ageu', abbrev: 'Ag', testament: 'OT', chaptersCount: 2 },
  { id: 38, name: 'Zacarias', abbrev: 'Zc', testament: 'OT', chaptersCount: 14 },
  { id: 39, name: 'Malaquias', abbrev: 'Ml', testament: 'OT', chaptersCount: 4 },

  // Novo Testamento (27)
  { id: 40, name: 'Mateus', abbrev: 'Mt', testament: 'NT', chaptersCount: 28 },
  { id: 41, name: 'Marcos', abbrev: 'Mc', testament: 'NT', chaptersCount: 16 },
  { id: 42, name: 'Lucas', abbrev: 'Lc', testament: 'NT', chaptersCount: 24 },
  { id: 43, name: 'João', abbrev: 'Jo', testament: 'NT', chaptersCount: 21 },
  { id: 44, name: 'Atos', abbrev: 'At', testament: 'NT', chaptersCount: 28 },
  { id: 45, name: 'Romanos', abbrev: 'Rm', testament: 'NT', chaptersCount: 16 },
  { id: 46, name: '1 Coríntios', abbrev: '1Co', testament: 'NT', chaptersCount: 16 },
  { id: 47, name: '2 Coríntios', abbrev: '2Co', testament: 'NT', chaptersCount: 13 },
  { id: 48, name: 'Gálatas', abbrev: 'Gl', testament: 'NT', chaptersCount: 6 },
  { id: 49, name: 'Efésios', abbrev: 'Ef', testament: 'NT', chaptersCount: 6 },
  { id: 50, name: 'Filipenses', abbrev: 'Fp', testament: 'NT', chaptersCount: 4 },
  { id: 51, name: 'Colossenses', abbrev: 'Cl', testament: 'NT', chaptersCount: 4 },
  { id: 52, name: '1 Tessalonicenses', abbrev: '1Ts', testament: 'NT', chaptersCount: 5 },
  { id: 53, name: '2 Tessalonicenses', abbrev: '2Ts', testament: 'NT', chaptersCount: 3 },
  { id: 54, name: '1 Timóteo', abbrev: '1Tm', testament: 'NT', chaptersCount: 6 },
  { id: 55, name: '2 Timóteo', abbrev: '2Tm', testament: 'NT', chaptersCount: 4 },
  { id: 56, name: 'Tito', abbrev: 'Tt', testament: 'NT', chaptersCount: 3 },
  { id: 57, name: 'Filemom', abbrev: 'Fm', testament: 'NT', chaptersCount: 1 },
  { id: 58, name: 'Hebreus', abbrev: 'Hb', testament: 'NT', chaptersCount: 13 },
  { id: 59, name: 'Tiago', abbrev: 'Tg', testament: 'NT', chaptersCount: 5 },
  { id: 60, name: '1 Pedro', abbrev: '1Pe', testament: 'NT', chaptersCount: 5 },
  { id: 61, name: '2 Pedro', abbrev: '2Pe', testament: 'NT', chaptersCount: 3 },
  { id: 62, name: '1 João', abbrev: '1Jo', testament: 'NT', chaptersCount: 5 },
  { id: 63, name: '2 João', abbrev: '2Jo', testament: 'NT', chaptersCount: 1 },
  { id: 64, name: '3 João', abbrev: '3Jo', testament: 'NT', chaptersCount: 1 },
  { id: 65, name: 'Judas', abbrev: 'Jd', testament: 'NT', chaptersCount: 1 },
  { id: 66, name: 'Apocalipse', abbrev: 'Ap', testament: 'NT', chaptersCount: 22 },
];

export class Book {
  private bible: Bible | null = null;
  private idInt: number = 0;

  public readonly id: string;
  public readonly name: string;
  public readonly abbrev: string;
  public readonly testament: string;
  public readonly testamentAbbrev: string;

  constructor(id: string, name: string, abbrev: string, testament: string, testamentAbbrev: string) {
    this.id = id;
    this.name = name;
    this.abbrev = abbrev;
    this.testament = testament;
    this.testamentAbbrev = testamentAbbrev;
  }

  public toString(): string {
    return this.name;
  }

  public getIdInt(): number {
    return this.idInt > 0 ? this.idInt : (this.idInt = parseInt(this.id, 10));
  }

  public setBible(bible: Bible | null): void {
    this.bible = bible;
  }

  public getAmountChapter(): number {
    const chapters = new Set(this.getAllVerses().map((v) => v.chapter));
    return chapters.size;
  }

  public getAllVerses(): Verse[] {
    if (!this.bible) {
      return [];
    }
    const bookId = this.getIdInt();
    return this.bible.verses.filter((v) => v.book === bookId);
  }

  public getVerse(chapter: number, verse: number, safe: boolean = false): Verse | null {
    if (!this.bible) {
      return null;
    }
    const bookId = this.getIdInt();
    const ret = this.bible.verses.find(
      (v) => v.book === bookId && v.chapter === chapter && v.verse === verse
    ) || null;

    if (ret === null && safe) {
      const paddedBook = String(bookId).padStart(2, '0');
      const paddedChapter = String(chapter).padStart(3, '0');
      const paddedVerse = String(verse).padStart(3, '0');
      return this.bible.getVerse(`${paddedBook}${paddedChapter}${paddedVerse}`, safe);
    }

    return ret;
  }

  public getVerses(chapter: number): Verse[] {
    if (!this.bible) {
      return [];
    }
    const bookId = this.getIdInt();
    return this.bible.verses.filter((v) => v.book === bookId && v.chapter === chapter);
  }

  /**
   * Helper to find standard book info by abbrev or name
   */
  public static findStandardBook(query: string): BookInfo | null {
    if (!query) return null;
    const q = query.trim().toLowerCase();
    return (
      STANDARD_BOOKS.find(
        (b) =>
          b.abbrev.toLowerCase() === q ||
          b.name.toLowerCase() === q ||
          b.name.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '') === q
      ) || null
    );
  }
}
