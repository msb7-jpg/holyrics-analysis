import type { ForegroundLayer, ScreenState } from './screen-state'
import type { ActionCommand } from './commands'

/**
 * Resolve os campos derivados de um slide específico quando o array `slides` está presente.
 *
 * @param foreground - Camada foreground atual.
 * @param targetIndex - Índice do slide alvo.
 * @returns Nova camada foreground sincronizada com o slide alvo e preview do próximo slide.
 */
function resolveForegroundSlide(foreground: ForegroundLayer, targetIndex: number): ForegroundLayer {
  const slides = foreground.slides
  if (!slides || slides.length === 0 || !slides[targetIndex]) {
    return {
      ...foreground,
      slideIndex: targetIndex,
    }
  }

  const currentSlide = slides[targetIndex]
  const nextSlide = slides[targetIndex + 1]

  return {
    ...foreground,
    slideIndex: targetIndex,
    content: currentSlide.content,
    secondaryContent: currentSlide.secondaryContent,
    reference: currentSlide.reference ?? foreground.reference,
    secondaryReference: currentSlide.secondaryReference ?? foreground.secondaryReference,
    speakerNotes: currentSlide.speakerNotes ?? foreground.speakerNotes,
    imageUrl: currentSlide.imageUrl,
    nextSlideContent: nextSlide ? nextSlide.content : undefined,
  }
}

/**
 * Reducer puro da Máquina de Estados Finita (FSM).
 * Determinístico, imutável e sem efeitos colaterais.
 *
 * @param state - Estado atual completo da tela (camadas, modo e máscaras de pânico).
 * @param action - Comando de ação despachado pelo operador, teclado ou rede.
 * @returns Novo estado imutável da tela após a aplicação do comando.
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
        foreground: resolveForegroundSlide(state.foreground, nextSlide),
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
        foreground: resolveForegroundSlide(state.foreground, prevSlide),
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
        foreground: resolveForegroundSlide(state.foreground, target),
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
      const normalizedForeground = resolveForegroundSlide(
        action.foreground,
        action.foreground.slideIndex
      )
      return {
        ...state,
        version,
        timestamp,
        projectedAt: timestamp,
        mode: action.mode,
        activeItemId: action.itemId,
        foreground: normalizedForeground,
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
        projectedAt: null,
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
