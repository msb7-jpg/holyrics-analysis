/**
 * @module @slices/bible
 * @description Fatia de Motor Bíblico, Escrituras Offline e Projeção Dual Version.
 *
 * ## Responsabilidades:
 * - Leitura e consulta de traduções bíblicas locais em SQLite (NVI, NAA, ARA, ARC, NVT).
 * - Reconhecimento e parsing ultrarrápido de referências em texto livre (ex.: "jo 3 16", "1co 13:4-8").
 * - Busca textual por palavras-chave com indexação FTS5.
 * - Suporte a projeção simultânea de duas traduções bíblicas (Dual Version).
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `PRESENT_ITEM` para o `@lumen/core` carregando versículos no `ForegroundLayer`.
 * - **Consome:** Conexão SQLite do `apps/daemon` e despachante de eventos litúrgicos.
 * - **Exporta para UI:** Componente de Spotlight de busca bíblica e seletor rápido de versículos.
 */

export * from './contracts/bible-contracts'
export * from './daemon/verse-parser'
