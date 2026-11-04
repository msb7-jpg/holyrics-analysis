/**
 * @module @slices/songs
 * @description Fatia de Gestão de Músicas, Letras, Acervo e Busca FTS5.
 *
 * ## Responsabilidades:
 * - Persistência, consulta e modelagem semântica de letras e hinos no SQLite WAL (`SongRepository`).
 * - Busca textual instantânea (<1ms) via tabela virtual SQLite FTS5 com ranking BM25 e snippets destacados.
 * - Integração online assíncrona com a API do Vagalume para importação rápida de letras e metadados (`VagalumeClient`).
 * - Sementes e acervo inicial offline embutido com hinos e louvores tradicionais.
 * - Leitura e higienização de arquivos legados de backup do Holyrics (.zip/.hly).
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `PRESENT_ITEM` para o `@lumen/core` carregando estrofes e slides para o `ForegroundLayer`.
 * - **Consome:** Conexão SQLite (`bun:sqlite`) fornecida pelo `apps/daemon`.
 * - **Exporta para UI:** Hook de busca reativa `useSongSearch` e visualização de estrofes numeradas no `OperatorView`.
 */

export type { Song, SongSection } from './contracts/song-contracts'
export { SongSchema, SongSectionSchema } from './contracts/song-contracts'

export { SongRepository } from './daemon/song-repository'
export type { SongSearchResult } from './daemon/song-repository'

export { VagalumeClient } from './daemon/vagalume-client'
export type { VagalumeSearchResultItem } from './daemon/vagalume-client'
