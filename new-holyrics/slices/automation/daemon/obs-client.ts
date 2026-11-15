/**
 * @module @slices/automation/daemon/obs-client
 * @description Cliente nativo de alta performance para OBS Studio WebSocket v5 no runtime Bun.
 *
 * ## Responsabilidades:
 * - Gerenciar conexão persistente e reconexão automática com o OBS Studio v5 (porta padrão 4455).
 * - Realizar handshake seguro com hash SHA-256 duplo para autenticação sem bibliotecas externas pesadas.
 * - Despachar requisições (OpCode 6) com timeouts estritos para garantir 0ms de impacto na projeção.
 *
 * ## Comunicação e Fronteiras:
 * - Consumido pelo DAG runner (`dag-runner.ts`) para envio de comandos de corte de câmera e áudio.
 */

import { createHash, randomUUID } from 'node:crypto'
import { createLogger } from '@lumen/core'
import type {
  ObsConnectionConfig,
  ObsRequestType,
} from '../contracts/automation-contracts'

const logger = createLogger('obs-client')

export interface ObsClientStatus {
  connected: boolean
  host: string
  port: number
  version?: string
}

interface PendingRequest {
  resolve: (value: unknown) => void
  reject: (reason: Error) => void
  timer: ReturnType<typeof setTimeout>
}

/**
 * Cliente WebSocket v5 do OBS Studio nativo em Bun.
 */
export class ObsClient {
  private config: ObsConnectionConfig
  private socket: WebSocket | null = null
  private connected = false
  private obsVersion: string | undefined
  private pendingRequests = new Map<string, PendingRequest>()
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null
  private reconnectAttempts = 0
  private hasLoggedOffline = false
  private isDisposed = false

  /**
   * Inicializa o cliente OBS com configurações de host, porta e credenciais.
   *
   * @param config - Parâmetros de conexão com o OBS Studio.
   */
  constructor(config: ObsConnectionConfig) {
    this.config = config
  }

  /**
   * Conecta ao servidor WebSocket do OBS Studio.
   */
  public connect(): void {
    if (this.isDisposed || this.socket) return

    const targetUrl = `ws://${this.config.host}:${this.config.port}`
    if (!this.hasLoggedOffline) {
      logger.info({ url: targetUrl }, 'Iniciando conexão com OBS Studio WebSocket v5...')
    } else {
      logger.debug({ url: targetUrl, attempt: this.reconnectAttempts }, 'Tentando reconexão silenciosa com OBS Studio...')
    }

    try {
      this.socket = new WebSocket(targetUrl)

      this.socket.onopen = () => {
        logger.debug('Socket TCP com OBS aberto, aguardando mensagem Hello (OpCode 0)...')
      }

      this.socket.onmessage = (event: MessageEvent) => {
        this.handleMessage(String(event.data))
      }

      this.socket.onerror = () => {
        if (!this.hasLoggedOffline) {
          this.hasLoggedOffline = true
          logger.info(
            { url: targetUrl },
            'OBS Studio offline ou inacessível no momento (status: offline; reconexão automática silenciosa ativa)',
          )
        }
      }

      this.socket.onclose = () => {
        this.connected = false
        this.socket = null
        this.rejectAllPending(new Error('Conexão com OBS Studio encerrada'))

        if (this.config.autoReconnect && !this.isDisposed) {
          this.scheduleReconnect()
        }
      }
    } catch (error) {
      if (!this.hasLoggedOffline) {
        this.hasLoggedOffline = true
        logger.info({ error, url: targetUrl }, 'OBS Studio offline (falha ao instanciar WebSocket)')
      }
      this.scheduleReconnect()
    }
  }

  /**
   * Processa mensagens do protocolo OBS WebSocket v5.
   *
   * @param rawData - Payload serializado em string JSON.
   */
  private handleMessage(rawData: string): void {
    try {
      const message = JSON.parse(rawData) as {
        op: number
        d?: Record<string, unknown>
      }

      // OpCode 0: Hello do Servidor OBS
      if (message.op === 0 && message.d) {
        this.handleHello(message.d)
        return
      }

      // OpCode 2: Identified (Handshake concluído com sucesso)
      if (message.op === 2) {
        this.connected = true
        this.reconnectAttempts = 0
        this.hasLoggedOffline = false
        logger.info({ version: this.obsVersion }, 'Handshake concluído. Conectado ao OBS Studio v5!')
        return
      }

      // OpCode 7: RequestResponse
      if (message.op === 7 && message.d) {
        this.handleRequestResponse(message.d)
      }
    } catch (parseError) {
      logger.error({ parseError }, 'Erro ao decodificar mensagem do OBS Studio')
    }
  }

  /**
   * Trata a mensagem Hello calculando hash de autenticação se necessário e enviando Identify.
   *
   * @param helloData - Dados recebidos na saudação inicial do OBS.
   */
  private handleHello(helloData: Record<string, unknown>): void {
    this.obsVersion = String(helloData.obsWebSocketVersion || '5.x')
    const authentication = helloData.authentication as { challenge: string; salt: string } | undefined

    const identifyPayload: Record<string, unknown> = {
      rpcVersion: 1,
      eventSubscriptions: 0,
    }

    if (authentication && this.config.password) {
      const secret = createHash('sha256')
        .update(this.config.password + authentication.salt)
        .digest('base64')

      const authResponse = createHash('sha256')
        .update(secret + authentication.challenge)
        .digest('base64')

      identifyPayload.authentication = authResponse
    }

    this.sendRaw({
      op: 1, // OpCode 1: Identify
      d: identifyPayload,
    })
  }

  /**
   * Trata respostas de comandos enviados.
   *
   * @param responseData - Dados contendo requestId e requestStatus.
   */
  private handleRequestResponse(responseData: Record<string, unknown>): void {
    const requestId = String(responseData.requestId || '')
    const pending = this.pendingRequests.get(requestId)
    if (!pending) return

    clearTimeout(pending.timer)
    this.pendingRequests.delete(requestId)

    const requestStatus = responseData.requestStatus as { result: boolean; code: number; comment?: string } | undefined

    if (requestStatus && requestStatus.result === true) {
      pending.resolve(responseData.responseData)
    } else {
      const errorMessage = requestStatus?.comment || `Erro OBS Code ${requestStatus?.code ?? 'desconhecido'}`
      pending.reject(new Error(errorMessage))
    }
  }

  /**
   * Despacha um comando para o OBS Studio com timeout estrito.
   *
   * @param requestType - Nome da operação OBS v5 (ex: SetCurrentProgramScene).
   * @param requestData - Parâmetros da requisição.
   * @param timeoutMs - Tempo máximo de espera em milissegundos.
   * @returns Promise resolvida com os dados de resposta do OBS.
   */
  public async sendRequest<T = unknown>(
    requestType: ObsRequestType,
    requestData: Record<string, unknown> = {},
    timeoutMs = 1000,
  ): Promise<T> {
    if (!this.connected || !this.socket) {
      throw new Error(`OBS Studio não está conectado (${this.config.host}:${this.config.port})`)
    }

    const requestId = randomUUID()

    return new Promise<T>((resolve, reject) => {
      const timer = setTimeout(() => {
        this.pendingRequests.delete(requestId)
        reject(new Error(`Timeout de ${timeoutMs}ms aguardando resposta do OBS para ${requestType}`))
      }, timeoutMs)

      this.pendingRequests.set(requestId, {
        resolve: resolve as (value: unknown) => void,
        reject,
        timer,
      })

      this.sendRaw({
        op: 6, // OpCode 6: Request
        d: {
          requestType,
          requestId,
          requestData,
        },
      })
    })
  }

  /**
   * Envia payload JSON bruto pelo socket WebSocket.
   *
   * @param payload - Objeto com opcode e dados.
   */
  private sendRaw(payload: Record<string, unknown>): void {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(payload))
    }
  }

  /**
   * Agenda tentativa de reconexão exponencial.
   */
  private scheduleReconnect(): void {
    if (this.reconnectTimer || this.isDisposed) return

    this.reconnectAttempts += 1
    const delayMs = Math.min(3000 * Math.pow(2, Math.min(this.reconnectAttempts - 1, 4)), 30000)

    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = null
      this.connect()
    }, delayMs)
  }

  /**
   * Rejeita todas as promises pendentes quando a conexão cai.
   *
   * @param error - Erro de desconexão.
   */
  private rejectAllPending(error: Error): void {
    for (const [requestId, pending] of this.pendingRequests.entries()) {
      clearTimeout(pending.timer)
      pending.reject(error)
      this.pendingRequests.delete(requestId)
    }
  }

  /**
   * Retorna o estado atual da conexão com o OBS.
   *
   * @returns Objeto com booleano de conexão, host e porta.
   */
  public getStatus(): ObsClientStatus {
    return {
      connected: this.connected,
      host: this.config.host,
      port: this.config.port,
      version: this.obsVersion,
    }
  }

  /**
   * Encerra a conexão e libera recursos.
   */
  public dispose(): void {
    this.isDisposed = true
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    this.rejectAllPending(new Error('OBS client disposed'))
    if (this.socket) {
      this.socket.close()
      this.socket = null
    }
    this.connected = false
  }
}
