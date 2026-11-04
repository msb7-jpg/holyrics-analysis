/**
 * @module @lumen/daemon/ws/ws-handler
 * @description Manipulador de ciclo de vida das conexões WebSocket e sincronização reativa com a FSM.
 */

import type { Server, ServerWebSocket } from 'bun'
import type { ActionCommand, FsmEngine, ScreenState } from '@lumen/core'
import type { Logger } from 'pino'

/**
 * Metadados vinculados a cada conexão WebSocket ativa.
 */
export interface WebSocketData {
  id: string
  clientType: 'operator' | 'projector' | 'stage' | 'broadcast'
}

/**
 * Parâmetros de inicialização do manipulador de WebSocket.
 */
export interface WebSocketHandlerParams {
  fsm: FsmEngine
  logger: Logger
}

/**
 * Cria a estrutura de handlers para conexões WebSocket gerenciadas pelo Bun.
 *
 * @param params - Instâncias da máquina de estados e do logger Pino.
 * @returns Manipuladores de eventos de socket e função de vinculação à FSM.
 */
export function createWebSocketHandler(params: WebSocketHandlerParams) {
  const { fsm, logger } = params

  return {
    websocket: {
      /**
       * Invocado quando um novo cliente WebSocket estabelece conexão.
       *
       * @param socket - Socket do cliente conectado.
       */
      open(socket: ServerWebSocket<WebSocketData>) {
        socket.subscribe('screen-state')
        logger.info({ clientType: socket.data.clientType, id: socket.data.id }, 'Cliente WebSocket conectado')
        // Envia snapshot do estado atual imediatamente na conexão
        socket.send(JSON.stringify({ type: 'SYNC_STATE', state: fsm.getState() }))
      },

      /**
       * Invocado na recepção de mensagens de comando ou sincronização.
       *
       * @param _socket - Socket do emissor da mensagem.
       * @param message - Conteúdo bruto recebido via rede.
       */
      message(_socket: ServerWebSocket<WebSocketData>, message: string | Buffer) {
        try {
          const rawMessage = typeof message === 'string' ? message : new TextDecoder().decode(message)
          const parsedData = JSON.parse(rawMessage)

          if (parsedData.type === 'DISPATCH' && parsedData.command) {
            fsm.dispatch(parsedData.command as ActionCommand)
          }
        } catch (error) {
          logger.error({ error }, 'Erro ao processar mensagem WebSocket')
        }
      },

      /**
       * Invocado quando uma conexão WebSocket é encerrada.
       *
       * @param socket - Socket do cliente que desconectou.
       */
      close(socket: ServerWebSocket<WebSocketData>) {
        logger.info({ clientType: socket.data.clientType, id: socket.data.id }, 'Cliente WebSocket desconectado')
      },
    },

    /**
     * Vincula a emissão de novos estados da FSM ao canal de pub/sub do servidor Bun.
     *
     * @param serverInstance - Instância ativa do servidor Bun.
     */
    bindFsm(serverInstance: Server<WebSocketData>) {
      fsm.subscribe((newState: ScreenState) => {
        serverInstance.publish('screen-state', JSON.stringify({ type: 'STATE_CHANGED', state: newState }))
      })
    },
  }
}
