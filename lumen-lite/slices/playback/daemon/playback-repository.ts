/**
 * @module @slices/playback/daemon/playback-repository
 * @description Repositório para persistência atômica de configurações de Playback Sincronizado (`playback-tracks.json` - RF-22).
 */

import { existsSync, mkdirSync, readFileSync, renameSync, writeFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { z } from 'zod'
import { PlaybackTrackSchema, type PlaybackTrack } from '../contracts/playback-contracts'

const DEFAULT_PLAYBACK_TRACKS: PlaybackTrack[] = [
  {
    id: 'pb-song-001',
    songId: 'song-001',
    title: 'Porque Ele Vive (Playback Sincronizado)',
    artist: 'Harpa Cristã',
    audioUrl: '/api/media/stream/audio-preludio-1',
    startWithTitle: true,
    startWithDurationMs: 2000,
    slideDurationsMs: [12000, 14000, 12000, 15000],
    updatedAt: Date.now(),
  },
]

/**
 * Repositório atômico de faixas de playback sincronizadas.
 */
export class PlaybackRepository {
  private readonly filePath: string

  /**
   * Inicializa o repositório apontando para o arquivo JSON de playbacks.
   *
   * @param customFilePath - Caminho customizado opcional para testes.
   */
  constructor(customFilePath?: string) {
    this.filePath = customFilePath || resolve(process.cwd(), 'data/playback-tracks.json')
    this.ensureSeed()
  }

  /**
   * Lista todas as faixas de playback cadastradas.
   *
   * @returns Lista validada de `PlaybackTrack`.
   */
  listAll(): PlaybackTrack[] {
    try {
      if (!existsSync(this.filePath)) {
        return DEFAULT_PLAYBACK_TRACKS
      }
      const rawContent = readFileSync(this.filePath, 'utf-8')
      const parsed = z.array(PlaybackTrackSchema).safeParse(JSON.parse(rawContent))
      return parsed.success ? parsed.data : DEFAULT_PLAYBACK_TRACKS
    } catch {
      return DEFAULT_PLAYBACK_TRACKS
    }
  }

  /**
   * Obtém o playback vinculado a um ID de música ou ID de playback.
   *
   * @param idOrSongId - ID do playback ou `songId`.
   * @returns Faixa encontrada ou null.
   */
  getBySongOrId(idOrSongId: string): PlaybackTrack | null {
    return (
      this.listAll().find(
        (track) => track.id === idOrSongId || track.songId === idOrSongId,
      ) || null
    )
  }

  /**
   * Cria ou atualiza uma faixa de playback sincronizada.
   *
   * @param track - Dados da faixa de playback.
   * @returns Faixa persistida.
   */
  upsertTrack(track: PlaybackTrack): PlaybackTrack {
    const validated = PlaybackTrackSchema.parse({
      ...track,
      updatedAt: Date.now(),
    })

    const others = this.listAll().filter(
      (item) => item.id !== validated.id && item.songId !== validated.songId,
    )
    const updatedList = [validated, ...others]
    this.writeAtomic(updatedList)
    return validated
  }

  private ensureSeed(): void {
    if (!existsSync(this.filePath)) {
      this.writeAtomic(DEFAULT_PLAYBACK_TRACKS)
    }
  }

  private writeAtomic(tracks: PlaybackTrack[]): void {
    const dir = dirname(this.filePath)
    if (!existsSync(dir)) {
      mkdirSync(dir, { recursive: true })
    }
    const tempPath = `${this.filePath}.tmp`
    writeFileSync(tempPath, JSON.stringify(tracks, null, 2), 'utf-8')
    renameSync(tempPath, this.filePath)
  }
}
