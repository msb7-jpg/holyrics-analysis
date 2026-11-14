/**
 * @module @slices/songs/daemon/routes/songs-router
 * @description Handlers de busca FTS5, consulta por identificador, importação de backups legados (RF-11) e histórico de execução (RF-12).
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import {
  LegacyHolyricsSongSchema,
  SongSchema,
  type LegacyHolyricsSong,
  type Song,
} from '../../contracts/song-contracts'
import { importHolyricsBatch } from '../importer/holyrics-importer'

/**
 * Roteia e processa requisições direcionadas para o acervo de músicas (/api/songs).
 *
 * @param request - Requisição HTTP recebida.
 * @param targetUrl - Objeto URL decomposto para análise de caminhos e parâmetros.
 * @param context - Contexto da aplicação com acesso ao SongRepository e SongHistoryRepository.
 * @returns Resposta HTTP processada ou null se a rota não pertencer a este módulo.
 */
export async function handleSongsRoute(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const { pathname } = targetUrl

  if (!pathname.startsWith('/api/songs')) {
    return null
  }

  // 1. GET /api/songs/history - Histórico recente de músicas executadas (RF-12)
  if (pathname === '/api/songs/history' && request.method === 'GET') {
    const limit = parseInt(targetUrl.searchParams.get('limit') || '50', 10)
    const entries = context.songHistoryRepo ? context.songHistoryRepo.listRecent(limit) : []
    return jsonResponse({ ok: true, data: entries })
  }

  // 2. GET /api/songs/history/stats - Ranking de frequência das músicas mais cantadas (RF-12)
  if (pathname === '/api/songs/history/stats' && request.method === 'GET') {
    const limit = parseInt(targetUrl.searchParams.get('limit') || '30', 10)
    const stats = context.songHistoryRepo ? context.songHistoryRepo.getUsageStats(limit) : []
    return jsonResponse({ ok: true, data: stats })
  }

  // 3. POST /api/songs/import - Importador completo de backups Holyrics (.zip ou JSON - RF-11)
  if (pathname === '/api/songs/import' && request.method === 'POST') {
    try {
      const contentType = request.headers.get('content-type') || ''

      if (contentType.includes('application/zip') || contentType.includes('application/octet-stream')) {
        const arrayBuf = await request.arrayBuffer()
        const report = importHolyricsBatch(context.songRepo, new Uint8Array(arrayBuf))
        return jsonResponse({ ok: true, data: report })
      }

      const body = (await request.json()) as {
        zipBase64?: string
        songs?: unknown[]
      }

      if (typeof body.zipBase64 === 'string' && body.zipBase64.length > 0) {
        const zipBuffer = Buffer.from(body.zipBase64, 'base64')
        const report = importHolyricsBatch(context.songRepo, new Uint8Array(zipBuffer))
        return jsonResponse({ ok: true, data: report })
      }

      if (Array.isArray(body.songs)) {
        const validLegacySongs: LegacyHolyricsSong[] = []
        for (const rawSong of body.songs) {
          const parsed = LegacyHolyricsSongSchema.safeParse(rawSong)
          if (parsed.success) {
            validLegacySongs.push(parsed.data)
          }
        }
        const report = importHolyricsBatch(context.songRepo, validLegacySongs)
        return jsonResponse({ ok: true, data: report })
      }

      return jsonResponse({ ok: false, error: 'Formato de importação inválido' }, 400)
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Falha na importação de músicas',
        },
        400,
      )
    }
  }

  // 4. GET /api/songs - Busca com suporte a FTS5 e paginação simples
  if (pathname === '/api/songs' && request.method === 'GET') {
    const searchTerm = targetUrl.searchParams.get('q') || ''
    const resultLimit = parseInt(targetUrl.searchParams.get('limit') || '20', 10)
    const searchResults = context.songRepo.search(searchTerm, resultLimit)
    return jsonResponse({ ok: true, data: searchResults })
  }

  // 5. GET /api/songs/:id - Busca detalhada de hino/música por ID único
  if (pathname.startsWith('/api/songs/') && request.method === 'GET') {
    const songId = pathname.replace('/api/songs/', '')
    const targetSong = context.songRepo.getById(songId)
    if (!targetSong) {
      return jsonResponse({ ok: false, error: 'Música não encontrada' }, 404)
    }
    return jsonResponse({ ok: true, data: targetSong })
  }

  // 6. POST /api/songs - Criação ou atualização de música no SQLite com validação Zod
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
