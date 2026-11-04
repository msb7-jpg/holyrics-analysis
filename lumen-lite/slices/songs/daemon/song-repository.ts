import { Database } from 'bun:sqlite'
import type { Song } from '../contracts/song-contracts'
import { SongSchema } from '../contracts/song-contracts'

export interface SongSearchResult {
  id: string
  title: string
  artist?: string
  snippet: string
  rank?: number
}

/**
 * Repositório de músicas com persistência SQLite e índice FTS5 (BM25).
 */
export class SongRepository {
  private database: Database

  /**
   * Inicializa o repositório e suas tabelas com índices FTS5.
   *
   * @param database - Instância ativa da conexão bun:sqlite.
   */
  constructor(database: Database) {
    this.database = database
    this.initTables()
  }

  private initTables(): void {
    // Tabela principal de músicas
    this.database.run(`
      CREATE TABLE IF NOT EXISTS songs (
        id TEXT PRIMARY KEY,
        title TEXT NOT NULL,
        artist TEXT,
        bpm INTEGER,
        time_signature TEXT,
        sections_json TEXT NOT NULL,
        arrangement_json TEXT,
        updated_at INTEGER NOT NULL
      );
    `)

    // Tabela FTS5 com unindexed id para performance de lookup
    this.database.run(`
      CREATE VIRTUAL TABLE IF NOT EXISTS songs_fts USING fts5(
        song_id UNINDEXED,
        title,
        artist,
        content,
        tokenize='unicode61 remove_diacritics 2'
      );
    `)
  }

  /**
   * Salva ou atualiza uma música completa e atualiza o índice FTS5.
   *
   * @param song - Estrutura da música a ser persistida.
   */
  upsertSong(song: Song): void {
    SongSchema.parse(song)

    const now = Date.now()
    const sectionsJson = JSON.stringify(song.sections)
    const arrangementJson = song.defaultArrangement ? JSON.stringify(song.defaultArrangement) : null

    // Monta o texto puro para busca FTS5 (todas as linhas das estrofes)
    const fullContent = song.sections
      .flatMap((section) => section.lines)
      .join('\n')

    const transaction = this.database.transaction(() => {
      // Upsert na tabela canônica
      this.database
        .prepare(
          `
        INSERT INTO songs (id, title, artist, bpm, time_signature, sections_json, arrangement_json, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(id) DO UPDATE SET
          title = excluded.title,
          artist = excluded.artist,
          bpm = excluded.bpm,
          time_signature = excluded.time_signature,
          sections_json = excluded.sections_json,
          arrangement_json = excluded.arrangement_json,
          updated_at = excluded.updated_at
      `
        )
        .run(
          song.id,
          song.title,
          song.artist ?? null,
          song.bpm ?? null,
          song.timeSignature ?? null,
          sectionsJson,
          arrangementJson,
          now
        )

      // Atualiza o índice FTS5 (remove anterior e insere novo)
      this.database.prepare('DELETE FROM songs_fts WHERE song_id = ?').run(song.id)
      this.database
        .prepare('INSERT INTO songs_fts (song_id, title, artist, content) VALUES (?, ?, ?, ?)')
        .run(song.id, song.title, song.artist ?? '', fullContent)
    })

    transaction()
  }

  /**
   * Busca músicas instantaneamente pelo título, artista ou letra via FTS5 e BM25.
   *
   * @param query - Termos de busca (ex: "porque ele vive", "graça").
   * @param limit - Máximo de resultados (default 20).
   * @returns Lista de resultados classificados por relevância BM25 com snippets.
   */
  search(query: string, limit = 20): SongSearchResult[] {
    const trimmed = query.trim()
    if (!trimmed) {
      // Retorna as músicas mais recentes caso a query esteja vazia
      const rows = this.database
        .prepare<
          { id: string; title: string; artist: string | null },
          [number]
        >('SELECT id, title, artist FROM songs ORDER BY updated_at DESC LIMIT ?')
        .all(limit)

      return rows.map((row) => ({
        id: row.id,
        title: row.title,
        artist: row.artist ?? undefined,
        snippet: '',
      }))
    }

    // Sanitiza e formata tokens para FTS5 com suporte a busca por prefixo (*)
    const ftsQuery = trimmed
      .replace(/["*]/g, '')
      .split(/\s+/)
      .filter(Boolean)
      .map((term) => `"${term}"*`)
      .join(' ')

    if (!ftsQuery) return []

    try {
      const rows = this.database
        .prepare<
          {
            song_id: string
            title: string
            artist: string
            rank: number
            snippet: string
          },
          [string, number]
        >(
          `
        SELECT 
          song_id,
          title,
          artist,
          bm25(songs_fts) AS rank,
          snippet(songs_fts, 3, '<mark>', '</mark>', '...', 10) AS snippet
        FROM songs_fts
        WHERE songs_fts MATCH ?
        ORDER BY rank
        LIMIT ?
      `
        )
        .all(ftsQuery, limit)

      return rows.map((row) => ({
        id: row.song_id,
        title: row.title,
        artist: row.artist || undefined,
        snippet: row.snippet,
        rank: row.rank,
      }))
    } catch {
      // Fallback gracioso com LIKE se a query FTS5 tiver sintaxe atípica
      const rows = this.database
        .prepare<
          { id: string; title: string; artist: string | null },
          [string, string, number]
        >(
          `SELECT id, title, artist FROM songs 
           WHERE title LIKE ? OR artist LIKE ? 
           LIMIT ?`
        )
        .all(`%${trimmed}%`, `%${trimmed}%`, limit)

      return rows.map((row) => ({
        id: row.id,
        title: row.title,
        artist: row.artist ?? undefined,
        snippet: '',
      }))
    }
  }

  /**
   * Obtém uma música completa pelo ID.
   *
   * @param id - Identificador único da música.
   * @returns Objeto Song estruturado ou null se não for encontrado.
   */
  getById(id: string): Song | null {
    const row = this.database
      .prepare<
        {
          id: string
          title: string
          artist: string | null
          bpm: number | null
          time_signature: string | null
          sections_json: string
          arrangement_json: string | null
        },
        [string]
      >('SELECT * FROM songs WHERE id = ?')
      .get(id)

    if (!row) return null

    return {
      id: row.id,
      title: row.title,
      artist: row.artist ?? undefined,
      bpm: row.bpm ?? undefined,
      timeSignature: row.time_signature ?? undefined,
      sections: JSON.parse(row.sections_json),
      defaultArrangement: row.arrangement_json ? JSON.parse(row.arrangement_json) : undefined,
    }
  }

  /**
   * Retorna a contagem total de músicas cadastradas.
   *
   * @returns Número total de registros na tabela de músicas.
   */
  count(): number {
    const res = this.database.prepare<{ count: number }, []>('SELECT COUNT(*) as count FROM songs').get()
    return res?.count ?? 0
  }

  /**
   * Popula a base com músicas padrão caso o acervo esteja vazio.
   *
   * @param seeds - Lista de músicas a serem inseridas caso o banco esteja limpo.
   */
  seedIfEmpty(seeds: Song[]): void {
    if (this.count() === 0 && seeds.length > 0) {
      for (const song of seeds) {
        this.upsertSong(song)
      }
    }
  }
}
