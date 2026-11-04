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
  it('valida a lista padrão de mídias com Zod v4', () => {
    for (const item of DEFAULT_MEDIA_ITEMS) {
      const parseResult = MediaItemSchema.safeParse(item)
      expect(parseResult.success).toBe(true)
    }
  })

  it('valida as playlists de áudio padrão com Zod v4', () => {
    for (const playlist of DEFAULT_AUDIO_PLAYLISTS) {
      const parseResult = AudioPlaylistSchema.safeParse(playlist)
      expect(parseResult.success).toBe(true)
      expect(playlist.tracks.length).toBeGreaterThan(0)
    }
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
