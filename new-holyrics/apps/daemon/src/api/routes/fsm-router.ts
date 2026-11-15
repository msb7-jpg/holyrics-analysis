/**
 * @module @lumen/daemon/api/routes/fsm-router
 * @description Handler REST para comandos de despacho direto na Máquina de Estados Finita (`/api/fsm/*`).
 *
 * ## Responsabilidades:
 * - POST `/api/fsm/dispatch`: Recebe comandos tipados (`ActionCommand`) e despacha na FSM.
 * - GET `/api/fsm/state`: Retorna o estado atual da projeção (`ScreenState`).
 */

import { jsonResponse } from '../context'
import type { ApiContext } from '../context'
import type { ActionCommand } from '@lumen/core'

/**
 * Processa requisições direcionadas à FSM da projeção.
 *
 * @param request - Requisição HTTP recebida pelo servidor Bun.
 * @param targetUrl - Objeto URL contendo rota e parâmetros.
 * @param context - Contexto global com serviços injetados.
 * @returns Response formatada ou null se a rota não corresponder a esta fatia.
 */
export async function handleFsmRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const pathname = targetUrl.pathname

  if (!pathname.startsWith('/api/fsm')) {
    return null
  }

  // 1. GET /api/fsm/state - Retorna estado atual da projeção
  if (pathname === '/api/fsm/state' && request.method === 'GET') {
    return jsonResponse({
      ok: true,
      data: context.fsm.getState(),
    })
  }

  // 2. POST /api/fsm/dispatch - Despacha comando na FSM
  if (pathname === '/api/fsm/dispatch' && request.method === 'POST') {
    try {
      const body = (await request.json()) as { command?: ActionCommand }
      if (!body.command || !body.command.type) {
        return jsonResponse({ ok: false, error: 'Comando inválido ou tipo ausente' }, 400)
      }

      context.fsm.dispatch(body.command)
      const nextState = context.fsm.getState()

      return jsonResponse({
        ok: true,
        data: nextState,
        dispatched: body.command.type,
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Falha ao despachar comando na FSM',
        },
        400,
      )
    }
  }

  return null
}
