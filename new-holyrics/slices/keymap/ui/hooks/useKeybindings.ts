/**
 * @module @slices/keymap/ui/hooks/useKeybindings
 * @description Hook reativo que escuta e despacha atalhos de teclado configurados em `keybindings.json`.
 *
 * ## Responsabilidades:
 * - Escutar eventos de teclado globais no navegador respeitando o foco de campos de texto e modais.
 * - Localizar o atalho correspondente via `matchesKeyCombo`.
 * - Prevenir o comportamento padrão do navegador (`event.preventDefault()`) para atalhos capturados.
 * - Invocar os manipuladores de ação informados.
 */

import { useEffect } from 'react'
import type { KeyActionId, Keybinding } from '../../contracts/keymap-contracts'
import { matchesKeyCombo, DEFAULT_KEYBINDINGS } from '../../contracts/keymap-contracts'

export interface UseKeybindingsOptions {
  bindings?: Keybinding[]
  enabled?: boolean
  isModalOpen?: boolean
  onAction: (actionId: KeyActionId) => void
}

/**
 * Escuta atalhos de teclado no window de forma declarativa e determinística.
 *
 * @param options - Opções de configuração e callback de ação.
 * @param options.bindings - Lista de atalhos ativos (padrão: DEFAULT_KEYBINDINGS).
 * @param options.enabled - Se a escuta de atalhos está ativa.
 * @param options.isModalOpen - Se há algum diálogo modal aberto (como Spotlight).
 * @param options.onAction - Callback invocado com o ID da ação disparada.
 */
export function useKeybindings({
  bindings = DEFAULT_KEYBINDINGS,
  enabled = true,
  isModalOpen = false,
  onAction,
}: UseKeybindingsOptions): void {
  useEffect(() => {
    if (!enabled) return

    const handleKeyDown = (event: KeyboardEvent) => {
      const targetElement = event.target as HTMLElement | null
      const isInputFocused =
        targetElement?.tagName === 'INPUT' ||
        targetElement?.tagName === 'TEXTAREA' ||
        targetElement?.isContentEditable

      // Procura atalhos globais que devam funcionar mesmo em inputs (ex: Escape para fechar)
      for (const binding of bindings) {
        if (matchesKeyCombo(event, binding.combo)) {
          // Se o foco está em campo de texto, só permite teclas especiais como Escape
          if (isInputFocused && binding.action !== 'NAV_CLEAR') {
            continue
          }

          // Se um modal está aberto, restringe navegações de slide normais
          if (isModalOpen && (binding.action === 'NAV_NEXT' || binding.action === 'NAV_PREV')) {
            continue
          }

          event.preventDefault()
          onAction(binding.action)
          return
        }
      }
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [bindings, enabled, isModalOpen, onAction])
}
