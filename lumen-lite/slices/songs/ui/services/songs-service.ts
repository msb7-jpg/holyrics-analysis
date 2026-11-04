/**
 * @module @slices/songs/ui/services/songs-service
 * @description Serviço assíncrono para operações de rede relacionadas ao acervo de músicas.
 *
 * ## Responsabilidades:
 * - Realizar buscas FTS5 no acervo via HTTP client.
 * - Obter detalhes e estrofes de uma música por ID.
 * - Inserir ou atualizar músicas no banco de dados SQLite do daemon.
 */

import { z } from 'zod'
import { httpClient } from '@ui/core/network/http-client'
import {
  SongSchema,
  SongSearchResultSchema,
  type Song,
  type SongSearchResult,
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
}
