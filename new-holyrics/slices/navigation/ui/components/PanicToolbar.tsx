/**
 * @module @slices/navigation/ui/components/PanicToolbar
 * @description Barra de controles críticos de saída ao vivo (F1, F2, F3).
 */

import { EyeOff, FileText, Snowflake } from 'lucide-react'
import type { PanicMask } from '@lumen/core'

export interface PanicToolbarProps {
  panic: PanicMask
  onTogglePanic: (target: 'blackout' | 'clearText' | 'freeze') => void
}

/**
 * Componente com os três controles imediatos de saída ao vivo do operador.
 *
 * @param props - Propriedades da barra.
 * @param props.panic - Máscara de estados de pânico ativos.
 * @param props.onTogglePanic - Callback para alternar estado de pânico.
 * @returns Elemento JSX com botões de controle de saída.
 */
export function PanicToolbar({ panic, onTogglePanic }: PanicToolbarProps) {
  return (
    <div className="rounded-lg bg-[#141820] p-0.5 border border-white/5 flex items-center">
      <button
        type="button"
        onClick={() => onTogglePanic('blackout')}
        title="Escurecer completamente o telão (F1)"
        className={`flex items-center gap-1.5 px-2.5 py-1 text-[11px] font-medium rounded transition select-none ${
          panic.blackout
            ? 'bg-rose-500/20 text-rose-300 ring-1 ring-rose-500/50'
            : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
        }`}
      >
        <EyeOff className="h-3.5 w-3.5" />
        <span>Blackout</span>
      </button>

      <button
        type="button"
        onClick={() => onTogglePanic('clearText')}
        title="Ocultar texto do telão mantendo o fundo (F2)"
        className={`flex items-center gap-1.5 px-2.5 py-1 text-[11px] font-medium rounded transition select-none ${
          panic.clearText
            ? 'bg-amber-500/20 text-amber-300 ring-1 ring-amber-500/50'
            : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
        }`}
      >
        <FileText className="h-3.5 w-3.5" />
        <span>Ocultar Letra</span>
      </button>

      <button
        type="button"
        onClick={() => onTogglePanic('freeze')}
        title="Congelar imagem atual do telão na saída (F3)"
        className={`flex items-center gap-1.5 px-2.5 py-1 text-[11px] font-medium rounded transition select-none ${
          panic.freeze
            ? 'bg-cyan-500/20 text-cyan-300 ring-1 ring-cyan-500/50'
            : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
        }`}
      >
        <Snowflake className="h-3.5 w-3.5" />
        <span>Congelar</span>
      </button>
    </div>
  )
}
