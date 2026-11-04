/**
 * @module @slices/navigation/ui/components/OutputMonitorPanel
 * @description Coluna 4 do console com preview 16:9 em tempo real, player de áudio litúrgico e galeria de fundos.
 *
 * ## Responsabilidades:
 * - Renderizar o preview fiel do projetor HDMI com proporção 16:9 via ProjectorScreen.
 * - Permitir ao operador abrir a janela `/projector` em uma nova aba/janela externa.
 * - Integrar o reprodutor de áudio Web Audio API (AudioPlayerPanel) para trilhas de oração e recepção.
 * - Integrar a galeria de fundos (MediaGridPanel) para seleção rápida de loops de vídeo e imagens.
 */

import { useState } from 'react'
import { ExternalLink, Music, Film } from 'lucide-react'
import type { ScreenState, BackgroundLayer } from '@lumen/core'
import { ProjectorScreen } from './ProjectorScreen'
import { AudioPlayerPanel, MediaGridPanel } from '@slices/media'

export interface OutputMonitorPanelProps {
  state: ScreenState
  onSelectBackground?: (background: BackgroundLayer) => void
}

/**
 * Painel da Coluna 4 do operador contendo preview de saída, áudio ambiente e mídia.
 *
 * @param props - Propriedades do painel.
 * @param props.state - Estado reativo da projeção FSM.
 * @param props.onSelectBackground - Callback para despacho de novo fundo de projeção.
 * @returns Elemento JSX com preview 16:9 e abas de áudio/mídia.
 */
export function OutputMonitorPanel({ state, onSelectBackground }: OutputMonitorPanelProps) {
  const [activeTab, setActiveTab] = useState<'AUDIO' | 'MEDIA'>('AUDIO')

  const handleOpenProjectorWindow = () => {
    window.open('/projector', 'LumenLiteProjector', 'width=1280,height=720,menubar=no,toolbar=no')
  }

  return (
    <section className="col-span-2 flex flex-col bg-slate-900 p-3 border-l border-white/5 justify-between overflow-y-auto">
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

        <div className="mt-1.5 mb-3 flex items-center justify-between text-[10px] text-slate-500 font-mono">
          <span>1920x1080 @ 60FPS</span>
          <span className="text-emerald-400 font-medium">HDMI ATIVO</span>
        </div>

        {/* Alternador de Abas: Áudio Litúrgico vs Fundos de Mídia */}
        <div className="flex rounded border border-white/10 bg-slate-950/60 p-0.5 mb-2 text-xs">
          <button
            type="button"
            onClick={() => setActiveTab('AUDIO')}
            className={`flex flex-1 items-center justify-center gap-1.5 rounded py-1 text-[11px] font-medium transition ${
              activeTab === 'AUDIO' ? 'bg-sky-500 text-slate-950' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Music className="h-3 w-3" />
            <span>Áudio</span>
          </button>
          <button
            type="button"
            onClick={() => setActiveTab('MEDIA')}
            className={`flex flex-1 items-center justify-center gap-1.5 rounded py-1 text-[11px] font-medium transition ${
              activeTab === 'MEDIA' ? 'bg-sky-500 text-slate-950' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Film className="h-3 w-3" />
            <span>Mídias & Loops</span>
          </button>
        </div>

        {/* Painel Ativo */}
        {activeTab === 'AUDIO' ? (
          <AudioPlayerPanel />
        ) : (
          <MediaGridPanel
            activeBackground={state.background}
            onSelectBackground={(background) => {
              if (onSelectBackground) {
                onSelectBackground(background)
              }
            }}
          />
        )}
      </div>
    </section>
  )
}
