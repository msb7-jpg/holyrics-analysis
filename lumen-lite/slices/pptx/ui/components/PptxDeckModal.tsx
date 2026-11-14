/**
 * @module @slices/pptx/ui/components/PptxDeckModal
 * @description Modal de Ingestão Nativa e Projeção de Apresentações PowerPoint (`.pptx`) e PDF (RF-21).
 * Exibe as lâminas extraídas e as notas do orador (`speakerNotes`), sincronizando-as automaticamente com `/stage` e `/remote/preacher`.
 */

import { useState, useEffect } from 'react'
import { X, Presentation, Upload, FileText, Play, Trash2 } from 'lucide-react'
import type { ActionCommand } from '@lumen/core'
import { Button } from '@ui/components/ui/button'
import { Input } from '@ui/components/ui/input'
import { Badge } from '@ui/components/ui/badge'
import type { PptxPresentation } from '../../contracts/pptx-contracts'
import { pptxService } from '../services/pptx-service'

export interface PptxDeckModalProps {
  isOpen: boolean
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
export function PptxDeckModal({ isOpen, onClose, dispatch }: PptxDeckModalProps) {
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
      const arrayBuf = await file.arrayBuffer()
      const bytes = new Uint8Array(arrayBuf)
      let binaryStr = ''
      for (let idx = 0; idx < bytes.length; idx++) {
        binaryStr += String.fromCharCode(bytes[idx])
      }
      const fileBase64 = btoa(binaryStr)
      const created = await pptxService.ingestFile(file.name, fileBase64)
      setDecks((prev) => [created, ...prev])
      setSelectedDeck(created)
    } finally {
      setIsUploading(false)
    }
  }

  const handleProjectSlide = (deck: PptxPresentation, slideIndex: number) => {
    const slides = deck.slides.map((slide) => ({
      content: slide.textLines,
      speakerNotes: slide.speakerNotes,
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
        slides,
        slideIndex,
        totalSlides: slides.length,
      },
    })
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
      <div className="flex h-[85vh] w-full max-w-4xl flex-col rounded-xl border border-white/10 bg-slate-900 text-slate-100 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-800/60 px-5 py-3.5">
          <div className="flex items-center gap-2">
            <Presentation className="h-4 w-4 text-sky-400" />
            <h2 className="text-sm font-semibold text-white">
              Apresentações PowerPoint (.pptx) & PDF com Notas do Orador (RF-21)
            </h2>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="grid flex-1 grid-cols-12 overflow-hidden">
          {/* Sidebar: Lista de Apresentações + Upload */}
          <aside className="col-span-4 flex flex-col gap-3 border-r border-white/10 bg-slate-950/50 p-4 overflow-y-auto">
            <div className="flex flex-col gap-2 rounded-lg border border-dashed border-white/15 bg-slate-900 p-3 text-xs">
              <span className="flex items-center gap-1.5 font-semibold text-sky-400">
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
                      ? 'border-sky-500/50 bg-slate-800 text-white'
                      : 'border-white/5 bg-slate-900/60 text-slate-300 hover:bg-slate-800/50'
                  }`}
                >
                  <div className="truncate pr-2">
                    <p className="font-semibold truncate">{deck.title}</p>
                    <p className="text-[10px] text-slate-400">
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
                    <Trash2 className="h-3.5 w-3.5 text-slate-500" />
                  </Button>
                </div>
              ))}
            </div>
          </aside>

          {/* Painel Principal: Lâminas e Notas do Orador */}
          <section className="col-span-8 flex flex-col gap-3 p-4 overflow-y-auto">
            {selectedDeck ? (
              <>
                <div className="flex items-center justify-between border-b border-white/10 pb-3">
                  <div>
                    <h3 className="text-sm font-bold text-white">{selectedDeck.title}</h3>
                    <p className="text-xs text-slate-400">
                      Clique em qualquer slide para projetar no telão e enviar as anotações ao púlpito (/stage e /remote/preacher)
                    </p>
                  </div>
                  <Button size="sm" onClick={() => handleProjectSlide(selectedDeck, 0)}>
                    <Play className="h-3.5 w-3.5" />
                    <span>Iniciar Apresentação</span>
                  </Button>
                </div>

                <div className="grid grid-cols-1 gap-3">
                  {selectedDeck.slides.map((slide, idx) => (
                    <div
                      key={slide.index}
                      onClick={() => handleProjectSlide(selectedDeck, idx)}
                      className="flex cursor-pointer flex-col gap-2 rounded-xl border border-white/10 bg-slate-800/50 p-4 hover:border-sky-500/40 hover:bg-slate-800 transition"
                    >
                      <div className="flex items-center justify-between">
                        <Badge variant="default">SLIDE {idx + 1}</Badge>
                        <span className="text-[11px] font-mono text-sky-400">
                          Clique para Projetar
                        </span>
                      </div>

                      <div className="rounded-lg bg-slate-950/70 p-3 text-sm font-semibold text-white">
                        {slide.textLines.map((line, lineIdx) => (
                          <p key={lineIdx}>{line}</p>
                        ))}
                      </div>

                      {slide.speakerNotes && (
                        <div className="flex items-start gap-2 rounded-lg border border-amber-500/20 bg-amber-500/10 px-3 py-2 text-xs text-amber-200">
                          <FileText className="mt-0.5 h-3.5 w-3.5 shrink-0 text-amber-400" />
                          <div>
                            <span className="font-mono text-[10px] font-bold uppercase text-amber-400 block">
                              Nota do Orador (Visível no Palco e Celular do Pastor):
                            </span>
                            <span>{slide.speakerNotes}</span>
                          </div>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </>
            ) : (
              <div className="m-auto text-center text-xs text-slate-500">
                Selecione ou importe uma apresentação PPTX/PDF.
              </div>
            )}
          </section>
        </div>
      </div>
    </div>
  )
}
