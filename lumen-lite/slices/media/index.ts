/**
 * @module @slices/media
 * @description Fatia de Gestão de Mídias, Loops Contínuos e Player de Áudio com Web Audio API (RF-27, RF-28, RF-29).
 *
 * ## Responsabilidades:
 * - Catálogo e indexação de mídias para backgrounds de projeção (vídeos e imagens).
 * - Reprodução de trilhas de áudio ambiente pré/pós-culto e oração com equalizador gráfico 3-band (`BiquadFilterNode`).
 * - Roteamento REST `/api/media/*` com suporte a HTTP Range streaming de áudio e vídeo local.
 * - Motor reativo `useAudioEngine` com FFT em tempo real para visualizador de espectro sonoro.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos de troca de fundo de projeção (`SET_BACKGROUND`) e fades de áudio.
 * - **Consome:** Eventos de pânico e transições de tela da FSM.
 * - **Exporta:** Contratos, repositório, roteador REST, hooks de áudio e componentes de visualização.
 */

export type {
  MediaType,
  MediaItem,
  AudioTrack,
  AudioPlaylist,
  RepeatMode,
  EqualizerSettings,
  AudioEngineState,
} from './contracts/media-contracts'

export {
  MediaTypeSchema,
  MediaItemSchema,
  AudioTrackSchema,
  AudioPlaylistSchema,
  RepeatModeSchema,
  EqualizerSettingsSchema,
  AudioEngineStateSchema,
  DEFAULT_EQUALIZER_SETTINGS,
  DEFAULT_AUDIO_PLAYLISTS,
  DEFAULT_MEDIA_ITEMS,
} from './contracts/media-contracts'

export { MediaRepository } from './daemon/media-repository'
export type { MediaFilterOptions } from './daemon/media-repository'
export { handleMediaRoutes } from './daemon/routes/media-router'

export { useAudioEngine } from './ui/hooks/useAudioEngine'
export type { AudioEngineHookResult } from './ui/hooks/useAudioEngine'

export { AudioSpectrumVisualizer } from './ui/components/AudioSpectrumVisualizer'
export type { AudioSpectrumVisualizerProps } from './ui/components/AudioSpectrumVisualizer'

export { AudioPlayerPanel } from './ui/components/AudioPlayerPanel'
export type { AudioPlayerPanelProps } from './ui/components/AudioPlayerPanel'

export { MediaGridPanel } from './ui/components/MediaGridPanel'
export type { MediaGridPanelProps } from './ui/components/MediaGridPanel'
