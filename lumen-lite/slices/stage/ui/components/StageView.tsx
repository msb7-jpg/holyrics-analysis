/**
 * @module @slices/stage/ui/components/StageView
 * @description Monitor de Retorno de Palco (`/stage` - RF-02, RF-25).
 * Tela dedicada de alto contraste para púlpito e músicos: exibe slide atual, próximo slide em destaque,
 * relógio de cabeçalho tabular, cronômetro de pregação sincronizado e notas do orador.
 */

import { useState, useEffect } from 'react'
import { Clock, Timer, FileText, ArrowRight, AlertCircle } from 'lucide-react'
import type { ScreenState } from '@lumen/core'
import type { LiveStageState } from '../../contracts/stage-contracts'
import { Badge } from '@ui/components/ui/badge'

export interface StageViewProps {
  state: ScreenState
  stageState: LiveStageState
}

/**
 * Formata segundos em `MM:SS` ou `HH:MM:SS` tabular para o cronômetro do palco.
 *
 * @param rawSeconds - Segundos restantes (ou negativos se estourado).
 * @returns String formatada para leitura rápida à distância.
 */
function formatStageTimer(rawSeconds: number): string {
  const isNegative = rawSeconds < 0
  const absSeconds = Math.abs(Math.floor(rawSeconds))
  const hours = Math.floor(absSeconds / 3600)
  const minutes = Math.floor((absSeconds % 3600) / 60)
  const seconds = absSeconds % 60

  const prefix = isNegative ? '-' : ''
  const minStr = String(minutes).padStart(2, '0')
  const secStr = String(seconds).padStart(2, '0')

  if (hours > 0) {
    return `${prefix}${String(hours).padStart(2, '0')}:${minStr}:${secStr}`
  }
  return `${prefix}${minStr}:${secStr}`
}

/**
 * Renderiza o Monitor de Retorno (`/stage`) para púlpito, banda e ministros.
 *
 * @param props - Propriedades com estado do telão e estado do palco.
 * @param props.state - Estado atual da projeção (`ScreenState`).
 * @param props.stageState - Estado atual do palco, alertas e cronômetro (`LiveStageState`).
 * @returns Elemento JSX em tela cheia de alto contraste neutro (Slate Pro Studio).
 */
export function StageView({ state, stageState }: StageViewProps) {
  const [clockText, setClockText] = useState(() => {
    const now = new Date()
    return now.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  })
  const [remainingSeconds, setRemainingSeconds] = useState<number>(
    stageState.timer.pausedRemainingSeconds ?? stageState.timer.durationSeconds,
  )

  useEffect(() => {
    const interval = setInterval(() => {
      const now = new Date()
      setClockText(
        now.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit', second: '2-digit' }),
      )

      if (stageState.timer.isRunning && stageState.timer.endTime) {
        const diff = Math.round((stageState.timer.endTime - Date.now()) / 1000)
        setRemainingSeconds(diff)
      } else {
        setRemainingSeconds(
          stageState.timer.pausedRemainingSeconds ?? stageState.timer.durationSeconds,
        )
      }
    }, 250)

    return () => clearInterval(interval)
  }, [stageState.timer])

  const { foreground, panic, mode } = state
  const isOvertime = stageState.timer.isRunning && remainingSeconds < 0
  const currentLines = foreground.content || []
  const nextLines =
    foreground.nextSlideContent ??
    (foreground.slides && foreground.slides[foreground.slideIndex + 1]
      ? foreground.slides[foreground.slideIndex + 1].content
      : undefined)
  const isLastSlide =
    foreground.totalSlides > 0 && foreground.slideIndex >= foreground.totalSlides - 1

  const transformCase = (line: string) =>
    stageState.uppercaseMode ? line.toUpperCase() : line

  return (
    <div className="flex h-screen w-screen flex-col bg-slate-950 text-slate-50 select-none overflow-hidden">
      {/* Header de Alto Contraste: Status, Relógio Tabular & Cronômetro de Pregação */}
      <header className="flex h-20 items-center justify-between border-b border-white/10 bg-slate-900 px-8">
        <div className="flex items-center gap-4">
          <Badge variant="live">PALCO / RETORNO</Badge>
          {foreground.title && (
            <span className="text-lg font-semibold text-slate-200 truncate max-w-md">
              {foreground.title}
            </span>
          )}
          {foreground.reference && (
            <span className="text-base font-medium text-sky-400">
              {foreground.reference}
            </span>
          )}
          {panic.blackout && <Badge variant="panic">TELÃO EM BLACKOUT</Badge>}
          {panic.clearText && <Badge variant="panic">TEXTO OCULTO NO TELÃO</Badge>}
        </div>

        <div className="flex items-center gap-8">
          {/* Cronômetro de Pregação (RF-25) */}
          <div
            className={`flex items-center gap-3 rounded-xl border px-5 py-2 font-mono ${
              isOvertime
                ? 'border-red-500/50 bg-red-500/15 text-red-300 animate-pulse'
                : stageState.timer.isRunning
                  ? 'border-sky-500/40 bg-sky-500/10 text-sky-300'
                  : 'border-white/10 bg-slate-800/70 text-slate-300'
            }`}
          >
            <Timer className="h-5 w-5" />
            <div className="flex flex-col">
              <span className="text-[10px] uppercase tracking-wider text-slate-400">
                {stageState.timer.label}
              </span>
              <span className="text-3xl font-bold tracking-tight tabular-nums">
                {formatStageTimer(remainingSeconds)}
              </span>
            </div>
          </div>

          {/* Relógio de Cabeçalho Tabular */}
          <div className="flex items-center gap-3 rounded-xl border border-white/10 bg-slate-800/50 px-5 py-2 font-mono">
            <Clock className="h-5 w-5 text-slate-400" />
            <div className="flex flex-col">
              <span className="text-[10px] uppercase tracking-wider text-slate-400">
                HORA LOCAL
              </span>
              <span className="text-3xl font-bold tracking-tight text-white tabular-nums">
                {clockText}
              </span>
            </div>
          </div>
        </div>
      </header>

      {/* Banner de Mensagem Direta ao Púlpito (PastorMessage) */}
      {stageState.pastorMessage && (
        <div
          className={`flex items-center justify-center gap-3 border-b px-8 py-3 text-xl font-bold ${
            stageState.pastorMessage.level === 'urgent'
              ? 'border-red-500/40 bg-red-500/20 text-red-200'
              : stageState.pastorMessage.level === 'warning'
                ? 'border-amber-500/40 bg-amber-500/20 text-amber-200'
                : 'border-sky-500/40 bg-sky-500/20 text-sky-200'
          }`}
        >
          <AlertCircle className="h-6 w-6 shrink-0" />
          <span>RECADO DA CABINE: {stageState.pastorMessage.message}</span>
        </div>
      )}

      {/* Área Principal: Slide Atual (Esquerda 62%) + Próximo Slide e Notas do Orador (Direita 38%) */}
      <main className="grid flex-1 grid-cols-12 gap-6 p-6 overflow-hidden">
        {/* Slide Atual em Destaque */}
        <section className="col-span-7 flex flex-col justify-between rounded-2xl border border-white/10 bg-slate-900/90 p-8 shadow-xl overflow-hidden">
          <div className="flex items-center justify-between border-b border-white/10 pb-4">
            <span className="font-mono text-xs font-bold tracking-widest text-sky-400 uppercase">
              SLIDE ATUAL ({mode})
            </span>
            {foreground.totalSlides > 0 && (
              <span className="font-mono text-sm font-semibold text-slate-400">
                Slide {foreground.slideIndex + 1} de {foreground.totalSlides}
              </span>
            )}
          </div>

          <div className="my-auto flex flex-col gap-4 py-6">
            {currentLines.length > 0 ? (
              currentLines.map((line, index) => (
                <p
                  key={`${line}-${index}`}
                  className="text-4xl font-bold leading-snug tracking-tight text-white"
                >
                  {transformCase(line)}
                </p>
              ))
            ) : (
              <p className="text-2xl font-medium text-slate-500 italic">
                Nenhum texto em exibição no momento
              </p>
            )}
          </div>

          <div className="flex items-center justify-between border-t border-white/5 pt-3 text-xs text-slate-500">
            <span>LumenLite Stage Monitor</span>
            <span>Sincronizado em Tempo Real (Sub-ms)</span>
          </div>
        </section>

        {/* Coluna Direita: Próximo Slide em Destaque + Notas do Orador */}
        <section className="col-span-5 flex flex-col gap-6 overflow-hidden">
          {/* Próximo Slide (inspirado em StageViewUtils.java) */}
          <div className="flex flex-1 flex-col rounded-2xl border border-white/10 bg-slate-900/60 p-6 overflow-y-auto">
            <div className="mb-4 flex items-center justify-between border-b border-white/10 pb-3">
              <div className="flex items-center gap-2 text-amber-400">
                <ArrowRight className="h-4 w-4" />
                <span className="font-mono text-xs font-bold tracking-widest uppercase">
                  PRÓXIMO SLIDE
                </span>
              </div>
              {isLastSlide && (
                <Badge variant="default">FIM DO ITEM</Badge>
              )}
            </div>

            <div className="flex flex-col gap-2.5 my-auto">
              {nextLines && nextLines.length > 0 ? (
                nextLines.map((line, index) => (
                  <p
                    key={`next-${index}`}
                    className="text-2xl font-semibold leading-relaxed text-slate-300"
                  >
                    {transformCase(line)}
                  </p>
                ))
              ) : isLastSlide ? (
                <p className="text-xl font-mono font-semibold text-amber-400/80">
                  — FIM DA MÚSICA / APRESENTAÇÃO —
                </p>
              ) : (
                <p className="text-base text-slate-500 italic">
                  Aguardando próximo slide...
                </p>
              )}
            </div>
          </div>

          {/* Notas do Orador (Speaker Notes - RF-02 / RF-21) */}
          <div className="flex flex-1 flex-col rounded-2xl border border-white/10 bg-slate-900/60 p-6 overflow-y-auto">
            <div className="mb-3 flex items-center gap-2 border-b border-white/10 pb-3 text-slate-300">
              <FileText className="h-4 w-4 text-sky-400" />
              <span className="font-mono text-xs font-bold tracking-widest uppercase">
                ANOTAÇÕES DO ORADOR / PÚLPITO
              </span>
            </div>

            <div className="text-lg leading-relaxed text-slate-200 whitespace-pre-wrap">
              {foreground.speakerNotes ? (
                foreground.speakerNotes
              ) : (
                <span className="text-sm text-slate-500 italic">
                  Nenhuma anotação vinculada a este slide.
                </span>
              )}
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}
