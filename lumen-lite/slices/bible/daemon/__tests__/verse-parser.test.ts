import { describe, it, expect } from 'bun:test'
import { parseVerseReference } from '../verse-parser'

describe('Bible Micro-Parser', () => {
  it('reconhece referências abreviadas com espaço "jo 3 16"', () => {
    const res = parseVerseReference('jo 3 16')
    expect(res).not.toBeNull()
    expect(res?.book).toBe('João')
    expect(res?.chapter).toBe(3)
    expect(res?.verseStart).toBe(16)
    expect(res?.normalized).toBe('João 3:16')
  })

  it('reconhece referências com intervalo "1co 13:4-8"', () => {
    const res = parseVerseReference('1co 13:4-8')
    expect(res).not.toBeNull()
    expect(res?.book).toBe('1 Coríntios')
    expect(res?.chapter).toBe(13)
    expect(res?.verseStart).toBe(4)
    expect(res?.verseEnd).toBe(8)
    expect(res?.normalized).toBe('1 Coríntios 13:4-8')
  })

  it('retorna null para formatos inválidos', () => {
    expect(parseVerseReference('apenas um texto qualquer')).toBeNull()
  })
})
