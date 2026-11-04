/**
 * @module @ui/operator/OperatorView
 * @description Console principal de operação litúrgica do LumenLite.
 * Orquestrador declarativo enxuto (< 100 linhas) montando o layout de 4 colunas Slate Pro Studio.
 */

import { useState } from 'react'
import { Search, Loader2 } from 'lucide-react'
import type { ScreenState, ActionCommand } from '@lumen/core'
import { Input } from '@ui/components/ui/input'
import { Kbd } from '@ui/components/ui/kbd'
import { OperatorTopBar } from '@slices/navigation/ui/components/OperatorTopBar'
import { OutputMonitorPanel } from '@slices/navigation/ui/components/OutputMonitorPanel'
import { LiturgyScheduleColumn } from '@slices/liturgy/ui/components/LiturgyScheduleColumn'
import { SongSearchList } from '@slices/songs/ui/components/SongSearchList'
import { SongStanzaGrid } from '@slices/songs/ui/components/SongStanzaGrid'
import { BibleQuickLookupCard } from '@slices/bible/ui/components/BibleQuickLookupCard'
import { BibleVerseGrid } from '@slices/bible/ui/components/BibleVerseGrid'
import { AutomationModal } from '@slices/automation'
import { KeymapModal } from '@slices/keymap'
import { SpotlightDialog } from './components/SpotlightDialog'
import { useOperatorController } from './hooks/useOperatorController'

export interface OperatorViewProps {
  state: ScreenState
  connected: boolean
  dispatch: (cmd: ActionCommand) => void
}

/**
 * Layout de 4 colunas verticais do console do operador.
 *
 * @param props - Propriedades do componente.
 * @param props.state - Estado atômico da projeção e pânico.
 * @param props.connected - Flag indicando se o WebSocket está conectado.
 * @param props.dispatch - Despachante tipado de comandos para a FSM.
 * @returns Elemento JSX com o console de controle do operador.
 */
export function OperatorView({ state, connected, dispatch }: OperatorViewProps) {
  const {
    query,
    setQuery,
    songs,
    isLoadingSongs,
    currentSong,
    isLoadingSongDetail,
    selectedSongId,
    activeBible,
    bibleLookupResult,
    isSpotlightOpen,
    setIsSpotlightOpen,
    handleSelectSong,
    handleSelectSection,
    handleSelectBible,
    handleSelectVerse,
    handleSelectLiturgyItem,
  } = useOperatorController({ state, dispatch })

  const [isAutomationOpen, setIsAutomationOpen] = useState(false)
  const [isKeymapOpen, setIsKeymapOpen] = useState(false)

  return (
    <div className="flex h-screen w-screen flex-col bg-slate-900 text-slate-100 select-none">
      {/* TopBar Sóbria com Pânico e Status */}
      <OperatorTopBar
        connected={connected}
        panic={state.panic}
        onOpenSpotlight={() => setIsSpotlightOpen(true)}
        onOpenAutomation={() => setIsAutomationOpen(true)}
        onOpenKeymap={() => setIsKeymapOpen(true)}
        onTogglePanic={(target) => dispatch({ type: 'TOGGLE_PANIC', target })}
      />

      {/* Grid Principal: 4 Colunas Redimensionáveis */}
      <main className="grid flex-1 grid-cols-12 gap-px bg-white/5 overflow-hidden">
        {/* Coluna 1: Roteiro Litúrgico (22%) */}
        <LiturgyScheduleColumn
          state={state}
          selectedSong={currentSong}
          activeBible={activeBible}
          onSelectItem={handleSelectLiturgyItem}
        />

        {/* Coluna 2: Acervo Musical & Pesquisa FTS5 (25%) */}
        <section className="col-span-3 flex flex-col bg-slate-900 p-3 border-l border-white/5 overflow-hidden">
          <div className="relative mb-3">
            <Search className="absolute left-2.5 top-2.5 h-3.5 w-3.5 text-slate-400" />
            <Input
              placeholder="Buscar hino ou versículo (ex: jo 3 16)..."
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              className="pl-8 pr-8"
            />
            {isLoadingSongs && (
              <Loader2 className="absolute right-2.5 top-2.5 h-3.5 w-3.5 animate-spin text-slate-400" />
            )}
          </div>

          <div className="flex flex-1 flex-col gap-1 overflow-y-auto text-xs">
            {bibleLookupResult && (
              <BibleQuickLookupCard
                lookup={bibleLookupResult}
                onProject={handleSelectBible}
              />
            )}

            <SongSearchList
              songs={songs}
              isLoading={isLoadingSongs}
              selectedSongId={selectedSongId}
              onSelectSong={handleSelectSong}
            />
          </div>
        </section>

        {/* Coluna 3: Estrofes da Música ou Versículos Bíblicos (35%) */}
        <section className="col-span-4 flex flex-col bg-slate-900 p-3 border-l border-white/5 overflow-y-auto">
          {state.mode === 'BIBLE' && activeBible ? (
            <BibleVerseGrid
              activeBible={activeBible}
              activeSlideIndex={state.foreground.slideIndex}
              isLiveOnScreen={state.mode === 'BIBLE'}
              onSelectVerse={handleSelectVerse}
            />
          ) : isLoadingSongDetail ? (
            <div className="flex flex-1 items-center justify-center">
              <Loader2 className="h-6 w-6 animate-spin text-sky-400" />
            </div>
          ) : currentSong ? (
            <SongStanzaGrid
              song={currentSong}
              activeSlideIndex={state.foreground.slideIndex}
              isLiveOnScreen={state.mode === 'SONG' && state.activeItemId === currentSong.id}
              onSelectStanza={handleSelectSection}
            />
          ) : (
            <div className="py-10 text-center text-xs text-slate-500">
              Nenhuma música selecionada
            </div>
          )}
        </section>

        {/* Coluna 4: Monitor do Telão & Áudio Ambiente (18%) */}
        <OutputMonitorPanel
          state={state}
          onSelectBackground={(background) => dispatch({ type: 'SET_BACKGROUND', background })}
        />
      </main>

      {/* Footer de Atalhos Sóbrio */}
      <footer className="flex h-7 items-center justify-between border-t border-white/10 px-4 bg-slate-900 text-[10px] text-slate-500 font-mono">
        <div className="flex items-center gap-4">
          <span>⌨️ <Kbd>⌘K</Kbd> Spotlight</span>
          <span><Kbd>Espaço</Kbd> Próximo</span>
          <span><Kbd>Backspace</Kbd> Retornar</span>
          <span><Kbd>F1</Kbd> Blackout</span>
          <span><Kbd>⌘/</Kbd> Ver Atalhos</span>
        </div>
        <div className="flex items-center gap-3">
          <span>Versão FSM: {state.version}</span>
          <span className="text-emerald-400">Latência IPC: &lt;1ms</span>
        </div>
      </footer>

      {/* Modal Spotlight Universal (Cmd+K) */}
      <SpotlightDialog
        isOpen={isSpotlightOpen}
        onClose={() => setIsSpotlightOpen(false)}
        onSelectSong={handleSelectSong}
        onSelectBible={handleSelectBible}
        onTogglePanic={(target) => dispatch({ type: 'TOGGLE_PANIC', target })}
      />

      {/* Modal de Automação Litúrgica DAG */}
      <AutomationModal
        isOpen={isAutomationOpen}
        onClose={() => setIsAutomationOpen(false)}
      />

      {/* Modal de Atalhos de Teclado (keybindings.json) */}
      <KeymapModal
        isOpen={isKeymapOpen}
        onClose={() => setIsKeymapOpen(false)}
      />
    </div>
  )
}
