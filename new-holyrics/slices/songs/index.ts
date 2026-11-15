/**
 * @module @slices/songs
 * @description Fatia de Gestão de Músicas, Letras, Acervo, Importador Legado (RF-11), Histórico (RF-12) e Busca FTS5.
 *
 * ## Responsabilidades:
 * - Persistência, consulta e modelagem semântica de letras e hinos no SQLite WAL (`SongRepository`).
 * - Busca textual instantânea (<1ms) via tabela virtual SQLite FTS5 com ranking BM25 e snippets destacados.
 * - Integração online assíncrona com a API do Vagalume para importação rápida de letras e metadados (`VagalumeClient`).
 * - Leitura e higienização de arquivos legados de backup do Holyrics (`.zip` via `fflate` e limpeza de tags `<c>`, `<t>`).
 * - Gravação automática e relatório de frequência de execução no culto (`SongHistoryRepository`).
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `PRESENT_ITEM` para o `@lumen/core` carregando estrofes e slides para o `ForegroundLayer`.
 * - **Consome:** Conexão SQLite (`bun:sqlite`) fornecida pelo `apps/daemon`.
 * - **Exporta para UI:** Serviços, hooks de busca e modais de importação e histórico.
 */

export type {
  Song,
  SongSection,
  LegacyHolyricsSong,
  SongImportReport,
  SongHistoryEntry,
  SongUsageStat,
} from './contracts/song-contracts'
export {
  SongSchema,
  SongSectionSchema,
  LegacyHolyricsSongSchema,
  SongImportReportSchema,
  SongHistoryEntrySchema,
  SongUsageStatSchema,
} from './contracts/song-contracts'

export { SongRepository } from './daemon/song-repository'
export type { SongSearchResult } from './daemon/song-repository'

export {
  SongHistoryRepository,
  HISTORY_DEDUP_WINDOW_MS,
} from './daemon/song-history-repository'

export {
  cleanHolyricsTags,
  extractFormattingTag,
  convertLegacySongToLumenSong,
  parseHolyricsTxtSong,
} from './daemon/importer/tag-cleaner'

export {
  extractSongsFromZipBuffer,
  importHolyricsBatch,
} from './daemon/importer/holyrics-importer'

export { VagalumeClient } from './daemon/vagalume-client'
export type { VagalumeSearchResultItem } from './daemon/vagalume-client'

export { SongHistoryModal } from './ui/components/SongHistoryModal'
export { SongImporterModal } from './ui/components/SongImporterModal'
