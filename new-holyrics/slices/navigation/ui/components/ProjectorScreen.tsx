/**
 * @module @slices/navigation/ui/components/ProjectorScreen
 * @description Telão completo de projeção HDMI integrando background em loop, texto auto-fit, Lower Thirds e máscaras de pânico.
 */

import { useRef, useEffect, useState } from 'react'
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
 * Componente do telão HDMI com suporte integral à FSM, Lower Thirds e escalonamento proporcional 16:9 no modo preview.
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
  const previewWrapperRef = useRef<HTMLDivElement | null>(null)
  const [previewScale, setPreviewScale] = useState(0.2)

  useEffect(() => {
    if (!state.panic.freeze) {
      frozenStateRef.current = state
    }
  }, [state])

  useEffect(() => {
    if (!isPreview) return
    const wrapper = previewWrapperRef.current
    if (!wrapper) return

    const updateScale = () => {
      const width = wrapper.clientWidth
      if (width > 0) {
        setPreviewScale(width / 1920)
      }
    }

    updateScale()
    const observer = new ResizeObserver(updateScale)
    observer.observe(wrapper)
    return () => observer.disconnect()
  }, [isPreview])

  const effectiveState = state.panic.freeze ? frozenStateRef.current : state
  const { panic, background, foreground, mode } = effectiveState

  // 1. Blackout Total
  if (panic.blackout) {
    return (
      <div
        ref={previewWrapperRef}
        className="relative h-full w-full bg-black flex items-center justify-center select-none overflow-hidden"
      >
        {isPreview && (
          <span className="text-[10px] font-mono font-bold tracking-widest text-red-500/80 uppercase">
            BLACKOUT ATIVO
          </span>
        )}
      </div>
    )
  }

  if (isPreview) {
    return (
      <div
        ref={previewWrapperRef}
        className="relative h-full w-full bg-black overflow-hidden select-none"
      >
        <div
          style={{
            width: '1920px',
            height: '1080px',
            transform: `scale(${previewScale})`,
            transformOrigin: 'top left',
          }}
          className="relative bg-black overflow-hidden"
        >
          {!panic.clearBg && mode !== 'PPTX' ? (
            <BackgroundVideoLoop
              background={background}
              crossfadeDurationMs={config.videoCrossfadeDurationMs}
            />
          ) : (
            <div className="absolute inset-0 bg-black" />
          )}

          {!panic.clearText && mode !== 'MEDIA' && (
            <ForegroundTextDisplay
              foreground={foreground}
              mode={mode}
              config={config}
              isPreview={false}
            />
          )}

          <LowerThirdOverlay alert={activeAlert} />
        </div>

        {panic.freeze && (
          <div className="absolute top-2 right-2 z-40 rounded bg-cyan-500/20 px-1.5 py-0.5 text-[9px] font-mono text-cyan-300 border border-cyan-500/30">
            CONGELADO
          </div>
        )}
      </div>
    )
  }

  return (
    <div className="relative h-full w-full bg-black overflow-hidden select-none">
      {/* Camada 1: Background com Seamless Double-Buffer (suprimido em apresentações PPTX/PDF) */}
      {!panic.clearBg && mode !== 'PPTX' ? (
        <BackgroundVideoLoop
          background={background}
          crossfadeDurationMs={config.videoCrossfadeDurationMs}
        />
      ) : (
        <div className="absolute inset-0 bg-black" />
      )}

      {/* Camada 2: Foreground Text Display (suporta single e Dual Version) */}
      {!panic.clearText && mode !== 'MEDIA' && (
        <ForegroundTextDisplay
          foreground={foreground}
          mode={mode}
          config={config}
          isPreview={false}
        />
      )}

      {/* Camada 3: Alertas Instantâneos & Lower Thirds */}
      <LowerThirdOverlay alert={activeAlert} />
    </div>
  )
}
