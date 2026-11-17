/**
 * @module @slices/pptx/ui/components/PptxDeckModal
 * @description Modal de Ingestão Nativa e Projeção de Apresentações PowerPoint (`.pptx`) e PDF (RF-21).
 * Exibe as lâminas extraídas e as notas do orador (`speakerNotes`), sincronizando-as automaticamente com `/stage` e `/remote/preacher`.
 */

import { useState, useEffect } from 'react'
import { X, Presentation, Upload, FileText, Play, Trash2 } from 'lucide-react'
import type { ActionCommand, ScreenState } from '@lumen/core'
import { Button } from '@ui/components/ui/button'
import { Input } from '@ui/components/ui/input'
import { Badge } from '@ui/components/ui/badge'
import { SlidePreviewCard } from '@slices/navigation/ui/components/SlidePreviewCard'
import type { PptxPresentation } from '../../contracts/pptx-contracts'
import { pptxService } from '../services/pptx-service'
import { renderPdfFileToPresentation } from '../utils/pdf-renderer'

export interface PptxDeckModalProps {
  isOpen: boolean
  state?: ScreenState
  onClose: () => void
  dispatch: (command: ActionCommand) => void
}

/**
 * Modal de gerenciamento e projeção de apresentações PPTX/PDF com notas do orador (RF-21).
 *
 * @param props - Propriedades do modal.
 * @param props.isOpen - Indica se o modal está visível.
 * @param props.onClose - Callback de fechamento.
 * @param props.dispatch - Despachante de comandos da FSM.
 * @returns Elemento JSX do modal ou null.
 */
export function PptxDeckModal({ isOpen, state, onClose, dispatch }: PptxDeckModalProps) {
  const [decks, setDecks] = useState<PptxPresentation[]>([])
  const [selectedDeck, setSelectedDeck] = useState<PptxPresentation | null>(null)
  const [isUploading, setIsUploading] = useState(false)

  useEffect(() => {
    if (!isOpen) return
    pptxService
      .listAll()
      .then((loaded) => {
        setDecks(loaded)
        if (loaded.length > 0 && !selectedDeck) {
          setSelectedDeck(loaded[0])
        }
      })
      .catch(() => setDecks([]))
  }, [isOpen, selectedDeck])

  if (!isOpen) return null

  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    setIsUploading(true)
    try {
      let created: PptxPresentation
      if (file.name.toLowerCase().endsWith('.pdf')) {
        try {
          const rendered = await renderPdfFileToPresentation(file)
          created = await pptxService.ingestPresentation(rendered)
        } catch {
          const arrayBuf = await file.arrayBuffer()
          const bytes = new Uint8Array(arrayBuf)
          let binaryStr = ''
          for (let idx = 0; idx < bytes.length; idx++) {
            binaryStr += String.fromCharCode(bytes[idx])
          }
          created = await pptxService.ingestFile(file.name, btoa(binaryStr))
        }
      } else {
        const arrayBuf = await file.arrayBuffer()
        const bytes = new Uint8Array(arrayBuf)
        let binaryStr = ''
        for (let idx = 0; idx < bytes.length; idx++) {
          binaryStr += String.fromCharCode(bytes[idx])
        }
        created = await pptxService.ingestFile(file.name, btoa(binaryStr))
      }
      setDecks((prev) => [created, ...prev])
      setSelectedDeck(created)
    } finally {
      setIsUploading(false)
    }
  }

  const handleProjectSlide = (deck: PptxPresentation, slideIndex: number, closeAfter = false) => {
    const slides = deck.slides.map((slide) => ({
      content: slide.textLines,
      speakerNotes: slide.speakerNotes,
      imageUrl: slide.imageUrl,
      reference: `${deck.title} (${slide.index + 1}/${deck.slides.length})`,
    }))
    const targetSlide = slides[slideIndex] || slides[0]

    dispatch({
      type: 'PRESENT_ITEM',
      itemId: deck.id,
      mode: 'PPTX',
      foreground: {
        title: deck.title,
        reference: targetSlide.reference,
        content: targetSlide.content,
        speakerNotes: targetSlide.speakerNotes,
        imageUrl: targetSlide.imageUrl,
        slides,
        slideIndex,
        totalSlides: slides.length,
      },
    })

    if (closeAfter) {
      onClose()
    }
  }

  const handleDeleteDeck = async (id: string) => {
    const remaining = await pptxService.deletePresentation(id)
    setDecks(remaining)
    if (selectedDeck?.id === id) {
      setSelectedDeck(remaining[0] || null)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex h-[85vh] w-full max-w-4xl flex-col rounded-xl border border-zinc-800 bg-zinc-950 text-zinc-100 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between border-b border-zinc-800 bg-zinc-900 px-5 py-3.5">
          <div className="flex items-center gap-2">
            <Presentation className="h-4 w-4 text-zinc-400" />
            <h2 className="text-sm font-semibold text-white">
              Apresentações PowerPoint (.pptx) & PDF com Notas do Orador
            </h2>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="grid flex-1 grid-cols-12 overflow-hidden">
          {/* Sidebar: Lista de Apresentações + Upload */}
          <aside className="col-span-4 flex flex-col gap-3 border-r border-zinc-800 bg-zinc-950/70 p-4 overflow-y-auto">
            <div className="flex flex-col gap-2 rounded-lg border border-dashed border-zinc-800 bg-zinc-900 p-3 text-xs">
              <span className="flex items-center gap-1.5 font-semibold text-zinc-400">
                <Upload className="h-3.5 w-3.5" />
                <span>Importar .pptx ou .pdf</span>
              </span>
              <Input
                type="file"
                accept=".pptx,.pdf"
                disabled={isUploading}
                onChange={handleFileChange}
              />
            </div>

            <div className="flex flex-col gap-1.5">
              {decks.map((deck) => (
                <div
                  key={deck.id}
                  onClick={() => setSelectedDeck(deck)}
                  className={`flex cursor-pointer items-center justify-between rounded-lg border p-2.5 text-xs transition ${
                    selectedDeck?.id === deck.id
                      ? 'border-zinc-700 bg-zinc-800 ring-1 ring-zinc-600 text-white'
                      : 'border-zinc-800 bg-zinc-900 text-zinc-300 hover:bg-zinc-800'
                  }`}
                >
                  <div className="truncate pr-2">
                    <p className="font-semibold truncate">{deck.title}</p>
                    <p className="text-[10px] text-zinc-400">
                      {deck.slides.length} slides • {deck.format}
                    </p>
                  </div>
                  <Button
                    size="icon"
                    variant="ghost"
                    onClick={(event) => {
                      event.stopPropagation()
                      handleDeleteDeck(deck.id)
                    }}
                  >
                    <Trash2 className="h-3.5 w-3.5 text-zinc-500" />
                  </Button>
                </div>
              ))}
            </div>
          </aside>

          {/* Painel Principal: Lâminas e Notas do Orador */}
          <section className="col-span-8 flex flex-col gap-3 p-4 overflow-y-auto">
            {selectedDeck ? (
              <>
                <div className="flex items-center justify-between gap-3 border-b border-zinc-800 pb-3">
                  <div>
                    <h3 className="text-sm font-bold text-white">{selectedDeck.title}</h3>
                    <p className="text-xs text-zinc-400">
                      Clique em qualquer slide para projetar no telão
                    </p>
                  </div>
                  <Button
                    size="sm"
                    className="whitespace-nowrap shrink-0"
                    onClick={() => handleProjectSlide(selectedDeck, 0, true)}
                  >
                    <Play className="h-3.5 w-3.5" />
                    <span>Iniciar Apresentação</span>
                  </Button>
                </div>

                <div className="grid grid-cols-1 gap-3">
                  {selectedDeck.slides.map((slide, idx) => {
                    const isLiveSlide =
                      state?.mode === 'PPTX' &&
                      state.activeItemId === selectedDeck.id &&
                      state.foreground.slideIndex === idx
                    return (
                      <SlidePreviewCard
                        key={slide.index}
                        variant="detailed"
                        slideNumber={idx + 1}
                        lines={slide.textLines}
                        imageUrl={slide.imageUrl}
                        speakerNotes={slide.speakerNotes}
                        isLive={isLiveSlide}
                        onClick={() => handleProjectSlide(selectedDeck, idx)}
                      />
                    )
                  })}
                </div>
              </>
            ) : (
              <div className="m-auto text-center text-xs text-zinc-500">
                Selecione ou importe uma apresentação PPTX/PDF.
              </div>
            )}
          </section>
        </div>
      </div>
    </div>
  )
}
