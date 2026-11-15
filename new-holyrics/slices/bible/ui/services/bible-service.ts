/**
 * @module @slices/bible/ui/services/bible-service
 * @description Serviço de rede para lookup de referências (simples e Dual Version - RF-14), busca FTS5 e gerenciador de downloads bíblicos (RF-17).
 */

import { z } from 'zod'
import { httpClient } from '@ui/core/network/http-client'
import {
  AvailableBibleDownloadItemSchema,
  BibleLookupResponseSchema,
  BibleSearchResultSchema,
  BibleVersionSummarySchema,
  type AvailableBibleDownloadItem,
  type BibleLookupResponse,
  type BibleSearchResult,
  type BibleVersionSummary,
} from '../../contracts/bible-contracts'

export const bibleService = {
  /**
   * Tenta resolver uma referência bíblica (ex: "jo 3 16", "sl 23:1-4") com suporte opcional a 2ª versão (Dual Version).
   *
   * @param reference - Texto digitado da referência bíblica.
   * @param version - Tradução bíblica principal (padrão: ACF).
   * @param signal - AbortSignal para cancelamento da requisição.
   * @param dualVersion - Segunda tradução opcional para tela dividida (RF-14).
   * @returns Objeto BibleLookupResponse ou null se inválida.
   */
  async lookup(
    reference: string,
    version = 'ACF',
    signal?: AbortSignal,
    dualVersion?: string,
  ): Promise<BibleLookupResponse | null> {
    if (!reference.trim()) return null

    try {
      return await httpClient.get<BibleLookupResponse>('/api/bible/lookup', {
        searchParams: {
          ref: reference.trim(),
          version,
          dualVersion: dualVersion || undefined,
        },
        signal,
        schema: BibleLookupResponseSchema,
      })
    } catch {
      return null
    }
  },

  /**
   * Alias semântico para lookup de referência bíblica.
   *
   * @param reference - Texto digitado da referência bíblica.
   * @param version - Tradução bíblica desejada.
   * @returns Resposta validada ou null.
   */
  async lookupReference(reference: string, version = 'ACF'): Promise<BibleLookupResponse | null> {
    return this.lookup(reference, version)
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
    signal?: AbortSignal,
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

  /**
   * Lista o catálogo completo de versões bíblicas disponíveis para download/instalação (RF-17).
   *
   * @param signal - Sinal de cancelamento.
   * @returns Catálogo com status de instalação.
   */
  async listAvailableDownloads(signal?: AbortSignal): Promise<AvailableBibleDownloadItem[]> {
    return httpClient.get<AvailableBibleDownloadItem[]>('/api/bible/downloads', {
      signal,
      schema: z.array(AvailableBibleDownloadItemSchema),
    })
  },

  /**
   * Instala uma nova tradução bíblica em tempo de execução sem reiniciar a aplicação (RF-17).
   *
   * @param versionId - Sigla da versão (ex: 'NVI', 'ARA', 'NAA').
   * @param versionName - Nome opcional da versão.
   * @returns Dados da versão instalada.
   */
  async installVersion(
    versionId: string,
    versionName?: string,
  ): Promise<AvailableBibleDownloadItem> {
    return httpClient.post<AvailableBibleDownloadItem>(
      '/api/bible/downloads/install',
      { versionId, versionName },
      { schema: AvailableBibleDownloadItemSchema },
    )
  },

  /**
   * Remove uma tradução bíblica instalada.
   *
   * @param versionId - Sigla da versão a desinstalar.
   * @returns Catálogo atualizado.
   */
  async removeVersion(versionId: string): Promise<AvailableBibleDownloadItem[]> {
    return httpClient.delete<AvailableBibleDownloadItem[]>(
      `/api/bible/downloads/${encodeURIComponent(versionId)}`,
      { schema: z.array(AvailableBibleDownloadItemSchema) },
    )
  },
}
