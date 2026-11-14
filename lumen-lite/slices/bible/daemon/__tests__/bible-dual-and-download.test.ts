import { describe, expect, it } from 'bun:test'
import { Database } from 'bun:sqlite'
import { BibleRepository } from '../bible-repository'
import { BibleDownloader } from '../bible-downloader'
import { handleBibleRoutes } from '../routes/bible-router'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import type { ExtractedBibleData } from '../../contracts/bible-contracts'

describe('Bible Dual Version & Downloader (RF-14, RF-17)', () => {
  it('installs and removes Bible versions via BibleDownloader and resolves Dual Version via router', async () => {
    const database = new Database(':memory:')
    const bibleRepo = new BibleRepository(database)

    const sampleAcf: ExtractedBibleData = {
      language: 'pt-BR',
      version: 'ACF',
      books: [
        {
          number: 43,
          name: 'João',
          abbreviation: 'Jo',
          testament: 'Novo Testamento',
          testamentAbbreviation: 'NT',
        },
      ],
      verses: [
        {
          bookNumber: 43,
          chapter: 3,
          verse: 16,
          text: 'Porque Deus amou o mundo de tal maneira',
        },
      ],
    }
    const sampleKjv: ExtractedBibleData = {
      language: 'en-US',
      version: 'KJV',
      books: [
        {
          number: 43,
          name: 'John',
          abbreviation: 'Jn',
          testament: 'New Testament',
          testamentAbbreviation: 'NT',
        },
      ],
      verses: [
        {
          bookNumber: 43,
          chapter: 3,
          verse: 16,
          text: 'For God so loved the world',
        },
      ],
    }

    bibleRepo.seedFromExtractedData(sampleAcf, 'ACF', 'Almeida Corrigida e Fiel')

    const downloader = new BibleDownloader(bibleRepo)
    const initialCatalog = downloader.listAvailableDownloads()
    expect(initialCatalog.length).toBeGreaterThanOrEqual(5)

    // Install KJV dynamically
    const installedItem = downloader.installVersion({
      versionId: 'KJV',
      versionName: 'King James Version',
      extractedData: sampleKjv,
    })
    expect(installedItem.installed).toBe(true)
    expect(installedItem.verseCount).toBe(1)

    // Query Dual Version via router (ACF + KJV for João 3:16)
    const dualUrl = new URL(
      'http://localhost:4040/api/bible/lookup?ref=jo+3+16&version=ACF&dualVersion=KJV',
    )
    const response = await handleBibleRoutes(dualUrl, {
      bibleRepo,
    } as unknown as ApiContext)

    expect(response).not.toBeNull()
    expect(response?.status).toBe(200)
    const body = (await response?.json()) as {
      ok: boolean
      data: {
        version: string
        dualVersion: string
        verses: Array<{ text: string }>
        secondaryVerses: Array<{ text: string }>
      }
    }
    expect(body.ok).toBe(true)
    expect(body.data.version).toBe('ACF')
    expect(body.data.dualVersion).toBe('KJV')
    expect(body.data.verses[0]?.text).toContain('Porque Deus amou o mundo')
    expect(body.data.secondaryVerses[0]?.text).toContain('For God so loved the world')

    // Remove KJV
    const removed = bibleRepo.removeVersion('KJV')
    expect(removed).toBe(true)
  })
})
