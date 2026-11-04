/**
 * @module @slices/bible/ui/services/bible-service
 * @description Serviço de rede para lookup de referências, busca FTS5 e consulta de versões bíblicas.
 */

import { z } from 'zod'
import { httpClient } from '@ui/core/network/http-client'
import {
  BibleLookupResponseSchema,
  BibleSearchResultSchema,
  BibleVersionSummarySchema,
  type BibleLookupResponse,
  type BibleSearchResult,
  type BibleVersionSummary,
} from '../../contracts/bible-contracts'

export const bibleService = {
  /**
   * Tenta resolver uma referência bíblica (ex: "jo 3 16", "sl 23:1-4").
   * Retorna null se não for reconhecida como referência válida, prevenindo erros ruidosos no console.
   *
   * @param reference - Texto digitado da referência bíblica.
   * @param version - Tradução bíblica desejada (padrão: ACF).
   * @param signal - AbortSignal para cancelamento da requisição.
   * @returns Objeto BibleLookupResponse ou null se inválida.
   */
  async lookup(
    reference: string,
    version = 'ACF',
    signal?: AbortSignal
  ): Promise<BibleLookupResponse | null> {
    if (!reference.trim()) return null

    try {
      return await httpClient.get<BibleLookupResponse>('/api/bible/lookup', {
        searchParams: { ref: reference.trim(), version },
        signal,
        schema: BibleLookupResponseSchema,
      })
    } catch {
      return null
    }
  },

  /**
   * Executa busca textual FTS5 em todo o texto das Escrituras.
   *
   * @param query - Palavra-chave ou frase a buscar.
   * @param limit - Quantidade máxima de versículos.
   * @param version - Versão bíblica para a pesquisa.
   * @param signal - Sinal de cancelamento.
   * @returns Lista de versículos com snippets destacados.
   */
  async search(
    query: string,
    limit = 25,
    version = 'ACF',
    signal?: AbortSignal
  ): Promise<BibleSearchResult[]> {
    if (!query.trim()) return []

    return httpClient.get<BibleSearchResult[]>('/api/bible/search', {
      searchParams: { q: query.trim(), limit, version },
      signal,
      schema: z.array(BibleSearchResultSchema),
    })
  },

  /**
   * Obtém a lista de versões e traduções bíblicas instaladas localmente no SQLite.
   *
   * @param signal - Sinal de cancelamento.
   * @returns Lista de versões bíblicas ativas.
   */
  async listVersions(signal?: AbortSignal): Promise<BibleVersionSummary[]> {
    return httpClient.get<BibleVersionSummary[]>('/api/bible/versions', {
      signal,
      schema: z.array(BibleVersionSummarySchema),
    })
  },
}
