import { Database } from "bun:sqlite";
import * as crypto from "crypto";
import * as fs from "fs";
import * as path from "path";
import * as zlib from "zlib";
import { Bible } from "./Bible";
import { Book } from "./Book";
import { Verse } from "./Verse";
import { removeAccent } from "./BibleFind";

/**
 * Key used in the legacy Holyrics encryption format for .bib files.
 * Derived from "$%#@keyOBJ*&¨bib"
 */
const KEY_OBJ = "$%#@keyOBJ*&¨bib";
const AES_IV = Buffer.from("AAAAAAAAAAAAAAAA", "utf8");

interface VerseRow {
  id: string;
  book: number;
  chapter: number;
  verse: number;
  content: string;
}

interface BookRow {
  id: string;
  name: string;
  abbrev: string;
  testament: string;
  testament_abbrev: string;
}

interface BibleInfoRow {
  key: string;
  value: string;
}

let defaultDb: Database | null = null;
let currentLoadedBible: Bible | null = null;

/**
 * Returns a 16-character SHA-256 derived hex key for AES-128-CBC.
 */
function getPass16(key: string): string {
  if (!key) key = "A";
  const hash = crypto.createHash("sha256").update(key, "utf8").digest("hex").toUpperCase();
  return hash.substring(0, 16);
}

/**
 * Initializes table schemas in the SQLite database.
 */
export function initSchema(db: Database): void {
  db.run(`
    CREATE TABLE IF NOT EXISTS bible_info (
      key TEXT PRIMARY KEY,
      value TEXT
    );
  `);
  db.run(`
    CREATE TABLE IF NOT EXISTS books (
      id TEXT PRIMARY KEY,
      name TEXT,
      abbrev TEXT,
      testament TEXT,
      testament_abbrev TEXT
    );
  `);
  db.run(`
    CREATE TABLE IF NOT EXISTS verses (
      id TEXT PRIMARY KEY,
      book INTEGER,
      chapter INTEGER,
      verse INTEGER,
      content TEXT
    );
  `);
  db.run(`CREATE INDEX IF NOT EXISTS idx_verses_b_c ON verses(book, chapter);`);
  db.run(`CREATE INDEX IF NOT EXISTS idx_verses_b_c_v ON verses(book, chapter, verse);`);
}

/**
 * Returns or creates the active default SQLite database instance.
 */
export function getDatabase(dbPath?: string): Database {
  if (dbPath) {
    const db = new Database(dbPath);
    initSchema(db);
    return db;
  }
  if (!defaultDb) {
    const defaultPath = process.env.HOLYRICS_BIBLE_DB_PATH || ":memory:";
    defaultDb = new Database(defaultPath);
    initSchema(defaultDb);
  }
  return defaultDb;
}

/**
 * Overrides the default active SQLite database instance.
 */
export function setDatabase(db: Database): void {
  defaultDb = db;
  initSchema(defaultDb);
}

/**
 * Isomorphic TypeScript port of com.limagiran.bible.FactoryBible
 * with bun:sqlite integration and legacy .bib support.
 */
export class FactoryBible {
  /**
   * Decrypts a legacy encrypted .bib byte buffer into its JSON string.
   */
  public static decryptLegacyBib(data: Buffer | Uint8Array): string {
    const pass16 = getPass16(KEY_OBJ);
    const decipher = crypto.createDecipheriv("aes-128-cbc", Buffer.from(pass16, "utf8"), AES_IV);
    const decryptedBytes = Buffer.concat([decipher.update(data), decipher.final()]);
    return zlib.inflateSync(decryptedBytes).toString("utf8");
  }

  /**
   * Encrypts a JSON string into the legacy encrypted .bib format.
   */
  public static encryptLegacyBib(json: string): Buffer {
    const pass16 = getPass16(KEY_OBJ);
    const compressed = zlib.deflateSync(Buffer.from(json, "utf8"));
    const cipher = crypto.createCipheriv("aes-128-cbc", Buffer.from(pass16, "utf8"), AES_IV);
    return Buffer.concat([cipher.update(compressed), cipher.final()]);
  }

  /**
   * Imports a loaded Bible instance into a SQLite database.
   */
  public static importBible(bible: Bible, targetDb?: Database): void {
    const db = targetDb || getDatabase();
    initSchema(db);

    const insertInfo = db.prepare("INSERT OR REPLACE INTO bible_info (key, value) VALUES (?, ?)");
    const insertBook = db.prepare(
      "INSERT OR REPLACE INTO books (id, name, abbrev, testament, testament_abbrev) VALUES (?, ?, ?, ?, ?)"
    );
    const insertVerse = db.prepare(
      "INSERT OR REPLACE INTO verses (id, book, chapter, verse, content) VALUES (?, ?, ?, ?, ?)"
    );

    db.transaction(() => {
      insertInfo.run("version", bible.version);
      insertInfo.run("abbrev", bible.abbrev);
      insertInfo.run("language", bible.language);
      insertInfo.run("update", String(bible.update));

      for (const b of bible.books) {
        insertBook.run(b.id, b.name, b.abbrev, b.testament, b.testamentAbbrev);
      }

      for (const v of bible.verses) {
        insertVerse.run(v.getId(), v.book, v.chapter, v.verse, v.content);
      }
    })();
  }

  /**
   * Loads a Bible instance from an initialized SQLite database.
   */
  public static loadFromDatabase(targetDb?: Database): Bible {
    const db = targetDb || getDatabase();
    initSchema(db);

    const infoRows = db.query<BibleInfoRow, []>("SELECT key, value FROM bible_info").all();
    const infoMap = new Map<string, string>();
    for (const r of infoRows) {
      infoMap.set(r.key, r.value);
    }

    const version = infoMap.get("version") || "Biblia";
    const abbrev = infoMap.get("abbrev") || "bib";
    const language = infoMap.get("language") || "pt";
    const update = parseInt(infoMap.get("update") || "0", 10);

    const bookRows = db.query<BookRow, []>("SELECT * FROM books ORDER BY CAST(id AS INTEGER) ASC").all();
    const books = bookRows.map(
      (r) => new Book(r.id, r.name, r.abbrev, r.testament, r.testament_abbrev)
    );

    const verseRows = db.query<VerseRow, []>("SELECT * FROM verses ORDER BY book, chapter, verse").all();
    const verses = verseRows.map(
      (r) => new Verse(r.book, r.chapter, r.verse, r.content)
    );

    const bible = new Bible(version, abbrev, update, language, books, verses);

    // Create searchable Bible instance with normalized accents and lowercase
    const searchBooks = books.map(
      (b) =>
        new Book(
          b.id,
          removeAccent(b.name).toLowerCase(),
          removeAccent(b.abbrev).toLowerCase(),
          b.testament,
          b.testamentAbbrev
        )
    );
    const searchVerses = verses.map(
      (v) =>
        new Verse(
          v.book,
          v.chapter,
          v.verse,
          removeAccent(v.content).toLowerCase()
        )
    );
    const bibleFind = new Bible(version, abbrev, update, language, searchBooks, searchVerses);
    bible.setBibleFind(bibleFind);

    currentLoadedBible = bible;
    return bible;
  }

  /**
   * Loads and instantiates a Bible from either an SQLite database file or legacy encrypted .bib file.
   * Maps to com.limagiran.bible.FactoryBible.create(File path)
   */
  public static create(filePath: string, targetDb?: Database): Bible {
    const buffer = fs.readFileSync(filePath);

    // Check if the file is a native SQLite database (magic header "SQLite format 3\0")
    const isSQLite = buffer.length >= 16 && buffer.subarray(0, 16).toString("ascii").startsWith("SQLite format 3");

    if (isSQLite) {
      const db = new Database(filePath);
      return FactoryBible.loadFromDatabase(db);
    }

    // Otherwise, treat as legacy AES encrypted and deflated .bib file
    const json = FactoryBible.decryptLegacyBib(buffer);
    const rawData = JSON.parse(json);

    const books: Book[] = (rawData.books || []).map(
      (b: any) => new Book(String(b.id), b.name, b.abbrev, b.testament, b.testamentAbbrev || b.testamentabbrev)
    );

    const verses: Verse[] = (rawData.verses || []).map(
      (v: any) => new Verse(v.book, v.chapter, v.verse, v.content)
    );

    const bible = new Bible(
      rawData.version || "",
      rawData.abbrev || "",
      rawData.update || 0,
      rawData.language || "",
      books,
      verses
    );

    // Prepare search bible (lowercase and remove accents)
    const jsonSearch = removeAccent(json.toLowerCase()).replace(/"testamentabbrev"/g, '"testamentAbbrev"');
    const rawSearch = JSON.parse(jsonSearch);
    const searchBooks: Book[] = (rawSearch.books || []).map(
      (b: any) => new Book(String(b.id), b.name, b.abbrev, b.testament, b.testamentAbbrev || b.testamentabbrev)
    );
    const searchVerses: Verse[] = (rawSearch.verses || []).map(
      (v: any) => new Verse(v.book, v.chapter, v.verse, v.content)
    );
    const bibleFind = new Bible(
      bible.version,
      bible.abbrev,
      bible.update,
      bible.language,
      searchBooks,
      searchVerses
    );
    bible.setBibleFind(bibleFind);

    currentLoadedBible = bible;

    // If a SQLite DB was requested or available, sync it into SQLite
    const db = targetDb || getDatabase();
    try {
      FactoryBible.importBible(bible, db);
    } catch (e) {
      // Best-effort cache in sqlite
    }

    return bible;
  }

  /**
   * Returns a summary "(language) version" from a .bib file.
   * Maps to com.limagiran.bible.FactoryBible.getLanguageAndVersion(File path)
   */
  public static getLanguageAndVersion(filePath: string): string {
    const buffer = fs.readFileSync(filePath);
    const isSQLite = buffer.length >= 16 && buffer.subarray(0, 16).toString("ascii").startsWith("SQLite format 3");

    if (isSQLite) {
      const db = new Database(filePath);
      const rows = db.query<BibleInfoRow, []>("SELECT key, value FROM bible_info").all();
      const map = new Map(rows.map((r) => [r.key, r.value]));
      return `(${map.get("language") || "pt"}) ${map.get("version") || ""}`;
    }

    const json = FactoryBible.decryptLegacyBib(buffer);
    const parsed = JSON.parse(json);
    return `(${parsed.language}) ${parsed.version}`;
  }

  /**
   * Saves a Bible instance to a file.
   * If target path ends with .sqlite or is an SQLite db, saves as SQLite, otherwise as encrypted .bib.
   * Maps to com.limagiran.bible.FactoryBible.save(Bible bible, File file)
   */
  public static save(bible: Bible, targetPath: string): boolean {
    const tempPath = path.join(path.dirname(targetPath), `tmp_${Date.now()}.rw`);

    try {
      if (targetPath.endsWith(".sqlite") || targetPath.endsWith(".db")) {
        const db = new Database(tempPath);
        FactoryBible.importBible(bible, db);
        db.close();
      } else {
        const obj = {
          version: bible.version,
          abbrev: bible.abbrev,
          update: bible.update,
          language: bible.language,
          books: bible.books.map((b) => ({
            id: b.id,
            name: b.name,
            abbrev: b.abbrev,
            testament: b.testament,
            testamentAbbrev: b.testamentAbbrev,
          })),
          verses: bible.verses.map((v) => ({
            book: v.book,
            chapter: v.chapter,
            verse: v.verse,
            content: v.content,
          })),
        };
        const bytesEnc = FactoryBible.encryptLegacyBib(JSON.stringify(obj));
        fs.writeFileSync(tempPath, bytesEnc);
      }

      if (fs.existsSync(targetPath)) {
        fs.unlinkSync(targetPath);
      }
      fs.renameSync(tempPath, targetPath);
      return true;
    } finally {
      if (fs.existsSync(tempPath)) {
        try {
          fs.unlinkSync(tempPath);
        } catch {
          // Ignore cleanup errors
        }
      }
    }
  }

  /**
   * Retrieves a Verse from the SQLite database.
   */
  public static getVerse(
    book: number,
    chapter: number,
    verse: number,
    db?: Database
  ): Verse | null {
    const activeDb = db || getDatabase();
    const query = activeDb.prepare(
      "SELECT id, book, chapter, verse, content FROM verses WHERE book = ? AND chapter = ? AND verse = ? LIMIT 1"
    );
    const row = query.get(book, chapter, verse) as VerseRow | null;
    if (!row) {
      if (currentLoadedBible) {
        return currentLoadedBible.getVerseByLocation(book, chapter, verse);
      }
      return null;
    }
    const result = new Verse(row.book, row.chapter, row.verse, row.content);
    if (currentLoadedBible) {
      result.setBible(currentLoadedBible);
    }
    return result;
  }

  /**
   * Retrieves all verses for a given book and chapter from the SQLite database.
   */
  public static getChapter(
    book: number,
    chapter: number,
    db?: Database
  ): Verse[] {
    const activeDb = db || getDatabase();
    const query = activeDb.prepare(
      "SELECT id, book, chapter, verse, content FROM verses WHERE book = ? AND chapter = ? ORDER BY verse ASC"
    );
    const rows = query.all(book, chapter) as VerseRow[];
    return rows.map((r) => {
      const v = new Verse(r.book, r.chapter, r.verse, r.content);
      if (currentLoadedBible) {
        v.setBible(currentLoadedBible);
      }
      return v;
    });
  }

  /**
   * Searches verses by text in the SQLite database.
   */
  public static search(
    queryText: string,
    options?: { book?: number; limit?: number; exact?: boolean },
    db?: Database
  ): Verse[] {
    const activeDb = db || getDatabase();
    let sql = "SELECT id, book, chapter, verse, content FROM verses WHERE ";
    const params: (string | number)[] = [];

    const cleanQuery = queryText.trim();
    if (!cleanQuery) return [];

    if (options?.exact) {
      sql += "content LIKE ? ";
      params.push(`%${cleanQuery}%`);
    } else {
      const words = cleanQuery.split(/\s+/).filter(Boolean);
      const clauses = words.map(() => "content LIKE ?");
      sql += `(${clauses.join(" AND ")}) `;
      for (const w of words) {
        params.push(`%${w}%`);
      }
    }

    if (options?.book !== undefined) {
      sql += "AND book = ? ";
      params.push(options.book);
    }

    sql += "ORDER BY book, chapter, verse ";

    if (options?.limit) {
      sql += "LIMIT ? ";
      params.push(options.limit);
    }

    const stmt = activeDb.prepare(sql);
    const rows = stmt.all(...params) as VerseRow[];
    return rows.map((r) => {
      const v = new Verse(r.book, r.chapter, r.verse, r.content);
      if (currentLoadedBible) {
        v.setBible(currentLoadedBible);
      }
      return v;
    });
  }

  /**
   * Retrieves all books from the SQLite database.
   */
  public static getBooks(db?: Database): Book[] {
    const activeDb = db || getDatabase();
    const rows = activeDb.query<BookRow, []>(
      "SELECT id, name, abbrev, testament, testament_abbrev FROM books ORDER BY CAST(id AS INTEGER) ASC"
    ).all();
    return rows.map((r) => {
      const b = new Book(r.id, r.name, r.abbrev, r.testament, r.testament_abbrev);
      if (currentLoadedBible) {
        b.setBible(currentLoadedBible);
      }
      return b;
    });
  }

  /**
   * Retrieves a single Book by id from the SQLite database.
   */
  public static getBook(id: number, db?: Database): Book | null {
    const activeDb = db || getDatabase();
    const row = activeDb.prepare(
      "SELECT id, name, abbrev, testament, testament_abbrev FROM books WHERE CAST(id AS INTEGER) = ? LIMIT 1"
    ).get(id) as BookRow | null;
    if (!row) return null;
    const b = new Book(row.id, row.name, row.abbrev, row.testament, row.testament_abbrev);
    if (currentLoadedBible) {
      b.setBible(currentLoadedBible);
    }
    return b;
  }

  /**
   * Returns the currently active Bible object, if loaded.
   */
  public static getCurrentBible(): Bible | null {
    return currentLoadedBible;
  }
}
