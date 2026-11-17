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
    <div className="inline-flex items-center rounded-md border border-zinc-800 bg-zinc-900/50 p-0.5">
      <button
        type="button"
        onClick={() => onTogglePanic('blackout')}
        title="Escurecer completamente o telão (F1)"
        className={`flex items-center gap-1.5 px-2.5 py-1 text-xs font-medium rounded-sm transition-colors select-none ${
          panic.blackout
            ? 'bg-red-500/15 text-red-400 border border-red-500/30'
            : 'text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800/50 border border-transparent'
        }`}
      >
        <EyeOff className="h-3.5 w-3.5" strokeWidth={1.75} />
        <span>Blackout</span>
      </button>

      <button
        type="button"
        onClick={() => onTogglePanic('clearText')}
        title="Ocultar texto do telão mantendo o fundo (F2)"
        className={`flex items-center gap-1.5 px-2.5 py-1 text-xs font-medium rounded-sm transition-colors select-none ${
          panic.clearText
            ? 'bg-amber-500/15 text-amber-300 border border-amber-500/30'
            : 'text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800/50 border border-transparent'
        }`}
      >
        <FileText className="h-3.5 w-3.5" strokeWidth={1.75} />
        <span>Ocultar Letra</span>
      </button>

      <button
        type="button"
        onClick={() => onTogglePanic('freeze')}
        title="Congelar imagem atual do telão na saída (F3)"
        className={`flex items-center gap-1.5 px-2.5 py-1 text-xs font-medium rounded-sm transition-colors select-none ${
          panic.freeze
            ? 'bg-sky-500/15 text-sky-300 border border-sky-500/30'
            : 'text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800/50 border border-transparent'
        }`}
      >
        <Snowflake className="h-3.5 w-3.5" strokeWidth={1.75} />
        <span>Congelar</span>
      </button>
    </div>
  )
}
