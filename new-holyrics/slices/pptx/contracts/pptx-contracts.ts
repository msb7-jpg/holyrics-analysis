/**
 * @module @slices/pptx/contracts/pptx-contracts
 * @description Contratos Zod v4 para Ingestão Nativa de Apresentações PowerPoint (`.pptx`) e PDF (`.pdf`) (RF-21).
 */

import { z } from 'zod'

/**
 * Representa uma lâmina (slide) extraída de uma apresentação PPTX ou PDF, incluindo anotações do orador.
 */
export const PptxSlideSchema = z.object({
  index: z.number().int().min(0),
  title: z.string(),
  textLines: z.array(z.string()),
  speakerNotes: z.string().default(''),
  imageUrl: z.string().optional(),
})

export type PptxSlide = z.infer<typeof PptxSlideSchema>

/**
 * Apresentação completa (deck de slides) persistida no LumenLite.
 */
export const PptxPresentationSchema = z.object({
  id: z.string(),
  title: z.string(),
  filename: z.string(),
  folder: z.string().optional(),
  format: z.enum(['PPTX', 'PDF']).default('PPTX'),
  slides: z.array(PptxSlideSchema),
  createdAt: z.number(),
})

export type PptxPresentation = z.infer<typeof PptxPresentationSchema>
