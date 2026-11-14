/**
 * @module @slices/playback
 * @description Fatia de Playback Musical Sincronizado com Letras (RF-22).
 *
 * ## Responsabilidades:
 * - Execução de faixas de playback sincronizadas com estrofes e slides individuais (`PlayBack.java`).
 * - Sincronização em tempo real baseada em durações por milissegundo (`resolvePlaybackSlideIndex`).
 * - Editor visual de marcação de tempos de troca de estrofe.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `NAV_GOTO` e `PRESENT_ITEM` sincronizados para o `@lumen/core`.
 * - **Consome:** Estruturas de canção de `@slices/songs`.
 * - **Exporta para UI:** `PlaybackSyncModal` e `playbackService`.
 */

export const SLICE_NAME = 'playback'

export type { PlaybackTrack } from './contracts/playback-contracts'
export {
  PLAYBACK_MIN_DURATION_MS,
  PLAYBACK_MAX_DURATION_MS,
  PLAYBACK_DEFAULT_SLIDE_DURATION_MS,
  PLAYBACK_DEFAULT_START_WITH_DURATION_MS,
  PlaybackTrackSchema,
  resolvePlaybackSlideIndex,
} from './contracts/playback-contracts'

export { PlaybackRepository } from './daemon/playback-repository'
export { handlePlaybackRoutes } from './daemon/routes/playback-router'
export { playbackService } from './ui/services/playback-service'
export { PlaybackSyncModal } from './ui/components/PlaybackSyncModal'
