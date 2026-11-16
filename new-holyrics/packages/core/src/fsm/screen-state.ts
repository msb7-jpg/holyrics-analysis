import { z } from 'zod'

export type MasterMode = 'SONG' | 'BIBLE' | 'PPTX' | 'MEDIA' | 'EMPTY'

export interface PanicMask {
  blackout: boolean
  clearText: boolean
  clearBg: boolean
  freeze: boolean
}

export interface BackgroundLayer {
  type: 'color' | 'image' | 'video'
  source: string
  muted?: boolean
  loop?: boolean
}

/**
 * Representa um slide individual dentro de uma sequência apresentável na FSM.
 */
export const SlideItemSchema = z.object({
  content: z.array(z.string()),
  secondaryContent: z.array(z.string()).optional(),
  reference: z.string().optional(),
  secondaryReference: z.string().optional(),
  speakerNotes: z.string().optional(),
  imageUrl: z.string().optional(),
})

export type SlideItem = z.infer<typeof SlideItemSchema>

export const ForegroundLayerSchema = z.object({
  title: z.string().optional(),
  reference: z.string().optional(),
  secondaryReference: z.string().optional(),
  content: z.array(z.string()),
  secondaryContent: z.array(z.string()).optional(),
  nextSlideContent: z.array(z.string()).optional(),
  speakerNotes: z.string().optional(),
  imageUrl: z.string().optional(),
  slides: z.array(SlideItemSchema).optional(),
  activeLineIndex: z.number().optional(),
  slideIndex: z.number(),
  totalSlides: z.number(),
})

export interface ForegroundLayer extends z.infer<typeof ForegroundLayerSchema> {
  title?: string
  reference?: string
  secondaryReference?: string
  content: string[]
  secondaryContent?: string[]
  nextSlideContent?: string[]
  speakerNotes?: string
  imageUrl?: string
  slides?: SlideItem[]
  activeLineIndex?: number
  slideIndex: number
  totalSlides: number
}

export interface ScreenState {
  version: number
  timestamp: number
  projectedAt?: number | null
  mode: MasterMode
  panic: PanicMask
  background: BackgroundLayer
  foreground: ForegroundLayer
  activeItemId: string | null
}

export const INITIAL_SCREEN_STATE: ScreenState = {
  version: 1,
  timestamp: Date.now(),
  projectedAt: null,
  mode: 'EMPTY',
  panic: {
    blackout: false,
    clearText: false,
    clearBg: false,
    freeze: false,
  },
  background: {
    type: 'color',
    source: '#0F172A', // Slate 900
  },
  foreground: {
    content: [],
    slideIndex: 0,
    totalSlides: 0,
  },
  activeItemId: null,
}
