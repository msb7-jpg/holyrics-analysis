/**
 * @module @slices/playback/contracts/playback-contracts
 * @description Contratos Zod v4 para Playback Musical Sincronizado com Letras (RF-22).
 *
 * ## Referência no Código Original:
 * - `src/com/limagiran/holyrics/playback/PlayBack.java`:
 *   Modela `durations` (lista de durações em ms por estrofe), `startWith` (slide inicial de título)
 *   e `startWithDuration` (padrão 2000ms).
 */

import { z } from 'zod'

export const PLAYBACK_MIN_DURATION_MS = 500
export const PLAYBACK_MAX_DURATION_MS = 999000
export const PLAYBACK_DEFAULT_SLIDE_DURATION_MS = 15000
export const PLAYBACK_DEFAULT_START_WITH_DURATION_MS = 2000

/**
 * Faixa de Playback Sincronizado vinculando uma música do acervo a uma trilha de áudio e durações por estrofe.
 */
export const PlaybackTrackSchema = z.object({
  id: z.string(),
  songId: z.string(),
  title: z.string(),
  artist: z.string().optional(),
  audioUrl: z.string(),
  startWithTitle: z.boolean().default(true),
  startWithDurationMs: z.number().int().min(0).max(600000).default(PLAYBACK_DEFAULT_START_WITH_DURATION_MS),
  slideDurationsMs: z.array(z.number().int().min(PLAYBACK_MIN_DURATION_MS).max(PLAYBACK_MAX_DURATION_MS)),
  updatedAt: z.number(),
})

export type PlaybackTrack = z.infer<typeof PlaybackTrackSchema>

/**
 * Resolve deterministicamente qual índice de slide (`0 .. N-1`) deve estar ativo em determinado instante `elapsedMs` do áudio.
 *
 * @param track - Configuração de tempos da faixa de playback.
 * @param elapsedMs - Tempo decorrido da faixa de áudio em milissegundos.
 * @returns Índice 0-based da estrofe ativa (ou -1 caso ainda esteja na introdução `startWithDurationMs`).
 */
export function resolvePlaybackSlideIndex(track: PlaybackTrack, elapsedMs: number): number {
  if (elapsedMs < 0 || track.slideDurationsMs.length === 0) {
    return 0
  }

  let cursorMs = track.startWithTitle ? track.startWithDurationMs : 0
  if (elapsedMs < cursorMs) {
    return 0
  }

  for (let idx = 0; idx < track.slideDurationsMs.length; idx++) {
    const duration = track.slideDurationsMs[idx]
    if (elapsedMs < cursorMs + duration) {
      return idx
    }
    cursorMs += duration
  }

  return track.slideDurationsMs.length - 1
}
