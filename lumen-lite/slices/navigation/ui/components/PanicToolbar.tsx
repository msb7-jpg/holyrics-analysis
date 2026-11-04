/**
 * @module @slices/navigation/ui/components/PanicToolbar
 * @description Barra de botões de pânico e cortes rápidos de emergência do telão (F1, F2, F3).
 */

import { EyeOff, FileText, Snowflake } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import type { PanicMask } from '@lumen/core'

export interface PanicToolbarProps {
  panic: PanicMask
  onTogglePanic: (target: 'blackout' | 'clearText' | 'freeze') => void
}

/**
 * Componente com as três ações de pânico fundamentais do operador.
 *
 * @param props - Propriedades da barra.
 * @returns Elemento JSX com botões estilizados.
 */
export function PanicToolbar({ panic, onTogglePanic }: PanicToolbarProps) {
  return (
    <div className="flex items-center gap-2">
      <Button
        variant={panic.blackout ? 'activePanic' : 'secondary'}
        onClick={() => onTogglePanic('blackout')}
        title="F1: Alternar Blackout (Telão Preto)"
      >
        <EyeOff className="h-3.5 w-3.5" />
        <span>Blackout</span>
      </Button>

      <Button
        variant={panic.clearText ? 'activeInfo' : 'secondary'}
        onClick={() => onTogglePanic('clearText')}
        title="F2: Ocultar Letra preservando o fundo em vídeo"
      >
        <FileText className="h-3.5 w-3.5" />
        <span>Ocultar Letra</span>
      </Button>

      <Button
        variant={panic.freeze ? 'activeInfo' : 'secondary'}
        onClick={() => onTogglePanic('freeze')}
        title="F3: Congelar Projeção (Freeze)"
      >
        <Snowflake className="h-3.5 w-3.5" />
        <span>Congelar</span>
      </Button>
    </div>
  )
}
