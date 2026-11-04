import { describe, it, expect, afterAll, beforeAll } from 'bun:test'
import { join } from 'node:path'
import { existsSync, unlinkSync, mkdirSync, writeFileSync, rmSync } from 'node:fs'
import { MediaRepository } from '../media-repository'
import { DEFAULT_AUDIO_PLAYLISTS, DEFAULT_MEDIA_ITEMS } from '../../contracts/media-contracts'

describe('MediaRepository (slices/media/daemon)', () => {
  const testPlaylistsPath = join(process.cwd(), 'slices/media/daemon/__tests__/test-media-playlists.json')
  const testMediaDir = join(process.cwd(), 'slices/media/daemon/__tests__/test-media-dir')

  beforeAll(() => {
    if (!existsSync(testMediaDir)) {
      mkdirSync(testMediaDir, { recursive: true })
    }
    // Cria arquivos mock para testar a varredura
    writeFileSync(join(testMediaDir, 'background_loop.mp4'), 'mock-video-binary')
    writeFileSync(join(testMediaDir, 'cross_vector.png'), 'mock-image-binary')
    writeFileSync(join(testMediaDir, 'ambient_organ.mp3'), 'mock-audio-binary')
  })

  afterAll(() => {
    if (existsSync(testPlaylistsPath)) {
      unlinkSync(testPlaylistsPath)
    }
    if (existsSync(testMediaDir)) {
      rmSync(testMediaDir, { recursive: true, force: true })
    }
  })

  it('carrega mídias padrão e gera media-playlists.json se inexistente', () => {
    const repo = new MediaRepository(testPlaylistsPath, testMediaDir)
    const playlists = repo.listPlaylists()

    expect(playlists.length).toBe(DEFAULT_AUDIO_PLAYLISTS.length)
    expect(existsSync(testPlaylistsPath)).toBe(true)

    const allMedia = repo.listMedia()
    expect(allMedia.length).toBeGreaterThanOrEqual(DEFAULT_MEDIA_ITEMS.length)
  })

  it('filtra mídias por tipo VIDEO e IMAGE', () => {
    const repo = new MediaRepository(testPlaylistsPath, testMediaDir)
    const videoItems = repo.listMedia({ type: 'VIDEO' })
    const imageItems = repo.listMedia({ type: 'IMAGE' })

    expect(videoItems.length).toBeGreaterThan(0)
    expect(videoItems.every((item) => item.type === 'VIDEO')).toBe(true)

    expect(imageItems.length).toBeGreaterThan(0)
    expect(imageItems.every((item) => item.type === 'IMAGE')).toBe(true)
  })

  it('filtra mídias por busca textual', () => {
    const repo = new MediaRepository(testPlaylistsPath, testMediaDir)
    const searchResults = repo.listMedia({ query: 'nuvens' })

    expect(searchResults.length).toBe(1)
    expect(searchResults[0].id).toBe('bg-video-sky-clouds')
  })

  it('adiciona faixa a uma playlist existente com persistência atômica', () => {
    const repo = new MediaRepository(testPlaylistsPath, testMediaDir)
    const targetPlaylistId = DEFAULT_AUDIO_PLAYLISTS[0].id

    const newTrack = {
      id: 'custom-track-cello',
      title: 'Solo de Violoncelo em Ré Maior',
      artist: 'Worship Ensemble',
      durationSeconds: 215,
      url: '/assets/audio/cello_re_maior.mp3',
      tags: ['cordas', 'clássico'],
    }

    const updated = repo.addTrackToPlaylist(targetPlaylistId, newTrack)
    expect(updated.tracks.some((track) => track.id === 'custom-track-cello')).toBe(true)

    // Recarrega do disco para atestar a persistência
    const reloadedRepo = new MediaRepository(testPlaylistsPath, testMediaDir)
    const reloadedPlaylist = reloadedRepo.getPlaylistById(targetPlaylistId)
    expect(reloadedPlaylist?.tracks.some((track) => track.id === 'custom-track-cello')).toBe(true)
  })
})
