/**
 * @module @slices/hitems/contracts/hitem-contracts
 * @description Contratos polimórficos de itens de apresentação litúrgica (HItems).
 *
 * ## Responsabilidades:
 * - Modelar tipos de itens projetáveis suportados pela liturgia (Música, Versículo, Mídia, Apresentação PPTX, Texto Avulso, Contagem).
 * - Prover Schemas Zod v4 discriminados para validação isomórfica (UI e Daemon).
 * - Padronizar metadados de apresentação e badges de exibição.
 */

import { z } from 'zod'

/**
 * Tipos canônicos de itens polimórficos no roteiro litúrgico.
 */
export const HItemTypeSchema = z.enum([
  'SONG',
  'BIBLE',
  'MEDIA',
  'PPTX',
  'TEXT',
  'COUNTDOWN',
])

export type HItemType = z.infer<typeof HItemTypeSchema>

/**
 * Item litúrgico de Canção / Louvor.
 */
export const HItemSongSchema = z.object({
  id: z.string(),
  type: z.literal('SONG'),
  title: z.string(),
  songId: z.string(),
  artist: z.string().optional(),
  arrangement: z.array(z.string()).optional(),
  backgroundOverride: z.string().optional(),
})
export type HItemSong = z.infer<typeof HItemSongSchema>

/**
 * Item litúrgico de Leitura Bíblica.
 */
export const HItemBibleSchema = z.object({
  id: z.string(),
  type: z.literal('BIBLE'),
  title: z.string(),
  reference: z.string(),
  version: z.string().default('ACF'),
  dualVersion: z.string().optional(),
})
export type HItemBible = z.infer<typeof HItemBibleSchema>

/**
 * Item litúrgico de Mídia (Imagem, Vídeo de Fundo ou Áudio).
 */
export const HItemMediaSchema = z.object({
  id: z.string(),
  type: z.literal('MEDIA'),
  title: z.string(),
  mediaSrc: z.string(),
  mediaType: z.enum(['IMAGE', 'VIDEO', 'AUDIO']).default('IMAGE'),
  autoAdvance: z.boolean().optional(),
  durationSeconds: z.number().optional(),
})
export type HItemMedia = z.infer<typeof HItemMediaSchema>

/**
 * Item litúrgico de Apresentação PPTX ou PDF.
 */
export const HItemPptxSchema = z.object({
  id: z.string(),
  type: z.literal('PPTX'),
  title: z.string(),
  fileSrc: z.string(),
  slidesCount: z.number().optional(),
})
export type HItemPptx = z.infer<typeof HItemPptxSchema>

/**
 * Item litúrgico de Texto Avulso ou Anotação Pastoral (Quick Text).
 */
export const HItemTextSchema = z.object({
  id: z.string(),
  type: z.literal('TEXT'),
  title: z.string(),
  content: z.array(z.string()),
  category: z.enum(['ANNOUNCEMENT', 'PASTORAL_NOTE', 'ALERT', 'FREE_TEXT']).default('ANNOUNCEMENT'),
})
export type HItemText = z.infer<typeof HItemTextSchema>

/**
 * Item litúrgico de Contagem Regressiva / Cronômetro.
 */
export const HItemCountdownSchema = z.object({
  id: z.string(),
  type: z.literal('COUNTDOWN'),
  title: z.string(),
  targetDurationSeconds: z.number().default(300),
  targetTime: z.string().optional(),
  description: z.string().optional(),
})
export type HItemCountdown = z.infer<typeof HItemCountdownSchema>

/**
 * União discriminada completa de itens litúrgicos polimórficos.
 */
export const HItemSchema = z.discriminatedUnion('type', [
  HItemSongSchema,
  HItemBibleSchema,
  HItemMediaSchema,
  HItemPptxSchema,
  HItemTextSchema,
  HItemCountdownSchema,
])

export type HItem = z.infer<typeof HItemSchema>
