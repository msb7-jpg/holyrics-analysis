/**
 * @module @slices/navigation/ui/components/ProjectorScreen
 * @description Telão completo de projeção HDMI integrando background em loop, texto auto-fit e máscaras de pânico.
 *
 * ## Responsabilidades:
 * - Espelhar fielmente o ScreenState emitido pelo daemon via WebSocket.
 * - Tratar imediatamente ações de pânico:
 *   - blackout: tela preta absoluta instantânea (0ms).
 *   - clearText: suprime texto sem interromper o loop de vídeo em background.
 *   - clearBg: oculta o vídeo de fundo mantendo apenas o texto sobre fundo escuro neutro.
 *   - freeze: congela o frame visual ignorando alterações.
 * - Servir como renderizador compartilhado tanto para a janela cheia (/projector) quanto para o preview (Coluna 4).
 */

import { useRef, useEffect } from 'react'
import type { ScreenState } from '@lumen/core'
import { BackgroundVideoLoop } from './BackgroundVideoLoop'
import { ForegroundTextDisplay } from './ForegroundTextDisplay'
import type { ProjectorDisplayConfig } from '../../contracts/projector-contracts'
import { DEFAULT_PROJECTOR_CONFIG } from '../../contracts/projector-contracts'

export interface ProjectorScreenProps {
  state: ScreenState
  config?: ProjectorDisplayConfig
  isPreview?: boolean
}

/**
 * Componente do telão HDMI com suporte integral à FSM e máscaras de pânico.
 *
 * @param props - Propriedades contendo o estado da tela e configurações.
 * @returns Elemento JSX com a composição visual completa da projeção.
 */
export function ProjectorScreen({
  state,
  config = DEFAULT_PROJECTOR_CONFIG,
  isPreview = false,
}: ProjectorScreenProps) {
  // Lógica de congelamento (Freeze)
  const frozenStateRef = useRef<ScreenState>(state)

  useEffect(() => {
    if (!state.panic.freeze) {
      frozenStateRef.current = state
    }
  }, [state])

  const effectiveState = state.panic.freeze ? frozenStateRef.current : state
  const { panic, background, foreground, mode } = effectiveState

  // 1. Blackout Total
  if (panic.blackout) {
    return (
      <div className="relative h-full w-full bg-black flex items-center justify-center select-none overflow-hidden">
        {isPreview && (
          <span className="text-[10px] font-mono font-bold tracking-widest text-red-500/80 uppercase">
            BLACKOUT ATIVO
          </span>
        )}
      </div>
    )
  }

  return (
    <div className="relative h-full w-full bg-black overflow-hidden select-none">
      {/* Camada 1: Background com Seamless Double-Buffer */}
      {!panic.clearBg ? (
        <BackgroundVideoLoop
          background={background}
          crossfadeDurationMs={config.videoCrossfadeDurationMs}
        />
      ) : (
        <div className="absolute inset-0 bg-slate-950" />
      )}

      {/* Camada 2: Foreground Text Display */}
      {!panic.clearText && (
        <ForegroundTextDisplay
          foreground={foreground}
          mode={mode}
          config={config}
        />
      )}

      {/* Badge discreto de FREEZE no modo de preview */}
      {isPreview && panic.freeze && (
        <div className="absolute top-2 right-2 z-40 rounded bg-cyan-500/20 px-1.5 py-0.5 text-[9px] font-mono text-cyan-300 border border-cyan-500/30">
          CONGELADO
        </div>
      )}
    </div>
  )
}
