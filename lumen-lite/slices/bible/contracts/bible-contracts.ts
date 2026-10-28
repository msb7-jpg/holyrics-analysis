import { z } from 'zod'

export const BibleReferenceSchema = z.object({
  book: z.string(),
  bookCode: z.string(),
  chapter: z.number().int().positive(),
  verseStart: z.number().int().positive(),
  verseEnd: z.number().int().positive().optional(),
})

export type BibleReference = z.infer<typeof BibleReferenceSchema>

export interface ParsedVerseRef {
  raw: string
  normalized: string
  book: string
  chapter: number
  verseStart: number
  verseEnd?: number
}
