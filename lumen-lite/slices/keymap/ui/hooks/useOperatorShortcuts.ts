/**
 * @module @slices/keymap/ui/hooks/useOperatorShortcuts
 * @description Hook reativo que escuta atalhos globais de teclado usando @siberiacancode/reactuse.
 */

import { useEventListener } from '@siberiacancode/reactuse'

export interface OperatorShortcutsOptions {
  isSpotlightOpen: boolean
  onToggleSpotlight: () => void
  onNavNext: () => void
  onNavPrev: () => void
  onPanicBlackout?: () => void
  onPanicClearText?: () => void
  onPanicFreeze?: () => void
}

/**
 * Escuta eventos de teclado no window de forma resiliente, respeitando inputs em foco.
 *
 * @param options - Ações e estados para os atalhos.
 * @param options.isSpotlightOpen - Se o Spotlight universal está aberto no momento.
 * @param options.onToggleSpotlight - Ação de alternar visibilidade do Spotlight (Cmd+K).
 * @param options.onNavNext - Ação de avançar para o próximo slide (Barra de Espaço).
 * @param options.onNavPrev - Ação de retornar ao slide anterior (Backspace).
 * @param options.onPanicBlackout - Ação de alternar blackout (F1).
 * @param options.onPanicClearText - Ação de alternar ocultar letra (F2).
 * @param options.onPanicFreeze - Ação de alternar congelar telão (F3).
 */
export function useOperatorShortcuts({
  isSpotlightOpen,
  onToggleSpotlight,
  onNavNext,
  onNavPrev,
  onPanicBlackout,
  onPanicClearText,
  onPanicFreeze,
}: OperatorShortcutsOptions) {
  useEventListener('keydown', (event: KeyboardEvent) => {
    const targetElement = event.target as HTMLElement | null
    const isInputFocused =
      targetElement?.tagName === 'INPUT' ||
      targetElement?.tagName === 'TEXTAREA' ||
      targetElement?.isContentEditable

    // Cmd+K / Ctrl+K abre ou fecha o Spotlight universal
    if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k') {
      event.preventDefault()
      onToggleSpotlight()
      return
    }

    // Se estiver digitando em um campo de texto ou com o Spotlight aberto, ignora navegação
    if (isInputFocused || isSpotlightOpen) {
      return
    }

    if (event.key === ' ') {
      event.preventDefault()
      onNavNext()
    } else if (event.key === 'Backspace') {
      event.preventDefault()
      onNavPrev()
    } else if (event.key === 'F1' && onPanicBlackout) {
      event.preventDefault()
      onPanicBlackout()
    } else if (event.key === 'F2' && onPanicClearText) {
      event.preventDefault()
      onPanicClearText()
    } else if (event.key === 'F3' && onPanicFreeze) {
      event.preventDefault()
      onPanicFreeze()
    }
  })
}
