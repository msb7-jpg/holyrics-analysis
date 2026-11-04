/**
 * @module @slices/keymap/contracts/keymap-contracts
 * @description Esquemas Zod v4 e interfaces tipadas para o mapeamento declarativo de atalhos de teclado (keybindings.json).
 *
 * ## Responsabilidades:
 * - Definir o vocabulário canônico de ações disparáveis por atalhos (`KeyActionId`).
 * - Validar o formato do arquivo declarativo `keybindings.json` com Zod v4 e suporte a `$schema`.
 * - Fornecer utilitários puros de normalização e comparação de eventos de teclado W3C (`matchesKeyCombo`).
 *
 * ## Comunicação e Fronteiras:
 * - Consumido pelo hook `useKeybindings` no frontend (`apps/ui`) e pelo repositório em disco no backend (`apps/daemon`).
 */

import { z } from 'zod'

/**
 * Identificadores canônicos de ações acionáveis via atalho de teclado.
 */
export const KeyActionIdSchema = z.enum([
  'NAV_NEXT',
  'NAV_PREV',
  'NAV_CLEAR',
  'PANIC_BLACKOUT',
  'PANIC_CLEAR_TEXT',
  'PANIC_CLEAR_BG',
  'PANIC_FREEZE',
  'PANIC_CLEAR',
  'OPEN_SPOTLIGHT',
  'OPEN_AUTOMATION',
  'OPEN_KEYMAP',
  'QUICK_TEXT',
])
export type KeyActionId = z.infer<typeof KeyActionIdSchema>

/**
 * Escopos de contexto de execução dos atalhos.
 */
export const KeymapScopeSchema = z.enum([
  'global',
  'operator',
  'projector',
])
export type KeymapScope = z.infer<typeof KeymapScopeSchema>

/**
 * Definição atômica de um atalho de teclado.
 */
export const KeybindingSchema = z.object({
  id: z.string().min(1),
  action: KeyActionIdSchema,
  combo: z.string().min(1),
  scope: KeymapScopeSchema.default('global'),
  description: z.string().min(1),
})
export type Keybinding = z.infer<typeof KeybindingSchema>

/**
 * Estrutura do arquivo de configuração `keybindings.json`.
 */
export const KeymapConfigSchema = z.object({
  $schema: z.string().optional(),
  version: z.number().default(1),
  bindings: z.array(KeybindingSchema),
})
export type KeymapConfig = z.infer<typeof KeymapConfigSchema>

/**
 * Conjunto de atalhos padrão ergonômicos da cabine de operação do LumenLite.
 */
export const DEFAULT_KEYBINDINGS: Keybinding[] = [
  {
    id: 'nav-next',
    action: 'NAV_NEXT',
    combo: 'Space',
    scope: 'global',
    description: 'Avançar para o próximo slide ou versículo',
  },
  {
    id: 'nav-next-arrow',
    action: 'NAV_NEXT',
    combo: 'ArrowRight',
    scope: 'global',
    description: 'Avançar para o próximo slide (seta)',
  },
  {
    id: 'nav-prev',
    action: 'NAV_PREV',
    combo: 'Backspace',
    scope: 'global',
    description: 'Retornar ao slide ou versículo anterior',
  },
  {
    id: 'nav-prev-arrow',
    action: 'NAV_PREV',
    combo: 'ArrowLeft',
    scope: 'global',
    description: 'Retornar ao slide anterior (seta)',
  },
  {
    id: 'nav-clear',
    action: 'NAV_CLEAR',
    combo: 'Escape',
    scope: 'global',
    description: 'Limpar texto da tela ou fechar modais abertos',
  },
  {
    id: 'panic-blackout',
    action: 'PANIC_BLACKOUT',
    combo: 'F1',
    scope: 'global',
    description: 'Alternar Blackout instantâneo (tela preta total)',
  },
  {
    id: 'panic-clear-text',
    action: 'PANIC_CLEAR_TEXT',
    combo: 'F2',
    scope: 'global',
    description: 'Alternar ocultar letra/texto (mantendo vídeo de fundo)',
  },
  {
    id: 'panic-clear-bg',
    action: 'PANIC_CLEAR_BG',
    combo: 'F3',
    scope: 'global',
    description: 'Alternar ocultar fundo (fundo preto com letra branca)',
  },
  {
    id: 'panic-freeze',
    action: 'PANIC_FREEZE',
    combo: 'F4',
    scope: 'global',
    description: 'Alternar congelamento do quadro do projetor (Freeze)',
  },
  {
    id: 'open-spotlight',
    action: 'OPEN_SPOTLIGHT',
    combo: 'CommandOrControl+k',
    scope: 'operator',
    description: 'Abrir barra de busca rápida universal (Spotlight)',
  },
  {
    id: 'open-automation',
    action: 'OPEN_AUTOMATION',
    combo: 'CommandOrControl+a',
    scope: 'operator',
    description: 'Abrir editor visual de automações por grafo (DAG)',
  },
  {
    id: 'open-keymap',
    action: 'OPEN_KEYMAP',
    combo: 'CommandOrControl+/',
    scope: 'operator',
    description: 'Exibir tabela de atalhos e keybindings',
  },
]

/**
 * Configuração padrão inicial serializável em `keybindings.json`.
 */
export const DEFAULT_KEYMAP_CONFIG: KeymapConfig = {
  version: 1,
  bindings: DEFAULT_KEYBINDINGS,
}

/**
 * Interface compatível com eventos de teclado W3C (KeyboardEvent) ou objetos mockados.
 */
export interface KeyComboEventLike {
  key: string
  code?: string
  metaKey?: boolean
  ctrlKey?: boolean
  shiftKey?: boolean
  altKey?: boolean
}

/**
 * Normaliza e verifica se um evento de teclado W3C corresponde à combinação declarada.
 *
 * @param event - Evento KeyboardEvent ou objeto estrutural com propriedades da tecla.
 * @param combo - String da combinação (ex: 'CommandOrControl+k', 'Space', 'Shift+F1').
 * @returns Booleano indicando se o evento coincide com o atalho.
 */
export function matchesKeyCombo(event: KeyComboEventLike, combo: string): boolean {
  const parts = combo.split('+').map((part) => part.trim().toLowerCase())
  const keyPart = parts[parts.length - 1]
  const modifiers = new Set(parts.slice(0, parts.length - 1))

  const wantsCmdOrCtrl = modifiers.has('commandorcontrol') || modifiers.has('cmdorctrl')
  const wantsCtrl = modifiers.has('ctrl') || modifiers.has('control')
  const wantsMeta = modifiers.has('meta') || modifiers.has('cmd') || modifiers.has('command')
  const wantsShift = modifiers.has('shift')
  const wantsAlt = modifiers.has('alt') || modifiers.has('option')

  const isMeta = Boolean(event.metaKey)
  const isCtrl = Boolean(event.ctrlKey)
  const isShift = Boolean(event.shiftKey)
  const isAlt = Boolean(event.altKey)

  // Verificação de modificadores
  if (wantsCmdOrCtrl) {
    if (!isMeta && !isCtrl) return false
  } else {
    if (wantsCtrl && !isCtrl) return false
    if (!wantsCtrl && isCtrl) return false
    if (wantsMeta && !isMeta) return false
    if (!wantsMeta && isMeta) return false
  }

  if (wantsShift && !isShift) return false
  if (!wantsShift && isShift) return false

  if (wantsAlt && !isAlt) return false
  if (!wantsAlt && isAlt) return false

  // Comparação da tecla principal
  const eventKey = event.key.toLowerCase()
  const eventCode = (event.code || '').toLowerCase()

  if (keyPart === 'space' && (eventKey === ' ' || eventCode === 'space')) {
    return true
  }

  if (keyPart === 'escape' && (eventKey === 'escape' || eventCode === 'escape')) {
    return true
  }

  if (keyPart === 'backspace' && (eventKey === 'backspace' || eventCode === 'backspace')) {
    return true
  }

  if (keyPart === 'arrowright' && (eventKey === 'arrowright' || eventCode === 'arrowright')) {
    return true
  }

  if (keyPart === 'arrowleft' && (eventKey === 'arrowleft' || eventCode === 'arrowleft')) {
    return true
  }

  if (keyPart === '/' && (eventKey === '/' || eventCode === 'slash')) {
    return true
  }

  return eventKey === keyPart || eventCode === `key${keyPart}` || eventCode === keyPart
}

/**
 * Formata a combinação de teclas para renderização visual ergonômica.
 *
 * @param combo - Combinação de teclas (ex: 'CommandOrControl+k').
 * @param isApplePlatform - Se o ambiente do cliente é macOS ou iOS.
 * @returns String formatada para exibição em badges `<Kbd>`.
 */
export function formatKeyComboDisplay(combo: string, isApplePlatform = false): string {
  return combo
    .replace(/CommandOrControl/gi, isApplePlatform ? '⌘' : 'Ctrl')
    .replace(/CmdOrCtrl/gi, isApplePlatform ? '⌘' : 'Ctrl')
    .replace(/Control/gi, 'Ctrl')
    .replace(/Meta/gi, isApplePlatform ? '⌘' : 'Win')
    .replace(/Shift/gi, isApplePlatform ? '⇧' : 'Shift')
    .replace(/Alt/gi, isApplePlatform ? '⌥' : 'Alt')
    .replace(/Space/gi, 'Espaço')
    .replace(/ArrowRight/gi, '→')
    .replace(/ArrowLeft/gi, '←')
}
