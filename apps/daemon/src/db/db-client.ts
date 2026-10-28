import { Database } from 'bun:sqlite'

export class DbClient {
  private db: Database

  constructor(dbPath = 'lumenlite.db') {
    this.db = new Database(dbPath, { create: true })
    // Ativa WAL (Write-Ahead Logging) para concorrência de leitura e escrita sem travas
    this.db.run('PRAGMA journal_mode = WAL;')
    this.db.run('PRAGMA synchronous = NORMAL;')
    this.initTables()
  }

  private initTables(): void {
    this.db.run(`
      CREATE TABLE IF NOT EXISTS songs (
        id TEXT PRIMARY KEY,
        title TEXT NOT NULL,
        artist TEXT,
        content TEXT NOT NULL,
        updated_at INTEGER NOT NULL
      );
    `)

    // FTS5 para busca instantânea sub-milissegundo
    this.db.run(`
      CREATE VIRTUAL TABLE IF NOT EXISTS songs_fts USING fts5(
        title,
        content,
        content='songs',
        content_rowid='rowid'
      );
    `)
  }

  getDb(): Database {
    return this.db
  }

  close(): void {
    this.db.close()
  }
}
