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
