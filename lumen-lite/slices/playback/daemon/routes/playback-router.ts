/**
 * @module @slices/playback/daemon/routes/playback-router
 * @description Roteador REST para gestão e sincronização de Playbacks Musicais (`/api/playback/*` - RF-22).
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import {
  PlaybackTrackSchema,
  resolvePlaybackSlideIndex,
} from '../../contracts/playback-contracts'

/**
 * Processa requisições HTTP direcionadas a `/api/playback/*`.
 *
 * @param request - Requisição HTTP recebida.
 * @param targetUrl - Objeto URL decomposto.
 * @param context - Contexto de dependências do daemon.
 * @returns Resposta HTTP ou null caso a rota não pertença a esta fatia.
 */
export async function handlePlaybackRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const { pathname } = targetUrl

  if (!pathname.startsWith('/api/playback')) {
    return null
  }

  const playbackRepo = context.playbackRepo
  if (!playbackRepo) {
    return jsonResponse({ ok: false, error: 'PlaybackRepository não inicializado' }, 500)
  }

  // 1. GET /api/playback - Lista todos os playbacks configurados
  if (pathname === '/api/playback' && request.method === 'GET') {
    return jsonResponse({
      ok: true,
      data: playbackRepo.listAll(),
    })
  }

  // 2. POST /api/playback - Salva ou atualiza minutagem de playback
  if (pathname === '/api/playback' && request.method === 'POST') {
    try {
      const body = await request.json()
      const parsed = PlaybackTrackSchema.parse({
        ...(body as Record<string, unknown>),
        updatedAt: Date.now(),
      })
      const saved = playbackRepo.upsertTrack(parsed)
      return jsonResponse({ ok: true, data: saved }, 201)
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Payload inválido para PlaybackTrack',
        },
        400,
      )
    }
  }

  // 3. POST /api/playback/sync - Sincroniza o slide da FSM com o tempo decorrido do áudio (`elapsedMs`)
  if (pathname === '/api/playback/sync' && request.method === 'POST') {
    try {
      const body = (await request.json()) as {
        trackId: string
        elapsedMs: number
      }

      const track = playbackRepo.getBySongOrId(body.trackId)
      if (!track) {
        return jsonResponse({ ok: false, error: 'Playback não encontrado' }, 404)
      }

      const targetSlideIndex = resolvePlaybackSlideIndex(track, body.elapsedMs)
      const currentState = context.fsm.getState()

      if (currentState.foreground.slideIndex !== targetSlideIndex) {
        context.fsm.dispatch({ type: 'NAV_GOTO', slideIndex: targetSlideIndex })
      }

      return jsonResponse({
        ok: true,
        data: {
          trackId: track.id,
          elapsedMs: body.elapsedMs,
          slideIndex: targetSlideIndex,
        },
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Erro ao sincronizar playback',
        },
        400,
      )
    }
  }

  return null
}
