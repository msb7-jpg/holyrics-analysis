/**
 * @module @slices/bible/daemon/routes/bible-router
 * @description Roteador REST para operações do motor bíblico (lookup de referências, busca FTS5 e versões).
 */

import { parseVerseReference } from '../verse-parser'
import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'

/**
 * Manipula rotas que iniciam com `/api/bible`.
 *
 * @param url - Instância URL da requisição HTTP recebida.
 * @param context - Contexto injetado contendo o `BibleRepository`.
 * @returns Instância Response ou null caso a rota não pertença a este roteador.
 */
export function handleBibleRoutes(url: URL, context: ApiContext): Response | null {
  // GET /api/bible/lookup?ref=jo+3+16&version=ACF
  if (url.pathname === '/api/bible/lookup') {
    const rawReference = url.searchParams.get('ref') || ''
    const versionId = url.searchParams.get('version') || 'ACF'

    if (!rawReference.trim()) {
      return jsonResponse({ ok: false, error: 'Parâmetro "ref" é obrigatório' }, 400)
    }

    const parsedReference = parseVerseReference(rawReference)
    if (!parsedReference) {
      return jsonResponse({ ok: false, error: 'Referência bíblica não reconhecida' }, 400)
    }

    const verses = context.bibleRepo.lookupReference(parsedReference, versionId)
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
      },
    })
  }

  // GET /api/bible/search?q=luz+do+mundo&version=ACF&limit=25
  if (url.pathname === '/api/bible/search') {
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

  // GET /api/bible/versions
  if (url.pathname === '/api/bible/versions') {
    const versions = context.bibleRepo.listVersions()
    return jsonResponse({
      ok: true,
      data: versions,
    })
  }

  return null
}
