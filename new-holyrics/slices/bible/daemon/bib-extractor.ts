/**
 * @module @slices/bible/daemon/bib-extractor
 * @description Extrator e conversor nativo de arquivos de Bíblia proprietários do Holyrics (.bib).
 *
 * ## Responsabilidades:
 * - Descriptografar arquivos `.bib` utilizando cifra AES-128-CBC com derivação SHA-256 e IV canônico do Holyrics.
 * - Descomprimir o payload binário via zlib inflate.
 * - Normalizar os dados estruturados de livros e versículos em modelos DTO fortemente tipados.
 *
 * ## Comunicação e Fronteiras:
 * - Consome arquivos `.bib` do sistema de arquivos local ou buffers de upload.
 * - Fornece dados normalizados para a camada de persistência `BibleRepository`.
 */

import { createDecipheriv, createHash } from 'node:crypto'
import { inflateSync } from 'node:zlib'
import { readFileSync } from 'node:fs'
import type { ExtractedBibleData, ExtractedBibleBook, ExtractedBibleVerse } from '../contracts/bible-contracts'

const HOLYRICS_BIB_KEY_RAW = '$%#@keyOBJ*&¨bib'
const HOLYRICS_IV_RAW = 'AAAAAAAAAAAAAAAA'

/**
 * Deriva a chave simétrica de 16 bytes padrão do Holyrics a partir da senha mestra.
 *
 * @returns Chave de 16 bytes derivada para AES-128.
 */
function deriveHolyricsKey(): Buffer {
  const hashDigest = createHash('sha256').update(HOLYRICS_BIB_KEY_RAW, 'utf8').digest('hex').toUpperCase()
  const passwordSlice = hashDigest.substring(0, 16)
  return Buffer.from(passwordSlice, 'utf8')
}

/**
 * Descriptografa e descomprime um arquivo `.bib` legado do Holyrics, retornando os dados tipados.
 *
 * @param bibFilePath - Caminho absoluto ou relativo do arquivo `.bib` no disco.
 * @returns Dados estruturados da Bíblia contendo idioma, versão, livros e todos os versículos.
 */
export function extractHolyricsBibFile(bibFilePath: string): ExtractedBibleData {
  const fileBuffer = readFileSync(bibFilePath)
  return extractHolyricsBibBuffer(fileBuffer)
}

/**
 * Descriptografa e descomprime um Buffer binário de arquivo `.bib`.
 *
 * @param encryptedBuffer - Buffer contendo os bytes criptografados do arquivo `.bib`.
 * @returns Dados estruturados da Bíblia normalizados.
 */
export function extractHolyricsBibBuffer(encryptedBuffer: Buffer): ExtractedBibleData {
  const secretKey = deriveHolyricsKey()
  const initializationVector = Buffer.from(HOLYRICS_IV_RAW, 'utf8')

  const decipher = createDecipheriv('aes-128-cbc', secretKey, initializationVector)
  const decryptedBytes = Buffer.concat([decipher.update(encryptedBuffer), decipher.final()])
  const decompressedString = inflateSync(decryptedBytes).toString('utf8')

  const rawJson = JSON.parse(decompressedString) as {
    language: string
    version: string
    books: Array<{
      id: string
      name: string
      abbrev: string
      testament: string
      testamentAbbrev: string
    }>
    verses: Array<{
      book: number
      chapter: number
      verse: number
      content: string
    }>
  }

  const books: ExtractedBibleBook[] = rawJson.books.map((bookItem) => ({
    number: parseInt(bookItem.id, 10),
    name: bookItem.name,
    abbreviation: bookItem.abbrev,
    testament: bookItem.testament,
    testamentAbbreviation: bookItem.testamentAbbrev,
  }))

  const verses: ExtractedBibleVerse[] = rawJson.verses.map((verseItem) => ({
    bookNumber: verseItem.book,
    chapter: verseItem.chapter,
    verse: verseItem.verse,
    text: verseItem.content,
  }))

  return {
    language: rawJson.language,
    version: rawJson.version,
    books,
    verses,
  }
}
