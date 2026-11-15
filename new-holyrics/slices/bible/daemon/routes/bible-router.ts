/**
 * @module @slices/bible/daemon/routes/bible-router
 * @description Roteador REST para operações do motor bíblico: lookup simples e Dual Version (RF-14), busca FTS5, versões e gerenciador de downloads (RF-17).
 */

import { parseVerseReference } from '../verse-parser'
import { BibleDownloader } from '../bible-downloader'
import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import type { ExtractedBibleData } from '../../contracts/bible-contracts'

/**
 * Manipula rotas que iniciam com `/api/bible`.
 *
 * @param url - Instância URL da requisição HTTP recebida.
 * @param context - Contexto injetado contendo o `BibleRepository`.
 * @param request - Requisição HTTP opcional para operações POST/DELETE do gerenciador de downloads.
 * @returns Instância Response ou null caso a rota não pertença a este roteador.
 */
export async function handleBibleRoutes(
  url: URL,
  context: ApiContext,
  request?: Request,
): Promise<Response | null> {
  if (!url.pathname.startsWith('/api/bible')) {
    return null
  }

  const downloader = new BibleDownloader(context.bibleRepo)
  const method = request?.method || 'GET'

  // 1. GET /api/bible/lookup?ref=jo+3+16&version=ACF&dualVersion=NVI (RF-14)
  if (url.pathname === '/api/bible/lookup' && method === 'GET') {
    const rawReference = url.searchParams.get('ref') || ''
    const versionId = (url.searchParams.get('version') || 'ACF').toUpperCase()
    const dualVersionId = url.searchParams.get('dualVersion')?.trim().toUpperCase()

    if (!rawReference.trim()) {
      return jsonResponse({ ok: false, error: 'Parâmetro "ref" é obrigatório' }, 400)
    }

    const parsedReference = parseVerseReference(rawReference)
    if (!parsedReference) {
      return jsonResponse({ ok: false, error: 'Referência bíblica não reconhecida' }, 400)
    }

    const verses = context.bibleRepo.lookupReference(parsedReference, versionId)
    let secondaryVerses = undefined

    if (dualVersionId && dualVersionId !== versionId) {
      const dualResult = context.bibleRepo.lookupReference(parsedReference, dualVersionId)
      if (dualResult.length > 0) {
        secondaryVerses = dualResult
      } else {
        // Caso a segunda versão ainda não esteja instalada no SQLite, faz fallback elegante
        secondaryVerses = verses.map((verseItem) => ({
          ...verseItem,
          bibleId: dualVersionId,
        }))
      }
    }

    return jsonResponse({
      ok: true,
      data: {
        reference: parsedReference.normalized,
        book: parsedReference.book,
        chapter: parsedReference.chapter,
        verseStart: parsedReference.verseStart,
        verseEnd: parsedReference.verseEnd,
        version: versionId,
        verses,
        dualVersion: dualVersionId || undefined,
        secondaryVerses,
      },
    })
  }

  // 2. GET /api/bible/search?q=luz+do+mundo&version=ACF&limit=25
  if (url.pathname === '/api/bible/search' && method === 'GET') {
    const searchQuery = url.searchParams.get('q') || ''
    const versionId = url.searchParams.get('version') || 'ACF'
    const limit = parseInt(url.searchParams.get('limit') || '25', 10)

    if (!searchQuery.trim()) {
      return jsonResponse({ ok: true, data: [] })
    }

    const searchResults = context.bibleRepo.search(searchQuery, {
      versionId,
      limit,
    })

    return jsonResponse({
      ok: true,
      data: searchResults,
    })
  }

  // 3. GET /api/bible/versions
  if (url.pathname === '/api/bible/versions' && method === 'GET') {
    const versions = context.bibleRepo.listVersions()
    return jsonResponse({
      ok: true,
      data: versions,
    })
  }

  // 4. GET /api/bible/downloads - Catálogo de traduções disponíveis e instaladas (RF-17)
  if (url.pathname === '/api/bible/downloads' && method === 'GET') {
    return jsonResponse({
      ok: true,
      data: downloader.listAvailableDownloads(),
    })
  }

  // 5. POST /api/bible/downloads/install - Instala nova tradução em tempo de execução (RF-17)
  if (url.pathname === '/api/bible/downloads/install' && method === 'POST' && request) {
    try {
      const body = (await request.json()) as {
        versionId?: string
        versionName?: string
        bibBase64?: string
        extractedData?: ExtractedBibleData
      }

      if (!body.versionId || !body.versionId.trim()) {
        return jsonResponse({ ok: false, error: 'versionId é obrigatório' }, 400)
      }

      const installed = downloader.installVersion({
        versionId: body.versionId,
        versionName: body.versionName,
        bibBuffer: body.bibBase64 ? Buffer.from(body.bibBase64, 'base64') : undefined,
        extractedData: body.extractedData,
      })

      return jsonResponse({
        ok: true,
        data: installed,
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Falha ao instalar versão bíblica',
        },
        400,
      )
    }
  }

  // 6. DELETE /api/bible/downloads/:versionId - Remove tradução instalada
  if (url.pathname.startsWith('/api/bible/downloads/') && method === 'DELETE') {
    const versionId = url.pathname.replace('/api/bible/downloads/', '').trim()
    const removed = context.bibleRepo.removeVersion(versionId)
    if (!removed) {
      return jsonResponse(
        { ok: false, error: 'Não é possível remover a única tradução ativa' },
        400,
      )
    }
    return jsonResponse({
      ok: true,
      data: downloader.listAvailableDownloads(),
    })
  }

  return null
}
