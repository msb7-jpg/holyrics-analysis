/**
 * @module @slices/songs/daemon/song-history-repository
 * @description Repositório SQLite para gravação automática e relatório de execução de músicas no culto (RF-12).
 *
 * ## Referência no Código Original:
 * - `src/com/limagiran/holyrics/model/History.java`:
 *   Implementa a regra de janela de deduplicação de 30 minutos (`minutesBetweenLastPlayed <= 30`)
 *   para evitar registros repetidos quando o operador alterna estrofes da mesma música no culto.
 */

import type { Database } from 'bun:sqlite'
import type { SongHistoryEntry, SongUsageStat } from '../contracts/song-contracts'

/** Janela de deduplicação padrão de 30 minutos em milissegundos (idêntica a `History.java`). */
export const HISTORY_DEDUP_WINDOW_MS = 30 * 60 * 1000

/**
 * Repositório SQLite para histórico cronológico e estatísticas de frequência de hinos.
 */
export class SongHistoryRepository {
  private database: Database

  /**
   * Inicializa a tabela `song_history` e índices de consulta rápida.
   *
   * @param database - Conexão ativa do `bun:sqlite`.
   */
  constructor(database: Database) {
    this.database = database
    this.initTables()
  }

  private initTables(): void {
    this.database.run(`
      CREATE TABLE IF NOT EXISTS song_history (
        id TEXT PRIMARY KEY,
        song_id TEXT NOT NULL,
        title TEXT NOT NULL,
        artist TEXT,
        played_at INTEGER NOT NULL
      );
    `)

    this.database.run(`
      CREATE INDEX IF NOT EXISTS idx_song_history_lookup
      ON song_history (song_id, played_at DESC);
    `)
  }

  /**
   * Registra a execução de uma música no culto caso ela não tenha sido registrada nos últimos 30 minutos.
   *
   * @param songId - ID da música projetada.
   * @param title - Título da música.
   * @param artist - Artista opcional.
   * @param playedAt - Timestamp de execução (padrão `Date.now()`).
   * @returns Registro criado ou null caso tenha sido ignorado pela janela de deduplicação.
   */
  recordPlay(
    songId: string,
    title: string,
    artist?: string,
    playedAt: number = Date.now(),
  ): SongHistoryEntry | null {
    const lastEntry = this.database
      .query<{ played_at: number }, [string]>(
        'SELECT played_at FROM song_history WHERE song_id = ? ORDER BY played_at DESC LIMIT 1',
      )
      .get(songId)

    if (lastEntry && Math.abs(playedAt - lastEntry.played_at) <= HISTORY_DEDUP_WINDOW_MS) {
      return null
    }

    const entryId = `hist_${playedAt}_${Math.random().toString(36).slice(2, 7)}`
    this.database
      .prepare(
        'INSERT INTO song_history (id, song_id, title, artist, played_at) VALUES (?, ?, ?, ?, ?)',
      )
      .run(entryId, songId, title, artist ?? null, playedAt)

    return {
      id: entryId,
      songId,
      title,
      artist,
      playedAt,
    }
  }

  /**
   * Lista o histórico recente de músicas executadas nos cultos em ordem cronológica inversa.
   *
   * @param limit - Máximo de registros retornados (padrão 50).
   * @returns Lista de execuções registradas.
   */
  listRecent(limit = 50): SongHistoryEntry[] {
    const rows = this.database
      .query<
        { id: string; song_id: string; title: string; artist: string | null; played_at: number },
        [number]
      >('SELECT id, song_id, title, artist, played_at FROM song_history ORDER BY played_at DESC LIMIT ?')
      .all(limit)

    return rows.map((row) => ({
      id: row.id,
      songId: row.song_id,
      title: row.title,
      artist: row.artist ?? undefined,
      playedAt: row.played_at,
    }))
  }

  /**
   * Retorna o relatório de frequência de uso das músicas mais executadas (equivalente a `History.count` e `History.lastPlayed`).
   *
   * @param limit - Máximo de músicas no ranking (padrão 30).
   * @returns Lista ordenada por frequência de execução (`playCount DESC`).
   */
  getUsageStats(limit = 30): SongUsageStat[] {
    const rows = this.database
      .query<
        {
          song_id: string
          title: string
          artist: string | null
          play_count: number
          last_played_at: number
        },
        [number]
      >(
        `SELECT
          song_id,
          title,
          artist,
          COUNT(*) as play_count,
          MAX(played_at) as last_played_at
        FROM song_history
        GROUP BY song_id
        ORDER BY play_count DESC, last_played_at DESC
        LIMIT ?`,
      )
      .all(limit)

    return rows.map((row) => ({
      songId: row.song_id,
      title: row.title,
      artist: row.artist ?? undefined,
      playCount: row.play_count,
      lastPlayedAt: row.last_played_at,
    }))
  }
}
