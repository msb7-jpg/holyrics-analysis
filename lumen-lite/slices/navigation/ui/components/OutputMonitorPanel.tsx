/**
 * @module @slices/navigation/ui/components/OutputMonitorPanel
 * @description Coluna 4 com monitor de saída 16:9 em tempo real e controle de áudio ambiente.
 *
 * ## Responsabilidades:
 * - Renderizar o preview fiel do projetor HDMI com proporção 16:9 via ProjectorScreen.
 * - Permitir ao operador abrir a janela `/projector` em uma nova aba/janela externa.
 * - Exibir indicadores de status HDMI, taxa de quadros e áudio ambiente.
 */

import { ExternalLink, Volume2 } from 'lucide-react'
import type { ScreenState } from '@lumen/core'
import { ProjectorScreen } from './ProjectorScreen'

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
  const handleOpenProjectorWindow = () => {
    window.open('/projector', 'LumenLiteProjector', 'width=1280,height=720,menubar=no,toolbar=no')
  }

  return (
    <section className="col-span-2 flex flex-col bg-slate-900 p-3 border-l border-white/5 justify-between">
      <div>
        <div className="mb-2 flex items-center justify-between text-[10px] uppercase font-bold tracking-wider text-slate-400">
          <span>Monitor de Saída</span>
          <button
            type="button"
            onClick={handleOpenProjectorWindow}
            title="Abrir janela externa do projetor HDMI"
            className="flex items-center gap-1 rounded bg-slate-800 px-1.5 py-0.5 text-[9px] font-mono normal-case text-sky-400 hover:bg-slate-700 hover:text-sky-300 transition"
          >
            <ExternalLink className="h-2.5 w-2.5" />
            Abrir Telão
          </button>
        </div>

        {/* Preview 16:9 Fiel */}
        <div className="relative aspect-video w-full rounded border border-white/10 bg-black overflow-hidden shadow-inner">
          <ProjectorScreen state={state} isPreview={true} />
        </div>

        <div className="mt-2 flex items-center justify-between text-[10px] text-slate-500 font-mono">
          <span>1920x1080 @ 60FPS</span>
          <span className="text-emerald-400 font-medium">HDMI ATIVO</span>
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
