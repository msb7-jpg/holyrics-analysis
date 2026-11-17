/**
 * @module @slices/remote/ui/components/PreacherRemoteView
 * @description Endpoint Web do Pregador / Pastor (`/remote/preacher` - RF-26A).
 * Interface móvel/tablet focada na ministração: avanço/recuo de slides com botões táteis amplos,
 * visão do slide atual, próximo slide em destaque, anotações do orador e cronômetro sincronizado.
 */

import { useState, useEffect } from 'react'
import { ChevronLeft, ChevronRight, Timer, FileText, ArrowRight, AlertCircle } from 'lucide-react'
import type { ActionCommand, ScreenState } from '@lumen/core'
import type { LiveStageState } from '@slices/stage/contracts/stage-contracts'
import { Button } from '@ui/components/ui/button'
import { Badge } from '@ui/components/ui/badge'

export interface PreacherRemoteViewProps {
  state: ScreenState
  stageState: LiveStageState
  dispatch: (command: ActionCommand) => void
}

/**
 * Formata segundos em `MM:SS` tabular para o púlpito móvel do pastor.
 *
 * @param rawSeconds - Segundos restantes.
 * @returns String formatada.
 */
function formatPreacherTimer(rawSeconds: number): string {
  const isNegative = rawSeconds < 0
  const absSeconds = Math.abs(Math.floor(rawSeconds))
  const minutes = Math.floor(absSeconds / 60)
  const seconds = absSeconds % 60
  return `${isNegative ? '-' : ''}${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

/**
 * Interface Web do Púlpito do Pregador (`/remote/preacher`).
 *
 * @param props - Propriedades da visão móvel do pregador.
 * @param props.state - Estado atual da projeção (`ScreenState`).
 * @param props.stageState - Estado sincronizado do cronômetro e recados (`LiveStageState`).
 * @param props.dispatch - Despachante de comandos restrito à navegação da apresentação.
 * @returns Elemento JSX responsivo para tablet e smartphone no púlpito.
 */
export function PreacherRemoteView({ state, stageState, dispatch }: PreacherRemoteViewProps) {
  const [remainingSeconds, setRemainingSeconds] = useState<number>(
    stageState.timer.pausedRemainingSeconds ?? stageState.timer.durationSeconds,
  )

  useEffect(() => {
    const interval = setInterval(() => {
      if (stageState.timer.isRunning && stageState.timer.endTime) {
        setRemainingSeconds(Math.round((stageState.timer.endTime - Date.now()) / 1000))
      } else {
        setRemainingSeconds(
          stageState.timer.pausedRemainingSeconds ?? stageState.timer.durationSeconds,
        )
      }
    }, 300)
    return () => clearInterval(interval)
  }, [stageState.timer])

  const { foreground } = state
  const isOvertime = stageState.timer.isRunning && remainingSeconds < 0
  const nextLines =
    foreground.nextSlideContent ??
    (foreground.slides && foreground.slides[foreground.slideIndex + 1]
      ? foreground.slides[foreground.slideIndex + 1].content
      : undefined)

  return (
    <div className="flex min-h-screen w-full flex-col bg-zinc-950 text-zinc-100 select-none">
      {/* Barra Superior Compacta com Cronômetro de Pregação */}
      <header className="flex items-center justify-between border-b border-zinc-800 bg-zinc-950 px-4 py-3">
        <div className="flex items-center gap-2">
          <span className="text-sm font-semibold text-white truncate max-w-[180px] sm:max-w-md">
            {foreground.title || 'Apresentação Ativa'}
          </span>
        </div>

        <div
          className={`flex items-center gap-2 rounded-lg border px-3 py-1.5 font-mono ${
            isOvertime
              ? 'border-red-500/50 bg-red-500/20 text-red-300 animate-pulse'
              : 'border-zinc-700 bg-zinc-800 text-zinc-200'
          }`}
        >
          <Timer className="h-4 w-4" />
          <span className="text-xl font-bold tabular-nums">
            {formatPreacherTimer(remainingSeconds)}
          </span>
        </div>
      </header>

      {/* Recado Urgente da Cabine */}
      {stageState.pastorMessage && (
        <div className="flex items-center gap-2 border-b border-amber-500/40 bg-amber-500/20 px-4 py-2.5 text-sm font-semibold text-amber-200">
          <AlertCircle className="h-4 w-4 shrink-0" />
          <span>{stageState.pastorMessage.message}</span>
        </div>
      )}

      {/* Corpo com Slide Atual, Próximo Slide e Notas do Orador */}
      <main className="flex flex-1 flex-col gap-4 p-4 overflow-y-auto">
        {/* Card do Slide Atual */}
        <div className="flex flex-col rounded-xl border border-zinc-700 bg-zinc-800 p-5 shadow-lg">
          <div className="mb-2 flex items-center justify-between text-xs tabular-nums font-mono text-zinc-400">
            <span>NO TELÃO AGORA</span>
            {foreground.totalSlides > 0 && (
              <span>
                {foreground.slideIndex + 1} / {foreground.totalSlides}
              </span>
            )}
          </div>
          {foreground.reference && (
            <div className="mb-2 text-xs font-semibold text-zinc-400">
              {foreground.reference}
            </div>
          )}
          <div className="flex flex-col gap-2 py-2">
            {foreground.content.length > 0 ? (
              foreground.content.map((line, index) => (
                <p key={`${line}-${index}`} className="text-2xl font-bold leading-snug text-white">
                  {line}
                </p>
              ))
            ) : (
              <p className="text-base text-zinc-500 italic">Nenhum slide projetado</p>
            )}
          </div>
        </div>

        {/* Card do Próximo Slide */}
        <div className="flex flex-col rounded-xl border border-zinc-800 bg-zinc-900 p-4">
          <div className="mb-1.5 flex items-center gap-1.5 text-xs font-mono text-amber-400 uppercase">
            <ArrowRight className="h-3.5 w-3.5" />
            <span>Próximo Slide</span>
          </div>
          {nextLines && nextLines.length > 0 ? (
            nextLines.map((line, index) => (
              <p key={`next-${index}`} className="text-lg font-medium text-zinc-300">
                {line}
              </p>
            ))
          ) : (
            <p className="text-sm text-zinc-500 italic">Fim dos slides desta seção</p>
          )}
        </div>

        {/* Card das Anotações do Orador (Speaker Notes) */}
        <div className="flex flex-1 flex-col rounded-xl border border-zinc-800 bg-zinc-900 p-4">
          <div className="mb-2 flex items-center gap-1.5 text-xs font-mono text-zinc-400 uppercase">
            <FileText className="h-3.5 w-3.5 text-zinc-400" />
            <span>Notas do Orador</span>
          </div>
          <p className="text-base leading-relaxed text-zinc-200 whitespace-pre-wrap">
            {foreground.speakerNotes || 'Sem anotações adicionais para este slide.'}
          </p>
        </div>
      </main>

      {/* Barra Inferior de Toque Generoso (Bloqueada apenas para Navegação - RF-26A) */}
      <footer className="grid grid-cols-2 gap-3 border-t border-zinc-800 bg-zinc-950 p-4">
        <Button
          variant="secondary"
          size="lg"
          className="h-14 text-base font-semibold"
          onClick={() => dispatch({ type: 'NAV_PREV' })}
        >
          <ChevronLeft className="h-5 w-5" />
          <span>Anterior</span>
        </Button>

        <Button
          variant="default"
          size="lg"
          className="h-14 text-base font-semibold"
          onClick={() => dispatch({ type: 'NAV_NEXT' })}
        >
          <span>Próximo Slide</span>
          <ChevronRight className="h-5 w-5" />
        </Button>
      </footer>
    </div>
  )
}
