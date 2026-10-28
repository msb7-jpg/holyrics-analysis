import React, { useState } from 'react'
import {
  EyeOff,
  FileText,
  Snowflake,
  Search,
  Music,
  BookOpen,
  Calendar,
  Volume2,
  ChevronRight,
  Monitor,
  Radio,
} from 'lucide-react'
import type { ScreenState, ActionCommand } from '@lumen/core'

interface OperatorViewProps {
  state: ScreenState
  connected: boolean
  dispatch: (cmd: ActionCommand) => void
}

export function OperatorView({ state, connected, dispatch }: OperatorViewProps) {
  const [searchQuery, setSearchQuery] = useState('')

  // Slides mockados para visualização sóbria das estrofes
  const mockSlides = [
    { id: '1', number: 1, text: 'Te amo, ó Deus\nTua graça nunca falha\nTodos os dias\nEu estou em Tuas mãos' },
    { id: '2', number: 2, text: 'Desde o amanhecer\nAté o pôr do sol\nEu cantarei\nDa bondade de Deus' },
    { id: '3', number: 3, text: 'És fiel em todo tempo\nEm todo tempo Tu és tão, tão bom\nCom tudo o que sou\nEu cantarei da bondade de Deus' },
  ]

  return (
    <div className="flex h-screen w-screen flex-col bg-slate-900 text-slate-100 select-none">
      {/* TopBar Sóbria */}
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

        {/* Barra de Pânico / Ações Rápidas (Discretas, sem semáforo) */}
        <div className="flex items-center gap-2">
          <button
            onClick={() => dispatch({ type: 'TOGGLE_PANIC', target: 'blackout' })}
            className={`flex items-center gap-1.5 rounded px-2.5 py-1 text-xs font-medium transition ${
              state.panic.blackout
                ? 'bg-red-500/20 text-red-300 border border-red-500/50'
                : 'bg-slate-800 text-slate-300 hover:bg-slate-700 border border-white/10'
            }`}
          >
            <EyeOff className="h-3.5 w-3.5" />
            <span>Blackout</span>
          </button>

          <button
            onClick={() => dispatch({ type: 'TOGGLE_PANIC', target: 'clearText' })}
            className={`flex items-center gap-1.5 rounded px-2.5 py-1 text-xs font-medium transition ${
              state.panic.clearText
                ? 'bg-sky-500/20 text-sky-300 border border-sky-500/50'
                : 'bg-slate-800 text-slate-300 hover:bg-slate-700 border border-white/10'
            }`}
          >
            <FileText className="h-3.5 w-3.5" />
            <span>Ocultar Letra</span>
          </button>

          <button
            onClick={() => dispatch({ type: 'TOGGLE_PANIC', target: 'freeze' })}
            className={`flex items-center gap-1.5 rounded px-2.5 py-1 text-xs font-medium transition ${
              state.panic.freeze
                ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/50'
                : 'bg-slate-800 text-slate-300 hover:bg-slate-700 border border-white/10'
            }`}
          >
            <Snowflake className="h-3.5 w-3.5" />
            <span>Congelar</span>
          </button>
        </div>
      </header>

      {/* Grid Principal: 4 Colunas Redimensionáveis */}
      <main className="grid flex-1 grid-cols-12 gap-px bg-white/5 overflow-hidden">
        {/* Coluna 1: Roteiro Litúrgico (22%) */}
        <section className="col-span-3 flex flex-col bg-slate-900 p-3 overflow-y-auto">
          <div className="mb-3 flex items-center justify-between text-xs font-semibold uppercase tracking-wider text-slate-400">
            <span className="flex items-center gap-1.5">
              <Calendar className="h-3.5 w-3.5" />
              Roteiro do Culto
            </span>
            <span className="text-[10px] text-slate-500 font-mono">culto.json</span>
          </div>
          <div className="flex flex-col gap-1.5 text-xs">
            <div className="rounded border border-white/10 bg-slate-800/80 p-2.5 hover:bg-slate-800 transition">
              <span className="text-[10px] uppercase text-slate-400 font-mono">Mídia</span>
              <p className="font-medium text-slate-200">1. Contagem Regressiva 5min</p>
            </div>
            <div className="rounded border border-sky-500/40 bg-slate-800 p-2.5 shadow-sm">
              <div className="flex items-center justify-between">
                <span className="text-[10px] uppercase text-sky-400 font-mono">Louvor em Andamento</span>
                <span className="rounded bg-sky-500/20 px-1 text-[9px] text-sky-300 font-mono">AO VIVO</span>
              </div>
              <p className="font-semibold text-white">2. Bondade de Deus</p>
              <p className="text-[11px] text-slate-400">Isaías Saad • 7 estrofes</p>
            </div>
            <div className="rounded border border-white/10 bg-slate-800/80 p-2.5 hover:bg-slate-800 transition">
              <span className="text-[10px] uppercase text-slate-400 font-mono">Bíblia</span>
              <p className="font-medium text-slate-200">3. Leitura Litúrgica (João 3:16-18)</p>
            </div>
          </div>
        </section>

        {/* Coluna 2: Acervo & Pesquisa Instantânea FTS5 (25%) */}
        <section className="col-span-3 flex flex-col bg-slate-900 p-3 border-l border-white/5 overflow-hidden">
          <div className="relative mb-3">
            <Search className="absolute left-2.5 top-2.5 h-3.5 w-3.5 text-slate-400" />
            <input
              type="text"
              placeholder="Buscar hino, versículo (jo 3 16)..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full rounded bg-slate-800/90 pl-8 pr-3 py-1.5 text-xs text-slate-100 placeholder:text-slate-500 border border-white/10 focus:border-sky-500/50 focus:outline-none"
            />
          </div>
          <div className="flex flex-1 flex-col gap-1 overflow-y-auto text-xs">
            <div className="flex items-center gap-2 rounded p-2 hover:bg-slate-800 text-slate-300">
              <Music className="h-3.5 w-3.5 text-slate-400" />
              <span>Porque Ele Vive</span>
            </div>
            <div className="flex items-center gap-2 rounded p-2 hover:bg-slate-800 text-slate-300">
              <Music className="h-3.5 w-3.5 text-slate-400" />
              <span>Graça Sobrenatural</span>
            </div>
            <div className="flex items-center gap-2 rounded p-2 hover:bg-slate-800 text-slate-300">
              <BookOpen className="h-3.5 w-3.5 text-slate-400" />
              <span>Salmos 23 (O Senhor é o meu pastor)</span>
            </div>
          </div>
        </section>

        {/* Coluna 3: Estrofes / Slides da Música (Leitura Limpa sem semáforo) (35%) */}
        <section className="col-span-4 flex flex-col bg-slate-900 p-3 border-l border-white/5 overflow-y-auto">
          <div className="mb-3 flex items-center justify-between text-xs font-semibold text-slate-400">
            <span>BONDADE DE DEUS</span>
            <span className="text-[10px] text-slate-500 font-mono">3 estrofes</span>
          </div>

          <div className="flex flex-col gap-2">
            {mockSlides.map((slide, idx) => {
              const isCurrent = state.foreground.slideIndex === idx
              return (
                <div
                  key={slide.id}
                  onClick={() => dispatch({ type: 'NAV_GOTO', slideIndex: idx })}
                  className={`group relative flex cursor-pointer rounded border p-3 transition ${
                    isCurrent
                      ? 'border-sky-500/50 bg-slate-800/90 shadow-sm'
                      : 'border-white/5 bg-slate-800/40 hover:bg-slate-800/70'
                  }`}
                >
                  {/* Linha discreta de 2px à esquerda para slide no ar */}
                  {isCurrent && (
                    <div className="absolute left-0 top-0 bottom-0 w-1 rounded-l bg-sky-400" />
                  )}

                  {/* Numeração sóbria (1, 2, 3...) ao invés de labels berrantes */}
                  <span className="mr-3 font-mono text-xs text-slate-500 group-hover:text-slate-300">
                    {slide.number}
                  </span>

                  <div className="flex-1">
                    <pre className="font-sans text-xs leading-relaxed text-slate-200 whitespace-pre-wrap">
                      {slide.text}
                    </pre>
                  </div>

                  {isCurrent && (
                    <span className="self-start text-[10px] font-mono text-sky-400 font-medium">
                      NO AR
                    </span>
                  )}
                </div>
              )
            })}
          </div>
        </section>

        {/* Coluna 4: Monitor do Telão & Áudio Ambiente (18%) */}
        <section className="col-span-2 flex flex-col bg-slate-900 p-3 border-l border-white/5 justify-between">
          <div>
            <div className="mb-2 text-[10px] uppercase font-bold tracking-wider text-slate-400">
              Monitor de Saída
            </div>

            {/* Preview 16:9 */}
            <div className="relative aspect-video w-full rounded border border-white/10 bg-black flex items-center justify-center p-2 text-center overflow-hidden">
              {state.panic.blackout ? (
                <span className="text-[10px] font-mono text-red-400 font-bold uppercase tracking-widest">
                  BLACKOUT ATIVO
                </span>
              ) : state.panic.clearText ? (
                <span className="text-[10px] font-mono text-slate-500">Texto Oculto (Loop Fundo)</span>
              ) : (
                <p className="text-[10px] font-semibold text-white leading-tight">
                  {mockSlides[state.foreground.slideIndex]?.text.split('\n')[0] || 'Pronto'}
                </p>
              )}
            </div>

            <div className="mt-2 flex items-center justify-between text-[10px] text-slate-500 font-mono">
              <span>1920x1080 @ 60FPS</span>
              <span className="text-emerald-400">HDMI ATIVO</span>
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
      </main>

      {/* Barra de Status Inferior */}
      <footer className="flex h-7 items-center justify-between border-t border-white/10 bg-slate-900 px-3 text-[11px] text-slate-400 font-mono">
        <div className="flex items-center gap-4">
          <span>⌨️ [Espaço]: Próximo</span>
          <span>[Backspace]: Retornar</span>
          <span>[F1]: Blackout</span>
          <span>[F2]: Ocultar Texto</span>
        </div>
        <div className="flex items-center gap-3">
          <span>Versão FSM: {state.version}</span>
          <span className="text-emerald-400">Latência IPC: &lt;1ms</span>
        </div>
      </footer>
    </div>
  )
}
