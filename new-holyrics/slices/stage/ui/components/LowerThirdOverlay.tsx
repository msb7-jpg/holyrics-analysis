/**
 * @module @slices/stage/ui/components/LowerThirdOverlay
 * @description Componente de overlay para Alertas Instantâneos e Lower Thirds (RF-24).
 * Renderizado tanto no telão principal (`/projector`) quanto na saída transparente do OBS (`/broadcast`).
 */

import { Bell, AlertTriangle } from 'lucide-react'
import type { LowerThirdAlert } from '../../contracts/stage-contracts'

export interface LowerThirdOverlayProps {
  alert: LowerThirdAlert | null
}

/**
 * Renderiza o banner sobreposto (Lower Third) para avisos emergenciais (berçário, estacionamento, recados).
 *
 * @param props - Propriedades contendo o alerta ativo.
 * @param props.alert - Alerta ativo em exibição ou null.
 * @returns Elemento JSX do banner Lower Third ou null.
 */
export function LowerThirdOverlay({ alert }: LowerThirdOverlayProps) {
  if (!alert) {
    return null
  }

  const isUrgent = alert.style === 'urgent'
  const isMinimal = alert.style === 'minimal'
  const positionClass = alert.position === 'top' ? 'top-6' : 'bottom-8'

  return (
    <div
      className={`pointer-events-none absolute inset-x-0 ${positionClass} z-50 flex justify-center px-8 select-none`}
    >
      <div
        className={`flex max-w-4xl items-center gap-4 rounded-xl border px-6 py-3.5 shadow-2xl backdrop-blur-md transition-all duration-300 ${
          isUrgent
            ? 'border-red-500/50 bg-slate-950/90 text-white'
            : isMinimal
              ? 'border-white/15 bg-slate-900/80 text-slate-100'
              : 'border-sky-500/40 bg-slate-900/90 text-slate-50'
        }`}
      >
        <div
          className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-lg ${
            isUrgent ? 'bg-red-500/20 text-red-400' : 'bg-sky-500/20 text-sky-400'
          }`}
        >
          {isUrgent ? <AlertTriangle className="h-5 w-5" /> : <Bell className="h-5 w-5" />}
        </div>

        <div className="flex flex-col">
          {!isMinimal && alert.label && (
            <span
              className={`font-mono text-[11px] font-bold tracking-widest uppercase ${
                isUrgent ? 'text-red-400' : 'text-sky-400'
              }`}
            >
              {alert.label}
            </span>
          )}
          <p className="text-lg font-semibold leading-snug tracking-tight text-white">
            {alert.message}
          </p>
        </div>
      </div>
    </div>
  )
}
