/**
 * @module @lumen/broadcast/BroadcastView
 * @description Renderizador principal do overlay de Broadcast (Lower Thirds e legendas) para o OBS Studio (RF-23, RF-24).
 */

import { useBroadcastConnector } from './useBroadcastConnector'
import { LowerThirdOverlay } from '@slices/stage/ui/components/LowerThirdOverlay'

/**
 * Componente principal da visualização de broadcast com canal alfa transparente (`rgba(0,0,0,0)`).
 *
 * @returns Elemento JSX do overlay de transmissão para OBS Studio / vMix.
 */
export function BroadcastView() {
  const { state, stageState, isConnected } = useBroadcastConnector()

  if (!isConnected || state.panic.blackout) {
    return <div className="fixed inset-0 pointer-events-none bg-transparent" />
  }

  const { foreground } = state
  const hasSlideContent =
    !state.panic.clearText &&
    state.mode !== 'EMPTY' &&
    foreground.content &&
    foreground.content.length > 0

  return (
    <div className="fixed inset-0 pointer-events-none bg-transparent flex flex-col justify-end items-center p-12 select-none">
      {/* Alertas de Emergência Lower Thirds (RF-24) */}
      <LowerThirdOverlay alert={stageState.activeAlert} />

      {/* Legenda / Lower Third Litúrgico (RF-23) */}
      {hasSlideContent && (
        <div className="bg-slate-900/85 px-8 py-6 rounded-xl max-w-4xl w-full border border-white/10 shadow-2xl backdrop-blur-md transition-all duration-300">
          {(foreground.title || foreground.reference) && (
            <div className="mb-3 text-sky-400 font-semibold tracking-wider text-base uppercase text-center">
              {foreground.title}
              {foreground.reference && (
                <span className="opacity-80 text-slate-300 font-normal normal-case">
                  {' '}
                  — {foreground.reference}
                </span>
              )}
            </div>
          )}

          <div className="text-white text-4xl font-bold leading-tight drop-shadow-[0_4px_4px_rgba(0,0,0,0.8)] flex flex-col gap-1.5">
            {foreground.content.map((line, index) => (
              <div key={`${line}-${index}`} className="text-center">
                {line}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
