import type { PanicMask } from '@lumen/core'

export interface NavigationStatePayload {
  activeSlide: number
  totalSlides: number
  canNext: boolean
  canPrev: boolean
  panic: PanicMask
}

export type KeyAction =
  | 'nav next'
  | 'nav prev'
  | 'nav return'
  | 'panic blackout'
  | 'panic clear-text'
  | 'panic logo'
  | 'panic freeze'
  | 'quick bible'
  | 'quick song'
