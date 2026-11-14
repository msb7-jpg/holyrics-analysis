/**
 * @module @slices/pptx/ui/services/pptx-service
 * @description Serviço cliente HTTP para listagem, ingestão e projeção de apresentações PPTX e PDF (RF-21).
 */

import { z } from 'zod'
import { httpClient } from '@ui/core/network/http-client'
import { PptxPresentationSchema, type PptxPresentation } from '../../contracts/pptx-contracts'

export const pptxService = {
  /**
   * Lista todas as apresentações PPTX e PDF disponíveis.
   *
   * @param signal - Sinal de cancelamento opcional.
   * @returns Lista de `PptxPresentation`.
   */
  async listAll(signal?: AbortSignal): Promise<PptxPresentation[]> {
    return httpClient.get('/api/pptx', {
      signal,
      schema: z.array(PptxPresentationSchema),
    })
  },

  /**
   * Ingere um arquivo `.pptx` ou `.pdf` convertendo slides e notas do orador em memória.
   *
   * @param filename - Nome do arquivo.
   * @param fileBase64 - Conteúdo binário em Base64.
   * @returns Apresentação processada e salva.
   */
  async ingestFile(filename: string, fileBase64: string): Promise<PptxPresentation> {
    return httpClient.post(
      '/api/pptx/ingest',
      { filename, fileBase64 },
      { schema: PptxPresentationSchema },
    )
  },

  /**
   * Remove uma apresentação do repositório.
   *
   * @param id - ID da apresentação.
   * @returns Lista atualizada.
   */
  async deletePresentation(id: string): Promise<PptxPresentation[]> {
    return httpClient.delete(`/api/pptx/${encodeURIComponent(id)}`, {
      schema: z.array(PptxPresentationSchema),
    })
  },
}
