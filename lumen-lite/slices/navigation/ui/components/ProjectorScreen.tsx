/**
 * @module @slices/navigation/ui/components/ProjectorScreen
 * @description Telão completo de projeção HDMI integrando background em loop, texto auto-fit, Lower Thirds e máscaras de pânico.
 */

import { useRef, useEffect } from 'react'
import type { ScreenState } from '@lumen/core'
import type { LowerThirdAlert } from '@slices/stage/contracts/stage-contracts'
import { LowerThirdOverlay } from '@slices/stage/ui/components/LowerThirdOverlay'
import { BackgroundVideoLoop } from './BackgroundVideoLoop'
import { ForegroundTextDisplay } from './ForegroundTextDisplay'
import type { ProjectorDisplayConfig } from '../../contracts/projector-contracts'
import { DEFAULT_PROJECTOR_CONFIG } from '../../contracts/projector-contracts'

export interface ProjectorScreenProps {
  state: ScreenState
  activeAlert?: LowerThirdAlert | null
  config?: ProjectorDisplayConfig
  isPreview?: boolean
}

/**
 * Componente do telão HDMI com suporte integral à FSM, Lower Thirds e máscaras de pânico.
 *
 * @param props - Propriedades contendo o estado da tela, alertas e configurações.
 * @param props.state - Estado atômico da projeção.
 * @param props.activeAlert - Alerta Lower Third ativo opcional.
 * @param props.config - Configurações tipográficas e de margem.
 * @param props.isPreview - Indica se está renderizado na miniatura da Coluna 4.
 * @returns Elemento JSX com a composição visual completa da projeção.
 */
export function ProjectorScreen({
  state,
  activeAlert = null,
  config = DEFAULT_PROJECTOR_CONFIG,
  isPreview = false,
}: ProjectorScreenProps) {
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

      {/* Camada 2: Foreground Text Display (suporta single e Dual Version) */}
      {!panic.clearText && (
        <ForegroundTextDisplay
          foreground={foreground}
          mode={mode}
          config={config}
          isPreview={isPreview}
        />
      )}

      {/* Camada 3: Alertas Instantâneos & Lower Thirds (RF-24) */}
      <LowerThirdOverlay alert={activeAlert} />

      {/* Badge discreto de FREEZE no modo de preview */}
      {isPreview && panic.freeze && (
        <div className="absolute top-2 right-2 z-40 rounded bg-cyan-500/20 px-1.5 py-0.5 text-[9px] font-mono text-cyan-300 border border-cyan-500/30">
          CONGELADO
        </div>
      )}
    </div>
  )
}
