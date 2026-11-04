/**
 * @module @slices/keymap/daemon/routes/keymap-router
 * @description Roteador REST modular para gerenciamento declarativo de atalhos de teclado (`/api/keymap/*`).
 *
 * ## Responsabilidades:
 * - GET `/api/keymap`: Retorna a lista ativa de atalhos e metadados.
 * - PUT `/api/keymap`: Atualiza e valida a configuração declarativa de atalhos.
 * - POST `/api/keymap/reset`: Restaura os atalhos padrão de fábrica.
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'
import { KeymapConfigSchema } from '../../contracts/keymap-contracts'

/**
 * Processa requisições HTTP direcionadas à fatia de atalhos de teclado.
 *
 * @param request - Requisição HTTP recebida pelo servidor Bun.
 * @param targetUrl - Objeto URL contendo parâmetros e rota.
 * @param context - Contexto global injetado da API.
 * @returns Response formatada ou null se a rota não corresponder a esta fatia.
 */
export async function handleKeymapRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const pathname = targetUrl.pathname

  if (!pathname.startsWith('/api/keymap')) {
    return null
  }

  // 1. GET /api/keymap - Retorna a configuração ativa
  if (pathname === '/api/keymap' && request.method === 'GET') {
    const config = context.keymapRepo.load()
    return jsonResponse({
      ok: true,
      data: config,
    })
  }

  // 2. PUT /api/keymap - Atualiza a configuração de atalhos
  if (pathname === '/api/keymap' && request.method === 'PUT') {
    try {
      const body = await request.json()
      const parsedConfig = KeymapConfigSchema.parse(body)

      context.keymapRepo.save(parsedConfig)
      return jsonResponse({
        ok: true,
        data: parsedConfig,
      })
    } catch (error: unknown) {
      return jsonResponse(
        {
          ok: false,
          error: error instanceof Error ? error.message : 'Falha ao salvar configuração de atalhos',
        },
        400,
      )
    }
  }

  // 3. POST /api/keymap/reset - Restaura os atalhos padrão
  if (pathname === '/api/keymap/reset' && request.method === 'POST') {
    const defaultData = context.keymapRepo.resetToDefaults()
    return jsonResponse({
      ok: true,
      data: defaultData,
      message: 'Atalhos de teclado restaurados para os padrões de fábrica.',
    })
  }

  return null
}
