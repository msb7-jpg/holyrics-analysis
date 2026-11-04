/**
 * @module @slices/bible
 * @description Fatia de Motor Bíblico, Escrituras Offline e Projeção Dual Version.
 *
 * ## Responsabilidades:
 * - Leitura e consulta de traduções bíblicas locais em SQLite (ACF, NVI, NAA, ARA, ARC, NVT).
 * - Reconhecimento e parsing ultrarrápido de referências em texto livre (ex.: "jo 3 16", "1co 13:4-8").
 * - Busca textual por palavras-chave com indexação FTS5 e ranking BM25.
 * - Descriptografia e conversão nativa de arquivos `.bib` legados do Holyrics.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `PRESENT_ITEM` para o `@lumen/core` carregando versículos no `ForegroundLayer`.
 * - **Consome:** Conexão SQLite do `apps/daemon` e despachante de eventos litúrgicos.
 * - **Exporta para UI:** Componente de Spotlight de busca bíblica e visualização de versículos.
 */

export type {
  BibleReference,
  ParsedVerseRef,
  BibleVerse,
  BibleSearchResult,
  BibleVersionSummary,
  ExtractedBibleData,
  ExtractedBibleBook,
  ExtractedBibleVerse,
} from './contracts/bible-contracts'

export {
  BibleReferenceSchema,
  BibleVerseSchema,
  BibleSearchResultSchema,
  BibleVersionSummarySchema,
} from './contracts/bible-contracts'

export { parseVerseReference, isBibleReference } from './daemon/verse-parser'
export { BibleRepository } from './daemon/bible-repository'
export { extractHolyricsBibFile, extractHolyricsBibBuffer } from './daemon/bib-extractor'
