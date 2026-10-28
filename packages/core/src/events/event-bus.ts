import type { ScreenState } from '../fsm/screen-state'

export interface StateTransitionEvent {
  type: 'STATE_TRANSITION'
  current: ScreenState
  previous: ScreenState
  timestamp: number
}

export type EventBusListener<T> = (event: T) => void

export class LumenEventBus {
  private listeners: Map<string, Set<EventBusListener<unknown>>> = new Map()

  on<T>(eventType: string, listener: EventBusListener<T>): () => void {
    if (!this.listeners.has(eventType)) {
      this.listeners.set(eventType, new Set())
    }
    const set = this.listeners.get(eventType)!
    set.add(listener as EventBusListener<unknown>)

    return () => {
      set.delete(listener as EventBusListener<unknown>)
    }
  }

  emit<T>(eventType: string, payload: T): void {
    const set = this.listeners.get(eventType)
    if (!set) return
    for (const listener of set) {
      try {
        listener(payload)
      } catch (err) {
        console.error(`Erro ao processar evento ${eventType}:`, err)
      }
    }
  }
}

export const globalEventBus = new LumenEventBus()
