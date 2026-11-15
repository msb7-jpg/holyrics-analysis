/**
 * @module @slices/bible
 * @description Fatia de Motor Bíblico, Escrituras Offline, Projeção Dual Version (RF-14) e Gerenciador de Traduções (RF-17).
 *
 * ## Responsabilidades:
 * - Leitura e consulta de traduções bíblicas locais em SQLite (ACF, NVI, NAA, ARA, ARC, NVT).
 * - Reconhecimento e parsing ultrarrápido de referências em texto livre (ex.: "jo 3 16", "1co 13:4-8").
 * - Busca textual por palavras-chave com indexação FTS5 e ranking BM25.
 * - Descriptografia e conversão nativa de arquivos `.bib` legados do Holyrics.
 * - Gerenciamento e instalação dinâmica de traduções bíblicas (`BibleDownloader`).
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `PRESENT_ITEM` para o `@lumen/core` carregando versículos (simples ou Dual Version) no `ForegroundLayer`.
 * - **Consome:** Conexão SQLite do `apps/daemon` e despachante de eventos litúrgicos.
 * - **Exporta para UI:** Componentes de busca bíblica, grid Dual Version e modal de downloads.
 */

export type {
  BibleReference,
  ParsedVerseRef,
  BibleVerse,
  BibleSearchResult,
  BibleVersionSummary,
  BibleLookupResponse,
  AvailableBibleDownloadItem,
  ExtractedBibleData,
  ExtractedBibleBook,
  ExtractedBibleVerse,
} from './contracts/bible-contracts'

export {
  BibleReferenceSchema,
  BibleVerseSchema,
  BibleSearchResultSchema,
  BibleVersionSummarySchema,
  BibleLookupResponseSchema,
  AvailableBibleDownloadItemSchema,
} from './contracts/bible-contracts'

export { parseVerseReference, isBibleReference } from './daemon/verse-parser'
export { BibleRepository } from './daemon/bible-repository'
export { BibleDownloader } from './daemon/bible-downloader'
export { extractHolyricsBibFile, extractHolyricsBibBuffer } from './daemon/bib-extractor'
export { handleBibleRoutes } from './daemon/routes/bible-router'
export { BibleDownloadModal } from './ui/components/BibleDownloadModal'
