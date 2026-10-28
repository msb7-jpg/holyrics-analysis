import { FsmEngine } from '@lumen/core'
import type { ActionCommand, ScreenState } from '@lumen/core'
import { DbClient } from './db/db-client'

const PORT = parseInt(process.env.PORT || '4040', 10)
const fsm = new FsmEngine()
const dbClient = new DbClient()

interface WebSocketData {
  id: string
  clientType: 'operator' | 'projector' | 'stage' | 'broadcast'
}

console.log(`[LumenLite Daemon] Inicializando na porta ${PORT}...`)

const server = Bun.serve<WebSocketData>({
  port: PORT,
  fetch(req, server) {
    const url = new URL(req.url)

    // Upgrade para WebSocket em /ws
    if (url.pathname === '/ws') {
      const clientType = (url.searchParams.get('type') || 'operator') as WebSocketData['clientType']
      const success = server.upgrade(req, {
        data: {
          id: crypto.randomUUID(),
          clientType,
        },
      })
      if (success) return undefined
    }

    // Health check & status
    if (url.pathname === '/api/status') {
      return Response.json({
        ok: true,
        state: fsm.getState(),
        clients: server.subscriberCount('screen-state'),
      })
    }

    return new Response('LumenLite Daemon Active', { status: 200 })
  },
  websocket: {
    open(ws) {
      ws.subscribe('screen-state')
      console.log(`[WS] Cliente conectado: ${ws.data.clientType} (${ws.data.id})`)
      // Envia estado atual imediatamente ao conectar
      ws.send(JSON.stringify({ type: 'SYNC_STATE', state: fsm.getState() }))
    },
    message(ws, message) {
      try {
        const data = JSON.parse(typeof message === 'string' ? message : new TextDecoder().decode(message))

        if (data.type === 'DISPATCH' && data.command) {
          const newState = fsm.dispatch(data.command as ActionCommand)
          server.publish('screen-state', JSON.stringify({ type: 'STATE_CHANGED', state: newState }))
        }
      } catch (err) {
        console.error('[WS] Erro ao processar mensagem:', err)
      }
    },
    close(ws) {
      console.log(`[WS] Cliente desconectado: ${ws.data.clientType} (${ws.data.id})`)
    },
  },
})

// Assina mudanças da FSM para propagar via WebSocket
fsm.subscribe((newState: ScreenState) => {
  server.publish('screen-state', JSON.stringify({ type: 'STATE_CHANGED', state: newState }))
})

console.log(`[LumenLite Daemon] Rodando em http://localhost:${server.port} (WS: ws://localhost:${server.port}/ws)`)
