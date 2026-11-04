/**
 * @module @slices/liturgy/daemon/routes/liturgy-router
 * @description Roteador REST modular para operações da liturgia de culto (`/api/liturgy/*`).
 *
 * ## Responsabilidades:
 * - GET `/api/liturgy/current`: Retorna o arquivo de culto ativo (`culto.json`).
 * - POST `/api/liturgy/save`: Salva e valida a liturgia completa com preflight.
 * - POST `/api/liturgy/reorder`: Reordena itens litúrgicos (suporte a Drag & Drop).
 * - POST `/api/liturgy/item`: Adiciona um item polimórfico (louvor, versículo, mídia, texto avulso).
 * - DELETE `/api/liturgy/item/:id`: Remove um item pelo ID.
 * - POST `/api/liturgy/quick-text`: Dispara projeção instantânea de texto livre de emergência (RF-22A).
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import {
  LiturgyFileSchema,
  ReorderLiturgyPayloadSchema,
  LiturgyItemSchema,
  QuickTextPayloadSchema,
} from '../../contracts/liturgy-schema'
import { validateLiturgy } from '../preflight-validator'

/**
 * Processa requisições HTTP direcionadas à fatia de liturgia.
 *
 * @param request - Requisição HTTP recebida pelo servidor Bun.
 * @param targetUrl - Objeto URL parseado com parâmetros e rota.
 * @param context - Contexto global com repositórios e instâncias da FSM.
 * @returns Response formatada ou null caso a rota não pertença à fatia.
 */
export async function handleLiturgyRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext
): Promise<Response | null> {
  const pathname = targetUrl.pathname

  if (!pathname.startsWith('/api/liturgy')) {
    return null
  }

  // 1. GET /api/liturgy/current - Consulta a liturgia ativa
  if (pathname === '/api/liturgy/current' && request.method === 'GET') {
    const liturgy = context.liturgyRepo.getCurrentLiturgy()
    return jsonResponse({ ok: true, data: liturgy })
  }

  // 2. POST /api/liturgy/save - Salva a liturgia com preflight
  if (pathname === '/api/liturgy/save' && request.method === 'POST') {
    try {
      const body = await request.json()
      const preflight = validateLiturgy(body)

      if (!preflight.valid) {
        return jsonResponse(
          { ok: false, error: 'Falha de validação no preflight', details: preflight.errors },
          400
        )
      }

      const validated = LiturgyFileSchema.parse(body)
      const saved = context.liturgyRepo.saveLiturgy(validated)
      return jsonResponse({ ok: true, data: saved })
    } catch (error: unknown) {
      return jsonResponse(
        { ok: false, error: error instanceof Error ? error.message : 'Falha ao salvar liturgia' },
        400
      )
    }
  }

  // 3. POST /api/liturgy/reorder - Reordena itens da liturgia (Drag & Drop)
  if (pathname === '/api/liturgy/reorder' && request.method === 'POST') {
    try {
      const body = await request.json()
      const validated = ReorderLiturgyPayloadSchema.parse(body)
      const updated = context.liturgyRepo.reorderItems(validated.itemIds)
      return jsonResponse({ ok: true, data: updated })
    } catch (error: unknown) {
      return jsonResponse(
        { ok: false, error: error instanceof Error ? error.message : 'Falha ao reordenar liturgia' },
        400
      )
    }
  }

  // 4. POST /api/liturgy/item - Adiciona um novo item polimórfico
  if (pathname === '/api/liturgy/item' && request.method === 'POST') {
    try {
      const body = await request.json()
      const item = LiturgyItemSchema.parse(body.item ?? body)
      const insertIndex = typeof body.insertIndex === 'number' ? body.insertIndex : undefined
      const updated = context.liturgyRepo.addItem(item, insertIndex)
      return jsonResponse({ ok: true, data: updated })
    } catch (error: unknown) {
      return jsonResponse(
        { ok: false, error: error instanceof Error ? error.message : 'Falha ao adicionar item na liturgia' },
        400
      )
    }
  }

  // 5. DELETE /api/liturgy/item/:id - Remove um item do roteiro
  if (pathname.startsWith('/api/liturgy/item/') && request.method === 'DELETE') {
    const itemId = decodeURIComponent(pathname.replace('/api/liturgy/item/', ''))
    if (!itemId) {
      return jsonResponse({ ok: false, error: 'ID do item obrigatório' }, 400)
    }

    const updated = context.liturgyRepo.removeItem(itemId)
    return jsonResponse({ ok: true, data: updated })
  }

  // 6. POST /api/liturgy/quick-text - Projeção instantânea de texto livre de emergência (RF-22A)
  if (pathname === '/api/liturgy/quick-text' && request.method === 'POST') {
    try {
      const body = await request.json()
      const payload = QuickTextPayloadSchema.parse(body)

      // Quebra linhas preservando parágrafos
      const lines = payload.text
        .split('\n')
        .map((line) => line.trim())
        .filter((line) => line.length > 0)

      const itemId = `quick_text_${Date.now()}`

      // Despacha diretamente para a FSM do motor de projeção
      context.fsm.dispatch({
        type: 'PRESENT_ITEM',
        itemId,
        mode: 'EMPTY', // Modo avulso com apresentação sobre o fundo ativo
        foreground: {
          title: payload.title,
          content: lines,
          slideIndex: 0,
          totalSlides: 1,
        },
      })

      return jsonResponse({
        ok: true,
        data: {
          itemId,
          title: payload.title,
          lines,
          dispatched: true,
        },
      })
    } catch (error: unknown) {
      return jsonResponse(
        { ok: false, error: error instanceof Error ? error.message : 'Falha na projeção de texto rápido' },
        400
      )
    }
  }

  return null
}
