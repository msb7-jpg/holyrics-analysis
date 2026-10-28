import { z } from 'zod'

export const LiturgyItemSchema = z.discriminatedUnion('type', [
  z.object({
    id: z.string(),
    type: z.literal('MEDIA'),
    title: z.string(),
    mediaSrc: z.string(),
    autoAdvance: z.boolean().optional(),
  }),
  z.object({
    id: z.string(),
    type: z.literal('SONG'),
    title: z.string(),
    songId: z.string(),
    arrangement: z.array(z.string()).optional(),
    backgroundOverride: z.string().optional(),
  }),
  z.object({
    id: z.string(),
    type: z.literal('BIBLE'),
    title: z.string(),
    reference: z.string(),
    version: z.string().default('NVI'),
    dualVersion: z.string().optional(),
  }),
  z.object({
    id: z.string(),
    type: z.literal('PPTX'),
    title: z.string(),
    fileSrc: z.string(),
  }),
  z.object({
    id: z.string(),
    type: z.literal('TEXT'),
    title: z.string(),
    content: z.array(z.string()),
  }),
])

export type LiturgyItem = z.infer<typeof LiturgyItemSchema>

export const LiturgyThemeSchema = z.object({
  defaultBackground: z.string().optional(),
  fontFamily: z.string().default('Inter'),
  textColor: z.string().default('#FFFFFF'),
  textShadow: z.string().optional(),
})

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
