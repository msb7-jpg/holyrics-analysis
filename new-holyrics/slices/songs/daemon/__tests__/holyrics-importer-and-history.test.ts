import { describe, expect, it } from 'bun:test'
import { Database } from 'bun:sqlite'
import { zipSync, strToU8 } from 'fflate'
import { cleanHolyricsTags, convertLegacySongToLumenSong } from '../importer/tag-cleaner'
import { extractSongsFromZipBuffer } from '../importer/holyrics-importer'
import { SongHistoryRepository } from '../song-history-repository'

describe('Holyrics Importer & Song History (RF-11, RF-12)', () => {
  it('cleans legacy Holyrics formatting tags and ## headers', () => {
    const dirty =
      '##n(CORO)\n<font color="#ff0000">Santo, Santo é o Senhor</font>\n<c>Digno de louvor</c>'
    const cleaned = cleanHolyricsTags(dirty)
    expect(cleaned).not.toContain('##n(CORO)')
    expect(cleaned).not.toContain('<font')
    expect(cleaned).not.toContain('<c>')
    expect(cleaned).toContain('Santo, Santo é o Senhor')
    expect(cleaned).toContain('Digno de louvor')
  })

  it('converts legacy Holyrics JSON song and extracts from .zip archive', () => {
    const legacyJson = {
      title: 'Castelo Forte',
      artist: 'Martinho Lutero',
      lyrics: 'Castelo forte é nosso Deus\nEspada e bom escudo\n\nTriunfará na batalha',
      formatting: '##n(V1)\n##n(CORO)',
      order: [0, 1],
      note: '',
      language: 'pt-BR',
    }

    const converted = convertLegacySongToLumenSong(legacyJson)
    expect(converted.title).toBe('Castelo Forte')
    expect(converted.artist).toBe('Martinho Lutero')
    expect(converted.sections.length).toBe(2)
    expect(converted.sections[0]?.lines).toEqual([
      'Castelo forte é nosso Deus',
      'Espada e bom escudo',
    ])
    expect(converted.sections[0]?.tags).toEqual(['V1'])
    expect(converted.sections[1]?.tags).toEqual(['CORO'])

    const zipped = zipSync({
      'music/castelo.json': strToU8(JSON.stringify(legacyJson)),
    })
    const extracted = extractSongsFromZipBuffer(zipped)
    expect(extracted.length).toBe(1)
    expect(extracted[0]?.title).toBe('Castelo Forte')
  })

  it('records song projection history with 30-minute deduplication window and usage stats', () => {
    const database = new Database(':memory:')
    const historyRepo = new SongHistoryRepository(database)
    const baseTime = 1_700_000_000_000

    // First play
    const firstRecord = historyRepo.recordPlay(
      'song-1',
      'Santo, Santo, Santo',
      'Reginald Heber',
      baseTime,
    )
    expect(firstRecord).not.toBeNull()

    // Duplicate play 5 minutes later (within 30m window)
    const duplicateRecord = historyRepo.recordPlay(
      'song-1',
      'Santo, Santo, Santo',
      'Reginald Heber',
      baseTime + 5 * 60 * 1000,
    )
    expect(duplicateRecord).toBeNull()

    // Second valid play 35 minutes later
    const secondRecord = historyRepo.recordPlay(
      'song-1',
      'Santo, Santo, Santo',
      'Reginald Heber',
      baseTime + 35 * 60 * 1000,
    )
    expect(secondRecord).not.toBeNull()

    const recent = historyRepo.listRecent(10)
    expect(recent.length).toBe(2)
    expect(recent[0]?.title).toBe('Santo, Santo, Santo')

    const stats = historyRepo.getUsageStats(10)
    expect(stats.length).toBe(1)
    expect(stats[0]?.playCount).toBe(2)
  })
})
