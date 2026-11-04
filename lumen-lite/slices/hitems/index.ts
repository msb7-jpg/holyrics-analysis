/**
 * @module @slices/hitems
 * @description Fatia de Itens Polimórficos de Apresentação Litúrgica (HItems).
 *
 * ## Responsabilidades:
 * - Modelagem polimórfica unificada de itens projetáveis (Músicas, Versículos, Avisos, Mídias, Textos Avulsos, Contagens).
 * - Prover Schemas Zod v4 e contratos compartilhados isomórficos.
 * - Despachante polimórfico de renderização para o projetor HDMI e telão.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos de renderização direcionados ao `@lumen/core`.
 * - **Consome:** Dados resolvidos por fatias de domínio (`songs`, `bible`, `media`, `pptx`).
 * - **Exporta:** Contratos e schemas Zod para `@slices/liturgy` e interface visual do operador.
 */

export const SLICE_NAME = 'hitems'

export type {
  HItem,
  HItemType,
  HItemSong,
  HItemBible,
  HItemMedia,
  HItemPptx,
  HItemText,
  HItemCountdown,
} from './contracts/hitem-contracts'

export {
  HItemSchema,
  HItemTypeSchema,
  HItemSongSchema,
  HItemBibleSchema,
  HItemMediaSchema,
  HItemPptxSchema,
  HItemTextSchema,
  HItemCountdownSchema,
} from './contracts/hitem-contracts'
