import { z } from 'zod'

export const SongSectionSchema = z.object({
  id: z.string(),
  index: z.number().int().positive(), // 1, 2, 3... Leitura sóbria, sem labels intrusivos
  lines: z.array(z.string()),
  tags: z.array(z.string()).optional(),
})

export type SongSection = z.infer<typeof SongSectionSchema>

export const SongSchema = z.object({
  id: z.string(),
  title: z.string(),
  artist: z.string().optional(),
  bpm: z.number().optional(),
  timeSignature: z.string().optional(),
  sections: z.array(SongSectionSchema),
  defaultArrangement: z.array(z.string()).optional(), // Lista de Section IDs
})

export type Song = z.infer<typeof SongSchema>

export const SongSearchResultSchema = z.object({
  id: z.string(),
  title: z.string(),
  artist: z.string().optional(),
  snippet: z.string().optional(),
})

export type SongSearchResult = z.infer<typeof SongSearchResultSchema>

/**
 * Estrutura bruta de música legada exportada pelo Holyrics (`Music.java`).
 */
export const LegacyHolyricsSongSchema = z.object({
  id: z.union([z.number(), z.string()]).optional(),
  title: z.string(),
  artist: z.string().optional().default(''),
  lyrics: z.string(),
  formatting: z.string().optional().default(''),
  order: z.array(z.number()).optional().default([]),
  note: z.string().optional().default(''),
  language: z.string().optional().default('pt-BR'),
})

export type LegacyHolyricsSong = z.infer<typeof LegacyHolyricsSongSchema>

/**
 * Relatório de importação em lote de músicas do Holyrics (RF-11).
 */
export const SongImportReportSchema = z.object({
  importedCount: z.number(),
  skippedCount: z.number(),
  songs: z.array(SongSchema),
})

export type SongImportReport = z.infer<typeof SongImportReportSchema>

/**
 * Registro individual de histórico de execução de hino/música (RF-12 - `History.java`).
 */
export const SongHistoryEntrySchema = z.object({
  id: z.string(),
  songId: z.string(),
  title: z.string(),
  artist: z.string().optional(),
  playedAt: z.number(),
})

export type SongHistoryEntry = z.infer<typeof SongHistoryEntrySchema>

/**
 * Estatística agregada de frequência de uso de cada música no culto (RF-12).
 */
export const SongUsageStatSchema = z.object({
  songId: z.string(),
  title: z.string(),
  artist: z.string().optional(),
  playCount: z.number(),
  lastPlayedAt: z.number(),
})

export type SongUsageStat = z.infer<typeof SongUsageStatSchema>
