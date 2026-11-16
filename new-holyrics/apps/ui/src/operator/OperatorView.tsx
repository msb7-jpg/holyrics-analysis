/**
 * @module @ui/operator/OperatorView
 * @description Console principal de operação litúrgica do LumenLite (4 colunas Slate Pro Studio).
 *
 * ## Responsabilidades:
 * - Orquestrar o fluxo Preparar (Esquerda) -> Operar (Centro) -> Monitorar (Direita) com proporção 20% | 22% | 33% | 25%.
 * - Aplicar hierarquia de superfícies (#090D14 -> #0F1622 -> #131C2B) e semântica Preview (Azul) vs Ao Vivo (Esmeralda).
 * - Coordenar modais de ferramentas secundárias e diagnóstico sem vazamentos técnicos na interface principal.
 */

import { useState, useEffect } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { Loader2, ChevronLeft, ChevronRight, Presentation } from 'lucide-react'
import { songKeys } from '@slices/songs/ui/queries/songs-query-keys'
import { ResizableSplitPane } from '@ui/components/ui/ResizableSplitPane'
import type { ScreenState, ActionCommand } from '@lumen/core'
import { bibleService } from '@slices/bible/ui/services/bible-service'
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
import type { Song } from '@slices/songs/contracts/song-contracts'
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
    workspaceItem,
    workspaceFocusedIndex,
    setWorkspaceFocusedIndex,
    handleSelectSong,
    handleSelectBible,
    handleChangeDualVersion,
    handleInspectPptxDeck,
    handleProjectSongById,
    handleProjectSongStanza,
    handleProjectBibleVerse,
    handleProjectMediaFullscreen,
    handleProjectWorkspaceCurrent,
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
  const [songBeingEdited, setSongBeingEdited] = useState<Song | null>(null)
  const [isDiagnosticsOpen, setIsDiagnosticsOpen] = useState(false)
  const [selectedLiturgyItemId, setSelectedLiturgyItemId] = useState<string | null>(null)

  // Trata tecla Enter para disparar o item e slide focado no workspace
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key !== 'Enter') return
      const target = event.target as HTMLElement | null
      const tagName = target?.tagName
      if (tagName === 'INPUT' || tagName === 'TEXTAREA' || target?.isContentEditable) {
        return
      }
      event.preventDefault()
      handleProjectWorkspaceCurrent()
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [handleProjectWorkspaceCurrent])

  // 1 clique: seleciona e carrega no workspace central
  const handleSelectItemFromRoteiro = (item: LiturgyItem) => {
    setSelectedLiturgyItemId(item.id)
    if (item.type === 'MEDIA') {
      return
    }
    if (item.type === 'PPTX') {
      setIsPptxOpen(true)
      return
    }
    handleSelectLiturgyItem(item)
  }

  // 2 cliques (duplo clique): projeta imediatamente para o telão
  const handleProjectItemFromRoteiro = (item: LiturgyItem) => {
    setSelectedLiturgyItemId(item.id)
    if (item.type === 'MEDIA') {
      handleProjectMediaFullscreen({
        id: item.id,
        title: item.title,
        mediaSrc: item.mediaSrc,
        mediaType: item.mediaType,
      })
      return
    }
    if (item.type === 'SONG') {
      handleProjectSongById(item.songId)
      return
    }
    if (item.type === 'BIBLE') {
      bibleService
        .lookup(item.reference, item.version || 'ACF', undefined, item.dualVersion)
        .then((lookup) => {
          if (lookup) {
            handleChangeDualVersion(item.dualVersion || null)
            handleSelectBible(lookup)
            handleProjectBibleVerse(lookup, 0)
          }
        })
        .catch(() => {
          // Fallback silencioso
        })
      return
    }
    if (item.type === 'PPTX') {
      setIsPptxOpen(true)
      return
    }
    if (item.type === 'TEXT') {
      dispatch({
        type: 'PRESENT_ITEM',
        itemId: item.id,
        mode: 'EMPTY',
        foreground: {
          title: item.title,
          content: item.content,
          slideIndex: 0,
          totalSlides: 1,
        },
      })
      return
    }
    if (item.type === 'COUNTDOWN') {
      dispatch({
        type: 'PRESENT_ITEM',
        itemId: item.id,
        mode: 'EMPTY',
        foreground: {
          title: item.title,
          content: [`Início em ${Math.round((item.targetDurationSeconds || 300) / 60)} minutos`],
          slideIndex: 0,
          totalSlides: 1,
        },
      })
      return
    }
  }

  // Determina se o item atualmente visualizado no Workspace é o que está no telão (Live)
  const isWorkspaceItemLive = Boolean(
    workspaceItem
      ? (workspaceItem.type === 'SONG' &&
          state.mode === 'SONG' &&
          state.activeItemId === workspaceItem.songId) ||
        (workspaceItem.type === 'BIBLE' &&
          state.mode === 'BIBLE' &&
          state.activeItemId === `bible_${workspaceItem.lookup.reference}`) ||
        (workspaceItem.type === 'PPTX' &&
          state.mode === 'PPTX' &&
          (!workspaceItem.deckId || state.activeItemId === workspaceItem.deckId))
      : state.mode !== 'EMPTY'
  )

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
            onProjectItem={handleProjectItemFromRoteiro}
            onProjectMedia={handleProjectMediaFullscreen}
            onSelectBackground={(background) => dispatch({ type: 'SET_BACKGROUND', background })}
            onSelectPptxDeck={(deck) => handleInspectPptxDeck(deck)}
            songs={songs}
            songQuery={query}
            onChangeSongQuery={setQuery}
            isLoadingSongs={isLoadingSongs}
            selectedSongId={selectedSongId}
            bibleLookupResult={bibleLookupResult}
            onSelectSong={handleSelectSong}
            onProjectSong={handleProjectSongById}
            onSelectBible={handleSelectBible}
            onOpenSongImporter={() => {
              setSongBeingEdited(null)
              setIsSongImporterOpen(true)
            }}
            onOpenSongHistory={() => setIsSongHistoryOpen(true)}
          />

          {/* Coluna 2 (Centro): Workspace Central (Preview) */}
          <section className="flex h-full flex-col bg-[#131C2B] p-4 overflow-y-auto">
            {/* Renderização do Conteúdo: PPTX, BÍBLIA ou MÚSICA */}
            {workspaceItem?.type === 'PPTX' ||
            (!workspaceItem &&
              state.mode === 'PPTX' &&
              state.foreground.slides &&
              state.foreground.slides.length > 0) ? (
              (() => {
                const pptxSlides =
                  workspaceItem?.type === 'PPTX'
                    ? workspaceItem.slides
                    : state.foreground.slides || []
                return (
                  <div className="flex flex-1 flex-col gap-3">
                    <div className="flex items-center justify-between border-b border-white/5 pb-2">
                      <div className="flex items-center gap-2 min-w-0">
                        <Presentation className="h-4 w-4 text-emerald-400 shrink-0" />
                        <div className="min-w-0">
                          <h3 className="text-xs font-semibold text-white truncate">
                            {workspaceItem?.type === 'PPTX'
                              ? workspaceItem.title || 'Apresentação'
                              : state.foreground.title || 'Apresentação'}
                          </h3>
                          <p className="text-[10px] text-slate-400">
                            Slide {workspaceFocusedIndex + 1} de {pptxSlides.length}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-1.5 shrink-0">
                        <button
                          type="button"
                          onClick={() => {
                            if (isWorkspaceItemLive) {
                              dispatch({ type: 'NAV_PREV' })
                            } else {
                              setWorkspaceFocusedIndex((prev) => Math.max(0, prev - 1))
                            }
                          }}
                          className="flex items-center gap-1 rounded bg-[#192334] hover:bg-[#1E2B40] px-2.5 py-1 text-[11px] font-medium text-slate-200 transition"
                        >
                          <ChevronLeft className="h-3.5 w-3.5" />
                          <span>Anterior</span>
                        </button>
                        <button
                          type="button"
                          onClick={() => {
                            if (isWorkspaceItemLive) {
                              dispatch({ type: 'NAV_NEXT' })
                            } else {
                              setWorkspaceFocusedIndex((prev) =>
                                Math.min(pptxSlides.length - 1, prev + 1)
                              )
                            }
                          }}
                          className="flex items-center gap-1 rounded bg-sky-500 hover:bg-sky-400 px-2.5 py-1 text-[11px] font-semibold text-slate-950 transition"
                        >
                          <span>Próximo</span>
                          <ChevronRight className="h-3.5 w-3.5" />
                        </button>
                      </div>
                    </div>

                    <div className="grid grid-cols-2 auto-rows-max content-start gap-3">
                      {pptxSlides.map((slide, slideIdx) => {
                        const isLive =
                          isWorkspaceItemLive && state.foreground.slideIndex === slideIdx
                        const isFocused = !isLive && workspaceFocusedIndex === slideIdx
                        return (
                          <div
                            key={slideIdx}
                            onClick={() => setWorkspaceFocusedIndex(slideIdx)}
                            onDoubleClick={() => {
                              setWorkspaceFocusedIndex(slideIdx)
                              if (isWorkspaceItemLive) {
                                dispatch({ type: 'NAV_GOTO', slideIndex: slideIdx })
                              } else if (workspaceItem?.type === 'PPTX') {
                                const targetSlide = workspaceItem.slides[slideIdx]
                                dispatch({
                                  type: 'PRESENT_ITEM',
                                  itemId: workspaceItem.deckId || 'pptx_deck',
                                  mode: 'PPTX',
                                  foreground: {
                                    title: workspaceItem.title || 'Apresentação',
                                    reference: targetSlide?.reference,
                                    content: targetSlide?.content || [],
                                    speakerNotes: targetSlide?.speakerNotes,
                                    imageUrl: targetSlide?.imageUrl,
                                    slides: workspaceItem.slides,
                                    slideIndex: slideIdx,
                                    totalSlides: workspaceItem.slides.length,
                                  },
                                })
                              }
                            }}
                            className={`group relative flex flex-col rounded-lg border cursor-pointer overflow-hidden transition select-none ${
                              isLive
                                ? 'border-emerald-500 bg-emerald-950/20 ring-1 ring-emerald-500/50'
                                : isFocused
                                  ? 'border-sky-500/80 bg-[#172338] ring-1 ring-sky-500/40'
                                  : 'border-white/10 bg-[#192334] hover:border-sky-500/50'
                            }`}
                          >
                            <div className="flex items-center justify-between px-2.5 py-1 bg-[#090D14]/60 text-[10px]">
                              <span className="font-semibold text-slate-300">
                                Slide {slideIdx + 1}
                              </span>
                              {isLive ? (
                                <span className="rounded bg-emerald-500/20 px-1.5 py-0.2 font-bold text-emerald-300">
                                  AO VIVO
                                </span>
                              ) : isFocused ? (
                                <span className="rounded bg-sky-500/20 px-1.5 py-0.2 font-bold text-sky-300">
                                  FOCADO
                                </span>
                              ) : null}
                            </div>
                            <div className="aspect-video w-full bg-black flex items-center justify-center overflow-hidden p-2">
                              {slide.imageUrl ? (
                                <img
                                  src={slide.imageUrl}
                                  alt={`Slide ${slideIdx + 1}`}
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
                )
              })()
            ) : workspaceItem?.type === 'BIBLE' || (!workspaceItem && activeBible) ? (
              (() => {
                const targetBible =
                  workspaceItem?.type === 'BIBLE' ? workspaceItem.lookup : activeBible!
                return (
                  <BibleVerseGrid
                    activeBible={targetBible}
                    activeSlideIndex={state.foreground.slideIndex}
                    isLiveOnScreen={
                      state.mode === 'BIBLE' &&
                      state.activeItemId === `bible_${targetBible.reference}`
                    }
                    focusedIndex={workspaceFocusedIndex}
                    dualVersion={dualBibleVersion}
                    onChangeDualVersion={handleChangeDualVersion}
                    onSelectVerse={(verseIdx) => setWorkspaceFocusedIndex(verseIdx)}
                    onProjectVerse={(verseIdx) => {
                      setWorkspaceFocusedIndex(verseIdx)
                      handleProjectBibleVerse(targetBible, verseIdx)
                    }}
                  />
                )
              })()
            ) : isLoadingSongDetail ? (
              <div className="flex flex-1 items-center justify-center">
                <Loader2 className="h-6 w-6 animate-spin text-sky-400" />
              </div>
            ) : currentSong ? (
              <SongStanzaGrid
                song={currentSong}
                activeSlideIndex={state.foreground.slideIndex}
                isLiveOnScreen={state.mode === 'SONG' && state.activeItemId === currentSong.id}
                focusedIndex={workspaceFocusedIndex}
                onSelectStanza={(stanzaIdx) => setWorkspaceFocusedIndex(stanzaIdx)}
                onProjectStanza={(stanzaIdx) => {
                  setWorkspaceFocusedIndex(stanzaIdx)
                  handleProjectSongStanza(currentSong, stanzaIdx)
                }}
                onEditSong={(song) => {
                  setSongBeingEdited(song)
                  setIsSongImporterOpen(true)
                }}
              />
            ) : (
              <div className="flex flex-1 items-center justify-center text-xs text-slate-400">
                Selecione uma música, passagem bíblica ou apresentação para preparar no workspace
              </div>
            )}
          </section>

          {/* Coluna 3 (Direita): Monitor de Saída 16:9 & Áudio Ambiente */}
          <OutputMonitorPanel
            state={state}
            onSelectBackground={(background) => dispatch({ type: 'SET_BACKGROUND', background })}
            onProjectMedia={handleProjectMediaFullscreen}
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
        songToEdit={songBeingEdited}
        onClose={() => {
          setIsSongImporterOpen(false)
          setSongBeingEdited(null)
        }}
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
