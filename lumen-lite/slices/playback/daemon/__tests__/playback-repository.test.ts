import { describe, expect, it, afterAll } from 'bun:test'
import { existsSync, unlinkSync } from 'node:fs'
import { resolve } from 'node:path'
import { resolvePlaybackSlideIndex } from '../../contracts/playback-contracts'
import { PlaybackRepository } from '../playback-repository'

const TEST_PLAYBACK_JSON = resolve(import.meta.dir, 'test-playback-tracks.json')

describe('Playback Sync Repository & Timecode Resolver (RF-22)', () => {
  afterAll(() => {
    if (existsSync(TEST_PLAYBACK_JSON)) {
      unlinkSync(TEST_PLAYBACK_JSON)
    }
  })

  it('persists slide duration cues and resolves active slide index for audio playback', () => {
    const repository = new PlaybackRepository(TEST_PLAYBACK_JSON)

    const saved = repository.upsertTrack({
      id: 'pb-castelo',
      songId: 'song-castelo',
      title: 'Castelo Forte',
      artist: 'Martinho Lutero',
      audioUrl: '/media/castelo.mp3',
      startWithTitle: true,
      startWithDurationMs: 2000,
      slideDurationsMs: [10000, 15000, 20000],
      updatedAt: Date.now(),
    })

    // Intro (0..2000ms) -> slide 0
    expect(resolvePlaybackSlideIndex(saved, 500)).toBe(0)
    // First stanza (2000..12000ms) -> slide 0
    expect(resolvePlaybackSlideIndex(saved, 5000)).toBe(0)
    // Second stanza (12000..27000ms) -> slide 1
    expect(resolvePlaybackSlideIndex(saved, 15000)).toBe(1)
    // Third stanza (27000..47000ms) -> slide 2
    expect(resolvePlaybackSlideIndex(saved, 30000)).toBe(2)

    const fetched = repository.getBySongOrId('song-castelo')
    expect(fetched?.id).toBe('pb-castelo')
    expect(fetched?.slideDurationsMs).toEqual([10000, 15000, 20000])
  })
})
