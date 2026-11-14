/**
 * @module @slices/navigation/ui/components/ForegroundTextDisplay
 * @description Renderizador principal do texto litúrgico no telão com auto-fit dinâmico,
 * higienização de tags e suporte nativo a Tela Dividida Bíblica (Dual Version - RF-14).
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
 * Display de texto do telão do projetor HDMI com suporte a projeção simples e Dual Version (RF-14).
 *
 * @param props - Propriedades com camada foreground e modo da projeção.
 * @param props.foreground - Camada de texto ativa na FSM.
 * @param props.mode - Modo mestre da projeção.
 * @param props.config - Configurações tipográficas do telão.
 * @returns Elemento JSX com o texto auto-ajustado e metadados sóbrios.
 */
export function ForegroundTextDisplay({
  foreground,
  mode,
  config = DEFAULT_PROJECTOR_CONFIG,
}: ForegroundTextDisplayProps) {
  const containerRef = useRef<HTMLDivElement>(null)

  const cleanLines = sanitizeSlideLines(foreground.content)
  const cleanSecondaryLines = foreground.secondaryContent
    ? sanitizeSlideLines(foreground.secondaryContent)
    : []
  const isDualBible = mode === 'BIBLE' && cleanSecondaryLines.length > 0

  // Se for Dual Version, passamos o maior texto entre as duas colunas para que o auto-fit caiba ambas perfeitamente
  const longestColumnLines =
    isDualBible && cleanSecondaryLines.join(' ').length > cleanLines.join(' ').length
      ? cleanSecondaryLines
      : cleanLines

  const { fontSize } = useAutoFitText({
    containerRef,
    content: longestColumnLines,
    minFontSize: 22,
    maxFontSize: isDualBible ? 64 : 86,
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
      {isDualBible ? (
        /* Modo Tela Dividida (Dual Version - RF-14) */
        <div className="grid h-full w-full grid-cols-2 items-center gap-8 divide-x divide-white/15">
          {/* Coluna Esquerda: Versão Primária */}
          <div className="flex flex-col items-center justify-center pr-4 text-center">
            {foreground.reference && (
              <div className="mb-4 inline-flex items-center rounded-full bg-slate-900/70 px-4 py-1 text-sm font-semibold tracking-wide text-sky-300 border border-white/10 shadow-lg">
                {foreground.reference}
              </div>
            )}
            <div
              className="w-full font-bold tracking-tight text-white transition-all duration-150"
              style={{
                fontSize: `${Math.max(20, Math.round(fontSize * 0.82))}px`,
                lineHeight: 1.3,
                textShadow: config.textShadow,
                letterSpacing: config.letterSpacing,
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
              }}
            >
              {cleanLines.map((line, index) => (
                <p key={`primary-${index}`} className="mb-2 last:mb-0">
                  {line}
                </p>
              ))}
            </div>
          </div>

          {/* Coluna Direita: Segunda Tradução (Dual Version) */}
          <div className="flex flex-col items-center justify-center pl-8 text-center">
            {(foreground.secondaryReference || foreground.reference) && (
              <div className="mb-4 inline-flex items-center rounded-full bg-slate-900/70 px-4 py-1 text-sm font-semibold tracking-wide text-amber-300 border border-white/10 shadow-lg">
                {foreground.secondaryReference || foreground.reference}
              </div>
            )}
            <div
              className="w-full font-bold tracking-tight text-slate-100 transition-all duration-150"
              style={{
                fontSize: `${Math.max(20, Math.round(fontSize * 0.82))}px`,
                lineHeight: 1.3,
                textShadow: config.textShadow,
                letterSpacing: config.letterSpacing,
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
              }}
            >
              {cleanSecondaryLines.map((line, index) => (
                <p key={`secondary-${index}`} className="mb-2 last:mb-0">
                  {line}
                </p>
              ))}
            </div>
          </div>
        </div>
      ) : (
        /* Modo Padrão de Projeção (Coluna Única) */
        <>
          {mode === 'BIBLE' && foreground.reference && (
            <div className="mb-4 inline-flex items-center rounded-full bg-slate-900/60 px-4 py-1 text-sm font-semibold tracking-wide text-sky-300 backdrop-blur-xs border border-white/10 shadow-lg">
              {foreground.reference}
            </div>
          )}

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
        </>
      )}

      {/* Indicador discreto de slide/estrofe */}
      {(mode === 'SONG' || mode === 'PPTX') && foreground.totalSlides > 1 && (
        <div className="absolute bottom-4 right-6 text-xs font-mono text-white/30 select-none">
          {foreground.slideIndex + 1} / {foreground.totalSlides}
        </div>
      )}
    </div>
  )
}
