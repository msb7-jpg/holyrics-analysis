/**
 * @module @slices/navigation/ui/components/PanicToolbar
 * @description Barra de controles críticos de saída ao vivo (F1, F2, F3).
 */

import { EyeOff, FileText, Snowflake } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import type { PanicMask } from '@lumen/core'

export interface PanicToolbarProps {
  panic: PanicMask
  onTogglePanic: (target: 'blackout' | 'clearText' | 'freeze') => void
}

/**
 * Componente com os três controles imediatos de saída ao vivo do operador.
 *
 * @param props - Propriedades da barra.
 * @returns Elemento JSX com botões de controle de saída.
 */
export function PanicToolbar({ panic, onTogglePanic }: PanicToolbarProps) {
  return (
    <div className="flex items-center gap-1.5">
      <Button
        variant={panic.blackout ? 'activePanic' : 'secondary'}
        onClick={() => onTogglePanic('blackout')}
        title="Escurecer completamente o telão (F1)"
      >
        <EyeOff className="h-3.5 w-3.5" />
        <span>Blackout</span>
      </Button>

      <Button
        variant={panic.clearText ? 'activeInfo' : 'secondary'}
        onClick={() => onTogglePanic('clearText')}
        title="Ocultar texto do telão mantendo o fundo (F2)"
      >
        <FileText className="h-3.5 w-3.5" />
        <span>Ocultar Letra</span>
      </Button>

      <Button
        variant={panic.freeze ? 'activeInfo' : 'secondary'}
        onClick={() => onTogglePanic('freeze')}
        title="Congelar imagem atual do telão na saída (F3)"
      >
        <Snowflake className="h-3.5 w-3.5" />
        <span>Congelar</span>
      </Button>
    </div>
  )
}
