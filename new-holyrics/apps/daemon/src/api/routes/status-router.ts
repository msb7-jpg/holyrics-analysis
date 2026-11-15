/**
 * @module @lumen/daemon/api/routes/status
 * @description Handler do endpoint de status e telemetria do daemon.
 */

import type { Server } from 'bun'
import { jsonResponse } from '../context'
import type { ApiContext } from '../context'

/**
 * Processa a requisição GET /api/status reportando integridade, estado da FSM e contadores.
 *
 * @param context - Contexto da aplicação contendo instâncias de serviços.
 * @param serverInstance - Instância do servidor Bun para leitura de assinantes WebSocket.
 * @returns Resposta HTTP com JSON de telemetria.
 */
export function handleStatusRoute(context: ApiContext, serverInstance: Server<unknown>): Response {
  return jsonResponse({
    ok: true,
    state: context.fsm.getState(),
    clients: serverInstance.subscriberCount('screen-state'),
    songsCount: context.songRepo.count(),
  })
}
