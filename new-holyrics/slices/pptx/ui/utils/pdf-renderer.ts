/**
 * @module @slices/pptx/ui/utils/pdf-renderer
 * @description Renderizador visual de páginas PDF em alta resolução via pdfjs-dist para projeção fiel no telão.
 */

import * as pdfjsLib from 'pdfjs-dist'
import type { PptxPresentation, PptxSlide } from '../../contracts/pptx-contracts'

pdfjsLib.GlobalWorkerOptions.workerSrc = new URL(
  'pdfjs-dist/build/pdf.worker.min.mjs',
  import.meta.url,
).toString()

/**
 * Converte um arquivo `.pdf` em uma `PptxPresentation` com imagens renderizadas de cada página.
 *
 * @param file - Arquivo PDF selecionado pelo usuário.
 * @param folder - Pasta opcional de destino.
 * @returns Apresentação pronta para persistência e projeção fiel.
 */
export async function renderPdfFileToPresentation(
  file: File,
  folder?: string,
): Promise<PptxPresentation> {
  const arrayBuffer = await file.arrayBuffer()
  const loadingTask = pdfjsLib.getDocument({ data: new Uint8Array(arrayBuffer) })
  const pdf = await loadingTask.promise
  const cleanTitle = file.name.replace(/\.pdf$/i, '') || 'Apresentação PDF'
  const slides: PptxSlide[] = []

  for (let pageNum = 1; pageNum <= pdf.numPages; pageNum++) {
    const page = await pdf.getPage(pageNum)
    const baseViewport = page.getViewport({ scale: 1 })
    const targetScale = Math.min(2.0, Math.max(1.0, 1280 / baseViewport.width))
    const viewport = page.getViewport({ scale: targetScale })

    const canvas = document.createElement('canvas')
    canvas.width = Math.round(viewport.width)
    canvas.height = Math.round(viewport.height)
    const context = canvas.getContext('2d')

    let imageUrl: string | undefined
    if (context) {
      await page.render({ canvasContext: context, viewport, canvas }).promise
      imageUrl = canvas.toDataURL('image/jpeg', 0.85)
    }

    let textLines: string[] = [`Página ${pageNum}`]
    try {
      const textContent = await page.getTextContent()
      const extracted = textContent.items
        .map((item) => ('str' in item ? item.str : ''))
        .join(' ')
        .trim()
      if (extracted) {
        textLines = [extracted.slice(0, 180)]
      }
    } catch {
      // Mantém fallback de página
    }

    slides.push({
      index: pageNum - 1,
      title: `${cleanTitle} - Pág. ${pageNum}`,
      textLines,
      speakerNotes: '',
      imageUrl,
    })
  }

  return {
    id: `pdf_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`,
    title: cleanTitle,
    filename: file.name,
    folder: folder || undefined,
    format: 'PDF',
    slides,
    createdAt: Date.now(),
  }
}
