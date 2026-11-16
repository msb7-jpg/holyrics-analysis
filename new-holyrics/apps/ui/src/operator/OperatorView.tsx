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
import { useQueryClient } from '@tanstack/react-query'
import { Loader2, ChevronLeft, ChevronRight, Presentation } from 'lucide-react'
import { songKeys } from '@slices/songs/ui/queries/songs-query-keys'
import { ResizableSplitPane } from '@ui/components/ui/ResizableSplitPane'
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
import { OperatorTopBar } from '@slices/navigation/ui/components/OperatorTopBar'
import { OutputMonitorPanel } from '@slices/navigation/ui/components/OutputMonitorPanel'
import { DiagnosticsModal } from '@slices/navigation/ui/components/DiagnosticsModal'
import { LiturgyScheduleColumn } from '@slices/liturgy/ui/components/LiturgyScheduleColumn'
import {
  useCurrentLiturgyQuery,
  useUpdateLiturgyTitleMutation,
  useSaveLiturgyMutation,
} from '@slices/liturgy/ui/queries/liturgy-queries'
import type { LiturgyItem, LiturgyFile } from '@slices/liturgy/contracts/liturgy-schema'
import { SongStanzaGrid } from '@slices/songs/ui/components/SongStanzaGrid'
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
  const queryClient = useQueryClient()
  const { data: currentLiturgy } = useCurrentLiturgyQuery()
  const { mutate: updateLiturgyTitle } = useUpdateLiturgyTitleMutation()
  const { mutate: saveLiturgy } = useSaveLiturgyMutation()

  const handleExportLiturgy = () => {
    if (!currentLiturgy) return
    const jsonStr = JSON.stringify(currentLiturgy, null, 2)
    const blob = new Blob([jsonStr], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const anchor = document.createElement('a')
    const safeName = (currentLiturgy.title || 'roteiro-culto')
      .toLowerCase()
      .replace(/[^a-z0-9-_]+/gi, '-')
    anchor.href = url
    anchor.download = `${safeName}.json`
    anchor.click()
    URL.revokeObjectURL(url)
  }

  const handleImportLiturgy = async (file: File) => {
    try {
      const text = await file.text()
      const parsed = JSON.parse(text) as LiturgyFile
      saveLiturgy(parsed)
    } catch {
      // Arquivo JSON inválido ignorado silenciosamente
    }
  }

  const handleNewLiturgy = () => {
    const emptyLiturgy: LiturgyFile = {
      id: `culto_${Date.now()}`,
      title: 'Novo Culto',
      date: new Date().toISOString().slice(0, 10),
      items: [],
      hooksEnabled: true,
    }
    saveLiturgy(emptyLiturgy)
  }

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
        onExportLiturgy={handleExportLiturgy}
        onImportLiturgy={handleImportLiturgy}
        onNewLiturgy={handleNewLiturgy}
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

      {/* 2. Layout Principal de 3 Colunas Redimensionáveis (Esquerda: Navegador | Centro: Palco | Direita: Monitor) */}
      <main className="flex flex-1 overflow-hidden">
        <ResizableSplitPane
          direction="horizontal"
          initialSizes={[27, 47, 26]}
          minSizes={[18, 28, 18]}
          storageKey="lumen-split-operator-cols"
          className="h-full w-full"
        >
          {/* Coluna 1 (Esquerda): Roteiro, Letras, Imagens, Vídeos, Áudio & Arquivos */}
          <LiturgyScheduleColumn
            state={state}
            selectedSong={currentSong}
            activeBible={activeBible}
            selectedLiturgyItemId={selectedLiturgyItemId}
            onSelectItem={handleSelectItemFromRoteiro}
            onSelectBackground={(background) => dispatch({ type: 'SET_BACKGROUND', background })}
            onSelectPptxDeck={() => setIsPptxOpen(true)}
            songs={songs}
            songQuery={query}
            onChangeSongQuery={setQuery}
            isLoadingSongs={isLoadingSongs}
            selectedSongId={selectedSongId}
            bibleLookupResult={bibleLookupResult}
            onSelectSong={handleSelectSong}
            onSelectBible={handleSelectBible}
            onOpenSongImporter={() => setIsSongImporterOpen(true)}
            onOpenSongHistory={() => setIsSongHistoryOpen(true)}
          />

          {/* Coluna 2 (Centro): Palco / Slides da Música, Versículos ou Apresentação */}
          <section className="flex h-full flex-col bg-[#131C2B] p-4 overflow-y-auto">
          {state.mode === 'PPTX' && state.foreground.slides && state.foreground.slides.length > 0 ? (
            <div className="flex flex-1 flex-col gap-3">
              <div className="flex items-center justify-between border-b border-white/10 pb-2.5">
                <div className="flex items-center gap-2 min-w-0">
                  <Presentation className="h-4 w-4 text-emerald-400 shrink-0" />
                  <div className="min-w-0">
                    <h3 className="text-xs font-semibold text-white truncate">
                      {state.foreground.title || 'Apresentação'}
                    </h3>
                    <p className="text-[10px] text-slate-400">
                      Slide {state.foreground.slideIndex + 1} de {state.foreground.totalSlides}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-1.5 shrink-0">
                  <button
                    type="button"
                    onClick={() => dispatch({ type: 'NAV_PREV' })}
                    className="flex items-center gap-1 rounded bg-[#192334] hover:bg-[#1E2B40] px-2.5 py-1 text-[11px] font-medium text-slate-200 transition"
                  >
                    <ChevronLeft className="h-3.5 w-3.5" />
                    <span>Anterior</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => dispatch({ type: 'NAV_NEXT' })}
                    className="flex items-center gap-1 rounded bg-sky-500 hover:bg-sky-400 px-2.5 py-1 text-[11px] font-semibold text-slate-950 transition"
                  >
                    <span>Próximo</span>
                    <ChevronRight className="h-3.5 w-3.5" />
                  </button>
                </div>
              </div>

              <div className="grid grid-cols-2 auto-rows-max content-start gap-3">
                {state.foreground.slides.map((slide, idx) => {
                  const isLive = state.foreground.slideIndex === idx
                  return (
                    <div
                      key={idx}
                      onClick={() => dispatch({ type: 'NAV_GOTO', slideIndex: idx })}
                      className={`group relative flex flex-col rounded-lg border cursor-pointer overflow-hidden transition ${
                        isLive
                          ? 'border-emerald-500 bg-emerald-950/20 ring-1 ring-emerald-500/50'
                          : 'border-white/10 bg-[#192334] hover:border-sky-500/50'
                      }`}
                    >
                      <div className="flex items-center justify-between px-2.5 py-1 bg-[#090D14]/60 text-[10px]">
                        <span className="font-semibold text-slate-300">Slide {idx + 1}</span>
                        {isLive && (
                          <span className="rounded bg-emerald-500/20 px-1.5 py-0.2 font-bold text-emerald-300">
                            AO VIVO
                          </span>
                        )}
                      </div>
                      <div className="aspect-video w-full bg-black flex items-center justify-center overflow-hidden p-2">
                        {slide.imageUrl ? (
                          <img
                            src={slide.imageUrl}
                            alt={`Slide ${idx + 1}`}
                            className="h-full w-full object-contain"
                          />
                        ) : (
                          <div className="text-[11px] text-center font-medium text-slate-200 line-clamp-4">
                            {slide.content.join(' ')}
                          </div>
                        )}
                      </div>
                    </div>
                  )
                })}
              </div>
            </div>
          ) : activeBible ? (
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

        {/* Coluna 3 (Direita): Monitor de Saída 16:9 & Áudio Ambiente */}
        <OutputMonitorPanel
          state={state}
          onSelectBackground={(background) => dispatch({ type: 'SET_BACKGROUND', background })}
        />
        </ResizableSplitPane>
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
        state={state}
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
        onImported={() => {
          void queryClient.invalidateQueries({ queryKey: songKeys.all })
        }}
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
