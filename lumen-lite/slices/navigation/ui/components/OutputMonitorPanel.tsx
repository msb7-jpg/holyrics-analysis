/**
 * @module @slices/navigation/ui/components/OutputMonitorPanel
 * @description Coluna 4 com monitor de saída 16:9 em tempo real e controle de áudio ambiente.
 */

import { Volume2 } from 'lucide-react'
import type { ScreenState } from '@lumen/core'

export interface OutputMonitorPanelProps {
  state: ScreenState
}

/**
 * Painel da Coluna 4 do operador contendo preview de saída e áudio.
 *
 * @param props - Propriedades do painel.
 * @returns Elemento JSX com preview 16:9.
 */
export function OutputMonitorPanel({ state }: OutputMonitorPanelProps) {
  return (
    <section className="col-span-2 flex flex-col bg-slate-900 p-3 border-l border-white/5 justify-between">
      <div>
        <div className="mb-2 text-[10px] uppercase font-bold tracking-wider text-slate-400">
          Monitor de Saída
        </div>

        {/* Preview 16:9 */}
        <div className="relative aspect-video w-full rounded border border-white/10 bg-black flex items-center justify-center p-3 text-center overflow-hidden">
          {state.panic.blackout ? (
            <span className="text-[10px] font-mono text-red-400 font-bold uppercase tracking-widest">
              BLACKOUT ATIVO
            </span>
          ) : state.panic.clearText ? (
            <span className="text-[10px] font-mono text-slate-500">Texto Oculto (Loop Fundo)</span>
          ) : (
            <div className="flex flex-col gap-1 items-center">
              {state.mode === 'BIBLE' && state.foreground.reference && (
                <span className="text-[9px] font-mono text-sky-300 font-semibold uppercase">
                  {state.foreground.reference}
                </span>
              )}
              <p className="text-[11px] font-semibold text-white leading-tight">
                {state.foreground.content[0] || 'Pronto para projeção'}
              </p>
            </div>
          )}
        </div>

        <div className="mt-2 flex items-center justify-between text-[10px] text-slate-500 font-mono">
          <span>1920x1080 @ 60FPS</span>
          <span className="text-emerald-400">HDMI ATIVO</span>
        </div>
      </div>

      {/* Player de Áudio Ambiente */}
      <div className="rounded border border-white/10 bg-slate-800/60 p-2.5">
        <div className="flex items-center justify-between text-xs text-slate-300 mb-1.5">
          <span className="flex items-center gap-1.5 text-[11px] font-medium">
            <Volume2 className="h-3 w-3 text-slate-400" />
            Áudio Ambiente
          </span>
          <span className="text-[10px] text-slate-500 font-mono">-12 dB</span>
        </div>
        <p className="text-[10px] text-slate-400 truncate">Instrumental Pré-Culto (Pad)</p>
      </div>
    </section>
  )
}
