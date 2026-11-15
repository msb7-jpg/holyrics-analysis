/**
 * @module @slices/liturgy/contracts/liturgy-schema
 * @description Esquemas Zod v4 e contratos canônicos para roteiro litúrgico (`culto.json`).
 */

import { z } from 'zod'
import { HItemSchema, type HItem } from '@slices/hitems'

/**
 * Re-exportação do esquema polimórfico de item litúrgico.
 */
export const LiturgyItemSchema = HItemSchema
export type LiturgyItem = HItem

/**
 * Tema visual de sobreposição e tipografia da liturgia.
 */
export const LiturgyThemeSchema = z.object({
  defaultBackground: z.string().optional(),
  fontFamily: z.string().default('Inter'),
  textColor: z.string().default('#FFFFFF'),
  textShadow: z.string().optional(),
})
export type LiturgyTheme = z.infer<typeof LiturgyThemeSchema>

/**
 * Esquema completo do arquivo declarativo de culto (`culto.json`).
 */
export const LiturgyFileSchema = z.object({
  $schema: z.string().optional(),
  id: z.string(),
  title: z.string(),
  date: z.string(),
  theme: LiturgyThemeSchema.optional(),
  items: z.array(LiturgyItemSchema),
  hooksEnabled: z.boolean().default(true),
})

export type LiturgyFile = z.infer<typeof LiturgyFileSchema>

/**
 * Schema para requisições de salvamento / atualização de itens na liturgia.
 */
export const SaveLiturgyPayloadSchema = LiturgyFileSchema

export type SaveLiturgyPayload = z.infer<typeof SaveLiturgyPayloadSchema>

/**
 * Schema para reordenação de itens por Drag & Drop.
 */
export const ReorderLiturgyPayloadSchema = z.object({
  itemIds: z.array(z.string()),
})

export type ReorderLiturgyPayload = z.infer<typeof ReorderLiturgyPayloadSchema>

/**
 * Schema para projeção rápida de texto avulso (RF-22A: Quick Text).
 */
export const QuickTextPayloadSchema = z.object({
  title: z.string().default('Aviso Rápido'),
  text: z.string().min(1, 'O texto da projeção não pode ser vazio'),
  category: z.enum(['ANNOUNCEMENT', 'PASTORAL_NOTE', 'ALERT', 'FREE_TEXT']).default('FREE_TEXT'),
  broadcastOnly: z.boolean().default(false),
})

export type QuickTextPayload = z.infer<typeof QuickTextPayloadSchema>
