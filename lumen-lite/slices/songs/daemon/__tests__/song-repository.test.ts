import { describe, it, expect, beforeEach } from 'bun:test'
import { Database } from 'bun:sqlite'
import { SongRepository } from '../song-repository'
import type { Song } from '../../contracts/song-contracts'

describe('SongRepository (FTS5 & SQLite)', () => {
  let db: Database
  let repo: SongRepository

  const mockSong1: Song = {
    id: 'song-1',
    title: 'Porque Ele Vive',
    artist: 'Harpa Cristã',
    sections: [
      {
        id: 'sec-1',
        index: 1,
        lines: ['Deus enviou Seu Filho amado', 'Para morrer em meu lugar', 'Na cruz pagou por meus pecados'],
      },
      {
        id: 'sec-2',
        index: 2,
        lines: ['Porque Ele vive, eu posso crer no amanhã', 'Porque Ele vive, temor não há'],
      },
    ],
    defaultArrangement: ['sec-1', 'sec-2'],
  }

  const mockSong2: Song = {
    id: 'song-2',
    title: 'Bondade de Deus',
    artist: 'Isaías Saad',
    sections: [
      {
        id: 'sec-1',
        index: 1,
        lines: ['Te amo, ó Deus', 'Tua graça nunca falha'],
      },
    ],
    defaultArrangement: ['sec-1'],
  }

  beforeEach(() => {
    db = new Database(':memory:')
    repo = new SongRepository(db)
  })

  it('insere músicas e recupera por ID corretamente', () => {
    repo.upsertSong(mockSong1)
    const retrieved = repo.getById('song-1')

    expect(retrieved).not.toBeNull()
    expect(retrieved?.title).toBe('Porque Ele Vive')
    expect(retrieved?.artist).toBe('Harpa Cristã')
    expect(retrieved?.sections).toHaveLength(2)
    expect(retrieved?.sections[1].lines[0]).toBe('Porque Ele vive, eu posso crer no amanhã')
  })

  it('busca instantânea via FTS5 encontra por trecho da letra com snippet', () => {
    repo.upsertSong(mockSong1)
    repo.upsertSong(mockSong2)

    // Busca pela palavra "pecados", que está apenas na estrofe 1 de "Porque Ele Vive"
    const results = repo.search('pecados')
    expect(results).toHaveLength(1)
    expect(results[0].id).toBe('song-1')
    expect(results[0].snippet).toContain('<mark>pecados</mark>')
  })

  it('busca por parte do título ou artista com insensitive e prefixo', () => {
    repo.upsertSong(mockSong1)
    repo.upsertSong(mockSong2)

    const byArtist = repo.search('Isaías')
    expect(byArtist).toHaveLength(1)
    expect(byArtist[0].id).toBe('song-2')

    const byPrefix = repo.search('Porq')
    expect(byPrefix).toHaveLength(1)
    expect(byPrefix[0].id).toBe('song-1')
  })

  it('suporta seed inicial quando a base está vazia', () => {
    expect(repo.count()).toBe(0)
    repo.seedIfEmpty([mockSong1, mockSong2])
    expect(repo.count()).toBe(2)

    // Não duplica se rodar de novo
    repo.seedIfEmpty([mockSong1])
    expect(repo.count()).toBe(2)
  })
})
