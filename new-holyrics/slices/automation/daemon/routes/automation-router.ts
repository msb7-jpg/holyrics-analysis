/**
 * @module @slices/automation/daemon/routes/automation-router
 * @description Roteador REST modular para o motor de automações por grafo (`/api/automation/*`).
 *
 * ## Responsabilidades:
 * - GET `/api/automation`: Retorna o grafo ativo (`automation.json`) e status do OBS WebSocket.
 * - PUT `/api/automation`: Salva e valida novo grafo DAG acíclico.
 * - POST `/api/automation/test-trigger`: Simula disparo manual de gatilho para testes.
 * - POST `/api/automation/webhook/:triggerId`: Endpoint receptor de webhooks externos.
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import {
  AutomationGraphSchema,
  validateGraphAcyclic,
  type TriggerType,
} from '../../contracts/automation-contracts'

/**
 * Processa requisições HTTP direcionadas ao motor de automação.
 *
 * @param request - Requisição HTTP recebida pelo servidor Bun.
 * @param targetUrl - Objeto URL contendo parâmetros e rota.
 * @param context - Contexto global com repositórios e instâncias da FSM.
 * @returns Response formatada ou null caso a rota não pertença a esta fatia.
 */
export async function handleAutomationRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const pathname = targetUrl.pathname

  if (!pathname.startsWith('/api/automation')) {
    return null
  }

  // 1. GET /api/automation - Retorna grafo e status do OBS
  if (pathname === '/api/automation' && request.method === 'GET') {
    const graph = context.automationRepo.load()
    const obsStatus = context.obsClient?.getStatus() ?? {
      connected: false,
      host: graph.obsConnection.host,
      port: graph.obsConnection.port,
    }

    return jsonResponse({
      ok: true,
      data: {
        graph,
        obsStatus,
      },
    })
  }

  // 2. PUT /api/automation - Salva novo grafo DAG
  if (pathname === '/api/automation' && request.method === 'PUT') {
    try {
      const body = await request.json()
      const parsedGraph = AutomationGraphSchema.parse(body)

      const validation = validateGraphAcyclic(parsedGraph.nodes, parsedGraph.edges)
      if (!validation.isValid) {
        return jsonResponse(
          { ok: false, error: `Grafo inválido: ${validation.error}` },
          400,
        )
      }

      context.automationRepo.save(parsedGraph)
      return jsonResponse({ ok: true, data: parsedGraph })
    } catch (error: unknown) {
      return jsonResponse(
        { ok: false, error: error instanceof Error ? error.message : 'Falha ao salvar automações' },
        400,
      )
    }
  }

  // 3. POST /api/automation/test-trigger - Disparo manual para testes
  if (pathname === '/api/automation/test-trigger' && request.method === 'POST') {
    try {
      const body = (await request.json()) as { triggerType: TriggerType }
      await context.dagRunner.executeTrigger(body.triggerType, {
        triggerType: body.triggerType,
        state: context.fsm.getState(),
      })
      const latest = context.dagRunner.getHistory()[0]
      return jsonResponse({
        ok: true,
        message: latest?.details || `Gatilho ${body.triggerType} executado`,
        history: context.dagRunner.getHistory().slice(0, 10),
      })
    } catch (error: unknown) {
      return jsonResponse(
        { ok: false, error: error instanceof Error ? error.message : 'Falha ao testar gatilho' },
        400,
      )
    }
  }

  // 3b. GET /api/automation/history - Consulta histórico de execução
  if (pathname === '/api/automation/history' && request.method === 'GET') {
    return jsonResponse({
      ok: true,
      data: context.dagRunner.getHistory(),
    })
  }

  // 3c. DELETE /api/automation/history - Limpa histórico de execução
  if (pathname === '/api/automation/history' && request.method === 'DELETE') {
    context.dagRunner.clearHistory()
    return jsonResponse({ ok: true })
  }

  // 4. POST /api/automation/webhook/:triggerId - Webhook externo
  if (pathname.startsWith('/api/automation/webhook/') && request.method === 'POST') {
    const triggerId = pathname.replace('/api/automation/webhook/', '')
    if (!triggerId) {
      return jsonResponse({ ok: false, error: 'triggerId ausente na rota' }, 400)
    }

    await context.dagRunner.executeTrigger('ON_HTTP_WEBHOOK', {
      triggerType: 'ON_HTTP_WEBHOOK',
      state: context.fsm.getState(),
      webhookTriggerId: triggerId,
    })

    return jsonResponse({ ok: true, message: `Webhook ${triggerId} processado com sucesso` })
  }

  return null
}
