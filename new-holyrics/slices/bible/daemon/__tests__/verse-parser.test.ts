import { describe, it, expect } from 'bun:test'
import { parseVerseReference, isBibleReference } from '../verse-parser'

describe('Bible Micro-Parser', () => {
  it('reconhece referências abreviadas com espaço "jo 3 16"', () => {
    const parsed = parseVerseReference('jo 3 16')
    expect(parsed).not.toBeNull()
    expect(parsed?.book).toBe('João')
    expect(parsed?.bookNumber).toBe(43)
    expect(parsed?.chapter).toBe(3)
    expect(parsed?.verseStart).toBe(16)
    expect(parsed?.verseEnd).toBeUndefined()
    expect(parsed?.normalized).toBe('João 3:16')
  })

  it('reconhece referências com intervalo "1co 13:4-8"', () => {
    const parsed = parseVerseReference('1co 13:4-8')
    expect(parsed).not.toBeNull()
    expect(parsed?.book).toBe('1 Coríntios')
    expect(parsed?.bookNumber).toBe(46)
    expect(parsed?.chapter).toBe(13)
    expect(parsed?.verseStart).toBe(4)
    expect(parsed?.verseEnd).toBe(8)
    expect(parsed?.normalized).toBe('1 Coríntios 13:4-8')
  })

  it('reconhece livros do Antigo Testamento como Gênesis e Salmos', () => {
    const genesis = parseVerseReference('gn 1 1')
    expect(genesis?.book).toBe('Gênesis')
    expect(genesis?.bookNumber).toBe(1)
    expect(genesis?.chapter).toBe(1)
    expect(genesis?.verseStart).toBe(1)

    const salmos = parseVerseReference('sl 23 1-6')
    expect(salmos?.book).toBe('Salmos')
    expect(salmos?.bookNumber).toBe(19)
    expect(salmos?.chapter).toBe(23)
    expect(salmos?.verseStart).toBe(1)
    expect(salmos?.verseEnd).toBe(6)
  })

  it('valida isBibleReference corretamente', () => {
    expect(isBibleReference('jo 3 16')).toBe(true)
    expect(isBibleReference('sl 23:1-4')).toBe(true)
    expect(isBibleReference('Grande é o Senhor')).toBe(false)
    expect(isBibleReference('')).toBe(false)
  })

  it('retorna null para formatos inválidos', () => {
    expect(parseVerseReference('')).toBeNull()
    expect(parseVerseReference('hino avulso')).toBeNull()
    expect(parseVerseReference('jo')).toBeNull()
    expect(parseVerseReference('jo -1 0')).toBeNull()
    expect(parseVerseReference('jo 3:10-5')).toBeNull() // verseEnd < verseStart
  })
})
