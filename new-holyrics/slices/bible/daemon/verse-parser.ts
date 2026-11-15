/**
 * @module @slices/bible/daemon/verse-parser
 * @description Micro-parser sintático léxico e determinístico para referências bíblicas em português.
 *
 * ## Responsabilidades:
 * - Analisar referências textuais informais (ex: "jo 3 16", "sl 23:1-4", "1co 13 4-8", "gn 1 1").
 * - Mapear nomes abreviados e populares para o cânon oficial dos 66 livros bíblicos com número canônico.
 * - Fornecer helper de detecção rápida para roteamento no console do operador.
 *
 * ## Comunicação e Fronteiras:
 * - Utilizado por `BibleRepository`, pelo roteador da API (`/api/bible/lookup`) e pelo hook de busca da UI.
 */

import type { ParsedVerseRef } from '../contracts/bible-contracts'

interface BookDefinition {
  number: number
  name: string
  abbreviation: string
}

const CANONICAL_BOOKS: Record<string, BookDefinition> = {
  // Antigo Testamento
  gn: { number: 1, name: 'Gênesis', abbreviation: 'Gn' },
  genesis: { number: 1, name: 'Gênesis', abbreviation: 'Gn' },
  ex: { number: 2, name: 'Êxodo', abbreviation: 'Êx' },
  exodo: { number: 2, name: 'Êxodo', abbreviation: 'Êx' },
  lv: { number: 3, name: 'Levítico', abbreviation: 'Lv' },
  levitico: { number: 3, name: 'Levítico', abbreviation: 'Lv' },
  nm: { number: 4, name: 'Números', abbreviation: 'Nm' },
  numeros: { number: 4, name: 'Números', abbreviation: 'Nm' },
  dt: { number: 5, name: 'Deuteronômio', abbreviation: 'Dt' },
  deuteronomio: { number: 5, name: 'Deuteronômio', abbreviation: 'Dt' },
  js: { number: 6, name: 'Josué', abbreviation: 'Js' },
  josue: { number: 6, name: 'Josué', abbreviation: 'Js' },
  jz: { number: 7, name: 'Juízes', abbreviation: 'Jz' },
  juizes: { number: 7, name: 'Juízes', abbreviation: 'Jz' },
  rt: { number: 8, name: 'Rute', abbreviation: 'Rt' },
  rute: { number: 8, name: 'Rute', abbreviation: 'Rt' },
  '1sm': { number: 9, name: '1 Samuel', abbreviation: '1Sm' },
  '1samuel': { number: 9, name: '1 Samuel', abbreviation: '1Sm' },
  '2sm': { number: 10, name: '2 Samuel', abbreviation: '2Sm' },
  '2samuel': { number: 10, name: '2 Samuel', abbreviation: '2Sm' },
  '1rs': { number: 11, name: '1 Reis', abbreviation: '1Rs' },
  '1reis': { number: 11, name: '1 Reis', abbreviation: '1Rs' },
  '2rs': { number: 12, name: '2 Reis', abbreviation: '2Rs' },
  '2reis': { number: 12, name: '2 Reis', abbreviation: '2Rs' },
  '1cr': { number: 13, name: '1 Crônicas', abbreviation: '1Cr' },
  '1cronicas': { number: 13, name: '1 Crônicas', abbreviation: '1Cr' },
  '2cr': { number: 14, name: '2 Crônicas', abbreviation: '2Cr' },
  '2cronicas': { number: 14, name: '2 Crônicas', abbreviation: '2Cr' },
  ed: { number: 15, name: 'Esdras', abbreviation: 'Ed' },
  esdras: { number: 15, name: 'Esdras', abbreviation: 'Ed' },
  ne: { number: 16, name: 'Neemias', abbreviation: 'Ne' },
  neemias: { number: 16, name: 'Neemias', abbreviation: 'Ne' },
  et: { number: 17, name: 'Ester', abbreviation: 'Et' },
  ester: { number: 17, name: 'Ester', abbreviation: 'Et' },
  job: { number: 18, name: 'Jó', abbreviation: 'Jó' },
  sl: { number: 19, name: 'Salmos', abbreviation: 'Sl' },
  salmo: { number: 19, name: 'Salmos', abbreviation: 'Sl' },
  salmos: { number: 19, name: 'Salmos', abbreviation: 'Sl' },
  pv: { number: 20, name: 'Provérbios', abbreviation: 'Pv' },
  proverbio: { number: 20, name: 'Provérbios', abbreviation: 'Pv' },
  proverbios: { number: 20, name: 'Provérbios', abbreviation: 'Pv' },
  ec: { number: 21, name: 'Eclesiastes', abbreviation: 'Ec' },
  eclesiastes: { number: 21, name: 'Eclesiastes', abbreviation: 'Ec' },
  ct: { number: 22, name: 'Cânticos', abbreviation: 'Ct' },
  canticos: { number: 22, name: 'Cânticos', abbreviation: 'Ct' },
  cantares: { number: 22, name: 'Cânticos', abbreviation: 'Ct' },
  is: { number: 23, name: 'Isaías', abbreviation: 'Is' },
  isaias: { number: 23, name: 'Isaías', abbreviation: 'Is' },
  jr: { number: 24, name: 'Jeremias', abbreviation: 'Jr' },
  jeremias: { number: 24, name: 'Jeremias', abbreviation: 'Jr' },
  lm: { number: 25, name: 'Lamentações', abbreviation: 'Lm' },
  lamentacoes: { number: 25, name: 'Lamentações', abbreviation: 'Lm' },
  ez: { number: 26, name: 'Ezequiel', abbreviation: 'Ez' },
  ezequiel: { number: 26, name: 'Ezequiel', abbreviation: 'Ez' },
  dn: { number: 27, name: 'Daniel', abbreviation: 'Dn' },
  daniel: { number: 27, name: 'Daniel', abbreviation: 'Dn' },
  os: { number: 28, name: 'Oséias', abbreviation: 'Os' },
  oseias: { number: 28, name: 'Oséias', abbreviation: 'Os' },
  jl: { number: 29, name: 'Joel', abbreviation: 'Jl' },
  joel: { number: 29, name: 'Joel', abbreviation: 'Jl' },
  am: { number: 30, name: 'Amós', abbreviation: 'Am' },
  amos: { number: 30, name: 'Amós', abbreviation: 'Am' },
  ob: { number: 31, name: 'Obadias', abbreviation: 'Ob' },
  obadias: { number: 31, name: 'Obadias', abbreviation: 'Ob' },
  jn: { number: 32, name: 'Jonas', abbreviation: 'Jn' },
  jonas: { number: 32, name: 'Jonas', abbreviation: 'Jn' },
  mq: { number: 33, name: 'Miquéias', abbreviation: 'Mq' },
  miqueias: { number: 33, name: 'Miquéias', abbreviation: 'Mq' },
  na: { number: 34, name: 'Naum', abbreviation: 'Na' },
  naum: { number: 34, name: 'Naum', abbreviation: 'Na' },
  hc: { number: 35, name: 'Habacuque', abbreviation: 'Hc' },
  habacuque: { number: 35, name: 'Habacuque', abbreviation: 'Hc' },
  sf: { number: 36, name: 'Sofonias', abbreviation: 'Sf' },
  sofonias: { number: 36, name: 'Sofonias', abbreviation: 'Sf' },
  ag: { number: 37, name: 'Ageu', abbreviation: 'Ag' },
  ageu: { number: 37, name: 'Ageu', abbreviation: 'Ag' },
  zc: { number: 38, name: 'Zacarias', abbreviation: 'Zc' },
  zacarias: { number: 38, name: 'Zacarias', abbreviation: 'Zc' },
  ml: { number: 39, name: 'Malaquias', abbreviation: 'Ml' },
  malaquias: { number: 39, name: 'Malaquias', abbreviation: 'Ml' },

  // Novo Testamento
  mt: { number: 40, name: 'Mateus', abbreviation: 'Mt' },
  mateus: { number: 40, name: 'Mateus', abbreviation: 'Mt' },
  mc: { number: 41, name: 'Marcos', abbreviation: 'Mc' },
  marcos: { number: 41, name: 'Marcos', abbreviation: 'Mc' },
  lc: { number: 42, name: 'Lucas', abbreviation: 'Lc' },
  lucas: { number: 42, name: 'Lucas', abbreviation: 'Lc' },
  jo: { number: 43, name: 'João', abbreviation: 'Jo' },
  joao: { number: 43, name: 'João', abbreviation: 'Jo' },
  at: { number: 44, name: 'Atos', abbreviation: 'At' },
  atos: { number: 44, name: 'Atos', abbreviation: 'At' },
  rm: { number: 45, name: 'Romanos', abbreviation: 'Rm' },
  romanos: { number: 45, name: 'Romanos', abbreviation: 'Rm' },
  '1co': { number: 46, name: '1 Coríntios', abbreviation: '1Co' },
  '1corintios': { number: 46, name: '1 Coríntios', abbreviation: '1Co' },
  '2co': { number: 47, name: '2 Coríntios', abbreviation: '2Co' },
  '2corintios': { number: 47, name: '2 Coríntios', abbreviation: '2Co' },
  gl: { number: 48, name: 'Gálatas', abbreviation: 'Gl' },
  galatas: { number: 48, name: 'Gálatas', abbreviation: 'Gl' },
  ef: { number: 49, name: 'Efésios', abbreviation: 'Ef' },
  efesios: { number: 49, name: 'Efésios', abbreviation: 'Ef' },
  fp: { number: 50, name: 'Filipenses', abbreviation: 'Fp' },
  fl: { number: 50, name: 'Filipenses', abbreviation: 'Fp' },
  filipenses: { number: 50, name: 'Filipenses', abbreviation: 'Fp' },
  cl: { number: 51, name: 'Colossenses', abbreviation: 'Cl' },
  colossenses: { number: 51, name: 'Colossenses', abbreviation: 'Cl' },
  '1ts': { number: 52, name: '1 Tessalonicenses', abbreviation: '1Ts' },
  '1tessalonicenses': { number: 52, name: '1 Tessalonicenses', abbreviation: '1Ts' },
  '2ts': { number: 53, name: '2 Tessalonicenses', abbreviation: '2Ts' },
  '2tessalonicenses': { number: 53, name: '2 Tessalonicenses', abbreviation: '2Ts' },
  '1tm': { number: 54, name: '1 Timóteo', abbreviation: '1Tm' },
  '1timoteo': { number: 54, name: '1 Timóteo', abbreviation: '1Tm' },
  '2tm': { number: 55, name: '2 Timóteo', abbreviation: '2Tm' },
  '2timoteo': { number: 55, name: '2 Timóteo', abbreviation: '2Tm' },
  tt: { number: 56, name: 'Tito', abbreviation: 'Tt' },
  tito: { number: 56, name: 'Tito', abbreviation: 'Tt' },
  fm: { number: 57, name: 'Filemom', abbreviation: 'Fm' },
  flm: { number: 57, name: 'Filemom', abbreviation: 'Fm' },
  filemom: { number: 57, name: 'Filemom', abbreviation: 'Fm' },
  hb: { number: 58, name: 'Hebreus', abbreviation: 'Hb' },
  hebreus: { number: 58, name: 'Hebreus', abbreviation: 'Hb' },
  tg: { number: 59, name: 'Tiago', abbreviation: 'Tg' },
  tiago: { number: 59, name: 'Tiago', abbreviation: 'Tg' },
  '1pe': { number: 60, name: '1 Pedro', abbreviation: '1Pe' },
  '1pedro': { number: 60, name: '1 Pedro', abbreviation: '1Pe' },
  '2pe': { number: 61, name: '2 Pedro', abbreviation: '2Pe' },
  '2pedro': { number: 61, name: '2 Pedro', abbreviation: '2Pe' },
  '1jo': { number: 62, name: '1 João', abbreviation: '1Jo' },
  '1joao': { number: 62, name: '1 João', abbreviation: '1Jo' },
  '2jo': { number: 63, name: '2 João', abbreviation: '2Jo' },
  '2joao': { number: 63, name: '2 João', abbreviation: '2Jo' },
  '3jo': { number: 64, name: '3 João', abbreviation: '3Jo' },
  '3joao': { number: 64, name: '3 João', abbreviation: '3Jo' },
  jd: { number: 65, name: 'Judas', abbreviation: 'Jd' },
  judas: { number: 65, name: 'Judas', abbreviation: 'Jd' },
  ap: { number: 66, name: 'Apocalipse', abbreviation: 'Ap' },
  apocalipse: { number: 66, name: 'Apocalipse', abbreviation: 'Ap' },
}

/**
 * Normaliza uma chave de livro removendo espaços, pontuações e acentos.
 *
 * @param rawText - Texto bruto do livro.
 * @returns Chave normalizada para busca no dicionário canônico.
 */
function normalizeBookKey(rawText: string): string {
  return rawText
    .toLowerCase()
    .replace(/\s+/g, '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
}

/**
 * Micro-parser léxico determinístico de referências bíblicas.
 * Cobre entradas como "jo 3 16", "jo 3:16", "sl 23:1-4", "1co 13:4-8", "gn 1 1".
 *
 * @param input - String de entrada informal informada pelo usuário.
 * @returns Objeto estruturado com referência normalizada ou null se não for reconhecido.
 */
export function parseVerseReference(input: string): ParsedVerseRef | null {
  const cleanInput = input.trim().toLowerCase().replace(/\s+/g, ' ')
  // Suporta: "jo 3:16", "jo 3 16", "1 co 13:4-8", "1co 13 4-8", "sl 23 1"
  const matchResult = cleanInput.match(/^([1-3]?\s?[a-zçãéíóú]+)\s+(\d+)[:\s]+(\d+)(?:-(\d+))?$/i)

  if (!matchResult) return null

  const [, rawBook, rawChapter, rawVerseStart, rawVerseEnd] = matchResult
  const normalizedKey = normalizeBookKey(rawBook)
  const canonicalBook = CANONICAL_BOOKS[normalizedKey]

  const bookName = canonicalBook ? canonicalBook.name : rawBook
  const bookNumber = canonicalBook ? canonicalBook.number : undefined

  const chapter = parseInt(rawChapter, 10)
  const verseStart = parseInt(rawVerseStart, 10)
  const verseEnd = rawVerseEnd ? parseInt(rawVerseEnd, 10) : undefined

  if (chapter <= 0 || verseStart <= 0) return null
  if (verseEnd && verseEnd < verseStart) return null

  return {
    raw: input,
    normalized: `${bookName} ${chapter}:${verseStart}${verseEnd ? `-${verseEnd}` : ''}`,
    book: bookName,
    bookNumber,
    chapter,
    verseStart,
    verseEnd,
  }
}

/**
 * Verifica rapidamente se uma string de entrada aparenta ser uma referência bíblica.
 *
 * @param input - String de busca inserida no console.
 * @returns Booleano indicando se é uma referência bíblica válida.
 */
export function isBibleReference(input: string): boolean {
  return parseVerseReference(input) !== null
}
