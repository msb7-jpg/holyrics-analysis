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

import { z } from 'zod'

export const ForegroundLayerSchema = z.object({
  title: z.string().optional(),
  reference: z.string().optional(),
  content: z.array(z.string()),
  activeLineIndex: z.number().optional(),
  slideIndex: z.number(),
  totalSlides: z.number(),
})

export interface ForegroundLayer extends z.infer<typeof ForegroundLayerSchema> {
  title?: string
  reference?: string
  content: string[]
  activeLineIndex?: number
  slideIndex: number
  totalSlides: number
}

export interface ScreenState {
  version: number
  timestamp: number
  mode: MasterMode
  panic: PanicMask
  background: BackgroundLayer
  foreground: ForegroundLayer
  activeItemId: string | null
}

export const INITIAL_SCREEN_STATE: ScreenState = {
  version: 1,
  timestamp: Date.now(),
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
