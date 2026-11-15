/**
 * @module @slices/navigation/daemon/routes/display-router
 * @description Roteador REST para os renderizadores de Broadcast, Stage e Remote.
 *
 * ## Responsabilidades:
 * - Servir o HTML base para o overlay de broadcast (`/broadcast`).
 * - Servir o HTML base para o monitor de palco (`/stage`).
 * - Servir o HTML base para o púlpito do pregador (`/remote/preacher`).
 */

import { jsonResponse } from '../../../../apps/daemon/src/api/context'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'

const serveStaticHtml = (title: string) => {
  return new Response(`
    <!DOCTYPE html>
    <html lang="en">
      <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>${title}</title>
        <style>
          body { margin: 0; background-color: transparent; }
        </style>
      </head>
      <body>
        <div id="root"></div>
        <script type="module" src="/src/main.tsx"></script>
      </body>
    </html>
  `, {
    headers: { 'Content-Type': 'text/html' },
  });
};

/**
 * Processa requisições HTTP para os renderizadores de display.
 *
 * @param request A requisição HTTP de entrada.
 * @param targetUrl O objeto URL da requisição.
 * @param context O contexto da API com repositórios injetados.
 * @returns Uma resposta HTTP ou nulo se nenhuma rota corresponder.
 */
export async function handleDisplayRoutes(
  request: Request,
  targetUrl: URL,
  context: ApiContext,
): Promise<Response | null> {
  const pathname = targetUrl.pathname

  if (pathname.startsWith('/api/navigation')) {
    if (request.method === 'GET' && pathname === '/api/navigation/broadcast-settings') {
        const settings = context.displayRepo.getBroadcastSettings();
        return jsonResponse({ ok: true, data: settings });
    }
    
    if (request.method === 'POST' && pathname === '/api/navigation/broadcast-settings') {
        try {
            const body = await request.json();
            context.displayRepo.saveBroadcastSettings(body);
            return jsonResponse({ ok: true, message: 'Configurações de broadcast salvas.' });
        } catch {
            return jsonResponse({ ok: false, error: 'Payload inválido.' }, 400);
        }
    }

    return jsonResponse({ ok: false, error: 'Rota de navegação não encontrada' }, 404);
  }

  if (pathname === '/broadcast') {
    return serveStaticHtml('LumenLite Broadcast');
  }

  if (pathname === '/stage') {
    return serveStaticHtml('LumenLite Stage Monitor');
  }

  if (pathname === '/remote/preacher') {
    return serveStaticHtml('LumenLite Preacher Remote');
  }

  return null
}
