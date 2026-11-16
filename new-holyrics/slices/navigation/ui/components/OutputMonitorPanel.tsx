/**
 * @module @slices/navigation/ui/components/OutputMonitorPanel
 * @description Coluna 4 do console com preview 16:9 fiel, seletor unificado de saída, player de áudio ambiente e galeria de fundos.
 */

import { useState, useEffect } from 'react'
import { ExternalLink, Music, Film, Monitor, Clock } from 'lucide-react'
import type { ScreenState, BackgroundLayer } from '@lumen/core'
import { ProjectorScreen } from './ProjectorScreen'
import { AudioPlayerPanel } from '@slices/media/ui/components/AudioPlayerPanel'
import type { MediaItem } from '@slices/media/contracts/media-contracts'
import { MediaGridPanel } from '@slices/media/ui/components/MediaGridPanel'
import { ResizableSplitPane } from '@ui/components/ui/ResizableSplitPane'

export interface OutputMonitorPanelProps {
  state: ScreenState
  onSelectBackground?: (background: BackgroundLayer) => void
  onProjectMedia?: (item: MediaItem) => void
}

/**
 * Painel da Coluna 4 do operador contendo preview de saída 16:9, áudio ambiente e mídias.
 *
 * @param props - Propriedades do painel.
 * @param props.state - Estado reativo da projeção FSM.
 * @param props.onSelectBackground - Callback para despacho de novo fundo de projeção.
 * @param props.onProjectMedia - Callback para projetar mídia em tela cheia.
 * @returns Elemento JSX com preview 16:9 e abas de áudio/mídia.
 */
function formatElapsedProjection(seconds: number): string {
  const safe = Math.max(0, Math.floor(seconds))
  const hours = Math.floor(safe / 3600)
  const mins = Math.floor((safe % 3600) / 60)
  const secs = safe % 60
  if (hours > 0) {
    return `${String(hours).padStart(2, '0')}:${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
  }
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

export function OutputMonitorPanel({
  state,
  onSelectBackground,
  onProjectMedia,
}: OutputMonitorPanelProps) {
  const [activeTab, setActiveTab] = useState<'AUDIO' | 'MEDIA'>('AUDIO')
  const [nowMs, setNowMs] = useState(() => Date.now())
  const isOutputLive = state.mode !== 'EMPTY' && !state.panic.blackout

  useEffect(() => {
    if (!state.projectedAt || state.mode === 'EMPTY') return
    setNowMs(Date.now())
    const timer = window.setInterval(() => setNowMs(Date.now()), 1000)
    return () => window.clearInterval(timer)
  }, [state.projectedAt, state.mode])

  const elapsedSeconds =
    state.projectedAt && state.mode !== 'EMPTY' ? Math.max(0, Math.floor((nowMs - state.projectedAt) / 1000)) : 0

  const handleOpenProjectorWindow = () => {
    window.open('/projector', 'LumenLiteProjector', 'width=1280,height=720,menubar=no,toolbar=no')
  }

  return (
    <section className="flex h-full flex-col bg-[#11141A] p-3 border-l border-white/5 overflow-hidden">
      <ResizableSplitPane
        direction="vertical"
        initialSizes={[42, 58]}
        minSizes={[25, 25]}
        storageKey="lumen-split-output-vertical"
        className="flex-1 gap-2"
      >
        <div className="flex flex-col gap-2 overflow-y-auto pb-1">
          {/* Header Unificado do Monitor de Saída */}
          <div className="flex items-center justify-between gap-2">
            <div className="flex items-center gap-1.5 min-w-0">
              <span
                className={`h-2 w-2 rounded-full shrink-0 ${
                  state.panic.blackout
                    ? 'bg-rose-500'
                    : isOutputLive
                      ? 'bg-emerald-400 shadow-[0_0_8px_rgba(16,185,129,0.6)]'
                      : 'bg-emerald-500/60'
                }`}
              />
              <Monitor className="h-3.5 w-3.5 text-slate-400 shrink-0" />
              <span className="truncate text-[11px] font-semibold text-slate-200">
                Saída: Telão Principal (1080p)
              </span>
            </div>

            <div className="flex items-center gap-1.5 shrink-0">
              {state.mode !== 'EMPTY' && state.projectedAt && (
                <span
                  title="Tempo decorrido de projeção do item atual"
                  className="flex items-center gap-1 rounded bg-[#141820] px-1.5 py-0.5 tabular-nums font-mono text-[10px] font-semibold text-emerald-300 border border-white/5"
                >
                  <Clock className="h-2.5 w-2.5" />
                  <span>{formatElapsedProjection(elapsedSeconds)}</span>
                </span>
              )}
              <button
                type="button"
                onClick={handleOpenProjectorWindow}
                title="Abrir janela externa de projeção para o segundo monitor ou telão"
                className="flex items-center gap-1 rounded-md bg-[#141820] px-2 py-1 text-[10px] font-medium text-sky-400 hover:bg-[#181C24] hover:text-sky-300 border border-white/5 transition shrink-0"
              >
                <ExternalLink className="h-3 w-3" />
                <span>Abrir Telão</span>
              </button>
            </div>
          </div>

          {/* Canvas do Monitor de Saída (16:9 Real com Escalonamento Proporcional) */}
          <div
            data-testid="output-monitor-canvas"
            className="relative aspect-video w-full rounded-lg bg-black overflow-hidden shadow-2xl shadow-black/90 border border-white/5 transition"
          >
            <ProjectorScreen state={state} isPreview={true} />
          </div>
        </div>

        <div className="flex h-full flex-col gap-2 overflow-hidden pt-1">
          {/* Alternador de Abas: Áudio Ambiente vs Fundos de Mídia */}
          <div className="flex rounded-lg bg-[#0B0D10]/80 p-0.5 border border-white/5 text-xs shrink-0">
            <button
              type="button"
              onClick={() => setActiveTab('AUDIO')}
              className={`flex flex-1 items-center justify-center gap-1.5 py-1 text-[11px] font-medium transition ${
                activeTab === 'AUDIO'
                  ? 'bg-[#181C24] text-sky-400 shadow-sm font-semibold rounded-md'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-white/5 rounded-md'
              }`}
            >
              <Music className="h-3 w-3" />
              <span>Áudio Ambiente</span>
            </button>
            <button
              type="button"
              onClick={() => setActiveTab('MEDIA')}
              className={`flex flex-1 items-center justify-center gap-1.5 py-1 text-[11px] font-medium transition ${
                activeTab === 'MEDIA'
                  ? 'bg-[#181C24] text-sky-400 shadow-sm font-semibold rounded-md'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-white/5 rounded-md'
              }`}
            >
              <Film className="h-3 w-3" />
              <span>Fundos & Loops</span>
            </button>
          </div>

          {/* Painel Ativo */}
          <div className="flex-1 min-h-0 overflow-y-auto">
            {activeTab === 'AUDIO' ? (
              <AudioPlayerPanel />
            ) : (
              <MediaGridPanel
                filterType="VISUAL"
                activeBackground={state.background}
                onProjectMedia={onProjectMedia}
                onSelectBackground={(background) => {
                  if (onSelectBackground) {
                    onSelectBackground(background)
                  }
                }}
              />
            )}
          </div>
        </div>
      </ResizableSplitPane>
    </section>
  )
}
