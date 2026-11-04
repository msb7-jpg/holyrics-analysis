/**
 * @module @slices/media/daemon/routes/media-router
 * @description Roteador REST modular para o catálogo de mídias, playlists de áudio e streaming local (`/api/media/*`).
 *
 * ## Responsabilidades:
 * - Listagem e filtragem do acervo de loops de vídeo e imagens (`GET /api/media`).
 * - Gestão de playlists litúrgicas de áudio (`GET/POST /api/media/audio/playlists`).
 * - Streaming de arquivos locais com suporte a cabeçalhos `Range` para reprodução suave (`GET /api/media/stream/:id`).
 */

import { existsSync, statSync, createReadStream } from 'node:fs'
import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import { AudioPlaylistSchema, AudioTrackSchema, MediaTypeSchema } from '../../contracts/media-contracts'

/**
 * Processa requisições HTTP direcionadas à fatia de mídias e áudio.
 *
 * @param request - Requisição HTTP recebida pelo servidor Bun.
 * @param targetUrl - Objeto URL contendo rota e query params.
 * @param context - Contexto global injetado da API.
 * @returns Response formatada ou null se a rota não corresponder a esta fatia.
 */
export async function handleMediaRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const pathname = targetUrl.pathname

  if (!pathname.startsWith('/api/media')) {
    return null
  }

  // 1. GET /api/media/audio/playlists - Lista todas as playlists de áudio
  if (pathname === '/api/media/audio/playlists' && request.method === 'GET') {
    const playlists = context.mediaRepo.listPlaylists()
    return jsonResponse({
      ok: true,
      data: playlists,
    })
  }

  // 2. POST /api/media/audio/playlists - Cadastra ou atualiza uma playlist
  if (pathname === '/api/media/audio/playlists' && request.method === 'POST') {
    try {
      const payload = await request.json()
      const validatedPlaylist = AudioPlaylistSchema.parse(payload)
      context.mediaRepo.upsertPlaylist(validatedPlaylist)

      return jsonResponse({
        ok: true,
        data: validatedPlaylist,
        message: 'Playlist salva com sucesso.',
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Dados da playlist inválidos',
        },
        400,
      )
    }
  }

  // 3. POST /api/media/audio/playlists/:id/tracks - Adiciona faixa à playlist
  if (pathname.startsWith('/api/media/audio/playlists/') && pathname.endsWith('/tracks') && request.method === 'POST') {
    const segments = pathname.split('/')
    const playlistId = segments[segments.length - 2]

    try {
      const trackPayload = await request.json()
      const validatedTrack = AudioTrackSchema.parse(trackPayload)
      const updatedPlaylist = context.mediaRepo.addTrackToPlaylist(playlistId, validatedTrack)

      return jsonResponse({
        ok: true,
        data: updatedPlaylist,
        message: 'Faixa adicionada à playlist.',
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Falha ao adicionar faixa',
        },
        400,
      )
    }
  }

  // 4. GET /api/media/stream/:id - Streaming de arquivos locais com suporte a Range
  if (pathname.startsWith('/api/media/stream/') && request.method === 'GET') {
    const mediaId = pathname.replace('/api/media/stream/', '')
    const mediaItem = context.mediaRepo.getMediaById(mediaId)

    if (!mediaItem || !existsSync(mediaItem.path)) {
      return jsonResponse({ ok: false, error: 'Arquivo de mídia não encontrado para streaming' }, 404)
    }

    const stats = statSync(mediaItem.path)
    const fileSize = stats.size
    const rangeHeader = request.headers.get('range')

    if (rangeHeader) {
      const parts = rangeHeader.replace(/bytes=/, '').split('-')
      const startByte = parseInt(parts[0], 10)
      const endByte = parts[1] ? parseInt(parts[1], 10) : fileSize - 1
      const chunkSize = endByte - startByte + 1

      const nodeStream = createReadStream(mediaItem.path, { start: startByte, end: endByte })
      // @ts-expect-error - Readable.toWeb do Node está disponível no Bun
      const webStream = typeof nodeStream.toWeb === 'function' ? nodeStream.toWeb() : (nodeStream as unknown as ReadableStream)

      return new Response(webStream as BodyInit, {
        status: 206,
        headers: {
          'Content-Range': `bytes ${startByte}-${endByte}/${fileSize}`,
          'Accept-Ranges': 'bytes',
          'Content-Length': String(chunkSize),
          'Content-Type': mediaItem.mimeType,
          'Access-Control-Allow-Origin': '*',
        },
      })
    }

    const fullFileStream = Bun.file(mediaItem.path)
    return new Response(fullFileStream, {
      status: 200,
      headers: {
        'Content-Length': String(fileSize),
        'Content-Type': mediaItem.mimeType,
        'Accept-Ranges': 'bytes',
        'Access-Control-Allow-Origin': '*',
      },
    })
  }

  // 5. GET /api/media/:id - Detalhes de uma mídia específica
  if (pathname.startsWith('/api/media/') && request.method === 'GET') {
    const mediaId = pathname.replace('/api/media/', '')
    const mediaItem = context.mediaRepo.getMediaById(mediaId)

    if (!mediaItem) {
      return jsonResponse({ ok: false, error: 'Item de mídia não encontrado' }, 404)
    }

    return jsonResponse({
      ok: true,
      data: mediaItem,
    })
  }

  // 6. GET /api/media - Listagem geral de mídias com filtros
  if (pathname === '/api/media' && request.method === 'GET') {
    const rawType = targetUrl.searchParams.get('type')
    const query = targetUrl.searchParams.get('q') || undefined

    let filteredType: import('../../contracts/media-contracts').MediaType | undefined
    if (rawType) {
      const parsedType = MediaTypeSchema.safeParse(rawType.toUpperCase())
      if (parsedType.success) {
        filteredType = parsedType.data
      }
    }

    const items = context.mediaRepo.listMedia({ type: filteredType, query })

    return jsonResponse({
      ok: true,
      data: items,
      total: items.length,
    })
  }

  return null
}
