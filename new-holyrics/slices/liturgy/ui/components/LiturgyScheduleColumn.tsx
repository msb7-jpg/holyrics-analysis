/**
 * @module @slices/liturgy/ui/components/LiturgyScheduleColumn
 * @description Coluna 1 (Esquerda) do operador com abas de acesso rápido (Roteiro | Imagens | Vídeos | Arquivos), Drag & Drop universal e menu de contexto.
 */

import { useState, useRef } from 'react'
import {
  Calendar,
  Plus,
  Loader2,
  Image as ImageIcon,
  Film,
  Presentation,
  Upload,
  Music,
  Headphones,
  Search,
  History,
} from 'lucide-react'
import type { ScreenState, BackgroundLayer } from '@lumen/core'
import type { Song, SongSearchResult } from '@slices/songs/contracts/song-contracts'
import { SongSearchList } from '@slices/songs/ui/components/SongSearchList'
import type { BibleLookupResponse } from '@slices/bible/contracts/bible-contracts'
import { BibleQuickLookupCard } from '@slices/bible/ui/components/BibleQuickLookupCard'
import type { PptxPresentation } from '@slices/pptx/contracts/pptx-contracts'
import { pptxService } from '@slices/pptx/ui/services/pptx-service'
import { renderPdfFileToPresentation } from '@slices/pptx/ui/utils/pdf-renderer'
import { PptxFilesPanel } from '@slices/pptx/ui/components/PptxFilesPanel'
import { MediaGridPanel } from '@slices/media/ui/components/MediaGridPanel'
import type { MediaItem } from '@slices/media/contracts/media-contracts'
import { Input } from '@ui/components/ui/input'
import { Button } from '@ui/components/ui/button'
import type { LiturgyItem } from '../../contracts/liturgy-schema'
import { LiturgyItemCard } from './LiturgyItemCard'
import { LiturgyContextMenu } from './LiturgyContextMenu'
import { QuickTextModal } from './QuickTextModal'
import { useLiturgyController } from '../hooks/useLiturgyController'

export type LeftColumnTab = 'ROTEIRO' | 'LETRAS' | 'IMAGENS' | 'VIDEOS' | 'AUDIO' | 'ARQUIVOS'

export interface LiturgyScheduleColumnProps {
  state: ScreenState
  selectedSong?: Song | null
  activeBible?: BibleLookupResponse | null
  selectedLiturgyItemId?: string | null
  onSelectItem?: (item: LiturgyItem) => void
  onProjectItem?: (item: LiturgyItem) => void
  onProjectMedia?: (item: MediaItem) => void
  onSelectBackground?: (background: BackgroundLayer) => void
  onSelectPptxDeck?: (deck: PptxPresentation) => void
  songs?: SongSearchResult[]
  songQuery?: string
  onChangeSongQuery?: (query: string) => void
  isLoadingSongs?: boolean
  selectedSongId?: string | null
  bibleLookupResult?: BibleLookupResponse | null
  onSelectSong?: (songId: string) => void
  onProjectSong?: (songId: string) => void
  onSelectBible?: (lookup: BibleLookupResponse) => void
  onOpenSongImporter?: () => void
  onOpenSongHistory?: () => void
}

/**
 * Coluna 1 interativa do roteiro do culto e navegador de mídias/arquivos com Drag & Drop universal.
 *
 * @param props - Propriedades da Coluna 1.
 * @returns Elemento JSX com abas Roteiro, Imagens, Vídeos e Arquivos.
 */
export function LiturgyScheduleColumn({
  state,
  selectedSong,
  activeBible,
  selectedLiturgyItemId,
  onSelectItem,
  onProjectItem,
  onProjectMedia,
  onSelectBackground,
  onSelectPptxDeck,
  songs = [],
  songQuery = '',
  onChangeSongQuery,
  isLoadingSongs = false,
  selectedSongId,
  bibleLookupResult,
  onSelectSong,
  onProjectSong,
  onSelectBible,
  onOpenSongImporter,
  onOpenSongHistory,
}: LiturgyScheduleColumnProps) {
  const {
    liturgy,
    isLoading,
    draggedIndex,
    contextMenu,
    isQuickTextOpen,
    setIsQuickTextOpen,
    handleDragStart,
    handleDragOver,
    handleDrop,
    handleOpenContextMenu,
    handleCloseContextMenu,
    handleAddCountdown,
    handleAddNote,
    handleDuplicateItem,
    handleMoveItemUp,
    handleMoveItemDown,
    handleAddCustomItem,
    handleRemoveItem,
  } = useLiturgyController()

  const [activeTab, setActiveTab] = useState<LeftColumnTab>('ROTEIRO')
  const [isRoteiroDropHover, setIsRoteiroDropHover] = useState(false)
  const roteiroHoverTimerRef = useRef<number | null>(null)

  const isItemLive = (item: LiturgyItem): boolean => {
    if (state.activeItemId === item.id) return true
    if (item.type === 'SONG' && state.mode === 'SONG' && state.activeItemId === item.songId) return true
    if (item.type === 'BIBLE' && state.mode === 'BIBLE' && state.activeItemId === `bible_${item.reference}`) return true
    return false
  }

  const handleAddSelectedSongToLiturgy = () => {
    if (!selectedSong) return
    handleAddCustomItem({
      id: `item-song-${Date.now()}`,
      type: 'SONG',
      title: selectedSong.title,
      songId: selectedSong.id,
      artist: selectedSong.artist,
    })
  }

  const handleAddSelectedBibleToLiturgy = () => {
    if (!activeBible) return
    handleAddCustomItem({
      id: `item-bible-${Date.now()}`,
      type: 'BIBLE',
      title: `Leitura: ${activeBible.reference}`,
      reference: activeBible.reference,
      version: activeBible.version,
    })
  }

  const handleOsFilesDropOnRoteiro = async (files: FileList) => {
    for (const file of Array.from(files)) {
      const lowerName = file.name.toLowerCase()
      if (lowerName.endsWith('.pptx') || lowerName.endsWith('.pdf')) {
        let created: PptxPresentation
        if (lowerName.endsWith('.pdf')) {
          try {
            const rendered = await renderPdfFileToPresentation(file)
            created = await pptxService.ingestPresentation(rendered)
          } catch {
            const arrayBuf = await file.arrayBuffer()
            const bytes = new Uint8Array(arrayBuf)
            let binaryStr = ''
            for (let index = 0; index < bytes.length; index++) {
              binaryStr += String.fromCharCode(bytes[index])
            }
            created = await pptxService.ingestFile(file.name, btoa(binaryStr))
          }
        } else {
          const arrayBuf = await file.arrayBuffer()
          const bytes = new Uint8Array(arrayBuf)
          let binaryStr = ''
          for (let index = 0; index < bytes.length; index++) {
            binaryStr += String.fromCharCode(bytes[index])
          }
          created = await pptxService.ingestFile(file.name, btoa(binaryStr))
        }
        handleAddCustomItem({
          id: `item-pptx-${Date.now()}`,
          type: 'PPTX',
          title: created.title,
          fileSrc: created.id,
          slidesCount: created.slides.length,
        })
      } else {
        const formData = new FormData()
        formData.append('file', file)
        const res = await fetch('/api/media/upload', { method: 'POST', body: formData })
        const json = (await res.json()) as { ok: boolean; data?: MediaItem }
        if (json.ok && json.data) {
          const streamUrl = `/api/media/stream/${encodeURIComponent(json.data.id)}`
          handleAddCustomItem({
            id: `item-media-${Date.now()}`,
            type: 'MEDIA',
            title: json.data.name,
            mediaSrc: streamUrl,
            mediaType:
              json.data.type === 'VIDEO'
                ? 'VIDEO'
                : json.data.type === 'AUDIO'
                  ? 'AUDIO'
                  : 'IMAGE',
          })
        }
      }
    }
  }

  const items = liturgy?.items || []

  return (
    <section
      className="flex flex-col bg-zinc-950 p-3 border-r border-zinc-800/80 overflow-y-auto relative select-none"
      onContextMenu={(event) => {
        if (activeTab === 'ROTEIRO') {
          handleOpenContextMenu(event, null)
        }
      }}
    >
      {/* Abas Superiores da Coluna Esquerda: Roteiro | Letras | Imagens | Vídeos | Áudio | Arquivos */}
      <div className="mb-3 grid grid-cols-6 gap-0.5 rounded-lg bg-zinc-900 p-1 border border-zinc-800/80 text-[11px] shrink-0">
        <button
          type="button"
          onClick={() => setActiveTab('ROTEIRO')}
          onDragOver={(event) => {
            event.preventDefault()
            setIsRoteiroDropHover(true)
            if (activeTab !== 'ROTEIRO' && roteiroHoverTimerRef.current === null) {
              roteiroHoverTimerRef.current = window.setTimeout(() => {
                setActiveTab('ROTEIRO')
                roteiroHoverTimerRef.current = null
              }, 350)
            }
          }}
          onDragLeave={() => {
            setIsRoteiroDropHover(false)
            if (roteiroHoverTimerRef.current !== null) {
              window.clearTimeout(roteiroHoverTimerRef.current)
              roteiroHoverTimerRef.current = null
            }
          }}
          onDrop={(event) => {
            setIsRoteiroDropHover(false)
            if (roteiroHoverTimerRef.current !== null) {
              window.clearTimeout(roteiroHoverTimerRef.current)
              roteiroHoverTimerRef.current = null
            }
            if (event.dataTransfer.files && event.dataTransfer.files.length > 0) {
              event.preventDefault()
              void handleOsFilesDropOnRoteiro(event.dataTransfer.files)
              setActiveTab('ROTEIRO')
              return
            }
            handleDrop(event)
            setActiveTab('ROTEIRO')
          }}
          title="Ordem do Culto (arraste qualquer item ou arquivo para cá)"
          className={`flex flex-col items-center justify-center gap-0.5 py-1.5 font-medium rounded-md transition ${
            isRoteiroDropHover
              ? 'bg-zinc-700 text-zinc-100 ring-1 ring-zinc-400'
              : activeTab === 'ROTEIRO'
                ? 'bg-zinc-800 text-zinc-100 shadow-sm'
                : 'text-zinc-400 hover:text-zinc-200'
          }`}
        >
          <Calendar className="h-4 w-4" strokeWidth={1.75} />
          <span className="text-[11px] font-medium">Roteiro</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('LETRAS')}
          title="Letras de Músicas e Busca Bíblica"
          className={`flex flex-col items-center justify-center gap-0.5 py-1.5 font-medium rounded-md transition ${
            activeTab === 'LETRAS'
              ? 'bg-zinc-800 text-zinc-100 shadow-sm'
              : 'text-zinc-400 hover:text-zinc-200'
          }`}
        >
          <Music className="h-4 w-4" strokeWidth={1.75} />
          <span className="text-[11px] font-medium">Letras</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('IMAGENS')}
          title="Galeria de Imagens (clique para projetar ou arraste ao roteiro)"
          className={`flex flex-col items-center justify-center gap-0.5 py-1.5 font-medium rounded-md transition ${
            activeTab === 'IMAGENS'
              ? 'bg-zinc-800 text-zinc-100 shadow-sm'
              : 'text-zinc-400 hover:text-zinc-200'
          }`}
        >
          <ImageIcon className="h-4 w-4" strokeWidth={1.75} />
          <span className="text-[11px] font-medium">Imagens</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('VIDEOS')}
          title="Galeria de Vídeos e Loops (clique para projetar ou arraste ao roteiro)"
          className={`flex flex-col items-center justify-center gap-0.5 py-1.5 font-medium rounded-md transition ${
            activeTab === 'VIDEOS'
              ? 'bg-zinc-800 text-zinc-100 shadow-sm'
              : 'text-zinc-400 hover:text-zinc-200'
          }`}
        >
          <Film className="h-4 w-4" strokeWidth={1.75} />
          <span className="text-[11px] font-medium">Vídeos</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('AUDIO')}
          title="Biblioteca de Áudio (arraste ao Roteiro)"
          className={`flex flex-col items-center justify-center gap-0.5 py-1.5 font-medium rounded-md transition ${
            activeTab === 'AUDIO'
              ? 'bg-zinc-800 text-zinc-100 shadow-sm'
              : 'text-zinc-400 hover:text-zinc-200'
          }`}
        >
          <Headphones className="h-4 w-4" strokeWidth={1.75} />
          <span className="text-[11px] font-medium">Áudio</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('ARQUIVOS')}
          title="Apresentações e Arquivos (.pptx, .pdf)"
          className={`flex flex-col items-center justify-center gap-0.5 py-1.5 font-medium rounded-md transition ${
            activeTab === 'ARQUIVOS'
              ? 'bg-zinc-800 text-zinc-100 shadow-sm'
              : 'text-zinc-400 hover:text-zinc-200'
          }`}
        >
          <Presentation className="h-4 w-4" strokeWidth={1.75} />
          <span className="text-[11px] font-medium">Arquivos</span>
        </button>
      </div>

      {/* ABA 1: ROTEIRO DO CULTO */}
      {activeTab === 'ROTEIRO' && (
        <div
          className="flex flex-1 flex-col"
          onDragOver={(event) => handleDragOver(event)}
          onDrop={(event) => {
            if (event.dataTransfer.files && event.dataTransfer.files.length > 0) {
              event.preventDefault()
              handleOsFilesDropOnRoteiro(event.dataTransfer.files)
              return
            }
            handleDrop(event)
          }}
        >
          <div className="mb-2.5 flex items-center justify-between text-xs font-semibold text-zinc-300">
            <span className="text-[11px] uppercase tracking-wider text-zinc-400">
              Ordem do Culto ({items.length})
            </span>
            <div className="flex items-center gap-1.5">
              <button
                type="button"
                onClick={() => setIsQuickTextOpen(true)}
                title="Exibir aviso rápido ou adicionar texto ao roteiro"
                className="flex items-center gap-1 rounded-md bg-zinc-900 hover:bg-zinc-800 border border-zinc-800 px-2.5 py-1 text-xs font-medium text-zinc-200 transition"
              >
                <Plus className="h-3 w-3 text-zinc-400" strokeWidth={1.75} />
                <span>Adicionar</span>
              </button>
            </div>
          </div>

          <div className="flex flex-1 flex-col gap-1 text-xs">
            {isLoading ? (
              <div className="flex flex-1 items-center justify-center py-10">
                <Loader2 className="h-5 w-5 animate-spin text-zinc-400" />
              </div>
            ) : items.length === 0 ? (
              <div
                onClick={(event) => handleOpenContextMenu(event, null)}
                className="flex flex-1 flex-col items-center justify-center py-10 px-3 text-center text-xs text-zinc-400 border border-dashed border-zinc-800 rounded-lg cursor-pointer hover:border-zinc-700 transition"
              >
                <Calendar className="h-7 w-7 mb-2 text-zinc-500" />
                <p className="font-medium text-zinc-300">Roteiro vazio</p>
                <p className="text-[11px] text-zinc-500 mt-1">
                  Arraste músicas, imagens, vídeos ou arquivos para cá, ou clique com o botão direito para adicionar
                </p>
              </div>
            ) : (
              items.map((item, index) => (
                <LiturgyItemCard
                  key={item.id}
                  item={item}
                  index={index}
                  isLive={isItemLive(item)}
                  isSelected={selectedLiturgyItemId === item.id}
                  isDragging={draggedIndex === index}
                  onSelect={(selected) => onSelectItem?.(selected)}
                  onProject={(projected) => onProjectItem?.(projected)}
                  onContextMenu={handleOpenContextMenu}
                  onDragStart={handleDragStart}
                  onDragOver={handleDragOver}
                  onDrop={(event, dropIdx) => {
                    event.stopPropagation()
                    handleDrop(event, dropIdx)
                  }}
                />
              ))
            )}
          </div>
        </div>
      )}

      {/* ABA 1B: LETRAS & BÍBLIA RÁPIDA (Unificada na Coluna Esquerda) */}
      {activeTab === 'LETRAS' && (
        <div className="flex flex-1 flex-col overflow-hidden">
          <div className="mb-2 flex items-center justify-between">
            <span className="text-[11px] font-semibold uppercase tracking-wider text-zinc-400">
              Letras ({songs.length})
            </span>
            <div className="flex items-center gap-1">
              <Button
                variant="secondary"
                size="sm"
                onClick={onOpenSongImporter}
                title="Importar ou cadastrar música"
              >
                <Upload className="h-3 w-3 text-zinc-400" strokeWidth={1.75} />
                <span>Importar</span>
              </Button>
              <Button
                variant="secondary"
                size="sm"
                onClick={onOpenSongHistory}
                title="Consultar histórico e músicas mais cantadas"
              >
                <History className="h-3 w-3 text-zinc-400" strokeWidth={1.75} />
                <span>Histórico</span>
              </Button>
            </div>
          </div>

          <div className="relative mb-2.5">
            <Search className="absolute left-2.5 top-2.5 h-3.5 w-3.5 text-zinc-400" />
            <Input
              placeholder="Filtrar hino ou versículo (ex: jo 3 16)..."
              value={songQuery}
              onChange={(event) => onChangeSongQuery?.(event.target.value)}
              className="pl-8 pr-8 bg-zinc-900 border-zinc-800"
            />
            {isLoadingSongs && (
              <Loader2 className="absolute right-2.5 top-2.5 h-3.5 w-3.5 animate-spin text-zinc-400" />
            )}
          </div>

          <div className="flex flex-1 flex-col gap-1.5 overflow-y-auto text-xs pr-0.5">
            {bibleLookupResult && onSelectBible && (
              <BibleQuickLookupCard lookup={bibleLookupResult} onProject={onSelectBible} />
            )}
            <SongSearchList
              songs={songs}
              isLoading={isLoadingSongs}
              selectedSongId={selectedSongId}
              liveSongId={state.mode === 'SONG' ? state.activeItemId : null}
              onSelectSong={(songId) => onSelectSong?.(songId)}
              onProjectSong={onProjectSong}
            />
          </div>
        </div>
      )}

      {/* ABA 2: IMAGENS */}
      {activeTab === 'IMAGENS' && (
        <div className="flex flex-1 flex-col gap-2">
          <div className="flex items-center justify-between text-[11px] text-zinc-400">
            <span className="font-semibold uppercase tracking-wider">Imagens</span>
            <span className="text-[10px] text-zinc-500">Arraste para o Roteiro</span>
          </div>
          <MediaGridPanel
            filterType="IMAGE"
            activeBackground={state.background}
            onProjectMedia={onProjectMedia}
            onSelectBackground={(background) => onSelectBackground?.(background)}
          />
        </div>
      )}

      {/* ABA 3: VÍDEOS */}
      {activeTab === 'VIDEOS' && (
        <div className="flex flex-1 flex-col gap-2">
          <div className="flex items-center justify-between text-[11px] text-zinc-400">
            <span className="font-semibold uppercase tracking-wider">Vídeos & Loops</span>
            <span className="text-[10px] text-zinc-500">Arraste para o Roteiro</span>
          </div>
          <MediaGridPanel
            filterType="VIDEO"
            activeBackground={state.background}
            onProjectMedia={onProjectMedia}
            onSelectBackground={(background) => onSelectBackground?.(background)}
          />
        </div>
      )}

      {/* ABA 3B: ÁUDIO */}
      {activeTab === 'AUDIO' && (
        <div className="flex flex-1 flex-col gap-2 overflow-hidden">
          <div className="flex items-center justify-between text-[11px] text-zinc-400">
            <span className="font-semibold uppercase tracking-wider">Biblioteca de Áudio</span>
            <span className="text-[10px] text-zinc-500">Arraste para o Roteiro</span>
          </div>
          <MediaGridPanel
            filterType="AUDIO"
            activeBackground={state.background}
            onProjectMedia={onProjectMedia}
            onSelectBackground={(background) => onSelectBackground?.(background)}
          />
        </div>
      )}

      {/* ABA 4: ARQUIVOS (PPTX / PDF) */}
      {activeTab === 'ARQUIVOS' && (
        <div className="flex flex-1 flex-col gap-2 overflow-hidden">
          <div className="flex items-center justify-between text-[11px] text-zinc-400">
            <span className="font-semibold uppercase tracking-wider">Apresentações & PDFs</span>
            <span className="text-[10px] text-zinc-500">Arraste para o Roteiro</span>
          </div>
          <PptxFilesPanel
            onSelectDeck={onSelectPptxDeck}
            onAddToLiturgy={(deck) => {
              handleAddCustomItem({
                id: `item-pptx-${Date.now()}`,
                type: 'PPTX',
                title: deck.title,
                fileSrc: deck.id,
                slidesCount: deck.slides.length,
              })
              setActiveTab('ROTEIRO')
            }}
          />
        </div>
      )}

      {/* Menu de Contexto (Botão Direito) */}
      {contextMenu.isOpen && (
        <LiturgyContextMenu
          posX={contextMenu.posX}
          posY={contextMenu.posY}
          targetItem={contextMenu.item}
          hasSelectedSong={Boolean(selectedSong)}
          hasSelectedBible={Boolean(activeBible)}
          onClose={handleCloseContextMenu}
          onProjectItem={(item) => (onProjectItem ? onProjectItem(item) : onSelectItem?.(item))}
          onDuplicateItem={handleDuplicateItem}
          onMoveItemUp={handleMoveItemUp}
          onMoveItemDown={handleMoveItemDown}
          onDeleteItem={handleRemoveItem}
          onAddSelectedSong={handleAddSelectedSongToLiturgy}
          onAddSelectedBible={handleAddSelectedBibleToLiturgy}
          onOpenQuickText={() => setIsQuickTextOpen(true)}
          onAddCountdown={handleAddCountdown}
          onAddNote={handleAddNote}
          onAddMediaTab={() => setActiveTab('IMAGENS')}
          onAddFileTab={() => setActiveTab('ARQUIVOS')}
        />
      )}

      {/* Modal de Quick Text / Aviso */}
      <QuickTextModal
        isOpen={isQuickTextOpen}
        onClose={() => setIsQuickTextOpen(false)}
      />
    </section>
  )
}
