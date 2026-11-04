/**
 * @module @slices/keymap/ui/hooks/useOperatorShortcuts
 * @description Hook de atalhos do operador construído sobre o motor declarativo `useKeybindings`.
 *
 * ## Responsabilidades:
 * - Mapear os callbacks do operador para as ações declarativas canônicas (`KeyActionId`).
 * - Fornecer compatibilidade transparente para o console do operador e modais.
 */

import { useKeybindings } from './useKeybindings'
import type { KeyActionId } from '../../contracts/keymap-contracts'

export interface OperatorShortcutsOptions {
  isSpotlightOpen: boolean
  onToggleSpotlight: () => void
  onNavNext: () => void
  onNavPrev: () => void
  onPanicBlackout?: () => void
  onPanicClearText?: () => void
  onPanicClearBg?: () => void
  onPanicFreeze?: () => void
  onOpenAutomation?: () => void
  onOpenKeymap?: () => void
}

/**
 * Escuta eventos de teclado no window conectando o motor declarativo aos manipuladores do console.
 *
 * @param options - Ações e estados para os atalhos.
 * @param options.isSpotlightOpen - Se o Spotlight universal está aberto no momento.
 * @param options.onToggleSpotlight - Ação de alternar visibilidade do Spotlight.
 * @param options.onNavNext - Ação de avançar para o próximo slide.
 * @param options.onNavPrev - Ação de retornar ao slide anterior.
 * @param options.onPanicBlackout - Ação de alternar blackout (F1).
 * @param options.onPanicClearText - Ação de alternar ocultar letra (F2).
 * @param options.onPanicClearBg - Ação de alternar ocultar fundo (F3).
 * @param options.onPanicFreeze - Ação de alternar congelar telão (F4).
 * @param options.onOpenAutomation - Ação de abrir editor de automação.
 * @param options.onOpenKeymap - Ação de abrir tabela de atalhos.
 */
export function useOperatorShortcuts({
  isSpotlightOpen,
  onToggleSpotlight,
  onNavNext,
  onNavPrev,
  onPanicBlackout,
  onPanicClearText,
  onPanicClearBg,
  onPanicFreeze,
  onOpenAutomation,
  onOpenKeymap,
}: OperatorShortcutsOptions): void {
  const handleAction = (actionId: KeyActionId) => {
    switch (actionId) {
      case 'NAV_NEXT':
        onNavNext()
        break
      case 'NAV_PREV':
        onNavPrev()
        break
      case 'PANIC_BLACKOUT':
        onPanicBlackout?.()
        break
      case 'PANIC_CLEAR_TEXT':
        onPanicClearText?.()
        break
      case 'PANIC_CLEAR_BG':
        onPanicClearBg?.()
        break
      case 'PANIC_FREEZE':
        onPanicFreeze?.()
        break
      case 'OPEN_SPOTLIGHT':
        onToggleSpotlight()
        break
      case 'OPEN_AUTOMATION':
        onOpenAutomation?.()
        break
      case 'OPEN_KEYMAP':
        onOpenKeymap?.()
        break
      default:
        break
    }
  }

  useKeybindings({
    enabled: true,
    isModalOpen: isSpotlightOpen,
    onAction: handleAction,
  })
}
