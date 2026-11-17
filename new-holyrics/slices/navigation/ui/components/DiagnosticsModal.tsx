/**
 * @module @slices/navigation/ui/components/DiagnosticsModal
 * @description Modal de Diagnóstico e Informações Técnicas do Sistema (telemetria da FSM, IPC e conexões).
 */

import { X, Activity, Cpu, Wifi, Monitor } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import type { ScreenState } from '@lumen/core'

export interface DiagnosticsModalProps {
  isOpen: boolean
  isConnected: boolean
  state: ScreenState
  onClose: () => void
}

/**
 * Modal de diagnóstico técnico e status de infraestrutura do LumenLite.
 *
 * @param props - Propriedades do modal de diagnóstico.
 * @returns Elemento JSX do modal ou null se fechado.
 */
export function DiagnosticsModal({
  isOpen,
  isConnected,
  state,
  onClose,
}: DiagnosticsModalProps) {
  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex w-full max-w-md flex-col rounded-xl border border-zinc-800 bg-zinc-950 text-zinc-100 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between border-b border-zinc-800 bg-zinc-900 px-5 py-3.5">
          <div className="flex items-center gap-2">
            <Activity className="h-4 w-4 text-zinc-400" />
            <h2 className="text-sm font-semibold text-white">Diagnóstico & Sobre o LumenLite</h2>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose} title="Fechar diagnóstico">
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex flex-col gap-3 p-5 text-xs">
          <div className="grid grid-cols-2 gap-2.5">
            <div className="flex flex-col gap-1 rounded-lg border border-zinc-800 bg-zinc-900 p-3">
              <span className="flex items-center gap-1.5 text-[11px] text-zinc-400">
                <Wifi className="h-3.5 w-3.5 text-emerald-400" />
                Serviço Local
              </span>
              <span className="tabular-nums font-mono text-xs font-semibold text-zinc-100">
                {isConnected ? 'Online (WebSocket Ativo)' : 'Reconectando...'}
              </span>
            </div>

            <div className="flex flex-col gap-1 rounded-lg border border-zinc-800 bg-zinc-900 p-3">
              <span className="flex items-center gap-1.5 text-[11px] text-zinc-400">
                <Cpu className="h-3.5 w-3.5 text-zinc-400" />
                Estado de Projeção
              </span>
              <span className="tabular-nums font-mono text-xs font-semibold text-zinc-100">
                Revisão #{state.version} • &lt;1ms
              </span>
            </div>
          </div>

          <div className="flex flex-col gap-1.5 rounded-lg border border-zinc-800 bg-zinc-900 p-3">
            <span className="flex items-center gap-1.5 text-[11px] text-zinc-400">
              <Monitor className="h-3.5 w-3.5 text-zinc-400" />
              Modo de Saída Atual
            </span>
            <span className="tabular-nums font-mono text-xs text-zinc-200">
              Modo: {state.mode} | Slide: {state.foreground.slideIndex + 1}/
              {Math.max(state.foreground.totalSlides, 1)}
            </span>
          </div>

          <div className="mt-1 flex items-center justify-between border-t border-zinc-800 pt-3 text-[11px] text-zinc-400">
            <span>LumenLite Studio Console</span>
            <span className="tabular-nums font-mono text-zinc-500">v1.4.0</span>
          </div>
        </div>
      </div>
    </div>
  )
}
