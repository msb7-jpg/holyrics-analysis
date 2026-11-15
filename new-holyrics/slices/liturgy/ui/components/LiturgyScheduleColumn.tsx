/**
 * @module @slices/liturgy/ui/components/LiturgyScheduleColumn
 * @description Coluna 1 (Esquerda) do operador com abas de acesso rápido (Roteiro | Imagens | Vídeos | Arquivos), Drag & Drop universal e menu de contexto.
 */

import { useState, useEffect, useRef } from 'react'
import {
  Calendar,
  Plus,
  Loader2,
  Image as ImageIcon,
  Film,
  Presentation,
  Upload,
  Trash2,
} from 'lucide-react'
import type { ScreenState, BackgroundLayer } from '@lumen/core'
import type { Song } from '@slices/songs/contracts/song-contracts'
import type { BibleLookupResponse } from '@slices/bible/contracts/bible-contracts'
import type { PptxPresentation } from '@slices/pptx/contracts/pptx-contracts'
import { pptxService } from '@slices/pptx/ui/services/pptx-service'
import { MediaGridPanel } from '@slices/media/ui/components/MediaGridPanel'
import type { MediaItem } from '@slices/media/contracts/media-contracts'
import type { LiturgyItem } from '../../contracts/liturgy-schema'
import { LiturgyItemCard } from './LiturgyItemCard'
import { LiturgyContextMenu } from './LiturgyContextMenu'
import { QuickTextModal } from './QuickTextModal'
import { useLiturgyController } from '../hooks/useLiturgyController'

export type LeftColumnTab = 'ROTEIRO' | 'IMAGENS' | 'VIDEOS' | 'ARQUIVOS'

export interface LiturgyScheduleColumnProps {
  state: ScreenState
  selectedSong?: Song | null
  activeBible?: BibleLookupResponse | null
  selectedLiturgyItemId?: string | null
  onSelectItem?: (item: LiturgyItem) => void
  onSelectBackground?: (background: BackgroundLayer) => void
  onSelectPptxDeck?: (deck: PptxPresentation) => void
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
  onSelectBackground,
  onSelectPptxDeck,
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
  const [pptxDecks, setPptxDecks] = useState<PptxPresentation[]>([])
  const [isUploadingFile, setIsUploadingFile] = useState(false)
  const pptxInputRef = useRef<HTMLInputElement | null>(null)

  const loadPptxDecks = () => {
    pptxService
      .listAll()
      .then(setPptxDecks)
      .catch(() => setPptxDecks([]))
  }

  useEffect(() => {
    if (activeTab === 'ARQUIVOS') {
      loadPptxDecks()
    }
  }, [activeTab])

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

  const handleUploadPptxFiles = async (files: FileList | File[]) => {
    setIsUploadingFile(true)
    try {
      for (const file of Array.from(files)) {
        const arrayBuf = await file.arrayBuffer()
        const bytes = new Uint8Array(arrayBuf)
        let binaryStr = ''
        for (let index = 0; index < bytes.length; index++) {
          binaryStr += String.fromCharCode(bytes[index])
        }
        const fileBase64 = btoa(binaryStr)
        const created = await pptxService.ingestFile(file.name, fileBase64)
        setPptxDecks((previous) => [created, ...previous])
      }
    } finally {
      setIsUploadingFile(false)
    }
  }

  const handleOsFilesDropOnRoteiro = async (files: FileList) => {
    for (const file of Array.from(files)) {
      const lowerName = file.name.toLowerCase()
      if (lowerName.endsWith('.pptx') || lowerName.endsWith('.pdf')) {
        const arrayBuf = await file.arrayBuffer()
        const bytes = new Uint8Array(arrayBuf)
        let binaryStr = ''
        for (let index = 0; index < bytes.length; index++) {
          binaryStr += String.fromCharCode(bytes[index])
        }
        const created = await pptxService.ingestFile(file.name, btoa(binaryStr))
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
            mediaType: json.data.type === 'VIDEO' ? 'VIDEO' : 'IMAGE',
          })
        }
      }
    }
  }

  const items = liturgy?.items || []

  return (
    <section
      className="flex flex-col bg-[#0F1622] p-3 border-r border-white/5 overflow-y-auto relative select-none"
      onContextMenu={(event) => {
        if (activeTab === 'ROTEIRO') {
          handleOpenContextMenu(event, null)
        }
      }}
    >
      {/* Abas Superiores da Coluna 1: Roteiro | Imagens | Vídeos | Arquivos */}
      <div className="mb-3 grid grid-cols-4 gap-1 rounded-lg bg-[#090D14] p-1 text-[11px]">
        <button
          type="button"
          onClick={() => setActiveTab('ROTEIRO')}
          title="Ordem do Culto (arraste itens para cá)"
          className={`flex flex-col items-center justify-center gap-0.5 rounded-md py-1.5 font-medium transition ${
            activeTab === 'ROTEIRO'
              ? 'bg-[#192334] text-sky-400 shadow-xs'
              : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          <Calendar className="h-3.5 w-3.5" />
          <span className="text-[10px]">Roteiro</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('IMAGENS')}
          title="Galeria de Imagens (clique para projetar ou arraste ao roteiro)"
          className={`flex flex-col items-center justify-center gap-0.5 rounded-md py-1.5 font-medium transition ${
            activeTab === 'IMAGENS'
              ? 'bg-[#192334] text-sky-400 shadow-xs'
              : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          <ImageIcon className="h-3.5 w-3.5" />
          <span className="text-[10px]">Imagens</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('VIDEOS')}
          title="Galeria de Vídeos e Loops (clique para projetar ou arraste ao roteiro)"
          className={`flex flex-col items-center justify-center gap-0.5 rounded-md py-1.5 font-medium transition ${
            activeTab === 'VIDEOS'
              ? 'bg-[#192334] text-sky-400 shadow-xs'
              : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          <Film className="h-3.5 w-3.5" />
          <span className="text-[10px]">Vídeos</span>
        </button>

        <button
          type="button"
          onClick={() => setActiveTab('ARQUIVOS')}
          title="Apresentações e Arquivos (.pptx, .pdf)"
          className={`flex flex-col items-center justify-center gap-0.5 rounded-md py-1.5 font-medium transition ${
            activeTab === 'ARQUIVOS'
              ? 'bg-[#192334] text-sky-400 shadow-xs'
              : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          <Presentation className="h-3.5 w-3.5" />
          <span className="text-[10px]">Arquivos</span>
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
          <div className="mb-2.5 flex items-center justify-between text-xs font-semibold text-slate-300">
            <span className="text-[11px] uppercase tracking-wider text-slate-400">
              Ordem do Culto ({items.length})
            </span>
            <div className="flex items-center gap-1.5">
              <button
                type="button"
                onClick={() => setIsQuickTextOpen(true)}
                title="Exibir aviso rápido ou adicionar texto ao roteiro"
                className="flex items-center gap-1 rounded-md bg-sky-500/15 hover:bg-sky-500/25 border border-sky-500/30 px-2 py-0.5 text-[10px] font-medium text-sky-300 transition"
              >
                <Plus className="h-3 w-3" />
                <span>Adicionar</span>
              </button>
            </div>
          </div>

          <div className="flex flex-1 flex-col gap-1.5 text-xs">
            {isLoading ? (
              <div className="flex flex-1 items-center justify-center py-10">
                <Loader2 className="h-5 w-5 animate-spin text-sky-400" />
              </div>
            ) : items.length === 0 ? (
              <div
                onClick={(event) => handleOpenContextMenu(event, null)}
                className="flex flex-1 flex-col items-center justify-center py-10 px-3 text-center text-xs text-slate-400 border border-dashed border-white/10 rounded-lg cursor-pointer hover:border-sky-500/40 transition"
              >
                <Calendar className="h-7 w-7 mb-2 text-slate-500" />
                <p className="font-medium text-slate-300">Roteiro vazio</p>
                <p className="text-[11px] text-slate-500 mt-1">
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

      {/* ABA 2: IMAGENS */}
      {activeTab === 'IMAGENS' && (
        <div className="flex flex-1 flex-col gap-2">
          <div className="flex items-center justify-between text-[11px] text-slate-400">
            <span className="font-semibold uppercase tracking-wider">Imagens</span>
            <span className="text-[10px] text-slate-500">Arraste para o Roteiro</span>
          </div>
          <MediaGridPanel
            filterType="IMAGE"
            activeBackground={state.background}
            onSelectBackground={(background) => onSelectBackground?.(background)}
          />
        </div>
      )}

      {/* ABA 3: VÍDEOS */}
      {activeTab === 'VIDEOS' && (
        <div className="flex flex-1 flex-col gap-2">
          <div className="flex items-center justify-between text-[11px] text-slate-400">
            <span className="font-semibold uppercase tracking-wider">Vídeos & Loops</span>
            <span className="text-[10px] text-slate-500">Arraste para o Roteiro</span>
          </div>
          <MediaGridPanel
            filterType="VIDEO"
            activeBackground={state.background}
            onSelectBackground={(background) => onSelectBackground?.(background)}
          />
        </div>
      )}

      {/* ABA 4: ARQUIVOS (PPTX / PDF) */}
      {activeTab === 'ARQUIVOS' && (
        <div
          className="flex flex-1 flex-col gap-2"
          onDragOver={(event) => event.preventDefault()}
          onDrop={(event) => {
            if (event.dataTransfer.files && event.dataTransfer.files.length > 0) {
              event.preventDefault()
              handleUploadPptxFiles(event.dataTransfer.files)
            }
          }}
        >
          <div className="flex items-center justify-between text-[11px] text-slate-400">
            <span className="font-semibold uppercase tracking-wider">Apresentações & PDFs</span>
            <input
              ref={pptxInputRef}
              type="file"
              accept=".pptx,.pdf"
              multiple
              className="hidden"
              onChange={(event) => {
                if (event.target.files && event.target.files.length > 0) {
                  handleUploadPptxFiles(event.target.files)
                  event.target.value = ''
                }
              }}
            />
            <button
              type="button"
              disabled={isUploadingFile}
              onClick={() => pptxInputRef.current?.click()}
              className="flex items-center gap-1 rounded bg-sky-500/15 hover:bg-sky-500/25 border border-sky-500/30 px-2 py-0.5 text-[10px] font-medium text-sky-300 transition"
            >
              <Upload className="h-3 w-3" />
              <span>{isUploadingFile ? 'Enviando...' : 'Importar'}</span>
            </button>
          </div>

          {pptxDecks.length === 0 ? (
            <div
              onClick={() => pptxInputRef.current?.click()}
              className="flex flex-col items-center justify-center rounded-lg border border-dashed border-white/10 py-10 px-3 text-center cursor-pointer hover:border-sky-500/40 transition"
            >
              <Presentation className="h-6 w-6 text-slate-500 mb-1.5" />
              <p className="text-xs font-medium text-slate-300">Nenhum arquivo importado</p>
              <p className="text-[10px] text-slate-500 mt-0.5">
                Arraste arquivos .pptx ou .pdf aqui ou clique para importar
              </p>
            </div>
          ) : (
            <div className="flex flex-col gap-1.5">
              {pptxDecks.map((deck) => (
                <div
                  key={deck.id}
                  draggable
                  onDragStart={(event) => {
                    event.dataTransfer.setData(
                      'application/lumen-item',
                      JSON.stringify({
                        type: 'PPTX',
                        title: deck.title,
                        fileSrc: deck.id,
                        slidesCount: deck.slides.length,
                      }),
                    )
                  }}
                  onClick={() => onSelectPptxDeck?.(deck)}
                  className="group flex items-center justify-between rounded-[6px] bg-[#192334] hover:bg-[#1E2B40] p-2.5 cursor-pointer transition"
                >
                  <div className="min-w-0 pr-2">
                    <p className="text-xs font-medium text-slate-100 truncate">{deck.title}</p>
                    <p className="text-[10px] text-slate-400">
                      {deck.slides.length} slides • {deck.format}
                    </p>
                  </div>
                  <div className="flex items-center gap-1 shrink-0">
                    <button
                      type="button"
                      onClick={(event) => {
                        event.stopPropagation()
                        handleAddCustomItem({
                          id: `item-pptx-${Date.now()}`,
                          type: 'PPTX',
                          title: deck.title,
                          fileSrc: deck.id,
                          slidesCount: deck.slides.length,
                        })
                        setActiveTab('ROTEIRO')
                      }}
                      title="Adicionar ao Roteiro"
                      className="rounded bg-sky-500/15 hover:bg-sky-500/30 px-1.5 py-0.5 text-[10px] text-sky-300 transition"
                    >
                      + Roteiro
                    </button>
                    <button
                      type="button"
                      onClick={async (event) => {
                        event.stopPropagation()
                        const remaining = await pptxService.deletePresentation(deck.id)
                        setPptxDecks(remaining)
                      }}
                      title="Excluir apresentação"
                      className="rounded p-1 text-slate-500 hover:text-rose-400 transition"
                    >
                      <Trash2 className="h-3.5 w-3.5" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
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
          onProjectItem={(item) => onSelectItem?.(item)}
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
