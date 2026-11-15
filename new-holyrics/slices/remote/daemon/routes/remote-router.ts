/**
 * @module @slices/remote/daemon/routes/remote-router
 * @description Roteador REST para pareamento via QR Code e despacho autenticado de comandos móveis (`/api/remote/*`).
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import type { ActionCommand } from '@lumen/core'

/**
 * Processa requisições HTTP para a fatia Remote (`/api/remote/*`).
 *
 * @param request - Requisição HTTP recebida.
 * @param targetUrl - Objeto URL com caminho e query params.
 * @param context - Contexto global de dependências do daemon.
 * @returns Resposta HTTP ou null se a rota não corresponder.
 */
export async function handleRemoteRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const pathname = targetUrl.pathname

  if (!pathname.startsWith('/api/remote')) {
    return null
  }

  const remoteAuth = context.remoteAuth
  if (!remoteAuth) {
    return jsonResponse({ ok: false, error: 'RemoteAuthManager não inicializado' }, 500)
  }

  // 1. GET /api/remote/info - Retorna URLs e tokens para o modal de QR Code
  if (request.method === 'GET' && pathname === '/api/remote/info') {
    return jsonResponse({
      ok: true,
      data: remoteAuth.getPairingInfo(),
    })
  }

  // 2. POST /api/remote/rotate - Regenera tokens de pareamento
  if (request.method === 'POST' && pathname === '/api/remote/rotate') {
    return jsonResponse({
      ok: true,
      data: remoteAuth.rotateTokens(),
    })
  }

  // 3. POST /api/remote/dispatch - Despacha comando validando RBAC (preacher vs operator)
  if (request.method === 'POST' && pathname === '/api/remote/dispatch') {
    try {
      const body = (await request.json()) as {
        token?: string
        command?: ActionCommand
      }

      if (!body.command || !body.command.type) {
        return jsonResponse({ ok: false, error: 'Comando ausente ou inválido' }, 400)
      }

      const authResult = remoteAuth.authorizeCommand(body.token, body.command)
      if (!authResult.authorized) {
        return jsonResponse(
          {
            ok: false,
            error: authResult.reason || 'Acesso negado',
          },
          403,
        )
      }

      const nextState = context.fsm.dispatch(body.command)
      return jsonResponse({
        ok: true,
        role: authResult.role,
        data: nextState,
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Falha ao processar comando remoto',
        },
        400,
      )
    }
  }

  return null
}
