import { Database } from 'bun:sqlite'

/**
 * Cliente de persistência SQLite em modo WAL (Write-Ahead Logging).
 * Gerencia a conexão com o banco e inicializa configurações de concorrência.
 */
export class DbClient {
  private database: Database

  /**
   * Inicializa a conexão SQLite com WAL ativo.
   *
   * @param dbPath - Caminho do arquivo de banco no sistema de arquivos.
   */
  constructor(dbPath = 'lumenlite.db') {
    this.database = new Database(dbPath, { create: true })
    // Ativa WAL (Write-Ahead Logging) para concorrência de leitura e escrita sem travas
    this.database.run('PRAGMA journal_mode = WAL;')
    this.database.run('PRAGMA synchronous = NORMAL;')
  }

  /**
   * Retorna a instância ativa do banco de dados SQLite.
   *
   * @returns Instância do Database bun:sqlite.
   */
  getDb(): Database {
    return this.database
  }

  /**
   * Encerra a conexão com o banco de dados.
   */
  close(): void {
    this.database.close()
  }
}
