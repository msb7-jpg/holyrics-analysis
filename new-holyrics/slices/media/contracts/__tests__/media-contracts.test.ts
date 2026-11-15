import { describe, it, expect } from 'bun:test'
import {
  MediaItemSchema,
  AudioPlaylistSchema,
  EqualizerSettingsSchema,
  AudioEngineStateSchema,
  DEFAULT_MEDIA_ITEMS,
  DEFAULT_AUDIO_PLAYLISTS,
  DEFAULT_EQUALIZER_SETTINGS,
} from '../media-contracts'

describe('Media Contracts (slices/media/contracts)', () => {
  it('inicia listas padrão vazias (zero mocks) e valida itens reais com Zod v4', () => {
    expect(DEFAULT_MEDIA_ITEMS.length).toBe(0)
    const sampleItem = {
      id: 'media-loop-1',
      name: 'Loop Céu Noturno',
      path: '/assets/media/loop.mp4',
      type: 'VIDEO',
      sizeBytes: 102400,
      mimeType: 'video/mp4',
      tags: ['video', 'mp4'],
    }
    const parseResult = MediaItemSchema.safeParse(sampleItem)
    expect(parseResult.success).toBe(true)
  })

  it('inicia playlists padrão vazias (zero mocks) e valida playlist real com Zod v4', () => {
    expect(DEFAULT_AUDIO_PLAYLISTS.length).toBe(0)
    const samplePlaylist = {
      id: 'playlist-custom',
      name: 'Recepção',
      repeatMode: 'all',
      shuffle: false,
      tracks: [
        {
          id: 'track-1',
          title: 'Instrumental 1',
          artist: 'Local',
          durationSeconds: 180,
          url: '/api/media/stream/media-1',
          tags: ['mp3'],
        },
      ],
    }
    const parseResult = AudioPlaylistSchema.safeParse(samplePlaylist)
    expect(parseResult.success).toBe(true)
  })

  it('valida e impõe limites de decibéis no equalizador (-12dB a +12dB)', () => {
    const valid = EqualizerSettingsSchema.safeParse(DEFAULT_EQUALIZER_SETTINGS)
    expect(valid.success).toBe(true)

    const boosted = EqualizerSettingsSchema.safeParse({ low: 6, mid: -2, high: 4.5 })
    expect(boosted.success).toBe(true)

    const invalidTooHigh = EqualizerSettingsSchema.safeParse({ low: 15, mid: 0, high: 0 })
    expect(invalidTooHigh.success).toBe(false)

    const invalidTooLow = EqualizerSettingsSchema.safeParse({ low: 0, mid: -18, high: 0 })
    expect(invalidTooLow.success).toBe(false)
  })

  it('valida o estado do motor de áudio com valores padrão seguros', () => {
    const defaultState = AudioEngineStateSchema.parse({})
    expect(defaultState.status).toBe('idle')
    expect(defaultState.volume).toBe(0.8)
    expect(defaultState.repeatMode).toBe('all')
    expect(defaultState.eq.low).toBe(0)
  })
})
