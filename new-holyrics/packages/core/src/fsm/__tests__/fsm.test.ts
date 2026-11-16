import { describe, it, expect } from 'bun:test'
import { INITIAL_SCREEN_STATE } from '../screen-state'
import { fsmReducer } from '../fsm-reducer'
import { FsmEngine } from '../fsm-engine'

describe('FSM Reducer', () => {
  it('inicializa no estado EMPTY', () => {
    expect(INITIAL_SCREEN_STATE.mode).toBe('EMPTY')
    expect(INITIAL_SCREEN_STATE.panic.blackout).toBe(false)
  })

  it('alterna o modo blackout corretamente via TOGGLE_PANIC', () => {
    const s1 = fsmReducer(INITIAL_SCREEN_STATE, { type: 'TOGGLE_PANIC', target: 'blackout' })
    expect(s1.panic.blackout).toBe(true)

    const s2 = fsmReducer(s1, { type: 'TOGGLE_PANIC', target: 'blackout' })
    expect(s2.panic.blackout).toBe(false)
  })

  it('navega entre slides dentro dos limites', () => {
    const presenting = fsmReducer(INITIAL_SCREEN_STATE, {
      type: 'PRESENT_ITEM',
      itemId: 'hino-1',
      mode: 'SONG',
      foreground: {
        title: 'Porque Ele Vive',
        content: ['Deus enviou Seu Filho amado', 'Para morrer em meu lugar'],
        slideIndex: 0,
        totalSlides: 2,
      },
    })

    expect(presenting.foreground.slideIndex).toBe(0)

    const next = fsmReducer(presenting, { type: 'NAV_NEXT' })
    expect(next.foreground.slideIndex).toBe(1)

    // Não deve ultrapassar totalSlides - 1
    const beyond = fsmReducer(next, { type: 'NAV_NEXT' })
    expect(beyond.foreground.slideIndex).toBe(1)

    const prev = fsmReducer(beyond, { type: 'NAV_PREV' })
    expect(prev.foreground.slideIndex).toBe(0)
  })

  it('limpa texto e slides de foreground quando PRESENT_ITEM possui mode MEDIA', () => {
    // Projeta uma música anteriormente
    const stateWithSong = fsmReducer(INITIAL_SCREEN_STATE, {
      type: 'PRESENT_ITEM',
      itemId: 'song-1',
      mode: 'SONG',
      foreground: {
        title: 'Música 1',
        content: ['Linha 1', 'Linha 2'],
        slides: [{ content: ['Linha 1'] }, { content: ['Linha 2'] }],
        slideIndex: 0,
        totalSlides: 2,
      },
    })

    // Agora projeta uma mídia em tela cheia
    const stateWithMedia = fsmReducer(stateWithSong, {
      type: 'PRESENT_ITEM',
      itemId: 'media-video-1',
      mode: 'MEDIA',
      background: {
        type: 'video',
        source: '/api/media/stream/video-1',
        loop: true,
        muted: true,
      },
      foreground: {
        title: 'Vídeo Institucional',
        content: ['Texto que deve ser descartado'],
        slides: [{ content: ['Slide ignorado'] }],
        slideIndex: 0,
        totalSlides: 1,
      },
    })

    expect(stateWithMedia.mode).toBe('MEDIA')
    expect(stateWithMedia.activeItemId).toBe('media-video-1')
    expect(stateWithMedia.foreground.content).toEqual([])
    expect(stateWithMedia.foreground.slides).toEqual([])
    expect(stateWithMedia.foreground.totalSlides).toBe(0)
    expect(stateWithMedia.foreground.title).toBe('Vídeo Institucional')
    expect(stateWithMedia.background?.type).toBe('video')
    expect(stateWithMedia.background?.source).toBe('/api/media/stream/video-1')
  })
})

describe('FsmEngine & ReturnStack', () => {
  it('permite interrupção e retorno ao estado anterior', () => {
    const engine = new FsmEngine()

    engine.dispatch({
      type: 'PRESENT_ITEM',
      itemId: 'hino-principal',
      mode: 'SONG',
      foreground: {
        title: 'Hino Principal',
        content: ['Linha 1'],
        slideIndex: 0,
        totalSlides: 1,
      },
    })

    expect(engine.getState().activeItemId).toBe('hino-principal')

    // Interrupção: Leitura de versículo espontâneo
    engine.interrupt({
      type: 'PRESENT_ITEM',
      itemId: 'versiculo-rapido',
      mode: 'BIBLE',
      foreground: {
        title: 'João 3:16',
        content: ['Porque Deus amou o mundo...'],
        slideIndex: 0,
        totalSlides: 1,
      },
    })

    expect(engine.getState().activeItemId).toBe('versiculo-rapido')

    // Retorno da interrupção
    engine.returnFromInterrupt()
    expect(engine.getState().activeItemId).toBe('hino-principal')
  })
})
