/**
 * @module @ui/operator/OperatorView
 * @description Console principal de operação litúrgica do LumenLite (4 colunas Slate Pro Studio).
 *
 * ## Responsabilidades:
 * - Orquestrar o fluxo Preparar (Esquerda) -> Operar (Centro) -> Monitorar (Direita) com proporção 20% | 22% | 33% | 25%.
 * - Aplicar hierarquia de superfícies (#090D14 -> #0F1622 -> #131C2B) e semântica Preview (Azul) vs Ao Vivo (Esmeralda).
 * - Coordenar modais de ferramentas secundárias e diagnóstico sem vazamentos técnicos na interface principal.
 */

import { useState } from 'react'
import { Search, Loader2, History, Upload } from 'lucide-react'
import type { ScreenState, ActionCommand } from '@lumen/core'
import {
  INITIAL_LIVE_STAGE_STATE,
  type LiveStageState,
} from '@slices/stage/contracts/stage-contracts'
import { StageControlModal } from '@slices/stage/ui/components/StageControlModal'
import { RemotePairingModal } from '@slices/remote/ui/components/RemotePairingModal'
import { PptxDeckModal } from '@slices/pptx/ui/components/PptxDeckModal'
import { SongHistoryModal } from '@slices/songs/ui/components/SongHistoryModal'
import { SongImporterModal } from '@slices/songs/ui/components/SongImporterModal'
import { BibleDownloadModal } from '@slices/bible/ui/components/BibleDownloadModal'
import { Input } from '@ui/components/ui/input'
import { Button } from '@ui/components/ui/button'
import { OperatorTopBar } from '@slices/navigation/ui/components/OperatorTopBar'
import { OutputMonitorPanel } from '@slices/navigation/ui/components/OutputMonitorPanel'
import { DiagnosticsModal } from '@slices/navigation/ui/components/DiagnosticsModal'
import { LiturgyScheduleColumn } from '@slices/liturgy/ui/components/LiturgyScheduleColumn'
import {
  useCurrentLiturgyQuery,
  useUpdateLiturgyTitleMutation,
} from '@slices/liturgy/ui/queries/liturgy-queries'
import type { LiturgyItem } from '@slices/liturgy/contracts/liturgy-schema'
import { SongSearchList } from '@slices/songs/ui/components/SongSearchList'
import { SongStanzaGrid } from '@slices/songs/ui/components/SongStanzaGrid'
import { BibleQuickLookupCard } from '@slices/bible/ui/components/BibleQuickLookupCard'
import { BibleVerseGrid } from '@slices/bible/ui/components/BibleVerseGrid'
import { AutomationModal } from '@slices/automation/ui/components/AutomationModal'
import { KeymapModal } from '@slices/keymap/ui/components/KeymapModal'
import { SpotlightDialog } from './components/SpotlightDialog'
import { useOperatorController } from './hooks/useOperatorController'

export interface OperatorViewProps {
  state: ScreenState
  stageState?: LiveStageState
  connected: boolean
  dispatch: (cmd: ActionCommand) => void
}

/**
 * Layout de 4 colunas verticais do console do operador.
 *
 * @param props - Propriedades do componente.
 * @param props.state - Estado atômico da projeção e pânico.
 * @param props.stageState - Estado sincronizado de alertas e cronômetros de palco.
 * @param props.connected - Flag indicando se o WebSocket está conectado.
 * @param props.dispatch - Despachante tipado de comandos para a FSM.
 * @returns Elemento JSX com o console de controle do operador.
 */
export function OperatorView({
  state,
  stageState = INITIAL_LIVE_STAGE_STATE,
  connected,
  dispatch,
}: OperatorViewProps) {
  const { data: currentLiturgy } = useCurrentLiturgyQuery()
  const { mutate: updateLiturgyTitle } = useUpdateLiturgyTitleMutation()

  const {
    query,
    setQuery,
    songs,
    isLoadingSongs,
    currentSong,
    isLoadingSongDetail,
    selectedSongId,
    activeBible,
    dualBibleVersion,
    bibleLookupResult,
    isSpotlightOpen,
    setIsSpotlightOpen,
    handleSelectSong,
    handleSelectSection,
    handleSelectBible,
    handleChangeDualVersion,
    handleSelectVerse,
    handleSelectLiturgyItem,
  } = useOperatorController({ state, dispatch })

  const [isAutomationOpen, setIsAutomationOpen] = useState(false)
  const [isKeymapOpen, setIsKeymapOpen] = useState(false)
  const [isStageControlOpen, setIsStageControlOpen] = useState(false)
  const [isRemotePairingOpen, setIsRemotePairingOpen] = useState(false)
  const [isPptxOpen, setIsPptxOpen] = useState(false)
  const [isBibleDownloadsOpen, setIsBibleDownloadsOpen] = useState(false)
  const [isSongHistoryOpen, setIsSongHistoryOpen] = useState(false)
  const [isSongImporterOpen, setIsSongImporterOpen] = useState(false)
  const [isDiagnosticsOpen, setIsDiagnosticsOpen] = useState(false)
  const [selectedLiturgyItemId, setSelectedLiturgyItemId] = useState<string | null>(null)

  const handleSelectItemFromRoteiro = (item: LiturgyItem) => {
    setSelectedLiturgyItemId(item.id)
    if (item.type === 'MEDIA') {
      dispatch({
        type: 'SET_BACKGROUND',
        background: {
          type: item.mediaType === 'VIDEO' ? 'video' : 'image',
          source: item.mediaSrc,
          loop: item.mediaType === 'VIDEO',
          muted: true,
        },
      })
      return
    }
    if (item.type === 'PPTX') {
      setIsPptxOpen(true)
      return
    }
    handleSelectLiturgyItem(item)
  }

  return (
    <div className="flex h-screen w-screen flex-col bg-[#090D14] text-slate-100 select-none overflow-hidden">
      {/* 1. Barra Superior Redesenha (Esquerda: Evento Editável | Centro: Busca Global | Direita: Saída + Ferramentas) */}
      <OperatorTopBar
        isConnected={connected}
        panic={state.panic}
        eventTitle={currentLiturgy?.title || 'Culto de Celebração'}
        onChangeEventTitle={(nextTitle) => updateLiturgyTitle(nextTitle)}
        onTogglePanic={(target) => dispatch({ type: 'TOGGLE_PANIC', target })}
        onOpenSpotlight={() => setIsSpotlightOpen(true)}
        onOpenAutomation={() => setIsAutomationOpen(true)}
        onOpenKeymap={() => setIsKeymapOpen(true)}
        onOpenStageControl={() => setIsStageControlOpen(true)}
        onOpenRemotePairing={() => setIsRemotePairingOpen(true)}
        onOpenPptxModal={() => setIsPptxOpen(true)}
        onOpenBibleDownloads={() => setIsBibleDownloadsOpen(true)}
        onOpenDiagnostics={() => setIsDiagnosticsOpen(true)}
      />

      {/* 2. Grid Principal de 4 Colunas (Proporção: 20% | 22% | 33% | 25%) */}
      <main className="grid flex-1 grid-cols-1 lg:grid-cols-[20fr_22fr_33fr_25fr] overflow-hidden">
        {/* Coluna 1 (20%): Roteiro do Culto & Abas de Mídia/Arquivos (Esquerda) */}
        <LiturgyScheduleColumn
          state={state}
          selectedSong={currentSong}
          activeBible={activeBible}
          selectedLiturgyItemId={selectedLiturgyItemId}
          onSelectItem={handleSelectItemFromRoteiro}
          onSelectBackground={(background) => dispatch({ type: 'SET_BACKGROUND', background })}
          onSelectPptxDeck={() => setIsPptxOpen(true)}
        />

        {/* Coluna 2 (22%): Biblioteca / Catálogo de Músicas & Bíblia */}
        <section className="flex flex-col bg-[#0F1622] p-3 border-r border-white/5 overflow-hidden">
          <div className="mb-2 flex items-center justify-between">
            <span className="text-[11px] font-semibold uppercase tracking-wider text-slate-400">
              Biblioteca ({songs.length})
            </span>
            <div className="flex items-center gap-1">
              <Button
                variant="secondary"
                size="sm"
                onClick={() => setIsSongImporterOpen(true)}
                title="Importar pacote de músicas ou colar letra"
              >
                <Upload className="h-3 w-3 text-sky-400" />
                <span>Importar</span>
              </Button>

              <Button
                variant="secondary"
                size="sm"
                onClick={() => setIsSongHistoryOpen(true)}
                title="Consultar histórico e músicas mais cantadas"
              >
                <History className="h-3 w-3 text-slate-400" />
                <span>Histórico</span>
              </Button>
            </div>
          </div>

          <div className="relative mb-2.5">
            <Search className="absolute left-2.5 top-2.5 h-3.5 w-3.5 text-slate-400" />
            <Input
              placeholder="Filtrar hino ou versículo (ex: jo 3 16)..."
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              className="pl-8 pr-8 bg-[#192334] border-transparent focus:border-sky-500/50"
            />
            {isLoadingSongs && (
              <Loader2 className="absolute right-2.5 top-2.5 h-3.5 w-3.5 animate-spin text-slate-400" />
            )}
          </div>

          <div className="flex flex-1 flex-col gap-1.5 overflow-y-auto text-xs pr-0.5">
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
              liveSongId={state.mode === 'SONG' ? state.activeItemId : null}
              onSelectSong={handleSelectSong}
            />
          </div>
        </section>

        {/* Coluna 3 (33%): Palco / Slides da Música ou Versículos (Superfície #131C2B mais clara para foco) */}
        <section className="flex flex-col bg-[#131C2B] p-4 overflow-y-auto">
          {activeBible ? (
            <BibleVerseGrid
              activeBible={activeBible}
              activeSlideIndex={state.foreground.slideIndex}
              isLiveOnScreen={state.mode === 'BIBLE'}
              dualVersion={dualBibleVersion}
              onChangeDualVersion={handleChangeDualVersion}
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
            <div className="flex flex-1 items-center justify-center text-xs text-slate-400">
              Selecione uma música ou referência bíblica para visualizar e projetar
            </div>
          )}
        </section>

        {/* Coluna 4 (25%): Monitor de Saída 16:9 & Áudio Ambiente */}
        <OutputMonitorPanel
          state={state}
          onSelectBackground={(background) => dispatch({ type: 'SET_BACKGROUND', background })}
        />
      </main>

      {/* 3. Modais de Ferramentas Secundárias e Diagnóstico */}
      <SpotlightDialog
        isOpen={isSpotlightOpen}
        onClose={() => setIsSpotlightOpen(false)}
        onSelectSong={handleSelectSong}
        onSelectBible={handleSelectBible}
        onTogglePanic={(target) => dispatch({ type: 'TOGGLE_PANIC', target })}
      />
      <AutomationModal isOpen={isAutomationOpen} onClose={() => setIsAutomationOpen(false)} />
      <KeymapModal isOpen={isKeymapOpen} onClose={() => setIsKeymapOpen(false)} />
      <StageControlModal
        isOpen={isStageControlOpen}
        stageState={stageState}
        onClose={() => setIsStageControlOpen(false)}
      />
      <RemotePairingModal
        isOpen={isRemotePairingOpen}
        onClose={() => setIsRemotePairingOpen(false)}
      />
      <PptxDeckModal
        isOpen={isPptxOpen}
        onClose={() => setIsPptxOpen(false)}
        dispatch={dispatch}
      />
      <BibleDownloadModal
        isOpen={isBibleDownloadsOpen}
        onClose={() => setIsBibleDownloadsOpen(false)}
      />
      <SongHistoryModal
        isOpen={isSongHistoryOpen}
        onClose={() => setIsSongHistoryOpen(false)}
        onSelectSong={handleSelectSong}
      />
      <SongImporterModal
        isOpen={isSongImporterOpen}
        onClose={() => setIsSongImporterOpen(false)}
      />
      <DiagnosticsModal
        isOpen={isDiagnosticsOpen}
        isConnected={connected}
        state={state}
        onClose={() => setIsDiagnosticsOpen(false)}
      />
    </div>
  )
}
