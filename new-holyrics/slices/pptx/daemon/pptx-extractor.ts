/**
 * @module @slices/pptx/daemon/pptx-extractor
 * @description Extrator nativo de arquivos PowerPoint (`.pptx`) e PDF sem dependência do Microsoft Office (RF-21).
 * Utiliza `fflate` para descompactação ZIP em memória e `fast-xml-parser` para leitura de `ppt/slides/slide*.xml`
 * e `ppt/notesSlides/notesSlide*.xml`.
 */

import { unzipSync, strFromU8 } from 'fflate'
import { XMLParser } from 'fast-xml-parser'
import type { PptxPresentation, PptxSlide } from '../contracts/pptx-contracts'

const xmlParser = new XMLParser({
  ignoreAttributes: false,
  removeNSPrefix: true,
  trimValues: false,
})

/**
 * Percorre recursivamente uma árvore XML parseada pelo `fast-xml-parser` coletando parágrafos (`p`) e nós de texto (`t`).
 *
 * @param node - Nó XML desserializado.
 * @returns Lista de linhas de texto extraídas na ordem do documento.
 */
export function collectXmlParagraphLines(node: unknown): string[] {
  const lines: string[] = []

  const extractTextRuns = (subNode: unknown): string => {
    if (subNode === null || subNode === undefined) return ''
    if (typeof subNode === 'string' || typeof subNode === 'number') {
      return String(subNode)
    }
    if (Array.isArray(subNode)) {
      return subNode.map(extractTextRuns).join('')
    }
    if (typeof subNode === 'object') {
      const record = subNode as Record<string, unknown>
      let acc = ''
      for (const [key, val] of Object.entries(record)) {
        if (key === 't') {
          if (typeof val === 'string' || typeof val === 'number') {
            acc += String(val)
          } else if (Array.isArray(val)) {
            acc += val.map(extractTextRuns).join('')
          } else if (val && typeof val === 'object' && '#text' in val) {
            acc += String((val as Record<string, unknown>)['#text'] ?? '')
          }
        } else if (!key.startsWith('@_')) {
          acc += extractTextRuns(val)
        }
      }
      return acc
    }
    return ''
  }

  const walkParagraphs = (current: unknown): void => {
    if (!current || typeof current !== 'object') return
    if (Array.isArray(current)) {
      for (const item of current) {
        walkParagraphs(item)
      }
      return
    }

    const record = current as Record<string, unknown>
    for (const [key, val] of Object.entries(record)) {
      if (key === 'p') {
        const paragraphs = Array.isArray(val) ? val : [val]
        for (const para of paragraphs) {
          const textLine = extractTextRuns(para).trim()
          if (textLine.length > 0) {
            lines.push(textLine)
          }
        }
      } else if (!key.startsWith('@_')) {
        walkParagraphs(val)
      }
    }
  }

  walkParagraphs(node)
  return lines
}

/**
 * Extrai todos os slides e anotações do orador (`notesSlides`) de um buffer `.pptx` em memória.
 *
 * @param zipBytes - Bytes do arquivo `.pptx`.
 * @param filename - Nome original do arquivo.
 * @returns Apresentação `PptxPresentation` estruturada.
 */
export function extractPptxFromBuffer(
  zipBytes: Uint8Array,
  filename = 'Apresentacao.pptx',
): PptxPresentation {
  const files = unzipSync(zipBytes)

  // Localiza todos os arquivos ppt/slides/slideN.xml ordenados numericamente
  const slideEntries: Array<{ slideNumber: number; path: string }> = []
  for (const filePath of Object.keys(files)) {
    const match = filePath.match(/^ppt\/slides\/slide(\d+)\.xml$/i)
    if (match) {
      slideEntries.push({
        slideNumber: parseInt(match[1], 10),
        path: filePath,
      })
    }
  }

  slideEntries.sort((first, second) => first.slideNumber - second.slideNumber)

  const slides: PptxSlide[] = slideEntries.map((entry, idx) => {
    const rawSlideXml = strFromU8(files[entry.path])
    const parsedSlide = xmlParser.parse(rawSlideXml)
    const textLines = collectXmlParagraphLines(parsedSlide)

    // Verifica se existe notesSlide correspondente em ppt/notesSlides/notesSlideN.xml
    const notesPath = `ppt/notesSlides/notesSlide${entry.slideNumber}.xml`
    let speakerNotes = ''
    if (files[notesPath]) {
      const rawNotesXml = strFromU8(files[notesPath])
      const parsedNotes = xmlParser.parse(rawNotesXml)
      const noteLines = collectXmlParagraphLines(parsedNotes).filter(
        (line) => !/^\d+$/.test(line.trim()),
      )
      speakerNotes = noteLines.join('\n')
    }

    const slideTitle = textLines[0] || `Slide ${idx + 1}`

    // Tenta extrair imagem associada ao slide via .rels quando disponível
    let imageUrl: string | undefined
    const relsPath = `ppt/slides/_rels/slide${entry.slideNumber}.xml.rels`
    if (files[relsPath]) {
      try {
        const relsXml = strFromU8(files[relsPath])
        const parsedRels = xmlParser.parse(relsXml) as {
          Relationships?: { Relationship?: Array<{ Target?: string; Type?: string }> | { Target?: string; Type?: string } }
        }
        const relsRaw = parsedRels.Relationships?.Relationship
        const relList = Array.isArray(relsRaw) ? relsRaw : relsRaw ? [relsRaw] : []
        const imgRel = relList.find(
          (r) => typeof r.Target === 'string' && /\.(png|jpe?g|webp|gif)$/i.test(r.Target),
        )
        if (imgRel?.Target) {
          const mediaPath = imgRel.Target.replace(/^\.\.\//, 'ppt/')
          const imgBytes = files[mediaPath]
          if (imgBytes && textLines.length === 0) {
            const ext = mediaPath.split('.').pop()?.toLowerCase() || 'png'
            const mime = ext === 'jpg' || ext === 'jpeg' ? 'image/jpeg' : `image/${ext}`
            imageUrl = `data:${mime};base64,${Buffer.from(imgBytes).toString('base64')}`
          }
        }
      } catch {
        // Ignora erro de parse de .rels
      }
    }

    return {
      index: idx,
      title: slideTitle,
      textLines: textLines.length > 0 ? textLines : [`Slide ${idx + 1}`],
      speakerNotes,
      imageUrl,
    }
  })

  const cleanTitle = filename.replace(/\.(pptx|ppt|pdf)$/i, '')

  return {
    id: `pptx_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`,
    title: cleanTitle || 'Apresentação Pastoral',
    filename,
    format: 'PPTX',
    slides:
      slides.length > 0
        ? slides
        : [
            {
              index: 0,
              title: cleanTitle,
              textLines: [cleanTitle],
              speakerNotes: '',
            },
          ],
    createdAt: Date.now(),
  }
}

/**
 * Extrai lâminas textuais de um documento PDF simples ou cria estrutura paginada para projeção.
 *
 * @param pdfBytes - Bytes do arquivo `.pdf`.
 * @param filename - Nome do arquivo PDF.
 * @returns Estrutura `PptxPresentation` com `format: 'PDF'`.
 */
export function extractPdfFromBuffer(
  pdfBytes: Uint8Array,
  filename = 'Esboco.pdf',
): PptxPresentation {
  const rawText = new TextDecoder('latin1').decode(pdfBytes)
  const textMatches = Array.from(rawText.matchAll(/\(([^()]{3,120})\)\s*Tj/g)).map((match) =>
    match[1].trim(),
  )

  const cleanTitle = filename.replace(/\.pdf$/i, '')
  const slides: PptxSlide[] =
    textMatches.length > 0
      ? textMatches.map((line, idx) => ({
          index: idx,
          title: `${cleanTitle} - Pág. ${idx + 1}`,
          textLines: [line],
          speakerNotes: '',
        }))
      : [
          {
            index: 0,
            title: cleanTitle,
            textLines: [cleanTitle],
            speakerNotes: '',
          },
        ]

  return {
    id: `pdf_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`,
    title: cleanTitle,
    filename,
    format: 'PDF',
    slides,
    createdAt: Date.now(),
  }
}
