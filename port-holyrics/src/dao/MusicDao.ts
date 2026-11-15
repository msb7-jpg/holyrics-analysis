import { Database } from "bun:sqlite";
import { Music } from "../model/Music";
import { Utils } from "../util/Utils";

/**
 * Row structure for the 'music' table.
 */
interface MusicRow {
  _id: number;
  music: string;
}

let defaultDb: Database | null = null;

/**
 * Returns the active Database instance, initializing schema if not already initialized.
 */
export function getDatabase(dbPath?: string): Database {
  if (dbPath) {
    const db = new Database(dbPath);
    initSchema(db);
    return db;
  }
  if (!defaultDb) {
    const path = process.env.HOLYRICS_DB_PATH || ":memory:";
    defaultDb = new Database(path);
    initSchema(defaultDb);
  }
  return defaultDb;
}

/**
 * Overrides the default Database instance (useful for tests or custom paths).
 */
export function setDatabase(db: Database): void {
  defaultDb = db;
  initSchema(defaultDb);
  MusicDao.updateAll();
}

/**
 * Initializes the SQLite table schema for music.
 */
function initSchema(db: Database): void {
  db.run("CREATE TABLE IF NOT EXISTS music (_id INTEGER PRIMARY KEY, music TEXT)");
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.dao.MusicDao
 */
export class MusicDao {
  private static readonly ALL: Music[] = [];
  private static readonly ALL_TO_SEARCH: Music[] = [];
  private static initialized: boolean = false;

  private static getDb(): Database {
    const db = getDatabase();
    if (!MusicDao.initialized) {
      MusicDao.initialized = true;
      MusicDao.updateAll();
    }
    return db;
  }

  /**
   * Saves a new Music record or updates existing if id > 0 and already exists.
   * Maps to com.limagiran.holyrics.dao.MusicDao.add(Music m)
   */
  public static add(m: Music): boolean {
    const db = MusicDao.getDb();
    if (m.getId() > 0 && MusicDao.get(m.getId()) !== null) {
      return true;
    }

    try {
      m.setId(Utils.generateMillisecondId());
      const sql = "INSERT INTO music VALUES (?, ?)";
      const stmt = db.query(sql);
      stmt.run(m.getId(), m.getSerial());

      const existingIndex = MusicDao.ALL.findIndex((item) => item.getId() === m.getId());
      if (existingIndex >= 0) {
        MusicDao.ALL[existingIndex] = m;
      } else {
        MusicDao.ALL.push(m);
      }

      const toSearch = m.getInstanceToSearch();
      const existingSearchIndex = MusicDao.ALL_TO_SEARCH.findIndex(
        (item) => item.getId() === m.getId()
      );
      if (existingSearchIndex >= 0) {
        MusicDao.ALL_TO_SEARCH[existingSearchIndex] = toSearch;
      } else {
        MusicDao.ALL_TO_SEARCH.push(toSearch);
      }

      return true;
    } catch (error) {
      console.error(error);
      return false;
    }
  }

  /**
   * Alias for add(m).
   */
  public static save(music: Music): boolean {
    return MusicDao.add(music);
  }

  /**
   * Adds a music keeping its existing ID if it doesn't exist yet.
   */
  public static addKeepId(m: Music): boolean {
    const db = MusicDao.getDb();
    if (m.getId() > 0 && MusicDao.get(m.getId()) === null) {
      try {
        const sql = "INSERT INTO music VALUES (?, ?)";
        const stmt = db.query(sql);
        stmt.run(m.getId(), m.getSerial());

        const existingIndex = MusicDao.ALL.findIndex((item) => item.getId() === m.getId());
        if (existingIndex >= 0) {
          MusicDao.ALL[existingIndex] = m;
        } else {
          MusicDao.ALL.push(m);
        }

        const toSearch = m.getInstanceToSearch();
        const existingSearchIndex = MusicDao.ALL_TO_SEARCH.findIndex(
          (item) => item.getId() === m.getId()
        );
        if (existingSearchIndex >= 0) {
          MusicDao.ALL_TO_SEARCH[existingSearchIndex] = toSearch;
        } else {
          MusicDao.ALL_TO_SEARCH.push(toSearch);
        }

        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    }
    return true;
  }

  /**
   * Adds a list of music items in batches.
   */
  public static addBatch(list: Music[]): void {
    const ms = [...list];
    for (let i = 0; i < ms.length; i++) {
      const item = ms[i];
      if (item) {
        item.setId(-i);
      }
    }

    const limit = 200;
    while (ms.length > 0) {
      const batch = ms.splice(0, Math.min(ms.length, limit));
      MusicDao.batchAdd(batch);
    }

    MusicDao.updateAll();
  }

  /**
   * Batch insertion helper.
   */
  private static batchAdd(list: Music[]): void {
    if (list.length === 0 || list.length > 200) {
      return;
    }

    const db = MusicDao.getDb();
    try {
      const placeholders = list.map(() => "(?, ?)").join(", ");
      const sql = `INSERT INTO music VALUES ${placeholders}`;
      const params: (number | string)[] = [];

      for (let i = 0; i < list.length; i++) {
        const item = list[i];
        if (item) {
          const m = item.setId(Utils.generateMillisecondId());
          params.push(m.getId(), m.getSerial());
        }
      }

      db.query(sql).run(...params);
    } catch (error) {
      console.error(error);
    }
  }

  /**
   * Finds a music record by its ID using java.sql ResultSet equivalent loops with db.query(...).all().
   */
  public static get(id: number): Music | null {
    const db = MusicDao.getDb();
    const sql = "SELECT * FROM music WHERE _id = ?";

    try {
      const rows = db.query(sql).all(id) as MusicRow[];
      let music: Music | null = null;
      for (const rs of rows) {
        music = Music.create(rs.music);
        break;
      }
      return music;
    } catch (error) {
      console.error(error);
      return null;
    }
  }

  /**
   * Alias for get(id).
   */
  public static find(id: number): Music | null {
    return MusicDao.get(id);
  }

  /**
   * Updates an existing Music record.
   */
  public static set(m: Music): boolean {
    const db = MusicDao.getDb();
    const sql = "UPDATE music SET music = ? WHERE _id = ?";

    try {
      db.query(sql).run(m.getSerial(), m.getId());

      const existingIndex = MusicDao.ALL.findIndex((item) => item.getId() === m.getId());
      if (existingIndex >= 0) {
        MusicDao.ALL[existingIndex] = m;
      }

      const toSearch = m.getInstanceToSearch();
      const existingSearchIndex = MusicDao.ALL_TO_SEARCH.findIndex(
        (item) => item.getId() === m.getId()
      );
      if (existingSearchIndex >= 0) {
        MusicDao.ALL_TO_SEARCH[existingSearchIndex] = toSearch;
      }

      return true;
    } catch (error) {
      console.error(error);
      return false;
    }
  }

  /**
   * Alias for set(m).
   */
  public static update(music: Music): boolean {
    return MusicDao.set(music);
  }

  /**
   * Deletes a music record by id or Music object.
   */
  public static remove(m: Music | number): boolean {
    const db = MusicDao.getDb();
    const id = typeof m === "number" ? m : m.getId();
    const sql = "DELETE FROM music WHERE _id = ?";

    try {
      db.query(sql).run(id);

      const idx = MusicDao.ALL.findIndex((item) => item.getId() === id);
      if (idx >= 0) {
        MusicDao.ALL.splice(idx, 1);
      }

      const sIdx = MusicDao.ALL_TO_SEARCH.findIndex((item) => item.getId() === id);
      if (sIdx >= 0) {
        MusicDao.ALL_TO_SEARCH.splice(sIdx, 1);
      }

      return true;
    } catch (error) {
      console.error(error);
      return false;
    }
  }

  /**
   * Alias for remove(id).
   */
  public static delete(id: number): boolean {
    return MusicDao.remove(id);
  }

  /**
   * Removes a list of music items in batches.
   */
  public static removeList(list: Music[]): void {
    const ms = list.filter((m) => m.getId() > 0);
    const limit = 100;

    while (ms.length > 0) {
      const batch = ms.splice(0, Math.min(ms.length, limit));
      MusicDao.removeAll(batch);
    }

    MusicDao.updateAll();
  }

  /**
   * Batch deletion helper.
   */
  private static removeAll(list: Music[]): void {
    if (list.length === 0) return;
    const db = MusicDao.getDb();

    try {
      const ids = list
        .filter((m) => m !== null && m !== undefined)
        .map((m) => m.getId());

      if (ids.length === 0) return;

      const placeholders = ids.map(() => "?").join(",");
      const sql = `DELETE FROM music WHERE _id IN (${placeholders})`;
      db.query(sql).run(...ids);

      const idSet = new Set(ids);
      for (let i = MusicDao.ALL.length - 1; i >= 0; i--) {
        const item = MusicDao.ALL[i];
        if (item && idSet.has(item.getId())) {
          MusicDao.ALL.splice(i, 1);
        }
      }
      for (let i = MusicDao.ALL_TO_SEARCH.length - 1; i >= 0; i--) {
        const item = MusicDao.ALL_TO_SEARCH[i];
        if (item && idSet.has(item.getId())) {
          MusicDao.ALL_TO_SEARCH.splice(i, 1);
        }
      }
    } catch (error) {
      console.error(error);
    }
  }

  /**
   * Returns a copy of all loaded Music records.
   */
  public static getAll(): Music[] {
    MusicDao.getDb();
    return [...MusicDao.ALL];
  }

  /**
   * Alias for getAll().
   */
  public static findAll(): Music[] {
    return MusicDao.getAll();
  }

  /**
   * Returns a copy of all Music records optimized for search.
   */
  public static getAllToSearch(): Music[] {
    MusicDao.getDb();
    return [...MusicDao.ALL_TO_SEARCH];
  }

  /**
   * Returns the count of music records.
   */
  public static getSize(): number {
    MusicDao.getDb();
    return MusicDao.ALL_TO_SEARCH.length;
  }

  /**
   * Lazy lookup by id from memory cache.
   */
  public static getLazy(id: number): Music | null {
    MusicDao.getDb();
    return MusicDao.ALL.find((m) => m.getId() === id) ?? null;
  }

  /**
   * Lazy lookup for multiple ids from memory cache.
   */
  public static getLazyList(ids: number[]): Music[] {
    MusicDao.getDb();
    const idSet = new Set(ids);
    return MusicDao.ALL.filter((m) => idSet.has(m.getId()));
  }

  /**
   * Lazy lookup by id from memory search cache.
   */
  public static getLazyToSearch(id: number): Music | null {
    MusicDao.getDb();
    return MusicDao.ALL_TO_SEARCH.find((m) => m.getId() === id) ?? null;
  }

  /**
   * Lazy lookup for multiple ids from memory search cache.
   */
  public static getLazyToSearchList(ids: number[]): Music[] {
    MusicDao.getDb();
    const idSet = new Set(ids);
    return MusicDao.ALL_TO_SEARCH.filter((m) => idSet.has(m.getId()));
  }

  /**
   * Batch updates a list of music items in the database.
   */
  public static updateDatabase(list: Music[]): void {
    const ms = list.filter((m) => m.getId() > 0);
    const limit = 200;

    while (ms.length > 0) {
      const batch = ms.splice(0, Math.min(ms.length, limit));
      MusicDao.batchUpdate(batch);
    }

    MusicDao.updateAll();
  }

  /**
   * Reloads all music records from the database using equivalent ResultSet loop.
   */
  public static updateAll(): void {
    const db = getDatabase();
    const result: Music[] = [];
    const sql = "SELECT * FROM music";

    try {
      const rows = db.query(sql).all() as MusicRow[];
      for (const rs of rows) {
        result.push(Music.create(rs.music));
      }
    } catch (error) {
      console.error(error);
      return;
    }

    MusicDao.ALL.length = 0;
    MusicDao.ALL.push(...result);

    MusicDao.ALL_TO_SEARCH.length = 0;
    MusicDao.ALL_TO_SEARCH.push(...MusicDao.ALL.map((m) => m.getInstanceToSearch()));
  }

  /**
   * Batch update helper.
   */
  private static batchUpdate(list: Music[]): void {
    if (list.length === 0 || list.length > 200) {
      return;
    }

    const db = MusicDao.getDb();
    try {
      const placeholders = list.map(() => "(?, ?)").join(", ");
      const sql = `INSERT OR REPLACE INTO music VALUES ${placeholders}`;
      const params: (number | string)[] = [];

      for (let i = 0; i < list.length; i++) {
        const item = list[i];
        if (item) {
          params.push(item.getId(), item.getSerial());
        }
      }

      db.query(sql).run(...params);
    } catch (error) {
      console.error(error);
    }
  }

  /**
   * Checks if a music item with given title and artist exists.
   */
  public static contains(title: string, artist: string): boolean;
  public static contains(music: Music): boolean;
  public static contains(arg1: string | Music, arg2?: string): boolean {
    MusicDao.getDb();
    if (typeof arg1 === "object" && arg1 !== null) {
      const ms = arg1.getInstanceToSearch();
      return MusicDao.contains(ms.getTitle(), ms.getArtist());
    }

    const title = arg1;
    const artist = arg2 ?? "";
    const hash = Utils.removeAccent(`${title};${artist}`).toLowerCase();
    return MusicDao.ALL_TO_SEARCH.some(
      (m) => m && `${m.getTitle()};${m.getArtist()}` === hash
    );
  }

  // Instance methods mapping to static methods for convenience
  public find(id: number): Music | null {
    return MusicDao.find(id);
  }

  public findAll(): Music[] {
    return MusicDao.findAll();
  }

  public save(music: Music): boolean {
    return MusicDao.save(music);
  }

  public update(music: Music): boolean {
    return MusicDao.update(music);
  }

  public delete(id: number): boolean {
    return MusicDao.delete(id);
  }

  public get(id: number): Music | null {
    return MusicDao.get(id);
  }

  public getAll(): Music[] {
    return MusicDao.getAll();
  }

  public add(music: Music): boolean {
    return MusicDao.add(music);
  }

  public set(music: Music): boolean {
    return MusicDao.set(music);
  }

  public remove(m: Music | number): boolean {
    return MusicDao.remove(m);
  }
}

export default MusicDao;
