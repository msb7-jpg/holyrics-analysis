/**
 * @module @slices/stage/daemon/routes/stage-router
 * @description Roteador REST modular para controle do Monitor de Palco, Alertas Lower Thirds e Cronômetros (`/api/stage/*`).
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import { formatCountdownTime } from '../stage-engine'

/**
 * Processa requisições HTTP direcionadas à fatia Stage (`/api/stage/*`).
 *
 * @param request - Requisição HTTP recebida pelo servidor Bun.
 * @param targetUrl - Objeto URL da requisição.
 * @param context - Contexto da API com motores e repositórios injetados.
 * @returns Resposta HTTP ou null caso a rota não pertença a esta fatia.
 */
export async function handleStageRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const pathname = targetUrl.pathname

  if (!pathname.startsWith('/api/stage')) {
    return null
  }

  const stageEngine = context.stageEngine
  if (!stageEngine) {
    return jsonResponse({ ok: false, error: 'StageEngine não inicializado' }, 500)
  }

  // 1. GET /api/stage/state - Consulta estado consolidado de palco, alertas e cronômetro
  if (request.method === 'GET' && pathname === '/api/stage/state') {
    return jsonResponse({
      ok: true,
      data: stageEngine.getState(),
    })
  }

  // 2. POST /api/stage/alert - Enfileira e dispara novo alerta Lower Third (RF-24)
  if (request.method === 'POST' && pathname === '/api/stage/alert') {
    try {
      const body = (await request.json()) as Record<string, unknown>
      const createdAlert = stageEngine.enqueueAlert({
        label: typeof body.label === 'string' ? body.label : undefined,
        message: typeof body.message === 'string' ? body.message : '',
        style: body.style as 'classic' | 'urgent' | 'minimal' | undefined,
        position: body.position as 'bottom' | 'top' | undefined,
        durationSeconds: typeof body.durationSeconds === 'number' ? body.durationSeconds : undefined,
      })

      return jsonResponse({
        ok: true,
        data: createdAlert,
        state: stageEngine.getState(),
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Payload inválido para alerta Lower Third',
        },
        400,
      )
    }
  }

  // 3. DELETE /api/stage/alert - Dispensa o alerta atual ou limpa toda a fila
  if (request.method === 'DELETE' && pathname === '/api/stage/alert') {
    const clearAll = targetUrl.searchParams.get('all') === 'true'
    if (clearAll) {
      stageEngine.clearAllAlerts()
    } else {
      stageEngine.dismissActiveAlert()
    }

    return jsonResponse({
      ok: true,
      data: stageEngine.getState(),
    })
  }

  // 4. POST /api/stage/timer - Controla o cronômetro sincronizado (RF-25)
  if (request.method === 'POST' && pathname === '/api/stage/timer') {
    try {
      const body = (await request.json()) as Record<string, unknown>
      const timerState = stageEngine.controlTimer({
        action: (body.action as 'start' | 'pause' | 'reset') || 'start',
        mode: body.mode as 'DURATION' | 'TARGET_TIME' | undefined,
        label: typeof body.label === 'string' ? body.label : undefined,
        durationSeconds: typeof body.durationSeconds === 'number' ? body.durationSeconds : undefined,
        targetTime: typeof body.targetTime === 'string' ? body.targetTime : undefined,
        textBefore: typeof body.textBefore === 'string' ? body.textBefore : undefined,
        textAfter: typeof body.textAfter === 'string' ? body.textAfter : undefined,
        projectOnScreen: Boolean(body.projectOnScreen),
      })

      // Se solicitado, projeta também como slide de contagem regressiva no telão público
      if (body.projectOnScreen && timerState.endTime) {
        const remainingSec = Math.max(0, Math.round((timerState.endTime - Date.now()) / 1000))
        context.fsm.dispatch({
          type: 'PRESENT_ITEM',
          itemId: `countdown_${Date.now()}`,
          mode: 'EMPTY',
          foreground: {
            title: timerState.label,
            content: [
              timerState.textBefore,
              formatCountdownTime(remainingSec, timerState.zeroFill),
              timerState.textAfter,
            ].filter(Boolean),
            slideIndex: 0,
            totalSlides: 1,
          },
        })
      }

      return jsonResponse({
        ok: true,
        data: timerState,
        state: stageEngine.getState(),
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Payload inválido para controle de timer',
        },
        400,
      )
    }
  }

  // 5. POST /api/stage/pastor-message - Envia ou limpa recado exclusivo de púlpito
  if (request.method === 'POST' && pathname === '/api/stage/pastor-message') {
    try {
      const body = (await request.json()) as {
        message?: string | null
        level?: 'info' | 'warning' | 'urgent'
      }
      const pastorMessage = stageEngine.setPastorMessage(body.message ?? null, body.level ?? 'info')

      return jsonResponse({
        ok: true,
        data: pastorMessage,
        state: stageEngine.getState(),
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Falha ao atualizar recado pastoral',
        },
        400,
      )
    }
  }

  return null
}
