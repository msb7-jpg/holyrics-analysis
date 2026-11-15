/**
 * @module @slices/keymap
 * @description Fatia de Mapeamento Declarativo de Atalhos de Teclado do LumenLite (keybindings.json).
 *
 * ## Responsabilidades:
 * - Validação e persistência atômica de atalhos em `keybindings.json` (RF-34).
 * - Mapeamento ergonômico de combinações W3C para ações litúrgicas canônicas.
 * - Roteamento REST `/api/keymap/*` para consulta, atualização e reset de atalhos.
 * - Hook reativo `useKeybindings` com isolamento de foco em formulários/inputs.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Ações para a FSM (`@lumen/core`), Spotlight e modais do operador.
 * - **Consome:** Configuração em disco e eventos de teclado do navegador.
 * - **Exporta:** Contratos, repositório, hooks e componentes de UI.
 */

export type {
  KeyActionId,
  KeymapScope,
  Keybinding,
  KeymapConfig,
  KeyComboEventLike,
} from './contracts/keymap-contracts'

export {
  KeyActionIdSchema,
  KeymapScopeSchema,
  KeybindingSchema,
  KeymapConfigSchema,
  DEFAULT_KEYBINDINGS,
  DEFAULT_KEYMAP_CONFIG,
  matchesKeyCombo,
  formatKeyComboDisplay,
} from './contracts/keymap-contracts'

export { KeymapRepository } from './daemon/keymap-repository'
export { handleKeymapRoutes } from './daemon/routes/keymap-router'

export { useKeybindings } from './ui/hooks/useKeybindings'
export type { UseKeybindingsOptions } from './ui/hooks/useKeybindings'

export { KeymapModal } from './ui/components/KeymapModal'
export type { KeymapModalProps } from './ui/components/KeymapModal'
