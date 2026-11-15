/**
 * @module @ui/core/sync/ws-client
 * @description Store external agnóstico do React para gerenciamento da conexão WebSocket com o Daemon.
 *
 * ## Responsabilidades:
 * - Manter conexão singleton resiliente com reconexão automática e backoff exponencial.
 * - Manter snapshot imutável do ScreenState da projeção e do LiveStageState (alertas e cronômetros).
 * - Prover métodos subscribe() e getSnapshot() compatíveis com useSyncExternalStore do React 19.
 * - Despachar comandos tipados ActionCommand sem acoplamento a componentes visuais.
 */

import { INITIAL_SCREEN_STATE, type ActionCommand, type ScreenState } from '@lumen/core'
import { INITIAL_LIVE_STAGE_STATE, type LiveStageState } from '@slices/stage/contracts/stage-contracts'

export type ConnectionListener = (connected: boolean) => void
export type StateListener = (state: ScreenState) => void
export type StageStateListener = (stageState: LiveStageState) => void

/**
 * Store external singleton para controle e sincronização de eventos WebSocket.
 */
export class LumenWebSocketStore {
  private socket: WebSocket | null = null
  private screenState: ScreenState = INITIAL_SCREEN_STATE
  private stageState: LiveStageState = INITIAL_LIVE_STAGE_STATE
  private isConnected = false
  private retryDelayMs = 1500
  private maxRetryDelayMs = 10000
  private retryTimer: ReturnType<typeof setTimeout> | null = null
  private explicitDisconnect = false

  private stateListeners = new Set<StateListener>()
  private stageListeners = new Set<StageStateListener>()
  private connectionListeners = new Set<ConnectionListener>()

  /**
   * Constrói a instância do WebSocket store com a URL do servidor local.
   *
   * @param serverUrl - Endereço WebSocket de conexão.
   */
  constructor(private readonly serverUrl: string = 'ws://localhost:4040/ws?type=operator') {}

  /**
   * Inicia o ciclo de vida da conexão WebSocket.
   */
  public connect(): void {
    if (this.socket && (this.socket.readyState === WebSocket.OPEN || this.socket.readyState === WebSocket.CONNECTING)) {
      return
    }

    this.explicitDisconnect = false

    try {
      this.socket = new WebSocket(this.serverUrl)

      this.socket.onopen = () => {
        this.isConnected = true
        this.retryDelayMs = 1500
        this.notifyConnection(true)
      }

      this.socket.onmessage = (event: MessageEvent) => {
        try {
          const payload = JSON.parse(event.data)
          if (payload.type === 'SYNC_STATE' || payload.type === 'STATE_CHANGED') {
            if (payload.state) {
              this.screenState = payload.state as ScreenState
              this.notifyState(this.screenState)
            }
            if (payload.stageState) {
              this.stageState = payload.stageState as LiveStageState
              this.notifyStageState(this.stageState)
            }
          } else if (payload.type === 'STAGE_STATE_CHANGED' && payload.stageState) {
            this.stageState = payload.stageState as LiveStageState
            this.notifyStageState(this.stageState)
          }
        } catch {
          // Ignora mensagens malformadas
        }
      }

      this.socket.onclose = () => {
        this.isConnected = false
        this.notifyConnection(false)
        this.socket = null
        if (!this.explicitDisconnect) {
          this.scheduleReconnect()
        }
      }

      this.socket.onerror = () => {
        if (this.socket) {
          this.socket.close()
        }
      }
    } catch {
      this.isConnected = false
      this.notifyConnection(false)
      this.scheduleReconnect()
    }
  }

  /**
   * Encerra a conexão WebSocket e cancela tentativas de reconexão agendadas.
   */
  public disconnect(): void {
    this.explicitDisconnect = true
    if (this.retryTimer) {
      clearTimeout(this.retryTimer)
      this.retryTimer = null
    }
    if (this.socket) {
      this.socket.close()
      this.socket = null
    }
    this.isConnected = false
    this.notifyConnection(false)
  }

  /**
   * Despacha um comando tipado de ação litúrgica para o Daemon.
   *
   * @param command - Comando a ser executado na FSM do servidor.
   */
  public dispatch(command: ActionCommand): void {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify({ type: 'DISPATCH', command }))
    } else {
      console.warn('[LumenWS] Comando ignorado: WebSocket desconectado.')
    }
  }

  /**
   * Retorna o snapshot atual do ScreenState para useSyncExternalStore.
   *
   * @returns Snapshot imutável do estado do telão.
   */
  public getSnapshot = (): ScreenState => {
    return this.screenState
  }

  /**
   * Retorna o snapshot atual do LiveStageState (alertas e cronômetros).
   *
   * @returns Snapshot imutável do estado do palco.
   */
  public getStageSnapshot = (): LiveStageState => {
    return this.stageState
  }

  /**
   * Retorna o estado booleano de conexão para useSyncExternalStore.
   *
   * @returns Verdadeiro se o socket estiver conectado.
   */
  public getConnectionSnapshot = (): boolean => {
    return this.isConnected
  }

  /**
   * Inscreve um ouvinte para alterações no ScreenState.
   *
   * @param listener - Callback disparado ao receber novo estado.
   * @returns Função de cancelamento da inscrição.
   */
  public subscribeState = (listener: StateListener): (() => void) => {
    this.stateListeners.add(listener)
    return () => {
      this.stateListeners.delete(listener)
    }
  }

  /**
   * Inscreve um ouvinte para alterações no LiveStageState.
   *
   * @param listener - Callback disparado ao receber novo estado de palco/alertas.
   * @returns Função de cancelamento da inscrição.
   */
  public subscribeStageState = (listener: StageStateListener): (() => void) => {
    this.stageListeners.add(listener)
    return () => {
      this.stageListeners.delete(listener)
    }
  }

  /**
   * Inscreve um ouvinte para alterações no status de conexão.
   *
   * @param listener - Callback disparado em mudanças de conectividade.
   * @returns Função de cancelamento da inscrição.
   */
  public subscribeConnection = (listener: ConnectionListener): (() => void) => {
    this.connectionListeners.add(listener)
    return () => {
      this.connectionListeners.delete(listener)
    }
  }

  private scheduleReconnect(): void {
    if (this.retryTimer) return
    this.retryTimer = setTimeout(() => {
      this.retryTimer = null
      this.retryDelayMs = Math.min(this.retryDelayMs * 1.5, this.maxRetryDelayMs)
      this.connect()
    }, this.retryDelayMs)
  }

  private notifyState(state: ScreenState): void {
    for (const listener of this.stateListeners) {
      listener(state)
    }
  }

  private notifyStageState(stageState: LiveStageState): void {
    for (const listener of this.stageListeners) {
      listener(stageState)
    }
  }

  private notifyConnection(connected: boolean): void {
    for (const listener of this.connectionListeners) {
      listener(connected)
    }
  }
}

export const wsStore = new LumenWebSocketStore()
