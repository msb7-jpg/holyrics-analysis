/**
 * @module @slices/navigation/ui/components/OperatorTopBar
 * @description Barra superior do console do operador com status de hardware, conexão e atalho do Spotlight.
 */

import { Monitor, Radio, Search, Cpu, Keyboard } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { Kbd } from '@ui/components/ui/kbd'
import { PanicToolbar } from './PanicToolbar'
import type { PanicMask } from '@lumen/core'

export interface OperatorTopBarProps {
  connected: boolean
  panic: PanicMask
  onOpenSpotlight: () => void
  onOpenAutomation?: () => void
  onOpenKeymap?: () => void
  onTogglePanic: (target: 'blackout' | 'clearText' | 'freeze') => void
}

/**
 * Cabeçalho principal com telemetria e controles rápidos.
 *
 * @param props - Propriedades do cabeçalho.
 * @param props.connected - Se o WebSocket está conectado.
 * @param props.panic - Máscara de pânico ativa.
 * @param props.onOpenSpotlight - Abertura do Spotlight.
 * @param props.onOpenAutomation - Abertura do editor visual DAG de automações.
 * @param props.onTogglePanic - Disparo de máscaras de pânico.
 * @returns Elemento JSX com a TopBar estilizada.
 */
export function OperatorTopBar({
  connected,
  panic,
  onOpenSpotlight,
  onOpenAutomation,
  onOpenKeymap,
  onTogglePanic,
}: OperatorTopBarProps) {
  return (
    <header className="flex h-12 items-center justify-between border-b border-white/10 px-4 bg-slate-900/90 backdrop-blur">
      <div className="flex items-center gap-4 text-xs font-medium">
        <div className="flex items-center gap-2">
          <span className="h-2 w-2 rounded-full bg-sky-400"></span>
          <span className="font-semibold text-white tracking-wide">LUMENLITE</span>
        </div>
        <div className="flex items-center gap-1.5 text-slate-400">
          <Monitor className="h-3.5 w-3.5" />
          <span>HDMI 1: 1080p@60Hz</span>
        </div>
        <div className="flex items-center gap-1.5 text-slate-400">
          <Radio className="h-3.5 w-3.5" />
          <span className={connected ? 'text-emerald-400' : 'text-amber-400'}>
            {connected ? 'Daemon Conectado' : 'Conectando ao Daemon...'}
          </span>
        </div>
      </div>

      <div className="flex items-center gap-2">
        {onOpenKeymap && (
          <Button variant="ghost" onClick={onOpenKeymap} title="Atalhos de Teclado (⌘/)">
            <Keyboard className="h-3.5 w-3.5 text-slate-400" />
            <span>Atalhos</span>
          </Button>
        )}

        {onOpenAutomation && (
          <Button variant="ghost" onClick={onOpenAutomation} title="Configurar Automações (OBS, DMX, Webhooks)">
            <Cpu className="h-3.5 w-3.5 text-sky-400" />
            <span>Automações</span>
          </Button>
        )}

        <Button variant="secondary" onClick={onOpenSpotlight}>
          <Search className="h-3.5 w-3.5 text-sky-400" />
          <span>Spotlight</span>
          <Kbd>⌘K</Kbd>
        </Button>

        <PanicToolbar panic={panic} onTogglePanic={onTogglePanic} />
      </div>
    </header>
  )
}

