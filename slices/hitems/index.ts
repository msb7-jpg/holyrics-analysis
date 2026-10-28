/**
 * @module @slices/hitems
 * @description Fatia de Itens Polimórficos de Apresentação Litúrgica.
 *
 * ## Responsabilidades:
 * - Modelagem polimórfica unificada de itens projetáveis (Músicas, Versículos, Avisos, Mídias, Textos Avulsos).
 * - Despachante polimórfico de renderização para o projetor HDMI e telão.
 * - Gerenciamento de transições temporizadas entre avisos institucionais.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos de renderização direcionados ao `@lumen/core`.
 * - **Consome:** Dados resolvidos por fatias de domínio (`songs`, `bible`, `media`, `pptx`).
 * - **Exporta para UI:** Componente despachante visual `HItemDispatcher` para projeção multiformato.
 */

export const SLICE_NAME = 'hitems'
