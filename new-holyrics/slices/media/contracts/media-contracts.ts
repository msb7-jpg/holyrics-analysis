/**
 * @module @slices/media/contracts/media-contracts
 * @description Esquemas Zod v4 e definições tipadas para gestão de mídias, áudio ambiente e equalizador sonoro (RF-27, RF-28, RF-29).
 *
 * ## Responsabilidades:
 * - Validar catálogo de mídias locais (vídeos em loop, imagens de fundo e faixas de áudio).
 * - Tipar playlists litúrgicas de áudio para prelúdio, oração e pós-culto.
 * - Parametrizar nós de equalização de 3 bandas (BiquadFilterNode) e estado do reprodutor sonoro.
 *
 * ## Comunicação e Fronteiras:
 * - Consumido pelo MediaRepository, roteador HTTP `/api/media/*`, hook `useAudioEngine` e CLI `lumen`.
 */

import { z } from 'zod'

/**
 * Tipos de mídia suportados pelo ecossistema LumenLite.
 */
export const MediaTypeSchema = z.enum(['IMAGE', 'VIDEO', 'AUDIO'])
export type MediaType = z.infer<typeof MediaTypeSchema>

/**
 * Item atômico de mídia indexado no acervo local.
 */
export const MediaItemSchema = z.object({
  id: z.string().min(1),
  name: z.string().min(1),
  path: z.string().min(1),
  folder: z.string().optional(),
  type: MediaTypeSchema,
  sizeBytes: z.number().nonnegative(),
  mimeType: z.string().min(1),
  durationSeconds: z.number().nonnegative().optional(),
  resolution: z.string().optional(), // ex: "1920x1080"
  thumbnailUrl: z.string().optional(),
  tags: z.array(z.string()).default([]),
})
export type MediaItem = z.infer<typeof MediaItemSchema>

/**
 * Faixa de áudio individual para playlists litúrgicas.
 */
export const AudioTrackSchema = z.object({
  id: z.string().min(1),
  title: z.string().min(1),
  artist: z.string().default('Instrumental'),
  durationSeconds: z.number().nonnegative(),
  url: z.string().min(1),
  thumbnailUrl: z.string().optional(),
  tags: z.array(z.string()).default([]),
})
export type AudioTrack = z.infer<typeof AudioTrackSchema>

/**
 * Modo de repetição para execução de playlists de áudio.
 */
export const RepeatModeSchema = z.enum(['off', 'all', 'one'])
export type RepeatMode = z.infer<typeof RepeatModeSchema>

/**
 * Playlist litúrgica para momentos do culto.
 */
export const AudioPlaylistSchema = z.object({
  id: z.string().min(1),
  name: z.string().min(1),
  description: z.string().optional(),
  tracks: z.array(AudioTrackSchema).default([]),
  repeatMode: RepeatModeSchema.default('all'),
  shuffle: z.boolean().default(false),
})
export type AudioPlaylist = z.infer<typeof AudioPlaylistSchema>

/**
 * Parâmetros de equalização gráfica de 3 bandas em decibéis (-12dB a +12dB).
 */
export const EqualizerSettingsSchema = z.object({
  low: z.number().min(-12).max(12).default(0), // Frequência baixa ~120Hz (Graves)
  mid: z.number().min(-12).max(12).default(0), // Frequência média ~1000Hz (Médios)
  high: z.number().min(-12).max(12).default(0), // Frequência alta ~6000Hz (Agudos)
})
export type EqualizerSettings = z.infer<typeof EqualizerSettingsSchema>

/**
 * Estado reativo do reprodutor de áudio Web Audio API.
 */
export const AudioEngineStateSchema = z.object({
  status: z.enum(['idle', 'playing', 'paused', 'stopped']).default('idle'),
  currentTrackId: z.string().nullable().default(null),
  currentTime: z.number().nonnegative().default(0),
  duration: z.number().nonnegative().default(0),
  volume: z.number().min(0).max(1).default(0.8),
  eq: EqualizerSettingsSchema.default({ low: 0, mid: 0, high: 0 }),
  crossfadeSeconds: z.number().min(0).max(10).default(2),
  repeatMode: RepeatModeSchema.default('all'),
  shuffle: z.boolean().default(false),
})
export type AudioEngineState = z.infer<typeof AudioEngineStateSchema>

/**
 * Configurações padrão de equalizador plano.
 */
export const DEFAULT_EQUALIZER_SETTINGS: EqualizerSettings = {
  low: 0,
  mid: 0,
  high: 0,
}

/**
 * Coleção inicial de playlists de áudio (vazia por padrão — sem mocks fictícios).
 */
export const DEFAULT_AUDIO_PLAYLISTS: AudioPlaylist[] = []

/**
 * Banco inicial de mídias visuais (vazio por padrão — populado por arquivos reais em disco).
 */
export const DEFAULT_MEDIA_ITEMS: MediaItem[] = []
