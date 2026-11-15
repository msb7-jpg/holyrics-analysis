import { describe, it, expect, beforeAll } from 'bun:test'
import { Database } from 'bun:sqlite'
import { resolve } from 'node:path'
import { existsSync } from 'node:fs'
import { BibleRepository } from '../bible-repository'
import { parseVerseReference } from '../verse-parser'
import { extractHolyricsBibFile } from '../bib-extractor'

describe('BibleRepository (FTS5 & SQLite WAL)', () => {
  let database: Database
  let repository: BibleRepository
  const bibCandidates = [
    resolve(import.meta.dir, '../../pt_acrf.bib'),
    resolve(import.meta.dir, '../../../../../holyrics-source/legacy-java/com/limagiran/bible/database/pt_acrf.bib'),
    resolve(import.meta.dir, '../../../../../src/com/limagiran/bible/database/pt_acrf.bib'),
  ]
  const bibFilePath = bibCandidates.find((candidate) => existsSync(candidate)) ?? bibCandidates[0]

  beforeAll(() => {
    database = new Database(':memory:')
    repository = new BibleRepository(database)
    repository.seedFromBibFile(bibFilePath, 'ACF', 'Almeida Corrigida e Fiel')
  })

  it('descriptografa e extrai dados de pt_acrf.bib do Holyrics', () => {
    const extracted = extractHolyricsBibFile(bibFilePath)
    expect(extracted.language).toBe('Português')
    expect(extracted.books.length).toBe(66)
    expect(extracted.verses.length).toBe(31106)
    expect(extracted.books[0].name).toBe('Gênesis')
    expect(extracted.books[42].name).toBe('João')
    expect(extracted.verses[0].text).toContain('No princípio criou Deus')
  })

  it('popula o banco de dados e recupera versículos por referência única', () => {
    expect(repository.hasBibles()).toBe(true)
    expect(repository.count('ACF')).toBe(31106)

    const reference = parseVerseReference('jo 3 16')
    expect(reference).not.toBeNull()

    const verses = repository.lookupReference(reference!, 'ACF')
    expect(verses.length).toBe(1)
    expect(verses[0].bookName).toBe('João')
    expect(verses[0].chapter).toBe(3)
    expect(verses[0].verse).toBe(16)
    expect(verses[0].text).toContain('Porque Deus amou o mundo de tal maneira')
  })

  it('recupera intervalos de versículos ordenados corretamente (Salmos 23:1-4)', () => {
    const reference = parseVerseReference('sl 23:1-4')
    expect(reference).not.toBeNull()

    const verses = repository.lookupReference(reference!, 'ACF')
    expect(verses.length).toBe(4)
    expect(verses[0].verse).toBe(1)
    expect(verses[0].text).toContain('O SENHOR é o meu pastor')
    expect(verses[1].verse).toBe(2)
    expect(verses[2].verse).toBe(3)
    expect(verses[3].verse).toBe(4)
  })

  it('busca instantânea via FTS5 encontra termos com snippets destacados', () => {
    const results = repository.search('luz do mundo', { versionId: 'ACF', limit: 5 })
    expect(results.length).toBeGreaterThan(0)
    expect(results[0].snippet).toContain('<mark')
    expect(results[0].reference).toBeDefined()
  })

  it('lista versões cadastradas com resumo estruturado', () => {
    const versions = repository.listVersions()
    expect(versions.length).toBe(1)
    expect(versions[0].id).toBe('ACF')
    expect(versions[0].language).toBe('Português')
    expect(versions[0].verseCount).toBe(31106)
  })
})
