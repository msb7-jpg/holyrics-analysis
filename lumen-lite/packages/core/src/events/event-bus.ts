export type EventBusListener<T> = (event: T) => void

/**
 * Emissor e barramento de eventos desacoplado do LumenLite.
 * Permite que subsistemas reajam a transições litúrgicas sem acoplamento direto.
 */
export class LumenEventBus {
  private listeners: Map<string, Set<EventBusListener<unknown>>> = new Map()

  /**
   * Registra um ouvinte para um tipo específico de evento.
   *
   * @param eventType - Identificador único do evento.
   * @param listener - Função ouvinte que receberá o payload do evento.
   * @returns Função para cancelar a inscrição do ouvinte.
   */
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

  /**
   * Emite um evento para todos os ouvintes inscritos.
   *
   * @param eventType - Identificador do evento a ser disparado.
   * @param payload - Dados associados ao evento.
   */
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
