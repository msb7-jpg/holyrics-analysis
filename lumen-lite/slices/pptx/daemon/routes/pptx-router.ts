/**
 * @module @slices/pptx/daemon/routes/pptx-router
 * @description Roteador REST para ingestão e consulta de apresentações PowerPoint (`.pptx`) e PDF (`/api/pptx/*` - RF-21).
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import { extractPdfFromBuffer, extractPptxFromBuffer } from '../pptx-extractor'
import { PptxPresentationSchema } from '../../contracts/pptx-contracts'

/**
 * Processa requisições HTTP direcionadas a `/api/pptx/*`.
 *
 * @param request - Requisição HTTP recebida.
 * @param targetUrl - Objeto URL decomposto.
 * @param context - Contexto de dependências do daemon.
 * @returns Resposta HTTP ou null caso a rota não pertença a esta fatia.
 */
export async function handlePptxRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const { pathname } = targetUrl

  if (!pathname.startsWith('/api/pptx')) {
    return null
  }

  const pptxRepo = context.pptxRepo
  if (!pptxRepo) {
    return jsonResponse({ ok: false, error: 'PptxRepository não inicializado' }, 500)
  }

  // 1. GET /api/pptx - Lista todas as apresentações ingeridas
  if (pathname === '/api/pptx' && request.method === 'GET') {
    return jsonResponse({
      ok: true,
      data: pptxRepo.listAll(),
    })
  }

  // 2. POST /api/pptx/ingest - Ingere um arquivo .pptx ou .pdf em memória (RF-21)
  if (pathname === '/api/pptx/ingest' && request.method === 'POST') {
    try {
      const body = (await request.json()) as {
        filename?: string
        fileBase64?: string
        presentation?: unknown
      }

      if (body.presentation) {
        const parsed = PptxPresentationSchema.parse(body.presentation)
        const saved = pptxRepo.savePresentation(parsed)
        return jsonResponse({ ok: true, data: saved }, 201)
      }

      if (!body.fileBase64) {
        return jsonResponse({ ok: false, error: 'fileBase64 ou presentation obrigatório' }, 400)
      }

      const filename = body.filename || 'Apresentacao.pptx'
      const rawBytes = new Uint8Array(Buffer.from(body.fileBase64, 'base64'))
      const isPdf = filename.toLowerCase().endsWith('.pdf')

      const extracted = isPdf
        ? extractPdfFromBuffer(rawBytes, filename)
        : extractPptxFromBuffer(rawBytes, filename)

      const saved = pptxRepo.savePresentation(extracted)
      return jsonResponse({ ok: true, data: saved }, 201)
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Falha ao ingerir arquivo PPTX/PDF',
        },
        400,
      )
    }
  }

  // 3. GET /api/pptx/:id - Detalhes de uma apresentação específica
  if (pathname.startsWith('/api/pptx/') && request.method === 'GET') {
    const id = pathname.replace('/api/pptx/', '').trim()
    const presentation = pptxRepo.getById(id)
    if (!presentation) {
      return jsonResponse({ ok: false, error: 'Apresentação não encontrada' }, 404)
    }
    return jsonResponse({ ok: true, data: presentation })
  }

  // 4. DELETE /api/pptx/:id - Remove uma apresentação
  if (pathname.startsWith('/api/pptx/') && request.method === 'DELETE') {
    const id = pathname.replace('/api/pptx/', '').trim()
    const remaining = pptxRepo.deletePresentation(id)
    return jsonResponse({ ok: true, data: remaining })
  }

  return null
}
