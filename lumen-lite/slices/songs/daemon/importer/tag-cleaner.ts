/**
 * @module @slices/songs/daemon/importer/tag-cleaner
 * @description Higienizador léxico de tags proprietárias do Holyrics (`<c>`, `<t>`, `<p>`, `##`) e conversor para `SongSchema` (RF-11).
 *
 * ## Referência no Código Original:
 * - `src/com/limagiran/holyrics/model/Music.java` (`getParagraphs()`, `getFormatting()`, `getOrder()`)
 * - `src/com/limagiran/holyrics/control/MusicController.java` (`Paragraph.TOKEN = "##"`, `getFormattingLabel()`)
 */

import type { LegacyHolyricsSong, Song, SongSection } from '../../contracts/song-contracts'

/**
 * Remove tags proprietárias inline do Holyrics (`<c>`, `</c>`, `<t>`, `</t>`, `<p>`, `</p>`, `<b>`, `<i>`)
 * e cabeçalhos de formatação `##...` de um bloco de letra.
 *
 * @param rawText - Texto bruto contendo marcações legadas do Holyrics.
 * @returns Texto limpo e normalizado para projeção.
 */
export function cleanHolyricsTags(rawText: string): string {
  return rawText
    .replace(/<\/?(?:c|t|p|b|i|u|font|span|color)(?:\s[^>]*)?>/gi, '')
    .replace(/<[ct][^>]*>/gi, '')
    .replace(/^##[^\n]*$/gm, '')
    .replace(/<#4\(\|<-?\|3\|_@>/g, '\n')
    .replace(/\r\n/g, '\n')
    .trim()
}

/**
 * Extrai um rótulo semântico opcional de uma linha de formatação `##...(LABEL)` do `MusicController.Paragraph`.
 *
 * @param formatLine - Linha de formatação (ex: `##n(CORO)`).
 * @returns Rótulo extraído (ex: `CORO`) ou undefined.
 */
export function extractFormattingTag(formatLine?: string): string | undefined {
  if (!formatLine) return undefined
  const match = formatLine.match(/\(([^)]+)\)/)
  if (match && match[1]) {
    return match[1].trim().toUpperCase()
  }
  return undefined
}

/**
 * Converte uma estrutura `LegacyHolyricsSong` (de JSON ou arquivo `.txt`/`.muf`) para o modelo canônico `Song` do LumenLite.
 *
 * @param legacy - Objeto de música no formato do Holyrics original.
 * @returns Entidade `Song` higienizada e pronta para o SQLite FTS5.
 */
export function convertLegacySongToLumenSong(legacy: LegacyHolyricsSong): Song {
  const normalizedLyrics = legacy.lyrics.replace(/\r\n/g, '\n')
  const rawParagraphs = normalizedLyrics.split(/\n{2,}/)
  const formatLines = (legacy.formatting || '').replace(/\r\n/g, '\n').split('\n')

  const sections: SongSection[] = []

  for (let idx = 0; idx < rawParagraphs.length; idx++) {
    const cleanedBlock = cleanHolyricsTags(rawParagraphs[idx])
    if (!cleanedBlock) continue

    const lines = cleanedBlock
      .split('\n')
      .map((line) => line.trim())
      .filter((line) => line.length > 0)

    if (lines.length === 0) continue

    const sectionIndex = sections.length + 1
    const tagLabel = extractFormattingTag(formatLines[idx])

    sections.push({
      id: `s${sectionIndex}`,
      index: sectionIndex,
      lines,
      tags: tagLabel ? [tagLabel] : undefined,
    })
  }

  // Converte o array `order` de índices 0-based do Music.java para IDs de seção ('s1', 's2'...)
  let defaultArrangement: string[] | undefined
  if (legacy.order && legacy.order.length > 0) {
    const mappedOrder = legacy.order
      .filter((orderIdx) => orderIdx >= 0 && orderIdx < sections.length)
      .map((orderIdx) => sections[orderIdx].id)
    if (mappedOrder.length > 0) {
      defaultArrangement = mappedOrder
    }
  }

  const slugTitle = legacy.title
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '')

  const songId = legacy.id
    ? `holyrics-${String(legacy.id)}`
    : `holyrics-${slugTitle || Date.now()}`

  return {
    id: songId,
    title: cleanHolyricsTags(legacy.title) || 'Sem Título',
    artist: cleanHolyricsTags(legacy.artist || '') || 'Desconhecido',
    sections:
      sections.length > 0
        ? sections
        : [
            {
              id: 's1',
              index: 1,
              lines: [cleanHolyricsTags(legacy.title) || 'Letra vazia'],
            },
          ],
    defaultArrangement,
  }
}

/**
 * Faz parsing de um arquivo de texto exportado pelo Holyrics (`Music.getMusic()`):
 * Formato:
 * Artista: Nome do Artista
 * Título: Nome da Música
 *
 * Estrofe 1...
 *
 * @param textContent - Conteúdo do arquivo `.txt` exportado pelo Holyrics.
 * @param fallbackFilename - Nome do arquivo caso o cabeçalho não esteja presente.
 * @returns Objeto `LegacyHolyricsSong` extraído.
 */
export function parseHolyricsTxtSong(
  textContent: string,
  fallbackFilename = 'Musica Importada',
): LegacyHolyricsSong {
  const normalized = textContent.replace(/\r\n/g, '\n').trim()
  const lines = normalized.split('\n')

  let artist = ''
  let title = ''
  let bodyStartIndex = 0

  for (let idx = 0; idx < Math.min(lines.length, 6); idx++) {
    const line = lines[idx].trim()
    const artistMatch = line.match(/^(?:Artista|Artist|Autor)\s*:\s*(.+)$/i)
    const titleMatch = line.match(/^(?:T[íi]tulo|Title)\s*:\s*(.+)$/i)

    if (artistMatch) {
      artist = artistMatch[1].trim()
      bodyStartIndex = idx + 1
    } else if (titleMatch) {
      title = titleMatch[1].trim()
      bodyStartIndex = idx + 1
    } else if (line.startsWith('Nota:') || line.startsWith('Note:')) {
      bodyStartIndex = idx + 1
    }
  }

  const lyricsBody = lines.slice(bodyStartIndex).join('\n').trim()
  const fallbackClean = fallbackFilename.replace(/\.(txt|json|muf)$/i, '')

  return {
    title: title || fallbackClean,
    artist: artist || 'Importado Holyrics',
    lyrics: lyricsBody || normalized,
    formatting: '',
    order: [],
    note: '',
    language: 'pt-BR',
  }
}
