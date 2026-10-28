import type { ParsedVerseRef } from '../contracts/bible-contracts'

// Mapeamento estático e rápido dos livros comuns em PT-BR
const BOOK_MAP: Record<string, string> = {
  gn: 'Gênesis', genesis: 'Gênesis',
  ex: 'Êxodo', exodo: 'Êxodo',
  sl: 'Salmos', salmos: 'Salmos', salmo: 'Salmos',
  pv: 'Provérbios', proverbios: 'Provérbios',
  is: 'Isaías', isaias: 'Isaías',
  mt: 'Mateus', mateus: 'Mateus',
  mc: 'Marcos', marcos: 'Marcos',
  lc: 'Lucas', lucas: 'Lucas',
  jo: 'João', joao: 'João',
  at: 'Atos', atos: 'Atos',
  rm: 'Romanos', romanos: 'Romanos',
  '1co': '1 Coríntios', '1 corintios': '1 Coríntios',
  '2co': '2 Coríntios', '2 corintios': '2 Coríntios',
  gl: 'Gálatas', galatas: 'Gálatas',
  ef: 'Efésios', efesios: 'Efésios',
  fp: 'Filipenses', filipenses: 'Filipenses',
  cl: 'Colossenses', colossenses: 'Colossenses',
  hb: 'Hebreus', hebreus: 'Hebreus',
  tg: 'Tiago', tiago: 'Tiago',
  '1pe': '1 Pedro', '1 pedro': '1 Pedro',
  '2pe': '2 Pedro', '2 pedro': '2 Pedro',
  '1jo': '1 João', '1 joao': '1 João',
  ap: 'Apocalipse', apocalipse: 'Apocalipse',
}

/**
 * Micro-parser nativo em TypeScript (< 60 linhas).
 * Cobre entradas rápidas: "jo 3 16", "jo 3:16", "sl 23:1-4", "1co 13:4-8".
 *
 * @param input - String de entrada contendo a referência bíblica informal.
 * @returns Objeto estruturado com livro, capítulo e versículos, ou `null` se não for reconhecido.
 */
export function parseVerseReference(input: string): ParsedVerseRef | null {
  const clean = input.trim().toLowerCase().replace(/\s+/g, ' ')
  // Ex: "jo 3:16-18" ou "jo 3 16" ou "1co 13 4-8"
  const match = clean.match(/^([1-3]?\s?[a-zçãéíóú]+)\s+(\d+)[:\s]+(\d+)(?:-(\d+))?$/i)

  if (!match) return null

  const [, rawBook, rawChapter, rawVerseStart, rawVerseEnd] = match
  const normalizedBookKey = rawBook.replace(/\s+/g, '').normalize('NFD').replace(/[\u0300-\u036f]/g, '')
  const bookName = BOOK_MAP[normalizedBookKey] || rawBook

  const chapter = parseInt(rawChapter, 10)
  const verseStart = parseInt(rawVerseStart, 10)
  const verseEnd = rawVerseEnd ? parseInt(rawVerseEnd, 10) : undefined

  return {
    raw: input,
    normalized: `${bookName} ${chapter}:${verseStart}${verseEnd ? `-${verseEnd}` : ''}`,
    book: bookName,
    chapter,
    verseStart,
    verseEnd,
  }
}
