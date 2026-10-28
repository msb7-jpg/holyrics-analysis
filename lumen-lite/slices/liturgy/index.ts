/**
 * @module @slices/liturgy
 * @description Fatia de Roteiro de Culto e Planejamento Litúrgico ("Culto como Código").
 *
 * ## Responsabilidades:
 * - Leitura, gravação e validação estrita do arquivo declarativo `culto.json`.
 * - Geração e sincronização do JSON Schema Draft 2020-12 (`liturgy.schema.json`).
 * - Pré-voo litúrgico (`preflight-validator`): validação de caminhos de arquivos e integridade de banco antes do culto.
 * - Suporte a arranjo e sequenciamento de itens polimórficos de culto.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Carregamento de itens litúrgicos para a fila de execução no `apps/daemon`.
 * - **Consome:** Contratos de itens polimórficos de `@slices/hitems`.
 * - **Exporta para UI:** Barra lateral de roteiro do culto (`LiturgyQueue`) e status de pré-voo em `apps/ui`.
 */

export type { LiturgyItem, LiturgyFile } from './contracts/liturgy-schema'
export {
  LiturgyItemSchema,
  LiturgyThemeSchema,
  LiturgyFileSchema,
} from './contracts/liturgy-schema'

export type { PreflightResult } from './daemon/preflight-validator'
export { validateLiturgy } from './daemon/preflight-validator'
