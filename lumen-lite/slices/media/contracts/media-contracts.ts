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
 * Coleção de playlists litúrgicas padrão fornecidas de fábrica.
 */
export const DEFAULT_AUDIO_PLAYLISTS: AudioPlaylist[] = [
  {
    id: 'playlist-prelude',
    name: 'Prelúdio & Recepção',
    description: 'Trilhas suaves e contemplativas para o momento de entrada e recepção da congregação',
    repeatMode: 'all',
    shuffle: true,
    tracks: [
      {
        id: 'track-peaceful-piano',
        title: 'Paz Profunda (Piano Acústico)',
        artist: 'Lumen Worship Ensemble',
        durationSeconds: 245,
        url: '/assets/audio/prelude_peaceful_piano.mp3',
        tags: ['piano', 'prelúdio', 'suave'],
      },
      {
        id: 'track-gentle-strings',
        title: 'Céus Abertos (Cordas & Ambient Pad)',
        artist: 'Lumen Worship Ensemble',
        durationSeconds: 310,
        url: '/assets/audio/prelude_gentle_strings.mp3',
        tags: ['cordas', 'pad', 'contemplação'],
      },
      {
        id: 'track-acoustic-grace',
        title: 'Graça Infinita (Violão Acústico)',
        artist: 'Lumen Worship Ensemble',
        durationSeconds: 198,
        url: '/assets/audio/prelude_acoustic_grace.mp3',
        tags: ['violão', 'acústico', 'manhã'],
      },
    ],
  },
  {
    id: 'playlist-prayer',
    name: 'Momento de Oração & Intercessão',
    description: 'Pads contínuos e texturas etéreas sem ritmo marcado para fundo de orações',
    repeatMode: 'all',
    shuffle: false,
    tracks: [
      {
        id: 'track-ethereal-pad',
        title: 'Luz Celestial (Warm Worship Pad)',
        artist: 'Lumen Atmosphere',
        durationSeconds: 480,
        url: '/assets/audio/prayer_warm_pad.mp3',
        tags: ['pad', 'oração', 'ambiente'],
      },
      {
        id: 'track-still-waters',
        title: 'Águas Tranquilas (Drone Contínuo em D)',
        artist: 'Lumen Atmosphere',
        durationSeconds: 600,
        url: '/assets/audio/prayer_still_waters.mp3',
        tags: ['drone', 'meditação', 'intercessão'],
      },
    ],
  },
  {
    id: 'playlist-postlude',
    name: 'Pós-Culto & Despedida',
    description: 'Harmonias de celebração e encorajamento para a saída da congregação',
    repeatMode: 'all',
    shuffle: true,
    tracks: [
      {
        id: 'track-joyful-reflection',
        title: 'Bênção e Paz (Piano & Orquestra)',
        artist: 'Lumen Worship Ensemble',
        durationSeconds: 275,
        url: '/assets/audio/postlude_joyful.mp3',
        tags: ['despedida', 'celebração', 'orquestra'],
      },
    ],
  },
]

/**
 * Banco de mídias de vídeo e imagens padrão para backgrounds de projeção.
 */
export const DEFAULT_MEDIA_ITEMS: MediaItem[] = [
  {
    id: 'bg-video-particles',
    name: 'Partículas Douradas Especulares (Loop)',
    path: '/assets/media/backgrounds/particles_ambient.mp4',
    type: 'VIDEO',
    sizeBytes: 15420000,
    mimeType: 'video/mp4',
    durationSeconds: 30,
    resolution: '1920x1080',
    tags: ['loop', 'ouro', 'partículas', 'louvor'],
  },
  {
    id: 'bg-video-sky-clouds',
    name: 'Nuvens Suaves em Câmera Lenta (Loop)',
    path: '/assets/media/backgrounds/clouds_slow.mp4',
    type: 'VIDEO',
    sizeBytes: 24100000,
    mimeType: 'video/mp4',
    durationSeconds: 45,
    resolution: '1920x1080',
    tags: ['loop', 'nuvens', 'céu', 'adoração'],
  },
  {
    id: 'bg-video-abstract-waves',
    name: 'Ondas Abstratas Slate Pro (Loop)',
    path: '/assets/media/backgrounds/slate_waves.mp4',
    type: 'VIDEO',
    sizeBytes: 18200000,
    mimeType: 'video/mp4',
    durationSeconds: 25,
    resolution: '1920x1080',
    tags: ['loop', 'abstrato', 'sóbrio', 'minimalista'],
  },
  {
    id: 'bg-image-mountains',
    name: 'Cordilheira Nebulosa ao Amanhecer',
    path: '/assets/media/backgrounds/mountains_dawn.jpg',
    type: 'IMAGE',
    sizeBytes: 2450000,
    mimeType: 'image/jpeg',
    resolution: '1920x1080',
    tags: ['imagem', 'montanha', 'natureza', 'púlpito'],
  },
  {
    id: 'bg-image-cross-minimal',
    name: 'Cruz Monolítica em Ardósia',
    path: '/assets/media/backgrounds/cross_slate.jpg',
    type: 'IMAGE',
    sizeBytes: 1890000,
    mimeType: 'image/jpeg',
    resolution: '1920x1080',
    tags: ['imagem', 'cruz', 'sóbrio', 'solene'],
  },
]
