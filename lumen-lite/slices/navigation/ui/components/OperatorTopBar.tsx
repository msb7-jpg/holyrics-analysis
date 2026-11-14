/**
 * @module @slices/navigation/ui/components/OperatorTopBar
 * @description Barra superior do console do operador com status de hardware, multi-monitor, remotos QR Code, palco, PPTX e pânico.
 */

import {
  Monitor,
  Radio,
  Search,
  Cpu,
  Keyboard,
  QrCode,
  Timer,
  Presentation,
  BookOpen,
} from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { Kbd } from '@ui/components/ui/kbd'
import { PanicToolbar } from './PanicToolbar'
import { useMultiMonitor } from '../hooks/useMultiMonitor'
import type { PanicMask } from '@lumen/core'

export interface OperatorTopBarProps {
  connected: boolean
  panic: PanicMask
  onOpenSpotlight: () => void
  onOpenAutomation?: () => void
  onOpenKeymap?: () => void
  onOpenStageControl?: () => void
  onOpenRemotePairing?: () => void
  onOpenPptxDeck?: () => void
  onOpenBibleDownloads?: () => void
  onTogglePanic: (target: 'blackout' | 'clearText' | 'freeze') => void
}

/**
 * Cabeçalho principal com telemetria, gerenciamento multi-monitor (RF-01) e controles rápidos.
 *
 * @param props - Propriedades do cabeçalho.
 * @param props.connected - Se o WebSocket está conectado.
 * @param props.panic - Máscara de pânico ativa.
 * @param props.onOpenSpotlight - Abertura do Spotlight.
 * @param props.onOpenAutomation - Abertura do editor visual DAG de automações.
 * @param props.onOpenKeymap - Abertura do modal de atalhos.
 * @param props.onOpenStageControl - Abertura do modal de Palco, Timers e Lower Thirds.
 * @param props.onOpenRemotePairing - Abertura do modal de QR Code Mobile.
 * @param props.onOpenPptxDeck - Abertura do gerenciador de apresentações PPTX/PDF.
 * @param props.onOpenBibleDownloads - Abertura do gerenciador de traduções bíblicas.
 * @param props.onTogglePanic - Disparo de máscaras de pânico.
 * @returns Elemento JSX com a TopBar estilizada.
 */
export function OperatorTopBar({
  connected,
  panic,
  onOpenSpotlight,
  onOpenAutomation,
  onOpenKeymap,
  onOpenStageControl,
  onOpenRemotePairing,
  onOpenPptxDeck,
  onOpenBibleDownloads,
  onTogglePanic,
}: OperatorTopBarProps) {
  const { openProjectorWindow } = useMultiMonitor()

  return (
    <header className="flex h-12 items-center justify-between border-b border-white/10 px-4 bg-slate-900/90 backdrop-blur">
      <div className="flex items-center gap-4 text-xs font-medium">
        <div className="flex items-center gap-2">
          <span className="h-2 w-2 rounded-full bg-sky-400"></span>
          <span className="font-semibold text-white tracking-wide">LUMENLITE</span>
        </div>
        <Button
          variant="ghost"
          size="sm"
          onClick={openProjectorWindow}
          title="Abrir Projetor HDMI no 2º Monitor (RF-01)"
        >
          <Monitor className="h-3.5 w-3.5 text-sky-400" />
          <span>HDMI 2: 1080p@60Hz</span>
        </Button>
        <div className="flex items-center gap-1.5 text-slate-400">
          <Radio className="h-3.5 w-3.5" />
          <span className={connected ? 'text-emerald-400' : 'text-amber-400'}>
            {connected ? 'Daemon Conectado' : 'Conectando...'}
          </span>
        </div>
      </div>

      <div className="flex items-center gap-1.5">
        {onOpenPptxDeck && (
          <Button variant="ghost" size="sm" onClick={onOpenPptxDeck} title="Apresentações PPTX e PDF (RF-21)">
            <Presentation className="h-3.5 w-3.5 text-sky-400" />
            <span>PPTX/PDF</span>
          </Button>
        )}

        {onOpenBibleDownloads && (
          <Button variant="ghost" size="sm" onClick={onOpenBibleDownloads} title="Traduções Bíblicas Offline (RF-17)">
            <BookOpen className="h-3.5 w-3.5 text-slate-400" />
            <span>Bíblias</span>
          </Button>
        )}

        {onOpenStageControl && (
          <Button variant="ghost" size="sm" onClick={onOpenStageControl} title="Palco, Cronômetros e Lower Thirds (RF-02, RF-24, RF-25)">
            <Timer className="h-3.5 w-3.5 text-amber-400" />
            <span>Palco & Alertas</span>
          </Button>
        )}

        {onOpenRemotePairing && (
          <Button variant="ghost" size="sm" onClick={onOpenRemotePairing} title="Parear Celular via QR Code (RF-26A, RF-26B)">
            <QrCode className="h-3.5 w-3.5 text-sky-400" />
            <span>QR Remote</span>
          </Button>
        )}

        {onOpenKeymap && (
          <Button variant="ghost" size="sm" onClick={onOpenKeymap} title="Atalhos de Teclado (⌘/)">
            <Keyboard className="h-3.5 w-3.5 text-slate-400" />
            <span>Atalhos</span>
          </Button>
        )}

        {onOpenAutomation && (
          <Button variant="ghost" size="sm" onClick={onOpenAutomation} title="Configurar Automações (OBS, DMX, Webhooks)">
            <Cpu className="h-3.5 w-3.5 text-sky-400" />
            <span>Automações</span>
          </Button>
        )}

        <Button variant="secondary" size="sm" onClick={onOpenSpotlight}>
          <Search className="h-3.5 w-3.5 text-sky-400" />
          <span>Spotlight</span>
          <Kbd>⌘K</Kbd>
        </Button>

        <PanicToolbar panic={panic} onTogglePanic={onTogglePanic} />
      </div>
    </header>
  )
}
