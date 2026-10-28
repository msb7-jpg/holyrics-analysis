import type { MasterMode, BackgroundLayer, ForegroundLayer } from './screen-state'

export type ActionCommand =
  | { type: 'NAV_NEXT' }
  | { type: 'NAV_PREV' }
  | { type: 'NAV_GOTO'; slideIndex: number }
  | { type: 'SET_PANIC'; mask: Partial<{ blackout: boolean; clearText: boolean; clearBg: boolean; freeze: boolean }> }
  | { type: 'TOGGLE_PANIC'; target: 'blackout' | 'clearText' | 'clearBg' | 'freeze' }
  | { type: 'CLEAR_PANIC' }
  | {
      type: 'PRESENT_ITEM'
      itemId: string
      mode: MasterMode
      foreground: ForegroundLayer
      background?: BackgroundLayer
    }
  | { type: 'SET_BACKGROUND'; background: BackgroundLayer }
  | { type: 'CLEAR_SCREEN' }
