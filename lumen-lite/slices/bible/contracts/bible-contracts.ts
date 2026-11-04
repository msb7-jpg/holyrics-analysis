/**
 * @module @slices/bible/contracts
 * @description Contratos de dados e schemas Zod v4 para o motor bíblico do LumenLite.
 */

import { z } from 'zod'

export const BibleReferenceSchema = z.object({
  book: z.string(),
  bookCode: z.string().optional(),
  chapter: z.number().int().positive(),
  verseStart: z.number().int().positive(),
  verseEnd: z.number().int().positive().optional(),
})

export type BibleReference = z.infer<typeof BibleReferenceSchema>

export interface ParsedVerseRef {
  raw: string
  normalized: string
  book: string
  bookNumber?: number
  chapter: number
  verseStart: number
  verseEnd?: number
}

export const BibleVerseSchema = z.object({
  id: z.string(),
  bibleId: z.string(),
  bookNumber: z.number().int().positive(),
  bookName: z.string(),
  bookAbbrev: z.string(),
  chapter: z.number().int().positive(),
  verse: z.number().int().positive(),
  text: z.string(),
})

export type BibleVerse = z.infer<typeof BibleVerseSchema>

export const BibleSearchResultSchema = z.object({
  id: z.string(),
  bibleId: z.string(),
  reference: z.string(),
  bookName: z.string(),
  chapter: z.number().int().positive(),
  verse: z.number().int().positive(),
  text: z.string(),
  snippet: z.string().optional(),
})

export type BibleSearchResult = z.infer<typeof BibleSearchResultSchema>

export const BibleVersionSummarySchema = z.object({
  id: z.string(),
  name: z.string(),
  abbrev: z.string(),
  language: z.string(),
  verseCount: z.number().int(),
})

export type BibleVersionSummary = z.infer<typeof BibleVersionSummarySchema>

export const BibleLookupResponseSchema = z.object({
  reference: z.string(),
  book: z.string(),
  chapter: z.number().int().positive(),
  verseStart: z.number().int().positive(),
  verseEnd: z.number().int().positive().optional(),
  version: z.string(),
  verses: z.array(BibleVerseSchema),
})

export type BibleLookupResponse = z.infer<typeof BibleLookupResponseSchema>

export interface ExtractedBibleBook {
  number: number
  name: string
  abbreviation: string
  testament: string
  testamentAbbreviation: string
}

export interface ExtractedBibleVerse {
  bookNumber: number
  chapter: number
  verse: number
  text: string
}

export interface ExtractedBibleData {
  language: string
  version: string
  books: ExtractedBibleBook[]
  verses: ExtractedBibleVerse[]
}
