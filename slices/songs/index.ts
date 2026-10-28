/**
 * @module @slices/songs
 * @description Fatia de Gestão de Músicas, Letras, Acervo e Busca FTS5.
 *
 * ## Responsabilidades:
 * - Persistência, consulta e modelagem semântica de letras e hinos no SQLite.
 * - Busca textual instantânea (sub-2ms) via tabela virtual SQLite FTS5 (BM25).
 * - Integração online com a API do Vagalume para importação rápida de letras e metadados.
 * - Leitura e higienização de arquivos legados de backup do Holyrics (.zip).
 * - Histórico de músicas executadas durante os cultos.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `PRESENT_ITEM` para o `@lumen/core` com as estrofes selecionadas.
 * - **Consome:** Conexão SQLite (`DbClient`) fornecida pelo `apps/daemon`.
 * - **Exporta para UI:** Componentes de busca (`SongSearch`), visualização limpa de estrofes
 *   numeradas (`SongSearchResults`) e modal de importação do Vagalume em `apps/ui`.
 */

export type { Song, SongSection } from './contracts/song-contracts'
export { SongSchema, SongSectionSchema } from './contracts/song-contracts'
