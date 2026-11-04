/**
 * @module @ui/projector/ProjectorView
 * @description Janela dedicada de saída de projeção para o telão HDMI (/projector).
 *
 * ## Responsabilidades:
 * - Renderização em tela cheia sem barras de navegação ou cursor do mouse.
 * - Integração do ProjectorScreen com auto-fit, seamless loop e máscaras de pânico.
 * - Suporte a atalhos de emergência diretamente da janela do projetor (F1 = Blackout, Esc = Fechar tela cheia).
 */

import { useEffect } from 'react'
import type { ScreenState, ActionCommand } from '@lumen/core'
import { ProjectorScreen } from '@slices/navigation'

export interface ProjectorViewProps {
  state: ScreenState
  dispatch?: (command: ActionCommand) => void
}

/**
 * Visualização pública do Projetor HDMI (/projector).
 *
 * @param props - Propriedades com o estado da tela e despachante opcional.
 * @param props.state - Estado da tela compartilhado da FSM.
 * @param props.dispatch - Função de despacho de comandos de pânico/navegação.
 * @returns Elemento JSX em tela cheia.
 */
export function ProjectorView({ state, dispatch }: ProjectorViewProps) {
  // Oculta o cursor do mouse e gerencia fullscreen ao pressionar F11 ou clique duplo
  useEffect(() => {
    document.title = 'LumenLite — Projetor HDMI'

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'F1' && dispatch) {
        event.preventDefault()
        dispatch({ type: 'TOGGLE_PANIC', target: 'blackout' })
      } else if (event.key === 'F2' && dispatch) {
        event.preventDefault()
        dispatch({ type: 'TOGGLE_PANIC', target: 'clearText' })
      }
    }

    window.addEventListener('keydown', handleKeyDown)

    return () => {
      window.removeEventListener('keydown', handleKeyDown)
    }
  }, [dispatch])

  return (
    <div className="h-screen w-screen bg-black overflow-hidden cursor-none select-none">
      <ProjectorScreen state={state} isPreview={false} />
    </div>
  )
}
