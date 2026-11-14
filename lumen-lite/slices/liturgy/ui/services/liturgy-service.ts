/**
 * @module @slices/liturgy/ui/services/liturgy-service
 * @description Serviço assíncrono para operações de rede do roteiro de culto.
 */

import { httpClient } from '@ui/core/network/http-client'
import {
  LiturgyFileSchema,
  type LiturgyFile,
  type LiturgyItem,
  type QuickTextPayload,
} from '../../contracts/liturgy-schema'

export const liturgyService = {
  /**
   * Obtém a liturgia ativa configurada no daemon.
   *
   * @param signal - AbortSignal para cancelamento automático.
   * @returns Arquivo de liturgia validado.
   */
  async getCurrentLiturgy(signal?: AbortSignal): Promise<LiturgyFile> {
    return httpClient.get<LiturgyFile>('/api/liturgy/current', {
      signal,
      schema: LiturgyFileSchema,
    })
  },

  /**
   * Salva uma nova versão completa da liturgia no disco.
   *
   * @param liturgy - Objeto de liturgia a salvar.
   * @param signal - Sinal de cancelamento.
   * @returns Liturgia salva e validada pelo servidor.
   */
  async saveLiturgy(liturgy: LiturgyFile, signal?: AbortSignal): Promise<LiturgyFile> {
    return httpClient.post<LiturgyFile>('/api/liturgy/save', liturgy, {
      signal,
      schema: LiturgyFileSchema,
    })
  },

  /**
   * Atualiza o título do culto ativo.
   *
   * @param title - Novo título do evento/culto.
   * @param signal - Sinal de cancelamento.
   * @returns Liturgia atualizada.
   */
  async updateTitle(title: string, signal?: AbortSignal): Promise<LiturgyFile> {
    return httpClient.post<LiturgyFile>(
      '/api/liturgy/title',
      { title },
      {
        signal,
        schema: LiturgyFileSchema,
      },
    )
  },

  /**
   * Reordena os itens da liturgia com base em uma nova lista ordenada de IDs.
   *
   * @param itemIds - Lista ordenada dos identificadores dos itens.
   * @param signal - Sinal de cancelamento.
   * @returns Liturgia reordenada.
   */
  async reorderItems(itemIds: string[], signal?: AbortSignal): Promise<LiturgyFile> {
    return httpClient.post<LiturgyFile>(
      '/api/liturgy/reorder',
      { itemIds },
      {
        signal,
        schema: LiturgyFileSchema,
      },
    )
  },

  /**
   * Adiciona um item polimórfico ao roteiro do culto.
   *
   * @param item - Novo item litúrgico.
   * @param insertIndex - Posição de inserção opcional.
   * @param signal - Sinal de cancelamento.
   * @returns Liturgia atualizada.
   */
  async addItem(item: LiturgyItem, insertIndex?: number, signal?: AbortSignal): Promise<LiturgyFile> {
    return httpClient.post<LiturgyFile>(
      '/api/liturgy/item',
      { item, insertIndex },
      {
        signal,
        schema: LiturgyFileSchema,
      },
    )
  },

  /**
   * Remove um item da liturgia pelo ID.
   *
   * @param itemId - Identificador do item a ser removido.
   * @param signal - Sinal de cancelamento.
   * @returns Liturgia atualizada sem o item.
   */
  async removeItem(itemId: string, signal?: AbortSignal): Promise<LiturgyFile> {
    return httpClient.delete<LiturgyFile>(`/api/liturgy/item/${encodeURIComponent(itemId)}`, {
      signal,
      schema: LiturgyFileSchema,
    })
  },

  /**
   * Dispara a projeção imediata de um texto avulso de emergência.
   *
   * @param payload - Parâmetros do texto e categoria.
   * @param signal - Sinal de cancelamento.
   * @returns Confirmação do despacho.
   */
  async presentQuickText(payload: QuickTextPayload, signal?: AbortSignal): Promise<{ dispatched: boolean }> {
    return httpClient.post<{ dispatched: boolean }>('/api/liturgy/quick-text', payload, {
      signal,
    })
  },
}
