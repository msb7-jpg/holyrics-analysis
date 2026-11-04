import { describe, it, expect } from 'bun:test'
import { LumenWebSocketStore } from '../ws-client'
import { INITIAL_SCREEN_STATE } from '@lumen/core'

describe('LumenWebSocketStore (External Store)', () => {
  it('inicializa no estado desconectado com ScreenState padrão', () => {
    const store = new LumenWebSocketStore('ws://localhost:9999')
    expect(store.getConnectionSnapshot()).toBe(false)
    expect(store.getSnapshot()).toEqual(INITIAL_SCREEN_STATE)
  })

  it('permite inscrição e cancelamento de ouvintes de estado', () => {
    const store = new LumenWebSocketStore('ws://localhost:9999')
    let called = false
    const unsubscribe = store.subscribeState(() => {
      called = true
    })

    expect(typeof unsubscribe).toBe('function')
    unsubscribe()
    expect(called).toBe(false)
  })

  it('permite inscrição e cancelamento de ouvintes de conexão', () => {
    const store = new LumenWebSocketStore('ws://localhost:9999')
    let called = false
    const unsubscribe = store.subscribeConnection(() => {
      called = true
    })

    expect(typeof unsubscribe).toBe('function')
    unsubscribe()
    expect(called).toBe(false)
  })
})
