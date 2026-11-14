/**
 * @module @slices/playback/ui/services/playback-service
 * @description Serviço cliente HTTP para gerenciamento e sincronização de Playbacks Musicais (RF-22).
 */

import { z } from 'zod'
import { httpClient } from '@ui/core/network/http-client'
import { PlaybackTrackSchema, type PlaybackTrack } from '../../contracts/playback-contracts'

export const playbackService = {
  /**
   * Lista todas as faixas de playback sincronizadas.
   *
   * @param signal - Sinal de cancelamento opcional.
   * @returns Lista de `PlaybackTrack`.
   */
  async listAll(signal?: AbortSignal): Promise<PlaybackTrack[]> {
    return httpClient.get('/api/playback', {
      signal,
      schema: z.array(PlaybackTrackSchema),
    })
  },

  /**
   * Salva ou atualiza os tempos por estrofe de um playback.
   *
   * @param track - Configuração completa do playback.
   * @returns Faixa persistida.
   */
  async saveTrack(track: PlaybackTrack): Promise<PlaybackTrack> {
    return httpClient.post('/api/playback', track, {
      schema: PlaybackTrackSchema,
    })
  },
}
