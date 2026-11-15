/**
 * @module @slices/songs/ui/services/songs-service
 * @description Serviço assíncrono para operações de rede relacionadas ao acervo de músicas, importador Holyrics (RF-11) e histórico (RF-12).
 */

import { z } from 'zod'
import { httpClient } from '@ui/core/network/http-client'
import {
  SongHistoryEntrySchema,
  SongImportReportSchema,
  SongSchema,
  SongSearchResultSchema,
  SongUsageStatSchema,
  type LegacyHolyricsSong,
  type Song,
  type SongHistoryEntry,
  type SongImportReport,
  type SongSearchResult,
  type SongUsageStat,
} from '../../contracts/song-contracts'

export const songsService = {
  /**
   * Busca músicas por título, artista ou trecho de letra no SQLite FTS5.
   *
   * @param query - Termo de busca textual.
   * @param limit - Quantidade máxima de resultados (padrão: 20).
   * @param signal - AbortSignal para cancelamento automático.
   * @returns Lista de resultados resumidos com snippets destacados.
   */
  async search(query: string, limit = 20, signal?: AbortSignal): Promise<SongSearchResult[]> {
    return httpClient.get<SongSearchResult[]>('/api/songs', {
      searchParams: { q: query, limit },
      signal,
      schema: z.array(SongSearchResultSchema),
    })
  },

  /**
   * Obtém a canção completa com todas as suas estrofes e metadados.
   *
   * @param songId - Identificador único da música.
   * @param signal - AbortSignal para cancelamento da requisição.
   * @returns Entidade Song com estrofes e ordem padrão.
   */
  async getById(songId: string, signal?: AbortSignal): Promise<Song> {
    return httpClient.get<Song>(`/api/songs/${encodeURIComponent(songId)}`, {
      signal,
      schema: SongSchema,
    })
  },

  /**
   * Cria ou atualiza uma música no acervo.
   *
   * @param song - Dados da música.
   * @param signal - Sinal de cancelamento da requisição.
   * @returns Música persistida.
   */
  async upsert(song: Song, signal?: AbortSignal): Promise<Song> {
    return httpClient.post<Song>('/api/songs', song, {
      signal,
      schema: SongSchema,
    })
  },

  /**
   * Importa um pacote de músicas legadas do Holyrics (via base64 ZIP ou lista JSON - RF-11).
   *
   * @param payload - Objeto contendo `zipBase64` ou array `songs`.
   * @param payload.zipBase64 - Conteúdo binário do `.zip`/`.hrm` codificado em Base64.
   * @param payload.songs - Lista direta de músicas no formato legado do Holyrics.
   * @returns Relatório de importação com músicas salvas.
   */
  async importHolyrics(payload: {
    zipBase64?: string
    songs?: LegacyHolyricsSong[]
  }): Promise<SongImportReport> {
    return httpClient.post<SongImportReport>('/api/songs/import', payload, {
      schema: SongImportReportSchema,
    })
  },

  /**
   * Obtém o histórico cronológico recente de músicas executadas nos cultos (RF-12).
   *
   * @param limit - Quantidade máxima de registros.
   * @param signal - Sinal de cancelamento opcional.
   * @returns Lista de execuções recentes.
   */
  async getHistory(limit = 50, signal?: AbortSignal): Promise<SongHistoryEntry[]> {
    return httpClient.get<SongHistoryEntry[]>('/api/songs/history', {
      searchParams: { limit },
      signal,
      schema: z.array(SongHistoryEntrySchema),
    })
  },

  /**
   * Obtém as estatísticas de frequência de execução de músicas (RF-12).
   *
   * @param limit - Quantidade máxima de músicas no ranking.
   * @param signal - Sinal de cancelamento opcional.
   * @returns Lista de estatísticas por música.
   */
  async getUsageStats(limit = 30, signal?: AbortSignal): Promise<SongUsageStat[]> {
    return httpClient.get<SongUsageStat[]>('/api/songs/history/stats', {
      searchParams: { limit },
      signal,
      schema: z.array(SongUsageStatSchema),
    })
  },
}
