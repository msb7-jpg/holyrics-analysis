/**
 * @module @slices/songs/daemon/routes/songs-router
 * @description Handlers de busca FTS5, consulta por identificador e persistência de músicas.
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import { SongSchema, type Song } from '../../contracts/song-contracts'

/**
 * Roteia e processa requisições direcionadas para o acervo de músicas (/api/songs).
 *
 * @param request - Requisição HTTP recebida.
 * @param targetUrl - Objeto URL decomposto para análise de caminhos e parâmetros.
 * @param context - Contexto da aplicação com acesso ao SongRepository.
 * @returns Resposta HTTP processada ou null se a rota não pertencer a este módulo.
 */
export async function handleSongsRoute(
  request: Request,
  targetUrl: URL,
  context: ApiContext
): Promise<Response | null> {
  const { pathname } = targetUrl

  // GET /api/songs - Busca com suporte a FTS5 e paginação simples
  if (pathname === '/api/songs' && request.method === 'GET') {
    const searchTerm = targetUrl.searchParams.get('q') || ''
    const resultLimit = parseInt(targetUrl.searchParams.get('limit') || '20', 10)
    const searchResults = context.songRepo.search(searchTerm, resultLimit)
    return jsonResponse({ ok: true, data: searchResults })
  }

  // GET /api/songs/:id - Busca detalhada de hino/música por ID único
  if (pathname.startsWith('/api/songs/') && request.method === 'GET') {
    const songId = pathname.replace('/api/songs/', '')
    const targetSong = context.songRepo.getById(songId)
    if (!targetSong) {
      return jsonResponse({ ok: false, error: 'Música não encontrada' }, 404)
    }
    return jsonResponse({ ok: true, data: targetSong })
  }

  // POST /api/songs - Criação ou atualização de música no SQLite com validação Zod
  if (pathname === '/api/songs' && request.method === 'POST') {
    try {
      const rawBody = await request.json()
      const parsedSong = SongSchema.safeParse(rawBody)
      if (!parsedSong.success) {
        return jsonResponse({ ok: false, error: `Validação falhou: ${parsedSong.error.message}` }, 400)
      }
      const payloadSong: Song = parsedSong.data
      context.songRepo.upsertSong(payloadSong)
      return jsonResponse({ ok: true, data: payloadSong }, 201)
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Erro ao processar música'
      return jsonResponse({ ok: false, error: errorMessage }, 400)
    }
  }

  return null
}
