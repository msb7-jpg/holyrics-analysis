import type { Bible } from "./Bible";
import { Book, STANDARD_BOOKS } from "./Book";
import { Verse } from "./Verse";

/**
 * Filter criteria for verse search
 */
export interface VerseFilterOptions {
  search: string;
  exactPhrase?: boolean;
  anyWord?: boolean;
  wholeWord?: boolean;
  testament?: "OT" | "NT" | "ALL";
  bookId?: number;
}

/**
 * Structure of a parsed Bible reference (e.g. "Jo 3:16", "Gn 1", "Ap 2:1-5")
 */
export interface ParsedBibleReference {
  raw: string;
  bookQuery: string;
  book?: Book;
  chapter: number;
  verseStart?: number;
  verseEnd?: number;
}

/**
 * Normalizes text by removing accents and leading/trailing whitespace.
 */
export function removeAccent(str: string): string {
  return str
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .trim();
}

/**
 * Clamps a number between min and max.
 */
function range(value: number, min: number, max: number): number {
  return Math.max(min, Math.min(max, value));
}

/**
 * Isomorphic TypeScript port of com.limagiran.bible.BibleFind
 */
export abstract class BibleFind {
  protected abstract getBible(): Bible;
  protected abstract getBibleFind(): Bible;

  /**
   * Retrieves a Book by 1-based index (1..66).
   */
  public getBook(book: number, safe: boolean = false): Book | null {
    try {
      if (safe) {
        book = range(book, 1, 66);
      }
      const b = this.getBible().books[book - 1];
      return b || null;
    } catch {
      return null;
    }
  }

  /**
   * Retrieves a Verse by its formatted ID ("01001001").
   */
  public getVerse(id: string, safe: boolean = false): Verse | null {
    const verses = this.getBible().verses;
    const found = verses.find((v) => v.getId() === id);
    if (found) {
      return found;
    }
    if (safe && verses.length > 0) {
      let result: Verse | null = verses[0] ?? null;
      for (let i = verses.length - 1; i >= 0; i--) {
        const item = verses[i];
        if (item && id.localeCompare(item.getId()) > 0) {
          result = item;
          break;
        }
      }
      return result;
    }
    return null;
  }

  /**
   * Returns the verse following the given one.
   */
  public nextVerse(v: Verse): Verse {
    const verses = this.getBible().verses;
    const index = verses.findIndex((item) => item.equals(v));
    if (index !== -1 && index + 1 < verses.length) {
      const next = verses[index + 1];
      if (next) {
        return next;
      }
    }
    return v;
  }

  /**
   * Returns the verse preceding the given one.
   */
  public previousVerse(v: Verse): Verse {
    const verses = this.getBible().verses;
    const index = verses.findIndex((item) => item.equals(v));
    if (index > 0) {
      const prev = verses[index - 1];
      if (prev) {
        return prev;
      }
    }
    return v;
  }

  /**
   * Retrieves a Verse by book, chapter and verse numbers.
   */
  public getVerseByLocation(
    book: number,
    chapter: number,
    verse: number,
    safe: boolean = false
  ): Verse | null {
    try {
      if (safe) {
        book = range(book, 1, 66);
      }
      const targetBook = this.getBook(book, safe);
      if (!targetBook) {
        return null;
      }
      return targetBook.getVerse(chapter, verse, safe);
    } catch {
      return null;
    }
  }

  /**
   * Retrieves all verses in a given book and chapter.
   */
  public getVerses(book: number, chapter: number): Verse[] {
    try {
      const targetBook = this.getBook(book, false);
      if (!targetBook) {
        return [];
      }
      return targetBook.getVerses(chapter);
    } catch {
      return [];
    }
  }

  /**
   * Finds a Book by its abbreviation (case-insensitive).
   */
  public findBookByAbbrev(abbrev: string): Book | null {
    const lower = abbrev.toLowerCase();
    const books = this.getBible().books;

    // 1. Exact match with accents preserved (case-insensitive)
    const exact = books.find((b) => b.abbrev.toLowerCase() === lower);
    if (exact) {
      return exact;
    }

    // 2. Normalized without accents
    const norm = removeAccent(abbrev).toLowerCase();
    return (
      books.find(
        (b) => removeAccent(b.abbrev).toLowerCase() === norm
      ) || null
    );
  }

  /**
   * Finds a Book by its name (accents and spaces removed).
   */
  public findBookByName(name: string): Book | null {
    const nameClean = name.replace(/\s+/g, "").toLowerCase();
    const books = this.getBible().books;

    // 1. Exact name match (case-insensitive, spaces ignored)
    const exact = books.find(
      (b) => b.name.replace(/\s+/g, "").toLowerCase() === nameClean
    );
    if (exact) {
      return exact;
    }

    // 2. Normalized without accents
    const nameNorm = removeAccent(name).replace(/\s+/g, "").toLowerCase();
    const searchTarget = this.getBibleFind();
    const match = searchTarget.books.find((b) => {
      const bNorm = removeAccent(b.name).replace(/\s+/g, "").toLowerCase();
      return bNorm === nameNorm;
    });

    if (match) {
      return this.getBible().books[match.getIdInt() - 1] || null;
    }

    // Fallback: check standard books list
    const std = Book.findStandardBook(name);
    if (std) {
      return this.getBible().books[std.id - 1] || null;
    }

    return null;
  }

  /**
   * Resolves a book by abbreviation, name or query.
   */
  public findBook(query: string): Book | null {
    if (!query || !query.trim()) return null;
    const trimmed = query.trim();

    // Try abbreviation
    let book = this.findBookByAbbrev(trimmed);
    if (book) return book;

    // Try name
    book = this.findBookByName(trimmed);
    if (book) return book;

    // Try partial startsWith
    const qNorm = removeAccent(trimmed).replace(/\s+/g, "").toLowerCase();
    book =
      this.getBible().books.find((b) => {
        const bName = removeAccent(b.name).replace(/\s+/g, "").toLowerCase();
        const bAbbrev = removeAccent(b.abbrev).replace(/\s+/g, "").toLowerCase();
        return bName.startsWith(qNorm) || bAbbrev.startsWith(qNorm);
      }) || null;

    if (book) return book;

    // Check standard books
    const std = Book.findStandardBook(trimmed);
    if (std) {
      return this.getBible().books[std.id - 1] || null;
    }

    return null;
  }

  /**
   * Parses Bible references such as:
   * "Jo 3:16", "Gn 1", "Ap 2:1-5", "1 Jo 1:9", "Salmos 23", "Sl 23:1-6", "1Co 13:4-7", "Mt 28:19,20"
   */
  public static parseReference(reference: string): ParsedBibleReference | null {
    if (!reference || typeof reference !== "string") {
      return null;
    }

    const raw = reference.trim();
    if (!raw) return null;

    // Pattern matches:
    // (Optional prefix like 1, 2, 3 or I, II, III followed by letters)
    // followed by chapter and optional : or , or . and verse / range
    // Examples: "1 Jo 3:16", "Jo 3:16", "Gn 1", "Ap 2:1-5", "Sl 23, 1-6"
    const regex = /^((?:[1-3I|i]{1,3}\s*)?[a-zA-ZáéíóúâêôãõçÁÉÍÓÚÂÊÔÃÕÇ]+)\s*(\d+)(?:[:,\.\s]+(\d+)(?:\s*[-–—]\s*(\d+))?)?$/i;
    const match = raw.match(regex);

    if (!match) {
      // Secondary attempt: maybe just "Book Chapter" e.g. "Gn 1" or "Salmo 23"
      const simpleRegex = /^((?:[1-3I|i]{1,3}\s*)?[a-zA-ZáéíóúâêôãõçÁÉÍÓÚÂÊÔÃÕÇ\s]+?)\s+(\d+)$/i;
      const simpleMatch = raw.match(simpleRegex);
      if (simpleMatch && simpleMatch[1] && simpleMatch[2]) {
        return {
          raw,
          bookQuery: simpleMatch[1].trim(),
          chapter: parseInt(simpleMatch[2], 10),
        };
      }
      return null;
    }

    const bookQuery = match[1]?.trim() ?? "";
    const chapter = parseInt(match[2] ?? "0", 10);
    const verseStart = match[3] ? parseInt(match[3], 10) : undefined;
    const verseEnd = match[4] ? parseInt(match[4], 10) : verseStart;

    return {
      raw,
      bookQuery,
      chapter,
      verseStart,
      verseEnd,
    };
  }

  /**
   * Finds verses according to a reference string like "Jo 3:16", "Gn 1", "Ap 2:1-5".
   */
  public findVersesByReference(ref: string): Verse[] {
    const parsed = BibleFind.parseReference(ref);
    if (!parsed) {
      return [];
    }

    const book = this.findBook(parsed.bookQuery);
    if (!book) {
      return [];
    }

    parsed.book = book;
    const chapterVerses = book.getVerses(parsed.chapter);

    // If no specific verse requested, return all verses of the chapter
    if (parsed.verseStart === undefined) {
      return chapterVerses;
    }

    const start = parsed.verseStart;
    const end = parsed.verseEnd !== undefined ? parsed.verseEnd : start;

    return chapterVerses.filter((v) => v.verse >= start && v.verse <= end);
  }

  /**
   * Searches verses using filters (exact phrase, any word, whole word, testament).
   */
  public find(filter: VerseFilterOptions): Verse[] {
    const rawSearch = filter.search ? filter.search.trim() : "";
    if (!rawSearch) {
      return [];
    }

    const searchTarget = this.getBibleFind();
    const targetBible = this.getBible();
    let candidates = searchTarget.verses;

    if (filter.bookId) {
      candidates = candidates.filter((v) => v.book === filter.bookId);
    }
    if (filter.testament === "OT") {
      candidates = candidates.filter((v) => v.isOldTestament());
    } else if (filter.testament === "NT") {
      candidates = candidates.filter((v) => v.isNewTestament());
    }

    const queryNorm = removeAccent(rawSearch).toLowerCase();

    let matched: Verse[];

    if (filter.exactPhrase) {
      matched = candidates.filter((v) =>
        removeAccent(v.content).toLowerCase().includes(queryNorm)
      );
    } else {
      const words = queryNorm
        .split(/\s+/)
        .map((w) => w.trim())
        .filter(Boolean);

      if (words.length === 0) {
        return [];
      }

      matched = candidates.filter((v) => {
        const contentNorm = removeAccent(v.content).toLowerCase();

        if (filter.wholeWord) {
          const contentWords = contentNorm
            .split(/[^a-zA-Z0-9'-]+/)
            .filter(Boolean);

          if (filter.anyWord) {
            return words.some((w) => contentWords.includes(w));
          } else {
            return words.every((w) => contentWords.includes(w));
          }
        } else {
          if (filter.anyWord) {
            return words.some((w) => contentNorm.includes(w));
          } else {
            return words.every((w) => contentNorm.includes(w));
          }
        }
      });
    }

    // Map matched verses back to main bible verses
    const matchedIds = new Set(matched.map((v) => v.getId()));
    return targetBible.verses.filter((v) => matchedIds.has(v.getId()));
  }

  /**
   * Creates a Map of Book -> Chapter -> Verses Count
   */
  public static loadMap(bible: Bible): Map<number, Map<number, number>> {
    const map = new Map<number, Map<number, number>>();
    for (const v of bible.verses) {
      let bookMap = map.get(v.book);
      if (!bookMap) {
        bookMap = new Map<number, number>();
        map.set(v.book, bookMap);
      }
      bookMap.set(v.chapter, (bookMap.get(v.chapter) || 0) + 1);
    }
    return map;
  }

  /**
   * Serializes the chapter/verse map as a list of ';' separated string entries.
   */
  public static loadMapAsList(map: Map<number, Map<number, number>>): string[] {
    const result: string[] = [];
    for (let i = 1; i <= 66; i++) {
      const bookMap = map.get(i);
      if (!bookMap) {
        result.push("1");
        continue;
      }
      let maxChapter = 1;
      for (const ch of bookMap.keys()) {
        if (ch > maxChapter) maxChapter = ch;
      }

      const versesInChapters: string[] = [];
      for (let ch = 1; ch <= maxChapter; ch++) {
        versesInChapters.push(String(bookMap.get(ch) || 1));
      }
      result.push(versesInChapters.join(";"));
    }
    return result;
  }
}
