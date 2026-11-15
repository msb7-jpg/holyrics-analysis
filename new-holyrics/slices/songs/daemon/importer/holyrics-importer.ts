/**
 * @module @slices/songs/daemon/importer/holyrics-importer
 * @description Importador de backups `.zip` e pacotes JSON/TXT do Holyrics legado utilizando `fflate` (RF-11).
 */

import { unzipSync, strFromU8 } from 'fflate'
import type { SongRepository } from '../song-repository'
import type { LegacyHolyricsSong, Song, SongImportReport } from '../../contracts/song-contracts'
import { convertLegacySongToLumenSong, parseHolyricsTxtSong } from './tag-cleaner'

/**
 * Extrai músicas de um arquivo `.zip` em memória (via `fflate.unzipSync`) e converte para `Song[]`.
 * Suporta arquivos `.json` (únicos ou arrays) e `.txt` dentro do pacote ZIP.
 *
 * @param zipBytes - Buffer ou Uint8Array do arquivo `.zip` de backup do Holyrics.
 * @returns Lista de músicas convertidas e higienizadas.
 */
export function extractSongsFromZipBuffer(zipBytes: Uint8Array): Song[] {
  const unzippedFiles = unzipSync(zipBytes)
  const extractedSongs: Song[] = []

  for (const [entryPath, fileData] of Object.entries(unzippedFiles)) {
    if (!fileData || fileData.length === 0 || entryPath.endsWith('/')) {
      continue
    }

    const lowerPath = entryPath.toLowerCase()
    const baseName = entryPath.split('/').pop() || entryPath

    try {
      const textContent = strFromU8(fileData).trim()
      if (!textContent) continue

      if (lowerPath.endsWith('.json')) {
        const parsedJson = JSON.parse(textContent) as unknown
        const items = Array.isArray(parsedJson) ? parsedJson : [parsedJson]

        for (const rawItem of items) {
          if (rawItem && typeof rawItem === 'object' && 'title' in rawItem) {
            const itemObj = rawItem as Record<string, unknown>
            if (typeof itemObj.lyrics === 'string') {
              extractedSongs.push(
                convertLegacySongToLumenSong({
                  id: itemObj.id as string | number | undefined,
                  title: String(itemObj.title),
                  artist: typeof itemObj.artist === 'string' ? itemObj.artist : '',
                  lyrics: itemObj.lyrics,
                  formatting: typeof itemObj.formatting === 'string' ? itemObj.formatting : '',
                  order: Array.isArray(itemObj.order) ? (itemObj.order as number[]) : [],
                  note: typeof itemObj.note === 'string' ? itemObj.note : '',
                  language: 'pt-BR',
                }),
              )
            }
          }
        }
      } else if (lowerPath.endsWith('.txt') || lowerPath.endsWith('.muf')) {
        const legacyTxt = parseHolyricsTxtSong(textContent, baseName)
        extractedSongs.push(convertLegacySongToLumenSong(legacyTxt))
      }
    } catch {
      // Ignora entradas corrompidas individuais sem abortar o lote inteiro
    }
  }

  return extractedSongs
}

/**
 * Importa um lote de músicas legadas ou buffer `.zip` diretamente para o `SongRepository` SQLite.
 *
 * @param songRepo - Instância ativa do repositório de músicas.
 * @param payload - Array de músicas legadas ou buffer binário `.zip`.
 * @returns Relatório detalhado `SongImportReport` com total de músicas importadas.
 */
export function importHolyricsBatch(
  songRepo: SongRepository,
  payload: LegacyHolyricsSong[] | Uint8Array,
): SongImportReport {
  const songsToSave: Song[] =
    payload instanceof Uint8Array
      ? extractSongsFromZipBuffer(payload)
      : payload.map((item) => convertLegacySongToLumenSong(item))

  const savedSongs: Song[] = []
  let skippedCount = 0

  for (const song of songsToSave) {
    if (!song.title || song.sections.length === 0) {
      skippedCount++
      continue
    }
    songRepo.upsertSong(song)
    savedSongs.push(song)
  }

  return {
    importedCount: savedSongs.length,
    skippedCount,
    songs: savedSongs,
  }
}
