/**
 * @module @slices/navigation/ui/components/ForegroundTextDisplay
 * @description Renderizador principal do texto litúrgico no telão com auto-fit dinâmico e higienização.
 *
 * ## Responsabilidades:
 * - Aplicar useAutoFitText para cálculo responsivo de fonte sem quebra de tela.
 * - Higienizar marcações musicais legadas ([CORO], [V1], etc) em tempo de execução.
 * - Renderizar metadados discretos (referência bíblica, título de hino).
 * - Aplicar sombra suave de texto conforme tokens Slate Pro Studio.
 */

import { useRef } from 'react'
import type { ForegroundLayer, MasterMode } from '@lumen/core'
import { sanitizeSlideLines } from '../../daemon/text-sanitizer'
import { useAutoFitText } from '../hooks/useAutoFitText'
import type { ProjectorDisplayConfig } from '../../contracts/projector-contracts'
import { DEFAULT_PROJECTOR_CONFIG } from '../../contracts/projector-contracts'

export interface ForegroundTextDisplayProps {
  foreground: ForegroundLayer
  mode: MasterMode
  config?: ProjectorDisplayConfig
}

/**
 * Display de texto do telão do projetor HDMI.
 *
 * @param props - Propriedades com camada foreground e modo da projeção.
 * @returns Elemento JSX com o texto auto-ajustado e metadados sóbrios.
 */
export function ForegroundTextDisplay({
  foreground,
  mode,
  config = DEFAULT_PROJECTOR_CONFIG,
}: ForegroundTextDisplayProps) {
  const containerRef = useRef<HTMLDivElement>(null)

  // Higieniza as linhas de texto para a congregação
  const cleanLines = sanitizeSlideLines(foreground.content)

  const { fontSize } = useAutoFitText({
    containerRef,
    content: cleanLines,
    minFontSize: 24,
    maxFontSize: 86,
    lineHeightMultiplier: 1.3,
  })

  if (cleanLines.length === 0) {
    return null
  }

  return (
    <div
      ref={containerRef}
      className="relative z-30 flex h-full w-full flex-col justify-center items-center select-none"
      style={{
        padding: `${config.safeAreaMarginPercent}%`,
        fontFamily: config.fontFamily,
      }}
    >
      {/* Metadados sóbrios no topo/rodapé */}
      {mode === 'BIBLE' && foreground.reference && (
        <div className="mb-4 inline-flex items-center rounded-full bg-slate-900/60 px-4 py-1 text-sm font-semibold tracking-wide text-sky-300 backdrop-blur-xs border border-white/10 shadow-lg">
          {foreground.reference}
        </div>
      )}

      {/* Conteúdo de Texto com Auto-Fit e Sombra */}
      <div
        className="w-full text-center font-bold tracking-tight text-white transition-all duration-150"
        style={{
          fontSize: `${fontSize}px`,
          lineHeight: 1.3,
          textShadow: config.textShadow,
          letterSpacing: config.letterSpacing,
          whiteSpace: 'pre-wrap',
          wordBreak: 'break-word',
        }}
      >
        {cleanLines.map((line, index) => (
          <p key={`${line}-${index}`} className="mb-2 last:mb-0">
            {line}
          </p>
        ))}
      </div>

      {/* Indicador discreto de slide/estrofe se for louvor */}
      {mode === 'SONG' && foreground.totalSlides > 1 && (
        <div className="absolute bottom-4 right-6 text-xs font-mono text-white/30 select-none">
          {foreground.slideIndex + 1} / {foreground.totalSlides}
        </div>
      )}
    </div>
  )
}
