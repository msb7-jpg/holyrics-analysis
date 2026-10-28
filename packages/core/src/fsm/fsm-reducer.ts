import type { ScreenState } from './screen-state'
import type { ActionCommand } from './commands'

/**
 * Reducer puro da Máquina de Estados Finita (FSM).
 * Determinístico, imutável e sem efeitos colaterais.
 */
export function fsmReducer(state: ScreenState, action: ActionCommand): ScreenState {
  const timestamp = Date.now()
  const version = state.version + 1

  switch (action.type) {
    case 'NAV_NEXT': {
      if (state.foreground.totalSlides <= 0) return state
      const nextSlide = Math.min(
        state.foreground.slideIndex + 1,
        state.foreground.totalSlides - 1
      )
      if (nextSlide === state.foreground.slideIndex) return state

      return {
        ...state,
        version,
        timestamp,
        foreground: {
          ...state.foreground,
          slideIndex: nextSlide,
        },
      }
    }

    case 'NAV_PREV': {
      if (state.foreground.totalSlides <= 0) return state
      const prevSlide = Math.max(state.foreground.slideIndex - 1, 0)
      if (prevSlide === state.foreground.slideIndex) return state

      return {
        ...state,
        version,
        timestamp,
        foreground: {
          ...state.foreground,
          slideIndex: prevSlide,
        },
      }
    }

    case 'NAV_GOTO': {
      const target = Math.max(
        0,
        Math.min(action.slideIndex, Math.max(0, state.foreground.totalSlides - 1))
      )
      if (target === state.foreground.slideIndex) return state

      return {
        ...state,
        version,
        timestamp,
        foreground: {
          ...state.foreground,
          slideIndex: target,
        },
      }
    }

    case 'TOGGLE_PANIC': {
      return {
        ...state,
        version,
        timestamp,
        panic: {
          ...state.panic,
          [action.target]: !state.panic[action.target],
        },
      }
    }

    case 'SET_PANIC': {
      return {
        ...state,
        version,
        timestamp,
        panic: {
          ...state.panic,
          ...action.mask,
        },
      }
    }

    case 'CLEAR_PANIC': {
      return {
        ...state,
        version,
        timestamp,
        panic: {
          blackout: false,
          clearText: false,
          clearBg: false,
          freeze: false,
        },
      }
    }

    case 'PRESENT_ITEM': {
      return {
        ...state,
        version,
        timestamp,
        mode: action.mode,
        activeItemId: action.itemId,
        foreground: action.foreground,
        background: action.background ?? state.background,
        panic: {
          ...state.panic,
          blackout: false,
          clearText: false,
        },
      }
    }

    case 'SET_BACKGROUND': {
      return {
        ...state,
        version,
        timestamp,
        background: action.background,
      }
    }

    case 'CLEAR_SCREEN': {
      return {
        ...state,
        version,
        timestamp,
        mode: 'EMPTY',
        activeItemId: null,
        foreground: {
          content: [],
          slideIndex: 0,
          totalSlides: 0,
        },
      }
    }

    default:
      return state
  }
}
