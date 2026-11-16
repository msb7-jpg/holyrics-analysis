/**
 * @module @slices/songs/daemon/routes/vagalume-router
 * @description Roteador para busca e importação rápida de letras através da API Vagalume.
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'

/**
 * Manipula requisições direcionadas para `/api/vagalume`.
 *
 * @param request - Requisição HTTP recebida.
 * @param targetUrl - Objeto URL para leitura de query parameters.
 * @param context - Contexto injetado contendo o VagalumeClient.
 * @returns Instância Response ou null caso a rota não pertença a este roteador.
 */
export async function handleVagalumeRoute(
  request: Request,
  targetUrl: URL,
  context: ApiContext
): Promise<Response | null> {
  const { pathname } = targetUrl

  // GET /api/vagalume/search?q=porque+ele+vive
  if (pathname === '/api/vagalume/search' && request.method === 'GET') {
    const searchTerm = targetUrl.searchParams.get('q') || ''

    if (!searchTerm.trim()) {
      return jsonResponse({ ok: false, error: 'Parâmetro de busca "q" é obrigatório' }, 400)
    }

    try {
      const searchResults = await context.vagalumeClient.search(searchTerm)
      return jsonResponse({ ok: true, data: searchResults })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Falha na busca Vagalume'
      return jsonResponse({ ok: false, error: errorMessage }, 502)
    }
  }

  // GET /api/vagalume/fetch?musId=...
  if (pathname === '/api/vagalume/fetch' && request.method === 'GET') {
    const musicId = targetUrl.searchParams.get('musId') || ''

    if (!musicId.trim()) {
      return jsonResponse({ ok: false, error: 'Parâmetro "musId" é obrigatório' }, 400)
    }

    try {
      const targetSong = await context.vagalumeClient.getSongByMusId(musicId)
      if (!targetSong) {
        return jsonResponse({ ok: false, error: 'Letra não encontrada para esta música.' }, 404)
      }
      return jsonResponse({ ok: true, data: targetSong })
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Falha ao importar letra do Vagalume'
      return jsonResponse({ ok: false, error: errorMessage }, 502)
    }
  }

  return null
}
