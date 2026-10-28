import { useEffect, useState, useCallback, useRef } from 'react'
import type { ScreenState, ActionCommand } from '@lumen/core'
import { INITIAL_SCREEN_STATE } from '@lumen/core'

export function useWebSocketTransport(url = 'ws://localhost:4040/ws?type=operator') {
  const [state, setState] = useState<ScreenState>(INITIAL_SCREEN_STATE)
  const [connected, setConnected] = useState(false)
  const wsRef = useRef<WebSocket | null>(null)

  useEffect(() => {
    let ws: WebSocket
    let retryTimeout: Timer

    function connect() {
      try {
        ws = new WebSocket(url)
        wsRef.current = ws

        ws.onopen = () => {
          setConnected(true)
          console.log('[WS] Conectado ao Daemon LumenLite')
        }

        ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data)
            if (data.type === 'SYNC_STATE' || data.type === 'STATE_CHANGED') {
              setState(data.state)
            }
          } catch (err) {
            console.error('[WS] Erro ao decodificar estado:', err)
          }
        }

        ws.onclose = () => {
          setConnected(false)
          // Reconexão automática resiliente a cada 2s
          retryTimeout = setTimeout(connect, 2000)
        }

        ws.onerror = () => {
          ws.close()
        }
      } catch {
        retryTimeout = setTimeout(connect, 2000)
      }
    }

    connect()

    return () => {
      clearTimeout(retryTimeout)
      if (wsRef.current) wsRef.current.close()
    }
  }, [url])

  const dispatch = useCallback((command: ActionCommand) => {
    if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify({ type: 'DISPATCH', command }))
    } else {
      console.warn('[WS] Comando não enviado: WebSocket desconectado.')
    }
  }, [])

  return { state, connected, dispatch }
}
