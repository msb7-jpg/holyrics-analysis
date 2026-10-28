import { Database } from 'bun:sqlite'

/**
 * Cliente de persistência SQLite em modo WAL (Write-Ahead Logging).
 * Gerencia a conexão com o banco e inicializa as tabelas com índices FTS5.
 */
export class DbClient {
  private db: Database

  /**
   * Inicializa a conexão SQLite e executa migrações estruturais básicas.
   *
   * @param dbPath - Caminho do arquivo de banco no sistema de arquivos.
   */
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

  /**
   * Retorna a instância ativa do banco de dados SQLite.
   *
   * @returns Instância do Database bun:sqlite.
   */
  getDb(): Database {
    return this.db
  }

  /**
   * Encerra a conexão com o banco de dados.
   */
  close(): void {
    this.db.close()
  }
}
