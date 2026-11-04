/**
 * @module @lumen/daemon/api/router
 * @description Despachante HTTP central do daemon, gerenciando CORS, WebSocket upgrade e delegação de rotas.
 */

import type { Server } from 'bun'
import type { WebSocketData } from '../ws/ws-handler'
import { CORS_HEADERS } from './context'
import type { ApiContext } from './context'
import { handleStatusRoute } from './routes/status-router'
import { handleSongsRoute } from '@slices/songs/daemon/routes/songs-router'
import { handleVagalumeRoute } from '@slices/songs/daemon/routes/vagalume-router'
import { handleBibleRoutes } from '@slices/bible/daemon/routes/bible-router'
import { handleLiturgyRoutes } from '@slices/liturgy/daemon/routes/liturgy-router'

/**
 * Cria a interface do roteador da API com injeção de dependências do contexto.
 *
 * @param context - Instâncias de serviços e repositórios injetados.
 * @returns Objeto com o método `handle` responsável por despachar requisições.
 */
export function createApiRouter(context: ApiContext) {
  return {
    /**
     * Processa a requisição HTTP e retorna a resposta adequada.
     *
     * @param request - Requisição HTTP recebida pelo servidor Bun.
     * @param serverInstance - Instância ativa do Bun.serve para upgrades e métricas.
     * @returns Resposta HTTP ou undefined caso o upgrade WebSocket tenha sido realizado.
     */
    async handle(request: Request, serverInstance: Server<WebSocketData>): Promise<Response | undefined> {
      const targetUrl = new URL(request.url)

      // Resposta imediata para preflight CORS
      if (request.method === 'OPTIONS') {
        return new Response(null, { headers: CORS_HEADERS })
      }

      // Upgrade de conexão HTTP para WebSocket no canal /ws
      if (targetUrl.pathname === '/ws') {
        const clientCategory = (targetUrl.searchParams.get('type') || 'operator') as WebSocketData['clientType']
        const upgraded = serverInstance.upgrade(request, {
          data: {
            id: crypto.randomUUID(),
            clientType: clientCategory,
          },
        })
        if (upgraded) return undefined
      }

      // Rota de integridade e telemetria
      if (targetUrl.pathname === '/api/status') {
        return handleStatusRoute(context, serverInstance)
      }

      // Delegação para o roteador de músicas da fatia @slices/songs
      const songsResponse = await handleSongsRoute(request, targetUrl, context)
      if (songsResponse) return songsResponse

      // Delegação para o roteador de integração Vagalume da fatia @slices/songs
      const vagalumeResponse = await handleVagalumeRoute(request, targetUrl, context)
      if (vagalumeResponse) return vagalumeResponse

      // Delegação para o roteador bíblico da fatia @slices/bible
      const bibleResponse = handleBibleRoutes(targetUrl, context)
      if (bibleResponse) return bibleResponse

      // Delegação para o roteador de liturgia da fatia @slices/liturgy
      const liturgyResponse = await handleLiturgyRoutes(request, targetUrl, context)
      if (liturgyResponse) return liturgyResponse

      // Fallback para raiz ou rota padrão
      return new Response('LumenLite Daemon Active', { status: 200, headers: CORS_HEADERS })
    },
  }
}
