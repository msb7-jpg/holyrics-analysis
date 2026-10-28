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

export interface ForegroundLayer {
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
